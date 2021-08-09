package com.everis.springboot.fixedterm.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.everis.springboot.fixedterm.documents.FixedTermDocument;
import com.everis.springboot.fixedterm.service.FixedTermService;

import reactor.core.publisher.Mono;

@RestController
public class FixedTermController {
	
	@Autowired
	private FixedTermService fixedTermService;
	
	@PostMapping("/saveAccount")
	public Mono<FixedTermDocument> saveAccount(@Valid @RequestBody FixedTermDocument document){
		System.out.println("Entro al metodo guardar cuenta");
		return fixedTermService.createAccount(document);
	}

}
