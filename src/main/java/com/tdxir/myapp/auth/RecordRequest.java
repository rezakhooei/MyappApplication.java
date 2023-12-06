package com.tdxir.myapp.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordRequest {

    private  String userid;
    private  String date;
    private  String inf1;
    private  String inf2;
    private  String inf3;
    private  String inf4;
    private String fileName;
}
