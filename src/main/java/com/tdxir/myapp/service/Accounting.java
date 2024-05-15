package com.tdxir.myapp.service;

import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.UserRepository;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.tdxir.myapp.tools.DateConvertor;
import com.tdxir.myapp.utils.Utils;
import com.tosan.tools.jalali.JalaliCalendar;
import com.tosan.tools.jalali.JalaliDate;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.xpath.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.Role.USER;

@Service


public class Accounting {

    DateConvertor dateConverter = new DateConvertor();
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    WkhPostMetaRepository wkhPostMetaRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired

    private  RecordAndProccessMessageService recordAndProccessMessageService;

    @Autowired
    private ProccessMessage proccessMessage;

    private Utils utils;

    private static final String SERVER_LOCATION = "/opt/tomcat/uploads";




    private static final String SERVER_LOCATION_PRODUCT_IMG = "/var/www/khooei.ir/public_html/wp-content/uploads/";

    private static final String SERVER_LOCATION_INVOICES = "/opt/tomcat/uploads/invoices";
    private String errorMsg="";
    //parameters : Rd=panel2 means return image or voice or text ---inf1 نام کالا یا فروشنده inf2 کد کالا
    //inf3 تعداد inf4 قیمت
    public ResponseEntity<JSONObject> saveDoc(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                   String checkBox1, String checkBox2, String checkBox3, String userName)
    {
        errorMsg="";
        DecimalFormat df = new DecimalFormat("###,###,###");
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();
      //  LocalDate dateNow=LocalDate.now();
        List<String> processList=new ArrayList<>();
        String docName=inf.get(1),
                description=inf.get(0),fileNameImg=null,fileNameVoice=null;
        Integer flag1;
        Boolean flagImg=false;


        if(processList.size()==0)
            try {

                if(fileImage!=null)
                {fileNameImg = recordAndProccessMessageService.storeInvoiceImg(fileImage);
                    processList.add(fileNameImg);
                    flagImg=true;
                }
                if(fileVoice!=null)
                {fileNameVoice = recordAndProccessMessageService.storeInvoiceImg(fileVoice);
                    processList.add(fileNameVoice);
                }

                flag1 = wkhPostMetaRepository.insertDoc( userName,date.toString(),docName,description,fileNameVoice,fileNameImg);
            } catch (Exception e){
                processList.add(e.getMessage());

            }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);


