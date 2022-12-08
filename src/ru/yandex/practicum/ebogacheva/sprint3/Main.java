package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        // Тестирование Трекера задач

        // Создать 2 задачи (Task)
        Task task1 = new Task("Налоговая декларация", "Отправить декладацию до 31 декабря", Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task("Чемодан", "Размер L - заказать, забрать.", Status.NEW);
        manager.createTask(task2);

        // Создать один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        Epic epic1 = new Epic("Новый год", "Организовать праздник", Status.NEW);
        manager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1.getID(), Status.NEW);
        manager.createSubtask(subtask11);

        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1.getID(), Status.NEW);
        manager.createSubtask(subtask12);

        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату", Status.NEW);
        manager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2.getID(), Status.NEW);
        manager.createSubtask(subtask21);
        //Распечатайте списки эпиков, задач и подзадач
        //Получите списки всех задач:
        ArrayList<Task> tasks = manager.getTasks();
        ArrayList<Subtask> subtasks = manager.getSubtasks();
        ArrayList<Epic> epics = manager.getEpics();
        printTasks(tasks, subtasks, epics);

        manager.deleteAllTasks();

        ArrayList<Task> tasks2 = manager.getTasks();
        printTasks(tasks2, subtasks, epics);

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        // Изменения в Task:
        task1.setStatus(Status.DONE);
        manager.updateTask(task1);
        // Изменения в Subtask:
        subtask11.setStatus(Status.DONE);
        manager.updateSubtask(subtask11);
        // Изменения в Epic:
        epic2.setStatus(Status.DONE);
        manager.updateEpic(epic2);
        printTasks(tasks, subtasks, epics);

        // Все subtasks в одном эпике - DONE (epic ? done)
        subtask12.setStatus(Status.DONE);
        manager.updateSubtask(subtask12);
        printTasks(tasks, subtasks, epics);

        // попробуйте удалить одну из задач и один из эпиков.
//        manager.deleteById(subtask11.getId());
//        printTasks(manager);
    }

    private static void printTasks(ArrayList<Task> tasks, ArrayList<Subtask> subtasks, ArrayList<Epic> epics) {
        tasks.forEach(System.out::println);
        subtasks.forEach(System.out::println);
        epics.forEach(System.out::println);
        System.out.println("-------------------------------------------------------");
    }
}
