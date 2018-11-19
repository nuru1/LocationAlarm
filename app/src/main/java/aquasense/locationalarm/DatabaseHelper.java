package aquasense.locationalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocationsDB";
    private static final int DbVersion = 1;

    private static final String TABLE1 = "LOCATIONS";
    private static final String TABLE2 = "SUGGESTIONS";

    private static final String TABLE1_COL1 = "id";
    private static final String TABLE1_COL2 = "latitude";
    private static final String TABLE1_COL3 = "longitude";
    private static final String TABLE1_COL4 = "address";
    private static final String TABLE1_COL5 = "message";
    private static final String TABLE1_COL6 = "loc_type";

    private static final String TABLE2_COL1 = "loc_type";
    private static final String TABLE2_COL2 = "suggestion";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String create_table1 = "create table "+ TABLE1 + " ( " +
                TABLE1_COL1 +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TABLE1_COL2 +" DOUBLE NOT NULL, "+
                TABLE1_COL3 +" DOUBLE NOT NULL, "+
                TABLE1_COL4 +" TEXT, "+
                TABLE1_COL5 +" TEXT, "+
                TABLE1_COL6 +" TEXT, "+
                " FOREIGN KEY("+ TABLE1_COL6 +") REFERENCES "+TABLE2+" ("+TABLE2_COL1+")"+
                " )";

        final String create_table2 = "create table " +TABLE2+" ( " +
                TABLE2_COL1 + " TEXT PRIMARY KEY, " +
                TABLE2_COL2 +" TEXT NOT NULL"+
                "  )";

        sqLiteDatabase.execSQL(create_table2);
        sqLiteDatabase.execSQL(create_table1);

        ContentValues cv = new ContentValues();
        cv.put(TABLE2_COL1,"market");
        cv.put(TABLE2_COL2,"Don't forget the carry bag");
        sqLiteDatabase.insert(TABLE2,null,cv);
        cv.clear();
        cv.put(TABLE2_COL1,"medical");
        cv.put(TABLE2_COL2,"Don't forget to Check the expiry date");
        sqLiteDatabase.insert(TABLE2,null,cv);
        cv.clear();
        cv.put(TABLE2_COL1,"College");
        cv.put(TABLE2_COL2,"Don't forget anything important");
        sqLiteDatabase.insert(TABLE2,null,cv);
        cv.put(TABLE2_COL1,"Home");
        cv.put(TABLE2_COL2,"Don't forget to lock the door");
        sqLiteDatabase.insert(TABLE2,null,cv);
        cv.put(TABLE2_COL1,"-NULL-");
        cv.put(TABLE2_COL2,"--Nothing--");
        sqLiteDatabase.insert(TABLE2,null,cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE1);
        onCreate(sqLiteDatabase);
    }

    public boolean Insert_Alarm(double lat, double lng, String adrs, String msg, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE1_COL2,lat);
        contentValues.put(TABLE1_COL3,lng);
        contentValues.put(TABLE1_COL4,adrs);
        contentValues.put(TABLE1_COL5,msg);
        contentValues.put(TABLE1_COL6,type);
        long res = db.insert(TABLE1,null,contentValues);
        if(res==-1)
            return false;
        else
            return true;
    }

    public boolean Insert_Suggestion(String type, String sugg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TABLE2_COL1,type);
        cv.put(TABLE2_COL2,sugg);
        long res = db.insert(TABLE2,null,cv);
        if(res==-1)
            return false;
        else
            return true;
    }

    public Cursor getSuggestionTypes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select "+TABLE2_COL1+" from "+TABLE2,null);
        return result;
    }

    public Cursor getSuggestion(String typ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE2+" where "+TABLE2_COL1+" = '"+typ+"'",null);
        return result;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE1,null);
        return cursor;
    }

    public Cursor getAllSuggestions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE2,null);
        return cursor;
    }

    public boolean DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE1,null,null);
        if (res == -1)
            return false;
        return true;
    }

    public boolean DeleteAlarm(int id){
        String ID = String.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE1,"id=?",new String[]{ID});
        if (res>0)
                return true;
        return false;
    }

    public boolean DeleteSuggestion(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE2,"loc_type=?",new String[]{id});
        if (res>0)
            return true;
        return false;
    }

}
