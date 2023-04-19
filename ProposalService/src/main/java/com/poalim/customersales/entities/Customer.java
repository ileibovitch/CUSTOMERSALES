package com.poalim.customersales.entities;


public class Customer {
	
	private Long customerId;
	
	private String name;
	
	private boolean blocked;
	
	private int proposalsPerDay;
	
	public Long getCustomerId() {
		return customerId;
	}
	public void setcustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public int getProposalsPerDay() {
		return proposalsPerDay;
	}
	public void setProposalsPerDay(int proposalsPerDay) {
		this.proposalsPerDay = proposalsPerDay;
	}
	
	
	
	
	
	
}
