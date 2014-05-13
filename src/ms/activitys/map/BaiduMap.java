package ms.activitys.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.ContactActivity;
import ms.activitys.hotel.Exit;
import ms.activitys.hotel.HotelActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.activitys.hotel.HotelServiceActivity;
import ms.activitys.hotel.StoreMainActivity;
import ms.activitys.hotel.StoreViewActivity;
import ms.activitys.map.Panel.OnPanelListener;
import ms.activitys.vipcards.CardsView;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.PinyinComparator;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaiduMap extends Activity implements OnPanelListener,LocationListener {
	private SharedPreferences share;
	private Douban api;
	private MyApp myapp;

	static MapView mMapView = null;
	private MapController mMapController = null;
	public MKMapViewListener mMapListener = null;
	FrameLayout mMapViewContainer = null;

	EditText indexText = null;
	MyLocationOverlay myLocationOverlay = null;
	int index = 0;
	LocationData locData = null;
	public static BaiduMap instance;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Toast.makeText(BaiduMap.this, "msg:" + msg.what, Toast.LENGTH_SHORT)
					.show();
		};
	};

	private MyOverlay itemOverlay;

	private String flocation = "";
	private String filterContent = "";
	private String typeid = "";
	private String distance = "All";
	private String score = "All";

	private FrameLayout flayout;
	private TextView textView;
	private ImageView timg;
	private ImageView upimg;
	private boolean isOpen = true;
	private boolean istypeOpen = false;
	private Panel panel;
	private Panel typepanel;

	private String menutag = "map";// 展示类型，地图map;列表list
	private ViewFlipper mViewFlipper;

	private Rotate3d leftAnimation;
	private Rotate3d rightAnimation;
	private int mCenterX = 160;
	private int mCenterY = 0;

	private ListView clistView;// 列表展示
	private ProgressBar pb;

	private static List<Map<String, Object>> ctypelist;// 类型列表
	
	private ProgressDialog mypDialog;
	private String idex;
	private ImageView storeimgview;
	private Dialog myDialogs;
	private DBHelperMessage db;
	private BMapManager mBMapManager = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myapp = (MyApp) this.getApplication();
//		if (myapp.getmBMapManager() == null) {
//			myapp.setmBMapManager(new BMapManager(this));
//			myapp.getmBMapManager().init(MyApp.strKey,
//					new MyApp.MyGeneralListener());
//		}
		
		// 如果使用地图SDK，请初始化地图Activity
		setContentView(R.layout.map_baidu);
		
		mBMapManager = myapp.getmBMapManager();
		
		share = BaiduMap.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		
		instance = this;

		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		pb = (ProgressBar)findViewById(R.id.probar);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		
		db = new DBHelperMessage(this, myapp);

		Button break_btn = (Button)findViewById(R.id.home);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMainView();
			}
		});
		
		initMapView();
		init();
//		initSearchBar();
		initSearchPanel();
		initListView();
	}

	private void initMapView() {
		mMapView.setLongClickable(true);
	}

	private void initListView() {
		clistView = (ListView) findViewById(R.id.ListView_cards);
		clistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
//				mypDialog = ProgressDialog.show(BaiduMap.this, null, BaiduMap.this.getString(R.string.map_lable_11), true,
//		                true);
				Map map = myapp.getStorelist().get(arg2);
				makeOnClick((String)map.get("pkid"));
			}
			
		});
	}

	private void init() {
		System.out.println("百度地图初始化");

		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);

		mMapListener = new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				GeoPoint cPoint = mMapView.getMapCenter();
				myapp.setCenterLat(String.valueOf(cPoint.getLatitudeE6() / 1e6));
				myapp.setCenterLng(String.valueOf(cPoint.getLongitudeE6() / 1e6));

				System.out.println("移动后，中心坐标：lat==" + myapp.getCenterLat());
				System.out.println("移动后，中心坐标：lon==" + myapp.getCenterLng());

				// 获取坐标集合
