package ru.yandex.practicum.ebogacheva.tracker.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    public Status status;
    private List<Integer> subIds;

    public Epic(String title, String description) {
        super(title, description);
        subIds = new ArrayList<>();
    }

    public List<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(List<Integer> subIds) {
        this.subIds = subIds;
        if (this.subIds == null) {
            this.subIds = new ArrayList<>();            
        }
    }

    public void removeSubTask(int id) {
        subIds.remove((Integer)id);
    }

    public void addSubTask(int id) {
        subIds.add(id);
    }

    public void clearSubTasks() {
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
                ", subTasks=" + subIds +
                '}';
    }
}
