package ch.supsi.dti.isin.meteoapp.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ch.supsi.dti.isin.meteoapp.database.DbHelper;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.service.NotificationService;

public class MainActivity extends SingleFragmentActivity {

    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DbHelper(getApplicationContext()).getWritableDatabase();
        NotificationService.setServiceAlarm(this, true, this);
    }

    @Override
    protected Fragment createFragment() {
        return new ListFragment();
    }
}
