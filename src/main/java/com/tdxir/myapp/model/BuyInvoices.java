package com.tdxir.myapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class BuyInvoices {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDoc")
    private Long idDoc;//shomare sanad hesabdari
    private String idInvoice;//shomare faktor
    private String userName;
    private String sellerID;
    private Date date;
    private Long numProduct;//tedad kala

    private Long price;
    private String fileImage;

}
