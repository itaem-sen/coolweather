package net.itaem.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import net.itaem.db.CoolWeatherDB;
import net.itaem.model.City;
import net.itaem.model.Province;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * ����������
 * @author sen
 * @version 1.0,2014-9-11
 */
public class Utility {
	
	/**
	 * 
	 * ����ʡ����
	 * @author sen
	 * @version 1.0,2014-9-15
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response) {
		if(!TextUtils.isEmpty(response)){
			Document doc = null ;
			try {
				doc = DocumentHelper.parseText(response) ;
				Element root = doc.getRootElement() ;
				Iterator iter = root.elementIterator("string") ;
				while(iter.hasNext()){
					Element ele = (Element)iter.next() ;
					String info = ele.getTextTrim() ;
					String[] array = info.split(",") ;
					Province province = new Province() ;
					province.setProvinceName(array[0]) ;
					province.setProvinceCode(array[1]) ;
					
					coolWeatherDB.saveProvince(province) ;
				}
				return true;
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				Log.e("errorPro", e.getMessage()) ;
			}
		}
		return false ;
	}
	
	/**
	 * 
	 * �����м���
	 * @author sen
	 * @version 1.0,2014-9-15
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			Document doc = null ;
			try {
				doc = DocumentHelper.parseText(response) ;
				Element root = doc.getRootElement() ;
				Iterator iter = root.elementIterator("string") ;
				while(iter.hasNext()){
					Element ele = (Element)iter.next() ;
					String info = ele.getTextTrim() ;
					String[] array = info.split(",") ;
					City city = new City() ;
					city.setCityName(array[0]) ;
					city.setCityCode(array[1]) ;
					city.setProvinceId(provinceId) ;
					coolWeatherDB.saveCity(city) ;
				}
				return true;
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				Log.e("errorCity", e.getMessage()) ;
			}
		}
		return false ;
	}
	
	/**
	 * 
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 * @author sen
	 * @version 1.0,2014-9-15
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		if(!TextUtils.isEmpty(response)){
			Document doc = null ;
			try {
				doc = DocumentHelper.parseText(response) ;
				Element root = doc.getRootElement() ;
				Iterator iter = root.elementIterator("string") ;
				
				Element elementOne = (Element)iter.next() ;
				String cityName = elementOne.getText() ;
				
				int i = 0;
				
				while(iter.hasNext()){
					if(i>5){
						Element element = (Element)iter.next() ;
						String info = element.getText() ;
						Log.d("info", info);
					}
					i++ ;
				}
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				Log.e("errorCity", e.getMessage()) ;
			}
		}
	}
	
	/**
	 * 
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
	 * @author sen
	 * @version 1.0,2014-9-15
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */
	public static void saveWeatherInfo(Context context,String cityName,
			String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA) ;
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit() ;
		editor.putBoolean("city_selected", true) ;
		editor.putString("city_name", cityName) ;
		editor.putString("weather_code", weatherCode) ;
		editor.putString("temp1", temp1) ;
		editor.putString("temp2", temp2) ;
		editor.putString("weather_deep", weatherDesp);
		editor.putString("publish_time", publishTime) ;
		editor.putString("current_date", sdf.format(new Date())) ;
		editor.commit() ;
	}
	
}
