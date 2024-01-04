package com.tdxir.myapp.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MySentenceRecognizer {


    public List<String> recognizeWords(String text){
      //  this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir-linux", ""/*"~/uploads/files"*/));
         // tokenize   make words
        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
        //String text="I am reza who are you";
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
            File fileTok = new File("f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.tok");//ner_training.tok");
            File fileTsv = new File("f:\\opt\\tomcat\\my-nlp\\stanfordexample\\jane-austen-emma-ch2.tsv");//ner_training.tsv");
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
        return stringList;

    }
    public List<String> recognizeSentiment(String text){
        // Status of sentence negative positive neutral ....

        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
        //String text="I am reza who are you";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> sentences=coreDocument.sentences();
        List<String> stringList=new ArrayList<String>();
        for (CoreSentence sentence:sentences)
        {
            stringList.add(sentence.sentiment());
            System.out.println(sentence);
        }
        return stringList;

    }
    public List<String> recognizeNer(String text){

         // kind of noun ... city or person ...
        StanfordCoreNLP stanfordCoreNLP =MyPipeline.getPipeline();
        //text="Obama lives in Berlin";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<String> stringList=new ArrayList<String>();
        for (CoreEntityMention em : coreDocument.entityMentions())
        {
            stringList.add("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
            System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
        }

        System.out.println("---");
        System.out.println("tokens and ner tags");
        String tokensAndNERTags = coreDocument.tokens().stream().map(token -> "("+token.word()+","+token.ner()+")").collect(
                Collectors.joining(" "));
        System.out.println(tokensAndNERTags);

        List<CoreLabel> coreLabelList=coreDocument.tokens();


        for (CoreLabel coreLabel:coreLabelList)
        {
            stringList.add(coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class));
            System.out.println(coreLabel.originalText());


        }


        return stringList;

    }
    public List<String> recognizePos(String text){

         //                position of words(noun verb ....)
        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
        // text="I live in Berlin";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList=coreDocument.tokens();
        List<String> stringList=new ArrayList<String>();
        for (CoreLabel coreLabel:coreLabelList)
        {
            stringList.add(coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class));
            System.out.println(coreLabel.originalText());
        }
        return stringList;

    }
    public List<String> recognizeLemma(String text){

         //  Root of words
        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
        //String text="I am reza who are you";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList=coreDocument.tokens();
        List<String> stringList=new ArrayList<String>();
        for (CoreLabel coreLabel:coreLabelList)
        {
            stringList.add(coreLabel.lemma());
            System.out.println(coreLabel.originalText());
        }
        return stringList;

    }
 public List<String> recognizeSentence(String text){


        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
        //String text="I am reza who are you";
        CoreDocument coreDocument=new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> coreSentencesList=coreDocument.sentences();
        List<String> stringList=new ArrayList<String>();
        for (CoreSentence coreSentence:coreSentencesList)
        {
            stringList.add(coreSentence.toString());
            System.out.println(coreSentence.toString());
        }
return stringList;

    }
}


