package aquasense.locationalarm;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static GeofencingRequest geofencingRequest;
    private static GoogleApiClient googleApiClient = null;
    private static PendingIntent pendingIntent = null;

    static Context context;
    static ArrayList<AlarmData> list = null;

    static DatabaseHelper Db;
    private final String TAG = "LocationService";

    public LocationService() {
        super("LocationService");
    }

    public LocationService(Context context, ArrayList<AlarmData> objects) {
        super("LocationService");
        this.context = context;
        list = new ArrayList<>();
        list = objects;
        new GeofenceRegistrationService(objects);
    }


    @Override
    public void onDestroy() {
        Log.e(TAG,"In onDestroy");
        super.onDestroy();
    }

    public void StopService(){
        Log.e("Service runnning","Stopping service");

        stopGeoFencing();
        stopSelf();
//        onDestroy();
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Db = new DatabaseHelper(this);
        //list = new ArrayList<>();
        //list = getAlarms();

        if(list.isEmpty()){
            StopService();
        }
        new GeofenceRegistrationService(list);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d("Location Service", "Google Play Service Not Available");
            Toast.makeText(getApplicationContext(),"Google play services are unavailable",Toast.LENGTH_SHORT).show();
            } else {
            Log.d("Location Service", "Google play service available");
        }
    }

    private void startLocationMonitor() {
        Log.d(TAG, "start location monitor");
        final LocationRequest locationRequest = LocationRequest.create()
                .setInterval(20000)
                .setFastestInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());
                    //Toast.makeText(getApplicationContext(),"Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                    if(list.isEmpty()){
                        //LocationServices.re
                        StopService();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();

        ArrayList<Geofence> geofences = getGeofence();

        geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .addGeofences(geofences)
                    .build();

            if (!googleApiClient.isConnected()) {
                Log.d(TAG, "Google API client not connected");
            } else {
                try {
                    LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "Successfully Geofencing Connected ");
                            } else {
                                Log.d(TAG, "Failed to add Geofencing " + status.getStatus()+status.hasResolution());
                                Toast.makeText(getApplicationContext(),"Failed to add Geo Fencing\n Please change Location service to High Accuracy\n status code: "+status.getStatusCode(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (SecurityException e) {
                    Log.d(TAG, e.getMessage());
                }

        }
    }

    private void stopGeoFencing() {
        if( googleApiClient !=null && googleApiClient.isConnected()) {
            pendingIntent = getGeofencePendingIntent();
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess())
                                Log.d(TAG, "Stop geofencing");
                            else
                                Log.d(TAG, "Not stop geofencing");
                        }
                    });

            googleApiClient.disconnect();
        }

    }


    @NonNull
    private ArrayList<Geofence> getGeofence() {

        ArrayList<Geofence> geofences = new ArrayList();
        AlarmData alarmData;
        Iterator<AlarmData> iterator = list.iterator();
        while (iterator.hasNext()) {
            alarmData = iterator.next();
            Log.e("geofence req", alarmData.id + " " + alarmData.message);

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(String.valueOf(alarmData.getId()))
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(alarmData.latitude, alarmData.longitude, 100)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            geofences.add(geofence);
        }
        return  geofences;
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.e(TAG,"In PendingIntent");
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Log.e(TAG,"In PendingIntent not null");
        Intent intent = new Intent(this, GeofenceRegistrationService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(!list.isEmpty()) {
            startGeofencing();
            startLocationMonitor();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "Google Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google Connection Failed");
    }

    public ArrayList<AlarmData> getAlarms() {
        Cursor res = Db.getAllData();
        while (res.moveToNext()){
            list.add(new AlarmData(
                    res.getInt(0),
                    res.getDouble(1),
                    res.getDouble(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5)
            ));
        }
        return list;
    }
}
