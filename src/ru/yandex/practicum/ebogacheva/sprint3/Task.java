package ru.yandex.practicum.ebogacheva.sprint3;


import java.util.Objects;

public class Task {

    private int id;
    protected final String title;
    protected final String description;
    protected Status status;

    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task=(Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
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

}

