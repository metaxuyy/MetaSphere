package ms.activitys.travel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.product.ProductImageSwitcher;
import ms.globalclass.TomorrowWeatherPullParse;
import ms.globalclass.TomorrowWeatherVO;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class TravelDetatilActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private LocationManager locationManager;
	private int storeindex;
	private Map<String,Object> storemap;
	private String pkidsel;
	private ImageView lbimgview;
	private TextView txtlbdoc;
	private String clicktag = "1";
//	private ProgressBar pb2;
	private TextView tvShow;
	private String landscapeId;
	private List<TomorrowWeatherVO> tvolist;
	private boolean isnfc = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_detailed);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
//		pb2 = (ProgressBar)findViewById(R.id.probar2);
		
		Bundle bunde = this.getIntent().getExtras();
		storeindex = bunde.getInt("index");
		landscapeId = bunde.getString("landscapeId");
		
		if(bunde.containsKey("nfc"))
		{
			List<Map<String,Object>> list = myapp.getNfcStoreList();
			storemap = list.get(storeindex);
			isnfc = true;
		}
		else
		{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			storemap = list.get(storeindex);
		}
		
		getGPSLocation();
		
		loadThreadData();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(isnfc)
			{
				Intent intent = new Intent();
			    intent.setClass( this,MainTabActivity.class);
			    Bundle bundle = new Bundle();
//				bundle.putString("role", "Cleaner");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//关闭显示的Activity
			}
			else
			{
				TravelDetatilActivity.this.setResult(RESULT_OK, getIntent());
				TravelDetatilActivity.this.finish();
			}
			return false;
		}
		return false;
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
//					String storeid = (String)storemap.get("storeid");
					
					jobj = api.getTravelList("","all",landscapeId);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getTravelListDate(jArr);
					}
					myapp.setTravelAllList(list);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				
				if(dlist != null)
				{
					openTravelDetailed(dlist.get(0));
				}
				break;
			case 1:
				Bitmap bitm = (Bitmap)msg.obj;
				if(bitm != null)
				{
//					pb2.setVisibility(View.GONE);
					lbimgview.setImageBitmap(bitm);
					lbimgview.setVisibility(View.VISIBLE);
					lbimgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
						    intent.setClass( TravelDetatilActivity.this,ProductImageSwitcher.class);
						    Bundle bundle = new Bundle();
							bundle.putString("productId", pkidsel);
							intent.putExtras(bundle);
						    startActivity(intent);//开始界面的跳转函数
						    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
						}
					});
				}
				else
				{
					lbimgview.setImageBitmap(null);
					makeText(TravelDetatilActivity.this.getString(R.string.attractions_lable_13));
				}
				break;
			case 2:
				String strData = (String)msg.obj;
				showWeather(strData);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openTravelDetailed(final Map<String,Object> map){
		try{
			pkidsel = (String) map.get("pkid"); 
			final String landscapeName = (String) map.get("landscapeName");
			String landscapeNo = (String) map.get("landscapeNo");
			String landscapeDesc = (String) map.get("landscapeDesc");
			final String img = (String) map.get("img");
			final String notesnumber = (String) map.get("notesnumber");
			final String pinlun = (String) map.get("pinlun");
			final String voidname = (String)map.get("voidname");
			final String voidpath = (String)map.get("voidpath");
			final String storeId = (String)map.get("storeId");
			final String longtude = (String)map.get("longtude");
			final String woof = (String)map.get("woof");
			final String flatlong = (String)map.get("flatlong");
			final String flatwoof = (String)map.get("flatwoof");
			final String address = (String)map.get("address");
			final String price = (String)map.get("price");
			final float score= (Float)map.get("score");
			String openHour = (String)map.get("openHours");
			
			lbimgview = (ImageView)findViewById(R.id.lbimg);
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					
					Bitmap bitm = getImageBitmap(img,true);
					msg.obj = bitm;
					handler.sendMessage(msg);
				}
			}.start();
			
			RatingBar rb = (RatingBar)findViewById(R.id.rb);
			rb.setRating(score);
			
			TextView areaTicket = (TextView)findViewById(R.id.txt_areaTicket);
			if(price == null)
				areaTicket.setText(this.getString(R.string.attractions_lable_3));
			else
				areaTicket.setText(price);
			
			TextView areaAddress = (TextView)findViewById(R.id.txt_areaAddress);
			areaAddress.setText(address);
			
			TextView hourtext = (TextView)findViewById(R.id.txt_openHour);
			hourtext.setText(openHour);
			
			TextView txttitle = (TextView)findViewById(R.id.txtv1);
			txttitle.setText(landscapeName);
			
