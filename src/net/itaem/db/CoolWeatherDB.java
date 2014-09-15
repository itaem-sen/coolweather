package net.itaem.db;

import java.util.ArrayList;
import java.util.List;

import net.itaem.model.City;
import net.itaem.model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 数据库控制类
 * @author sen
 * @version 1.0,2014-9-11
 */
public class CoolWeatherDB {

	public static final String DB_NAME = "cool_weather" ; //数据库名称
	
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1 ;//版本1
	
	private static CoolWeatherDB coolWeatherDB ; //单例
	
	private SQLiteDatabase db ; //数据库写
	
	/**
	 * 私有构造器，保证单例
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION) ;
		/*
		 * 创建或打开一个现有的数据库，并返回一个可对数据库进行读写操作的对象
		 * getReaderableDatabase() ; 数据库不可写入时（如磁盘空间已满）返回对象只能读方式打开数据库
		 * getWritableDatabase() ; 数据库不可写入时（如磁盘空间已满）异常
		 */
		db = dbHelper.getWritableDatabase() ; 
	}
	
	/**
	 * 
	 * 获取实例对象
	 * @author sen
	 * @version 1.0,2014-9-11
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context) ;
		}
		return coolWeatherDB ;
	}
	
	
	/**
	 * 
	 * 添加Province
	 * 
	 * ContentValues 数据对象，插入语句的数据对象
	 * @author sen
	 * @version 1.0,2014-9-11
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues() ;
			values.put("province_name", province.getProvinceName()) ;
			values.put("province_code", province.getProvinceCode()) ;
			db.insert("Province", null, values) ;
		}
	}
	
	/**
	 * 
	 * 查找所有Province
	 * 
	 * Cursor 结果集对象
	 * @author sen
	 * @version 1.0,2014-9-11
	 * @return
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>() ;
		Cursor cursor = db.query("Province", null, null, null, null, null, null) ;
		
		if(cursor.moveToFirst()){
			do{
				Province province = new Province() ;
				province.setId(cursor.getInt(cursor.getColumnIndex("id"))) ;
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name"))) ;
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province) ;
			}while(cursor.moveToNext()) ;
		}
		
		return list ;
	}
	
	/**
	 * 
	 * 添加City
	 * @author sen
	 * @version 1.0,2014-9-11
	 * @param city
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues() ;
			values.put("city_name", city.getCityName()) ;
			values.put("city_code", city.getCityCode()) ;
			values.put("province_id", city.getProvinceId()) ;
			db.insert("City", null, values) ;
		}
	}
	
	/**
	 * 
	 * 查找City 根据ProvinceId
	 * @author sen
	 * @version 1.0,2014-9-11
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>() ;
		Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null) ;
		
		if(cursor.moveToFirst()){
			do{
				City city = new City() ;
				city.setId(cursor.getInt(cursor.getColumnIndex("id"))) ;
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name"))) ;
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code"))) ;
				city.setProvinceId(provinceId) ;
				
				list.add(city) ;
			} while(cursor.moveToNext()) ;
		}
		
		return list ;
	}
}
