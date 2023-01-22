package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.List;

public class InMemoryTaskManagerTests {

    public static void testInMemoryTaskManager (TaskManager taskManager) {
        TestUtils.printTitle("Тест 1: Заполнение задачами");
        // Создать 2 задачи (Task)
        Task task1 = new Task("Налоговая декларация", "Отправить декларацию до 31 декабря");
        taskManager.createTask(task1);

        Task task2 = new Task("Чемодан", "Размер L - заказать, забрать.");
        taskManager.createTask(task2);

        // Создать один эпик с 2 подзадачами, ...
        Epic epic1 = new Epic("Новый год", "Организовать праздник");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1.getId());
        taskManager.createSubtask(subtask11);

        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1.getId());
        taskManager.createSubtask(subtask12);

        // а другой эпик с 1 подзадачей.
        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату");
        taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2.getId());
        taskManager.createSubtask(subtask21);
        TestUtils.printAllTasks(taskManager);

        TestUtils.printTitle("Тест 2: Удаление всех задач");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        TestUtils.printAllTasks(taskManager);

        TestUtils.printTitle("Тест 3: Снова заполнили тестовые значения");
        // Создать 2 задачи (Task) - ПОСЛЕ УДАЛЕНИЯ
        Task task3 = new Task("НАЛОГОВАЯ ДЕКЛАРАЦИЯ", "Отправить декларацию до 31 декабря");
        taskManager.createTask(task3);

        Task task4 = new Task("ЧЕМОДАН", "Размер L - заказать, забрать.");
        taskManager.createTask(task4);

        // Создать один эпик с 2 подзадачами
        Epic epic3 = new Epic("НОВЫЙ ГОД", "Организовать праздник");
        taskManager.createEpic(epic3);

        Subtask subtask31 = new Subtask("ЕЛКА", "Купить до 30 декабря", epic3.getId());
        taskManager.createSubtask(subtask31);

        Subtask subtask32 = new Subtask("ПОДАРКИ", "Купить и завернуть подарки", epic3.getId());
        taskManager.createSubtask(subtask32);

        // а другой эпик с 1 подзадачей.
        Epic epic4 = new Epic("РЕМОНТ", "Обновить детскую комнату");
        taskManager.createEpic(epic4);

        Subtask subtask41 = new Subtask("МАТЕРИАЛЫ", "Купить обои, клей, ламинат, краску, валики", epic4.getId());
        taskManager.createSubtask(subtask41);

        TestUtils.printAllTasks(taskManager);

        TestUtils.printTitle("Тест 4: Получение по идентификатору");
        List<Task> tasks2 = taskManager.getTasks();
        List<Subtask> subtasks2 = taskManager.getSubtasks();
        List<Epic> epics2 = taskManager.getEpics();
        System.out.println("Task by id: " + taskManager.getTask(tasks2.get(0).getId()));
        System.out.println("Epic by id: " + taskManager.getTask(epics2.get(0).getId()));
        System.out.println("Subs by id: " + taskManager.getTask(subtasks2.get(0).getId()));
        System.out.println(taskManager.getHistory());

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        TestUtils.printTitle("Тест 5: Изменения в Task - DONE");
        task3.setStatus(Status.DONE);
        taskManager.updateTask(task3);
        int idTask3 = task3.getId();
        System.out.println("Task by id - must be DONE: ");
        System.out.println(taskManager.getTask(idTask3));

        TestUtils.printTitle("Тест 6: Изменения в Subtask - DONE");
        subtask31.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask31);
        int idSub31 = subtask31.getId();
        System.out.println("Subs by id - must be DONE: ");
        System.out.println(taskManager.getTask(idSub31));
        int idEpic3 = epics2.get(0).getId();
        System.out.println("Epic by id - check status: ");
        System.out.println(taskManager.getTask(idEpic3));


        TestUtils.printTitle("Тест 7: Изменения в Epic - DONE");
        epic3.setStatus(Status.DONE);
        taskManager.updateEpic(epic3);
        System.out.println("Epic by id - check status: ");
        System.out.println(taskManager.getTask(idEpic3));


        TestUtils.printTitle("Тест 8: Все Subtasks в одном Epic - DONE");
        subtask31.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask31);
        subtask32.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask32);
        int idSub32 = subtask32.getId();
        System.out.println("Subs by id - must be DONE: ");
        System.out.println(taskManager.getTask(idSub31));
        System.out.println("Subs by id - must be DONE: ");
        System.out.println(taskManager.getTask(idSub32));
        System.out.println("Epic by id - check status: ");
        System.out.println(taskManager.getTask(idEpic3));


        TestUtils.printTitle("Тест 9: Все Subtasks в одном Epic - NEW");
        subtask31.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask31);
        subtask32.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask32);
        System.out.println("Subs by id - must be NEW: ");
        System.out.println(taskManager.getTask(idSub31));
        System.out.println("Subs by id - must be NEW: ");
        System.out.println(taskManager.getTask(idSub32));
        System.out.println("Epic by id - check status: ");
        System.out.println(taskManager.getTask(idEpic3));


       TestUtils.printTitle("Тест 10: Удаление по идентификатору");
        taskManager.deleteTaskById(idTask3);
        taskManager.deleteSubtaskById(idSub31);
        taskManager.deleteEpicById(epics2.get(1).getId());
        TestUtils.printAllTasks(taskManager);

        TestUtils.printTitle("Тест 11: История просмотров");
        // по предыдущим вызовам должны быть примерно такие таски в истории (13 вызовов getTask - 10 последние соотв.):
        //    Task:    НАЛОГОВАЯ...
        //    Subtask: ЁЛКА
        //    Epic:    НОВЫЙ ГОД
        //    ...
        //    Subtask: ПОДАРКИ
        //    Epic:    НОВЫЙ ГОД
        TestUtils.printManagerHistory(taskManager);

        TestUtils.printTitle("Тест 12: Не изменение истории просмотров при добавлении тасков");
        Task task12_1 = new Task("ПОДГУЗНИКИ", "Продать на avito лишние");
        taskManager.createTask(task12_1);

        Epic epic12_1 = new Epic("ИПОТЕКА", "Купить ещё одну квартиру");
        taskManager.createEpic(epic12_1);

        // история не должна была измениться с прошлого раза
        TestUtils.printManagerHistory(taskManager);

        TestUtils.printTitle("Тест 13: Изменение истории после getTask");
        taskManager.getTask(epic12_1.getId());
        taskManager.getTask(task12_1.getId());

        // два последних таска должны быть новые: Epic: ИПОТЕКА; Task: ПОДГУЗНИКИ
        TestUtils.printManagerHistory(taskManager);

        TestUtils.printTitle("Тест 14: Неизменение истории даже при удалении тасков (история хранит даже удалённые таски)");
        taskManager.deleteAllTasks();
        TestUtils.printAllTasks(taskManager);
        TestUtils.printManagerHistory(taskManager);
    }


}
