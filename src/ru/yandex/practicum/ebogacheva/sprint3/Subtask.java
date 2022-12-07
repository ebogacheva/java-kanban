package ru.yandex.practicum.ebogacheva.sprint3;

public class Subtask extends Task {

    public Epic epic;

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.type = TaskType.SUB_TASK;
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicID=" + epic.ID +
                '}';
    }
}
