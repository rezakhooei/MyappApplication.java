package com.tdxir.myapp.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tdxir.myapp.MyappApplication;
import com.tdxir.myapp.auth.OpenApiKeyValidation;
import com.tdxir.myapp.model.UserKind;
import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.nlp.SentenceRecognizer;
import com.tdxir.myapp.nlp.training.MakeNer;
import com.tdxir.myapp.repository.UsersDataRepository;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.service.OpenAiService;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.tdxir.myapp.MyappApplication.*;

@Service
public class RecordAndProccessMessageService {
    @Autowired
    private final GoogleSpeech googleSpeech = new GoogleSpeech();
    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private ChatGPTService serviceSpeecToText;
    @Value("${openai.api.key}")
    private String apikey;
    @Autowired
    private OpenApiKeyValidation openApiKeyValidation;
    @Value("${app.file.resource-dir-win}")
    private String pathWin;
    @Value("${app.file.resource-dir-linux}")
    private String pathLinux;
    private final Path fileStorageLocation;
    @Value("/opt/tomcat/uploads/invoices/")
    private  Path invoiceDirLinux;
    //private final UsersDataRepository repository;
    @Autowired
    private UsersDataRepository usersDataRepository;
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    private WkhPostMetaRepository wkhPostMetaRepository;

    @Autowired
    public RecordAndProccessMessageService(Environment env) {
        this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir-linux", ""/*"~/uploads/files"*/))
                //      this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir-win"))
                .toAbsolutePath().normalize();


        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }


    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String[] fileNameParts = fileName.split("\\.");

        return fileNameParts[fileNameParts.length - 1];
    }
    public String storeInfs(MultipartFile voiceFile,MultipartFile imageFile , List<String> inf) {
        Date date = new Date();
        // for record db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //String currentUserName = authentication.getName();
        // Normalize file name
        String date_str = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String voiceFileName ="";
        String imageFileName ="";
        if(voiceFile!=null) {
            voiceFileName = voiceFile.getOriginalFilename();
            //  File oldFile = new File(fileName);
            voiceFileName = authentication.getName() + '-' + date_str + '-' + voiceFileName;

            //openAi API key="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"


            // String output =    new Date().getTime() + "-file." + getFileExtension(file.getOriginalFilename());
/*
        File newFile = new File(fileName);

        if(oldFile.renameTo(newFile)) {
            System.out.println("File renamed successfully!");  // Output: File renamed successfully!
        } else {
            System.out.println("Failed to rename the file.");
        }
*/
            try {
                // Check if the filename contains invalid characters
                if (voiceFileName.contains("..")) {
                    throw new RuntimeException(
                            "Sorry! Filename contains invalid path sequence " + voiceFileName);
                }

                Path targetLocation = this.fileStorageLocation.resolve(voiceFileName);
                Files.copy(voiceFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                File filereply = new File(targetLocation.toString());

            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + voiceFileName + ". Please try again!", ex);
            }
        }
        if(imageFile!=null) {
            imageFileName = imageFile.getOriginalFilename();
            //  File oldFile = new File(fileName);
            imageFileName = authentication.getName() + '-' + date_str + '-' + imageFileName;

            //openAi API key="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"


            // String output =    new Date().getTime() + "-file." + getFileExtension(file.getOriginalFilename());
/*
        File newFile = new File(fileName);

        if(oldFile.renameTo(newFile)) {
            System.out.println("File renamed successfully!");  // Output: File renamed successfully!
        } else {
            System.out.println("Failed to rename the file.");
        }
*/
            try {
                // Check if the filename contains invalid characters
                if (imageFileName.contains("..")) {
                    throw new RuntimeException(
                            "Sorry! Filename contains invalid path sequence " + imageFileName);
                }

                Path targetLocation = this.fileStorageLocation.resolve(imageFileName);
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                File filereply = new File(targetLocation.toString());

            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + imageFileName + ". Please try again!", ex);
            }
        }


        if(MyappApplication.WinLinux==1) {

            inf.add(0,"قیمت رژ چنده؟");//[0]=;
        }
        else {
            //  @@@@    below code is for using openAI Speech to text
                /*
               try //if(openApiKeyValidation.checkApi(apikey))

                {
                    OpenAiService service = new OpenAiService(apikey);

                    CreateTranscriptionRequest request = new CreateTranscriptionRequest();
                    request.setModel("whisper-1");//gpt-3.5-turbo
                    request.setLanguage("FA");
                  String transcription=service.createTranscription(request,filereply).getText();//.createTranscription((request,file).getText();
                    String strTemp=googleSpeech.transcribeSpeech("f:\\opt\\tomcat\\uploads\\reza@yahoo.com-20231204002912-file1.mp3");//receivedmessage.wav");
                   inf1= transcription;
                }*/
            //  @@@ below code is for using GOOGLE Speech to text instead of above code

            try

            {


                //     inf.add(0,googleSpeech.transcribeSpeech(filereply));


            }
            catch (Exception e) {
                inf.add(0,"apikey not valid OR google didn't reply");
            }


        }


        System.out.println(inf.get(0));
        // inf.add(1, "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد رضا");//
        for(int i=inf.size();i<=3;++i)
            inf.add("");
