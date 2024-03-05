package com.tdxir.myapp.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
//@AllArgsConstructor

public class Panels {

    //public String name;
    ArrayList<RadioButton> panels;

    public Panels() {

    }

    public Panels( ArrayList<RadioButton> panels) {

     //   this.name=name;
        this.panels=panels;
    }
}
