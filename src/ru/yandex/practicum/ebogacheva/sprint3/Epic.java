package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<Subtask> subTasks) {
        this.subTasks=subTasks;
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
