package com.gmail.buer2012.bot;

import com.gmail.buer2012.entity.Task;
import com.google.common.collect.ImmutableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.Query;
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
    private List<Task> tasks;
    private Task currentTask;
    private Random random;
    
    public TelegramBot(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.random = new Random(System.currentTimeMillis());
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
    
    public void sendTask(Long chatId) {
        sendMessage(chatId, currentTask.getRussianWord());
    }
    
    
    public void setNewTask() {
        currentTask = tasks.get(random.nextInt(tasks.size()));
    }
    
    public void setTasks() {
        Session session = getSession();
        session.beginTransaction();
        Query query = session.createQuery("from Task");
        List<Task> tasks = query.getResultList();
        session.getTransaction().commit();
        this.tasks = tasks;
        session.close();
    }
    
    public void deleteTask() {
        Session session = getSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from Task where Task.id = (:id)");
        query.setParameter("id", currentTask.getId());
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
    
    private Session getSession() {
        return sessionFactory.openSession();
    }
    
    private void sendAnswer(Long chatId) {
        sendMessage(chatId, currentTask.getEnglishWord());
    }
    
    private void sendRandomTask(Long chatId) {
        Task randomTask = tasks.get(random.nextInt(tasks.size()));
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
    
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
    
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
