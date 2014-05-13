package ms.activitys;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ms.activitys.hotel.ContactActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.map.BaiduMap;
import ms.activitys.more.MoreActivity;
import ms.globalclass.AnimationTabHost;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;

//import org.jivesoftware.smack.util.Base64;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MainTabActivitybake extends TabActivity {

	public AnimationTabHost mth;
	public static MainTabActivitybake instance;
	public static final String TAB_HOME="vipcard";
	public static final String TAB_SAOMIAO="saomiao";
	public static final String TAB_FRIEND="friend";
	public static final String TAB_FIND="find";
	public static final String TAB_WANT="want";
	public static final String TAB_MORE="more";
	public static final String TAB_ADDRESS="address";
	public RadioGroup radioGroup;
	
	private ImageView mTabImg;// 动画图片
	private ImageView mTab1,mTab2,mTab3,mTab4,mTab5;
	private int zero = 16;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;//单个水平动画位移
	private int two;
	private int three;
	private int four;
	private MyApp myapp;
	private LocationClient mLocClient;
	public MyLocationListenner myListener;
	private TextView numbertxt;
	private MediaPlayer mMediaPlayer;
	private Vibrator mVibrator01;
	private Timer mTimer;
	private static DBHelperMessage db;
	private Douban api;
	private static SharedPreferences  share;
	private Notification mNotification;  
    public NotificationManager mNotificationManager;  
    public static int NOTIFICATION_ID = 0x0001;  
    public static FileUtils fileUtil = new FileUtils();
    private boolean flag;
