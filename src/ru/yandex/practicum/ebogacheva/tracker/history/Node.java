package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.model.Task;

class Node<T extends Task> {

    final T task;
    Node<T> next;
    Node<T> prev;

    Node(Node<T> prev, T task, Node<T> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

}
