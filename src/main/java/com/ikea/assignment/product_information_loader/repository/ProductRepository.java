package com.ikea.assignment.product_information_loader.repository;

import com.ikea.assignment.product_information_loader.model.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CassandraRepository<Product, String> {
}
