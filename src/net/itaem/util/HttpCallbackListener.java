package net.itaem.util;

/**
 * 
 * HttpClientUtils�첽������Ҫ�Ľӿ�
 * @author sen
 * @version 1.0,2014��9��9��
 */
public interface HttpCallbackListener {

	public <T> void onFinish(T t) ;
	
	public void onError(Exception e) ;
}
