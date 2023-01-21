package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
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

        Subtask subtask12 = new Subtask("Подзадача 2 Эпик 1", "Описание подзадачи 2 эпик 1", epic1.getId());
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
        TestUtils.printTitle("Тест 3: Восстановление данных менеджера из файла при запуске.");
        TestUtils.printLine("Заполнили историю просмотров.");
        Task newTask2 = taskManager.getTask(2);
        Subtask newSubtask2 = taskManager.getSubtask(4);
        Epic newEpic2 = taskManager.getEpic(6);
        Epic newEpic3 = taskManager.getEpic(3);
        Task newTask1 = taskManager.getTask(1);
        Subtask newSubtask3 = taskManager.getSubtask(7);
        Subtask newSubtask4 = taskManager.getSubtask(5);
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Задача 2" + System.lineSeparator() +
                "Подзадача 1 Эпика 1" + System.lineSeparator() +
                "Эпик 2" + System.lineSeparator() +
                "Эпик 1" + System.lineSeparator() +
                "Задача 1" + System.lineSeparator() +
                "Подзадача 1 Эпика 2" + System.lineSeparator() +
                "Подзадача 2 Эпика 1" + System.lineSeparator());

        File file = new File("test1.txt");
        TaskManager taskManagerNEW = FileBackedTaskManager.loadFromFile(file);
        TestUtils.printTitle("Полученная история просмотров после восстановления:");
        TestUtils.printManagerHistory(taskManagerNEW);

        System.out.println();
        TestUtils.printTitle("Тест 4: Изменения в Subtask - DONE.");
        TestUtils.printLine("Изменим в Подзадаче 1 Эпика 2 статус на DONE. Обновим задачу в менеджере.");
        TestUtils.printLine("Запросим из менеджера подзадачу по id (7). Статус задачи должен быть DONE:");
        subtask21.setStatus(Status.DONE);
        taskManagerNEW.updateSubtask(subtask21);
        System.out.println(taskManagerNEW.getSubtask(7));
        TestUtils.printLine("При этом должен был обновиться статус Эпика 2. Стать DONE. Проверяем:");
        System.out.println(taskManagerNEW.getEpic(6));
        System.out.println();
        TestUtils.printTitle("История просмотров (последние две - подзадача (id 7) и эпик (id 6)):");
        TestUtils.printManagerHistory(taskManagerNEW);

        System.out.println();
        TestUtils.printTitle("Тест 5: Удаление по идентификатору");
        TestUtils.printLine("Удаляем Задачу 2 (id 2), Подзадачу 1 эпика 2 (id 7), Эпик 1 (id 3).");
        TestUtils.printLine("Должны удалиться также Подзадачи 1 и 2 (Эпик 1).");
        TestUtils.printLine("В истории просмотров должны остаться Эпик 2 (id 6) и Задача 1 (id 1)");
        taskManagerNEW.deleteTaskById(2);
        taskManagerNEW.deleteSubtaskById(7);
        taskManagerNEW.deleteEpicById(3);
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Задача 1" + System.lineSeparator() +
                "Эпик 2" + System.lineSeparator());
        TestUtils.printTitle("Полученная история просмотров удаления тасков:");
        TestUtils.printManagerHistory(taskManagerNEW);

    }

}
