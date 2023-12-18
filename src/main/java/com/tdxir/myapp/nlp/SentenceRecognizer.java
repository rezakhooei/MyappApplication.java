package com.tdxir.myapp.nlp;

import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.SocketOption;
import java.util.*;
import java.util.stream.Collectors;

public class SentenceRecognizer {
    public List<String> recognizeWords(String text){

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
        }
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
        StanfordCoreNLP stanfordCoreNLP =Pipeline.getPipeline();
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


