package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, Subtask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, Subtask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String toString() {
        return "Epic{"+
                "ID=" + getID() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + this.status +
                ", subTasks=" + subTasks + "\n" +
                '}';
    }
}
