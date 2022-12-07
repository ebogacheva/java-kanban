package ru.yandex.practicum.ebogacheva.sprint3;

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
        manager.createTask(epic1);
        Subtask subtask11 = new Subtask("Елка", "Купить до 30 декабря", epic1);
        manager.createTask(subtask11);
        Subtask subtask12 = new Subtask("Подарки", "Купить и завернуть подарки", epic1);
        manager.createTask(subtask12);
        Epic epic2 = new Epic("Ремонт", "Обновить детскую комнату");
        manager.createTask(epic2);
        Subtask subtask21 = new Subtask("Материалы", "Купить обои, клей, ламинат, краску, валики", epic2);
        manager.createTask(subtask21);

        //Распечатайте списки эпиков, задач и подзадач
        manager.printListOfTasks();

        // Измените статусы созданных объектов
    }
}
