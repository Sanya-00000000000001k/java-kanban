package com.yandex.app.logic;

import com.yandex.app.entities.*;

import java.util.ArrayList;
import java.util.List;


public interface TaskManager {

    int generateTaskId();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    List<Subtask> getSubtaskByEpic(Epic epic);

    void checkEpicStatus(Epic epic);

    List<Task> getHistory();;
}
