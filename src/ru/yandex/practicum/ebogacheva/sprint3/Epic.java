package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Subtask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                " ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subTasks=" + subTasks + "\n" +
                '}';
    }
}
