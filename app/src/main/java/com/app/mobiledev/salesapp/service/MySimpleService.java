package com.app.mobiledev.salesapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.app.mobiledev.salesapp.R;
import com.app.mobiledev.salesapp.main_fragment;


public class MySimpleService extends JobIntentService {
	public static final int JOB_ID = 100;
	public static final int NOTIF_ID = 56;
	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	long timestamp;

	public static void enqueueWork(Context context, Intent work) {
		enqueueWork(context, MySimpleService.class, JOB_ID, work);
	}

	// This describes what will happen when service is triggered
	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		Log.d("DEBUG", "MySimpleService triggered");
		timestamp =  System.currentTimeMillis();
	    // Extract additional values from the bundle
	    String val = intent.getStringExtra("foo");
		// Extract the receiver passed into the service
	    ResultReceiver rec = intent.getParcelableExtra("receiver");
		// Send result to activity
	    sendResultValue(rec, val);
		// Let's also create notification
	    createNotification(val);
	}

	// Send result to activity using ResultReceiver
	private void sendResultValue(ResultReceiver rec, String val) {
		// To send a message to the Activity, create a pass a Bundle
	    Bundle bundle = new Bundle();
	    bundle.putString("resultValue", "My Result Value. You Passed in: " + val + " with timestamp: " + timestamp);
	    // Here we call send passing a resultCode and the bundle of extras
	    rec.send(AppCompatActivity.RESULT_OK, bundle);
	}

	// Construct compatible notification
	private void createNotification(String val) {
		// Construct pending intent to serve as action for notification item
		Intent intent = new Intent(this, main_fragment.class);
		intent.putExtra("message", "Launched via notification with message: " + val + " and timestamp " + timestamp);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		// Create notification
		String longText = "Intent service has a new message with: " + val + " and a timestamp of: " + timestamp;
		Notification noti =
		        new NotificationCompat.Builder(this, DemoApplication.CHANNEL_ID)
		        .setSmallIcon(R.mipmap.ic_logofix)
						.setSound(alarmSound)
		        .setContentTitle("New Result!")
		        .setContentText("Simple Intent service has a new message")
		        .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
		        .setContentIntent(pIntent)
		        .build();
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIF_ID, noti);
	}
}