package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.tests.FileBackedTaskManagerTests;
import ru.yandex.practicum.ebogacheva.tracker.tests.InMemoryHistoryManagerTests;

public class Main {

    public static void main(String[] args) {

        //TaskManager taskManager = Managers.getDefault();

        // Внесла принципиальное изменение в таски и менеджер.
        // При обращении к мененджеру за тасками (по id или списком) - создается копия таска, существующего в менеджере.
        // Копия создается через конструктор тасков / эпиков / подзадач. У копии и исходного таска совпадают id.
        // Но это разные объекты.

        //Тестирование Трекера задач (Спринт 3,4)
        //InMemoryTaskManagerTestsUpdate.testInMemoryTaskManager(taskManager);

        // Тестирование CustomLinkedList и InMemoryHistoryManager (Спринт 5)
        //InMemoryHistoryManagerTests.testInMemoryHistoryManager(taskManager);

        // Тестирование FileBAckedTaskManager (Спринт 6)
        TaskManager taskManager = Managers.getFileBackedManager("test1.txt");
        FileBackedTaskManagerTests.testFileBackedTaskManager(taskManager);

    }

}
