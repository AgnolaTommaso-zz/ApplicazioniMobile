package ch.supsi.dti.isin.meteoapp.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ch.supsi.dti.isin.meteoapp.database.DbHelper;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;

public class MainActivity extends SingleFragmentActivity {

    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DbHelper(getApplicationContext()).getWritableDatabase();
    }

    @Override
    protected Fragment createFragment() {
        return new ListFragment();
    }
}
