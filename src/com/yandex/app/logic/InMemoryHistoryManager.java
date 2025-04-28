package com.yandex.app.logic;

import com.yandex.app.entities.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int HISTORY_SIZE = 10;

    private List<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (historyList.size() >= HISTORY_SIZE) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}
