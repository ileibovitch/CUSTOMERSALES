package com.poalim.customersales.service;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.entities.Proposal;
import com.poalim.customersales.entities.ProposalStatus;
import com.poalim.customersales.repository.ProposalRepository;

import reactor.core.publisher.Mono;

@Service
public class ProposalService {
	@Autowired
	private WebClient webClient;
	
	@Value("${customerserver.url}")
	private String customerServerURL;
	
	private ProposalRepository proposalRepository;
    
	private static final Logger logger = LogManager.getLogger(ProposalService.class);
	
    @Autowired
    public ProposalService(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public Proposal createProposal(Long customerId, Proposal proposal) {
    	String retrieveUrl = customerServerURL + customerId;
    	logger.info("createProposal - fetching customer id {0}.", customerId);
    	Mono<Customer> monoCust = webClient.get()
    								.uri(retrieveUrl)
							        .retrieve()
							        .bodyToMono(Customer.class);
							        
        monoCust.subscribe(
        		customer -> {
        			logger.info("createProposal - customer {0} was retrieved", customer.getName());
        			// Check if customer has exceeded the maximum number of proposals for the day
        	        if (customer.getProposalsPerDay() >= 5) {
        	        	logger.error("createProposal - customer {0} has already 5 proposals", customer.getName());
        	        	proposal.setStatus(ProposalStatus.REJECTED);
        	        	proposal.setErrorMsg("Customer already made 5 proposals today");
        	        } else if (customer.isBlocked()) {
        	        	// Check if customer is blocked
        	        	logger.error("createProposal - customer {0} is blocked", customer.getName());
    	            	proposal.setStatus(ProposalStatus.REJECTED);
    	            	proposal.setErrorMsg("Customer is blocked and not allowed to make proposals");
    	            	
    	            } else {
    	            	//otherwise the proposal approved
    	            	logger.info("createProposal - customer {0} is ok to make a proposal", customer.getName());
    	                String addUrl = customerServerURL + customerId + "/addProposal";
    	                webClient.post()
							.uri(addUrl)
							.retrieve()
							.bodyToMono(String.class)
							.subscribe(
									resposnse -> {
										logger.info("createProposal - customer {0} made a proposal", customer.getName());
									},
									error -> {
										logger.error("createProposal - something went wrong proposal rejected. {0}", error.getMessage());
										proposal.setStatus(ProposalStatus.REJECTED);
				    	            	proposal.setErrorMsg("Customer is blocked and not allowed to make proposals");
									}
							);
    	                
    	            }
        	    },
        	    error -> {
        	    	//in case CustomerService is down, we want to store the proposal and retry later
        	    	if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
        	    		logger.error("createProposal - CustomerService is down. proposal will be stored and will be sent later. {0} ", error.getMessage());
        	    		proposal.setStatus(ProposalStatus.PENDDING);
        	    	} else {
        	    		logger.error("createProposal - something went wrong in customer retrieving proposal rejected. {0}", error.getMessage());
        	    		proposal.setStatus(ProposalStatus.REJECTED);
        	    		proposal.setErrorMsg("Customer does not exist");
        	    	}
                	
        	    }
        );
        if (ProposalStatus.REJECTED.equals(proposal.getStatus())) {
        	return proposal;
        } else if (ProposalStatus.PENDDING.equals(proposal.getStatus())) {
        	proposalRepository.save(proposal);
        } else {
        	proposal.setStatus(ProposalStatus.APPROVED);
        }
        
        return proposalRepository.save(proposal);
    }

    public Proposal getProposal(Long proposalId) {
        return proposalRepository.findByProposalId(proposalId).get();
                
    }

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }

    public List<Proposal> getProposalsByCustomerId(Long customerId) {
        return proposalRepository.findByCustomerId(customerId);
    }

    public void deleteProposal(Long proposalId) {
    	logger.info("deleteProposal - delete proposal with ID {0}", proposalId);
    	Optional<Proposal> optionalProposal = proposalRepository.findByProposalId(proposalId);
    	if (optionalProposal.isEmpty()) {
    		logger.error("deleteProposal - could not find proposal with ID {0}", proposalId);
    		throw new RuntimeException("Proposal not found with id: " + proposalId);
    	}
        Proposal proposal = optionalProposal.get();
        String removeUrl = customerServerURL + proposal.getCustomerId() + "/removeProposal";
        webClient.post()
			.uri(removeUrl)
			.retrieve()
			.bodyToMono(String.class)
			.subscribe(
					resposnse -> {
						logger.info("deleteProposal -  proposal with ID {0} was deleted", proposalId);
						proposalRepository.delete(proposal);
					},
					error -> {
						logger.error("deleteProposal - something went wrong in delete proposal. {0}" + error.getMessage());
					}
			);
        ;
    }

    public void purchaseProposal(Long proposalId) {
    	logger.info("purchaseProposal - porchase proposal with ID {0}", proposalId);
    	Optional<Proposal> optionalProposal = proposalRepository.findByProposalId(proposalId);
    	if (optionalProposal.isEmpty()) {
    		logger.error("purchaseProposal - could not find proposal with ID {0}", proposalId);
    		throw new RuntimeException("Proposal not found with id: " + proposalId);
    	}
        Proposal proposal = optionalProposal.get();
             

        // Check if customer is blocked
        if (!ProposalStatus.APPROVED.equals(proposal.getStatus())) {
        	logger.error("purchaseProposal - Proposal with ID {0} is not approved", proposalId);
            throw new RuntimeException("Proposal with ID " + proposalId + " is not approved to be purchased");
        }
        deleteProposal(proposalId);
    }
    
    /**
     * in case we have pending proposals we want to resend them every 10 minutes
     */
    @Scheduled(fixedRate = 600000)
    public void resendPendingProposals() {
    	List<Proposal> pendingProposals = proposalRepository.findByStatus(ProposalStatus.PENDDING);
    	pendingProposals.stream().forEach(proposal -> createProposal(proposal.getCustomerId(), proposal));
    }
    
    /**
    * every midnight we delete the pending proposals
    */
    @Scheduled(cron = "0 0 0 * * *")
    public void deletePendingProposals() {
    	List<Proposal> pendingProposals = proposalRepository.findByStatus(ProposalStatus.PENDDING);
    	pendingProposals.stream().forEach(proposal -> deleteProposal(proposal.getProposalId()));
    }
}
