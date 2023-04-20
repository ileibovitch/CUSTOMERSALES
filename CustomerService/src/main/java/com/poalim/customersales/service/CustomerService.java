package com.poalim.customersales.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.poalim.customersales.repository.CustomerRepository;
import com.poalim.customersales.entities.Customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Service
public class CustomerService{
	
	private final CustomerRepository customerRepository;
	private static final Logger logger = LogManager.getLogger(CustomerService.class);
	@Autowired 
	Map<Long, Customer> customerMap;
	
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Customer addCustomer(Customer customer) {
    	customerMap.put(customer.getCustomerId(), customer);
    	return customerRepository.save(customer);
    }
    
    public Customer getCustomerById(Long customerId) {
        if (customerMap.containsKey(customerId)) {
        	logger.info("getCustomerById - Customer with id {} was found ", customerId);
            return customerMap.get(customerId);
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
        if (customerMap.containsKey(customerId)) {
            Customer customer = customerMap.get(customerId);
            customer.setBlocked(true);
            customerMap.put(customerId, customer);
            customerRepository.save(customer);
            logger.info("blockCustomer - Customer with id {} was blocked ", customerId);
            return customer;
        }
        logger.error("blockCustomer - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}

	
	public Customer unblockCustomer(Long customerId) {
		if (customerMap.containsKey(customerId)) {
            Customer customer = customerMap.get(customerId);
            customer.setBlocked(false);
            customerMap.put(customerId, customer);
            customerRepository.save(customer);
            logger.info("blockCustomer - Customer with id {} was unblocked ", customerId);
            return customer;
        }
        logger.error("unblockCustomer - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}
	
	public synchronized Customer addProposal(long customerId) {
		if (customerMap.containsKey(customerId)) {
            Customer customer = customerMap.get(customerId);
            customer.setProposalsPerDay(customer.getProposalsPerDay() + 1);
            customerMap.put(customerId, customer);
            customerRepository.save(customer);
            logger.info("addProposal - proposal was added to customer {0}. number of proposals for today is {1} ", customerId, customer.getProposalsPerDay());
            return customer;
        }
        logger.error("addProposal - Customer with id {} was not found ", customerId);
        throw new RuntimeException("Customer not found");
	}
	
	public synchronized Customer removeProposal(long customerId) {
		if (customerMap.containsKey(customerId)) {
            Customer customer = customerMap.get(customerId);
            customer.setProposalsPerDay(customer.getProposalsPerDay() - 1);
            customerMap.put(customerId, customer);
            customerRepository.save(customer);
            logger.info("removeProposal - proposal was removed from customer {0}. number of proposals for today is {1} ", customerId, customer.getProposalsPerDay());
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
		customerMap.clear();
		List<Customer> customers = getAllCustomers();
		customers.stream().forEach(customer -> {
			customer.setProposalsPerDay(0);
			customerMap.put(customer.getCustomerId(), customer);
		});
		customerRepository.saveAll(customers);
	}

}
