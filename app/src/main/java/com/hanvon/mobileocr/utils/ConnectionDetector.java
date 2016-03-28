package com.hanvon.mobileocr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Net Check
 * @author Hu
 *
 */
public class ConnectionDetector
{
	
	private Context context;
	
	public ConnectionDetector(Context context) {
		this.context = context;
	}
	
	public boolean isConnectingTOInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isWifi() {
		ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}

		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}
