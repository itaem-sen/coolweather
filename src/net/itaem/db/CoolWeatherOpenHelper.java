package net.itaem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 数据库创建类
 * @author sen
 * @version 1.0,2014-9-11
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{

	/**
	 * Province表建表语句
	 */
	public static final String CREATE_PROVINCE = "create table Province(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)" ;
	
	/**
	 * City表建表语句
	 */
	public static final String CREATE_CITY = "create table City(" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)" ;
	
	
	/**
	 * SQLite对象
	 * @param context 上下文
	 * @param name	数据库名字
	 * @param factory 查询数据库时，返回的自定义cursor。一般为null
	 * @param version 版本号
	 */
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE) ;
		db.execSQL(CREATE_CITY) ;
	}

	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
}