//			TextView txtjanjie = (TextView)findViewById(R.id.txt_jianjie);
//			txtjanjie.setText(landscapeName+"简介");
			
			txtlbdoc = (TextView)findViewById(R.id.lbdoc);
			txtlbdoc.setText(landscapeDesc);
			
//			TextView txt_youji = (TextView)findViewById(R.id.txt_youji);
//			if(notesnumber.equals("0"))
//				txt_youji.setText("游记（暂无）");
//			txt_youji.setText("游记（"+notesnumber+"篇）");
//			
//			TextView txt_yiyou = (TextView)findViewById(R.id.txt_yiyou);
//			if(pinlun.equals("0"))
//				txt_yiyou.setText("到此一游和评论（暂无）");
//			else
//				txt_yiyou.setText("到此一游和评论（"+pinlun+"人）");
			
			tvShow = (TextView)findViewById(R.id.weather_txt);
			
			if(myapp.getTvolist() != null && myapp.getTvolist().size() > 0)
			{
				showWeather(myapp.getTvolist());
			}
			else
			{
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 2;
						
						String strData = searchWeather();
						msg.obj = strData;
						handler.sendMessage(msg);
					}
				}.start();
			}
			
//			LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout1);
//			layout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(clicktag.equals("1"))
//					{
//						txtlbdoc.setVisibility(View.VISIBLE);
//						clicktag = "2";
//					}
//					else
//					{
//						txtlbdoc.setVisibility(View.GONE);
//						clicktag = "1";
//					}
//				}
//			});
			
			Button layout2 = (Button)findViewById(R.id.youji_btn);
			if(notesnumber.equals("0"))
				layout2.setText(this.getString(R.string.attractions_lable_4));
			layout2.setText(this.getString(R.string.attractions_lable_4)+"（"+notesnumber+"）");
			layout2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
