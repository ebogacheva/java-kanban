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

    public List<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    public List<Epic> getEpics() {
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

    public void deleteById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = subtask.getEpic();
            epic.getSubTasks().remove(subtask);
            subtasks.remove(id);
        } else if (epics.containsKey(id)) {
            Map<Integer, Subtask> map = subtasks
                    .entrySet().stream().filter(a -> a.getValue().getEpic().getID() != id)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            subtasks = new HashMap<>(map);
            epics.remove(id);
        }
    }
    public Task getTask(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        if (epics.containsKey(id)) return epics.get(id);
        return null;
    }

    public void createTask(Task task) {
        int id = idProvider.incrementAndGet();
        task.setID(id);
        tasks.put(id, task);
    }

    public void createSubtask(Subtask subtask) {
        int id = idProvider.incrementAndGet();
        subtask.setID(id);
        subtasks.put(id, subtask);
        Epic epic = subtask.getEpic();
        epic.getSubTasks().put(subtask.getID(), subtask);
        epics.put(epic.getID(), updateEpicStatus(epic));
    }

    public void createEpic(Epic epic) {
        int id = idProvider.incrementAndGet();
        epic.setID(id);
        epics.put(id, epic);
    }


    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return new ArrayList<>(epic.getSubTasks().values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getID(), subtask);
        subtask.getEpic().getSubTasks().put(subtask.getID(), subtask);
        Epic epic = updateEpicStatus(subtask.getEpic());
        epics.put(epic.getID(), epic);

    }

    public void updateEpic(Epic epic) {
        epics.put(updateEpicStatus(epic).getID(), epic);
    }

    private Epic updateEpicStatus(Epic epic) {
        int countNEW = 0;
        int countDONE = 0;
        ArrayList<Subtask> list = new ArrayList<>(epic.getSubTasks().values());
        for (Subtask subtask : list) {
            if (subtask.status == Status.IN_PROGRESS) {
                epic.status = Status.IN_PROGRESS;
                break;
            }
            if (subtask.status == Status.NEW) {
                countNEW++;
                if (countNEW == epic.getSubTasks().size()) {
                    epic.status = Status.NEW;
                    break;
                }
            }
            if (subtask.status == Status.DONE) {
                countDONE++;
                if (countDONE == epic.getSubTasks().size()) {
                    epic.status = Status.DONE;
                    break;
                }
            }
        }
        return epic;
    }

}
