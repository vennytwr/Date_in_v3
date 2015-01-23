package com.datein.date_in.events;


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

public class CreateEventController {

	private static final String TAG = "CreateEventController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static CreateEventController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;

	public CreateEventController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static CreateEventController getInstance() {
		return instance;
	}

	public void onRequestCommonTime(final ArrayList<String> friendsAdded) {
		doChangeState(Constants.STATE_EVENTS_REQUEST_COMMON_TIME);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_EVENT_REQUEST);

					data.putString("friendsAddedSize", String.valueOf(friendsAdded.size()));
					for(int i = 0; i < friendsAdded.size(); i++)
						data.putString(String.valueOf(i), friendsAdded.get(i));

					String msgId = Integer.toString(activity.getNextMsgId());
					activity.getGcm().send(
							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
				} catch (IOException e) {
					Logger.d(TAG, "IOException: " + e);
				}
				return null;
			}
		}.execute();
	}

	public void onRequestCommonTimeOK(Bundle data) {
		activity.getCalendarBuilder().updateCalendar(data);
		doChangeState(Constants.STATE_EVENTS_REQUEST_COMMON_TIME_OK);
	}

	public void onCreatEvent(final String selectedDate, final ArrayList<String> friendsAdded) {
		doChangeState(Constants.STATE_EVENTS_CREATING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_EVENT_CREATE);

					data.putString("selectedDate", selectedDate);
					data.putString("friendsAddedSize", String.valueOf(friendsAdded.size()));
					for(int i = 0; i < friendsAdded.size(); i++)
						data.putString(String.valueOf(i), friendsAdded.get(i));

					String msgId = Integer.toString(activity.getNextMsgId());
					activity.getGcm().send(
							Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
				} catch (IOException e) {
					Logger.d(TAG, "IOException: " + e);
				}
				return null;
			}
		}.execute();
	}

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
