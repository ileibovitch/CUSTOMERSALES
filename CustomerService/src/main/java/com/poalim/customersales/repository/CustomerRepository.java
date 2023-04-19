package com.poalim.customersales.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poalim.customersales.entities.Customer;



@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByCustomerId(Long customerId);

    Customer findByCustomerIdAndBlockedFalse(Long customerId);

    List<Customer> findByBlockedTrue();
}
