package com.tdxir.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
//@ComponentScan(basePackages ={"com.tdxir.dependency.exception"})
public class MyappApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(MyappApplication.class);
	}
	public static final int WinLinux=0;//  0 for linux  1 for windows
/*	public static final int PERSON=2;// for using Personal app
	public static final int SPORT=1;// for using Sporting app
	public static final int SHOP=0;// for using Shopping app*/
	public static void main(String[] args) {
		SpringApplication.run(MyappApplication.class, args);
	}


}
