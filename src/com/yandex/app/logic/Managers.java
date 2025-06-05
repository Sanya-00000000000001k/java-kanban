package com.yandex.app.logic;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTaskManager getDefaultBackendFile() {
        return new FileBackedTaskManager(new File("./resources/test.csv"));
    }
}
