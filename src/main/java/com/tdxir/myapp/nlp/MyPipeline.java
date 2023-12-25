
    package com.tdxir.myapp.nlp;



//import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

    public class MyPipeline {
        private   static Properties properties;
        private  static  String propertiesName="tokenize,ssplit,pos";//"tokenize,ssplit,pos,lemma,ner,parse,sentiment";
        private static StanfordCoreNLP stanfordCoreNLP;
        private MyPipeline(){}
        static {
            properties=new Properties();
            properties.setProperty("annotators",propertiesName);
            properties.setProperty("ner.useSUTime", "false");
            properties.setProperty("lang","fa");
            properties.setProperty("pos.model","F:\\opt\\tomcat\\resource\\persian.tagger");//english-left3words-distsim.tagger");//langdetect-183.bin");//"F:\\opt\\tomcat\\resource\\en_ewt_tagger.pt");//
            //  properties.setProperty("ner.model","F:\\opt\\tomcat\\resource\\englishPCFG.ser");
        }

        public static StanfordCoreNLP getPipeline() {
            if (stanfordCoreNLP==null) {
                stanfordCoreNLP = new StanfordCoreNLP(properties);
            }
            return stanfordCoreNLP;
        }
    }

