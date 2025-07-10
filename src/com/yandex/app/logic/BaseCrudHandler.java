package com.yandex.app.logic;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseCrudHandler<T> implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public BaseCrudHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);
                default -> sendText(exchange, "{\"Ошибка\": \"Метод не поддерживается\"}", 406);
            }
        } catch (Exception exception) {
            sendText(exchange, "{\"Ошибка\": \"Внутренняя ошибка сервера\"}", 500);
        }
    }

    protected abstract void handleGet(HttpExchange exchange, String path) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String path) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String path) throws IOException;

    protected boolean requestHasId(String path) {
        return path.matches(".*/\\d+");
    }

    protected int getIdFromRequest(String path) {
        Pattern pattern = Pattern.compile(".*/(\\d+)");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("ID не найден в пути: " + path);
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendJson(HttpExchange exchange, int statusCode, Object object) throws IOException {
        String json = gson.toJson(object);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.close();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, text.getBytes().length);
        exchange.getResponseBody().write(text.getBytes());
        exchange.close();
    }
}
