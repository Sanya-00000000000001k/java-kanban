package com.yandex.app.logic;

import com.yandex.app.entities.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".csv").toFile();
        tempFile.deleteOnExit();

        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveMethodWithTasksEpicsAndSubtasks() throws IOException {
        Task task = new Task("Задача 1", "Описание 1", StatusOfTask.NEW);
        Epic epic = new Epic("Эпик 1", "Описание 1");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        taskManager.save();

        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertEquals(3, lines.size());

        assertEquals("id,type,name,status,description,epic", lines.get(0));

        String taskLine = task.getTaskId() + ",TASK,Задача 1,NEW,Описание 1";
        assertEquals(taskLine, lines.get(1));

        String epicLine = epic.getTaskId() + ",EPIC,Эпик 1,NEW,Описание 1";
        assertEquals(epicLine, lines.get(2));
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", StatusOfTask.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", StatusOfTask.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadData(tempFile);

        List<Task> loadedTasks = loadedManager.getTasks();
        assertEquals(2, loadedTasks.size());
    }

    @Test
    void testSaveAndLoadEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadData(tempFile);

        List<Epic> loadedEpics = loadedManager.getEpics();
        assertEquals(2, loadedEpics.size());
    }

    @Test
    void testSaveAndLoadSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", StatusOfTask.NEW, epic.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", StatusOfTask.IN_PROGRESS, epic.getTaskId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadData(tempFile);

        List<Subtask> loadedSubtasks = loadedManager.getSubtasks();
        assertEquals(2, loadedSubtasks.size());

        Epic loadedEpic = loadedManager.getEpics().get(0);
        assertNotNull(loadedEpic);
    }
}