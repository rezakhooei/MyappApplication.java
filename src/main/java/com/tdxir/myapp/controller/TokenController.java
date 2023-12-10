package com.tdxir.myapp.controller;

import com.tdxir.myapp.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping

public class TokenController {

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    UserDetails userDetails;
    @Autowired
    private  JwtService jwtService;
    @PostMapping("/api/checkToken")
    Boolean token(@RequestParam String token)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();



        return !jwtService.isTokenExPired(token);
    }
}
