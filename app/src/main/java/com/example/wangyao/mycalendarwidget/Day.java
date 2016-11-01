package com.example.wangyao.mycalendarwidget;

/**
 * Created by wangyao on 31/10/16.
 */

public class Day {
    boolean inMonth;
    boolean isToday;
    int day;

    public Day(boolean isMonth, boolean isToday, int day) {
        this.inMonth = isMonth;
        this.isToday = isToday;
        this.day = day;
    }
}
