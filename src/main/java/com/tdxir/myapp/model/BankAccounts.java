package com.tdxir.myapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class BankAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    private String nameBank;
    @NotNull
    private String nameBranch;
    @NotNull
    private String codeBranch;
    @NotNull
    private String owner;
    @NotNull
    private String account;
    private String card;
    @NotNull
    private String sheba;
}
