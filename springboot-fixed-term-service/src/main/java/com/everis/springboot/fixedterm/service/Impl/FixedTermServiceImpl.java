package com.everis.springboot.fixedterm.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.everis.springboot.fixedterm.dao.FixedTermDao;
import com.everis.springboot.fixedterm.documents.FixedTermDocument;
import com.everis.springboot.fixedterm.service.FixedTermService;

import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {
	
	@Autowired
	private FixedTermDao fixedTermDao;

	@Override
	public Mono<FixedTermDocument> createAccount(FixedTermDocument document) {
		return fixedTermDao.save(document);
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta,Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		
		return fixedTermDao.findById(idCuenta).flatMap( c -> {
			c.setSaldo(c.getSaldo() + cantidad);
			return fixedTermDao.save(c).flatMap(acc -> {
				response.put("mensaje", "Se hizo el deposito exitosamente");
				response.put("cuenta", acc);
				return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			});
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> retirar(String idCuenta,Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		
		return fixedTermDao.findById(idCuenta).flatMap( c -> {
			if(c.getSaldo() - cantidad < 0) {
				response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
				return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			}else {
				c.setSaldo(c.getSaldo() - cantidad);
				return fixedTermDao.save(c).flatMap(acc -> {
					response.put("mensaje", "Se hizo el retiro exitosamente");
					response.put("cuenta", acc);
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				});
			}
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
