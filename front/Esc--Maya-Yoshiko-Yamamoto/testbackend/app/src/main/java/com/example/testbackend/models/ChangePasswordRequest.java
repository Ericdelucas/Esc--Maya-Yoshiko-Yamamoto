package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("current_password")
    private String currentPassword;

    @SerializedName("new_password")
    private String newPassword;

    @SerializedName("confirm_password")
    private String confirmPassword;

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}