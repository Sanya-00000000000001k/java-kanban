package com.yandex.app.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public Epic(String epicName, String description, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(epicName, description, StatusOfTask.NEW, startTime, duration);
    }

    public StatusesList getType() {
        return StatusesList.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
