package ru.yandex.practicum.ebogacheva.sprint3;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Integer, Task> listOfTasks;
    private HashMap<Integer, Subtask> listOfSubTasks;
    private HashMap<Integer, Epic> lisOfEpics;

    public Manager() {
        this.listOfTasks = new HashMap<>();
        this.listOfSubTasks = new HashMap<>();
        this.lisOfEpics = new HashMap<>();
    }

    public HashMap<Integer, Task> getListOfTasks() {
        return listOfTasks;
    }

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubTasks;
    }

    public HashMap<Integer, Epic> getListOfEpics() {
        return lisOfEpics;
    }

    public void printListOfTasks() {
        listOfTasks.entrySet().forEach(System.out::println);
    }

    public void printListOfSubTasks() {
        listOfSubTasks.entrySet().forEach(System.out::println);
    }

    public void printListOfEpics() {
        lisOfEpics.entrySet().forEach(System.out::println);
    }

    public void deleteAllTasks() {
        listOfTasks.clear();
    }

    public void deleteAllSubtasks() {
        listOfSubTasks.clear();
        lisOfEpics.forEach((key, value) -> value.status = Status.NEW);
    }

    public void deleteAllEpics() {
        lisOfEpics.clear();
        listOfSubTasks.clear();
    }

    public void deleteById(int id) {
        listOfTasks.remove(id);
        if (listOfSubTasks.containsKey(id)) {
            lisOfEpics.get(listOfSubTasks.get(id).epic.ID);

        }
        lisOfEpics.remove(id);
    }

    private void deleteTasksByType(TaskType type) {
        listOfTasks.entrySet().removeIf(entry -> entry.getValue().type == type);
    }

    public Task getTask(int id) {
        if (listOfTasks.containsKey(id)) return listOfTasks.get(id);
        if (listOfSubTasks.containsKey(id)) return listOfSubTasks.get(id);
        if (lisOfEpics.containsKey(id)) return lisOfEpics.get(id);
        return null;
    }

    public void createTask(Task task) {
        listOfTasks.put(task.ID, task);
        if (task.type == TaskType.SUB_TASK) {
            Subtask subtask = (Subtask) task;
            subtask.epic.subTasks.add(subtask);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.subTasks;
    }

    public void updateTask(Task task) {
        listOfTasks.put(task.ID, task);
        if (task.type == TaskType.SUB_TASK) {
            Subtask subtask = (Subtask) task;
            updateEpicStatus(subtask.epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        int countNEW = 0;
        int countDONE = 0;
        for (Subtask subtask : epic.subTasks) {
            if (subtask.status == Status.IN_PROGRESS) {
                epic.status = Status.IN_PROGRESS;
                break;
            }
            if (subtask.status == Status.NEW) {
                countNEW++;
                if (countNEW == epic.subTasks.size()) {
                    epic.status = Status.NEW;
                    break;
                }
            }
            if (subtask.status == Status.DONE) {
                countDONE++;
                if (countDONE == epic.subTasks.size()) {
                    epic.status = Status.DONE;
                    break;
                }
            }
        }
    }

}
