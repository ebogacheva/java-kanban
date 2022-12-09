package ru.yandex.practicum.ebogacheva.sprint3;


import java.util.Objects;

public class Task {

    private int id;
    // Сделано. Я стараюсь придерживаться правил, но с короткими именами часто забываюсь.
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
    // На мой взгляд это важно сделать,
    // если у нас есть необходимость сравнивать и считать равными (при равенстве полей) объекты
    // с разными id (и ссылками / адресами) - для корректного манипулирвония задачами.
    // Иначе, если мы не считаем равными таски с разными id, то equals/hashcode не нужны (есть id). На мой взгляд.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task=(Task) o;
        return Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status);
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

