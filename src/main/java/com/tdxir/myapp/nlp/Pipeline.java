package com.tdxir.myapp.nlp;



//import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Pipeline {
    private   static Properties properties;
    private  static  String propertiesName="tokenize,ssplit,pos,lemma,ner,parse,sentiment";
    private static StanfordCoreNLP stanfordCoreNLP;
    private Pipeline(){}
    static {
        properties=new Properties();
        properties.setProperty("annotators",propertiesName);
        properties.setProperty("ner.useSUTime", "false");
       // properties.setProperty("lang","en");
       // properties.setProperty("pos.model","F:\\opt\\tomcat\\resource\\english-left3words-distsim.tagger");//langdetect-183.bin");//"F:\\opt\\tomcat\\resource\\en_ewt_tagger.pt");//
        //properties.setProperty("ner.model","F:\\opt\\tomcat\\resource\\englishPCFG.ser");
    }

    public static StanfordCoreNLP getPipeline() {
        if (stanfordCoreNLP==null) {
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }
        return stanfordCoreNLP;
    }
}
