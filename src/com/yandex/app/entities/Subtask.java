package com.yandex.app.entities;

public class Subtask extends Task {

    private int epicId;

    //не понимаю зачем убирать здесь этот конструктор, я же не смогу создать тогда сабтаску
    public Subtask(String subtaskName, String description, StatusOfTask status, int epicId) {
        super(subtaskName, description, status);
        this.epicId = epicId;
    }

    public Subtask(String subtaskName, String description, int id, StatusOfTask status, int epicId) {
        super(subtaskName, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
