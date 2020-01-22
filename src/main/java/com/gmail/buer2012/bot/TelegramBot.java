package com.gmail.buer2012.bot;

import com.gmail.buer2012.entity.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

public class TelegramBot extends TelegramLongPollingBot {
    
    private static final String BOT_TOKEN = "980204452:AAGaHipzq3CNsGiFdQTR-Fd0lU_PdSe2pgo";
    private static final String BOT_USERNAME = "BukerEnglish_bot";
    private static final String DELETE_STRING = "Delete";
    private static final String NEXT_STRING = "Next";
    private static final String ANSWER_STRING = "Answer";
    private static final String SUCCESS = "Deleted";
    
    private SessionFactory sessionFactory;
    private Task currentTask;
    
    public TelegramBot(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            String text = update.getMessage().getText();
            switch (text) {
                case DELETE_STRING:
                    deleteTask();
                    sendMessage(chatId, SUCCESS);
                    break;
                case NEXT_STRING:
                    setNewTask();
                    sendTask(chatId);
                    break;
                case ANSWER_STRING:
                    sendAnswer(chatId);
                    break;
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
    
    private void sendMessage(Long chatId, String body) {
        try {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(DELETE_STRING);
            keyboardRow.add(NEXT_STRING);
            keyboardRow.add(ANSWER_STRING);
            
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
            replyKeyboardMarkup.setResizeKeyboard(true);
            execute(new SendMessage().setChatId(chatId).setText(body).setReplyMarkup(replyKeyboardMarkup));
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
