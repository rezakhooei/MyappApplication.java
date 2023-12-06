package com.tdxir.myapp.controller;

import com.google.gson.JsonObject;
import com.tdxir.myapp.service.FileStorageService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@RequestMapping("/api/uploadFile")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    private static final String EXTENSION = ".wav";
    private static final String SERVER_LOCATION = "/opt/tomcat/uploads";

    public FileUploadController(FileStorageService fileStorageService) {

        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<JSONObject> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("inf1") String inf1 ,@RequestParam("inf2") String inf2,@RequestParam("inf3") String inf3,@RequestParam("inf4") String inf4 )throws IOException
     {

         JSONObject jsonObject = new JSONObject();


         JSONArray array = new JSONArray();
        /*array.add("element_1");
        array.add("element_2");
        array.add("element_3");*/
         jsonObject.put("inf1","صدای شما ذخیره شد");
         jsonObject.put("inf2","اطلاعات شماره 2");
         jsonObject.put("inf3","اطلاعات شماره 3");
         jsonObject.put("inf4","اطلاعات شماره 4");

        String fileName = fileStorageService.storeFile(file,inf1,inf2,inf3,inf4);
        //fileName="monshi.mp3";
        inf1="صدای شما ذخیره شد";
        UploadResponse uploadResponse = new UploadResponse(fileName, inf1,inf2,inf3,inf4);

         String image=fileName;//"file";
         File filereply = new File(SERVER_LOCATION + File.separator + image );//+ EXTENSION);

         HttpHeaders header = new HttpHeaders();
         header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

         header.add("Cache-Control", "no-cache, no-store, must-revalidate");
         header.add("Pragma", "no-cache");
         header.add("Expires", "0");

         Path path = Paths.get(filereply.getAbsolutePath());
         ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

         byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

         jsonObject.put("file_content",resource.getByteArray());


         InputStream is = new ByteArrayInputStream(encoder);
         InputStreamResource resource1 = new InputStreamResource(is);

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
         ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
         headers.setContentDisposition(disposition);

         return new ResponseEntity<>(jsonObject, headers, HttpStatus.OK);





    }
}