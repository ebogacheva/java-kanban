package ru.yandex.practicum.ebogacheva.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {

    protected int id;
    protected final String title;
    protected final String description;
    protected Status status;
    protected TaskType type = TaskType.TASK;
    protected Duration duration;
    protected LocalDateTime startDateTime;


    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ZERO;
        this.startDateTime= null;
    }

    public Task(String title, String description, long duration) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startDateTime= null;
    }

    public Task(String title, String description, long duration, String startDateTime) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startDateTime= getStartDateTimeFormatted(startDateTime);
    }

    private LocalDateTime getStartDateTimeFormatted(String input) {
        LocalDateTime outputDateTime = null;
        try {
            outputDateTime = LocalDateTime.parse(input, TaskManagerConstants.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
        }
        return outputDateTime;
    }

    private LocalDateTime getEndDateTimeFormatted() {
        LocalDateTime output = null;
        if (this.startDateTime != null) {
            output = this.startDateTime.plusMinutes(this.duration.toMinutes());
        }
        return output;
    }

    public Task(int id,
                String title,
                String description,
                Status status,
                long duration,
                LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startDateTime= startTime;
    }

    public Task(Task task) {
        this(task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration().toMinutes(),
                task.getStartDateTime());
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

    public void setDuration(Duration duration) {this.duration = duration;}

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return getEndDateTimeFormatted();
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
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
        String time = getStartEndTime();
        return "Task{" +
                "ID=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toString() +
                ", start=" + time +
                '}';
    }

    public String toFileString() {
        String time = getStartEndTime();
        return String.join(",",
                String.valueOf(this.id),
                this.type.name(),
                this.title,
                this.status.name(),
                this.description,
                this.duration.toString(),
                time);
    }

    protected String getStartEndTime() {
        String time;
        if (startDateTime == null) {
            time = "null";
        } else {
            time = startDateTime.toString();
        }
        return time;
    }

}

