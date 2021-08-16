package com.everis.springboot.clients.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.everis.springboot.clients.dao.ClientDao;
import com.everis.springboot.clients.documents.ClientDocument;
import com.everis.springboot.clients.service.ClientService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {
	
	@Autowired
	private ClientDao clientDao;

	public static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

	@Override
	public Mono<ResponseEntity<?>> saveClient(ClientDocument client) {
		Map<String, Object> response = new HashMap<>();
		return clientDao.save(client).map( c -> {
			response.put("mensaje", "Se registró el cliente correctamente");
			response.put("cliente", c);
			log.info("Cliente: " + c.getFirst_name() + " " + c.getLast_name() + " Creado!");
			return new ResponseEntity<>(response,HttpStatus.OK);
		});
	}

	@Override
	public Flux<ClientDocument> findClients() {
		return clientDao.findAll().doOnNext(c -> {
			log.info("Cliente: " + c.getFirst_name() + " " + c.getLast_name());
		});
	}

	@Override
	public Mono<ClientDocument> findClient(String id) {
		return clientDao.findById(id).doOnNext(c -> {
			log.info("Cliente: " + c.getFirst_name() + " " + c.getLast_name());
		});
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> updateClient(String id, ClientDocument client) {
		Map<String, Object> response = new HashMap<>();
	
		return clientDao.findById(id).flatMap(c -> {
			c.setFirst_name(client.getFirst_name());
			c.setLast_name(client.getLast_name());
			c.setClient_type(client.getClient_type());
			return clientDao.save(c);
		}).map(clientUpdated -> {
			response.put("mensaje", "Se actualizo el cliente correctamente");
			response.put("cliente", clientUpdated);
			log.info("Cliente: " + clientUpdated.getFirst_name() + " " + clientUpdated.getLast_name() + " Actualizado!");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
			
	}

	@Override
	public ResponseEntity<String> deleteClient(String id) {
		try {
//			clientDao.deleteById(id).subscribe();
			clientDao.findById(id).flatMap(c -> {
				log.info("Cliente: " + c.getFirst_name() + " " + c.getLast_name() + " Eliminado!");
				return clientDao.deleteById(id);
			}).subscribe();
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar cliente", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Cliente eliminado con éxito", HttpStatus.OK);
	}

}
