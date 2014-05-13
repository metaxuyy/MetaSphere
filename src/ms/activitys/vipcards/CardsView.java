package ms.activitys.vipcards;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ms.activitys.MainTabActivity;
import ms.activitys.MyCommentView;
import ms.activitys.R;
import ms.activitys.bill.MyBillList;
import ms.activitys.coupon.MyCouponView;
import ms.activitys.hotel.HotelReservation;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.hotel.MyHotelReservation;
import ms.activitys.hotel.StoreViewActivity;
import ms.activitys.integral.GetIntegralHistoryActity;
import ms.activitys.map.BaiduMap;
import ms.activitys.map.BaiduMapRouteSearch;
import ms.activitys.map.PeripheryMapsActivity;
import ms.activitys.orders.MyOrederListActivity;
import ms.activitys.product.ProductShoppingCartActivity;
import ms.activitys.product.ProductTypeActivity;
import ms.activitys.restaurant.RestaurantDestine;
import ms.activitys.traffic.GetTicketsInfomationActivity;
import ms.activitys.travel.BaiduMapTravel;
import ms.activitys.travel.RealActivity;
import ms.activitys.travel.TravelActivity;
import ms.activitys.user.UserInfoDetailed;
import ms.globalclass.FriendlyScrollView;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperShoppingCart;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.PageControlView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

public class CardsView extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// 首页
	private final int TOOLBAR_ITEM_MAP = 1;// 退后
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// 前进
	private final int TOOLBAR_ITEM_NFC = 3;// 创建
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	
	private Integer index; 
	
	private static String[] m = new String[4];
    
    private static String[] m2 = new String[2];
    
    private SensorManager sm;
    private SensorEventListener sl;
    private Sensor acceleromererSensor;
    //数字越低敏感度越
    private static final int SHAKE_THRESHOLD = 500;
    
  //检测摇动相关变量
    private long initTime = 0;
    private long lastTime = 0;
    private long curTime = 0;
    private long duration = 0;
    
    private float last_x = 0.0f;
    private float last_y = 0.0f;
    private float last_z = 0.0f;
    
    private float shake = 0.0f;
    private float totalShake = 0.0f;
    
    private int mapcontext = 0;
    
    private long flsttime = 0;
    private long lasttime = 0;
    
    private String isASttention;
    
    private String appliescStoreid;
    
    private ProgressDialog mypDialog;
    
    private ViewFlipper mViewFlipper;
    
    private GestureDetector mGestureDetector;  
    
    private RelativeLayout rlayout;
    
    private int layoutindex = 0;
    
    private static final int FLING_MIN_DISTANCE = 100;  
    private static final int FLING_MIN_VELOCITY = 200;
    
    private static final String URL_ACTIVITY_CALLBACK = "myweiboandroidsdk://TimeLineActivity";
	private static final String FROM = "xweibo";
	
	private static final String CONSUMER_KEY = "3998648098";
	private static final String CONSUMER_SECRET = "38fafd88414f561b8ddc00b1c4c877e7";
	
	private String accessToken = "";
    private String tokenSecret = "";
    private String verifier = "";
    
    private String maptag = "";
    private String advertiseNotification;
    
    private PageControlView pageControl;
    private View travelview;
    private Integer [] bgcolors = {};
	private int randomNumber = 0;
	
	private DBHelperShoppingCart db;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_detail_activity);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		
		m[0] = this.getString(R.string.cards_lable_1);
		m[1] = this.getString(R.string.cards_lable_2);
		m[2] = this.getString(R.string.cards_lable_3);
		m[3] = this.getString(R.string.cards_lable_4);

		m2[0] = this.getString(R.string.cards_lable_5);
		m2[1] = this.getString(R.string.cards_lable_6);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = CardsView.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperShoppingCart(this,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = Integer.valueOf(bunde.getString("index"));
		maptag = bunde.getString("tag");
		advertiseNotification = bunde.getString("advertiseNotification");
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		if(index == null)
			index = 0;
		Map map = list.get(index);
		String storeid = (String)map.get("storeid");
		appliescStoreid = storeid;
	
		String typesMapping = (String)map.get("typesMapping");
		if(typesMapping == null)
			typesMapping = "01";
		if(typesMapping != null)
		{
			if(typesMapping.equals("05"))
			{
				LinearLayout tlayout = (LinearLayout)findViewById(R.id.layout_customize_view);
				
				travelview = LayoutInflater.from(this).inflate(R.layout.my_card_travel_view, null);
				tlayout.addView(travelview);
			}
			else
			{
				LinearLayout tlayout = (LinearLayout)findViewById(R.id.layout_customize_view);
				mViewFlipper.removeView(tlayout);
			}
		}
		
		pageControl=(PageControlView) findViewById(R.id.pageControl);
		pageControl.bindScrollViewGroup(mViewFlipper);
		
		mGestureDetector = new GestureDetector(new CommonGestureListener());  
		
		final String woof = (String)map.get("woof");
		final String longItude = (String)map.get("longItude");
		final String storeName = (String)map.get("storeName");
//		Button realBtn = (Button)findViewById(R.id.real_btn);
//		realBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				showRealActivity(woof,longItude,storeName);
//			}
//		});
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				CardsView.this.setResult(RESULT_OK, getIntent());
//				CardsView.this.finish();
				breakActivity();
			}
		});
		
		List<Map<String,Object>> cartlist = db.loadMyShoopingCart(appliescStoreid);
		TextView cartTxt = (TextView)findViewById(R.id.cart_number);
		if(cartlist != null && cartlist.size()>0)
			cartTxt.setText(String.valueOf(cartlist.size()));
		else
			cartTxt.setVisibility(View.GONE);
//		rlayout.setOnTouchListener(this);  
//		
//		rlayout.setLongClickable(true);  
		
		loadScrollView();
		
		mapcontext = myapp.getMyCardsAll().size();
		
