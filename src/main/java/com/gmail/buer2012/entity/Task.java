package com.gmail.buer2012.entity;

import javax.persistence.*;

@Entity
@Table(name="task")
public class Task {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "englishWord")
    private String englishWord;

    @Column(name = "russianWord")
    private String russianWord;

    public Task(Long id, String englishWord, String russianWord) {
        this.englishWord = englishWord;
        this.russianWord = russianWord;
        this.id = id;
    }
    
    public Task(String russianWord, String englishWord) {
        this.englishWord = englishWord;
        this.russianWord = russianWord;
    }
    
    public Task() {

    }
    
    @Override
    public String toString() {
        return "Task{" +
                "englishWord='" + englishWord + '\'' +
                ", russianWord='" + russianWord + '\'' +
                '}';
    }
    
    public String getEnglishWord() {
        return englishWord;
    }

    public String getRussianWord() {
        return russianWord;
    }

    public Long getId() {
        return id;
    }
}
