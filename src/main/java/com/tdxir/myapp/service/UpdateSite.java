package com.tdxir.myapp.service;

import com.tdxir.myapp.model.Mahak;
import com.tdxir.myapp.repository.MahakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@RequiredArgsConstructor
public class UpdateSite {
    @Autowired
    private final  MahakRepository mahakRepositories;
    public List<Mahak> update(int code , int stock, long price ){

      return mahakRepositories.findAll();//.get(0);

    }
}
