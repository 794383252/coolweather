package util;

import model.City;
import model.County;
import model.Province;
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
					logUtil.i("这是p:" + p.toString());
					String[] array = p.split("\\|");
					logUtil.i("这是array[0]:" + array[0] + "这是array[]:"
							+ array[0]);
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

}
