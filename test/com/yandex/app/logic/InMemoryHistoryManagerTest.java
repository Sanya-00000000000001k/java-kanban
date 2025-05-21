package com.yandex.app.logic;

import com.yandex.app.entities.Task;
import com.yandex.app.logic.HistoryManager;
import com.yandex.app.logic.InMemoryHistoryManager.CustomLinkedList;
import com.yandex.app.logic.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private CustomLinkedList customLinkedList;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testAddSingleTask() {
        Task task1 = new Task("Задача 1", "Описание 1", null);
        task1.setTaskId(1);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task entry = history.get(0);
        assertNotNull(entry);
        assertEquals("Задача 1", entry.getTaskName());
        assertEquals("Описание 1", entry.getDescription());
    }

    @Test
    public void testAddMultipleTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", null);
        task1.setTaskId(1);

        Task task2 = new Task("Задача 2", "Описание 2", null);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
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

    @Test
    public void testRemoveNodeById() {
        Task task1 = new Task("Задача 1", "Описание 1", null);
        task1.setTaskId(1);

        Task task2 = new Task("Задача 2", "Описание 2", null);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task remainingEntry = history.get(0);
        assertNotNull(remainingEntry);
        assertEquals("Задача 2", remainingEntry.getTaskName());
        assertEquals("Описание 2", remainingEntry.getDescription());

        historyManager.remove(2);

        history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void testRemoveNonExistentNode() {
        Task task1 = new Task("Задача 1", "Описание 1", null);
        task1.setTaskId(1);

        historyManager.add(task1);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task remainingEntry = history.get(0);
        assertNotNull(remainingEntry);
        assertEquals("Задача 1", remainingEntry.getTaskName());
        assertEquals("Описание 1", remainingEntry.getDescription());
    }

    @Test
    public void testGetTasksEmptyList() {
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }
}