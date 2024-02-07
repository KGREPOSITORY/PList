package com.plist.component;

import com.plist.command.CommandContainer;
import com.plist.configuration.TelegramBotConfig;
import com.plist.processor.TextMessageProcessorContainer;
import com.plist.service.telegram.SendBotMessageService;
import lombok.SneakyThrows;
import org.aspectj.apache.bcel.classfile.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfig telegramBotConfig;
    private final CommandContainer commandContainer;
    private final TextMessageProcessorContainer textMessageProcessorContainer;
    private Map<String, String> lastButtonAction = new HashMap<>();


    @Autowired
    public TelegramBot(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
        this.commandContainer = new CommandContainer(new SendBotMessageService(this));
        this.textMessageProcessorContainer =
                new TextMessageProcessorContainer(new SendBotMessageService(this));
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getText().startsWith("/")) {
            commandContainer.findCommand(update.getMessage().getText()).execute(update);
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            lastButtonAction.put(callbackQuery.getMessage().getChatId().toString(),
                    callbackQuery.getData());
            commandContainer.findCommand(callbackQuery.getData()).execute(update);
        } else if (update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            textMessageProcessorContainer
                    .findCommand(lastButtonAction.get(chatId)).processMessage(update);
            lastButtonAction.remove(chatId);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }
}
