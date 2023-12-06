package com.tdxir.myapp.controller;

import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.service.HistoryService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DownloadFile {

    private static final String EXTENSION = ".mp3";
    private static final String EXTENSION1 = ".mp3";
    private static final String SERVER_LOCATION ="/opt/tomcat/uploads";// "uploads";
    @Autowired
    HistoryService historyService;

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<JSONArray> download(/*@RequestParam String countRequest*/) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<UsersData> usersData=historyService.usersdatahistory(authentication.getName(), 1);

        JSONObject jsonObject = new JSONObject();


        JSONArray array= new JSONArray();




        //select filename from users_data where userid=userid;
        int lastrecordindex =usersData.size();
         String image;
         String userid;
        for(int i=1;i<=3;++i) {
            userid=usersData.get(lastrecordindex-i).getUserid();
            if(userid.equals("javadghane18@gmail.com")){

                jsonObject.put("inf1","اطلاعات شماره 1");
                jsonObject.put("inf2","اطلاعات شماره 2");
            }
            else {
                jsonObject.put("inf1", usersData.get(lastrecordindex - i).getInf1());
                jsonObject.put("inf2", usersData.get(lastrecordindex - i).getInf2());
            }

            jsonObject.put("inf3","اطلاعات شماره 3");
            jsonObject.put("inf4","اطلاعات شماره 4");

            image = usersData.get(lastrecordindex - i).getFilename();//.indexOf(33)[u].getFilename();

            File filereply1 = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);
            Path path1 = Paths.get(filereply1.getAbsolutePath());
            ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));
            byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
            //array.add(resource1.getByteArray());
            jsonObject.put("file_content",resource1.getByteArray());



            array.add(new JSONObject(jsonObject));
            //array.set(i-1,jsonObject);
           // array.add(jsonObject);
            //array=new JSONArray();
           //jsonObject.clear();
/*
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reza.mp3");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
*/

            //Path path = Paths.get(filereply.getAbsolutePath());


        }




        //jsonObject.put("file_content",array);


        //jsonObject.put("file_content",array);
        //jsonObject.put("file_content"+String.valueOf(i),resource1.getByteArray());
/*
        FileWriter file = new FileWriter("E:/json_array_output1.json");
        file.write(jsonObject.toJSONString());
        file.close();*/

      //  InputStream is = new ByteArrayInputStream(encoder);

        //InputStreamResource resource1 = new InputStreamResource(is);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        headers.setContentDisposition(disposition);
/*
        ResponseFiles responseFiles=new ResponseFiles();

         responseFiles= ResponseFiles.builder()
                .file_name(path.getFileName())
                .file_content(is)
                .build();*/

        return  new ResponseEntity<>(array,headers,HttpStatus.OK);



       // return new ResponseEntity<>(resource1, headers, HttpStatus.OK);
        /*return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);*/
    }

}

