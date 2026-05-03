package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class LoginStatus {
    private String identifier;
    private boolean blocked;
    @SerializedName("attempts_used")
    private int attemptsUsed;
    @SerializedName("attempts_remaining")
    private int attemptsRemaining;
    @SerializedName("max_attempts")
    private int maxAttempts;
    @SerializedName("block_until")
    private String blockUntil;

    public String getIdentifier() {
        return identifier;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public String getBlockUntil() {
        return blockUntil;
    }
}
