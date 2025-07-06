package com.yandex.app.logic;

import com.google.gson.reflect.TypeToken;
import com.yandex.app.entities.*;
import org.junit.jupiter.api.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {

    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer;

    {
        try {
            taskServer = new HttpTaskServer(manager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws Exception {
        Task task = new Task("Задача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode(), "Задача не была создана");

        List<Task> tasksFromManager = manager.getTasks();
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Задача 1", tasksFromManager.get(0).getTaskName());
    }

    @Test
    public void testGetTasks() throws Exception {
        Task task = new Task("Задача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task("Задача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        Assertions.assertTrue(tasksFromManager.isEmpty());
    }

    @Test
    public void testAddEpic() throws Exception {
        Epic epic = new Epic("Эпик 1", "Описание 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Эпик 1", epicsFromManager.get(0).getTaskName());
    }

    @Test
    public void testGetEpics() throws Exception {
        Epic epic = new Epic("Эпик 1", "Описание 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws Exception {
        Epic epic = new Epic("Test Epic", "Testing epic description",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        Assertions.assertTrue(epicsFromManager.isEmpty());
    }

    @Test
    public void testAddSubtask() throws Exception {
        Epic epic = new Epic("Эпик 1", "Описание 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(30), epic.getTaskId());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Подзадача 1", subtasksFromManager.get(0).getTaskName());
    }

    @Test
    public void testGetSubtasks() throws Exception {
        Epic epic = new Epic("Эпик 1", "Описание 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(30), epic.getTaskId());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws Exception {
        Epic epic = new Epic("Эпик 1", "Описание 1",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(30), epic.getTaskId());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        Assertions.assertTrue(subtasksFromManager.isEmpty());
    }

    @Test
    public void testGetPrioritizedTasks() throws Exception {
        Task task1 = new Task("Задача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание 2",
                StatusOfTask.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertNotNull(prioritizedTasks);
        Assertions.assertEquals(2, prioritizedTasks.size());
        Assertions.assertEquals("Задача 1", prioritizedTasks.get(0).getTaskName());
        Assertions.assertEquals("Задача 2", prioritizedTasks.get(1).getTaskName());
    }

    @Test
    public void testNoPrioritizedTasks() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertNotNull(prioritizedTasks);
        Assertions.assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    public void testGetHistory() throws Exception {
        Task task1 = new Task("Задача 1", "Описание 1",
                StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание 2",
                StatusOfTask.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getTaskId());
        manager.getTaskById(task2.getTaskId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertNotNull(history);
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals("Задача 1", history.get(0).getTaskName());
        Assertions.assertEquals("Задача 2", history.get(1).getTaskName());
    }

    @Test
    public void testEmptyHistory() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Не удалось получить историю просмотров");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertNotNull(history, "Ответ пустой");
        Assertions.assertTrue(history.isEmpty(), "История должна быть пустой");
    }
}