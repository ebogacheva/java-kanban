package ru.yandex.practicum.ebogacheva.tracker.model;

import java.time.LocalDateTime;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, long duration, int epicId) {
        super(title, description, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, long duration, String startDateTime, int epicId) {
        super(title, description, duration, startDateTime);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id,
                   String title,
                   String description,
                   Status status,
                   long duration,
                   LocalDateTime startDateTime,
                   int epicId) {
        super(id, title, description, status, duration, startDateTime);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getId(),
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getDuration().toMinutes(),
                subtask.getStartDateTime(),
                subtask.getEpicId());
    }

    public int getEpicId() {
        return this.epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void resetEpicId() {
        this.epicId = -1;
    }
    
    public boolean isAttachedToEpic() {
        return this.epicId > -1;
    }

    @Override
    public String toString() {
        String task = super.toString();
        String taskShortened = task.substring(0, task.length() - 2);
        return taskShortened +
                ", epicID=" + epicId +
                '}';
    }

    @Override
    public String toFileString() {
        return String.join(",",
                super.toFileString(),
                String.valueOf(this.epicId));
    }
}
