package com.gmail.buer2012;

import com.gmail.buer2012.bot.TelegramBot;
import com.gmail.buer2012.entity.Task;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    
    private static final List<Long> chatIds = Arrays.asList(304022315L, 397052865L);
    
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Task.class);
        SessionFactory sessionFactory = configuration.configure().buildSessionFactory();
        
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        
        TelegramBot bot = new TelegramBot(sessionFactory);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            bot.setTasks();
            bot.setNewTask();
            chatIds.forEach(bot::sendTask);
        }, 1L, 30L, TimeUnit.SECONDS);
    }
    
    
}
