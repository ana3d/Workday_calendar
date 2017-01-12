package com.example.antti.widget_demo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Antti on 10-Jun-16.
 */
public class SimpleWidgetProvider extends AppWidgetProvider {

    private PendingIntent service = null;
    public static ArrayList<WorkDay> dates;
    public static boolean workToday = false;
    public static SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
    public static int refreshCount = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        /*
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, WidgetUpdateService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000*1, service);


        */

        final int count = appWidgetIds.length;

        String type = "";
        String line1 = "Widget not yet initialized";
        String line2 = "Widget not yet initialized";


        final OmaDbAdapteri db = new OmaDbAdapteri(context);
        dates = new ArrayList<WorkDay>();
        ArrayList<String> datesString = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];


            type = "beer";


            try {
                dates = db.haeKaikki();
                db.puraKantayhteys();
                dates = removePastDays(dates);
                datesString = shiftsToString(datesString);
                workToday = checkCurrentShift(dates);
                refreshCount++;


                if (datesString.size() > 2) {
                    if ((workToday)) {
                        line1 = "Workday ahead!"
                                + "\n" + datesString.get(0);
                        line2 = "\nNext workday"
                                + "\n" + datesString.get(1);
                        type = "money";
                    } else {

                        line1 = "No work today!";
                        line2 = "\nNext workday\n"+datesString.get(0);
                        type = "beer";

                    }
                }

                Class res = R.drawable.class;
                Field field = res.getField(type);
                int drawableId = field.getInt(null);



        /*
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


                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget);


                remoteViews.setTextViewText(R.id.textView, "" + line1);
                remoteViews.setTextViewText(R.id.textView2, "" +line2);
                remoteViews.setImageViewResource(R.id.imageView, drawableId);
                //remoteViews.setTextViewText(R.id.textView3, "" + refreshCount);

                Intent intent = new Intent(context, SimpleWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);


            } catch (Exception e) {
                Log.e("Update()", e.toString());
            }
        }


    }

    //Checks if workday = true, if true widget shows current workday + next workday
    public static boolean checkCurrentShift(ArrayList<WorkDay> dates) {

        //for (int i = 0; i < dates.size(); i++) {
        try {
            Date compareDate = form.parse(dates.get(0).getWorkday());

            if (isToday(compareDate.getTime())) {

                //Log.e("Date found:", "" + isToday(compareDate.getTime())+ "\n" + compareDate.toString() );

                return true;
            }

        } catch (Exception e) {
            Log.e("checkCurrentShift", e.toString());
        }
        //}

        return false;
    }

    // Creates ListView String to display in widget
    public static ArrayList<String> shiftsToString(ArrayList<String> datesString) {

        if (dates != null && dates.size() > 0) {
            for (int i = 0; i < dates.size(); i++) {

                String temp = "";
                String shift = dates.get(i).getShift();
                String date = dates.get(i).getWorkday();

                try {
                    if (dates.get(i).getShift().length() > 0) {

                        if (shift.contains("7")) {
                            temp = "Dawn";
                        } else if (shift.contains("8")) {
                            temp = "Morning";
                        } else if (shift.contains("15")) {
                            temp = "Evening";
                        } else if (shift.contains("16")) {
                            temp = "Night";
                        }
                    }


                    datesString.add("Date: " + date + "\nWork shift: " + temp);
                } catch (Exception e) {
                    Log.e("shiftToString()", e.toString());
                }

            }
        }

        return datesString;
    }

    // Removes past days
    public static ArrayList<WorkDay> removePastDays(ArrayList<WorkDay> dates) {

        Date CURRENT_DATE = new Date();

        CURRENT_DATE.setHours(0);
        CURRENT_DATE.setMinutes(0);
        CURRENT_DATE.setSeconds(0);

        for (int x = 0; x < dates.size(); x++) {

            try {
                Date compareDate = form.parse(dates.get(x).getWorkday());

                if (compareDate.getTime() < (CURRENT_DATE.getTime())
                        && !isToday(compareDate.getTime()) ) {
                    dates.remove(x);
                    x--;
                }
            } catch (Exception e) {
                Log.e("removePastDays()", e.toString());
            }

        }

        return dates;
    }

    // Check if today is today, retuns true if today is today
    public static boolean isToday(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        return (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR));
    }



    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (service != null) {
            m.cancel(service);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

    }

}