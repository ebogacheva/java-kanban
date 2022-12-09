package ru.yandex.practicum.ebogacheva.sprint3;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subIds;

    public Epic(String title, String description) {
        super(title, description);
        subIds = new ArrayList<>();
    }

    public List<Integer> getSubIds() {
        return subIds;
    }

    void setSubIds(List<Integer> subIds) {
        this.subIds = subIds;
        if (this.subIds == null) {
            this.subIds = new ArrayList<>();            
        }
    }

    void removeSubTask(int id) {
        subIds.remove((Integer)id);
    }

    void addSubTask(int id) {
        subIds.add(id);
    }

    void clearSubTasks() {
        subIds.clear();
        status = Status.NEW;
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("ОШИБКА: Статус Epic не может быть установлен.");
    }

    @Override
    public String toString() {
        return "Epic{"+
                "ID=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + this.status +
                ", subTasks=" + subIds + "\'" +
                '}';
    }
}
