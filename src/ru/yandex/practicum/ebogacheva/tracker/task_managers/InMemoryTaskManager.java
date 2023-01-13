package ru.yandex.practicum.ebogacheva.tracker.task_managers;

import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    private static final AtomicInteger idProvider = new AtomicInteger(0);

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> taskOut = new ArrayList<>();
        for (Task taskInside : this.tasks.values()) {
            taskOut.add(new Task(taskInside));
            historyManager.add(taskInside);
        }
        return taskOut;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtaskOut = new ArrayList<>();
        for (Subtask subtaskInside : this.subtasks.values()) {
            subtaskOut.add(new Subtask(subtaskInside));
            historyManager.add(subtaskInside);
        }
        return subtaskOut;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epicOut = new ArrayList<>();
        for (Epic epicInside : this.epics.values()) {
            epicOut.add(new Epic(epicInside));
            historyManager.add(epicInside);
        }
        return epicOut;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : this.tasks.values()) {
            this.historyManager.remove(task.getId());
        }
        this.tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : this.subtasks.values()) {
            this.historyManager.remove(subtask.getId());
        }
        this.subtasks.clear();
        this.epics.forEach((key, value) -> {
            value.clearSubTasks();
        });
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : this.epics.values()) {
            this.historyManager.remove(epic.getId());
        }
        this.epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public Task getTask(int id) {
        Task taskOut = null;
        if (tasks.containsKey(id)) {
            Task taskInside = tasks.get(id);
            taskOut = new Task(taskInside);
            historyManager.add(taskInside);
        }
        return taskOut;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epicOut = null;
        if (epics.containsKey(id)) {
            Epic epicInside = epics.get(id);
            epicOut = new Epic(epicInside);
            historyManager.add(epicInside);
        }
        return epicOut;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtaskOut = null;
        if (subtasks.containsKey(id)) {
            Subtask subtaskInside = subtasks.get(id);
            subtaskOut = new Subtask(subtaskInside);
            historyManager.add(subtaskInside);
        }
        return subtaskOut;
    }

    @Override
    public void deleteTaskById(int id) {
        this.tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!this.subtasks.containsKey(id))  {
            return;
        }

        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubTask(id);
        updateEpicStatus(epic);

        this.subtasks.remove(id);
        this.historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        List<Subtask> toRemove = subtasks
                .values()
                .stream()
                .filter(a -> a.getEpicId() == id)
                .collect(Collectors.toList());
        for (Subtask subtaskToRemove : toRemove) {
            int idToRemove = subtaskToRemove.getId();
            this.subtasks.remove(idToRemove);
            this.historyManager.remove(idToRemove);
        }
        this.historyManager.remove(id);
        this.epics.remove(id);
    }

    @Override
    public Task createTask(Task task) {
        int id = idProvider.incrementAndGet();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = idProvider.incrementAndGet();
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubTask(id);
        updateEpicStatus(epic);

        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = idProvider.incrementAndGet();
        epic.setId(id);
        epics.put(id, updateEpicStatus(epic));
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            Subtask subtask = subtasks.get(id);
            epicSubs.add(subtask);
            historyManager.add(subtask);
        }
        return epicSubs;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // если такой задачи нету, то добавляем
        if (!subtasks.containsKey(subtask.getId())) {
            createSubtask(subtask);
            return;
        }

        // для старой задачи удаляем из старого epic
        Subtask oldSubtask = subtasks.get(subtask.getId());
        boolean isSameEpic = oldSubtask.getEpicId() == subtask.getEpicId();
        if (!isSameEpic) {
            Epic oldEpic = epics.get(oldSubtask.getEpicId());
            oldEpic.removeSubTask(subtask.getId());
            updateEpicStatus(oldEpic);
        }

        // заменяем
        subtasks.put(subtask.getId(), subtask);

        // обновляем новый epic
        Epic epic = epics.get(subtask.getEpicId());
        if (!isSameEpic) {
            epic.addSubTask(subtask.getId());
        }
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        
        int id = epic.getId();
        
        // если не существует - просто добавить
        if (!epics.containsKey(id)) {
            epics.put(id, epic);
            return;
        }

        // отсоединить все подзадачи от старого Epic
        for(Subtask subtask : getEpicSubtasks(epics.get(id))) {
            subtask.resetEpicId();
        }

        // заменить epic
        epics.put(id, epic);

        // присоединить подзадачи к epic, если они ещё не присоединены
        for(Subtask subtask : getEpicSubtasks(epic)) {
            if (subtask.isAttachedToEpic()) {
                int oldEpicIdOfSubTask = subtask.getEpicId();
                // отсоединить от какого-то старого епика
                if (oldEpicIdOfSubTask != id) {
                    Epic subTaskOldEpic = epics.get(oldEpicIdOfSubTask);
                    subTaskOldEpic.removeSubTask(subtask.getId());
                    updateEpicStatus(subTaskOldEpic);    
                }
            }
            subtask.setEpicId(id);
        }

        // удалить все subtask которые больше не привязаны к какому либо epic
        this.subtasks.entrySet().removeIf(entry -> !entry.getValue().isAttachedToEpic());
        
        updateEpicStatus(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Status getEpicStatusBySubtasks(Epic epic) {
        if (epic.getSubIds().isEmpty()) {
            return Status.NEW;
        } 

        int countNEW = 0;
        int countDONE = 0;
        
        List<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            Subtask subtask = subtasks.get(id);
            epicSubs.add(subtask);
        }
        for (Subtask subtask : epicSubs){
            if (subtask.getStatus()== Status.IN_PROGRESS) {
                return Status.IN_PROGRESS;
            } 
            
            if (subtask.getStatus() == Status.NEW) {
                countNEW++;
                if (countDONE > 0) {
                    return Status.IN_PROGRESS;
                }
            } else if (subtask.getStatus() == Status.DONE) {
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
        epic.setStatus(getEpicStatusBySubtasks(epic));
        return epic;
    }
}
