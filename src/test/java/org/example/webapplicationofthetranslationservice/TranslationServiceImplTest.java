package org.example.webapplicationofthetranslationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.webapplicationofthetranslationservice.service.TranslationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TranslationServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Runtime runtime;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTranslateText() throws Exception {
        String ipAddress = "127.0.0.1";
        String inputText = "Hello";
        String sourceLang = "en";
        String targetLang = "es";
        String expectedTranslation = "Hola";

        // Создаем мок ответа от Node.js скрипта
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("text", expectedTranslation);
        String mockResponse = mapper.writeValueAsString(responseNode);

        // Мокируем Process и его InputStream
        Process mockProcess = mock(Process.class);
        InputStream mockInputStream = new ByteArrayInputStream(mockResponse.getBytes());
        when(mockProcess.getInputStream()).thenReturn(mockInputStream);

        // Мокируем Runtime для возврата нашего мок-процесса
        when(runtime.exec(any(String[].class))).thenReturn(mockProcess);

        // Выполняем тестируемый метод
        String actualTranslation = translationService.translateText(ipAddress, inputText, sourceLang, targetLang);

        // Проверяем результат
        assertThat(actualTranslation).isEqualTo(expectedTranslation);

        // Проверяем, что результат был сохранен в базу данных
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO translation_requests (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)"),
                eq(ipAddress),
                eq(inputText),
                eq(expectedTranslation),
                any(Timestamp.class)
        );
    }
}