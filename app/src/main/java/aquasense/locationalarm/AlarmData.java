package aquasense.locationalarm;

import android.util.Log;

import java.io.Serializable;

public class AlarmData implements Serializable {

    int id;
    double latitude,longitude;
    String message, adress, LocType;

    public AlarmData() {
    }

    public AlarmData(int id, double latitude, double longitude, String adress, String message, String locType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.adress = adress;
        LocType = locType;

        Log.e("Alarm Data",id+" "+latitude+" "+longitude+" "+adress+"\n"+message+" "+locType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLocType() {
        return LocType;
    }

    public void setLocType(String locType) {
        LocType = locType;
    }
}
