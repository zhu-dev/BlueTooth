package com.example.mybluetooth.manager;

import java.util.Date;

public class UserBean {
    private int sys_pressure;//收缩压
    private int dia_pressure;//舒张压
    private String name;
    private int id;

    private Date date;
    private String dateStr;
    private String timeStr;
    public UserBean() {
    }

    public int getSys_pressure() {
        return sys_pressure;
    }

    public void setSys_pressure(int sys_pressure) {
        this.sys_pressure = sys_pressure;
    }

    public int getDia_pressure() {
        return dia_pressure;
    }

    public void setDia_pressure(int dia_pressure) {
        this.dia_pressure = dia_pressure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }
    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
}
