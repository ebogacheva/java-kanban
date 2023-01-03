package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        this.history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}

class CustomLinkedList {

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> historyInMap;
    private Node head;
    private Node tail;
    private int size;

    CustomLinkedList() {
        this.historyInMap = new HashMap<>();
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(Task task) {

        final int currId = task.getId();
        Node curr = getById(currId);
        if (curr != null) {
            removeNode(curr);
        }
        historyInMap.put(currId, linkLast(task));
    }

    public void remove(int id) {
        Node curr = getById(id);
        removeNode(curr);
    }

    private Node getById(int id) {
        return this.historyInMap.remove(id);
    }

    private Node linkLast(Task task) {

        final Node oldTail = this.tail;
        final Node newNode = new Node(oldTail, task, null);
        this.tail = newNode;
        if (oldTail == null) {
            this.head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
        return newNode;
    }

    private void removeNode(Node node) {
        final Node currPrev = node.prev;
        final Node currNext = node.next;
        if (currPrev == null) {
            this.head = currNext;
            currNext.prev = null;
        } else if (currNext == null) {
            this.tail = currPrev;
            currPrev.next = null;
        } else {
            currPrev.next = currNext;
            currNext.prev = currPrev;
        }

        node.prev = null;
        node.next = null;
        size--;
    }

    public List<Task> getTasks () {

        if (this.size == 0) {
            return Collections.emptyList();
        }
        List<Task> historyInList = new ArrayList<>();
        Node curr = this.head;
        historyInList.add(curr.task);
        while (curr.next != null) {
            historyInList.add(curr.next.task);
            curr = curr.next;
        }
        return historyInList;

    }



}
