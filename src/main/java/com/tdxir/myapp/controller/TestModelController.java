package com.tdxir.myapp.controller;

import com.tdxir.myapp.nlp.training.MakeNer;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestModelController {
    MakeNer makeNer=new MakeNer();
    @PostMapping("/api/test")
    public void test(CRFClassifier model) {

        model=makeNer.getModel("F:\\opt\\tomcat\\resource\\ner-model.ser.gz");
       // String[] tests = new String[]{"apple watch", "samsung mobile phones", " lcd 52 inch tv"};
         String[] tests = new String[]{"فرش قرمز 1500 تومان است","سال 1389 چه سالی بود؟","جیمی ولز نامبیا دسامبر 2001", "من پنکک سفید نمیخوام شانه قرمز میخوام قیمتش چنده؟","برس زرد 1000 تومان است","1000","من"};
        for (String item : tests) {
            makeNer.doTagging(model, item);
        }
    }
}
