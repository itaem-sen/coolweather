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
	 * ��ʾ������
	 */
	private TextView cityNameText ;
	/**
	 * ����ʱ��
	 */
	private TextView publishText ;
	/**
	 * ��ʾ����������Ϣ
	 */
	private TextView weatherDespText ;
	/**
	 * ��ʾ����1
	 */
	private TextView temp1Text ;
	/**
	 * ��ʾ����2
	 */
	private TextView temp2Text ;
	/**
	 * ��ʾ��ǰ����
	 */
	private TextView currentDateText ;
	/**
	 * �л����а�ť 
	 */
	private Button switchCity ;
	/**
	 * ����������ť
	 */
	private Button refreshWeather ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		setContentView(R.layout.weather_layout) ;
		//��ʼ�����ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout) ;
		cityNameText = (TextView) findViewById(R.id.city_name) ;
		publishText = (TextView) findViewById(R.id.public_text) ;
		weatherDespText = (TextView) findViewById(R.id.weather_desp) ;
		temp1Text = (TextView) findViewById(R.id.temp1) ;
		temp2Text = (TextView) findViewById(R.id.temp2) ;
		currentDateText = (TextView) findViewById(R.id.current_date) ;
		//switchCity = (Button) findViewById(R.id.switch_city) ;
		//refreshWeather = (Button) findViewById(R.id.refresh_weather) ;
		
		//��intent��ȡ��cityCode
		String cityCode = getIntent().getStringExtra("city_code") ;
		//���cityCode��Ϊ��
		if(!TextUtils.isEmpty(cityCode)){
			publishText.setText("ͬ����...") ;
			weatherInfoLayout.setVisibility(View.INVISIBLE) ;
			cityNameText.setVisibility(View.INVISIBLE) ;
			queryWeatherInfo(cityCode) ;
		//cityCodeΪ��
		}else{
			showWeather() ;
		}
		//�л����а�ť
		//switchCity.setOnClickListener(this) ;
		//ˢ������
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
				publishText.setText("ͬ����...") ;
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
	 * ��ѯ����
	 */
	private void queryWeatherInfo(String cityCode){
		String weatherUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather" ;
		queryFromServer(weatherUrl, cityCode) ;
	}
	
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ������Ϣ
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
						publishText.setText("ͬ��ʧ��") ;
					}
					
				}) ;
			}
		}) ;
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
		cityNameText.setText(prefs.getString("city_name", "")) ;
		temp1Text.setText(prefs.getString("temp1", "")) ;
		temp2Text.setText(prefs.getString("temp2", "")) ;
		weatherDespText.setText(prefs.getString("weather_desp", "")) ;
		publishText.setText("����"+prefs.getString("publish_time", "") + "����") ;
		currentDateText.setText(prefs.getString("current_date", "")) ;
		weatherInfoLayout.setVisibility(View.VISIBLE) ;
		cityNameText.setVisibility(View.VISIBLE) ;
	}
}
