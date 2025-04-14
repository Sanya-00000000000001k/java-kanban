package com.yandex.app.entities;


import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();


    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }
    //не понимаю почему должен убрать этот конструктор, ибо я тогда не смогу создавать эпики не зная изначально их id
    public Epic(String epicName, String description) {
        super(epicName, description, StatusOfTask.NEW);
    }

    public Epic(String epicName, String description, int id, StatusOfTask status, ArrayList<Integer> subtaskIds) {
        super(epicName, description, id, status);
        this.subtasksIds = subtaskIds;
    }
}
