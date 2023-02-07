package ru.yandex.practicum.ebogacheva.tracker.managers;

import ru.yandex.practicum.ebogacheva.tracker.exceptions.ManagerSaveException;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            fileWriter.write(historyToString(this.historyManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public void save(File file) throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            List<Task> saveToFile = new ArrayList<>(this.tasks.values());
            saveToFile.addAll(this.epics.values());
            saveToFile.addAll(this.subtasks.values());
            saveToFile.sort(Comparator.comparingInt(Task::getId));
            for (Task task : saveToFile) {
                fileWriter.write(task.toFileString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(this.historyManager.getHistory()));
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
        if(loadedFromFile.isEmpty()) {
            return taskManager;
        }
        for (String line : loadedFromFile) {
            if (line.isBlank()) {
                continue;
            }
            Task task = fromString(line);
            if (task == null) {
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
                continue;
            }
            if (task.getType().equals(TaskType.TASK)) {
                taskManager.tasks.put(task.getId(), task);
                taskManager.sortedTasks.add(task);
            } else if (task.getType().equals(TaskType.EPIC)) {
                taskManager.epics.put(task.getId(), (Epic) task);
            } else if (task.getType().equals(TaskType.SUBTASK)) {
                Subtask subtask = (Subtask) task;
                taskManager.subtasks.put(task.getId(), subtask);
                taskManager.sortedTasks.add(subtask);
                int epicId = subtask.getEpicId();
                Epic epic = taskManager.epics.get(epicId);
                List<Integer> subIds = epic.getSubIds();
                subIds.add(subtask.getId());
            }
        }
        return taskManager;
    }

    public static String historyToString(List<Task> history) {
        StringBuilder historySb = new StringBuilder();
        if (!history.isEmpty()) {
            for (Task task : history) {
                historySb.append(task.getId()).append(",");
            }
            historySb.deleteCharAt(historySb.length() - 1);
        }
        return historySb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        String[] historyInString = value.split(",");
        List<Integer> history =  new ArrayList<>();
        for (String taskId : historyInString) {
            try {
                history.add(Integer.parseInt(taskId));
            } catch (NumberFormatException ignored) {
            }
        }
        return history;
    }

   private static Task fromString(String value) {
        String[] taskInString = value.split(",");
        if (taskInString.length < 7) {
            return null;
        }
        if (taskInString[1].equals(TaskType.TASK.name())) {
            return new Task(Integer.parseInt(taskInString[0]),
                    taskInString[2],
                    taskInString[4],
                    Status.valueOf(taskInString[3]),
                    Duration.parse(taskInString[5]).toMinutes(),
                    getTaskDateTimeFromString(taskInString[6]));
        }
        if (taskInString[1].equals(TaskType.EPIC.name())) {
            return new Epic(Integer.parseInt(taskInString[0]),
                    taskInString[2],
                    taskInString[4],
                    Status.valueOf(taskInString[3]),
                    Duration.parse(taskInString[5]).toMinutes(),
                    getTaskDateTimeFromString(taskInString[6]),
                    new ArrayList<>());
        }
        if (taskInString[1].equals(TaskType.SUBTASK.name())) {
            return new Subtask(Integer.parseInt(taskInString[0]),
                    taskInString[2],
                    taskInString[4],
                    Status.valueOf(taskInString[3]),
                    Duration.parse(taskInString[5]).toMinutes(),
                    getTaskDateTimeFromString(taskInString[6]),
                    Integer.parseInt(taskInString[7]));
        }
        return null;
    }

    public static LocalDateTime getTaskDateTimeFromString(String input) {
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime outputDateTime = null;
        try {
            outputDateTime = LocalDateTime.parse(input, formatterDateTime);
        } catch (DateTimeParseException ignored) {
        }
        return outputDateTime;
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