//    private Intent service;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_page);
        
        instance = this;
        
        myapp = (MyApp)this.getApplicationContext();
        share = getSharedPreferences("perference", MODE_PRIVATE);
        api = new Douban(share,myapp);
        
        mth = (AnimationTabHost)this.getTabHost();
        mth.setup();
        mth.setOpenAnimation(true);
        
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        if(StreamTool.isNetworkVailable(this))
		{
        	myListener = new MyLocationListenner(myapp);
        	System.out.println("百度地图定位注册");
    		mLocClient = new LocationClient(this);
    		mLocClient.registerLocationListener(myListener);
    		LocationClientOption option = new LocationClientOption();
    		option.setOpenGps(true);// 打开gps
    		option.setCoorType("bd09ll"); // 设置坐标类型
    		option.setScanSpan(5000);
    		mLocClient.setLocOption(option);
    		mLocClient.start();
		}
        
        fileUtil.createSDDir();
        
        Intent service = new Intent(MainTabActivitybake.this, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startService(service);
//        Intent serviceintent = new Intent(this,MyService.class);
//        bindService(serviceintent, conn, Context.BIND_AUTO_CREATE);
        
        try{
        	db = new DBHelperMessage(this, myapp);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        
        numbertxt = (TextView)findViewById(R.id.new_number);
        
        if(mMediaPlayer == null){
			mMediaPlayer = MediaPlayer.create(this, R.raw.phonering);
//			try {
//				mMediaPlayer.prepare();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
        
        TabSpec ts1=mth.newTabSpec(TAB_HOME).setIndicator(TAB_HOME);
        ts1.setContent(new Intent(MainTabActivitybake.this,HotelMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        mth.addTab(ts1);
        
        TabSpec ts2=mth.newTabSpec(TAB_FIND).setIndicator(TAB_FIND);
//        ts2.setContent(new Intent(MainTabActivity.this,MapPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        ts2.setContent(new Intent(MainTabActivitybake.this,BaiduMap.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        mth.addTab(ts2);
        
//        TabSpec ts3=mth.newTabSpec(TAB_SAOMIAO).setIndicator(TAB_SAOMIAO);
//        ts3.setContent(new Intent(MainTabActivity.this,CaptureActivity.class));
//        mth.addTab(ts3);
        
        TabSpec ts5=mth.newTabSpec(TAB_ADDRESS).setIndicator(TAB_ADDRESS);
	    ts5.setContent(new Intent(MainTabActivitybake.this,ContactActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    mth.addTab(ts5);
        
//        TabSpec ts3=mth.newTabSpec(TAB_FRIEND).setIndicator(TAB_FRIEND);
//	    ts3.setContent(new Intent(MainTabActivity.this,SearchFriendActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//	    mth.addTab(ts3);
	    
//        TabSpec ts4=mth.newTabSpec(TAB_MORE).setIndicator(TAB_MORE);
//        ts4.setContent(new Intent(MainTabActivity.this,CaptureActivity.class));
//        mth.addTab(ts4);
        
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("tabindex"))
		{
        	String code = this.getIntent().getExtras().getString("code");
        	int tabindex = this.getIntent().getExtras().getInt("tabindex");
        	
        	Intent intent = new Intent();
    	    intent.setClass( this,MoreActivity.class);
    	    Bundle bundle = new Bundle();
    	    bundle.putString("code", code);
    		intent.putExtras(bundle);
    		
    		TabSpec ts4=mth.newTabSpec(TAB_MORE).setIndicator(TAB_MORE);
            ts4.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mth.addTab(ts4);
            
            mth.setCurrentTab(tabindex);
            
//            RadioButton rb1 = (RadioButton)findViewById(R.id.radio_button4);
//        	rb1.setChecked(true);
		}
        else
        {
        	TabSpec ts4=mth.newTabSpec(TAB_MORE).setIndicator(TAB_MORE);
            ts4.setContent(new Intent(MainTabActivitybake.this,MoreActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mth.addTab(ts4);
            
        	mth.setCurrentTab(0);
        	
//        	RadioButton rb1 = (RadioButton)findViewById(R.id.radio_button0);
//        	rb1.setChecked(true);
        }
        
        mTab1 = (ImageView) findViewById(R.id.img_weixin);
        mTab2 = (ImageView) findViewById(R.id.img_address);
//        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_settings);
        mTab5 = (ImageView) findViewById(R.id.img_contact);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout2);
//        LinearLayout layout3 = (LinearLayout)findViewById(R.id.layout3);
        LinearLayout layout4 = (LinearLayout)findViewById(R.id.layout4);
        LinearLayout layout5 = (LinearLayout)findViewById(R.id.layout5);
        layout1.setOnClickListener(new MyOnClickListener(0));
        layout2.setOnClickListener(new MyOnClickListener(1));
        layout5.setOnClickListener(new MyOnClickListener(2));
//        layout3.setOnClickListener(new MyOnClickListener(3));
        layout4.setOnClickListener(new MyOnClickListener(3));
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab5.setOnClickListener(new MyOnClickListener(2));
//        mTab3.setOnClickListener(new MyOnClickListener(3));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        myapp.setCurrentWidth(displayWidth);
        int displayHeight = currDisplay.getHeight();
        
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
                //获得手机的宽度和高度像素单位为px  
//        String strPM = "手机屏幕分辨率为:" + dm.widthPixels+"* "+dm.heightPixels;
        myapp.setScreenWidth(dm.widthPixels);
        myapp.setScreenHeight(dm.heightPixels);
//        one = displayWidth/5; //设置水平动画平移大小
//        two = one*2;
//        three = one*3;
//        four = one*4;
        int width = displayWidth/4; //设置水平动画平移大小
        int a = 0;
        if(width > 122)
        {
        	a = (width - 120) / 2;
        	Animation animation = new TranslateAnimation(a, 0, 0, 0);
        	animation.setFillAfter(true);// True:图片停在动画结束位置
   			animation.setDuration(150);
   			mTabImg.startAnimation(animation);
   			zero = a;
        }
        one = width;
        two = width*2;
        three = width*3;
//        four = one*4;
        
//        this.radioGroup=(RadioGroup)findViewById(R.id.main_radio);
//        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				// TODO Auto-generated method stub
//				
//				switch(checkedId){
//				case R.id.radio_button0:
//					mth.setCurrentTabByTag(TAB_HOME);
//					break;
//				case R.id.radio_button1:
//					mth.setCurrentTabByTag(TAB_FIND);
//					break;
//				case R.id.radio_button2:
//					mth.setCurrentTabByTag(TAB_SAOMIAO);
//					break;
//				case R.id.radio_button3:
//					Log.d("select ID", "===={"+checkedId);
//					break;
//				case R.id.radio_button4:
//					mth.setCurrentTabByTag(TAB_MORE);
//					break;
//				}
//			}
//		});
        
//        myAnimation_Translate= AnimationUtils.loadAnimation(MainTabActivity.this,R.anim.hyperspace_in);
//        myAnimation_Translate= AnimationUtils.loadAnimation(MainTabActivity.this,R.anim.hyperspace_out);
//        radioGroup.setAnimation(myAnimation_Translate);
        
//        RadioButton rb1 = (RadioButton)findViewById(R.id.radio_button0);
//        rb1.setChecked(true);
        
      //标签切换事件处理，setOnTabChangedListener
        mth.setOnTabChangedListener(new OnTabChangeListener()
        {
               @Override
               public void onTabChanged(String tabId)
               {
            	   Animation animation = null;
            	   int arg0 = 0;
            	   if(tabId.equals(TAB_HOME))
            	   {
            		   arg0 = 0;
						mTab1.setImageDrawable(getResources().getDrawable(
								R.drawable.tab_weixin_pressed));
						if (currIndex == 1) {
							animation = new TranslateAnimation(one, 0, 0, 0);
							mTab2.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_address_normal));
						} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, 0, 0, 0);
							mTab5.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_contact_normal));
						} 
//						else if (currIndex == 3) {
//							animation = new TranslateAnimation(three, 0, 0, 0);
//							mTab3.setImageDrawable(getResources().getDrawable(
//									R.drawable.tab_find_frd_normal));
//						} 
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three, 0, 0, 0);
							mTab4.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_settings_normal));
						}
//            		   /**
//            		    * 设置为竖屏
//            		    */
//            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            		   } 
            	   }
            	   else if(tabId.equals(TAB_FIND))
            	   {
            		   arg0 = 1;
            		    mTab2.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_address_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, one, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_weixin_normal));
	       				} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, one, 0, 0);
							mTab5.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_contact_normal));
						} 
