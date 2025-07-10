package com.yandex.app.logic;

import com.yandex.app.entities.Subtask;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.exceptions.CollisionException;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseCrudHandler<Subtask> {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        if (requestHasId(path)) {
            int id = getIdFromRequest(path);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                sendText(exchange, "{\"Ошибка\": \"Подзадача не найдена\"}", 404);
            } else {
                sendJson(exchange, 200, subtask);
            }
        } else {
            List<Subtask> subtasks = taskManager.getSubtasks();
            sendJson(exchange, 200, subtasks);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        String requestBody = readRequestBody(exchange);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        try {
            if (requestHasId(path)) {
                int id = getIdFromRequest(path);
                subtask.setTaskId(id);
                taskManager.updateSubtask(subtask);
                sendText(exchange, "{\"Успех\": \"Подзадача успешно обновлена\"}", 200);
            } else {
                taskManager.createSubtask(subtask);
                sendText(exchange, "{\"Успех\": \"Подзадача успешно создана\"}", 201);
            }
        } catch (CollisionException collisionException) {
            sendText(exchange, "{\"Ошибка\": \"Задача пересекается с другой\"}", 406);
        } catch (Exception exception) {
            sendText(exchange, "{\"Ошибка\": \"Не удалось создать или обновить подзадачу\"}", 500);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (requestHasId(path)) {
            int id = getIdFromRequest(path);
            taskManager.removeSubtaskById(id);
            sendText(exchange, "{\"Успех\": \"Подзадача удалена\"}", 200);
        } else {
            sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
        }
    }
}
