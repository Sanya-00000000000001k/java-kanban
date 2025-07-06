package com.yandex.app.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.entities.Epic;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import com.yandex.app.exceptions.CollisionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    protected static final Gson gson = getGson();
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/epics", new TaskHandler());
        httpServer.createContext("/subtasks", new TaskHandler());
        httpServer.createContext("/history", new UserHandler());
        httpServer.createContext("/prioritized", new UserHandler());
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    private class TaskHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if (path.startsWith("/tasks")) {
                    handleTasks(exchange, method, path);
                } else if (path.startsWith("/subtasks")) {
                    handleSubtasks(exchange, method, path);
                } else if (path.startsWith("/epics")) {
                    handleEpics(exchange, method, path);
                } else {
                    sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "{\"Ошибка\": \"Внутренняя ошибка сервера\"}", 500);
            }
        }

        private void handleTasks(HttpExchange exchange, String method, String path) throws IOException {
            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        List<Task> tasks = taskManager.getTasks();
                        String json = gson.toJson(tasks);
                        sendText(exchange, json, 200);
                    } else if (path.matches("/tasks/\\d+")) {
                        int id = Integer.parseInt(path.substring("/tasks/".length()));
                        Task task = taskManager.getTaskById(id);
                        if (task == null) {
                            sendText(exchange, "{\"Ошибка\": \"Задача не найдена\"}", 404);
                        } else {
                            String json = gson.toJson(task);
                            sendText(exchange, json, 200);
                        }
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                case "POST":
                    if (path.equals("/tasks")) {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(requestBody, Task.class);
                        try {
                            taskManager.createTask(task);
                            sendText(exchange, "{\"Успех\": \"Задача успешно создана\"}", 201);
                        } catch (CollisionException e) {
                            sendText(exchange, "{\"Ошибка\": \"Задача пересекается с другой\"}", 406);
                        }

                    } else if (path.matches("/tasks/\\d+")) {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        int id = Integer.parseInt(path.substring("/tasks/".length()));
                        Task task = gson.fromJson(requestBody, Task.class);

                        task.setTaskId(id);
                        try {
                            taskManager.updateTask(task);
                            sendText(exchange, "{\"Успех\": \"Задача успешно обновлена!\"}", 200);
                        } catch (CollisionException e) {
                            sendText(exchange, "{\"Ошибка\": \"Задача пересекается с другой\"}", 406);
                        }

                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        int id = Integer.parseInt(path.substring("/tasks/".length()));
                        taskManager.removeTaskById(id);
                        sendText(exchange, "{\"Задача удалена\"}", 200);
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                default:
                    sendText(exchange, "{\"Ошибка\": \"Не поддерживаемый метод\"}", 405);
                    break;
            }
        }

        private void handleEpics(HttpExchange exchange, String method, String path) throws IOException {
            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        List<Epic> epics = taskManager.getEpics();
                        String json = gson.toJson(epics);
                        sendText(exchange, json, 200);
                    } else if (path.matches("/epics/\\d+")) {
                        int id = Integer.parseInt(path.substring("/epics/".length()));
                        Epic epic = taskManager.getEpicById(id);
                        if (epic == null) {
                            sendText(exchange, "{\"Ошибка\": \"Эпик не найден\"}", 404);
                        } else {
                            String json = gson.toJson(epic);
                            sendText(exchange, json, 200);
                        }
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        String[] parts = path.split("/");
                        int id = Integer.parseInt(parts[2]);
                        Epic epic = taskManager.getEpicById(id);
                        if (epic == null) {
                            sendText(exchange, "{\"Ошибка\": \"Эпик не найден\"}", 404);
                        } else {
                            String json = gson.toJson(taskManager.getSubtaskByEpic(epic));
                            sendText(exchange, json, 200);
                        }
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                case "POST":
                    if (path.equals("/epics")) {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(requestBody, Epic.class);
                        taskManager.createEpic(epic);
                        sendText(exchange, "{\"Успех\": \"Эпик успешно создан\"}", 201);

                    } else if (path.matches("/epics/\\d+")) {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        int id = Integer.parseInt(path.substring("/epics/".length()));
                        Task task = gson.fromJson(requestBody, Task.class);

                        task.setTaskId(id);
                        taskManager.updateTask(task);
                        sendText(exchange, "{\"Успех\": \"Эпик успешно обновлен!\"}", 200);

                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        int id = Integer.parseInt(path.substring("/epics/".length()));
                        taskManager.removeEpicById(id);
                        sendText(exchange, "{\"Эпик удален\"}", 200);
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                default:
                    sendText(exchange, "{\"Ошибка\": \"Не поддерживаемый метод\"}", 405);
                    break;
            }
        }

        private void handleSubtasks(HttpExchange exchange, String method, String path) throws IOException {
            switch (method) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        String json = gson.toJson(subtasks);
                        sendText(exchange, json, 200);
                    } else if (path.matches("/subtasks/\\d+")) {
                        int id = Integer.parseInt(path.substring("/subtasks/".length()));
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask == null) {
                            sendText(exchange, "{\"Ошибка\": \"Подзадача не найдена\"}", 404);
                        } else {
                            String json = gson.toJson(subtask);
                            sendText(exchange, json, 200);
                        }
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                case "POST":
                    if (path.equals("/subtasks")) {
                        try {
                            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

                            taskManager.createSubtask(subtask);
                            sendText(exchange, "{\"Успех\": \"Подзадача успешно создана\"}", 201);
                        } catch (CollisionException e) {
                            sendText(exchange, "{\"Ошибка\": \"Подзадача пересекается с другой\"}", 409);
                        }
                        catch (Exception e) {
                            sendText(exchange, "{\"Ошибка\": \"Не удалось создать подзадачу\"}", 500);
                        }
                    } else if (path.matches("/subtasks/\\d+")) {
                        try {
                            int id = Integer.parseInt(path.substring("/subtasks/".length()));
                            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

                            subtask.setTaskId(id);
                            taskManager.updateSubtask(subtask);
                            sendText(exchange, "{\"Успех\": \"Подзадача обновлена\"}", 200);
                        } catch (CollisionException e) {
                            sendText(exchange, "{\"Ошибка\": \"Подзадача пересекается с другой\"}", 409);
                        }
                        catch (Exception e) {
                            sendText(exchange, "{\"Ошибка\": \"Ошибка при обновлении подазадачи\"}", 500);
                        }
                    } else {
                        sendText(exchange, "{\"error\": \"Invalid endpoint\"}", 404);
                    }
                    break;

                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        try {
                            int id = Integer.parseInt(path.substring("/subtasks/".length()));
                            taskManager.removeSubtaskById(id);
                            sendText(exchange, "{\"Успех\": \"Подазадча уделена\"}", 200);
                        } catch (NumberFormatException e) {
                            sendText(exchange, "{\"Ошибка\": \"Неверный id задачи\"}", 400);
                        } catch (Exception e) {
                            sendText(exchange, "{\"Ошибка\": \"Ошибка при удалении подзадачи\"}", 500);
                        }
                    } else {
                        sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                    }
                    break;

                default:
                    sendText(exchange, "{\"Ошибка\": \"Неподдерживаемый метод\"}", 405);
                    break;
            }
        }
    }

    private class UserHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String path = exchange.getRequestURI().getPath();

            try {
                if (path.startsWith("/history")) {
                    String historyString = gson.toJson(taskManager.getHistory());
                    sendText(exchange, historyString, 200);
                } else if (path.startsWith("/prioritized")) {
                    String prioritizedString = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, prioritizedString, 200);
                } else {
                    sendText(exchange, "{\"Ошибка\": \"Неверный эндпоинт\"}", 404);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "{\"Ошибка\": \"Внутренняя ошибка сервера\"}", 500);
            }
        }


    }

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        HttpTaskServer server = null;
        try {
            server = new HttpTaskServer(taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }
}
