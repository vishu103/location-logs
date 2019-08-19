package in.edureal.locationlogs;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String dbname="logsdb";
    private static final int version=1;

    MySQLiteHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String str="CREATE TABLE logs (_id INTEGER PRIMARY KEY AUTOINCREMENT, latitude DECIMAL(12,4), longitude DECIMAL(12,4), logDate TEXT, logTime12 TEXT, logTime24 TEXT, address TEXT, reason TEXT, mobile TEXT, wifi TEXT, batteryRate INTEGER, climate TEXT, temperature DOUBLE(12,6))";
        sqLiteDatabase.execSQL(str);
    }

    void insertData(SQLiteDatabase db, double lati, double longi, String logDate, String logTime12, String logTime24, String address, String reason, String mobile, String wifi, int battery, String climate, double temp){
        ContentValues values=new ContentValues();
        values.put("latitude",lati);
        values.put("longitude",longi);
        values.put("logDate",logDate);
        values.put("logTime12",logTime12);
        values.put("logTime24",logTime24);
        values.put("address",address);
        values.put("reason",reason);
        values.put("mobile",mobile);
        values.put("wifi",wifi);
        values.put("batteryRate",battery);
        values.put("climate",climate);
        values.put("temperature",temp);
        db.insert("logs",null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
