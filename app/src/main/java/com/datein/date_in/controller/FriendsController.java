package com.datein.date_in.controller;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.log.Logger;

import java.io.IOException;

public class FriendsController {

	private static final String TAG = "FriendsController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static FriendsController instance;
	private DateInActivity activity;
	private FriendsStateChangeListener listener;
	private String currentState;

	public FriendsController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		instance = this;
	}

	public static FriendsController getInstance() {
		return instance;
	}

	public void onSearch(final String searchString) {
		doChangeState(Constants.STATE_FRIENDS_SEARCHING, null);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_SEARCH);
					data.putString("search", searchString);
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

	public void onSearchOK(Bundle data) {
		String displayName = data.getString("displayName");
		doChangeState(Constants.STATE_FRIENDS_SEARCH_OK, displayName);
	}

	public void setCurrentState(String state) {
		this.currentState = state;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setListener(FriendsStateChangeListener listener) {
		this.listener = listener;
	}

	public FriendsStateChangeListener getListener() {
		return listener;
	}

	public void doChangeState(final String nState, final String displayName) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onStateChanged(nState, displayName);
				}
			}
		});
	}
}
