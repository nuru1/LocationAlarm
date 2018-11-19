package aquasense.locationalarm;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    DatabaseHelper Mydb ;
    ArrayList<AlarmData> arrayList;
    AlarmAdapter adapter;
    static Intent Serviceintent = null;
    private static final int LOC_REQ_CODE = 1;
    public static final String FILTER_ACTION_KEY = "any_key";

    static LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckPermissions();

        listView = (ListView)findViewById(R.id.listView);
        Mydb = new DatabaseHelper(this);
        arrayList = new ArrayList<>();

        Cursor res = Mydb.getAllData();
        while (res.moveToNext()){
            arrayList.add(new AlarmData(
                    res.getInt(0),
                    res.getDouble(1),
                    res.getDouble(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5)
                    ));
        }

        adapter = new AlarmAdapter(this,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Log.e("Size Arraylist",arrayList.size()+" "+arrayList.isEmpty());
        if(Serviceintent==null ) {
            locationService = new LocationService(this, arrayList);
            Log.e("Service","Starting service.....");
            Serviceintent = new Intent(MainActivity.this, LocationService.class);
            startService(Serviceintent);
        }
        else {
            Log.e("Service","Stoping service.....");
            locationService.StopService();
            locationService = new LocationService(this, arrayList);
            Log.e("Service","Starting service.....");
            Serviceintent = new Intent(MainActivity.this, LocationService.class);
            startService(Serviceintent);
            }
        }

        //finish();



    public void AddAlert(View view) {
        Intent i = new Intent(MainActivity.this,NewTask.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onDestroy() {
        //stopService(Serviceintent);
        //locationService.StopService();
        Log.e("MainActivity","In OnDestroy");
        super.onDestroy();
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int index, long l) {

        String[] options = {"Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an action");
        builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlarmData selected = arrayList.get(index);
                Log.e("Selected",selected.getMessage()+"  "+i);
                if(i==0){
                    Log.e("Selected","DELETE");
                    boolean res = Mydb.DeleteAlarm(selected.getId());
                    if(res){
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        recreate();
                    }
                }
                /*else if (i==1){
                    Log.e("Selected","EDIT");
                    boolean res = Mydb.DeleteAlarm(selected.getId());
                    if(res){
                        Intent intent = new Intent(MainActivity.this,NewTask.class);
                        intent.putExtra("TYPE","EDIT");
                        intent.putExtra("DATA", selected);
                        startActivity(intent);
                    }
                }*/
            }
        });
        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.suggestions:
                Intent intent = new Intent(MainActivity.this,SuggestionsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void CheckPermissions() {
        if (isLocationAccessPermitted()) {
            return;
        } else {
            requestLocationAccessPermission();
        }
    }

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            return false;
        } else {
            return true;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                LOC_REQ_CODE);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
            else {
                Log.e("running services",service.service.getClassName());
                Log.e("My services",serviceClass.getName());
            }
        }
        return false;
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("LocationService".equals(service.service.getClassName())) {
                return true;
            }
            else {
                Log.e("running services",service.service.getClassName());
            }
        }
        return false;
    }
}
