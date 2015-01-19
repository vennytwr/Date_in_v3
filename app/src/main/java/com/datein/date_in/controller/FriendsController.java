package com.datein.date_in.controller;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.log.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class FriendsController {

	private static final String TAG = "FriendsController";

	private Handler handler = new Handler(Looper.getMainLooper());

	private static FriendsController instance;
	private DateInActivity activity;
	private FriendsStateChangeListener listener;
	private String currentState;
	private String searchResult;
	private ArrayList<String> friendList;
	private ArrayList<String> requestList;
	private ArrayList<String> pendingList;
	private ArrayList<String> ignoreList;

	public FriendsController(DateInActivity activity) {
		this.activity = activity;
		this.currentState = Constants.STATE_START;
		this.friendList = new ArrayList<>();
		this.requestList = new ArrayList<>();
		this.pendingList = new ArrayList<>();
		this.ignoreList = new ArrayList<>();
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
		searchResult = data.getString("displayName");
		doChangeState(Constants.STATE_FRIENDS_SEARCH_OK, searchResult);
	}

	public void onAddFriend() {
		doChangeState(Constants.STATE_FRIENDS_ADDING, null);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_ADD_FRIEND);
					data.putString("requestTo", searchResult);
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

	public void onFriendList() {
		doChangeState(Constants.STATE_FRIENDS_LIST_REQUESTING, null);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected  Void doInBackground(Void... params) {
				try {
					Bundle data = new Bundle();
					String regId = activity.getRegistrationId();
					data.putString(Constants.REGISTRATION_ID, regId);
					data.putString(Constants.ACTION, Constants.ACTION_FRIEND);
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

	public void onFriendListOK(Bundle data) {
		int friendListIndex = Integer.valueOf(data.getString("friendListIndex"));
		int requestListIndex = Integer.valueOf(data.getString("requestListIndex"));
		int pendingListIndex = Integer.valueOf(data.getString("pendingListIndex"));
		int ignoreListIndex = Integer.valueOf(data.getString("ignoreListIndex"));
		int i = 0, j = 0;
		for(; j < friendListIndex; i++, j++)
			friendList.add(data.getString(String.valueOf(i)));
		for(j = 0; j < requestListIndex; i++, j++)
			requestList.add(data.getString(String.valueOf(i)));
		for(j = 0; j < pendingListIndex; i++, j++)
			pendingList.add(data.getString(String.valueOf(i)));
		for(j = 0; j < ignoreListIndex; i++, j++)
			ignoreList.add(data.getString(String.valueOf(i)));

		Logger.d(TAG, "F: " + friendListIndex);
		Logger.d(TAG, "R: " + requestListIndex);
		Logger.d(TAG, "P: " + pendingListIndex);
		Logger.d(TAG, "I: " + ignoreListIndex);

		doChangeState(Constants.STATE_FRIENDS_LIST_OK, null);
	}

	public ArrayList<String> getFriendList() {
		return friendList;
	}

	public ArrayList<String> getRequestList() {
		return requestList;
	}

	public ArrayList<String> getPendingList() {
		return pendingList;
	}

	public ArrayList<String> getIgnoreList() {
		return ignoreList;
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
