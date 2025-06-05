package com.yandex.app.logic;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int id = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int generateTaskId() {
        return id++;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksIds = epic.getSubtasksIds();
            for (int subtaskId : subtasksIds) {
                subtasks.remove(subtaskId);
            }
        }
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            checkEpicStatus(epic);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void createTask(Task task) {
        int taskId = generateTaskId();
        task.setTaskId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int taskId = generateTaskId();
        epic.setTaskId(taskId);
        epics.put(taskId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int taskId = generateTaskId();
        subtask.setTaskId(taskId);
        subtasks.put(taskId, subtask);
        epics.get(subtask.getEpicId()).getSubtasksIds().add(taskId);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        List<Integer> subtasksIds = epics.get(id).getSubtasksIds();
        for (int subtaskId : subtasksIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasksIds().remove(Integer.valueOf(id));
        subtasks.remove(id);
        checkEpicStatus(epics.get(epicId));
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtaskByEpic(Epic epic) {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subIds : epic.getSubtasksIds()) {
            subtaskList.add(subtasks.get(subIds));
        }
        return subtaskList;
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        int counterProgress = 0;
        int counterNew = 0;
        List<Integer> subtaskIds = epic.getSubtasksIds();
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
