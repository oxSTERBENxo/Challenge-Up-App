package org.example;

public class Task {
    private String ToDo;

    public Task (String ToDo) {
        this.ToDo = ToDo;
    }
    public Task (){
    }

    public String getToDo() {
        return ToDo;
    }
    public void setToDo(String toDo) {
        ToDo = toDo;
    }
}
