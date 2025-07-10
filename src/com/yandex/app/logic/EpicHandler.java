package com.yandex.app.logic;

import com.yandex.app.entities.Epic;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.entities.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseCrudHandler<Epic> {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getEpics();
            sendJson(exchange, 200, epics);
        } else if (path.matches("/epics/\\d+")) {
            int id = getIdFromRequest(path);
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendText(exchange, "{\"Ошибка\": \"Эпик не найден\"}", 404);
            } else {
                sendJson(exchange, 200, epic);
            }
        } else if (path.matches("/epics/\\d+/subtasks")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendText(exchange, "{\"Ошибка\": \"Эпик не найден\"}", 404);
            } else {
                List<Subtask> subtasks = taskManager.getSubtaskByEpic(epic);
                sendJson(exchange, 200, subtasks);
            }
        } else {
            sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        String requestBody = readRequestBody(exchange);
        Epic epic = gson.fromJson(requestBody, Epic.class);

        try {
            if (requestHasId(path)) {
                int id = getIdFromRequest(path);
                epic.setTaskId(id);
                taskManager.updateEpic(epic);
                sendText(exchange, "{\"Успех\": \"Эпик успешно обновлен\"}", 200);
            } else {
                taskManager.createEpic(epic);
                sendText(exchange, "{\"Успех\": \"Эпик успешно создан\"}", 201);
            }
        } catch (Exception exception) {
            sendText(exchange, "{\"Ошибка\": \"Не удалось создать или обновить эпик\"}", 500);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (requestHasId(path)) {
            int id = getIdFromRequest(path);
            taskManager.removeEpicById(id);
            sendText(exchange, "{\"Успех\": \"Эпик удален\"}", 200);
        } else {
            sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
        }
    }
}
