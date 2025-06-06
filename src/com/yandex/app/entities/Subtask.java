package com.yandex.app.entities;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String subtaskName, String description, StatusOfTask status, int epicId) {
        super(subtaskName, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public StatusesList getType() {
        return StatusesList.SUBTASK;
    }
}
