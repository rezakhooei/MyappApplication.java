package com.tdxir.myapp.controller;

import ai.picovoice.leopard.*;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@RestController
@RequestMapping("/api/v1/auth/convert")
/**
 * A snippet for Google Natural Language API showing how to convert houman speech from an audio file
 * into a text form.
 */
public class RecognizeSpeech {

    @PostMapping
    public String convert() throws Exception {


//secret key for picovoice.ai: vP7kVQA9VuP+4Ldc+tVlahUJgCTxDWgEwHkfTKTA6xAo10pFhYRtkQ==


        final String accessKey = "vP7kVQA9VuP+4Ldc+tVlahUJgCTxDWgEwHkfTKTA6xAo10pFhYRtkQ==";// "${ACCESS_KEY}"; // AccessKey provided by Picovoice Console (https://console.picovoice.ai/)


        String transcription = null;
        try {
            Leopard leopard = new Leopard.Builder()
                    .setAccessKey(accessKey).build();
            LeopardTranscript result = leopard.processFile("F:\\projects\\java\\14020730\\newsroom\\reza.mp3");//"${AUDIO_PATH}");
            transcription = result.getTranscriptString();
            leopard.delete();
        } catch (LeopardException ex) {
        }


        System.out.println(transcription);

        // Instantiates a client

        try {
            SpeechClient speech = SpeechClient.create();

            // The path to the audio file to transcribe
            String fileName = "/opt/tomcat/uploads/file1.mp3"; // for example "./resources/audio.raw";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setSampleRateHertz(16000)
                            .setLanguageCode("en-US")
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                for (SpeechRecognitionAlternative alternative : alternatives) {
                    System.out.printf("Transcription: %s%n", alternative.getTranscript());
                }
            }
            speech.close();
        } catch (IOException e) {
            System.out.println("Divide by 0: " + e);
        }
        return transcription;
    }



}
