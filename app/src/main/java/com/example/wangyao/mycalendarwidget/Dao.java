package com.example.wangyao.mycalendarwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wangyao on 1/11/16.
 */

public class Dao {
    private Context mContext;
    private SharedPreferences sp;
    private int thisMonth;
    private boolean mini;
    private int num;
    List<Day> days;
    public Dao(Context mContext) {
        this.mContext = mContext;
        sp= PreferenceManager.getDefaultSharedPreferences(mContext);
        days = new ArrayList<>();
        mini = sp.getBoolean("mini",false);
        num = sp.getInt("num",35);
    }
    public List<Day> getDatas(){



        Calendar cal =Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int todayYear = cal.get(Calendar.YEAR);
        if(!mini){
            thisMonth = sp.getInt(CalendarAppWidget.PREF_MONTH, cal.get(Calendar.MONTH));
            int thisYear = sp.getInt(CalendarAppWidget.PREF_YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, thisMonth);

            cal.set(Calendar.YEAR, thisYear);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
        }
        else {
            thisMonth = cal.get(Calendar.MONTH);
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
        }

        for (int i = 0 ; i<num;i++){
            boolean inMonth = cal.get(Calendar.MONTH) == thisMonth;
            boolean inYear  = cal.get(Calendar.YEAR) == todayYear;
            boolean isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);
            days.add(i,new Day(inMonth,isToday,cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH,1);
        }
            return days;
    }
      public boolean com(){
          return  days.size()==num;
      }

}
