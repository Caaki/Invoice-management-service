package com.app.ares.repository;

import com.app.ares.domain.Customer;
import com.app.ares.domain.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {

    Page<Customer> findByNameContaining(String name, Pageable pageable);

}
