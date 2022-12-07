package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private static final AtomicInteger idProvider = new AtomicInteger(0);
    protected final int ID;
    protected final String title;
    protected final String description;
    protected Status status;
    protected TaskType type;

    public Task(String title, String description) {
        this.ID = idProvider.incrementAndGet();
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.type = TaskType.TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}

