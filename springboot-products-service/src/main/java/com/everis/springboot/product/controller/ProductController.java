package com.everis.springboot.product.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.everis.springboot.product.document.ProductDocument;
import com.everis.springboot.product.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping("/saveProduct/{id}")
	public Mono<ResponseEntity<Map<String,Object>>> saveProduct(@PathVariable String id,@Valid @RequestBody ProductDocument product){
		System.out.println("Entro al metodo guardar");
		return productService.saveProduct(id,product);
	}
	
	
	

}
