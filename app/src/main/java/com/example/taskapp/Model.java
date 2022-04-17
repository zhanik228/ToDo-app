package com.example.taskapp;

public class Model {

    private String task, description, id, date, planToDate;

    public Model() {}


    public Model(String task, String description, String id, String date, String planToDate) {
        this.task = task;
        this.description = description;
        this.id = id;
        this.date = date;
        this.planToDate = planToDate;
    }


    public String getPlanToDate() {
        return planToDate;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
