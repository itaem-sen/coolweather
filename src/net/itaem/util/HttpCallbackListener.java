package net.itaem.util;

/**
 * 
 * HttpClientUtils异步操作需要的接口
 * @author sen
 * @version 1.0,2014年9月9日
 */
public interface HttpCallbackListener {

	public <T> void onFinish(T t) ;
	
	public void onError(Exception e) ;
}
