package com.tdxir.myapp.service;

import com.tdxir.myapp.model.Mahak;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class Maghaze {

    private  final  WkhPostMetaRepository postMetaRepository;
    @Autowired
    public Maghaze(WkhPostMetaRepository postMetaRepository) {
        this.postMetaRepository = postMetaRepository;
    }


    public boolean update(List<Mahak> mahakList) throws IOException {

     int numbeforeerror=0;

            File fileNotExist = new File("F:\\personal\\magaze\\filenotexist.txt");//ner_training.tok");
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fileNotExist));

            if (!fileNotExist.exists())
                fileNotExist.createNewFile();//.createNewFile();

int i=0;
     for (Mahak mahak : mahakList)
     {    ++i;
         if(mahak!=null) {

             try {
                 long pid = postMetaRepository.post_id(String.valueOf(mahak.getCode()));

                 String stock = String.valueOf(mahak.getStock());
                 String price = String.valueOf(mahak.getPrice());
                 postMetaRepository.updateStock(stock, String.valueOf(pid));
                 postMetaRepository.updatePrice(price, String.valueOf(pid));
                 ++numbeforeerror;
                 //System.out.println("hohoho hoo hoo  "+String.valueOf(++numbeforeerror));

             } catch (RuntimeException e) {
                 System.out.println("Number doesn't exist                   " + String.valueOf(mahak.getCode()));
                 //throw e;
                // e.printStackTrace();


                 buffer.write(String.valueOf(mahak.getCode())+"\n");//mahakList.get(numbeforeerror).getCode()) + "\n");
             }
         }
     }

        buffer.close();
        System.out.println("Number updated   ========  "+String.valueOf(numbeforeerror));

        return true;
    }
}
