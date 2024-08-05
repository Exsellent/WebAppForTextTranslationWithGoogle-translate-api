package org.example.webapplicationofthetranslationservice.controller;

public class TranslationResponse {
    private String sourceLanguage;
    private String targetLanguage;
    private String inputText;
    private String translatedText;

    public TranslationResponse(String sourceLanguage, String targetLanguage, String inputText, String translatedText) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.inputText = inputText;
        this.translatedText = translatedText;
    }

    // Геттеры и сеттеры
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
