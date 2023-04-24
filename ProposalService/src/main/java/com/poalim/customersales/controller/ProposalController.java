package com.poalim.customersales.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poalim.customersales.service.ProposalService;

import reactor.core.publisher.Mono;

import com.poalim.customersales.entities.Proposal;
import com.poalim.customersales.entities.ProposalStatus;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private ProposalService proposalService;

    @Autowired
    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PostMapping
    public Mono<ResponseEntity<Proposal>> createProposal(@RequestBody ProposalRequest proposalRequest) {
        Proposal proposal = new Proposal();
        proposal.setCustomerId(proposalRequest.getCustomerId());
        proposal.setCustomerName(proposalRequest.getCustomerName());
        proposal.setCampaignId(proposalRequest.getCampaignId());
        proposal.setAmmount(proposalRequest.getAmmount());

        Mono<Proposal> createdProposal = proposalService.createProposal(proposal.getCustomerId(), proposal);
        return createdProposal.map(proposalResult -> {
            if (proposalResult.getStatus() == ProposalStatus.REJECTED) {
                // If proposal is rejected, return an appropriate response
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(proposalResult);
            } else {
                // If proposal is approved, return a success response
                return ResponseEntity.status(HttpStatus.CREATED).body(proposalResult);
            }
        });
    }

    @GetMapping("/{proposalId}")
    public ResponseEntity<Proposal> getProposal(@PathVariable Long proposalId) {
        Proposal proposal = proposalService.getProposal(proposalId);
        return ResponseEntity.ok(proposal);
    }

    @GetMapping
    public ResponseEntity<List<Proposal>> getAllProposals() {
        List<Proposal> proposals = proposalService.getAllProposals();
        return ResponseEntity.ok(proposals);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Proposal>> getProposalsByCustomerId(@PathVariable Long customerId) {
        List<Proposal> proposals = proposalService.getProposalsByCustomerId(customerId);
        return ResponseEntity.ok(proposals);
    }

    @DeleteMapping("/{proposalId}")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long proposalId) {
        proposalService.deleteProposal(proposalId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/purchase/{proposalId}")
    public ResponseEntity<Void> purchaseProposal(@PathVariable Long proposalId) {
        proposalService.purchaseProposal(proposalId);
        return ResponseEntity.ok().build();
    }

}
