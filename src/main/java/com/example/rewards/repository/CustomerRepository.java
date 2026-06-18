package com.example.rewards.repository;

import com.example.rewards.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for performing database operations
 * on customer transactions.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
