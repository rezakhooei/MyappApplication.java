package com.tdxir.myapp.nlp;



//import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import com.tdxir.myapp.MyappApplication;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Pipeline {

    private   static Properties properties;
   // @Value("${app.file.resource-dir-win}")
   // private   static final   String pathProps = "f://opt//tomcat//resource//";
    private  static  String propertiesName="tokenize,ssplit,pos,lemma,ner";//,parse,sentiment";
    private static StanfordCoreNLP stanfordCoreNLP;
    private Pipeline(){}
    static {

        properties=new Properties();
        properties.setProperty("annotators",propertiesName);
        properties.setProperty("ner.useSUTime", "false");
        properties.setProperty("lang","fa");
        //properties.setProperty("pos.model","F:\\opt\\tomcat\\resource\\persian.tagger");//english-left3words-distsim.tagger");//langdetect-183.bin");//"F:\\opt\\tomcat\\resource\\en_ewt_tagger.pt");//
        if(MyappApplication.WinLinux==1) {
            properties.setProperty("ner.model", "F:\\opt\\tomcat\\resource\\ner-model.ser.gz");
        }
        else{
            properties.setProperty("ner.model", "/opt/tomcat/resource/ner-model.ser.gz");
        }
       //@@@@@@@@@@@@@@  austen.prop
       /* String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String catalogConfigPath = pathProps+ "austen.prop";
        try {
            properties.load(new FileInputStream(catalogConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    }

    public static StanfordCoreNLP getPipeline() {


        if (stanfordCoreNLP==null) {
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }
        return stanfordCoreNLP;
    }
}
