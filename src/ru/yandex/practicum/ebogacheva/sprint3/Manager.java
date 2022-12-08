package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        this.tasks.clear();
    }

    public void deleteAllSubtasks() {
        this.subtasks.clear();
        this.epics.forEach((key, value) -> {
            value.clearSubTasks();
        });
    }

    public void deleteAllEpics() {
        this.epics.clear();
        this.subtasks.clear();
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public void deleteTaskById(int id) {
        this.tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        if (!this.subtasks.containsKey(id))  {
            return;
        }

        int epicId = subtasks.get(id).getEpicId();
        Epic epic = (Epic) getTask(epicId);
        epic.removeSubTask(id);
        updateEpicStatus(epic);

        this.subtasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }

        subtasks.entrySet().removeIf(a -> a.getValue().getEpicId() == id);
        epics.remove(id);
    }

    public Task createTask(Task task) {
        int id = idProvider.incrementAndGet();
        task.setID(id);
        tasks.put(id, task);
        return task;
    }

    public Subtask createSubtask(Subtask subtask) {
        int id = idProvider.incrementAndGet();
        subtask.setID(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTask(id);
        updateEpicStatus(epic);

        return subtask;
    }

    public Epic createEpic(Epic epic) {
        int id = idProvider.incrementAndGet();
        epic.setID(id);
        epics.put(id, updateEpicStatus(epic));
        return epic;
    }

    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            epicSubs.add(subtasks.get(id));
        }
        return epicSubs;
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void updateSubtask(Subtask subtask) {
        
        // если такой задачи нету, то добавляем
        if (!subtasks.containsKey(subtask.getID())) {
            createSubtask(subtask);
            return;
        }

        // для старой задачи удаляем из старого epic
        Subtask oldSubtask = subtasks.get(subtask.getID());
        Epic oldEpic = epics.get(oldSubtask.getEpicId());
        oldEpic.removeSubTask(subtask.getID());
        updateEpicStatus(oldEpic);

        // заменяем
        subtasks.put(subtask.getID(), subtask);

        // обновляем новый epic
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTask(subtask.getID());
        updateEpicStatus(epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getID(), epic);
        updateEpicStatus(epic);
    }

    private Status getEpicStatusBySubtasks(Epic epic) {
        if (epic.getSubIds().isEmpty()) {
            return Status.NEW;
        } 

        int countNEW = 0;
        int countDONE = 0;
        
        List<Subtask> epicSubs = getEpicSubtasks(epic);
        for (Subtask subtask : epicSubs){
            if (subtask.status == Status.IN_PROGRESS) {
                return Status.IN_PROGRESS;
            } 
            
            if (subtask.status == Status.NEW) {
                countNEW++;
                if (countDONE > 0) {
                    return Status.IN_PROGRESS;
                }
            } else if (subtask.status == Status.DONE) {
                countDONE++;
                if (countNEW > 0) {
                    return Status.IN_PROGRESS;
                }
            }
        }

        int N = epic.getSubIds().size();
        if (countNEW == N) {
            return Status.NEW;
        } else if (countDONE == N) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

    private Epic updateEpicStatus(Epic epic) {
        epic.status = getEpicStatusBySubtasks(epic);
        return epic;
    }
}
