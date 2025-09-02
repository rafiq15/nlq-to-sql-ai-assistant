package com.bi.assistant.repository;

import com.bi.assistant.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByCustomerSegment(String segment);
    
    List<Customer> findByCity(String city);
    
    @Query("SELECT DISTINCT c.customerSegment FROM Customer c WHERE c.customerSegment IS NOT NULL ORDER BY c.customerSegment")
    List<String> findAllCustomerSegments();
    
    @Query("SELECT DISTINCT c.country FROM Customer c WHERE c.country IS NOT NULL ORDER BY c.country")
    List<String> findAllCountries();
}
