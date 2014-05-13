package ms.activitys.travel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.ListViewAdapter;
import ms.globalclass.U;
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
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
import com.baidu.platform.comapi.map.Projection;

public class BaiduMapTravel extends Activity {

	private SharedPreferences share;
	private Douban api;
	private MyApp myapp;
	private String landid;
	private String storeName;
	private String storeImg;
	private String storeDesc;

	private ImageView imgTitle;
	private RelativeLayout mapsteplayout;
	private RelativeLayout layoutStepList;
	public MapView mapView;
	private MapController mc;
	private String closeTag = "g";
	public List<OverlayItem> GeoList;
	public List<View> viewlist;
	public OverlayItem coitem;
	public View popView;
	public String indextag = "";
	// private List<Overlay> mapOverlays;
	private GeoPoint point;
	private List<Map<String, Object>> wayDetailList;
//	private OverItemMapPopuForBaidu overitem;
	private OverItemT oits;
	private ViewFlipper switcher;
	private int sindex;
	private CharSequence[] coloritems = {};
	private ImageButton btnClose;
	private Map<String, Object> storemap;

//	private MyCustomLocationOverlay myLocation;

	private MyOverlay itemOverlay;

	private String search_driver = "Driver"; // 驾车搜索
	private String search_transit = "Transit"; // 公交搜索
	private String search_walk = "Walk"; // 步行搜索
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	GraphicsOverlay graphicsOverlay = null;
	long envelopeId = 0;

	public List<View> getViewlist() {
		return viewlist;
	}

	public void setViewlist(List<View> viewlist) {
		this.viewlist = viewlist;
	}

	public List<OverlayItem> getGeoList() {
		return GeoList;
	}

	public void setGeoList(List<OverlayItem> geoList) {
		GeoList = geoList;
	}

	public OverlayItem getCoitem() {
		return coitem;
	}

	public void setCoitem(OverlayItem coitem) {
		this.coitem = coitem;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_baidu_travel);

		myapp = (MyApp) this.getApplicationContext();

		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		landid = this.getIntent().getExtras().getString("landid");
		storeName = this.getIntent().getExtras().getString("storeName");
		storeImg = this.getIntent().getExtras().getString("storeImg");
		storeDesc = this.getIntent().getExtras().getString("storeDesc");
		sindex = this.getIntent().getExtras().getInt("sindex");

		List<Map<String, Object>> list = myapp.getMyCardsAll();
		storemap = list.get(sindex);

		initRouteSearch();

		loadThreadData();

