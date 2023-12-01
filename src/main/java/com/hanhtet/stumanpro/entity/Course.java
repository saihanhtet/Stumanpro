package com.hanhtet.stumanpro.entity;
import java.util.List;
import java.util.ArrayList;
public class Course {
    private String name;

    public Course(String id, String name, String price) {
        this.name = name;
        this.id = id;
        this.price = price;
    }

    private String id;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public List<Object> getAllDataAsList() {
        List<Object> courseData = new ArrayList<>();
        courseData.add(name);
        courseData.add(price);
        return courseData;
    }
}
