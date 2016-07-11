package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import model.City;
import model.County;
import model.Province;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		LogUtil logUtil = new LogUtil();
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvince_code(array[0]);
					province.setProvince_name(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回来的市级信息
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @param province_id
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int province_id) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCity_code(array[0]);
					city.setCity_name(array[1]);
					city.setProvince_id(province_id);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取和处理服务器返回的县级数据
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @param City_id
	 * @return
	 */
	public synchronized static boolean handerCountiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int City_id) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null & allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCounty_code(array[0]);
					county.setCounty_name(array[1]);
					county.setCity_id(City_id);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * 解析服务器返回的json数据，并将解析出的数据保存到本地
	 * 
	 * @param context
	 * @param response
	 */
	public synchronized static void handleWeatherResponse(Context context,
			String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到sharedpreferences文件中
	 * 
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_deap", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
