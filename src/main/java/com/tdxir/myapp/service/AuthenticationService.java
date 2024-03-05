package com.tdxir.myapp.service;

import com.tdxir.myapp.auth.AuthenticationRequest;
import com.tdxir.myapp.auth.AuthenticationResponse;
import com.tdxir.myapp.auth.RegisterRequest;
import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public JSONObject authenticate(AuthenticationRequest request) {
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
                JSONObject jsonObjectMain = new JSONObject();
                JSONObject jsonObjectPanel = new JSONObject();
                jsonObjectMain.put("token",jwtToken);
                jsonObjectMain.put("paramCount",2);
                jsonObjectMain.put("paramTime",60);


                JSONObject jsonObject = new JSONObject();

                JSONArray array1 = new JSONArray();

                    for(int i=1;i<=3;++i) {
                        jsonObject.put("id", i);//String.valueOf(i));
                        jsonObject.put("name", "Rd"+String.valueOf(i));
                        if(i==1)
                        jsonObject.put("isSelct", true);
                        else jsonObject.put("isSelct", false);

                        array1.add(new JSONObject(jsonObject));
                    }

                jsonObjectPanel.put("panel1",array1);

                    jsonObject.clear();
                JSONArray array2= new JSONArray();
                    //array.clear();
                for(int i=1;i<=3;++i) {
                    jsonObject.put("id", i);//String.valueOf(i));
                    jsonObject.put("name", "Rd"+String.valueOf(i)+String.valueOf(i));
                    if(i==1)
                        jsonObject.put("isSelct", true);
                    else jsonObject.put("isSelct", false);

                    array2.add(new JSONObject(jsonObject));
                }

                jsonObjectPanel.put("panel2",array2);
                jsonObjectMain.put("radioButtonsList", jsonObjectPanel);
               /* Panel panel=new Panel();
                ArrayList<RadioButton> rdTest=new ArrayList<>();
                rdTest.add(new RadioButton(1,"rdBtn1",true));
                rdTest.add(new RadioButton(2,"rdBtn2",false));
                rdTest.add(new RadioButton(3,"rdBtn3",false));
                List<RadioButton> radioButton1=new ArrayList<>();
                radioButton1.add(new RadioButton(1,"rdBtn1",true));
                radioButton1.add(new RadioButton(2,"rdBtn2",false));
                radioButton1.add(new RadioButton(3,"rdBtn3",false));

                ArrayList<List<RadioButton>>  radioBottonList=new ArrayList<>();
                radioBottonList.add(radioButton1);
                panel.setId(1);
                panel.setPanel(rdTest);

                ArrayList<RadioButton> radioButton2=new ArrayList<>();
                radioButton2.add(new RadioButton(1,"rdBtn11",true));
                radioButton2.add(new RadioButton(2,"rdBtn22",false));
              //  radioButton2.add(new RadioButton(3,"rdBtn33",false));
                panel
                panel.setId(2);
                panel.setPanel(radioButton2);


                */
                ArrayList<CheckBox> checkBoxes=new ArrayList<CheckBox>();

                checkBoxes.add(new CheckBox(1,"chkBox1",false));
                checkBoxes.add(new CheckBox(2,"chkBox2",false));
                checkBoxes.add(new CheckBox(3,"chkBox3",false));
                jsonObjectMain.put("checkBoxes",checkBoxes);


                return jsonObjectMain;

                /*AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("2")
                        .paramTime(("60"))
                        .checkBoxes(checkBoxes)
                      //  .radioButtonsList(radioBottonList)
                        .panel(panel)
                        .build();*/
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
