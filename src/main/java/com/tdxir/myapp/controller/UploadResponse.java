package com.tdxir.myapp.controller;

public class UploadResponse {
    private final String fileName;
    private final String inf1;
    private final String inf2;
    private final String inf3;
    private final String inf4;



    public UploadResponse(String fileName, String inf1,String inf2,String inf3,String inf4) {
        this.fileName = fileName;
        this.inf1 = inf1;
        this.inf2 = inf2;
        this.inf3 = inf3;
        this.inf4 = inf4;


    }



    public String getFileName() {
        return fileName;
    }
    public String getInf1() { return inf1;}
    public String getInf2() { return inf2;}
    public String getInf3() { return inf3;}
    public String getInf4() { return inf4;}
}