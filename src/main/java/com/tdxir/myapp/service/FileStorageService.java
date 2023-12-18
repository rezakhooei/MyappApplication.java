package com.tdxir.myapp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.type.DateTime;
import com.tdxir.myapp.ChatGpt.response.ChatGPTResponse;
import com.tdxir.myapp.model.Users;
import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.nlp.SentenceRecognizer;
import com.tdxir.myapp.repository.UsersDataRepository;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.hibernate.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private ChatGPTService serviceSpeecToText;
    @Value("${openai.api.key}")
    private  String apikey;
    private final Path fileStorageLocation;

    //private final UsersDataRepository repository;
    @Autowired
    private UsersDataRepository usersDataRepository;

    @Autowired
    public FileStorageService(Environment env) {
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

    public String storeFile(MultipartFile file,String inf1,String inf2,String inf3,String inf4) {
        Date date = new Date();
        // for record db
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //String currentUserName = authentication.getName();
        // Normalize file name
        String date_str = new SimpleDateFormat("yyyyMMddHHmmss").format(date) ;
        String fileName =file.getOriginalFilename();
      //  File oldFile = new File(fileName);
        fileName= authentication.getName()+'-'+date_str+'-'+fileName;

        //openAi API key="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"

        OpenAiService service=new OpenAiService(apikey);

        CreateTranscriptionRequest request= new CreateTranscriptionRequest();
        request.setModel("whisper-1");//gpt-3.5-turbo
        request.setLanguage("FA");

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

            File filereply=new File(targetLocation.toString());


        //  String transcription=service.createTranscription(request,filereply).getText();//.createTranscription((request,file).getText();
           // inf1=transcription;
           // inf1="I love hassan but he doesn't so";
            inf1="Reza has a session on sunday 2 pm in Berlin. after that i should call my mam";//"من و حسن با هم غذا میخوریم و پیراشکی هم دوست داریم";
            SentenceRecognizer sentenceRecognizer=new SentenceRecognizer();

           // inf1="افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلانه به تصمیمات مان فکر کنیم. و به همین دلیل، ارزش ها، روابط و شغل هایی نامناسب نصیب مان می شود. ";

          //  List<String> temp=sentenceRecognizer.recognizeSentence(inf1);
         //   List<String> temp1=sentenceRecognizer.recognizeWords(inf1);
          //  List<String> temp2=sentenceRecognizer.recognizePos(inf1);
            List<String> temp3=sentenceRecognizer.recognizeNer(inf1);
            inf2=temp3.toString();
          //  List<String> temp4=sentenceRecognizer.recognizeLemma(inf1);
          //  List<String> temp5=sentenceRecognizer.recognizeSentiment(inf1);

            /*
            inf2=temp.get(0);
            if (temp.size()>1) inf3=temp.get(1);*/
         //   ChatGPTResponse chatCPTResponse = chatGPTService.getChatCPTResponse(inf1);//chatbotInputRequest.getMessage());
            //inf2=chatCPTResponse.getChoices().get(0).getMessage().getContent();
            inf2="افلاطون بیان می کند که زندگی ما در بیشتر مواقع به این خاطر با مشکل مواجه می شود که ما تقریباً هیچ وقت فرصت کافی به خودمان نمی دهیم تا به شکلی دقیق و عاقلان افلاطون قصد داشت تا نظم و شفافیت را در ذهن مخاطبینش به وجود آورد رضا";

// for record db
        //    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
            //String currentUserName = authentication.getName();


            // display time and date using toString()
          //  System.out.println(date.toString());

            var userData= UsersData.builder()

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


  // till record db

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
