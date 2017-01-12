package com.example.antti.widget_demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends ListActivity {

    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 200;
    public static ArrayList<WorkDay> dates;
    public static boolean workToday = false;
    public static SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");



    // Path to download database copy from
    private String PATH = "http://korpisoturit.com/tuntitesti/tunnit.db";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int refreshCount = 0;
        String type = "";
        String line1 = "Widget not yet initialized";
        String line2 = "Widget not yet initialized";


        /**********************************************************************************
         * Permissions, needed Internet (To download External database)
         * Write to external storage, save External database somewhere
         * Read database
         *
         * Manifest.permission.WRITE_EXTERNAL_STORAGE
         * Manifest.permission.READ_EXTERNAL_STORAGE
         * Manifest.permission.INTERNET
         *********************************************************************************/

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);


        /*
        if (permissions.size() != 3) {
            System.exit(0);
        }
        */



        /*
        //App crashes if file exists and tries to delete, check later
        String file = Environment.getExternalStorageDirectory() + "/date/tunnit_testi.db";
        File databaseFile = new File(file);
        if(databaseFile.exists()) {
            databaseFile.delete();
            Log.e("Delete", "File deleted: " + file);
        }

        if (!databaseFile.exists()) {
            new DownloadFileFromURL().execute(PATH);
        }
        */
        //
        final OmaDbAdapteri db = new OmaDbAdapteri(this);
        dates = new ArrayList<WorkDay>();
        ArrayList<String> datesString = new ArrayList<>();





        try {
            dates = db.haeKaikki(); // Get all from DB to arraylist
            db.puraKantayhteys(); // Close db connection
            dates = removePastDays(dates);
            datesString = shiftsToString(datesString);
            workToday = checkCurrentShift(dates);
            refreshCount++;

            if (datesString.size() > 2){
                if ((workToday)) {
                    line1 = "Workday ahead!"
                            + "\n" + datesString.get(0);
                    line2 = "\nNext workday"
                            + "\n" + datesString.get(1);
                    type = "money";
                } else {

                    line1 = "No work today!";
                    type = "beer";
                    line2 = "\nNext workday:\n" +datesString.get(0);

                }
            }


            //Populate listview
            if (dates != null && dates.size() > 0) {
                setListAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        datesString.toArray(new String[1])));

            }


        } catch (Exception e) {
            Log.e("Create()", e.toString());
        }

        try {
            Class res = R.drawable.class;
            Field field = res.getField(type);
            int drawableId = field.getInt(null);

            RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);
            view.setTextViewText(R.id.textView, "" + line1);
            view.setTextViewText(R.id.textView2, "" + line2);
            view.setImageViewResource(R.id.imageView, drawableId);
            //view.setTextViewText(R.id.textView3, "" + refreshCount);

            ComponentName thisWidget = new ComponentName(this, SimpleWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, view);

            //Log.e("Drawable", type);

        } catch (Exception e){
            Log.e("Drawable", "Failure to get drawable id.", e);
        }


        Button buttonDelete = (Button) findViewById(R.id.delete);

        buttonDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

               //
                String file = Environment.getExternalStorageDirectory() + "/date/tunnit.db";
                File databaseFile = new File(file);
                if(databaseFile.exists()) {
                    databaseFile.delete();
                    Log.e("Delete", "File deleted: " + file);
                }
                showToast("Deleting local database file..");

            }
        });


        Button buttonDownload = (Button) findViewById(R.id.download);

        buttonDownload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //

                String file = Environment.getExternalStorageDirectory() + "/date/tunnit.db";
                File databaseFile = new File(file);

                if (!databaseFile.exists()) {
                    new DownloadFileFromURL().execute(PATH);
                    showToast("Downloading new database...");
                    startActivity(getIntent());
                }

                showToast("Database file already exists.\nRemove old database first.");
            }
        });

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

    public void showToast(String text) {

        int duration = Toast.LENGTH_LONG;
        Context context = getApplicationContext();

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }


}
