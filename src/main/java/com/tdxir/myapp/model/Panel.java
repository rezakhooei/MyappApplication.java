package com.tdxir.myapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class Panel {

    public String name;
    Panels panels;

    public Panel() {

    }

    public Panel(Panels panels) {

        this.panels = panels;
        this.name=name;

    }
}
