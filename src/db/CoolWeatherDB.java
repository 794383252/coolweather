package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	public static final String DB_NAME = "Cool_Weather";

	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;

	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 * 
	 * getWritableDatabase和getReadableDatabase都是获取一个可以操作数据库的SQLiteDatabase实例
	 * getWritableDatabase是以读写方式打开，如果数据库满了就只能读不能写，这时getWritableDatabase
	 * 就会报错，getReadableDatabase是以只读方式打开
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenhelper dbOpenhelper = new CoolWeatherOpenhelper(context,
				DB_NAME, null, VERSION);
		db = dbOpenhelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例
	 * 
	 * 使用单例模式节约系统资源提高性能
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province添加到数据库中
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("Province_name", province.getProvince_name());
			contentValues.put("Province_code", province.getProvince_code());
			db.insert("Province", null, contentValues);
		}
	}

	/**
	 * 从数据库中读取全国所有省份的信息
	 * 
	 * @return
	 */
	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvince_code(cursor.getString(cursor
						.getColumnIndex("Province_code")));
				province.setProvince_name(cursor.getString(cursor
						.getColumnIndex("Province_name")));
				list.add(province);
			} while (cursor.moveToNext());

		}

		return list;
	}

	/**
	 * 将City实例添加到数据库中
	 * 
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("City_name", city.getCity_name());
			contentValues.put("City_code", city.getCity_code());
			contentValues.put("Province_id", city.getProvince_id());
			db.insert("City", null, contentValues);
		}
	}

	/**
	 * 从数据库中读取某个省所有城市的信息
	 * 
	 * @param Province_id
	 * @return
	 */
	public List<City> loadCity(int Province_id) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "Province_id=?",
				new String[] { String.valueOf(Province_id) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setCity_code(cursor.getString(cursor
						.getColumnIndex("City_code")));
				city.setCity_name(cursor.getString(cursor
						.getColumnIndex("City_name")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setProvince_id(cursor.getInt(cursor
						.getColumnIndex("Province_id")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将county实例添加到数据库中
	 * 
	 * @param county
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("County_code", county.getCounty_code());
			contentValues.put("County_name", county.getCounty_name());
			contentValues.put("City_id", county.getCity_id());
			db.insert("County", null, contentValues);
		}
	}

	/**
	 * 从数据库读取某城市下所有的县信息
	 * 
	 * @param City_id
	 * @return
	 */
	public List<County> loadCounty(int City_id) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "City_id=?",
				new String[] { String.valueOf(City_id) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setCity_id(cursor.getInt(cursor
						.getColumnIndex("City_id")));
				county.setCounty_code(cursor.getString(cursor
						.getColumnIndex("County_code")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCounty_name(cursor.getString(cursor
						.getColumnIndex("County_name")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}

}
