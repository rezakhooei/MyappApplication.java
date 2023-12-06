package com.tdxir.myapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class UsersData {
    @Id
    @GeneratedValue
    private Long id;
    private String userid;
    private Date date;
    private  String inf1;
    private  String inf2;
    private  String inf3;
    private  String inf4;
    private String filename;

}
