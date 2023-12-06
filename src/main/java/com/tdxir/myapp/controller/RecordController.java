package com.tdxir.myapp.controller;

import com.tdxir.myapp.auth.RecordRequest;
import com.tdxir.myapp.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recdata")
@RequiredArgsConstructor
public class RecordController {


    private final RecordService usersDataService;
   @PostMapping

   public  ResponseEntity<RecordResponse> record(@RequestBody RecordRequest request){

      return  ResponseEntity.ok(usersDataService.save(request));

    }
}
