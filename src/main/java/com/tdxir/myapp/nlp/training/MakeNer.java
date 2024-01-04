package com.tdxir.myapp.nlp.training;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;
import java.util.regex.*;
import java.io.File;
import java.util.Properties;

public class MakeNer {
  /*  Properties props=new Properties();
    public static boolean makener(File filetsv){

        props..setProperty();
        StanfordCoreNLP stanfordCoreNLP=new StanfordCoreNLP(props);
    }*/

    public void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) {
        Properties props = StringUtils.propFileToProperties(prop);
        props.setProperty("serializeTo", modelOutPath);
        //if input use that, else use from properties file.
      /*  if (trainingFilepath != null) {
            props.setProperty("trainFile", trainingFilepath);
        }*/
        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();
        crf.serializeClassifier(modelOutPath);
    }
    public CRFClassifier getModel(String modelPath) {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }
    public String doTagging(CRFClassifier model, String input) {
        input = input.trim();



        String sentence = "قیمت قابلمه چند دلار است";
        Pattern pattern = Pattern.compile("قیمت قابلمه (\\d+) دلار");
        Matcher matcher = pattern.matcher(sentence);

        if (matcher.find()) {
            String price = matcher.group(1);
            System.out.println("قیمت قابلمه " + price + " دلار است");
        }



       // System.out.println(input + "=>"  +  model.classifyToString(input));
        return /*input + "=>"  + */ model.classifyToString(input);
    }
}
