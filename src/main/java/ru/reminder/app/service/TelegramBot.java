package ru.reminder.app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.reminder.app.config.BotConfig;
import ru.reminder.app.model.entity.User;
import ru.reminder.app.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserRepository userRepo;

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
            if (messageText.equals("/start")) {
                registerUser(update.getMessage().getFrom().getUserName(), chatId);
            }
            else {
                sendMessage(chatId, "Команда не найдена");
            }

        }
    }

    private void registerUser(String username, Long chatId) {
        Optional<User> element = userRepo.findByUserName(username);
        if (element.isEmpty()) {
            sendMessage(chatId, "Ошибка: Пользователь '" + username + "' не найден в системе. \n" + "Пожалуйста, сначала зарегистрируйтесь: https://my-site.com/register");
        } else {
            User user = element.get();
            user.setChatId(chatId);
            userRepo.save(user);
            sendMessage(chatId, " Успешно! Аккаунт '" + username + "' привязан к этому чату. " + "Теперь вы будете получать уведомления здесь.");
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

