package ctk43.phantrungkien.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME="History";
    public DataHelper(Context context) {
        super(context, "History.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_history="create table "+TABLE_NAME +"(" +
                " idWeather text(8) primary key," +
                " dateTime text(20)," +
                " maxTemp int(3)," +
                " minTemp int(3) )";
        db.execSQL(create_history);
    }

    public void add(WeatherHistory wh){
        SQLiteDatabase database=getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("ID",wh.getTxtID());
        values.put("Date",wh.getTxtDate());
        values.put("maxTemp",wh.getTxtMaxTemp());
        values.put("minTemp",wh.getTxtMinTemp());
        database.insert(TABLE_NAME,null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
