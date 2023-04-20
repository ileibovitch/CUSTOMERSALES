package com.poalim.customersales.controller;

public class ProposalRequest {
	
    private Long customerId;
    private String customerName;
    private Long campaignId;
    private double ammount;
    
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public void setAmmount(double amount) {
		this.ammount = amount;
	}

	    
	
}
