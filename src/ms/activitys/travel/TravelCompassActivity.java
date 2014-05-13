package ms.activitys.travel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.StreamTool;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class TravelCompassActivity extends Activity implements SensorListener {
	private static final String TAG = "Compass";

	private ImageView ImgCompass;
	private TextView OrientText;
	private SensorManager sm = null;
	private RotateAnimation myAni = null;
	private float DegressQuondam = 0.0f;
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private LocationManager locationManager;
	private String longtude;
	private String woof;
	private String landscapeName;

	private AlphaAnimation myAnimation_Alpha;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "Compass: onCreate");
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.travel_compass_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		longtude = this.getIntent().getExtras().getString("longtude");
		woof = this.getIntent().getExtras().getString("woof");
		landscapeName = this.getIntent().getExtras().getString("name");
		
		if(StreamTool.isNetworkVailable(this))
		{
				getGPSLocation();
		}
		
		OrientText = (TextView) findViewById(R.id.OrientText);
		ImgCompass = (ImageView) findViewById(R.id.ivCompass);
		
		Button backBtn = (Button)findViewById(R.id.brack_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TravelCompassActivity.this.setResult(RESULT_OK, getIntent());
				TravelCompassActivity.this.finish();
			}
		});
		
		Button realBtn = (Button)findViewById(R.id.real_btn);
		realBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showRealActivity();
			}
		});
	}
	
	public void showRealActivity()
	{
		Intent intent = new Intent();
	    intent.setClass( this,RealActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("longtude", longtude);
		bundle.putString("woof", woof);
		bundle.putString("name", landscapeName);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	@Override
	public void onStart() {
		Log.e(TAG, "Compass: onStart");
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);

		super.onStart();
		
	}	

	@Override
	protected void onResume() {
		Log.e(TAG, "Compass: onResume");
		super.onResume();
		sm.registerListener(this, SensorManager.SENSOR_ORIENTATION
		 | SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "Compass: onStop");
		sm.unregisterListener(this);		
		super.onStop();
	}

	public void onPause() {
		Log.e(TAG, "Compass: onPause");
		locationManager.removeUpdates(locationListener);
		super.onPause();
	}

	public void onDestroy() {
		Log.e(TAG, "Compass: onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, this.getString(R.string.travel_lable_24)).setIcon(R.drawable.icon);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			AniRotateImage(DegressQuondam + 90.0f);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void AniRotateImage(float fDegress) {
		Log.d(TAG, "Degress: " + DegressQuondam + ", " + fDegress);
		myAni = new RotateAnimation(DegressQuondam, fDegress,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		myAni.setDuration(300);
		myAni.setFillAfter(true);

		ImgCompass.startAnimation(myAni);

		DegressQuondam = fDegress;
	}

	public void onSensorChanged(int sensor, float[] values) {
		synchronized (this) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				Log.d(TAG, "onSensorChanged: " + sensor + ", x: " + values[0]
						+ ", y: " + values[1] + ", z: " + values[2]);

				// OrientText.setText("--- NESW ---");
				if (Math.abs(values[0] - DegressQuondam) < 1)
					return;
				
				switch ((int) values[0]) {
				case 0: // North 北
					OrientText.setText(this.getString(R.string.travel_lable_9));
					break;
				case 90: // East 东
					OrientText.setText(this.getString(R.string.travel_lable_10));
					break;
				case 180: // South 南
					OrientText.setText(this.getString(R.string.travel_lable_11));
					break;
				case 270: // West 西
					OrientText.setText(this.getString(R.string.travel_lable_12));
					break;
				default: {
					int v = (int) values[0];
					if (v > 0 && v < 90) {
						OrientText.setText(this.getString(R.string.travel_lable_13) + v);
					}

					if (v > 90 && v < 180) {
						v = 180 - v;
						OrientText.setText(this.getString(R.string.travel_lable_14) + v);
					}

					if (v > 180 && v < 270) {
						v = v - 180;
						OrientText.setText(this.getString(R.string.travel_lable_15) + v);
					}
					if (v > 270 && v < 360) {
						v = 360 - v;
						OrientText.setText(this.getString(R.string.travel_lable_16) + v);
					}
				}
				}

//				((TextView) findViewById(R.id.OrientValue)).setText(String.valueOf(values[0]));

				if (DegressQuondam != -values[0])
					AniRotateImage(-values[0]);
			}

			// if (sensor == SensorManager.SENSOR_ACCELEROMETER) { // //}

		}
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		Log.d(TAG, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
	}
	
    private final double EARTH_RADIUS = 6378137.0;  
    // 计算两点距离
    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
       double radLat1 = (lat_a * Math.PI / 180.0);
       double radLat2 = (lat_b * Math.PI / 180.0);
       double a = radLat1 - radLat2;
       double b = (lng_a - lng_b) * Math.PI / 180.0;
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
              + Math.cos(radLat1) * Math.cos(radLat2)
              * Math.pow(Math.sin(b / 2), 2)));

       s = s * EARTH_RADIUS;
       s = Math.round(s * 10000) / 10000;

       return s;
    }
	
	// 计算方位角pab。
    private double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
       double d = 0;
       
       lat_a=lat_a*Math.PI/180;
       lng_a=lng_a*Math.PI/180;
       lat_b=lat_b*Math.PI/180;
       lng_b=lng_b*Math.PI/180;

       d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
       d=Math.sqrt(1-d*d);
       d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
       d=Math.asin(d)*180/Math.PI;
