package com.yandex.app.logic;

import com.yandex.app.entities.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void add(Task task);
}
