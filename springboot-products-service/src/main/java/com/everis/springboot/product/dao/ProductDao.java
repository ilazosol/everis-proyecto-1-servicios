package com.everis.springboot.product.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.product.document.ProductDocument;

import reactor.core.publisher.Flux;

public interface ProductDao extends ReactiveMongoRepository<ProductDocument, String> {
	Flux<ProductDocument> findByClient(String client);
}
