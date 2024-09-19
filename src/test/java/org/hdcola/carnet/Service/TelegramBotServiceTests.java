package org.hdcola.carnet.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestPropertySource("classpath:test.properties")
class TelegramBotServiceTests {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private TelegramBotService telegramBotService;

    @Value("${telegram.bot.admin.chat.id}")
    private String adminChatId;

    @Value("${telegram.bot.token}")
    private String botToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telegramBotService = new TelegramBotService(webClient);
    }

    @Test
    void testSendMessage() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Message sent"));

        Mono<String> response = telegramBotService.sendMessage(123L, "Test message");

        assertNotNull(response);
        assertEquals("Message sent", response.block());
    }

    @Test
    void testSendMessageWithEmptyToken() {
        telegramBotService = new TelegramBotService(webClient);
        telegramBotService.setBOT_TOKEN("");

        Mono<String> response = telegramBotService.sendMessage(123L, "Test message");

        assertTrue(response.blockOptional().isEmpty());
    }

    @Test
    void testSendMessageToAdmin() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Message sent"));

        telegramBotService.sendMessageToAdmin("Test message");

        verify(webClient, times(1)).post();
    }

    @Test
    void testSendMessageToAdminWithEmptyAdminChatId() {
        telegramBotService = new TelegramBotService(webClient);
        telegramBotService.setADMIN_CHAT_ID("");

        telegramBotService.sendMessageToAdmin("Test message");

        verify(webClient, never()).post();
    }
}