//if(inf1 != "apikey not valid") {
        var userData = UsersData.builder()

                .date(date)
                .userid(authentication.getName())
                .voiceFileName(voiceFileName)
                .imageFileName(imageFileName)
                .inf1(inf.get(0))
                .inf2(inf.get(1))
                .inf3(inf.get(2))
                .inf4(inf.get(3))
                .build();
        //userData.setUserid(authentication.getName());
        usersDataRepository.save(userData);// repository.save(userData);

        //       }
        // till record db

        return inf.get(0);//fileName;

    }
    public String storeInvoiceImg(MultipartFile imageFile) {
        Date date = new Date();
        // for record db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //String currentUserName = authentication.getName();
        // Normalize file name
        String date_str = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String voiceFileName ="";
        String imageFileName ="";

        if(imageFile!=null) {
            imageFileName = imageFile.getOriginalFilename();
            //  File oldFile = new File(fileName);
            imageFileName = authentication.getName() + '-' + date_str + '-' + imageFileName;


            try {
                // Check if the filename contains invalid characters
                if (imageFileName.contains("..")) {
                    throw new RuntimeException(
                            "Sorry! Filename contains invalid path sequence " + imageFileName);
                }
                if (WinLinux == 1)
                {   Path targetLocation = this.fileStorageLocation.resolve(imageFileName);
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }
                else {  Path targetLocation = this.fileStorageLocation.resolve(imageFileName);//invoiceDirLinux.resolve(imageFileName);
                    Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                }
               // File filereply = new File(targetLocation.toString());

            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + imageFileName + ". Please try again!", ex);
            }
        }



        return imageFileName;

    }
    public Boolean storeImage(MultipartFile  imageFile ,String path ) {
        Date date = new Date();
        // for record db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //String currentUserName = authentication.getName();
        // Normalize file name
        //String date_str = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String voiceFileName ="";
        String imageFileName ="";

        if(imageFile!=null) {
            imageFileName = imageFile.getOriginalFilename();
            //  File oldFile = new File(fileName);
           // imageFileName = authentication.getName() + '-' + date_str + '-' + imageFileName;


            try {
                // Check if the filename contains invalid characters
                if (imageFileName.contains("..")) {
                    throw new RuntimeException(
                            "Sorry! Filename contains invalid path sequence " + imageFileName);
                }

                Path targetLocation =Path.of(path+imageFileName);// this.fileStorageLocation.resolve(imageFileName);
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                File filereply = new File(targetLocation.toString());

            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + imageFileName + ". Please try again!", ex);
                //return false;
            }
        }

   return true;//fileName;

    }

    public List<String> proccessMessage(String message, UserKind userKind) {

       MakeNer makeNer = new MakeNer();
        CRFClassifier model;
       if(MyappApplication.WinLinux==1) {

            model = makeNer.getModel(pathWin+"ner-model.ser.gz");
        }
        else {

            model = makeNer.getModel(pathLinux+"ner-model.ser.gz");
        }

          String messageTagged=makeNer.doTagging(model, message);//"قیمت/Price مو/NameShop چنده/O ?/Qsign";
        SentenceRecognizer sentenceRecognizer = new SentenceRecognizer();

       // ArrayList<String> temp33= sentenceRecognizer.recognizeNer(message);////tests[0]);
        //List<String> temp4= sentenceRecognizer.recognizePos(message);
        ArrayList<String> temp3=new ArrayList<>();
        temp3.clear();
        String[] substrings = messageTagged.split(" ");
        for (String s : substrings)
        {
            String[] substrings1 = s.split("/");
            temp3.add(substrings1[0]);
            temp3.add(substrings1[1]);

            System.out.println(s);
        }
        // قطعه زیر برای چک makener.doTaging  و recognizener بود که موقتا حذف نشد
   /*    for(int i=0;i<temp3.size();++i)
        if (String.valueOf(temp3.get(i))!=String.valueOf(temp33.get(i))) {
            ArrayList<String> message1=new ArrayList<String>();
            System.out.println("for test ner and dotag");
            message1.add(String.valueOf(i) +temp3.get(i)+","+temp33.get(i)+ "doTagging with ner difference error");
            return message1;//temp3;

        }
*/

        String sentence = "";
        for (int i = 1; i <= temp3.size() - 1; ++i)
        {
            sentence += temp3.get(i++);
            sentence += " ";
        }
        Pattern pattern1 = Pattern.compile("Qsign");

        Matcher matcher1 = pattern1.matcher(sentence);
//  if sentence is question
        if (matcher1.find())
        {   if (userKind==UserKind.SHOP) {
            Pattern pattern2 = Pattern.compile("Product Price");
            Pattern pattern3 = Pattern.compile("Price Product");
            Matcher matcher2 = pattern2.matcher(sentence);
            Matcher matcher3 = pattern3.matcher(sentence);
            if (matcher2.find() || matcher3.find()) {  //     if sentence is question about price by name of product

                for (int i = 0; i <= temp3.size() - 1; ++i) {
                    String strTemp = new String(temp3.get(++i));
                    if (strTemp.equals("NameShop")) {
                        strTemp = temp3.get(i - 1);

                        List<String> postIds = wkhPostsRepository.PostIdName("%" + strTemp + "%");

                        if (postIds.size() != 0) {
                            List<String> pricelist = new ArrayList<>();
                            for (String post_id : postIds) {

                                pricelist.add(String.valueOf(post_id.substring(0, post_id.indexOf(","))) + "-" + String.valueOf(wkhPostMetaRepository.price(String.valueOf(post_id.substring(post_id.indexOf(",") + 1)))) + "ریال");
                            }
                            return pricelist;
                        }

                    }

                }


            }
        }
        else if (userKind==UserKind.SPORT){}
        else if(userKind==UserKind.PERSON){}
        else {}
        }
        else
        {
            ArrayList<String> message1=new ArrayList<String>();
            System.out.println("for test google");
            message1.add("Message Recorded");
            return message1;//temp3;
        }

        ArrayList<String> message1=new ArrayList<String>();
        System.out.println("for test google");
        message1.add(message);
        return temp3;//message1;//
    }

}



