package ms.activitys;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ms.activitys.hotel.ContactActivity;
import ms.activitys.hotel.DowloadVersionDialog;
import ms.activitys.hotel.Exit;
import ms.activitys.hotel.FriendsContactActivity;
import ms.activitys.hotel.HotelActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.activitys.hotel.HotelServiceActivity;
import ms.activitys.hotel.HotelWinActivity;
import ms.activitys.hotel.InformationFriendActivity;
import ms.activitys.hotel.LoginDialog;
import ms.activitys.hotel.ManagerPublishBoardActivity;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.hotel.MomentsActivity;
import ms.activitys.more.MoreActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.UpdateManager;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class MainTabActivity extends TabActivity{

	public TabHost mth;
	public static MainTabActivity instance;
	public static final String TAB_HOME="vipcard";
	public static final String TAB_SAOMIAO="saomiao";
	public static final String TAB_FRIEND="friend";
	public static final String TAB_FIND="find";
	public static final String TAB_WANT="want";
	public static final String TAB_SETTING="setting";
	public static final String TAB_SERVICE="service";
	public static final String TAB_MESSAGE="message";
	public static final String TAB_CONTENT="content";
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
	private TextView serviceNumbertxt;
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
    public TextView new_yanzhen_number;
    public ImageView new_moments_img;
    public TextView new_moments_txt;
    private static boolean start = false;
    private BMapManager mBMapManager = null;
    public static final String BAIDU_MAP_KEY = "BBFDD59585B8518AAEB2AE3E0799F6BDF5D303A1";
    private String userimg;
    private Timer timer = new Timer();
    public Timer timer2 = null;
    private String user;
    private String paw;
    public boolean storeIsloade  = false;
    public Map<String,Object> newGonGaomap;
    private TextView service_lable_txt;
    
    private UpdateManager mUpdateManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        // 注意：请在调用setContentView前初始化BMapManager对象，否则会报错  
        mBMapManager = new BMapManager(this.getApplicationContext());  
        mBMapManager.init(BAIDU_MAP_KEY, new MKGeneralListener() {  
 
            @Override 
            public void onGetNetworkState(int iError) {  
                if (iError == MKEvent.ERROR_NETWORK_CONNECT) {  
                    Toast.makeText(MainTabActivity.this.getApplicationContext(),  
                            "您的网络出错啦！",   
                            Toast.LENGTH_LONG).show();  
                }  
            }  
 
            @Override 
            public void onGetPermissionState(int iError) {  
                if (iError == MKEvent.ERROR_PERMISSION_DENIED) {  
                    // 授权Key错误：  
                    Toast.makeText(MainTabActivity.this.getApplicationContext(),   
                            "请在 DemoApplication.java文件输入正确的授权Key！",   
                            Toast.LENGTH_LONG).show();  
                    myapp.setM_bKeyRight(false);
                }  
            }  
        }); 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
        setContentView(R.layout.main_hotel_tab_page);
        
        instance = this;
        
        myapp = (MyApp)this.getApplicationContext();
        share = getSharedPreferences("perference", MODE_PRIVATE);
        api = new Douban(share,myapp);
        
        myapp.setmBMapManager(mBMapManager);
        
        try{
        	db = new DBHelperMessage(this, myapp);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        
        mth = this.getTabHost();
        mth.setup();
        
        String ip = this.share.getString("ipadrees", "121.199.8.186");
		myapp.setHost(ip);
		
		user = share.getString("user", "");
		paw = share.getString("pwa", "");
        
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
        
        if(!fileUtil.isFileExist2(myapp.getAppstoreid()))
    	{
    		fileUtil.createUserFile(myapp.getAppstoreid());
    	}
        
        if(!user.equals(""))
        {
	        getMyCardListData();
	        loadTrim();
	        //获取朋友圈
	        getMomentsDataAll();
	        //检查是否有新版本
	        checkVersion();
        }
        
        if(!user.equals(""))
        {
	        Intent service = new Intent(this, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startService(service);
        }
        
       
        loadUserImage();
        numbertxt = (TextView)findViewById(R.id.new_number);
        serviceNumbertxt = (TextView)findViewById(R.id.new_service_number);
        new_yanzhen_number = (TextView)findViewById(R.id.new_yanzhen_number);
        new_moments_img = (ImageView)findViewById(R.id.new_moments_img);
        new_moments_txt = (TextView)findViewById(R.id.new_moments_txt);
        service_lable_txt = (TextView)findViewById(R.id.service_lable_txt);
        
        if(mMediaPlayer == null){
			mMediaPlayer = MediaPlayer.create(this, R.raw.phonering);
		}
        
//        try{
//        TabSpec ts1=mth.newTabSpec(TAB_HOME).setIndicator(TAB_HOME);
//        if(myapp.getIsServer())
//        	ts1.setContent(new Intent(this,ManagerPublishBoardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        else
////        	ts1.setContent(new Intent(this,HotelActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        	//ts1.setContent(new Intent(this,HotelWinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        	ts1.setContent(new Intent(this,MenuLoadActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        	mth.addTab(ts1);
//        }catch(Exception ex){
//        	ex.printStackTrace();
//        }
        
		TabSpec ts1 = mth.newTabSpec(TAB_HOME).setIndicator(TAB_HOME);
		ts1.setContent(new Intent(this, MenuLoadActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		ts1.setContent(new Intent(this,HotelWinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		mth.addTab(ts1);
        
		try{
			TabSpec ts2 = mth.newTabSpec(TAB_SERVICE).setIndicator(TAB_SERVICE);
			if (myapp.getIsServer()) {
				ts2.setContent(new Intent(this,ManagerPublishBoardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				service_lable_txt.setText(getString(R.string.hotel_label_191));
			} else {
				// ts2.setContent(new
				// Intent(MainTabActivity.this,MapPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				ts2.setContent(new Intent(this, HotelServiceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				service_lable_txt.setText(getString(R.string.hotel_label_121));
			}
			mth.addTab(ts2);
		}catch(Exception ex){
        	ex.printStackTrace();
        }
        
        TabSpec ts3=mth.newTabSpec(TAB_MESSAGE).setIndicator(TAB_MESSAGE);
        ts3.setContent(new Intent(MainTabActivity.this,HotelMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        mth.addTab(ts3);
        
        TabSpec ts4=mth.newTabSpec(TAB_CONTENT).setIndicator(TAB_CONTENT);
	    ts4.setContent(new Intent(this,ContactActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    mth.addTab(ts4);
        
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("tabindex"))
		{
        	String code = this.getIntent().getExtras().getString("code");
        	int tabindex = this.getIntent().getExtras().getInt("tabindex");
        	
        	Intent intent = new Intent();
    	    intent.setClass( this,MoreActivity.class);
    	    Bundle bundle = new Bundle();
    	    bundle.putString("code", code);
    		intent.putExtras(bundle);
    		
    		TabSpec ts5=mth.newTabSpec(TAB_SETTING).setIndicator(TAB_SETTING);
            ts5.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mth.addTab(ts5);
            
            mth.setCurrentTab(tabindex);
            
		}
        else
        {
        	TabSpec ts5=mth.newTabSpec(TAB_SETTING).setIndicator(TAB_SETTING);
            ts5.setContent(new Intent(this,MoreActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mth.addTab(ts5);
            
        	mth.setCurrentTab(0);
        }
        
        mTab1 = (ImageView) findViewById(R.id.img_home);
        mTab2 = (ImageView) findViewById(R.id.img_service);
        mTab3 = (ImageView) findViewById(R.id.img_weixin);
        mTab4 = (ImageView) findViewById(R.id.img_contact);
        mTab5 = (ImageView) findViewById(R.id.img_settings);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout6);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout7);
        LinearLayout layout3 = (LinearLayout)findViewById(R.id.layout1);
        LinearLayout layout4 = (LinearLayout)findViewById(R.id.layout5);
        LinearLayout layout5 = (LinearLayout)findViewById(R.id.layout4);
        layout1.setOnClickListener(new MyOnClickListener(0));
        layout2.setOnClickListener(new MyOnClickListener(1));
        layout3.setOnClickListener(new MyOnClickListener(2));
        layout4.setOnClickListener(new MyOnClickListener(3));
        layout5.setOnClickListener(new MyOnClickListener(4));
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        mTab5.setOnClickListener(new MyOnClickListener(4));
        Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        myapp.setCurrentWidth(displayWidth);
        int displayHeight = currDisplay.getHeight();
        
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
             
        myapp.setScreenWidth(dm.widthPixels);
        myapp.setScreenHeight(dm.heightPixels);
        
        int width = displayWidth/5; //设置水平动画平移大小
//        if(myapp.getIsServer())
//        {
//        	layout2.setVisibility(View.GONE);
//        	width = displayWidth/4;
//        	
//        	int a = 0;
//            if(width > 122)
//            {
//            	a = (width - 120) / 2;
//            	Animation animation = new TranslateAnimation(a, 0, 0, 0);
//            	animation.setFillAfter(true);// True:图片停在动画结束位置
//       			animation.setDuration(150);
//       			mTabImg.startAnimation(animation);
//       			zero = a;
//            }
//            one = width;
//            two = width;
//            three = width*2;
//            four = width*3;
//        }
//        else
//        {
//        	int a = 0;
//            if(width > 122)
//            {
//            	a = (width - 120) / 2;
//            	Animation animation = new TranslateAnimation(a, 0, 0, 0);
//            	animation.setFillAfter(true);// True:图片停在动画结束位置
//       			animation.setDuration(150);
//       			mTabImg.startAnimation(animation);
//       			zero = a;
//            }
//            one = width;
//            two = width*2;
//            three = width*3;
//            four = width*4;
//        }
        
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
        four = width*4;
        
        
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
								R.drawable.tab_home_pressed));
						if (currIndex == 1) {
							animation = new TranslateAnimation(one, 0, 0, 0);
							mTab2.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_service_normal));
						} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, 0, 0, 0);
							mTab3.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_weixin_normal));
						} 
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three, 0, 0, 0);
							mTab4.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_contact_normal));
						}
						else if (currIndex == 4) {
							animation = new TranslateAnimation(four, 0, 0, 0);
							mTab5.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_settings_normal));
						}
            	   }
            	   else if(tabId.equals(TAB_SERVICE))
            	   {
            		   arg0 = 1;
            		    mTab2.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_service_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, one, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_home_normal));
	       				} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, one, 0, 0);
							mTab3.setImageDrawable(getResources().getDrawable(
									R.drawable.tab_weixin_normal));
						} 
	       				else if (currIndex == 3) {
	       					animation = new TranslateAnimation(three, one, 0, 0);
	       					mTab4.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_contact_normal));
	       				}
	       				else if (currIndex == 4) {
	       					animation = new TranslateAnimation(four, one, 0, 0);
	       					mTab5.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_settings_normal));
	       				}
            	   }
            	   else if(tabId.equals(TAB_MESSAGE))
            	   {
            		   arg0 = 2;
            		    mTab3.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_weixin_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, two, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_home_normal));
	       				} else if (currIndex == 1) {
	       					animation = new TranslateAnimation(one, two, 0, 0);
	       					mTab2.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_service_normal));
	       				} 
	       				else if (currIndex == 3) {
	       					animation = new TranslateAnimation(three, two, 0, 0);
	       					mTab4.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_contact_normal));
	       				}
	       				else if (currIndex == 4) {
	       					animation = new TranslateAnimation(four, two, 0, 0);
	       					mTab5.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_settings_normal));
	       				}
            	   }
            	   else if(tabId.equals(TAB_CONTENT))
            	   {
            		   arg0 = 3;
            		    mTab4.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_contact_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, three, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_home_normal));
	       				} else if (currIndex == 1) {
	       					animation = new TranslateAnimation(one, three, 0, 0);
	       					mTab2.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_service_normal));
	       				} else if (currIndex == 2) {
	       					animation = new TranslateAnimation(two, three, 0, 0);
	       					mTab3.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_weixin_normal));
	       				} else if (currIndex == 4) {
	       					animation = new TranslateAnimation(four, three, 0, 0);
	       					mTab5.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_settings_normal));
	       				} 
            	   }
            	   else if(tabId.equals(TAB_SETTING))
            	   {
            		   arg0 = 4;
            		    mTab5.setImageDrawable(getResources().getDrawable(
       						R.drawable.tab_settings_pressed));
	       				if (currIndex == 0) {
	       					animation = new TranslateAnimation(zero, four, 0, 0);
	       					mTab1.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_home_normal));
	       				} else if (currIndex == 1) {
	       					animation = new TranslateAnimation(one, four, 0, 0);
	       					mTab2.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_service_normal));
	       				} else if (currIndex == 2) {
	       					animation = new TranslateAnimation(two, four, 0, 0);
	       					mTab3.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_weixin_normal));
	       				} else if (currIndex == 3) {
	       					animation = new TranslateAnimation(three, four, 0, 0);
	       					mTab4.setImageDrawable(getResources().getDrawable(
	       							R.drawable.tab_contact_normal));
	       				} 
            	   }
            	   
            	    currIndex = arg0;
	       			animation.setFillAfter(true);// True:图片停在动画结束位置
	       			animation.setDuration(150);
	       			mTabImg.startAnimation(animation);
	       			mth.setCurrentTab(currIndex);
	       			System.gc();
               }            
        });
    }
    
    /************获取朋友圈列表********/
    public void getMomentsDataAll() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> listsNoHas = new ArrayList<Map<String, Object>>();
					JSONObject jobj = api.getMyFriendsMoments("0", "0", "");
					if (jobj != null && jobj.has("data")) {
						// 解析数据格式
						JSONArray jArr = (JSONArray) jobj.get("data");
						lists = myapp.getMyFriendMomentsList(jArr);

						db.openDB();
						// 判断本地是否已保存
						for (int i = 0; i < lists.size(); i++) {
							Map<String, Object> tempMap = lists.get(i);
							String pkid = (String) tempMap.get("pkid");
							boolean isHas = db.isMomentHas(pkid);
							if (!isHas) {
								listsNoHas.add(tempMap);
							}
						}

						// 未保存的保存到本地
						db.saveMomentsAll(listsNoHas);
						lists = db.getMomentsAllData(0, "");
						db.closeDB();
					}
					
					msg.obj = listsNoHas;
				} catch (Exception ex) {
					// Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handlerForGetMoments.sendMessage(msg);
			}
		}.start();
	}
    
    private Handler handlerForGetMoments = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> list3 = (List<Map<String, Object>>) msg.obj;
				if (list3 != null && list3.size()>0) {
					//说明有新消息，标识“我”有新消息，标识“朋友圈”有新消息
					System.out.println("有新消息");
				}
				break;
			}
		}
	};
    
    
    /************检查版本************/
    private void checkVersion() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					Map<String,Object> map = getVersion(MainTabActivity.this);
					String curVersion = (String)map.get("versionName");
					int versionCode = (Integer)map.get("versionCode");
					String packageNames = (String)map.get("packageNames");
					msg.obj = api.checkVersion(curVersion,versionCode,packageNames);
				} catch (Exception ex) {
					msg.obj = null;
					ex.printStackTrace();
				}

				handlerForCheckVersion.sendMessage(msg);
			}
		}.start();
	}

	// 获取版本号
	public Map<String,Object> getVersion(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (info != null) {
			// 当前应用的版本名称
			String versionName = info.versionName;

			// 当前版本的版本号
			int versionCode = info.versionCode;

			// 当前版本的包名
			String packageNames = info.packageName;
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("versionName", versionName);
			map.put("versionCode", versionCode);
			map.put("packageNames", packageNames);

			return map;
		}

		return null;
	}

	private Handler handlerForCheckVersion = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (msg.obj != null) {
					JSONObject jsonObj = (JSONObject) msg.obj;
					try {
						if (jsonObj.get("code") != null && !jsonObj.get("code").equals("")) {
							String curCode = (String) jsonObj.get("code");
							String updataMsg = (String) jsonObj.get("des");
							String apkUrl = (String) jsonObj.get("url");
							initUpdate(updataMsg, apkUrl);
						} else {
							System.out.println("已是最新版本");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("检查版本失败，请检查网络是否工作正常！");
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void dowloadData()
	{
		myapp.saveSharedPerferences("isUpdate", "0");
		new_moments_txt.setVisibility(View.GONE);
	}
	
	public void dowloadData2()
	{
		myapp.saveSharedPerferences("isUpdate", "1");
		new_moments_txt.setVisibility(View.VISIBLE);
	}

	// 检测版本是否需要更新
	private void initUpdate(String updataMsg, String apkUrl) {
//		mUpdateManager = new UpdateManager(this);
//		mUpdateManager.apkUrl = apkUrl;
//		AlertDialog.Builder builder = new Builder(MainTabActivity.this);
//		builder.setTitle(getString(R.string.hotel_label_148));
//		builder.setMessage(updataMsg);
//		builder.setPositiveButton(getString(R.string.hotel_label_150), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				
//				mUpdateManager.showDownloadDialog();
//			}
//		});
//		builder.setNegativeButton(getString(R.string.hotel_label_167),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						
//					}
//				});
//		Dialog noticeDialog = builder.create();
//		noticeDialog.show();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("updataMsg", updataMsg);
		bundle.putString("apkUrl", apkUrl);
		bundle.putString("tag", "maintab");
		intent.putExtras(bundle);
    	intent.setClass(MainTabActivity.this,DowloadVersionDialog.class);
    	startActivity(intent);
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
			if(!user.equals("") && !paw.equals(""))
				mth.setCurrentTab(index);
			else
			{
				Intent intent = new Intent();
		    	intent.setClass(MainTabActivity.this,LoginDialog.class);
		    	startActivity(intent);
			}
				
		}
	};
    
    public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
//	/**
//	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
//	 */
//	public class MyLocationListenner implements BDLocationListener {
//		private MyApp myapp;
//		
//		public MyLocationListenner(MyApp myapp)
//		{
//			this.myapp = myapp;
//		}
//		
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null)
//				return;
//			myapp.setLatitude((int)(location.getLatitude() * 1E6));
//			myapp.setLongitude((int)(location.getLongitude() * 1E6));
//			myapp.setLat(String.valueOf(location.getLatitude()));
//			myapp.setLng(String.valueOf(location.getLongitude()));
////			System.out.println("百度定位 lat==" + myapp.getLat());
////			System.out.println("百度定位 lng==" + myapp.getLng());
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null) {
//				return;
//			}
//		}
//	}
	
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
	
	public void loadButtomActivity(Intent intent)
	{
		startActivity(intent);//开始界面的跳转函数
		overridePendingTransition(R.anim.slide_up_in,R.anim.faded_out);
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
        if(!start && !user.equals(""))
        {
        	start = true;
	        mNotificationManager.cancelAll();
	//        startBroadcastReceiver();
	        
	        IntentFilter filter = new IntentFilter();
	
			filter.addAction("NEW_MESSAGE_LIST_HUA_MEIDA");
			filter.addAction("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA");
			filter.addAction("unread_message_pus_hua_meida");
			
			registerReceiver(mBroadcastReceiver, filter);
			
			IntentFilter filter2 = new IntentFilter();
			filter2.addAction("VERIFICATION_MESSAGE_PUS_HUA_MEIDA");
			filter2.addAction("MOMENTS_NEW_PUS_HUA_MEIDA");
			filter2.addAction("CONFIRM_MESSAGE_PUS_HUA_MEIDA");
			filter2.addAction("MOMENTS_GONGAO_NEW_PUS_HUA_MEIDA");
			registerReceiver(mBroadcastReceiver2, filter2);
        }
        else
        {
        	mBroadcastReceiver = null;
        	mBroadcastReceiver2 = null;
        }
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
//        unregisterReceiver(mBroadcastReceiver);//关闭广播监听ddd
        super.onStop();  
    }  
    
    public void stopReceiver()
    {
    	if(mBroadcastReceiver != null)
    	{
    		unregisterReceiver(mBroadcastReceiver);//关闭广播监听ddd
    		mBroadcastReceiver = null;
    	}
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
    
    public void setServiceNewNumberValue(int number)
    {
    	if(number > 0)
    	{
    		serviceNumbertxt.setVisibility(View.VISIBLE);
    		serviceNumbertxt.setText(String.valueOf(number));
    	}
    	else
    	{
    		serviceNumbertxt.setVisibility(View.GONE);
    	}
    }
    
	public void startBroadcastReceiver() {
		
	}
	
	public void stopBroadcastReceiver()
	{

	}
	
	@Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBroadcastReceiver != null)
    	{
    		unregisterReceiver(mBroadcastReceiver);//关闭广播监听ddd
    		mBroadcastReceiver = null;
    		
    		unregisterReceiver(mBroadcastReceiver2);//关闭广播监听ddd
    		mBroadcastReceiver2 = null;
    	}
    }
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA"))
			{
				try{
					String datastr = intent.getStringExtra("datastr");
					String pusid = intent.getStringExtra("pusid");
//					JSONArray jArry = new JSONArray(datastr);
//					JSONObject jobj = jArry.getJSONObject(0);
//					String toname = jobj.getString("toid");
//					String storeName = jobj.getString("fname");
////					List<Map<String,Object>> newmessagelist = MessageListActivity.instance.getMessageDetialData(jArry);
//					boolean b = true;
//					if(MessageListActivity.instance != null)
//					{
//						b = false;
//					}
//					List<Map<String,Object>> newmessagelist = myapp.getMessageDetialData(jArry, toname, storeName, myapp.getPfprofileId(), myapp.getPfprofileId(),b);
//					Map<String,Object> map = newmessagelist.get(0);
//					String sname = (String)map.get("sname");
//					String mysendname = (String)map.get("mysendname");
//					shockAndRingtones(sname);
					saveMessageLocal2(datastr,pusid);
//					db.openDB();
//					Map<String,Object> dmap = db.saveMessageData(newmessagelist.get(0));
//					db.closeDB();
//					String mysendname = (String)dmap.get("mysendname");
//					if(HotelMainActivity.instance != null)
//						HotelMainActivity.instance.loadeListItemData();
//					
//					if(MessageListActivity.instance != null)
//					{
//						if(mysendname.equals(MessageListActivity.instance.toname))
//						{
//							MessageListActivity.instance.TextView01.setText(storeName);
//							MessageListActivity.instance.mymessageItem.addAll(newmessagelist);
//							MessageListActivity.instance.myAdapter.notifyDataSetChanged();
//						}
//					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(action.equals("unread_message_pus_hua_meida"))
			{
				try{
					String pusid = intent.getStringExtra("pusid");
					confirmPusMessage(pusid);
					getMyUnreadMessageListData();
					getMyUnreadRequestData();
					if (myapp.getIsServer()) {
						getMyUnreadGonGaoMessageListData();
					}
					else
					{
						getMyUnreadPenyouMessageListData();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	};
	
	private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("VERIFICATION_MESSAGE_PUS_HUA_MEIDA"))
			{
				String pusid = intent.getStringExtra("pusid");
				//更新新的好友请求验证信息
				getMyVerificationMessageListData();
				confirmPusMessage(pusid);
			}
			else if(action.equals("MOMENTS_NEW_PUS_HUA_MEIDA"))
			{
				try{
					String moments = intent.getStringExtra("moments");
					String momentsfiles = intent.getStringExtra("momentsfiles");
					String puspfid = intent.getStringExtra("puspfid");
					String pusid = intent.getStringExtra("pusid");
					JSONArray jArry = new JSONArray(momentsfiles);
					JSONObject jobj = new JSONObject(moments);
					Map<String,Object> map = myapp.getMyFriendMoments(jobj, jArry);
					List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
					dlist.add(map);
					if(myapp.getMomentsAllData().containsKey("frierd"))
					{
						List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get("frierd");
						alldata.addAll(0,dlist);
						myapp.getMomentsAllData().put("frierd", alldata);
					}
					if(MomentsActivity.instance != null)
					{
						if(MomentsActivity.instance.type.equals(""))
						{
							MomentsActivity.instance.chanegDataList(dlist);
						}
						else
						{
							db.openDB();
							String puspfpath = db.getFriendImagePath(puspfid);
//							db.saveMomentsAll(dlist);
							db.closeDB();
							updateMomentsImageStart(View.VISIBLE);
							myapp.setNewmomentsuserimg(puspfpath);
							myapp.setPuspfid(puspfid);
							if(MoreActivity.instance != null)
								MoreActivity.instance.updateMomentsImageStart(View.VISIBLE,puspfpath,puspfid);
//							else
//								myapp.setNewmomentsuserimg(puspfpath);
						}
					}
					else
					{
						db.openDB();
						String puspfpath = db.getFriendImagePath(puspfid);
//						db.saveMomentsAll(dlist);
						db.closeDB();
						updateMomentsImageStart(View.VISIBLE);
						myapp.setNewmomentsuserimg(puspfpath);
						myapp.setPuspfid(puspfid);
						if(MoreActivity.instance != null)
							MoreActivity.instance.updateMomentsImageStart(View.VISIBLE,puspfpath,puspfid);
//						else
//							myapp.setNewmomentsuserimg(puspfpath);
					}
					
					confirmPusMessage(pusid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(action.equals("MOMENTS_GONGAO_NEW_PUS_HUA_MEIDA"))
			{
				try{
					String moments = intent.getStringExtra("moments");
					String momentsfiles = intent.getStringExtra("momentsfiles");
					String puspfid = intent.getStringExtra("puspfid");
					String pusid = intent.getStringExtra("pusid");
					JSONArray jArry = new JSONArray(momentsfiles);
					JSONObject jobj = new JSONObject(moments);
					Map<String,Object> map = myapp.getMomentsGonGao(jobj, jArry);
					String publishtype = (String)map.get("publishtype");
					String key = "";
					if(publishtype.equals(""))
						key = "frierd";
					else
						key = publishtype;
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					lists.add(map);
					if(myapp.getMomentsAllData().containsKey(key))
					{
						List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
						alldata.addAll(0,lists);
						myapp.getMomentsAllData().put(key, alldata);
					}
					if(MomentsActivity.instance != null)
					{
						if(MomentsActivity.instance.type.equals(publishtype))
						{
							MomentsActivity.instance.chanegDataList(lists);
						}
						else
						{
							if(ManagerPublishBoardActivity.instance != null)
								ManagerPublishBoardActivity.instance.changListData(publishtype);
						}
					}
					else
					{
						if(ManagerPublishBoardActivity.instance != null)
							ManagerPublishBoardActivity.instance.changListData(publishtype);
					}
					
					confirmPusMessage(pusid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(action.equals("CONFIRM_MESSAGE_PUS_HUA_MEIDA"))
			{
				try{
					String mid = intent.getStringExtra("mid");
					String pusid = intent.getStringExtra("pusid");
					
					if(MessageListActivity.instance != null)
					{
						if(MessageListActivity.instance.tempmap.containsKey(mid))
						{
							int index = MessageListActivity.instance.tempmap.get(mid);
							MessageListActivity.instance.tempmap.remove(mid);
							MessageListActivity.instance.mymessageItem.get(index).put("messagestart", "0");
							MessageListActivity.instance.myAdapter.notifyDataSetChanged();
						}
						
						db.openDB();
						db.updateMessageData(mid,"0");
						db.closeDB();
						
					}
					else
					{
						db.openDB();
						db.updateMessageData(mid,"0");
						db.closeDB();
					}
					confirmPusMessage(pusid);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	};
	
	public void updateMomentsImageStart(int start)
	{
		new_moments_img.setVisibility(start);
	}
	
	public void getMyVerificationMessageListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					List<Map<String,Object>> list = null;
					int newnumber = 0;
					JSONObject jobj = api.selectVerificationMessage();
					if(jobj != null && jobj.has("data"))
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						Map<String,Object> dmap = myapp.getMyVerificationMessageList(jArr);
						list = (List<Map<String,Object>>)dmap.get("datalist");
						newnumber = (Integer)dmap.get("newnumber");
						myapp.setVerificationMessage(list);
					}
					
					msg.obj = newnumber;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
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
						list = myapp.getMessageDetialData(jArr);
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
	
	public void getMyUnreadGonGaoMessageListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 9;
				
				try {
					Map<String,Object> dmap = new HashMap<String,Object>();
					List<Map<String,Object>> list = null;
					List<Map<String,Object>> list2 = null;
					
					JSONObject job = api.getMyUnreadGonGaoMessageListData();
					if(job != null && job.has("data"))
					{
						JSONArray jarry = job.getJSONArray("data");
						if(jarry != null && jarry.length() > 0)
							list = new ArrayList<Map<String,Object>>();
						for(int i=0;i<jarry.length();i++)
						{
							JSONObject jobj = jarry.getJSONObject(i);
							String fpfid = jobj.getString("fpfid");
							String fname = jobj.getString("fname");
							String jsonstr = jobj.getString("jsonstr");
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("fpfid", fpfid);
							map.put("fname", fname);
							map.put("jsonstr", jsonstr);
							list.add(map);
						}
					}
					
					if(job != null && job.has("data2"))
					{
						JSONArray jarry = job.getJSONArray("data2");
						if(jarry != null && jarry.length() > 0)
							list2 = new ArrayList<Map<String,Object>>();
						for(int i=0;i<jarry.length();i++)
						{
							JSONObject jobj = jarry.getJSONObject(i);
							String fpfid = jobj.getString("fpfid");
							String fname = jobj.getString("fname");
							String jsonstr = jobj.getString("jsonstr");
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("fpfid", fpfid);
							map.put("fname", fname);
							map.put("jsonstr", jsonstr);
							list2.add(map);
						}
					}
					
					dmap.put("list", list);
					dmap.put("list2", list2);//朋友圈
					msg.obj = dmap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyUnreadPenyouMessageListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 9;
				
				try {
					Map<String,Object> dmap = new HashMap<String,Object>();
					List<Map<String,Object>> list2 = null;
					
					JSONObject job = api.getMyUnreadPenyouMessageListData();
					
					if(job != null && job.has("data2"))
					{
						list2 = new ArrayList<Map<String,Object>>();
						JSONArray jarry = job.getJSONArray("data2");
						for(int i=0;i<jarry.length();i++)
						{
							JSONObject jobj = jarry.getJSONObject(i);
							String fpfid = jobj.getString("fpfid");
							String fname = jobj.getString("fname");
							String jsonstr = jobj.getString("jsonstr");
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("fpfid", fpfid);
							map.put("fname", fname);
							map.put("jsonstr", jsonstr);
							list2.add(map);
						}
					}
					
					dmap.put("list", null);
					dmap.put("list2", list2);//朋友圈
					msg.obj = dmap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyUnreadRequestData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				
				try {
//					List<Map<String,Object>> list = null;
					int size = 0;
					
					JSONObject jobj = api.selectUnreadVerificationMessage();
					if(jobj != null && jobj.has("data"))
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						Map<String,Object> dmap = myapp.getMyVerificationMessageList(jArr);
						size = (Integer)dmap.get("newnumber");
					}
					
					msg.obj = size;
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
	
	public void saveMessageLocal2(final String datastr,final String pusid)
	{
		try{
			new Thread(){
				public void run(){
					Message msg = new Message();
					msg.what = 0;
					
					try{
						JSONArray jArry = new JSONArray(datastr);
						JSONObject jobj = jArry.getJSONObject(0);
						String toname = jobj.getString("toid");
						String storeName = jobj.getString("fname");
						String messagetype = jobj.getString("messagetype");
						String content = jobj.getString("content");
						String groupid = "";
						String groupName = "";
						if(jobj.has("groupid"))
							groupid = jobj.getString("groupid");
						if(jobj.has("groupName"))
							groupName = jobj.getString("groupName");
	//					List<Map<String,Object>> newmessagelist = MessageListActivity.instance.getMessageDetialData(jArry);
						boolean b = true;
						if(MessageListActivity.instance != null)
						{
							b = false;
						}
						List<Map<String,Object>> newmessagelist = myapp.getMessageDetialData(jArry, toname, storeName, myapp.getPfprofileId(), myapp.getPfprofileId(),b);
						Map<String,Object> map = new HashMap<String,Object>();
						db.openDB();
						Map<String,Object> dmap = db.saveMessageData(newmessagelist.get(0));
						db.closeDB();
						map.put("datamap", dmap);
						map.put("storeName", storeName);
						map.put("messagetype", messagetype);
						map.put("content", content);
						map.put("pusid", pusid);
						map.put("groupid", groupid);
						map.put("groupName", groupName);
						
						msg.obj = map;
					}catch(Exception ex){
						ex.printStackTrace();
						msg.obj = null;
					}
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadUserImage()
	{
		String userimgBase64 = share.getString("userimg", "");
        if(userimgBase64 != null && !userimgBase64.equals(""))
        {
		    // 对Base64格式的字符串进行解码
		    byte[] base64Bytes = Base64.decodeBase64(userimgBase64.getBytes());
		    Bitmap bitmap = BitmapFactory.decodeByteArray(base64Bytes,0,base64Bytes.length);
		    bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
		    myapp.setUserimgbitmap(bitmap);
        }
        else
        {
        	userimg = myapp.getUserimg();
        	if(userimg != null && !userimg.equals(""))
        		loadUserImageThread();
        }
	}
	
	public void loadUserImageThread()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				
				try {
					Bitmap bitmap = myapp.getImageBitmap(userimg);
					if(bitmap != null)
					{
						int size=bitmap.getWidth()*bitmap.getHeight()*1; 
						ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, oss);
	                	String productBase64 = new String(Base64.encodeBase64(oss.toByteArray()));
	                	SharedPreferences.Editor editor = share.edit();
	                	// 将编码后的字符串写到base64.xml文件中
	                	editor.putString("userimg", productBase64);
	                	editor.commit();
						myapp.setUserimgbitmap(bitmap);
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				makeText("数据保存本地成功");
//				HotelMainActivity.instance.loadeListItemData();
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				if(map != null)
				{
					Map<String,Object> dmap = (Map<String,Object>)map.get("datamap");
					String storeName = (String)map.get("storeName");
					String mysendname = (String)dmap.get("mysendname");
					String sname = (String)dmap.get("sname");
					String messagetype = (String)map.get("messagetype");
					String content = (String)map.get("content");
					String pusid = (String)map.get("pusid");
					String groupid = (String)map.get("groupid");
					String groupName = (String)map.get("groupName");
					
					if(messagetype.equals("yanzhenjieguo"))
					{
						if(content.equals("0"))
						{
							String isYn = "";
							String isYn2 = "";
							if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
							{
								isYn = "0";
								isYn2 = "1";
							}
							else
							{
								isYn = "1";
								isYn2 = "0";
							}
							
							getMyFriendData(isYn,mysendname);
						}
					}
					
					if(myapp.getIsServer())//如果是服务新信息都在信息里显示
					{
						if(HotelMainActivity.instance != null)
							HotelMainActivity.instance.loadeListItemData();
						else
							loadeListItemData();
					}
					else//如果是前台
					{
						if(messagetype.equals("friend") || messagetype.equals("yanzhenjieguo") || messagetype.equals("group"))//如果是好友验证和好友信息都在信息栏里显示
						{
							if(HotelMainActivity.instance != null)
								HotelMainActivity.instance.loadeListItemData();
							else
								loadeListItemData();
						}
						else//其他都在服务栏里显示
						{
							if(HotelServiceActivity.instance != null)
								HotelServiceActivity.instance.getMyStoreListDatas();
							else
								loadeServiceListItemData();
						}
						
					}
					
					if(MessageListActivity.instance != null)
					{
						if(mysendname.equals(MessageListActivity.instance.toname))
						{
							MessageListActivity.instance.TextView01.setText(storeName);
							MessageListActivity.instance.mymessageItem.add(dmap);
							MessageListActivity.instance.myAdapter.notifyDataSetChanged();
						}
						else if(groupid.equals(MessageListActivity.instance.toname))
						{
							MessageListActivity.instance.TextView01.setText(groupName);
							MessageListActivity.instance.mymessageItem.add(dmap);
							MessageListActivity.instance.myAdapter.notifyDataSetChanged();
						}
					}
					else
					{
						shockAndRingtones(sname);
					}
					
					confirmPusMessage(pusid);
				}
				else
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					else
						loadeListItemData();
				}
				break;
			case 1:
				List<Map<String,Object>> newmessagelist = (List<Map<String,Object>>)msg.obj;
				if(newmessagelist != null && newmessagelist.size() > 0)
				{
//					shockAndRingtones();
					saveMessageLocal(newmessagelist);
				}
				break;
			case 2:
				int newnumber = (Integer)msg.obj;
				if(newnumber > 0)
				{
					new_yanzhen_number.setVisibility(View.VISIBLE);
					new_yanzhen_number.setText(String.valueOf(newnumber));
					
					if(ContactActivity.instance != null)
						ContactActivity.instance.setNewNumberTxtValue(String.valueOf(newnumber));
					
					if(InformationFriendActivity.instance != null)
						InformationFriendActivity.instance.refreshListItem();
				}
				break;
			case 3:
				int size = (Integer)msg.obj;
				if(size > 0)
				{
					new_yanzhen_number.setVisibility(View.VISIBLE);
					new_yanzhen_number.setText(String.valueOf(size));
					
					if(ContactActivity.instance != null)
						ContactActivity.instance.setNewNumberTxtValue(String.valueOf(size));
					
					if(InformationFriendActivity.instance != null)
						InformationFriendActivity.instance.refreshListItem();
				}
				break;
			case 4:
				if(HotelServiceActivity.instance != null)
					HotelServiceActivity.instance.getMyStoreListDatas();
				break;
			case 5:
				int zonshu = (Integer)msg.obj;
				setNewNumberValue(zonshu);
				break;
			case 6:
				String isYn = (String)msg.obj;
				String isYn2 = "";
				if(isYn.equals("0"))
					isYn2 = "1";
				else
					isYn2 = "0";
				if(FriendsContactActivity.instance != null)
					FriendsContactActivity.instance.getMyStoreListDatas("",isYn2);
				else
				{
					if(ContactActivity.instance != null)
						ContactActivity.instance.getMyFriendDatas(isYn);
				}
				break;
			case 7:
				storeIsloade = true;
				if(myapp.getIsServer())
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					else
						loadeListItemData();
				}
				else
				{
					if(HotelServiceActivity.instance != null)
					{
						HotelServiceActivity.instance.showMyLoadingDialog();
						HotelServiceActivity.instance.getMyStoreListDatas();
					}
					else
						loadeServiceListItemData();
					
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					else
						loadeListItemData();
				}
				break;
			case 8:
				int szonshu = (Integer)msg.obj;
				setServiceNewNumberValue(szonshu);
				break;
			case 9:
				Map<String,Object> dmap = (Map<String,Object>)msg.obj;
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)dmap.get("list");
				List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)dmap.get("list2");
				loadUnreadGonGao(dlist);
				loadUnreadPenyouQuan(dlist2);
				break;
			}
		}
	};
	
	public void loadUnreadGonGao(List<Map<String,Object>> dlist)
	{
		try{
			if(dlist != null)
			{
				newGonGaomap = new HashMap<String,Object>();
				for(int i=0;i<dlist.size();i++)
				{
					Map<String,Object> maps = dlist.get(i);
					String jsonstr = (String)maps.get("jsonstr");
					JSONObject job = new JSONObject(jsonstr);
					
					JSONArray jArry = job.getJSONArray("friend_moments_files_pus");
					JSONObject jobj = job.getJSONObject("gongao_moments_pus");
					String puspfid = job.getString("update_moments_pfid_pus");
					
					Map<String,Object> map = myapp.getMomentsGonGao(jobj, jArry);
					String publishtype = (String)map.get("publishtype");
					String key = "";
					if(publishtype.equals(""))
						key = "frierd";
					else
						key = publishtype;
					if(newGonGaomap.containsKey(key))
					{
						List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> lists2 = (List<Map<String, Object>>)newGonGaomap.get(key);
						lists.add(map);
						lists2.add(map);
						if(myapp.getMomentsAllData().containsKey(key))
						{
							List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
							alldata.addAll(lists);
							myapp.getMomentsAllData().put(key, alldata);
						}
						newGonGaomap.put(key, lists2);
					}
					else
					{
						List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> lists2 = new ArrayList<Map<String, Object>>();
						lists.add(map);
						lists2.add(map);
						if(myapp.getMomentsAllData().containsKey(key))
						{
							List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
							alldata.addAll(lists);
							myapp.getMomentsAllData().put(key, alldata);
						}
						newGonGaomap.put(key, lists2);
					}
					
				}
				
				if(MomentsActivity.instance != null)
				{
					if(newGonGaomap.containsKey(MomentsActivity.instance.type))
					{
						List<Map<String, Object>> lists = (List<Map<String, Object>>)newGonGaomap.get(MomentsActivity.instance.type);
						MomentsActivity.instance.chanegDataList(lists);
						newGonGaomap.remove(MomentsActivity.instance.type);
					}
					
					if(ManagerPublishBoardActivity.instance != null)
						ManagerPublishBoardActivity.instance.changListData(newGonGaomap);
					
				}
				else
				{
					if(ManagerPublishBoardActivity.instance != null)
						ManagerPublishBoardActivity.instance.changListData(newGonGaomap);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadUnreadPenyouQuan(List<Map<String,Object>> dlist)
	{
		try{
			if(dlist != null)
			{
				List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
				String puspfid = "";
				for(int i=0;i<dlist.size();i++)
				{
					Map<String,Object> maps = dlist.get(i);
					String jsonstr = (String)maps.get("jsonstr");
					JSONObject job = new JSONObject(jsonstr);
					
					JSONArray jArry = job.getJSONArray("friend_moments_files_pus");
					JSONObject jobj = job.getJSONObject("friend_moments_pus");
					puspfid = job.getString("update_moments_pfid_pus");
					
					Map<String,Object> map = myapp.getMyFriendMoments(jobj, jArry);
					lists.add(map);
				}
				
				String key = "frierd";
				if(myapp.getMomentsAllData().containsKey(key))
				{
					List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
					alldata.addAll(lists);
					myapp.getMomentsAllData().put(key, alldata);
				}
				
				if(MomentsActivity.instance != null)
				{
					if(MomentsActivity.instance.type.equals(""))
					{
						MomentsActivity.instance.chanegDataList(lists);
					}
					else
					{
						db.openDB();
						String puspfpath = db.getFriendImagePath(puspfid);
						db.closeDB();
						updateMomentsImageStart(View.VISIBLE);
						myapp.setNewmomentsuserimg(puspfpath);
						myapp.setPuspfid(puspfid);
						if(MoreActivity.instance != null)
							MoreActivity.instance.updateMomentsImageStart(View.VISIBLE,puspfpath,puspfid);
					}
				}
				else
				{
					db.openDB();
					String puspfpath = db.getFriendImagePath(puspfid);
					db.closeDB();
					updateMomentsImageStart(View.VISIBLE);
					myapp.setNewmomentsuserimg(puspfpath);
					myapp.setPuspfid(puspfid);
					if(MoreActivity.instance != null)
						MoreActivity.instance.updateMomentsImageStart(View.VISIBLE,puspfpath,puspfid);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadeListItemData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 5;
				
				try {
					List<Map<String,Object>> list = null;
					db.openDB();
					Map<String,Object> dmap = db.getNewMessageData("0");
					db.closeDB();
					list = (List<Map<String,Object>>)dmap.get("dlist");
					int zonshu = (Integer)dmap.get("zhonshu");
					
					msg.obj = zonshu;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadeServiceListItemData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 8;
				
				try {
					List<Map<String,Object>> list = null;
					db.openDB();
					Map<String,Object> dmap = db.getNewMessageData("1");
					db.closeDB();
					list = (List<Map<String,Object>>)dmap.get("dlist");
					int zonshu = (Integer)dmap.get("zhonshu");
					
					msg.obj = zonshu;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyCardListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 7;
				
				try {
//					List<Map<String,Object>> list = null;
					List<Map<String,Object>> storelist = null;
					db.openDB();
//					Map<String,Object> dmap = db.getNewMessageData("2");
//					db.deleteStroeInfoAll();
					storelist = db.getStoreInfoAllData("");
					myapp.setMyCardsAll(storelist);
					if(storelist == null || storelist.size() == 0)
					{
						JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> lists = myapp.getMyCardList(jArr);
							db.saveStoreInfoAllData(lists);
							storelist = db.getStoreInfoAllData("");
							myapp.setMyCardsAll(storelist);
						}
					}
					db.closeDB();
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyFriendData(final String tag,final String friendid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 6;
				
				try {
					JSONObject jobj = api.selectMyFriendList(tag,friendid);
					if(jobj != null && jobj.has("data"))
					{
						db.openDB();
						JSONArray jarry = jobj.getJSONArray("data");
						List<Map<String,Object>> dlist = myapp.getMyFriendList(jarry);
						if(dlist != null && dlist.size() > 0)
						{
							Map<String,Object> fmap = dlist.get(0);
							db.saveFriendAllData(fmap);
						}
						db.closeDB();
					}
					
					msg.obj = tag;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public boolean isInetnState() {
		boolean inetnState = true;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			inetnState = false;
		} else {
			inetnState = true;
		}
		return inetnState;
	}
	
	public void loadTrim()
	{
		try{
			timer.scheduleAtFixedRate(new TimerTask()
	        {  
	            @Override  
	            public void run()  
	            {  
	                // TODO Auto-generated method stub  
	                Message mesasge = new Message();  
	                mesasge.what = 4;
	                try{
		                List<Map<String,Object>> storelist = null;
						db.openDB();
		                JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> lists = myapp.getMyCardList(jArr);
							db.saveOrUpdateStoreInfo(lists);
							storelist = db.getStoreInfoAllData("");
							myapp.setMyCardsAll(storelist);
						}
						db.closeDB();
	                }catch(Exception ex){
	                	ex.printStackTrace();
	                }
	                handler.sendMessage(mesasge); 
	            }  
	        }, 60000*60*12, 60000*60*12);  
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadNettyTrim()
	{
		try{
			timer2 = new Timer();
			timer2.schedule(new TimerTask()
	        {  
	            @Override  
	            public void run()  
	            {  
	                // TODO Auto-generated method stub  
	                try{
		               if(myapp.getChannel() != null)
		               {
		            	   myapp.getChannel().write("xintiao...\r\n");
		               }
	                }catch(Exception ex){
	                	ex.printStackTrace();
	                }
	            }  
	        }, 5000, 8000);
		}catch(Exception ex){
			ex.printStackTrace();
			timer2 = null;
			loadNettyTrim();
		}
	}
	
	public void openLogin()
	{
		try{
			Intent intent=new Intent();
			intent.setClass(MainTabActivity.this,LoginMain.class);
			startActivity(intent);
//			HotelActivity.instance.finish();
			//HotelWinActivity.instance.finish();
			MenuLoadActivity.instance.finish();
			finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openRegisterAction()
	{
		try{
			Intent intent=new Intent();
			intent.setClass(MainTabActivity.this,RegisterAction.class);
			startActivity(intent);
//			HotelActivity.instance.finish();
			//HotelWinActivity.instance.finish();
			MenuLoadActivity.instance.finish();
			finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void confirmPusMessage(final String pusid)
	{
		new Thread() {
			public void run() {
				try {
					JSONObject jobj = api.confirmPusMessage(pusid);
//					if(jobj != null && jobj.getString("tag").equals("success"))
//					{
//						if(SecureChatClientHandler.pusids.contains(pusid))
//							SecureChatClientHandler.pusids.remove(pusid);
//					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}
	
	// 新信息来时铃声加震动
	public void shockAndRingtones(String sname) {
		try {
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
}
