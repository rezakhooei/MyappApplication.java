package com.tdxir.myapp.controller;

import com.tdxir.myapp.model.Mahak;
import com.tdxir.myapp.model.wkh_postmeta;
import com.tdxir.myapp.model.WkhPosts;
import com.tdxir.myapp.repository.MahakRepository;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
import com.tdxir.myapp.repository.WkhPostsRepository;
import com.tdxir.myapp.service.UpdateShopSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping

public class SiteController {
    @Autowired
    private MahakRepository mahakRepository;
    @Autowired
    private WkhPostsRepository wkhPostsRepository;
    @Autowired
    private WkhPostMetaRepository wkhPostMetaRepository;
    @PostMapping("/api/updatesite")
    public List<Mahak> updatesite() throws IOException {
        UpdateShopSiteService updateShopSiteService=new UpdateShopSiteService(wkhPostMetaRepository,wkhPostsRepository);
        List<Mahak> mahakRepositoryList=mahakRepository.findAll();
        updateShopSiteService.update(mahakRepositoryList);
        List<WkhPosts> wkhPostsList=wkhPostsRepository.findAll();
        List<wkh_postmeta> wkhPostMetaList=wkhPostMetaRepository.findAll();

        return mahakRepositoryList;
    }
}
