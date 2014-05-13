package ms.activitys.vipcards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.activitys.R;
import ms.activitys.hotel.HotelActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class OtherStore extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressDialog mypDialog;
	
	private String businessId;
	
	private ListView clistView;
	private ProgressBar pb;
	private int index;
	private List<Map<String,Object>> list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_store_list);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		businessId = bunde.getString("businessId");
		index = bunde.getInt("index");
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyCards();
			}
		});
		
		loadThreadData();
		
		showDataList();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showMyCards();
			return false;
		}
		return false;
	}
	
	public void showDataList()
	{
		try{
			clistView = (ListView)findViewById(R.id.ListView_cards);
			
			clistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
//					String sid = dlist.get(arg2).get("sid").toString();
					Map map = myapp.getMyCardsAll().get(index);
					Map oldmap = list.get(arg2);
					Map newMap = getMyCardMap(map,oldmap);
					if(myapp.getTravelList() != null && myapp.getTravelList().size() > 0)
						myapp.getTravelList().removeAll(myapp.getTravelList());
//					myapp.getMyCardsAll().remove(index);
//					myapp.getMyCardsAll().add(map);
					
					showMyCards();
				}
				
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMyCards()
	{
		try{
			Map<String,Object> map = myapp.getMyCardsAll().get(Integer.valueOf(index));
			String typesMapping = (String)map.get("typesMapping");
			if(typesMapping.equals("09"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,HotelActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putInt("index", Integer.valueOf(index));
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//关闭显示的Activity
			}
			else
			{
				Intent intent = new Intent();
			    intent.setClass( this,CardsView.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//关闭显示的Activity
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try{
					List<Map<String,Object>> dlist = myapp.getMyCardsAll();
					Map map = dlist.get(index);
					
					String storeid = (String)map.get("storeid");
					
					list = new ArrayList<Map<String,Object>>();
					JSONObject jobj = api.getOtherStoreAll(businessId,storeid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getMyCardList(jArr);
					}
					
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
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				if(dlist != null)
				{
					clistView.setAdapter(getAdapter(dlist));
				}
				pb.setVisibility(View.GONE);
				clistView.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	private SimpleAdapter getAdapter(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.other_store_item, new String[] { "storeName","distance" },
				new int[] { R.id.store_name,R.id.vip_juli });
		return simperAdapter;
	}
	
	public List<Map<String,Object>> getMyCardList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String storeid = ""; // 门店id
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
				String storeName = ""; // 门店名字
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String img = ""; // 门店会员卡图片
				if(dobj.has("img"))
					img = (String) dobj.get("img"); 
				
				String couponNumber = ""; 
				if(dobj.has("couponNumber"))
					couponNumber = (String) dobj.get("couponNumber"); 
				
				String storePhone = ""; 
				if(dobj.has("storePhone"))
					storePhone = (String) dobj.get("storePhone"); 
				
				String addressInfomation = ""; 
				if(dobj.has("addressInfomation"))
					addressInfomation = (String) dobj.get("addressInfomation"); 
				
				String storeDesc = ""; 
				if(dobj.has("storeDesc"))
					storeDesc = (String) dobj.get("storeDesc"); 
				
				String typeName = "";  //酒店类型
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				String typesMapping = "";  //酒店类型与客户端得映射
				if(dobj.has("typesMapping"))
					typesMapping = (String) dobj.get("typesMapping"); 
				
				String businessId = ""; 
				if(dobj.has("businessId"))
					businessId = (String) dobj.get("businessId"); 
				
				String distance = ""; 
				if(dobj.has("distance"))
					distance = (String) dobj.get("distance"); 
				
				String woof = ""; 
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof"); 
				
				String longItude = ""; 
				if(dobj.has("longItude"))
					longItude = (String) dobj.get("longItude"); 
				
				String isLu = ""; 
				if(dobj.has("isLu"))
					isLu = (String) dobj.get("isLu"); 
				
				String roomIntroduction = ""; 
				if(dobj.has("roomIntroduction"))
					roomIntroduction = (String) dobj.get("roomIntroduction");
				
				String periphery = ""; 
				if(dobj.has("periphery"))
					periphery = (String) dobj.get("periphery");
				
				String trafficWay = ""; 
				if(dobj.has("trafficWay"))
					trafficWay = (String) dobj.get("trafficWay");
				
				String startingPrice = ""; 
				if(dobj.has("startingPrice"))
					startingPrice = (String) dobj.get("startingPrice");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("storeid", storeid);
				map.put("storeName", storeName);
				if(storePhone == null)
					storePhone = "";
				map.put("storePhone", storePhone);
				map.put("addressInfomation", addressInfomation);
				map.put("storeDesc", storeDesc); 
				map.put("imgurl", img);
				map.put("couponNumber", couponNumber);
				map.put("typeName", typeName);
				map.put("typesMapping", typesMapping);
				map.put("businessId", businessId);
				map.put("distance", this.getString(R.string.cards_lable_20)+"："+distance+" "+this.getString(R.string.map_lable_9));
				map.put("woof", woof);
				map.put("longItude", longItude);
				map.put("isLu", isLu);
				map.put("roomIntroduction", roomIntroduction);
				map.put("periphery", periphery);
				map.put("trafficWay", trafficWay);
				map.put("startingPrice", startingPrice);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public Map getMyCardMap(Map map,Map oldmap){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
				
				map.put("storeid", oldmap.get("storeid"));
				map.put("storeName", oldmap.get("storeName"));
				map.put("storePhone", oldmap.get("storePhone"));
				map.put("addressInfomation", oldmap.get("addressInfomation"));
				map.put("storeDesc", oldmap.get("storeDesc"));
				map.put("imgurl", oldmap.get("imgurl"));
				map.put("couponNumber", oldmap.get("couponNumber"));
				map.put("typeName", oldmap.get("typeName"));
				map.put("typesMapping", oldmap.get("typesMapping"));
				map.put("businessId", oldmap.get("businessId"));
				map.put("woof", oldmap.get("woof"));
				map.put("longItude", oldmap.get("longItude"));
				map.put("isLu", oldmap.get("isLu"));
				map.put("roomIntroduction", oldmap.get("roomIntroduction"));
				map.put("periphery", oldmap.get("periphery"));
				map.put("trafficWay", oldmap.get("trafficWay"));
				map.put("startingPrice", oldmap.get("startingPrice"));
	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
}
