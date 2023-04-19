package com.poalim.customersales.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "proposals")
public class Proposal {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long proposalId;
	
	private Long customerId;
	private String CustomerName;
	private Long campaignId;
	private double ammount;
	private ProposalStatus status;
	private String errorMsg;
	
	
	public Long getProposalId() {
		return proposalId;
	}

	public void setProposalId(Long proposalId) {
		this.proposalId = proposalId;
	}

	public Long getCustomerId() {
		return customerId;
	}
	
	public String getCustomerName() {
		return CustomerName;
	}
	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	public double getAmmount() {
		return ammount;
	}
	public void setAmmount(double ammount) {
		this.ammount = ammount;
	}
	public ProposalStatus getStatus() {
		return status;
	}
	public void setStatus(ProposalStatus status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
