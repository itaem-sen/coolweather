package net.itaem.activity;

import java.util.HashMap;
import java.util.Map;

import net.itaem.util.HttpCallbackListener;
import net.itaem.util.HttpURLUtils;
import net.itaem.util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{

	private LinearLayout weatherInfoLayout ;
	
	/**
	 * 显示城市名
	 */
	private TextView cityNameText ;
	/**
	 * 发布时间
	 */
	private TextView publishText ;
	/**
	 * 显示天气描述信息
	 */
	private TextView weatherDespText ;
	/**
	 * 显示气温1
	 */
	private TextView temp1Text ;
	/**
	 * 显示气温2
	 */
	private TextView temp2Text ;
	/**
	 * 显示当前日期
	 */
	private TextView currentDateText ;
	/**
	 * 切换城市按钮 
	 */
	private Button switchCity ;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		setContentView(R.layout.weather_layout) ;
		//初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout) ;
		cityNameText = (TextView) findViewById(R.id.city_name) ;
		publishText = (TextView) findViewById(R.id.public_text) ;
		weatherDespText = (TextView) findViewById(R.id.weather_desp) ;
		temp1Text = (TextView) findViewById(R.id.temp1) ;
		temp2Text = (TextView) findViewById(R.id.temp2) ;
		currentDateText = (TextView) findViewById(R.id.current_date) ;
		//switchCity = (Button) findViewById(R.id.switch_city) ;
		//refreshWeather = (Button) findViewById(R.id.refresh_weather) ;
		
		//从intent中取出cityCode
		String cityCode = getIntent().getStringExtra("city_code") ;
		//如果cityCode不为空
		if(!TextUtils.isEmpty(cityCode)){
			publishText.setText("同步中...") ;
			weatherInfoLayout.setVisibility(View.INVISIBLE) ;
			cityNameText.setVisibility(View.INVISIBLE) ;
			queryWeatherInfo(cityCode) ;
		//cityCode为空
		}else{
			showWeather() ;
		}
		//切换城市按钮
		//switchCity.setOnClickListener(this) ;
		//刷新天气
		//refreshWeather.setOnClickListener(this) ;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*switch(v.getId()){
			case R.id.switch_city:
				Intent intent = new Intent(this,ChooseAreaActivity.class) ;
				intent.putExtra("from_weather_activity", true) ;
				startActivity(intent) ;
				finish() ;
				break ;
			case R.id.refresh_weather:
				publishText.setText("同步中...") ;
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
				String cityCode = prefs.getString("city_code", "") ;
				if(!TextUtils.isEmpty(cityCode)){
					queryWeatherInfo(cityCode) ;
				}
				break ;
			default:
				break ;
		}*/
	}

	/**
	 * 查询天气
	 */
	private void queryWeatherInfo(String cityCode){
		String weatherUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather" ;
		queryFromServer(weatherUrl, cityCode) ;
	}
	
	/**
	 * 根据传入的地址和类型去想服务器查询天气信息
	 */
	private void queryFromServer(final String address, final String cityCode){
		Map<String,String> params = new HashMap<String,String>() ;
		params.put("theCityCode", cityCode) ;
		params.put("theUserId", "") ;
		HttpURLUtils.doPost(address, params, new HttpCallbackListener() {
			
			@Override
			public <T> void onFinish(T t) {
				// TODO Auto-generated method stub
				String response = (String) t ;
				Utility.handleWeatherResponse(WeatherActivity.this, response) ;
				
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						showWeather() ;
					}
					
				}) ;
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败") ;
					}
					
				}) ;
			}
		}) ;
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
		cityNameText.setText(prefs.getString("city_name", "")) ;
		temp1Text.setText(prefs.getString("temp1", "")) ;
		temp2Text.setText(prefs.getString("temp2", "")) ;
		weatherDespText.setText(prefs.getString("weather_desp", "")) ;
		publishText.setText("今天"+prefs.getString("publish_time", "") + "发布") ;
		currentDateText.setText(prefs.getString("current_date", "")) ;
		weatherInfoLayout.setVisibility(View.VISIBLE) ;
		cityNameText.setVisibility(View.VISIBLE) ;
	}
}
