import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    public void initiateTaskManager() {
        taskManager = Managers.getFileBackedManager("test1.txt");
    }

    @Test
    void getSubIds() {
        List<Subtask> subtasks = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        int subtaskId = subtasks.get(0).getId();
        Epic epic1 = taskManager.getEpics().get(0);
        Epic epic2 = taskManager.getEpics().get(1);
        assertFalse(epic1.getSubIds().isEmpty());
        assertTrue(epic1.getSubIds().contains(subtaskId) || epic2.getSubIds().contains(subtaskId));

        Epic epic3 = TestObjectsProvider.getEpicForTesting(3);
        List<Integer> subIds = epic3.getSubIds();
        assertEquals(subIds, List.of());
    }

    @Test
    void setSubIds() {
        Epic epic = TestObjectsProvider.getEpicForTesting(1);
        taskManager.createEpic(epic);
        int epicId = epic.getId();
        epic.setSubIds(new ArrayList<>(Arrays.asList(1,2)));
        taskManager.updateEpic(epic);
        Epic actual = taskManager.getEpic(epicId);
        assertTrue(actual.getSubIds().isEmpty());
    }

    @Test
    void removeSubTask() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epic.getId());
        Subtask subtask2 = TestObjectsProvider.getSubtaskForTesting(2, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        int id = epic.getId();
        Epic epicNew = taskManager.getEpic(id);
        epicNew.removeSubTask(subtask1.getId());
        taskManager.updateEpic(epicNew);
        assertEquals(1,taskManager.getEpic(id).getSubIds().size());
    }

    @Test
    void addSubTask() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epic.getId());
        epic.addSubTask(subtask1.getId());
        assertTrue(epic.getSubIds().contains(subtask1.getId()));
    }

    @Test
    void clearSubTasks() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epic.getId());
        epic.clearSubTasks();
        assertTrue(epic.getSubIds().isEmpty());
    }

    @Test
    void toFileString() {
        Epic epic1 = new Epic(1, "title", "description", Status.NEW, 0, null, List.of(2,3));
        String epic1Expected = "1,EPIC,title,NEW,description,PT0S,null,[2, 3]";
        String epic1Actual = epic1.toFileString();
        assertEquals(epic1Expected, epic1Actual);
    }
}