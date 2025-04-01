package com.summarizer.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    public String extractText(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        
        String fileName = file.getOriginalFilename();
        logger.info("Processing file: {}", fileName);
        
        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            return extractPdfText(file.getInputStream());
        } else {
            return new String(file.getBytes());
        }
    }

    private String extractPdfText(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.debug("Extracted {} characters from PDF", text.length());
            return text;
        } catch (IOException e) {
            logger.error("Failed to extract text from PDF", e);
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }
    
    // Updated for Claude Messages API
    private record AnthropicRequest(
        @JsonProperty("model") String model,
        @JsonProperty("max_tokens") Integer maxTokens,
        @JsonProperty("messages") Message[] messages,
        @JsonProperty("system") String system
    ) {}
    
    private record Message(
        @JsonProperty("role") String role,
        @JsonProperty("content") String content
    ) {}
    
    private record AnthropicResponse(
        @JsonProperty("content") Content[] content,
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("role") String role,
        @JsonProperty("model") String model,
        @JsonProperty("stop_reason") String stopReason
    ) {}
    
    private record Content(
        @JsonProperty("type") String type,
        @JsonProperty("text") String text
    ) {}

    public String generateSummary(String text, String apiKey) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            logger.info("Generating summary for text of length: {}", text.length());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", ANTHROPIC_VERSION);
            headers.set("x-api-key", apiKey);

            String systemPrompt = "You are a helpful AI assistant that creates comprehensive document summaries.";
            String userPrompt = String.format("""
                Please provide a comprehensive summary of the following document.
                Focus on key points, main arguments, and important details:
                
                %s
                
                Please structure the summary with:
                1. Main Points
                2. Key Findings
                3. Important Details
                """, text);

            // Create messages array with user message
            Message[] messages = new Message[] {
                new Message("user", userPrompt)
            };

            // Updated request for messages API
            AnthropicRequest request = new AnthropicRequest(
                "claude-3-sonnet-20240229", // Using newer model
                2048,
                messages,
                systemPrompt
            );

            HttpEntity<AnthropicRequest> entity = new HttpEntity<>(request, headers);
            logger.debug("Sending request to Anthropic API");
            
            try {
                AnthropicResponse response = restTemplate.postForObject(ANTHROPIC_API_URL, entity, AnthropicResponse.class);
                logger.info("Successfully received response from Anthropic API");
                
                if (response == null || response.content() == null || response.content().length == 0) {
                    throw new RuntimeException("Empty response received from Anthropic API");
                }
                
                // Extract text from the first content block
                return response.content()[0].text();
            } catch (RestClientException e) {
                logger.error("Error calling Anthropic API: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to call Anthropic API: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error generating summary", e);
            throw new RuntimeException("Error generating summary: " + e.getMessage(), e);
        }
    }
}