package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Manager {

    private HashMap<Integer, Task> listOfTasks;

    public Manager() {
        this.listOfTasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getListOfTasks() {
        return getListByType(TaskType.TASK);
    }

    public HashMap<Integer, Task> getListOfSubtasks() {
        return getListByType(TaskType.SUB_TASK);
    }

    public HashMap<Integer, Task> getListOfEpics() {
        return getListByType(TaskType.EPIC);
    }

    private HashMap<Integer, Task> getListByType(TaskType type) {
        Map<Integer, Task> map = listOfTasks
                .entrySet()
                .stream()
                .filter(a -> a.getValue().type == type)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new HashMap<>(map);
    }

    public void printListOfTasks() {
        printList(TaskType.TASK);
    }

    public void printListOfSubtasks() {
        printList(TaskType.SUB_TASK);
    }

    public void printListOfEpics() {
        printList(TaskType.EPIC);
    }

    public void printList(TaskType type) {
        getListByType(type).forEach((key, value) -> System.out.println(key + " " + value));
    }

    public void deleteAllTasks() {
        deleteTasksByType(TaskType.TASK);
    }

    public void deleteAllSubtasks() {
        deleteTasksByType(TaskType.SUB_TASK);
        getListByType(TaskType.EPIC).forEach((key, value) -> value.status = Status.NEW);
    }

    public void deleteAllEpics() {
        deleteTasksByType(TaskType.EPIC);
    }

    public void deleteById(int id) {
        listOfTasks.remove(id);
    }

    private void deleteTasksByType(TaskType type) {
        listOfTasks.entrySet().removeIf(entry -> entry.getValue().type == type);
    }

    public Task getTask(int id) {
        if (listOfTasks.containsKey(id)) return listOfTasks.get(id);
        return null;
    }

    public void createTask(Task task) {
        listOfTasks.put(task.ID, task);
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.subTasks;
    }

    public void updateTask(Task task) {
        listOfTasks.put(task.ID, task);
        if (task.type == TaskType.SUB_TASK) {
            Subtask subtask = (Subtask) task;
            updateEpicStatus(subtask.epicID);
        }
    }

    private void updateEpicStatus(int epicID) {
        Epic epic = (Epic) getTask(epicID);
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
