package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class UserOrderHistoryActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static UserOrderHistoryActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private String openid;
	private String tag;
	
	private ListView mylist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_order_history_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		openid = bunde.getString("openid");
		if(bunde.containsKey("tag"))
		{
			tag = bunde.getString("tag");
		}
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openChatMessageInfo();
			}
		});
		
		initView();
	}
	
	public void initView()
	{
		try{
			mylist = (ListView)findViewById(R.id.listviews);
			
			showMyLoadingDialog(getString(R.string.map_lable_11));
			getUserOrderListData();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getUserOrderListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
						JSONObject jobj = null;
						if(tag.equals("xieyi"))
						{
							jobj = api.getUserAgreementOrderHistory();
						}
						else
							jobj = api.getUserOrderHistory(openid);
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> list = getOrderList(jArr);
							msg.obj = list;
						}
						else
						{
							msg.obj = null;
						}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
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
				List<Map<String, Object>> list3 = (List<Map<String, Object>>)msg.obj;
				if(list3 != null)
				{
					mylist.setVisibility(View.VISIBLE);
					
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter simperAdapter = new SpecialAdapter(UserOrderHistoryActivity.this, list3,
							R.layout.user_order_list_item, new String[] { "storeName", "orderTime","personName","nickName","orderPhone","orderState","remark","totalAmount" },
							new int[] { R.id.store_name, R.id.order_time,R.id.order_username,R.id.order_weixin,R.id.order_phone,R.id.order_start,R.id.store_doc_txt,R.id.order_totle },55,55,false,share,"max");
		
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
						}
					});
					
				}
				else
				{
					mylist.setAdapter(null);
					mylist.setVisibility(View.GONE);
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			}
		}
	};
	
	public List<Map<String,Object>> getOrderList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String,Object> dmap = new HashMap<String,Object>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String personName = ""; // 预定人名字
				if(dobj.has("personName"))
					personName = (String) dobj.get("personName");
				
				String orderTime = ""; // 预定时间
				if(dobj.has("orderTime"))
					orderTime = (String) dobj.get("orderTime"); 
				
				String orderPhone = ""; // 预定人电话
				if(dobj.has("orderPhone"))
					orderPhone = (String) dobj.get("orderPhone"); 
				
				String totalAmount = ""; // 总金额
				if(dobj.has("totalAmount"))
					totalAmount = (String) dobj.get("totalAmount"); 
				
				String orderRoomNo = ""; // 预定房间数
				if(dobj.has("orderRoomNo"))
					orderRoomNo = (String) dobj.get("orderRoomNo");
				
				String orderState = ""; // 订单状态 1为已经确认，0.为未确认，2。为取消订单
				if(dobj.has("orderState"))
					orderState = (String) dobj.get("orderState"); 
				
				String orderNo = ""; // 订单号
				if(dobj.has("orderNo"))
					orderNo = (String) dobj.get("orderNo"); 
				
				String checkoutDate = ""; // 离店时间
				if(dobj.has("checkoutDate"))
					checkoutDate = (String) dobj.get("checkoutDate"); 
				
				String ifneedBreakfast = ""; // 是否需要早餐 0是否 1是是
				if(dobj.has("ifneedBreakfast"))
					ifneedBreakfast = (String) dobj.get("ifneedBreakfast");
				
				String roomType = ""; // 房型
				if(dobj.has("roomType"))
				{
					Object rooms = dobj.get("roomType");
					roomType = rooms.toString();
				}
				
				String appName = ""; // 客户端名字
				if(dobj.has("appName"))
					appName = (String) dobj.get("appName"); 
				
				String remark = ""; // 备注
				if(dobj.has("remark"))
					remark = (String) dobj.get("remark"); 
				
				String checkinDate = ""; // 入住时间
				if(dobj.has("checkinDate"))
					checkinDate = (String) dobj.get("checkinDate");
				
				String dayNum = ""; //入住天数
				if(dobj.has("dayNum"))
					dayNum = (String) dobj.get("dayNum"); 
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String checkinNum = ""; //入住人数
				if(dobj.has("checkinNum"))
					checkinNum = (String) dobj.get("checkinNum"); 
				
				String storeId = ""; 
				if(dobj.has("storeId"))
					storeId = (String) dobj.get("storeId"); 
				
				String storeName = ""; //商家名
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String nickName = ""; 
				if(dobj.has("nickName"))//微信昵称
					nickName = (String) dobj.get("nickName"); 
				
				String companyName = ""; 
				if(dobj.has("companyName"))//协议公司名字
					companyName = (String) dobj.get("companyName"); 
				
				String str = "";
				if(!checkinNum.equals("") && !checkinNum.equals("0"))
				{
					str = str + getString(R.string.hotel_label_139) + checkinNum+" ";
					str = str + getString(R.string.hotel_label_143) + dayNum+" ";
					str = str + getString(R.string.hotel_label_140) + checkinDate+" ";
					str = str + getString(R.string.hotel_label_142) + roomType+" ";
					str = str + getString(R.string.hotel_label_141) + checkoutDate+" ";
					if(ifneedBreakfast.equals("0"))
						str = str + getString(R.string.hotel_label_145);
					else
						str = str + getString(R.string.hotel_label_144);
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("personName", personName);
				map.put("orderTime", orderTime);
				map.put("orderPhone", orderPhone);
				map.put("totalAmount", totalAmount);
				map.put("orderRoomNo", orderRoomNo);
				map.put("orderState", orderState);
				map.put("orderNo", orderNo);
				map.put("checkoutDate", checkoutDate);
				map.put("ifneedBreakfast", ifneedBreakfast);
				map.put("roomType", roomType);
				map.put("appName", appName);
				map.put("remark", str + " " +remark);
				map.put("checkinDate", checkinDate);
				map.put("dayNum", dayNum);
				map.put("checkinNum", checkinNum);
				map.put("storeId", storeId);
				map.put("storeName", storeName);
				map.put("nickName", nickName);
				map.put("companyName", companyName);
					
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openChatMessageInfo();
			return false;
		}
		return false;
	}
	
	public void openChatMessageInfo()
	{
		try{
			Intent intent = new Intent();
			if(tag != null && !tag.equals(""))
				intent.setClass( this,MainTabActivity.class);
			else
				intent.setClass( this,ChatMessageInfoActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMyLoadingDialog(String message)
    {
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
}
