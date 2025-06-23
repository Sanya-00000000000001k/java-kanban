package com.yandex.app.logic;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private TaskManager taskManager;
    private Task task1;
    private Subtask subtask1;
    private Epic epic1;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        super.taskManager = new InMemoryTaskManager();
        initNewTasks();
    }
}