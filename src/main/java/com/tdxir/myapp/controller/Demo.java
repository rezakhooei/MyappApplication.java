package com.tdxir.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class Demo {
        @GetMapping
        public String sayH()
        {

            return "rrrreeezzzzzzzzzaaaaaaaaa    khooei";
        }
   /* public ResponseEntity<String> sayHello(){


        return ResponseEntity.ok("Hello from secured endpoint");
    }*/
}
