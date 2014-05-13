//package ms.globalclass.map;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
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
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.text.Html;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import ms.activitys.map.AttractionsMapActivity;
//import ms.activitys.more.Settings;
//import ms.activitys.vipcards.CardsView;
//import ms.globalclass.U;
//import ms.globalclass.httppost.Douban;
//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.MapView;
//import com.google.android.maps.OverlayItem;
//import ms.activitys.R;
//
//public class CustomItemOverlayAttracions extends ItemizedOverlay<OverlayItem>{
//
//	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
//	private MyApp myapp;
//	private Douban api;
//	private boolean moveMarker = true;
//	private Drawable marker;
//	private MapView map;
//	private int sindex;
//	private ProgressDialog mypDialog;
//	private ImageView storeimgview;
//	private Dialog myDialogs;
//	  
//	   private Context context;
//	  
//	   public CustomItemOverlayAttracions(Drawable defaultMarker) {
//	        super(boundCenterBottom(defaultMarker));
//	   }
//	  
//	   public CustomItemOverlayAttracions(Drawable defaultMarker, Context context,MyApp mapp,Douban apis,MapView mapView) {
//		   super(boundCenterBottom(defaultMarker));
////	        this(defaultMarker);
//	        this.marker = defaultMarker;
//	        this.myapp = mapp;
//	        this.context = context;
//	        this.api = apis;
//	        this.map = mapView;
//	   }
//
//	   @Override
//	   protected OverlayItem createItem(int i) {
//	      return mapOverlays.get(i);
//	   }
//
//	   @Override
//	   public int size() {
//	      return mapOverlays.size();
//	   }
//	   
//	   private Handler handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				switch (msg.what) {
//				case 0:
//					Bitmap bitm = (Bitmap)msg.obj;
//					storeimgview.setImageBitmap(bitm);
//					if(mypDialog != null)
//						mypDialog.dismiss();
//					if(myDialogs != null)
//						myDialogs.show();
//					break;
//				default:
//					super.handleMessage(msg);
//				}
//			}
//		};
//	  
//	   @Override
//	   protected boolean onTap(int index) {
//	      OverlayItem item = mapOverlays.get(index);
//	      
//	      mypDialog = ProgressDialog.show(context, null, context.getString(R.string.map_lable_11), true,
//	                false);
//	      
//	      String [] strs = item.getSnippet().split(",");
//	      String pkid = strs[0];
//	      final String findex = strs[1];
//	      List<Map<String,Object>> dlist = myapp.getTravelAllList();
//	      final Map<String,Object> maps = getMap(dlist,pkid);
//	      
//	      if(maps != null)
//	      {
//	    	  	Map<String,Object> nmap = (Map<String,Object>)maps.get("map");
//	    	  	sindex = (Integer)maps.get("sindex");
//	    	  	final String pid = (String)nmap.get("pkid");
//	    	  	final String imgurl = (String)nmap.get("img");
//	    	  	final String landscapeName = (String)nmap.get("landscapeName");
//				final String landscapeDesc = (String)nmap.get("landscapeDesc");
//				final String address = (String)nmap.get("address");
//				
//				final View view = LayoutInflater.from(context).inflate(R.layout.google_map_popup, null);
//				
//				storeimgview = (ImageView)view.findViewById(R.id.widget34);
//				
//				myDialogs = new Dialog(context, R.style.AliDialog);
//				myDialogs.setContentView(view);
//				
//				new Thread(){
//					public void run(){
//						Message msg = new Message();
//						msg.what = 0;
//						Bitmap bit = getImageBitmap(imgurl);
//						msg.obj = bit;
//						handler.sendMessage(msg);
//					}
//				}.start();
//				
//				TextView title = (TextView)view.findViewById(R.id.widget35);
//				title.setText(landscapeName);
//				
//				String source = landscapeDesc + "<br><b>"+context.getString(R.string.map_lable_12)+"："+ address+"</b>";
//				TextView count = (TextView)view.findViewById(R.id.widget36);
//				count.setText(Html.fromHtml(source)); 
//				
//				Button btn = (Button)view.findViewById(R.id.add_vip_btn);
//				
//				btn.setText(context.getString(R.string.gmap_lable_1));
//				btn.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						
//						if(myDialogs != null)
//							myDialogs.dismiss();
//						
//						Intent intent = new Intent();
//					    intent.setClass( context,AttractionsMapActivity.class);
//					    Bundle bundle = new Bundle();
//					    bundle.putInt("index", Integer.valueOf(findex));
//						bundle.putInt("sindex", sindex);
//						intent.putExtras(bundle);
//						context.startActivity(intent);//开始界面的跳转函数
////					    ((Activity) context).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//					    ((Activity) context).finish();//关闭显示的Activity
//						
//					}
//				});
//				
//				Button btn2 = (Button)view.findViewById(R.id.clear_btn);
//				btn2.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						myDialogs.dismiss();
//					}
//				});
//	      }
//	      else
//	      {
//	    	  if(mypDialog != null)
//					mypDialog.dismiss();
//	    	  
//	    	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//		      dialog.setTitle(item.getTitle());
//		      dialog.setMessage(item.getSnippet());
//		      dialog.show();
//	      }
//	      return true;
//	   }
//	   
//	public void addOverlay(OverlayItem overlay) {
//		mapOverlays.add(overlay);
//		this.populate();
//	}
//	
//	public Bitmap getImageBitmap(String value)
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
//	public Map<String,Object> getMap(List<Map<String,Object>> dlist,String pkid)
//	{
//		Map<String,Object> maps = new HashMap<String,Object>();
//		Map<String,Object> nmap = null;
//		int index = 0;
//		if (dlist != null) {
//			for (int i = 0; i < dlist.size(); i++) {
//				Map<String, Object> map = dlist.get(i);
//				String pid = (String) map.get("pkid");
//
//				if (pid.equals(pkid)) {
//					nmap = map;
//					index = i;
//					break;
//				}
//			}
//		}
//		maps.put("map", nmap);
//		maps.put("sindex", index);
//		return maps;
//	}
//}
