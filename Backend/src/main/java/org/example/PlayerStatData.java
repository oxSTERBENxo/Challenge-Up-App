package org.example;

public class PlayerStatData {
    private String Username;
    private Integer Points;
    private Integer Days;
    private boolean Compleated;

    public PlayerStatData(String username, Integer points, Integer days) {
        this.Username = username;
        this.Points = points;
        this.Days = days;
        this.Compleated = false;
    }

    public void UpdateValue() {
        if(isCompleted()){
            int EffectiveStreak = Math.max(getDays(), 10);
            int Points = 50 + ((int)Math.pow(EffectiveStreak, 1.5) * 5);
            setPoints(getPoints() + Points);
            setDays(getDays() + 1);
        }else {
            setDays(0);
            setPoints(0);
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
        return Compleated;
    }
    public void setCompleted(boolean completed) {
        Compleated = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStatData)) return false;
        PlayerStatData that = (PlayerStatData) o;
        return  (Username.equals(that.Username));
    }
}
