package com.poalim.customersales.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.entities.Proposal;
import com.poalim.customersales.entities.ProposalStatus;
import com.poalim.customersales.repository.ProposalRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProposalServiceTest {
	@InjectMocks
    private ProposalService proposalService;
	
	@Mock
	private ProposalRepository proposalRepository;
	 
	@Mock
    private WebClient webClient;
	
	@Value("${customer.maxProposals}")
	private int maxProposals;
	
	private Customer customer = new Customer();
	private Proposal proposal = new Proposal();
	
	@BeforeEach
    public void setUp() {
		this.proposal.setProposalId(1234L);
		this.proposal.setAmmount(10000.0);
	    this.proposal.setCampaignId(1234L);
	        
        // Set the value of maxProposals in ProposalService to 5 before each test
        ReflectionTestUtils.setField(proposalService, "maxProposals", maxProposals);
        
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecGet = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecGet = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecGet = Mockito.mock(WebClient.ResponseSpec.class);
        
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpecGet);
        Mockito.when(requestHeadersUriSpecGet.uri(anyString())).thenReturn(requestHeadersSpecGet);
        Mockito.when(requestHeadersSpecGet.retrieve()).thenReturn(responseSpecGet);
        Mockito.when(responseSpecGet.bodyToMono(Customer.class)).thenReturn(Mono.just(this.customer));
        
     // Mock the webClient response for adding proposal
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpecPost = Mockito.mock(WebClient.ResponseSpec.class);
        
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.retrieve()).thenReturn(responseSpecPost);
        Mockito.when(responseSpecPost.bodyToMono(String.class)).thenReturn(Mono.just("Proposal added"));
        
        // Mock the customerRepository behavior
        Mockito.when(proposalRepository.save(Mockito.any(Proposal.class))).thenReturn(this.proposal);
    }
    

    @Test
    public void testCreateProposal() {
        // Given
        Long customerId = 1234L;
        this.customer.setcustomerId(customerId);
        this.customer.setProposalsPerDay(2); // Set customer's proposals per day to 2
        this.customer.setBlocked(false); // Set customer's blocked status to false

        Mono<Proposal> result = proposalService.createProposal(customerId, proposal);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getStatus() == ProposalStatus.APPROVED) // Verify proposal status is APPROVED
                .expectComplete()
                .verify();
    }
    
    @Test
    public void testCreateProposalBlockedCustomer() {
        // Given
        Long customerId = 1234L;
        this.customer.setcustomerId(customerId);
        this.customer.setProposalsPerDay(maxProposals); // Set customer's proposals per day to maxProposals
        this.customer.setBlocked(true); // Set customer's blocked status to true
        
        // When
      
        Mono<Proposal> result = proposalService.createProposal(customerId, proposal);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getStatus() == ProposalStatus.REJECTED) // Verify proposal status is APPROVED
                .expectComplete()
                .verify();
    }
    
    @Test
    public void testCreateProposalCustomerWithMaxProposals() {
        // Given
        Long customerId = 1234L;
        this.customer.setcustomerId(customerId);
        this.customer.setProposalsPerDay(5); // Set customer's proposals per day to 
        this.customer.setBlocked(false); // Set customer's blocked status to false
        
        // When
      
        Mono<Proposal> result = proposalService.createProposal(customerId, proposal);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getStatus() == ProposalStatus.REJECTED) // Verify proposal status is APPROVED
                .expectComplete()
                .verify();
    }
    
    @Test
    public void testPurchaseProposal() {
    	proposal.setStatus(ProposalStatus.APPROVED);
        // Mocking the proposalRepository.findByProposalId() method
        Mockito.when(proposalRepository.findByProposalId(anyLong())).thenReturn(Optional.of(proposal));
        
        
     // Call the method to be tested
        proposalService.purchaseProposal(proposal.getProposalId());
        
     // Verifying the proposalRepository.findByProposalId() and is called twicw
        Mockito.verify(proposalRepository, Mockito.times(2)).findByProposalId(proposal.getProposalId());


        // Verifying that the deleteProposal() method also called findByProposalId() method
        Mockito.verify(proposalRepository, Mockito.times(1)).delete(proposal);
    }
    
    @Test
    public void testPurchaseProposalWithNonExistingProposal() {
        // Mocking the proposalRepository.findByProposalId() method to return an empty optional
    	Mockito.when(proposalRepository.findByProposalId(1L)).thenReturn(Optional.empty());

        // Call the method to be tested
    	Assertions.assertThrows(RuntimeException.class, () -> proposalService.purchaseProposal(proposal.getProposalId()));
    }

    @Test
    public void testPurchaseProposalWithNonApprovedProposal() {
        proposal.setStatus(ProposalStatus.PENDING);

        // Mocking the proposalRepository.findByProposalId() method
        Mockito.when(proposalRepository.findByProposalId(1L)).thenReturn(Optional.of(proposal));

        // Call the method to be tested
        Assertions.assertThrows(RuntimeException.class, () -> proposalService.purchaseProposal(proposal.getProposalId()));
    }
    
}
