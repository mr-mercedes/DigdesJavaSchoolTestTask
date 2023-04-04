package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {
    protected static final List<Map<String, Object>> TABLE = new ArrayList<>();
    private static Long id;
    private static String lastname;
    private static Double cost;
    private static Long age;
    private static Boolean active;

    public Table() {
    }

    public static Long getId() {
        return id;
    }

    public static void setId(Long id) {
        Table.id = id;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        Table.lastname = lastname;
    }

    public static Double getCost() {
        return cost;
    }

    public static void setCost(Double cost) {
        Table.cost = cost;
    }

    public static Long getAge() {
        return age;
    }

    public static void setAge(Long age) {
        Table.age = age;
    }

    public static Boolean getActive() {
        return active;
    }

    public static void setActive(Boolean active) {
        Table.active = active;
    }

    @Override
    public String toString() {
        return "Table{" +
                "table=" + TABLE +
                ", id=" + id +
                ", lastname='" + lastname + '\'' +
                ", cost=" + cost +
                ", age=" + age +
                ", active=" + active +
                '}';
    }
}
