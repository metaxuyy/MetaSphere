package ms.activitys.map;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.ContactActivity;
import ms.activitys.hotel.HotelActivity;
import ms.activitys.hotel.HotelServiceActivity;
import ms.activitys.hotel.StoreMainActivity;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.PinyinComparator;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PeripheryMapsActivity extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private MapController mc;
	private MapController mapController;
	private Canvas circleCanvas;
	
	private static String[] m = {};
	private static String[] m2 = {};
	private String [] types = {"500","1000","1500","2000","All"};
	
	private final int TOOLBAR_ITEM_MYCARD = 0;
	private final int TOOLBAR_ITEM_MAP = 1;
	private final int TOOLBAR_ITEM_CAOMIAO = 2;
	private final int TOOLBAR_ITEM_NFC = 3;
	
	private final int cx = 3;
	private final int cy = 1;
	private ImageView storeimgview;
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; 
	
	String cviewstr; 
	String qviewstr; 
	public View popView;
	private Dialog myDialogs;
	
	private String homepage = "";
	
	private LocationManager locationManager;
    String provider;
    Location location;
    private MyOverlay myLocationOverlay=null;
    private GeoPoint mypoint;
    private List<Overlay> mapOverlays;
//    private MyCustomLocationOverlay myOverlay;
    
    MapView mapView;
    
    protected Menu myMenu;
	
	private ArrayAdapter<String> adapter;
	
	private String idex;
	
	private ViewFlipper mViewFlipper;    
	
	private String menutag = "map";
	
	private int typeindex = 0;
	private int metarindex = 3;
	
	private Integer storeindex;
	
	private String storelat;
	private String storelng;
	private String pkid;
	
	private ProgressDialog mypDialog;
	
	private ListView clistView;
	
	private ProgressBar pb;
	private static List<Map<String,Object>> ctypelist;
	private Map<String,Object> storemap;
	
	private AlertDialog alertDialog;
	
	private DBHelperMessage db;
	private MyOverlay itemizedOverlay2;
	private String tag;
	
	public List<OverlayItem> GeoList;
	public List<OverlayItem> getGeoList() {
		return GeoList;
	}

	public void setGeoList(List<OverlayItem> geoList) {
		GeoList = geoList;
	}
	
	public OverlayItem coitem;
	public OverlayItem getCoitem() {
		return coitem;
	}

	public void setCoitem(OverlayItem coitem) {
		this.coitem = coitem;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baidu_map_local_periphery);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setUpdatetag("1");
		
		db = new DBHelperMessage(this, myapp);
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("index"))
		{
			storeindex = bunde.getInt("index");
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			storemap = list.get(storeindex);
		}
		else
			storemap = myapp.getHotelMap();
		tag = bunde.getString("tag");
//		String typeid = bunde.getString("type");
		
		
		
		storelat = (String)storemap.get("woof");
		storelng = (String)storemap.get("longItude");
		pkid = (String)storemap.get("pkid");
//		Bitmap imgurl = (Bitmap)storemap.get("imgurl");
		Bitmap imgurl = null;
		String imageurl = (String)storemap.get("imgurl");
		if(imgurl != null && !imgurl.equals(""))
		{
			imgurl = getLoacalBitmap(imageurl);
		}
		
		storemap.put("imageurl", imgurl);
		storemap.put("bid", storemap.get("businessId"));
		storemap.put("adress", storemap.get("addressInfomation"));
		
		getGPSLocation();
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		if(this.getIntent().getExtras() != null)
		{
			if(this.getIntent().getExtras().containsKey("homepage"))
				homepage = this.getIntent().getExtras().getString("homepage");
		}
		
		loadThreadData("150000");
		
		showGoogleMap();
		
		loadSelectCardsList();
	}
	
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		mc = mapView.getController();
		mc.zoomIn();
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu = menu;
//		super.onCreateOptionsMenu(menu);
//		menu.add(0, Menu.FIRST, 1, this.getString(R.string.map_lable_1)).setIcon(R.drawable.jiejing);
//		menu.add(0, Menu.FIRST + 1, 2, this.getString(R.string.map_lable_2)).setIcon(R.drawable.puton);
//		menu.add(0, Menu.FIRST + 2, 3, this.getString(R.string.map_lable_3)).setIcon(R.drawable.c2);
//		menu.add(0, Menu.FIRST + 3, 4, this.getString(R.string.map_lable_4)).setIcon(R.drawable.typesouty);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		U.dout(item.getItemId());
		switch (item.getItemId()) {		
		case 1:
			mapView.setTraffic(true);
			break;
		case 2:
			mapView.setSatellite(false);
            mapView.setTraffic(false);
			break;
		case 3:
			if(myMenu != null)
			{
				if(menutag.equals("map"))
				{
					myMenu.getItem(2).setTitle(this.getString(R.string.map_lable_5)).setIcon(R.drawable.muen_map);
					menutag = "list";
				}
				else
				{
					myMenu.getItem(2).setTitle(this.getString(R.string.map_lable_6)).setIcon(R.drawable.c2);
					menutag = "map";
				}
			}
			
			mViewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);    
			mViewFlipper.showNext();
			break;
		case 4:
