package com.gmail.buer2012.bot;

import com.gmail.buer2012.App;
import com.gmail.buer2012.entity.Task;
import org.hibernate.Session;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.Query;
import java.util.List;
import java.util.Random;

public class TelegramBot extends TelegramLongPollingBot {

    private static String BOT_TOKEN = "980204452:AAGaHipzq3CNsGiFdQTR-Fd0lU_PdSe2pgo";
    private static String BOT_USERNAME = "BukerEnglish_bot";
    private static String DELETE = "Delete";
    private static String NEXT = "Next";
    private Session session;
    private Task currentTask;

    public TelegramBot(Session session) {
        this.session = session;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            try {
                if (update.getMessage().toString().equals(DELETE)) {
                    this.session.beginTransaction();
                    Query qry = this.session.createQuery("delete from Task where Task.id = (:id)");
                    qry.setParameter("id", currentTask.getId());
                    List<Task> tasks = App.getTasks(this.session);
                    Random rand = new Random();
                    Task randomTask = tasks.get(rand.nextInt(tasks.size()));
                    this.setCurrentTask(randomTask);
                    this.execute(new SendMessage(update.getMessage().getChatId(), randomTask.getRussianWord()));
                    this.session.getTransaction().commit();
                } else if (update.getMessage().toString().equals(NEXT)) {
                    this.session.beginTransaction();
                    List<Task> tasks = App.getTasks(this.session);
                    Random rand = new Random();
                    Task randomTask = tasks.get(rand.nextInt(tasks.size()));
                    this.setCurrentTask(randomTask);
                    this.execute(new SendMessage(update.getMessage().getChatId(), randomTask.getRussianWord()));
                    this.session.getTransaction().commit();
                } else {
                    execute(new SendMessage(update.getMessage().getChatId(), currentTask.getEnglishWord()));
                }
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
