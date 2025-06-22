package com.yandex.app.entities;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void checkTasksWithTheSameIdIsEqual() {
        Task task1 = new Task("Задача", "Озадачен", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
        task1.setTaskId(1);
        Task task2 = new Task("Незадача", "Озадачен", StatusOfTask.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(100));
        task2.setTaskId(1);

        assertEquals(task1, task2);
    }
}