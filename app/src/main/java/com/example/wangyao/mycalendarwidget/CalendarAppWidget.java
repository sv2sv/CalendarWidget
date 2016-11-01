package com.example.wangyao.mycalendarwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class CalendarAppWidget extends AppWidgetProvider {
    private final String TAG = "CalendarAppWidget";
    private static final String ACTION_PREVIOUS_MONTH
            = "com.example.android.action.PREVIOUS_MONTH";
    private static final String ACTION_NEXT_MONTH
            = "com.example.android.action.NEXT_MONTH";
    private static final String ACTION_RESET_MONTH
            = "com.example.android.action.RESET_MONTH";
    public static final String COLLECTION_VIEW_ACTION = "com.example.android.COLLECTION_VIEW_ACTION";

    public static final String PREF_MONTH = "month";
    public static final String PREF_YEAR = "year";

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        boolean shortMonthName = false;
        boolean mini = false;
        int numweeks =  6;
        if(widgetOptions!=null){
            int minWidthDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int minHeightDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

            // 小于 240(4格)，年月显示变短
            shortMonthName = minWidthDp <= 240 ;
            //小于180（3格），不显示年月
            mini = minHeightDp <= 180 ;
            if(mini){
                numweeks = minHeightDp <= 120 ? 1 : 2;
            }
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.new_app_widget);

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int todayYear = cal.get(Calendar.YEAR);
        int thisMonth;
        if(!mini){
            thisMonth = sp.getInt(PREF_MONTH, cal.get(Calendar.MONTH));
            int thisYear = sp.getInt(PREF_YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, thisMonth);
            cal.set(Calendar.YEAR, thisYear);
        }
        else {
            thisMonth = cal.get(Calendar.MONTH);
        }

        remoteViews.setTextViewText(R.id.system, android.text.format.DateFormat.format(shortMonthName ? "MMM yy" : "MMM yyyy" ,cal));
        if (!mini) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
        } else {
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
        }
        //
        int num = 35;
        if(mini) {
            if (numweeks == 1) {
                num = 7;
            } else if (numweeks == 2) {
                num = 14;
            }
        }else
            num = 35;
//        datas=new ArrayList<>();
//        for (int i = 0 ; i<num;i++){
//            boolean inMonth = cal.get(Calendar.MONTH) == thisMonth;
//            boolean inYear  = cal.get(Calendar.YEAR) == todayYear;
//            boolean isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);
//            datas.add(i,new Day(inMonth,isToday,cal.get(Calendar.DAY_OF_MONTH)));
//            cal.add(Calendar.DAY_OF_MONTH,1);
//        }
        sp.edit().putBoolean("mini",mini)
                .putInt("num",num)
                .putInt("thisMonth",thisMonth)
                .commit();

        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekDays = dfs.getShortWeekdays();

        remoteViews.setTextViewText(R.id.week1,weekDays[1]);
        remoteViews.setTextColor(R.id.week1, Color.RED);
        remoteViews.setTextViewText(R.id.week2,weekDays[2]);
        remoteViews.setTextViewText(R.id.week3,weekDays[3]);
        remoteViews.setTextViewText(R.id.week4,weekDays[4]);
        remoteViews.setTextViewText(R.id.week5,weekDays[5]);
        remoteViews.setTextViewText(R.id.week6,weekDays[6]);
        remoteViews.setTextViewText(R.id.week7,weekDays[7]);
        remoteViews.setTextColor(R.id.week7, Color.BLUE);
        remoteViews.setTextColor(R.id.reseet,Color.GREEN);
        Intent intentService = new Intent(context,MyCalendarService.class);
        intentService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        remoteViews.setRemoteAdapter(R.id.grid,intentService);

        remoteViews.setPendingIntentTemplate(R.id.grid,PendingIntent.getBroadcast(context,0,
                new Intent(COLLECTION_VIEW_ACTION).setClass(context,this.getClass()).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId),
                PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setViewVisibility(R.id.pre,mini ? View.GONE : View.VISIBLE);
        remoteViews.setViewVisibility(R.id.next,mini ? View.GONE : View.VISIBLE);
        remoteViews.setViewVisibility(R.id.header,numweeks==1 ? View.GONE : View.VISIBLE);
//        remoteViews.setViewVisibility(R.id.system,numweeks==1? View.GONE : View.VISIBLE);
//        remoteViews.setViewVisibility(R.id.reseet,numweeks==1? View.GONE : View.VISIBLE);
        remoteViews.setViewVisibility(R.id.weeks,View.VISIBLE);
        remoteViews.setViewVisibility(R.id.grid,View.VISIBLE);
        remoteViews.setOnClickPendingIntent(R.id.pre,PendingIntent.getBroadcast(context,0,new Intent(ACTION_PREVIOUS_MONTH).setClass(context,CalendarAppWidget.class),PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.next,PendingIntent.getBroadcast(context,0,new Intent(ACTION_NEXT_MONTH).setClass(context,CalendarAppWidget.class),PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.reseet,PendingIntent.getBroadcast(context,0,new Intent(ACTION_RESET_MONTH).setClass(context,CalendarAppWidget.class),PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.system,PendingIntent.getActivity(context,0,new Intent()
                .setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity")),PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setPendingIntentTemplate(R.id.grid,PendingIntent.getBroadcast(context,0,
                new Intent()
                        .setAction(COLLECTION_VIEW_ACTION)
                        .setClass(context,this.getClass())
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
                        ,PendingIntent.FLAG_UPDATE_CURRENT));
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.grid);



    }

    private void update(Context context) {
       AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, CalendarAppWidget.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager,appWidgetId);

        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(ACTION_PREVIOUS_MONTH.equals(action)){
            Calendar calendar = Calendar.getInstance();
            // 获取sp中的  年  月
            int thisMonth = sp.getInt(PREF_MONTH,calendar.get(Calendar.MONTH));
            int thisyear = sp.getInt(PREF_YEAR,calendar.get(Calendar.YEAR));
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.set(Calendar.MONTH,thisMonth);
            calendar.set(Calendar.YEAR,thisyear);
            // 减一个月，
            calendar.add(Calendar.MONTH,-1);
            sp.edit()
                    .putInt(PREF_MONTH, calendar.get(Calendar.MONTH))
                    .putInt(PREF_YEAR, calendar.get(Calendar.YEAR))
                    .apply();

        }else if (ACTION_NEXT_MONTH.equals(action)){
            Calendar calendar = Calendar.getInstance();
            // 获取sp中的  年  月
            int thisMonth = sp.getInt(PREF_MONTH,calendar.get(Calendar.MONTH));
            int thisyear = sp.getInt(PREF_YEAR,calendar.get(Calendar.YEAR));
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.set(Calendar.MONTH,thisMonth);
            calendar.set(Calendar.YEAR,thisyear);
            // 加一个月，
            calendar.add(Calendar.MONTH,1);
            sp.edit()
                    .putInt(PREF_MONTH, calendar.get(Calendar.MONTH))
                    .putInt(PREF_YEAR, calendar.get(Calendar.YEAR))
                    .apply();

        }else if (ACTION_RESET_MONTH.equals(action)){
            sp.edit().remove(PREF_MONTH).remove(PREF_YEAR).apply();
        }else if(COLLECTION_VIEW_ACTION.equals(action)){
            Toast.makeText(context,"position"+intent.getIntExtra(COLLECTION_VIEW_ACTION,-1),Toast.LENGTH_LONG).show();
        }
        update(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.i(TAG, "onAppWidgetOptionsChanged: ");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidget(context,appWidgetManager,appWidgetId);
    }
}

