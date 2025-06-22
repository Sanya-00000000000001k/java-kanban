package com.yandex.app.entities;

import com.yandex.app.logic.Managers;
import com.yandex.app.logic.TaskManager;
import org.junit.jupiter.api.Test;

import java.awt.image.DataBufferUShort;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void checkEpicsWithTheSameIdIsEqual() {
        Epic epic1 = new Epic("Задача", "Озадачен", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
        epic1.setTaskId(3);
        Epic epic2 = new Epic("эпичег", "эпичный", LocalDateTime.now(), LocalDateTime.now(), Duration.ofMinutes(100));
        epic2.setTaskId(3);

        assertEquals(epic1, epic2);
    }
}