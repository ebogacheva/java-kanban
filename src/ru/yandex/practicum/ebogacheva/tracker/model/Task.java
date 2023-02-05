package ru.yandex.practicum.ebogacheva.tracker.model;

import java.time.Duration;
import java.util.Objects;

public class Task {

    protected int id;
    protected final String title;
    protected final String description;
    protected Status status;
    protected TaskType type = TaskType.TASK;
    protected Duration duration;


    public Task(String title, String description, long duration) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration= Duration.ofMinutes(duration);
    }

    public Task(int id, String title, String description, Status status, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration= Duration.ofMinutes(duration);
    }

    public Task(Task task) {
        this(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getDuration().toMinutes());
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

    public Duration getDuration() {return this.duration;}

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
                ", duration=" + duration.toString() +
                '}';
    }

    public String toFileString() {
        return String.join(",",
                String.valueOf(this.id),
                this.type.name(),
                this.title,
                this.status.name(),
                this.description,
                this.duration.toString());
    }

}

