package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;

import java.util.List;

public class InMemoryTaskManagerTestsUpdate {

    public static void testInMemoryTaskManager (TaskManager taskManager) {

        TestUtils.printTitle("Спринт 3-4. Тестирование InMemoryTaskManager");
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
        TestUtils.printTitle("Тест 2: Удаление всех задач");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        System.out.println();
        TestUtils.printTitle("Ожидаемый список задач: " + System.lineSeparator() +
                "---пустой список---" + System.lineSeparator());
        TestUtils.printAllTasks(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 3: Снова заполнили тестовые значения");
        // Создать 2 задачи (Task) - ПОСЛЕ УДАЛЕНИЯ
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        taskManager.createTask(task3);

        Task task4 = new Task("Задача 4", "Описание задачи 4");
        taskManager.createTask(task4);

        // Создать один эпик с 2 подзадачами
        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.createEpic(epic3);

        Subtask subtask31 = new Subtask("Подзадача 1 Эпика 3", "Описание подзадачи 1 эпика 3", epic3.getId());
        taskManager.createSubtask(subtask31);

        Subtask subtask32 = new Subtask("Подзадача 2 Эпика 3", "Описание подзадачи 2 эпика 3", epic3.getId());
        taskManager.createSubtask(subtask32);

        // а другой эпик с 1 подзадачей.
        Epic epic4 = new Epic("Эпик 4", "Описание Эпика 4");
        taskManager.createEpic(epic4);

        Subtask subtask41 = new Subtask("Подзадача 1 Эпика 4", "Описание подзадачи 1 эпика 4", epic4.getId());
        taskManager.createSubtask(subtask41);

        System.out.println();
        TestUtils.printTitle("Ожидаемый список задач: " + System.lineSeparator() +
                "Задача 3 - id = 8" + System.lineSeparator() +
                "Задача 4 - id = 9" + System.lineSeparator() +
                "Подзадача 1 Эпик 3 - id = 11" + System.lineSeparator() +
                "Подзадача 2 Эпик 3 - id = 12" + System.lineSeparator() +
                "Подзадача 1 Эпик 4 - id = 14" + System.lineSeparator() +
                "Эпик 3 - id = 10" + System.lineSeparator() +
                "Эпик 4 - id = 13" + System.lineSeparator());

        TestUtils.printTitle("Полученные задачи:");
        TestUtils.printAllTasks(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 4: Получение по идентификатору");
        TestUtils.printLine("Получили списки задач, подзадач, эпиков. Запросили id у первого элемента в каждом списке.");
        List<Task> tasks2 = taskManager.getTasks();
        List<Subtask> subtasks2 = taskManager.getSubtasks();
        List<Epic> epics2 = taskManager.getEpics();
        TestUtils.printLine("Запросили в TaskManager первые в списках Задачу (id 8), " +
                "Подзадачу (id 10), Эпик (id 11) - по полученным из списков id.");
        Task newTask = taskManager.getTask(tasks2.get(0).getId());
        Subtask newSubtask = taskManager.getSubtask(subtasks2.get(0).getId());
        Epic newEpic = taskManager.getEpic(epics2.get(0).getId());
        System.out.println("Task by id (8): " + newTask);
        System.out.println("Epic by id (11): " + newEpic);
        System.out.println("Subs by id (10): " + newSubtask);

        System.out.println();
        TestUtils.printTitle("Ожидаемая история просмотров: " + System.lineSeparator() +
                "Задача 4" + System.lineSeparator() +
                "Подзадача 2 Эпика 3" + System.lineSeparator() +
                "Подзадача 1 Эпика 4" + System.lineSeparator() +
                "Эпик 4" + System.lineSeparator() +
                "Задача 3" + System.lineSeparator() +
                "Подзадача 1 Эпика 3" + System.lineSeparator() +
                "Эпик 3" + System.lineSeparator());
        TestUtils.printTitle("Полученная история просмотров:");
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printLine("Проверим, что в первом элементе списка Задач и в полученной из менеджера задачи +" +
                "ссылки на разные объекты.");
        System.out.println("Т.е. запрошенные из менеджера Задачи, Подзадачи, Эпики 'отвязаны' от" +
                "хранящихся в менеджере.");
        System.out.println("Сравним две задачи с id = 8 через метод equals(). Результат должен быть true:");
        System.out.println(tasks2.get(0).equals(newTask));
        TestUtils.printLine("А теперь сравним ссылки на объекты. Должно быть false:");
        System.out.println(tasks2.get(0) == newTask);

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println();
        TestUtils.printTitle("Тест 5: Изменения в Task - DONE.");
        TestUtils.printLine("Изменим в Задаче 3 статус на DONE. Обновим задачу в менеджере.");
        TestUtils.printLine("Запросим из менеджера задачу по id (8). Статус задачи должен быть DONE:");
        task3.setStatus(Status.DONE);
        taskManager.updateTask(task3);
        int idTask3 = task3.getId();
        System.out.println(taskManager.getTask(idTask3));


        System.out.println();
        TestUtils.printTitle("Тест 6: Изменения в Subtask - DONE.");
        TestUtils.printLine("Изменим в Подзадаче 1 Эпика 3 статус на DONE. Обновим задачу в менеджере.");
        TestUtils.printLine("Запросим из менеджера подзадачу по id (11). Статус задачи должен быть DONE:");
        subtask31.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask31);
        int idSub31 = subtask31.getId();
        System.out.println(taskManager.getSubtask(idSub31));
        TestUtils.printLine("При этом должен был обновиться статус Эпика 3. Стать IN_PROGRESS. Проверяем:");
        int idEpic3 = subtask31.getEpicId();
        System.out.println(taskManager.getEpic(idEpic3));

        System.out.println();
        TestUtils.printTitle("Тест 7: Изменения в Epic - DONE.");
        TestUtils.printLine("Изменим в Эпике 3 статус на DONE. Обновим задачу в менеджере.");
        TestUtils.printLine("Запросим из менеджера Эпик по id (10). Статус эпика должен быть IN_PROGRESS. Проверяем:");
        epic3.setStatus(Status.DONE);
        taskManager.updateEpic(epic3);
        System.out.println(taskManager.getEpic(idEpic3));

        System.out.println();
        TestUtils.printTitle("Тест 8: Все Subtasks в одном Epic - DONE");
        TestUtils.printLine("Установим статусы всех подзадач Эпика 3 (id 11,12) - DONE. Проверим статус Эпика 3 (id 10).");
        subtask31.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask31);
        subtask32.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask32);
        int idSub32 = subtask32.getId();
        System.out.println(taskManager.getSubtask(idSub31));
        System.out.println(taskManager.getSubtask(idSub32));
        System.out.println(taskManager.getEpic(idEpic3));

