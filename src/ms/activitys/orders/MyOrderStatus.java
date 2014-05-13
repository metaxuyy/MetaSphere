package ms.activitys.orders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
//import com.google.android.maps.MapView;
import ms.activitys.R;

//本类准备弃用
public class MyOrderStatus extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	AlertDialog menuDialog;// menu菜单Dialog

	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
    
//    MapView mapView;
    
    protected Menu myMenu;
    
    private ViewFlipper mViewFlipper; 
	
	private String appliescStoreid;
	
	private String pagetag;
	
	private ProgressBar pb;
	
	private ListView orderView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_status_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = MyOrderStatus.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		Bundle bunde = this.getIntent().getExtras();
		appliescStoreid = bunde.getString("appliescStoreid");
		
//		getGPSLocation();
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		showOrderItems();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("orderlist"))
				{
					MyOrderStatus.this.setResult(RESULT_OK, getIntent());
					MyOrderStatus.this.finish();
				}
				else if(pagetag.equals("orderDetails"))
				{
					pagetag = "orderlist";
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
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
	
	private SimpleAdapter getAdapter(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.order_status_item, new String[] { "orderNo","orderStatus","totalPrice" },
				new int[] { R.id.order_no_all,R.id.order_status,R.id.total_price });
		return simperAdapter;
	}
	
	private SimpleAdapter getAdapter2(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.order_details_item, new String[] { "productName","productPrices","buyNumber","totalPrices" },
				new int[] { R.id.product_name,R.id.product_price,R.id.buy_number,R.id.total_prices });
		return simperAdapter;
	}
	
	private void showOrderItems()
	{
		pagetag = "orderlist";
		try{
			orderView = (ListView)findViewById(R.id.order_list);
			
			loadThreadData();
			
			orderView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
//					String sid = dlist.get(arg2).get("sid").toString();
					Map map = myapp.getMyOrderList().get(arg2);
					showOrderDetails(map);
				}
				
			});
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
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj = api.getOrderItem(appliescStoreid);
					JSONArray jArr = (JSONArray) jobj.get("data");
					list = getOrderItem(jArr);
					
					myapp.setMyOrderList(list);
				}catch(Exception ex){
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
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				orderView.setAdapter(getAdapter(dlist));
				pb.setVisibility(View.GONE);
				orderView.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	private void showOrderDetails(Map<String,Object> map)
	{
		pagetag = "orderDetails";
		try{
			final String orderid = (String)map.get("orderid");
			String orderNo = (String)map.get("orderNo");
			String ostatus = (String)map.get("ostatus");
			String ostatusid = (String)map.get("ostatusid");
			String totalPrice = (String)map.get("totalPrice");
			String sendStatusid = (String)map.get("sendStatusid");
			String sendTime = (String)map.get("sendTime");
			String sendstatus = (String)map.get("sendstatus");
			String receiName = (String)map.get("receiName");
			String phone = (String)map.get("phone");
			String address = (String)map.get("address");
			String opinion = (String)map.get("opinion");
			String logisticsNo = (String)map.get("logisticsNo");
			String logisticsComp = (String)map.get("logisticsComp");
			String orderStatus = (String)map.get("orderStatus");
			String orderTime = (String)map.get("orderTime");
			String logisticsName = (String)map.get("logisticsName");
			String logisticspice = (String)map.get("logisticspice");
			
			TextView ordernoText = (TextView)findViewById(R.id.order_nod);
			ordernoText.setText(orderNo);
			
			TextView orderstatusText = (TextView)findViewById(R.id.order_statusd);
			orderstatusText.setText(orderStatus);
			
			TextView totalpriceText = (TextView)findViewById(R.id.total_priced);
			totalpriceText.setText(totalPrice);
			
			TextView orderTimeText = (TextView)findViewById(R.id.order_time);
			orderTimeText.setText(orderTime);
			
			TextView log_name = (TextView)findViewById(R.id.log_name);
			log_name.setText(logisticsName);
			
			TextView log_price = (TextView)findViewById(R.id.log_price);
			log_price.setText(logisticspice + " ￥");
			
			if(ostatusid.equals("2")) 
			{
				LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout05);
				layoutview.setVisibility(View.VISIBLE);
				
				LinearLayout layoutview2 = (LinearLayout)findViewById(R.id.LinearLayout06);
				layoutview2.setVisibility(View.GONE);
				
				TextView opinion_text = (TextView)findViewById(R.id.opinion);
				opinion_text.setText(opinion);
			}
			else if(ostatusid.equals("3"))
			{
				LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout06);
				layoutview.setVisibility(View.VISIBLE);
				
				LinearLayout layoutview2 = (LinearLayout)findViewById(R.id.LinearLayout05);
				layoutview2.setVisibility(View.GONE);
				
				TextView receinameText = (TextView)findViewById(R.id.recei_name_true);
				receinameText.setText(receiName);
				
				TextView phonetext = (TextView)findViewById(R.id.phone_true);
				phonetext.setText(phone);
				
				TextView logisticsnoText = (TextView)findViewById(R.id.logistics_no_true);
				logisticsnoText.setText(logisticsNo);
				
				TextView logisticscompanyText = (TextView)findViewById(R.id.logistics_company_true);
				logisticscompanyText.setText(logisticsComp);
				
				TextView sendtimeText = (TextView)findViewById(R.id.send_timed);
				sendtimeText.setText(sendTime);
				
				TextView addressText = (TextView)findViewById(R.id.address_true);
				addressText.setText(address);
			}
			else
			{
				LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout06);
				layoutview.setVisibility(View.GONE);
				
				LinearLayout layoutview2 = (LinearLayout)findViewById(R.id.LinearLayout05);
				layoutview2.setVisibility(View.GONE);
			}
			
			LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout07);
			List<Map<String,Object>> dlist = getOrderDetailsItems(orderid);
			if(dlist != null && dlist.size()>0)
			{
				layoutview.removeAllViews();
				for(int i=0;i<dlist.size();i++)
				{
					Map<String,Object> maps = dlist.get(i);
					String productName = (String)maps.get("productName"); 
					String typeName = (String)maps.get("typeName");
					String productPrices = (String)maps.get("productPrices");
					String buyNumber = (String)maps.get("buyNumber");
					String productImg = (String)maps.get("productImg");
					String productColor = (String)maps.get("productColor");
					String totalPrices = (String)maps.get("totalPrices");
					
					View view = View.inflate(this, R.layout.order_detailed_list_item, null);
					
					TextView pname = (TextView) view.findViewById(R.id.product_name);
					pname.setText(productName);
					
					TextView prices = (TextView) view.findViewById(R.id.product_price);
					prices.setText(productPrices);
					
					TextView pnumber = (TextView) view.findViewById(R.id.product_number);
					pnumber.setText(buyNumber);
					
					TextView pcolor = (TextView) view.findViewById(R.id.product_color);
					if(productColor != null && !productColor.equals(""))
						pcolor.setText(productColor);
					else
						pcolor.setVisibility(View.GONE);
					
					TextView tprices = (TextView) view.findViewById(R.id.product_total_price);
					tprices.setText(totalPrices);
					
					layoutview.addView(view);
				}
			}
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pagetag = "orderlist";
					mViewFlipper.showPrevious();
				}
			});
			
			if(ostatusid.equals("3"))
			{
				Button cobtn = (Button)findViewById(R.id.cancel_order_btn);
				cobtn.setVisibility(View.GONE);
				
				Button cmbtn = (Button)findViewById(R.id.confirm_proudect_btn);
				if(sendStatusid != null && !sendStatusid.equals(""))
				{
					if(sendStatusid.equals("3"))
						cmbtn.setVisibility(View.GONE);
					else
						cmbtn.setVisibility(View.VISIBLE);
				}
				else
				{
					cmbtn.setVisibility(View.VISIBLE);
				}
				
				cmbtn.setText(this.getString(R.string.order_lable_1));
				
				cmbtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						confirmProudect(orderid);
					}
				});
			}
			else if(ostatusid.equals("4"))
			{
				Button cobtn = (Button)findViewById(R.id.cancel_order_btn);
				cobtn.setVisibility(View.GONE);
				
				Button cmbtn = (Button)findViewById(R.id.confirm_proudect_btn);
				cmbtn.setText(this.getString(R.string.order_lable_2));
				
				cmbtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							JSONObject jobj = api.getOrderStatusChange(orderid,"reduction",appliescStoreid);
							if(jobj != null)
							{
								makeText(MyOrderStatus.this.getString(R.string.order_lable_2));
								showOrderItems();
								pagetag = "orderlist";
								mViewFlipper.showPrevious();
							}
							else
							{
								makeText(MyOrderStatus.this.getString(R.string.order_lable_4));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			else
			{
				Button cobtn = (Button)findViewById(R.id.cancel_order_btn);
				cobtn.setVisibility(View.VISIBLE);
				
				cobtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							JSONObject jobj = api.getOrderStatusChange(orderid,"cancel",appliescStoreid);
							if(jobj != null)
							{
								makeText(MyOrderStatus.this.getString(R.string.order_lable_5));
								showOrderItems();
								pagetag = "orderlist";
								mViewFlipper.showPrevious();
							}
							else
							{
								makeText(MyOrderStatus.this.getString(R.string.order_lable_6));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				Button cmbtn = (Button)findViewById(R.id.confirm_proudect_btn);
				cmbtn.setVisibility(View.GONE);
			}
			
			mViewFlipper.showNext();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void confirmProudect(final String orderid)
	{
		try{
			View view = LayoutInflater.from(this).inflate(R.layout.confirm_proudect_popup, null);
			
			final Dialog myDialog = new Dialog(this, R.style.AliDialog);
			myDialog.setContentView(view);
			myDialog.show();
			
			Button conbtn = (Button)view.findViewById(R.id.confirm_btn);
			conbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						JSONObject jobj = api.getOrderStatusChange(orderid,"confirm",appliescStoreid);
						if(jobj != null)
						{
							myDialog.dismiss();
							makeText(MyOrderStatus.this.getString(R.string.order_lable_7));
							showOrderItems();
							pagetag = "orderlist";
							mViewFlipper.showPrevious();
						}
						else
						{
							makeText(MyOrderStatus.this.getString(R.string.order_lable_8));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			Button btn2 = (Button)view.findViewById(R.id.clear_btn);
			btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog.dismiss();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getOrderItems()
	{
		List<Map<String,Object>> list = null;
		try{
			JSONObject jobj = api.getOrderItem(appliescStoreid);
			JSONArray jArr = (JSONArray) jobj.get("data");
			list = getOrderItem(jArr);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getOrderDetailsItems(String orderid)
	{
		List<Map<String,Object>> list = null;
		try{
			JSONObject jobj = api.getOrderDetailsItem(orderid);
			JSONArray jArr = (JSONArray) jobj.get("data");
			list = getOrderDetailsItem(jArr);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
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
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderid", orderid);
				map.put("orderNo", orderNo);
				map.put("ostatus", ostatus);
				map.put("ostatusid", ostatusid);
				map.put("totalPrice",totalPrice + " ￥");
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
					map.put("orderStatus", ostatus + "," + sendstatus);
				else
					map.put("orderStatus", ostatus);
				map.put("orderTime",orderTime);
				map.put("logisticsName", logisticsName);
				map.put("logisticspice", logisticspice);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getOrderDetailsItem(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String productName = ""; 
				if(dobj.has("productName"))
					productName = (String) dobj.get("productName"); 
				
				String typeName = "";
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				
				String productPrices = ""; 
				if(dobj.has("productPrices"))
					productPrices = (String) dobj.get("productPrices"); 
				
				String totalPrices = ""; 
				if(dobj.has("totalPrices"))
					totalPrices = (String) dobj.get("totalPrices"); 
				
				String buyNumber = ""; 
				if(dobj.has("buyNumber"))
					buyNumber = (String) dobj.get("buyNumber"); 
				
				String productImg = ""; 
				if(dobj.has("productImg"))
					productImg = (String) dobj.get("productImg"); 
				
				String productColor = ""; 
				if(dobj.has("productColor"))
					productColor = (String) dobj.get("productColor"); 
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("productName", productName);
				map.put("typeName", typeName);
				map.put("productPrices", this.getString(R.string.mybill_list_lable_6)+productPrices + " ￥");
				map.put("totalPrices", this.getString(R.string.mybill_list_lable_7)+totalPrices + " ￥");
				map.put("buyNumber", this.getString(R.string.mybill_list_lable_8)+buyNumber);
				map.put("productImg", productImg);
				map.put("productColor", productColor);
				
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
