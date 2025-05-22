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
    private Task task1;
    private Subtask subtask1;
    private Epic epic1;
    private HistoryManager historyManager;

    @BeforeEach
    public void removeAllTasks() {
        taskManager = Managers.getDefault();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        task1 = new Task("Задача", "Озадачен", StatusOfTask.NEW);
        subtask1 = new Subtask("Задача", "Озадачен", StatusOfTask.NEW, 1);
        epic1 = new Epic("Задача", "Озадачен");
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testTaskManagerCanCreateTaskAndFindId() {
        taskManager.createTask(task1);
        assertEquals(task1, taskManager.getTaskById(0));
    }

    @Test
    public void testTaskManagerCanCreateSubtaskAndFindId() {
        taskManager.createTask(subtask1);
        assertEquals(subtask1, taskManager.getTaskById(0));
    }

    @Test
    public void testTaskManagerCanCreateEpicAndFindId() {
        taskManager.createTask(epic1);
        assertEquals(epic1, taskManager.getTaskById(0));
    }

    @Test
    public void testSpecifiedIdAndGeneratedIdConflict() {
        Task task2 = new Task("Задачка", "Озадачен", StatusOfTask.NEW);
        taskManager.createTask(task1);
        task2.setTaskId(1);

        assertEquals(task1.getTaskId() + 1, task2.getTaskId());
    }

    @Test
    public void testTaskRemainsUnchangedAfterAddingToManager() {
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
        Task task1 = new Task("Задача", "Описание", StatusOfTask.NEW);
        taskManager.createTask(task1);

        int taskId = task1.getTaskId();

        Task retrievedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedTask);

        Task updatedTask = new Task("Задача 2", "Описание 2", StatusOfTask.IN_PROGRESS);
        updatedTask.setTaskId(taskId);
        taskManager.updateTask(updatedTask);

        Task retrievedUpdatedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedUpdatedTask);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());

        Task secondEntry = history.getFirst();
        assertNotNull(secondEntry);
        assertEquals("Задача 2", secondEntry.getTaskName());
        assertEquals("Описание 2", secondEntry.getDescription());
    }

    @Test
    public void testRemoveSubtaskClearsEpicSubtaskIds() {
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.createEpic(epic);
        int epicId = epic.getTaskId();

        Subtask subtask1 = new Subtask("Сабтаска 1", "Описание сабтаски 1", StatusOfTask.IN_PROGRESS, epicId);
        taskManager.createSubtask(subtask1);
        int subtaskId1 = subtask1.getTaskId();

        Subtask subtask2 = new Subtask("Сабтаска 2", "Описание сабтаски 2", StatusOfTask.NEW, epicId);
        taskManager.createSubtask(subtask2);
        int subtaskId2 = subtask2.getTaskId();

        Epic retrievedEpic = taskManager.getEpicById(epicId);
        assertNotNull(retrievedEpic);
        List<Integer> subtaskIds = retrievedEpic.getSubtasksIds();
        assertEquals(2, subtaskIds.size());
        assertTrue(subtaskIds.contains(subtaskId1));
        assertTrue(subtaskIds.contains(subtaskId2));

        taskManager.removeSubtaskById(subtaskId1);

        retrievedEpic = taskManager.getEpicById(epicId);
        assertNotNull(retrievedEpic);
        subtaskIds = retrievedEpic.getSubtasksIds();
        assertEquals(1, subtaskIds.size());
        assertFalse(subtaskIds.contains(subtaskId1));
        assertTrue(subtaskIds.contains(subtaskId2));

        taskManager.removeSubtaskById(subtaskId2);

        retrievedEpic = taskManager.getEpicById(epicId);
        assertNotNull(retrievedEpic);
        subtaskIds = retrievedEpic.getSubtasksIds();
        assertEquals(0, subtaskIds.size());
    }
}