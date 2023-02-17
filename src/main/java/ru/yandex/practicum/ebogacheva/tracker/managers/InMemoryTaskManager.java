package ru.yandex.practicum.ebogacheva.tracker.managers;

import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> sortedTasks;
    protected static final AtomicInteger idProvider = new AtomicInteger(0);

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.sortedTasks = new TreeSet<>(this::getTaskComparator);
        this.historyManager = Managers.getDefaultHistory();
    }

    protected int getTaskComparator(Task task1, Task task2) {
        if (task1 == null || task2 == null) {
            return -1;
        }
        if (task1.getStartDateTime() != null && task2.getStartDateTime() != null) {
            return task1.getStartDateTime().compareTo(task2.getStartDateTime());
        } else if (task1.getStartDateTime() != null) {
            return -1;
        } else if (task2.getStartDateTime() != null) {
            return 1;
        } else {
            return task1.getId() - task2.getId();
        }
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
        this.sortedTasks.removeIf(x -> x.getClass().equals(Task.class));
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : this.subtasks.values()) {
            this.historyManager.remove(subtask.getId());
        }
        this.subtasks.clear();
        this.epics.forEach((key, value) -> value.clearSubTasks());
        this.sortedTasks.removeIf(x -> x.getClass().equals(Subtask.class));
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
        Task task = this.tasks.get(id);
        this.tasks.remove(id);
        this.historyManager.remove(id);
        this.sortedTasks.remove(task);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!this.subtasks.containsKey(id))  {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubTask(id);
        updateEpicStatusStartDuration(epic);

        Subtask subtask = subtasks.get(id);
        this.subtasks.remove(id);
        this.historyManager.remove(id);
        this.sortedTasks.remove(subtask);
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
    public void createTask(Task task) {
        int id = idProvider.incrementAndGet();
        task.setId(id);
        Task newTask = new Task(task);
        if (checkIfTimeNotAvailable(newTask)) {
            newTask.setStartDateTime(null);
        }
        tasks.put(id, newTask);
        sortedTasks.add(newTask);
    }

    protected boolean checkIfTimeNotAvailable(Task taskToCheck) {
        LocalDateTime start = taskToCheck.getStartDateTime();
        LocalDateTime end = taskToCheck.getEndDateTime();
        if (start == null) {
            return false;
        }
        List<Task> prioritized = getPrioritizedTasks();
        for (Task task : prioritized) {
            if (task.getStartDateTime() != null) {
                if (task.getStartDateTime().isBefore(start) && task.getEndDateTime().isAfter(start)) {
                    return true;
                }
                if (task.getStartDateTime().isAfter(start) && task.getStartDateTime().isBefore(end)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int id = idProvider.incrementAndGet();
        subtask.setId(id);
        Subtask newSubtask = new Subtask(subtask);
        if (checkIfTimeNotAvailable(newSubtask)) {
            newSubtask.setStartDateTime(null);
        }
        subtasks.put(id, newSubtask);
        sortedTasks.add(newSubtask);

        Epic epic = epics.get(newSubtask.getEpicId());
        epic.addSubTask(id);
        updateEpicStatusStartDuration(epic);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = idProvider.incrementAndGet();
        epic.setId(id);
        Epic newEpic = new Epic(epic);
        epics.put(id, updateEpicStatusStartDuration(newEpic));
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            Subtask subtaskInside = subtasks.get(id);
            if (subtaskInside == null) {
                continue;
            }
            Subtask subtaskOutput = new Subtask(subtaskInside);
            epicSubs.add(subtaskOutput);
            historyManager.add(subtaskInside);
        }
        return epicSubs;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())){
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // если такой задачи нет, то добавляем
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
            updateEpicStatusStartDuration(oldEpic);
        }

        // заменяем
        subtasks.put(subtask.getId(), subtask);

        // обновляем новый epic
        Epic epic = epics.get(subtask.getEpicId());
        if (!isSameEpic) {
            epic.addSubTask(subtask.getId());
        }
        updateEpicStatusStartDuration(epic);

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
            int subId = subtask.getId();
            subtasks.get(subId).resetEpicId();
        }

        // заменить epic
        epics.put(id, epic);

        // присоединить подзадачи к epic, если они ещё не присоединены
        for(Subtask subtask : getEpicSubtasks(epic)) {
            if (subtask.isAttachedToEpic()) {
                int oldEpicIdOfSubTask = subtask.getEpicId();
                // отсоединить от какого-то старого эпика
                if (oldEpicIdOfSubTask != id) {
                    Epic subTaskOldEpic = epics.get(oldEpicIdOfSubTask);
                    subTaskOldEpic.removeSubTask(subtask.getId());
                    updateEpicStatusStartDuration(subTaskOldEpic);
                }
            }
            subtasks.get(subtask.getId()).setEpicId(id);
        }

        // удалить все subtask которые больше не привязаны к какому-либо epic
        this.subtasks.entrySet().removeIf(entry -> !entry.getValue().isAttachedToEpic());
        for (Subtask subtask : subtasks.values()) {
            if (epics.get(id).getSubIds().contains(subtask.getId())) {
            } else {
                subtasks.remove(subtask.getId());
            }
        }
        epics.get(id).getSubIds().removeIf(entry -> !subtasks.containsKey(entry));
        updateEpicStatusStartDuration(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Epic updateEpicStatusStartDuration(Epic epic) {
        Status newEpicStatus = Status.NEW;
        Duration newEpicDuration = Duration.ZERO;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (epic.getSubIds().isEmpty()) {
            epic.setStatus(newEpicStatus);
            epic.setDuration(newEpicDuration);
            epic.setStartDateTime(start);
            epic.setEndDateTime(end);
            return epic;
        } 

        int countNEW = 0;
        int countDONE = 0;
        List<Subtask> epicSubs = new ArrayList<>();
        for (Integer id : epic.getSubIds()) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                epicSubs.add(subtask);
            }
        }
        for (Subtask subtask : epicSubs){
            newEpicDuration = newEpicDuration.plus(subtask.getDuration());
            if (subtask.getStartDateTime() != null) {
                if (start != null) {
                    start = subtask.getStartDateTime().isBefore(start) ? subtask.getStartDateTime() : start;
                } else {
                    start = subtask.getStartDateTime();
                }
            }
            if (subtask.getEndDateTime() != null) {
                if (end != null) {
                    end = subtask.getEndDateTime().isAfter(end) ? subtask.getEndDateTime() : end;
                } else {
                    end = subtask.getEndDateTime();
                }
            }
            if (subtask.getStatus()== Status.IN_PROGRESS) {
                newEpicStatus = Status.IN_PROGRESS;
            } 
            
            if (subtask.getStatus() == Status.NEW) {
                countNEW++;
                if (countDONE > 0) {
                    newEpicStatus = Status.IN_PROGRESS;
                }
            } else if (subtask.getStatus() == Status.DONE) {
                countDONE++;
                if (countNEW > 0) {
                    newEpicStatus = Status.IN_PROGRESS;
                }
            }
        }

        int N = epic.getSubIds().size();
        if (countNEW == N) {
            newEpicStatus = Status.NEW;
        } else if (countDONE == N) {
            newEpicStatus = Status.DONE;
        }

        epic.setStatus(newEpicStatus);
        epic.setDuration(newEpicDuration);
        epic.setStartDateTime(start);
        epic.setEndDateTime(end);
        return epic;
    }

}
