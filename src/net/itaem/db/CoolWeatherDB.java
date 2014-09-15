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
 * ���ݿ������
 * @author sen
 * @version 1.0,2014-9-11
 */
public class CoolWeatherDB {

	public static final String DB_NAME = "cool_weather" ; //���ݿ�����
	
	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1 ;//�汾1
	
	private static CoolWeatherDB coolWeatherDB ; //����
	
	private SQLiteDatabase db ; //���ݿ�д
	
	/**
	 * ˽�й���������֤����
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION) ;
		/*
		 * �������һ�����е����ݿ⣬������һ���ɶ����ݿ���ж�д�����Ķ���
		 * getReaderableDatabase() ; ���ݿⲻ��д��ʱ������̿ռ����������ض���ֻ�ܶ���ʽ�����ݿ�
		 * getWritableDatabase() ; ���ݿⲻ��д��ʱ������̿ռ��������쳣
		 */
		db = dbHelper.getWritableDatabase() ; 
	}
	
	/**
	 * 
	 * ��ȡʵ������
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
	 * ���Province
	 * 
	 * ContentValues ���ݶ��󣬲����������ݶ���
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
	 * ��������Province
	 * 
	 * Cursor ���������
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
	 * ���City
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
	 * ����City ����ProvinceId
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