		showRouteView();
	}

	public void showRouteView() {
		try {
			imgTitle = (ImageView) findViewById(R.id.imgTitle);
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;

					Bitmap bitm = getImageBitmap(storeImg, false);
					msg.obj = bitm;
					handler.sendMessage(msg);
				}
			}.start();

			TextView lblTitle = (TextView) findViewById(R.id.lblTitle);
			lblTitle.setText(storeName);

			TextView lblNote = (TextView) findViewById(R.id.lblNote);
			lblNote.setText(storeDesc);

			mapsteplayout = (RelativeLayout) findViewById(R.id.map_step_layout);
			mapsteplayout.getBackground().setAlpha(10);
			mapsteplayout.setVisibility(View.VISIBLE);

			layoutStepList = (RelativeLayout) findViewById(R.id.layoutStepList);

			ImageButton btnShowAllStep = (ImageButton) findViewById(R.id.btnShowAllStep);
			btnShowAllStep.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					closeTag = "k";
					layoutStepList.setVisibility(View.VISIBLE);
				}
			});

			mapView = (MapView) findViewById(R.id.map);
			String isLu = (String) storemap.get("isLu");
			if (isLu.equals("1")) {
				mapView.setSatellite(true);
			} else {
				mapView.setSatellite(false);
				mapView.setTraffic(false);
			}
			mapView.setBuiltInZoomControls(true);

			mc = mapView.getController();
			mc.setZoom(18);

			graphicsOverlay = new GraphicsOverlay(mapView);
			mapView.getOverlays().add(graphicsOverlay);

			popView = super.getLayoutInflater().inflate(
					R.layout.map_popup_route, null);
			mapView.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView.setVisibility(View.GONE);

			// mapOverlays = mapView.getOverlays();

			// point = new GeoPoint(myapp.getLatitude(), myapp.getLongitude());
			// myLocation = new MyCustomLocationOverlay(this, mapView,point);
			// myLocation.enableMyLocation();
			// mapOverlays.add(myLocation);

			btnClose = (ImageButton) findViewById(R.id.btnClose);
			btnClose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (closeTag.equals("g"))
						mapsteplayout.setVisibility(View.GONE);
					else if (closeTag.equals("k")) {
						closeTag = "g";
						layoutStepList.setVisibility(View.GONE);
					}
				}
			});

			// ZoomButtonsController zoomControls =
			// mapView.getZoomButtonsController();
			// zoomControls.setOnZoomListener(new OnZoomListener() {
			//
			// @Override
			// public void onZoom(boolean zoomIn) {
			// // TODO Auto-generated method stub
			// checgMapZoom(zoomIn);
			// }
			//
			// @Override
			// public void onVisibilityChanged(boolean visible) {
			// // TODO Auto-generated method stub
			//
			// }
			// });

			Button btnspotlist = (Button) findViewById(R.id.btn_spot_list);
			btnspotlist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showAttractionsPage();
				}
			});

			Button btnselectroute = (Button) findViewById(R.id.btn_select_route);
			btnselectroute.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showColorDialog();
				}
			});

			popView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!indextag.equals("")) {
						Map<String, Object> map = wayDetailList.get(Integer
								.valueOf(indextag));
						showTravelDetail((String) map.get("landspaceid"));
					}
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 该门店下的旅游景点介绍
	 */
	public void showAttractionsPage() {
		try {
			Intent intent = new Intent();
			intent.setClass(this, TravelActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("index", sindex);
			intent.putExtras(bundle);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			// this.finish();//关闭显示的Activity
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void showTravelDetail(String landscapeId) {
		try {
			Intent intent = new Intent();
			intent.setClass(this, TravelDetatilActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("index", sindex);
			bundle.putString("landscapeId", landscapeId);
			intent.putExtras(bundle);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			// this.finish();//关闭显示的Activity
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void showColorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = null;

		builder.setItems(coloritems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Map<String, Object> map = myapp.getRecommendAllWay().get(which);

				loadRoutsList(map);
			}

		});

		alertDialog = builder.create();
		alertDialog.show();
	}

	public void loadThreadData() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				List<Map<String, Object>> list = null;
				try {
					JSONObject jobj;
					U.dout(0);

					jobj = api.getRecommendAllWay(landid);
					if (jobj != null) {
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getAllWayDate(jArr);
						myapp.setRecommendAllWay(list);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> dlist = (List<Map<String, Object>>) msg.obj;
				myapp.setRecommendAllWay(dlist);
				if (dlist != null && dlist.size() > 0) {
					Map<String, Object> map = dlist.get(0);
					loadRoutsList(map);
				}
				break;
			case 1:
				Bitmap bitm = (Bitmap) msg.obj;
				imgTitle.setImageBitmap(bitm);
				break;
			case 2:
				List<GeoPoint> points = (List<GeoPoint>) msg.obj;
				if (points != null) {
					OverItemT myOverlay = new OverItemT(null,
							BaiduMapTravel.this, points, true, myapp,
							wayDetailList);
					// mapOverlays.add(myOverlay);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	public void loadRoutsList(Map<String, Object> map) {
		try {
			if (popView.getVisibility() == View.VISIBLE)
				popView.setVisibility(View.GONE);

			if (mapsteplayout.getVisibility() == View.GONE)
				mapsteplayout.setVisibility(View.VISIBLE);

			// mapOverlays.clear();

			// Map<String,Object> map = dlist.get(0);
			String betterWayId = (String) map.get("betterWayId");
			String recommendName = (String) map.get("recommendName");
			String betterWayDoc = (String) map.get("betterWayDoc");
			wayDetailList = (List<Map<String, Object>>) map
					.get("wayDetailList");

			ListView lstStep = (ListView) findViewById(R.id.lstStep);
			ListViewAdapter listItemAdapter = new ListViewAdapter(
					BaiduMapTravel.this,
					wayDetailList,// 数据源
					R.layout.step_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "imgBullet", "lblNumber", "lblName" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.imgBullet, R.id.lblNumber, R.id.lblName },
					share, wayDetailList.size(), "ico");
			lstStep.setAdapter(listItemAdapter);

			lstStep.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					listSelectItem(arg2);
				}
			});

			TextView lblName = (TextView) findViewById(R.id.lblName);
			lblName.setText(recommendName);

			switcher = (ViewFlipper) findViewById(R.id.switcher);
			switcher.removeAllViews();

			View view = LayoutInflater.from(BaiduMapTravel.this).inflate(
					R.layout.map_step_tip, null);
			TextView lblCurrentSpot = (TextView) view
					.findViewById(R.id.lblCurrentSpot);
			lblCurrentSpot.setText(recommendName);
			TextView lblNextSpot = (TextView) view
					.findViewById(R.id.lblNextSpot);
			lblNextSpot.setText(betterWayDoc);
			TextView lblTip = (TextView) view.findViewById(R.id.lblTip);
			lblTip.setText(this.getString(R.string.travel_lable_1)
					+ wayDetailList.size() + " "
					+ this.getString(R.string.travel_lable_2));
			switcher.addView(view);

			List<Map<String, Object>> glist = new ArrayList<Map<String, Object>>();
			List<GeoPoint> geolist = new ArrayList<GeoPoint>();
			for (int i = 0; i < wayDetailList.size(); i++) {
				Map<String, Object> waymap = wayDetailList.get(i);
				String formname = (String) waymap.get("landspaceName");
				String flatLong = (String) waymap.get("longtude");
				String flatWoof = (String) waymap.get("woof");
				String isLu = (String) storemap.get("isLu");
				if (isLu.equals("0")) {
					flatLong = (String) waymap.get("flatLong");
					flatWoof = (String) waymap.get("flatWoof");
				}
				String sysImg = (String) waymap.get("sysImg");
				String str = sysImg.substring(0, sysImg.lastIndexOf("."));
				String str2 = sysImg.substring(sysImg.lastIndexOf("."),
						sysImg.length());
				sysImg = str + "_ico" + str2;
				String landscapeDesc = (String) waymap.get("landscapeDesc");

				String toname = "";
				String flatLong2 = "";
				String flatWoof2 = "";
				Map<String, Object> gmap = new HashMap<String, Object>();
				if (i < wayDetailList.size() - 1) {
					if (i == 0) {
						formname = this.getString(R.string.travel_lable_3)
								+ formname;
						GeoPoint point = new GeoPoint(
								(int) (Double.valueOf(flatWoof) * 1E6),
								(int) (Double.valueOf(flatLong) * 1E6));
						mc.animateTo(point);
						// mc.setCenter(point);
					} else {
						formname = this.getString(R.string.travel_lable_4)
								+ formname;
					}

					Map<String, Object> waymap2 = wayDetailList.get(i + 1);
					toname = this.getString(R.string.travel_lable_5)
							+ (String) waymap2.get("landspaceName");
					flatLong2 = (String) waymap2.get("longtude");// 风景点平面经度
					flatWoof2 = (String) waymap2.get("woof");

					double juli = gps2m(Double.valueOf(flatWoof),
							Double.valueOf(flatLong),
							Double.valueOf(flatWoof2),
							Double.valueOf(flatLong2));
					String pabstr = getYawByPoints(Double.valueOf(flatWoof),
							Double.valueOf(flatLong),
							Double.valueOf(flatWoof2),
							Double.valueOf(flatLong2));

					// drawRoute(Double.valueOf(flatWoof),Double.valueOf(flatLong),Double.valueOf(flatWoof2),Double.valueOf(flatLong2));

					final GeoPoint point2 = new GeoPoint(
							(int) (Double.valueOf(flatWoof) * 1000000),
							(int) (Double.valueOf(flatLong) * 1000000));
					geolist.add(point2);

					gmap.put("geo", point2);
					gmap.put("title", formname);
					gmap.put("img", sysImg);
					gmap.put("desc", landscapeDesc);
					glist.add(gmap);
					Drawable marker2 = getResources().getDrawable(
							R.drawable.map_entertainment_icon);
					marker2.setBounds(0, 0, 2, 2);
					// OverItemT oit = new OverItemT(marker2,
					// RecommendedRouteActivity.this, point2,formname,myapp);
					// mapOverlays.add(oit);
					initMarkerData(Double.parseDouble(flatWoof),
							Double.parseDouble(flatLong), marker2);

					String justr = pabstr + " "
							+ this.getString(R.string.travel_lable_6) + juli
							+ this.getString(R.string.travel_lable_7);

					View view2 = LayoutInflater.from(BaiduMapTravel.this)
							.inflate(R.layout.map_step_tip, null);
					TextView lblCurrentSpot2 = (TextView) view2
							.findViewById(R.id.lblCurrentSpot);
					lblCurrentSpot2.setText(formname);
					TextView lblNextSpot2 = (TextView) view2
							.findViewById(R.id.lblNextSpot);
					lblNextSpot2.setText(toname);
					TextView lblTip2 = (TextView) view2
							.findViewById(R.id.lblTip);
					lblTip2.setText(justr);

					switcher.addView(view2);
				} else {
					formname = this.getString(R.string.travel_lable_8)
							+ formname;

					// drawRoute(Double.valueOf(flatWoof),Double.valueOf(flatLong),Double.valueOf(flatWoof2),Double.valueOf(flatLong2));

					final GeoPoint point2 = new GeoPoint(
							(int) (Double.valueOf(flatWoof) * 1000000),
							(int) (Double.valueOf(flatLong) * 1000000));
					geolist.add(point2);

					gmap.put("geo", point2);
					gmap.put("title", formname);
					gmap.put("img", sysImg);
					gmap.put("desc", landscapeDesc);
					glist.add(gmap);
					Drawable marker2 = getResources().getDrawable(
							R.drawable.map_entertainment_icon);
					marker2.setBounds(0, 0, 2, 2);
					// OverItemT oit = new OverItemT(marker2,
					// RecommendedRouteActivity.this, point2, formname,
					// myapp);
					// mapOverlays.add(oit);
					initMarkerData(Double.parseDouble(flatWoof),
							Double.parseDouble(flatLong), marker2);

					View view2 = LayoutInflater.from(BaiduMapTravel.this)
							.inflate(R.layout.map_step_tip, null);
					TextView lblCurrentSpot2 = (TextView) view2
							.findViewById(R.id.lblCurrentSpot);
					lblCurrentSpot2.setText(formname);
					TextView lblNextSpot2 = (TextView) view2
							.findViewById(R.id.lblNextSpot);
					lblNextSpot2.setText("");
					TextView lblTip2 = (TextView) view2
							.findViewById(R.id.lblTip);
					lblTip2.setText("");

					switcher.addView(view2);
				}
			}

			String isLu = (String) storemap.get("isLu");
			if (isLu.equals("1")) {
				OverItemT myOverlay = new OverItemT(null, BaiduMapTravel.this,
						geolist, true, myapp, wayDetailList);
				// mapOverlays.add(myOverlay);
			} else {
				drawRoute(geolist);
			}

			for (int j = 0; j < glist.size(); j++) {
				Map<String, Object> gmap = glist.get(j);
				GeoPoint geop = (GeoPoint) gmap.get("geo");
				String title = (String) gmap.get("title");
				String index = String.valueOf(j);

				String tag = "z";
				if (j == 0)
					tag = "s";
				else if (j == glist.size() - 1)
					tag = "e";

				Drawable marker2 = getResources().getDrawable(
						R.drawable.map_entertainment_icon);
				marker2.setBounds(0, 0, 2, 2);
				OverItemT oit = new OverItemT(marker2, BaiduMapTravel.this,
						geop, index, myapp, tag, wayDetailList);
				// mapOverlays.add(oit);
			}

			ImageButton btnPrev = (ImageButton) findViewById(R.id.btnPrev);
			btnPrev.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index = switcher.getDisplayedChild() - 1;
					if (index == -1)
						index = wayDetailList.size();
					if (index > 0) {
						// if (overitem != null)
						// mapView.getOverlays().remove(overitem);
						// if (oits != null)
						// mapView.getOverlays().remove(oits);
						Map<String, Object> wmap = wayDetailList.get(index - 1);
						String formname = (String) wmap.get("landspaceName");
						String flatLong = (String) wmap.get("longtude");// 风景点平面经度
						String flatWoof = (String) wmap.get("woof");
						String isLu = (String) storemap.get("isLu");
						if (isLu.equals("0")) {
							flatLong = (String) wmap.get("flatLong");
							flatWoof = (String) wmap.get("flatWoof");
						}

						removeCircle();
						drawCircle(Double.valueOf(flatWoof),
								Double.valueOf(flatLong));

						GeoPoint point = new GeoPoint((int) (Double
								.valueOf(flatWoof) * 1E6), (int) (Double
								.valueOf(flatLong) * 1E6));
						// Drawable marker2 = getResources().getDrawable(
						// R.drawable.map_entertainment_icon);
						// overitem = new OverItemMapPopuForBaidu(marker2,
						// BaiduMapTravel.this, point, "", mapView, myapp,
						// mc, null);
						// mapView.getOverlays().add(overitem);
						//
						// String tag = "z";
						// if (index - 1 == 0)
						// tag = "s";
						// else if (index - 1 == wayDetailList.size() - 1)
						// tag = "e";
						//
						// oits = new OverItemT(marker2, BaiduMapTravel.this,
						// point, String.valueOf(index - 1), myapp, tag,
						// wayDetailList, true);
						// mapView.getOverlays().add(oits);
						mc.animateTo(point);
						// mc.setCenter(point);
					}
					switcher.showPrevious();
				}
			});

			ImageButton btnNext = (ImageButton) findViewById(R.id.btnNext);
			btnNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index = switcher.getDisplayedChild() - 1;
					if (index < wayDetailList.size() - 1) {
						// if (overitem != null)
						// mapView.getOverlays().remove(overitem);
						// if (oits != null)
						// mapView.getOverlays().remove(oits);
						Map<String, Object> wmap = wayDetailList.get(index + 1);
						String formname = (String) wmap.get("landspaceName");
						String flatLong = (String) wmap.get("longtude");// 风景点平面经度
						String flatWoof = (String) wmap.get("woof");
						String isLu = (String) storemap.get("isLu");
						if (isLu.equals("0")) {
							flatLong = (String) wmap.get("flatLong");
							flatWoof = (String) wmap.get("flatWoof");
						}

						removeCircle();
						drawCircle(Double.valueOf(flatWoof),
								Double.valueOf(flatLong));

						GeoPoint point = new GeoPoint((int) (Double
								.valueOf(flatWoof) * 1E6), (int) (Double
								.valueOf(flatLong) * 1E6));
						// Drawable marker2 = getResources().getDrawable(
						// R.drawable.map_entertainment_icon);
						// overitem = new OverItemMapPopuForBaidu(marker2,
						// BaiduMapTravel.this, point, "", mapView, myapp,
						// mc, null);
						// mapView.getOverlays().add(overitem);
						//
						// String tag = "z";
						// if (index - 1 == 0)
						// tag = "s";
						// else if (index - 1 == wayDetailList.size() - 1)
						// tag = "e";
						//
						// oits = new OverItemT(marker2, BaiduMapTravel.this,
						// point, String.valueOf(index + 1), myapp, tag,
						// wayDetailList, true);
						// mapView.getOverlays().add(oits);
						mc.animateTo(point);
						// mc.setCenter(point);
					}
					switcher.showNext();
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 地图上添加圆
	public void drawCircle(double mLat1, double mLon1) {
		int lat = (int) (mLat1 * 1E6);
		int lon = (int) (mLon1 * 1E6);
		GeoPoint pt1 = new GeoPoint(lat, lon);

		// 构建点并显示
		Geometry circleGeometry = new Geometry();

		circleGeometry.setCircle(pt1, 350);

		Symbol circleSymbol = new Symbol();
		Symbol.Color circleColor = circleSymbol.new Color();
		circleColor.red = 255;
		circleColor.green = 0;
		circleColor.blue = 0;
		circleColor.alpha = 126;
		circleSymbol.setSurface(circleColor, 1, 3);

		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);

		envelopeId = graphicsOverlay.setData(circleGraphic);
		mapView.refresh();
	}

	// 地图删除圆
	public void removeCircle() {
		System.out.println("circle size before:"
				+ graphicsOverlay.getAllGraphics().size());
		if (graphicsOverlay.getAllGraphics().size() > 0) {
			graphicsOverlay.removeGraphic(envelopeId);
			System.out.println("circle size after:"
					+ graphicsOverlay.getAllGraphics().size());
			mapView.refresh();
		}
	}

	// 添加一个标注
	private void initMarkerData(double cLat, double cLon, Drawable mark) {
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
		// 准备overlay图像数据，根据实情情况修复
		// 用OverlayItem准备Overlay数据
		OverlayItem item1 = new OverlayItem(p1, "item1", "item1");
		item1.setMarker(mark);

		// 创建IteminizedOverlay
		MyOverlay itemOverlay = new MyOverlay(mark, this);
		// 将IteminizedOverlay添加到MapView中
		// 注意：目前IteminizedOverlay不支持多实例，MapView中只能有一个IteminizedOverlay实例
		// mMapView.getOverlays().clear();
		mapView.getOverlays().add(itemOverlay);

		// 现在所有准备工作已准备好，使用以下方法管理overlay.
		// 添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高
		itemOverlay.addItem(item1);
		mapView.refresh();
	}

	// 添多个标注
	private void initMarkerDataAll(List<Map<String, Object>> dlist) {
		Drawable marker = getResources().getDrawable(R.drawable.map_hotel_icon);
		if (itemOverlay == null) {
			itemOverlay = new MyOverlay(marker, this);
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
				if (imgmaping != null && !imgmaping.equals("")) {
					int imgid = getResources().getIdentifier(
							getPackageName() + ":drawable/" + imgmaping, null,
							null);
					mark = getResources().getDrawable(imgid);
				} else {
					mark = getResources().getDrawable(
							R.drawable.map_unknown_icon);
				}

				OverlayItem item = new OverlayItem(p, pkid, pkid);
				item.setMarker(mark);

				itemOverlay.addItem(item);
			}
		}

		mapView.getOverlays().add(itemOverlay);
		mapView.refresh();
	}

	public void listSelectItem(int index) {
		try {
			Map<String, Object> wmap = wayDetailList.get(index);
			switcher.setDisplayedChild(index + 1);

//			if (overitem != null)
//				mapView.getOverlays().remove(overitem);
			if (oits != null)
				mapView.getOverlays().remove(oits);
			String formname = (String) wmap.get("landspaceName");
			String flatLong = (String) wmap.get("longtude");
			String flatWoof = (String) wmap.get("woof");
			String isLu = (String) storemap.get("isLu");
			if (isLu.equals("0")) {
				flatLong = (String) wmap.get("flatLong");
				flatWoof = (String) wmap.get("flatWoof");
			}
			GeoPoint point = new GeoPoint(
					(int) (Double.valueOf(flatWoof) * 1E6),
					(int) (Double.valueOf(flatLong) * 1E6));
			Drawable marker2 = getResources().getDrawable(
					R.drawable.map_entertainment_icon);
//			overitem = new OverItemMapPopuForBaidu(marker2,
//					BaiduMapTravel.this, point, "", mapView, myapp, mc, null);
//			mapView.getOverlays().add(overitem);

			String tag = "z";
			if (index - 1 == 0)
				tag = "s";
			else if (index - 1 == wayDetailList.size() - 1)
				tag = "e";

			oits = new OverItemT(marker2, BaiduMapTravel.this, point,
					String.valueOf(index), myapp, tag, wayDetailList, true);
			// mapOverlays.add(oits);
			mc.animateTo(point);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<Map<String, Object>> getAllWayDate(JSONArray jArr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			coloritems = new String[jArr.length()];
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);

				String betterWayId = "";
				if (dobj.has("betterWayId"))
					betterWayId = (String) dobj.get("betterWayId");

				String recommendName = "";
				if (dobj.has("recommendName"))
					recommendName = (String) dobj.get("recommendName");

				String area = "";
				if (dobj.has("area"))
					area = (String) dobj.get("area");

				String area_cod = "";
				if (dobj.has("area_cod"))
					area_cod = (String) dobj.get("area_cod");

				String storeInfo = "";
				if (dobj.has("storeInfo"))
					storeInfo = (String) dobj.get("storeInfo");

				String storeInfoId = "";
				if (dobj.has("storeInfoId"))
					storeInfoId = (String) dobj.get("storeInfoId");

				String betterWayDoc = "";
				if (dobj.has("betterWayDoc"))
					betterWayDoc = (String) dobj.get("betterWayDoc");

				List<Map<String, Object>> wayDetailList = null;
				if (dobj.has("wayDetailList")) {
					JSONArray detailjArr = (JSONArray) dobj
							.get("wayDetailList");
					wayDetailList = getWayDetailDate(detailjArr);
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("betterWayId", betterWayId);
				map.put("recommendName", recommendName);
				map.put("area", area);
				map.put("area_cod", area_cod);
				map.put("storeInfo", storeInfo);
				map.put("storeInfoId", storeInfoId);
				map.put("betterWayDoc", betterWayDoc);
				map.put("wayDetailList", wayDetailList);

				coloritems[i] = recommendName;
				list.add(map);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> getWayDetailDate(JSONArray jArr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);

				String betterWayFullId = "";
				if (dobj.has("betterWayFullId"))
					betterWayFullId = (String) dobj.get("betterWayFullId");

				String landspaceName = "";
				if (dobj.has("landspaceName"))
					landspaceName = (String) dobj.get("landspaceName");

				String landspaceid = "";
				if (dobj.has("landspaceid"))
					landspaceid = (String) dobj.get("landspaceid");

				String sysImg = "";
				if (dobj.has("sysImg"))
					sysImg = (String) dobj.get("sysImg");

				String landscapeDesc = "";
				if (dobj.has("landscapeDesc"))
					landscapeDesc = (String) dobj.get("landscapeDesc");

				String price = "";
				if (dobj.has("price"))
					price = (String) dobj.get("price");

				String address = "";
				if (dobj.has("address"))
					address = (String) dobj.get("address");

				String getScore = "";
				if (dobj.has("getScore"))
					getScore = (String) dobj.get("getScore");

				String flatLong = "";
				if (dobj.has("flatLong"))
					flatLong = (String) dobj.get("flatLong");

				String flatWoof = "";
				if (dobj.has("flatWoof"))
					flatWoof = (String) dobj.get("flatWoof");

				String longtude = "";
				if (dobj.has("longtude"))
					longtude = (String) dobj.get("longtude");

				String woof = "";
				if (dobj.has("woof"))
					woof = (String) dobj.get("woof");

				String wayNo = "";
				if (dobj.has("wayNo"))
					wayNo = (String) dobj.get("wayNo");

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("betterWayFullId", betterWayFullId);// 路线详细ID
				map.put("landspaceName", landspaceName);// 风景点名称
				map.put("landspaceid", landspaceid);
				map.put("sysImg", sysImg);// 风景点图片
				map.put("landscapeDesc", landscapeDesc);// 风景点描述
				map.put("price", price);// 风景点门票价格
				map.put("address", address);// 实风景点地址
				map.put("getScore", getScore);// 风景点得分
				map.put("flatLong", flatLong);// 风景点平面经度
				map.put("flatWoof", flatWoof);// 风景点平面纬度
				map.put("longtude", longtude);// 经度
				map.put("woof", woof);// 纬度
				map.put("wayNo", wayNo);// 路线序号
				if (wayNo.equals("1"))
					map.put("imgBullet", R.drawable.ic_step_bullet_start);
				else if (i == jArr.length() - 1)
					map.put("imgBullet", R.drawable.ic_step_bullet_end);
				else
					map.put("imgBullet", R.drawable.ic_step_bullet);
				map.put("lblNumber", wayNo + ". ");
				map.put("lblName", landspaceName);

				list.add(map);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static Bitmap getImageBitmap(String value, boolean b) {
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		System.out.println("menulist_imageurl===" + value);
		if (value == null)
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

			// bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			if (b)
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	// 计算两点距离
	private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));

		s = s * 6378137.0;
		s = Math.round(s * 10000) / 10000;

		return s;
	}

	/**
	 * 计算两点间的正北方向
	 * 
	 * @param lon1
	 *            点1
	 * @param lat1
	 * @param lon2
	 *            点2
	 * @param lat2
	 * @return
	 */
	public String getYawByPoints(double lat1, double lon1, double lat2,
			double lon2) {
		double yaw = 0;
		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;
		double dMin = 0.00001;
		if (Math.abs(dlon) < dMin) {
			if (dlat > dMin)
				yaw = 0;
			else if (dlat < dMin)
				yaw = 180;
			return getFxian(yaw);
		}
		if (Math.abs(dlat) < dMin) {
			if (dlon > dMin)
				yaw = 90;
			else if (dlon < dMin)
				yaw = 270;
			return getFxian(yaw);
		}
		double kk = (dlon) / (dlat);
		if (dlon > dMin && dlat > dMin) {
			yaw = (Math.atan(kk) * 180 / Math.PI);
		} else if (dlon > dMin && dlat < dMin) {
			yaw = (Math.atan(kk) * 180 / Math.PI) + 180;
		} else if (dlon < dMin && dlat < dMin) {
			yaw = (Math.atan(kk) * 180 / Math.PI) + 180;
		} else {
			yaw = (Math.atan(kk) * 180 / Math.PI) + 360;
		}

		return getFxian(yaw);
	}

	public String getFxian(double pab) {
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
				pabstr = this.getString(R.string.travel_lable_13);
			}

			if (v > 90 && v < 180) {
				v = 180 - v;
				pabstr = this.getString(R.string.travel_lable_14);
			}

			if (v > 180 && v < 270) {
				v = v - 180;
				pabstr = this.getString(R.string.travel_lable_15);
			}
			if (v > 270 && v < 360) {
				v = 360 - v;
				pabstr = this.getString(R.string.travel_lable_16);
			}
		}
		}
		return pabstr + this.getString(R.string.travel_lable_17);
	}

	public void checgMapZoom(boolean zoomIn) {
		try {
			if (zoomIn) {
				mc.setZoom(mapView.getZoomLevel() + 1);
			} else {
				mc.setZoom(mapView.getZoomLevel() - 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 通过解析google map返回的xml，在map中画路线图
	 */
	public void drawRoute(final List<GeoPoint> geolist) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;

				String modestr = "walking";

				List<GeoPoint> geoalllist = new ArrayList<GeoPoint>();

				for (int i = 0; i < geolist.size() - 1; i++) {
					GeoPoint geo = geolist.get(i);
					GeoPoint geo2 = geolist.get(i + 1);

					String lat = String.valueOf(geo.getLatitudeE6() / 1E6);
					String lng = String.valueOf(geo.getLongitudeE6() / 1E6);

					String d = String.valueOf(geo2.getLatitudeE6() / 1E6);
					String f = String.valueOf(geo2.getLongitudeE6() / 1E6);

					String url = "http://maps.google.com/maps/api/directions/json?origin="
							+ lat
							+ ","
							+ lng
							+ "&destination="
							+ d
							+ ","
							+ f
							+ "&sensor=true&mode="
							+ modestr
							+ "&language=zh-CN&alternatives=true";

					searchButtonProcess(search_walk, Double.parseDouble(lat),
							Double.parseDouble(lng), Double.parseDouble(d),
							Double.parseDouble(f));

					System.out.println("mapurl====" + url);
					HttpGet get = new HttpGet(url);
					String strResult = "";
					try {
						HttpParams httpParameters = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(
								httpParameters, 3000);
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
							List<GeoPoint> points = (List<GeoPoint>) map
									.get("points");
							geoalllist.addAll(points);
							// Map<String,Object> nmap = new
							// HashMap<String,Object>();
							// nmap.put("plist", points);
							// JSONObject jobe = new JSONObject(nmap);
							// try {
							// JSONObject jobj =
							// api.getPointJZS(jobe.toString());
							// if(jobj != null)
							// {
							// JSONArray jArr = (JSONArray) jobj.get("data");
							// List<GeoPoint> geos = getJsonGeoList(jArr);
							// geoalllist.addAll(geos);
							// }
							// } catch (Exception e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				msg.obj = geoalllist;

				handler.sendMessage(msg);
			}
		}.start();
	}

	public List<GeoPoint> getJsonGeoList(JSONArray jArr) {
		List<GeoPoint> geos = new ArrayList<GeoPoint>();
		try {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);

				double lat = dobj.getDouble("lat");
				double lng = dobj.getDouble("lng");

				GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6),
						(int) (lng * 1E6));

				geos.add(geoPoint);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return geos;
	}

	/**
	 * 解析返回xml中overview_polyline的路线编码
	 * 
	 * @param encoded
	 * @return
	 */
	private Map<String, Object> decodePoly(JSONObject job) {
		List<GeoPoint> poly = new ArrayList<GeoPoint>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> dlist = new ArrayList<Map<String, Object>>();
		int juli = 0;
		int ytime = 0;
		try {
			JSONArray routes = job.getJSONArray("routes");
			JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
			JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
			String summary = routes.getJSONObject(0).getString("summary");

			JSONObject startAddress = legs.getJSONObject(0).getJSONObject(
					"start_location");
			double start_location_lat = startAddress.getDouble("lat");
			double start_location_lng = startAddress.getDouble("lng");
			GeoPoint geoPoint2 = new GeoPoint((int) (start_location_lat * 1E6),
					(int) (start_location_lng * 1E6));

			// Map<String,Object> gmap = new HashMap<String,Object>();
			// gmap.put("lat", start_location_lat);
			// gmap.put("lng", start_location_lng);
			poly.add(geoPoint2);

			for (int i = 0; i < steps.length(); i++) {
				JSONObject step = steps.getJSONObject(i);
				String distanceT = step.getJSONObject("distance").getString(
						"text");
				String distanceV = step.getJSONObject("distance").getString(
						"value");
				juli = juli + Integer.valueOf(distanceV);
				String durationT = step.getJSONObject("duration").getString(
						"text");
				String durationV = step.getJSONObject("duration").getString(
						"value");
				ytime = ytime + Integer.valueOf(durationV);
				double end_location_lat = step.getJSONObject("end_location")
						.getDouble("lat");
				double end_location_lng = step.getJSONObject("end_location")
						.getDouble("lng");
				String html_instructions = step.getString("html_instructions");
				GeoPoint geoPoint = new GeoPoint(
						(int) (end_location_lat * 1E6),
						(int) (end_location_lng * 1E6));
				// poly.add(geoPoint);

				String encoded = step.getJSONObject("polyline").getString(
						"points");
				List<GeoPoint> points = decodePoly(encoded);
				poly.addAll(points);
			}

			// TextView tview = (TextView)findViewById(R.id.map_lable_text);
			// tview.setText("距离你大约："+juli+" 米 ，总共用时："+(ytime / 60) + " 分钟");

			map.put("juli", String.valueOf(juli));
			map.put("summary", summary);
			map.put("ytime", String.valueOf((ytime / 60)));
			map.put("points", poly);
			map.put("detitle", dlist);
		} catch (Exception ex) {
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
	private List<GeoPoint> decodePoly(String encoded) {

		List<GeoPoint> poly = new ArrayList<GeoPoint>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));

			// Map<String,Object> gmap = new HashMap<String,Object>();
			// gmap.put("lat", ( double ) lat / 1E5);
			// gmap.put("lng", ( double ) lng / 1E5);

			poly.add(p);
		}

		return poly;
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
					Toast.makeText(BaiduMapTravel.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				System.out.println("res.getAddrResult()" + res.getAddrResult());
				System.out.println("路线数量：" + res.getNumPlan());
				RouteOverlay routeOverlay = new RouteOverlay(
						BaiduMapTravel.this, mapView);
				// 此处仅展示一个方案作为示例
				MKRoute mkRoute = res.getPlan(0).getRoute(0);
				routeOverlay.setData(mkRoute);
				mapView.getOverlays().add(routeOverlay);

				mapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapTravel.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay(
						BaiduMapTravel.this, mapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0));
				mapView.getOverlays().add(routeOverlay);
				mapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapTravel.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(
						BaiduMapTravel.this, mapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				System.out.println("before 覆盖数量:"
						+ mapView.getOverlays().size());
				mapView.getOverlays().add(routeOverlay);
				System.out
						.println("after 覆盖数量:" + mapView.getOverlays().size());
				mapView.refresh();
				mapView.invalidate();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());

				mapView.getController().animateTo(res.getStart().pt);
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

	private void searchButtonProcess(String type, Double startLat,
			Double startLng, Double endLat, Double endLng) {
		System.out.println("查找线路 开始lat==" + startLat);
		System.out.println("查找线路 开始lon==" + startLng);
		System.out.println("查找线路 目标lat==" + endLat);
		System.out.println("查找线路 目标lon==" + endLng);
		System.out.println("查找线路 类型type==" + type);
		// 处理搜索按钮响应
		GeoPoint startPoint = new GeoPoint((int) (startLat * 1e6),
				(int) (startLng * 1e6));
		GeoPoint endPoint = new GeoPoint((int) (endLat * 1e6),
				(int) (endLng * 1e6));

		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt = startPoint;
		MKPlanNode enNode = new MKPlanNode();
		enNode.pt = endPoint;

		// 实际使用中请对起点终点城市进行正确的设定
		if (type.equals(search_driver)) {
			mSearch.drivingSearch(null, stNode, null, enNode);
		} else if (type.equals(search_transit)) {
			mSearch.transitSearch(null, stNode, enNode);
		} else if (type.equals(search_walk)) {
			mSearch.walkingSearch(null, stNode, null, enNode);
		}
	}
}

