package com.gaigai.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static final String GET = "GET";
	public static final String POST = "POST";

	public static final int CONNECT_TIMEOUT = 8000;
	public static final int READ_TIMEOUT = 8000;

	/**
	 * 像指定URL地址的获取数据
	 * @param address
	 * @param listener
	 */
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setReadTimeout(READ_TIMEOUT);
					connection.setConnectTimeout(CONNECT_TIMEOUT);
					connection.setRequestMethod(GET);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					StringBuffer result = new StringBuffer();
					while ((line = reader.readLine()) != null){
						result.append(line);
					}
					if (listener != null){
						listener.onSuccess(result.toString());
					}
				} catch (Exception e) {
					// TODO: handle exceptio
					if (listener != null){
						listener.onError(e);
					}
				}finally{
					if (connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
