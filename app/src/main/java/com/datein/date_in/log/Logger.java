package com.datein.date_in.log;

import android.util.Log;

/**
 * Created by Yiming on 2/12/2014.
 */
public class Logger {

	private static final boolean ENABLED = true;

	public static void d(String TAG, String message) {
		if(ENABLED) {
			Log.d("MainLogger|" + TAG, message);
		}
	}
}
