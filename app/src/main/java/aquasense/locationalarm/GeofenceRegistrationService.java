package aquasense.locationalarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeofenceRegistrationService extends IntentService {

    private static final String TAG = "GeoIntentService";
    static ArrayList<AlarmData> list;
    DatabaseHelper Db;
    public GeofenceRegistrationService() {
        super(TAG);
        Log.e(TAG, "In GeofencingReg " );
    }

    public GeofenceRegistrationService(ArrayList<AlarmData> objects) {
        super(TAG);
        list = objects;
        Log.e(TAG, "In GeofencingReg " );
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG, "In GeofencingReg onhandle event" );

        Db = new DatabaseHelper(this);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER || transaction==Geofence.GEOFENCE_TRANSITION_EXIT ) {
                Log.e(TAG, "IN GEOFENCES "+geofence.getRequestId());

                AlarmData alarmData = getAlarm(Integer.parseInt(geofence.getRequestId()));
                Cursor cursor = Db.getAllSuggestions();
                String suggestion = Suggestion(cursor,alarmData.LocType);

                if (alarmData != null) {
                    //Toast.makeText(getApplicationContext(),"Reminder:   "+alarmData.message+" \n Just a suggestion:"+suggestion,Toast.LENGTH_LONG).show();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.location_1055034)
                            .setContentTitle("Reminder: "+alarmData.message)
                            .setContentText("Just a suggestion:  "+suggestion)
                            //.setStyle(new NotificationCompat.BigTextStyle()
                                    //.bigText("vvvvvvv"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    Intent intent1 = new Intent(this,MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                    taskStackBuilder.addParentStack(MainActivity.class);
                    taskStackBuilder.addNextIntent(intent1);
                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

                    //PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                    Notification notification = mBuilder.build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    manager.notify(0,notification);

                    Db.DeleteAlarm(Integer.parseInt(geofence.getRequestId()));

                    //Intent intent1 = new Intent(this, MainActivity.class);

                    startActivity(intent1);
                    //stopSelf();
                }
                else {
                    Toast.makeText(getApplicationContext(),"There is an error",Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "You are outside Stanford University "+geofence.getRequestId());
            }
        }
    }

    private String Suggestion(Cursor cursor, String typ) {

        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(typ)){
                return cursor.getString(1);
            }
        }
        return "Be Safe";
    }

    public AlarmData getAlarm(int id) {
        AlarmData alarm;
        Iterator<AlarmData> iterator = list.iterator();
        while (iterator.hasNext()){
            alarm = iterator.next();
            if(id == alarm.getId()){
                return alarm;
            }
        }
        return null;
    }
}