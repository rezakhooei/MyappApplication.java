package com.tdxir.myapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wkh_postmeta",schema = "test")
public class wkh_postmeta {
    @Id
    @GeneratedValue
    long metaid;
    long post_id;
    String meta_key;
    String meta_value;

}