//	       				else if (currIndex == 3) {
//	       					animation = new TranslateAnimation(three, one, 0, 0);
//	       					mTab3.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_find_frd_normal));
//	       				}
	       				else if (currIndex == 3) {
	       					animation = new TranslateAnimation(three, one, 0, 0);
	       					mTab4.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_settings_normal));
	       				}
//            		   /**
//            		    * 设置为竖屏
//            		    */
//            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            		   } 
            	   }
            	   else if(tabId.equals(TAB_ADDRESS))
            	   {
            		   arg0 = 2;
            		    mTab5.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_contact_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, two, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_weixin_normal));
	       				} else if (currIndex == 1) {
	       					animation = new TranslateAnimation(one, two, 0, 0);
	       					mTab2.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_address));
	       				} 
//	       				else if (currIndex == 3) {
//	       					animation = new TranslateAnimation(three, two, 0, 0);
//	       					mTab3.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_find_frd_normal));
//	       				} 
	       				else if (currIndex == 3) {
	       					animation = new TranslateAnimation(three, two, 0, 0);
	       					mTab4.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_settings_normal));
	       				}
//            		   /**
//            		    * 设置为竖屏
//            		    */
//            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            		   } 
            	   }
//            	   else if(tabId.equals(TAB_FRIEND))
//            	   {
//            		   arg0 = 3;
//            		    mTab3.setImageDrawable(getResources().getDrawable(
//       						R.drawable.tab_find_frd_pressed));
//	       				if (currIndex == 0) {
//	       					animation = new TranslateAnimation(zero, three, 0, 0);
//	       					mTab1.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_weixin_normal));
//	       				} else if (currIndex == 1) {
//	       					animation = new TranslateAnimation(one, three, 0, 0);
//	       					mTab2.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_address_normal));
//	       				} else if (currIndex == 2) {
//							animation = new TranslateAnimation(two, three, 0, 0);
//							mTab5.setImageDrawable(getResources().getDrawable(
//									R.drawable.tab_contact_normal));
//						} else if (currIndex == 4) {
//	       					animation = new TranslateAnimation(four, three, 0, 0);
//	       					mTab4.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_settings_normal));
//	       				}
////            		   /**
////            		    * 设置为竖屏
////            		    */
////            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
////            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////            		   } 
//            	   }
            	   else if(tabId.equals(TAB_MORE))
            	   {
            		   arg0 = 3;
            		    mTab4.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_settings_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, three, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_weixin_normal));
	       				} else if (currIndex == 1) {
	       					animation = new TranslateAnimation(one, three, 0, 0);
	       					mTab2.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_address_normal));
	       				} else if (currIndex == 2) {
	       					animation = new TranslateAnimation(two, three, 0, 0);
	       					mTab5.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_contact_normal));
	       				} 
