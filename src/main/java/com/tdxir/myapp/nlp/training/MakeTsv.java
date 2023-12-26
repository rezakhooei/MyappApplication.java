package com.tdxir.myapp.nlp.training;

import com.tdxir.myapp.nlp.Pipeline;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// this class gets a text file and create a text file with tsv extension i.e added tab and 0 after every token
public class MakeTsv {
    private static final String SERVER_LOCATION = "/opt/tomcat/resource";

    public MultipartFile craeteTsv(MultipartFile file) throws IOException {
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
            /*If file gets created then the createNewFile()
             * method would return true or if the file is
             * already present it would return false
             */

            if(!fileTok.exists())
                fileTok.createNewFile();//.createNewFile();
            if(!fileTsv.exists())
                fileTsv.createNewFile();//.createNewFile();

            BufferedWriter bufferTok = new BufferedWriter(new FileWriter(fileTok));
            BufferedWriter bufferTsv = new BufferedWriter(new FileWriter(fileTsv));
            for (int lenghstr =0;lenghstr<=stringList.size()-1;++lenghstr) {
                bufferTok.write(stringList.get(lenghstr)+"\n");
                bufferTsv.write(stringList.get(lenghstr)+"\tO\n");
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
}
