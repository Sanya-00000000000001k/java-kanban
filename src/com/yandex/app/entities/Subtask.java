package com.yandex.app.entities;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String subtaskName, String description) {
        super(subtaskName, description);
    }

    public Subtask(String subtaskName, String description, int id, StatusOfTask status, int epicId) {
        super(subtaskName, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
