package com.tdxir.myapp.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GoogleSpeech {



        public String transcribeSpeech(String audioFilePath) throws IOException
        {
            Path path = Paths.get(audioFilePath);
            byte[] audioBytes = Files.readAllBytes(path);
            ByteString audioData = ByteString.copyFrom(audioBytes);

            // Set up the credentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    Files.newInputStream(Paths.get("F:\\opt\\tomcat\\resource\\newsroom-413413-480f64d71e05.json"))
            );

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

