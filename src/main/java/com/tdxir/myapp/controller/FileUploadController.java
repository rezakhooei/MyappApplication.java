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
         JSONObject jsonObjectMain=new JSONObject();
         JSONObject jsonObject = new JSONObject();


         JSONArray array = new JSONArray();
        /*array.add("element_1");
        array.add("element_2");
        array.add("element_3");*/
         for (int i=1;i<=3;++i){
             jsonObject.put("inf_id",String.valueOf(i));
             jsonObject.put("inf_text",String.valueOf(i)+"افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد. او دریافته بود که بسیاری از نظرات و قضاوت های ما از تفکرات جامعه نشأت می گیرد، از چیزی که یونانی ها آن را «دوکسا» یا «عقل جمعی حاکم» می نامند. افلاطون در 36 کتابی که نوشت، بارها و بارها نشان داد که این «عقل جمعی حاکم» می تواند پر از اشتباه، تبعیض و خرافه باشد و تفکرات شایع در مورد عشق، شهرت، پول و یا خوبی، چندان با منطق و واقعیت همخوانی ندارن" +
                                   "افلاطون همچنین دریافته بود که چگونه انسان های مغرور، تحت سلطه ی غرایز و احساسات خود هستند و آن ها را با افرادی مقایسه می کرد که توسط اسب هایی وحشی که چشم هایشان پوشانده شده، به این سو و آن سو کشیده می شوند.رویای موجود در پسِ مفهوم عشق این است که می توانیم با نزدیک شدن به چنین افرادی، اندکی مانند آن ها شویم. عشق در نظر افلاطون، نوعی آموزش است. او عقیده دارد کسی که به معنای واقعی کلمه به فردی دیگر عشق می ورزد، تمایل خواهد داشت که توسط معشوق خود به فرد بهتری تبدیل شود. این یعنی شخص باید با کسی باشد که بخشی گمشده از هستی او را در اختیار دارد: ویژگی های خوبی که خودمان از آن ها بی بهره ایم." +
                                     "افلاطون از ما می خواهد این را بپذیریم که کامل نیستیم و تمایل داشته باشیم که ویژگی های خوب دیگران را در خودمان پرورش دهیم. ");
             array.add(new JSONObject(jsonObject));
             jsonObject.clear();
         }
         jsonObjectMain.put("inf",array);
        // array.add(jsonObject);




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

         return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);





    }
}