//		addMainMenu();
//		weiboBinding();
		
		loadScheduledMenu();
		
		loadSensor();
		
		showMycardview();
	}
	
	public void showRealActivity(String woof,String longItude,String storeName)
	{
		Intent intent = new Intent();
	    intent.setClass( this,RealActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("longtude", longItude);
		bundle.putString("woof", woof);
		bundle.putString("name", storeName);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public int getRandomNumber(int number)
	{
		int randomNumber = (int)(Math.random()*6);
		if(number == randomNumber)
			return getRandomNumber(number);
		return randomNumber;
	}
	
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		 
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};
	
	public void loadScrollView()
	{
		FriendlyScrollView scroll = (FriendlyScrollView) findViewById(R.id.ScrollView01);
		scroll.setOnTouchListener(onTouchListener);
		scroll.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll2 = (FriendlyScrollView) findViewById(R.id.ScrollView02);
		scroll2.setOnTouchListener(onTouchListener);
		scroll2.setGestureDetector(mGestureDetector);
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		Map map = list.get(Integer.valueOf(index));
	
		String typesMapping = (String)map.get("typesMapping");
		if(typesMapping == null)
			typesMapping = "01";
		if(typesMapping != null)
		{
			if(typesMapping.equals("05"))
			{
				FriendlyScrollView scroll8 = (FriendlyScrollView) travelview.findViewById(R.id.ScrollView08);
				scroll8.setOnTouchListener(onTouchListener);
				scroll8.setGestureDetector(mGestureDetector);
			}
		}
		
		FriendlyScrollView scroll3 = (FriendlyScrollView) findViewById(R.id.ScrollView03);
		scroll3.setOnTouchListener(onTouchListener);
		scroll3.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll4 = (FriendlyScrollView) findViewById(R.id.ScrollView04);
		scroll4.setOnTouchListener(onTouchListener);
		scroll4.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll5 = (FriendlyScrollView) findViewById(R.id.ScrollView05);
		scroll5.setOnTouchListener(onTouchListener);
		scroll5.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll6 = (FriendlyScrollView) findViewById(R.id.ScrollView06);
		scroll6.setOnTouchListener(onTouchListener);
		scroll6.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll7 = (FriendlyScrollView) findViewById(R.id.ScrollView07);
		scroll7.setOnTouchListener(onTouchListener);
		scroll7.setGestureDetector(mGestureDetector);
	}
	
	public void addMainMenu()
	{
		// 设置自定义menu菜单
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		
		menuGrid.setAdapter(getMenuAdapter(myapp.getCard_menu_name_array(), myapp.getCard_menu_image_array()));
		/** 监听menu选项 **/
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
					case 0:// 我的历史消费
						if(StreamTool.isNetworkVailable(CardsView.this))
							showHistoricalConsumption();
						else
							makeText(CardsView.this.getString(R.string.seting_lable_7));
						break;
					case 1:// 点菜单
						if(StreamTool.isNetworkVailable(CardsView.this))
							showStoreMume();
						else
							makeText(CardsView.this.getString(R.string.seting_lable_7));
						break;
					case 2:// 订单状态
						if(StreamTool.isNetworkVailable(CardsView.this))
							showOrderStatus();
						else
							makeText(CardsView.this.getString(R.string.seting_lable_7));
						break;
					case 3:// 拨打预定电话
						showPhoneBook();
						break;
					case 4:// 在线预定
						
						break;
					case 5:// 优惠券
						showCouponView();
						break;
					case 6:// 发表评论
						if(StreamTool.isNetworkVailable(CardsView.this))
							showComment();
						else
							makeText(CardsView.this.getString(R.string.seting_lable_7));
						break;
					case 7:// 条形码
						showCardTiaoXinMa();
						break;
					case 8:// 积分历史
						
						break;
					case 9:// 积分换物
						
						break;
					case 10:// 积分交易
						
						break;
					case 11:// 捐献积分
						
						break;
					case 12:// 会员卡详细
						showCardDetails();
						break;
				}
				
				
			}
		});
		
//		LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout05);
//		layout.getBackground().setAlpha(100);
//		layout.addView(menuView);
		
	}
	
	public void loadScheduledMenu()
	{
		final LinearLayout dianhua_yudin = (LinearLayout)findViewById(R.id.dianhua_yudin);// 拨打预定电话
		dianhua_yudin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPhoneBook();
			}
		});
		
		final LinearLayout zaixian_yudin = (LinearLayout)findViewById(R.id.zaixian_yudin);// 在线预定
		zaixian_yudin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showOnlineReservationsView();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout my_yudin = (LinearLayout)findViewById(R.id.my_yudin);// 我的预定
		my_yudin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showMyReservationsView();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout download_coupon = (LinearLayout)findViewById(R.id.download_coupon);// 下载优惠券
		download_coupon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showDownloadCouponView();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout my_use_coupon = (LinearLayout)findViewById(R.id.my_use_coupon);// 我的优惠券
		my_use_coupon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyCouponView();
			}
		});
		
		final LinearLayout online_products = (LinearLayout)findViewById(R.id.online_products);// 选择商品
		online_products.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showStoreMume();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout shopping_cart = (LinearLayout)findViewById(R.id.shopping_cart);// 购物车
		shopping_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showShoppingCart();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout look_my_order = (LinearLayout)findViewById(R.id.look_my_order);// 我的订单
		look_my_order.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showOrderStatus();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout look_history_order = (LinearLayout)findViewById(R.id.look_history_order);// 我的历史消费
		look_history_order.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showHistoricalConsumption();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		Map map = list.get(Integer.valueOf(index));
	
		String typesMapping = (String)map.get("typesMapping");
		if(typesMapping == null)
			typesMapping = "01";
		if(typesMapping.equals("05"))
		{
//			LinearLayout attractions = (LinearLayout)travelview.findViewById(R.id.attractions);// 旅游景点介绍
//			attractions.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(CardsView.this))
//						showAttractionsPage();
//					else
//						makeText("当前网络不可用！");
//				}
//			});
//			
//			LinearLayout attractions2 = (LinearLayout)travelview.findViewById(R.id.periphery_hottel);// 周边酒店搜索
//			attractions2.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(CardsView.this))
//						showTypeMapSearch("09");
//					else
//						makeText("当前网络不可用！");
//				}
//			});
//			
//			LinearLayout attractions3 = (LinearLayout)travelview.findViewById(R.id.periphery_food);// 周边美食搜索
//			attractions3.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(CardsView.this))
//						showTypeMapSearch("10");
//					else
//						makeText("当前网络不可用！");
//				}
//			});
//			
//			LinearLayout attractions4 = (LinearLayout)travelview.findViewById(R.id.periphery_shopping);// 周边购物搜索
//			attractions4.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(CardsView.this))
//						showTypeMapSearch("12");
//					else
//						makeText("当前网络不可用！");
//				}
//			});
//			
//			LinearLayout attractions5 = (LinearLayout)travelview.findViewById(R.id.periphery_parking);// 周边停车场搜索
//			attractions5.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(CardsView.this))
//						showTypeMapSearch("11");
//					else
//						makeText("当前网络不可用！");
//				}
//			});
			
			GridView menuGrid = (GridView) travelview.findViewById(R.id.gridview);
			menuGrid.setAdapter(getMenuAdapter(myapp.getCard_menu_name_array(), myapp.getCard_menu_image_array()));
			/** 监听menu选项 **/
			menuGrid.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					switch (arg2) {
						case 0:// 旅游景点介绍
							if(StreamTool.isNetworkVailable(CardsView.this))
								showAttractionsPage();
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 1:// 周边酒店搜索
							if(StreamTool.isNetworkVailable(CardsView.this))
								showTypeMapSearch("09");
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 2:// 周边美食搜索
							if(StreamTool.isNetworkVailable(CardsView.this))
								showTypeMapSearch("10");
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 3:// 周边购物搜索
							if(StreamTool.isNetworkVailable(CardsView.this))
								showTypeMapSearch("12");
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 4:// 周边停车场搜索
							if(StreamTool.isNetworkVailable(CardsView.this))
								showTypeMapSearch("11");
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 5:// 周边娱乐搜索
							if(StreamTool.isNetworkVailable(CardsView.this))
								showTypeMapSearch("01");
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
						case 6:// 旅游攻略
							if(StreamTool.isNetworkVailable(CardsView.this))
								showReommendedRoute();
							else
								makeText(CardsView.this.getString(R.string.seting_lable_7));
							break;
					 }
				}
			});


		}
		
		final LinearLayout look_my_integral_history = (LinearLayout)findViewById(R.id.look_my_integral_history);// 我的积分历史
		look_my_integral_history.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showIntegralHistory();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout exchange_items = (LinearLayout)findViewById(R.id.exchange_items);// 积分换物
		exchange_items.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showIntegralType();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout credits_transaction = (LinearLayout)findViewById(R.id.credits_transaction);// 积分交易
		credits_transaction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final LinearLayout points_donated = (LinearLayout)findViewById(R.id.points_donated);// 积分交易
		points_donated.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final LinearLayout post_my_msm = (LinearLayout)findViewById(R.id.post_my_msm);// 发表评论
		post_my_msm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final LinearLayout look_store_msm = (LinearLayout)findViewById(R.id.look_store_msm);// 查看商家评论
		look_store_msm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showComment();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout look_my_detailed_info = (LinearLayout)findViewById(R.id.look_my_detailed_info);// 查看个人详细信息
		look_my_detailed_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showCardDetails();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
		final LinearLayout traffic_view_show = (LinearLayout)findViewById(R.id.traffic_view_show);// 查看飞机,火车,大巴查询
		traffic_view_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(StreamTool.isNetworkVailable(CardsView.this))
					showTrafficView();
				else
					makeText(CardsView.this.getString(R.string.seting_lable_7));
			}
		});
		
