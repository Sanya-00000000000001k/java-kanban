package com.yandex.app.logic;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void removeAllTasks() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
    }

    @Test
    public void testTaskManagerCanCreateTaskAndFindId() {
        Task task1 = new Task("Задача", "Озадачен", StatusOfTask.NEW);
        taskManager.createTask(task1);
        assertEquals(task1, taskManager.getTaskById(0));
    }

    @Test
    public void testTaskManagerCanCreateSubtaskAndFindId() {
        Subtask subtask1 = new Subtask("Задача", "Озадачен", StatusOfTask.NEW, 1);
        taskManager.createTask(subtask1);
        assertEquals(subtask1, taskManager.getTaskById(0));
    }

    @Test
    public void testTaskManagerCanCreateEpicAndFindId() {
        Epic epic1 = new Epic("Задача", "Озадачен");
        taskManager.createTask(epic1);
        assertEquals(epic1, taskManager.getTaskById(0));
    }

    @Test
    public void testSpecifiedIdAndGeneratedIdConflict() {
        Task task1 = new Task("Задача", "Озадачен", StatusOfTask.NEW);
        Task task2 = new Task("Задачка", "Озадачен", StatusOfTask.NEW);
        taskManager.createTask(task1);
        task2.setTaskId(1);

        assertEquals(task1.getTaskId() + 1, task2.getTaskId());
    }

    @Test
    public void testTaskRemainsUnchangedAfterAddingToManager() {
        Task task1 = new Task("Задача 1", "Описание 1", StatusOfTask.NEW);
        Task task2 = new Task(task1.getTaskName(), task1.getDescription(), task1.getStatus());

        taskManager.createTask(task1);
        Task retrievedTask = taskManager.getTaskById(task1.getTaskId());

        assertEquals(task2.getTaskId(), retrievedTask.getTaskId());
        assertEquals(task2.getTaskName(), retrievedTask.getTaskName());
        assertEquals(task2.getDescription(), retrievedTask.getDescription());
        assertEquals(task2.getStatus(), retrievedTask.getStatus());
    }

    @Test
    public void testTaskHistoryAfterUpdate() {

        Task initialTask = new Task("Задача 1", "Описание 1", StatusOfTask.NEW);
        taskManager.createTask(initialTask);

        int taskId = initialTask.getTaskId();

        Task retrievedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedTask);

        Task updatedTask = new Task("Задача 2", "Описание 2", StatusOfTask.IN_PROGRESS);
        updatedTask.setTaskId(taskId);
        taskManager.updateTask(updatedTask);

        Task retrievedUpdatedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedUpdatedTask);

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());

        Task firstEntry = history.get(0);
        assertNotNull(firstEntry);
        assertEquals("Задача 1", firstEntry.getTaskName());
        assertEquals("Описание 1", firstEntry.getDescription());

        Task secondEntry = history.get(1);
        assertNotNull(secondEntry);
        assertEquals("Задача 2", secondEntry.getTaskName());
        assertEquals("Описание 2", secondEntry.getDescription());
    }
}