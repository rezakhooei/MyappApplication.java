package com.tdxir.myapp.service;

import com.tdxir.myapp.auth.AuthenticationRequest;
import com.tdxir.myapp.auth.AuthenticationResponse;
import com.tdxir.myapp.auth.RegisterRequest;
import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.tdxir.myapp.model.Role.ADMIN;
import static com.tdxir.myapp.model.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService1 {



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
            if (user.getRole() == ADMIN) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("4")
                        .paramTime(("300"))
                        .build();*/

            } else if (user.getRole() == USER) {

                Panels panel1=new Panels();

                ArrayList<RadioButton> rdTest=new ArrayList<>();
                rdTest.add(new RadioButton(1,"rdBtn1",true));
                rdTest.add(new RadioButton(2,"rdBtn2",false));
                rdTest.add(new RadioButton(3,"rdBtn3",false));
                Panels panels =new Panels();
                panels.setPanels(rdTest);
              //  panel1.setRadioButtons(rdTest);
                //panel1.setName("test1");

               //panel1[1].setPanelRD(rdTest);

                //panel.add(panel1);

                ArrayList<RadioButton> radioButton1=new ArrayList<>();
                radioButton1.add(new RadioButton(1,"rdBtn11",true));
                radioButton1.add(new RadioButton(2,"rdBtn22",false));
                radioButton1.add(new RadioButton(3,"rdBtn333333",false));

                //ArrayList<List<RadioButton>>  radioBottonList=new ArrayList<>();
               // radioBottonList.add(radioButton1);
                //panel1.setName("panel2");
             //   panel1.setPanelRD(radioButton1);

                //panel.add(panel1);
                ArrayList<CheckBox> checkBoxes=new ArrayList<>();
                CheckBox checkBox=new CheckBox();
                checkBox.setId(1);
                checkBox.setName("chkBox1");
                checkBox.setDefaulfValue(true);
                checkBoxes.add(checkBox);





                return  AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("60"))
                        .checkBoxes(checkBoxes)
                      //  .radioButtonsList(radioBottonList)
                        .radioButtonList(panels)
                        .build();
            }

        }
        if (user.getUserKind() ==UserKind.SPORT )            // User Sport
        {
            if (user.getRole() == ADMIN) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();*/
            } else if (user.getRole() == USER) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();*/
            }

        }
        if (user.getUserKind() ==UserKind.PERSON)            //Person
        {
            if (user.getRole() == ADMIN) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("30"))
                        .build();*/
            } else if (user.getRole() == USER) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("1")
                        .paramTime(("90"))
                        .build();*/
            }
        }
    return null;

    }



}
