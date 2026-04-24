package com.example.testbackend.models;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Patient implements Serializable {
    private Integer id;
    @SerializedName("full_name")
    private String full_name;
    private String email;
    private String role;
    private String cpf;
    private String phone;
    private String birth_date;
    
    public Patient() {}
    
    public Patient(Integer id, String full_name, String email, String role) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.role = role;
    }

    public Patient(Integer id, String full_name, String email, String cpf, String phone, String birth_date, String role) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.cpf = cpf;
        this.phone = phone;
        this.birth_date = birth_date;
        this.role = role;
    }
    
    public Integer getId() { return id != null ? id : 0; }
    public void setId(Integer id) { this.id = id; }
    
    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBirth_date() { return birth_date; }
    public void setBirth_date(String birth_date) { this.birth_date = birth_date; }

    public String getDisplayName() {
        if (full_name != null && !full_name.trim().isEmpty()) {
            return full_name;
        }
        if (email != null && !email.trim().isEmpty()) {
            return email;
        }
        return "Paciente sem nome";
    }
    
    @NonNull
    @Override
    public String toString() {
        return getDisplayName();
    }
}
