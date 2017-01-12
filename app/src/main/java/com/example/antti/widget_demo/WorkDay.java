package com.example.antti.widget_demo;

/**
 * Created by Anderssi on 13.6.2016.
 */
public class WorkDay {

    String workday, shift;

    public WorkDay(String workday, String shift) {
        super();
        this.workday = workday;
        this.shift = shift;
    }

    public WorkDay() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getWorkday() {
        return workday;
    }

    public void setWorkday(String workday) {
        this.workday = workday;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "WorkDay [workday=" + workday + ", shift=" + shift + "]";
    }

}
