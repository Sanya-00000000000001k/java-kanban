package com.yandex.app.entities;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public Epic(String epicName, String description) {
        super(epicName, description, StatusOfTask.NEW);
    }
}
