package ru.yandex.practicum.ebogacheva.tracker.task_managers;

import ru.yandex.practicum.ebogacheva.tracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager{

    private final String fileName;

    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
    }

    private void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(this.fileName, StandardCharsets.UTF_8)) {
            List<Task> saveToFile = new ArrayList<>(this.tasks.values());
            saveToFile.addAll(this.epics.values());
            saveToFile.addAll(this.subtasks.values());
            saveToFile.sort(Comparator.comparingInt(Task::getId));
            for (Task task : saveToFile) {
                fileWriter.write(task.toFileString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(this.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        List<String> loadedFromFile;
        try (Stream<String> lines = Files.lines(Paths.get(file.getPath()))) {
            loadedFromFile = lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.getPath());

        if (!loadedFromFile.isEmpty()) {
            for (String line : loadedFromFile) {
                if (!line.isBlank()) {
                    String[] separatedLine=line.split(",");
                    if (separatedLine[1].equals(TaskType.TASK.name())) {
                        Task task = fromString(line);
                        if (task != null) {
                            taskManager.tasks.put(task.getId(), task);
                        }
                    } else if (separatedLine[1].equals(TaskType.EPIC.name())) {
                        Epic epic = (Epic) fromString(line);
                        if (epic != null) {
                            taskManager.epics.put(epic.getId(), epic);
                        }
                    } else if (separatedLine[1].equals(TaskType.SUBTASK.name())) {
                        Subtask subtask = (Subtask) fromString(line);
                        if (subtask != null) {
                            taskManager.subtasks.put(subtask.getId(), subtask);
                            int epicId = Integer.parseInt(separatedLine[5]);
                            Epic epic = taskManager.epics.get(epicId);
                            List<Integer> subIds = epic.getSubIds();
                            subIds.add(subtask.getId());
                        }
                    } else {
                        List<Integer> history = historyFromString(line);
                        for (Integer taskId : history) {
                            if (taskManager.tasks.containsKey(taskId)) {
                                taskManager.historyManager.add(taskManager.tasks.get(taskId));
                            } else if (taskManager.epics.containsKey(taskId)) {
                                taskManager.historyManager.add(taskManager.epics.get(taskId));
                            } else if (taskManager.subtasks.containsKey(taskId)) {
                                taskManager.historyManager.add(taskManager.subtasks.get(taskId));
                            }
                        }
                    }
                }
            }
        }
        return taskManager;
    }

    private static String historyToString(HistoryManager historyManager) {
        StringBuilder historySb = new StringBuilder();
        if (!historyManager.getHistory().isEmpty()) {
            for (Task task : historyManager.getHistory()) {
                historySb.append(task.getId()).append(",");
            }
            historySb.deleteCharAt(historySb.length() - 1);
        }
        return historySb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        String[] historyInString = value.split(",");
        List<Integer> history =  new ArrayList<>();
        for (String taskId : historyInString) {
            history.add(Integer.parseInt(taskId));
        }
        return history;
    }

    private static Task fromString(String value) {
        String[] taskInString = value.split(",");
        if (taskInString[1].equals(TaskType.TASK.name())) {
            return new Task(Integer.parseInt(taskInString[0]), taskInString[2], taskInString[4], Status.valueOf(taskInString[3]));
        }
        if (taskInString[1].equals(TaskType.EPIC.name())) {
            return new Epic(Integer.parseInt(taskInString[0]), taskInString[2], taskInString[4], Status.valueOf(taskInString[3]), new ArrayList<>());
        }
        if (taskInString[1].equals(TaskType.SUBTASK.name())) {
            return new Subtask(Integer.parseInt(taskInString[0]), taskInString[2], taskInString[4], Status.valueOf(taskInString[3]), Integer.parseInt(taskInString[5]));
        }
        return null;
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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
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
