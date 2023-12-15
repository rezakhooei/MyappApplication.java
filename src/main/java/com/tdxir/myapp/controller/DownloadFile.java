package com.tdxir.myapp.controller;

import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.service.HistoryService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hibernate.internal.util.collections.JoinedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DownloadFile {

    private static final String EXTENSION = ".mp3";
    private static final String EXTENSION1 = ".mp3";
    private static final String SERVER_LOCATION ="/opt/tomcat/uploads";// "uploads";
    @Autowired
    HistoryService historyService;

    @RequestMapping(path = "/download", method = RequestMethod.POST)
    public ResponseEntity<JSONArray> download(/*@RequestParam String countRequest*/) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<UsersData> usersData=historyService.usersdatahistory(authentication.getName(), 1);

        JSONObject jsonObject = new JSONObject();


        ArrayList<JSONObject> array= new ArrayList<JSONObject>();
        ArrayList<JSONArray> arrayInfList=new ArrayList<>();//JSONArray();JoinedList<JSONArray>();//
        JSONArray arrayInf=new JSONArray();
        for(int z=1;z<=3;++z)
        arrayInf.add(new JSONObject());


        //select filename from users_data where userid=userid;
        int lastrecordindex =usersData.size();
         String image;
         String userid;

        JSONArray final_array=new JSONArray();
        for(int info=1;info<=3;++info) {
            userid=usersData.get(lastrecordindex-info).getUserid();
            JSONObject jonInfo = new JSONObject();
            JSONArray arrayInfo=new JSONArray();
            for (int id=1;id<=3;++id){
                JSONObject jonId = new JSONObject();
                jonId.put("inf_id"+String.valueOf(info),id);
                if(userid.equals("javadghane18@gmail.com")){

                    jonId.put("inf_text","اطلاعات شماره "+String.valueOf(info) );
                }
                else {
                    if(id==1)
                    jonId.put("inf_text",usersData.get(lastrecordindex - info).getInf1() );
                    else if (id==1) {
                        jonId.put("inf_text",usersData.get(lastrecordindex - info).getInf2() );
                    } else if (id==1) {
                        jonId.put("inf_text",usersData.get(lastrecordindex - info).getInf3() );
                    }
                }

                arrayInfo.add(jonId);
            }
            jonInfo.put("inf",arrayInfo);


       /* for(int i=1;i<=3;++i) {

            for (int j=1;j<=3;++j){
                jsonObject.put("inf_id",String.valueOf(j));
                jsonObject.put("inf_text",String.valueOf(j)+"افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد. او دریافته بود که بسیاری از نظرات و قضاوت های ما از تفکرات جامعه نشأت می گیرد، از چیزی که یونانی ها آن را «دوکسا» یا «عقل جمعی حاکم» می نامند. افلاطون در 36 کتابی که نوشت، بارها و بارها نشان داد که این «عقل جمعی حاکم» می تواند پر از اشتباه، تبعیض و خرافه باشد و تفکرات شایع در مورد عشق، شهرت، پول و یا خوبی، چندان با منطق و واقعیت همخوانی ندارند" +
                        "افلاطون همچنین دریافته بود که چگونه انسان های مغرور، تحت سلطه ی غرایز و احساسات خود هستند و آن ها را با افرادی مقایسه می کرد که توسط اسب هایی وحشی که چشم هایشان پوشانده شده، به این سو و آن سو کشیده می شوند.رویای موجود در پسِ مفهوم عشق این است که می توانیم با نزدیک شدن به چنین افرادی، اندکی مانند آن ها شویم. عشق در نظر افلاطون، نوعی آموزش است. او عقیده دارد کسی که به معنای واقعی کلمه به فردی دیگر عشق می ورزد، تمایل خواهد داشت که توسط معشوق خود به فرد بهتری تبدیل شود. این یعنی شخص باید با کسی باشد که بخشی گمشده از هستی او را در اختیار دارد: ویژگی های خوبی که خودمان از آن ها بی بهره ایم." +
                        "افلاطون از ما می خواهد این را بپذیریم که کامل نیستیم و تمایل داشته باشیم که ویژگی های خوب دیگران را در خودمان پرورش دهیم. ");
                arrayInf.set(j-1,new JSONObject(jsonObject));

                jsonObject.clear();
            }

             arrayInfList.add(arrayInf);
            jsonObject.put("inf",arrayInfList.get(i-1));
*/
           /* userid=usersData.get(lastrecordindex-i).getUserid();
            if(userid.equals("javadghane18@gmail.com")){

                jsonObject.put("inf1","اطلاعات شماره 1");
                jsonObject.put("inf2","اطلاعات شماره 2");
            }
            else {
                jsonObject.put("inf1", usersData.get(lastrecordindex - i).getInf1());
                jsonObject.put("inf2", usersData.get(lastrecordindex - i).getInf2());
            }

            jsonObject.put("inf3","اطلاعات شماره 3");
            jsonObject.put("inf4","اطلاعات شماره 4");*/

            image = usersData.get(lastrecordindex - info).getFilename();//.indexOf(33)[u].getFilename();

            File filereply1 = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);
            Path path1 = Paths.get(filereply1.getAbsolutePath());
            ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));
            byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
/*
            jsonObject.put("file_content",resource1.getByteArray());
            array.add(new JSONObject(jsonObject));
            jsonObject.clear();*/

            jonInfo.put("file_content",resource1.getByteArray());
            final_array.add(jonInfo);
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

        return  new ResponseEntity<>(final_array,headers,HttpStatus.OK);



       // return new ResponseEntity<>(resource1, headers, HttpStatus.OK);
        /*return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);*/
    }



}

