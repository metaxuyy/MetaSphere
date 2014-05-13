//package ms.activitys.wikitudear;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import ms.activitys.R;
//import ms.activitys.hotel.HotelActivity;
//import ms.activitys.vipcards.CardsView;
//import ms.activitys.vipcards.HomePage;
//import ms.globalclass.httppost.Douban;
//import ms.globalclass.map.MyApp;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Html;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.RotateAnimation;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//import com.wikitude.architect.ArchitectUrlListener;
//import com.wikitude.architect.ArchitectView;
//
///**
// * 
// * @author Wikitude
// * @date   JAN 2012
// * 
// * @class SimpleARBrowserActivity
// * 
// *	sample application to show how to use the ARchitect SDK
// * 	loads simple pois via javascript into the ARchitect world and displays them accordingly
// *  displays a bubble with information about the selected poi on the screen and displays a detail page when the bubble is clicked
// *  uses Android's LocationManager to get updates on the user's location
// * 
// *  important is that the methods of the activity lifecycle are forwarded to the ArchitectView
// *  Important methods:  	onPostCreate()
// * 							onResume()
// *							onPause()
// * 							onDestroy()
// * 							onLowMemory()	
// * 
// * 	Please also have a look at the application's Manifest and layout xml-file to see the permissions and requirements 
// * 	an activity using the SDK has to possess. (REF: ARchitect Documentation)		  	  
// */
//public class SimpleARBrowserActivity extends Activity implements ArchitectUrlListener{
//	
//	private static final String TAG = SimpleARBrowserActivity.class.getSimpleName();
//	
//	private  float  TEST_LATITUDE = 31.226337f;
//	private  float  TEST_LONGITUDE = 121.450067f;
////	private final static float  TEST_LATITUDE =  31.223238f;
////	private final static float  TEST_LONGITUDE = 121.445521f;
//	
//	private ArchitectView architectView;
//	private LocationManager locationManager;
//	private Location loc;
//	private List<PoiBean> poiBeanList;
//	
//	private SharedPreferences  share;
//	private Douban api;
//	private MyApp myapp;
//	
//	private ProgressDialog mypDialog;
//	
//	String provider;
//	private String idex = "0";
//	private ImageView storeimgview;
//	private Dialog myDialogs;
//	private static List<Map<String,Object>> storelist;
//	private static String selectpkid;
//	private LinearLayout dlayout;
//	private TextView jtxt;
//	private ImageView ImgCompass;
//	private double angle;
//	private float DegressQuondam = 0.0f;
//	private RotateAnimation myAni = null;
//	private ImageView simg;
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        //let the application be fullscreen
//        this.getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
//        
//        //check if the device fulfills the SDK'S minimum requirements
//        if(!ArchitectView.isDeviceSupported(this))
//        {
//        	Toast.makeText(this, "minimum requirements not fulfilled", Toast.LENGTH_LONG).show();
//        	this.finish();
//        	return;
//        }
//        setContentView(R.layout.wikitude_ar_view);
//        
//        myapp = (MyApp)this.getApplicationContext();
//		myapp.setUpdatetag("1");
//		
//		share = this.getSharedPreferences("perference", MODE_PRIVATE);
//		api = new Douban(share,myapp);
//		
//		storelist = myapp.getStorelist();
//		
//        //set the devices' volume control to music to be able to change the volume of possible soundfiles to play
//        this.setVolumeControlStream( AudioManager.STREAM_MUSIC );
//        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
//        //onCreate method for setting the license key for the SDK
//        architectView.onCreate("1234");
//        
//        ImgCompass = (ImageView)findViewById(R.id.img);
//        
//        simg = (ImageView)findViewById(R.id.simg);
//        
//        dlayout = (LinearLayout)findViewById(R.id.d_layout);
//        dlayout.getBackground().setAlpha(130);
//        dlayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				loadPopClick(selectpkid);
//			}
//		});
//        //in order to inform the ARchitect framework about the user's location Androids LocationManager is used in this case
////        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
////        locManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this);
//        getGPSLocation();
////        showreal();
//     }
//    
//    public void loadPopClick(String pkid)
//    {
//    	try{
//    		mypDialog = ProgressDialog.show(this, null, this.getString(R.string.map_lable_11), true,
//	                false);
//    		
//	      List<Map<String,Object>> dlist = storelist;
//	      final Map<String,Object> nmap = getMap(dlist,pkid);
//	      String bid2 = (String)nmap.get("bid");  
//	      
//	      List<Map<String,Object>> cardlist = myapp.getMyCardsAll();
//	      if(cardlist != null)
//	      {
//		      for(int i=0;i<cardlist.size();i++)
//		      {
//		        	Map<String,Object> map = cardlist.get(i);
//		        	String bid = (String)map.get("businessId");
//		        	
//					if(bid.equals(bid2))
//					{
//						idex = String.valueOf(i);
//						break;
//					}
//		      }
//	      }
//	      
//	      if(nmap != null)
//	      {
//	    	  	final String pid = (String)nmap.get("pkid");
//	    	  	final String imgurl = (String)nmap.get("imageurl");
//	    	  	final String storeName = (String)nmap.get("storeName");
//	    	  	final String lons = (String)nmap.get("longItude");
//	    	  	final String lats = (String)nmap.get("woof");
//	    	  	final String storeTyle = (String)nmap.get("storeType");
//	    	  	final String areaid = (String)nmap.get("areaid");
//	    	  	String adress = (String)nmap.get("adress");
//				String storePhone = (String)nmap.get("storePhone");
//				String stroeDesc = (String)nmap.get("storeDesc");
//				String bname = (String)nmap.get("bname");
//				final String bid = (String)nmap.get("bid");
//				final String province = (String)nmap.get("province");
//				
//				new Thread(){
//					public void run(){
//						Message msg = new Message();
//						msg.what = 4;
//						
//						try{
//							JSONObject jobj = api.isSelectCards(myapp.getPfprofileId(),pid,bid,null);
//							String tag = jobj.getString("tag");
//							Map map = new HashMap();
//							map.put("tag", tag);
//							map.put("nmap", nmap);
//							msg.obj = map;
//						}catch(Exception ex){
//							ex.printStackTrace();
//						}
//						
//						handler.sendMessage(msg);
//					}
//				}.start();
//	      }
//	      else
//	      {
//	    	  if(mypDialog != null)
//					mypDialog.dismiss();
//	    	  
//	    	  AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//		      dialog.setTitle((String)nmap.get("storeName"));
//		      dialog.setMessage((String)nmap.get("storeDesc"));
//		      dialog.show();
//	      }
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    }
//    
//    public void showStorePop(final Map<String,Object> nmap,String tag)
//    {
//    	try{
//    		final String pid = (String)nmap.get("pkid");
//    	  	final String imgurl = (String)nmap.get("imageurl");
//    	  	final String storeName = (String)nmap.get("storeName");
//    	  	final String lons = (String)nmap.get("longItude");
//    	  	final String lats = (String)nmap.get("woof");
//    	  	final String storeTyle = (String)nmap.get("storeType");
//    	  	final String areaid = (String)nmap.get("areaid");
//    	  	String adress = (String)nmap.get("adress");
//			String storePhone = (String)nmap.get("storePhone");
//			String stroeDesc = (String)nmap.get("storeDesc");
//			String bname = (String)nmap.get("bname");
//			final String bid = (String)nmap.get("bid");
//			final String province = (String)nmap.get("province");
//			
//			final View view = LayoutInflater.from(this).inflate(R.layout.google_map_popup2, null);
//			
//			storeimgview = (ImageView)view.findViewById(R.id.widget34);
//			
//			new Thread(){
//				public void run(){
//					Message msg = new Message();
//					msg.what = 0;
//					Bitmap bit = getImageBitmap2(imgurl);
//					msg.obj = bit;
//					handler.sendMessage(msg);
//				}
//			}.start();
//			
//			TextView title = (TextView)view.findViewById(R.id.widget35);
//			title.setText(storeName);
//			
//			String source = stroeDesc + "<br><b>"+this.getString(R.string.map_lable_12)+"："+ adress+"</b><br><b>"+this.getString(R.string.map_lable_13)+"："+storePhone+"</b>";
//			TextView count = (TextView)view.findViewById(R.id.widget36);
//			count.setText(Html.fromHtml(source)); 
//			
//			myDialogs = new Dialog(this, R.style.AliDialog);
//			myDialogs.setContentView(view);
//			
//			Button btn = (Button)view.findViewById(R.id.add_vip_btn);
//			
//			try{
//				if(tag.equals("1"))
//				{
//					btn.setText(this.getString(R.string.map_lable_14));
//					btn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							myDialogs.dismiss();
//							
//							Map<String,Object> map = myapp.getMyCardsAll().get(Integer.valueOf(idex));
//							getMyCardMap(map,nmap);
//							
//							String typesMapping = (String)map.get("typesMapping");
//							System.out.println("typesMapping======"+typesMapping);
//							if(typesMapping.equals("09"))
//							{
//								Intent intent = new Intent();
//							    intent.setClass( SimpleARBrowserActivity.this,HotelActivity.class);
//							    Bundle bundle = new Bundle();
//								bundle.putInt("index", Integer.valueOf(idex));
//								intent.putExtras(bundle);
//								SimpleARBrowserActivity.this.startActivity(intent);//开始界面的跳转函数
//								((Activity) SimpleARBrowserActivity.this).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//								((Activity) SimpleARBrowserActivity.this).finish();//关闭显示的Activity
//							}
//							else
//							{
//								Intent intent = new Intent();
//							    intent.setClass( SimpleARBrowserActivity.this,CardsView.class);
//							    Bundle bundle = new Bundle();
//								bundle.putString("index", idex);
//								bundle.putString("map", "map");
//								intent.putExtras(bundle);
//								SimpleARBrowserActivity.this.startActivity(intent);//开始界面的跳转函数
//							    ((Activity) SimpleARBrowserActivity.this).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//							    ((Activity) SimpleARBrowserActivity.this).finish();//关闭显示的Activity
//							}
//						}
//					});
//				}
//				else
//				{
//					btn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							new Thread(){
//								public void run(){
//									Message msg = new Message();
//									msg.what = 1;
//									JSONObject jobj = null;
//									try {
//										jobj = api.addCards(myapp.getPfprofileId(),pid,bid,storeTyle,province);
//									} catch (JSONException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									msg.obj = jobj;
//									handler.sendMessage(msg);
//								}
//							}.start();
//							mypDialog = ProgressDialog.show(SimpleARBrowserActivity.this, null, SimpleARBrowserActivity.this.getString(R.string.map_lable_15), true,
//					                false);
//						}
//					});
//				}
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//			
//			
//			
//			Button btn2 = (Button)view.findViewById(R.id.clear_btn);
//			btn2.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					myDialogs.dismiss();
//				}
//			});
//			
//			if(mypDialog != null)
//				mypDialog.dismiss();
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    }
//    
//    public Map<String,Object> getMyCardMap(Map<String,Object> map,Map<String,Object> oldmap){
//		try{
//				
//				map.put("storeid", oldmap.get("pkid"));
//				map.put("storeName", oldmap.get("storeName"));
//				map.put("storePhone", oldmap.get("storePhone"));
//				map.put("addressInfomation", oldmap.get("adress"));
//				map.put("storeDesc", oldmap.get("storeDesc"));
//				map.put("imgurl", oldmap.get("imageurl"));
//				map.put("couponNumber", oldmap.get("couponnumber"));
//				map.put("typeName", oldmap.get("typeName"));
//				map.put("typesMapping", oldmap.get("typesMapping"));
//				map.put("businessId", oldmap.get("bid"));
//				map.put("woof", oldmap.get("woof"));
//				map.put("longItude", oldmap.get("longItude"));
//	
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return map;
//	}
//    
//    public Bitmap getImageBitmap(String value)
//	{
//		URL imageUrl = null;
//		Bitmap bitmap = null;
//		Drawable drawable = null;
//		try {
//			imageUrl = new URL(value);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		try {
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.connect();
//			InputStream is = conn.getInputStream();
//
//			bitmap = BitmapFactory.decodeStream(is);
////			bitmap = Bitmap.createScaledBitmap(bitmap,500,200,true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return bitmap;
//	}
//    
//    public Bitmap getImageBitmap2(String value)
//	{
//		URL imageUrl = null;
//		Bitmap bitmap = null;
//		Drawable drawable = null;
//		try {
//			imageUrl = new URL(value);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		try {
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.connect();
//			InputStream is = conn.getInputStream();
//
//			bitmap = BitmapFactory.decodeStream(is);
//			bitmap = Bitmap.createScaledBitmap(bitmap,500,200,true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return bitmap;
//	}
//    
//    public Map<String,Object> getMap(List<Map<String,Object>> dlist,String pkid)
//	{
//		Map<String,Object> nmap = null;
//		if (dlist != null) {
//			for (int i = 0; i < dlist.size(); i++) {
//				Map<String, Object> map = dlist.get(i);
//				String pid = (String) map.get("pkid");
//
//				if (pid.equals(pkid)) {
//					nmap = map;
//					break;
//				}
//			}
//		}
//		return nmap;
//	}
//    
//    public void getGPSLocation()
//    {
//    	double latitude,longitude =0.0;
//        String serviceName = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager)getSystemService(serviceName);
////        String provider = LocationManager.GPS_PROVIDER;
//        
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//        provider = locationManager.getBestProvider(criteria, true);
//        
//        Location location = null;
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//		{
//        	location = locationManager.getLastKnownLocation(provider);
//        	if(location == null)
//        		locationManager.requestLocationUpdates(provider, 0, 0,locationListener2);
//        	else
//        		locationManager.requestLocationUpdates(provider, 1000, 10,locationListener);
//		}
//        else
//        {
//        	location = locationManager.getLastKnownLocation(provider);
//        	if(location == null)
//        	{
//        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener2);
//        	}
//        	else
//        	{
//        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10,locationListener);
//        	}	
//        }
////        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
////        double altitude =  location.getAltitude();     //海拔  
//        if(location != null)
//        	updateWithNewLocation(location);
//    }
//    
//    private void updateWithNewLocation(Location location) {
//		String latLongString = null;
//		if (location != null) {
//			locationManager.removeUpdates(locationListener2);
//			final double lat = location.getLatitude();
//			final double lng = location.getLongitude();
//			new Thread() {
//				public void run() {
//					Message msg = new Message();
//					msg.what = 2;
//					
//					GeoPoint gp = null;
////					String url = "http://www.anttna.com/goffset/goffset1.php?lat="+lat+"&lon="+lng;  
////
////					System.out.println("mapurl===="+url);
////					HttpGet get = new HttpGet(url);  
////					String strResult = "";  
////					try {  
////					    HttpParams httpParameters = new BasicHttpParams();  
////					    HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);  
////					    HttpClient httpClient = new DefaultHttpClient(httpParameters);   
////					      
////					    HttpResponse httpResponse = null;  
////					    httpResponse = httpClient.execute(get);  
////					      
////					    if (httpResponse.getStatusLine().getStatusCode() == 200){  
////					        strResult = EntityUtils.toString(httpResponse.getEntity());  
////					    }  
////					} catch (Exception e) {  
////					    e.printStackTrace();
////					}  
////					
////					JSONObject json = null;
////					if(strResult != null && !strResult.equals(""))
////					{
////						String [] strs = strResult.split(",");
////						int lat2 = (int)(Double.valueOf(strs[0]) * 1E6);
////						int lon2 = (int)(Double.valueOf(strs[1]) * 1E6);
////						
////						gp =  new GeoPoint(lat2, lon2);
//////						Point point = mapView.getProjection().toPixels(gp, null);
////						
//////						gp = mapView.getProjection().fromPixels(point.x + 6,point.y - 6);
////						
////						double lat = gp.getLatitudeE6() / 1E6;
////						double lng = gp.getLongitudeE6() / 1E6;
////						
////						json = geocodeAddr(lat,lng);
////						
////						myapp.setLatitude(gp.getLatitudeE6());
////						myapp.setLongitude(gp.getLongitudeE6());
////						myapp.setLat(String.valueOf(lat));
////						myapp.setLng(String.valueOf(lng));
////					}
////					else
////					{
////						myapp.setLat(String.valueOf(lat));
////						myapp.setLng(String.valueOf(lng));
////						json = geocodeAddr(lat,lng);
////						int lats = (int)(lat * 1E6);
////				        int lons = (int)(lng * 1E6);
////				        myapp.setLatitude(lats);
////						myapp.setLongitude(lons);
////					}
//					
//					try {
//						JSONObject jobj = api.getGPSOffset(String.valueOf(lat),String.valueOf(lng));
//						
//						String offsetlat = jobj.getString("offsetlat");
//						String offsetlng = jobj.getString("offsetlng");
//						
//						int lat2 = (int)(Double.valueOf(offsetlat) * 1E6);
//						int lon2 = (int)(Double.valueOf(offsetlng) * 1E6);
//						gp =  new GeoPoint(lat2, lon2);
//						
//						double lat = gp.getLatitudeE6() / 1E6;
//						double lng = gp.getLongitudeE6() / 1E6;
//						
//						myapp.setLatitude(gp.getLatitudeE6());
//						myapp.setLongitude(gp.getLongitudeE6());
//						myapp.setLat(String.valueOf(lat));
//						myapp.setLng(String.valueOf(lng));
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					if(selectpkid != null && !selectpkid.equals(""))
//					{
//						Map<String,String> smap = getSelectStorePoin(selectpkid);
//						if(smap != null && myapp.getLat() != null)
//						{
//							String woof = smap.get("woof");
//							String longtude = smap.get("long");
//							System.out.println("getlat=="+myapp.getLat()+";lng===="+myapp.getLng()+";woof===="+woof+";long===="+longtude);
//							double distance = gps2m(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(woof),Double.valueOf(longtude));
//							
//							msg.obj = String.valueOf((int)distance);
//						}
//					}
//					
//					handler.sendMessage(msg);
//				}
//			}.start();
//		} else {
//			latLongString = this.getString(R.string.login_lable_17);
//		}
//		System.out.println("您当前的位置是:\n" + latLongString);
//	}
//    
//    private final double EARTH_RADIUS = 6378137.0;  
//    // 计算两点距离
//    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
//       double radLat1 = (lat_a * Math.PI / 180.0);
//       double radLat2 = (lat_b * Math.PI / 180.0);
//       double a = radLat1 - radLat2;
//       double b = (lng_a - lng_b) * Math.PI / 180.0;
//       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//              + Math.cos(radLat1) * Math.cos(radLat2)
//              * Math.pow(Math.sin(b / 2), 2)));
//
//       s = s * EARTH_RADIUS;
//       s = Math.round(s * 10000) / 10000;
//
//       return s;
//    }
//    
//    public Map<String,String> getSelectStorePoin(String id)
//    {
//    	Map<String,String> map = null;
//    	try{
//    		for(int i=0;i<storelist.size();i++)
//    		{
//    			Map<String,Object> smap = storelist.get(i);
//    			String pkid = (String)smap.get("pkid");
//    			if(id.equals(pkid))
//    			{
//    				map = new HashMap<String,String>();
//					String lons = (String)smap.get("longItude");
//					String lats = (String)smap.get("woof");
//					map.put("woof", lats);
//					map.put("long", lons);
//    			}
//    		}
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    	return map;
//    }
//    
//    private Handler handler = new Handler() {
//		@Override
//		public synchronized void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				Bitmap bitm = (Bitmap)msg.obj;
//				storeimgview.setImageBitmap(bitm);
//				if(mypDialog != null)
//					mypDialog.dismiss();
//				myDialogs.show();
//				break;
//			case 1:
//				try {
//					JSONObject jobj = (JSONObject) msg.obj;
//					String success = jobj.getString("success");
//					if(mypDialog != null)
//						mypDialog.dismiss();
//					if(success.equals("true"))
//					{
//						JSONObject jobjs = api.getMyCardsAll("1");
//						if(jobjs != null)
//						{
//							JSONArray jArr = (JSONArray) jobjs.get("data");
//							List<Map<String,Object>> list = HomePage.getMyCardList(jArr);
//							myapp.setMyCardsAll(list);
//						}
//						
////						Toast.makeText(this, this.getString(R.string.coupon_lable_25), Toast.LENGTH_LONG).show();
//						myapp.setUpdatetag("0");
//					}
//					else
//					{
//						String msgstr = jobj.getString("msg");
////						Toast.makeText(context, context.getString(R.string.coupon_lable_26)+msgstr, Toast.LENGTH_LONG).show();
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				break;
//			case 2:
//				String jili = (String)msg.obj;
//				if(jili != null)
//				{
//					updateStoreJuli(jili);
//				}
//				if(myapp.getLatitude() > 0)
//				{
//					TEST_LATITUDE = (float) (myapp.getLatitude() / 1E6);
//					TEST_LONGITUDE = (float) (myapp.getLongitude() / 1E6);
////			        mapView.invalidate();
//				}
//				if (loc != null){
//					SimpleARBrowserActivity.this.architectView.setLocation((float) (TEST_LATITUDE),
//							(float) (TEST_LONGITUDE), loc.getAccuracy(),
//							loc.getTime());
//				}
//				else
//					SimpleARBrowserActivity.this.architectView.setLocation(TEST_LATITUDE, TEST_LONGITUDE, 1f);
//				break;
//			case 3:
//				Bitmap bitm2 = (Bitmap)msg.obj;
//				simg.setImageBitmap(bitm2);
//				break;
//			case 4:
//				Map maps = (Map)msg.obj;
//				String tag = (String)maps.get("tag");
//				Map nmap = (Map)maps.get("nmap");
//				showStorePop(nmap,tag);
//			default:
//				super.handleMessage(msg);
//			}
//		}
//	};
//	
//	public void updateStoreJuli(String juli)
//	{
//		if(jtxt != null)
//			jtxt.setText("距离您大约："+juli+"米");
//	}
//    
//    private static JSONObject geocodeAddr(double lat, double lng) {
////		String urlString = "http://ditu.google.com/maps/geo?q=+" + lat + ","+ lng + "&output=json&oe=utf8&hl=zh-CN&sensor=false";
//		String urlString = "http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&language=zh_CN&sensor=false";
//		
//		StringBuilder sTotalString = new StringBuilder();
//		try {
//
//			URL url = new URL(urlString);
//			URLConnection connection = url.openConnection();
//			HttpURLConnection httpConnection = (HttpURLConnection) connection;
//
//			InputStream urlStream = httpConnection.getInputStream();
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(urlStream));
//
//			String sCurrentLine = "";
//			while ((sCurrentLine = bufferedReader.readLine()) != null) {
//				sTotalString.append(sCurrentLine);
//			}
//			bufferedReader.close();
//			httpConnection.disconnect(); // 关闭http连接
//
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject = new JSONObject(sTotalString.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return jsonObject;
//	}
//    
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//    	super.onPostCreate(savedInstanceState);
//    	
//    	//IMPORTANT: creates ARchitect core modules
//    	if(this.architectView != null)
//    		this.architectView.onPostCreate();
//    	
//    	//register this activity as handler of "architectsdk://" urls
//    	this.architectView.registerUrlListener(this);
//    	
//    	try {
//			loadSampleWorld();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//    }
//    
//	@Override
//	protected void onResume() {
//		super.onResume();
//		if(myapp.getLatitude() > 0)
//		{
//			TEST_LATITUDE = (float) (myapp.getLatitude() / 1E6);
//			TEST_LONGITUDE = (float) (myapp.getLongitude() / 1E6);
////	        mapView.invalidate();
//		}
//		System.out.println("lat============="+TEST_LATITUDE);
//		System.out.println("lng============="+TEST_LATITUDE);
//		this.architectView.onResume();
//		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//		{
//			loc = locationManager.getLastKnownLocation(provider);
//		}
//        else
//        {
//        	loc = locationManager.getLastKnownLocation(provider);
//        }
//		if (loc != null){
////			this.architectView.setLocation((float) (TEST_LATITUDE),
////					(float) (TEST_LONGITUDE), loc.getAccuracy(),
////					loc.getTime());
//			updateWithNewLocation(loc);
//		}
//		else
//			this.architectView.setLocation(TEST_LATITUDE, TEST_LONGITUDE, 1f);
//
//	}
//    @Override
//    protected void onPause() {
//    	super.onPause();
//    	if(this.architectView != null)
//    		this.architectView.onPause();
//    }
//    
//    @Override
//    protected void onDestroy() {
//    	super.onDestroy();
//    	
//    	if(this.architectView != null)
//    		this.architectView.onDestroy();
//    }
//    
//    @Override
//    public void onLowMemory() {
//    	super.onLowMemory();
//    	
//    	if(this.architectView != null)
//    		this.architectView.onLowMemory();
//    }
//
//    /**
//     * <p>
//     * interface method of {@link ArchitectUrlListener} class
//     * called when an url with host "architectsdk://" is discovered
//     * 
//     * can be parsed and allows to react to events triggered in the ARchitect World
//     * </p>
//     */
//	@Override
//	public boolean urlWasInvoked(String url) {
//		//parsing the retrieved url string
//		List<NameValuePair> queryParams = URLEncodedUtils.parse(URI.create(url), "UTF-8");
//		
//		String id = "";
//		String tag = "";
//		// getting the values of the contained GET-parameters
//		for(NameValuePair pair : queryParams)
//		{
//			if(pair.getName().equals("id"))
//			{
//				id = pair.getValue();
//			}
//			
//			if(pair.getName().equals("tag"))
//			{
//				tag = pair.getValue();
//			}
//		}
//		
//		//get the corresponding poi bean for the given id
////		PoiBean bean = poiBeanList.get(Integer.parseInt(id));
////		//start a new intent for displaying the content of the bean
////		Intent intent = new Intent(this, PoiDetailActivity.class);
////		intent.putExtra("POI_NAME", bean.getName());
////		intent.putExtra("POI_DESC", bean.getDescription());
////		this.startActivity(intent);
//		if(tag.equals(""))
//			loadPopClick(id);
//		else
//		{
//			System.out.println("tag===="+tag);
//			if(tag.equals("1"))
//			{
//				selectpkid = id;
//				dlayout.setVisibility(View.VISIBLE);
//				
//				List<Map<String,Object>> dlist = storelist;
//			    Map<String,Object> map = getMap(dlist,selectpkid);
//			    final String imgurl = (String)map.get("imageurl");
//				String storeName = (String)map.get("storeName");
//				String storeDesc = (String)map.get("storeDesc");
//				String lons = (String)map.get("longItude");
//				String lats = (String)map.get("woof");
//				
//				new Thread(){
//					public void run(){
//						Message msg = new Message();
//						msg.what = 3;
//						Bitmap bit = getImageBitmap(imgurl);
//						msg.obj = bit;
//						handler.sendMessage(msg);
//					}
//				}.start();
//				
//				double distance = gps2m(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(lats),Double.valueOf(lons));
//				String juli = String.valueOf((int)distance);
//				
//				TextView ntxt = (TextView)findViewById(R.id.name);
//				ntxt.setText(storeName);
//				
//				TextView dtxt = (TextView)findViewById(R.id.descri);
//				dtxt.setText(storeDesc);
//				
//				jtxt = (TextView)findViewById(R.id.juli);
//				jtxt.setText("距离您大约："+juli+"米");
//			}
//			else if(tag.equals("2"))
//			{
//				dlayout.setVisibility(View.INVISIBLE);
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * method for creating random locations in the vicinity of the user
//	 * @return array with lat and lon values as doubles
//	 */
//	private double[] createRandLocation() {
//		 
//		if(loc != null)
//		{
//			return new double[]{ loc.getLatitude() + ((Math.random() - 0.5) / 500), loc.getLongitude() + ((Math.random() - 0.5) / 500)};
//		}
//		else
//			return new double[]{ TEST_LATITUDE + ((Math.random() - 0.5) / 500), TEST_LONGITUDE + ((Math.random() - 0.5) / 500)};
//	}
//	
//	private double[] createRandLocation2(float lat,float log,float alt) {
//		return new double[]{ lat, log, alt};
//	}
//
//	/**
//	 * loads a sample architect world and
//	 * creates a definable amount of pois in beancontainers 
//	 * and converts them into a jsonstring that can be sent to the framework
//	 * @throws IOException exception thrown while loading an Architect world
//	 */
//	private void loadSampleWorld() throws IOException {
//		this.architectView.load("tutorial1.html");
//
//		JSONArray array = new JSONArray();
//		poiBeanList = new ArrayList<PoiBean>();
//		try {
//			List<Map<String,Object>> dlist = storelist;
//			
//			if(dlist != null)
//			{
//				int size = 15;
////				if(dlist.size() < 15)
//					size = dlist.size();
//				for(int i=0;i<size;i++)
//		        {
//		        	Map map = dlist.get(i);
//		        	String pkid = (String)map.get("pkid");
//					String imgurl = (String)map.get("imageurl");
//					String lons = (String)map.get("longItude");
//					String lats = (String)map.get("woof");
//					String altitude = (String)map.get("altitude");
//					String imgmaping = (String)map.get("imgmaping");
//					String storeName = (String)map.get("storeName");
//					String storeDesc = (String)map.get("storeDesc");
//					String juli = (String)map.get("juli2");
//					if(storeDesc != null)
//					{
//						storeDesc = storeDesc.replaceAll("“", "").replaceAll("”", "").replaceAll("\"", "");
//					}
//					
//					if(lats != null && !lats.equals(""))
//					{
//						int lat2 = (int)(Double.valueOf(lats) * 1E6);
//				        int lon2 = (int)(Double.valueOf(lons) * 1E6);
//				        double[] location = createRandLocation2(Float.valueOf(lats),Float.valueOf(lons),Float.valueOf(altitude));
//				        
//						double distance = gps2m(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(lats),Double.valueOf(lons));
//						juli = String.valueOf((int)distance);
////						System.out.println("imgurl======="+imgurl);
////						Drawable drawable2 = null;
////						if(imgmaping != null && !imgmaping.equals(""))
////						{
////							int imgid = this.getResources().getIdentifier(this.getPackageName()+":drawable/"+imgmaping,null,null);
////					        drawable2 = this.getResources().getDrawable(imgid);
////						}
////						else
////						{
////							drawable2 = this.getResources().getDrawable(R.drawable.map_unknown_icon);
////						}
////				        if(drawable2 != null)
////				        {
////							
////				        }
//				        PoiBean bean = new PoiBean(
//								pkid,
//								storeName,
//								storeDesc,imgmaping,juli, (int) (Math.random() * 3),location[0], location[1], location[2]);
//				        
//						array.put(bean.toJSONObject());
//						poiBeanList.add(bean);
//					}
//		        }
//				
//			}
//			System.out.println("wikitudearray====="+array.toString());
//		this.architectView.callJavascript("newData2('" + array.toString() + "');");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//	}
//
//	/**
//	 * listener method called when the location of the user has changed
//	 * used for informing the ArchitectView about a new location of the user
//	 */
////	@Override
////	public void onLocationChanged(Location loc) {
//		// IMPORTANT: 
//		// use this method for informing the SDK about a location change by the user
//		// for simplicity not used in this example
//		
//		//inform ArchitectView about location changes
////		if(this.architectView != null)
////			this.architectView.setLocation((float)(loc.getLatitude()), (float)(loc.getLongitude()), loc.getAccuracy());
////	}
//	
//	private final LocationListener locationListener = new LocationListener() {
//		public void onLocationChanged(Location location) {
//			if(location != null)
//			{
////				if(SimpleARBrowserActivity.this.architectView != null)
////					SimpleARBrowserActivity.this.architectView.setLocation((float)(location.getLatitude()), (float)(location.getLongitude()), location.getAccuracy());
//				updateWithNewLocation(location);
//			}
//		}
//
//		public void onProviderDisabled(String provider) {
//			updateWithNewLocation(null);
//		}
//
//		public void onProviderEnabled(String provider) {
//		}
//
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//		}
//	};
//	
//	private final LocationListener locationListener2 = new LocationListener() {
//		public void onLocationChanged(Location location) {
//			if(location != null)
//			{
////				if(SimpleARBrowserActivity.this.architectView != null)
////					SimpleARBrowserActivity.this.architectView.setLocation((float)(location.getLatitude()), (float)(location.getLongitude()), location.getAccuracy());
//				updateWithNewLocation(location);
//			}
//		}
//
//		public void onProviderDisabled(String provider) {
//			updateWithNewLocation(null);
//		}
//
//		public void onProviderEnabled(String provider) {
//		}
//
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//		}
//	};
//
//	public void showreal()
//    {
//    	try{
//    		LinearLayout layout = (LinearLayout)findViewById(R.id.liner);
//    		layout.removeAllViews();
//    		
//    		List<Map<String,Object>> dlist = myapp.getStorelist();
//    		
//			for(int i=0;i<dlist.size();i++)
//			{
//				Map map = dlist.get(i);
//				String lons = (String)map.get("longItude");
//				String lats = (String)map.get("woof");
//				
//				double angle = getYawByPoints(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(lats),Double.valueOf(lons));
//	            
//				ImageView img = new ImageView(this);
//				LayoutParams lp2 = new LayoutParams(10,
//						10);
//				if ((int) angle < 90) {
//					int v = (int) angle;
//					lp2.leftMargin = 67 - 6 - (v / 2);
//					lp2.topMargin = 67 - 6 - ((90 - (int) angle) / 2);
//				} else if ((int) angle < 180) {
//					int v = 180 - (int) angle;
//					lp2.leftMargin = 67 - 6 - (v / 2);
//					lp2.topMargin = 67 - 6 + ((180 - (int) angle) / 2);
//				} else if ((int) angle < 270) {
//					int v = (int) angle - 180;
//					lp2.leftMargin = 67 - 6 + (v / 2);
//					lp2.topMargin = 67 - 6 + ((270 - (int) angle) / 2);
//				} else if ((int) angle < 359) {
//					int v = 359 - (int) angle;
//					lp2.leftMargin = 67 - 6 + (v / 2);
//					lp2.topMargin = 67 - 6 - ((359 - (int) angle) / 2);
//				}
//				// lp2.leftMargin = 67 - 6 - (int)xd;
//				// lp2.topMargin = 67 - 6 - (int)yd;
//				img.setLayoutParams(lp2);
//				img.setImageResource(R.drawable.round);
//
//				layout.addView(img);
//			}
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    }
//	
//	/**
//	    * 计算两点间的正北方向
//	    * @param lon1 点1
//	    * @param lat1
//	    * @param lon2 点2
//	    * @param lat2
//	    * @return
//	    */
//	    public double getYawByPoints(double lat1,double lon1,double lat2,double lon2)
//	    {
//		    double yaw = 0;
//		    double dlon = lon2-lon1;
//		    double dlat = lat2-lat1;
//		    double dMin = 0.00001;
//		    if(Math.abs(dlon)<dMin)
//		    {
//			    if(dlat>dMin)
//			    	yaw = 0;
//			    else if(dlat<dMin)
//			    	yaw = 180;
//			    return yaw;
//		    }
//		    if(Math.abs(dlat)<dMin)
//		    {
//			    if(dlon>dMin)
//			    	yaw = 90;
//			    else if(dlon<dMin)
//			    	yaw = 270;
//			    return yaw;
//		    }
//		    double kk = (dlon)/(dlat);
//		    if(dlon>dMin&&dlat>dMin)
//		    {
//		    	yaw = (Math.atan(kk)*180/Math.PI);
//		    }
//		    else if(dlon>dMin&&dlat<dMin)
//		    {
//		    	yaw = (Math.atan(kk)*180/Math.PI)+180;
//		    }
//		    else if(dlon<dMin&&dlat<dMin)
//		    {
//		    	yaw = (Math.atan(kk)*180/Math.PI)+180;
//		    }
//		    else 
//		    {
//		    	yaw = (Math.atan(kk)*180/Math.PI)+360;
//		    }
//		
//		    return yaw;
//	    }
//	
////	@Override
////	public void onAccuracyChanged(int arg0, int arg1) {
////		// TODO Auto-generated method stub
////		
////	}
////
////	@Override
////	public void onSensorChanged(int sensor, float[] values) {
////		// TODO Auto-generated method stub
////		synchronized (this) {
////			if (sensor == SensorManager.SENSOR_ORIENTATION) {
////				Log.d("compass", "onSensorChanged: " + sensor + ", x: " + values[0]
////						+ ", y: " + values[1] + ", z: " + values[2]);
////
////				// OrientText.setText("--- NESW ---");
////				if (Math.abs(values[0] - DegressQuondam) < 1)
////					return;
////				
////				if (DegressQuondam != -values[0])
////					AniRotateImage(-values[0],values[0]);
////			}
////		}
////	}
//	
//	private void AniRotateImage(float fDegress,float ff) {
//		Log.d("compass", "Degress: " + DegressQuondam + ", " + fDegress);
//		myAni = new RotateAnimation(DegressQuondam, fDegress,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		myAni.setDuration(300);
//		myAni.setFillAfter(true);
//
//		ImgCompass.startAnimation(myAni);
//
//		DegressQuondam = fDegress;
//	}
//}