package com.tdxir.myapp.controller;

import java.util.List;

public class UploadResponse {
    private final String fileNameSound;
    private final String fileNamePic;
    private final List<String> inf;




    public UploadResponse(String fileNameSound, String fileNamePic ,List<String> inf) {
        this.fileNameSound = fileNameSound;
        this.fileNamePic = fileNamePic;
        this.inf = inf;


    }



    public String getFileNameSound() {
        return fileNameSound;
    }
    public String getFileNamePic() {
        return fileNamePic;
    }
    public List<String> getInf() { return inf;}

}