        try {


            List<Docs> docs=wkhPostMetaRepository.reportDocs(docName,userName);
            if(docs.size()!=0)
                for(int i=0;i<=docs.size()-1;++i)
                {
                    processList.add(docs.get(i).getDate()+"نام : "+docs.get(i).getDocName()+"توضیحات"+docs.get(i).getDescription());
                }
            else processList.add("اطلاعاتی برای نمایش این نام وجود ندارد--"+docName);

        }catch (Exception e){processList.add(e.getMessage());
        }

        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" پاسخی پیدا نکردم");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                processList.add( ex.getMessage());
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(0)!=null&&flagImg)
                try {
                String image1 = FilenameUtils.getName(processList.get(0));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(0));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);


                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());

                } else jsonObjectMain.put("fileContentImage", null);
                } catch (IOException ex) {
                    processList.add(ex.getMessage() + "Path or file isn't correct");

                }

             else processList.add("وجود ندارد");

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                processList.add(ex.getMessage());
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);


            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();
        try {
            for (int i = 1; i <= processList.size(); ++i) {
                jsonObject.put("inf_id", String.valueOf(i));
                jsonObject.put("inf_text", processList.get(i - 1));

                array.add(new JSONObject(jsonObject));
                jsonObject.clear();
            }

        }catch (Exception ex) {
            errorMsg += ex.getMessage();
            jsonObject.put("inf_text", "" + errorMsg);//, processList.get(i - 1) + ":" + "عکس");
            jsonObject.put("inf_text", "عکس  تعریف نشده است" + errorMsg);

            array.add(new JSONObject(jsonObject));
            jsonObject.clear();

        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> saveMyCredit(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, String userName,Integer companyId)
    {
        errorMsg="";
        DecimalFormat df = new DecimalFormat("###,###,###");
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();
        LocalDate dateInvoice=LocalDate.now();
        List<String> processList=new ArrayList<>();
        String idInvoice=null,message=inf.get(0), postId,sellerId=null,description="";
        String[] idList=inf.get(0).split("@");
        String[] stockANDdateList=inf.get(2).split("@");
        String[] priceAndCheck=inf.get(3).split("@");
        Boolean isCheck=false;
        Long price=Long.valueOf(0),idCheck;
        Long numProduct=null;
        Integer flag1;
        Boolean flag2;


        if(utils.isNumeric(priceAndCheck[0].replaceAll(",","")))
        {
            price = Long.valueOf(priceAndCheck[0].replaceAll(",",""));
            if (priceAndCheck.length==2 ){
                if (priceAndCheck[1].equals("چک")) {

                    isCheck = true;
                } else
                    processList.add("وقتی میخواهیم چک را ثبت کنیم باید به شکل مثلا 25,000,000@چک نوشته شود شما کلمه چک را اشتباه نوشته اید");


            }
        }
        else processList.add("اشکال در نوشتن قیمت -دستورالعمل بنویسید");
        if(stockANDdateList.length==2){
            if(utils.isNumeric(stockANDdateList[0])&&utils.isNumeric(stockANDdateList[1])) {
                if (stockANDdateList[1].length() == 8) {
                    dateInvoice = LocalDate.parse(stockANDdateList[1], DateTimeFormatter.BASIC_ISO_DATE);
                    numProduct=Long.valueOf(stockANDdateList[0]);
                }
                else processList.add("اشکال در نوشتن تاریخ -طبق دستورالعمل بنویسید(تعداد و تاریخ نوشته اید!)");
            }   else processList.add("اشکال در نوشتن تعداد و تاریخ -طبق دستورالعمل بنویسید");
        }
        else if(stockANDdateList.length==1 && utils.isNumeric(stockANDdateList[0])&&(stockANDdateList[1].length() == 8))
            dateInvoice = LocalDate.parse(stockANDdateList[0], DateTimeFormatter.BASIC_ISO_DATE);
        else processList.add("اشکال در نوشتن تاریخ -طبق دستورالعمل بنویسید(فقط تاریخ نوشته اید!)");


        if(idList.length==2&&idList[0]!="شماره فاکتور"){
            sellerId=idList[1];
            idInvoice=idList[0];
        }
        else {description=inf.get(0);idInvoice=inf.get(1);}

        if(utils.isNumeric(inf.get(0)))
        {
            idCheck=Long.valueOf(inf.get(0));
        }
        else idCheck= Long.valueOf(-1);
        if(processList.size()==0)
            try {
                // product code = inf1 and  exists then change price and stock

                if (wkhPostMetaRepository.existsCodeInvoice(idInvoice)==null ) {
                    if(sellerId!=null) {
                        String fileName = recordAndProccessMessageService.storeInvoiceImg(fileImage);
                        flag1 = wkhPostMetaRepository.insertInvoice(idInvoice, userName, fileName, date.toString(), dateInvoice, Long.valueOf(numProduct), Long.valueOf(price), sellerId, companyId, false, "SELL");
                        Integer idDoc = wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                        wkhPostMetaRepository.insertBills(date.toString(), dateInvoice, Long.valueOf(idDoc), idInvoice, price, "INVOICESELL", userName, fileName, false, "فاکتور", companyId);
                    } else processList.add("نام فروشنده و شماره فاکتور درست تعریف نشده است-طبق دستورالعمل اطلاعات را وارد نمایید!@!");
                }
                else {
                    Invoices invoices =wkhPostMetaRepository.reportInvoices(idInvoice,companyId);
                    Long idDoc= invoices.getIdDoc();//wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                    if(invoices.getPaid()!=true&& invoices.getSellOrBuy().equals("BUY"))
                    {
                        if(isCheck){
                        String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                        wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,idDoc,idInvoice,price,"CHECK",userName,fileName,false ,description,companyId);
                    }
                    else {

                        wkhPostMetaRepository.updateInvoices(numProduct,price,idInvoice);
                        String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                        wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,idDoc,idInvoice,price,"cash",userName,fileName,false ,description,companyId);
                    }
                        List<Long> billPrice=wkhPostMetaRepository.priceOfInvoiceBill(idInvoice);
                        Long tempPrice=Long.valueOf(0);
                        for(int i=0;i<=billPrice.size()-1;++i)
                        {tempPrice+=billPrice.get(i);

                        }
                        if(tempPrice>=0) wkhPostMetaRepository.updateInvoicesPaid(true,idInvoice);

                    }
                    else processList.add("این فاکتور قبلا تسویه شده است");

                }




            } catch (Exception e){
                processList.add(e.getMessage());

            }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);


        try {


            List<Bills> bills=wkhPostMetaRepository.reportInvoiceInBills(idInvoice,companyId);
            if(bills.size()!=0)
                for(int i=0;i<=bills.size()-1;++i)
                {
                    processList.add(bills.get(i).getDate()+"مبلغ"+String.valueOf(df.format(bills.get(i).getPrice()))+"ریال-"+bills.get(i).getPayKind());
                }
            else processList.add("اطلاعاتی برای نمایش این فاکتور و آی دی وجود ندارد--"+idInvoice+"--"+String.valueOf(companyId));

        }catch (Exception e){processList.add(e.getMessage());
        }

        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" پاسخی پیدا نکردم");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                processList.add( ex.getMessage());
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(3)!=null) {
                String image1 = FilenameUtils.getName(processList.get(3));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(3));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        processList.add(ex.getMessage() + "Path or file isn't correct");

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else processList.add("وجود ندارد");

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                processList.add(ex.getMessage());
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);


            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();
        try {
            for (int i = 1; i <= processList.size(); ++i) {
                jsonObject.put("inf_id", String.valueOf(i));
                jsonObject.put("inf_text", processList.get(i - 1));

                array.add(new JSONObject(jsonObject));
                jsonObject.clear();
            }

        }catch (Exception ex) {
            errorMsg += ex.getMessage();
            jsonObject.put("inf_text", "" + errorMsg);//, processList.get(i - 1) + ":" + "عکس");
            jsonObject.put("inf_text", "عکس  تعریف نشده است" + errorMsg);

            array.add(new JSONObject(jsonObject));
            jsonObject.clear();

        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> saveMyDebit(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, String userName,Integer companyId)
    {   errorMsg="";
        DecimalFormat df = new DecimalFormat("###,###,###");
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();
        LocalDate dateInvoice=LocalDate.now();
        List<String> processList=new ArrayList<>();
        String idInvoice=null,message=inf.get(0), postId,sellerId=null,description="";
        String[] idList=inf.get(0).split("@");
        String[] stockANDdateList=inf.get(2).split("@");
        String[] priceAndCheck=inf.get(3).split("@");
        Boolean isCheck=false;
        Long price=Long.valueOf(0),idCheck;
        Long numProduct=null;
        Integer flag1;
        Boolean flag2;


        if(utils.isNumeric(priceAndCheck[0].replaceAll(",","")))
        {
            price = Long.valueOf(priceAndCheck[0].replaceAll(",",""));
            if (priceAndCheck.length==2 ){
                if (priceAndCheck[1].equals("چک")) {

                isCheck = true;
            } else
                   processList.add("وقتی میخواهیم چک را ثبت کنیم باید به شکل مثلا 25,000,000@چک نوشته شود شما کلمه چک را اشتباه نوشته اید");


            }
        }
        else processList.add("اشکال در نوشتن قیمت -طبق دستورالعمل بنویسید");
        if(stockANDdateList.length==2){
            if(utils.isNumeric(stockANDdateList[0])&&utils.isNumeric(stockANDdateList[1])) {
                if (stockANDdateList[1].length() == 8) {
                    dateInvoice = LocalDate.parse(stockANDdateList[1], DateTimeFormatter.BASIC_ISO_DATE);
                    numProduct=Long.valueOf(stockANDdateList[0]);
                }
                else processList.add("اشکال در نوشتن تاریخ -طبق دستورالعمل بنویسید(تعداد و تاریخ نوشته اید!)");
            }   else processList.add("اشکال در نوشتن تعداد و تاریخ -طبق دستورالعمل بنویسید");
        }
        else if(stockANDdateList.length==1 && utils.isNumeric(stockANDdateList[0])&&(stockANDdateList[1].length() == 8))
            dateInvoice = LocalDate.parse(stockANDdateList[0], DateTimeFormatter.BASIC_ISO_DATE);
            else processList.add("اشکال در نوشتن تاریخ -طبق دستورالعمل بنویسید(فقط تاریخ نوشته اید!)");


               if(idList.length==2&&idList[0]!="شماره فاکتور"){
            sellerId=idList[1];
            idInvoice=idList[0];
        }
               else {description=inf.get(0);idInvoice=inf.get(1);}

        if(utils.isNumeric(inf.get(0)))
        {
            idCheck=Long.valueOf(inf.get(0));
        }
        else idCheck= Long.valueOf(-1);
        if(processList.size()==0)
        try {
            // product code = inf1 and  exists then change price and stock

            if (wkhPostMetaRepository.existsCodeInvoice(idInvoice) == null) {

                String fileName = recordAndProccessMessageService.storeInvoiceImg(fileImage);
                flag1 = wkhPostMetaRepository.insertInvoice(idInvoice, userName, fileName, date.toString(), dateInvoice, Long.valueOf(numProduct), Long.valueOf(price), sellerId, companyId, false, "BUY");
                Integer idDoc = wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                wkhPostMetaRepository.insertBills(date.toString(), dateInvoice, Long.valueOf(idDoc), idInvoice, -price, "INVOICEBUY", userName, fileName, false, "فاکتور", companyId);
                processList.add(fileName);
                errorMsg="";
            } else {
                Invoices invoices = wkhPostMetaRepository.reportInvoices(idInvoice,companyId);
                if (invoices.getCompleted())
                {
                    Long idDoc = invoices.getIdDoc();//wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                    if (invoices.getPaid() != true && invoices.getSellOrBuy().equals("SELL")) {
                        if (isCheck) {
                            String fileName = recordAndProccessMessageService.storeInvoiceImg(fileImage);
                            wkhPostMetaRepository.insertBills(date.toString(), dateInvoice, idDoc, idInvoice, -price, "CHECK", userName, fileName, false, description, companyId);
                            processList.add(fileName);
                            errorMsg="";
                        } else {

                            wkhPostMetaRepository.updateInvoices(numProduct, price, idInvoice);
                            String fileName = recordAndProccessMessageService.storeInvoiceImg(fileImage);
                            wkhPostMetaRepository.insertBills(date.toString(), dateInvoice, idDoc, idInvoice, -price, "cash", userName, fileName, false, description, companyId);
                            processList.add(fileName);
                            errorMsg="";
                        }
                        List<Long> billPrice = wkhPostMetaRepository.priceOfInvoiceBill(idInvoice);
                        Long tempPrice = Long.valueOf(0);
                        for (int i = 0; i <= billPrice.size() - 1; ++i) {
                            tempPrice += billPrice.get(i);

                        }
                        if (tempPrice >= 0) wkhPostMetaRepository.updateInvoicesPaid(true, idInvoice);

                    } else {
                        processList.add("این فاکتور قبلا تسویه شده است یا پرداختی مربوط به این فاکتور نیست");
                        errorMsg="قبلا تسویه شده یا مربوط به این فاکتور نیست";
                    }
                }


                    else {
                    processList.add("ابتدا باید کالاهای فاکتور در قسمت -خرید-وارد شود");
                    errorMsg="کالاهای فاکتور ثبت نشده است";
                    }
               }




        } catch (Exception e){
            processList.add(e.getMessage());

        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);


        try {


            List<Bills> bills=wkhPostMetaRepository.reportInvoiceInBills(idInvoice,companyId);
            if(bills.size()!=0)
                for(int i=0;i<=bills.size()-1;++i)
                {
                    processList.add(bills.get(i).getDate()+"مبلغ"+String.valueOf(df.format(bills.get(i).getPrice()))+"ریال-"+bills.get(i).getPayKind());
                }
            else processList.add("اطلاعاتی برای نمایش این فاکتور و آی دی وجود ندارد--"+idInvoice+"--"+String.valueOf(companyId));

            }catch (Exception e){processList.add(e.getMessage());
        }

        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" پاسخی پیدا نکردم");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                processList.add( ex.getMessage());
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(0)!=null && errorMsg=="") {
                String image1 = FilenameUtils.getName(processList.get(0));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(0));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        processList.add(ex.getMessage() + "Path or file isn't correct");

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else processList.add(errorMsg);

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
               processList.add(ex.getMessage());
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);


            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();
       try {
           if(processList.size()>1)
           for (int i = 1; i <= processList.size()-1; ++i) {
               jsonObject.put("inf_id", String.valueOf(i));
               jsonObject.put("inf_text", processList.get(i));

           array.add(new JSONObject(jsonObject));
           jsonObject.clear();
       }

       }catch (Exception ex) {
           errorMsg += ex.getMessage();
           jsonObject.put("inf_text", "" + errorMsg);//, processList.get(i - 1) + ":" + "عکس");
                   jsonObject.put("inf_text", "عکس  تعریف نشده است" + errorMsg);

        array.add(new JSONObject(jsonObject));
        jsonObject.clear();

       }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> saveProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, UserKind userKind) {
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();


        String yearAndmounth= new SimpleDateFormat("yyyy/MM/").format(date);
        String message=inf.get(1), postId;
        String[] nameList=inf.get(0).split("@");

        try {
            Long code,price;
            Integer stock ,flag1,flag2;
            if(utils.isNumeric(inf.get(1)))
            {
                code = Long.valueOf(inf.get(1));
            }
            else code= Long.valueOf(-1);
            if(utils.isNumeric(inf.get(2)))
            {
                stock=Integer.valueOf(inf.get(2));
            }
            else stock= -1;
            if(utils.isNumeric(inf.get(3).replaceAll(",","")))
            {
                price = Long.valueOf(inf.get(3).replaceAll(",",""));
            }
            else price= Long.valueOf(-1);




            // product code = inf1 and  exists then change price and stock
            if(code!=Long.valueOf(-1)) {
                postId = wkhPostMetaRepository.existsCode(String.valueOf(code));
                if (postId != null) {
                    if(stock!=-1)
                        flag1 = wkhPostMetaRepository.updateStock(String.valueOf(stock), postId);
                    if(price!=Long.valueOf(-1)) {
                        flag2 = wkhPostMetaRepository.updatePrice(String.valueOf(price), postId);
                         wkhPostMetaRepository.updateRegularPrice(String.valueOf(price), postId);
                         wkhPostMetaRepository.updateWholeSalePrice(String.valueOf(price), postId);
                         wkhPostMetaRepository.updateSalePrice(String.valueOf(price), postId);
                    }
                    if(nameList[1].equals(inf.get(1)))
                        wkhPostsRepository.updateName(nameList[0], postId);
                    if (fileImage != null) {
                        List<String> thumbnail = wkhPostMetaRepository.findThumbnail(String.valueOf(code));
                        if (thumbnail.get(0) == null) {


                            Integer test = wkhPostMetaRepository.updateImage(yearAndmounth + fileImage.getOriginalFilename(), thumbnail.get(0));
                            recordAndProccessMessageService.storeImage(fileImage, SERVER_LOCATION_PRODUCT_IMG);
                        } else {


                            Integer test = wkhPostMetaRepository.updateImage(yearAndmounth + fileImage.getOriginalFilename(), thumbnail.get(0));
                            recordAndProccessMessageService.storeImage(fileImage, SERVER_LOCATION_PRODUCT_IMG);


                        }
                        ;


                    }

                }
            }
        } catch (Exception e){
            errorMsg+=e.getMessage();

        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(message, userKind, Rd);
        }
        else{
            List<String> name=wkhPostMetaRepository.nameIdCode(message);
            if(name.size()>0)
                processList.add(name.get(0));
            else processList.add("-1");
            List<String> price=wkhPostMetaRepository.priceIdCode(message);
            if(price.size()>0)
                processList.add(price.get(0));
            else processList.add("-1");
            List<String> stock=wkhPostMetaRepository.stockIdCode(message);
            if(stock.size()>0)
                processList.add(stock.get(0));
            else processList.add("-1");

            processList.add(wkhPostMetaRepository.imageUrlId(message));

        }
        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" پاسخی پیدا نکردم");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(3)!=null) {
                String image1 = FilenameUtils.getName(processList.get(3));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_PRODUCT_IMG + FilenameUtils.getPath(processList.get(3));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);


            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));
            if(i==1) {
                if (processList.get(i - 1) != "-1")
                    jsonObject.put("inf_text", "نام کالا : " + processList.get(i - 1) );
                else jsonObject.put("inf_text", "نام کالا تعریف نشده است");
            }
            else if(i==2) {
                if(processList.get(i - 1)!="-1")
                    jsonObject.put("inf_text", "قیمت : " + processList.get(i - 1) + "ریال");
                else jsonObject.put("inf_text",  "قیمت تعریف نشده است");

            }
            else if(i==3)
            {
                if(processList.get(i - 1)!="-1")
                    jsonObject.put("inf_text","تعداد :    " +processList.get(i - 1) );
                else jsonObject.put("inf_text",  "موجودی تعریف نشده است");
            }
            else if(i==4) {
                if(processList.get(i - 1)!=null)
                    jsonObject.put("inf_text",""+errorMsg);//, processList.get(i - 1) + ":" + "عکس");
                else jsonObject.put("inf_text",  "عکس  تعریف نشده است"+errorMsg);
            }
            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
   public ResponseEntity<JSONObject> searchProductInAccountingforremove_az_inja(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                    String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role)
    {
        String message=inf.get(1);
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(message, userKind, Rd);
        }
        else{
            List<String> name=wkhPostMetaRepository.nameIdCode(message);
            if(name.size()>0)
                processList.add(name.get(0));
            else processList.add("-1");
            List<String> price=wkhPostMetaRepository.priceIdCode(message);
            if(price.size()>0) {
                if(role==USER)
                    processList.add(price.get(0));
                else if(role==ADMIN)
                {   List<Long> buyPrice=wkhPostMetaRepository.buyPrice(message);
                    processList.add(price.get(0)+"(قیمت خرید"+String.valueOf(buyPrice.get(buyPrice.size()-1))+")");
                }
            }
            else processList.add("-1");
            List<String> stock=wkhPostMetaRepository.stockIdCode(message);
            if(stock.size()>0)
                processList.add(stock.get(0));
            else processList.add("-1");

            processList.add(wkhPostMetaRepository.imageUrlId(message));

        }
        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" پاسخی پیدا نکردم");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(3)!=null) {
                String image1 = FilenameUtils.getName(processList.get(3));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_PRODUCT_IMG + FilenameUtils.getPath(processList.get(3));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));
            if(i==1) {
                if (processList.get(i - 1) != "-1")
                    jsonObject.put("inf_text", "نام کالا : " + processList.get(i - 1) );
                else jsonObject.put("inf_text", "نام کالا تعریف نشده است");
            }
            else if(i==2) {
                if(processList.get(i - 1)!="-1")
                    jsonObject.put("inf_text", "قیمت : " + processList.get(i - 1) + "ریال");
                else jsonObject.put("inf_text",  "قیمت تعریف نشده است");

            }
            else if(i==3)
            {
                if(processList.get(i - 1)!="-1")
                    jsonObject.put("inf_text","تعداد :    " +processList.get(i - 1) );
                else jsonObject.put("inf_text",  "موجودی تعریف نشده است");
            }
            else if(i==4) {
                if(processList.get(i - 1)!=null)
                    jsonObject.put("inf_text","");//, processList.get(i - 1) + ":" + "عکس");
                else jsonObject.put("inf_text",  "عکس  تعریف نشده است");
            }
            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        // ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
        //headers.setContentDisposition(disposition);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> reportProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                    String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role)
    {
        DecimalFormat df = new DecimalFormat("###,###,###");

        String message=inf.get(1);
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(message, userKind, Rd);
        }
        else{
            List<BuyData> buyData=wkhPostMetaRepository.reportProduct(message);
           if(buyData.size()!=0)
            { Integer lastrecordindex=buyData.size()-1;

            for(int i=lastrecordindex;i>=0;--i)


                    processList.add("خرید تاریخ"+buyData.get(i).getDate()+ "-"+"تعداد"+buyData.get(i).getStock()+ "-"+"قیمت"+df.format(buyData.get(i).getPrice())+"ریال");

            }
            else processList.add("-1");
        }
        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" گزارشی وجود ندارد");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(3)!=null) {
                String image1 = FilenameUtils.getName(processList.get(3));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_PRODUCT_IMG + FilenameUtils.getPath(processList.get(3));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));

                if (processList.get(i - 1) != "-1")
                    jsonObject.put("inf_text", processList.get(i - 1) );
                else jsonObject.put("inf_text", "گزارشی وجود ندارد");




            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        // ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
        //headers.setContentDisposition(disposition);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }

    public ResponseEntity<JSONObject> reportInvoiceBuy(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                    String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role , Integer companyId)
    {



        DecimalFormat df = new DecimalFormat("###,###,###");
        String idInvoice=inf.get(1);
        List<BuyData> buyData=wkhPostMetaRepository.findInvoiceInBuyData(idInvoice);
        if(buyData.size()!=0){
         Long num_product=Long.valueOf(0),price=Long.valueOf(0);
        for(int i=0;i<=buyData.size()-1;++i)
        {
         num_product+=1;
         price+=buyData.get(i).getPrice()*buyData.get(i).getStock();
        }
        // این کد در قسمت saveinvoice اصلاح و از اینجا موقتا comment شد
        //   wkhPostMetaRepository.updateInvoices(num_product,price,String.valueOf(idInvoice));
        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(idInvoice, userKind, Rd);
        }
        else{
            Invoices invoices =wkhPostMetaRepository.reportInvoices(idInvoice,companyId);

            if(invoices !=null)
            {
                processList.add("تاریخ-"+ invoices.getDate());
                processList.add("تعداد-"+ invoices.getNumProduct());
                processList.add("فروشنده-"+ invoices.getSellerID());
                processList.add("قیمت-"+String.valueOf(df.format(invoices.getPrice()))+"-ریال");
                processList.add(invoices.getFileImage());
                for(int i=0;i<=buyData.size()-1;++i){
                processList.add("کد-"+buyData.get(i).getSku()+"تعداد-"+buyData.get(i).getStock()+"قیمت-"+String.valueOf(df.format(buyData.get(i).getPrice())));
                }

            }
            else processList.add("-1");
        }
        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" گزارشی وجود ندارد");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(4)!=null) {
                String image1 = FilenameUtils.getName(processList.get(4));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(4));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));

            if (processList.get(i - 1) != "-1") {
             if(i==5)    jsonObject.put("inf_text",  "لیست کالاهای خرید شده این فاکتور : ");

               else  jsonObject.put("inf_text", processList.get(i - 1));
            }
            else jsonObject.put("inf_text", "گزارشی وجود ندارد");




            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        // ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
        //headers.setContentDisposition(disposition);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }

    public ResponseEntity<JSONObject> reportInvoicePay(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                       String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role,Integer companyId)
    {



        DecimalFormat df = new DecimalFormat("###,###,###");
        String idInvoice=inf.get(1);
        List<BuyData> buyData=wkhPostMetaRepository.findInvoiceInBuyData(idInvoice);
        if(buyData.size()!=0){
            Long num_product=Long.valueOf(0),price=Long.valueOf(0);
            for(int i=0;i<=buyData.size()-1;++i)
            {
                num_product+=1;
                price+=buyData.get(i).getPrice()*buyData.get(i).getStock();
            }
            // این کد در قسمت saveinvoice اصلاح و از اینجا موقتا comment شد
            //   wkhPostMetaRepository.updateInvoices(num_product,price,String.valueOf(idInvoice));
        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(idInvoice, userKind, Rd);
        }
        else{
          //  BuyInvoices buyInvoices=wkhPostMetaRepository.reportInvoices(idInvoice);
            List<Bills> bills=wkhPostMetaRepository.reportInvoiceInBills(idInvoice,companyId);
            if(bills.size()!=0)
                for(int i=0;i<=bills.size()-1;++i)
                {
                    processList.add(bills.get(i).getDate()+"مبلغ"+String.valueOf(df.format(bills.get(i).getPrice()))+"ریال-"+bills.get(i).getPayKind());
                }
         /*   if(buyInvoices!=null)
            {
                processList.add("تاریخ-"+buyInvoices.getDate());
                processList.add("تعداد-"+buyInvoices.getNumProduct());
                processList.add("فروشنده-"+buyInvoices.getSellerID());
                processList.add("قیمت-"+String.valueOf(df.format(buyInvoices.getPrice()))+"-ریال");
                processList.add(buyInvoices.getFileImage());
                for(int i=0;i<=buyData.size()-1;++i){
                    processList.add("کد-"+buyData.get(i).getSku()+"تعداد-"+buyData.get(i).getStock()+"قیمت-"+String.valueOf(df.format(buyData.get(i).getPrice())));
                }

            }*/
            else processList.add("-1");
        }
        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" گزارشی وجود ندارد");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&processList.get(4)!=null) {
                String image1 = FilenameUtils.getName(processList.get(4));//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(4));
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));

            if (processList.get(i - 1) != "-1") {
                if(i==5)    jsonObject.put("inf_text",  "لیست کالاهای خرید شده این فاکتور : ");

                else  jsonObject.put("inf_text", processList.get(i - 1));
            }
            else jsonObject.put("inf_text", "گزارشی وجود ندارد");




            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        // ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
        //headers.setContentDisposition(disposition);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> reportDoc(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                       String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role,String userName)
    {
        Boolean flagImg=false;
        Integer selectImgRow=0;
        if(utils.isNumeric(inf.get(2))) selectImgRow=Integer.valueOf(inf.get(2));

        List<String> processList=new ArrayList<>();
        String docName=inf.get(1),imgSelected=null;
        List<Docs> docs=wkhPostMetaRepository.reportDocs(docName,userName);

        if(docs.size()!=0&&docs.size()>selectImgRow){
            if(docs.get(selectImgRow).getImageFileName()!=null)
            imgSelected=docs.get(selectImgRow).getImageFileName();
            for(int i=1;i<=docs.size();++i)

                if(selectImgRow==i) {
                    imgSelected=docs.get(i).getImageFileName();
                    flagImg = true;
                }
                else if(docs.get(i-1).getImageFileName()!=null) {
                    processList.add("ردیف " + String.valueOf(i-1) + "--" + docs.get(i - 1).getDate() + "--" + docs.get(i - 1).getDocName() + "===" + docs.get(i - 1).getDescription());
                } else processList.add( docs.get(i - 1).getDate() + "--" + docs.get(i - 1).getDocName() + "===" + docs.get(i - 1).getDescription());
             }





        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        if ((processList == null)|| (processList.size()==0)) {
            processList = new ArrayList<>();
            processList.add(" گزارشی وجود ندارد");
        }

        if(Rd.equals("Rd1")) {


            jsonObjectMain.put("fileContentImage", null);
            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());
                jsonObjectMain.put("fileContentImage", null);
                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }


        }
        else if(Rd.equals("Rd2")){

            jsonObjectMain.put("fileContentVoice", null);
            if((processList.size()>1)&&imgSelected!=null) {
                String image1 = FilenameUtils.getName(imgSelected);//"replyimage.jpg"
                if (image1 != null) {
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(imgSelected);
                    File filereplyImg = new File(pathFile + File.separator + image1);//+ EXTENSION);

                    try {
                        Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                        ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                        byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());
                        //jsonObjectMain.put("fileContentVoice", null);
                        jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                    } catch (IOException ex) {
                        errorMsg = ex.getMessage() + "Path or file isn't correct";

                    }
                } else jsonObjectMain.put("fileContentImage", null);
            } else errorMsg+="وجود ندارد";

        }
        else if(Rd.equals("Rd3")){


            String fileName = "receivedmessage.wav";
            inf.add(0, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد");
            //UploadResponse uploadResponse = new UploadResponse(fileName,fileName,inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path = Paths.get(filereply.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

                jsonObjectMain.put("fileContentVoice", resource.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }

            String image1 = "replyimage.jpg";

            File filereplyImg = new File(SERVER_LOCATION + File.separator + image1);//+ EXTENSION);

             /*HttpHeaders header = new HttpHeaders();
             header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

             header.add("Cache-Control", "no-cache, no-store, must-revalidate");
             header.add("Pragma", "no-cache");
             header.add("Expires", "0");
*/
            try {
                Path path1 = Paths.get(filereplyImg.getAbsolutePath());
                ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

                byte[] encoder1 = Base64.getEncoder().encode(resource1.getByteArray());

                jsonObjectMain.put("fileContentImage", resource1.getByteArray());
            } catch (IOException ex) {
                errorMsg = ex.getMessage();
            }
        }
        else if(Rd.equals("Rd4")){

            jsonObjectMain.put("fileContentVoice", null);
            jsonObjectMain.put("fileContentImage", null);
        }



        JSONArray array = new JSONArray();

        for (int i = 1; i <= processList.size(); ++i) {
            jsonObject.put("inf_id", String.valueOf(i));
            jsonObject.put("inf_text", processList.get(i-1 ));
            array.add(new JSONObject(jsonObject));
            jsonObject.clear();
        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ContentDisposition disposition = ContentDisposition.attachment().filename("monshi.mp3").build();
        // ContentDisposition disposition = ContentDisposition.attachment().filename(filereply.getName()).build();
        //headers.setContentDisposition(disposition);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
    public ResponseEntity<JSONObject> saveCompanyId(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, String userName,Integer companyId)
    {   errorMsg="";

        String userId=inf.get(0),branch=inf.get(1);
        List<String> processList=new ArrayList<>();

        Integer flag1;
        Boolean flag2;


            try {
                // product code = inf1 and  exists then change price and stock

                if (userRepository.findByEmail(userId) != null) {


                    flag1 = wkhPostMetaRepository.insertCompanyId(userId,branch);
                    if(flag1==1)
                    processList.add("با موفقیت اضافه شد");
                    List<Company> companyList=wkhPostMetaRepository.reportCompanies(inf.get(2));
                    for(int i=0;i<=companyList.size()-1;++i)
                        processList.add(companyList.get(i).getOwnerName()+"----"+companyList.get(i).getBranch());
                    errorMsg="";
                } else {
                    processList.add("این نام کاربری وجود ندارد");
                }




            } catch (Exception e){
                processList.add(e.getMessage());

            }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();


        jsonObjectMain.put("fileContentVoice", null);
        jsonObjectMain.put("fileContentImage", null);

        JSONArray array = new JSONArray();
        try {
            if(processList.size()>1)
                for (int i = 1; i <= processList.size()-1; ++i) {
                    jsonObject.put("inf_id", String.valueOf(i));
                    jsonObject.put("inf_text", processList.get(i));

                    array.add(new JSONObject(jsonObject));
                    jsonObject.clear();
                }

        }catch (Exception ex) {
            errorMsg += ex.getMessage();
            jsonObject.put("inf_text", "" + errorMsg);//, processList.get(i - 1) + ":" + "عکس");
            jsonObject.put("inf_text", "عکس  تعریف نشده است" + errorMsg);

            array.add(new JSONObject(jsonObject));
            jsonObject.clear();

        }
        jsonObjectMain.put("inf", array);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(jsonObjectMain, headers, HttpStatus.OK);
    }
}
