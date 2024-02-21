package com.tdxir.myapp.service;

import com.tdxir.myapp.MyappApplication;
import com.tdxir.myapp.model.UserKind;
import com.tdxir.myapp.nlp.SentenceRecognizer;
import com.tdxir.myapp.nlp.training.MakeNer;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class ProccessMessage {
    UserKind userKind;
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    private WkhPostMetaRepository wkhPostMetaRepository;
    @Value("${app.file.resource-dir-win}")
    private String pathWin;
    @Value("${app.file.resource-dir-linux}")
    private String pathLinux;
    /*public ProccessMessage(UserKind userKind){
        this.userKind=userKind;
    }*/
    public List<String> proccess(String message,UserKind userKind)
    {
        switch (userKind)
        {
            case SHOP :System.out.println("I AM SHOPPER");
                return  proccessMessageShop(message);
            case SPORT:System.out.println("I AM SPORTER");
                return  proccessMessageSport(message);
            case PERSON:System.out.println("I am Person");
               // return  proccessMessageShop(message);
                return  proccessMessagePerson(message);
        }
        ArrayList<String> message1=new ArrayList<String>();
        System.out.println("Nothing to select kind");
        message1.add(message);
        return message1;//

    }
    public List<String> proccessMessageShop(String message) {

        MakeNer makeNer = new MakeNer();
        CRFClassifier model;
        if (MyappApplication.WinLinux == 1) {

            model = makeNer.getModel(pathWin + "ner-model.ser.gz");
        } else {

            model = makeNer.getModel(pathLinux + "ner-model.ser.gz");
        }

        String messageTagged = makeNer.doTagging(model, message);//"قیمت/Price مو/NameShop چنده/O ?/Qsign";
        SentenceRecognizer sentenceRecognizer = new SentenceRecognizer();

        ArrayList<String> temp33= sentenceRecognizer.recognizeNer(message);////tests[0]);
        //List<String> temp4= sentenceRecognizer.recognizePos(message);
        ArrayList<String> temp3 = new ArrayList<>();
        temp3.clear();
        String[] substrings = messageTagged.split(" ");
        for (String s : substrings) {
            String[] substrings1 = s.split("/");
            temp3.add(substrings1[0]);
            temp3.add(substrings1[1]);

            System.out.println(s);
        }
        // قطعه زیر برای چک makener.doTaging  و recognizener بود که موقتا حذف نشد
   /*    for(int i=0;i<temp3.size();++i)
        if (String.valueOf(temp3.get(i))!=String.valueOf(temp33.get(i))) {
            ArrayList<String> message1=new ArrayList<String>();
            System.out.println("for test ner and dotag");
            message1.add(String.valueOf(i) +temp3.get(i)+","+temp33.get(i)+ "doTagging with ner difference error");
            return message1;//temp3;

        }
*/

        String sentence = "";
        for (int i = 1; i <= temp3.size() - 1; ++i) {
            sentence += temp3.get(i++);
            sentence += " ";
        }
        Pattern pattern1 = Pattern.compile("Qsign");

        Matcher matcher1 = pattern1.matcher(sentence);
//  if sentence is question
        if (matcher1.find()) {

                Pattern pattern2 = Pattern.compile("Code Price");
                Pattern pattern3 = Pattern.compile("Price Code");
                Matcher matcher2 = pattern2.matcher(sentence);
                Matcher matcher3 = pattern3.matcher(sentence);
                if (matcher2.find() || matcher3.find()) {  //     if sentence is question about price by name of product

                    for (int i = 0; i <= temp3.size() - 1; ++i) {
                        String strTemp = new String(temp3.get(++i));
                        if (strTemp.equals("Code")) {
                            strTemp = temp3.get(i - 1);

                         //   List<String> postIds = wkhPostsRepository.PostIdName("%" + strTemp + "%");
                            List<String> postIds = wkhPostMetaRepository.PostIdCode(strTemp);
                            if (postIds.size() != 0) {
                                List<String> pricelist = new ArrayList<>();
                                for (String post_id : postIds)
                                {
                                    pricelist.add(post_id +"   "+ "ریال");
                                  // for name was  pricelist.add(String.valueOf(post_id.substring(0, post_id.indexOf(","))) + "-" + String.valueOf(wkhPostMetaRepository.price(String.valueOf(post_id.substring(post_id.indexOf(",") + 1)))) + "ریال");
                                }
                                return pricelist;
                            }
                        }
                    }
                }

        }
        return null;
    }
    public List<String> proccessMessageSport(String message)
                        {

                            MakeNer makeNer = new MakeNer();
                            CRFClassifier model;
                            if(MyappApplication.WinLinux==1) {

                                model = makeNer.getModel(pathWin+"ner-model.ser.gz");
                            }
                            else {

                                model = makeNer.getModel(pathLinux+"ner-model.ser.gz");
                            }

                            String messageTagged=makeNer.doTagging(model, message);//"قیمت/Price مو/NameShop چنده/O ?/Qsign";
                            SentenceRecognizer sentenceRecognizer = new SentenceRecognizer();

                            // ArrayList<String> temp33= sentenceRecognizer.recognizeNer(message);////tests[0]);
                            //List<String> temp4= sentenceRecognizer.recognizePos(message);
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
                            // قطعه زیر برای چک makener.doTaging  و recognizener بود که موقتا حذف نشد
   /*    for(int i=0;i<temp3.size();++i)
        if (String.valueOf(temp3.get(i))!=String.valueOf(temp33.get(i))) {
            ArrayList<String> message1=new ArrayList<String>();
            System.out.println("for test ner and dotag");
            message1.add(String.valueOf(i) +temp3.get(i)+","+temp33.get(i)+ "doTagging with ner difference error");
            return message1;//temp3;

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

                                Pattern pattern2 = Pattern.compile("Product Price");
                                Pattern pattern3 = Pattern.compile("Price Product");
                                Matcher matcher2 = pattern2.matcher(sentence);
                                Matcher matcher3 = pattern3.matcher(sentence);
                                if (matcher2.find() || matcher3.find())
                                {  //     if sentence is question about price by name of product

                                    for (int i = 0; i <= temp3.size() - 1; ++i)
                                    {
                                        String strTemp = new String(temp3.get(++i));
                                        if (strTemp.equals("NameShop"))
                                        {
                                            strTemp = temp3.get(i - 1);

                                            List<String> postIds = wkhPostsRepository.PostIdName("%" + strTemp + "%");

                                            if (postIds.size() != 0)
                                            {
                                                List<String> pricelist = new ArrayList<>();
                                                for (String post_id : postIds)
                                                {

                                                    pricelist.add(String.valueOf(post_id.substring(0, post_id.indexOf(","))) + "-" + String.valueOf(wkhPostMetaRepository.price(String.valueOf(post_id.substring(post_id.indexOf(",") + 1)))) + "ریال");
                                                }
                                                return pricelist;
                                            }
                                        }
                                    }
                                }

                            }
                            return null;
                        }
    public List<String> proccessMessagePerson(String message)
    {
        ArrayList<String> temp3=new ArrayList<>();
        temp3.clear();
        temp3.add(message);
        return temp3;

                                                /*MakeNer makeNer = new MakeNer();
                                                CRFClassifier model;
                                                if(MyappApplication.WinLinux==1) {

                                                    model = makeNer.getModel(pathWin+"ner-model.ser.gz");
                                                }
                                                else {

                                                    model = makeNer.getModel(pathLinux+"ner-model.ser.gz");
                                                }

                                                String messageTagged=makeNer.doTagging(model, message);//"قیمت/Price مو/NameShop چنده/O ?/Qsign";
                                                SentenceRecognizer sentenceRecognizer = new SentenceRecognizer();

                                                // ArrayList<String> temp33= sentenceRecognizer.recognizeNer(message);////tests[0]);
                                                //List<String> temp4= sentenceRecognizer.recognizePos(message);

                                                String[] substrings = messageTagged.split(" ");
                                                for (String s : substrings)
                                                {
                                                    String[] substrings1 = s.split("/");
                                                    temp3.add(substrings1[0]);
                                                    temp3.add(substrings1[1]);

                                                    System.out.println(s);
                                                }
                                                // قطعه زیر برای چک makener.doTaging  و recognizener بود که موقتا حذف نشد

                                                String sentence = "";
                                                for (int i = 1; i <= temp3.size() - 1; ++i)
                                                {
                                                    sentence += temp3.get(i++);
                                                    sentence += " ";
                                                }
                                                Pattern pattern1 = Pattern.compile("Qsign");

                                                Matcher matcher1 = pattern1.matcher(sentence);
//  if sentence is question
                                                if (matcher1.find()) {

                                                        Pattern pattern2 = Pattern.compile("Product Price");
                                                        Pattern pattern3 = Pattern.compile("Price Product");
                                                        Matcher matcher2 = pattern2.matcher(sentence);
                                                        Matcher matcher3 = pattern3.matcher(sentence);
                                                        if (matcher2.find() || matcher3.find()) {  //     if sentence is question about price by name of product

                                                            for (int i = 0; i <= temp3.size() - 1; ++i) {
                                                                String strTemp = new String(temp3.get(++i));
                                                                if (strTemp.equals("NameShop")) {
                                                                    strTemp = temp3.get(i - 1);

                                                                    List<String> postIds = wkhPostsRepository.PostId("%" + strTemp + "%");

                                                                    if (postIds.size() != 0) {
                                                                        List<String> pricelist = new ArrayList<>();
                                                                        for (String post_id : postIds) {

                                                                            pricelist.add(String.valueOf(post_id.substring(0, post_id.indexOf(","))) + "-" + String.valueOf(wkhPostMetaRepository.price(String.valueOf(post_id.substring(post_id.indexOf(",") + 1)))) + "ریال");
                                                                        }
                                                                        return pricelist;
                                                                    }

                                                                }
                                                            }
                                                        }

                                                }
                                                return null;*/

    }
}

