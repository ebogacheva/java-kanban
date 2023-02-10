package ru.yandex.practicum.ebogacheva.tracker.history;

class Node<T> {

    final T task;
    Node<T> next;
    Node<T> prev;

    Node(Node<T> prev, T task, Node<T> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

}
