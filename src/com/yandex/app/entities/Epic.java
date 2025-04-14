package com.yandex.app.entities;


import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();


    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public Epic(String epicName, String description) {
        super(epicName, description);
    }

    public Epic(String epicName, String description, int id, StatusOfTask status, ArrayList<Integer> subtaskIds) {
        super(epicName, description, id, status);
        this.subtasksIds = subtaskIds;
    }
}
