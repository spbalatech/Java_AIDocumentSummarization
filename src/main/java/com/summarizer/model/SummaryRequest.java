package com.summarizer.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SummaryRequest {
    private MultipartFile file;
    private String apiKey;
}