//		final LinearLayout modify_my_password = (LinearLayout)findViewById(R.id.modify_my_password);// 修改密码
//		modify_my_password.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//					updateUserPassword();
//				else
//					makeText("当前网络不可用！");
//			}
//		});
		
//		final LinearLayout binding_sina = (LinearLayout)findViewById(R.id.binding_sina);// 新浪微博绑定
//		binding_sina.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingSina", false))
//					{
//						notBinding("sina");
//					}
//					else
//						bindingSinaWeibo();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
		
//		final LinearLayout binding_renren = (LinearLayout)findViewById(R.id.binding_renren);// 人人网绑定
//		binding_renren.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingRenren", false))
//					{
//						notBinding("renren");
//					}
//					else
//						bindingRenren();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
//		
//		final LinearLayout binding_qq = (LinearLayout)findViewById(R.id.binding_qq);// 腾讯微博绑定
//		binding_qq.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingQq", false))
//					{
//						notBinding("qq");
//					}
//					else
//						bindingQQ();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
//		
//		final LinearLayout binding_kx = (LinearLayout)findViewById(R.id.binding_kx);// 开心网绑定
//		binding_kx.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingkx", false))
//					{
//						notBinding("kx");
//					}
//					else
//						bindingKx();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
		
//		final LinearLayout binding_sh = (LinearLayout)findViewById(R.id.binding_sh);// 搜狐微博绑定
//		binding_sh.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingsh", false))
//					{
//						notBinding("sh");
//					}
//					else
//						bindingSh();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
		
