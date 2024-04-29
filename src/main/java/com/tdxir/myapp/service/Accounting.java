package com.tdxir.myapp.service;

import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.tdxir.myapp.service.ProccessMessage;
import com.tdxir.myapp.service.RecordAndProccessMessageService;
import com.tdxir.myapp.tools.DateConvertor;
import com.tdxir.myapp.utils.Utils;
import com.tosan.tools.jalali.JalaliCalendar;
import com.tosan.tools.jalali.JalaliDate;
import jakarta.persistence.criteria.CriteriaBuilder;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Length;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.tdxir.myapp.model.Operation.*;
import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.Role.USER;
import static com.tdxir.myapp.model.UserKind.SHOP;

@Service


public class Accounting {

    DateConvertor dateConverter = new DateConvertor();
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    WkhPostMetaRepository wkhPostMetaRepository;
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
    public ResponseEntity<JSONObject> saveMyCredit(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, String userName,Integer companyId)
    {
        DecimalFormat df = new DecimalFormat("###,###,###");
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();
        LocalDate dateInvoice;

        String idInvoice=null,message=inf.get(0), postId,sellerId=null,description="";
        String[] idList=inf.get(0).split("@");
        String[] stockANDdateList=inf.get(2).split("@");
        String[] priceAndCheck=inf.get(3).split("@");
        Boolean isCheck=false;
        if (priceAndCheck.length==2 && priceAndCheck[1].equals("چک")) isCheck=true;
        if(stockANDdateList.length==2) {
            dateInvoice = LocalDate.parse(stockANDdateList[1], DateTimeFormatter.BASIC_ISO_DATE);
        }
        else {
            dateInvoice = LocalDate.parse(stockANDdateList[0], DateTimeFormatter.BASIC_ISO_DATE);
        }
        Long price,idCheck;
        Long numProduct=null;
        Integer flag1;
        Boolean flag2;
        if(idList.length==2&&idList[0]!="شماره فاکتور"){
            sellerId=idList[1];
            idInvoice=idList[0];
        }
        else {description=inf.get(0);idInvoice=inf.get(1);}


        try {
            if(utils.isNumeric(inf.get(0)))
            {
                idCheck=Long.valueOf(inf.get(0));
            }
            else idCheck= Long.valueOf(-1);

            if(utils.isNumeric(stockANDdateList[0]))
            {
                numProduct=Long.valueOf(stockANDdateList[0]);
            }
            else numProduct= Long.valueOf(-1);

            if(utils.isNumeric(priceAndCheck[0].replaceAll(",","")))
            {
                price = Long.valueOf(priceAndCheck[0].replaceAll(",",""));
            }
            else price= Long.valueOf(-1);



            errorMsg+="-BSF";

            // product code = inf1 and  exists then change price and stock

            if (wkhPostMetaRepository.existsCodeInvoice(idInvoice)==null) {

                String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);



                errorMsg+="-ASF";



                flag1 = wkhPostMetaRepository.insertInvoice(idInvoice,userName,fileName,date.toString(),dateInvoice,Long.valueOf(numProduct),Long.valueOf(price),sellerId,companyId );
                Integer idDoc=wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,price,"INVOICEBUY",userName,fileName,false ,"فاکتور",companyId);
                errorMsg+="-ASData";
            }
            else {

                Integer idDoc=wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                if(isCheck){
                    String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                    wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,price,"CHECK",userName,fileName,false ,description,companyId);


                }
                else {

                    wkhPostMetaRepository.updateInvoices(numProduct,price,idInvoice);
                    String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                    wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,price,"cash",userName,fileName,false ,description,companyId);
                }

            }




        } catch (Exception e){
            errorMsg+=e.getMessage();

        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();

        try {


            List<Bills> bills=wkhPostMetaRepository.reportInvoiceInBills(idInvoice,companyId);
            if(bills.size()!=0)
                for(int i=0;i<=bills.size()-1;++i)
                {
                    processList.add(bills.get(i).getDate()+"مبلغ"+String.valueOf(df.format(bills.get(i).getPrice()))+"ریال-"+bills.get(i).getPayKind());
                }

        }catch (Exception e){errorMsg+=e.getMessage();
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
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(3));
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
    {
        DecimalFormat df = new DecimalFormat("###,###,###");
        JalaliDate date=new JalaliDate();

        JalaliCalendar jalaliCalendar=new JalaliCalendar();//date);
        date=jalaliCalendar.getJalaliDate();
        LocalDate dateInvoice;

        String idInvoice=null,message=inf.get(0), postId,sellerId=null,description="";
        String[] idList=inf.get(0).split("@");
        String[] stockANDdateList=inf.get(2).split("@");
        String[] priceAndCheck=inf.get(3).split("@");
        Boolean isCheck=false;
        if (priceAndCheck.length==2 && priceAndCheck[1].equals("چک")) isCheck=true;
        if(stockANDdateList.length==2) {
             dateInvoice = LocalDate.parse(stockANDdateList[1], DateTimeFormatter.BASIC_ISO_DATE);
        }
        else {
             dateInvoice = LocalDate.parse(stockANDdateList[0], DateTimeFormatter.BASIC_ISO_DATE);
        }
        Long price,idCheck;
        Long numProduct=null;
        Integer flag1;
        Boolean flag2;
               if(idList.length==2&&idList[0]!="شماره فاکتور"){
            sellerId=idList[1];
            idInvoice=idList[0];
        }
               else {description=inf.get(0);idInvoice=inf.get(1);}


        try {
            if(utils.isNumeric(inf.get(0)))
            {
                idCheck=Long.valueOf(inf.get(0));
            }
            else idCheck= Long.valueOf(-1);

            if(utils.isNumeric(stockANDdateList[0]))
            {
                numProduct=Long.valueOf(stockANDdateList[0]);
            }
            else numProduct= Long.valueOf(-1);

            if(utils.isNumeric(priceAndCheck[0].replaceAll(",","")))
            {
                price = Long.valueOf(priceAndCheck[0].replaceAll(",",""));
            }
            else price= Long.valueOf(-1);



            errorMsg+="-BSF";

                // product code = inf1 and  exists then change price and stock

                    if (wkhPostMetaRepository.existsCodeInvoice(idInvoice)==null) {

                            String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);



                        errorMsg+="-ASF";



                        flag1 = wkhPostMetaRepository.insertInvoice(idInvoice,userName,fileName,date.toString(),dateInvoice,Long.valueOf(numProduct),Long.valueOf(price),sellerId,companyId );
                        Integer idDoc=wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                        wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,-price,"INVOICEBUY",userName,fileName,false ,"فاکتور",companyId);
                        errorMsg+="-ASData";
                    }
                    else {

                        Integer idDoc=wkhPostMetaRepository.existsCodeInvoice(idInvoice);
                        if(isCheck){
                            String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                            wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,-price,"CHECK",userName,fileName,false ,description,companyId);


                        }
                        else {

                        wkhPostMetaRepository.updateInvoices(numProduct,price,idInvoice);
                            String fileName =recordAndProccessMessageService.storeInvoiceImg(fileImage);
                            wkhPostMetaRepository.insertBills(date.toString(),dateInvoice,Long.valueOf(idDoc),idInvoice,-price,"cash",userName,fileName,false ,description,companyId);
                    }

                    }




        } catch (Exception e){
            errorMsg+=e.getMessage();

        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();

        try {


            List<Bills> bills=wkhPostMetaRepository.reportInvoiceInBills(idInvoice,companyId);
            if(bills.size()!=0)
                for(int i=0;i<=bills.size()-1;++i)
                {
                    processList.add(bills.get(i).getDate()+"مبلغ"+String.valueOf(df.format(bills.get(i).getPrice()))+"ریال-"+bills.get(i).getPayKind());
                }

            }catch (Exception e){errorMsg+=e.getMessage();
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
                    String pathFile = SERVER_LOCATION_INVOICES + FilenameUtils.getPath(processList.get(3));
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
                                                    String checkBox1, String checkBox2, String checkBox3, UserKind userKind, Role role)
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
            BuyInvoices buyInvoices=wkhPostMetaRepository.reportInvoices(idInvoice);

            if(buyInvoices!=null)
            {
                processList.add("تاریخ-"+buyInvoices.getDate());
                processList.add("تعداد-"+buyInvoices.getNumProduct());
                processList.add("فروشنده-"+buyInvoices.getSellerID());
                processList.add("قیمت-"+String.valueOf(df.format(buyInvoices.getPrice()))+"-ریال");
                processList.add(buyInvoices.getFileImage());
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
}
