package com.summarizer.controller;

import com.summarizer.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/summarize")
    public String summarize(@RequestParam("file") MultipartFile file,
                          @RequestParam("apiKey") String apiKey,
                          Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                return "index";
            }

            // Extract text from the document
            String text = documentService.extractText(file);
            if (text.trim().length() < 100) {
                model.addAttribute("error", "The document appears to be too short or empty");
                return "index";
            }

            // Generate summary
            String summary = documentService.generateSummary(text, apiKey);
            
            // Add attributes to the model
            model.addAttribute("summary", summary);
            model.addAttribute("wordCount", text.split("\\s+").length);
            model.addAttribute("fileName", file.getOriginalFilename());
            
            return "index";

        } catch (Exception e) {
            model.addAttribute("error", "Error processing document: " + e.getMessage());
            return "index";
        }
    }
}
