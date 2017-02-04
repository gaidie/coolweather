package com.gaigai.coolweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaigai.coolweather.service.AutoUpdateService;
import com.gaigai.coolweather.util.HttpCallbackListener;
import com.gaigai.coolweather.util.HttpUtil;
import com.gaigai.coolweather.util.Utility;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	private TextView cityName;
	private TextView publishTime;
	private TextView weatherDesp;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDate;
	
	private Button switchCity;
	private Button refresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		initViews();
		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)){
			publishTime.setText("同步数据中。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else {
			showWeather();//没有的话 就显示本地数据
		}
		initEvents();
	}

	private void initEvents() {
		// TODO Auto-generated method stub
		switchCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				publishTime.setText("同步中。。。。");
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
				String weatherCode = sp.getString("weather_code", "");
				if (!TextUtils.isEmpty(weatherCode)){
					queryWeatherInfo(weatherCode);
				}
			}
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(preferences.getString("city_name", ""));
		temp1.setText(preferences.getString("temp1", ""));
		temp2.setText(preferences.getString("temp2", ""));
		weatherDesp.setText(preferences.getString("weather_desp", ""));
		publishTime.setText(preferences.getString("publist_time", ""));
		currentDate.setText(preferences.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
		
		//启动定时任务
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	/**
	 * 查询县级代号所对应的天气代号
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
		queryFromServer(address,"countryCode");
	}
	
	/**
	 * 查询天气代号所对应的天气
	 * @param countryCode
	 */
	private void queryWeatherInfo(String countryCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/" + countryCode + ".html";
		queryFromServer(address,"weatherCode");
	}

	private void queryFromServer(String address,final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				if ("countryCode".equals(type)){
					if (!(TextUtils.isEmpty(response))){
						String[] array = response.split("\\|");
						if (array != null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
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
						publishTime.setText("同步失败");
						
					}
				});
			}
		});
	}
		
		

	private void initViews() {
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityName = (TextView) findViewById(R.id.city_name);
		publishTime = (TextView) findViewById(R.id.publish_text);
		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDate = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refresh = (Button) findViewById(R.id.refresh_weather);
	}
	
}
