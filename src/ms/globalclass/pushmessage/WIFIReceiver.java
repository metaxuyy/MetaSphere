package ms.globalclass.pushmessage;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.globalclass.map.MyApp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WIFIReceiver extends BroadcastReceiver {
	private MyApp myapp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		myapp = (MyApp)context.getApplicationContext();
		String action = intent.getAction();
		//监听WIFI状态变化
		if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			System.out.print("网络状态已经改变了");
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected())
			{
				myapp.setNetwork(true);
				
				context.startService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				
//				context.stopService(new Intent(context, NotificationService.class));
//				
//				Intent intents = new Intent();
//		        intents.setClass(context, NotificationService.class);
//		        context.startService(intents);
		        
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
			else
			{
				context.stopService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				myapp.setNetwork(false);
				myapp.setSessionId(null);
				
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
		}  else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			////监听连接状态的变化莫测
			System.out.print("网络状态已经改变了");
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected())
			{
				myapp.setNetwork(true);
				context.startService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				
//				context.stopService(new Intent(context, NotificationService.class));
//				
//				Intent intents = new Intent();
//		        intents.setClass(context, NotificationService.class);
//		        context.startService(intents);
		        
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
			else
			{
				context.stopService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				myapp.setNetwork(false);
				
				myapp.setSessionId(null);
				
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
		}else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			////监听连接状态的变化莫测
			System.out.print("网络状态已经改变了");
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected())
			{
				myapp.setNetwork(true);
				context.startService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				
//				context.stopService(new Intent(context, NotificationService.class));
//				
//				Intent intents = new Intent();
//		        intents.setClass(context, NotificationService.class);
//		        context.startService(intents);
		        
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
			else
			{
				context.stopService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				myapp.setNetwork(false);
				
				myapp.setSessionId(null);
				
				if(HotelMainActivity.instance != null)
					HotelMainActivity.instance.changNetLayout();
			}
		}
//		else if (action.endsWith(NotificationService.ACTION_LOGIN_CLOSE)) {
//			String sid = intent.getExtras().getString("sid");
//			String message = intent.getExtras().getString("message");
//			String title = intent.getExtras().getString("title");
//			
//	        Notification notification = new Notification();  
//	        notification.icon = R.drawable.notification_icon_warning; 
//	        notification.defaults = Notification.DEFAULT_ALL; 
//	        notification.tickerText = title;  
//	        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService("notification"); 
//	        Intent intentnot = new Intent(context,  WIFIReceiver.class);  
//	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentnot, Intent.FLAG_ACTIVITY_CLEAR_TOP);  
//	        notification.setLatestEventInfo(context, title, message,  pendingIntent);  
//	        mNotificationManager.notify(0, notification);  
//	        
//			context.stopService(new Intent(context, NotificationService.class));
//        	if(!sid.equals(""))
//			{
//				Intent startMain = new Intent(Intent.ACTION_MAIN);
//				startMain.addCategory(Intent.CATEGORY_HOME);
//				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(startMain);
//				System.exit(0);
//			}
//		}
	}
}
