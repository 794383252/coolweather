package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenhelper extends SQLiteOpenHelper {

	private static final String CREATE_PROVINCE = "create table Province("
			+ " id integer primary key autoincrement," + " Province_name text,"
			+ " Province_code text)";

	private static final String CREATE_CITY = "create table City("
			+ " id integer primary key autoincrement," + " City_name text,"
			+ " City_code text," + " Province_id integer)";
	
	private static final String CREATE_COUNTY = "create table County("
			+ " id integer primary key autoincrement," + " County_name text,"
			+ " County_code text," + " City_id integer)";

	public CoolWeatherOpenhelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
