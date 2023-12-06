package com.tdxir.myapp.ChatGpt;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChatgptApplicationConfig {

    @Bean
    public RestTemplate restTemplateChatgpt(RestTemplateBuilder builder) {
        return builder.build();
    }
}
