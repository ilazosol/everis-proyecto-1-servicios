package com.everis.springboot.product.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.product.dao.ProductDao;
import com.everis.springboot.product.document.ClientDocument;
import com.everis.springboot.product.document.ProductDocument;
import com.everis.springboot.product.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Override
	public Mono<ResponseEntity<Map<String,Object>>> saveProduct(String id, ProductDocument product) {
		Map<String, Object> response = new HashMap<>();
		
		Mono<ClientDocument> client = webClientBuilder.build().get()
				.uri("http://localhost:8090/api/client/client/"+id)
				.retrieve()
				.bodyToMono(ClientDocument.class);
		
		
		return productDao.findByClient(id).collectList().flatMap( productos -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				Integer cAhorro = 0;
				Integer cCorriente = 0;
				
				System.out.println(c.toString());
				
				if(c.getClient_type().getDescription().equals("Personal") ) {
					for (ProductDocument prod : productos) {
						if(prod.getProduct_type().getDescription().equals("Cuenta de Ahorro")) {
							cAhorro++;
						}
						if(prod.getProduct_type().getDescription().equals("Cuenta Corriente")) {
							cCorriente++;
						}
						if(product.getProduct_type().getDescription().equals("Cuenta de Ahorro") && cAhorro>0) {
							response.put("mensaje", "No puede guardar un cliente con mas de una cuenta de ahorro");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(product.getProduct_type().getDescription().equals("Cuenta Corriente") && cCorriente>0) {
							response.put("mensaje", "No puede guardar un cliente con mas de una cuenta corriente");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
					product.setClient(id);
					
					
					
					
					return productDao.save(product).flatMap( p -> {
						response.put("productSaved", p);
						response.put("mensaje", "Cuenta registrada con exito");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
					});
					
				}else if(c.getClient_type().getDescription().equals("Empresarial")) {
					for (ProductDocument prod : productos) {
						if(prod.getProduct_type().getDescription().equals("Cuenta de Ahorro")) {
							response.put("mensaje", "Un usuario empresarial no puede tener cuenta de ahorro");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(prod.getProduct_type().getDescription().equals("Cuenta Plazo Fijo")) {
							response.put("mensaje", "Un usuario empresarial no puede tener cuenta a plazo fijo");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
					product.setClient(id);
					
					return productDao.save(product).flatMap( p -> {
						response.put("productSaved", p);
						response.put("mensaje", "Cuenta registrada con exito");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
					});
				}else {
					System.out.println("Entro aqui 3");
					response.put("mensaje", "Ocurrio un error al guardar");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}
			});
			
			
			return res;
		});
	}

	@Override
	public Flux<ProductDocument> findProductsByClient(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<ProductDocument> findProductsById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> updateProduct(String id, ProductDocument client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> deleteProduct(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
