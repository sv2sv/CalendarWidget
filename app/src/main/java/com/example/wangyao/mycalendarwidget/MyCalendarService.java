package com.example.wangyao.mycalendarwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.wangyao.mycalendarwidget.MyFactory.TAG;


/**
 * Created by wangyao on 31/10/16.
 */

public class MyCalendarService extends RemoteViewsService {
    @Override
    public android.widget.RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG, "onGetViewFactory: ");
        return new MyFactory(intent,this);
    }

}
class MyFactory implements RemoteViewsService.RemoteViewsFactory{
    public static  String TAG = "MyFactory";
    private Context mContext;
 //   private SharedPreferences sp;
//    private int num = 42;
//    private int thisMonth;
//    private boolean mini;
//    private List<Day> nextDatas;
    private int mAppWidgetId ;
    private List<Day> datas;

    public MyFactory(Intent intent, Context context) {

        mContext = context;
        mAppWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
        Log.i(TAG, "MyFactory: "+mAppWidgetId);
//        sp = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");


    }

    @Override
    public void onDataSetChanged() {

//        mini = sp.getBoolean("mini",false);
//        num = sp.getInt("num",42);
//        Log.i(TAG, "onDataSetChanged: num:"+num);
//         Calendar cal =Calendar.getInstance();
//        int today = cal.get(Calendar.DAY_OF_YEAR);
//        int todayYear = cal.get(Calendar.YEAR);
//        if(!mini){
//            thisMonth = sp.getInt(CalendarAppWidget.PREF_MONTH, cal.get(Calendar.MONTH));
//            int thisYear = sp.getInt(CalendarAppWidget.PREF_YEAR, cal.get(Calendar.YEAR));
//            cal.set(Calendar.DAY_OF_MONTH, 1);
//            cal.set(Calendar.MONTH, thisMonth);
//
//            cal.set(Calendar.YEAR, thisYear);
//            cal.set(Calendar.DAY_OF_MONTH, 1);
//            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
//            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
//        }
//        else {
//            thisMonth = cal.get(Calendar.MONTH);
//            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
//            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
//        }
//      datas.clear();
//        for (int i = 0 ; i<num;i++){
//            boolean inMonth = cal.get(Calendar.MONTH) == thisMonth;
//            boolean inYear  = cal.get(Calendar.YEAR) == todayYear;
//            boolean isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);
//            datas.add(i,new Day(inMonth,isToday,cal.get(Calendar.DAY_OF_MONTH)));
//            cal.add(Calendar.DAY_OF_MONTH,1);
//        }


                datas=new Dao(mContext).getDatas();



    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: "+datas.size());
        return datas.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
         Bundle widgetOptions = AppWidgetManager.getInstance(mContext).getAppWidgetOptions(mAppWidgetId);
        int miniheight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxheight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        Log.i(TAG, "getViewAt: "+miniheight+" "+maxheight);
        int num = datas.size();
        Log.i(TAG, "getViewAt: "+i);
        if(i<num) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item);
           //设置gridview的每项高度
            if(num==7){
                rv.setInt(R.id.textView,"setHeight",DensityUtil.dip2px(mContext,maxheight)-DensityUtil.dip2px(mContext,50));
            }else if(num==14){
                rv.setInt(R.id.textView,"setHeight",(DensityUtil.dip2px(mContext,maxheight)-DensityUtil.dip2px(mContext,90))/2);
            }else if(num==35){
                rv.setInt(R.id.textView,"setHeight",(DensityUtil.dip2px(mContext,maxheight)-DensityUtil.dip2px(mContext,100))/5);
            }

            // 清除上次数据遗留
            rv.setTextColor(R.id.textView, Color.WHITE);
            rv.setInt(R.id.textView,"setBackgroundColor",Color.TRANSPARENT);
            if (datas.get(i).inMonth)
            rv.setInt(R.id.textView,"setBackgroundColor",Color.GRAY);


            if (datas.get(i).isToday) {
                rv.setTextColor(R.id.textView, Color.GREEN);
            } else if (i % 7 == 0) {
                rv.setTextColor(R.id.textView, Color.RED);
            } else if (i % 7 == 6) {
                rv.setTextColor(R.id.textView, Color.BLUE);
            }
//        rv.setTextViewText(R.id.textView,Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
            rv.setTextViewText(R.id.textView, datas.get(i).day + "");


            //  cal.add(Calendar.DAY_OF_MONTH, 1);

            //设置点击事件
            rv.setOnClickFillInIntent(R.id.item_layout, new Intent().putExtra(CalendarAppWidget.COLLECTION_VIEW_ACTION, i));
            rv.setViewVisibility(R.id.textView, View.VISIBLE);
            return rv;
        }
        else return null;
    }

    @Override
    public RemoteViews getLoadingView() {

        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        Log.i(TAG, "getItemId: "+i);
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        Log.i(TAG, "hasStableIds: ");
        return true;
    }


}