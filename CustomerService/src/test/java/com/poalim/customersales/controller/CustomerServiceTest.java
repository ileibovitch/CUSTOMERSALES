package com.poalim.customersales.controller;

import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.repository.CustomerRepository;
import com.poalim.customersales.service.CustomerService;

@SpringBootTest
public class CustomerServiceTest {
	@InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private Map<Long, Customer> customerMap;
    
    

    @Test
    public void testBlockCustomer() {
        // Given
        Long id = 1234L;
        Customer mockedCustomer = new Customer();
        mockedCustomer.setCustomerId(id);
        mockedCustomer.setName("CustomerName");
        mockedCustomer.setBlocked(false);
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(mockedCustomer));
        Mockito.when(customerMap.get(anyLong())).thenReturn(mockedCustomer);
        Mockito.when(customerMap.containsKey(anyLong())).thenReturn(true);

        // When
        Customer result = customerService.blockCustomer(id);

        Assertions.assertTrue(result.isBlocked());
    }
    
    @Test
    public void testUnblockCustomer() {
        // Given
        Long id = 1234L;
        Customer mockedCustomer = new Customer();
        mockedCustomer.setCustomerId(id);
        mockedCustomer.setName("CustomerName");
        mockedCustomer.setBlocked(true);
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(mockedCustomer));
        Mockito.when(customerMap.get(anyLong())).thenReturn(mockedCustomer);
        Mockito.when(customerMap.containsKey(anyLong())).thenReturn(true);

        // When
        Customer result = customerService.unblockCustomer(id);

        Assertions.assertFalse(result.isBlocked());
    }
}
