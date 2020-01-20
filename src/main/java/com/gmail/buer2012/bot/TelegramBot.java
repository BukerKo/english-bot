package com.gmail.buer2012.bot;

import com.gmail.buer2012.entity.Task;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.EntityManager;

public class TelegramBot extends TelegramLongPollingBot {

    private static String BOT_TOKEN = "980204452:AAGaHipzq3CNsGiFdQTR-Fd0lU_PdSe2pgo";
    private static String BOT_USERNAME = "BukerEnglish_bot";
    private EntityManager entityManager;
    private Task currentTask;
    
    public TelegramBot(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            try {
                execute(new SendMessage(update.getMessage().getChatId(), currentTask.getEnglishWord()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
    
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
