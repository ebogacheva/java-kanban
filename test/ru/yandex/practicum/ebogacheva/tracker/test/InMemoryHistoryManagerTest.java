package ru.yandex.practicum.ebogacheva.tracker.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.history.InMemoryHistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void initiateTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskToHistory() {
        Task task = TestObjectsProvider.getTaskForTesting(1);
        task.setId(10);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
        assertThrows(NullPointerException.class, () -> historyManager.add(null));
    }

    @Test
    void remove() {
        Task task1 = TestObjectsProvider.getTaskForTesting(1);
        task1.setId(10);
        Task task2 = TestObjectsProvider.getTaskForTesting(2);
        task2.setId(20);
        Task task3 = TestObjectsProvider.getTaskForTesting(3);
        task3.setId(30);
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task1));
        historyManager.remove(30);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        Task task1 = TestObjectsProvider.getTaskForTesting(1);
        task1.setId(10);
        Task task2 = TestObjectsProvider.getTaskForTesting(2);
        task2.setId(20);
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(historyManager.getHistory(), List.of(task1, task2));
    }
}