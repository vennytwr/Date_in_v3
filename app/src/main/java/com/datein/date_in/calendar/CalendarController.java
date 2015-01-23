package com.datein.date_in.calendar;


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

public class CalendarController {

	private static final String TAG = "CalendarController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static CalendarController instance;
	private DateInActivity activity;
	private StateChangeListener listener;
	private String currentState;

	public CalendarController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static CalendarController getInstance() {
		return instance;
	}

	public void onSync() {
		Logger.d(TAG, "New block size: " + activity.getCalendarBuilder().getNewBlockDates().size());
		Logger.d(TAG, "New unblock size: " + activity.getCalendarBuilder().getNewUnblockDates().size());

		for(int k = 0; k < activity.getCalendarBuilder().getNewBlockDates().size(); k++)
			System.out.println("block: " + activity.getCalendarBuilder().getNewBlockDates().get(k));

		for(int k = 0; k < activity.getCalendarBuilder().getNewUnblockDates().size(); k++)
			System.out.println("unblock: " + activity.getCalendarBuilder().getNewUnblockDates().get(k));

		doChangeState(Constants.STATE_EDIT_CALENDAR_SYNCING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_EDIT_CALENDAR);

					ArrayList<String> blockedDates = activity.getCalendarBuilder().getNewBlockDates();
					data.putString("blockDatesSize", String.valueOf(blockedDates.size()));
					int i = 0;
					for(int j = 0; j < blockedDates.size(); i++, j++)
						data.putString(String.valueOf(i), blockedDates.get(j));
					activity.getCalendarBuilder().getNewBlockDates().clear();

					ArrayList<String> unblockedDates = activity.getCalendarBuilder().getNewUnblockDates();
					data.putString("unblockDatesSize", String.valueOf(unblockedDates.size()));
					for(int j = 0; j < unblockedDates.size(); i++, j++)
						data.putString(String.valueOf(i), unblockedDates.get(j));
					activity.getCalendarBuilder().getNewUnblockDates().clear();

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

	public void onCalendar() {
		doChangeState(Constants.STATE_CALENDAR_REQUESTING);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_CALENDAR);
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

	public void onCalendarOK(Bundle data) {
		activity.getCalendarBuilder().updateCalendar(data);
		doChangeState(Constants.STATE_CALENDAR);
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
