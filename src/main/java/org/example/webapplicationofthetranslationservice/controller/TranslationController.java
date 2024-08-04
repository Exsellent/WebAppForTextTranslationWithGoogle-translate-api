package org.example.webapplicationofthetranslationservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
public class TranslationController {

    @GetMapping("/translate")
    public TranslationResponse translate(@RequestParam String inputText,
                                         @RequestParam String sourceLang,
                                         @RequestParam String targetLang,
                                         @RequestParam String ipAddress) {
        // Двойное декодирование для устранения остатков URL-кодирования
        String decodedText = URLDecoder.decode(URLDecoder.decode(inputText, StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        // Обработка перевода
        String translatedText = translateText(decodedText, sourceLang, targetLang);

        return new TranslationResponse(sourceLang, targetLang, decodedText, translatedText);
    }

    private String translateText(String inputText, String sourceLang, String targetLang) {

        return "Привет, мир! Это моя первая программа";
    }

    public static class TranslationResponse {
        private String sourceLang;
        private String targetLang;
        private String inputText;
        private String text;


        public TranslationResponse(String sourceLang, String targetLang, String inputText, String text) {
            this.sourceLang = sourceLang;
            this.targetLang = targetLang;
            this.inputText = inputText;
            this.text = text;
        }

        public String getSourceLang() {
            return sourceLang;
        }

        public String getTargetLang() {
            return targetLang;
        }

        public String getInputText() {
            return inputText;
        }

        public String getText() {
            return text;
        }
    }
}
