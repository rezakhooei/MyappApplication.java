package com.tdxir.myapp.controller;

//import com.google.gson.JsonObject;
import com.tdxir.myapp.model.UserKind;
import com.tdxir.myapp.model.Users;
import com.tdxir.myapp.nlp.training.MakeTsv;
import com.tdxir.myapp.repository.UserRepository;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.tdxir.myapp.service.GoogleSpeech;
import com.tdxir.myapp.service.ProccessMessage;
import com.tdxir.myapp.service.RecordAndProccessMessageService;
import com.tdxir.myapp.utils.Utils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.UserKind.SHOP;

@RestController
@RequestMapping("/api/uploadFile")
public class ReciveMessageController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    private WkhPostMetaRepository wkhPostMetaRepository;
    private final RecordAndProccessMessageService recordAndProccessMessageService;
    @Autowired
    private ProccessMessage proccessMessage;
    private final GoogleSpeech googleSpeech = new GoogleSpeech();
    private  MakeTsv makeTsv;
    private Utils utils;
    private static final String EXTENSION = ".wav";
    private static final String SERVER_LOCATION = "/opt/tomcat/uploads";
    private static final String SERVER_LOCATION_PRODUCT_IMG = "/var/www/khooei.ir/public_html/wp-content/uploads/";
    private static final String SERVER_LOCATION_PRODUCT_IMG_WIN = "/var/www/khooei.ir/public_html/wp-content/uploads/2024/04/";
    private String errorMsg="";

    public ReciveMessageController(RecordAndProccessMessageService recordAndProccessMessageService, ProccessMessage proccessMessage) {

        this.recordAndProccessMessageService = recordAndProccessMessageService;
        this.proccessMessage = proccessMessage;
    }


    @PostMapping
    public ResponseEntity<JSONObject> uploadFile(
            @RequestParam(name = "fileVoice", required = false) MultipartFile fileVoice,
            @RequestParam(name = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam("inf1") String inf1, @RequestParam("inf2") String inf2,@RequestParam("inf3") String inf3,@RequestParam("inf4")String inf4,
            @RequestParam("selected_rds") String selected_rds ,@RequestParam("selected_chks") String selected_chks
                     ) throws Exception {   //googleSpeech.initialize();
        String checkBox1 = "false", checkBox2 = "false", checkBox3 = "false", checkBox4 = "false", panel1 = "", panel2 = "", panel3 = "";

        // inf1="Rds="+selected_rds+"-Chks="+selected_chks;
        // inf1=inf2;
        panel1 = "Rd" + selected_rds.substring(selected_rds.indexOf("panel1-") + 7, selected_rds.indexOf("panel1-") + 8);
        panel2 = "Rd" + selected_rds.substring(selected_rds.indexOf("panel2-") + 7, selected_rds.indexOf("panel2-") + 8);
        panel3 = "Rd" + selected_rds.substring(selected_rds.indexOf("panel3-") + 7, selected_rds.indexOf("panel3-") + 8);

        String[] checkBoxes = selected_chks.split(",");
        for (int i = 0; i <= checkBoxes.length - 1; ++i) {
            if (checkBoxes[i].equals(String.valueOf(1))) checkBox1 = "true";
            if (checkBoxes[i].equals(String.valueOf(2))) checkBox2 = "true";
            if (checkBoxes[i].equals(String.valueOf(3))) checkBox3 = "true";
            if (checkBoxes[i].equals(String.valueOf(4))) checkBox4 = "true";

        }


        List<String> inf = null;
        inf = new ArrayList<>();

        inf.add(new String(inf1));
        inf.add(new String(inf2));
        inf.add(new String(inf3));
        inf.add(new String(inf4));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Users user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        String message = inf2;
        // message= recordAndProccessMessageService.storeInfs(file, inf1, inf2, inf3, inf4);
        System.out.println(authentication.getName());

// below added for test 14021229
        //   String fileNameTemp = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
        // inf1=fileNameTemp;

        if (!authentication.getName().equals("javadghane18@gmail.com") && (message != "apikey not valid OR google didn't reply"))
        {

            //message="کد 902 چند تا داریم و برای چیست و چقدر بخریم ؟";
            //List<String> processList=recordAndProccessMessageService.proccessMessage(message,user.getUserKind());


            //  ProccessMessage proccessMessage=new ProccessMessage(user.getUserKind());
            //proccessMessage.(user.getUserKind());
            // array.add(jsonObject);

          /*   MakeTsv makeTsv;//=new MakeTsv(new MahakRepository()) ;
             makeTsv = new MakeTsv();
             makeTsv.createTsv(file);//.getResource().getFile());
*/
          if(user.getUserKind()==SHOP){
              if (user.getRole()==ADMIN) {
                  if (panel3.equals("Rd1")) {

                      return searchProduct(panel2, fileVoice, fileImage, inf, checkBox1, checkBox2, checkBox3, user.getUserKind());

                  } else if (panel3.equals("Rd2")) {
                      return saveProduct(panel2, fileVoice, fileImage, inf, checkBox1, checkBox2, checkBox3, user.getUserKind());
                  } else if (panel3.equals("Rd3")) {
                  }
              }
              else{                  if (panel3.equals("Rd1")) {

                  return searchProduct(panel2, fileVoice, fileImage, inf, checkBox1, checkBox2, checkBox3, user.getUserKind());

              } else if (panel3.equals("Rd2")) {
                  return saveProduct(panel2, fileVoice, fileImage, inf, checkBox1, checkBox2, checkBox3, user.getUserKind());
              } else if (panel3.equals("Rd3")) {
              }

              }
          }

        } else if (authentication.getName().equals("javadghane18@gmail.com"))
        {
            JSONObject jsonObjectMain = new JSONObject();
            JSONObject jsonObject = new JSONObject();


            if (panel2.equals("Rd3")) {//  Server will reply ImageANdVoice to android app

                String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
                fileName = "receivedmessage.wav";
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
            } else if (panel2.equals("Rd1")) {  // Server will reply only Voice to android app
                String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
                fileName = "receivedmessage.wav";
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

            } else if (panel2.equals("Rd2")) {    // Server will reply only Image to android app
                String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
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
                    jsonObjectMain.put("fileContentVoice", null);
                    jsonObjectMain.put("fileContentImage", resource1.getByteArray());
                } catch (IOException ex) {
                    errorMsg = ex.getMessage();
                }

            } else if (panel2.equals("Rd4") || panel2.equals("noFile")) {
                String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
                jsonObjectMain.put("fileContentVoice", null);
                jsonObjectMain.put("fileContentImage", null);

            }
            JSONArray array = new JSONArray();

            for (int i = 1; i <= 3; ++i) {
                jsonObject.put("inf_id", String.valueOf(i));
                if (i == 1) {
                    String chkBoxStr = "";
                    if (checkBox1.equals("true")) chkBoxStr = "check box1 selected" + "\n";
                    if (checkBox2.equals("true")) chkBoxStr += "check box2 selected" + "\n";
                    if (checkBox3.equals("true")) chkBoxStr += "check box3 selected" + "\n";
                    chkBoxStr += errorMsg;
                    jsonObject.put("inf_text", chkBoxStr);
                } else
                    jsonObject.put("inf_text", String.valueOf(i) + "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد. او دریافته بود که بسیاری از نظرات و قضاوت های ما از تفکرات جامعه نشأت می گیرد، از چیزی که یونانی ها آن را «دوکسا» یا «عقل جمعی حاکم» می نامند. افلاطون در 36 کتابی که نوشت، بارها و بارها نشان داد که این «عقل جمعی حاکم» می تواند پر از اشتباه، تبعیض و خرافه باشد و تفکرات شایع در مورد عشق، شهرت، پول و یا خوبی، چندان با منطق و واقعیت همخوانی ندارن" +
                            "افلاطون همچنین دریافته بود که چگونه انسان های مغرور، تحت سلطه ی غرایز و احساسات خود هستند و آن ها را با افرادی مقایسه می کرد که توسط اسب هایی وحشی که چشم هایشان پوشانده شده، به این سو و آن سو کشیده می شوند.رویای موجود در پسِ مفهوم عشق این است که می توانیم با نزدیک شدن به چنین افرادی، اندکی مانند آن ها شویم. عشق در نظر افلاطون، نوعی آموزش است. او عقیده دارد کسی که به معنای واقعی کلمه به فردی دیگر عشق می ورزد، تمایل خواهد داشت که توسط معشوق خود به فرد بهتری تبدیل شود. این یعنی شخص باید با کسی باشد که بخشی گمشده از هستی او را در اختیار دارد: ویژگی های خوبی که خودمان از آن ها بی بهره ایم." +
                            "افلاطون از ما می خواهد این را بپذیریم که کامل نیستیم و تمایل داشته باشیم که ویژگی های خوب دیگران را در خودمان پرورش دهیم. ");
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


        } else  //////   google didn't reply
        {
            JSONObject jsonObjectMain = new JSONObject();
            JSONObject jsonObject = new JSONObject();


            JSONArray array = new JSONArray();
        /*array.add("element_1");
        array.add("element_2");
        array.add("element_3");*/
            for (int i = 1; i <= 3; ++i) {
                jsonObject.put("inf_id", String.valueOf(i));
                jsonObject.put("inf_text", String.valueOf(i) + "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد. او دریافته بود که بسیاری از نظرات و قضاوت های ما از تفکرات جامعه نشأت می گیرد، از چیزی که یونانی ها آن را «دوکسا» یا «عقل جمعی حاکم» می نامند. افلاطون در 36 کتابی که نوشت، بارها و بارها نشان داد که این «عقل جمعی حاکم» می تواند پر از اشتباه، تبعیض و خرافه باشد و تفکرات شایع در مورد عشق، شهرت، پول و یا خوبی، چندان با منطق و واقعیت همخوانی ندارن" +
                        "افلاطون همچنین دریافته بود که چگونه انسان های مغرور، تحت سلطه ی غرایز و احساسات خود هستند و آن ها را با افرادی مقایسه می کرد که توسط اسب هایی وحشی که چشم هایشان پوشانده شده، به این سو و آن سو کشیده می شوند.رویای موجود در پسِ مفهوم عشق این است که می توانیم با نزدیک شدن به چنین افرادی، اندکی مانند آن ها شویم. عشق در نظر افلاطون، نوعی آموزش است. او عقیده دارد کسی که به معنای واقعی کلمه به فردی دیگر عشق می ورزد، تمایل خواهد داشت که توسط معشوق خود به فرد بهتری تبدیل شود. این یعنی شخص باید با کسی باشد که بخشی گمشده از هستی او را در اختیار دارد: ویژگی های خوبی که خودمان از آن ها بی بهره ایم." +
                        "افلاطون از ما می خواهد این را بپذیریم که کامل نیستیم و تمایل داشته باشیم که ویژگی های خوب دیگران را در خودمان پرورش دهیم. ");
                array.add(new JSONObject(jsonObject));
                jsonObject.clear();
            }
            jsonObjectMain.put("inf", array);
            // array.add(jsonObject);


            String fileName = recordAndProccessMessageService.storeInfs(fileVoice, fileImage, inf);
            fileName = "monshi.mp3";
            inf.add(0, "جوجل پاسخ نداد");
            UploadResponse uploadResponse = new UploadResponse(fileName, fileName, inf);

            String image = fileName;//"file";
            File filereply = new File(SERVER_LOCATION + File.separator + image);//+ EXTENSION);

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filereply");//monshi.mp3");

            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            Path path = Paths.get(filereply.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            byte[] encoder = Base64.getEncoder().encode(resource.getByteArray());

            jsonObjectMain.put("file_content", resource.getByteArray());
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
        return null;
    }


    ResponseEntity<JSONObject> searchProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                             String checkBox1, String checkBox2, String checkBox3, UserKind userKind)
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
                   if(price.size()>0)
                   processList.add(price.get(0));
                   else processList.add("-1");
                   List<String> stock=wkhPostMetaRepository.stockIdCode(message);
                   if(stock.size()>0)
                   processList.add(stock.get(0));
                   else processList.add("-1");

                   processList.add(wkhPostMetaRepository.imageUrl(message));

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
    ResponseEntity<JSONObject> saveProduct(String Rd, MultipartFile fileVoice, MultipartFile fileImage, List<String> inf,
                                             String checkBox1, String checkBox2, String checkBox3, UserKind userKind) {
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
                    if(price!=Long.valueOf(-1))
                    flag2 = wkhPostMetaRepository.updatePrice(String.valueOf(price), postId);
                    if(nameList[1].equals(inf.get(1)))
                    wkhPostMetaRepository.updateName(nameList[0], postId);
                    if (fileImage != null) {
                        List<String> thumbnail = wkhPostMetaRepository.findThumbnail(String.valueOf(code));
                        if (thumbnail.get(0) == null) {


                            Integer test = wkhPostMetaRepository.updateImage("2024/04/" + fileImage.getOriginalFilename(), thumbnail.get(0));
                            recordAndProccessMessageService.storeImage(fileImage, SERVER_LOCATION_PRODUCT_IMG_WIN);
                        } else {


                            Integer test = wkhPostMetaRepository.updateImage("2024/04/" + fileImage.getOriginalFilename(), thumbnail.get(0));
                            recordAndProccessMessageService.storeImage(fileImage, SERVER_LOCATION_PRODUCT_IMG_WIN);


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

            processList.add(wkhPostMetaRepository.imageUrl(message));

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
                    jsonObject.put("inf_text",""+errorMsg);//, processList.get(i - 1) + ":" + "عکس");
                else jsonObject.put("inf_text",  "عکس  تعریف نشده است"+errorMsg);
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