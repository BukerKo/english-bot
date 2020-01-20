package com.gmail.buer2012;

import com.gmail.buer2012.bot.TelegramBot;
import com.gmail.buer2012.entity.Task;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
import java.util.Random;


public class App {
    
    private static final String PERSISTENCE_UNIT_NAME = "taskManager";
    private static final String chatId = "304022315";
    
    public static void main(String[] args) throws TelegramApiException, InterruptedException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        TelegramBot bot = new TelegramBot(entityManager);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        
        Random rand = new Random();
        while (true) {
            List<Task> tasks = getTasks(entityManager);
            Task randomTask = tasks.get(rand.nextInt(tasks.size()));
            bot.setCurrentTask(randomTask);
            bot.execute(new SendMessage(chatId, randomTask.getRussianWord()));
            Thread.sleep(10 * 60 * 1000);
        }
    }
    
    private static List<Task> getTasks(EntityManager entityManager) {
        Query query = entityManager.createQuery("SELECT t FROM Task t");
        return (List<Task>) query.getResultList();
    }
}
