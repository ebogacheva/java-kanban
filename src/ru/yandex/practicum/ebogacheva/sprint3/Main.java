package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        // Моему уважаемому ревьюверу: Давно я не сталкивалась с задачей, которая бы меня так вывела из себя)
        // Тестирование Трекера задач

        // Создать 2 задачи (Task)
        Task task1 = new Task("Налоговая декларация", "Отправить декладацию до 31 декабря");
        manager.createTask(task1);

        Task task2 = new Task("Чемодан", "Размер L - заказать, забрать.");
        manager.createTask(task2);

        // Создать один эпик с 2 подзадачами, ...
        Epic epic1 = new Epic("Новый год", "Организовать праздник");
        manager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1.getID());
        manager.createSubtask(subtask11);

        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1.getID());
        manager.createSubtask(subtask12);

        // а другой эпик с 1 подзадачей.
        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату");
        manager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2.getID());
        manager.createSubtask(subtask21);

        // Распечатайте списки эпиков, задач и подзадач
        // Получите списки всех задач:
        printAllTasks(manager);


        manager.deleteAllTasks();
        manager.deleteAllEpics();
        printAllTasks(manager);


        // Создать 2 задачи (Task) - ПОСЛЕ УДАЛЕНИЯ
        System.out.println("Снова заполненили тестовые значения\n");
        Task task3 = new Task("НАЛОГОВАЯ ДЕКЛАРАЦИЯ", "Отправить декладацию до 31 декабря");
        manager.createTask(task3);

        Task task4 = new Task("ЧЕМОДАН", "Размер L - заказать, забрать.");
        manager.createTask(task4);

        // Создать один эпик с 2 подзадачами
        Epic epic3 = new Epic("НОВЫЙ ГОД", "Организовать праздник");
        manager.createEpic(epic3);

        Subtask subtask31 = new Subtask("ЕЛКА", "Купить до 30 декабря", epic3.getID());
        manager.createSubtask(subtask31);

        Subtask subtask32 = new Subtask("ПОДАРКИ", "Купить и завернуть подарки", epic3.getID());
        manager.createSubtask(subtask32);

        // а другой эпик с 1 подзадачей.
        Epic epic4 = new Epic("РЕМОНТ", "Обновить детскую комнату");
        manager.createEpic(epic4);

        Subtask subtask41 = new Subtask("МАТЕРИАЛЫ", "Купить обои, клей, ламинат, краску, валики", epic4.getID());
        manager.createSubtask(subtask41);

        // Распечатайте списки эпиков, задач и подзадач
        printAllTasks(manager);

        // Получение по идентификатору
        List<Task> tasks2 = manager.getTasks();
        List<Subtask> subtasks2 = manager.getSubtasks();
        List<Epic> epics2 = manager.getEpics();
        System.out.println("Task by id: \n" + manager.getTask(tasks2.get(0).getID()));
        System.out.println("Epic by id: \n" + manager.getTask(epics2.get(0).getID()));
        System.out.println("Subs by id: \n" + manager.getTask(subtasks2.get(0).getID()));
        printLine();

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        // Изменения в Task:
        task3.setStatus(Status.DONE);
        manager.updateTask(task3);
        int idTask3 = task3.getID();
        System.out.println("Task by id - must be DONE: \n" + manager.getTask(idTask3));
        printLine();

        // Изменения в Subtask:
        subtask31.setStatus(Status.DONE);
        manager.updateSubtask(subtask31);
        int idSub31 = subtask31.getID();
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub31));
        int idEpic3 = epics2.get(0).getID();
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));
        printLine();

        // Изменения в Epic:
        epic3.setStatus(Status.DONE);
        manager.updateEpic(epic3);
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));
        printLine();

        // Все Subtasks в одном Epic - DONE:
        subtask31.setStatus(Status.DONE);
        manager.updateSubtask(subtask31);
        subtask32.setStatus(Status.DONE);
        manager.updateSubtask(subtask32);
        int idSub32 = subtask32.getID();
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub31));
        System.out.println("Subs by id - must be DONE: \n" + manager.getTask(idSub32));
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));
        printLine();

        // Все Subtasks в одном Epic - NEW:
        subtask31.setStatus(Status.NEW);
        manager.updateSubtask(subtask31);
        subtask32.setStatus(Status.NEW);
        manager.updateSubtask(subtask32);
        System.out.println("Subs by id - must be NEW: \n" + manager.getTask(idSub31));
        System.out.println("Subs by id - must be NEW: \n" + manager.getTask(idSub32));
        System.out.println("Epic by id - check status: \n" + manager.getTask(idEpic3));
        printLine();

        // Удаление по идентификатору:
        manager.deleteTaskById(idTask3);
        manager.deleteSubtaskById(idSub31);
        manager.deleteEpicById(epics2.get(1).getID());
        printAllTasks(manager);
    }

    private static void printAllTasks(Manager manager) {
        printTasks(manager.getTasks(), manager.getSubtasks(), manager.getEpics());
    }

    private static void printLine() {
        System.out.println("-------------------------------------------------------");
    }

    private static void printTasks(List<Task> tasks, List<Subtask> subtasks, List<Epic> epics) {
        printLine();
        tasks.forEach(System.out::println);
        subtasks.forEach(System.out::println);
        epics.forEach(System.out::println);
        printLine();
    }
}