//		final LinearLayout binding_wy = (LinearLayout)findViewById(R.id.binding_wy);// 网易微博绑定
//		binding_wy.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(StreamTool.isNetworkVailable(CardsView.this))
//				{
//					if(share.getBoolean("isBindingwy", false))
//					{
//						notBinding("wy");
//					}
//					else
//						bindingwy();
//				}
//				else
//					makeText("当前网络不可用！");
//			}
//		});
	}
	
	public void notBinding(final String tag)
	{
		new AlertDialog.Builder(this).setTitle(this.getString(R.string.setting_title))
		.setMessage(this.getString(R.string.seting_lable_8)).setIcon(R.drawable.error2)
		.setPositiveButton(this.getString(R.string.coupon_lable_14),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(tag.equals("sina"))
						{
							myapp.setBindingSina(false);
							share.edit().putString("sina_token", "").putString("sina_secret", "").putString("sina_userId", "").putBoolean("isBindingSina", false).commit();
							weiboBinding();
						}
						else if(tag.equals("renren"))
						{
							share.edit().putString("accessTokenRenren", "").putBoolean("isBindingRenren", false).commit();
							weiboBinding();
						}
						else if(tag.equals("qq"))
						{
							share.edit().putString("accessTokenQq", "").putString("accessTokenSecret", "").putString("name", "").putBoolean("isBindingQq", false).commit();
							weiboBinding();
						}
						else if(tag.equals("kx"))
						{
							share.edit().putString("accessTokenkx", "").putString("accessTokenSecretkx", "").putBoolean("isBindingkx", false).commit();
							weiboBinding();
						}
//						else if(tag.equals("sh"))
//						{
//							share.edit().putString("accessTokensh", "").putString("accessTokenSecretsh", "").putBoolean("isBindingsh", false).commit();
//							weiboBinding();
//						}
//						else if(tag.equals("wy"))
//						{
//							share.edit().putString("accessTokenwy", "").putString("accessTokenSecretwy", "").putBoolean("isBindingwy", false).commit();
//							weiboBinding();
//						}
						dialog.dismiss();
					}
				}).setNegativeButton(this.getString(R.string.coupon_lable_16),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// 取消按钮事件
					}
				}).show();
	}
	
	/**
	 * 显示店得分布地图
	 */
	public void showMyMap()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MapPage.class);
			intent.setClass( this,BaiduMap.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			breakActivity();	
			return false;
		}
		return false;
	}
	
	public void breakActivity()
	{
		if(maptag != null && !maptag.equals(""))
		{
			Intent intent = new Intent();
			if(maptag.equals("message"))
				intent.setClass( this,MessageListActivity.class);
			else if(maptag.equals("storeinfo"))
				intent.setClass( this,StoreViewActivity.class);
			else if(maptag.equals("pmap"))
				intent.setClass( this,PeripheryMapsActivity.class);
			else
				intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();//关闭显示的Activity
		}
		else if(advertiseNotification != null)
		{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();//关闭显示的Activity
		}
		else
		{
		    CardsView.this.setResult(RESULT_OK, getIntent());
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			CardsView.this.finish();
		}
	}
	
	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu2, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}
	
	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter2(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage","itemText" },
				new int[] { R.id.item_image,R.id.item_text });
		return simperAdapter;
	}
	
	public void showMycardview()
	{
		try{
//			TableLayout tlayout = (TableLayout)findViewById(R.id.card_info_layout);
//			
//			Resources r = this.getResources();  
//			//以数据流的方式读取资源  
//			InputStream is = r.openRawResource(R.drawable.biejing);  
//			BitmapDrawable  bmpDraw = new BitmapDrawable(is);  
//			Bitmap bmp = bmpDraw.getBitmap();  
//			
//			Bitmap bitmap = getRoundedCornerBitmap(bmp);
//			Drawable d = new BitmapDrawable(bitmap);
//			tlayout.setBackgroundDrawable(d);
			
//			View view = findViewById(R.id.card_info_layout);
//			view.getBackground().setAlpha(100);
			
//			Button btn = (Button)findViewById(R.id.mylishixiaofei);
//			btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showHistoricalConsumption();
//				}
//			});
//			
//			Button btn2 = (Button)findViewById(R.id.mydiancai);
//			btn2.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showStoreMume();
//				}
//			});
//			
//			Button btn3 = (Button)findViewById(R.id.my_coupon_btn);
//			btn3.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showCouponView();
//				}
//			});
//			
//			Button btn4 = (Button)findViewById(R.id.my_card_tiao_no);
//			btn4.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showCardTiaoXinMa();
//				}
//			});
//			
//			Button btn5 = (Button)findViewById(R.id.points_management_btn);
//			btn5.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showPointsManagement();
//				}
//			});
			
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			final Map map = list.get(Integer.valueOf(index));
			
			Integer points = (Integer)map.get("points");
			String nameOnCard = (String)map.get("nameOnCard");
			String nameid = (String)map.get("nameid");
			String pfids = (String)map.get("pfids");
			String cardno = (String)map.get("cardNo");
			String joineDate = (String)map.get("joinedDate");
			String mdmType = (String)map.get("mdmType");
			String mdmlevel = (String)map.get("mdmLevel");
			String mdmstatus = (String)map.get("mdmstatus");
			String expdate = (String)map.get("expDate");
			String chainCode = (String)map.get("chainCode");
			String storeName = (String)map.get("storeName");
			Drawable img = (Drawable)map.get("img");
			Bitmap bitimg = (Bitmap)map.get("img2");
			isASttention = (String)map.get("isASttention");
			final String pkid = (String)map.get("pkid");
			String storePhone = (String)map.get("storePhone");
			String address = (String)map.get("addressInfomation");
			String storeDesc = (String)map.get("storeDesc");
			final String businessId = (String)map.get("businessId");
			String score = (String)map.get("score");
			
			RatingBar RatingBar01 = (RatingBar)findViewById(R.id.RatingBar01);
			RatingBar01.setRating(Float.valueOf(score));
			
			RoundAngleImageView imgview = (RoundAngleImageView)findViewById(R.id.ImageView01);
			
//			ImageView imgview = (ImageView)findViewById(R.id.ImageView01);
//			imgview.setImageBitmap(bitimg);
			if(StreamTool.isNetworkVailable(this))
			{
				if(map.get("imgurl") instanceof String)
				{
					String imgurl = (String)map.get("imgurl");
					loadThreadData(imgurl);
				}
				else
				{
					Bitmap imgbitmap = (Bitmap)map.get("imgurl");
					imgview.setImageBitmap(imgbitmap);
				}
			}
			else
			{
				if(map.get("imgurl") instanceof Bitmap)
				{
					Bitmap imgbitmap = (Bitmap)map.get("imgurl");
					imgview.setImageBitmap(imgbitmap);
				}
			}
			
			
			TextView tview = (TextView)findViewById(R.id.TextView02);
			tview.setText(storeName);
			
			TextView tview2 = (TextView)findViewById(R.id.TextView13);
			tview2.setText(this.getString(R.string.cards_lable_7)+"："+cardno);
			
			TextView tview3 = (TextView)findViewById(R.id.TextView10);
			tview3.setText(address);
			
			TextView tview4 = (TextView)findViewById(R.id.TextView03);
			tview4.setText(this.getString(R.string.cards_lable_8)+"："+String.valueOf(points));
			
			TextView tview5 = (TextView)findViewById(R.id.TextView11);
			tview5.setText(storePhone);
			
			TextView tview6 = (TextView)findViewById(R.id.vip_live);
			tview6.setText(this.getString(R.string.cards_lable_9)+"："+mdmlevel);
			
			TextView tview7 = (TextView)findViewById(R.id.TextView12);
			tview7.setText(this.getString(R.string.cards_lable_10)+"："+storeDesc);
			
			ImageButton otherStore = (ImageButton)findViewById(R.id.sel_other_store);
			otherStore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showOtherStore(businessId);
				}
			});
