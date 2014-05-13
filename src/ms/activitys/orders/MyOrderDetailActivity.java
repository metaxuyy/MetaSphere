package ms.activitys.orders;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.product.AccountscenterActivity;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterOrder;
import ms.globalclass.map.MyApp;

public class MyOrderDetailActivity extends Activity {

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private int orderindex;
	private String orderId;
	private ProgressDialog mypDialog;
	private String payment;
	private int index;
	private String typesMapping;
	private String appliescStoreid;
	private String orderlisttag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorderdetailactivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		orderindex = this.getIntent().getExtras().getInt("orderindex");
		index = this.getIntent().getExtras().getInt("index");
		appliescStoreid = this.getIntent().getExtras().getString("appliescStoreid");
		orderlisttag = this.getIntent().getExtras().getString("orderlisttag");
		
		Button break_cart = (Button)findViewById(R.id.break_cart);
		break_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyOrderDetailActivity.this.setResult(RESULT_OK, getIntent());
				MyOrderDetailActivity.this.finish();
			}
		});
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		Map<String,Object> cardmap = list.get(index);
		typesMapping = (String)cardmap.get("typesMapping");
		
		showOrderDetail();
	}
	
	private void showOrderDetail()
	{
		showProgressDialog();
		try{
			DecimalFormat df = new DecimalFormat("######0.00");
			
			Map<String,Object> map = null;
			if(orderlisttag.equals("1"))
				map = myapp.getMyOrderList().get(orderindex);
			else if(orderlisttag.equals("2"))
				map = myapp.getMyOrderHitList().get(orderindex);
			String receiName = (String)map.get("receiName");
			orderId = (String)map.get("orderid");
			String phone = (String)map.get("phone");
			String address = (String)map.get("address");
			String zipCode = (String)map.get("zipCode");
			String orderNo = (String)map.get("orderNo");
			String orderStatus = (String)map.get("orderStatus");
			String paymentvalue = (String)map.get("paymentvalue");
			String orderTime = (String)map.get("orderTime");
			String sendTime = (String)map.get("sendTime");
			String invoicePayable = (String)map.get("invoicePayable");
			String requirements = (String)map.get("requirements");
			String desc = (String)map.get("desc");
			payment = (String)map.get("payment");
			String totalPrice = (String)map.get("totalPrice");
			String preferential = (String)map.get("preferential");
			String freight = (String)map.get("freight");
			String paid = (String)map.get("paid");
			String integral = (String)map.get("integral");
			String ostatusid = (String)map.get("ostatusid");
			String opinion = (String)map.get("opinion");
			String logisticsNo = (String)map.get("logisticsNo");
			String logisticsComp = (String)map.get("logisticsComp");
			String delivery = (String)map.get("delivery");
			String orderPointsType = (String)map.get("orderPointsType");
			String paycode = (String)map.get("paycode");
			
			TextView textAdress1 = (TextView)findViewById(R.id.textAdress1);
			textAdress1.setText(receiName);
			
			TextView textAdress2 = (TextView)findViewById(R.id.textAdress2);
			textAdress2.setText(phone);
			
			TextView textAdress3 = (TextView)findViewById(R.id.textAdress3);
			textAdress3.setText(address);
			
			TextView textAdress4 = (TextView)findViewById(R.id.textAdress4);
			textAdress4.setText(zipCode);
			
			TextView textDetail1 = (TextView)findViewById(R.id.textDetail1);
			textDetail1.setText(orderNo);
			
			TextView textDetail2 = (TextView)findViewById(R.id.textDetail2);
			textDetail2.setText(orderStatus);
			
			TextView textDetail3 = (TextView)findViewById(R.id.textDetail3);
			textDetail3.setText(paymentvalue);
			
			TextView textDetail4 = (TextView)findViewById(R.id.textDetail4);
			textDetail4.setText(orderTime);
			
			TextView textDetail5 = (TextView)findViewById(R.id.textDetail5);
			textDetail5.setText(sendTime);
			
			if(invoicePayable != null && !invoicePayable.equals(""))
			{
				TextView textDetail6 = (TextView)findViewById(R.id.textDetail6);
				textDetail6.setText("YES");
				
				TextView textDetail7 = (TextView)findViewById(R.id.textDetail7);
				textDetail7.setText(invoicePayable);
			}
			else
			{
				TextView textDetail6 = (TextView)findViewById(R.id.textDetail6);
				textDetail6.setText("NO");
			}
			
			TextView textDetail8 = (TextView)findViewById(R.id.textDetail8);
			textDetail8.setText(requirements);
			
			TextView textDetail9 = (TextView)findViewById(R.id.textDetail9);
			textDetail9.setText(desc);
			
			double cope = 0;
			cope = cope + Double.valueOf(totalPrice);
			double productTotal = cope - Double.valueOf(freight)+Double.valueOf(preferential);
			TextView textPrice1 = (TextView)findViewById(R.id.textPrice1);
			textPrice1.setText(productTotal + "￥");
			
			TextView textPrice2 = (TextView)findViewById(R.id.textPrice2);
			textPrice2.setText(preferential + "￥");
//			cope = cope - Double.valueOf(preferential);
			
			TextView textPrice3 = (TextView)findViewById(R.id.textPrice3);
			textPrice3.setText(freight + "￥");
			
			TextView textPrice4 = (TextView)findViewById(R.id.textPrice4);
			textPrice4.setText(paid + "￥");
//			cope = cope - Double.valueOf(paid);
			
			TextView textPriceLable5 = (TextView)findViewById(R.id.textPriceLable5);
			if(orderPointsType.equals("1"))
			{
				textPriceLable5.setText(getString(R.string.menu_lable_66));
			}
			else if(orderPointsType.equals("2"))
			{
				textPriceLable5.setText(getString(R.string.menu_lable_145));
			}
			else if(orderPointsType.equals("3"))
			{
				textPriceLable5.setText(getString(R.string.menu_lable_146));
			}
			
			double integ = Double.valueOf(integral);
			TextView textPrice5 = (TextView)findViewById(R.id.textPrice5);
			textPrice5.setText((int)integ + this.getString(R.string.menu_lable_43));
			
			TextView textPrice6 = (TextView)findViewById(R.id.textPrice6);
			textPrice6.setText(df.format(cope) + "￥");
			
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
				
				TextView logisticsnoText = (TextView)findViewById(R.id.logistics_no_true);
				logisticsnoText.setText(logisticsNo);
				
				TextView logisticscompanyText = (TextView)findViewById(R.id.logistics_company_true);
				logisticscompanyText.setText(logisticsComp);
				
				TextView deliverytxt = (TextView)findViewById(R.id.delivery);
				deliverytxt.setText(delivery);
			}
			
			if(orderlisttag.equals("1"))
			{
				Button zifubtn = (Button)findViewById(R.id.zifu_btn);
				if(payment.equals("3"))
				{
					if(paycode.equals(""))
					{
						zifubtn.setVisibility(View.VISIBLE);
						zifubtn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				}
				
				Button confirm_proudect_btn = (Button)findViewById(R.id.confirm_proudect_btn);
				if(payment.equals("1"))
				{
					if(paycode.equals(""))
					{
						confirm_proudect_btn.setVisibility(View.VISIBLE);
						confirm_proudect_btn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								showProgressDialog();
								querenOrder();
							}
						});
					}
				}
				
				Button cancel_order_btn = (Button)findViewById(R.id.cancel_order_btn);
				cancel_order_btn.setVisibility(View.VISIBLE);
				cancel_order_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showProgressDialog2(MyOrderDetailActivity.this.getString(R.string.menu_lable_150));
						cancelOrder();
					}
				});
			}
				
			if(orderlisttag.equals("1"))
				loadOrderDetialList();
			else if(orderlisttag.equals("2"))
				loadOrderDetialList2();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void cancelOrder()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try{
					JSONObject jobj = api.getOrderStatusChange(orderId,"cancel",appliescStoreid);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						msg.obj = "true";
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void querenOrder()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try{
					JSONObject jobj = api.getConfirmOrder(orderId);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						msg.obj = "true";
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadOrderDetialList()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				try{
					JSONObject jobj = api.getOrderDetailsItem(orderId);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getOrderDetailsItem(jArr);
						msg.obj = list;
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadOrderDetialList2()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				try{
					JSONObject jobj = api.getOrderHistoryDetailsItem(orderId);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getOrderDetailsItem(jArr);
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
					List<Map<String,Object>> list = (List<Map<String,Object>>)msg.obj;
					LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout07);
					layoutview.removeAllViews();
					for(int i=0;i<list.size();i++)
					{
						View view = View.inflate(MyOrderDetailActivity.this, R.layout.myorderdetailitems, null);
						Map<String,Object> map = list.get(i);
						String productid = (String)map.get("productid");
						String productCode = (String)map.get("productCode");
						String productName = (String)map.get("productName");
						String productColor = (String)map.get("productColor");
						String productSize = (String)map.get("productSize");
						String buyNumber = (String)map.get("buyNumber");
						String productPrices = (String)map.get("productPrices");
						String desc = (String)map.get("desc");
						
						TextView textProductcode = (TextView)view.findViewById(R.id.textProductcode);
						textProductcode.setText(productCode);
						
						TextView textName = (TextView)view.findViewById(R.id.textName);
						textName.setText(productName);
						
						if(typesMapping.equals("10"))
						{
							TextView textUnitIcon = (TextView)view.findViewById(R.id.textUnitIcon);
							textUnitIcon.setText(MyOrderDetailActivity.this.getString(R.string.menu_lable_138));
							
							RelativeLayout rldients = (RelativeLayout)view.findViewById(R.id.rldients);
							rldients.setVisibility(View.VISIBLE);
							
							TextView textDients = (TextView)view.findViewById(R.id.textDients);
							textDients.setText(MyOrderDetailActivity.this.getString(R.string.produce_with4)+": "+desc);
						}
						
						TextView textUnit = (TextView)view.findViewById(R.id.textUnit);
						textUnit.setText(productColor);
						
						TextView textSize = (TextView)view.findViewById(R.id.textSize);
						textSize.setText(productSize);
						
						TextView textAumount = (TextView)view.findViewById(R.id.textAumount);
						textAumount.setText(buyNumber);
						
						TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
						textPrice.setText(productPrices);
						
						layoutview.addView(view);
						
						ImageView lineimg = new ImageView(MyOrderDetailActivity.this);
						lineimg.setBackgroundResource(R.drawable.main_line);
						lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
						layoutview.addView(lineimg);
					}
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 1:
				if(msg.obj != null)
				{
					myapp.getMyOrderList().remove(orderindex);
					MyOrederListActivity.simperAdapter.delMoreData(myapp.getMyOrderList());
					MyOrederListActivity.simperAdapter.notifyDataSetChanged();
					makeText(MyOrderDetailActivity.this.getString(R.string.menu_lable_152));
					MyOrderDetailActivity.this.setResult(RESULT_OK, getIntent());
					MyOrderDetailActivity.this.finish();
				}
				else
				{
					makeText(MyOrderDetailActivity.this.getString(R.string.menu_lable_151));
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 2:
				if(msg.obj != null)
				{
					myapp.getMyOrderList().remove(orderindex);
					MyOrederListActivity.simperAdapter.delMoreData(myapp.getMyOrderList());
					MyOrederListActivity.simperAdapter.notifyDataSetChanged();
					makeText("该订单以完成");
					MyOrderDetailActivity.this.setResult(RESULT_OK, getIntent());
					MyOrderDetailActivity.this.finish();
				}
				else
				{
					makeText("订单确认失败");
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
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
				
				String productCode = ""; 
				if(dobj.has("productCode"))
					productCode = (String) dobj.get("productCode"); 
				
				String productSize = ""; 
				if(dobj.has("productSize"))
					productSize = (String) dobj.get("productSize"); 
				
				String productid = ""; 
				if(dobj.has("productid"))
					productid = (String) dobj.get("productid"); 
				
				String desc = ""; 
				if(dobj.has("desc"))
					desc = (String) dobj.get("desc"); 
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("productName", productName);
				map.put("typeName", typeName);
				map.put("productPrices", productPrices + " ￥");
				map.put("totalPrices", totalPrices + " ￥");
				map.put("buyNumber", buyNumber);
				map.put("productImg", productImg);
				map.put("productColor", productColor);
				map.put("productCode", productCode);
				map.put("productSize", productSize);
				map.put("productid", productid);
				map.put("desc", desc);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void showProgressDialog(){
 		try{
 			mypDialog=new ProgressDialog(this);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(this.getString(R.string.map_lable_11));
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
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
	
	public void showProgressDialog2(String str){
 		try{
 			mypDialog=new ProgressDialog(this);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(str);
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