class MyOverlay extends ItemizedOverlay<OverlayItem> {
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Context mContext = null;

	public MyOverlay(Drawable marker, Context context) {
		super(marker);
		this.mContext = context;
		populate();
	}

	protected boolean onTap(int index) {
		return true;
	}

	public boolean onTap(GeoPoint pt, MapView mapView) {
		super.onTap(pt, mapView);
		return false;
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

class OverItemT extends ItemizedOverlay<OverlayItem> {

	private Drawable marker;
	private BaiduMapTravel mContext;
	private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
	GeoPoint mpoint;
	private boolean isshowline = false;
	private MyApp myapp;
	private String isStart = "z";
	private List<Map<String, Object>> wlist;
	private boolean isYuanShow = false;

	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
			| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
			| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
			| Canvas.CLIP_TO_LAYER_SAVE_FLAG;

	/**
	 * 用于保存构成曲线的点的数据
	 */
	private List<GeoPoint> linePoints = new ArrayList<GeoPoint>();

	public OverItemT(Drawable defaultMarker) {
		// super(boundCenterBottom(defaultMarker));
		super(defaultMarker);
	}

	public OverItemT(Drawable defaultMarker, BaiduMapTravel context,
			GeoPoint point, String str, MyApp mapp, String starttag,
			List<Map<String, Object>> wayDetailList) {
		// super(boundCenterBottom(defaultMarker));
		super(defaultMarker);
		// this(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
		double lat = 39.9022;
		double lon = 116.3924;

		this.marker = defaultMarker;
		this.mContext = context;
		this.mpoint = point;
		this.myapp = mapp;
		this.isStart = starttag;
		this.wlist = wayDetailList;
		// GeoPoint p=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
		// GeoList.add(new OverlayItem(p,"",""));
		GeoList.add(new OverlayItem(point, str, ""));
		mContext.setGeoList(GeoList);
		populate();
	}

	public OverItemT(Drawable defaultMarker, BaiduMapTravel context,
			GeoPoint point, String str, MyApp mapp, String starttag,
			List<Map<String, Object>> wayDetailList, boolean yuantag) {
		// super(boundCenterBottom(defaultMarker));
		super(defaultMarker);
		// this(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
		double lat = 39.9022;
		double lon = 116.3924;

		this.marker = defaultMarker;
		this.mContext = context;
		this.mpoint = point;
		this.myapp = mapp;
		this.isStart = starttag;
		this.wlist = wayDetailList;
		this.isYuanShow = yuantag;
		// GeoPoint p=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
		// GeoList.add(new OverlayItem(p,"",""));
		GeoList.add(new OverlayItem(point, str, ""));
		mContext.setGeoList(GeoList);
		populate();
	}

	public OverItemT(Drawable defaultMarker, BaiduMapTravel context,
			List<GeoPoint> points, boolean isshow, MyApp mapp,
			List<Map<String, Object>> wayDetailList) {
		// super(boundCenterBottom(defaultMarker));
		super(defaultMarker);
		// this(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub

		this.marker = defaultMarker;
		this.mContext = context;
		this.linePoints = points;
		this.myapp = mapp;
		this.wlist = wayDetailList;
		deletePath();
		// GeoPoint p=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
		// GeoList.add(new OverlayItem(p,"",""));
		// GeoList.add(new OverlayItem(point,str,""));
		// mContext.setGeoList(GeoList);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return GeoList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return GeoList.size();
	}

	public void addOverlay(OverlayItem overlay) {
		GeoList.add(overlay);
		this.populate();
	}

	// @Override
	// public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	// // TODO Auto-generated method stub
	//
	// Projection projection = mapView.getProjection();
	// for (int index =0; index<size(); index++) {
	// OverlayItem overLayItem = getItem(index);
	//
	// String title = overLayItem.getTitle();
	// Point point = projection.toPixels(overLayItem.getPoint(), null);
	//
	// Paint paintCircle = new Paint();
	// paintCircle.setColor(Color.RED);
	// canvas.drawCircle(point.x, point.y, 3, paintCircle); // 画圆
	//
	// Resources r = mContext.getResources();
	// Bitmap bitmap = BitmapFactory.decodeResource(r,R.drawable.tuding);
	// canvas.drawBitmap(bitmap, 0, 0, null);
	//
	// Paint paintText = new Paint();
	// paintText.setColor(Color.BLACK);
	// paintText.setTextSize(15);
	//
	// canvas.drawText(title, point.x, point.y - 25, paintText); // 绘制文本
	//
	// }
	//
	// super.draw(canvas, mapView, shadow);
	// }

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		if (!shadow) {
			// System.out.println("!!!!!!!!!!!!!!");

			canvas.save(LAYER_FLAGS);
			// canvas.save();

			Projection projection = mapView.getProjection();
			int size = GeoList.size();
			Point point = new Point();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			OverlayItem overLayItem;

			// 画起点/终点
			for (int i = 0; i < size; i++) {

				overLayItem = GeoList.get(i);

				Drawable marker = overLayItem.getMarker();
				// marker.getBounds()
				/* 象素点取得转换 */
				projection.toPixels(overLayItem.getPoint(), point);

				if (marker != null) {
					// boundCenterBottom(marker);
				}

				if (isStart.equals("s")) {
					/* 圆圈 */
					Paint paintCircle = new Paint();
					paintCircle.setColor(Color.GREEN);
					paint.setColor(Color.GREEN);
					canvas.drawCircle(point.x, point.y, 10, paint);
				} else if (isStart.equals("e")) {
					/* 圆圈 */
					Paint paintCircle = new Paint();
					paintCircle.setColor(Color.RED);
					paint.setColor(Color.RED);
					canvas.drawCircle(point.x, point.y, 10, paint);
				}

				// if(i == count - 1)
				// {
				// /* 圆圈 */
				// Paint paintCircle = new Paint();
				// paintCircle.setColor(Color.GREEN);
				// paint.setColor(Color.GREEN);
				// canvas.drawCircle(point.x, point.y, 10, paint);
				// }

				if (isYuanShow) {
					/* 文字设置 */
					/* 标题 */
					String index = GeoList.get(i).getTitle();
					Map<String, Object> waymap = wlist.get(Integer
							.valueOf(index));
					String title = (String) waymap.get("landspaceName");

					if (title != null && title.length() > 0) {
						paint.setColor(Color.RED);
						paint.setFakeBoldText(true);
						paint.setSubpixelText(true);
						String familyName = "宋体";
						Typeface font = Typeface.create(familyName,
								Typeface.BOLD);
						paint.setTypeface(font);
						paint.setTextSize(20);
						canvas.drawText(title, point.x, point.y + 20, paint);
					}
				} else {
					/* 文字设置 */
					/* 标题 */
					String index = GeoList.get(i).getTitle();
					Map<String, Object> waymap = wlist.get(Integer
							.valueOf(index));
					String title = (String) waymap.get("landspaceName");

					if (title != null && title.length() > 0) {
						paint.setColor(Color.WHITE);
						paint.setFakeBoldText(true);
						paint.setSubpixelText(true);
						String familyName = "宋体";
						Typeface font = Typeface.create(familyName,
								Typeface.BOLD);
						paint.setTypeface(font);
						paint.setTextSize(20);
						canvas.drawText(title, point.x, point.y + 20, paint);
					}
				}
			}

			// 画线

			boolean prevInBound = false;// 前一个点是否在可视区域
			Point prev = null;
			int mapWidth = mapView.getWidth();
			int mapHeight = mapView.getHeight();
			// Paint paintLine = new Paint();
			paint.setColor(Color.BLUE);
			paint.setAlpha(150);
			// paint.setPathEffect(new CornerPathEffect(10));
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(8);
			int count = linePoints.size();
			myapp.getPathlist().add(paint);
			Path path = new Path();
			for (int i = 0; i < count; i++) {

				if (i == 0) {
					Point p1 = new Point();
					GeoPoint gpoint1 = linePoints.get(i);
					projection.toPixels(gpoint1, p1);
					path.moveTo(p1.x, p1.y);
				} else {
					Point p1 = new Point();
					GeoPoint gpoint1 = linePoints.get(i);
					projection.toPixels(gpoint1, p1);
					path.lineTo(p1.x, p1.y);
				}
			}
			canvas.drawPath(path, paint);// 画出路径
			// canvas.drawPath(path, paint);
			canvas.restore();
			// DebugUtils.showMemory();
		}

		// super.draw(canvas, mapView, shadow);
	}

	public void deletePath() {
		try {
			List<Paint> pathlist = myapp.getPathlist();
			if (pathlist != null && pathlist.size() > 0) {
				for (int i = 0; i < pathlist.size(); i++) {
					Paint path = pathlist.get(i);
					path.setAlpha(0);
					pathlist.remove(i);
				}
				mContext.mapView.invalidate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected boolean onTap(int i) {
		// TODO Auto-generated method stub
		// setFocus(GeoList.get(i));
		MapView.LayoutParams geoLP = (MapView.LayoutParams) mContext.popView
				.getLayoutParams();
		GeoPoint point = getPoint(GeoList.get(i));
		mContext.setCoitem(GeoList.get(i));
		Projection projection = mContext.mapView.getProjection();
		Point pt = projection.toPixels(point, null);
		if (GeoList.get(i).getSnippet().equals("1"))
			geoLP.point = mContext.mapView.getProjection().fromPixels(pt.x - 1,
					pt.y - 14);
		else
			geoLP.point = mContext.mapView.getProjection().fromPixels(pt.x - 1,
					pt.y - 38);
		mContext.mapView.updateViewLayout(mContext.popView, geoLP);

		String index = GeoList.get(i).getTitle();
		if (mContext.indextag.equals(index)) {
			if (mContext.popView.getVisibility() == View.VISIBLE)
				mContext.popView.setVisibility(View.GONE);
			else
				mContext.popView.setVisibility(View.VISIBLE);
		} else {
			mContext.popView.setVisibility(View.VISIBLE);
		}

		mContext.indextag = index;
		Map<String, Object> waymap = wlist.get(Integer.valueOf(index));
		String sysImg = (String) waymap.get("sysImg");
		String str = sysImg.substring(0, sysImg.lastIndexOf("."));
		String str2 = sysImg
				.substring(sysImg.lastIndexOf("."), sysImg.length());
		sysImg = str + "_ico" + str2;
		String landspaceName = (String) waymap.get("landspaceName");
		String landscapeDesc = (String) waymap.get("landscapeDesc");

		ImageView imgview = (ImageView) mContext.findViewById(R.id.imgMyAvisit);
		Bitmap bitm = mContext.getImageBitmap(sysImg, false);
		imgview.setImageBitmap(bitm);

		TextView txtAMName = (TextView) mContext.findViewById(R.id.txtAMName);
		txtAMName.setText(landspaceName);

		TextView txtAMNote = (TextView) mContext.findViewById(R.id.txtAMNote);
		txtAMNote.setText(landscapeDesc);

		return true;
	}

	public GeoPoint getPoint(OverlayItem overLayItem) {
		GeoPoint gpoint = null;
		try {
			int mapsize = mContext.mapView.getZoomLevel();
			int s = (19 - mapsize);
			int ss = 200;
			for (int i = 0; i < s; i++) {
				ss = ss + (200 * i);
			}
			int h = 0;
			if (s > 0) {
				h = ss * s;
			} else {
				h = ss;
			}
			// gpoint = new
			// GeoPoint(overLayItem.getPoint().getLatitudeE6()+h,overLayItem.getPoint().getLongitudeE6());
			gpoint = new GeoPoint(overLayItem.getPoint().getLatitudeE6(),
					overLayItem.getPoint().getLongitudeE6());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return gpoint;
	}

	public Point getPoint2(OverlayItem overLayItem) {
		Point point = null;
		try {
			Projection projection = mContext.mapView.getProjection();
			point = projection.toPixels(overLayItem.getPoint(), null);
			point.set(point.x, point.y + 100);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return point;
	}

}
