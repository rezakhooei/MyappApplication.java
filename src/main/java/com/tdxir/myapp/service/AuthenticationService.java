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
        String[] infList,checkBoxesList;

        String[][] panels;
        if (user.getUserKind() == UserKind.SHOP)            //User Shop
        {
            if (user.getRole() == ADMIN) {



            } else if (user.getRole() == USER) {
                infList=new String[]{"اطلاعات 1","اطلاعات 2","اطلاعات 3","اطلاعات 4"};
                checkBoxesList=new String[]{"chk1","chk2","chk3"};
                panels=new String[][]{{"panel1","panel2"},{"Rd1","Rd2","Rd3","Rd4"},{"Rd11","Rd22","Rd33","RD44"}};

                return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);
               /*ArrayList<JSONObject> jsonObjectRdList = new ArrayList<JSONObject>();
                JSONObject jsonObjectMain = new JSONObject();
                ArrayList<JSONObject> jsonObjectItems = new ArrayList<JSONObject>();
                jsonObjectMain.put("token",jwtToken);
                jsonObjectMain.put("paramCount",2);
                jsonObjectMain.put("paramTime",60);


                JSONObject jsonObjectRd = new JSONObject();
                JSONObject jsonObjectPanel = new JSONObject();
                JSONArray array1 = new JSONArray();

                    for(int i=1;i<=4;++i) {
                        jsonObjectRd.put("id", i);//String.valueOf(i));
                        if(i==1)
                        jsonObjectRd.put("name", "Rd1");
                        if(i==2)
                            jsonObjectRd.put("name", "Rd2");
                        if(i==3)
                            jsonObjectRd.put("name", "Rd3");
                        if(i==4)
                            jsonObjectRd.put("name", "Rd4");

                        if(i==1)
                        jsonObjectRd.put("isSelct", true);
                        else jsonObjectRd.put("isSelct", false);

                        jsonObjectItems.add(new JSONObject(jsonObjectRd));
                    }

                    jsonObjectPanel.put("name","panel1");
                    jsonObjectPanel.put("items",jsonObjectItems);
                    jsonObjectRdList.add(jsonObjectPanel);

                    jsonObjectRd.clear();
                     //jsonObjectItems.clear();

                JSONObject jsonObjectRd1 = new JSONObject();
                JSONObject jsonObjectPanel1 = new JSONObject();
                ArrayList<JSONObject> jsonObjectItems1 = new ArrayList<JSONObject>();

                for(int i=1;i<=4;++i) {
                    jsonObjectRd1.put("id", i);//String.valueOf(i));
                    if(i==1)
                        jsonObjectRd1.put("name", "Rd11");
                    if(i==2)
                        jsonObjectRd1.put("name", "Rd22");
                    if(i==3)
                        jsonObjectRd1.put("name", "Rd33");
                    if(i==4)
                        jsonObjectRd1.put("name", "Rd44");

                    if(i==1)
                        jsonObjectRd1.put("isSelct", true);
                    else jsonObjectRd1.put("isSelct", false);

                    jsonObjectItems1.add(new JSONObject(jsonObjectRd1));
                }


                jsonObjectPanel1.put("name","panel2");
                jsonObjectPanel1.put("items",jsonObjectItems1);
                jsonObjectRdList.add(jsonObjectPanel1);




                jsonObjectMain.put("radioButtonsList", jsonObjectRdList);

                ArrayList<CheckBox> checkBoxes=new ArrayList<CheckBox>();

                checkBoxes.add(new CheckBox(1,"انتخاب 1",false));
                checkBoxes.add(new CheckBox(2,"انتخاب 2",false));
                checkBoxes.add(new CheckBox(3,"انتخاب 3",false));
                jsonObjectMain.put("checkBoxes",checkBoxes);

                ArrayList<Inf> infs=new ArrayList<Inf>();

                infs.add(new Inf(1,"inf1","اطلاعات1"));
                infs.add(new Inf(2,"inf2","اطلاعات2"));
                infs.add(new Inf(3,"inf3","اطلاعات3"));
                infs.add(new Inf(3,"inf3","اطلاعات 4"));
                jsonObjectMain.put("infs",infs);

                return jsonObjectMain;*/

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

JSONObject sendAuthConfig(String infList[],String[] checkBoxesList,String[][] panels,String jwtToken){

    ArrayList<JSONObject> jsonObjectRdList = new ArrayList<JSONObject>();
    JSONObject jsonObjectMain = new JSONObject();
    ArrayList<JSONObject> jsonObjectItems = new ArrayList<JSONObject>();
    jsonObjectMain.put("token",jwtToken);
    jsonObjectMain.put("paramCount",2);
    jsonObjectMain.put("paramTime",60);

for(int panelNum=1;panelNum<=panels[0].length;++panelNum)
{
    JSONObject jsonObjectRd = new JSONObject();
    JSONObject jsonObjectPanel = new JSONObject();
    JSONArray array1 = new JSONArray();

    for (int rdNum = 1; rdNum <= panels[panelNum].length; ++rdNum) {
        jsonObjectRd.put("id", rdNum);//String.valueOf(i));
        jsonObjectRd.put("name", panels[panelNum][rdNum-1]);

        if (rdNum == 1)
            jsonObjectRd.put("isSelct", true);
        else jsonObjectRd.put("isSelct", false);

        jsonObjectItems.add(new JSONObject(jsonObjectRd));
    }

    jsonObjectPanel.put("name", panels[0][panelNum - 1]);
    jsonObjectPanel.put("items", jsonObjectItems);
    jsonObjectRdList.add(jsonObjectPanel);

    jsonObjectRd.clear();
    //jsonObjectItems.clear();
}





    jsonObjectMain.put("radioButtonsList", jsonObjectRdList);

    ArrayList<CheckBox> checkBoxes=new ArrayList<CheckBox>();
    for(int i=1;i<=checkBoxesList.length;++i)
    checkBoxes.add(new CheckBox(i,checkBoxesList[i-1],false));
    jsonObjectMain.put("checkBoxes",checkBoxes);

    ArrayList<Inf> infs=new ArrayList<Inf>();

    infs.add(new Inf(1,"inf1",infList[0]));
    infs.add(new Inf(2,"inf2",infList[1]));
    infs.add(new Inf(3,"inf3",infList[2]));
    infs.add(new Inf(3,"inf3",infList[3]));
    jsonObjectMain.put("infs",infs);


    return jsonObjectMain;
}

}
