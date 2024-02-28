package com.tdxir.myapp.auth;

import com.tdxir.myapp.model.UserKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String paramCount;
    private  String paramTime;

    private  String paramPanelRadioButton;
    private  String paramCheckBox;


}
