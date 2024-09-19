package org.hdcola.carnet.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TelegramBotService {
    private WebClient webClient;

    public TelegramBotService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${telegram.bot.admin.chat.id}")
    private String ADMIN_CHAT_ID="123";
    @Value("${telegram.bot.token}")
    private String BOT_TOKEN="token";


    public Mono<String> sendMessage(Long chatId, String message) {
        if(BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            log.error("Bot token is not set");
            return Mono.empty();
        }
        String TELEGRAM_API_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
        return webClient.post()
                .uri(TELEGRAM_API_URL)
                .body(BodyInserters.fromFormData("chat_id", String.valueOf(chatId))
                        .with("text", message))
                .retrieve()
                .bodyToMono(String.class);
    }

    public void sendMessageSync(Long chatId, String message) {
        sendMessage(chatId, message)
                .subscribe(
                        response -> log.debug("Message sent: {}", response),
                        error -> log.debug("Error sending message: {}", error.getMessage())
                );
    }

    public void sendMessageToAdmin(String message) {
        if(ADMIN_CHAT_ID == null || ADMIN_CHAT_ID.isEmpty()) {
            log.error("Admin chat id is not set");
            return;
        }
        sendMessageSync(Long.parseLong(ADMIN_CHAT_ID), message);
    }
}
