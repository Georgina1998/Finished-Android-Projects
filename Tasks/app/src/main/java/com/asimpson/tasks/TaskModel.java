package com.asimpson.tasks;

public class TaskModel {
    private int id;
    private String title;
    private String description;
    private boolean done;

    public TaskModel(int id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public TaskModel(){

    }

    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }



    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }



}
