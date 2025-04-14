package com.yandex.app.logic;

import com.yandex.app.entities.*;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateTaskId() {
        return id++;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
            for (int subtaskId : subtasksIds) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            checkEpicStatus(epic);
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createTask(Task task) {
        int taskId = generateTaskId();
        task.setTaskId(taskId);
        tasks.put(taskId, task);
    }

    public void createEpic(Epic epic) {
        int taskId = generateTaskId();
        epic.setTaskId(taskId);
        epics.put(taskId, epic);
    }

    public void createSubtask(Subtask subtask) {
        int taskId = generateTaskId();
        subtask.setTaskId(taskId);
        subtasks.put(taskId, subtask);
        epics.get(subtask.getEpicId()).getSubtasksIds().add(taskId);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        ArrayList<Integer> subtasksIds = epics.get(id).getSubtasksIds();
        for (int subtaskId : subtasksIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasksIds().remove(Integer.valueOf(id));
        subtasks.remove(id);
        checkEpicStatus(epics.get(epicId));
    }

    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer subIds : epic.getSubtasksIds()) {
            subtaskList.add(subtasks.get(subIds));
        }
        return subtaskList;
    }

    private void checkEpicStatus(Epic epic) {
        int counterProgress = 0;
        int counterNew = 0;
        ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(StatusOfTask.NEW);
            return;
        }
        for (Integer subIds : subtaskIds) {
            if (subtasks.get(subIds).getStatus() == StatusOfTask.IN_PROGRESS) {
                counterProgress++;
            } else if (subtasks.get(subIds).getStatus() == StatusOfTask.NEW) {
                counterNew++;
            }
        }
        if (counterProgress > 0) {
            epic.setStatus(StatusOfTask.IN_PROGRESS);
        } else if (counterNew > 0) {
            epic.setStatus(StatusOfTask.NEW);
        } else {
            epic.setStatus(StatusOfTask.DONE);
        }
    }

}
