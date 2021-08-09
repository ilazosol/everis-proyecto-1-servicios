package com.everis.springboot.createaccount.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedTermDocument {
	
	private String id;
	
	private Double saldo;
	
	private String fecha_creacion;
	
	private String id_cliente;
	
	private Integer dia_retiro;
}
