package com.tdxir.myapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(value ={ "handler","hibernateLazyInitializer","FieldHandler"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ResponseFiles {
    private Path file_name;

    private InputStream file_content;
}
