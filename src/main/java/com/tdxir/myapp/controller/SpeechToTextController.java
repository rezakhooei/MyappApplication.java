package com.tdxir.myapp.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class SpeechToTextController {

    @PostMapping("/speech-to-text")
    public String convertSpeechToText(@RequestBody byte[] audioData) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream("F:\\opt\\tomcat\\resource\\newsroom-google-key-14021116.json")); // Replace with your key file path

        SpeechClient speechClient = SpeechClient.create();//credentials);//credentials);

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();

        RecognizeRequest request = RecognizeRequest.newBuilder()
                .setConfig(config)
                .setAudio(com.google.cloud.speech.v1.RecognitionAudio.newBuilder()
                        .setContent(ByteString.copyFrom(audioData))
                        .build())
                .build();

        RecognizeResponse response = speechClient.recognize(request);
        List<SpeechRecognitionResult> results = response.getResultsList();

        StringBuilder transcriptBuilder = new StringBuilder();
        for (SpeechRecognitionResult result : results) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            String transcript = alternative.getTranscript();
            transcriptBuilder.append(transcript);
        }

        speechClient.close();

        return transcriptBuilder.toString();
    }
}