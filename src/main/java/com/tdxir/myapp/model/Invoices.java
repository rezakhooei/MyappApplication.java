package com.tdxir.myapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Invoices {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDoc")
    private Long idDoc;//shomare sanad hesabdari
    private String idInvoice;//shomare faktor
    private String userName;
    private String sellerID;
    private String date;
    private LocalDate dateInvoice;
    private Long numProduct;//tedad kala
    private Long idCheck;
    private Long price;
    private String fileImage;
    private Integer companyId;
    private Boolean paid=false;
    private String sellOrBuy="SELL";
    @Column
    private Boolean completed=false;




}
