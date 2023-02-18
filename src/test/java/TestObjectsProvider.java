import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.TaskManagerConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestObjectsProvider {

    public static Subtask getSubtaskForTesting(int number, int epicId) {
        return new Subtask("Test subtask #" + number, "Test subtask #" + number + " description", 60, epicId);
    }

    public static Epic getEpicForTesting(int number) {
        return new Epic("Test epic #" + number, "Test epic #" + number + " description");
    }

    public static Task getTaskForTesting(int number) {
        return new Task("Test task #" + number, "Test task #" + number + " description", 60);
    }

    public static LocalDateTime getDateTimeForTesting() {
        return LocalDateTime.parse("13.01.2023 12:00", TaskManagerConstants.DATE_TIME_FORMATTER);
    }

    public static LocalDateTime getDateTimeForTestingPlusMinutes(long minutes) {
        LocalDateTime dateTime = getDateTimeForTesting();
        return dateTime.plusMinutes(minutes);
    }

    public static List<Task> addThreeTasksToManager(TaskManager taskManager) {
        Task task1 = addTask(1, taskManager);
        Task task2 = addTask(2, taskManager);
        Task task3 = addTask(3, taskManager);
        return List.of(task1, task2, task3);
    }

    public static List<Epic> addThreeEpicsToManager(TaskManager taskManager) {
        Epic epic1 = addEpic(1, taskManager);
        Epic epic2 = addEpic(2, taskManager);
        Epic epic3 = addEpic(3, taskManager);
        return List.of(epic1, epic2, epic3);
    }

    public static List<Subtask> addThreeSubtasksToManager(TaskManager taskManager) {
        Epic epic1 = addEpic(1, taskManager);
        Epic epic2 = addEpic(2, taskManager);
        Subtask subtask1 = addSubtask(1, epic1.getId(), taskManager);
        Subtask subtask2 = addSubtask(2, epic1.getId(), taskManager);
        Subtask subtask3 = addSubtask(3, epic2.getId(), taskManager);
        return List.of(subtask1, subtask2, subtask3);
    }

    public static Epic addEpic(int number, TaskManager taskManager) {
        Epic epic = getEpicForTesting(number);
        taskManager.createEpic(epic);
        return epic;
    }

    public static Subtask addSubtask(int number, int id, TaskManager taskManager) {
        Subtask subtask = getSubtaskForTesting(number, id);
        taskManager.createSubtask(subtask);
        return subtask;
    }

    public static Subtask addSubtaskShifted(int number, int id, long shiftInMinutes, TaskManager taskManager) {
        Subtask subtask = getSubtaskForTesting(number, id);
        subtask.setStartDateTime(getDateTimeForTestingPlusMinutes(shiftInMinutes));
        taskManager.createSubtask(subtask);
        return subtask;
    }

    public static Task addTask(int number, TaskManager taskManager) {
        Task task = getTaskForTesting(number);
        taskManager.createTask(task);
        return task;
    }

    public static Task addTaskShifted(int number, long shiftInMinutes, TaskManager taskManager) {
        Task task = getTaskForTesting(number);
        task.setStartDateTime(getDateTimeForTestingPlusMinutes(shiftInMinutes));
        taskManager.createTask(task);
        return task;
    }

    public static List<String> getStringsFromFile(File file) {
        try (Stream<String> lines = Files.lines(Paths.get(file.getPath()))) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager getFileBackedManager() {
        return FileBackedTaskManager.loadFromFile(new File("test1.txt"));
    }

    public static void createManagerWithTestData() {
        TaskManager taskManager = Managers.getFileBackedManager("test-httpServer.txt");
        List<Task> tasks=TestObjectsProvider.addThreeTasksToManager(taskManager);
        List<Epic> epics=TestObjectsProvider.addThreeEpicsToManager(taskManager);
        Subtask subtask1=TestObjectsProvider.addSubtask(1, epics.get(0).getId(), taskManager);
        Subtask subtask2=TestObjectsProvider.addSubtask(2, epics.get(1).getId(), taskManager);
        Subtask subtask3=TestObjectsProvider.addSubtask(3, epics.get(2).getId(), taskManager);
        Subtask subtask4=TestObjectsProvider.addSubtask(4, epics.get(2).getId(), taskManager);
        Task task1=tasks.get(0);
        task1.setStartDateTime(LocalDateTime.parse("01.01.2023 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        task1.setDuration(Duration.ofMinutes(145));
        taskManager.updateTask(task1);
        Task task2=tasks.get(1);
        task2.setStartDateTime(LocalDateTime.parse("01.03.2022 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        task2.setDuration(Duration.ofMinutes(77));
        taskManager.updateTask(task2);
        Task task3=tasks.get(2);
        task3.setStartDateTime(LocalDateTime.parse("05.12.2021 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        task3.setDuration(Duration.ofMinutes(1000));
        taskManager.updateTask(task3);
        subtask1.setDuration(Duration.ofMinutes(777));
        subtask1.setStartDateTime(LocalDateTime.parse("01.01.2004 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        subtask2.setDuration(Duration.ofMinutes(7));
        subtask2.setStartDateTime(LocalDateTime.parse("02.01.2005 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        subtask3.setDuration(Duration.ofMinutes(333));
        subtask3.setStartDateTime(LocalDateTime.parse("02.01.2010 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        subtask4.setDuration(Duration.ofMinutes(10000));
        subtask4.setStartDateTime(LocalDateTime.parse("02.01.2030 12:00", TaskManagerConstants.DATE_TIME_FORMATTER));
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask4);
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epics.get(0).getId());
        taskManager.getSubtask(subtask4.getId());
        taskManager.getSubtask(subtask2.getId());
    }

}
