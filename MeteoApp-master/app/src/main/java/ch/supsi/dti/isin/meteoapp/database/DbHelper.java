package ch.supsi.dti.isin.meteoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "AppMeteo_DB";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.LocationsTable.TABLE_NAME + "("
                + " _id integer primary key autoincrement, "
                + DbSchema.LocationsTable.Cols.ID
                + ", "
                + DbSchema.LocationsTable.Cols.NAME
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
