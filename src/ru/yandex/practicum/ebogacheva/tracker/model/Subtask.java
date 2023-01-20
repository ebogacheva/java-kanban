package ru.yandex.practicum.ebogacheva.tracker.model;

public class Subtask extends Task {

    protected int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
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
                String.valueOf(this.epicId));
    }
}
