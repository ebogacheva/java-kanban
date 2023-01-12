package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history;

    public InMemoryHistoryManager() {
        this.history =new CustomLinkedList<>();
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

    private static class CustomLinkedList<T extends Task> {

        private static class Node<T extends Task> {
            final T task;
            Node<T> next;
            Node<T> prev;

            Node(Node<T> prev, T task, Node<T> next) {
                this.task = task;
                this.next = next;
                this.prev = prev;
            }
        }

        private final Map<Integer, Node<T>> historyInMap;
        private Node<T> head;
        private Node<T> tail;
        private int size;

        CustomLinkedList() {
            this.historyInMap = new HashMap<>();
            this.head = null;
            this.tail = null;
            this.size = 0;
        }

        public void add(T task) {

            final int currId = task.getId();
            Node<T> curr = getById(currId);
            if (curr != null) {
                removeNode(curr);
            }
            historyInMap.put(currId, linkLast(task));
        }

        public void remove(int id) {
            Node<T> curr = getById(id);
            if (curr != null) {
                removeNode(curr);
            }
        }

        private Node<T> getById(int id) {
            return this.historyInMap.remove(id);
        }

        private Node<T> linkLast(T task) {

            final Node<T> oldTail = this.tail;
            final Node<T> newNode = new Node<>(oldTail, task, null);
            this.tail = newNode;
            if (oldTail == null) {
                this.head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;
            return newNode;
        }


        private void removeNode(Node<T> node) {
            final Node<T> currPrev = node.prev;
            final Node<T> currNext = node.next;
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
            for (Node<T> node = this.head; node != null; node = node.next) {
                historyInList.add(node.task);
            }
            return historyInList;
        }
    }
}


