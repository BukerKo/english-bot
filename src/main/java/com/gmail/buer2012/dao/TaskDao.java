package com.gmail.buer2012.dao;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TaskDao {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String englishWord;

    private String russianWord;

    public TaskDao(String englishWord, String russianWord) {
        this.englishWord = englishWord;
        this.russianWord = russianWord;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getRussianWord() {
        return russianWord;
    }
}