        System.out.println();
        TestUtils.printTitle("Тест 9: Все Subtasks в одном Epic - NEW");
        TestUtils.printLine("Установим статусы всех подзадач Эпика 3 (id 11,12) - NEW. Проверим статус Эпика 3 (id 10).");
        subtask31.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask31);
        subtask32.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask32);
        System.out.println(taskManager.getSubtask(idSub31));
        System.out.println(taskManager.getSubtask(idSub32));
        System.out.println(taskManager.getEpic(idEpic3));

        System.out.println();
        TestUtils.printTitle("Тест 10: Удаление по идентификатору");
        TestUtils.printLine("Удаляем Задачу 3 (id 8), Подзадачу 1 эпика 3 (id 11), Эпик 4 (id 13)");
        taskManager.deleteTaskById(idTask3);
        taskManager.deleteSubtaskById(idSub31);
        taskManager.deleteEpicById(epics2.get(1).getId());

        System.out.println();
        TestUtils.printTitle("Ожидаемый список задач: " + System.lineSeparator() +
                "Задача 4 - id = 9" + System.lineSeparator() +
                "Подзадача 2 Эпик 3 - id = 12" + System.lineSeparator() +
                "Эпик 3 - id = 10" + System.lineSeparator());
        TestUtils.printTitle("Полученный список задач:");
        TestUtils.printAllTasks(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 11: История просмотров");
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 12: Не изменение истории просмотров при добавлении задач.");
        TestUtils.printLine("Добавляем новую задачу и новый эпик.");
        TestUtils.printLine("История не должна была измениться:");
        Task newTask2 = new Task("Новая задача", "Описание новой задачи.");
        taskManager.createTask(newTask2);
        Epic newEpic2 = new Epic("Новый эпик", "Описание нового эпика.");
        taskManager.createEpic(newEpic2);
        // история не должна была измениться с прошлого раза
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 13: Изменение истории после getTask / getEpic.");
        TestUtils.printLine("В истории должны появиться новая задача и новый эпик:");
        taskManager.getEpic(newEpic2.getId());
        taskManager.getTask(newTask2.getId());
        // новая задача и эпик в истории
        TestUtils.printManagerHistory(taskManager);

        System.out.println();
        TestUtils.printTitle("Тест 14: При удалении тасков (удалили все задачи) они исчезают из истории.");
        taskManager.deleteAllTasks();
        TestUtils.printLine("Должны остаться Эпики и Подзадача:");
        TestUtils.printAllTasks(taskManager);
        TestUtils.printManagerHistory(taskManager);

        //Очищаем списки менеджера для следующих тестов
        taskManager.deleteAllEpics();
        taskManager.deleteAllTasks();
    }


}
