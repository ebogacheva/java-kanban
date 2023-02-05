package ru.yandex.practicum.ebogacheva.tracker.model;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(String title, String description, long duration, int epicId) {
        super(title, description, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(int id, String title, String description, Status status, long duration, int epicId) {
        super(id, title, description, status, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getId(),
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getDuration().toMinutes(),
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
        return "Subtask{" +
                "ID=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toString() +
                ", epicID=" + epicId +
                '}';
    }

    @Override
    public String toFileString() {
        return String.join(",",
                String.valueOf(this.id),
                this.type.name(),
                this.title,
                this.status.name(),
                this.description,
                this.duration.toString(),
                String.valueOf(this.epicId));
    }
}
