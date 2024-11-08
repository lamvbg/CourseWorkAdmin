package com.example.courseworkadmin.ui.models;

public class ClassInstance {
    private int instanceId;
    private int courseId;
    private String date;
    private String teacher;
    private String comments;

    public ClassInstance( int courseId, String date, String teacher, String comments) {
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }

    public ClassInstance() {

    }

    // Getters and setters for the fields
    public int getInstanceId() { return instanceId; }
    public void setInstanceId(int instanceId) { this.instanceId = instanceId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

