//package ms.globalclass.map;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Point;
//import android.graphics.drawable.Drawable;
//import android.os.Vibrator;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.GestureDetector.OnGestureListener;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import ms.activitys.map.LocalMap;
//import ms.activitys.map.MapPage;
//import ms.activitys.travel.RecommendedRouteActivity;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//import com.google.android.maps.OverlayItem;
//import com.google.android.maps.Projection;
//import com.google.android.maps.MapView.LayoutParams;
//import ms.activitys.R;
//
//public class OverItemMapPopu extends ItemizedOverlay<OverlayItem> implements GestureDetector.OnDoubleTapListener{
//
//	private Drawable marker;
//	private RecommendedRouteActivity mContext;
//	private ArrayList<OverlayItem> GeoList = new ArrayList<OverlayItem>();
//	GeoPoint mpoint;
//	private boolean moveMarker = true;
//	private boolean isShow = false;
//	private boolean isshowline = false;
//	MapView mapView;
//	private int downindex = 0;
//	private MyApp myapp;
//	boolean b = false;
//	private Vibrator vibrator;
//	private MapController mc;
//	private int size = 3;
//	
//	private GestureDetector gestureDetector;
//	private OnGestureListener onGestureListener;
//	private Map<String,Integer> sizemap = new HashMap<String,Integer>();
//	
//	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
//    | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
//	
//	/**
//     * 用于保存构成曲线的点的数据
//     */
//    private List<GeoPoint> linePoints = new ArrayList<GeoPoint>();
//	
//    public OverItemMapPopu(Drawable defaultMarker) {
//        super(boundCenterBottom(defaultMarker));
//   }
//	
//	public OverItemMapPopu(Drawable defaultMarker,RecommendedRouteActivity context,GeoPoint point,String str,MapView map,MyApp mpp,MapController mapController,String tag) {
//		//super(boundCenterBottom(defaultMarker));
////		super(defaultMarker);
//		this(defaultMarker);
////		this(boundCenterBottom(defaultMarker));
//		// TODO Auto-generated constructor stub
//		double lat=39.9022;
//		double lon=116.3924;
//		
//		this.marker=defaultMarker;
//		this.mContext=context;
//		this.mpoint=point;
//		this.mapView = map;
//		this.myapp = mpp;
//		this.mc = mapController;
//		
//		sizemap.put("15", 2);
//		sizemap.put("16", 4);
//		sizemap.put("17", 6);
//		sizemap.put("18", 8);
//		sizemap.put("19", 10);
////		GeoPoint p=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
////		GeoList.add(new OverlayItem(p,"",""));
//		GeoList.add(new OverlayItem(point,str,tag));
//		mContext.setGeoList(GeoList);
//		populate();
//	}
//	
//	@Override
//	protected OverlayItem createItem(int i) {
//		// TODO Auto-generated method stub
//		return GeoList.get(i);
//	}
//
//	@Override
//	public int size() {
//		// TODO Auto-generated method stub
//		return GeoList.size();
//	}
//	
//	public void addOverlay(OverlayItem overlay) {
//		GeoList.add(overlay);
//	    this.populate();
//	}
//	
//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		// TODO Auto-generated method stub
//		
//		Projection projection = mapView.getProjection(); 
//		for (int index =0; index<size(); index++) {
//			OverlayItem overLayItem = getItem(index); 
//			
//			String title = overLayItem.getTitle();
//			Point point = projection.toPixels(overLayItem.getPoint(), null); 
//			
////			Paint paintCircle = new Paint();
////			paintCircle.setAntiAlias(true);
////			//抗锯齿
////			paintCircle.setColor(Color.RED);
////			paintCircle.setAlpha(100);
//			
//			Paint mRadiusPen = new Paint();
//			mRadiusPen.setStyle(Paint.Style.STROKE);
//			mRadiusPen.setColor(Color.RED);
//			mRadiusPen.setAntiAlias(true);
//			mRadiusPen.setStrokeWidth(1);
//			// Accuracy Radius Background Red Opaque
//			Paint mRadiusBackground = new Paint();
//			mRadiusBackground.setStyle(Paint.Style.FILL);
//			mRadiusBackground.setARGB(36, 255, 0, 0);
//			mRadiusBackground.setAntiAlias(true);
//			
////			if(mapView.getZoomLevel() > 14)
////			{
////				String mapzoom = String.valueOf(mapView.getZoomLevel());
////				int number = sizemap.get(mapzoom);
////				size = mapView.getZoomLevel() * number;
////			}
////			else
////			{
////				size = mapView.getZoomLevel() * 0;
////			}
//			System.out.println("oversize==========="+size);
//			canvas.drawCircle(point.x, point.y-14, 100, mRadiusPen); // 画圆
//			canvas.drawCircle(point.x, point.y-14, 100, mRadiusBackground); // 画圆
//
//		}
//		
//		super.draw(canvas, mapView, shadow);
//	}
//	
//	
//	@Override
//	protected boolean onTap(int i) {
//		// TODO Auto-generated method stub
////		if(!isShow)
////		{
////			setFocus(GeoList.get(i));
////			MapView.LayoutParams geoLP = (MapView.LayoutParams) mContext.popView.getLayoutParams();
////			GeoPoint point = getPoint(GeoList.get(i));
////			mContext.setCoitem(GeoList.get(i));
////			geoLP.point = point;
////			mapView.updateViewLayout(mContext.popView, geoLP);
////			mContext.popView.setVisibility(View.VISIBLE);
////			TextView tv=(TextView) mContext.findViewById(R.id.map_bubbleTitle);
////			tv.setText(GeoList.get(i).getTitle());
////			TextView tv2 = (TextView) mContext.findViewById(R.id.map_bubbleText);
////			tv2.setText(GeoList.get(i).getSnippet());
////			isShow = true;
////		}
////		else
////		{
////			mContext.popView.setVisibility(View.GONE);
////			isShow = false;
////		}
//		return true;
//	}
//	
//	public GeoPoint getPoint(OverlayItem overLayItem)
//	{
//		GeoPoint gpoint = null;
//		try{
//			int mapsize = mapView.getZoomLevel();
//			int s = (19 - mapsize);
//			int ss = 200;
//			for(int i=0;i<s;i++)
//			{
//				ss = ss + (200*i);
//			}
//			int h = 0;
//			if(s > 0)
//			{
//				h = ss * s;
//			}
//			else
//			{
//				h = ss;
//			}
//			gpoint = new GeoPoint(overLayItem.getPoint().getLatitudeE6()+h,overLayItem.getPoint().getLongitudeE6());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return gpoint;
//	}
//	
//	public Point getPoint2(OverlayItem overLayItem)
//	{
//		Point point = null;
//		try{
//			Projection projection = mapView.getProjection(); 
//			point = projection.toPixels(overLayItem.getPoint(), null); 
//			point.set(point.x, point.y+100);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return point;
//	}
//	
////	 @Override
////	public boolean onTouchEvent(MotionEvent ev, MapView view) {
////		super.onTouchEvent(ev, view);
////
////		switch (ev.getAction()) {
////		case MotionEvent.ACTION_DOWN:
////			
////			break;
////		case MotionEvent.ACTION_MOVE:
////			if (moveMarker) {
////				// Point p = new Point((int) ev.getX(), (int) ev.getY());
////				GeoPoint gp = mapView.getProjection().fromPixels((int) ev.getX(),
////						(int) ev.getY());
////				// remove the last marker
////				mapView.getOverlays().remove(this);
////				placeMarker(gp);
////			}
////			break;
////		case MotionEvent.ACTION_UP:
////			moveMarker = false;
////			break;
////		default:
////			break;
////		}
////
////		return moveMarker;
////	}
//	
//	 @Override
//	public boolean onTouchEvent(MotionEvent ev, MapView view) {
//		super.onTouchEvent(ev, view);
////		vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);  
////		switch (ev.getAction()) {
////		case MotionEvent.ACTION_DOWN:
//////			moveMarker = true;
////			Point point = mapView.getProjection().toPixels(mpoint, null);
////			System.out.println("evx====="+ev.getX());
////			System.out.println("px====="+point.x);
////			
////			if((int)ev.getX() - point.x < 0)
////			{
////				if(point.x - (int)ev.getX() < 1 && point.y - (int)ev.getY() < 1)
////				{
////					moveMarker = true;
////		            long[] pattern = {600,400}; // OFF/ON/OFF/ON...  
////		            vibrator.vibrate(pattern, -1);//-1不重复，非-1为从pattern的指定下标开始重复  
////				}
////				else
////					moveMarker = false;
////			}
////			else
////			{
////				if((int)ev.getX() - point.x < 1 && (int)ev.getY() - point.y < 1)
////				{
////					moveMarker = true;
////		            long[] pattern = {600,400}; // OFF/ON/OFF/ON...  
////		            vibrator.vibrate(pattern, -1);//-1不重复，非-1为从pattern的指定下标开始重复  
////				}
////				else
////					moveMarker = false;
////			}
////			downindex = 0;
////			break;
////		case MotionEvent.ACTION_MOVE:
////			downindex++;
////			
////			System.out.println("moveMarker========"+moveMarker);
////			if (moveMarker) {
////				// Point p = new Point((int) ev.getX(), (int) ev.getY());
//////				if(null!=vibrator){  
//////		            vibrator.cancel();  
//////		        }  
////				GeoPoint gp = mapView.getProjection().fromPixels(
////						(int) ev.getX(), (int) ev.getY());
////				
////				double lat = gp.getLatitudeE6() / 1E6;
////				double lng = gp.getLongitudeE6() / 1E6;
////				
////				myapp.setLatitude(gp.getLatitudeE6());
////				myapp.setLongitude(gp.getLongitudeE6());
////				myapp.setLat(String.valueOf(lat));
////				myapp.setLng(String.valueOf(lng));
////				// remove the last marker
////				mapView.getOverlays().remove(this);
////				
////				
////				placeMarker(gp);
////			}
////			break;
////		case MotionEvent.ACTION_UP:
////			downindex = 0;
////			if(null!=vibrator){  
////	            vibrator.cancel();  
////	        }  
////			moveMarker = false;
////			System.out.println("moveMarker_UP========"+moveMarker);
////			break;
////		default:
////			break;
////		}
////
////		return moveMarker;
//		return false;
//	}
//
//	private void placeMarker(GeoPoint gp) {
//		OverItemMapPopu itemizedOverlay = new OverItemMapPopu(
//				marker, mContext, gp, "您当前的位置", mapView,myapp,mc,"1");
//
//		OverlayItem overlayitem = new OverlayItem(gp, "您当前的位置",
//				"拖动可改变当前您的位置");
//
//		itemizedOverlay.addOverlay(overlayitem);
//		mapView.getOverlays().add(itemizedOverlay);
//		mapView.postInvalidate();
//	}
//
//	@Override
//	public boolean onDoubleTap(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onDoubleTapEvent(MotionEvent e) {
//		// TODO Auto-generated method stub
////		mc = mapView.getController();
//		mc.zoomIn();
//		return false;
//	}
//
//	@Override
//	public boolean onSingleTapConfirmed(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//}
