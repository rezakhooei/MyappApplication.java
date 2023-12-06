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
public class RecordService {

    private final UsersDataRepository repository;
    @Autowired
    private UsersDataRepository usersDataRepository;
    public RecordResponse save(RecordRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      //  if (!(authentication instanceof AnonymousAuthenticationToken)) {
            //String currentUserName = authentication.getName();
        Date date = new Date();

        // display time and date using toString()
        System.out.println(date.toString());
        var userData=UsersData.builder()

                .date(date)
                .userid(request.getUserid())
                .inf1(request.getInf1())
                .inf2(request.getInf2())
                .inf3(request.getInf3())
                .inf4(request.getInf4())
                .build();
        userData.setUserid(authentication.getName());
         repository.save(userData);


        return RecordResponse.builder()
                .str("Ok")
                .build();
    }
}
