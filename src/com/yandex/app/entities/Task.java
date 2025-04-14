package com.yandex.app.entities;

public class Task {
    protected int taskId;
    protected String taskName;
    protected String description;
    StatusOfTask status;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
    }

    public Task(String taskName, String description, StatusOfTask status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public Task(String taskName, String description, int taskId, StatusOfTask status) {
        this.taskName = taskName;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }


    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }
}

