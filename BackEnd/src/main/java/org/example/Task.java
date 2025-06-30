package org.example;

import java.time.LocalDate;
import java.util.List;

public class Task {
    private String ToDo;
    private LocalDate completedAt;
    private List<String> classes;

    public Task (String ToDo) {
        this.ToDo = ToDo;
    }
    public Task (String ToDo, LocalDate completedAt, List<String> classes){
        this.ToDo=ToDo;
        this.completedAt = completedAt;
        this.classes = classes;
    }
    public Task() {}

    public String getToDo() {
        return ToDo;
    }
    public void setToDo(String toDo) {
        ToDo = toDo;
    }
    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
}
