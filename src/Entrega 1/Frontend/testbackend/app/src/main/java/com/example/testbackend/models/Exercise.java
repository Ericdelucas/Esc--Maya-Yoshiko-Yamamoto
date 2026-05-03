package com.example.testbackend.models;

import java.io.Serializable;

/**
 * Modelo de dados para um exercício de RPG.
 * Implementa Serializable para permitir o envio entre Activities via Intent.
 */
public class Exercise implements Serializable {
    private int id;
    private String name;
    private String description;
    private String category;
    private String video_url;
    private int durationMinutes; // Dado numérico: duração recomendada
    private int frequencyPerWeek; // Dado numérico: frequência semanal

    public Exercise(int id, String name, String description, String category, String video_url, int durationMinutes, int frequencyPerWeek) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.video_url = video_url;
        this.durationMinutes = durationMinutes;
        this.frequencyPerWeek = frequencyPerWeek;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getVideoUrl() { return video_url; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getFrequencyPerWeek() { return frequencyPerWeek; }
}