package com.gaigai.coolweather.util;

import android.text.TextUtils;

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
}
