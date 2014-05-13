package ms.activitys.coupon;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import ms.activitys.R;
import ms.globalclass.StreamTool;
import ms.globalclass.dbhelp.DBHelperCoupon;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

public class MyCouponView extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// 首页
	private final int TOOLBAR_ITEM_MAP = 1;// 退后
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// 前进
	private final int TOOLBAR_ITEM_NFC = 3;// 创建
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	
	private String index;
	String tag;
	
	private ProgressBar pb;
	
	private DBHelperCoupon dbc;
	
	private ListView clistview;
	
	private ProgressDialog mypDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_coupon_list);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = MyCouponView.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("index"))
			index = bunde.getString("index");
		else
			index = null;
		tag = bunde.getString("tag");
		
		if(!StreamTool.isNetworkVailable(this))
			dbc = new DBHelperCoupon(this,myapp);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		showCouponView();
		
	}
	
	
	private SpecialAdapter getAdapter(List<Map<String,Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.coupon_list, new String[] { "imgurl","couponName","startTime" },
				new int[] { R.id.coupon_image,R.id.store_name,R.id.start_time},100,50,false,share,"ico");
		return simperAdapter;
	}
	
	private SpecialAdapter getAdapter2(List<Map<String,Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.coupon_list2, new String[] { "imgurl","couponName","startTime","isUser" },
				new int[] { R.id.coupon_image,R.id.store_name,R.id.start_time,R.id.is_user },100,50,false,share,"ico");
		return simperAdapter;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			new AlertDialog.Builder(this).setTitle("提示")
