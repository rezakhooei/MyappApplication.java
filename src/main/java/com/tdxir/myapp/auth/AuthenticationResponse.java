package com.tdxir.myapp.auth;

import com.tdxir.myapp.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String paramCount;
    private  String paramTime;

    private Panels radioButtonList;
    //private Panel panel;
    private ArrayList<CheckBox> checkBoxes;

}
