package org.example.testforexam.bot;

import org.example.testforexam.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final AiService aiService;

    // Spring Boot orqali AiService-ni bog'laymiz
    @Autowired
    public MyTelegramBot(AiService aiService) {
        this.aiService = aiService;
    }

    @Override
    public String getBotUsername() {
        return "Imortantbot";
    }

    @Override
    public String getBotToken() {
        return "8515610536:AAFcWX8ynk7OOslBLBOSn8aorT6JVj2CwNU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Faqat matnli xabarlarni qabul qilamiz go go go
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String userText = update.getMessage().getText();

            // 1. "Typing" (yozmoqda...) statusini yuborish
            sendAction(chatId, "typing");

            // 2. AI-dan javob olish (chatId xotira uchun kerak)
            String aiAnswer = aiService.askAi(chatId, userText);

            // 3. Javobni foydalanuvchiga yuborish
            sendMarkdownMessage(chatId, aiAnswer);
        }
    }

    /**
     * Foydalanuvchiga bot nima bilan bandligini ko'rsatish
     * @param actionType "typing", "upload_photo", "record_video" va hokazo
     */
    private void sendAction(String chatId, String actionType) {
        SendChatAction action = SendChatAction.builder()
                .chatId(chatId)
                .action(actionType)
                .build();
        try {
            execute(action);
        } catch (Exception e) {
            System.err.println("Action yuborishda xato: " + e.getMessage());
        }
    }

    /**
     * Markdown formatini qo'llab-quvvatlaydigan xabar yuborish
     */
    private void sendMarkdownMessage(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown") // AI-ning chiroyli formatlashi uchun
                .build();
        try {
            execute(message);
        } catch (Exception e) {
            // Agar Markdown xatosi bo'lsa, oddiy matn sifatida yuboramiz
            try {
                execute(new SendMessage(chatId, text));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}