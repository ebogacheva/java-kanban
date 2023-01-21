package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.TaskType;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;

import java.io.File;
import java.util.List;

public class FileBackedTaskManagerTests {

    public static void testFileBackedTaskManager (TaskManager taskManager) {

        TestUtils.printTitle("Спринт 6. Тестирование FileBackedTaskManager");
        TestUtils.printTitle("Тест 1: Заполнение задачами");
        // Создать 2 задачи (Task)
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task2);

        // Создать один эпик с 2 подзадачами, ...
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1 Эпик 1", "Описание подзадачи 1 эпик 1", epic1.getId());
        taskManager.createSubtask(subtask11);

        Subtask subtask12=new Subtask("Подзадача 2 Эпик 1", "Описание подзадачи 2 эпик 1", epic1.getId());
        taskManager.createSubtask(subtask12);

        // а другой эпик с 1 подзадачей.
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2", epic2.getId());
        taskManager.createSubtask(subtask21);

        System.out.println();
        TestUtils.printTitle("Ожидаемый список задач: " + System.lineSeparator() +
                "Задача 1 - id = 1" + System.lineSeparator() +
                "Задача 2 - id = 2" + System.lineSeparator() +
                "Подзадача 1 Эпик 1 - id = 4" + System.lineSeparator() +
                "Подзадача 2 Эпик 1 - id = 5" + System.lineSeparator() +
                "Подзадача 1 Эпик 2 - id = 7" + System.lineSeparator() +
                "Эпик 1 - id = 3" + System.lineSeparator() +
                "Эпик 2 - id = 6" + System.lineSeparator());

        TestUtils.printTitle("Полученные задачи:");
        TestUtils.printAllTasks(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 2: Получение по идентификатору");
        TestUtils.printLine("Получили списки задач, подзадач, эпиков. Запросили id у первого элемента в каждом списке.");
        List<Task> tasks2 = taskManager.getTasks();
        List<Subtask> subtasks2 = taskManager.getSubtasks();
        List<Epic> epics2 = taskManager.getEpics();
        TestUtils.printLine("Запросили в TaskManager первые в списках Задачу (id 1), " +
                "Подзадачу (id 4), Эпик (id 3) - по полученным из списков id.");
        Task newTask = taskManager.getTask(tasks2.get(0).getId());
        Subtask newSubtask = taskManager.getSubtask(subtasks2.get(0).getId());
        Epic newEpic = taskManager.getEpic(epics2.get(0).getId());
        System.out.println("Task by id (1): " + newTask);
        System.out.println("Epic by id (3): " + newEpic);
        System.out.println("Subs by id (4): " + newSubtask);

        System.out.println();


        System.out.println();
        TestUtils.printTitle("Тест 3: Восстановление данных менеджера из файла при запуске.");
        File file = new File("test1.txt");
        TaskManager taskManagerNEW = FileBackedTaskManager.loadFromFile(file);
        TestUtils.printManagerHistory(taskManagerNEW);
    }

}
