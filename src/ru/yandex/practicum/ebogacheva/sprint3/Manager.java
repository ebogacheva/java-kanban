package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Manager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private static final AtomicInteger idProvider = new AtomicInteger(0);

    public Manager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.forEach((key, value) -> value.status = Status.NEW);
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = (Epic) getTask(epicId);
            ArrayList<Integer> subIds = epic.getSubIds();
            subIds.remove(id);
            epic.setSubIds(subIds);
            epics.put(epicId, updateEpicStatus(epic));
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            subtasks.entrySet().removeIf(a -> a.getValue().getEpicId() == id);
            epics.remove(id);
        }
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        if (epics.containsKey(id)) return epics.get(id);
        return null;
    }

    public Task createTask(Task task) {
        int id = idProvider.incrementAndGet();
        task.setID(id);
        tasks.put(id, task);
        return task;
    }

    public int createSubtask(Subtask subtask) {
        int id = idProvider.incrementAndGet();
        subtask.setID(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubIds().add(id);
        epics.put(epic.getID(), updateEpicStatus(epic));
        return id;
    }

    public void createEpic(Epic epic) {
        int id = idProvider.incrementAndGet();
        epic.setID(id);
        epics.put(id, updateEpicStatus(epic));
    }


    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            epicSubs.add(subtasks.get(id));
        }
        return epicSubs;
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getID(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        Epic epicUpdated = updateEpicStatus(epic);
        epics.put(epic.getID(), epicUpdated);

    }

    public void updateEpic(Epic epic) {
        epics.put(updateEpicStatus(epic).getID(), epic);
    }

    private Epic updateEpicStatus(Epic epic) {

        if (epic.getSubIds().isEmpty()) {
            epic.status = Status.NEW;
        } else {
            int countNEW = 0;
            int countDONE = 0;
            ArrayList<Subtask> epicSubs = getEpicSubtasks(epic);
            for (Subtask subtask : epicSubs){
                if (subtask.status == Status.IN_PROGRESS) {
                    epic.status = Status.IN_PROGRESS;
                    break;
                }
                if (subtask.status == Status.NEW) {
                    countNEW++;
                    if (countNEW == epic.getSubIds().size()) {
                        epic.status = Status.NEW;
                        break;
                    }
                }
                if (subtask.status == Status.DONE) {
                    countDONE++;
                    if (countDONE == epic.getSubIds().size()) {
                        epic.status = Status.DONE;
                        break;
                    }
                }
            }
        }
        return epic;
    }

}
