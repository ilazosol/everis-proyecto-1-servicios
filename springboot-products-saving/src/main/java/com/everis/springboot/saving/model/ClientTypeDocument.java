package com.everis.springboot.saving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientTypeDocument {
    @Id
    private String id;

    private String description;

}
