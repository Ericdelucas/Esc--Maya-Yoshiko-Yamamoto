package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Exercise implements Serializable {
    private Integer id;
    private String title;
    private String description;
    
    @SerializedName("instructions")
    private String instructions;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("video_url")
    private String videoUrl;

    public Exercise() {}

    public Exercise(String title, String description, String instructions, String imageUrl, String videoUrl) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}