package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        // Тестирование Трекера задач

        // Создать 2 задачи (Task)
        Task task1 = new Task("Налоговая декларация", "Отправить декладацию до 31 декабря");
        manager.createTask(task1);
        Task task2 = new Task("Чемодан", "Размер L - заказать, забрать.");
        manager.createTask(task2);

        // Создать один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        Epic epic1 = new Epic("Новый год", "Организовать праздник");
        manager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1);
        manager.createSubtask(subtask11);

        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1);
        manager.createSubtask(subtask12);

        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату");
        manager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2);
        manager.createSubtask(subtask21);
        //Распечатайте списки эпиков, задач и подзадач
        printTasks(manager);

        // Измените статусы созданных объектов распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        subtask11.setStatus(Status.DONE);
        task1.setStatus(Status.DONE);
        epic1.setStatus(Status.DONE);
        manager.updateSubtask(subtask11);
        manager.updateEpic(epic1);
        manager.updateTask(task1);
        printTasks(manager);

        // попробуйте удалить одну из задач и один из эпиков.
//        manager.deleteById(subtask11.getId());
//        printTasks(manager);
    }

    private static void printTasks(Manager manager) {
        List<Task> tasks = manager.getTasks();
        tasks.forEach(System.out::println);
        List<Subtask> subtasks = manager.getSubtasks();
        subtasks.forEach(System.out::println);
        List<Epic> epics = manager.getEpics();
        epics.forEach(System.out::println);
        System.out.println("-------------------------------------------------------");
    }
}
