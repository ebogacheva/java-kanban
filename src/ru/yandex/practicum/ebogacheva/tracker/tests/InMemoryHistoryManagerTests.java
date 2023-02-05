package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

public class InMemoryHistoryManagerTests {


    public static void testInMemoryHistoryManager(TaskManager taskManager) {

        TestUtils.printTitle("Спринт 5. Тестирование CustomLinkedList");

        TestUtils.printTitle("Тест 1: Создать две задачи, эпик с тремя подзадачам и эпик без подзадач. " +
                "Запросить несколько раз. Вывести историю и убедиться, что в ней нет повторов.");

        // Создали задачи, подзадачи, эпики:
        Task task1 = new Task("Задача 1", "Описание задачи 1", 60);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 60);
        taskManager.createTask(task1);
        TestUtils.printLine("Создана Задача 1");
        taskManager.createTask(task2);
        TestUtils.printLine("Создана Задача 2");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1",60);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2",60);
        taskManager.createEpic(epic1);
        TestUtils.printLine("Создан Эпик 1");
        taskManager.createEpic(epic2);
        TestUtils.printLine("Создан Эпик 2");

        Subtask subtask11 = new Subtask("Подзадача 1 Эпик 1", "Описание подзадачи 1", 60, epic1.getId());
        Subtask subtask12 = new Subtask("Подзадача 2 Эпик 1", "Описание подзадачи 1", 60, epic1.getId());
        Subtask subtask13 = new Subtask("Подзадача 3 Эпик 1", "Описание подзадачи 1", 60, epic1.getId());
        taskManager.createSubtask(subtask11);
        TestUtils.printLine("Создана Подзадача 1 Эпик 1");
        taskManager.createSubtask(subtask12);
        TestUtils.printLine("Создана Подзадача 2 Эпик 1");
        taskManager.createSubtask(subtask13);
        TestUtils.printLine("Создана Подзадача 3 Эпик 1");

        // Запрос к задачам:
        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask11.getId());
        taskManager.getEpic(epic2.getId());

        // Протестировали полученную историю:
        System.out.println();
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Подзадача 2 Эпик 1" + System.lineSeparator() +
                "Задача 2" + System.lineSeparator() +
                "Эпик 1" + System.lineSeparator() +
                "Задача 1" + System.lineSeparator() +
                "Подзадача 1 Эпик 1" + System.lineSeparator() +
                "Эпик 2" + System.lineSeparator());
        TestUtils.printTitle("Полученная история просмотров:");
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 2: Удалить задачу, проверить, что при печати она не будет выводиться. " +
                "Удалить эпик с тремя подзадачами и проверить, что из истории удалился как сам эпик, " +
                "так и все его подзадачи.");

        // Удалили Задачу 1 и Эпик 1 (с тремя подзадачами):
        taskManager.deleteTaskById(task1.getId());
        TestUtils.printLine("Задача 1 удалена.");
        taskManager.deleteEpicById(epic1.getId());
        TestUtils.printLine("Эпик 1 удален.");

        // Протестировали полученную историю:
        System.out.println();
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Задача 2" + System.lineSeparator() +
                "Эпик 2" + System.lineSeparator());
        TestUtils.printTitle("Полученная история просмотров:");
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 3: Запросить список всех подзадач.");

        // Удалили все оставшиеся задачи:
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        // Снова создали задачи, подзадачи, эпики:
        Task task3 = new Task("Задача 3", "Описание задачи 3", 60);
        Task task4 = new Task("Задача 4", "Описание задачи 4", 60);
        taskManager.createTask(task3);
        TestUtils.printLine("Создана Задача 3");
        taskManager.createTask(task4);
        TestUtils.printLine("Создана Задача 4");

        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3",60);
        Epic epic4 = new Epic("Эпик 4", "Описание эпика 4",60);
        taskManager.createEpic(epic3);
        TestUtils.printLine("Создан Эпик 3");
        taskManager.createEpic(epic4);
        TestUtils.printLine("Создан Эпик 4");

        Subtask subtask31 = new Subtask("Подзадача 1 Эпик 3", "Описание подзадачи 1", 60, epic3.getId());
        Subtask subtask32 = new Subtask("Подзадача 2 Эпик 3", "Описание подзадачи 1", 60, epic3.getId());
        Subtask subtask33 = new Subtask("Подзадача 3 Эпик 3", "Описание подзадачи 1", 60, epic3.getId());
        taskManager.createSubtask(subtask31);
        TestUtils.printLine("Создана Подзадача 1 Эпик 3");
        taskManager.createSubtask(subtask32);
        TestUtils.printLine("Создана Подзадача 2 Эпик 3");
        taskManager.createSubtask(subtask33);
        TestUtils.printLine("Создана Подзадача 3 Эпик 3");

        // Запросили список всех подзадач:
        taskManager.getSubtasks();
        System.out.println();
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Подзадача 1 Эпик 3" + System.lineSeparator() +
                "Подзадача 2 Эпик 3" + System.lineSeparator() +
                "Подзадача 3 Эпик 3" + System.lineSeparator());
        TestUtils.printTitle("Полученная история просмотров:");
        TestUtils.printManagerHistory(taskManager);

    }

}
