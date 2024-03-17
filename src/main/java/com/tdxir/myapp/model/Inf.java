package com.tdxir.myapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@AllArgsConstructor

public class Inf {
    public int id;
    public String name;
    public String defaulfValue="inf";

    public Inf() {

    }

    public Inf(int id, String name, String defaulfValue) {
        this.id=id;
        this.name=name;
        this.defaulfValue=defaulfValue;
    }
}
