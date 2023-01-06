package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Epic;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Task;

public class InMemoryHistoryManagerTests {


    public static void testInMemoryHistoryManager(TaskManager taskManager) {

        TestUtils.printTitle("Спринт 5. Тестирование CustomLinkedList");
        TestUtils.printTitle("Тест 1: Создать две задачи, эпик с тремя подзадачам и эпик без подзадач. " +
                "Запросить несколько раз. Вывести историю и убедиться, что в ней нет повторов");
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask11 = new Subtask("Подзадача 1 Эпик 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 2 Эпик 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createSubtask(subtask12);
        Subtask subtask13 = new Subtask("Подзадача 3 Эпик 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createSubtask(subtask13);

        // Запрос к задачам:
        taskManager.getTask(task1.getId());
        taskManager.getTask(subtask12.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(epic1.getId());
        taskManager.getTask(epic2.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(subtask11.getId());
        taskManager.getTask(epic2.getId());

        // История должна быть:
        // Подзадача 2
        // Задача 2
        // Эпик 1
        // Задача 1
        // Подзадача 1 Эпик 1
        // Эпик 2
        TestUtils.printManagerHistory(taskManager);

        TestUtils.printTitle("Тест 2: Удалить задачу, проверить, что при печати она не будет выводиться. " +
                "Удалить эпик с тремя подзадачами и проверить, что из истории удалился как сам эпик, так и все его подзадачи.");
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        // История должна быть:
        // Задача 2
        // Эпик 2
        TestUtils.printManagerHistory(taskManager);

    }
}
