package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {
    private List<Map<String, Object>> table = new ArrayList<>();

    private Long id;
    private String lastname;
    private Double cost;
    private Long age;
    private Boolean active;

    public Table() {
    }

    public List<Map<String, Object>> getTable() {
        return table;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Table{" +
                "table=" + table +
                ", id=" + id +
                ", lastname='" + lastname + '\'' +
                ", cost=" + cost +
                ", age=" + age +
                ", active=" + active +
                '}';
    }
}
