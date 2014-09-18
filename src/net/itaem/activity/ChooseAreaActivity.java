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
	 * ʡ�б�
	 */
	private List<Province> provinceList ;
	/**
	 * ���б�
	 */
	private List<City> cityList ;
	
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince ;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity ;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel ;
	
	/**
	 * ��ɳ�ʼ������
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*
		 * 1.��ȡ���֣�Ĭ�ϴ���
		 */
		super.onCreate(savedInstanceState);
		
		/*
		 * SharedPreferences�ļ���ȡcity_selected��־λ
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
		 * 2.��ȡ�����������
		 */
		listView = (ListView) findViewById(R.id.list_view) ;
		titleText = (TextView) findViewById(R.id.title_text) ;
		
		/*
		 * 3.��������
		 */
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList) ;//�����������ڶ�listView���ݵ����䡣simple_list_item_1Ϊ����������ID
		listView.setAdapter(adapter) ;
		
		/*
		 * 4.��ʼ�����ݿ� 
		 */
		coolWeatherDB = CoolWeatherDB.getInstance(this) ;
		/*
		 * 5.���������¼�
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
		queryProvinces() ; //����ʡ������
	}
	
	/**
	 * 
	 * ��ѯȫ������ʡ�ݡ����ȴ����ݿ��ѯ�����û�в�ѯ���������ϲ�ѯ 
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
			adapter.notifyDataSetChanged() ; //��������ˢ��Activity��֪ͨActivity����ListView��
			listView.setSelection(0) ;	//Ĭ��ѡ�е�һ��
			titleText.setText("�й�") ;	//�й�
			currentLevel = LEVEL_PROVINCE ; //���ü���Ϊʡ
		}else{
			queryFromServer(null,"province") ;	//�ӷ�������ȡ
		}
	}
	
	/**
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ������ȥ�������ϲ�ѯ
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
			//�������ݸı䣬����Activity
			adapter.notifyDataSetChanged() ;
			//Ĭ��ѡ���һ��
			listView.setSelection(0) ;
			//���ñ���
			titleText.setText(selectedProvince.getProvinceName()) ;
			//����ΪCity
			currentLevel = LEVEL_CITY ;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city") ;
		}
	}
	
	
	/*
	 * 	//ʡ��Url
		private String provinceUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince" ;
		//����Url
		private String cityUrl = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode=" ;
	 */
	
	/**
	 * 
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
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
		 * ������ץȡ��Ϣ
		 */
		HttpURLUtils.doPost(address, params, new HttpCallbackListener(){
			/*
			 * �ɹ�
			 */
			@Override
			public <T> void onFinish(T t) {
				// TODO Auto-generated method stub
				String response = (String) t ;
				boolean result = false ;
				/*
				 * �ж����ĸ�����
				 * 	�������������ݿ�
				 */
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response) ;
				} else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId()) ;
				}
				
				
				/*
				 * �ص����̸߳��½�����Ϣ
				 */
				if(result){
					//ͨ��runOnUiThread�����ص����̴߳����߼�
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
			 * ʧ��
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show() ;
					}
					
				}) ;
			}
			
		}) ;

	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this) ;
			progressDialog.setMessage("���ڼ���...") ;
			progressDialog.setCanceledOnTouchOutside(false) ;
		}
		progressDialog.show() ;
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss() ;
		}
	}
	
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
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
