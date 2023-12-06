package com.tdxir.myapp.service;

import com.tdxir.myapp.model.UsersData;
import com.tdxir.myapp.repository.UsersDataRepositoryTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {
    @Autowired
   // UsersDataRepository usersDataRepository;
    UsersDataRepositoryTemp usersDataRepositoryTemp;

    public List<UsersData> usersdatahistory(String userid, int historycount) {

        // UsersData historydata=  usersDataRepository.findById(id)//.findByFieldIn(Set<String>,userid)
        List<UsersData> historydata = usersDataRepositoryTemp.findByUserid(userid);
        //List<UsersData> findByFieldIn(Set<String> userid);
        //  .orElseThrow(()->new RuntimeException("not found"));
        return historydata;
    }
}
