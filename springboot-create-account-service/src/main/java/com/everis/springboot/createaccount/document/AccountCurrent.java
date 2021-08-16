package com.everis.springboot.createaccount.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountCurrent{

    private String id;
    private String idClient;
    private String type;
    private Date accountCreate;
    private double accountMount;
    private double accountCost;
}
