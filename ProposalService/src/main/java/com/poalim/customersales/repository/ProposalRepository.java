package com.poalim.customersales.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poalim.customersales.entities.Proposal;
import com.poalim.customersales.entities.ProposalStatus;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long>  {
	Optional<Proposal> findByProposalId(Long proposalId);
	List<Proposal> findByCustomerId(Long customerId);
	List<Proposal> findByStatus(ProposalStatus status);
}
