package com.gmail.buer2012.bot;

import com.gmail.buer2012.entity.Task;
import com.google.common.collect.ImmutableList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramBot extends TelegramLongPollingBot {
    
    private static String BOT_TOKEN = "980204452:AAGaHipzq3CNsGiFdQTR-Fd0lU_PdSe2pgo";
    private static String BOT_USERNAME = "BukerEnglish_bot";
    private static List<String> DELETE_STRINGS = ImmutableList.of("Delete", "delete");
    private static List<String> NEXT_STRINGS = ImmutableList.of("Next", "next");
    private static String SUCCESS = "Удалено";
    
    private SessionFactory sessionFactory;
    private Task currentTask;
    
    public TelegramBot(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            if (DELETE_STRINGS.contains(update.getMessage().getText())) {
                deleteTask();
                sendMessage(update.getMessage().getChatId(), SUCCESS);
            } else if (NEXT_STRINGS.contains(update.getMessage().getText())) {
                sendRandomTask(update.getMessage().getChatId());
            } else {
                sendAnswer(update.getMessage().getChatId());
            }
        }
    }
    
    public void setNewTask() {
        currentTask = getRandomTaskFromDb();
    }
    
    public void sendTask(Long chatId) {
        sendMessage(chatId, currentTask.getRussianWord());
    }
    
    public void deleteTask() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Task task = session.get(Task.class, currentTask.getId());
        session.remove(task);
        transaction.commit();
        session.close();
    }
    
    private void sendAnswer(Long chatId) {
        sendMessage(chatId, currentTask.getEnglishWord());
    }
    
    private void sendRandomTask(Long chatId) {
        Task randomTask = getRandomTaskFromDb();
        sendMessage(chatId, randomTask.getRussianWord());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> sendMessage(chatId, randomTask.getEnglishWord()), 10L, TimeUnit.SECONDS);
    }
    
    private void sendMessage(Long chatId, String body) {
        try {
            execute(new SendMessage(chatId, body));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private Task getRandomTaskFromDb() {
        Session session = sessionFactory.openSession();
        Integer id = (Integer) session.createSQLQuery("SELECT ID FROM task ORDER BY RAND() LIMIT 1").getSingleResult();
        Task task = session.get(Task.class, Long.valueOf(id));
        session.close();
        return task;
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
