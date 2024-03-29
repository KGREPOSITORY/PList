package com.plist.command;

import com.plist.service.telegram.SendBotMessagePreparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class AddProductsToDishCommand implements Command{

    private final SendBotMessagePreparator sendBotMessageService;
    private final String CALLBACK_MESSAGE = "Specify dish name as a header, " +
            "after that specify each product from new line. \n" +
            "Like : \n" +
            "Dish name\n" +
            "Product 1, weight\n" +
            "Product 2, weight \n" +
            "and etc. \n" +
            "Additional Notes : \n" +
            "If you don't want to specify weight, just leave it. \n" +
            "Like: \n" +
            "'Product 1'";
    @Autowired
    public AddProductsToDishCommand(SendBotMessagePreparator sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public SendMessage execute(Update update) {
        return sendBotMessageService.prepareMessage(update
                        .getCallbackQuery()
                        .getMessage()
                        .getChatId()
                , CALLBACK_MESSAGE);
    }
}
