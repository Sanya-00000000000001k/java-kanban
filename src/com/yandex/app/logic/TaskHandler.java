package com.yandex.app.logic;

import com.yandex.app.entities.Task;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.exceptions.CollisionException;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseCrudHandler<Task> {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        if (requestHasId(path)) {
            int id = getIdFromRequest(path);
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                sendText(exchange, "{\"Ошибка\": \"Задача не найдена\"}", 404);
            } else {
                sendJson(exchange, 200, task);
            }
        } else {
            List<Task> tasks = taskManager.getTasks();
            sendJson(exchange, 200, tasks);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        String requestBody = readRequestBody(exchange);
        Task task = gson.fromJson(requestBody, Task.class);

        try {
            if (requestHasId(path)) {
                int id = getIdFromRequest(path);
                task.setTaskId(id);
                taskManager.updateTask(task);
                sendText(exchange, "{\"Успех\": \"Задача успешно обновлена\"}", 200);
            } else {
                taskManager.createTask(task);
                sendText(exchange, "{\"Успех\": \"Задача успешно создана\"}", 201);
            }
        } catch (CollisionException collisionException) {
            sendText(exchange, "{\"Ошибка\": \"Задача пересекается с другой\"}", 406);
        } catch (Exception exception) {
            sendText(exchange, "{\"Ошибка\": \"Не удалось создать или обновить задачу\"}", 500);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (requestHasId(path)) {
            int id = getIdFromRequest(path);
            taskManager.removeTaskById(id);
            sendText(exchange, "{\"Успех\": \"Задача удалена\"}", 200);
        } else {
            sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
        }
    }
}
