package com.gaigai.coolweather.util;

public interface HttpCallbackListener {

	public void onSuccess(String response);
	
	public void onError(Exception error);
	
}