//					if(Integer.valueOf(notesnumber) > 0)
//					{
						Intent intent = new Intent();
					    intent.setClass( TravelDetatilActivity.this,TravelNotesListActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("lvid", pkidsel);
						bundle.putString("storeId", storeId);
						intent.putExtras(bundle);
					    startActivity(intent);//开始界面的跳转函数
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//				    }
//					else
//					{
//						makeText("该景点没有游记");
//					}
				}
			});
			
			Button layout3 = (Button)findViewById(R.id.yiyon_btn);
			if(pinlun.equals("0"))
				layout3.setText(this.getString(R.string.attractions_lable_5));
			else
				layout3.setText(this.getString(R.string.attractions_lable_5)+"（"+pinlun+"）");
			layout3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Intent intent = new Intent();
				    intent.setClass( TravelDetatilActivity.this,TravelCommentListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("lsid", pkidsel);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			Button layout4 = (Button)findViewById(R.id.void_btn);
			layout4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(voidname.equals(""))
						makeText(TravelDetatilActivity.this.getString(R.string.attractions_lable_6));
					else
					{
//						if(voidname.indexOf(".mp4") >= 0)
//						{
//							Intent intent = new Intent();
//						    intent.setClass( TravelDetatilActivity.this,VideoViewDemo.class);
//						    Bundle bundle = new Bundle();
//							bundle.putString("path", voidpath);
//							bundle.putString("name", landscapeName);
//							intent.putExtras(bundle);
//						    startActivity(intent);//开始界面的跳转函数
//						    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//						}
//						else
//						{
							Intent it = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(voidpath);
							it.setDataAndType(uri, "video/mp4");
							startActivity(it);
//						}
					}
				}
			});
			
			ImageView commentBtn = (ImageView)findViewById(R.id.comment_btn);
			commentBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelDetatilActivity.this,TravelNotesActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("pkid", pkidsel);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			ImageView locationbtn = (ImageView)findViewById(R.id.location_btn);
			locationbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelDetatilActivity.this,TravelCompassActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("longtude", flatlong);
					bundle.putString("woof", flatwoof);
					bundle.putString("name", landscapeName);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			RelativeLayout layWeather = (RelativeLayout)findViewById(R.id.layWeather);
			layWeather.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showWeatherWind();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void getGPSLocation()
    {
    	double latitude,longitude =0.0;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(serviceName);
//        String provider = LocationManager.GPS_PROVIDER;
        
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
					
					
					String CountryName = ""; //国家名字
					String CountryNameCode = ""; //国家代码
					String LocalityName = ""; //城市名
					String ThoroughfareName = ""; //路名
					String quhao = "";//区号
					String menpai = ""; //门牌号
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
								System.out.println("国家名字===="+CountryName);
								myapp.setCountry(CountryName);
								System.out.println("国家代码===="+CountryNameCode);
								System.out.println("城市名===="+LocalityName);
								myapp.setCity(LocalityName);
								System.out.println("路名===="+ThoroughfareName);
								myapp.setRoad(ThoroughfareName);
								System.out.println("区名===="+quhao);
								myapp.setArea(quhao);
								System.out.println("门牌号===="+menpai);
								myapp.setNumbers(menpai);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			latLongString = this.getString(R.string.login_lable_17);
		}
		System.out.println("您当前的位置是:\n" + latLongString);
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
	
	public List<Map<String,Object>> getTravelListDate(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String landscapeName = ""; 
				if(dobj.has("landscapeName"))
					landscapeName = (String) dobj.get("landscapeName");
				
				String landscapeNo = ""; 
				if(dobj.has("landscapeNo"))
					landscapeNo = (String) dobj.get("landscapeNo");
				
				String landscapeDesc = ""; 
				if(dobj.has("landscapeDesc"))
					landscapeDesc = (String) dobj.get("landscapeDesc");
				
				String img = ""; 
				if(dobj.has("img"))
					img = (String) dobj.get("img");
				
				String notesnumber = ""; 
				if(dobj.has("notesnumber"))
					notesnumber = (String) dobj.get("notesnumber");
				
				String pinlun = ""; 
				if(dobj.has("pinlun"))
					pinlun = (String) dobj.get("pinlun");
				
				int score = 0; 
				if(dobj.has("score"))
					score = (Integer) dobj.get("score");
				
				String price = "0.00"; 
				if(dobj.has("price"))
					price = (String)dobj.get("price");
				
				String longtude = ""; 
				if(dobj.has("longtude"))
					longtude = (String) dobj.get("longtude");
				
				String woof = ""; 
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof");
				
				String address = ""; 
				if(dobj.has("address"))
					address = (String) dobj.get("address");
				
				String voidname = ""; 
				if(dobj.has("voidname"))
					voidname = (String) dobj.get("voidname");
				
				String voidpath = ""; 
				if(dobj.has("voidpath"))
					voidpath = (String) dobj.get("voidpath");
				
				String storeId = ""; 
				if(dobj.has("storeId"))
					storeId = (String) dobj.get("storeId");
				
				String flatlong = ""; 
				if(dobj.has("flatlong"))
					flatlong = (String) dobj.get("flatlong");
				
				String flatwoof = ""; 
				if(dobj.has("flatwoof"))
					flatwoof = (String) dobj.get("flatwoof");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("landscapeName", landscapeName);
				map.put("landscapeNo", landscapeNo);
				map.put("landscapeDesc", landscapeDesc);
				map.put("img", img);
				map.put("notesnumber", notesnumber);
				map.put("score", Float.valueOf(score));
				if(price.equals("0.00"))
				{
					map.put("pimg", null);
					map.put("price", null);
				}
				else
				{
					map.put("pimg", R.drawable.price);
					map.put("price", price);
				}
				map.put("pinlun", pinlun);
				map.put("pnumber", this.getString(R.string.attractions_lable_14)+" " + pinlun +" "+this.getString(R.string.attractions_lable_15));
				map.put("longtude", longtude);
				map.put("woof", woof);
				map.put("flatlong", flatlong);
				map.put("flatwoof", flatwoof);
				map.put("address", address);
				map.put("voidname", voidname);
				map.put("voidpath", voidpath);
				map.put("storeId", storeId);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private String searchWeather() {
//		List<Map<String,Object>> lists = myapp.getMyCardsAll();
//		Map<String,Object> map = lists.get(storeindex);
		String storelat = (String)storemap.get("woof");
		String storelng = (String)storemap.get("longItude");
		
		int a=(int)(Double.valueOf(storelat) * 1E6);
		int b=(int)(Double.valueOf(storelng) * 1E6);
		
		String strUrl ="http://www.google.com/ig/api?hl=zh-cn&weather=,,,"+a+","+b;
		System.out.println(strUrl);
		
		String strData = getResponse(strUrl);

		return strData;
		// SAX解析xml
//        try {
//            showWeather(strData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

	}
	
	//根据地址 获得xml的String
	protected String getResponse(String queryURL) {
        URL url;
        try {
            url = new URL(queryURL.replace(" ", "%20"));
            URLConnection urlconn = url.openConnection();
            urlconn.connect();

            InputStream is = urlconn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer buf = new ByteArrayBuffer(50);

            int read_data = -1;
            while ((read_data = bis.read()) != -1) {
                buf.append(read_data);
            }
            // String resp = buf.toString();
            String resp = EncodingUtils.getString(buf.toByteArray(), "GBK");
            return resp;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
	
	//显示天气
	private void showWeather(String strData) {
		String strTemp = "";
		
		TomorrowWeatherPullParse tom = new TomorrowWeatherPullParse();
		
		tvolist = tom.getGoogleWeatherList(strData);
		String iconimg = "";
//		for(int i=0;i<tvolist.size();i++)
//		{
//			TomorrowWeatherVO tvo = tvolist.get(i);
//			if(i == 0)
//			{
//				iconimg = tvo.getIcon();
//				strTemp += "明天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//			else if(i == 1)
//			{
//				strTemp += "后天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//			else if(i == 2)
//			{
//				strTemp += "大后天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//		}
		
		TomorrowWeatherVO tvo = tvolist.get(0);
		strTemp += tvo.getWeek() + " "+this.getString(R.string.attractions_lable_7)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
		iconimg = tvo.getIcon();
		
		Bitmap img_off = getImageBitmap("http://www.google.com"+iconimg,false);
		ImageView imgicon = (ImageView)findViewById(R.id.weather_icon);
		imgicon.setImageBitmap(img_off);
//		tvShow.setCompoundDrawables(img_off, null, null, null); //设置左图标
		tvShow.setText(strTemp);
	}
	
	//显示天气
	private void showWeather(List<TomorrowWeatherVO> tvolist) {
		String strTemp = "";
		
		TomorrowWeatherPullParse tom = new TomorrowWeatherPullParse();
		
		String iconimg = "";
		for(int i=0;i<tvolist.size();i++)
		{
			TomorrowWeatherVO tvo = tvolist.get(i);
			if(i == 0)
			{
				iconimg = tvo.getIcon();
				strTemp += this.getString(R.string.attractions_lable_10)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
			else if(i == 1)
			{
				strTemp += this.getString(R.string.attractions_lable_11)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
			else if(i == 2)
			{
				strTemp += this.getString(R.string.attractions_lable_12)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
		}
		
		Bitmap img_off = getImageBitmap("http://www.google.com"+iconimg,false);
		ImageView imgicon = (ImageView)findViewById(R.id.weather_icon);
		imgicon.setImageBitmap(img_off);
//		tvShow.setCompoundDrawables(img_off, null, null, null); //设置左图标
		tvShow.setText(strTemp);
	}
	
	public void showWeatherWind()
	{
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			
			final ListView view = new ListView(this);
			
			List<Map<String,String>> weatherList = new ArrayList<Map<String,String>>();
			for(int i=0;i<tvolist.size();i++)
			{
				TomorrowWeatherVO tvo = tvolist.get(i);
				String iconimg = "http://www.google.com"+tvo.getIcon();
				String context = tvo.getWeek() + " "+this.getString(R.string.attractions_lable_7)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
				
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("iconimg", iconimg);
				map.put("context", context);
				
				weatherList.add(map);
			}
			
			SpecialAdapter listItemAdapter = new SpecialAdapter(this, weatherList,// 数据源
					R.layout.menu_item_menu,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "iconimg", "context" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.mimg, R.id.menutype },share,"YU");
			
			view.setDividerHeight(0);
			// 添加并且显示
			view.setAdapter(listItemAdapter);
			
			builder.setView(view).setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); //设置宽和高
			alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Bitmap getImageBitmap(String value,boolean b)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		System.out.println("menulist_imageurl==="+value);
		if(value == null)
			return null;
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
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			if(b)
				bitmap = Bitmap.createScaledBitmap(bitmap,130,130,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
