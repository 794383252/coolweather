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
	 * �����췽��˽�л�
	 * 
	 * getWritableDatabase��getReadableDatabase���ǻ�ȡһ�����Բ������ݿ��SQLiteDatabaseʵ��
	 * getWritableDatabase���Զ�д��ʽ�򿪣�������ݿ����˾�ֻ�ܶ�����д����ʱgetWritableDatabase
	 * �ͻᱨ��getReadableDatabase����ֻ����ʽ��
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenhelper dbOpenhelper = new CoolWeatherOpenhelper(context,
				DB_NAME, null, VERSION);
		db = dbOpenhelper.getWritableDatabase();
	}

	/**
	 * ��ȡCoolWeatherDB��ʵ��
	 * 
	 * ʹ�õ���ģʽ��Լϵͳ��Դ�������
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * ��Province��ӵ����ݿ���
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
	 * �����ݿ��ж�ȡȫ������ʡ�ݵ���Ϣ
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
	 * ��Cityʵ����ӵ����ݿ���
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
	 * �����ݿ��ж�ȡĳ��ʡ���г��е���Ϣ
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
	 * ��countyʵ����ӵ����ݿ���
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
	 * �����ݿ��ȡĳ���������е�����Ϣ
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
