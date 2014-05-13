package ms.activitys.product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettlementActivity extends Activity{
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String activitytype;
	private String [] invoices = new String[6];
	private int invoiceindex;
	private String storeid;
	private ListView slistView;
	private String logid;
	private ProgressDialog mypDialog;
	private List<Map<String,Object>> loglist = new ArrayList<Map<String,Object>>();
	private int cpoints;
	private int points;
	private double ratenum;
	private int cardpoints;
	private List<Map<String,Object>> paylist = new ArrayList<Map<String,Object>>();
	private ListView plistView;
	private String paytag = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settlementactivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		activitytype = this.getIntent().getExtras().getString("activitytype");
		
		invoices[0] = getString(R.string.menu_lable_103);
		invoices[1] = getString(R.string.menu_lable_104);
		invoices[2] = getString(R.string.menu_lable_105);
		invoices[3] = getString(R.string.menu_lable_106);
		invoices[4] = getString(R.string.menu_lable_107);
		invoices[5] = getString(R.string.menu_lable_108);
		
		Button breakbtn = (Button)findViewById(R.id.break_cart);
		breakbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("activitytype", "activitytype");
				SettlementActivity.this.setResult(RESULT_OK, intent);
//				SettlementActivity.this.setResult(RESULT_OK, getIntent());
				SettlementActivity.this.finish();
			}
		});
		
		
		TextView title = (TextView)findViewById(R.id.TextView01);
		if(activitytype.equals("pay"))
		{
			title.setText(getString(R.string.menu_lable_58));
			storeid = this.getIntent().getExtras().getString("storeid");
			paytag = this.getIntent().getExtras().getString("paytag");
			showPay();
		}
		else if(activitytype.equals("sendtime"))
		{
			title.setText(getString(R.string.menu_lable_59));
			String tag = this.getIntent().getExtras().getString("sendtimetag");
			showSendTime(tag);
		}
		else if(activitytype.equals("invoice"))
		{
			title.setText(getString(R.string.menu_lable_102));
			String ititle = this.getIntent().getExtras().getString("title");
			String contentindex = this.getIntent().getExtras().getString("contentindex");
			showInvoice(ititle,contentindex);
		}
		else if(activitytype.equals("comment"))
		{
			title.setText(getString(R.string.hotel_reservation_lable_20));
			String content = this.getIntent().getExtras().getString("content");
			showComment(content);
		}
		else if(activitytype.equals("logistics"))
		{
			title.setText(getString(R.string.menu_lable_9));
			storeid = this.getIntent().getExtras().getString("storeid");
			logid = this.getIntent().getExtras().getString("logid");
			showLogistics();
		}
		else if(activitytype.equals("redeemPoints"))
		{
			title.setText(getString(R.string.menu_lable_139));
			storeid = this.getIntent().getExtras().getString("storeid");
			cpoints = this.getIntent().getExtras().getInt("cpoints");
			points = this.getIntent().getExtras().getInt("points");
			cardpoints = this.getIntent().getExtras().getInt("cardpoints");
			ratenum = this.getIntent().getExtras().getDouble("ratenum");
			showRedeemPoints();
		}
	}
	
	public void loadPayLayout()
	{
		showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				try{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					JSONObject jobj = api.getStorePayList(storeid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getStorePayList(jArr);
							msg.obj = list;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void showPay()
	{
		try{
			LinearLayout paylayout = (LinearLayout)findViewById(R.id.linOne);
			paylayout.setVisibility(View.VISIBLE);
			
			loadPayLayout();
			
			plistView = (ListView)findViewById(R.id.listpay);
			
			plistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
						Map<String,Object> map = paylist.get(arg2);
						String mappingtype = (String)map.get("mappingtype");
						String payname = (String)map.get("payname");
						
						Intent intent = new Intent();
						intent.putExtra("activitytype", activitytype);
						intent.putExtra("value", payname);
						intent.putExtra("paytag", mappingtype);
						SettlementActivity.this.setResult(RESULT_OK, intent);
						SettlementActivity.this.finish();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showSendTime(final String tag)
	{
		try{
			LinearLayout sendtimelayout = (LinearLayout)findViewById(R.id.linSendTime);
			sendtimelayout.setVisibility(View.VISIBLE);
			
			final ImageView img1 = (ImageView)findViewById(R.id.imgSendTime1);
			final ImageView img2 = (ImageView)findViewById(R.id.imgSendTime2);
			final ImageView img3 = (ImageView)findViewById(R.id.imgSendTime3);
			final ImageView img4 = (ImageView)findViewById(R.id.imgSendTime4);
			
			RelativeLayout relSendTime1 = (RelativeLayout)findViewById(R.id.relSendTime1);
			final TextView textSendTime1 = (TextView)findViewById(R.id.textSendTime1);
			relSendTime1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img1.setVisibility(View.VISIBLE);
					img2.setVisibility(View.GONE);
					img3.setVisibility(View.GONE);
					img4.setVisibility(View.GONE);
					Intent intent = new Intent();
					intent.putExtra("activitytype", activitytype);
					intent.putExtra("value", textSendTime1.getText().toString());
					intent.putExtra("sendtimetag", "1");
					SettlementActivity.this.setResult(RESULT_OK, intent);
					SettlementActivity.this.finish();
				}
			});
			if(tag.equals("1"))
				img1.setVisibility(View.VISIBLE);
			
			RelativeLayout relSendTime2 = (RelativeLayout)findViewById(R.id.relSendTime2);
			final TextView textSendTime2 = (TextView)findViewById(R.id.textSendTime2);
			relSendTime2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img1.setVisibility(View.GONE);
					img2.setVisibility(View.VISIBLE);
					img3.setVisibility(View.GONE);
					img4.setVisibility(View.GONE);
					Intent intent = new Intent();
					intent.putExtra("activitytype", activitytype);
					intent.putExtra("value", textSendTime2.getText().toString());
					intent.putExtra("sendtimetag", "2");
					SettlementActivity.this.setResult(RESULT_OK, intent);
					SettlementActivity.this.finish();
				}
			});
			if(tag.equals("2"))
				img2.setVisibility(View.VISIBLE);
			
			RelativeLayout relSendTime3 = (RelativeLayout)findViewById(R.id.relSendTime3);
			final TextView textSendTime3 = (TextView)findViewById(R.id.textSendTime3);
			relSendTime3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img1.setVisibility(View.GONE);
					img2.setVisibility(View.GONE);
					img3.setVisibility(View.VISIBLE);
					img4.setVisibility(View.GONE);
					Intent intent = new Intent();
					intent.putExtra("activitytype", activitytype);
					intent.putExtra("value", textSendTime3.getText().toString());
					intent.putExtra("sendtimetag", "3");
					SettlementActivity.this.setResult(RESULT_OK, intent);
					SettlementActivity.this.finish();
				}
			});
			if(tag.equals("3"))
				img3.setVisibility(View.VISIBLE);
			
			RelativeLayout relSendTime4 = (RelativeLayout)findViewById(R.id.relSendTime4);
			final TextView textSendTime4 = (TextView)findViewById(R.id.textSendTime4);
			relSendTime4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img1.setVisibility(View.GONE);
					img2.setVisibility(View.GONE);
					img3.setVisibility(View.GONE);
					img4.setVisibility(View.VISIBLE);
					Intent intent = new Intent();
					intent.putExtra("activitytype", activitytype);
					intent.putExtra("value", textSendTime4.getText().toString());
					intent.putExtra("sendtimetag", "4");
					SettlementActivity.this.setResult(RESULT_OK, intent);
					SettlementActivity.this.finish();
				}
			});
			if(tag.equals("4"))
				img4.setVisibility(View.VISIBLE);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void showInvoice(String ititle,String contentindex)
	{
		try{
			LinearLayout invoicelin = (LinearLayout)findViewById(R.id.invoicelin);
			invoicelin.setVisibility(View.VISIBLE);
			
			Button savebtn  = (Button)findViewById(R.id.save_btn);
			savebtn.setVisibility(View.VISIBLE);
			final EditText editContent = (EditText)findViewById(R.id.editContent);
			if(ititle != null)
			{
				editContent.setText(ititle);
			}
			final TextView textContent = (TextView)findViewById(R.id.textContent);
			if(contentindex != null && !contentindex.equals(""))
			{
				int index = Integer.valueOf(contentindex);
				invoiceindex = index;
				textContent.setText(invoices[invoiceindex]);
			}
			else
			{
				invoiceindex = 0;
				textContent.setText(invoices[invoiceindex]);
			}
			LinearLayout itypeinl = (LinearLayout)findViewById(R.id.itypeinl);
			itypeinl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showInvoiceDialog();
				}
			});
			
			savebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String ititle = editContent.getText().toString();
					String icontent = textContent.getText().toString();
					boolean a = true;
