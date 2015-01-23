package com.datein.date_in;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.datein.date_in.calendar.CalendarBuilder;
import com.datein.date_in.calendar.CalendarController;
import com.datein.date_in.calendar.EditCalendarFragment;
import com.datein.date_in.events.CreateEventController;
import com.datein.date_in.events.CreateEventFragment;
import com.datein.date_in.friends.FriendsController;
import com.datein.date_in.log.Logger;
import com.datein.date_in.login_register.LoginRegisterController;
import com.datein.date_in.login_register.LoginRegisterFragment;
import com.datein.date_in.main.MainController;
import com.datein.date_in.main.MainFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class DateInActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";

	private LoginRegisterController loginRegisterController;
	private MainController mainController;
	private FriendsController friendsController;
	private CalendarController calendarController;
	private CreateEventController createEventController;
	private CalendarBuilder calendarBuilder;
	private String regId;
	private int appVersion;
	private GoogleCloudMessaging gcm;
	private float density;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		long start = SystemClock.uptimeMillis();
		super.onCreate(savedInstanceState);

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId();

			if (regId.isEmpty()) {
				registerDevice();
			}
		} else {
			Logger.d(TAG, "No valid Google Play Services APK found.");
		}

		density = getResources().getDisplayMetrics().density;
		calendarBuilder = new CalendarBuilder(this);
		loginRegisterController = new LoginRegisterController(this);
		mainController = new MainController(this);
		friendsController = new FriendsController(this);
		calendarController = new CalendarController(this);
		createEventController = new CreateEventController(this);

		setContentView(R.layout.activity_main);

		if(getLoggedIn())
			doInitApp(false);
		else
			doInitApp(true);

		Logger.d(TAG, "Everything loaded in " + (SystemClock.uptimeMillis() - start) + " ms");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK.
		checkPlayServices();
	}

	@Override
	protected void onNewIntent(Intent intent) {

	}

	@Override
	public void onBackPressed() {
		if (loginRegisterController.getCurrentState().equals(Constants.STATE_REGISTER)) {
			loginRegisterController.doChangeState(Constants.STATE_LOGIN);
			return;
		}

		new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				})
				.show();
	}

	public float getDensity() {
		return density;
	}

	public LoginRegisterController getLoginRegisterController() {
		return loginRegisterController;
	}

	public MainController getMainController() {
		return mainController;
	}

	public FriendsController getFriendsController() {
		return friendsController;
	}

	public CalendarController getCalendarController() {
		return calendarController;
	}

	public CreateEventController getCreateEventController() {
		return createEventController;
	}

	public GoogleCloudMessaging getGcm() {
		return gcm;
	}

	public CalendarBuilder getCalendarBuilder() {
		return calendarBuilder;
	}

	public void doInitApp(boolean firstAttempt) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (firstAttempt) {
			transaction.add(R.id.fragmentContainer, new LoginRegisterFragment(), "loginRegisterFragment");
		} else {
			transaction.replace(R.id.fragmentContainer, new MainFragment(), "mainFragment");
		}
		transaction.commit();
	}

	public void openEditCalendar() {
		getMainController().doChangeState(Constants.STATE_MAIN_DRAWER_OPEN);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out);
		transaction.replace(R.id.fragmentContainer, new EditCalendarFragment(), "editCalendarFragment");
		transaction.commit();
	}

	public void closeEditCalendar() {
		getMainController().doChangeState(Constants.STATE_MAIN_DRAWER_CLOSE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.replace(R.id.fragmentContainer, new MainFragment(), "mainFragment");
		transaction.commit();
	}

	public void openCreateEvent() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out);
		transaction.replace(R.id.fragmentContainer, new CreateEventFragment(), "createEventFragment");
		transaction.commit();
	}

	public void closeCreateEvent() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.replace(R.id.fragmentContainer, new MainFragment(), "createEventFragment");
		transaction.commit();
	}

	private boolean getLoggedIn() {
		final SharedPreferences prefs = getPrefs();
		return prefs.getBoolean(Constants.KEY_LOGGED_IN, false);
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Logger.d(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Register the device on GCM service.
	 * Send the registration ID to backend and saved it in application storage.
	 * This must be done in background as GCM register() is blocking.
	 */
	private void registerDevice() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					regId = gcm.register(Constants.SENDER_ID);
					Logger.d(TAG, "Device registered. Registration ID: " + regId);

					// Send the registration ID to server.
					//sendRegistrationIdToBackend();

					// Save the registration ID to the application.
					storeRegistrationId();
				} catch (IOException e) {
					Logger.d(TAG, "Unable to register device. ERROR: " + e);
				}
				return null;
			}
		}.execute();
	}

	/**
	 * Send the registration ID to the server and saved it.
	 * Therefore our server can send message to this unique application.
	 */
	private void sendRegistrationIdToBackend() {
		try {
			Bundle data = new Bundle();
			data.putString("registrationId", regId);
			data.putString("action", Constants.ACTION_REGISTER);
			String msgId = Integer.toString(getNextMsgId());
			gcm.send(Constants.SENDER_ID + "@gcm.googleapis.com", msgId, Constants.GCM_DEFAULT_TTL, data);
			Logger.d(TAG, "Registration ID sent.");
		} catch (IOException e) {
			Logger.d(TAG, "IOException while sending registration to backend... " + e);
		}
	}

	/**
	 * Gets the current message ID of this application, if there is one.
	 * If no saved message ID, create a new starting from 0.
	 *
	 * @return message ID.
	 */
	public int getNextMsgId() {
		SharedPreferences prefs = getPrefs();
		int id = prefs.getInt(Constants.KEY_MSG_ID, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(Constants.KEY_MSG_ID, ++id);
		editor.apply();
		return id;
	}

	/**
	 * Save the registration ID that was created into the application storage.
	 */
	private void storeRegistrationId() {
		final SharedPreferences prefs = getPrefs();
		Logger.d(TAG, "Saving registration ID to prefs..");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.KEY_REG_ID, regId);
		editor.putInt(Constants.KEY_APP_VERSION, appVersion);
		editor.apply();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there is one.
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 * registration ID.
	 */
	public String getRegistrationId() {
		final SharedPreferences prefs = getPrefs();
		String registrationId = prefs.getString(Constants.KEY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Logger.d(TAG, "Registration not found.");
			// Check if app was updated; if so, it must clear the registration ID
			// since the existing regID is not guaranteed to work with the new
			// app version.
			appVersion = prefs.getInt(Constants.KEY_APP_VERSION, 1);
			int currentVersion = getAppVersion();
			if (appVersion != currentVersion) {
				Log.i(TAG, "App version changed.");
				return "";
			}
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private int getAppVersion() {
		try {
			PackageInfo packageInfo = getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return The shared preferences in private mode.
	 */
	public SharedPreferences getPrefs() {
		return getSharedPreferences(DateInActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
}
