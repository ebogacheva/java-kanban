package ru.yandex.practicum.ebogacheva.tracker.managers;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.ebogacheva.tracker.Managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void initiateTaskManager() {
        taskManager = Managers.getDefault();
    }
}
