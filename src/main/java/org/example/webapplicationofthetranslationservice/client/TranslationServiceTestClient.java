package org.example.webapplicationofthetranslationservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationServiceTestClient {

    public static void main(String[] args) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String inputText = "Hello world, this is my first program";
            String encodedText = URLEncoder.encode(inputText, StandardCharsets.UTF_8);
            String url = "http://localhost:8080/translate?inputText=" + encodedText + "&sourceLang=en&targetLang=ru&ipAddress=127.0.0.1";
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response);
            if (jsonResponse.has("translatedText")) {
                System.out.println("Translated Text: " + jsonResponse.get("translatedText").asText());
            } else {
                System.out.println("Error: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}