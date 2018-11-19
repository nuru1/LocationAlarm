package aquasense.locationalarm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dalvik.system.DelegateLastClassLoader;

public class SuggestionsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    DatabaseHelper Db;
    ArrayList<SuggestionsData> list;
    ListView listView_suggestions;
    SuggestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        listView_suggestions = (ListView)findViewById(R.id.listView_sugg);
        Db = new DatabaseHelper(this);
        Cursor result = Db.getAllSuggestions();
        list = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Suggestions");
        actionBar.setDisplayHomeAsUpEnabled(true);

        while (result.moveToNext()){
            list.add(new SuggestionsData(result.getString(0),
                    result.getString(1)));
            //Log.e("sug",result.getString(0)+" "+result.getString(1));
        }

        adapter = new SuggestionsAdapter(this,list);
        listView_suggestions.setAdapter(adapter);
        listView_suggestions.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_suggestions,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addNew:
                //........
  /*              inputDialog_suggestion dialog = new inputDialog_suggestion();
                dialog.setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomDialog);
                dialog.show(getFragmentManager(),"ADD SUGGESTION");*/
                showDialog(R.layout.add_suggestion);
                return true;
        }
        return true;
    }


    @Nullable
    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.add_suggestion,null);
        builder.setView(view);

        final TextInputEditText type_sug = (TextInputEditText)view.findViewById(R.id.add_type);
        final TextInputEditText _sug = (TextInputEditText)view.findViewById(R.id.add_sug);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("add sugg","save");
                        String t = type_sug.getText().toString();
                        String s = _sug.getText().toString();
                        Log.e("dialog sug",t+" "+s+"  000");
                        if(!t.isEmpty() && !s.isEmpty()){
                            boolean res = Db.Insert_Suggestion(t,s);
                            if (!res)
                                Toast.makeText(getApplicationContext(),"There might be a problem",Toast.LENGTH_SHORT).show();
                            else {
                                adapter.notifyDataSetChanged();
                                recreate();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("add sugg","cancel");
                    }
                });


        return builder.create();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {

        String[] options = {"Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an action");
        builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SuggestionsData selected = list.get(index);
                Log.e("Selected",selected.Suggestion+"  "+i);
                if(i==0){
                    Log.e("Selected","DELETE");
                    boolean res = Db.DeleteSuggestion(selected.Type);
                    if(res){
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        //finish();
                        recreate();
                    }
                }
            }
        });
        builder.show();
    }
}
