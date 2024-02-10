package com.tdxir.myapp.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import com.tdxir.myapp.MyappApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GoogleSpeech {

    @Value("${app.file.resource-dir-linux}")
    private String pathLinux;
    @Value("${app.file.resource-dir-win}")
    private String pathWin;
        public String transcribeSpeech(File audioFilePath) throws IOException
        {
            //Path path = Paths.get(audioFilePath);
            byte[] audioBytes = Files.readAllBytes(audioFilePath.toPath());
            ByteString audioData = ByteString.copyFrom(audioBytes);

            // Set up the credentials
            GoogleCredentials credentials;
            if(MyappApplication.WinLinux==1) {
                 credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(pathWin + "newsroom-413413-480f64d71e05.json")));
            }
            else {
                credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(pathLinux + "newsroom-413413-480f64d71e05.json")));
            }

            // Set up the Speech-to-Text client with the credentials
            try (SpeechClient speechClient = SpeechClient.create(SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build()))
            {

                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("fa-IR")
                        .build();

                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioData)
                        .build();

                RecognizeRequest request = RecognizeRequest.newBuilder()
                        .setConfig(config)
                        .setAudio(audio)
                        .build();

                RecognizeResponse response = speechClient.recognize(request);
                List<SpeechRecognitionResult> results = response.getResultsList();

                StringBuilder transcript = new StringBuilder();
                for (SpeechRecognitionResult result : results)
                {
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    transcript.append(alternative.getTranscript());
                }
                System.out.println(transcript.toString());
                return transcript.toString();
            }

        }
    }

