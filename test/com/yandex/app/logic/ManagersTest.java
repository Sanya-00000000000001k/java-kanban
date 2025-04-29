package com.yandex.app.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void checkManagerReturnsValidManagersExemplars() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}