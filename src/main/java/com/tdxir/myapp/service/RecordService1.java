package com.tdxir.myapp.service;

import com.tdxir.myapp.auth.RecordRequest;
import com.tdxir.myapp.controller.RecordResponse;
import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.repository.UsersDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RecordService1 {

    private final UsersDataRepository repository;
    @Autowired
    private UsersDataRepository usersDataRepository;
    public String save(String filename,String inf1,String inf2,String inf3,String inf4){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
            //String currentUserName = authentication.getName();
        Date date = new Date();

        // display time and date using toString()
        System.out.println(date.toString());
        var userData=UsersData.builder()

                .date(date)
                //.userid(request.getUserid())
                .inf1(inf1)
                .inf2(inf2)
                .inf3(inf3)
                .inf4(inf4)
                .build();
       // userData.setUserid();
        userData.setVoiceFileName(filename);
        userData.setUserid(authentication.getName());
         repository.save(userData);


        return "ok";//RecordResponse.builder()            .str("Ok")                .build();
    }
}
