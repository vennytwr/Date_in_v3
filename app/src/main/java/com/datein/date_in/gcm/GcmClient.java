//package com.datein.date_in.gcm;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//
//import com.datein.date_in.log.Logger;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//
//import java.io.IOException;
//
//import de.greenrobot.event.EventBus;
//
//public class GcmClient {
//
//	private static final String TAG = "GcmClient";
//
//	public static void doAction(Intent intent, Context mContext) {
//		String action = intent.getAction();
//		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
//		Logger.d(TAG, "Action: " + action);
//		switch (action) {
//			case Constants.ACTION_REGISTER:
//				register(gcm, intent);
//				break;
//			case Constants.ACTION_UNREGISTER:
//				unregister(gcm, intent);
//				break;
//			case Constants.ACTION_ECHO:
//				sendMessage(gcm, intent);
//				break;
//		}
//	}
//
//	private void unregister(GoogleCloudMessaging gcm, Intent intent) {
//		try {
//			Logger.d(TAG, "About to unregister...");
//			gcm.unregister();
//			Logger.d(TAG, "Device unregistered");
//
//			// Persist the regID - no need to register again.
//			removeRegistrationId();
//			Bundle bundle = new Bundle();
//			bundle.putInt(Constants.KEY_EVENT_TYPE,
//					Constants.EventbusMessageType.UNREGISTRATION_SUCCEEDED.ordinal());
//			EventBus.getDefault().post(bundle);
//		} catch (IOException e) {
//			// If there is an error, don't just keep trying to register.
//			// Require the user to click a button again, or perform
//			// exponential back-off.
//
//			// I simply notify the user:
//			Bundle bundle = new Bundle();
//			bundle.putInt(Constants.KEY_EVENT_TYPE,
//					Constants.EventbusMessageType.UNREGISTRATION_FAILED.ordinal());
//			EventBus.getDefault().post(bundle);
//			Logger.d(TAG, "Unregistration failed: " + e);
//		}
//	}
//
//	private void register(GoogleCloudMessaging gcm, Intent intent) {
//		try {
//			Logger.d(TAG, "About to register...");
//			String regid = gcm.register(mSenderId);
//			Logger.d(TAG, "Device registered: " + regid);
//
//			String username = intent.getStringExtra(Constants.KEY_USERNAME);
//			String email = intent.getStringExtra(Constants.KEY_EMAIL);
//			String password = intent.getStringExtra(Constants.KEY_PASSWORD);
//			sendRegistrationIdToBackend(gcm, regid, username, email, password);
//
//			// Persist the regID - no need to register again.
//			//storeRegistrationId(regid);
//			Bundle bundle = new Bundle();
//			bundle.putInt(Constants.KEY_EVENT_TYPE,
//					Constants.EventbusMessageType.REGISTRATION_SUCCEEDED.ordinal());
//			bundle.putString(Constants.KEY_REG_ID, regid);
//			EventBus.getDefault().post(bundle);
//		} catch (IOException e) {
//			// If there is an error, don't just keep trying to register.
//			// Require the user to click a button again, or perform
//			// exponential back-off.
//
//			// I simply notify the user:
//			Bundle bundle = new Bundle();
//			bundle.putInt(Constants.KEY_EVENT_TYPE,
//					Constants.EventbusMessageType.REGISTRATION_FAILED.ordinal());
//			EventBus.getDefault().post(bundle);
//			Logger.d(TAG, "Registration failed: " + e);
//		}
//	}
//
//	private void storeRegistrationId(String regId) {
//		final SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(this);
//		Logger.d(TAG, "Saving regId to prefs: " + regId);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putString(Constants.KEY_REG_ID, regId);
//		editor.putInt(Constants.KEY_STATE, Constants.State.REGISTERED.ordinal());
//		editor.apply();
//	}
//
//	private void removeRegistrationId() {
//		final SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(this);
//		Logger.d(TAG, "Removing regId from prefs..");
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.remove(Constants.KEY_REG_ID);
//		editor.putInt(Constants.KEY_STATE, Constants.State.UNREGISTERED.ordinal());
//		editor.apply();
//	}
//
//	private void sendRegistrationIdToBackend(GoogleCloudMessaging gcm, String regId,
//	                                         String username, String email, String password) {
//		try {
//			Bundle data = new Bundle();
//			// the name is used for keeping track of user notifications
//			// if you use the same name everywhere, the notifications will
//			// be cancelled
//			data.putString("username", username);
//			data.putString("email", email);
//			data.putString("password", password);
//			data.putString("register_id", regId);
//			data.putString("action", Constants.ACTION_REGISTER);
//			String msgId = Integer.toString(getNextMsgId());
//			gcm.send(mSenderId + "@gcm.googleapis.com", msgId,
//					Constants.GCM_DEFAULT_TTL, data);
//			Logger.d(TAG, "RegId sent: " + regId);
//		} catch (IOException e) {
//			Logger.d(TAG,
//					"IOException while sending registration to backend... " + e);
//		}
//	}
//
//	private void sendMessage(GoogleCloudMessaging gcm, Intent intent) {
//		try {
//			String msg = intent.getStringExtra(Constants.KEY_MESSAGE_TXT);
//			Bundle data = new Bundle();
//			data.putString(Constants.ACTION, Constants.ACTION_ECHO);
//			data.putString("message", msg);
//			String id = Integer.toString(getNextMsgId());
//			gcm.send(mSenderId + "@gcm.googleapis.com", id, data);
//			Logger.d(TAG, "Sent message: " + msg);
//		} catch (IOException e) {
//			Logger.d(TAG, "Error while sending a message: " + e);
//		}
//	}
//
//	private int getNextMsgId() {
//		SharedPreferences prefs = getPrefs();
//		int id = prefs.getInt(Constants.KEY_MSG_ID, 0);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putInt(Constants.KEY_MSG_ID, ++id);
//		editor.apply();
//		return id;
//	}
//
//	private SharedPreferences getPrefs() {
//		return PreferenceManager.getDefaultSharedPreferences(this);
//	}
//}
