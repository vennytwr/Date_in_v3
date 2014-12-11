package com.datein.date_in.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.datein.date_in.MainActivity;
import com.datein.date_in.R;
import com.datein.date_in.log.Logger;
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

		Logger.d(TAG, "action: " + intent.getAction());
		Logger.d(TAG, "id: " + intent.getStringExtra("registration_id"));

//		// The getMessageType() intent parameter must be the intent you received
//		// in your BroadcastReceiver.
//		String messageType = gcm.getMessageType(intent);
//
//		if (extras != null && !extras.isEmpty()) { // has effect of
//			// unparcelling Bundle
//			    /*
//	             * Filter messages based on message type. Since it is likely that
//	             * GCM will be extended in the future with new message types, just
//	             * ignore any message types you're not interested in, or that you
//	             * don't recognize.
//	             */
//			switch (messageType) {
//				case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
//					sendNotification("Send error: " + extras.toString());
//					break;
//				case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
//					sendNotification("Deleted messages on server: "
//							+ extras.toString());
//					// If it's a regular GCM message, do some work.
//					break;
//				case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
//					// Post notification of received message.
//					String msg = extras.getString("message");
//					if (TextUtils.isEmpty(msg)) {
//						msg = "empty message";
//					}
//					sendNotification(msg);
//					Logger.d(TAG, "Received: " + extras.toString()
//							+ ", sent: " + msg);
//					break;
//			}
//		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, MainActivity.class);
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