//				loadThreadData(null, null, null, null, null);
				
				if(typeid.equals("") && distance.equals("All") && filterContent.equals("") && score.equals("All") && flocation.equals(""))
				{
//					GeoPoint gpcenter = mapView.getMapCenter();
//					double lat = gpcenter.getLatitudeE6() / 1E6;
//					double lng = gpcenter.getLongitudeE6() / 1E6;
//					myapp.setCenterLat(String.valueOf(lat));
//					myapp.setCenterLng(String.valueOf(lng));
					myapp.setMapZoom(mMapView.getZoomLevel());
					int latspan = mMapView.getLatitudeSpan();
					int longspan = mMapView.getLongitudeSpan();
					myapp.setLatspan(latspan);
					myapp.setLongspan(longspan);
					loadThreadData(null,null,null,null,null);
//					spend = 0;
//					showGoogleMap();
				}
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					Toast.makeText(BaiduMap.this, title, Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {

			}

			@Override
			public void onMapAnimationFinish() {

			}
		};
		mMapView.regMapViewListener(myapp.getmBMapManager(), mMapListener);

		initMapData();
	}

	private void initMapData() {
		// 定位中心坐标
		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
		locData.latitude = Double.parseDouble(myapp.getLat());
		locData.longitude = Double.parseDouble(myapp.getLng());
		myLocationOverlay.setData(locData);
		myLocationOverlay.enableCompass();
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6));
		// mMapController.animateTo(point);
		mMapController.setCenter(point);
		mMapController.animateTo(point);

		// 获取数据
		// myapp.setMapZoom(mMapView.getZoomLevel());
		// int latspan = mMapView.getLatitudeSpan();
		// int longspan = mMapView.getLongitudeSpan();
		// myapp.setLatspan(latspan);
		// myapp.setLongspan(longspan);
		loadThreadData(null, null, null, null, null);

		// 获取类型
		loadThreadGridData();
	}

	private void initSearchBar() {
		final EditText scontent = (EditText) findViewById(R.id.search_edit_text);
		RelativeLayout searchbutton = (RelativeLayout) findViewById(R.id.search_button_container);
		searchbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				flocation = scontent.getText().toString();
				filterContent = scontent.getText().toString();
				loadThreadData(typeid, distance, filterContent, score,
						flocation);
				// if (!flocation.equals("")) {
				// loadThreadData2(typeid, distance, filterContent, score,
				// flocation);
				// } else {
				// myapp.setSlat("");
				// myapp.setSlng("");
				// flocation = "";
				// loadThreadData(typeid, distance, filterContent, score,
				// flocation);
				// showGoogleMap();
				// }
			}
		});
	}
	
	public void searchBtnClick(View v)
	{
		try{
			final EditText scontent = (EditText) findViewById(R.id.search_edit_text);
			filterContent = scontent.getText().toString();
			loadThreadData(typeid, distance, filterContent, score,
					flocation);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void initSearchPanel() {
		try {
			flayout = (FrameLayout) findViewById(R.id.details);
			textView = (TextView) findViewById(R.id.zg_lay);

			upimg = (ImageView) findViewById(R.id.up_arrow_filter_hotels);
			panel = (Panel) findViewById(R.id.bottomPanel);
			panel.setOnPanelListener(this);
			typepanel = (Panel) findViewById(R.id.type_panel);
			typepanel.setOnPanelListener(this);

			RelativeLayout filter_button = (RelativeLayout) findViewById(R.id.filter_button_layout);
			filter_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					istypeOpen = typepanel.isOpen();
					if (istypeOpen) {
						typepanel.setOpen(false, true);
					}
					panelOpen(panel);
				}
			});

			// typelayout = (LinearLayout)findViewById(R.id.sort_layout);
			RelativeLayout typebuttonlayout = (RelativeLayout) findViewById(R.id.sort_button_layout);
			timg = (ImageView) findViewById(R.id.up_arrow_sort_hotels);
			typebuttonlayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isOpen = panel.isOpen();
					if (isOpen) {
						panel.setOpen(false, true);
					}
					panelOpen2(typepanel);
				}
			});

			final EditText fcontent = (EditText) findViewById(R.id.filter_hotel_name_edit_text);
			fcontent.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus) {
						filterContent = fcontent.getText().toString();
						loadThreadData(typeid, distance, filterContent, score,
								flocation);
						// showGoogleMap();
						// loadSelectCardsList();
					}
				}
			});

			RadioGroup mgroup = (RadioGroup) findViewById(R.id.radius_filter_button_group);
			mgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					if (checkedId == R.id.radius_small_button) {
						distance = "2000";
					} else if (checkedId == R.id.radius_medium_button) {
						distance = "5000";
					} else if (checkedId == R.id.radius_large_button) {
						distance = "15000";
					} else if (checkedId == R.id.radius_all_button) {
						distance = "All";
					}
					loadThreadData(typeid, distance, filterContent, score,
							flocation);
					// showGoogleMap();
					// loadSelectCardsList();
				}
			});

			RadioGroup rgroup = (RadioGroup) findViewById(R.id.rating_filter_button_group);
			rgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					if (checkedId == R.id.rating_low_button) {
						score = "3";
					} else if (checkedId == R.id.rating_medium_button) {
						score = "4";
					} else if (checkedId == R.id.rating_high_button) {
						score = "5";
					} else if (checkedId == R.id.rating_all_button) {
						score = "All";
					}
					loadThreadData(typeid, distance, filterContent, score,
							flocation);
					// showGoogleMap();
					// loadSelectCardsList();
				}
			});

