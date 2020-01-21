package com.gmail.buer2012;

import com.gmail.buer2012.bot.TelegramBot;
import com.gmail.buer2012.entity.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class App {

    private static final List<String> chatIds = Arrays.asList("304022315", "397052865");
    
    public static void main(String[] args) throws TelegramApiException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Task.class);
        SessionFactory sessionFactory = configuration.configure().buildSessionFactory();

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        TelegramBot bot = new TelegramBot(sessionFactory.getCurrentSession());
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        
        Random rand = new Random();
        while (true) {
            List<Task> tasks = getTasks(sessionFactory.getCurrentSession());
            Task randomTask = tasks.get(rand.nextInt(tasks.size()));
            bot.setCurrentTask(randomTask);
            for(String chat: chatIds) {
                bot.execute(new SendMessage(chat, randomTask.getRussianWord()));
            }
            Thread.sleep(10 * 60 * 1000);
        }
    }
    
    public static List<Task> getTasks(Session session) {
        session.beginTransaction();
        Query query = session.createQuery("from Task");
        List<Task> tasks = query.getResultList();
        session.getTransaction().commit();
        return tasks;
    }
}