//			.setMessage("确定退出?").setIcon(R.drawable.error2)
//			.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
////							setResult(RESULT_OK);// 确定按钮事件
////							android.os.Process.killProcess(android.os.Process.myPid());
////							finish();
//							Intent startMain = new Intent(Intent.ACTION_MAIN);
//					         startMain.addCategory(Intent.CATEGORY_HOME);
//					         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					         startActivity(startMain);
//					         System.exit(0);
//						}
//					}).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// 取消按钮事件
//						}
//					}).show();
//			return false;
//		}
//		return false;
//	}
	
	@Override
	public void onRestart() {
	// TODO Auto-generated method stub
		try{
			Map map = null;
			if(index != null)
			{
				List<Map<String,Object>> cards = myapp.getMyCardsAll();
				map = cards.get(Integer.valueOf(index));
			}
			else
			{
				map = myapp.getHotelMap();
			}
			String storeid = (String)map.get("storeid");
			
			TextView tv = (TextView)findViewById(R.id.TextView01);
			if(StreamTool.isNetworkVailable(this))
			{
				if(tag.equals("download"))
				{
//					JSONObject jobj = api.getCouponAll(storeid);
//					JSONArray jArr = (JSONArray) jobj.get("data");
//					List<Map<String,Object>> list = getCouponsList(jArr);
//					myapp.setCouponAll(list);
//					tv.setText(this.getString(R.string.coupon_lable_4));
					loadThreadData(storeid);
				}
				else
				{
//					JSONObject jobj = api.getMyCouponAll(storeid);
//					JSONArray jArr = (JSONArray) jobj.get("data");
//					List<Map<String,Object>> list = getCouponsList(jArr);
//					myapp.setCouponAll(list);
					tv.setText(this.getString(R.string.coupon_lable_27));
					loadThreadData2(storeid);
				}
			}
			else
			{
				if(tag.equals("my"))
				{
					List<Map<String,Object>> list =  dbc.loadMyCoupon(storeid);
					dbc.close();
					myapp.setCouponAll(list);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		super.onPause();
	} 
	
	public void showCouponView()
	{
		try{
			List<Map<String,Object>> list = null;
			TextView tv = (TextView)findViewById(R.id.TextView01);
			if(tag.equals("download"))
			{
				tv.setText(getString(R.string.coupon_info));
				Map map = null;
				if(index != null)
				{
					List<Map<String,Object>> cards = myapp.getMyCardsAll();
					map = cards.get(Integer.valueOf(index));
				}
				else
				{
					map = myapp.getHotelMap();
				}
				String storeid = (String)map.get("storeid");
				
				loadThreadData(storeid);
				
				clistview = (ListView)findViewById(R.id.ListView_coupon);
				
				clistview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						showProgressDialog();
						// TODO Auto-generated method stub
						String index = String.valueOf(arg2);
//						showProgressDialog();
						openCouponView(index);
					}
				});
			}
			else
			{
				tv.setText(this.getString(R.string.coupon_lable_27));
				Map map = null;
				if(index != null)
				{
					List<Map<String,Object>> cards = myapp.getMyCardsAll();
					map = cards.get(Integer.valueOf(index));
				}
				else
				{
					map = myapp.getHotelMap();
				}
				String storeid = (String)map.get("storeid");
				
				if(StreamTool.isNetworkVailable(this))
				{
					
					loadThreadData2(storeid);
					
					clistview = (ListView)findViewById(R.id.ListView_coupon);
					
					clistview.setOnItemClickListener(new OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							showProgressDialog();
							// TODO Auto-generated method stub
							String index = String.valueOf(arg2);
//							showProgressDialog();
							openCouponView(index);
						}
					});
				}
				else
				{
					list = dbc.loadMyCoupon(storeid);
					dbc.close();
					myapp.setCouponAll(list);
					
					ListView clistview = (ListView)findViewById(R.id.ListView_coupon);
					clistview.setAdapter(getAdapter2(list));
					
					clistview.setOnItemClickListener(new OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							String index = String.valueOf(arg2);
							openCouponView(index);
						}
					});
					
					pb.setVisibility(View.GONE);
					clistview.setVisibility(View.VISIBLE);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadData(final String storeid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try{
					JSONObject jobj = api.getCouponAll(storeid);
					JSONArray jArr = (JSONArray) jobj.get("data");
					List<Map<String,Object>> list = getCouponsList(jArr);
					myapp.setCouponAll(list);
					
					msg.obj = list;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData2(final String storeid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try{
					JSONObject jobj = api.getMyCouponAll(storeid);
					JSONArray jArr = (JSONArray) jobj.get("data");
					List<Map<String,Object>> list = getCouponsList(jArr);
					myapp.setCouponAll(list);
					
					msg.obj = list;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String,Object>> list = (List<Map<String,Object>>)msg.obj;
				if(list != null)
				{
					clistview.setAdapter(getAdapter(list));
				}
				pb.setVisibility(View.GONE);
				clistview.setVisibility(View.VISIBLE);
				break;
			case 1:
				List<Map<String,Object>> list2 = (List<Map<String,Object>>)msg.obj;
				if(list2 != null)
				{
					clistview.setAdapter(getAdapter2(list2));
					pb.setVisibility(View.GONE);
					clistview.setVisibility(View.VISIBLE);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openCouponView(String indexs)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,CouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("cindex", indexs);
			bundle.putString("sindex", index);
			bundle.putString("tag", tag);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    mypDialog.dismiss();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getCouponsList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String couponId = ""; // 主键ID
				if(dobj.has("couponId"))
					couponId = (String) dobj.get("couponId"); 
				
				String startTime = ""; // 有效开始时间
				if(dobj.has("Meta_startTime"))
					startTime = (String) dobj.get("Meta_startTime"); 
				
				String endTime = ""; // 有效结束时间
				if(dobj.has("Meta_endTime"))
					endTime = (String) dobj.get("Meta_endTime"); 
				
				String couponDesc = ""; // 优惠券描述
				if(dobj.has("couponDesc"))
					couponDesc = (String) dobj.get("couponDesc"); 
				
				
				String img = ""; 
				if(dobj.has("sysImg"))
					img = (String) dobj.get("sysImg"); 
				
				String couponType = "";
				if(dobj.has("couponType"))
					couponType = (String) dobj.get("couponType"); 
				
				String sname = "";
				if(dobj.has("sname"))
					sname = (String) dobj.get("sname"); 
				
//				String gongli = "";
//				if(dobj.has("gongli"))
//					gongli = (String) dobj.get("gongli"); 
				
				String couponNumber = "";
				if(dobj.has("couponNumber"))
					couponNumber = (String) dobj.get("couponNumber"); 
				
				String couponName = "";
				if(dobj.has("couponName"))
					couponName = (String) dobj.get("couponName"); 
				
				String simgurl = "";
				if(dobj.has("simgurl"))
					simgurl = (String) dobj.get("simgurl"); 
				
				String storeid = "";
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
				String isASttention = "";
				if(dobj.has("isASttention"))
					isASttention = (String) dobj.get("isASttention"); 
					
				String couponNo = "";
					if(dobj.has("Meta_no"))
						couponNo = (String) dobj.get("Meta_no");
					
				String isUser = null;
					if(dobj.has("isUser"))
						isUser = (String) dobj.get("isUser");
					else
						isUser = "1";
					
				String couponLoadid = "";
					if(dobj.has("couponLoadid"))
						couponLoadid = (String) dobj.get("couponLoadid");
					
				String businessId = "";
					if(dobj.has("businessId"))
						businessId = (String) dobj.get("businessId");
					
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("couponId", couponId);
				map.put("startTime", startTime);
				map.put("endTime", endTime);
				map.put("couponDesc", couponDesc);
				boolean loadimgTag = share.getBoolean("webimage", true);
				if(loadimgTag)
				{
//					map.put("img", getImageBitmap(img,500,500));
//					map.put("simg", getImageBitmap(simgurl,80,50));
					
					map.put("img", null);
					map.put("simg", null);
				}
				else
				{
					map.put("img", null);
					map.put("simg", null);
				}
				map.put("imgurl", img);
				map.put("couponType", couponType);
				map.put("sname", sname);
//				map.put("gongli", "距离您大约："+gongli);
				map.put("couponNumber", couponNumber);
				map.put("couponName", couponName);
				map.put("simgurl", simgurl);
				map.put("storeid", storeid);
				map.put("isASttention", isASttention);
				map.put("couponNo", couponNo);
				if(isUser.equals("1"))
				{
					map.put("isUser", R.drawable.check);
					map.put("isUserStr", "1");
				}
				else
				{
					map.put("isUser", R.drawable.close);
					map.put("isUserStr", "0");
				}
				map.put("couponLoadid", couponLoadid);
				map.put("businessId", businessId);	
				
				
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
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
//			bitmap = Bitmap.createScaledBitmap(bitmap,300,188,true);
			drawable = new BitmapDrawable(bitmap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return drawable;
	}
	
	public Bitmap getImageBitmap(String value,int wit,int hig)
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

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    opt.inSampleSize = 2;
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			bitmap = Bitmap.createScaledBitmap(bitmap,wit,hig,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 1;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas
				.drawRect(0, height, width, height + reflectionGap,
						deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // land do nothing is ok
            	setContentView(R.layout.my_coupon_view);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // port do nothing is ok
            	setContentView(R.layout.my_coupon_view2);
            }
    }
	
	public void showProgressDialog(){
		try{
			mypDialog=new ProgressDialog(this);
            //实例化
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置进度条风格，风格为圆形，旋转的
//            mypDialog.setTitle("等待");
            //设置ProgressDialog 标题
            mypDialog.setMessage(this.getString(R.string.login_lable_21));
            //设置ProgressDialog 提示信息
//            mypDialog.setIcon(R.drawable.wait_icon);
            //设置ProgressDialog 标题图标
//            mypDialog.setButton("",this);
            //设置ProgressDialog 的一个Button
            mypDialog.setIndeterminate(false);
            //设置ProgressDialog 的进度条是否不明确
            mypDialog.setCancelable(true);
            //设置ProgressDialog 是否可以按退回按键取消
            mypDialog.show();
            //让ProgressDialog显示
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
