package ru.yandex.practicum.ebogacheva.tracker.tasks;

import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public void deleteAllTasks() {
        this.tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        this.subtasks.clear();
        this.epics.forEach((key, value) -> {
            value.clearSubTasks();
        });
    }

    @Override
    public void deleteAllEpics() {
        this.epics.clear();
        this.subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = null;
        if (tasks.containsKey(id)) {
            task = tasks.get(id);
        }
        if (subtasks.containsKey(id)) {
            task = subtasks.get(id);
        }
        if (epics.containsKey(id)) {
            task = epics.get(id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        this.tasks.remove(id);
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
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }

        subtasks.entrySet().removeIf(a -> a.getValue().getEpicId() == id);
        epics.remove(id);
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
            epicSubs.add(subtasks.get(id));
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
