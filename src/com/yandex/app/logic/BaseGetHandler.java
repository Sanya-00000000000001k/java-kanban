package com.yandex.app.logic;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class BaseGetHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public BaseGetHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (isSupportedPath(path)) {
                Object data = getData();
                sendJson(exchange, 200, data);
            } else {
                sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "{\"Ошибка\": \"Внутренняя ошибка сервера\"}", 500);
        }
    }

    protected abstract boolean isSupportedPath(String path);

    protected abstract Object getData();

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
