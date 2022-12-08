package ru.yandex.practicum.ebogacheva.sprint3;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    void resetEpicId() {
        this.epicId = -1;
    }
    
    public boolean isAttachedToEpic() {
        return this.epicId > -1;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "ID=" + getID() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicID=" + epicId +
                '}';
    }
}
