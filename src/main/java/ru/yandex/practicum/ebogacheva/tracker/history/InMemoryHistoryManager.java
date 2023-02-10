package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        this.history =new CustomLinkedList();
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

    private static class CustomLinkedList {

        private final Map<Integer, Node<Task>> historyInMap;
        private Node<Task> head;
        private Node<Task> tail;
        private int size;

        CustomLinkedList() {
            this.historyInMap = new HashMap<>();
            this.head = null;
            this.tail = null;
            this.size = 0;
        }

        public void add(Task task) {

            final int currId = task.getId();
            Node<Task> curr = getById(currId);
            if (curr != null) {
                removeNode(curr);
            }
            historyInMap.put(currId, linkLast(task));
        }

        public void remove(int id) {
            Node<Task> curr = getById(id);
            if (curr != null) {
                removeNode(curr);
            }
        }

        private Node<Task> getById(int id) {
            return this.historyInMap.remove(id);
        }

        private Node<Task> linkLast(Task task) {

            final Node<Task> oldTail = this.tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            this.tail = newNode;
            if (oldTail == null) {
                this.head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;
            return newNode;
        }

        private void removeNode(Node<Task> node) {
            final Node<Task> currPrev = node.prev;
            final Node<Task> currNext = node.next;
            if (currPrev == null) {
                this.head = currNext;
            } else {
                currPrev.next = currNext;
            }
            if (currNext == null) {
                this.tail = currPrev;
            } else {
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
            for (Node<Task> node = this.head; node != null; node = node.next) {
                historyInList.add(node.task);
            }
            return historyInList;
        }
    }
}


