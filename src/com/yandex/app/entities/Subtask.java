package com.yandex.app.entities;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String subtaskName, String description, StatusOfTask status, LocalDateTime startTime, Duration duration, int epicId) {
        super(subtaskName, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public StatusesList getType() {
        return StatusesList.SUBTASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + getStartTimeToString() +
                ", endTime=" + getEndTimeToString() +
                ", epicId=" + epicId +
                '}';
    }
}
