package com.everis.springboot.product.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.everis.springboot.product.document.ProductDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
	
	Mono<ResponseEntity<Map<String,Object>>> saveProduct(String id, ProductDocument client);
	
	Flux<ProductDocument> findProductsByClient(String id);
	
	Mono<ProductDocument> findProductsById(String id);
	
	Mono<ResponseEntity<Map<String,Object>>> updateProduct(String id,ProductDocument client);
	
	ResponseEntity<String> deleteProduct(String id);
}
