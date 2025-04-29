package com.yandex.app.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void checkSubtasksWithTheSameIdIsEqual() {
        Subtask subtask1 = new Subtask("Задача 1", "Описание 1", StatusOfTask.NEW, 1);
        subtask1.setTaskId(2);
        Subtask subtask2 = new Subtask("Задача 2", "Описание 2", StatusOfTask.NEW, 2);
        subtask2.setTaskId(2);

        assertEquals(subtask1, subtask2);
    }

}