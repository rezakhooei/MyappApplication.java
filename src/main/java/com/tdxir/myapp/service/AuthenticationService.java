package com.tdxir.myapp.service;

import com.tdxir.myapp.auth.AuthenticationRequest;
import com.tdxir.myapp.auth.AuthenticationResponse;
import com.tdxir.myapp.auth.RegisterRequest;
import com.tdxir.myapp.model.UserKind;
import com.tdxir.myapp.model.Users;
import com.tdxir.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tdxir.myapp.MyappApplication.*;
import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
       var user= Users.builder()
               .firstname(request.getFirstname())
               .lastname(request.getLastname())
               .mobile(request.getMobile())
               .active(false)
               .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword()))
               .role(USER)
               .userKind(UserKind.PERSON)//Shop kind
               .build();
       repository.save(user);
       var jwtToken=jwtService.generateToken(user);
       return AuthenticationResponse.builder().build();
       /*return AuthenticationResponse.builder()
               .token(jwtToken)
               .paramCount("0")
               .paramTime("10")
               .build();*/
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        if (!user.isActive()) return null;
        var jwtToken = jwtService.generateToken(user);

        if (user.getUserKind() == UserKind.SHOP)            //User Shop
        {
            if (user.getRole() == ADMIN) {
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("4")
                        .paramTime(("300"))
                        .build();

            } else if (user.getRole() == USER) {
                List<String[]> radioButtons=new ArrayList<>();


                String[] rdBtn1=new String[]{"rdBtn1","rdBtn2","rdBtn3"};
                String[] rdBtn2=new String[]{"rdBtn11","rdBtn22"};
                radioButtons.add(rdBtn1);
                radioButtons.add(rdBtn2);
                String[] checkBoxes=new String[]{"chkBox1","chkBox2","chkBox3","chkBox4"};
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("60"))
                        .checkBoxes(checkBoxes)
                        .radioButtons(radioButtons)
                        .build();
            }

        }
        if (user.getUserKind() ==UserKind.SPORT )            // User Sport
        {
            if (user.getRole() == ADMIN) {
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();
            } else if (user.getRole() == USER) {
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();
            }

        }
        if (user.getUserKind() ==UserKind.PERSON)            //Person
        {
            if (user.getRole() == ADMIN) {
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();
            } else if (user.getRole() == USER) {
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("1")
                        .paramTime(("90"))
                        .build();
            }
        }
    return null;

    }

}