//			
//			TextView tview8 = (TextView)findViewById(R.id.exp_Date);
//			tview8.setText(expdate);
//			
//			TextView tview9 = (TextView)findViewById(R.id.chain_Code);
//			tview9.setText(chainCode);
//			
//			TextView tview10 = (TextView)findViewById(R.id.name_On_Card);
//			tview10.setText(nameOnCard);
			
			final Drawable drawable = this.getResources().getDrawable(R.drawable.ic_fave_sel);
			final Drawable drawable2 = this.getResources().getDrawable(R.drawable.ic_fave);
			
			final ToggleButton ximg = (ToggleButton)findViewById(R.id.ToggleButton01);
			if(isASttention.equals("1"))//1为否，0为是
			{
				ximg.setCompoundDrawablesWithIntrinsicBounds(drawable2, null, null, null);
			}
			else
			{
				ximg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
			}
			ximg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("isASttention=="+isASttention);
					if(StreamTool.isNetworkVailable(CardsView.this))
					{
						if(isASttention.equals("1"))//1为否，0为是
						{
							try{
								JSONObject jobj = api.isASttention(pkid,"1");
								if(jobj != null)
								{
									ximg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
									isASttention = "0";
									map.put("isASttention", isASttention);
									map.put("xinxin", R.drawable.ic_star_small);
								}
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}
						else
						{
							try{
								JSONObject jobj = api.notASttention(pkid,"1");
								if(jobj != null)
								{
									ximg.setCompoundDrawablesWithIntrinsicBounds(drawable2, null, null, null);
									isASttention = "1";
									map.put("isASttention", isASttention);
									map.put("xinxin", null);
								}
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}
					}
					else
					{
						makeText(CardsView.this.getString(R.string.seting_lable_7));
					}
				}
			});
			
			
			Button tiaobtn = (Button)findViewById(R.id.Button02);
			tiaobtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCardTiaoXinMa();
				}
			});
			
			LinearLayout phoneLayout = (LinearLayout)findViewById(R.id.LinearLayout13);
			phoneLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView tview = (TextView)findViewById(R.id.TextView11);
					String phoneNumber = tview.getText().toString();
					
					phoneNumber = phoneNumber.replaceAll("-", "");
					
					if(isPhoneNumberValid(phoneNumber))
					{
//						 Intent myIntentDial = new Intent( Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));  
//			             
//			             startActivity(myIntentDial);  
						Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber));
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			            startActivity(intent);
					}
					else
					{
						makeText(CardsView.this.getString(R.string.cards_lable_11));
					}
				}
			});
			
			LinearLayout addressLayout = (LinearLayout) findViewById(R.id.LinearLayout12);
			addressLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					showProgressDialog();
					showStopAddress();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadData(final String imgurl)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				Bitmap bitmap = getImageBitmap(imgurl);
				msg.obj = bitmap;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Bitmap bitimg = (Bitmap)msg.obj;
				RoundAngleImageView imgview = (RoundAngleImageView)CardsView.this.findViewById(R.id.ImageView01);
				imgview.setImageBitmap(bitimg);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void showStopAddress()
	{
		try{
			showProgressDialog();
			Intent intent = new Intent();
//		    intent.setClass( this,LocalMap.class);
			intent.setClass( this,BaiduMapRouteSearch.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    mypDialog.dismiss();
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showOtherStore(String businessId)
	{
		try{
			showProgressDialog();
			Intent intent = new Intent();
		    intent.setClass( this,OtherStore.class);
		    Bundle bundle = new Bundle();
			bundle.putString("businessId",businessId);
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    mypDialog.dismiss();
		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadSensor()
	{
		try{
			//传感器管理对象
			sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			//传感器
			acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			//传感器侦听
			sl = new SensorEventListener()
			{
			    @Override
			    public void onSensorChanged(SensorEvent event)
			    {
			        //传感器获取值发生改变，在此处理
			    	 if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){//只捕获加速度传
			    		 float x = event.values[0];  //手机横向翻滚
			    		 float y = event.values[1];  //手机纵向翻滚
			    		 float z = event.values[2];  //屏幕向上，上下移动
		
			    		 //获取当前时刻的毫秒数
			    		 curTime = System.currentTimeMillis();
			    		 
			    		//100毫秒检测一次
						if ((curTime - lastTime) > 100) {

							duration = (curTime - lastTime);
							lastTime = curTime; 

							float speed = Math.abs(x+y+z - last_x - last_y - last_z) / duration * 10000;   			  
//							System.out.println("speed=="+speed + "duration=="+duration);
							if (speed > SHAKE_THRESHOLD) {   
//								System.out.println("x方向上的加速度为：" + x);
//								System.out.println("y方向上的加速度为：" + y);
//								System.out.println("z方向上的加速度为：" + z);
								nextCard(x,y);                      //检测到摇晃后执行的代码
							}  
							last_x = x;   
							last_y = y;   
							last_z = z;   	

//							System.out.println("总体晃动幅度=" + totalShake
//									+ "\n平均晃动幅度=" + totalShake
//									/ (curTime - initTime) * 1000);
						}

			    	 }
			    }
			    @Override
			    public void onAccuracyChanged(Sensor sensor, int accuracy)
			    {
			      //精度发生改变时在此做调整
			    }
			  };

			//定义好之后，需要注册侦听！
			sm.registerListener(sl, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void nextCard(float x,float y)
	{
		try{
			flsttime = System.currentTimeMillis();
			if((flsttime - lasttime) > 2000)
			{
				System.out.println("index==="+index);
				int tag = (int)x;
				if(tag < 0)
				{
						if(index == mapcontext - 1)
						{
							makeText(this.getString(R.string.cards_lable_12));
						}
						else
						{
							index = index + 1;
//							System.out.println("右边index==="+index);
//							showMycardview();
							Intent intent = new Intent();
						    intent.setClass( this,CardsView.class);
						    Bundle bundle = new Bundle();
							bundle.putString("index", String.valueOf(index));
							bundle.putInt("randomNumber", randomNumber);
							intent.putExtras(bundle);
						    startActivity(intent);//开始界面的跳转函数
						    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
						    this.finish();
						}
				}
				else
				{
						if(index == 0)
						{
							makeText(this.getString(R.string.cards_lable_13));
						}
						else
						{
							index = index - 1;
//							System.out.println("左边index==="+index);
//							showMycardview();
							Intent intent = new Intent();
						    intent.setClass( this,CardsView.class);
						    Bundle bundle = new Bundle();
							bundle.putString("index", String.valueOf(index));
							bundle.putInt("randomNumber", randomNumber);
							intent.putExtras(bundle);
						    startActivity(intent);//开始界面的跳转函数
						    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
						    this.finish();
						}
				}
				lasttime = flsttime;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() { // 重写的onResume方法
		sm.registerListener(sl, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}
	
	 @Override  
	protected void onPause() { // 重写onPause方法
		sm.unregisterListener(sl); // 取消注册监听器
		super.onPause();
	}
	 
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	public void showPointsManagement()
	{
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			AlertDialog alertDialog = null;
			final View view = LayoutInflater.from(this).inflate(R.layout.points_management_view,null);
			
			final Spinner spinner  = (Spinner)view.findViewById(R.id.Spinner02);
			
			//将可选内容与ArrayAdapter连接起来  
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
	          
	        //设置下拉列表的风格  
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	          
	        //将adapter 添加到spinner中  
	        spinner.setAdapter(adapter);  
	          
	        //添加事件Spinner事件监听    
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
//					tv.setText("你选择的是："+m[arg2]);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
	        	
			});  
	          
	        //设置默认值  
	        spinner.setVisibility(View.VISIBLE);  
			
			builder.setTitle(this.getString(R.string.cards_lable_14)).setView(view).setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showMark(CardsView.this.getString(R.string.cards_lable_15)+"："+m[spinner.getSelectedItemPosition()]);
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			alertDialog = builder.create();
			alertDialog.getWindow().setLayout(400, LayoutParams.WRAP_CONTENT); //设置宽和高
			alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMark(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showCardTiaoXinMa(){
		try{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			Map map = list.get(Integer.valueOf(index));
		
			String cardNo = (String)map.get("cardNo");
			View view = LayoutInflater.from(this).inflate(R.layout.card_tiaoxinma_view,null);
			
			ImageView imgview = (ImageView)view.findViewById(R.id.tiaoma_img);
			if(StreamTool.isNetworkVailable(this))
				imgview.setImageBitmap(getImageBitmap("http://"+myapp.getHost()+":80/customize/control/BarcodeServlet?msg="+cardNo));
			else
			{
				Bitmap bitm = (Bitmap)map.get("tiaoimg");
				imgview.setImageBitmap(bitm);
			}
			
			new AlertDialog.Builder(this).setTitle(this.getString(R.string.cards_lable_16)).setView(view).setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
//		    opt.inSampleSize = 8;
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 我的历史消费账单
	 */
	public void showHistoricalConsumption()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyBillList.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("appliescStoreid", appliescStoreid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 我的积分历史
	 */
	public void showIntegralHistory()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,GetIntegralHistoryActity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 我的积分换物
	 */
	public void showIntegralType()
	{
//		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,GetIntegralTypeActivity.class);
//		    Bundle bundle = new Bundle();
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
	}
	
	/**
	 * 该门店下的菜单
	 */
	public void showStoreMume()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductTypeActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 我的购物车
	 */
	public void showShoppingCart()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductShoppingCartActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			bundle.putString("storeid", appliescStoreid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 该门店下的旅游景点介绍
	 */
	public void showAttractionsPage()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,TravelActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 搜索该景点附近设施
	 */
	public void showTypeMapSearch(String type)
	{
		try{
			mypDialog = ProgressDialog.show(this, null, this.getString(R.string.cards_lable_17), true,
	                false);
			
			JSONObject job = api.getStoreTypeAll(type);
			String typeid = "";
			if(job != null)
			{
				JSONArray jArr = (JSONArray) job.get("data");
				for (int i = 0; i < jArr.length(); i++) {
					JSONObject jobs = (JSONObject)jArr.get(i);
					typeid = (String) jobs.get("id");
				}
			}
			Intent intent = new Intent();
//		    intent.setClass( this,PeripheryFacilitySearchActivity.class);
			intent.setClass( this,BaiduMapTravel.class);
		    Bundle bundle = new Bundle();
		    bundle.putInt("index", index);
			bundle.putString("type", typeid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    if(mypDialog != null)
				mypDialog.dismiss();
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 旅游路线攻略
	 */
	public void showReommendedRoute()
	{
		try{
			List<Map<String,Object>> lists = myapp.getMyCardsAll();
			Map map = lists.get(index);
			
			String storeid = (String)map.get("storeid");
			String storeName = (String)map.get("storeName");
			String storeImg = (String)map.get("imgurl");
			String storeDesc = (String)map.get("storeDesc");
			
			Intent intent = new Intent();
//		    intent.setClass( this,RecommendedRouteActivity.class);
			intent.setClass( this,BaiduMapTravel.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("landid", storeid);
		    bundle.putString("storeName", storeName);
		    bundle.putString("storeImg", storeImg);
		    bundle.putString("storeDesc", storeDesc);
		    bundle.putInt("sindex", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示下载优惠券界面
	 */
	public void showDownloadCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag", "download");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示我的优惠券界面
	 */
	public void showMyCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag", "my");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示我在该店的订单状态
	 */
	public void showOrderStatus()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyOrderStatus.class);
			intent.setClass( this,MyOrederListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("appliescStoreid", appliescStoreid);
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 拨打预定电话
	 */
	public void showPhoneBook()
	{
		try{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			Map map = list.get(Integer.valueOf(index));
			
			String storePhone = (String)map.get("storePhone");
			storePhone = storePhone.replaceAll("-", "");
			
			if(isPhoneNumberValid(storePhone))
			{
//				 Intent myIntentDial = new Intent( Intent.ACTION_CALL,Uri.parse("tel:"+storePhone));  
//	             
//	             startActivity(myIntentDial);  
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+storePhone));
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	            startActivity(intent);
			}
			else
			{
				makeText(this.getString(R.string.cards_lable_11));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示在线预定页面
	 */
	public void showOnlineReservationsView()
	{
		try{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			Map map = list.get(Integer.valueOf(index));
		
			String typesMapping = (String)map.get("typesMapping");
			if(typesMapping == null)
				typesMapping = "01";
			String typeName = (String)map.get("typeName");
			if(typesMapping.equals("09"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,HotelReservation.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			}
			else if(typesMapping.equals("10"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,RestaurantDestine.class);
			    Bundle bundle = new Bundle();
				bundle.putInt("index", index);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			}
			else
			{
				makeText(typeName+this.getString(R.string.cards_lable_18));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示我的预定页面
	 */
	public void showMyReservationsView()
	{
		try{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			Map map = list.get(Integer.valueOf(index));
		
			String typesMapping = (String)map.get("typesMapping");
			if(typesMapping == null)
				typesMapping = "01";
			String typeName = (String)map.get("typeName");
			if(typesMapping.equals("09"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,MyHotelReservation.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			}
			else
			{
				makeText(typeName+this.getString(R.string.cards_lable_18));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示优惠券界面
	 */
	public void showCouponView()
	{
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			AlertDialog alertDialog = null;
			final View view = LayoutInflater.from(this).inflate(R.layout.points_management_view,null);
			
			final Spinner spinner  = (Spinner)view.findViewById(R.id.Spinner02);
			
			//将可选内容与ArrayAdapter连接起来  
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m2);
	          
	        //设置下拉列表的风格  
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	          
	        //将adapter 添加到spinner中  
	        spinner.setAdapter(adapter);  
	          
	        //添加事件Spinner事件监听    
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
//					tv.setText("你选择的是："+m[arg2]);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
	        	
			});  
	          
	        //设置默认值  
	        spinner.setVisibility(View.VISIBLE);  
			
			builder.setTitle(this.getString(R.string.cards_lable_19)).setView(view).setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(spinner.getSelectedItemPosition() == 0)
							{
								if(StreamTool.isNetworkVailable(CardsView.this))
									showDownloadCouponView();
								else
									makeText(CardsView.this.getString(R.string.seting_lable_7));
							}
							else
							{
								//使用优惠券
								showMyCouponView();
							}
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			alertDialog = builder.create();
			alertDialog.getWindow().setLayout(400, LayoutParams.WRAP_CONTENT); //设置宽和高
			alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showCardDetails()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass(this,UserInfoDetailed.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showTrafficView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass(this,GetTicketsInfomationActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private class MyWebViewClient extends WebViewClient { 
    	@Override 
    	// 在WebView中显示页面,而不是默认浏览器中显示页面 
    	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
    	view.loadUrl(url); 
    	return true; 
    	} 
    }
	
	public void showComment()
	{
		try{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			Map map = list.get(Integer.valueOf(index));
			
			String storeid = (String)map.get("storeid");
			String storeName = (String)map.get("storeName");
			
			Intent intent = new Intent();
		    intent.setClass( this,MyCommentView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", storeid);
			bundle.putString("storeName", storeName);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void bindingSinaWeibo()
	{
//		Intent intent = new Intent();
//	    intent.setClass( this,AndroidSinaExample.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "sina");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingRenren()
	{
//		Intent intent = new Intent();
//	    intent.setClass( this,OAuthRenRen.class);
//	    Bundle bundle = new Bundle();
////		bundle.putString("tag", "sina");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingQQ()
	{
//		//初始化ConfigUtil信息
//		ConfigUtil conf = ConfigUtil.getInstance();
//		String curWeibo = String.valueOf(ConfigUtil.QQW);
//		conf.setCurWeibo(curWeibo);
//		conf.initQqData();
//		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "qq");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingSh()
	{
		//初始化ConfigUtil信息
//		ConfigUtil conf = ConfigUtil.getInstance();
//		String curWeibo = String.valueOf(ConfigUtil.SOHUW);
//		conf.setCurWeibo(curWeibo);
//		conf.initSohuData();
//		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "sh");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingKx()
	{
//		//初始化ConfigUtil信息
//		Kaixin kaixin = Kaixin.getInstance();
//		String[] permissions = {"basic",
//				"friends_records",
//				"create_records", 
//				"user_records",
//				"create_rgroup",
//				"user_rgroup",
//				"create_talk",
//				"create_repaste",
//				"user_repaste",
//				"friends_repaste",
//				"create_album",
//				"user_photo",
//				"friends_photo",
//				"upload_photo",
//				"send_message",
//				"user_messagebox",
//				"user_birthday",
//				"friends_birthday",
//				"user_bodyform",
//				"friends_bodyform",
//				"user_blood",
//				"friends_blood",
//				"user_marriage",
//				"friends_marriage",
//				"user_intro",
//				"friends_intro",
//				"user_education",
//				"friends_education",
//				"user_career",
//				"friends_career"
//				};
//		String url = "";
//		try {
//			if (kaixin.getRequestToken(CardsView.this,
//			"http://www.17k.com", permissions)) {
//				url = Kaixin.KX_AUTHORIZE_URL
//				+ "?oauth_token=" + kaixin.getRequestToken()
//				+ "&oauth_client=1";
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "kx");
//		bundle.putString("url", url);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingwy()
	{
//		//初始化ConfigUtil信息
//		ConfigUtil conf = ConfigUtil.getInstance();
//		String curWeibo = String.valueOf(ConfigUtil.WANGYIW);
//		conf.setCurWeibo(curWeibo);
//		conf.initWangyiData();
//		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "wy");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	@Override
	public void onRestart() {
	// TODO Auto-generated method stub
		try{
			weiboBinding();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		super.onPause();
	}
	
	public void weiboBinding()
	{
//		ImageView img = (ImageView)findViewById(R.id.image_sina);
//		if(share.getBoolean("isBindingSina",false))
//		{
//			img.setImageResource(R.drawable.ic_sina_press_off);
//		}
//		else
//		{
//			img.setImageResource(R.drawable.ic_sina_press_on);
//		}
//		
//		ImageView img2 = (ImageView)findViewById(R.id.image_renren);
//		if(share.getBoolean("isBindingRenren",false))
//		{
//			img2.setImageResource(R.drawable.ic_renren_press_off);
//		}
//		else
//		{
//			img2.setImageResource(R.drawable.ic_renren_press_on);
//		}
//		
//		ImageView img3 = (ImageView)findViewById(R.id.image_qq);
//		if(share.getBoolean("isBindingQq",false))
//		{
//			img3.setImageResource(R.drawable.ic_tecent_press_off);
//		}
//		else
//		{
//			img3.setImageResource(R.drawable.ic_tecent_press_on);
//		}
//		
//		ImageView img4 = (ImageView)findViewById(R.id.image_kx);
//		if(share.getBoolean("isBindingkx",false))
//		{
//			img4.setImageResource(R.drawable.ic_kaixin_press_off);
//		}
//		else
//		{
//			img4.setImageResource(R.drawable.ic_kaixin_press_on);
//		}
		
//		ImageView img4 = (ImageView)findViewById(R.id.image_sh);
//		if(share.getBoolean("isBindingsh",false))
//		{
//			img4.setImageResource(R.drawable.ic_souhu_press_off);
//		}
//		else
//		{
//			img4.setImageResource(R.drawable.ic_kaixin_press_on);
//		}
//		
//		ImageView img5 = (ImageView)findViewById(R.id.image_wy);
//		if(share.getBoolean("isBindingwy",false))
//		{
//			img5.setImageResource(R.drawable.ic_wanyi_press_off);
//		}
//		else
//		{
//			img5.setImageResource(R.drawable.ic_kaixin_press_on);
//		}
	}
	
	public static boolean isPhoneNumberValid(String phoneNumber){  
        
        boolean isValid = false;  
      //-------------手机号码，以1开始，13,15,18,19,为合法，后根9位数字------
        String expression = "[1]{1}[3,5,8,6]{1}[0-9]{9}";  
      //-------------电话号码,以0开始,不含括号,不含横杠------------
        String expression2 = "[0]{1}[0-9]{2,3}[0-9]{7,8}";  
        CharSequence inputStr = phoneNumber;  
          
        Pattern pattern = Pattern.compile(expression);  
          
        Matcher matcher = pattern.matcher(inputStr);  
          
        Pattern pattern2 = Pattern.compile(expression2);  
          
        Matcher matcher2 = pattern2.matcher(inputStr);  
          
        if(matcher.matches()||matcher2.matches()){  
            isValid = true;  
        }  
          
          
        return isValid;  
          
    }  
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void updateUserPassword()
	{
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			final View view = LayoutInflater.from(this).inflate(R.layout.user_update_paw,null);
			
			
			final EditText oldpawet = (EditText)view.findViewById(R.id.oldpassword);
			
			final EditText paw = (EditText)view.findViewById(R.id.password);
			
			final EditText paw2 = (EditText)view.findViewById(R.id.password2);
			
			builder.setTitle(this.getString(R.string.seting_lable_1)).setView(view).setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showProgressDialog();
							String oldpaw = oldpawet.getText().toString();
							String pawstr = paw.getText().toString();
							String pawstr2 = paw2.getText().toString();
							boolean b = true;
							
							if(oldpaw == null || oldpaw.equals(""))
							{
								b = false;
								makeText(CardsView.this.getString(R.string.seting_lable_4));
							}
							else if(pawstr == null || pawstr.equals(""))
							{
								b = false;
								makeText(CardsView.this.getString(R.string.login_lable_10));
							}
							else if(pawstr2 == null || pawstr2.equals(""))
							{
								b = false;
								makeText(CardsView.this.getString(R.string.login_lable_11));
							}
							else if(!pawstr2.equals(pawstr))
							{
								b = false;
								makeText(CardsView.this.getString(R.string.login_lable_12));
							}
							
							JSONObject jobj;
							U.dout(0);
							
							if(b)
							{
								try {
									jobj = api.updateUserPassword(oldpaw,pawstr);
									String tag = (String)jobj.get("success");
									if(tag.equals("true"))
									{
										b = true;
										makeText(CardsView.this.getString(R.string.seting_lable_5));
									}
									else
									{
										b = false;
										String msg = (String)jobj.get("msg");
										makeText(CardsView.this.getString(R.string.seting_lable_6) + msg);
										
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									b = false;
									e.printStackTrace();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									b = false;
									e.printStackTrace();
								}
							}
							
							mypDialog.dismiss();
							if(b)
								dialog.dismiss();
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProgressDialog(){
		try{
			mypDialog=new ProgressDialog(this);
            //实例化
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置进度条风格，风格为圆形，旋转的
//            mypDialog.setTitle("等待");
            //设置ProgressDialog 标题
            mypDialog.setMessage(this.getString(R.string.login_lable_21));
            //设置ProgressDialog 提示信息
//            mypDialog.setIcon(R.drawable.wait_icon);
            //设置ProgressDialog 标题图标
//            mypDialog.setButton("",this);
            //设置ProgressDialog 的一个Button
            mypDialog.setIndeterminate(false);
            //设置ProgressDialog 的进度条是否不明确
            mypDialog.setCancelable(true);
            //设置ProgressDialog 是否可以按退回按键取消
            mypDialog.show();
            //让ProgressDialog显示
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		System.out.println("onDown==========");
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		// 一定要将触屏事件交给手势识别类去处理（自己处理会很麻烦的）
//		return mGestureDetector.onTouchEvent(event);
//	}
	
	/**  
     * 定义从右侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromRightAnimation() {  
        Animation inFromRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromRight.setDuration(500);  
        inFromRight.setInterpolator(new AccelerateInterpolator());  
        return inFromRight;  
    }  
   
    /**  
     * 定义从左侧退出的动画效果  
     * @return  
     */  
    protected Animation outToLeftAnimation() {  
        Animation outtoLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoLeft.setDuration(500);  
        outtoLeft.setInterpolator(new AccelerateInterpolator());  
        return outtoLeft;  
    }  
   
    /**  
     * 定义从左侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromLeftAnimation() {  
        Animation inFromLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromLeft.setDuration(500);  
        inFromLeft.setInterpolator(new AccelerateInterpolator());  
        return inFromLeft;  
    }  
   
    /**  
     * 定义从右侧退出时的动画效果  
     * @return  
     */  
    protected Animation outToRightAnimation() {  
        Animation outtoRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoRight.setDuration(500);  
        outtoRight.setInterpolator(new AccelerateInterpolator());  
        return outtoRight;  
    } 
    
    /*  
     * 用户按下触摸屏、快速移动后松开即触发这个事件  
     * e1：第1个ACTION_DOWN MotionEvent  
     * e2：最后一个ACTION_MOVE MotionEvent  
     * velocityX：X轴上的移动速度，像素/秒  
     * velocityY：Y轴上的移动速度，像素/秒  
     * 触发条件 ：  
     * X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒  
     */
//    @Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		System.out.println("onFling==========");
//		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE  
//                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
//            // 当像左侧滑动的时候  
//            //设置View进入屏幕时候使用的动画  
//			mViewFlipper.setInAnimation(inFromRightAnimation());  
//            //设置View退出屏幕时候使用的动画  
//			mViewFlipper.setOutAnimation(outToLeftAnimation());  
//			mViewFlipper.showNext();  
//        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
//                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
//            // 当像右侧滑动的时候  
//        	mViewFlipper.setInAnimation(inFromLeftAnimation());  
//        	mViewFlipper.setOutAnimation(outToRightAnimation());  
//        	mViewFlipper.showPrevious();  
//        }  
//		return true;
//	}
    
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//    	menuGrid.onTouchEvent(ev);
//        mGestureDetector.onTouchEvent(ev);
//        scrollview.onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
//
//    }
    public class CommonGestureListener extends SimpleOnGestureListener {
    	 
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onDown...");
			return false;
		}
 
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onShowPress...");
			super.onShowPress(e);
		}
 
		@Override
	    public void onLongPress(MotionEvent e) {
	        // TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "----> Jieqi: do onLongPress...");
	    }
 
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapConfirmed...");
			return false;
		}
 
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapUp...");
			return false;
		}
 
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY){
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onFling...");
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            // 当像左侧滑动的时候  
	            //设置View进入屏幕时候使用的动画  
				mViewFlipper.setInAnimation(inFromRightAnimation());  
	            //设置View退出屏幕时候使用的动画  
				mViewFlipper.setOutAnimation(outToLeftAnimation());
//				selectLisgBg("next");
				if(layoutindex == 0)
					layoutindex++;
				else
				{
					if (layoutindex % (mViewFlipper.getChildCount()-1) == 0) 
						layoutindex = 0;
					else
						layoutindex++;
				}
				pageControl.generatePageControl(layoutindex);
				mViewFlipper.showNext(); 
	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            // 当像右侧滑动的时候  
	        	mViewFlipper.setInAnimation(inFromLeftAnimation());  
	        	mViewFlipper.setOutAnimation(outToRightAnimation());  
//	        	selectLisgBg("previous");
	        	if(layoutindex == 0)
	        	{
	        		layoutindex = mViewFlipper.getChildCount()-1;
	        	}
	        	else
	        	{
					layoutindex--;
	        	}
	        	pageControl.generatePageControl(layoutindex);
	        	mViewFlipper.showPrevious();  
	        }  
			return true;
		}
		
//		public void selectLisgBg(String tag)
//		{
//			switch (layoutindex) {
//				case 1:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout1);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout2);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex = 7;
//						ImageView img = (ImageView)findViewById(R.id.img_layout1);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout7);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 2:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout2);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout3);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout2);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout1);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 3:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout3);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout4);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout3);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout2);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 4:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout4);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout5);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout4);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout3);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 5:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout5);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout6);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout5);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout4);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 6:
//					if(tag.equals("next"))
//					{
//						layoutindex++;
//						ImageView img = (ImageView)findViewById(R.id.img_layout6);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout7);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout6);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout5);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				case 7:
//					if(tag.equals("next"))
//					{
//						layoutindex = 1;
//						ImageView img = (ImageView)findViewById(R.id.img_layout7);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout1);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					else
//					{
//						layoutindex--;
//						ImageView img = (ImageView)findViewById(R.id.img_layout7);
//						img.setBackgroundResource(R.drawable.account_lisg_bg);
//						ImageView img2 = (ImageView)findViewById(R.id.img_layout6);
//						img2.setBackgroundResource(R.drawable.account_lisg_bg_sel);
//					}
//					break;
//				default:
//					break;
//			}
//		}
 
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onScroll...");
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
 
    }
}
