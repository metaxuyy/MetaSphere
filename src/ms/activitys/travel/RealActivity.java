package ms.activitys.travel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RealActivity extends Activity implements OnClickListener, Callback, SensorListener{

	private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean mPreviewRunning;
    
    private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
    
    private float DegressQuondam = 0.0f;
    private RotateAnimation myAni = null;
    private ImageView ImgCompass;
    private SensorManager sm = null;
    private LocationManager locationManager;
    private HorizontalScrollView ai;
    private double angle;
    private String longtude;
	private String woof;
	private String landscapeName;
	private MediaPlayer mMediaPlayer;
	private boolean playertag = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_view);
        
        myapp = (MyApp)this.getApplicationContext();
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
        longtude = this.getIntent().getExtras().getString("longtude");
		woof = this.getIntent().getExtras().getString("woof");
		landscapeName = this.getIntent().getExtras().getString("name");
		
		mSurfaceView = (SurfaceView) findViewById(R.id.real_view_position_surfaceview);
	     mSurfaceHolder = mSurfaceView.getHolder();
	     mSurfaceHolder.setFixedSize(800, 480);
	     mSurfaceHolder.addCallback(this);
	     mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        
	     ImgCompass = (ImageView)findViewById(R.id.img);
	     
//	     Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//	     try{
//		     mMediaPlayer = new MediaPlayer();
//		     mMediaPlayer.setDataSource(this, alert);
//		     final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		     if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//		     mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//		     mMediaPlayer.setLooping(true);
//		     mMediaPlayer.prepare();
//		     }
//	     }catch (IllegalStateException e) {
//	    	 e.printStackTrace(); 
//	     } catch (IOException e) { 
//	    	 e.printStackTrace(); 
//	     }
	        
	     ai = (HorizontalScrollView)findViewById(R.id.real_view_scroll_view);
	     ai.setSmoothScrollingEnabled(true);
	     
        if(StreamTool.isNetworkVailable(this))
		{
				getGPSLocation();
		}
        
    }
    
    public void showreal(String distance)
    {
    	try{
    		LinearLayout layout = (LinearLayout)findViewById(R.id.liner);
    		layout.removeAllViews();
    		
    		angle = getYawByPoints(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(woof),Double.valueOf(longtude));
            
//          double xd = 61*Math.cos(angle);
//          double yd = 61*Math.sin(angle);
    		
    		TextView nametxt = (TextView)findViewById(R.id.real_view_title);
    		nametxt.setText(landscapeName);
    		
    		TextView distancetxt = (TextView)findViewById(R.id.real_view_distance);
    		distancetxt.setText(getString(R.string.map_lable_8)+":"+distance+" "+getString(R.string.travel_lable_7));
          
	          ImageView img = new ImageView(this);
	          LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	          if((int)angle < 90)
	          {
	          	int v = (int)angle;
	  	        lp2.leftMargin = 67 - 6 - (v/2);
	  	        lp2.topMargin = 67 - 6 - ((90-(int)angle)/2);
	          }
	          else if((int)angle < 180)
	          {
	          	int v = 180 - (int)angle;
	  	        lp2.leftMargin = 67 - 6 - (v/2);
	  	        lp2.topMargin = 67 - 6 + ((180-(int)angle)/2);
	          }
	          else if((int)angle < 270)
	          {
	          	int v = (int)angle - 180;
	  	        lp2.leftMargin = 67 - 6 + (v/2);
	  	        lp2.topMargin = 67 - 6 + ((270-(int)angle)/2);
	          }
	          else if((int)angle < 359)
	          {
	          	int v = 359 - (int)angle;
	  	        lp2.leftMargin = 67 - 6 + (v/2);
	  	        lp2.topMargin = 67 - 6 - ((359-(int)angle)/2);
	          }
	//          lp2.leftMargin = 67 - 6 - (int)xd;
	//          lp2.topMargin = 67 - 6 - (int)yd;
	          img.setLayoutParams(lp2);
	          img.setImageResource(R.drawable.round);
	          
	          layout.addView(img);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
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
//						angle = getYawByPoints(Double.valueOf(myapp.getLat()),Double.valueOf(myapp.getLng()),Double.valueOf(woof),Double.valueOf(longtude));
						
						msg.obj = String.valueOf((int)distance);
					
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
	
	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String distance = (String)msg.obj;
				
				showreal(distance);
				break;
			case 1:
				getGPSLocation();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
    
    @Override
	public void onStart() {
		Log.e("compass", "Compass: onStart");
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);

		super.onStart();
		
	}	

	@Override
	protected void onResume() {
		Log.e("compass", "Compass: onResume");
		super.onResume();
		sm.registerListener(this, SensorManager.SENSOR_ORIENTATION
		 | SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		Log.e("compass", "Compass: onStop");
		sm.unregisterListener(this);
		mCamera.stopPreview();
		mCamera.release();
		mCamera=null;
		
//		mMediaPlayer.stop();
//		mMediaPlayer = null;
		super.onStop();
	}

	public void onPause() {
		Log.e("compass", "Compass: onPause");
//		locationManager.removeUpdates(locationListener);
		super.onPause();
	}

	public void onDestroy() {
		Log.e("compass", "Compass: onDestroy");
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mCamera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
			Display display  = wm.getDefaultDisplay();//得到当前屏幕
			Camera.Parameters parameters = mCamera.getParameters();//得到摄像头的参数
//			parameters.setPreviewSize(display.getWidth(), display.getHeight());//设置预览照片的大小
			parameters.setPreviewFrameRate(3);//设置每秒3帧
			parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的格式
			parameters.setJpegQuality(90);//设置照片的质量
//			parameters.setPictureSize(display.getHeight(), display.getWidth());//设置照片的大小，默认是和     屏幕一样大
			mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(holder);//通过SurfaceView显示取景画面
			mCamera.startPreview();//开始预览
			mPreviewRunning = true;//设置是否预览参数为真
		} catch (IOException e) {
			Log.e("error", e.toString());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.d("compass", "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		synchronized (this) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				Log.d("compass", "onSensorChanged: " + sensor + ", x: " + values[0]
						+ ", y: " + values[1] + ", z: " + values[2]);

				// OrientText.setText("--- NESW ---");
				if (Math.abs(values[0] - DegressQuondam) < 1)
					return;
				
				if (DegressQuondam != -values[0])
					AniRotateImage(-values[0],values[0]);
			}
		}
	}
	
	private void AniRotateImage(float fDegress,float ff) {
		Log.d("compass", "Degress: " + DegressQuondam + ", " + fDegress);
		myAni = new RotateAnimation(DegressQuondam, fDegress,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		myAni.setDuration(300);
		myAni.setFillAfter(true);

		ImgCompass.startAnimation(myAni);
		
		int v = (int) ff;
		int spen = 750;
		int min = (int)angle - 50;
		int size = (int)angle + 50;
		if (v > min && v < size) {
			v = size - v;
			ai.smoothScrollTo(spen + (10*v), RESULT_CANCELED);
//			if(!playertag)
//			{
//				mMediaPlayer.start();
//				playertag = true;
//			}
//			System.out.println("西南spend="+(spen + (10*v)));
//			System.out.println("weinht=="+ai.getWidth());
		}
		else if(v > size)
		{
			ai.smoothScrollTo(600, RESULT_CANCELED);
//			if(playertag)
//			{
//				mMediaPlayer.stop();
//				playertag = false;
//			}
		}
		else if(v < min)
		{
			ai.smoothScrollTo(1900, RESULT_CANCELED);
//			if(playertag)
//			{
//				mMediaPlayer.stop();
//				playertag = false;
//			}
		}
		System.out.println("西南spend="+ff);

		DegressQuondam = fDegress;
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
}
