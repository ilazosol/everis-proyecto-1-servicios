package com.everis.springboot.product.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTypeDocument {

	@Id
	private String id;

	private String description;
}
