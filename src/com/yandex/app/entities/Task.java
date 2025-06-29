package com.yandex.app.entities;

import com.yandex.app.logic.DateTimePatterns;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int taskId;

    protected String taskName;
    protected String description;
    protected StatusOfTask status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String taskName, String description, StatusOfTask status, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeToString() {
        if (startTime != null) {
            return startTime.format(DateTimePatterns.DATE_TIME_FORMATTER);
        } else {
            return null;
        }

    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
            return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getEndTimeToString() {
        if (startTime != null) return startTime.plusMinutes(duration.toMinutes()).format(DateTimePatterns.DATE_TIME_FORMATTER);
        else return null;
    }

    public StatusesList getType() {
        return StatusesList.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId);
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
                '}';
    }
}

