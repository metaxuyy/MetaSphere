//package ms.globalclass.map;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
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
//import android.view.Display;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import ms.activitys.hotel.ContactActivity;
//import ms.activitys.hotel.HotelActivity;
//import ms.activitys.more.Settings;
//import ms.activitys.vipcards.CardsView;
//import ms.activitys.vipcards.HomePage;
//import ms.globalclass.U;
//import ms.globalclass.dbhelp.DBHelperMessage;
//import ms.globalclass.httppost.Douban;
//import ms.globalclass.listviewadapter.PinyinComparator;
//
//import com.google.android.maps.ItemizedOverlay;
//import com.google.android.maps.MapView;
//import com.google.android.maps.OverlayItem;
//import ms.activitys.R;
//
//public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem>{
//
//	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
//	private MyApp myapp;
//	private Douban api;
//	private String idex = "0";
//	private boolean moveMarker = true;
//	private Drawable marker;
//	private MapView map;
//	private ProgressDialog mypDialog;
//	private ImageView storeimgview;
//	private Dialog myDialogs;
//	private static DBHelperMessage db;
//	  
//	   private Context context;
//	  
//	   public CustomItemizedOverlay(Drawable defaultMarker) {
//	        super(boundCenterBottom(defaultMarker));
//	   }
//	  
////	   public CustomItemizedOverlay(Drawable defaultMarker, Context context,MyApp mapp,Douban apis) {
////	        this(defaultMarker);
////	        this.myapp = mapp;
////	        this.context = context;
////	        this.api = apis;
////	   }
//	   
//	   public CustomItemizedOverlay(Drawable defaultMarker, Context context,MyApp mapp,Douban apis,MapView mapView) {
//	        this(defaultMarker);
//	        this.marker = defaultMarker;
//	        this.myapp = mapp;
//	        this.context = context;
//	        this.api = apis;
//	        this.map = mapView;
//	        db = new DBHelperMessage(context, myapp);
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
//					myDialogs.show();
//					break;
//				case 1:
//					try {
//						JSONObject jobj = (JSONObject) msg.obj;
//						String success = jobj.getString("success");
//						if(mypDialog != null)
//							mypDialog.dismiss();
//						if(success.equals("true"))
//						{
//							JSONObject stroeobj = jobj.getJSONObject("newstoreinfo");
//							Map<String,Object> storemap = U.getNewStoreInfo(stroeobj);
//							List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
//							slist.add(storemap);
//							db.openDB();
//							db.saveStoreInfoAllData(slist);
//							List<Map<String,Object>> storelist = db.getStoreInfoAllData();
//							myapp.setMyCardsAll(storelist);
//							if(ContactActivity.instance != null)
//							{
//								Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());
//								ContactActivity.instance.getMyStoreListData();
//							}
//							db.closeDB();
//							
//							Toast.makeText(context, context.getString(R.string.coupon_lable_25), Toast.LENGTH_LONG).show();
//							myapp.setUpdatetag("0");
//							
//							if(myDialogs != null)
//								myDialogs.dismiss();
//						}
//						else
//						{
//							String msgstr = jobj.getString("msg");
//							Toast.makeText(context, context.getString(R.string.coupon_lable_26)+msgstr, Toast.LENGTH_LONG).show();
//						}
//					}catch(Exception ex){
//						ex.printStackTrace();
//					}
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
//	      String pkid = item.getSnippet();
//	      List<Map<String,Object>> dlist = myapp.getStorelist();
//	      final Map<String,Object> nmap = getMap(dlist,pkid);
//	      String bid2 = (String)nmap.get("bid");  
//	      
//	      List<Map<String,Object>> cardlist = myapp.getMyCardsAll();
//	      if(cardlist != null)
//	      {
//		      for(int i=0;i<cardlist.size();i++)
//		      {
//		        	Map<String,Object> map = cardlist.get(i);
////		        	String bid = (String)map.get("businessId");
//		        	String storeid = (String)map.get("storeid");
//		        	
//					if(storeid != null && storeid.equals(pkid))
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
////	    	  	final String areaName = (String)nmap.get("areaName");
//	    	  	final String lons = (String)nmap.get("longItude");
//	    	  	final String lats = (String)nmap.get("woof");
//	    	  	final String storeTyle = (String)nmap.get("storeType");
//	    	  	final String areaid = (String)nmap.get("areaid");
////	    	  	final String storeId = (String)nmap.get("storeId");
////	    	  	Bitmap bimg = (Bitmap)nmap.get("bimg");
//	    	  	String adress = (String)nmap.get("adress");
//				String storePhone = (String)nmap.get("storePhone");
//				String stroeDesc = (String)nmap.get("storeDesc");
//				String bname = (String)nmap.get("bname");
//				final String bid = (String)nmap.get("bid");
//				final String province = (String)nmap.get("province");
//				
////				Drawable drawable = getDrawableZoon(bimg,500,200);
//				
////				final View view = LayoutInflater.from(context).inflate(R.layout.google_map_popup, null);
//				final View view = LayoutInflater.from(context).inflate(R.layout.map_add_store_dialog, null);
//				
////				storeimgview = (ImageView)view.findViewById(R.id.widget34);
////				
////				new Thread(){
////					public void run(){
////						Message msg = new Message();
////						msg.what = 0;
////						Bitmap bit = getImageBitmap(imgurl);
////						msg.obj = bit;
////						handler.sendMessage(msg);
////					}
////				}.start();
//				
//				TextView title = (TextView)view.findViewById(R.id.tilte_txt);
//				title.setText(storeName);
//				
//				String source = stroeDesc + "<br><b>"+context.getString(R.string.map_lable_12)+"："+ adress+"</b><br><b>"+context.getString(R.string.map_lable_13)+"："+storePhone+"</b>";
//				TextView count = (TextView)view.findViewById(R.id.content_txt);
//				count.setText(Html.fromHtml(source)); 
//				
//				myDialogs = new Dialog(context,R.style.MyMapDialog);
//				myDialogs.setContentView(view);
//				
//				Window dialogWindow = myDialogs.getWindow();
//				WindowManager m = ((Activity) context).getWindowManager();
//		        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//				lp.width = (int) (d.getWidth() * 0.85);
//				dialogWindow.setAttributes(lp);
//				
////				myDialog.show();
////				final AlertDialog menuDialog = new AlertDialog.Builder(context).create();
////				menuDialog.setView(view);
////				menuDialog.getWindow().getDecorView().getBackground().setAlpha(100);
////				menuDialog.setOnKeyListener(new OnKeyListener() {
////					public boolean onKey(DialogInterface dialog, int keyCode,
////							KeyEvent event) {
////						if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
////							dialog.dismiss();
////						return false;
////					}
////				});
////				
////				menuDialog.show();
//				
////				View bottomBtn = view.findViewById(R.id.bottom_btn_layout);
////				bottomBtn.getBackground().setAlpha(150);
//				
////				View hearBtn = view.findViewById(R.id.btn_header);
////				hearBtn.getBackground().setAlpha(150);
//				
//				Button btn = (Button)view.findViewById(R.id.add_vip_btn);
//				
//				try{
////					JSONObject jobj = api.isSelectCards(myapp.getPfprofileId(),pid,bid,null);
////					String tag = jobj.getString("tag");
//					boolean tag = U.isStoreExist(pid, myapp.getMyCardsAll());
//					if(tag)
//					{
//						btn.setText(context.getString(R.string.hotel_label_11));
//						btn.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								myDialogs.dismiss();
//								
//								Map<String,Object> map = myapp.getMyCardsAll().get(Integer.valueOf(idex));
//								getMyCardMap(map,nmap);
//								
//								String typesMapping = (String)map.get("typesMapping");
//								System.out.println("typesMapping======"+typesMapping);
//								if(typesMapping.equals("09"))
//								{
//									Intent intent = new Intent();
//								    intent.setClass( context,HotelActivity.class);
//								    Bundle bundle = new Bundle();
//									bundle.putInt("index", Integer.valueOf(idex));
//									intent.putExtras(bundle);
//									context.startActivity(intent);//开始界面的跳转函数
//									((Activity) context).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//									((Activity) context).finish();//关闭显示的Activity
//								}
//								else
//								{
//									Intent intent = new Intent();
//								    intent.setClass( context,CardsView.class);
//								    Bundle bundle = new Bundle();
//									bundle.putString("index", idex);
//									bundle.putString("map", "map");
//									intent.putExtras(bundle);
//									context.startActivity(intent);//开始界面的跳转函数
//								    ((Activity) context).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//								    ((Activity) context).finish();//关闭显示的Activity
//								}
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
//										msg.what = 1;
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
//								mypDialog = ProgressDialog.show(context, null, context.getString(R.string.map_lable_15), true,
//						                false);
//							}
//						});
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				
//				
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
//				
//				mypDialog.dismiss();
//				myDialogs.show();
//				
////				builder.setTitle("门店信息").setView(view).setPositiveButton("加入",
////						new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog, int which) {
////								
////								JSONObject jobj;
////								U.dout(0);
////								
////								try {
////									jobj = api.addCards(myapp.getPfprofileId(),pid,areaid,storeId);
////									String success = jobj.getString("success");
////									if(success.equals("true"))
////									{
////										Toast.makeText(context, "成功加入该店会员", Toast.LENGTH_LONG).show();
////									}
////									else
////									{
////										String msg = jobj.getString("msg");
////										Toast.makeText(context, "加入失败 "+msg, Toast.LENGTH_LONG).show();
////									}
////								}catch(Exception ex){
////									ex.printStackTrace();
////								}
////							}
////						}).setNegativeButton("取消",
////						new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog, int which) {
////								
////							}
////						});
////				alertDialog = builder.create();
////				alertDialog.getWindow().setLayout(400, LayoutParams.WRAP_CONTENT); //设置宽和高
////				alertDialog.show();
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
//	public Drawable getImageDrawable(String value) {
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
//			bitmap = Bitmap.createScaledBitmap(bitmap, 300, 120, true);
//			drawable = new BitmapDrawable(bitmap);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return drawable;
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
//	public Drawable getDrawableZoon(Bitmap bitmap, int w, int h) {
//		Drawable drawable = null;
//		try {
//			bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
//			drawable = new BitmapDrawable(bitmap);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return drawable;
//	}
//	
//	public List<Map<String,Object>> getMyCardList(JSONArray jArr){
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		try{
//			for (int i = 0; i < jArr.length(); i++) {
//				JSONObject dobjs = (JSONObject) jArr.get(i);
//				Log.i("dobjString====", dobjs.toString());
//				String jsonstr = dobjs.toString();
//				JSONObject dobj = new JSONObject(jsonstr);
//				
//				Integer points = 0; // 当前积分
//				if(dobj.has("points"))
//					points = (Integer) dobj.get("points"); 
//				
//				String nameOnCard = ""; // 卡上的名字
//				if(dobj.has("nameOnCard"))
//					nameOnCard = (String) dobj.get("nameOnCard"); 
//				
//				String nameid = ""; // 
//				if(dobj.has("nameid"))
//					nameid = (String) dobj.get("nameid"); 
//				
//				String pfids = ""; // 
//				if(dobj.has("pfids"))
//					pfids = (String) dobj.get("pfids"); 
//				
//				String cardNo = ""; // 会员卡编号
//				if(dobj.has("cardNo"))
//					cardNo = (String) dobj.get("cardNo"); 
//				
//				String joinedDate = ""; // 加入日期
//				if(dobj.has("joinedDate"))
//				{
//					joinedDate = (String) dobj.get("joinedDate"); 
//				}
//				
//				String mdmType = ""; // 会员类型
//				if(dobj.has("mdmType"))
//					mdmType = (String) dobj.get("mdmType"); 
//				
//				String mdmLevel = ""; // 会员等级
//				if(dobj.has("mdmLevel"))
//					mdmLevel = (String) dobj.get("mdmLevel"); 
//				
//				String mdmstatus = ""; // 会员状态
//				if(dobj.has("mdmstatus"))
//					mdmstatus = (String) dobj.get("mdmstatus"); 
//				
//				String expDate = ""; // 失效日期
//				if(dobj.has("expDate"))
//					expDate = (String) dobj.get("expDate"); 
//				
//				String chainCode = ""; // 条形码号或硬卡号
//				if(dobj.has("chainCode"))
//					chainCode = (String) dobj.get("chainCode"); 
//				
//				String storeid = ""; // 门店id
//				if(dobj.has("storeid"))
//					storeid = (String) dobj.get("storeid"); 
//				
//				String storeName = ""; // 门店名字
//				if(dobj.has("storeName"))
//					storeName = (String) dobj.get("storeName"); 
//				
//				String img = ""; // 门店会员卡图片
//				if(dobj.has("img"))
//					img = (String) dobj.get("img"); 
//				
//				String isASttention = ""; 
//				if(dobj.has("isASttention"))
//					isASttention = (String) dobj.get("isASttention"); 
//				
//				String pkid = ""; 
//				if(dobj.has("pkid"))
//					pkid = (String) dobj.get("pkid"); 
//				
//				String couponNumber = ""; 
//				if(dobj.has("couponNumber"))
//					couponNumber = (String) dobj.get("couponNumber"); 
//				
//				String storePhone = ""; 
//				if(dobj.has("storePhone"))
//					storePhone = (String) dobj.get("storePhone"); 
//				
//				String addressInfomation = ""; 
//				if(dobj.has("addressInfomation"))
//					addressInfomation = (String) dobj.get("addressInfomation"); 
//				
//				String storeDesc = ""; 
//				if(dobj.has("storeDesc"))
//					storeDesc = (String) dobj.get("storeDesc"); 
//				
//				String typeName = "";  //酒店类型
//				if(dobj.has("typeName"))
//					typeName = (String) dobj.get("typeName"); 
//				
//				String typesMapping = "";  //酒店类型与客户端得映射
//				if(dobj.has("typesMapping"))
//					typesMapping = (String) dobj.get("typesMapping"); 
//				
//				String businessId = ""; 
//				if(dobj.has("businessId"))
//					businessId = (String) dobj.get("businessId"); 
//				
//				String woof = ""; 
//				if(dobj.has("woof"))
//					woof = (String) dobj.get("woof"); 
//				
//				String longItude = ""; 
//				if(dobj.has("longItude"))
//					longItude = (String) dobj.get("longItude"); 
//				
//				String userimg = ""; 
//				if(dobj.has("userimg"))
//					userimg = (String) dobj.get("userimg"); 
//				
//				Map<String,Object> map = new HashMap<String,Object>();
//				map.put("points", points);
//				map.put("nameOnCard", nameOnCard);
//				map.put("nameid", nameid);
//				map.put("pfids", pfids);
//				map.put("cardNo", cardNo);
//				map.put("joinedDate", joinedDate);
//				map.put("mdmType", mdmType);
//				map.put("mdmLevel", mdmLevel);
//				map.put("mdmstatus", mdmstatus);
//				map.put("expDate", expDate);
//				map.put("chainCode", chainCode);
//				map.put("storeid", storeid);
//				map.put("storeName", storeName);
//				map.put("imgurl", img);
//				map.put("pkid", pkid);
//				map.put("storePhone", storePhone);
//				map.put("addressInfomation", addressInfomation);
//				map.put("storeDesc", storeDesc);
//				map.put("isASttention", isASttention);
//				if(isASttention.equals("0"))
//					map.put("xinxin", R.drawable.ic_star_small);
//				else
//					map.put("xinxin", null);
//				map.put("couponNumber", couponNumber);
//				map.put("typeName", typeName);
//				map.put("typesMapping", typesMapping);
//				map.put("businessId", businessId);
//				map.put("woof", woof);
//				map.put("longItude", longItude);
//				map.put("userimg", userimg);
//	
//				list.add(map);
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return list;
//	}
//	   
////	   @Override
////	public boolean onTouchEvent(MotionEvent ev, MapView view) {
////		super.onTouchEvent(ev, view);
////		
////		switch (ev.getAction()) {
////        case MotionEvent.ACTION_DOWN:
////                
////                break;
////        case MotionEvent.ACTION_MOVE:
////        		if (moveMarker) {
//////        			Point p = new Point((int) ev.getX(), (int) ev.getY());
////                    GeoPoint gp = map.getProjection().fromPixels((int) ev.getX(), (int) ev.getY());
////	        	     // remove the last marker
////	        	    map.getOverlays().remove(this);
////	        	    placeMarker(gp);
////        	    }
////                break;
////        case MotionEvent.ACTION_UP:
////        		moveMarker = false;
////                break;
////        default:
////                break;
////        }
////
////        return moveMarker;
////	}
////	   
////	private void placeMarker(GeoPoint gp) {
////		CustomItemizedOverlay itemizedOverlay =new CustomItemizedOverlay(marker, context,myapp,api,map);
////       
////        OverlayItem overlayitem = new OverlayItem(gp, "Hello", "I'm in Athens, Greece!");
////        
////        itemizedOverlay.addOverlay(overlayitem);
////		map.getOverlays().add(itemizedOverlay);
////		map.postInvalidate();
////	}
//	
//	public Map<String,Object> getMap(List<Map<String,Object>> dlist,String pkid)
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
//	public Map<String,Object> getMyCardMap(Map<String,Object> map,Map<String,Object> oldmap){
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
//}
