package ru.yandex.practicum.ebogacheva.sprint3;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subIds;

    public Epic(String title, String description) {
        super(title, description);
        subIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(ArrayList<Integer> subIds) {
        this.subIds = subIds;
    }

    void removeSubTask(int id) {
        if (this.subIds != null) {
            subIds.remove((Integer)id);
        }
    }

    void addSubTask(int id) {
        if (this.subIds == null) {
            subIds = new ArrayList<>();
        }
        subIds.add(id);
    }

    void clearSubTasks() {
        if (this.subIds != null) {
            subIds.clear();
        }
        status = Status.NEW;
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
                ", subTasks=" + subIds + "\'" +
                '}';
    }
}
