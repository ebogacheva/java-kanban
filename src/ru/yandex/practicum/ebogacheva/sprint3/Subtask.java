package ru.yandex.practicum.ebogacheva.sprint3;

public class Subtask extends Task {

    public int epicID;

    public Subtask(String title, String description, int epicID) {
        super(title, description);
        this.type = TaskType.SUB_TASK;
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", epicID=" + epicID +
                '}';
    }
}
