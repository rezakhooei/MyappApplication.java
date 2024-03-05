package com.tdxir.myapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor

public class CheckBox {
    public int id;
    public String name;
    public Boolean defaulfValue=false;

    public CheckBox() {

    }

    public CheckBox(int id, String name, boolean defaulfValue) {
        this.id=id;
        this.name=name;
        this.defaulfValue=defaulfValue;
    }
}
