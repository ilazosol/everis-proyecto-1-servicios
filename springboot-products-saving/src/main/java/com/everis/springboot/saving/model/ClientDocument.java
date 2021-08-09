package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDocument {

    private String id;

    private String first_name;

    private String last_name;

    private ClientTypeDocument client_type;

    public ClientDocument(String first_name, String last_name, ClientTypeDocument client_type) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.client_type = client_type;
    }

}
