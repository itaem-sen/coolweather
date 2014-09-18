package net.itaem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.itaem.db.CoolWeatherDB;
import net.itaem.model.City;
import net.itaem.model.Province;
import net.itaem.util.HttpCallbackListener;
import net.itaem.util.HttpURLUtils;
import net.itaem.util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	

	public static final int LEVEL_PROVINCE = 0 ;
	public static final int LEVEL_CITY = 1 ;
	
	private ProgressDialog progressDialog ;
	private TextView titleText ;
	private ListView listView ;
	private ArrayAdapter<String> adapter ;
	private CoolWeatherDB coolWeatherDB ;
	private List<String> dataList = new ArrayList<String>() ;
	
	/**
	 * 省列表
	 */
	private List<Province> provinceList ;
	/**
	 * 市列表
	 */
	private List<City> cityList ;
	
	/**
	 * 选中的省份
	 */
	private Province selectedProvince ;
	/**
	 * 选中的城市
	 */
	private City selectedCity ;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel ;
	
	/**
	 * 完成初始化数据
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*
		 * 1.获取布局，默认创建
		 */
		super.onCreate(savedInstanceState);
		
		/*
		 * SharedPreferences文件读取city_selected标志位
		 */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
		if (prefs.getBoolean("city_selected", false)) {
			Intent intent = new Intent(this,WeatherActivity.class) ;
			startActivity(intent) ;
			finish() ;
			return ;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		setContentView(R.layout.choose_area) ;
		
		/*
		 * 2.获取各个处理组件
		 */
		listView = (ListView) findViewById(R.id.list_view) ;
		titleText = (TextView) findViewById(R.id.title_text) ;
		
		/*
		 * 3.设置数据
		 */
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList) ;//适配器，用于对listView数据的适配。simple_list_item_1为该适配器的ID
		listView.setAdapter(adapter) ;
		
		/*
		 * 4.初始化数据库 
		 */
		coolWeatherDB = CoolWeatherDB.getInstance(this) ;
		/*
		 * 5.设置项点击事件
		 */
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position) ;
					queryCities() ;
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position) ;
					String cityCode = cityList.get(position).getCityCode() ;
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class) ;
					intent.putExtra("city_code", cityCode) ;
					startActivity(intent) ;
					finish(); 
				}
			}
			
		}) ;
		queryProvinces() ; //加载省级数据
	}
	
	/**
	 * 
	 * 查询全国所有省份。优先从数据库查询，如果没有查询到服务器上查询 
	 * @author sen
	 * @version 1.0,2014-9-12
	 */
	private void queryProvinces(){
		provinceList = coolWeatherDB.loadProvince() ;
		if(provinceList.size() > 0){
			dataList.clear() ;
			for(Province province:provinceList){
				dataList.add(province.getProvinceName()) ;
			}
			adapter.notifyDataSetChanged() ; //不用重新刷新Activity，通知Activity更新ListView。
			listView.setSelection(0) ;	//默认选中第一个
			titleText.setText("中国") ;	//中国
			currentLevel = LEVEL_PROVINCE ; //设置级别为省
		}else{
			queryFromServer(null,"province") ;	//从服务器读取
		}
	}
	
	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再上去服务器上查询
	 * This is a description text
	 * @author sen
	 * @version 1.0,2014-9-12
	 */
	private void queryCities(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId()) ;
		if(cityList.size() > 0){
			dataList.clear() ;
			for(City city : cityList){
				dataList.add(city.getCityName()) ;
			}
			//提醒数据改变，更新Activity
			adapter.notifyDataSetChanged() ;
			//默认选择第一项
			listView.setSelection(0) ;
			//设置标题
			titleText.setText(selectedProvince.getProvinceName()) ;
			//级别为City
			currentLevel = LEVEL_CITY ;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city") ;
		}
	}
	
	
	/*
	 * 	//省级Url
		private String provinceUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince" ;
		//市区Url
		private String cityUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode=" ;
	 */
	
	/**
	 * 
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * @author sen
	 * @version 1.0,2014-9-12
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code,final String type){
		String address ;
		Map<String,String> params = null ;
		if(code ==null || TextUtils.isEmpty(code)){
			address = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince" ;
		} else {
			address = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString" ;
			params = new HashMap<String,String>() ;
			params.put("theRegionCode", code) ;
		}
		
		showProgressDialog() ;
		
		/*
		 * 服务器抓取信息
		 */
		HttpURLUtils.doPost(address, params, new HttpCallbackListener(){
			/*
			 * 成功
			 */
			@Override
			public <T> void onFinish(T t) {
				// TODO Auto-generated method stub
				String response = (String) t ;
				boolean result = false ;
				/*
				 * 判断是哪个类型
				 * 	解析并插入数据库
				 */
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response) ;
				} else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId()) ;
				}
				
				
				/*
				 * 回到主线程更新界面信息
				 */
				if(result){
					//通过runOnUiThread方法回到主线程处理逻辑
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog() ;
							if("province".equals(type)){
								queryProvinces() ;
							}else if("city".equals(type)){
								queryCities() ;
							}
						}
						
					});
				}
			}
			
			/*
			 * 失败
			 */
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				Log.d("error", e.getMessage()) ;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog() ;
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show() ;
					}
					
				}) ;
			}
			
		}) ;

	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this) ;
			progressDialog.setMessage("正在加载...") ;
			progressDialog.setCanceledOnTouchOutside(false) ;
		}
		progressDialog.show() ;
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss() ;
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel == LEVEL_CITY){
			queryProvinces() ;
		} else {
			finish() ;
		}
	}
}
