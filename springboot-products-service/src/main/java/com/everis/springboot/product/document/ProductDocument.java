package com.everis.springboot.product.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "products")
public class ProductDocument {
	
	@Id
	private String id;
	
	private ProductTypeDocument product_type;
	
	private String client;
	
	private Double mount;

}
