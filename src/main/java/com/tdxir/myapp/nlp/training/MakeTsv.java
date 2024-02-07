package com.tdxir.myapp.nlp.training;

import com.tdxir.myapp.model.Mahak;
import com.tdxir.myapp.nlp.Pipeline;
import com.tdxir.myapp.repository.MahakRepository;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
// this class gets a text file and create a text file with tsv extension i.e added tab and 0 after every token
public class MakeTsv {


    @Autowired
    private MahakRepository mahakRepository;

    private static final String SERVER_LOCATION = "/opt/tomcat/resource";

    public MultipartFile createTsv(MultipartFile file) throws IOException {
        //  this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir-linux", ""/*"~/uploads/files"*/));
        // tokenize   make words

        File filereply = new File(SERVER_LOCATION + File.separator + file.getOriginalFilename() );//+ EXTENSION);
        Path path = Paths.get(filereply.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));


        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        String text=null;
        try {
             text = new String(resource.getByteArray(), "UTF-8"); // for UTF-8 encoding
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       // String text=resource.getByteArray().toString();//"I am reza who are you";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList=coreDocument.tokens();
        List<String> stringList=new ArrayList<String>();
        for (CoreLabel coreLabel:coreLabelList)
        {
            stringList.add(coreLabel.originalText());
            System.out.println(coreLabel.originalText());
            System.out.println(stringList.get(0));
        }
        try {
            File fileTok =new File(SERVER_LOCATION + File.separator + file.getOriginalFilename() +".tok");
            //File fileTok = new File("f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.tok");//ner_training.tok");
            //File fileTsv = new File("f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.tsv");//ner_training.tsv");
            File fileTsv =new File(SERVER_LOCATION + File.separator + file.getOriginalFilename() +".tsv");


            if(!fileTok.exists())
                fileTok.createNewFile();//.createNewFile();
            if(!fileTsv.exists())
                fileTsv.createNewFile();//.createNewFile();

            BufferedWriter bufferTok = new BufferedWriter(new FileWriter(fileTok));
            BufferedWriter bufferTsv = new BufferedWriter(new FileWriter(fileTsv));
            for (int lenghstr =0;lenghstr<=stringList.size()-1;++lenghstr) {
                bufferTok.write(stringList.get(lenghstr)+"\n");
                bufferTsv.write(stringList.get(lenghstr)+"\tکالا\n");
                //buffer.newLine();
                System.out.println(stringList.get(lenghstr)+"\tO\n");
            }

            bufferTok.close();
            bufferTsv.close();
            System.out.println("File has been created successfully");

        } catch (IOException e) {
            System.out.println("Exception Occurred:");
            e.printStackTrace();
        }


        System.out.println("tokens and ner tags");
        String tokensAndNERTags = coreDocument.tokens().stream().map(token -> "("+token.word()+","+token.ner()+")").collect(
                Collectors.joining(" "));
        System.out.println(tokensAndNERTags);
        return file;

    }
    public String pretrain() throws IOException {

        List<Mahak> mahakList= mahakRepository.findAll();
        File file=new File("f:\\opt\\tomcat\\resource\\pretrainfile.txt");
        if (!file.exists())
            file.createNewFile();
        BufferedWriter bufferTxt=new BufferedWriter(new  FileWriter(file));
        for(Mahak mL:mahakList)  {
         //   String strTemp="کد"+"\tO\n"+String.valueOf(mL.getCode())+"\tcode\n"+"نام"+"\tO\n"+mL.getName()+"\tname\n"+"قیمت"+"\tO\n"+String.valueOf(mL.getPrice())+"\tprice\n"+"تومان"+"\tO\n" +"تعداد"+"\tO\n"+String.valueOf(mL.getStock())+"\tstock\n"+"میباشد"+"\tO\n";
         String strTemp="قیمت"+"\tPrice\n"+mL.getName()+"\tProduct\n"+"چنده"+"\tO\n"+"?"+"\tQsign\n"+"\n"+//قیمت .. چنده؟
                        "قیمت"+"\tPrice\n"+mL.getName()+"\tProduct\n"+"چند"+"\tO\n"+"?"+"\tQsign\n"+"\n"+//قیمت...چند؟
                        "قیمت"+"\tPrice\n"+mL.getName()+"\tProduct\n"+"چقدره"+"\tO\n"+"?"+"\tQsign\n"+"\n"+//قیمت ....چقدره
                        mL.getName()+"\tProduct\n"+"چنده"+"\tO\n"+"قیمتش"+"\tPrice\n"+"?"+"\tQsign\n"+"\n"+//....چنده قیمتش؟
                        mL.getName()+"\tProduct\n"+"چقدره"+"\tO\n"+"قیمتش"+"\tPrice\n"+"?"+"\tQsign\n"+"\n"+//....چقدره قیمتش؟
                        mL.getName()+"\tProduct\n"+"چند"+"\tO\n"+"?"+"\tQsign\n"+"\n"+//....چند؟
                        mL.getName()+"\tProduct\n"+"چنده"+"\tO\n"+"قیمتش"+"\tPrice\n"+"?"+"\tQsign\n"+"\n";//....چنده قیمتش؟

        bufferTxt.write(strTemp);
        }
        bufferTxt.close();
        return "ok joojoo";

    }
}
