package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.time.LocalDate;

public class PlayerStatData {
    private String Username;
    private Integer Points;
    private Integer Days;
    private Integer Gems;
    private boolean Completed;

    private List<Task> CompletedTasks;
    private Queue<Boolean> Last7Days;

    // temp data for backup
    private Queue<Boolean> Last7DaysTemp;
    private List<Task> CompletedTasksTemp;
    private Integer PointsTemp;
    private Integer DaysTemp;

    public PlayerStatData(String username, Integer points, Integer days) {
        this.Username = username;
        this.Points = points;
        this.Days = days;
        this.Gems = 100; // default gems
        this.Completed = false;

        this.CompletedTasks = new ArrayList<Task>();
        this.Last7Days = new LinkedList<Boolean>();
        this.CompletedTasksTemp = new ArrayList<Task>();
        this.Last7DaysTemp = new LinkedList<Boolean>();

        for (int i = 0; i < 6; i++) {
            this.Last7Days.add(false);
            this.Last7DaysTemp.add(false);
        }
    }

    public Integer CalulatePointsToBeEarned(Task T) {
        int EffectiveStreak = Math.min(getDays(), 14);
        double Points = T.getPointsAmount() * (EffectiveStreak+1);
        return (int) Points;
    }

    public void UpdateValue(Task Task) {
        if(isCompleted()){
            int Points = CalulatePointsToBeEarned(Task);
            setPoints(getPoints() + Points);
            setDays(getDays() + 1);
            PointsTemp = getPoints();
            DaysTemp = getDays();
            Task.setCompletedAt(LocalDate.now().toString());
            Task.setCompleted(true);
            CompletedTasks.add(Task);
            CompletedTasksTemp.add(Task);
            Last7Days.poll();
            Last7Days.add(true);
            Last7DaysTemp.poll();
            Last7DaysTemp.add(true);
            setCompleted(false);
        }else {
            CompletedTasksTemp.clear();
            CompletedTasksTemp.addAll(CompletedTasks);
            Last7DaysTemp.clear();
            Last7DaysTemp.addAll(Last7Days);
            PointsTemp = getPoints();
            DaysTemp = getDays();

            setPoints(0);
            setDays(0);
            CompletedTasks.clear();
            for (int i = 0; i < 6; i++) {
                Last7Days.poll();
                Last7Days.add(false);
            }
        }
    }

    public Integer RestoreTemp() {
        if (Gems >= 100) {
            CompletedTasks.clear();
            CompletedTasks.addAll(CompletedTasksTemp);
            Last7Days.clear();
            Last7Days.addAll(Last7DaysTemp);
            setGems(getGems() - 100);
            setPoints(PointsTemp);
            setDays(DaysTemp);
            return Gems+1;
        }else {
            return 0;
        }
    }


    public String getUsername() {
        return Username;
    }
    public Integer getPoints() {
        return Points;
    }
    public void setPoints(Integer points) {
        Points = points;
    }
    public Integer getDays() {
        return Days;
    }
    public void setDays(Integer days) {
        Days = days;
    }
    public boolean isCompleted() {
        return Completed;
    }
    public void setCompleted(boolean completed) {
        Completed = completed;
    }
    public Integer getGems() {
        return Gems;
    }
    public void setGems(Integer gems) {
        Gems = gems;
    }
    public void AddGems(Integer gems) {
        this.Gems += gems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStatData)) return false;
        PlayerStatData that = (PlayerStatData) o;
        return  (Username.equals(that.Username));
    }
}
