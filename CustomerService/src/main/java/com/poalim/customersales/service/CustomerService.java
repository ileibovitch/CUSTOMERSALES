package com.poalim.customersales.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.poalim.customersales.repository.CustomerRepository;
import com.poalim.customersales.entities.Customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Service
public class CustomerService{
	
	private final CustomerRepository customerRepository;
	private static final Logger logger = LogManager.getLogger(CustomerService.class);
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Customer addCustomer(Customer customer) {
    	return customerRepository.save(customer);
    }
    
    public Customer getCustomerById(Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);
        if (optionalCustomer.isPresent()) {
        	logger.info("getCustomerById - Customer with id {} was found ", customerId);
            return optionalCustomer.get();
        }
        logger.error("getCustomerById - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
    }

    public Customer getUnblockedCustomerById(Long customerId) {
        return customerRepository.findByCustomerIdAndBlockedFalse(customerId);
    }

    public List<Customer> getBlockedCustomers() {
        return customerRepository.findByBlockedTrue();
    }
    
	
	public Customer blockCustomer(Long customerId) {
		Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBlocked(true);
            customerRepository.save(customer);
            logger.info("blockCustomer - Customer with id {} was blocked ", customerId);
            return customer;
        }
        logger.error("blockCustomer - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}

	
	public Customer unblockCustomer(Long customerId) {
		Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBlocked(false);
            customerRepository.save(customer);
            logger.info("blockCustomer - Customer with id {} was unblocked ", customerId);
            return customer;
        }
        logger.error("unblockCustomer - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}
	
	public synchronized Customer addProposal(long customerId) {
		Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            int proposalsPerDay = customer.getProposalsPerDay();
            proposalsPerDay++;
            customer.setProposalsPerDay(proposalsPerDay);
            customerRepository.save(customer);
            logger.info("addProposal - proposal was added to customer {0}. number of proposals for today is {1} ", customerId, proposalsPerDay);
            return customer;
        }
        logger.error("addProposal - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}
	
	public synchronized Customer removeProposal(long customerId) {
		Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            int proposalsPerDay = customer.getProposalsPerDay();
            proposalsPerDay--;
            customer.setProposalsPerDay(proposalsPerDay);
            customerRepository.save(customer);
            logger.info("removeProposal - proposal was removed from customer {0}. number of proposals for today is {1} ", customerId, proposalsPerDay);
            return customer;
        }
        logger.error("removeProposal - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}
		
	

	
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}
	
	/**
	    * every midnight we set to zero the number of proposals for this customer
	    */
	@Scheduled(cron = "0 0 0 * * *")
	public void setProposalsPerDayToZero() {
		List<Customer> customers = getAllCustomers();
		customers.stream().forEach(customer -> customer.setProposalsPerDay(0));
		customerRepository.saveAll(customers);
	}

}
