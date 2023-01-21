package ru.yandex.practicum.ebogacheva.tracker.model;

import java.util.Objects;

public class Task {

    protected int id;
    protected final String title;
    protected final String description;
    protected Status status;
    protected TaskType type = TaskType.TASK;

    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(Task task) {
        this(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task=(Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toFileString() {
        return String.join(",",
                String.valueOf(this.id),
                this.type.name(),
                this.title,
                this.status.name(),
                this.description);
    }

}

