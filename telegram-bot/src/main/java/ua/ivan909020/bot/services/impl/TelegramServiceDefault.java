package ua.ivan909020.bot.services.impl;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.ivan909020.bot.core.ConfigReader;
import ua.ivan909020.bot.domain.models.InlineQuerySend;
import ua.ivan909020.bot.domain.models.MessageEdit;
import ua.ivan909020.bot.domain.models.MessageSend;
import ua.ivan909020.bot.exceptions.FailedSendMessageException;
import ua.ivan909020.bot.services.TelegramService;

public class TelegramServiceDefault extends DefaultAbsSender implements TelegramService {

    private static final TelegramService INSTANCE = new TelegramServiceDefault();

    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    private TelegramServiceDefault() {
        super(ApiContext.getInstance(DefaultBotOptions.class));
    }

    public static TelegramService getInstance() {
        return INSTANCE;
    }

    @Override
    public void sendMessage(MessageSend message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(message.getText());
        sendMessage.setParseMode("HTML");
        if (message.getKeyboard() != null) {
            sendMessage.setReplyMarkup(message.getKeyboard());
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new FailedSendMessageException(String.format("Failed send text message %s", message), e);
        }
    }

    @Override
    public void editMessageText(MessageEdit message) {
        EditMessageText editMessageText = new EditMessageText();
        if (message.getMessageId() != null) {
            editMessageText.setChatId(message.getChatId());
            editMessageText.setMessageId(message.getMessageId());
        } else {
            editMessageText.setInlineMessageId(message.getInlineMessageId());
        }
        editMessageText.setText(message.getText());
        editMessageText.setParseMode("HTML");
        if (message.getKeyboard() != null) {
            editMessageText.setReplyMarkup(message.getKeyboard());
        }
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new FailedSendMessageException(String.format("Failed edit text message %s", message), e);
        }
    }

    @Override
    public void editMessageKeyboard(MessageEdit message) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        if (message.getMessageId() != null) {
            editMessageReplyMarkup.setChatId(message.getChatId());
            editMessageReplyMarkup.setMessageId(message.getMessageId());
        } else {
            editMessageReplyMarkup.setInlineMessageId(message.getInlineMessageId());
        }
        editMessageReplyMarkup.setReplyMarkup(message.getKeyboard());
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new FailedSendMessageException(String.format("Failed edit text message %s", message), e);
        }
    }

    @Override
    public void sendInlineQuery(InlineQuerySend inlineQuery) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getInlineQueryId());
        answerInlineQuery.setResults(inlineQuery.getInlineQueryResults());
        answerInlineQuery.setCacheTime(1);
        try {
            execute(answerInlineQuery);
        } catch (TelegramApiException e) {
            throw new FailedSendMessageException(String.format("Failed send inline query %s", inlineQuery), e);
        }
    }

    @Override
    public String getBotToken() {
        return CONFIG.get("telegram.bot.token");
    }

}
