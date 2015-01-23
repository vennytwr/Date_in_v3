package com.datein.date_in.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.StateChangeListener;
import com.datein.date_in.log.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

	private static final String TAG = "MainFragmentController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static MainController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;

	public MainController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static MainController getInstance() {
		return instance;
	}

//
//	public void onGetCalendar() {
//		doChangeState(Constants.STATE_EDIT_CALENDAR_GETTING);
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected  Void doInBackground(Void... params) {
//				try {
//					Bundle data = new Bundle();
//					String regId = activity.getRegistrationId();
//					data.putString(Constants.REGISTRATION_ID, regId);
//					data.putString(Constants.ACTION, Constants.ACTION_EDIT_CALENDAR_GET);
//					String msgId = Integer.toString(activity.getNextMsgId());
//					activity.getGcm().send(
//							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
//				} catch (IOException e) {
//					Logger.d(TAG, "IOException: " + e);
//				}
//				return null;
//			}
//		}.execute();
//	}
//
//	public ArrayList<String> getCalendarSync() {
//		return calendarSync;
//	}

	public void setCurrentState(String state) {
		this.currentState = state;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setListener(StateChangeListener listener) {
		this.listener = listener;
	}

	public StateChangeListener getListener() {
		return listener;
	}

	public void doChangeState(final String nState) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onStateChanged(nState);
				}
			}
		});
	}
}
