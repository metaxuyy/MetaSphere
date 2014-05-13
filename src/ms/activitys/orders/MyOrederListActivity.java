package ms.activitys.orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.product.AccountscenterActivity;
import ms.activitys.product.ReceivingAddressActivity;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterOrder;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyOrederListActivity extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressBar pb;
	private ListView orderView;
	private String appliescStoreid;
	private int index;
	
	public static SpecialAdapterOrder simperAdapter;
	public static SpecialAdapterOrder simperAdapter2;
	private String orderlisttag = "1";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorderlistactivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		Bundle bunde = this.getIntent().getExtras();
		appliescStoreid = bunde.getString("appliescStoreid");
		index = bunde.getInt("index");
		
		showOrderItems();
	}
	
	private void showOrderItems()
	{
		try{
			orderView = (ListView)findViewById(R.id.listOrder);
			
			loadThreadData();
			
			orderView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(MyOrederListActivity.this, MyOrderDetailActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putInt("orderindex", arg2);
					bundle.putInt("index", index);
					bundle.putString("orderlisttag",orderlisttag);
					bundle.putString("appliescStoreid",appliescStoreid);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
				
			});
			
			final TextView textCate_notsend = (TextView)findViewById(R.id.textCate_notsend);
			final TextView textCate_mounth = (TextView)findViewById(R.id.textCate_mounth);
			final TextView textCate_all = (TextView)findViewById(R.id.textCate_all);
			
			textCate_notsend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textCate_notsend.setBackgroundResource(R.drawable.segment_selected_1_bg);
					textCate_notsend.setTextColor(Color.parseColor("#ffffffff"));
					textCate_mounth.setBackgroundResource(R.drawable.segment_normal_2_bg);
					textCate_mounth.setTextColor(Color.parseColor("#ff4b4b4b"));
					textCate_all.setBackgroundResource(R.drawable.segment_normal_3_bg);
					textCate_all.setTextColor(Color.parseColor("#ff4b4b4b"));
					
					TextView textOrderNull = (TextView)findViewById(R.id.textOrderNull);
					textOrderNull.setVisibility(View.GONE);
					pb.setVisibility(View.VISIBLE);
					orderView.setVisibility(View.GONE);
					orderlisttag = "1";
					loadThreadData();
				}
			});
			
			textCate_mounth.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textCate_notsend.setBackgroundResource(R.drawable.segment_normal_1_bg);
					textCate_notsend.setTextColor(Color.parseColor("#ff4b4b4b"));
					textCate_mounth.setBackgroundResource(R.drawable.segment_selected_2_bg);
					textCate_mounth.setTextColor(Color.parseColor("#ffffffff"));
					textCate_all.setBackgroundResource(R.drawable.segment_normal_3_bg);
					textCate_all.setTextColor(Color.parseColor("#ff4b4b4b"));
					
					TextView textOrderNull = (TextView)findViewById(R.id.textOrderNull);
					textOrderNull.setVisibility(View.GONE);
					pb.setVisibility(View.VISIBLE);
					orderView.setVisibility(View.GONE);
					
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar = Calendar.getInstance();
					String cdate = sf.format(calendar.getTime());
					
					calendar.add(Calendar.DAY_OF_MONTH, -30);//取当前日期的后7天. 
					String hdate = sf.format(calendar.getTime());
					orderlisttag = "2";
					loadDataOrderMonth(hdate,cdate);
				}
			});
			
			textCate_all.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textCate_notsend.setBackgroundResource(R.drawable.segment_normal_1_bg);
					textCate_notsend.setTextColor(Color.parseColor("#ff4b4b4b"));
					textCate_mounth.setBackgroundResource(R.drawable.segment_normal_2_bg);
					textCate_mounth.setTextColor(Color.parseColor("#ff4b4b4b"));
					textCate_all.setBackgroundResource(R.drawable.segment_selected_3_bg);
					textCate_all.setTextColor(Color.parseColor("#ffffffff"));
					
					TextView textOrderNull = (TextView)findViewById(R.id.textOrderNull);
					textOrderNull.setVisibility(View.GONE);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private SpecialAdapterOrder getAdapter(List<Map<String,Object>> data) {
		simperAdapter = new SpecialAdapterOrder(this, data,
				R.layout.myorderlistitems, new String[] { "orderNo","totalPrice","orderTime","orderStatus","paymentvalue" },
				new int[] { R.id.textOrderID,R.id.textPrice,R.id.textTime,R.id.textState,R.id.textZhiFuBaoPay },"ico",api,myapp,appliescStoreid,"handle");
		return simperAdapter;
	}
	
	private SpecialAdapterOrder getAdapter2(List<Map<String,Object>> data) {
		simperAdapter2 = new SpecialAdapterOrder(this, data,
				R.layout.myorderlistitems, new String[] { "orderNo","totalPrices","orderTime","orderStatus","paymentvalue" },
				new int[] { R.id.textOrderID,R.id.textPrice,R.id.textTime,R.id.textState,R.id.textZhiFuBaoPay },"ico",api,myapp,appliescStoreid,"history");
		return simperAdapter2;
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj = api.getOrderItem(appliescStoreid);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getOrderItem(jArr);
						
						msg.obj = list;
						myapp.setMyOrderList(list);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadDataOrderMonth(final String stime,final String etime)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				try{
					JSONObject jobj;
					U.dout(0);
					jobj = api.getBillListData(stime,etime,appliescStoreid);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getOrderMonthList(jArr);
						myapp.setMyOrderHitList(list);
						msg.obj = list;
					}
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
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					orderView.setAdapter(getAdapter(dlist));
					pb.setVisibility(View.GONE);
					orderView.setVisibility(View.VISIBLE);
				}
				else
				{
					pb.setVisibility(View.GONE);
					TextView textOrderNull = (TextView)findViewById(R.id.textOrderNull);
					textOrderNull.setVisibility(View.VISIBLE);
				}
				break;
			case 1:
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					orderView.setAdapter(getAdapter2(dlist));
					pb.setVisibility(View.GONE);
					orderView.setVisibility(View.VISIBLE);
				}
				else
				{
					pb.setVisibility(View.GONE);
					TextView textOrderNull = (TextView)findViewById(R.id.textOrderNull);
					textOrderNull.setVisibility(View.VISIBLE);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public List<Map<String,Object>> getOrderItem(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String orderid = ""; 
				if(isValueNull("orderid",dobj))
					orderid = (String) dobj.get("orderid"); 
				
				String orderNo = "";
				if(isValueNull("orderNo",dobj))
					orderNo = (String) dobj.get("orderNo"); 
				
				
				String ostatus = ""; 
				if(isValueNull("ostatus",dobj))
					ostatus = (String) dobj.get("ostatus"); 
				
				String ostatusid = ""; 
				if(isValueNull("ostatusid",dobj))
					ostatusid = (String) dobj.get("ostatusid"); 
				
				String totalPrice = ""; 
				if(isValueNull("totalPrice",dobj))
					totalPrice = (String) dobj.get("totalPrice"); 
				
				String sendStatusid = ""; 
				if(isValueNull("sendStatusid",dobj))
					sendStatusid = (String) dobj.get("sendStatusid"); 
				
				String sendTime = ""; 
				if(isValueNull("sendTime",dobj))
					sendTime = (String) dobj.get("sendTime"); 
				
				String sendstatus = ""; 
				if(isValueNull("sendstatus",dobj))
					sendstatus = (String) dobj.get("sendstatus"); 
				
				String receiName = ""; 
				if(isValueNull("receiName",dobj))
					receiName = (String) dobj.get("receiName");  
				
				String phone = ""; 
				if(isValueNull("phone",dobj))
					phone = (String) dobj.get("phone"); 
				
				
				String address = ""; 
				if(isValueNull("address",dobj))
					address = (String) dobj.get("address"); 
				
				
				String opinion = ""; 
				if(isValueNull("opinion",dobj))
					opinion = (String) dobj.get("opinion"); 
				
				
				String logisticsNo = ""; 
				if(isValueNull("logisticsNo",dobj))
					logisticsNo = (String) dobj.get("logisticsNo"); 
				
				String logisticsComp = ""; 
				if(isValueNull("logisticsComp",dobj))
					logisticsComp = (String) dobj.get("logisticsComp"); 
				
				String orderTime = ""; 
				if(isValueNull("orderTime",dobj))
					orderTime = (String) dobj.get("orderTime"); 
				
				String logisticsName = ""; 
				if(isValueNull("logisticsName",dobj))
					logisticsName = (String) dobj.get("logisticsName"); 
				
				String logisticspice = ""; 
				if(isValueNull("logisticspice",dobj))
					logisticspice = (String) dobj.get("logisticspice"); 
				
				String payment = ""; 
				if(isValueNull("payment",dobj))
					payment = (String) dobj.get("payment"); 
				
				String paymentvalue = ""; 
				if(isValueNull("paymentvalue",dobj))
					paymentvalue = (String) dobj.get("paymentvalue"); 
				
				String zipCode = ""; 
				if(isValueNull("zipCode",dobj))
					zipCode = (String) dobj.get("zipCode"); 
				
				String invoicePayable = ""; 
				if(isValueNull("invoicePayable",dobj))
					invoicePayable = (String) dobj.get("invoicePayable"); 
				
				String invoiceType = ""; 
				if(isValueNull("invoiceType",dobj))
					invoiceType = (String) dobj.get("invoiceType"); 
				
				String requirements = ""; 
				if(isValueNull("requirements",dobj))
					requirements = (String) dobj.get("requirements"); 
				
				String desc = ""; 
				if(isValueNull("desc",dobj))
					desc = (String) dobj.get("desc"); 
				
				String preferential = ""; 
				if(isValueNull("preferential",dobj))
					preferential = (String) dobj.get("preferential"); 
				
				String freight = ""; 
				if(isValueNull("freight",dobj))
					freight = (String) dobj.get("freight"); 
				
				String paid = ""; 
				if(isValueNull("paid",dobj))
					paid = (String) dobj.get("paid"); 
				
				String integral = ""; 
				if(isValueNull("integral",dobj))
					integral = (String) dobj.get("integral");
				
				String delivery = ""; 
				if(isValueNull("delivery",dobj))
					delivery = (String) dobj.get("delivery");
				
				String orderPointsType = ""; 
				if(isValueNull("orderPointsType",dobj))
					orderPointsType = (String) dobj.get("orderPointsType");
				
				String paycode = ""; 
				if(isValueNull("paycode",dobj))
					paycode = (String) dobj.get("paycode");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderid", orderid);
				map.put("orderNo", orderNo);
				map.put("ostatus", ostatus);
				map.put("ostatusid", ostatusid);
				map.put("totalPrice",totalPrice);
				map.put("sendStatusid",sendStatusid);
				map.put("sendTime",sendTime);
				map.put("sendstatus",sendstatus);
				map.put("receiName", receiName);
				map.put("phone", phone);
				map.put("address", address);
				map.put("opinion", opinion);
				map.put("logisticsNo", logisticsNo);
				map.put("logisticsComp", logisticsComp);
				if(sendstatus != null && !sendstatus.equals(""))
					map.put("orderStatus", sendstatus);
				else
					map.put("orderStatus", ostatus);
				map.put("orderTime",orderTime);
				map.put("logisticsName", logisticsName);
				map.put("logisticspice", logisticspice);
				map.put("payment", payment);
				map.put("paymentvalue", paymentvalue);
				map.put("zipCode", zipCode);
				map.put("invoicePayable", invoicePayable);
				map.put("invoiceType", invoiceType);
				map.put("requirements", requirements);
				map.put("desc", desc);
				map.put("preferential", preferential);
				map.put("freight", freight);
				map.put("paid", paid);
				map.put("integral", integral);
				map.put("delivery", delivery);
				map.put("orderPointsType", orderPointsType);
				map.put("paycode", paycode);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getOrderMonthList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String orderNo = ""; 
				if(dobj.has("orderNo"))
					orderNo = (String) dobj.get("orderNo"); 
				
				String orderhid = ""; 
				if(dobj.has("orderhid"))
					orderhid = (String) dobj.get("orderhid");
				
				String totalPrices = ""; 
				if(dobj.has("totalPrices"))
					totalPrices = (String) dobj.get("totalPrices");
				
				String storeName = ""; 
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String receviceTime = ""; 
				if(dobj.has("receviceTime"))
					receviceTime = (String) dobj.get("receviceTime"); 
				
				String receiName = ""; 
				if(isValueNull("receiName",dobj))
					receiName = (String) dobj.get("receiName");  
				
				String phone = ""; 
				if(isValueNull("phone",dobj))
					phone = (String) dobj.get("phone"); 
				
				
				String address = ""; 
				if(isValueNull("address",dobj))
					address = (String) dobj.get("address"); 
				
				String logisticsNo = ""; 
				if(isValueNull("logisticsNo",dobj))
					logisticsNo = (String) dobj.get("logisticsNo"); 
				
				String logisticsComp = ""; 
				if(isValueNull("logisticsComp",dobj))
					logisticsComp = (String) dobj.get("logisticsComp"); 
				
				String isDiscuss = "1";  //0是是，1是否没有发表过评论
				if(isValueNull("isDiscuss",dobj))
					isDiscuss = (String) dobj.get("isDiscuss"); 
				
				String payment = ""; 
				if(isValueNull("payment",dobj))
					payment = (String) dobj.get("payment"); 
				
				String orderTime = ""; 
				if(isValueNull("orderTime",dobj))
					orderTime = (String) dobj.get("orderTime"); 
				
				String orderStatus = ""; 
				if(isValueNull("orderStatus",dobj))
					orderStatus = (String) dobj.get("orderStatus");
				
				String paymentvalue = ""; 
				if(isValueNull("paymentvalue",dobj))
					paymentvalue = (String) dobj.get("paymentvalue"); 
				
				String sendTime = ""; 
				if(isValueNull("sendTime",dobj))
					sendTime = (String) dobj.get("sendTime"); 
				
				String oldOrderStatus = ""; 
				if(isValueNull("oldOrderStatus",dobj))
					oldOrderStatus = (String) dobj.get("oldOrderStatus"); 
				
				String opinion = ""; 
				if(isValueNull("opinion",dobj))
					opinion = (String) dobj.get("opinion");
				
				String sendstatus = ""; 
				if(isValueNull("opinion",dobj))
					opinion = (String) dobj.get("sendstatus");
				
				String zipCode = ""; 
				if(isValueNull("zipCode",dobj))
					zipCode = (String) dobj.get("zipCode");
				
				String invoicePayable = ""; 
				if(isValueNull("invoicePayable",dobj))
					invoicePayable = (String) dobj.get("invoicePayable");
				
				String invoiceType = ""; 
				if(isValueNull("invoiceType",dobj))
					invoiceType = (String) dobj.get("invoiceType");
				
				String requirements = ""; 
				if(isValueNull("requirements",dobj))
					requirements = (String) dobj.get("requirements");
				
				String desc = ""; 
				if(isValueNull("desc",dobj))
					desc = (String) dobj.get("desc");
				
				String preferential = ""; 
				if(isValueNull("preferential",dobj))
					preferential = (String) dobj.get("preferential");
				
				String freight = ""; 
				if(isValueNull("freight",dobj))
					freight = (String) dobj.get("freight");
				
				String paid = ""; 
				if(isValueNull("paid",dobj))
					paid = (String) dobj.get("paid");
				
				String integral = ""; 
				if(isValueNull("integral",dobj))
					integral = (String) dobj.get("integral");
				
				String delivery = ""; 
				if(isValueNull("delivery",dobj))
					delivery = (String) dobj.get("delivery");
				
				String orderPointsType = ""; 
				if(isValueNull("orderPointsType",dobj))
					orderPointsType = (String) dobj.get("orderPointsType");
				
				String paycode = ""; 
				if(isValueNull("paycode",dobj))
					paycode = (String) dobj.get("paycode");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderNo", orderNo);
				map.put("orderid", orderhid);
				map.put("totalPrice", totalPrices);
				map.put("storeName", storeName);
				map.put("receviceTime", receviceTime);
				
				map.put("receiName", receiName);
				map.put("phone", phone);
				map.put("address", address);
				map.put("logisticsNo", logisticsNo);
				map.put("logisticsComp", logisticsComp);
				map.put("isDiscuss", isDiscuss);
				map.put("payment", payment);
				map.put("orderTime", orderTime);
				map.put("orderStatus", orderStatus);
				map.put("paymentvalue", paymentvalue);
				map.put("ostatusid", oldOrderStatus);
				map.put("sendTime",sendTime);
				map.put("opinion", opinion);
				map.put("sendstatus", sendstatus);
				map.put("zipCode", zipCode);
				map.put("invoicePayable", invoicePayable);
				map.put("invoiceType", invoiceType);
				map.put("requirements", requirements);
				map.put("desc", desc);
				map.put("preferential", preferential);
				map.put("freight", freight);
				map.put("paid", paid);
				map.put("integral", integral);
				map.put("delivery", delivery);
				map.put("orderPointsType", orderPointsType);
				map.put("paycode", paycode);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public boolean isValueNull(String str,JSONObject dobj)
	{
		boolean b = true;
		try{
			if(dobj.has(str))
			{
				Object obj = dobj.get(str);
				if(obj.equals("null"))
					b = false;
			}
			else
			{
				b = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return b;
	}

}
