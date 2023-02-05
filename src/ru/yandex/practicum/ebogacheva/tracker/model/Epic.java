package ru.yandex.practicum.ebogacheva.tracker.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subIds;

    public Epic(String title, String description, long duration) {
        super(title, description, duration);
        subIds = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(int id,
                String title,
                String description,
                Status status,
                List<Integer> subIds,
                long duration) {
        super(id, title, description, status, duration);
        this.subIds = new ArrayList<>(subIds);
        this.type = TaskType.EPIC;
    }

    public Epic (Epic epic) {
        this(epic.getId(),
                epic.getTitle(),
                epic.getDescription(),
                epic.getStatus(),
                epic.getSubIds(),
                epic.getDuration().toMinutes());
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
    public String toString() {
        return "Epic{"+
                "ID=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + this.status +
                ", duration=" + duration.toString() +
                ", subTasks=" + subIds +
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
                this.duration.toString());
    }

}
