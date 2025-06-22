package com.yandex.app.logic;

import com.yandex.app.entities.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() {
        try {
            tempFile = Files.createTempFile("test", ".csv").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempFile.deleteOnExit();

        taskManager = new FileBackedTaskManager(tempFile);
        super.taskManager = taskManager;
        initNewTasks();
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveMethodWithTasksEpicsAndSubtasks() throws IOException {
        Task task = new Task("Задача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.of(2025, 2, 2, 0, 0), Duration.ofMinutes(100));
        Epic epic = new Epic("Эпик 1", "Описание 1", LocalDateTime.of(2025, 2, 2, 0, 0), LocalDateTime.now(), Duration.ofMinutes(100));

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        taskManager.save();

        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertEquals(3, lines.size());

        assertEquals("id,type,name,status,description,startTime,endTime,duration,epic", lines.get(0));

        String taskLine = task.getTaskId() + ",TASK,Задача 1,NEW,Описание 1,02.02.2025 00:00,02.02.2025 01:40,100";
        assertEquals(taskLine, lines.get(1));

        String epicLine = epic.getTaskId() + ",EPIC,Эпик 1,NEW,Описание 1,02.02.2025 00:00,02.02.2025 01:40,100";
        assertEquals(epicLine, lines.get(2));
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
        Task task2 = new Task("Задача 2", "Описание 2", StatusOfTask.IN_PROGRESS, LocalDateTime.of(2025, 2, 2, 0, 0), Duration.ofMinutes(100));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadData(tempFile);

        List<Task> loadedTasks = loadedManager.getTasks();
        assertEquals(2, loadedTasks.size());
    }

    @Test
    void testSaveAndLoadEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
        Epic epic2 = new Epic("Эпик 2", "Описание 2", LocalDateTime.of(2025, 2, 2, 0, 0), LocalDateTime.now(), Duration.ofMinutes(100));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadData(tempFile);

        List<Epic> loadedEpics = loadedManager.getEpics();
        assertEquals(2, loadedEpics.size());
    }

    @Test
    void testSaveAndLoadSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание 1", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100), epic.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", StatusOfTask.IN_PROGRESS, LocalDateTime.of(2025, 2, 2, 0, 0), Duration.ofMinutes(100), epic.getTaskId());
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