//       d = Math.round(d*10000);

       return d;

    }
    
    public static double GetBusDirection( double n1,double e1, double n2, double e2)
    {
        double e3 = 0;
        double n3 = 0;
        e3 = e1 + 0.005;
        n3 = n1;
        double a = 0;
        double b = 0;
        double c = 0;
        a = Distance(e1, n1, e3, n3);
        b = Distance(e3, n3, e2, n2);
        c = Distance(e1, n1, e2, n2);
        double cosB = 0;
        if ((a * c) != 0)
        {
            cosB = (a * a + c * c - b * b) / (2 * a * c);
        }
        double B = Math.acos(cosB) * 180 / Math.PI;

        if(n2<n1)
        {
            B=180+(180-B);
        }

        return B;
    }
    
    /**
    * 计算两点间的正北方向
    * @param lon1 点1
    * @param lat1
    * @param lon2 点2
    * @param lat2
    * @return
    */
    public double getYawByPoints(double lat1,double lon1,double lat2,double lon2)
    {
	    double yaw = 0;
	    double dlon = lon2-lon1;
	    double dlat = lat2-lat1;
	    double dMin = 0.00001;
	    if(Math.abs(dlon)<dMin)
	    {
		    if(dlat>dMin)
		    	yaw = 0;
		    else if(dlat<dMin)
		    	yaw = 180;
		    return yaw;
	    }
	    if(Math.abs(dlat)<dMin)
	    {
		    if(dlon>dMin)
		    	yaw = 90;
		    else if(dlon<dMin)
		    	yaw = 270;
		    return yaw;
	    }
	    double kk = (dlon)/(dlat);
	    if(dlon>dMin&&dlat>dMin)
	    {
	    	yaw = (Math.atan(kk)*180/Math.PI);
	    }
	    else if(dlon>dMin&&dlat<dMin)
	    {
	    	yaw = (Math.atan(kk)*180/Math.PI)+180;
	    }
	    else if(dlon<dMin&&dlat<dMin)
	    {
	    	yaw = (Math.atan(kk)*180/Math.PI)+180;
	    }
	    else 
	    {
	    	yaw = (Math.atan(kk)*180/Math.PI)+360;
	    }
	
	    return yaw;
    }
    
    public static double Distance(double n1, double e1, double n2, double e2)
    {
        double jl_jd = 102834.74258026089786013677476285;
        double jl_wd = 111712.69150641055729984301412873;
        double b = Math.abs((e1 - e2) * jl_jd);
        double a = Math.abs((n1 - n2) * jl_wd);
        return Math.sqrt((a * a + b * b));

    }
    
	public void getGPSLocation()
    {
    	double latitude,longitude =0.0;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(serviceName);
        //String provider = LocationManager.GPS_PROVIDER;
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
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
			double dd = location.getBearing();
			new Thread() {
				public void run() {
					Message msg = new Message();
					
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
						
						if(lat2 != 0)
						{
							gp =  new GeoPoint(lat2, lon2);
							
							double lat = gp.getLatitudeE6() / 1E6;
							double lng = gp.getLongitudeE6() / 1E6;
							
							json = geocodeAddr(lat,lng);
							
							myapp.setLatitude(gp.getLatitudeE6());
							myapp.setLongitude(gp.getLongitudeE6());
							myapp.setLat(String.valueOf(lat));
							myapp.setLng(String.valueOf(lng));
						}
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
					
					if(myapp.getLat() != null)
					{
						msg.what = 0;
						double distance = gps2m(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(woof),Double.valueOf(longtude));
						double pab = getYawByPoints(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(woof),Double.valueOf(longtude));
						String pabstr = getDirection((int)pab);
						
						Map<String,String> map = new HashMap<String,String>();
						map.put("distance", String.valueOf((int)distance));
						map.put("angle", pabstr);
						msg.obj = map;
					
						handler.sendMessage(msg);
					}
					else
					{
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
			}.start();
		} else {
			latLongString = this.getString(R.string.login_lable_17);
		}
		System.out.println("您当前的位置是:\n" + latLongString);
	}
	
	public String getDirection(int pab)
	{
		String pabstr = "";
			switch ((int) pab) {
			case 0: // North 北
				pabstr = this.getString(R.string.travel_lable_9);
				break;
			case 90: // East 东
				pabstr = this.getString(R.string.travel_lable_10);
				break;
			case 180: // South 南
				pabstr = this.getString(R.string.travel_lable_11);
				break;
			case 270: // West 西
				pabstr = this.getString(R.string.travel_lable_12);
				break;
			default: {
				int v = (int) pab;
				if (v > 0 && v < 90) {
					pabstr = this.getString(R.string.travel_lable_13) + v;
				}
	
				if (v > 90 && v < 180) {
					v = 180 - v;
					pabstr = this.getString(R.string.travel_lable_14) + v;
				}
	
				if (v > 180 && v < 270) {
					v = v - 180;
					pabstr = this.getString(R.string.travel_lable_15) + v;
				}
				if (v > 270 && v < 360) {
					v = 360 - v;
					pabstr = this.getString(R.string.travel_lable_16) + v;
				}
			}
			
		}
		return pabstr;
	}
	
	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String,String> map = (Map<String,String>)msg.obj;
				String distance = map.get("distance");
				String angle = map.get("angle");
				
				setTextValue(angle,distance);
				break;
			case 1:
				getGPSLocation();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void setTextValue(String angle,String distance)
	{
		TextView txt = (TextView)findViewById(R.id.local_txt);
		txt.setText(this.getString(R.string.travel_lable_25)+angle+"\n "+this.getString(R.string.travel_lable_26)+" "+distance+" "+this.getString(R.string.travel_lable_7));
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

}