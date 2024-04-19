package com.tdxir.myapp.service;

import com.tdxir.myapp.model.Mahak;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
//@RequiredArgsConstructor
public class UpdateShopSiteService {

    private    WkhPostMetaRepository postMetaRepository;

    private   WkhPostsRepository  postsRepository;

    public UpdateShopSiteService(WkhPostMetaRepository postMetaRepository, WkhPostsRepository postsRepository) {
        this.postMetaRepository = postMetaRepository;
        this.postsRepository = postsRepository;
    }


    public boolean update(List<Mahak> mahakList) throws IOException {

     int numbeforeerror=0;

            File fileNotExist = new File("F:\\personal\\magaze\\mahak-notexist-in-site.txt");//ner_training.tok");
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fileNotExist));

            if (!fileNotExist.exists())
                fileNotExist.createNewFile();//.createNewFile();

int i=0;
        long pid=0;
     for (Mahak mahak : mahakList)
     {    ++i;
         if(mahak!=null) {

             try {
                 pid = postMetaRepository.post_id(String.valueOf(mahak.getCode()));

                 String stock = String.valueOf(10000);//mahak.getStock());
                 String price = String.valueOf(mahak.getPrice());

                 postsRepository.updateName(mahak.getName(),pid);
                 postMetaRepository.updateStock(stock, String.valueOf(pid));
                 postMetaRepository.updatePrice(price, String.valueOf(pid));
                 postMetaRepository.updateRegularPrice(price, String.valueOf(pid));
                 postMetaRepository.updateSalePrice(price, String.valueOf(pid));
                 postMetaRepository.updateWholeSalePrice(price, String.valueOf(pid));
                 postMetaRepository.updateStockStatus("instock", String.valueOf(pid));
                 postMetaRepository.updateManageStock("yes",String.valueOf(pid));
                 ++numbeforeerror;
                 pid=0;
                 //System.out.println("hohoho hoo hoo  "+String.valueOf(++numbeforeerror));

             } catch (RuntimeException e) {
                 System.out.println("Number doesn't exist                   " + String.valueOf(mahak.getCode()));
                 //throw e;
                 // e.printStackTrace();

                     postsRepository.insertProduct(mahak.getName());
                     postMetaRepository.insertSku(postsRepository.lastId(), String.valueOf(mahak.getCode()));
                     postMetaRepository.insertStock(postsRepository.lastId(), String.valueOf(mahak.getStock()));
                     postMetaRepository.insertPrice(postsRepository.lastId(), String.valueOf(mahak.getPrice()));
                     postMetaRepository.insertRegularPrice(postsRepository.lastId(), String.valueOf(mahak.getPrice()));
                     buffer.write(String.valueOf(mahak.getCode()) + "\n");//mahakList.get(numbeforeerror).getCode()) + "\n");
                 }

         }
     }

        buffer.close();
        System.out.println("Number updated   ========  "+String.valueOf(numbeforeerror));

        return true;
    }
}
