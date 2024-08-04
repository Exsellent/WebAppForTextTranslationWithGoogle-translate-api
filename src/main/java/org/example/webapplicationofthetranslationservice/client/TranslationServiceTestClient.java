package org.example.webapplicationofthetranslationservice.client;

import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationServiceTestClient {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String inputText = "Hello world, this is my first program";
        String encodedText = URLEncoder.encode(inputText, StandardCharsets.UTF_8);
        String url = "http://localhost:8080/translate?inputText=" + encodedText + "&sourceLang=en&targetLang=ru&ipAddress=127.0.0.1";
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("Ответ: " + response);
    }
}
