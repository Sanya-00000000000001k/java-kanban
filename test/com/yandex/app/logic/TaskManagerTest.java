package com.yandex.app.logic;

import com.yandex.app.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Subtask subtask1;
    protected Epic epic1;
    protected HistoryManager historyManager;

    @BeforeEach
    void initNewTasks() {
        task1 = new Task("Задача", "Озадачен", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
        subtask1 = new Subtask("Подзадача", "Озадачен", StatusOfTask.NEW, LocalDateTime.now().plusDays(1), Duration.ofMinutes(100), 1);
        epic1 = new Epic("Эпик", "Описание эпика", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
    }

    @Test
    void testTaskManagerCanCreateTaskAndFindId() {
        taskManager.createTask(task1);
        assertEquals(task1, taskManager.getTaskById(task1.getTaskId()));
    }

    @Test
    void testTaskManagerCanCreateEpicAndFindId() {
        taskManager.createEpic(epic1);
        assertEquals(epic1, taskManager.getEpicById(epic1.getTaskId()));
    }

    @Test
    void testSpecifiedIdAndGeneratedIdConflict() {
        Task task2 = new Task("Задачка", "Озадачен", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
        taskManager.createTask(task1);
        task2.setTaskId(2);

        assertEquals(task1.getTaskId() + 1, task2.getTaskId());
    }

    @Test
    void testTaskRemainsUnchangedAfterAddingToManager() {
        taskManager.createTask(task1);
        Task retrievedTask = taskManager.getTaskById(task1.getTaskId());

        assertEquals(task1.getTaskId(), retrievedTask.getTaskId());
        assertEquals(task1.getTaskName(), retrievedTask.getTaskName());
        assertEquals(task1.getDescription(), retrievedTask.getDescription());
        assertEquals(task1.getStatus(), retrievedTask.getStatus());
    }

    @Test
    void testTaskHistoryAfterUpdate() {
        Task task = new Task("Задача", "Описание", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
        taskManager.createTask(task);

        int taskId = task.getTaskId();

        Task retrievedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedTask);

        Task updatedTask = new Task("Обновленная задача", "Новое описание", StatusOfTask.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(100));
        updatedTask.setTaskId(taskId);
        taskManager.updateTask(updatedTask);

        Task retrievedUpdatedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedUpdatedTask);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());

        Task secondEntry = history.getFirst();
        assertNotNull(secondEntry);
        assertEquals("Обновленная задача", secondEntry.getTaskName());
        assertEquals("Новое описание", secondEntry.getDescription());
    }

    @Test
    void testRemoveSubtaskClearsEpicSubtaskIds() {
        Epic epic = new Epic("Эпик 1", "Описание эпика", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
        taskManager.createEpic(epic);
        int epicId = epic.getTaskId();

        Subtask subtask1 = new Subtask("Сабтаска 1", "Описание сабтаски 1", StatusOfTask.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(100), epicId);
        taskManager.createSubtask(subtask1);
        int subtaskId1 = subtask1.getTaskId();

        Subtask subtask2 = new Subtask("Сабтаска 2", "Описание сабтаски 2", StatusOfTask.NEW, LocalDateTime.of(2025, 2, 2, 0, 0), Duration.ofMinutes(100), epicId);
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

    @Test
    void getPrioritizedTasks() {
        taskManager.createEpic(epic1);
        taskManager.createTask(task1);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача", "Озадачен", StatusOfTask.NEW, LocalDateTime.now().plusDays(2), Duration.ofMinutes(100), 1);
        taskManager.createSubtask(subtask2);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.get(1).getTaskId());
        assertEquals(4, prioritizedTasks.get(2).getTaskId());
    }

    @Test
    void checkEpicStatusCalculation() {
        taskManager.createEpic(epic1);
        int epicId = epic1.getTaskId();

        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Сабтаска 2", "Описание сабтаски 2", StatusOfTask.NEW, LocalDateTime.of(2025, 2, 2, 0, 0), Duration.ofMinutes(100), epicId);
        taskManager.createSubtask(subtask2);

        assertEquals(StatusOfTask.NEW, epic1.getStatus());

        subtask1.setStatus(StatusOfTask.DONE);
        subtask2.setStatus(StatusOfTask.DONE);
        taskManager.checkEpicStatus(epic1);

        assertEquals(StatusOfTask.DONE, epic1.getStatus());

        subtask2.setStatus(StatusOfTask.NEW);
        taskManager.checkEpicStatus(epic1);

        assertEquals(StatusOfTask.IN_PROGRESS, epic1.getStatus());

        subtask1.setStatus(StatusOfTask.IN_PROGRESS);
        subtask2.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.checkEpicStatus(epic1);

        assertEquals(StatusOfTask.IN_PROGRESS, epic1.getStatus());
    }
}