//			showFilterWind();
			break;
		}
		return true;
	}
	
	/**
	 * 
	 * @param menuNameArray
	 * @param imageResourceArray
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemtext", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage","itemtext" },
				new int[] { R.id.item_image,R.id.item_text });
		return simperAdapter;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(tag != null && !tag.equals(""))
			{
				Intent intent = new Intent();
				if(tag.equals("hotel"))
					intent.setClass( this,MainTabActivity.class);
				else
					intent.setClass( this,MainTabActivity.class);
			    Bundle bundle = new Bundle();
			    bundle.putString("tag", "hotel");
//				bundle.putString("role", "Cleaner");
				intent.putExtras(bundle);
			    startActivity(intent);
			    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			    this.finish();
			}
			else
			{
				setResult(RESULT_OK, getIntent());
				finish();
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 显示Google地图
	 */
	public void showGoogleMap()
	{
		try{
			mapView = (MapView) findViewById(R.id.map_view);
			mapView.setSatellite(false);
	        mapView.setTraffic(false);
			mapView.setBuiltInZoomControls(true);
	        
	        mapOverlays = mapView.getOverlays();
	        
	        mapOverlays.clear();
	        
	        mapController = mapView.getController();
	        
//	        if(myapp.getLatitude() != 0 && myapp.getLongitude() != 0)
//	        {
//		        int lat = myapp.getLatitude();
//		        int lon = myapp.getLongitude();
//		        
//		        GeoPoint point = new GeoPoint(lat, lon);
//		        myOverlay = new MyCustomLocationOverlay(this, mapView,point);
//				
//		        mapOverlays.add(myOverlay);
//	        }
//	        else
//	        {
//		        int lat = (int)(31.226149 * 1E6);
//		        int lon = (int)(121.449949 * 1E6);
//		        
//		        GeoPoint point = new GeoPoint(lat, lon);
//		        myOverlay = new MyCustomLocationOverlay(this, mapView,point);
//		        
//		        mapOverlays.add(myOverlay);
//	        }
	        
	        int lat = (int)(Double.valueOf(storelat) * 1E6);
		    int lon = (int)(Double.valueOf(storelng) * 1E6);
	        
//		    Drawable drawable = this.getResources().getDrawable(R.drawable.map_lable);
//	        CustomItemizedOverlay itemizedOverlay =new CustomItemizedOverlay(drawable, PeripheryFacilitySearchActivity.this,myapp,api,mapView);
//	        GeoPoint point = new GeoPoint(lat, lon);
//	        OverlayItem overlayitem = new OverlayItem(point, "Hello", pkid);
//	        
//	        itemizedOverlay.addOverlay(overlayitem);
//	        mapOverlays.add(itemizedOverlay);
	        
	        
	        GeoPoint point2 = new GeoPoint(lat, lon);
//        	mapController.animateTo(point2);
	        mapController.setCenter(point2);
        	
	        LinearLayout topPanel = (LinearLayout)findViewById(R.id.search_bar_layout);
	        topPanel.setVisibility(View.GONE);
	        ImageView topImg = (ImageView)findViewById(R.id.search_bar_layout_img);
	        topImg.setVisibility(View.GONE);
	        RelativeLayout bottomLayout = (RelativeLayout)findViewById(R.id.bottomLayout);
	        bottomLayout.setVisibility(View.GONE);

//	        if(myapp.getLatitude() != 0 && myapp.getLongitude() != 0)
//	        {
//	        	GeoPoint point2 = new GeoPoint(myapp.getLatitude(), myapp.getLongitude());
//	        	mapController.animateTo(point2);
//	        }
//	        else
//	        {
//	        	int lat = (int)(31.226149 * 1E6);
//		        int lon = (int)(121.449949 * 1E6);
//		        GeoPoint point2 = new GeoPoint(lat, lon);
//	        	mapController.animateTo(point2);
//	        }
	        mapController.setZoom(15);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void checgMapZoom(boolean zoomIn)
	{
		try{
			if(zoomIn)
			{
				mapController.setZoom(mapView.getZoomLevel() + 1);
			}
			else
			{
				mapController.setZoom(mapView.getZoomLevel() - 1);
			}

//			mapOverlays.remove(myOverlay);
//			addMylocal();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public GeoPoint correctLatitudeLongitude(GeoPoint mpoint)
	{
		GeoPoint gp = null;
		String url = "http://www.anttna.com/goffset/goffset1.php?lat="+myapp.getLat()+"&lon="+myapp.getLng();  

		System.out.println("mapurl===="+url);
		HttpGet get = new HttpGet(url);  
		String strResult = "";  
		try {  
		    HttpParams httpParameters = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);  
		    HttpClient httpClient = new DefaultHttpClient(httpParameters);   
		      
		    HttpResponse httpResponse = null;  
		    httpResponse = httpClient.execute(get);  
		      
		    if (httpResponse.getStatusLine().getStatusCode() == 200){  
		        strResult = EntityUtils.toString(httpResponse.getEntity());  
		    }  
		} catch (Exception e) {  
		    return null;  
		}  
		
		if(strResult != null && !strResult.equals(""))
		{
			String [] strs = strResult.split(",");
			int lat2 = (int)(Double.valueOf(strs[0]) * 1E6);
			int lon2 = (int)(Double.valueOf(strs[1]) * 1E6);
			gp =  new GeoPoint(lat2, lon2);
			
			double lat = gp.getLatitudeE6() / 1E6;
			double lng = gp.getLongitudeE6() / 1E6;
			
			myapp.setLatitude(gp.getLatitudeE6());
			myapp.setLongitude(gp.getLongitudeE6());
			myapp.setLat(String.valueOf(lat));
			myapp.setLng(String.valueOf(lng));
		}
		else
		{
			gp = mpoint;
		}
		return gp;
	}
	
	
	public Drawable getImageDrawable(String value)
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

			bitmap = BitmapFactory.decodeStream(is);
			if(bitmap != null)
			{
				bitmap = Bitmap.createScaledBitmap(bitmap,50,50,true);
				drawable = new BitmapDrawable(bitmap);
				System.out.println("dew===="+value);
			}
			else
			{
				System.out.println("nullurl======"+value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return drawable;
	}
	
	public Drawable getDrawableZoon(Bitmap bitmap,int w,int h)
	{
		Drawable drawable = null;
		try{
			bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
			drawable = new BitmapDrawable(bitmap);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return drawable;
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

			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap,500,300,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	
	public List<Map<String,Object>> getStoreListData(String typeid,String meter)
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			
			jobj = api.getStorePeripheryAll(typeid,meter,storelat,storelng);
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getStoreList(jArr);
				list.add(storemap);
				myapp.setStorelist(list);
			}
			else
			{
				list.add(storemap);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void loadThreadData(final String meter)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getStorePeripheryAll3("",meter,storelat,storelng);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getStoreList(jArr);
						list.add(storemap);
						myapp.setStorelist(list);
					}
					else
					{
						list.add(storemap);
					}
					msg.obj = list;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override  
    protected void onStop() {  
        // TODO Auto-generated method stub  
		myapp.setStorelist(null);  
		System.gc();
        super.onStop();  
    } 
	
	@Override  
    protected void onDestroy() {  
        // TODO Auto-generated method stub  
		myapp.setStorelist(null); 
		System.gc();
        super.onDestroy();  
    }  
	
	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				
				if(dlist != null)
				{
					Drawable drawable2 = null;
					String imgmaping = "map_entertainment_icon";
					if(imgmaping != null && !imgmaping.equals(""))
					{
						int imgid = getResources().getIdentifier(getPackageName()+":drawable/"+imgmaping,null,null);
				        drawable2 = getResources().getDrawable(imgid);
					}
					else
					{
						drawable2 = getResources().getDrawable(R.drawable.map_lable);
					}
					
					if (itemizedOverlay2 == null) {
						itemizedOverlay2 = new MyOverlay(drawable2,PeripheryMapsActivity.this,myapp,api,db,mapView);
						mapOverlays.add(itemizedOverlay2);
					} else {
						itemizedOverlay2.removeAll();
					}
					
//					for(int i=0;i<dlist.size();i++)
//			        {
//			        	Map map = dlist.get(i);
//			        	String pkid = (String)map.get("pkid");
////						Bitmap imgurl = (Bitmap)map.get("imageurl");
//						String lons = (String)map.get("longItude");
//						String lats = (String)map.get("woof");
//						Bitmap bimg = (Bitmap)map.get("bimg");
////						String imgmaping = (String)map.get("imgmaping");
//						
//						int lat2 = (int)(Double.valueOf(lats) * 1E6);
//				        int lon2 = (int)(Double.valueOf(lons) * 1E6);
////						System.out.println("imgurl======="+imgurl);
//	//			        Drawable drawable2 = getDrawableZoon(bimg,50,50);
//						
//				        if(drawable2 != null)
//				        {
////				        	PeripheryCustomItemizedOverlay itemizedOverlay2 =new PeripheryCustomItemizedOverlay(drawable2, this,myapp,api,mapView);
////				        	itemizedOverlay2 = new MyOverlay(drawable2,PeripheryMapsActivity.this,myapp,api,db,mapView);
//				        	GeoPoint p = new GeoPoint((int) (Double.valueOf(lats) * 1E6),
//									(int) (Double.valueOf(lons) * 1E6));
//				        	OverlayItem item = new OverlayItem(p, pkid, pkid);
//							item.setMarker(drawable2);
//
//							itemizedOverlay2.addItem(item);
//							
////					        GeoPoint point2 = new GeoPoint(lat2, lon2);
////					        OverlayItem overlayitem2 = new OverlayItem(point2, "Hello", pkid);
//					        
////					        itemizedOverlay2.addOverlay(overlayitem2);
////					        mapOverlays.add(itemizedOverlay2);
////					        mapView.getOverlays().add(itemizedOverlay2);
//				        }
//			        }
//					
//					mapView.refresh();
					
					clistView.setAdapter(getAdapter(dlist));
				}
				
//				addMylocal();
				pb.setVisibility(View.GONE);
				clistView.setVisibility(View.VISIBLE);
				mapView.invalidate();
				break;
			case 1:
//				GeoPoint point = new GeoPoint(myapp.getLatitude(), myapp.getLongitude());
//				mapOverlays.remove(myOverlay);
//				myOverlay = new MyCustomLocationOverlay(PeripheryFacilitySearchActivity.this, mapView,point);
//		        mapOverlays.add(myOverlay);
//		        mapView.invalidate();
				break;
			case 2:
				Bitmap bitm = (Bitmap)msg.obj;
				storeimgview.setImageBitmap(bitm);
				if(mypDialog != null)
					mypDialog.dismiss();
				myDialogs.show();
				break;
			case 3:
				try {
					JSONObject jobj = (JSONObject) msg.obj;
					String success = jobj.getString("success");
					if(mypDialog != null)
						mypDialog.dismiss();
					if(success.equals("true"))
					{
						makeText(getString(R.string.coupon_lable_25));
						myapp.setUpdatetag("0");
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
			default:
				super.handleMessage(msg);
			}
		}
	};
	
//	public void addMylocal()
//	{
//		try{
//			if(myOverlay != null)
//				mapOverlays.remove(myOverlay);
//			 if(myapp.getLatitude() != 0 && myapp.getLongitude() != 0)
//		        {
//			        int lat = myapp.getLatitude();
//			        int lon = myapp.getLongitude();
//			        
//			        GeoPoint point = new GeoPoint(lat, lon);
//			        myOverlay = new MyCustomLocationOverlay(this, mapView,point);
//					
//			        mapOverlays.add(myOverlay);
//		        }
//		        else
//		        {
//			        int lat = (int)(31.226149 * 1E6);
//			        int lon = (int)(121.449949 * 1E6);
//			        
//			        GeoPoint point = new GeoPoint(lat, lon);
//			        myOverlay = new MyCustomLocationOverlay(this, mapView,point);
//			        
//			        mapOverlays.add(myOverlay);
//		        }
//			 
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	public List<Map<String,Object>> getStoreList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; // 门店ID
				if(dobj.has("storesInfoId"))
					pkid = (String) dobj.get("storesInfoId"); 
				
				String imageurl = ""; // 图片
				if(dobj.has("sysImg"))
					imageurl = (String) dobj.get("sysImg"); 
				
				String storeName = ""; // 门店名字
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
//				String areaName = ""; // 区域名字
//				if(dobj.has("areaName"))
//					areaName = (String) dobj.get("areaName"); 
//				
//				String areaid = ""; // 区域id
//				if(dobj.has("areaid"))
//					areaid = (String) dobj.get("areaid"); 
				
				String longItude = ""; // 经度
				if(dobj.has("longItude"))
				{
					longItude = (String) dobj.get("longItude"); 
				}
				
				String woof = ""; // 纬度
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof"); 
				
				String storeType = ""; // 类型名字
				if(dobj.has("storeType"))
					storeType = (String) dobj.get("storeType"); 
				
//				String storeId = ""; // 类型id
//				if(dobj.has("storeId"))
//					storeId = (String) dobj.get("storeId"); 
				
				String adress = ""; // 地址
				if(dobj.has("adress"))
					adress = (String) dobj.get("adress"); 
				
				String storePhone = "";
				if(dobj.has("storePhone"))
					storePhone = (String) dobj.get("storePhone"); 
					
				String storeDesc = ""; 
					if(dobj.has("storeDesc"))
						storeDesc = (String) dobj.get("storeDesc"); 
					
				String bname = ""; 
					if(dobj.has("bname"))
						bname = (String) dobj.get("bname"); 
					
				String bid = ""; 
					if(dobj.has("bid"))
						bid = (String) dobj.get("bid"); 
				
				String province = ""; 
				if(dobj.has("province"))
					province = (String) dobj.get("province"); 
				
				String city = ""; 
				if(dobj.has("city"))
					city = (String) dobj.get("city"); 
				
				String vipnumber = ""; 
				if(dobj.has("vipnumber"))
					vipnumber = (String) dobj.get("vipnumber"); 
				
				String juli = "";
				if(dobj.has("juli"))
					juli = (String) dobj.get("juli"); 
				
				String couponnumber = ""; 
				if(dobj.has("couponnumber"))
					couponnumber = (String) dobj.get("couponnumber"); 
				
				String typeName = ""; 
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				String typesMapping = ""; 
				if(dobj.has("typesMapping"))
					typesMapping = (String) dobj.get("typesMapping"); 
				
				String imgmaping = ""; 
				if(dobj.has("imgmaping"))
					imgmaping = (String) dobj.get("imgmaping"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("imageurl", imageurl);
				map.put("storeName", storeName);
//				map.put("areaName", areaName);
				map.put("longItude", longItude);
//				map.put("areaid", areaid);
				map.put("woof", woof);
				map.put("storeType", storeType);
//				map.put("storeId", storeId);
//				map.put("bimg", getImageBitmap(imageurl));
				map.put("adress", adress);
				map.put("storePhone", storePhone);
				map.put("storeDesc", storeDesc);
				map.put("bname", bname);
				map.put("bid", bid);
				map.put("province", province);
				map.put("city", city);
				map.put("vipnumber", vipnumber);
				map.put("juli", this.getString(R.string.map_lable_8)+":"+juli+"("+this.getString(R.string.map_lable_9)+")");
				map.put("couponnumber", couponnumber);
				map.put("typeName", typeName);
				map.put("typesMapping", typesMapping);
				map.put("imgmaping", imgmaping);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private SimpleAdapter getAdapter(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.select_cards_item, new String[] { "storeName","vipnumber","juli" },
				new int[] { R.id.store_name,R.id.vip_number,R.id.vip_juli });
		return simperAdapter;
	}

	private SpecialAdapter setGridAdapter(List<Map<String,Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.item_menu2, new String[] { "imgurl","text" },
				new int[] {  R.id.item_image,R.id.item_text  },share,true,"ico");
		return simperAdapter;
	}

	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void getGPSLocation()
    {
    	double latitude,longitude =0.0;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(serviceName);
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        
        Location location = null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
        	location = locationManager.getLastKnownLocation(provider);
        	locationManager.requestLocationUpdates(provider, 3000, 0,locationListener);
		}
        else
        {
        	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0,locationListener);
        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
//        double altitude =  location.getAltitude();     //海拔  
        updateWithNewLocation(location);
    }
	
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void updateWithNewLocation(Location location) {
		String latLongString = null;
		if (location != null) {
			final double lat = location.getLatitude();
			final double lng = location.getLongitude();
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					
					GeoPoint gp = null;
					String url = "http://www.anttna.com/goffset/goffset1.php?lat="+lat+"&lon="+lng;  

					System.out.println("mapurl===="+url);
					HttpGet get = new HttpGet(url);  
					String strResult = "";  
					try {  
					    HttpParams httpParameters = new BasicHttpParams();  
					    HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);  
					    HttpClient httpClient = new DefaultHttpClient(httpParameters);   
					      
					    HttpResponse httpResponse = null;  
					    httpResponse = httpClient.execute(get);  
					      
					    if (httpResponse.getStatusLine().getStatusCode() == 200){  
					        strResult = EntityUtils.toString(httpResponse.getEntity());  
					    }  
					} catch (Exception e) {  
					    e.printStackTrace();
					}  
					
					JSONObject json = null;
					if(strResult != null && !strResult.equals(""))
					{
						String [] strs = strResult.split(",");
						int lat2 = (int)(Double.valueOf(strs[0]) * 1E6);
						int lon2 = (int)(Double.valueOf(strs[1]) * 1E6);
						
						gp =  new GeoPoint(lat2, lon2);
						Point point = mapView.getProjection().toPixels(gp, null);
						
//						gp = mapView.getProjection().fromPixels(point.x + 6,point.y - 6);
						
						double lat = gp.getLatitudeE6() / 1E6;
						double lng = gp.getLongitudeE6() / 1E6;
						
						json = geocodeAddr(lat,lng);
						
						myapp.setLatitude(gp.getLatitudeE6());
						myapp.setLongitude(gp.getLongitudeE6());
						myapp.setLat(String.valueOf(lat));
						myapp.setLng(String.valueOf(lng));
					}
					else
					{
						myapp.setLat(String.valueOf(lat));
						myapp.setLng(String.valueOf(lng));
						json = geocodeAddr(lat,lng);
						int lats = (int)(lat * 1E6);
				        int lons = (int)(lng * 1E6);
				        myapp.setLatitude(lats);
						myapp.setLongitude(lons);
					}
					
					
					String CountryName = ""; 
					String CountryNameCode = ""; 
					String LocalityName = ""; 
					String ThoroughfareName = "";
					String quhao = "";
					String menpai = ""; 
					try {
						if(json != null)
						{
							JSONArray results = json.getJSONArray("results");
//							latLongString = results.getJSONObject(0).getString("formatted_address");
							if(results.length() > 0)
							{
								CountryName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name");
								CountryNameCode = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("short_name");
								LocalityName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");
								ThoroughfareName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
								quhao = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");
								menpai = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
								myapp.setCountry(CountryName);
								myapp.setCity(LocalityName);
								myapp.setRoad(ThoroughfareName);
								myapp.setArea(quhao);
								myapp.setNumbers(menpai);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		} else {
			latLongString = this.getString(R.string.login_lable_17);
		}
	}
	
	private static JSONObject geocodeAddr(double lat, double lng) {
//		String urlString = "http://ditu.google.com/maps/geo?q=+" + lat + ","+ lng + "&output=json&oe=utf8&hl=zh-CN&sensor=false";
		String urlString = "http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&language=zh_CN&sensor=false";
		
		StringBuilder sTotalString = new StringBuilder();
		try {

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			InputStream urlStream = httpConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlStream));

			String sCurrentLine = "";
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				sTotalString.append(sCurrentLine);
			}
			bufferedReader.close();
			httpConnection.disconnect(); // 关闭http连接

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(sTotalString.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}
	
//	public void showFilterWind()
//	{
//		try{
//			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
//			
//			final View view = LayoutInflater.from(this).inflate(R.layout.map_type_filter_view,null);
//			
//			final Spinner meterText = (Spinner)view.findViewById(R.id.meter);
//	
//			ArrayAdapter<String> adapterm = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types);
//
//			adapterm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
//
//	        meterText.setAdapter(adapterm);  
//	        meterText.setSelection(metarindex,true);
//			
//	        if(m.length == 0)
//	        {
//				JSONObject job = api.getStoreTypeAll();
//				
//				if(job != null)
//				{
//					JSONArray jArr = (JSONArray) job.get("data");
//					m = new String[jArr.length()+1];
//					m2 = new String[jArr.length()+1];
//					ctypelist = new ArrayList<Map<String,Object>>();
//					for (int i = 0; i < jArr.length(); i++) {
//						JSONObject jobs = (JSONObject)jArr.get(i);
//						String value = (String) jobs.get("value");
//						String id = (String) jobs.get("id");
//						String imgurl = (String) jobs.get("imgurl");
//						String imgtype = (String) jobs.get("imgtype");
//						
//						m[i] = value;
//						m2[i] = id;
//						Map<String,Object> map = new HashMap<String,Object>();
//						map.put("id", id);
//						map.put("text", value);
//						map.put("imgurl", imgtype);
//						ctypelist.add(map);
//					}
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("id", "");
//					map.put("text", this.getString(R.string.map_lable_10));
//					map.put("imgurl", "map_unknown_icon");
//					ctypelist.add(map);
//					
//					m[jArr.length()] = "All";
//					m2[jArr.length()] = "";
//				}
//	        }
//			
//	        GridView ctypeGrid = (GridView)view.findViewById(R.id.GridView_ctypes);
//	        ctypeGrid.setBackgroundResource(R.drawable.background);// 设置背景
//	        ctypeGrid.setNumColumns(4);// 设置每行列数
//			ctypeGrid.setGravity(Gravity.CENTER);// 位置居中
//			ctypeGrid.setVerticalSpacing(10);// 垂直间隔
//			ctypeGrid.setHorizontalSpacing(10);// 水平间隔
//			ctypeGrid.setAdapter(setGridAdapter(ctypelist));// 设置菜单Adapter
//			/** 监听底部菜单选项 **/
//			ctypeGrid.setOnItemClickListener(new OnItemClickListener() {
//				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//						long arg3) {
//					pb.setVisibility(View.VISIBLE);
//					clistView.setVisibility(View.GONE);
//					Map<String,Object> map = ctypelist.get(arg2);
//					loadThreadData((String)map.get("id"),types[meterText.getSelectedItemPosition()]);
//					showGoogleMap();
//					typeindex = arg2;
//					metarindex = meterText.getSelectedItemPosition();
//					loadSelectCardsList();
//					alertDialog.dismiss();
//				}
//			});
//			
//			builder.setView(view).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							
//						}
//					});
//			alertDialog = builder.create();
//			alertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
//			alertDialog.show();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	
	public void loadSelectCardsList()
	{
		try{
//			final List<Map<String,Object>> dlist = myapp.getStorelist();
//			
//			final Spinner meterText = (Spinner)findViewById(R.id.meter);
//			
//			ArrayAdapter<String> adapterm = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types);
//
//			adapterm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
//
//	        meterText.setAdapter(adapterm);  
//	        meterText.setSelection(metarindex,true);
//			
//			final Spinner spinner  = (Spinner)findViewById(R.id.Spinner01);
//			
//			if(m.length == 0)
//			{
//				JSONObject job = api.getStoreTypeAll();
//				
//				if(job != null)
//				{
//					JSONArray jArr = (JSONArray) job.get("data");
//					m = new String[jArr.length()+1];
//					m2 = new String[jArr.length()+1];
//					ctypelist = new ArrayList<Map<String,Object>>();
//					for (int i = 0; i < jArr.length(); i++) {
//						JSONObject jobs = (JSONObject)jArr.get(i);
//						String value = (String) jobs.get("value");
//						String id = (String) jobs.get("id");
//						String imgurl = (String) jobs.get("imgurl");
//						String imgtype = (String) jobs.get("imgtype");
//						
//						m[i] = value;
//						m2[i] = id;
//						
//						Map<String,Object> map = new HashMap<String,Object>();
//						map.put("id", id);
//						map.put("text", value);
//						map.put("imgurl", imgtype);
//						ctypelist.add(map);
//					}
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("id", "");
//					map.put("text", this.getString(R.string.map_lable_10));
//					map.put("imgurl", "map_unknown_icon");
//					ctypelist.add(map);
//					
//					m[jArr.length()] = "All";
//					m2[jArr.length()] = "";
//				}
//			}
//			
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
//	          
//	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
//	          
//	        spinner.setAdapter(adapter);
//	        spinner.setSelection(typeindex,true);
	        
//	        Button sbtn = (Button)findViewById(R.id.search_cards_btn);
//	        sbtn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					pb.setVisibility(View.VISIBLE);
//					clistView.setVisibility(View.GONE);
//					loadThreadData(m2[spinner.getSelectedItemPosition()],types[meterText.getSelectedItemPosition()]);
//					typeindex = spinner.getSelectedItemPosition();
//					metarindex = meterText.getSelectedItemPosition();
//					loadSelectCardsList();
//					showGoogleMap();
//				}
//			});
	        
			
			clistView = (ListView)findViewById(R.id.ListView_cards);
			
			clistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					mypDialog = ProgressDialog.show(PeripheryMapsActivity.this, null, PeripheryMapsActivity.this.getString(R.string.map_lable_11), true,
			                false);
					Map map = myapp.getStorelist().get(arg2);
					openMapDetlitlPage(map);
				}
				
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	
	public List<Map<String,Object>> getCardList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String sid = ""; 
				if(dobj.has("sid"))
					sid = (String) dobj.get("sid"); 
				
				String sname = ""; // 
				if(dobj.has("sname"))
					sname = (String) dobj.get("sname"); 
				
				
				String vipnumber = ""; 
				if(dobj.has("vipnumber"))
					vipnumber = (String) dobj.get("vipnumber"); 
				
				String juli = ""; 
				if(dobj.has("juli"))
					juli = (String) dobj.get("juli"); 
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("sid", sid);
				map.put("sname", sname);
				map.put("vipnumber", vipnumber);
				map.put("img", R.drawable.vip_cards);
				map.put("juli",this.getString(R.string.map_lable_8)+":"+juli+"("+this.getString(R.string.map_lable_9)+")");
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void openMapDetlitlPage(Map nmap)
	{
		if(nmap != null)
	      {
	    	  	final String pid = (String)nmap.get("pkid");
	    	  	final String imgurl = (String)nmap.get("imageurl");
	    	  	final String storeName = (String)nmap.get("storeName");
//	    	  	final String areaName = (String)nmap.get("areaName");
	    	  	final String lons = (String)nmap.get("longItude");
	    	  	final String lats = (String)nmap.get("woof");
	    	  	final String storeTyle = (String)nmap.get("storeType");
	    	  	final String areaid = (String)nmap.get("areaid");
//	    	  	final String storeId = (String)nmap.get("storeId");
	    	  	Bitmap bimg = (Bitmap)nmap.get("bimg");
	    	  	String adress = (String)nmap.get("adress");
				String storePhone = (String)nmap.get("storePhone");
				String stroeDesc = (String)nmap.get("storeDesc");
				String bname = (String)nmap.get("bname");
				final String bid = (String)nmap.get("bid");
				final String province = (String)nmap.get("province");
				
				
				final View view = LayoutInflater.from(this).inflate(R.layout.google_map_popup, null);
				
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
				
				String source = stroeDesc + "<br><b>"+this.getString(R.string.map_lable_12)+":"+ adress+"</b><br><b>"+this.getString(R.string.map_lable_13)+":"+storePhone+"</b>";
				TextView count = (TextView)view.findViewById(R.id.widget36);
				count.setText(Html.fromHtml(source)); 
				
				myDialogs = new Dialog(this, R.style.AliDialog);
				myDialogs.setContentView(view);
//				myDialog.show();
//				
				
				Button btn = (Button)view.findViewById(R.id.add_vip_btn);
				btn.setVisibility(View.GONE);
//				try{
//					JSONObject jobj = api.isSelectCards(myapp.getPfprofileId(),pid,bid,null);
//					String tag = jobj.getString("tag");
//					if(tag.equals("1"))
//					{
//						btn.setText(this.getString(R.string.map_lable_14));
//						btn.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								openMycard();
//							}
//						});
//					}
//					else
//					{
//						btn.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								new Thread(){
//									public void run(){
//										Message msg = new Message();
//										msg.what = 3;
//										JSONObject jobj = null;
//										try {
//											jobj = api.addCards(myapp.getPfprofileId(),pid,bid,storeTyle,province);
//										} catch (JSONException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										} catch (Exception e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//										msg.obj = jobj;
//										handler.sendMessage(msg);
//									}
//								}.start();
//								mypDialog = ProgressDialog.show(PeripheryMapsActivity.this, null, PeripheryMapsActivity.this.getString(R.string.map_lable_15), true,
//						                false);
//							}
//						});
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
				
				
				
				Button btn2 = (Button)view.findViewById(R.id.clear_btn);
				btn2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myDialogs.dismiss();
					}
				});
				
	      }
	}
	
	public void openMycard()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,CardsView.class);
		    intent.setClass(this, StoreMainActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", idex);
			bundle.putString("tag", "pmap");
			intent.putExtras(bundle);
			startActivity(intent);
		    this.overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Map getMyCardMap(String pkid)
	{
		Map nmap = null;
		try{
			List<Map<String,Object>> dlist = myapp.getStorelist();
		      if(dlist != null)
		      {
			      for(int i=0;i<dlist.size();i++)
			      {
			        	Map map = dlist.get(i);
			        	String pid = (String)map.get("pkid");
			        	
						if(pid.equals(pkid))
						{
							nmap = map;
							break;
						}
			      }
		      }
		      
		      List<Map<String,Object>> cardlist = myapp.getMyCardsAll();
		      if(cardlist != null)
		      {
			      for(int i=0;i<cardlist.size();i++)
			      {
			        	Map map = cardlist.get(i);
			        	String pid = (String)map.get("storeid");
			        	
						if(pid.equals(pkid))
						{
							idex = String.valueOf(i);
							break;
						}
			      }
		      }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return nmap;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showProgressDialog(){
		try{
			mypDialog=new ProgressDialog(this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage(this.getString(R.string.login_lable_21));
            mypDialog.setIndeterminate(false);
            mypDialog.setCancelable(true);
            mypDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//    @Override
//    protected boolean isLocationDisplayed() {
//        return myOverlay.isMyLocationEnabled();
//    }
 
    @Override
    protected void onPause() {
        super.onPause();
 
//        myOverlay.disableMyLocation();
//        myOverlay.disableCompass();
 
        locationManager.removeUpdates(locationListener);
    }
    
    @Override
	protected void onResume()
	{
		super.onResume();
//		myOverlay.enableMyLocation();
//		myOverlay.enableCompass();
//		init();
	}
    
    private void init()
	{
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{

			new AlertDialog.Builder(this).setTitle(this.getString(R.string.record_lable_2)).setMessage(this.getString(R.string.attractions_lable_21))
					.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
					{

						public void onClick(DialogInterface dialog, int which)
						{
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							Toast.makeText(PeripheryMapsActivity.this, PeripheryMapsActivity.this.getString(R.string.attractions_lable_22), Toast.LENGTH_SHORT).show();
						}
					}).show();

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

		public MyOverlay(Drawable marker, Context context,MyApp myapp,Douban api,DBHelperMessage db,MapView mapview) {
			super(marker);
			this.mContext = context;
			pop = new PopupOverlay(mapview, new PopupClickListener() {

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
			
			mypDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.map_lable_11), true,
	                false);
	      
	      String pkid = item.getSnippet();
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
//	    	  	final String areaName = (String)nmap.get("areaName");
	    	  	final String lons = (String)nmap.get("longItude");
	    	  	final String lats = (String)nmap.get("woof");
	    	  	final String storeTyle = (String)nmap.get("storeType");
	    	  	final String areaid = (String)nmap.get("areaid");
//	    	  	final String storeId = (String)nmap.get("storeId");
//	    	  	Bitmap bimg = (Bitmap)nmap.get("bimg");
	    	  	String adress = (String)nmap.get("adress");
				String storePhone = (String)nmap.get("storePhone");
				String stroeDesc = (String)nmap.get("storeDesc");
				String bname = (String)nmap.get("bname");
				final String bid = (String)nmap.get("bid");
				final String province = (String)nmap.get("province");
				
//				final View view = LayoutInflater.from(context).inflate(R.layout.google_map_popup, null);
				final View view = LayoutInflater.from(mContext).inflate(R.layout.map_add_store_dialog, null);
				
				TextView title = (TextView)view.findViewById(R.id.tilte_txt);
				title.setText(storeName);
				
				String source = stroeDesc + "<br><b>"+mContext.getString(R.string.map_lable_12)+":"+ adress+"</b><br><b>"+mContext.getString(R.string.map_lable_13)+":"+storePhone+"</b>";
				TextView count = (TextView)view.findViewById(R.id.content_txt);
				count.setText(Html.fromHtml(source)); 
				
				myDialogs = new Dialog(mContext,R.style.MyMapDialog);
				myDialogs.setContentView(view);
				
				Window dialogWindow = myDialogs.getWindow();
				WindowManager m = ((Activity) mContext).getWindowManager();
		        Display d = m.getDefaultDisplay();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				lp.width = (int) (d.getWidth() * 0.85);
				dialogWindow.setAttributes(lp);
				
				Button btn = (Button)view.findViewById(R.id.add_vip_btn);
				
				try{
					boolean tag = U.isStoreExist(pid, myapp.getMyCardsAll());
					if(tag)
					{
						btn.setText(mContext.getString(R.string.hotel_label_11));
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
								    intent.setClass( mContext,HotelActivity.class);
								    Bundle bundle = new Bundle();
									bundle.putInt("index", Integer.valueOf(idex));
									bundle.putString("tag", "pmap");
									intent.putExtras(bundle);
									mContext.startActivity(intent);
									((Activity) mContext).overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
									((Activity) mContext).finish();
								}
								else
								{
									Intent intent = new Intent();
//								    intent.setClass( mContext,CardsView.class);
									intent.setClass(mContext, StoreMainActivity.class);
								    Bundle bundle = new Bundle();
									bundle.putString("index", idex);
									bundle.putString("tag", "pmap");
									intent.putExtras(bundle);
									mContext.startActivity(intent);
								    ((Activity) mContext).overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
								    ((Activity) mContext).finish();
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
								mypDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.map_lable_15), true,
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
				
				mypDialog.dismiss();
				myDialogs.show();
				
	      }
	      else
	      {
	    	  if(mypDialog != null)
					mypDialog.dismiss();
	    	  
	    	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		      dialog.setTitle(item.getTitle());
		      dialog.setMessage(item.getSnippet());
		      dialog.show();
	      }
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
								Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());
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
					mapView.removeView(mBtn);
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
    
    /**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
		
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}

}
