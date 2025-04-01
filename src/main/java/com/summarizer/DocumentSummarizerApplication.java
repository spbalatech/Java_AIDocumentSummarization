package com.summarizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DocumentSummarizerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentSummarizerApplication.class, args);
    }
}
