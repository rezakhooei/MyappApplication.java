package com.tdxir.myapp.controller;

//import com.google.gson.JsonObject;
import com.tdxir.myapp.nlp.training.MakeTsv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/train")
public class MakeTxtTrainController {


    @Autowired
    private  MakeTsv makeTsv;
    private static final String EXTENSION = ".wav";
    private static final String SERVER_LOCATION = "/opt/tomcat/uploads";



    @PostMapping
    public String maketxttrain(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("inf1") String inf1 ,@RequestParam("inf2") String inf2,@RequestParam("inf3") String inf3,@RequestParam("inf4") String inf4 )throws IOException
     {

        // MakeTsv makeTsv=new MakeTsv();
         makeTsv.pretrain();
         //makeTsv.craeteTsv(file);//.getResource().getFile());

/*
        String fileName = fileStorageService.storeFile(file,inf1,inf2,inf3,inf4);
        //fileName="monshi.mp3";
        inf1="افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد";
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

         jsonObjectMain.put("file_content",resource.getByteArray());
         // array.add(new JSONObject(jsonObject));
         // jsonObject.clear();

         InputStream is = new ByteArrayInputStream(encoder);
         InputStreamResource resource1 = new InputStreamResource(is);

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
         ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
         headers.setContentDisposition(disposition);
*/
         return "Ok JOOjoo";





    }
}