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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TranslationServiceImpl implements TranslationService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

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

            String[] words = inputText.split("\\s+");
            List<Future<String>> futures = new ArrayList<>();

            for (String word : words) {
                futures.add(executorService.submit(() -> translateWord(word, sourceLang, targetLang)));
            }

            StringBuilder result = new StringBuilder();
            for (Future<String> future : futures) {
                try {
                    result.append(future.get()).append(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to translate word: " + e.getMessage());
                }
            }

            String translatedText = result.toString().trim();
            saveTranslationRequest(ipAddress, inputText, translatedText);

            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Translation failed: " + e.getMessage());
        }
    }

    private String translateWord(String word, String sourceLang, String targetLang) {
        try {
            String[] command = {
                    "node",
                    "C:\\Java\\IntelIDEA\\WebApplicationOfTheTranslationService\\src\\main\\java\\org\\example\\webapplicationofthetranslationservice\\translate.js",
                    sourceLang,
                    targetLang,
                    word
            };

            Process p = runtime.exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String output = reader.lines().collect(Collectors.joining("\n"));

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8));
            String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
            if (!errorOutput.isEmpty()) {
                throw new RuntimeException("Node.js script error: " + errorOutput);
            }

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Node.js script exited with code " + exitCode);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(output);

            if (result.has("error")) {
                throw new RuntimeException(result.get("error").asText());
            }

            return result.get("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to translate word: " + e.getMessage());
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
