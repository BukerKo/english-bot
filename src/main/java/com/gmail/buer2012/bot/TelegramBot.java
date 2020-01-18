package com.gmail.buer2012.bot;

import com.gmail.buer2012.dao.TaskDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TelegramBot extends TelegramLongPollingBot {

    private static String BOT_TOKEN = "980204452:AAGaHipzq3CNsGiFdQTR-Fd0lU_PdSe2pgo";
    private static String BOT_USERNAME = "BukerEnglish_bot";

    private EntityManagerFactory sessionFactory;

    public TelegramBot() {
        sessionFactory = Persistence.createEntityManagerFactory( "com.gmail.buer2012.dao" );
    }


    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            EntityManager entityManager = sessionFactory.createEntityManager();
            entityManager.persist(new TaskDao("123", "123"));
        }
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
