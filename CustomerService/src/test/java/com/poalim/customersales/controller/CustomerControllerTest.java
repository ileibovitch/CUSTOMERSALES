package com.poalim.customersales.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    
    
    
    
	 @Test
    public void testAddCustomer()  throws Exception {
        // Given
        Long id = 1234L;
        String name = "My Customer";
        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/addCustomer/{id}/{name}", id, name))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
    }

}
