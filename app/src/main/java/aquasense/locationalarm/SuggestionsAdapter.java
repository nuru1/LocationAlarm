package aquasense.locationalarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SuggestionsAdapter extends ArrayAdapter<SuggestionsData> {

    Context context;
    ArrayList<SuggestionsData> list;
    public SuggestionsAdapter(@NonNull Context context, @NonNull ArrayList<SuggestionsData> objects) {
        super(context, R.layout.each_suggestion, objects);
        this.context=context;
        list = new ArrayList<>();
        list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.each_suggestion,parent,false);

        SuggestionsData suggestionsData = list.get(position);

        Log.e("Sug adap",suggestionsData.Suggestion+" "+suggestionsData.Type);

        TextView type = (TextView)listItem.findViewById(R.id.typ);
        TextView sugg = (TextView)listItem.findViewById(R.id.sug);

        type.setText(suggestionsData.Type);
        sugg.setText(suggestionsData.Suggestion);

        return listItem;
    }
}
