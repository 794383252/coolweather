package activity;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;

import com.coolweather.app.R;

import model.City;
import model.County;
import model.Province;
import db.CoolWeatherDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity implements OnItemClickListener {

	LogUtil logUtil = new LogUtil();

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> datalist = new ArrayList<String>();

	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countylist;
	private Province selectedProvince;
	private City selectedCity;

	private int currentLevel;

	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weather_activity", true);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		init();
	}

	/**
	 * 控件的初始化
	 */
	private void init() {
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, datalist);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(this);
		queryProvince();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (currentLevel == LEVEL_PROVINCE) {
			selectedProvince = provinceList.get(arg2);
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			selectedCity = cityList.get(arg2);
			queryCounty();
		} else if (currentLevel == LEVEL_COUNTY) {
			String countyCode = countylist.get(arg2).getCounty_code();
			Intent intent = new Intent(ChooseAreaActivity.this,
					WeatherActivity.class);
			intent.putExtra("county_code", countyCode);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 查询所有的省，优先从数据库中查询，数据库中没有再从服务器中查询
	 */
	private void queryProvince() {
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			datalist.clear();
			for (Province p : provinceList) {
				datalist.add(p.getProvince_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "Province");
		}
	}

	/**
	 * 查询某个省所有的市，优先从数据库中查询，数据库中没有再从服务器中查询
	 */
	private void queryCity() {
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size() > 0) {
			datalist.clear();
			for (City c : cityList) {
				datalist.add(c.getCity_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvince_name());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvince_code(), "City");
		}
	}

	/**
	 * 查询某个市下所有的县，优先从数据库中查询，数据库中没有再从服务器中查询
	 */
	private void queryCounty() {
		countylist = coolWeatherDB.loadCounty(selectedCity.getId());
		if (countylist.size() > 0) {
			datalist.clear();
			for (County c : countylist) {
				datalist.add(c.getCounty_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCity_name());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCity_code(), "County");
		}
	}

	/**
	 * 从服务器中查询数据
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address = null;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("Province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("City".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.id);
				} else if ("County".equals(type)) {
					result = Utility.handerCountiesResponse(coolWeatherDB,
							response, selectedCity.id);
				}
				if (result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("Province".equals(type)) {
								queryProvince();
							} else if ("City".equals(type)) {
								queryCity();
							} else if ("County".equals(type)) {
								queryCounty();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 打开加载对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭加载对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		logUtil.i("执行返回键");
		if (currentLevel == LEVEL_COUNTY) {
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(ChooseAreaActivity.this,
						WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
