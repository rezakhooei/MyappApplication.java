package com.tdxir.myapp.controller;

import com.tdxir.myapp.nlp.training.MakeNer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MakeModelController {
    MakeNer makeNer=new MakeNer();
    @PostMapping("/api/model")
    public void makemodel(){
        makeNer.trainAndWrite("F:\\opt\\tomcat\\resource\\ner-model.ser.gz","F:\\opt\\tomcat\\resource\\props.txt","F:\\opt\\tomcat\\resource\\stanford_train.txt");

    }
}
