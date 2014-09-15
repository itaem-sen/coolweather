package net.itaem.util;

import java.util.Iterator;

import net.itaem.db.CoolWeatherDB;
import net.itaem.model.City;
import net.itaem.model.Province;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * 解析工具类
 * @author sen
 * @version 1.0,2014-9-11
 */
public class Utility {
	
	/**
	 * 
	 * 解析省集合
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
	 * 解析市集合
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
	
}
