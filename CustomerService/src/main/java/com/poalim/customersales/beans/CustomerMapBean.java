package com.poalim.customersales.beans;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.repository.CustomerRepository;

@Configuration
public class CustomerMapBean {
	@Autowired 
	private CustomerRepository customerRepository;
	
	@Bean
	public Map<Long, Customer> customerMap() {
		Map<Long, Customer> customerMap = new ConcurrentHashMap<Long, Customer>();
		List<Customer> customers = customerRepository.findAll();
		customers.stream().forEach(customer -> customerMap.put(customer.getCustomerId(), customer));
		return customerMap;
	}

}
