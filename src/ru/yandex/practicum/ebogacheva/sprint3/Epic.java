package ru.yandex.practicum.ebogacheva.sprint3;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subIds;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        subIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(ArrayList<Integer> subIds) {
        this.subIds = subIds;
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Статус не может быть установлен.");
    }

    @Override
    public String toString() {
        return "Epic{"+
                "ID=" + getID() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + this.status +
                ", subTasks=" + subIds + "\n" +
                '}';
    }
}
