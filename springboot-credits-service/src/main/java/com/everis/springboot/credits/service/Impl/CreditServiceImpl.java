package com.everis.springboot.credits.service.Impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.credits.dao.CreditDao;
import com.everis.springboot.credits.document.ClientDocument;
import com.everis.springboot.credits.document.CreditDocument;
import com.everis.springboot.credits.document.MovementDocument;
import com.everis.springboot.credits.service.CreditService;

import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private CreditDao creditDao;

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> saveCredit(String id, CreditDocument credit) {
		Map<String, Object> response = new HashMap<>();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		
		Mono<ClientDocument> client = webClientBuilder.build().get()
				.uri("http://localhost:8090/api/client/client/"+id)
				.retrieve()
				.bodyToMono(ClientDocument.class);
		
		
		return creditDao.findByIdClient(id).collectList().flatMap( credits -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				Integer creditAccount = 0;
				Integer creditCard = 0;
				
				System.out.println(c.toString());
				
				if(c.getClient_type().getDescription().equals("Personal") ) {
					for (CreditDocument cre : credits) {
						if(cre.getCreditType().equals("Credito Personal")) {
							creditAccount++;
						}
						if(cre.getCreditType().equals("Tarjeta de Credito")) {
							creditCard++;
						}
	
						if(credit.getCreditType().equals("Credito Personal") && creditAccount>0) {
							response.put("mensaje", "No puede crear el credito, un cliente personal no puede tener más de un credito");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(credit.getCreditType().equals("Tarjeta de Credito") && creditCard>0) {
							response.put("mensaje", "No puede crear la tarjeta, un cliente personal no puede tener más de una tarjeta de credito");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
				}else if(c.getClient_type().getDescription().equals("Empresarial")) {
					for (CreditDocument cre : credits) {
						if(cre.getCreditType().equals("Tarjeta de Credito")) {
							creditCard++;
						}
						if(credit.getCreditType().equals("Tarjeta de Credito") && creditCard>0) {
							response.put("mensaje", "No puede crear la tarjeta, un cliente empresarial no puede tener más de una tarjeta de credito");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
				}else if(!c.getClient_type().getDescription().equals("Empresarial") && !c.getClient_type().getDescription().equals("Personal")) {
					response.put("mensaje", "El tipo de cliente incorrecto");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}
				Date date = Calendar.getInstance().getTime();
				credit.setIdClient(id);
				credit.setCreationDate(dateFormat.format(date));
				credit.setCreditLimit(credit.getBalance());
				credit.setCreditPaid(0.0);
				
				return creditDao.save(credit).flatMap( cre -> {
					response.put("creditSaved", cre);
					response.put("mensaje", "Credito registrada con exito");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
				});
				
			});
			
			
			return res;
		});
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> payCredit(String idCredit, Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	
		
		return creditDao.findById(idCredit).flatMap( c -> {
			
			
			if(c.getCreditPaid() == c.getCreditLimit()) {
				response.put("mensaje", "Usted ya termino de pagar el credito");
				return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
			}else if( c.getCreditPaid() + cantidad > c.getCreditLimit()) {
				response.put("mensaje", "No puede pagar mas del limite establecido del credito");
				return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
			}else {
				c.setCreditPaid(c.getCreditPaid() + cantidad);
				
				return creditDao.save(c).flatMap(cre -> {
					Date date = Calendar.getInstance().getTime();
					MovementDocument movement = MovementDocument.builder()
							.tipoMovimiento("Pago Credito")
							.tipoProducto(
									cre.getCreditType().equals("Credito Personal") ? "Credito Personal" : 
									cre.getCreditType().equals("Credito Empresarial") ? "Credito Empresarial" :
									"Tarjeta de Credito")
							.fechaMovimiento(dateFormat.format(date))
							.idCuenta(idCredit)
							.idCliente(cre.getIdClient())
							.build();
					
					webClientBuilder.build().post()
					.uri("http://localhost:8090/api/movement/saveMovement")
					.body(Mono.just(movement), MovementDocument.class)
					.retrieve().bodyToMono(MovementDocument.class).subscribe();
					
					
					response.put("mensaje", "Se hizo el pago del credito correctamente");
					response.put("credito", cre);
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				});
			}
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> spendCredit(String idCredit, Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	
		
		return creditDao.findById(idCredit).flatMap( c -> {
			if(c.getBalance() - cantidad < 0) {
				response.put("mensaje", "No puede gastar más del crédito dado");
				return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
			}else {
				c.setBalance(c.getBalance() - cantidad);
				
				return creditDao.save(c).flatMap(cre -> {
					Date date = Calendar.getInstance().getTime();
					MovementDocument movement = MovementDocument.builder()
							.tipoMovimiento("Consumo Credito")
							.tipoProducto(
									cre.getCreditType().equals("Credito Personal") ? "Credito Personal" : 
									cre.getCreditType().equals("Credito Empresarial") ? "Credito Empresarial" :
									"Tarjeta de Credito")
							.fechaMovimiento(dateFormat.format(date))
							.idCuenta(idCredit)
							.idCliente(cre.getIdClient())
							.build();
					
					webClientBuilder.build().post()
					.uri("http://localhost:8090/api/movement/saveMovement")
					.body(Mono.just(movement), MovementDocument.class)
					.retrieve().bodyToMono(MovementDocument.class).subscribe();
					
					
					response.put("mensaje", "Se hizo el gasto del credito correctamente");
					response.put("credito", cre);
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				});
			}
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> consultCredit(String idCredit) {
		Map<String, Object> response = new HashMap<>();
		
		return creditDao.findById(idCredit).flatMap( c -> {
			
			
			response.put("mensaje", "El saldo del credito es: S/."+c.getBalance());
			return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
