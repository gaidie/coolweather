package com.gaigai.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gaigai.coolweather.model.City;
import com.gaigai.coolweather.model.Country;
import com.gaigai.coolweather.model.Province;

public class CoolWeatherDB {

	public static final String DB_NAME = "cool_weather";

	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;

	private SQLiteDatabase db;

	/**
	 * 单例实现数据对象
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
	}
	
	public synchronized static CoolWeatherDB getInstance(Context context){
		if (coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 插入省份数据
	 * @param province
	 */
	public void saveProvice(Province province){
		if (province != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}
	}
	
	/**
	 * 获取所有的省份数据
	 * @return
	 */
	public List<Province> loadProvince(){
		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				provinces.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null){
			cursor.close();
		}
		return provinces;
	}
	
	/**
	 * 插入城市数据
	 * @param city
	 */
	public void saveCity(City city){
		if (city != null){
			ContentValues values = new ContentValues();
			values.put("city_code", city.getCityCode());
			values.put("city_name", city.getCityName());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * 根据省份获取所有城市
	 * @param provinceId
	 * @return
	 */
	public List<City> getCitesById(int provinceId){
		List<City> cities = new ArrayList<City>();
		Cursor cursor = db.query("City", null, " province_id = ?", new String[]{provinceId+""}, null, null, null);
		if (cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				
				cities.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null){
			cursor.close();
		}
		return cities;
	}
	
	/**
	 * 保存乡村
	 * @param country
	 */
	public void saveCountry(Country country){
		if (country != null){
			ContentValues values = new ContentValues();
//			values.put("", country.getCityId());
			values.put("country_code", country.getCountryCode());
			values.put("country_name", country.getCountryName());
			values.put("city_id", country.getCityId());
			db.insert("Country", null, values);
		}
	}
	
	/**
	 * 根据城市ID获取其归属下面的所有乡村
	 * @param cityId
	 * @return
	 */
	public List<Country> queryCountriesByCityId(int cityId){
		List<Country> countries = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, " city_id = ? ", new String[]{""+ cityId}, null, null, null);
		if (cursor.moveToFirst()){
			do {
				Country country = new Country();
				country.setCityId(cityId);
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				countries.add(country);
			} while (cursor.moveToNext());
		}
		if (cursor != null){
			cursor.close();
		}
		return countries;
	}
	
}
