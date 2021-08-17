package com.everis.springboot.saving.service.Impl;

import com.everis.springboot.saving.dao.SavingDao;
import com.everis.springboot.saving.model.ClientDocument;
import com.everis.springboot.saving.model.ProductDocument;
import com.everis.springboot.saving.model.SavingDocument;
import com.everis.springboot.saving.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SavingServiceImpl implements SavingService {

    @Autowired
    private SavingDao savingDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> saveSaving(String id, SavingDocument saving) {
        Map<String, Object> response = new HashMap<>();

        Mono<ProductDocument> product = webClientBuilder.build().get()
                .uri("http://localhost:8090/api/account/findAccount/"+id)
                .retrieve()
                .bodyToMono(ProductDocument.class);

        return savingDao.findByClient(id).collectList().flatMap( savings -> {

            Mono<ResponseEntity<Map<String,Object>>> res = product.flatMap(c -> {


                if(!c.getAccount_type().equals("Cuenta de Ahorro")){
                    response.put("mensaje", "No puede crear la cuenta, debido a que el cliente es empresarial");
                    return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
                }

                Mono<ClientDocument> client = webClientBuilder.build().get()
                .uri("http://localhost:8090/api/client/client/"+c.getClient())
                .retrieve()
                .bodyToMono(ClientDocument.class);

                    Mono<ResponseEntity<Map<String,Object>>> resClient = client.flatMap(cli -> {

                        if (savings.size() == 0 && cli.getClient_type().getDescription().equals("Personal")) {
                            saving.setProduct(c.getId());
                            saving.setClient(c.getClient());
                                return savingDao.save(saving).flatMap(p ->{
                                    response.put("savingSaved", p);
                                    response.put("mensaje", "Cuenta de Ahorros registrada con exito");
                                    return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                                });

                        } else {
                            response.put("mensaje", "No se puede guardar porque ya existe este producto o no es un cliente Personal");
                            return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
                        }
                    });

                    return resClient;
            });

            return res;


        });
    }

    @Override
    public Flux<SavingDocument> findSavingsByProduct(String id) {
        return null;
    }

    @Override
    public Mono<SavingDocument> findSaving(String id) {
        return savingDao.findById(id);
    }


    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateSaving(String id, SavingDocument saving) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteSaving(String id) {
        return null;
    }
}
