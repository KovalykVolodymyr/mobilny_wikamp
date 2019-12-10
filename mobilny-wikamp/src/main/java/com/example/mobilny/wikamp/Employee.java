package com.example.mobilny.wikamp;

public class Employee {
    String name;
    String title;
    String workingPlace;

    public Employee () {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    @Override
    public String toString() {
        return "{" + name + "}&{" + title + "}&{" + workingPlace + "}";
    }
}
