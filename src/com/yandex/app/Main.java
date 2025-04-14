package com.yandex.app;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import com.yandex.app.logic.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //сделаем все операции с задачами
        Task task1 = new Task("Выучить джаву", "Любыми способами", StatusOfTask.NEW);
        Task task2 = new Task("Запомнить всю инфу с 4го спринта...", "Трудно, но че поделаешь...", StatusOfTask.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task2.getTaskId());

        Task task3 = new Task("Выучить джаву", "было бы неплохо", task1.getTaskId(), StatusOfTask.IN_PROGRESS);

        taskManager.updateTask(task3);

        taskManager.getTasks();

        taskManager.removeTaskById(task2.getTaskId());

        taskManager.removeAllTasks();

        //сделаем все операции с эпиками и подзадачами
        Epic epic1 = new Epic("Эпичный", "эпик");
        Epic epic2 = new Epic("5к", "сторипоинтов");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.getEpicById(epic1.getTaskId());
        taskManager.getEpics();

        Epic epic3 = new Epic("Эпик", "чет сложное...", epic1.getTaskId(), epic1.getStatus(), epic1.getSubtasksIds());
        taskManager.updateEpic(epic3);


        Subtask subtask1 = new Subtask("Посмотреть рилсики", "С кайфом", StatusOfTask.NEW, 2);
        Subtask subtask2 = new Subtask("Посмотреть ВК клипы", "не с кайфом", StatusOfTask.NEW, 2);
        Subtask subtask3 = new Subtask("Посмотреть что-нибудь", "с кайфом",StatusOfTask.NEW, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getSubtaskById(subtask1.getTaskId());
        taskManager.getSubtaskByEpic(epic1);

        Subtask subtask4 = new Subtask("Посмотреть шортсы", "С кайфом", 4, StatusOfTask.DONE, 2);
        Subtask subtask5 = new Subtask("тик ток", "тебе че 5 лет?", 5, StatusOfTask.IN_PROGRESS, 2);
        taskManager.updateSubtask(subtask4);
        taskManager.updateSubtask(subtask5);

        taskManager.getSubtasks();

        taskManager.removeEpicById(epic2.getTaskId());

        taskManager.removeSubtaskById(subtask4.getTaskId());

        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }
}
