package com.encryption.com.Repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.encryption.com.pojo.Customer;

@Repository
public interface CustomerRepo extends ReactiveCrudRepository<Customer, String>{

}
