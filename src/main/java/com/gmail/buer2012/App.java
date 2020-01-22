package com.gmail.buer2012;

import com.gmail.buer2012.bot.TelegramBot;
import com.gmail.buer2012.entity.Task;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            bot.setNewTask();
            chatIds.forEach(bot::sendTask);
        }, 0L, 10L, TimeUnit.MINUTES);
    }
    
    private static void readFile(SessionFactory sessionFactory) {
        try (Stream<String> lines = Files.lines(Paths.get("W37.txt"))) {
            final AtomicInteger counter = new AtomicInteger(0);
            Session session = sessionFactory.openSession();
            List<Task> tasks = lines.filter(line -> !StringUtils.isNumericSpace(line)).collect(
                    Collectors.groupingBy(item -> {
                        final int i = counter.getAndIncrement();
                        return (i % 2 == 0) ? i : i - 1;
                    })).values().stream().map(a -> new Task(a.get(0), (a.size() == 2 ? a.get(1) : null)))
                    .collect(Collectors.toList());
        
            tasks.forEach(session::save);
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
