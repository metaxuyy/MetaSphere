package ms.activitys.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.HotelActivity;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.hotel.StoreMainActivity;
import ms.activitys.hotel.StoreViewActivity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaiduMapRouteSearch extends Activity {

	private String search_driver = "Driver"; // 驾车搜索
	private String search_transit = "Transit"; // 公交搜索
	private String search_walk = "Walk"; // 步行搜索

	MapView mMapView = null; // 地图View
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapController mMapController = null;

	private SharedPreferences share;
	private Douban api;
	private MyApp myapp;
	private LocationManager locationManager;

	private static String[] m = new String[3];

	private Integer index;// 当前商店所在总数据的索引
	private int typeindex = 0;//当前路线类型索引

	private ViewFlipper mViewFlipper;
	private String tag;
	private boolean type = false;
	private Button listbtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myapp = (MyApp) this.getApplication();
//		if (myapp.getmBMapManager() == null) {
//			myapp.setmBMapManager(new BMapManager(this));
//			myapp.getmBMapManager().init(MyApp.strKey,
//					new MyApp.MyGeneralListener());
//		}
		setContentView(R.layout.map_baidu_route);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();

		m[0] = this.getString(R.string.attractions_lable_16);// 步行
		m[1] = this.getString(R.string.attractions_lable_17);// 驾车
		m[2] = this.getString(R.string.attractions_lable_18);// 公交

		share = BaiduMapRouteSearch.this.getSharedPreferences("perference",
				MODE_PRIVATE);
		api = new Douban(share, myapp);

		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("index"))
		{	
			index = Integer.valueOf(bunde.getString("index"));
		}
		else
		{
			index = null;
		}
		tag = bunde.getString("tag");

		mViewFlipper = (ViewFlipper) findViewById(R.id.details);

		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				breakActivity();
			}
		});
		
		initData();
		initMapView();
		initRouteSearch();
		initSearchType();
		initShowInfo();

		// 默认驾车路线
		typeindex = 1;
		searchButtonProcess(search_driver);
	}
	
	public void breakActivity()
	{
		if(tag.equals("message"))
		{
			Intent intent = new Intent();
			intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}
		else if(tag.equals("hotel"))
		{
			Intent intent = new Intent();
			intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}
		else if(tag.equals("storemain"))
		{
			Intent intent = new Intent();
			intent.setClass( this,StoreMainActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", index);
		    bundle.putString("index", String.valueOf(index));
			bundle.putString("tag", "storeinfo");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}
		else
		{
			setResult(RESULT_OK, getIntent());
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
		}
	}

	private void initData() {
		List<Map<String, Object>> list = myapp.getMyCardsAll();
		Map map = null;
		if(index != null)
		{
			map = list.get(Integer.valueOf(index));
		}
		else
		{
			map = myapp.getHotelMap();
		}

		String storePhone = (String) map.get("storePhone");
		String address = (String) map.get("addressInfomation");
		String storeDesc = (String) map.get("storeDesc");
		String storeName = (String) map.get("storeName");
		String woof = (String) map.get("woof");
		String longItude = (String) map.get("longItude");

		myapp.setSlat(woof);
		myapp.setSlng(longItude);
	}

	private void initMapView() {
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setDoubleClickZooming(true);

		GeoPoint point = new GeoPoint(
				(int) (Double.parseDouble(myapp.getLat()) * 1e6),
				(int) (Double.parseDouble(myapp.getLng()) * 1e6));
		mMapController.setCenter(point);
	}

	private void initRouteSearch() {
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(myapp.getmBMapManager(), new MKSearchListener() {

			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapRouteSearch.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				System.out.println("res.getAddrResult()" + res.getAddrResult());
				RouteOverlay routeOverlay = new RouteOverlay(
						BaiduMapRouteSearch.this, mMapView);
				// 此处仅展示一个方案作为示例
				MKRoute mkRoute = res.getPlan(0).getRoute(0);
				int distance = mkRoute.getDistance();
				System.out.println("路线距离：" + distance);
				routeOverlay.setData(mkRoute);
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);

				// 封装路线信息
				TextView lumi = (TextView) findViewById(R.id.distance_mi);
				lumi.setText(String.valueOf(distance));
				TextView lutime = (TextView) findViewById(R.id.distance_time);
				lutime.setText("");
				TextView tujing = (TextView) findViewById(R.id.tujing);
				tujing.setText("");
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapRouteSearch.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay(
						BaiduMapRouteSearch.this, mMapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapRouteSearch.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(
						BaiduMapRouteSearch.this, mMapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);

			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				
			}

			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

		});

	}

	private void initSearchType() {
		final Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		// 将可选内容与ArrayAdapter连接起来
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);

		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter 添加到spinner中
		spinner.setAdapter(adapter);
		spinner.setSelection(1, true);// 默认选择驾车

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 被选中时候发生的动作
				System.out.println("选择的路线类型==" + arg2);
				if (arg2 == 0) {
					typeindex = 0;
					searchButtonProcess(search_walk);
				} else if (arg2 == 1) {
					typeindex = 1;
					searchButtonProcess(search_driver);
				} else if (arg2 == 2) {
					typeindex = 2;
					searchButtonProcess(search_transit);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initShowInfo() {
		listbtn = (Button) findViewById(R.id.list_btn);
		listbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewFlipper.showNext();
				if(type)
				{
					type = false;
					listbtn.setText(getString(R.string.hotel_label_40));
				}
				else
				{
					type = true;
					listbtn.setText(getString(R.string.menu_lable_180));
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
//		mMapView.destroy();
//		if (myapp.getmBMapManager() != null) {
//			myapp.getmBMapManager().destroy();
//			myapp.setmBMapManager(null);
//		}
//		System.gc();
		super.onDestroy();
	}

	private void searchButtonProcess(String type) {
		System.out.println("查找线路 开始lat==" + myapp.getLat());
		System.out.println("查找线路 开始lon==" + myapp.getLng());
		System.out.println("查找线路 目标lat==" + myapp.getSlat());
		System.out.println("查找线路 目标lon==" + myapp.getSlng());
		System.out.println("查找线路 类型type==" + type);
		// 处理搜索按钮响应
		GeoPoint startPoint = new GeoPoint((int) (Double.parseDouble(myapp
				.getLat()) * 1e6),
				(int) (Double.parseDouble(myapp.getLng()) * 1e6));
		GeoPoint endPoint = new GeoPoint((int) (Double.parseDouble(myapp
				.getSlat()) * 1e6),
				(int) (Double.parseDouble(myapp.getSlng()) * 1e6));
		
		drawRoute(Double.parseDouble(myapp
				.getSlat()), Double.parseDouble(myapp.getSlng()));

		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt = startPoint;
		MKPlanNode enNode = new MKPlanNode();
		enNode.pt = endPoint;

		// 实际使用中请对起点终点城市进行正确的设定
		if (type.equals(search_driver)) {
			mSearch.drivingSearch(null, stNode, null, enNode);
		} else if (type.equals(search_transit)) {
			mSearch.transitSearch("上海", stNode, enNode);
		} else if (type.equals(search_walk)) {
			mSearch.walkingSearch(null, stNode, null, enNode);
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		mMapView.refresh();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 通过解析google map返回的xml，在map中画路线图
	 */
	public void drawRoute(final double d, final double f) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;

				double lat = Double.parseDouble(myapp.getLat());
				double lng = Double.parseDouble(myapp.getLng());
				String modestr = "walking";
				if (typeindex == 0)
					modestr = "walking";
				else if (typeindex == 1)
					modestr = "driving";
				else if (typeindex == 2)
					modestr = "walking";

				String url = "http://maps.google.com/maps/api/directions/json?origin="
						+ lat
						+ ","
						+ lng
						+ "&destination="
						+ d
						+ ","
						+ f
						+ "&sensor=false&mode=" + modestr + "&language=zh-CN";

				System.out.println("mapurl====" + url);
				HttpGet get = new HttpGet(url);
				String strResult = "";
				try {
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters,
							3000);
					HttpClient httpClient = new DefaultHttpClient(
							httpParameters);

					HttpResponse httpResponse = null;
					httpResponse = httpClient.execute(get);

					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						strResult = EntityUtils.toString(httpResponse
								.getEntity());
					}
				} catch (Exception e) {
					return;
				}

				JSONObject job = null;
				try {
					job = new JSONObject(strResult);
					String status = job.getString("status");
					if (!status.equals("OK")) {
						msg.obj = null;
					} else {
						Map map = decodePoly(job);
						msg.obj = map;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/** 
     * 解析返回xml中overview_polyline的路线编码 
     *  
     * @param encoded 
     * @return 
     */  
    private Map<String,Object> decodePoly(JSONObject job) {  
        List<GeoPoint> poly = new ArrayList<GeoPoint>();  
        Map<String,Object> map = new HashMap<String,Object>();
        List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
        int juli = 0;
        int ytime = 0;
        try{
        	JSONArray routes = job.getJSONArray("routes");
        	JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
        	JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
        	String summary = routes.getJSONObject(0).getString("summary");
        	
        	JSONObject startAddress = legs.getJSONObject(0).getJSONObject("start_location");
        	double start_location_lat = startAddress.getDouble("lat");
    		double start_location_lng = startAddress.getDouble("lng");
    		GeoPoint geoPoint2 = new GeoPoint(
    				(int) (start_location_lat * 1E6),
    				(int) (start_location_lng * 1E6));
    		
    		
    		poly.add(geoPoint2);
    		
        	for(int i=0;i<steps.length();i++)
        	{
        		JSONObject step = steps.getJSONObject(i);
        		String distanceT = step.getJSONObject("distance").getString("text");
        		String distanceV = step.getJSONObject("distance").getString("value");
        		juli  = juli + Integer.valueOf(distanceV);
        		String durationT = step.getJSONObject("duration").getString("text");
        		String durationV = step.getJSONObject("duration").getString("value");
        		ytime = ytime + Integer.valueOf(durationV);
        		double end_location_lat = step.getJSONObject("end_location").getDouble("lat");
        		double end_location_lng = step.getJSONObject("end_location").getDouble("lng");
        		String html_instructions = step.getString("html_instructions");
        		GeoPoint geoPoint = new GeoPoint(
        				(int) (end_location_lat * 1E6),
        				(int) (end_location_lng * 1E6));
//        		poly.add(geoPoint);
        		
        		Map maps = new HashMap();
        		maps.put("distance", this.getString(R.string.attractions_lable_23)+":"+distanceT);
        		maps.put("duration", this.getString(R.string.attractions_lable_24)+":"+durationT);
        		maps.put("instructions", Html.fromHtml(html_instructions));
        		String str = html_instructions.replaceAll("\u003cb\u003e", "").replaceAll("\u003c/b\u003e", "");
        		if(str.indexOf("向左转") >= 0 || str.indexOf("向左") >= 0)
        			maps.put("img", R.drawable.reverse_left);
        		else if(str.indexOf("向右转") >= 0 || str.indexOf("向右") >= 0)
        			maps.put("img", R.drawable.reverse_right);
        		else if(str.indexOf("向南方向") >= 0)
        			maps.put("img", R.drawable.reverse_nan);
        		else if(str.indexOf("向北方向") >= 0)
        			maps.put("img", R.drawable.reverse_bie);
        		else if(str.indexOf("继续前行") >= 0)
        			maps.put("img", R.drawable.reverse_bie);
        		else if(str.indexOf("向西北方向") >= 0)
        			maps.put("img", R.drawable.reverse_xibei);
        		else if(str.indexOf("向西南方向") >= 0)
        			maps.put("img", R.drawable.reverse_xinang);
        		else if(str.indexOf("向东南方向") >= 0)
        			maps.put("img", R.drawable.reverse_dongnan);
        		else if(str.indexOf("向东北方向") >= 0)
        			maps.put("img", R.drawable.reverse_dongbie);
        		else
        			maps.put("img", R.drawable.reverse_bie);
        		
        		String encoded = step.getJSONObject("polyline").getString("points");
        		List<GeoPoint> points = decodePoly(encoded);
        		poly.addAll(points);
        		
        		dlist.add(maps);
        	}
        	
//        	TextView tview = (TextView)findViewById(R.id.map_lable_text);
//        	tview.setText("距离你大约："+juli+" 米 ，总共用时："+(ytime / 60) + " 分钟");
        	
        	map.put("juli", String.valueOf(juli));
        	map.put("summary", summary);
        	map.put("ytime", String.valueOf((ytime / 60)));
        	map.put("points", poly);
        	map.put("detitle",dlist);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        return map;  
    } 
    
    /**  
     * 解析返回xml中overview_polyline的路线编码  
     *   
     * @param encoded  
     * @return  
     */   
    private  List<GeoPoint> decodePoly(String encoded) {  
  
        List<GeoPoint> poly = new  ArrayList<GeoPoint>();  
        int  index =  0 , len = encoded.length();  
        int  lat =  0 , lng =  0 ;  
  
        while  (index < len) {  
            int  b, shift =  0 , result =  0 ;  
            do  {  
                b = encoded.charAt(index++) - 63 ;  
                result |= (b & 0x1f ) << shift;  
                shift += 5 ;  
            } while  (b >=  0x20 );  
            int  dlat = ((result &  1 ) !=  0  ? ~(result >>  1 ) : (result >>  1 ));  
            lat += dlat;  
  
            shift = 0 ;  
            result = 0 ;  
            do  {  
                b = encoded.charAt(index++) - 63 ;  
                result |= (b & 0x1f ) << shift;  
                shift += 5 ;  
            } while  (b >=  0x20 );  
            int  dlng = ((result &  1 ) !=  0  ? ~(result >>  1 ) : (result >>  1 ));  
            lng += dlng;  
  
            GeoPoint p = new  GeoPoint(( int ) ((( double ) lat / 1E5) * 1E6),  
                 (int ) ((( double ) lng / 1E5) * 1E6));  
            poly.add(p);  
        }  
  
        return  poly;  
    }  
    
    private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Map map = (Map)msg.obj;
				if(map != null)
				{
					List<GeoPoint> points = (List<GeoPoint>)map.get("points");  
					
			        List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("detitle");
			        
	//		        View navigationView = LayoutInflater.from(this).inflate(R.layout.navigation_view, null);
			        ListView dlistView = (ListView)findViewById(R.id.ListView_catalog);
					dlistView.setAdapter(getAdapter(data));
					
					TextView lumi = (TextView)findViewById(R.id.distance_mi);
			    	lumi.setText((String)map.get("juli"));
			    	
			    	TextView lutime = (TextView)findViewById(R.id.distance_time);
			    	lutime.setText((String)map.get("ytime"));
			    	
			    	TextView tujing = (TextView)findViewById(R.id.tujing);
			    	tujing.setText(BaiduMapRouteSearch.this.getString(R.string.attractions_lable_19)+map.get("summary"));
				}
				else
				{
					Toast.makeText(BaiduMapRouteSearch.this, BaiduMapRouteSearch.this.getString(R.string.attractions_lable_20), Toast.LENGTH_SHORT).show();  
					BaiduMapRouteSearch.this.finish();  
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	private SimpleAdapter getAdapter(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.navigation_item_view, new String[] { "instructions","distance","duration","img" },
				new int[] { R.id.instructions,R.id.distance,R.id.duration,R.id.reverse_image });
		return simperAdapter;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			if(tag.equals("2"))	
//			{
//				tag = "1";
//				mViewFlipper.showPrevious();
//			}
//			else
//			{
			
//			}
			breakActivity();
			return false;
		}
		return false;
	}
}
