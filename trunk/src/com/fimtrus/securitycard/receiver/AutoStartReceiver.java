package com.fimtrus.securitycard.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.fimtrus.securitycard.R;
import com.fimtrus.securitycard.activity.SplashActivity;

public class AutoStartReceiver extends BroadcastReceiver {

	public static final int NOTIFY_ID = 3001;
	public static final String FLAG_NOTIFICATION = "flag_notification";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager notiManager;
		notiManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		
		String title = "Security Card";
		Intent intent1 = new Intent(context, SplashActivity.class);
		
		intent1.putExtra(FLAG_NOTIFICATION, true);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification notification = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(title)
		.setContentText("클릭하시면 보안카드 화면이 실행됩니다.")
//		.setTicker("보안카드 실행중....")
		.setAutoCancel(false)
		.setWhen(System.currentTimeMillis())
		.setContentIntent(pendingIntent).build();
		
		notification.flags = Notification.FLAG_NO_CLEAR;
		
		notiManager.notify(NOTIFY_ID, notification);
		
	}

}
