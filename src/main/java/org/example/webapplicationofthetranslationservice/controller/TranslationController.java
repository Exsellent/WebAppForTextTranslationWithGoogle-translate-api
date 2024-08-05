package org.example.webapplicationofthetranslationservice.controller;

import org.example.webapplicationofthetranslationservice.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;

@RestController
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @GetMapping("/translate")
    public ResponseEntity<TranslationResponse> translate(@RequestParam String inputText,
                                                         @RequestParam String sourceLang,
                                                         @RequestParam String targetLang,
                                                         @RequestParam String ipAddress) {
        try {
            String decodedText = URLDecoder.decode(URLDecoder.decode(inputText, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            String translatedText = translationService.translateText(ipAddress, decodedText, sourceLang, targetLang);
            TranslationResponse response = new TranslationResponse(sourceLang, targetLang, decodedText, translatedText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new TranslationResponse("error", e.getMessage(), "", ""));
        }
    }
}
