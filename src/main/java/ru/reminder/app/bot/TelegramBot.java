package ru.reminder.app.bot;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.reminder.app.bot.command.Command;
import ru.reminder.app.config.BotConfig;
import ru.reminder.app.model.entity.User;
import ru.reminder.app.repository.UserRepository;

import java.util.Optional;

import static ru.reminder.app.bot.command.Command.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserRepository userRepo;

    private static final String REG_ERROR_TEMPLATE = "Ошибка: Пользователь '%s' не найден";

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            Command command = Command.commandFinder(messageText);
            switch (command) {
                case START: {
                    registerUser(command.getResponse(), update.getMessage().getFrom().getUserName(), chatId);
                    break;
                }
                case UNKNOWN:
                    sendMessage(chatId, UNKNOWN.getResponse());
            }
        }
    }

    private void registerUser(String command,String username, Long chatId) {
        Optional<User> element = userRepo.findByUserName(username);
        if (element.isEmpty()) {
            sendMessage(chatId, String.format(REG_ERROR_TEMPLATE,username));
        } else {
            User user = element.get();
            user.setChatId(chatId);
            userRepo.save(user);
            sendMessage(chatId, command);
        }
    }

    public void sendMessage(Long chatId, String textToSend) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(textToSend);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error occurred {}", e.getMessage());

            }
        }
    }

