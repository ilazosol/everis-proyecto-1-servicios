package com.everis.springboot.createaccount.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingDocument {

    private String id;
    private String idClient;
    private String type;
    private Date accountCreate;
    private double accountMount;
    private double movMonth;


}