//	       				else if (currIndex == 3) {
//	       					animation = new TranslateAnimation(three, four, 0, 0);
//	       					mTab3.setImageDrawable(getResources().getDrawable(
//	       							R.drawable.tab_find_frd_normal));
//	       				}
//            		   /**
//            		    * 设置为竖屏
//            		    */
//            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            		   } 
            	   }
//            	   else if(tabId.equals(TAB_WANT))
//            	   {
////            		   /**
////            		    * 设置为竖屏
////            		    */
////            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
////            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////            		   } 
//            	   }
//            	   else if(tabId.equals(TAB_SAOMIAO))
//            	   {
////            		   /**
////            		    * 设置为横屏
////            		    */
////            		   if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
////            			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
////            		   } 
//            	   }
            	   
            	    currIndex = arg0;
	       			animation.setFillAfter(true);// True:图片停在动画结束位置
	       			animation.setDuration(150);
	       			mTabImg.startAnimation(animation);
	       			System.gc();
               }            
        });
    }
	
	 /**
		 * 头标点击监听
		 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mth.setCurrentTab(index);
		}
	};
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		private MyApp myapp;
		
		public MyLocationListenner(MyApp myapp)
		{
			this.myapp = myapp;
		}
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			myapp.setLatitude((int)(location.getLatitude() * 1E6));
			myapp.setLongitude((int)(location.getLongitude() * 1E6));
			myapp.setLat(String.valueOf(location.getLatitude()));
			myapp.setLng(String.valueOf(location.getLongitude()));
//			System.out.println("百度定位 lat==" + myapp.getLat());
//			System.out.println("百度定位 lng==" + myapp.getLng());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	
	public static void testPost(String x, String y) throws IOException {  
        URL url = new URL(  
                "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + x  
                        + "&y=" + y);  
        URLConnection connection = url.openConnection();  
        /** 
         * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。 
         * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做： 
         */  
        connection.setDoOutput(true);  
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");  
        // remember to clean up  
        out.flush();  
        out.close();  
        // 一旦发送成功，用以下方法就可以得到服务器的回应：  
        String sCurrentLine;  
        String sTotalString;  
        sCurrentLine = "";  
        sTotalString = "";  
        InputStream l_urlStream;  
        l_urlStream = connection.getInputStream();  
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(  
                l_urlStream));  
        while ((sCurrentLine = l_reader.readLine()) != null) {  
            if (!sCurrentLine.equals(""))  
                sTotalString += sCurrentLine;  
        }  
        System.out.println(sTotalString);  
        sTotalString = sTotalString.substring(1, sTotalString.length()-1);  
        System.out.println(sTotalString);  
        String[] results = sTotalString.split("\\,");  
        if (results.length == 3){  
            if (results[0].split("\\:")[1].equals("0")){  
                String mapX = results[1].split("\\:")[1];  
                String mapY = results[2].split("\\:")[1];  
                mapX = mapX.substring(1, mapX.length()-1);  
                mapY = mapY.substring(1, mapY.length()-1);  
                mapX = new String(Base64.decodeBase64(mapX.getBytes())); 
                mapY = new String(Base64.decodeBase64(mapY.getBytes()));  
//                mapY = new String(Base64.decode(mapY));  
                System.out.println(mapX);  
                System.out.println(mapY);  
            }  
        }  
          
    } 
	
	public void onMinimizeActivity()
	{
		moveTaskToBack(true);
		myapp.setBreakStrat(true);
	}
	
	public void loadLeftActivity(Intent intent)
	{
		startActivity(intent);//开始界面的跳转函数
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	 /** 
     * 重新开始 
     */  
    @Override  
    protected void onRestart() {  
        Log.i("TAG-onRestart", "onRestart()------------yin");  
        super.onRestart();  
    }  
  
    /** 
     * 启动 
     */  
    @Override  
    protected void onStart() {  
        Log.i("TAG-onStart", "onStart()------------yin");  
  
        super.onStart();  
    }  
  
    /** 
     * 重新启动 
     */  
    @Override  
    protected void onResume() {  
        Log.i("TAG-onResume", "onResume()------------yin");
        super.onResume();
        mNotificationManager.cancelAll();
//        startBroadcastReceiver();
        
        IntentFilter filter = new IntentFilter();

		filter.addAction("NEW_MESSAGE_LIST_HUA_MEIDA");
		filter.addAction("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA");
		filter.addAction("unread_message_pus_hua_meida");
		
		registerReceiver(mBroadcastReceiver, filter);
		
        if(myapp.isBreakStrat())
        {
        	mth.setCurrentTab(0);
        	myapp.setBreakStrat(false);
        }
    }  
	
	/** 
     * 暂停 
     */  
    @Override  
    protected void onPause() {  
        Log.i("TAG-onPause", "onPause()------------yin");  
//        stopBroadcastReceiver();
        super.onPause();  
    }  
  
    /** 
     * 停止 
     */  
    @Override  
    protected void onStop() {  
        Log.i("TAG-onStop", "onStop()------------yin");  
        unregisterReceiver(mBroadcastReceiver);//关闭广播监听ddd
        super.onStop();  
    }  
	
    public void setNewNumberValue(int number)
    {
    	if(number > 0)
    	{
    		numbertxt.setVisibility(View.VISIBLE);
    		numbertxt.setText(String.valueOf(number));
    	}
    	else
    	{
    		numbertxt.setVisibility(View.GONE);
    	}
    }
    
	public void startBroadcastReceiver() {
		
	}
	
	public void stopBroadcastReceiver()
	{
//		unregisterReceiver(mBroadcastReceiver);//关闭广播监听
	}
	
	@Override
    protected void onDestroy(){
        super.onDestroy();
//        if(flag == true){
//            unbindService(conn);
//            flag = false;
//        }
    }
	
//	private ServiceConnection conn = new ServiceConnection() {
//        
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            // TODO Auto-generated method stub
//        	
//        }
//        
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // TODO Auto-generated method stub
//            MyBinder binder = (MyBinder)service;
//            MyService bindService = binder.getService();
////            bindService.MyMethod();
//            flag = true;
//        }
//    };
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA"))
			{
				try{
//					String datastr = intent.getStringExtra("datastr");
//					JSONArray jArry = new JSONArray(datastr);
//					List<Map<String,Object>> newmessagelist = MessageListActivity.instance.getMessageDetialData(jArry);
//					Map<String,Object> map = newmessagelist.get(0);
//					String sname = (String)map.get("sname");
//					shockAndRingtones(sname);
//					saveMessageLocal(newmessagelist);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(action.equals("unread_message_pus_hua_meida"))
			{
				try{
//					String datastr = intent.getStringExtra("datastr");
//					JSONArray jArry = new JSONArray(datastr);
//					List<Map<String,Object>> newmessagelist = HotelMainActivity.instance.getMessageDetialData(jArry);
//					shockAndRingtones();
//					saveMessageLocal(newmessagelist);
					getMyUnreadMessageListData();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	};
	
	public void getMyUnreadMessageListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					List<Map<String,Object>> list = null;
					
					JSONObject jobj = api.getMyUnreadMessageListData(myapp.getPfprofileId());
					if(jobj != null && jobj.has("data"))
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
//						list = HotelMainActivity.instance.getMessageDetialData(jArr);
					}
					
					msg.obj = list;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void saveMessageLocal(final List<Map<String,Object>> dlist)
	{
		try{
			new Thread(){
				public void run(){
					Message msg = new Message();
					msg.what = 0;
					
					db.openDB();
					db.saveMessageData(dlist);
					db.closeDB();
					
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				makeText("数据保存本地成功");
				HotelMainActivity.instance.loadeListItemData();
				break;
			case 1:
				List<Map<String,Object>> newmessagelist = (List<Map<String,Object>>)msg.obj;
				if(newmessagelist != null && newmessagelist.size() > 0)
				{
//					shockAndRingtones();
					saveMessageLocal(newmessagelist);
				}
				break;
			}
		}
	};
	
	// 新信息来时铃声加震动
	public void shockAndRingtones(String sname) {
		try {
//			mMediaPlayer.start();
			// if(mMediaPlayer.isPlaying())
			// {
			// mMediaPlayer.release();
			// }
			// final AudioManager audioManager = (AudioManager)
			// getSystemService(Context.AUDIO_SERVICE);
			// if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
			// {
			// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			// mMediaPlayer.setLooping(true);
			// mMediaPlayer.prepare();
			// mMediaPlayer.start();
			// }
			String title = "您有新消息。。。";
			String msg = sname+"给你发来了新消息！";
			mNotification = new Notification(R.drawable.message_icon,title,System.currentTimeMillis());  
	           //将使用默认的声音来提醒用户  
	        mNotification.defaults = Notification.DEFAULT_ALL;  
//	        mNotification.number = nonumber;
	        
	     // 点击通知时转移内容也就是从哪个Activity跳到哪个Activity 这里是从Activity01跳到Activity02
    		Intent m_Intent = new Intent(this, MainTabActivity.class);
    		m_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
	        PendingIntent m_PendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
    				m_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
    		
            mNotification.setLatestEventInfo(this, title, msg, m_PendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification); 

			mVibrator01 = (Vibrator) getApplication().getSystemService(
					Service.VIBRATOR_SERVICE);
			mVibrator01.vibrate(new long[] { 100, 100, 100, 1000, 100, 100,
					100, 1000, 100, 100, 100, 1000, 100, 100, 100, 1000, 100,
					100, 100, 1000, 100, 100, 100, 1000 }, -1);
			/* 用Toast显示震动启动 */

			/* 用Toast显示震动启动 */
			// Toast.makeText(this, "震动启动", Toast.LENGTH_SHORT).show();

			mTimer = new Timer(true);
			TimerTask mTimerTask = new TimerTask() {
				public void run() {
					mVibrator01.cancel();
					mTimer.cancel();
					// mMediaPlayer.stop();
				}
			};

//			mTimer.schedule(mTimerTask, 5000, 5000);
			mTimer.schedule(mTimerTask, 1000, 1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			Intent intent = new Intent();
//        	intent.setClass(MainTabActivity.this,Exit.class);
//        	startActivity(intent);
//			new AlertDialog.Builder(this).setTitle("提示")
//			.setMessage("确定退出?").setIcon(R.drawable.error2)
//			.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
////							setResult(RESULT_OK);// 确定按钮事件
////							android.os.Process.killProcess(android.os.Process.myPid());
////							finish();
//							Intent startMain = new Intent(Intent.ACTION_MAIN);
//					         startMain.addCategory(Intent.CATEGORY_HOME);
//					         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					         startActivity(startMain);
//					         System.exit(0);
//						}
//					}).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// 取消按钮事件
//						}
//					}).show();
//			return false;
//		}
//		return false;
//	}
}
