package com.tdxir.myapp.service;

import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.tdxir.myapp.service.ProccessMessage;
import com.tdxir.myapp.service.RecordAndProccessMessageService;
import com.tdxir.myapp.utils.Utils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.tdxir.myapp.model.Operation.INVOICE;
import static com.tdxir.myapp.model.Operation.PRODUCT;
import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.Role.USER;
import static com.tdxir.myapp.model.UserKind.SHOP;

@Service
public class Shop {
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

    private String errorMsg="";
    //parameters : Rd=panel2 means return image or voice or text ---inf1 نام کالا یا فروشنده inf2 کد کالا
    //inf3 تعداد inf4 قیمت

    public ResponseEntity<JSONObject> buyProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                 String checkBox1, String checkBox2, String checkBox3, String userName) {
        Date date=new Date();
        String yearAndmounth= new SimpleDateFormat("yyyy/MM/").format(date);
        String nowDate= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
        String message=inf.get(1), postId,sellerId=inf.get(0);
        String[] idList=inf.get(0).split("@");
        Long idInvoice=null;
        Operation op=null;
        if((idList.length==1)&&utils.isNumeric(idList[0])){
            idInvoice=Long.valueOf(idList[0]);
            op=PRODUCT;
        }
        else if(idList.length==2&&utils.isNumeric(idList[0])&&utils.isNumeric(idList[1])) {
            sellerId=idList[1];
            op=INVOICE;
        }
        Long code,price=Long.valueOf(0);
        try {

            Integer stock ,flag1,flag2;
            if(utils.isNumeric(inf.get(1))&&utils.isNumeric(idList[0]))
            {
                code = Long.valueOf(inf.get(1));
            }
            else code= Long.valueOf(-1);
            if(utils.isNumeric(inf.get(2)))
            {
                stock=Integer.valueOf(inf.get(2));
            }
            else stock= -1;
            if(utils.isNumeric(inf.get(3)))
            {
                price = Long.valueOf(inf.get(3));
            }
            else price= Long.valueOf(-1);

            if(idInvoice!=null)
            {
                BuyInvoices buyInvoices=wkhPostMetaRepository.reportInvoices(String.valueOf(idInvoice));

    // product code = inf1 and  exists then change price and stock
    if (code != Long.valueOf(-1)&&String.valueOf(idInvoice).equals(buyInvoices.getIdInvoice())) {
        postId = wkhPostMetaRepository.existsCode(String.valueOf(code));
        if (postId != null) {
            Integer oldStock = Integer.valueOf(wkhPostMetaRepository.stockIdCode(String.valueOf(code)).get(0));
            Long oldPrice = Long.valueOf(wkhPostMetaRepository.priceIdCode(String.valueOf(code)).get(0));
            flag1 = wkhPostMetaRepository.insertBuyData(nowDate, userName, String.valueOf(code), stock, oldStock, price, oldPrice, idInvoice);

            if (stock != -1 && price != Long.valueOf(-1) ) {

                wkhPostMetaRepository.updateInvoices(buyInvoices.getNumProduct()+1,buyInvoices.getPrice()+price,String.valueOf(idInvoice));
                flag1 = wkhPostMetaRepository.updateStock(String.valueOf(stock + Integer.valueOf(wkhPostMetaRepository.stockIdCode(String.valueOf(code)).get(0))), postId);

            }
            else errorMsg+="ایراد در اطلاعات ورودی کد کالا یا شماره فاکتور";



        }
        else {// @@@ Adding new product to site
            wkhPostsRepository.insertProduct(String.valueOf(code));
            wkhPostMetaRepository.insertSku(wkhPostsRepository.lastId(), String.valueOf(code));
            wkhPostMetaRepository.insertStock(wkhPostsRepository.lastId(), String.valueOf(stock));
            wkhPostMetaRepository.insertPrice(wkhPostsRepository.lastId(), String.valueOf(price));
            wkhPostMetaRepository.insertRegularPrice(wkhPostsRepository.lastId(), String.valueOf(price));
            wkhPostMetaRepository.insertCategory(wkhPostsRepository.lastId(), 2);
            wkhPostMetaRepository.insertCategory(wkhPostsRepository.lastId(), 15);
            //@@@@@@@@@@@@@@@@@ Adding product to invoice


            flag1 = wkhPostMetaRepository.insertBuyData(nowDate, userName, String.valueOf(code), stock, 0, price, Long.valueOf(0), idInvoice);
            if (stock != -1) {
                wkhPostMetaRepository.updateInvoices(buyInvoices.getNumProduct()+1,buyInvoices.getPrice()+price,String.valueOf(idInvoice));
                flag1 = wkhPostMetaRepository.updateStock(String.valueOf(stock + Integer.valueOf(wkhPostMetaRepository.stockIdCode(String.valueOf(code)).get(0))), postId);

            }


        }

        }
    else errorMsg+="ایراد در اطلاعات ورودی کد کالا یا شماره فاکتور";
    }
    else errorMsg+="ایراد در اطلاعات ورودی کد کالا یا شماره فاکتور";

        } catch (Exception e){
            errorMsg+=e.getMessage();

        }
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        // String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        List<String> processList=new ArrayList<>();
        if(fileVoice!=null) {
            processList = proccessMessage.proccess(message, SHOP, Rd);
        }
        else{
            List<String> name=wkhPostMetaRepository.nameIdCode(message);
            if(name.size()>0)
                processList.add(name.get(0));
            else processList.add("-1");
           // List<String> price=wkhPostMetaRepository.priceIdCode(message);
           // if(price.size()>0)
                processList.add(String.valueOf(price));

            List<String> stock=wkhPostMetaRepository.stockIdCode(message);

                processList.add(String.valueOf(stock));


           // processList.add(wkhPostMetaRepository.imageUrlId(message));

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
    public ResponseEntity<JSONObject> saveProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                                  String checkBox1, String checkBox2, String checkBox3, UserKind userKind) {
        Date date=new Date();
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
            if(utils.isNumeric(inf.get(3)))
            {
                price = Long.valueOf(inf.get(3));
            }
            else price= Long.valueOf(-1);




            // product code = inf1 and  exists then change price and stock
            if(code!=Long.valueOf(-1)) {
                postId = wkhPostMetaRepository.existsCode(String.valueOf(code));
                if (postId != null) {
                    if(stock!=-1)
                        flag1 = wkhPostMetaRepository.updateStock(String.valueOf(stock), postId);
                    if(price!=Long.valueOf(-1)){
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
    public ResponseEntity<JSONObject> searchProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
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
            if(name.size()>0) {
               List<String> postIdLst=wkhPostMetaRepository.postIdCode(message);
               if(postIdLst.size()==1)
                processList.add(name.get(0) + "-pid" + postIdLst.get(0));
               else if(postIdLst.size()==2) processList.add(name.get(0) + "-pid" + postIdLst.get(0)+","+postIdLst.get(1));
               else processList.add(name.get(0) + "-pid..." );
            }
            else processList.add("-1");
            List<String> price=wkhPostMetaRepository.priceIdCode(message);
            if(price.size()>0) {
                if(role==USER)
                processList.add(price.get(0));
                else if(role==ADMIN)
                {   List<Long> buyPrice=wkhPostMetaRepository.buyPrice(message);
                    if(buyPrice.size()!=0)
                    processList.add(price.get(0)+ "ریال"+"(قیمت خرید"+String.valueOf(buyPrice.get(buyPrice.size()-1))+")");
                    else processList.add(price.get(0)+ "ریال"+"--"+"قیمت خرید ندارد");
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
                    jsonObject.put("inf_text", "قیمت : " + processList.get(i - 1) );
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
   }
