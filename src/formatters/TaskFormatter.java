package formatters;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskmanager.FileBackedTaskManager;
import taskmanager.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class TaskFormatter {

    private TaskFormatter(){

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            while (br.ready()) {
                Task task = fromString(br.readLine());
                if (task != null) {
                    if (task instanceof Epic) {
                        manager.addEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.addSubtask((Subtask) task);
                    } else {
                        manager.addTask(task);
                    }
                }

            }
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        if (TaskType.EPIC.name().equals(parts[1])) {
            return new Epic(Integer.parseInt(parts[0]), parts[2], parts[4], new ArrayList<>());
        } else if (TaskType.SUBTASK.name().equals(parts[1])) {
            return new Subtask(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]), Integer.parseInt(parts[5]));
        } else if (TaskType.TASK.name().equals(parts[1])) {
            return new Task(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
        }
        return null;
    }
}
