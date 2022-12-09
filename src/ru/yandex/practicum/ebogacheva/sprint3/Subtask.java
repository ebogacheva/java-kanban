package ru.yandex.practicum.ebogacheva.sprint3;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    void resetEpicId() {
        this.epicId = -1;
    }
    
    public boolean isAttachedToEpic() {
        return this.epicId > -1;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "ID=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicID=" + epicId +
                '}';
    }

    // Касательно equals - не могу решить. Для меня вообще это тема сложная.
    // Если (по формату выбранного equals в task) мы считаем, что id не участвует в сравнение объектов,
    // то нам нужно заглядывать внутрь эпиков (обращаться в менеджер), если мы хотим добавлять их к сравнению.
    // Или менять в данном классе поле с epicId на объект Epic (я так пыталась делать в начале).
    // Таким образом получается конфликт по тому, как связаны / взаимодействуют объекты.
    // С другой стороны можно принять, что когда id это не свойство / поле объекта, а поле с информацией об объекте
    // в другом объекте, то его можно и нужно включать в equals / hashcode.
    // Тут я запуталась окончательно.
}
