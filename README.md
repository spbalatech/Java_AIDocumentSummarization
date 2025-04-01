# Document Summarizer

A Spring Boot application that generates comprehensive summaries of PDF and text documents using Anthropic's Claude 2.1 AI model.

## Features

- Upload PDF or TXT files
- Extract text content automatically
- Generate structured summaries using Claude AI
- Display word count and file information
- Clean and responsive web interface

## Prerequisites

- Java 17 or higher
- Maven
- Anthropic API key (get it from [Anthropic Console](https://console.anthropic.com/settings/keys))

## Project Structure

```
document-summarizer/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── summarizer/
│   │   │           ├── controller/
│   │   │           │   └── DocumentController.java   # Handles web requests
│   │   │           ├── model/
│   │   │           │   └── SummaryRequest.java       # Data model
│   │   │           ├── service/
│   │   │           │   └── DocumentService.java      # Business logic & Claude API integration
│   │   │           └── DocumentSummarizerApplication.java
│   │   ├── resources/
│   │   │   ├── templates/
│   │   │   │   └── index.html                       # Web interface
│   │   │   └── application.properties               # App configuration
│   └── test/                                        # Test files
├── target/                                          # Compiled files
├── pom.xml                                         # Dependencies & build config
└── README.md                                       # Project documentation
```

## Setup

1. Clone the repository:
```bash
git clone [repository-url]
cd document-summarizer
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application:
- Open your browser and navigate to `http://localhost:8090`
- Enter your Claude API key
- Upload a document and get your summary

## Configuration

The application uses the following default configuration:
- Server port: 8090
- Maximum file size: 10MB
- Supported file types: PDF, TXT

You can modify these settings in `src/main/resources/application.properties`.

## Component Overview

### Controllers
- `DocumentController`: Handles file uploads and summary generation requests

### Services
- `DocumentService`: 
  - Extracts text from documents
  - Integrates with Claude API
  - Manages summary generation

### Models
- `SummaryRequest`: Data structure for summary requests

### Templates
- `index.html`: Responsive web interface with Bootstrap styling

## Technical Details

- **Framework**: Spring Boot 3.2.3
- **Template Engine**: Thymeleaf
- **PDF Processing**: Apache PDFBox
- **AI Model**: Claude 2.1 (via Anthropic API)
- **Build Tool**: Maven

## API Integration

The application integrates with Anthropic's Claude API to generate summaries. The summarization process:
1. Extracts text from uploaded documents
2. Sends the text to Claude API with structured prompting
3. Receives and formats the AI-generated summary
4. Displays the result with proper formatting

## Error Handling

- Validates file types and sizes
- Provides clear error messages for API issues
- Includes debug logging for troubleshooting
- Handles various edge cases gracefully

## Security

- API keys are transmitted securely
- File uploads are validated
- Error messages are sanitized
- No API keys or sensitive data are stored

## Development

To run in development mode with debug logging:
1. Update application.properties logging levels
2. Run with `mvn spring-boot:run`
3. Check console for detailed logs

## License

This project is licensed under the MIT License - see the LICENSE file for details.
