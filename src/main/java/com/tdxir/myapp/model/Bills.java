package com.tdxir.myapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String date;
    private LocalDate datePay;
    private Long idDoc;
    private String idInvoice;
    private Long price;
    @Enumerated(EnumType.STRING)
    private  Operation billKind=Operation.SELL;//sell or buy
    @Enumerated(EnumType.STRING)
    private Operation payKind=Operation.CASH;//cash or product or checque

    private  String userName;
    private String fileImage;
    private Boolean finish=false;
    private String description;


}
