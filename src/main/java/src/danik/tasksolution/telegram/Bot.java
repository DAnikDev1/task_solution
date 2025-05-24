package src.danik.tasksolution.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import src.danik.tasksolution.service.MenuService;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {
    private final String botUsername;
    private final MenuService menuService;

    public Bot(@Value("${bot.token}") String token,
               @Value("${bot.username}") String botUsername,
               MenuService menuService) {
        super(token);
        this.botUsername = botUsername;
        this.menuService = menuService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("New Update: {}", update);
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            try {
                SendMessage message = menuService.sendMenuKeyboard(chatId);
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}

