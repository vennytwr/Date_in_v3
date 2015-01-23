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
import com.datein.date_in.calendar.CalendarController;
import com.datein.date_in.events.CreateEventController;
import com.datein.date_in.friends.FriendsController;
import com.datein.date_in.login_register.LoginRegisterController;
import com.datein.date_in.log.Logger;
import com.datein.date_in.main.MainController;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

	private static final String TAG = "GcmIntentService";

	private int NOTIFICATION_ID = 1;

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
				Logger.d(TAG, "Send error: " + extras.toString());
			else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
				Logger.d(TAG, "Deleted messages on server: " + extras.toString());
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
					case Constants.ACTION_FRIEND_SEARCH_OK:
						FriendsController.getInstance().onSearchOK(extras);
						break;
					case Constants.ACTION_FRIEND_SEARCH_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_SEARCH_FAIL);
						break;
					case Constants.ACTION_FRIEND_ADD_OK:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ADD_OK);
						break;
					case Constants.ACTION_FRIEND_ADD_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ADD_FAIL);
						break;
					case Constants.ACTION_FRIEND_OK:
						FriendsController.getInstance().onFriendListOK(extras);
						break;
					case Constants.ACTION_FRIEND_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_LIST_FAIL);
						break;
					case Constants.ACTION_FRIEND_ACCEPT_OK:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ACCEPT_OK);
						break;
					case Constants.ACTION_FRIEND_ACCEPT_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_ACCEPT_FAIL);
						break;
					case Constants.ACTION_FRIEND_REJECT_OK:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_REJECT_OK);
						break;
					case Constants.ACTION_FRIEND_REJECT_FAIL:
						FriendsController.getInstance().doChangeState(Constants.STATE_FRIENDS_REJECT_FAIL);
						break;
					case Constants.ACTION_FRIEND_REQUEST:
						String requestFrom = extras.getString("requestFrom");
						sendNotificationRequest(requestFrom + " had sent you a friend request.");
						if(FriendsController.getInstance() != null)
							FriendsController.getInstance().onFriendList();
						break;
					case Constants.ACTION_FRIEND_ACCEPTED:
						String acceptTo = extras.getString("acceptTo");
						sendNotificationAccepted(acceptTo + " had accepted your friend request.");
						if(FriendsController.getInstance() != null)
							FriendsController.getInstance().onFriendList();
						break;
					case Constants.ACTION_EDIT_CALENDAR_SYNC_OK:
						CalendarController.getInstance().doChangeState(Constants.STATE_EDIT_CALENDAR_SYNC_OK);
						break;
					case Constants.ACTION_EDIT_CALENDAR_SYNC_FAIL:
						CalendarController.getInstance().doChangeState(Constants.STATE_EDIT_CALENDAR_SYNC_FAIL);
						break;
					case Constants.ACTION_CALENDAR_OK:
						CalendarController.getInstance().onCalendarOK(extras);
						break;
					case Constants.ACTION_CALENDAR_FAIL:
						CalendarController.getInstance().doChangeState(Constants.STATE_CALENDAR_FAIL);
						break;
					case Constants.ACTION_EVENT_REQUEST_OK:
						CreateEventController.getInstance().onRequestCommonTimeOK(extras);
						break;
					case Constants.ACTION_EVENT_REQUEST_FAIL:
						CalendarController.getInstance().doChangeState(Constants.STATE_EVENTS_REQUEST_COMMON_TIME_FAIL);
						break;
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotificationRequest(String msg) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, DateInActivity.class);
		notificationIntent.setAction(Constants.ACTION_NOTIFICATION_REQUEST);
		notificationIntent.putExtra(Constants.KEY_MESSAGE_TXT, msg);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("You have a new friend request.")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent).setAutoCancel(true).setTicker(msg);
		mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
	}

	private void sendNotificationAccepted(String msg) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, DateInActivity.class);
		notificationIntent.setAction(Constants.ACTION_NOTIFICATION_REQUEST);
		notificationIntent.putExtra(Constants.KEY_MESSAGE_TXT, msg);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Your request have been accepted")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent).setAutoCancel(true).setTicker(msg);
		mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
	}
}
