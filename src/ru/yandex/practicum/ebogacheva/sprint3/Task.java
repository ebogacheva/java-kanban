package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private int ID;
    protected final String title;
    protected final String description;
    protected Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status=status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public Task(String title, String description, Status status) {
        this.ID = 0;
        this.title = title;
        this.description = description;
        this.status = status;
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

