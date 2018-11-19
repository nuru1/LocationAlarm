package aquasense.locationalarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends ArrayAdapter<AlarmData>  {

    private Context context;
    private ArrayList<AlarmData> arrayList;

    public AlarmAdapter(@NonNull Context context, @NonNull ArrayList<AlarmData> objects) {
        super(context, R.layout.alert_each_item, objects);
        this.context = context;
        arrayList = new ArrayList<>();
        arrayList = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.alert_each_item,parent,false);

        AlarmData alarmData = arrayList.get(position);

        TextView reminder = (TextView)listItem.findViewById(R.id.ReminderMsg);
        TextView loc = (TextView)listItem.findViewById(R.id.ReminderLoc);
        TextView type = (TextView)listItem.findViewById(R.id.ReminderType);

        reminder.setText(alarmData.getMessage());
        loc.setText(alarmData.getAdress());
        type.setText(alarmData.getLocType());

        return listItem;
    }


}
