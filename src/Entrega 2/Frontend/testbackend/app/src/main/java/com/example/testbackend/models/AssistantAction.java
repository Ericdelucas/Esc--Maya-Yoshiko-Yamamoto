package com.example.testbackend.models;

public class AssistantAction {
    private String type;
    private String target;
    private String label;

    // Construtor padrão necessário para o GSON/Retrofit
    public AssistantAction() {}

    // Construtor personalizado para o Fallback Local no Android
    public AssistantAction(String type, String target, String label) {
        this.type = type;
        this.target = target;
        this.label = label;
    }

    public String getType() { return type; }
    public String getTarget() { return target; }
    public String getLabel() { return label; }
}