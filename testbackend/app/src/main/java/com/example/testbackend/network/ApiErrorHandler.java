package com.example.testbackend.network;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class ApiErrorHandler {

    public static String getErrorMessage(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return "A conexão demorou muito. Verifique sua internet.";
        } else if (t instanceof IOException) {
            return "Sem conexão com o servidor. Verifique sua internet.";
        } else {
            return "Ocorreu um erro inesperado: " + t.getMessage();
        }
    }

    public static String getHttpErrorMessage(int statusCode) {
        switch (statusCode) {
            case 401:
                return "E-mail ou senha incorretos.";
            case 403:
                return "Acesso negado.";
            case 404:
                return "Recurso não encontrado no servidor.";
            case 500:
                return "Erro interno no servidor. Tente mais tarde.";
            default:
                return "Erro no servidor (Código: " + statusCode + ")";
        }
    }
}