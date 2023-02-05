package ru.yandex.practicum.ebogacheva.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

public class Task {

    protected int id;
    protected final String title;
    protected final String description;
    protected Status status;
    protected TaskType type = TaskType.TASK;
    protected Duration duration;

    protected LocalDateTime startTime;
    protected LocalDateTime endTime;


    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;
    }

    public Task(String title, String description, long duration) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = null;
        this.endTime = null;
    }

    public Task(String title, String description, long duration, String startDateTime) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = getStartDateTimeFormatted(startDateTime);
        this.endTime = getEndDateTimeFormatted();
    }

    private LocalDateTime getStartDateTimeFormatted(String input) {
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime outputDateTime = null;
        try {
            outputDateTime = LocalDateTime.parse(input, formatterDateTime);
        } catch (DateTimeParseException ignored) {
        }
        return outputDateTime;
    }

    private LocalDateTime getEndDateTimeFormatted() {
        LocalDateTime output = null;
        if (this.startTime != null) {
           output = this.startTime.plusMinutes(this.duration.toMinutes());
        }
        return output;
    }

    public Task(int id,
                String title,
                String description,
                Status status,
                long duration,
                LocalDateTime startTime,
                LocalDateTime endTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(Task task) {
        this(task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration().toMinutes(),
                task.getStartTime(),
                task.getEndTime());
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime=startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime=endTime;
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
        String[] time = getStartEndTime();
        return "Task{" +
                "ID=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toString() +
                ", start=" + time[0] +
                ", end=" + time[1] +
                '}';
    }

    public String toFileString() {
        String[] time = getStartEndTime();
        return String.join(",",
                String.valueOf(this.id),
                this.type.name(),
                this.title,
                this.status.name(),
                this.description,
                this.duration.toString(),
                time[0],
                time[1]);
    }

    protected String[] getStartEndTime() {
        String[] time = new String[2];
        if (startTime == null) {
            time[0] = "null";
            time[1] = "null";
        } else {
            time[0] = startTime.toString();
            time[1] = endTime.toString();
        }
        return time;
    }

}

