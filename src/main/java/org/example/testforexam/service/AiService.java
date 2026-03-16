package org.example.testforexam.service;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.*;

@Service
public class AiService {
    private final String API_KEY = "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";
    private final String URL = "https://openrouter.ai/api/v1/chat/completions";

    // Kontekstni saqlash uchun xotira
    private final Map<String, List<Map<String, String>>> chatHistories = new HashMap<>();

    public String askAi(String chatId, String prompt) {
        // 1. Foydalanuvchi tarixini boshqarish
        List<Map<String, String>> history = chatHistories.getOrDefault(chatId, new ArrayList<>());

        if (history.isEmpty()) {
            history.add(Map.of("role", "system", "content",
                    "Sen 2026-yilning eng professional, nazokatli va bilimdon o'zbek yordamchisisan. " +
                            "Javoblaringni HAR DOIM Markdown formatida (qalin, ro'yxat, ajratilgan bloklar) chiroyli tuz. " +
                            "Javoblaring qisqa, lo'nda va foydalanuvchini ruhlantiradigan darajada xushmuomala bo'lsin. " +
                            "Bugungi sana: " + LocalDate.now()));
        }

        history.add(Map.of("role", "user", "content", prompt));

        // 2. API uchun so'rovni shakllantirish
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);
        headers.set("HTTP-Referer", "https://telegram.org");
        headers.set("X-Title", "ZohidExamBot");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-oss-120b");
        body.put("messages", history);
        body.put("temperature", 0.7); // Ijodkorlik darajasi

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(URL, entity, Map.class);

            String aiResponse = (String) ((Map<String, Object>) ((List<Map<String, Object>>) response.get("choices")).get(0).get("message")).get("content");

            // 3. Xotirani yangilash
            history.add(Map.of("role", "assistant", "content", aiResponse));
            if (history.size() > 10) history.remove(1); // Eng eski xabarni o'chirish
            chatHistories.put(chatId, history);

            return aiResponse;
        } catch (Exception e) {
            return "🥀 **Kechirasiz...** \n\nTexnik nosozlik yuz berdi. Birozdan so'ng yana urinib ko'ring. Men sizga yordam berishni juda xohlayman! 😊";
        }
    }
}