//					if(ititle == null || ititle.equals(""))
//					{
//						makeText("请填写发票抬头");
//						a = false;
//					}
//					else if(icontent == null || icontent.equals(""))
//					{
//						makeText("请选择发票类型");
//						a = false;
//					}
					
					if(a)
					{
						Intent intent = new Intent();
						intent.putExtra("activitytype", activitytype);
						intent.putExtra("ititle", ititle);
						intent.putExtra("icontent", icontent);
						intent.putExtra("invoiceindex", String.valueOf(invoiceindex));
						SettlementActivity.this.setResult(RESULT_OK, intent);
						SettlementActivity.this.finish();
					}
				}
			});
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void showComment(String content)
	{
		try{
			LinearLayout relComment = (LinearLayout)findViewById(R.id.relComment);
			relComment.setVisibility(View.VISIBLE);
			
			Button savebtn  = (Button)findViewById(R.id.save_btn);
			savebtn.setVisibility(View.VISIBLE);
			final EditText editComment = (EditText)findViewById(R.id.editComment);
			if(content != null && !content.equals(""))
			{
				editComment.setText(content);
			}
			
			savebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String content = editComment.getText().toString();
					boolean a = true;
					if(content.length() > 50)
					{
						makeText(getString(R.string.menu_lable_101));
						a = false;
					}
					
					if(a)
					{
						Intent intent = new Intent();
						intent.putExtra("activitytype", activitytype);
						intent.putExtra("content", content);
						SettlementActivity.this.setResult(RESULT_OK, intent);
						SettlementActivity.this.finish();
					}
				}
			});
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showLogistics()
	{
		try{
			LinearLayout relLogistics = (LinearLayout)findViewById(R.id.relLogistics);
			relLogistics.setVisibility(View.VISIBLE);
			
			slistView = (ListView)findViewById(R.id.listLogistics);
			
			loadLogisticsList();
			
			slistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
						Map<String,Object> map = loglist.get(arg2);
						String textName = (String)map.get("textName");
						String pkid = (String)map.get("pkid");
						String lpice = (String)map.get("lpice");
						Intent intent = new Intent();
						intent.putExtra("activitytype", activitytype);
						intent.putExtra("textName", textName);
						intent.putExtra("pkid", pkid);
						intent.putExtra("lpice", lpice);
						SettlementActivity.this.setResult(RESULT_OK, intent);
						SettlementActivity.this.finish();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showRedeemPoints()
	{
		try{
			LinearLayout pointslin = (LinearLayout)findViewById(R.id.pointslin);
			pointslin.setVisibility(View.VISIBLE);
			
			Button savebtn  = (Button)findViewById(R.id.save_btn);
			savebtn.setVisibility(View.VISIBLE);
			final EditText editPoints = (EditText)findViewById(R.id.editPoints);
			if(cpoints > 0)
				editPoints.setText(String.valueOf(cpoints));
			
			TextView currt_points = (TextView)findViewById(R.id.currt_points);
			currt_points.setText(String.valueOf(cardpoints));
			TextView max_points = (TextView)findViewById(R.id.max_points);
			max_points.setText(String.valueOf(points));
			
			savebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String content = editPoints.getText().toString();
					boolean a = true;
					if(!content.equals(""))
					{
						if(Integer.valueOf(content) > points)
						{
							makeText(getString(R.string.menu_lable_143));
							a = false;
						}
					}
					
					if(a)
					{
						Intent intent = new Intent();
						intent.putExtra("activitytype", activitytype);
						intent.putExtra("points", content);
						intent.putExtra("cardpoints", cardpoints);
						intent.putExtra("maxpoints", points);
						intent.putExtra("ratenum",String.valueOf(ratenum));
						SettlementActivity.this.setResult(RESULT_OK, intent);
						SettlementActivity.this.finish();
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadLogisticsList()
	{
		showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				try{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					JSONObject jobj = api.getLogisticsList(storeid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getLogisticsList(jArr);
							msg.obj = list;
						}
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
					loglist.addAll(dlist);
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(SettlementActivity.this, dlist,// 数据源
							R.layout.logisticsitem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] {"textName","imgOk" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textName,R.id.imgOk },share,"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					slistView.setAdapter(listItemAdapter);
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 1:
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					paylist.addAll(dlist);
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(SettlementActivity.this, dlist,// 数据源
							R.layout.pay_item,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] {"payname","imgOk","mappingtype" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textCash,R.id.imgMoney,R.id.textValueHidde },share,"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					plistView.setAdapter(listItemAdapter);
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public List<Map<String,Object>> getLogisticsList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		DecimalFormat df = new DecimalFormat("######0.00");
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String lpice = ""; 
				if(dobj.has("lpice"))
					lpice = (String) dobj.get("lpice"); 
				
				String lname = ""; 
				if(dobj.has("lname"))
					lname = (String) dobj.get("lname");
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("lpice", lpice);
				map.put("lname", lname);
				map.put("pkid", pkid);
				map.put("textName", lname + " "+lpice+"￥");
				if(logid != null && !logid.equals(""))
				{
					if(pkid.equals(logid))
					map.put("imgOk", R.drawable.ok);
				}
				else
				{
					map.put("imgOk", null);
				}
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getStorePayList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		DecimalFormat df = new DecimalFormat("######0.00");
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String mappingtype = ""; 
				if(dobj.has("mappingtype"))
					mappingtype = (String) dobj.get("mappingtype"); 
				
				String payname = ""; 
				if(dobj.has("payname"))
					payname = (String) dobj.get("payname");
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("mappingtype", mappingtype);
				map.put("payname", payname);
				if(paytag.equals(mappingtype))
					map.put("imgOk", R.drawable.ok);
				else
					map.put("imgOk", null);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void showInvoiceDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(invoices, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String value = invoices[which];
				invoiceindex = which;
				TextView textContent = (TextView)findViewById(R.id.textContent);
				textContent.setText(value);
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
