package ru.yandex.practicum.ebogacheva.tracker.task_managers;

import ru.yandex.practicum.ebogacheva.tracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager{

    private final String fileName;

    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
    }

    public void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(this.fileName, StandardCharsets.UTF_8, true)) {
            List<Task> saveToFile = new ArrayList<>(this.tasks.values());
            saveToFile.addAll(this.epics.values());
            saveToFile.addAll(this.subtasks.values());
            saveToFile.sort(Comparator.comparingInt(Task::getId).reversed());
            for (Task task : saveToFile) {
                fileWriter.write(task.toFileString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics =  super.getEpics();
        save();
        return epics;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;

    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public Task createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
    }

    @Override
    public Epic createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> subtasks = super.getEpicSubtasks(epic);
        save();
        return subtasks;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

}
