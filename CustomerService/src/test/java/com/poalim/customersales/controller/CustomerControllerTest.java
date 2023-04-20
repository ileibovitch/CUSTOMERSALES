package com.poalim.customersales.controller;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.repository.CustomerRepository;
import com.poalim.customersales.service.CustomerService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;
    
    @MockBean
    Map<Long, Customer> customerMap;
    
    
	 @Test
    public void testAddCustomer()  throws Exception {
        // Given
        Long id = 1234L;
        String name = "My Customer";
        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/addCustomer/{id}/{name}", id, name))
        							.andExpect(MockMvcResultMatchers.status().isOk())
        							.andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(id))
        							.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
        							.andReturn();
        
        String response = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer = objectMapper.readValue(response, Customer.class);

        // Then
        Assertions.assertNotNull(customer);
        Assertions.assertEquals(id, customer.getCustomerId());
        Assertions.assertEquals(name, customer.getName());
    }
	 
	

}
