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

    public String storeInfs(MultipartFile file, String inf1, String inf2, String inf3, String inf4) {
        Date date = new Date();
        // for record db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //String currentUserName = authentication.getName();
        // Normalize file name
        String date_str = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String fileName = file.getOriginalFilename();
        //  File oldFile = new File(fileName);
        fileName = authentication.getName() + '-' + date_str + '-' + fileName;

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
            if (fileName.contains("..")) {
                throw new RuntimeException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            File filereply = new File(targetLocation.toString());

            if(MyappApplication.WinLinux==1) {

               inf1=inf1;//googleSpeech.transcribeSpeech(filereply);


              // "قیمت رژ چنده؟";
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


                    inf1=inf1;//googleSpeech.transcribeSpeech(filereply);

                }
               catch (Exception e) {
                   inf1 = "apikey not valid";
               }


            }


            System.out.println(inf1);
            inf2 = "افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد رضا";

if(inf1 != "apikey not valid") {
    var userData = UsersData.builder()

            .date(date)
            .userid(authentication.getName())
            .filename(fileName)
            .inf1(inf1)
            .inf2(inf2)
            .inf3(inf3)
            .inf4(inf4)
            .build();
    //userData.setUserid(authentication.getName());
    usersDataRepository.save(userData);// repository.save(userData);

                      }
            // till record db

            return inf1;//fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public List<String> proccessMessage(String message) {
        MakeNer makeNer = new MakeNer();
        CRFClassifier model;
        if(MyappApplication.WinLinux==1) {

            model = makeNer.getModel(pathWin+"ner-model.ser.gz");
        }
        else {

            model = makeNer.getModel(pathLinux+"ner-model.ser.gz");
        }
        // String[] tests = new String[]{"apple watch", "samsung mobile phones", " lcd 52 inch tv"};
       // String[] tests = new String[]{"قیمت","رژ","چنده", };//"؟","برس زرد 1000 تومان است","1000","من"};
       /* for (String item : tests) {
           System.out.println(makeNer.doTagging(model, item));
        }*/
          String messageTagged=makeNer.doTagging(model, message);
        SentenceRecognizer sentenceRecognizer = new SentenceRecognizer();

        ArrayList<String> temp33= sentenceRecognizer.recognizeNer(message);////tests[0]);
        List<String> temp4= sentenceRecognizer.recognizePos(message);
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
        // قطعه زیر برای چک makener.doTaging  و recognizener بود که موقتا حذف شد
   /*     for(int i=0;i<temp3.size();++i)
        if (String.valueOf(temp3.get(i))!=String.valueOf(temp33.get(i))) {
            System.out.println(String.valueOf(i) +temp3.get(i)+","+temp33.get(i)+ "error");
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
        {
            Pattern pattern2 = Pattern.compile("NameShop Price");
            Pattern pattern3 = Pattern.compile("Price NameShop");
            Matcher matcher2 = pattern2.matcher(sentence);
            Matcher matcher3 = pattern3.matcher(sentence);
            if (matcher2.find() || matcher3.find())
            {  //     if sentence is question about price by name of product

                for (int i = 0; i <= temp3.size() - 1; ++i)
                {
                    String strTemp = new String(temp3.get(++i));
                    if (strTemp.equals("NameShop"))
                    {   strTemp=temp3.get(i-1);
                        List<String> postIds = wkhPostsRepository.PostId("%"+strTemp+"%");
                        if (postIds.size() != 0)
                        {
                            List<String> pricelist = new ArrayList<>();
                            for (String post_id : postIds)
                            {

                                pricelist.add(String.valueOf(post_id.substring(0,post_id.indexOf(",")))+"-"+String.valueOf(wkhPostMetaRepository.price(String.valueOf(post_id.substring(post_id.indexOf(",")+1))))+"ریال");
                            }
                            return pricelist;
                        }
                    }

                }


            }
        }
        ArrayList<String> message1=new ArrayList<String>();
        message1.add(message);
        return temp3;//message1;
    }

}







        // inf1="I love hassan but he doesn't so";
        // inf1="Reza has a session on sunday 2 pm in Berlin. after that i should call my mam";//"من و حسن با هم غذا میخوریم و پیراشکی هم دوست داریم";
          /*
            SentenceRecognizer sentenceRecognizer=new SentenceRecognizer();

            String line=null;
            try {
               // File fileText = new File("f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.txt");//ner_training_data.txt");
                String fileText="f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.txt";
                //if (fileText.exists()) {
                  //  System.out.println("****** Reading file ... ******");
                  //  BufferedReader buffer = new BufferedReader(new FileReader(fileText));

                  //  line = buffer.readLine();
                    String data = "";
                    data = new String(
                            Files.readAllBytes(Paths.get(targetLocation.toString())));//fileText)));
                    inf1=data;
                    System.out.println("****** Finish Reading file ******");


                   // List<String> temp0=mySentenceRecognizer.recognizeWords(data);
               // }else{
                  //  System.out.println(fileText.getAbsolutePath()+" not exist");
               // }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

         //   inf1="افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. ";

           // List<String> temp0=mySentenceRecognizer.recognizeWords(inf1);
          //  List<String> temp=sentenceRecognizer.recognizeSentence(inf1);
        //    List<String> temp1=sentenceRecognizer.recognizeWords(inf1);
          //  List<String> temp2=sentenceRecognizer.recognizePos(inf1);
            List<String> temp3=sentenceRecognizer.recognizeNer(inf1);
           // inf2=temp3.toString();
          //  List<String> temp4=sentenceRecognizer.recognizeLemma(inf1);
           // List<String> temp5=sentenceRecognizer.recognizeSentiment(inf1);

            /*
            inf2=temp.get(0);
            if (temp.size()>1) inf3=temp.get(1);*/
        //   ChatGPTResponse chatCPTResponse = chatGPTService.getChatCPTResponse(inf1);//chatbotInputRequest.getMessage());
        //inf2=chatCPTResponse.getChoices().get(0).getMessage().getContent();



