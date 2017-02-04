package com.gaigai.coolweather;

import java.util.ArrayList;
import java.util.List;

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

import com.gaigai.coolweather.db.CoolWeatherDB;
import com.gaigai.coolweather.model.City;
import com.gaigai.coolweather.model.Country;
import com.gaigai.coolweather.model.Province;
import com.gaigai.coolweather.util.HttpCallbackListener;
import com.gaigai.coolweather.util.HttpUtil;
import com.gaigai.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCES = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	
	private ProgressDialog progressDialog;
	
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	
	private CoolWeatherDB coolWeatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	
	private int currentLevel;//当前选中的级别
	
	private boolean isFromWeatherActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("city_selected", false) && !isFromWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示标题栏
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCES){
					selectedProvince = provinceList.get(position);
					queryCites();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCountries();
				}else if(currentLevel == LEVEL_COUNTRY){
					String countryCode = countryList.get(position).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
							
				}
			}
		});
		queryProvinces();//加载省级数据
	}

	/**
	 * 监听返回键事件
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel == LEVEL_COUNTRY){
			queryCites();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
//		super.onBackPressed();
	}
	
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList.size() > 0){
			dataList.clear();
			for (Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);//todo 不明白
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCES;
		}else{
			queryFromServer(null, "province");
		}
		
	}

	private void queryFromServer(String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				boolean result = false;
				if ("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("country".equals(type)){
					result = Utility.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if (result){
					//通知UI线程处理
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCites();
							}else if("country".equals(type)){
								queryCountries();
							}
						}

					});
				}
			}
			
			@Override
			public void onError(Exception error) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "数据请求失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}

	protected void queryCountries() {
		// TODO Auto-generated method stub
		countryList = coolWeatherDB.queryCountriesByCityId(selectedCity.getId());
		if (countryList.size() > 0){
			dataList.clear();
			for (Country country : countryList){
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	protected void queryCites() {
		// TODO Auto-generated method stub
		cityList = coolWeatherDB.getCitesById(selectedProvince.getId());
		if (cityList.size() > 0){
			dataList.clear();
			for (City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
		
	}
}
