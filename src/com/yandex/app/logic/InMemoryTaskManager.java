package com.yandex.app.logic;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import com.yandex.app.exceptions.CollisionException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getTaskId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

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
            prioritizedTasks.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            List<Integer> subtasksIds = epic.getSubtasksIds();
            for (int subtaskId : subtasksIds) {
                subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
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
            checkEpicEndTime(epic);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
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
        validate(task);
        int taskId = generateTaskId();
        task.setTaskId(taskId);
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        int taskId = generateTaskId();
        epic.setTaskId(taskId);
        epics.put(taskId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        validate(subtask);
        int taskId = generateTaskId();
        subtask.setTaskId(taskId);
        subtasks.put(taskId, subtask);
        epics.get(subtask.getEpicId()).getSubtasksIds().add(taskId);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        checkEpicEndTime(epics.get(subtask.getEpicId()));
        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateTask(Task task) {
        validate(task);
        prioritizedTasks.remove(tasks.get(task.getTaskId()));
        tasks.put(task.getTaskId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        validate(subtask);
        prioritizedTasks.remove(subtasks.get(subtask.getTaskId()));
        subtasks.put(subtask.getTaskId(), subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        checkEpicEndTime(epics.get(subtask.getEpicId()));
        prioritizedTasks.add(subtask);
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        List<Integer> subtasksIds = epics.get(id).getSubtasksIds();
        for (int subtaskId : subtasksIds) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        prioritizedTasks.remove(subtasks.get(id));
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasksIds().remove(Integer.valueOf(id));
        subtasks.remove(id);
        checkEpicStatus(epics.get(epicId));
        historyManager.remove(id);
        checkEpicEndTime(epics.get(epicId));
    }

    @Override
    public List<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(StatusOfTask.NEW);
            return;
        }

        long newCount = subtaskIds.stream()
                .map(subtasks::get)
                .map(Subtask::getStatus)
                .filter(status -> status == StatusOfTask.NEW)
                .count();

        long doneCount = subtaskIds.stream()
                .map(subtasks::get)
                .map(Subtask::getStatus)
                .filter(status -> status == StatusOfTask.DONE)
                .count();

        long inProgressCount = subtaskIds.stream()
                .map(subtasks::get)
                .map(Subtask::getStatus)
                .filter(status -> status == StatusOfTask.IN_PROGRESS)
                .count();

        if (inProgressCount > 0) {
            epic.setStatus(StatusOfTask.IN_PROGRESS);
        } else if (subtaskIds.size() > 1 && newCount > 0 && doneCount > 0) {
            epic.setStatus(StatusOfTask.IN_PROGRESS);
        } else if (newCount > 0) {
            epic.setStatus(StatusOfTask.NEW);
        } else {
            epic.setStatus(StatusOfTask.DONE);
        }
    }

    public void checkEpicEndTime(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksIds();

        List<Subtask> subtasksList = subtaskIds.stream()
                .map(subtasks::get)
                .toList();

        LocalDateTime minStartTime = subtasksList.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxEndTime = subtasksList.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(minStartTime);
        epic.setEndTime(maxEndTime);
        if (maxEndTime != null && minStartTime != null) {
            epic.setDuration(Duration.between(minStartTime, maxEndTime));
        } else {
            epic.setDuration(null);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
    @Override
    public void validate(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();

        boolean hasCollision = prioritizedTasks.stream()
                .filter(existingTask -> task.getStartTime() != null && existingTask.getStartTime() != null)
                .filter(existingTask -> task.getTaskId() != existingTask.getTaskId())
                .anyMatch(existingTask ->
                        task.getEndTime().isAfter(existingTask.getStartTime()) &&
                                task.getStartTime().isBefore(existingTask.getEndTime())
                );

        if (hasCollision) {
            throw new CollisionException("Наблюдается задача, время начала которой пересекаются с другими.");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
