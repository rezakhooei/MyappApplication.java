package com.tdxir.myapp.auth;
import okhttp3.*;


import org.springframework.stereotype.Service;

@Service

public class OpenApiKeyValidation {

    public  boolean checkApi(String apiKey) {

        boolean isValid = validateApiKey(apiKey);

        if (isValid) {
            System.out.println("API key is valid");
            return true;
            // Your code for using the API goes here
        } else {

            System.out.println("Invalid API key. Please provide a valid API key.");
            return false;
        }
    }

    private static boolean validateApiKey(String apiKey) {
       /*
        DefaultApi client = new DefaultApi();
        client.getApiClient().setApiKey(apiKey);

        try {
            CompletionsRequest request = new CompletionsRequest();
            request.setModel("text-davinci-003");
            request.setPrompt("Hello, world!");
            request.setMaxTokens(5);

            CompletionsResponse response = client.v1Completions(request);
            return response != null;
        } catch (ApiException e) {
            e.printStackTrace();
        }
*/
        return false;
    }


}
