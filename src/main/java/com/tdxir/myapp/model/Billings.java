package com.tdxir.myapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Billings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String date;
    private LocalDate datePay;
    private Long idDoc;
    private Long idInvoice;
    @Enumerated(EnumType.STRING)
    private  Operation billKind=Operation.SELL;//sell or buy
    private Long price;
    private Operation payKind=Operation.CASH;//cash or product or checque
    private  String userId;
    private String fileImage;
    private Boolean finish=false;
    private String desc;



}
