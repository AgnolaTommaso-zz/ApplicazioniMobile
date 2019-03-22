package ch.supsi.dti.isin.meteoapp.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.TimeUnit;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.utility.APIParser;
import ch.supsi.dti.isin.meteoapp.utility.VolleyCallback;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class NotificationService extends IntentService implements VolleyCallback {

    private static final String TAG = "NotificationService";
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1); // min. is 1 minute!
    private static Activity activity;

    public NotificationService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn, Activity a) {
        activity = a;

        Intent i = NotificationService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        else {
            alarmManager.cancel(pi);
            pi.cancel();
        }


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(0)
                .setInterval(1000*60);
        SmartLocation.with(activity).location().continuous().config(builder.build())//getActivity per recuperare l'activity
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        Log.i("Meteo onLocationUpdated", location.toString());
                        JsonObjectRequest jor = APIParser.getLocationInfo(location.getLatitude(), location.getLongitude(), NotificationService.this);
                        RequestQueue queue = Volley.newRequestQueue(activity);
                        queue.add(jor);

                    }
                });

    }

    private void sendNotification(Location location) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "TEST_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Test Channel Description");
            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Temperatura corrente")
                .setContentText(location.getTemperature())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onSuccess(Location location) {
        sendNotification(location);
    }
}
