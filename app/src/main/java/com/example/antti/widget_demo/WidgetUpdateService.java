package com.example.antti.widget_demo;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Antti on 10-Jun-16.
 */
public class WidgetUpdateService extends Service {

    public static ArrayList<WorkDay> dates;
    public static boolean workToday = false;
    public static SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");



    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //buildUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate()
    {
        /*
        String type ="beer";
        final OmaDbAdapteri db = new OmaDbAdapteri(this);
        dates = new ArrayList<WorkDay>();
        ArrayList<String> datesString = new ArrayList<>();

        try {
            dates = db.haeKaikki();
            db.puraKantayhteys();
            dates = MainActivity.removePastDays(dates);
            datesString = MainActivity.shiftsToString(datesString);
            workToday = MainActivity.checkCurrentShift(dates);
            MainActivity.refreshCount++;


            if (datesString.size() > 2){
                if ((workToday)) {
                    MainActivity.line1 = "Workday ahead!"
                            + "\n" + datesString.get(0);
                    MainActivity.line2 = "\nNext workday"
                            + "\n" + datesString.get(1);
                    type = "money";
                } else {

                    MainActivity.line1 = "No work today!";
                    MainActivity.line2 = datesString.get(0);
                    type = "beer";

                }
            }

        } catch (Exception e) {
            Log.e("Create()", e.toString());
        }

        try {
            Class res = R.drawable.class;
            Field field = res.getField(type);
            int drawableId = field.getInt(null);

            RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);
            view.setTextViewText(R.id.textView, MainActivity.line1);
            view.setTextViewText(R.id.textView2, MainActivity.line2);
            view.setImageViewResource(R.id.imageView, drawableId);
            view.setTextViewText(R.id.textView3, "" + MainActivity.refreshCount);

            ComponentName thisWidget = new ComponentName(this, SimpleWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, view);

            //Log.e("Drawable", MainActivity.type);

        } catch (Exception e){
            Log.e("Drawable", "Failure to get drawable id.", e);
        }

        */
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
