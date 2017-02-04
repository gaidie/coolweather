package com.gaigai.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.gaigai.coolweather.db.CoolWeatherDB;
import com.gaigai.coolweather.model.City;
import com.gaigai.coolweather.model.Country;
import com.gaigai.coolweather.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,
			String response){
		if (!TextUtils.isEmpty(response)){
			String[] allProvices = response.split(",");
			if (allProvices != null && allProvices.length > 0){
				for (String province : allProvices){
					String[] array = province.split("\\|");
					Province province2 = new Province();
					province2.setProvinceCode(array[0]);
					province2.setProvinceName(array[1]);
					coolWeatherDB.saveProvice(province2);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理返回的城市数据
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId){
		if (!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0){
				for (String s : allCities){
					String[] array = s.split("\\|");
					City city = new City();
					city.setProvinceId(provinceId);
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 保存乡村信息
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId){
		if (!TextUtils.isEmpty(response)){
			String[] allCountries = response.split(",");
			if (allCountries != null && allCountries.length > 0){
				for (String s : allCountries){
					String[] array = s.split("\\|");
					Country bean = new Country();
					bean.setCityId(cityId);
					bean.setCountryCode(array[0]);
					bean.setCountryName(array[1]);
					coolWeatherDB.saveCountry(bean);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context, String response){
		try {
			Log.i("WeatherInfo", response);
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("JSONE", e.getMessage());
		}
	}

	/**
	 * 保存当前天气信息到本地
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publist_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}
