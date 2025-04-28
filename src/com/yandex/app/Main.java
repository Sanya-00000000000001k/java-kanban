package com.yandex.app;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import com.yandex.app.logic.*;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        //сделаем все операции с задачами
        Task task1 = new Task("Выучить джаву", "Любыми способами", StatusOfTask.NEW);
        Task task2 = new Task("Запомнить всю инфу с 4го спринта...", "Трудно, но че поделаешь...", StatusOfTask.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task2.getTaskId());

        Task task3 = new Task("Выучить джаву", "было бы неплохо", StatusOfTask.IN_PROGRESS);

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

        Epic epic3 = new Epic("Эпик", "чет сложное...");
        epic3.setTaskId(epic1.getTaskId());
        taskManager.updateEpic(epic3);

        Subtask subtask1 = new Subtask("Посмотреть рилсики", "С кайфом", StatusOfTask.NEW, 2);
        Subtask subtask2 = new Subtask("Посмотреть ВК клипы", "не с кайфом", StatusOfTask.NEW, 2);
        Subtask subtask3 = new Subtask("Посмотреть что-нибудь", "с кайфом",StatusOfTask.NEW, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getSubtaskById(subtask1.getTaskId());
        taskManager.getSubtaskByEpic(epic1);

        Subtask subtask4 = new Subtask("Посмотреть шортсы", "С кайфом", StatusOfTask.IN_PROGRESS, epic1.getTaskId());
        subtask4.setTaskId(subtask1.getTaskId());
        Subtask subtask5 = new Subtask("тик ток", "тебе че 5 лет?", StatusOfTask.IN_PROGRESS, epic1.getTaskId());
        subtask5.setTaskId(subtask2.getTaskId());
        taskManager.updateSubtask(subtask4);
        taskManager.updateSubtask(subtask5);

        taskManager.getSubtasks();

        //проверка наполнения списка историй
        taskManager.getEpicById(2);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);

        taskManager.removeEpicById(epic2.getTaskId());

        taskManager.removeSubtaskById(subtask4.getTaskId());
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }
}
