package com.example.antti.widget_demo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import java.util.ArrayList;

public class OmaDbAdapteri {

    private static final String TIETOKANNAN_NIMI = Environment.getExternalStorageDirectory().toString() + "/date/tunnit.db";
    private static final String TIETOKANTATAULU = "date";
    private static final int TIETOKANNAN_VERSIO = 1;


    // Muuttuja tietokantainstanssille
    private SQLiteDatabase db;
    // Kantaa käyttävän sovelluksen konteksti
    private final Context context;

    private Avaajaavustin avaaja;

    private SQLiteStatement insertlause;
    private static final String INSERT = "insert into " + TIETOKANTATAULU
            + "(date) values (?)";

    public OmaDbAdapteri(Context context) {
        this.context = context;
        avaaja = new Avaajaavustin(this.context);
        luoKantayhteys();
        this.insertlause = this.db.compileStatement(INSERT);
    }

    private void luoKantayhteys() {
        if(db==null) {
            db = avaaja.getWritableDatabase();
        }
    }

    public void puraKantayhteys() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /*
     * Sisäluokka Avaajaavustin periytyy SQLiteOpenHelperistä. Luokka luo tai
     * päivittää tietokannan automaattisesti versionumeroon perustuen.
     */
    private static class Avaajaavustin extends SQLiteOpenHelper {

        Avaajaavustin(Context context) {
            super(context, TIETOKANNAN_NIMI, null, TIETOKANNAN_VERSIO);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL("CREATE TABLE " + TIETOKANTATAULU
            //        + "(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, vuoro TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int vanhaVersio, int uusiVersio) {
            db.execSQL("DROP TABLE IF EXISTS " + TIETOKANTATAULU);
            onCreate(db);
        }
    }
    /*
    public long suoritaInsert(String date) {
        this.insertlause.bindString(1, date);
        return this.insertlause.executeInsert();
    }
    */
    public void poistaKaikki() {
        this.db.delete(TIETOKANTATAULU, null, null);
    }

    public ArrayList<WorkDay> haeKaikki() {
        ArrayList<WorkDay> paivat = new ArrayList<WorkDay>();

        Cursor cursor = db.query(TIETOKANTATAULU, new String[] {"date", "vuoro" },
                 null, null, null, null, "date asc");

        if (cursor.moveToFirst()) {
            do {
                WorkDay wd = new WorkDay();
                wd.setWorkday(cursor.getString(0));
                wd.setShift(cursor.getString(1));
                paivat.add(wd);
            } while (cursor.moveToNext());
        }


        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return paivat;
    }

}