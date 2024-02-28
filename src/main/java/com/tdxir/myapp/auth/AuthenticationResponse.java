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
    private  Boolean img;
    private  String checkBoxCount;
    private  String radioButtonCount;

}
