package org.example.webapplicationofthetranslationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
public class TranslationServiceImpl implements TranslationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Runtime runtime = Runtime.getRuntime();

    @Override
    public String translateText(String ipAddress, String inputText, String sourceLang, String targetLang) {
        try {
            System.out.println("Starting translation...");
            System.out.println("Input text: " + inputText);
            System.out.println("Source language: " + sourceLang);
            System.out.println("Target language: " + targetLang);

            String translatedText = translateEntireText(inputText, sourceLang, targetLang);
            saveTranslationRequest(ipAddress, inputText, translatedText);

            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Translation failed: " + e.getMessage());
        }
    }

    private void checkNodeVersion() throws Exception {
        String[] command = { "node", "--version" };
        Process p = runtime.exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String nodeVersion = reader.lines().collect(Collectors.joining("\n"));
        System.out.println("Node.js version: " + nodeVersion);

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to check Node.js version. Exit code: " + exitCode);
        }
    }

    private String translateEntireText(String text, String sourceLang, String targetLang) throws Exception {
        String[] command = {
                "node",
                "C:\\Java\\IntelIDEA\\WebApplicationOfTheTranslationService\\src\\main\\java\\org\\example\\webapplicationofthetranslationservice\\translate.js",
                sourceLang,
                targetLang,
                text
        };

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String output = reader.lines().collect(Collectors.joining("\n"));
        System.out.println("Node.js script output: " + output);

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Node.js script exited with code " + exitCode + ". Output: " + output);
        }

        int jsonStart = output.indexOf('{');
        int jsonEnd = output.lastIndexOf('}');
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            String jsonStr = output.substring(jsonStart, jsonEnd + 1);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(jsonStr);

            if (result.has("error")) {
                throw new RuntimeException(result.get("error").asText());
            }

            return result.get("text").asText();
        } else {
            throw new RuntimeException("Unable to parse JSON from output: " + output);
        }
    }

    private void saveTranslationRequest(String ipAddress, String inputText, String translatedText) {
        try {
            System.out.println("Saving translation request...");
            jdbcTemplate.update(
                    "INSERT INTO translation_requests (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)",
                    ipAddress, inputText, translatedText, new Timestamp(System.currentTimeMillis())
            );
            System.out.println("Translation request saved.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save translation request: " + e.getMessage());
        }
    }
}
