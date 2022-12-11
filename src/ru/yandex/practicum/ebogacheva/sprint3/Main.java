package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager manager = new InMemoryTaskManager();

        // Тестирование Трекера задач

        printTitle("Тест 1: Заполнение задачами");
        // Создать 2 задачи (Task)
        Task task1 = new Task("Налоговая декларация", "Отправить декладацию до 31 декабря");
        manager.createTask(task1);

        Task task2 = new Task("Чемодан", "Размер L - заказать, забрать.");
        manager.createTask(task2);

        // Создать один эпик с 2 подзадачами, ...
        Epic epic1 = new Epic("Новый год", "Организовать праздник");
        manager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1.getId());
        manager.createSubtask(subtask11);

        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1.getId());
        manager.createSubtask(subtask12);

        // а другой эпик с 1 подзадачей.
        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату");
        manager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2.getId());
        manager.createSubtask(subtask21);
        printAllTasks(manager);

        printTitle("Тест 2: Удаление всех задач");
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        printAllTasks(manager);

        printTitle("Тест 3: Снова заполненили тестовые значения");
        // Создать 2 задачи (Task) - ПОСЛЕ УДАЛЕНИЯ
        Task task3 = new Task("НАЛОГОВАЯ ДЕКЛАРАЦИЯ", "Отправить декладацию до 31 декабря");
        manager.createTask(task3);

        Task task4 = new Task("ЧЕМОДАН", "Размер L - заказать, забрать.");
        manager.createTask(task4);

        // Создать один эпик с 2 подзадачами
        Epic epic3 = new Epic("НОВЫЙ ГОД", "Организовать праздник");
        manager.createEpic(epic3);

        Subtask subtask31 = new Subtask("ЕЛКА", "Купить до 30 декабря", epic3.getId());
        manager.createSubtask(subtask31);

        Subtask subtask32 = new Subtask("ПОДАРКИ", "Купить и завернуть подарки", epic3.getId());
        manager.createSubtask(subtask32);

        // а другой эпик с 1 подзадачей.
        Epic epic4 = new Epic("РЕМОНТ", "Обновить детскую комнату");
        manager.createEpic(epic4);

        Subtask subtask41 = new Subtask("МАТЕРИАЛЫ", "Купить обои, клей, ламинат, краску, валики", epic4.getId());
        manager.createSubtask(subtask41);

        printAllTasks(manager);

        printTitle("Тест 4: Получение по идентификатору");
        List<Task> tasks2 = manager.getTasks();
        List<Subtask> subtasks2 = manager.getSubtasks();
        List<Epic> epics2 = manager.getEpics();
        System.out.println("Task by id: " + manager.getTask(tasks2.get(0).getId()));
        System.out.println("Epic by id: " + manager.getTask(epics2.get(0).getId()));
        System.out.println("Subs by id: " + manager.getTask(subtasks2.get(0).getId()));

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        printTitle("Тест 5: Изменения в Task - DONE");
        task3.setStatus(Status.DONE);
        manager.updateTask(task3);
        int idTask3 = task3.getId();
        System.out.println("Task by id - must be DONE: \n" + manager.getTask(idTask3));

        printTitle("Тест 6: Изменения в Subtask - DONE");
        subtask31.setStatus(Status.DONE);
        manager.updateSubtask(subtask31);
        int idSub31 = subtask31.getId();
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub31));
        int idEpic3 = epics2.get(0).getId();
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));


        printTitle("Тест 7: Изменения в Epic - DONE");
        epic3.setStatus(Status.DONE);
        manager.updateEpic(epic3);
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));


        printTitle("Тест 8: Все Subtasks в одном Epic - DONE");
        subtask31.setStatus(Status.DONE);
        manager.updateSubtask(subtask31);
        subtask32.setStatus(Status.DONE);
        manager.updateSubtask(subtask32);
        int idSub32 = subtask32.getId();
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub31));
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub32));
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));


        printTitle("Тест 9: Все Subtasks в одном Epic - NEW");
        subtask31.setStatus(Status.NEW);
        manager.updateSubtask(subtask31);
        subtask32.setStatus(Status.NEW);
        manager.updateSubtask(subtask32);
        System.out.println("Subs by id - must be NEW: \n" + manager.getTask(idSub31));
        System.out.println("Subs by id - must be NEW: \n" + manager.getTask(idSub32));
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));


        printTitle("Тест 9: Удаление по идентификатору");
        manager.deleteTaskById(idTask3);
        manager.deleteSubtaskById(idSub31);
        manager.deleteEpicById(epics2.get(1).getId());
        printAllTasks(manager);
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        printTasks(manager.getTasks(), manager.getSubtasks(), manager.getEpics());
    }

    private static void printTitle(String title) {
        System.out.println("-----: " + title);
    }

    private static void printTasks(List<Task> tasks, List<Subtask> subtasks, List<Epic> epics) {
        printTitle("Список всех задач");
        tasks.forEach(System.out::println);
        subtasks.forEach(System.out::println);
        epics.forEach(System.out::println);
    }
}
