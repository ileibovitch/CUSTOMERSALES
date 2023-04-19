package com.poalim.customersales.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.service.CustomerService;

@RestController
@RequestMapping("/")
public class CustomerController {
	private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomersById(@PathVariable Long id) {
    	try {
    		Customer customer =  customerService.getCustomerById(id);
    		return ResponseEntity.ok(customer);
    	} catch (Exception e) {
    		// Handle the exception and return an appropriate response
            // e.g. log the error, return a specific error message, etc.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
    	}
    }

    @PostMapping("/{id}/block")
    public Customer blockCustomer(@PathVariable Long id) {
        return customerService.blockCustomer(id);
    }

    @PostMapping("/{id}/unblock")
    public Customer unblockCustomer(@PathVariable Long id) {
        return customerService.unblockCustomer(id);
    }
    
    @PostMapping("/addCustomer/{id}/{name}")
    public Customer addCustomer(@PathVariable Long id, @PathVariable String name) {
    	Customer customer = new Customer();
    	customer.setcustomerId(id);
    	customer.setName(name);
    	customer.setBlocked(false);
    	customer.setProposalsPerDay(0);
        return customerService.addCustomer(customer);
    }
    
    @PostMapping("/{id}/addProposal")
    public ResponseEntity<String> addProposal(@PathVariable Long id) {
    	try {
    		customerService.addProposal(id);
    		return ResponseEntity.ok("Proposal added");
    	} catch (Exception e) {
    		// Handle the exception and return an appropriate response
            // e.g. log the error, return a specific error message, etc.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
    	}
    }
    
    @PostMapping("/{id}/removeProposal")
    public ResponseEntity<String> removeProposal(@PathVariable Long id) {
    	try {
    		customerService.removeProposal(id);
    		return ResponseEntity.ok("Proposal removed");
    	} catch (Exception e) {
    		// Handle the exception and return an appropriate response
            // e.g. log the error, return a specific error message, etc.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
    	}
    }
    
}
