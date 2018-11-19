package aquasense.locationalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.sql.Array;
import java.sql.BatchUpdateException;
import java.util.ArrayList;

public class NewTask extends AppCompatActivity  {

    TextInputEditText _msg;
    TextView _ViewPlace;
    Button _PlacePicker,_Submit,_Cancel;
    Spinner _spinner;

    private static final int LOC_REQ_CODE = 1;
    int PLACE_PICKER_REQUEST = 1;
    DatabaseHelper db;
    data dt;

    class data{
        boolean flag;
        double lat;
        double lng;
        String adrs;
        String msg;
        String LocType;
        LatLng latLng;

        public boolean check() {
            if (flag == true && !msg.isEmpty() && !LocType.isEmpty()) {
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        _msg = (TextInputEditText)findViewById(R.id.msg_task);
        _ViewPlace = (TextView)findViewById(R.id.PlaceView);
        _PlacePicker = (Button)findViewById(R.id.PickPlace);
        _Submit = (Button)findViewById(R.id.SubmitTask);
        _Cancel = (Button)findViewById(R.id.CancelTask);
        _spinner = (Spinner)findViewById(R.id.loc_type);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add new Location Alert");
        //actionBar.setDisplayShowHomeEnabled(true);

        dt = new data();
        db = new DatabaseHelper(this);

        CheckPermissions();
        SpinnerSetting();


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String eds = extras.getString("TYPE");
            if(eds.equals("EDIT")){
                AlarmData alarmData = (AlarmData) getIntent().getSerializableExtra("DATA");
                Log.e("EDIT",alarmData.message+"  "+ alarmData.adress);
                dt.msg = alarmData.message;
                dt.lat = alarmData.latitude;
                dt.lng = alarmData.longitude;
                dt.adrs = alarmData.adress;
                dt.LocType = alarmData.LocType;
                dt.latLng = new LatLng(alarmData.latitude,alarmData.longitude);
                dt.flag = true;

                _msg.setText(dt.msg);
                _ViewPlace.setText(dt.adrs);
            }
        }

        _PlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(isLocationAccessPermitted()) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(NewTask.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Please grant permissions",Toast.LENGTH_SHORT).show();
            }
        });

        _Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                New_Task();
            }
        });

        _Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = db.getAllData();
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("ID: "+res.getInt(0)+"\n");
                    buffer.append("LAT: "+res.getDouble(1)+"\n");
                    buffer.append("LNG: "+res.getDouble(2)+"\n");
                    buffer.append("ADRS: "+res.getString(3)+"\n");
                    buffer.append("MSG: "+res.getString(4)+"\n");
                    buffer.append("TYP: "+res.getString(5)+"\n");
                }
                Log.e("Data",buffer.toString());

                Intent intent = new Intent(NewTask.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void SpinnerSetting() {
        Cursor res = db.getSuggestionTypes();
        final ArrayList type = new ArrayList();
        while (res.moveToNext()) {
            type.add(res.getString(0) );
        }
        //Log.e("Data","data: "+type.toString());
        ArrayAdapter<CharSequence> aa;
        aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(aa);

        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.e("Spinnerselect",type.get(i).toString());
                dt.LocType = type.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(),"Select a location Type",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void New_Task() {
        dt.msg = _msg.getText().toString();
        if(dt.check()){
            boolean d = db.Insert_Alarm(dt.lat,dt.lng,dt.adrs,dt.msg,dt.LocType);
            if(d) {
                Toast.makeText(getApplicationContext(), "Task Added!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NewTask.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                this.startActivity(intent);
                finish();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"Please insert the details",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(NewTask.this,data);
                String addrs =(String) place.getAddress();
                dt.adrs = addrs;
                dt.latLng = place.getLatLng();
                dt.lat = dt.latLng.latitude;
                dt.lng = dt.latLng.longitude;
                dt.flag = true;
                _ViewPlace.setText(addrs);
                Log.d("Selected ",dt.adrs+"  "+dt.latLng);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
