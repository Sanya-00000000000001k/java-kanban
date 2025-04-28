package com.yandex.app.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void checkManagerReturnsValidManagersExemplars() {
        assertTrue(Managers.getDefault() instanceof TaskManager);
        assertTrue(Managers.getDefaultHistory() instanceof HistoryManager);
    }
}