package com.tdxir.myapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class RadioButton {
    public int id;
    public String name;
    public Boolean isSelect=false;

    public RadioButton() {

    }

    public RadioButton(int id, String name, boolean isSelect) {
        this.id=id;
        this.name=name;
        this.isSelect=isSelect;
    }
}
