package com.tdxir.myapp.model;

import com.tosan.tools.jalali.JalaliDate;
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
public class BuyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String email;
    private String date;
    private String sku;
    private Long stock;
    private Long oldStock;
    private Long price;
    private Long oldPrice;
    @NotNull
    private String idInvoice;
    private Integer companyId;


}
