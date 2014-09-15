package net.itaem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * ���ݿⴴ����
 * @author sen
 * @version 1.0,2014-9-11
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{

	/**
	 * Province�������
	 */
	public static final String CREATE_PROVINCE = "create table Province(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)" ;
	
	/**
	 * City�������
	 */
	public static final String CREATE_CITY = "create table City(" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)" ;
	
	
	/**
	 * SQLite����
	 * @param context ������
	 * @param name	���ݿ�����
	 * @param factory ��ѯ���ݿ�ʱ�����ص��Զ���cursor��һ��Ϊnull
	 * @param version �汾��
	 */
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	/**
	 * �������ݿ�
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE) ;
		db.execSQL(CREATE_CITY) ;
	}

	/**
	 * �������ݿ�
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
}
