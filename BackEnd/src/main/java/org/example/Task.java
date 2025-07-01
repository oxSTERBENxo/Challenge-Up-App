package org.example;

import java.time.LocalDate;
import java.util.List;

public class Task {
    private String ToDo;
    private String completedAt;
    private Integer PointsAmount;

    public Task (String ToDo) {
        this.ToDo = ToDo;
        this.completedAt = "1970-01-01"; // default date
        this.PointsAmount = 10; // default points
    }
    public Task (String ToDo, String completedAt, Integer PointsAmount) {
        this.ToDo=ToDo;
        this.completedAt = completedAt;
        this.PointsAmount = PointsAmount;
    }
    public Task() {}

    public String getToDo() {
        return ToDo;
    }
    public void setToDo(String toDo) {
        ToDo = toDo;
    }
    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getPointsAmount() {
        return PointsAmount;
    }

    public void setPointsAmount(Integer pointsAmount) {
        PointsAmount = pointsAmount;
    }
}
