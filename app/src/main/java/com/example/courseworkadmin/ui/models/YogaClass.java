package com.example.courseworkadmin.ui.models;

public class YogaClass {
    private int id;  // Unique identifier for each class
    private String dayOfWeek;
    private String timeOfCourse; // Renamed for clarity
    private int capacity;
    private int duration;
    private double price;
    private String typeOfClass;
    private String description;
    private String title;

    // Constructor without id (for new records)
    public YogaClass(String dayOfWeek, String timeOfCourse, int capacity, int duration, double price, String typeOfClass, String description, String title) {
        this.dayOfWeek = dayOfWeek;
        this.timeOfCourse = timeOfCourse;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.typeOfClass = typeOfClass;
        this.description = description;
        this.title = title;
    }

    public YogaClass() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof YogaClass)) return false;
        YogaClass other = (YogaClass) obj;
        return id == other.id &&
                capacity == other.capacity &&
                duration == other.duration &&
                Double.compare(other.price, price) == 0 &&
                dayOfWeek.equals(other.dayOfWeek) &&
                timeOfCourse.equals(other.timeOfCourse) &&
                typeOfClass.equals(other.typeOfClass) &&
                description.equals(other.description) &&
                title.equals(other.title);
    }


    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTimeOfCourse() {
        return timeOfCourse;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public String getTypeOfClass() {
        return typeOfClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setTimeOfCourse(String timeOfCourse) {
        this.timeOfCourse = timeOfCourse;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCapacity(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1.");
        this.capacity = capacity;
    }

    public void setDuration(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive.");
        this.duration = duration;
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public void setTypeOfClass(String typeOfClass) {
        this.typeOfClass = typeOfClass;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "YogaClass{" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", courseTime='" + timeOfCourse + '\'' +
                ", capacity=" + capacity +
                ", duration=" + duration +
                ", price=" + price +
                ", typeOfClass='" + typeOfClass + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
