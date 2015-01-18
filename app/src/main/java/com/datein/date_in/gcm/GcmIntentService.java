package com.datein.date_in.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.datein.date_in.Constants;
import com.datein.date_in.DateInActivity;
import com.datein.date_in.R;
import com.datein.date_in.controller.FriendsController;
import com.datein.date_in.controller.LoginRegisterController;
import com.datein.date_in.controller.MainController;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

	private static final String TAG = "GcmIntentService";

	private static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";
	private static final String KEY_MESSAGE_TXT = "KEY_MESSAGE_TXT";

	private static final int NOTIFICATION_ID = 1;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
				sendNotification("Send error: " + extras.toString());
			else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
				sendNotification("Deleted messages on server: " + extras.toString());
				// If it's a regular GCM message, do some work.
			else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String action = intent.getStringExtra("action");
				switch (action) {
					case Constants.ACTION_LOGIN_OK:
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_LOGIN_OK);
						break;
					case Constants.ACTION_LOGIN_FAIL:
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_LOGIN_FAIL);
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_LOGIN);
						break;
					case Constants.ACTION_REGISTER_OK:
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_REGISTER_OK);
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_REGISTER);
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_LOGIN);
						break;
					case Constants.ACTION_REGISTER_EMAIL_TAKEN:
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_EMAIL_TAKEN);
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_REGISTER);
						break;
					case Constants.ACTION_REGISTER_DISPLAY_NAME_TAKEN:
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_DISPLAY_NAME_TAKEN);
						LoginRegisterController.getInstance().doChangeState(Constants.STATE_REGISTER);
						break;
					case Constants.ACTION_SEARCH_OK:
						FriendsController.getInstance().onSearchOK(extras);
						break;
					case Constants.ACTION_SEARCH_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_SEARCH_FAIL, null);
						break;
					case Constants.ACTION_ADD_OK:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ADD_OK, null);
						break;
					case Constants.ACTION_ADD_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ADD_FAIL, null);
						break;
					case Constants.ACTION_LIST_OK:
						FriendsController.getInstance().onFriendListOK(extras);
						break;


				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, DateInActivity.class);
		notificationIntent.setAction(ACTION_NOTIFICATION);
		notificationIntent.putExtra(KEY_MESSAGE_TXT, msg);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_action_forward)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
