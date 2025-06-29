package com.yandex.app;

import com.yandex.app.entities.Epic;
import com.yandex.app.entities.StatusOfTask;
import com.yandex.app.entities.Subtask;
import com.yandex.app.entities.Task;
import com.yandex.app.logic.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

//        TaskManager taskManager = Managers.getDefault();
//
//        //триггер исключения
//        Subtask subtask1 = new Subtask("Посмотреть рилсики", "С кайфом", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(20), 1);
//        Subtask subtask3 = new Subtask("Посмотреть чет", "С кайфом", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(50), 1);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask3);
//        System.out.println(taskManager.getPrioritizedTasks());

        File inputfile = new File("./resources/test.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(inputfile);

//        Task task1 = new Task("Задача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
//        fileBackedTaskManager.createTask(task1);
//        Epic epic1 = new Epic("Эпик 1", "Описание 1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(100), Duration.ofMinutes(100));
//        fileBackedTaskManager.createEpic(epic1);
//        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now().plusDays(1), Duration.ofMinutes(10), 2);
//        fileBackedTaskManager.createSubtask(subtask1);
//
//        //раскоментить тут, если нужно после запуска подгрузить задачи из файла
        fileBackedTaskManager.loadData(inputfile);
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
    }
}