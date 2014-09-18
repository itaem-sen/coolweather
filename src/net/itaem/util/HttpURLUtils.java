package net.itaem.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * UrlConnection抓包
 * @author sen
 * @version 1.0,2014年8月28日
 */
public class HttpURLUtils {
	
	/*public static void main(String[] args) throws Exception {
		Map<String,String> params = new HashMap<String,String>() ;
		params.put("theCityCode", "2350") ;
		params.put("theUserId", "") ;
		System.out.println(HttpURLUtils.doPost("http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather", params));
	}*/
	
	/**
	 * 
	 * 1.GET请求
	 * @author sen
	 * @param urlStr url地址
	 * @return 返回信息
	 * @throws IOException
	 */
	public static String doGet(String urlStr) throws IOException{
		
		//获取连接对象
		URLConnection connection = getURLConnection(urlStr) ;
		
		//返回接收信息
		return receiveMsg(connection) ;
	}
	
	/**
	 * 
	 * GET请求 （异步）
	 * @author sen
	 * @param urlStr
	 * @param callback
	 * @return
	 * @throws IOException
	 */
	public static void doGet(final String urlStr,final HttpCallbackListener listener) {
		
		new Thread(new Runnable(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				URLConnection connection = null ;
				try {
					//获取连接对象
					connection = getURLConnection(urlStr);
					
					//获取返回信息
					if(listener != null)
						listener.onFinish(receiveMsg(connection)) ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(listener != null)
						listener.onError(e);
				}
				
			}
			
		}).start();
		
	}
	
	/**
	 * 
	 * 2.POST请求
	 * @author sen
	 * @param urlStr url地址
	 * @param params 请求参数
	 * @return 返回信息
	 * @throws IOException
	 */
	public static String doPost(String urlStr,Map<String,String> params) throws IOException{
		
		//获取连接对象
		URLConnection connection = getURLConnection(urlStr) ;
		//发送数据
		sendMsg(connection,params);
		
		//返回接收信息
		return receiveMsg(connection) ;
	}
	
	/**
	 * 
	 * POST请求（异步）
	 * This is a description text
	 * @author sen
	 * @param urlStr
	 * @param params
	 * @param callback
	 * @return
	 * @throws IOException
	 */
	public static void doPost(final String urlStr,final Map<String,String> params,final HttpCallbackListener listener) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				URLConnection connection = null;
				try {
					
					//获取连接对象
					connection = getURLConnection(urlStr);
					//发送数据
					sendMsg(connection,params);
					
					//获取返回信息
					if(listener != null)
						listener.onFinish(receiveMsg(connection)) ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(listener != null)
						listener.onError(e);
				}
			}
		}).start();
		
	}
	
	
	
	
	
	/**
	 * 
	 * 获取URLConnection连接对象
	 * @author sen
	 * @param urlStr url地址
	 * @return
	 * @throws IOException
	 */
	private static URLConnection getURLConnection(String urlStr) throws IOException{
		//实例化URL对象
		URL url = new URL(urlStr) ;
		//获取URLConnection对象
		URLConnection connection = url.openConnection() ;
		//初始化HttpURLConnection对象
		//HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//设置请求头信息
		connection.setRequestProperty("Accept-Charset", "gbk");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="+"gbk");
		//设置连接超时时间
		connection.setConnectTimeout(8000);
		//设置读取document超时时间
		connection.setReadTimeout(8000);
		//设置接收数据权限
		connection.setDoInput(true);
		//设置发送数据权限
		connection.setDoOutput(true);
		//返回对象
		return connection ;
	}
	
	/**
	 * 
	 * 发送信息
	 * @author sen
	 * @param connection 连接对象
	 * @param params
	 * @throws IOException
	 */
	private static void sendMsg(URLConnection connection,Map<String,String> params) throws IOException{
		//设置为可向服务器发送信息
		connection.setDoOutput(true);
		/*
		 * POST请求发送数据
		 */
		//获取输出流，使客户端也可以想服务器进行写操作
		if(params!=null && params.size()>0){
			PrintWriter out = new PrintWriter(connection.getOutputStream()) ;
			boolean first = true ;
			for(Map.Entry<String, String> param : params.entrySet()){
				if(first)
					first = false ;
				else
					out.print('&');
				String name = param.getKey() ;
				String value = param.getValue() ;
				
				out.print(name);
				out.print('=');
				out.print(URLEncoder.encode(value,"UTF-8")); //发送键值对
			}
			
			out.close(); //关闭输出流
		}
	}
	
	/**
	 * 
	 * 接收信息
	 * @author sen
	 * @param connection 连接对象
	 * @return
	 * @throws IOException
	 */
	private static String receiveMsg(URLConnection connection) throws IOException{
		/*
		 * 接收返回信息
		 */
		Scanner in ;
		StringBuilder response = new StringBuilder() ;
		try{
			in = new Scanner(connection.getInputStream()) ;
		}catch(IOException e){
			if(!(connection instanceof HttpURLConnection))
				throw e ;
			InputStream error = ((HttpURLConnection)connection).getErrorStream() ;
			if(error == null)
				throw e ;
			in = new Scanner(error) ;
		}
		
		while(in.hasNextLine()){
			response.append(in.nextLine()) ;
			response.append("\n") ;
		}
		
		in.close();
		return response.toString() ;
	}
	
}

