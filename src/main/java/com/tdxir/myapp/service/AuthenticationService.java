package com.tdxir.myapp.service;

import com.tdxir.myapp.MyappApplication;
import com.tdxir.myapp.auth.AuthenticationRequest;
import com.tdxir.myapp.auth.AuthenticationResponse;
import com.tdxir.myapp.auth.RegisterRequest;
import com.tdxir.myapp.model.*;
import com.tdxir.myapp.repository.UserRepository;
import com.tdxir.myapp.repository.WkhPostMetaRepository;
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
import static com.tdxir.myapp.model.Role.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    WkhPostMetaRepository wkhPostMetaRepository;
    public static Integer companyId=0;

    public AuthenticationResponse register(RegisterRequest request) {
       var user= Users.builder()
               .firstname(request.getFirstname())
               .lastname(request.getLastname())
               .mobile(request.getMobile())
               .active(false)
               .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword()))
               .role(USER)
               .userKind(UserKind.SHOP)//Shop kind
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
        List<Company> company=wkhPostMetaRepository.reportCompanies(user.getEmail());
        if(company.size()==0) return null;
        else if(company.size()==0) companyId=company.get(0).getId();


        if (user.getUserKind() == UserKind.SHOP)            //User Shop
        {
            if (user.getRole() == ADMIN) {
                infList=new String[]{"نام کالا@کدکالا/شماره فاکتور","کد کالا","تعداد کالا","قیمت(ریال)"};
                checkBoxesList=new String[]{};//"chk1","chk2","chk3"};
                panels=new String[][]{{"نحوه ارسال","نحوه دریافت","دستور"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"بررسی","ذخیره","خرید","فاکتور"}};

                return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);


            } else if (user.getRole() == USER) {
                infList=new String[]{"نام کالا","کد کالا","موجودی","قیمت(ریال)"};
                checkBoxesList=new String[]{};//{"chk1","chk2","chk3"};
                panels=new String[][]{{"نحوه ارسال","نحوه دریافت","دستور"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"بررسی"}};

                return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);

            }
            else if(user.getRole() == ACCOUNTING){
                infList=new String[]{"شماره فاکتور@شناسه فروشنده توضیحات/آی دی چک","شماره فاکتور/نام سند","تعداد کالا@تاریخ","مبلغ(ریال)@چک"};
                checkBoxesList=new String[]{"chk1","chk2","chk3"};
                panels=new String[][]{{"نحوه ارسال","نحوه دریافت","دستور","کی","بیزینس"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"بدهکار","طلبکار","غیر مالی","گزارش کالا","گزارش فاکتور","گزارش سند"},{"شدیم ما","شدند ایشان"},{null,null,null,null,null,null,null,null,null,null}};


                for(int i=0;i<=company.size()-1;++i)
                panels[5][i]=company.get(i).getBranch();



                return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);

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
            if (user.getRole() ==ACCOUNTING) {

                infList=new String[]{"نام کاربری یا ایمیل","نام بیزینس","",""};
                checkBoxesList=new String[]{};//"chk1","chk2","chk3"};
                panels=new String[][]{{"نحوه ارسال","نحوه دریافت","دستور"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"صدا","تصویر","صداوتصویر","هیچکدام"},{"بررسی","ذخیره","خرید","فاکتور"}};

                return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);
            } else if (user.getRole() == USER) {/*
                return AuthenticationResponse.builder()
                        .token(jwtToken)
                        .paramCount("1")
                        .paramTime(("90"))
                        .build();*/
            }
        }
        else if (user.getUserKind() ==UserKind.TEST )
        {

            infList=new String[]{"اطلاعات 1","اطلاعات 2","اطلاعات 3","اطلاعات 4"};
            checkBoxesList=new String[]{"chk1","chk2","chk3"};
            panels=new String[][]{{"نحوه ارسال","پاسخ دریافتی","پنل 3","پنل 4"},{"صدا","عکس","صداوعکس","هیچکدام"},{"صدا","عکس","صداوعکس","هیچکدام"},{"رادیو باتن 1","رادیو باتن 2","رادیو باتن 3","رادیو باتن 4"},{"رادیو باتن 1","رادیو باتن 2"}};

            return  sendAuthConfig(infList,checkBoxesList,panels,jwtToken);

        }
    return null;

    }

JSONObject sendAuthConfig(String infList[],String[] checkBoxesList,String[][] panels,String jwtToken){

    ArrayList<JSONObject> jsonObjectRdList = new ArrayList<JSONObject>();
    JSONObject jsonObjectMain = new JSONObject();

    jsonObjectMain.put("token",jwtToken);
    jsonObjectMain.put("paramCount",4);
    jsonObjectMain.put("paramTime",60);

for(int panelNum=1;panelNum<=panels[0].length;++panelNum) {
    ArrayList<JSONObject> jsonObjectItems = new ArrayList<JSONObject>();
    JSONObject jsonObjectRd = new JSONObject();
    JSONObject jsonObjectPanel = new JSONObject();
    JSONArray array1 = new JSONArray();

    for (int rdNum = 1; rdNum <= panels[panelNum].length; ++rdNum) {
        if (panels[panelNum][rdNum - 1] != null)
        {
            jsonObjectRd.put("id", rdNum);//String.valueOf(i));
        jsonObjectRd.put("name", panels[panelNum][rdNum - 1]);

        if ((rdNum == 4 && (panelNum == 1 || panelNum == 2)) || (rdNum == 1 && panelNum == 3) || (rdNum == 1 && panelNum == 4) || (rdNum == 1 && panelNum == 5))
            jsonObjectRd.put("isSelct", true);
        else jsonObjectRd.put("isSelct", false);

        jsonObjectItems.add(new JSONObject(jsonObjectRd));
    }
}

    jsonObjectPanel.put("name", panels[0][panelNum - 1]);
    switch (panelNum) {
        case 1:jsonObjectPanel.put("latin_name","panel1");
            break;
        case 2:jsonObjectPanel.put("latin_name","panel2");
            break;
        case 3:jsonObjectPanel.put("latin_name","panel3");
            break;
        case 4:jsonObjectPanel.put("latin_name","panel4");
            break;
        case 5:jsonObjectPanel.put("latin_name","panel5");
            break;
    }
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
    infs.add(new Inf(3,"inf4",infList[3]));

    jsonObjectMain.put("infs",infs);


    return jsonObjectMain;
}

}
