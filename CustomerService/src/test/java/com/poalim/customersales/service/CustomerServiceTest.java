package com.poalim.customersales.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.poalim.customersales.entities.Customer;
import com.poalim.customersales.repository.CustomerRepository;


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
    
    @Test
    public void testAddProposal() {
        // Given
        Long id = 1234L;
        Customer mockedCustomer = new Customer();
        mockedCustomer.setCustomerId(id);
        mockedCustomer.setName("CustomerName");
        mockedCustomer.setBlocked(false);
        mockedCustomer.setProposalsPerDay(0);
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(mockedCustomer));
        Mockito.when(customerMap.get(anyLong())).thenReturn(mockedCustomer);
        Mockito.when(customerMap.containsKey(anyLong())).thenReturn(true);

        // When
        Customer result = customerService.addProposal(id);

        Assertions.assertEquals(result.getProposalsPerDay(), 1);
    }
    
    @Test
    public void testRemoveProposal() {
        // Given
        Long id = 1234L;
        Customer mockedCustomer = new Customer();
        mockedCustomer.setCustomerId(id);
        mockedCustomer.setName("CustomerName");
        mockedCustomer.setBlocked(false);
        mockedCustomer.setProposalsPerDay(1);
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(mockedCustomer));
        Mockito.when(customerMap.get(anyLong())).thenReturn(mockedCustomer);
        Mockito.when(customerMap.containsKey(anyLong())).thenReturn(true);

        // When
        Customer result = customerService.removeProposal(id);

        Assertions.assertEquals(result.getProposalsPerDay(), 0);
    }
    
    @Test
    public void testCustomeNotExist() {
        // Arrange
        Long customerId = 1234L;
        Mockito.when(customerMap.containsKey(customerId)).thenReturn(false); // Mocking customerMap to be empty

        // Act
        Assertions.assertThrows(RuntimeException.class, () -> customerService.addProposal(customerId));
        Assertions.assertThrows(RuntimeException.class, () -> customerService.removeProposal(customerId));
        Assertions.assertThrows(RuntimeException.class, () -> customerService.blockCustomer(customerId)); 
        Assertions.assertThrows(RuntimeException.class, () -> customerService.unblockCustomer(customerId));

        
    }
}
