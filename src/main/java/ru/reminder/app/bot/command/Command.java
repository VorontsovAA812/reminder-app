package ru.reminder.app.bot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Command {

    START("/start", "Добро пожаловать! Вы зарегистрированы."),
    STOP("/stop", "Сервис остановлен. До свидания!"),
    HELP("/help", "Список доступных команд: /start, /stop, /help"),
    UNKNOWN("", "Команда не найдена. Попробуйте /help");

    private final String commandName;
    private final String response;

    public static Command commandFinder(String messageText) {

        for (Command element : values()) {
            if (element.getCommandName().equals(messageText)) return element;
        }
        return UNKNOWN;
    }

}