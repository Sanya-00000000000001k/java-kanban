package com.yandex.app.logic;

import com.yandex.app.entities.*;
import com.yandex.app.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static void main(String[] args) {
        File inputfile = new File("./resources/test.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(inputfile);

//        Task task1 = new Task("Задача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(100));
//        fileBackedTaskManager.createTask(task1);
//        Epic epic1 = new Epic("Эпик 1", "Описание 1");
//        fileBackedTaskManager.createEpic(epic1);
//        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(10), 2);
//        fileBackedTaskManager.createSubtask(subtask1);
//
//        //раскоментить тут, если нужно после запуска подгрузить задачи из файла
        fileBackedTaskManager.loadData(inputfile);
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
    }

    private final File inputfile;
    private final String defaultTitle = "id,type,name,status,description,startTime,endTime,duration,epic\n";

    public FileBackedTaskManager(File inputfile) {
        this.inputfile = inputfile;
    }

    protected void save() {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(inputfile))) {
            addTasksToFile();
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void addTasksToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputfile))) {
            bw.write(defaultTitle);
            for (Task task : getTasks()) {
                bw.write(toString(task));
            }
            for (Epic epic : getEpics()) {
                bw.write(toString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    protected void loadData(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",");
                int currentId = Integer.parseInt(fields[0]);
                StatusesList type = StatusesList.valueOf(fields[1].toUpperCase());
                String name = fields[2];
                StatusOfTask status = StatusOfTask.valueOf(fields[3].toUpperCase());
                String description = fields[4];
                Integer epicId = null;
                LocalDateTime startTime = LocalDateTime.parse(fields[5], formatter);
                LocalDateTime endTime = LocalDateTime.parse(fields[6], formatter);
                Duration duration = Duration.ofMinutes(Integer.parseInt(fields[7]));

                if (type == StatusesList.SUBTASK) {
                    epicId = Integer.parseInt(fields[8]);
                }

                if (currentId > this.id) {
                    this.id = currentId;
                }

                switch (type) {
                    case TASK:
                        Task task = new Task(name, description, status, startTime, duration);
                        task.setTaskId(currentId);
                        tasks.put(currentId, task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(name, description, startTime, endTime, duration);
                        epic.setTaskId(currentId);
                        epics.put(currentId, epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);
                        subtask.setTaskId(currentId);
                        subtasks.put(currentId, subtask);
                        epics.get(epicId).getSubtasksIds().add(currentId);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + file.getAbsolutePath(), e);
        }
    }

//    private static Task fromString(String value) {
//        String[] splitValue = value.split(",");
//        int id = Integer.parseInt(splitValue[0]);
//        StatusOfTask status = StatusOfTask.valueOf(splitValue[3]);
//        String taskName = splitValue[2];
//        StatusesList statusesList = StatusesList.valueOf(splitValue[1]);
//        String description = splitValue[4];
//        LocalDateTime startTime = LocalDateTime.parse(splitValue[5]);
//        LocalDateTime endTime = LocalDateTime.parse(splitValue[6]);
//        Duration duration = Duration.ofMinutes(Integer.parseInt(splitValue[7]));
//
//        switch (statusesList) {
//            case TASK:
//                Task task = new Task(taskName, description, status, startTime, duration);
//                task.setTaskId(id);
//                return task;
//            case EPIC:
//                Epic epic = new Epic(taskName, description, startTime, duration);
//                epic.setTaskId(id);
//                return epic;
//            case SUBTASK:
//                int epicId = Integer.parseInt(splitValue[8]);
//                Subtask subtask = new Subtask(taskName, description, status, startTime, duration, epicId);
//                subtask.setTaskId(id);
//                return subtask;
//        }
//        return null;
//    }

    private String toString(Subtask subtask) {
        return subtask.getTaskId() +
                "," + subtask.getType() +
                "," + subtask.getTaskName() +
                "," + subtask.getStatus() +
                "," + subtask.getDescription() +
                "," + subtask.getStartTimeToString() +
                "," + subtask.getEndTimeToString() +
                "," + subtask.getDuration().toMinutes() +
                "," + subtask.getEpicId() +
                "\n";
    }

    private String toString(Task task) {
        return task.getTaskId() +
                "," + task.getType() +
                "," + task.getTaskName() +
                "," + task.getStatus() +
                "," + task.getDescription() +
                "," + task.getStartTimeToString() +
                "," + task.getEndTimeToString() +
                "," + (task.getDuration() != null ? task.getDuration().toMinutes() : 0) +
                "\n";

    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateTask(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}
