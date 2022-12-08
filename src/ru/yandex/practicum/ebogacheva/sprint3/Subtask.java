package ru.yandex.practicum.ebogacheva.sprint3;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic=epic;
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "ID=" + getID() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicID=" + epic.getID() +
                '}';
    }
}
