package ru.yandex.practicum.ebogacheva.tracker.tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
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
}
