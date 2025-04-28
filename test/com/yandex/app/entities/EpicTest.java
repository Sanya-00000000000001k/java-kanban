package com.yandex.app.entities;

import com.yandex.app.logic.Managers;
import com.yandex.app.logic.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void checkEpicsWithTheSameIdIsEqual() {
        Epic epic1 = new Epic("Задача", "Озадачен");
        epic1.setTaskId(3);
        Epic epic2 = new Epic("эпичег", "эпичный");
        epic2.setTaskId(3);

        assertEquals(epic1, epic2);
    }
}