//			final EditText scontent = (EditText) findViewById(R.id.search_edit_text);
//			RelativeLayout searchbutton = (RelativeLayout) findViewById(R.id.search_button_container);
//			searchbutton.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					flocation = scontent.getText().toString();
//					// if (!flocation.equals("")) {
//					// loadThreadData2(typeid, distance, filterContent, score,
//					// flocation);
//					// } else {
//					// myapp.setSlat("");
//					// myapp.setSlng("");
//					// flocation = "";
//					// loadThreadData(typeid, distance, filterContent, score,
//					// flocation);
//					// showGoogleMap();
//					// }
//				}
//			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		final ImageButton viewbtn = (ImageButton) findViewById(R.id.view_button);
		viewbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (menutag.equals("map")) {
					menutag = "list";
					initFirst();
					viewbtn.setImageResource(R.drawable.btn_actionbar_map);
					mViewFlipper
							.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
					// mViewFlipper.setAnimation(rightAnimation);
					// mViewFlipper.setInAnimation(rightAnimation);
					// mViewFlipper.setOutAnimation(leftAnimation);
					Animation myAnimation = AnimationUtils.loadAnimation(
							BaiduMap.this, R.anim.scale_rotate);
					//mViewFlipper.setAnimation(myAnimation);
					mViewFlipper.showNext();
				} else {
					menutag = "map";
					initSecond();
					viewbtn.setImageResource(R.drawable.btn_actionbar_list);
					mViewFlipper
							.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
					// mViewFlipper.setAnimation(leftAnimation);
					// mViewFlipper.setInAnimation(leftAnimation);
					// mViewFlipper.setOutAnimation(rightAnimation);
					Animation myAnimation = AnimationUtils.loadAnimation(
							BaiduMap.this, R.anim.scale_rotate);
					//mViewFlipper.setAnimation(myAnimation);
					mViewFlipper.showPrevious();
				}

			}
		});
	}

	public void initFirst() {
		leftAnimation = new Rotate3d(0, -90, 0.0f, 0.0f, mCenterX, mCenterY);
		rightAnimation = new Rotate3d(90, 0, 0.0f, 0.0f, mCenterX, mCenterY);
		leftAnimation.setFillAfter(true);
		leftAnimation.setDuration(1000);
		rightAnimation.setFillAfter(true);
		rightAnimation.setDuration(1000);
	}

	public void initSecond() {
		leftAnimation = new Rotate3d(-90, 0, 0.0f, 0.0f, mCenterX, mCenterY);
		rightAnimation = new Rotate3d(0, 90, 0.0f, 0.0f, mCenterX, mCenterY);
		leftAnimation.setFillAfter(true);
		leftAnimation.setDuration(1000);
		rightAnimation.setFillAfter(true);
		rightAnimation.setDuration(1000);
	}

	public void panelOpen(Panel panel) {
		try {
			isOpen = panel.isOpen();
			if (!isOpen) {
				RotateAnimation rotateAnimation = new RotateAnimation(0, 180,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				rotateAnimation.setFillAfter(true);
				rotateAnimation.setFillBefore(false);
				// 第一个参数fromDegrees为动画起始时的旋转角度
				// 第二个参数toDegrees 为动画旋转到的角度
				// 第三个参数pivotXType 为动画在X 轴相对于物件位置类型
				// 第四个参数pivotXValue 为动画相对于物件的X 坐标的开始位置
				// 第五个参数pivotXType 为动画在Y 轴相对于物件位置类型
				// 第六个参数pivotYValue 为动画相对于物件的Y 坐标的开始位置
				rotateAnimation.setDuration(500);
				upimg.startAnimation(rotateAnimation);
				panel.setVisibility(View.VISIBLE);
				panel.setOpen(true, true);
				// flayout.addView(textView);
				// isOpen = false;
			} else {
				panel.setOpen(false, true);
				// isOpen = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void panelOpen2(Panel typepanel) {
		try {
			istypeOpen = typepanel.isOpen();
			if (!istypeOpen) {
				RotateAnimation rotateAnimation = new RotateAnimation(0, 180,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				rotateAnimation.setFillAfter(true);
				rotateAnimation.setFillBefore(false);
				rotateAnimation.setDuration(500);
				timg.startAnimation(rotateAnimation);
				typepanel.setVisibility(View.VISIBLE);
				typepanel.setOpen(true, true);
			} else {
				typepanel.setOpen(false, true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		System.out.println("百度地图 onPause");
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		System.out.println("百度地图 onResume");
		mMapView.onResume();
		mMapView.refresh();
		mBMapManager.start();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		System.out.println("百度地图 onDestroy");
//		mMapView.destroy();
//		if (myapp.getmBMapManager() != null) {
//			myapp.getmBMapManager().destroy();
//			myapp.setmBMapManager(null);
//		}
		myapp.setStorelist(null); 
//		System.gc();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		System.out.println("百度地图 onSaveInstanceState");
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		System.out.println("百度地图 onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// 添加一个标注
	private void initMarkerData(double cLat, double cLon) {
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
		// 准备overlay图像数据，根据实情情况修复
		Drawable mark = getResources().getDrawable(R.drawable.map_hotel_icon);
		// 用OverlayItem准备Overlay数据
		OverlayItem item1 = new OverlayItem(p1, "item1", "item1");
		item1.setMarker(mark);

		Drawable marker = getResources().getDrawable(R.drawable.map_hotel_icon);
		// 创建IteminizedOverlay
		MyOverlay itemOverlay = new MyOverlay(marker,this,myapp,api,db);
		// 将IteminizedOverlay添加到MapView中
		// 注意：目前IteminizedOverlay不支持多实例，MapView中只能有一个IteminizedOverlay实例
		// mMapView.getOverlays().clear();
		mMapView.getOverlays().add(itemOverlay);

		// 现在所有准备工作已准备好，使用以下方法管理overlay.
		// 添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高
		itemOverlay.addItem(item1);
		mMapView.refresh();
	}

	// 添多个标注
	private void initMarkerDataAll(List<Map<String, Object>> dlist) {
		Drawable marker = getResources().getDrawable(R.drawable.map_hotel_icon);
		if (itemOverlay == null) {
			itemOverlay = new MyOverlay(marker, this,myapp,api,db);
			mMapView.getOverlays().add(itemOverlay);
		} else {
			itemOverlay.removeAll();
		}

		for (int i = 0; i < dlist.size(); i++) {
			Map map = dlist.get(i);
			String pkid = (String) map.get("pkid");
			String imgurl = (String) map.get("imageurl");
			String lons = (String) map.get("longItude");
			String lats = (String) map.get("woof");
			String imgmaping = (String) map.get("imgmaping");

			if (lats != null && !lats.equals("")) {
				GeoPoint p = new GeoPoint((int) (Double.valueOf(lats) * 1E6),
						(int) (Double.valueOf(lons) * 1E6));
				
				Drawable mark;
				if(imgmaping != null && !imgmaping.equals(""))
				{
					int imgid = getResources().getIdentifier(getPackageName()+":drawable/"+imgmaping,null,null);
					mark = getResources().getDrawable(imgid);
				}
				else
				{
					mark = getResources().getDrawable(R.drawable.map_unknown_icon);
				}
				
//				Drawable mark = getResources().getDrawable(
//						R.drawable.map_hotel_icon);
				OverlayItem item = new OverlayItem(p, pkid, pkid);
				item.setMarker(mark);

				itemOverlay.addItem(item);
			}
		}

//		mMapView.getOverlays().add(itemOverlay);
		mMapView.refresh();
	}
	
	public void makeOnClick(String pkid)
	{
		try{
			 List<Map<String,Object>> dlist = myapp.getStorelist();
		     final Map<String,Object> nmap = getMap(dlist,pkid);
		     String bid2 = (String)nmap.get("bid");  
		      
		      List<Map<String,Object>> cardlist = myapp.getMyCardsAll();
		      if(cardlist != null)
		      {
			      for(int i=0;i<cardlist.size();i++)
			      {
			        	Map<String,Object> map = cardlist.get(i);
//			        	String bid = (String)map.get("businessId");
			        	String storeid = (String)map.get("storeid");
			        	
						if(storeid != null && storeid.equals(pkid))
						{
							idex = String.valueOf(i);
							break;
						}
			      }
		      }
		      
		      if(nmap != null)
		      {
		    	  	final String pid = (String)nmap.get("pkid");
		    	  	final String imgurl = (String)nmap.get("imageurl");
		    	  	final String storeName = (String)nmap.get("storeName");
//		    	  	final String areaName = (String)nmap.get("areaName");
		    	  	final String lons = (String)nmap.get("longItude");
		    	  	final String lats = (String)nmap.get("woof");
		    	  	final String storeTyle = (String)nmap.get("storeType");
		    	  	final String areaid = (String)nmap.get("areaid");
//		    	  	final String storeId = (String)nmap.get("storeId");
//		    	  	Bitmap bimg = (Bitmap)nmap.get("bimg");
		    	  	String adress = (String)nmap.get("adress");
					String storePhone = (String)nmap.get("storePhone");
					String stroeDesc = (String)nmap.get("storeDesc");
					String bname = (String)nmap.get("bname");
					final String bid = (String)nmap.get("bid");
					final String province = (String)nmap.get("province");
					
//					final View view = LayoutInflater.from(context).inflate(R.layout.google_map_popup, null);
					final View view = LayoutInflater.from(this).inflate(R.layout.map_add_store_dialog, null);
					
					TextView title = (TextView)view.findViewById(R.id.tilte_txt);
					title.setText(storeName);
					
					String source = stroeDesc + "<br><b>"+getString(R.string.map_lable_12)+"："+ adress+"</b><br><b>"+getString(R.string.map_lable_13)+"："+storePhone+"</b>";
					TextView count = (TextView)view.findViewById(R.id.content_txt);
					count.setText(Html.fromHtml(source)); 
					
					myDialogs = new Dialog(this,R.style.MyMapDialog);
					myDialogs.setContentView(view);
					
					Window dialogWindow = myDialogs.getWindow();
					WindowManager m = getWindowManager();
			        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
					lp.width = (int) (d.getWidth() * 0.85);
					dialogWindow.setAttributes(lp);
					
					Button btn = (Button)view.findViewById(R.id.add_vip_btn);
					
					try{
						boolean tag = U.isStoreExist(pid, myapp.getMyCardsAll());
						if(tag)
						{
							btn.setText(getString(R.string.hotel_label_11));
							btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									myDialogs.dismiss();
									
									Map<String,Object> map = myapp.getMyCardsAll().get(Integer.valueOf(idex));
									getMyCardMap(map,nmap);
									
									String typesMapping = (String)map.get("typesMapping");
									System.out.println("typesMapping======"+typesMapping);
									if(typesMapping.equals("09"))
									{
										Intent intent = new Intent();
									    intent.setClass(BaiduMap.this,HotelActivity.class);
									    Bundle bundle = new Bundle();
										bundle.putInt("index", Integer.valueOf(idex));
										bundle.putString("tag", "map");
										intent.putExtras(bundle);
//										mContext.startActivity(intent);//开始界面的跳转函数
//										((Activity) mContext).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//										((Activity) mContext).finish();//关闭显示的Activity
										MainTabActivity.instance.loadLeftActivity(intent);
									}
									else
									{
										Intent intent = new Intent();
//									    intent.setClass( mContext,CardsView.class);
										intent.setClass(BaiduMap.this, StoreMainActivity.class);
									    Bundle bundle = new Bundle();
										bundle.putString("index", idex);
										bundle.putString("tag", "map");
										intent.putExtras(bundle);
//										mContext.startActivity(intent);//开始界面的跳转函数
//									    ((Activity) mContext).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//									    ((Activity) mContext).finish();//关闭显示的Activity
										MainTabActivity.instance.loadLeftActivity(intent);
									}
								}
							});
						}
						else
						{
							btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									new Thread(){
										public void run(){
											Message msg = new Message();
											msg.what = 1;
											JSONObject jobj = null;
											try {
												jobj = api.addCards(myapp.getPfprofileId(),pid,bid,storeTyle,province);
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											msg.obj = jobj;
											handler.sendMessage(msg);
										}
									}.start();
									mypDialog = ProgressDialog.show(BaiduMap.this, null, getString(R.string.map_lable_15), true,
							                false);
								}
							});
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
					
					
					Button btn2 = (Button)view.findViewById(R.id.clear_btn);
					btn2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							myDialogs.dismiss();
						}
					});
					
//					mypDialog.dismiss();
					myDialogs.show();
					
		      }
		      else
		      {
		    	  if(mypDialog != null)
						mypDialog.dismiss();
		    	  
		    	  AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		    	  dialog.setTitle((String)nmap.get("storeName"));
			      dialog.setMessage((String)nmap.get("storeDesc"));
			      dialog.show();
		      }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	static class MyOverlay extends ItemizedOverlay<OverlayItem> {
		public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Context mContext = null;
		static PopupOverlay pop = null;
		private Dialog myDialogs;
		private Button mBtn = null;
		private ImageView storeimgview;
		private ProgressDialog mypDialog;
		private MyApp myapp;
		private Douban api;
		private String idex = "0";
		private static DBHelperMessage db;

		public MyOverlay(Drawable marker, Context context,MyApp myapp,Douban api,DBHelperMessage db) {
			super(marker);
			this.mContext = context;
			pop = new PopupOverlay(BaiduMap.mMapView, new PopupClickListener() {

				@Override
				public void onClickedPopup(int index) {

				}
			});
			
			this.myapp = myapp;
			this.api = api;
			this.db = db;
			
			populate();
		}

		protected boolean onTap(int index) {
			OverlayItem item = mGeoList.get(index);
			
//		 mypDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.map_lable_11), true,
//	                false);
	      
	      String pkid = item.getSnippet();
	      BaiduMap.instance.makeOnClick(pkid);
	      return true;
		}

		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Bitmap bitm = (Bitmap) msg.obj;
					storeimgview.setImageBitmap(bitm);

					// System.out.println("窗口打开了？" + myDialogs.isShowing());
					// if (myDialogs.isShowing())
					// return;
					myDialogs.show();
					break;
				case 1:
					try {
						JSONObject jobj = (JSONObject) msg.obj;
						String success = jobj.getString("success");
						if(mypDialog != null)
							mypDialog.dismiss();
						if(success.equals("true"))
						{
							JSONObject stroeobj = jobj.getJSONObject("newstoreinfo");
							Map<String,Object> storemap = U.getNewStoreInfo(stroeobj);
							List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
							slist.add(storemap);
							db.openDB();
							db.saveStoreInfoAllData(slist);
							List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
							myapp.setMyCardsAll(storelist);
							if(ContactActivity.instance != null)
							{
//								Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());
//								ContactActivity.instance.getMyStoreListData();
								HotelServiceActivity.instance.getMyStoreListData();
							}
							db.closeDB();
							
							Toast.makeText(mContext, mContext.getString(R.string.coupon_lable_25), Toast.LENGTH_LONG).show();
							myapp.setUpdatetag("0");
							
							if(myDialogs != null)
								myDialogs.dismiss();
						}
						else
						{
							String msgstr = jobj.getString("msg");
							Toast.makeText(mContext, mContext.getString(R.string.coupon_lable_26)+msgstr, Toast.LENGTH_LONG).show();
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
					break;
				default:
					super.handleMessage(msg);
				}
			}
		};

		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				pop.hidePop();
				if (mBtn != null) {
					mMapView.removeView(mBtn);
					mBtn = null;
				}
			}
			super.onTap(pt, mapView);
			return false;
		}
		
		public Map<String,Object> getMyCardMap(Map<String,Object> map,Map<String,Object> oldmap){
			try{
					
					map.put("storeid", oldmap.get("pkid"));
					map.put("storeName", oldmap.get("storeName"));
					map.put("storePhone", oldmap.get("storePhone"));
					map.put("addressInfomation", oldmap.get("adress"));
					map.put("storeDesc", oldmap.get("storeDesc"));
					map.put("imgurl", oldmap.get("imageurl"));
					map.put("couponNumber", oldmap.get("couponnumber"));
					map.put("typeName", oldmap.get("typeName"));
					map.put("typesMapping", oldmap.get("typesMapping"));
					map.put("businessId", oldmap.get("bid"));
					map.put("woof", oldmap.get("woof"));
					map.put("longItude", oldmap.get("longItude"));
		
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return map;
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}

		public void addItem(OverlayItem item) {
			mGeoList.add(item);
			populate();
		}

		public void removeItem(int index) {
			mGeoList.remove(index);
			populate();
		}

		public void removeAll() {
			mGeoList.removeAll(mGeoList);
			populate();
		}

		public Map<String, Object> getMap(List<Map<String, Object>> dlist,
				String pkid) {
			Map<String, Object> nmap = null;
			if (dlist != null) {
				for (int i = 0; i < dlist.size(); i++) {
					Map<String, Object> map = dlist.get(i);
					String pid = (String) map.get("pkid");

					if (pid.equals(pkid)) {
						nmap = map;
						break;
					}
				}
			}
			return nmap;
		}
	}

	/************** 数据处理 ************/
	public void loadThreadData(final String typeid, final String meter,
			final String filterContent, final String score,
			final String flocation) {
		// if (menutag.equals("map"))
		// showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {
					JSONObject jobj;
					U.dout(0);

					jobj = api.getStoreAll(typeid, meter, filterContent, score,
							flocation);
					if (jobj != null) {
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getStoreList(jArr);
						myapp.setStorelist(list);
					}
					msg.obj = list;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	public void loadThreadGridData() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 4;

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {
					JSONObject job = api.getStoreTypeAll();
					if (job != null) {
						JSONArray jArr = (JSONArray) job.get("data");
						ctypelist = new ArrayList<Map<String, Object>>();
						for (int i = 0; i < jArr.length(); i++) {
							JSONObject jobs = (JSONObject) jArr.get(i);
							String value = (String) jobs.get("value");
							String id = (String) jobs.get("id");
							String imgurl = (String) jobs.get("imgurl");
							String imgtype = (String) jobs.get("imgtype");

							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", id);
							map.put("text", value);
							map.put("imgurl", imgtype);
							ctypelist.add(map);
						}
					}
					msg.obj = ctypelist;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> dlist = (List<Map<String, Object>>) msg.obj;

				if (dlist != null && dlist.size() > 0) {
					initMarkerDataAll(dlist);
					clistView.setAdapter(getAdapter(dlist));
				} else {
//					makeText(getString(R.string.task_failed_network_err));
				}
				
				pb.setVisibility(View.GONE);
				clistView.setVisibility(View.VISIBLE);
				break;
			case 1:
				try {
					JSONObject jobj = (JSONObject) msg.obj;
					String success = jobj.getString("success");
					if(mypDialog != null)
						mypDialog.dismiss();
					if(success.equals("true"))
					{
						JSONObject stroeobj = jobj.getJSONObject("newstoreinfo");
						Map<String,Object> storemap = U.getNewStoreInfo(stroeobj);
						List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
						slist.add(storemap);
						db.openDB();
						db.saveStoreInfoAllData(slist);
						List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
						myapp.setMyCardsAll(storelist);
						if(ContactActivity.instance != null)
						{
//							Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());
//							ContactActivity.instance.getMyStoreListData();
							HotelServiceActivity.instance.getMyStoreListData();
						}
						db.closeDB();
						
						makeText(getString(R.string.coupon_lable_25));
						myapp.setUpdatetag("0");
						
						if(myDialogs != null)
							myDialogs.dismiss();
					}
					else
					{
						String msgstr = jobj.getString("msg");
						makeText(getString(R.string.coupon_lable_26)+msgstr);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 4:
				if (ctypelist != null && ctypelist.size() > 0) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", "");
					map.put("text",
							BaiduMap.this.getString(R.string.map_lable_10));
					map.put("imgurl", "map_unknown_icon");
					ctypelist.add(map);

					GridView ctypeGrid = (GridView) findViewById(R.id.GridView_ctypes);
					ctypeGrid.setBackgroundResource(R.drawable.background);// 设置背景
					ctypeGrid.setNumColumns(4);// 设置每行列数
					ctypeGrid.setGravity(Gravity.CENTER);// 位置居中
					ctypeGrid.setVerticalSpacing(10);// 垂直间隔
					ctypeGrid.setHorizontalSpacing(10);// 水平间隔
					ctypeGrid.setAdapter(setGridAdapter(ctypelist));// 设置菜单Adapter
					/** 监听底部菜单选项 **/
					ctypeGrid.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Map<String, Object> map = ctypelist.get(arg2);
							typeid = (String) map.get("id");
							String name = (String) map.get("text");
							TextView typetext = (TextView) findViewById(R.id.sort_type_text_view);
							typetext.setText(name);

							loadThreadData(typeid, distance, filterContent,
									score, flocation);
						}
					});
				}
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	private SpecialAdapter getAdapter(List<Map<String,Object>> data) {
//		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
//				R.layout.select_cards_item, new String[] { "storeName","vipnumber","juli" },
//				new int[] { R.id.store_name,R.id.vip_number,R.id.vip_juli });
		SpecialAdapter listItemAdapter = new SpecialAdapter(this, data,// 数据源
				R.layout.select_cards_item,// ListItem的XML实现
				new String[] { "storeName","vipnumber","juli","imageurl" },
				new int[] { R.id.store_name,R.id.vip_number,R.id.vip_juli,R.id.img },share,"zhong");
		return listItemAdapter;
	}

	private SpecialAdapter setGridAdapter(List<Map<String, Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.item_menu2, new String[] { "imgurl", "text" },
				new int[] { R.id.item_image, R.id.item_text }, share, true,
				"ico");
		return simperAdapter;
	}

	public List<Map<String, Object>> getStoreList(JSONArray jArr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);

				String pkid = ""; // 门店ID
				if (dobj.has("storesInfoId"))
					pkid = (String) dobj.get("storesInfoId");

				String imageurl = ""; // 图片
				if (dobj.has("sysImg"))
					imageurl = (String) dobj.get("sysImg");

				String storeName = ""; // 门店名字
				if (dobj.has("storeName"))
					storeName = (String) dobj.get("storeName");

				// String areaName = ""; // 区域名字
				// if(dobj.has("areaName"))
				// areaName = (String) dobj.get("areaName");
				//
				// String areaid = ""; // 区域id
				// if(dobj.has("areaid"))
				// areaid = (String) dobj.get("areaid");

				String longItude = ""; // 经度
				if (dobj.has("longItude")) {
					longItude = (String) dobj.get("longItude");
				}

				String woof = ""; // 纬度
				if (dobj.has("woof"))
					woof = (String) dobj.get("woof");

				String storeType = ""; // 类型名字
				if (dobj.has("storeType"))
					storeType = (String) dobj.get("storeType");

				// String storeId = ""; // 类型id
				// if(dobj.has("storeId"))
				// storeId = (String) dobj.get("storeId");

				String adress = ""; // 地址
				if (dobj.has("adress"))
					adress = (String) dobj.get("adress");

				String storePhone = ""; // 电话
				if (dobj.has("storePhone"))
					storePhone = (String) dobj.get("storePhone");

				String storeDesc = ""; // 简介
				if (dobj.has("storeDesc"))
					storeDesc = (String) dobj.get("storeDesc");

				String bname = ""; // 所属商家
				if (dobj.has("bname"))
					bname = (String) dobj.get("bname");

				String bid = ""; // 所属商家ID
				if (dobj.has("bid"))
					bid = (String) dobj.get("bid");

				String province = ""; // 省份
				if (dobj.has("province"))
					province = (String) dobj.get("province");

				String city = ""; // 城市
				if (dobj.has("city"))
					city = (String) dobj.get("city");

				String vipnumber = ""; // 会员人数
				if (dobj.has("vipnumber"))
					vipnumber = (String) dobj.get("vipnumber");

				String juli = ""; // 距离
				if (dobj.has("juli"))
					juli = (String) dobj.get("juli");

				String couponnumber = ""; // 优惠券数量
				if (dobj.has("couponnumber"))
					couponnumber = (String) dobj.get("couponnumber");

				String typeName = "";
				if (dobj.has("typeName"))
					typeName = (String) dobj.get("typeName");

				String typesMapping = "";
				if (dobj.has("typesMapping"))
					typesMapping = (String) dobj.get("typesMapping");

				String imgmaping = "";
				if (dobj.has("imgmaping"))
					imgmaping = (String) dobj.get("imgmaping");

				String altitude = "0";
				if (dobj.has("altitude"))
					altitude = (String) dobj.get("altitude");

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("pkid", pkid);
				map.put("imageurl", imageurl);
				map.put("storeName", storeName);
				// map.put("areaName", areaName);
				map.put("longItude", longItude);
				// map.put("areaid", areaid);
				map.put("woof", woof);
				map.put("storeType", storeType);
				// map.put("storeId", storeId);
				// map.put("bimg", getImageBitmap(imageurl));
				map.put("adress", adress);
				map.put("storePhone", storePhone);
				map.put("storeDesc", storeDesc);
				map.put("bname", bname);
				map.put("bid", bid);
				map.put("province", province);
				map.put("city", city);
				map.put("vipnumber", vipnumber);
				map.put("juli2", juli);
				map.put("juli", this.getString(R.string.map_lable_8) + "："
						+ juli + "(" + this.getString(R.string.map_lable_9)
						+ ")");
				map.put("couponnumber", couponnumber);
				map.put("typeName", typeName);
				map.put("typesMapping", typesMapping);
				map.put("imgmaping", imgmaping);
				map.put("altitude", altitude);

				list.add(map);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
	
	public void loadPopClick(String pkid)
    {
    	try{
	      List<Map<String,Object>> dlist = myapp.getStorelist();
	      final Map<String,Object> nmap = getMap(dlist,pkid);
	      String bid2 = (String)nmap.get("bid");  
	      
	      List<Map<String,Object>> cardlist = myapp.getMyCardsAll();
	      if(cardlist != null)
	      {
		      for(int i=0;i<cardlist.size();i++)
		      {
		        	Map<String,Object> map = cardlist.get(i);
//		        	String bid = (String)map.get("businessId");
		        	String storeid = (String)map.get("storeid");
		        	
					if(storeid.equals(pkid))
					{
						idex = String.valueOf(i);
						break;
					}
		      }
	      }
	      
	      if(nmap != null)
	      {
	    	  	final String pid = (String)nmap.get("pkid");
	    	  	final String imgurl = (String)nmap.get("imageurl");
	    	  	final String storeName = (String)nmap.get("storeName");
	    	  	final String lons = (String)nmap.get("longItude");
	    	  	final String lats = (String)nmap.get("woof");
	    	  	final String storeTyle = (String)nmap.get("storeType");
	    	  	final String areaid = (String)nmap.get("areaid");
	    	  	String adress = (String)nmap.get("adress");
				String storePhone = (String)nmap.get("storePhone");
				String stroeDesc = (String)nmap.get("storeDesc");
				String bname = (String)nmap.get("bname");
				final String bid = (String)nmap.get("bid");
				final String province = (String)nmap.get("province");
				
				
				final View view = LayoutInflater.from(this).inflate(R.layout.map_add_store_dialog, null);
				
				storeimgview = (ImageView)view.findViewById(R.id.widget34);
				
				new Thread(){
					public void run(){
						Message msg = new Message();
						msg.what = 2;
						Bitmap bit = getImageBitmap(imgurl);
						msg.obj = bit;
						handler.sendMessage(msg);
					}
				}.start();
				
				TextView title = (TextView)view.findViewById(R.id.widget35);
				title.setText(storeName);
				
				String source = stroeDesc + "<br><b>"+this.getString(R.string.map_lable_12)+"："+ adress+"</b><br><b>"+this.getString(R.string.map_lable_13)+"："+storePhone+"</b>";
				TextView count = (TextView)view.findViewById(R.id.widget36);
				count.setText(Html.fromHtml(source)); 
				
				myDialogs = new Dialog(this, R.style.AliDialog);
				myDialogs.setContentView(view);
				
				Window dialogWindow = myDialogs.getWindow();
				WindowManager m = getWindowManager();
		        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				lp.width = (int) (d.getWidth() * 0.85);
				dialogWindow.setAttributes(lp);
				
				Button btn = (Button)view.findViewById(R.id.add_vip_btn);
				
				try{
					JSONObject jobj = api.isSelectCards(myapp.getPfprofileId(),pid,bid,null);
					String tag = jobj.getString("tag");
					if(tag.equals("1"))
					{
						btn.setText(this.getString(R.string.map_lable_14));
						btn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								myDialogs.dismiss();
								
								Map<String,Object> map = myapp.getMyCardsAll().get(Integer.valueOf(idex));
								getMyCardMap(map,nmap);
								
								String typesMapping = (String)map.get("typesMapping");
								System.out.println("typesMapping======"+typesMapping);
								if(typesMapping.equals("09"))
								{
									Intent intent = new Intent();
								    intent.setClass( BaiduMap.this,HotelActivity.class);
								    Bundle bundle = new Bundle();
									bundle.putInt("index", Integer.valueOf(idex));
									bundle.putString("tag", "map");
									intent.putExtras(bundle);
//									BaiduMap.this.startActivity(intent);//开始界面的跳转函数
//									((Activity) BaiduMap.this).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
									MainTabActivity.instance.loadLeftActivity(intent);
								}
								else
								{
									Intent intent = new Intent();
//								    intent.setClass( BaiduMap.this,CardsView.class);
									intent.setClass(BaiduMap.this, StoreMainActivity.class);
								    Bundle bundle = new Bundle();
									bundle.putString("index", idex);
									bundle.putString("tag", "map");
									intent.putExtras(bundle);
//									BaiduMap.this.startActivity(intent);//开始界面的跳转函数
//								    ((Activity) BaiduMap.this).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
									MainTabActivity.instance.loadLeftActivity(intent);
								}
							}
						});
					}
					else
					{
						btn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								new Thread(){
									public void run(){
										Message msg = new Message();
										msg.what = 1;
										JSONObject jobj = null;
										try {
											jobj = api.addCards(myapp.getPfprofileId(),pid,bid,storeTyle,province);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										msg.obj = jobj;
										handler.sendMessage(msg);
									}
								}.start();
								mypDialog = ProgressDialog.show(BaiduMap.this, null, BaiduMap.this.getString(R.string.map_lable_15), true,
						                false);
							}
						});
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				
				Button btn2 = (Button)view.findViewById(R.id.clear_btn);
				btn2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myDialogs.dismiss();
					}
				});
				
				if(mypDialog != null)
					mypDialog.dismiss();
				
	      }
	      else
	      {
	    	  if(mypDialog != null)
					mypDialog.dismiss();
	    	  
	    	  AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		      dialog.setTitle((String)nmap.get("storeName"));
		      dialog.setMessage((String)nmap.get("storeDesc"));
		      dialog.show();
	      }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
	
	public Map<String,Object> getMap(List<Map<String,Object>> dlist,String pkid)
	{
		Map<String,Object> nmap = null;
		if (dlist != null) {
			for (int i = 0; i < dlist.size(); i++) {
				Map<String, Object> map = dlist.get(i);
				String pid = (String) map.get("pkid");

				if (pid.equals(pkid)) {
					nmap = map;
					break;
				}
			}
		}
		return nmap;
	}
	
	public Map<String,Object> getMyCardMap(Map<String,Object> map,Map<String,Object> oldmap){
		try{
				
				map.put("storeid", oldmap.get("pkid"));
				map.put("storeName", oldmap.get("storeName"));
				map.put("storePhone", oldmap.get("storePhone"));
				map.put("addressInfomation", oldmap.get("adress"));
				map.put("storeDesc", oldmap.get("storeDesc"));
				map.put("imgurl", oldmap.get("imageurl"));
				map.put("couponNumber", oldmap.get("couponnumber"));
				map.put("typeName", oldmap.get("typeName"));
				map.put("typesMapping", oldmap.get("typesMapping"));
				map.put("businessId", oldmap.get("bid"));
				map.put("woof", oldmap.get("woof"));
				map.put("longItude", oldmap.get("longItude"));
	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}

	// 矫正百度坐标
	private List<String> getNewBaidu(String x, String y) {
		List<String> list = new ArrayList();
		// x=116.254615&y=29.814476
		String url = "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x="
				+ x + "&y=" + y + "";
		System.out.println("转换坐标url==" + url);

		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				System.out.println("转换坐标返回：" + strResult);
				// {"error":0,"x":"MTE2LjI2MTA5OTEyMjE=","y":"MjkuODIwNTYwODc0ODQ2"}
				JSONObject jObj = new JSONObject(strResult);

				String newX = encode(jObj.getString("x").getBytes());
				String newY = encode(jObj.getString("y").getBytes());
				System.out.println("转换后X:" + newX);
				System.out.println("转换后Y:" + newY);
				list.add(newX);
				list.add(newY);
			} else {
				System.out.println("转化坐标失败");
			}
		} catch (Exception e) {
			System.out.println("转换坐标失败");
			e.printStackTrace();
		}

		return list;
	}

	// 谷歌坐标转换为百度坐标
	private void googleToBaidu(String x, String y) {
		// x=116.254615&y=29.814476
		String url = "http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x="
				+ x + "&y=" + y + "";
		System.out.println("转换坐标url==" + url);

		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				System.out.println("转换坐标返回：" + strResult);
				// {"error":0,"x":"MTE2LjI2MTA5OTEyMjE=","y":"MjkuODIwNTYwODc0ODQ2"}
				JSONObject jObj = new JSONObject(strResult);

				String newX = Base64
						.decode(jObj.getString("x"), Base64.DEFAULT).toString();
				String newY = Base64
						.decode(jObj.getString("y"), Base64.DEFAULT).toString();
				System.out.println("转换后X:" + newX);
				System.out.println("转换后Y:" + newY);
			} else {
				System.out.println("转化坐标失败");
			}
		} catch (Exception e) {
			System.out.println("转换坐标失败");
			e.printStackTrace();
		}
	}

	/************ View转换为Bitmap ***********/
	public static Bitmap getImageBitmap(String value) {
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("imageUrl==" + imageUrl);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap, 500, 200, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	/************** base64解码 ************/
	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	/** Base64 encode the given data */
	public static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append("");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * Decodes the given Base64 encoded String to a new byte array. The byte
	 * array holding the decoded data is returned.
	 */

	public static byte[] decode(String s) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}

	@Override
	public void onPanelClosed(Panel panel) {
		// TODO Auto-generated method stub
		String panelName = getResources().getResourceEntryName(panel.getId());
		// Log.d("TestPanels", "Panel [" + panelName + "] closed");
		// flayout.removeView(textView);
		RotateAnimation rotateAnimation = new RotateAnimation(180, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setFillBefore(false);
		rotateAnimation.setDuration(500);
		if (panelName.equals("bottomPanel")) {
			upimg.startAnimation(rotateAnimation);
			if (!typepanel.isOpen())
				textView.setVisibility(View.GONE);
		} else {
			timg.startAnimation(rotateAnimation);
			if (!panel.isOpen())
				textView.setVisibility(View.GONE);
		}
		// textView.setVisibility(View.GONE);
		panel.setVisibility(View.GONE);
	}

	@Override
	public void onPanelOpened(Panel panel) {
		// TODO Auto-generated method stub
		String panelName = getResources().getResourceEntryName(panel.getId());
		// Log.d("TestPanels", "Panel [" + panelName + "] opened");
		// panel.setVisibility(View.VISIBLE);
		textView.setVisibility(View.VISIBLE);
		textView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
////			Intent intent = new Intent();
////        	intent.setClass(BaiduMap.this,Exit.class);
////        	startActivity(intent);
//			MainTabActivity.instance.onMinimizeActivity();
//			return false;
//		}
//		return false;
//	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainView();
			return false;
		}
		return false;
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MainTabActivity.class);
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {  
            // 将当前位置转换成地理坐标点  
            final GeoPoint pt = new GeoPoint((int) (location.getLatitude() * 1000000), (int) (location.getLongitude() * 1000000));  
            // 将当前位置设置为地图的中心  
            mMapController.setCenter(pt);  
        }  
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
