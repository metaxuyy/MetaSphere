package ms.activitys.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.listviewadapter.SpecialAdapterAddress;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReceivingAddressActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ListView listview;
	private ProgressBar pb;
	private TextView textAddr_tip;
	private String storeid;
	private List<Map<String,Object>> addresslist = new ArrayList<Map<String,Object>>();
	private boolean dfaddress = false;
	private String appdid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addresslistactivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		textAddr_tip = (TextView)findViewById(R.id.textAddr_tip);
		
		storeid = this.getIntent().getExtras().getString("storeid");
		
		Button break_cart = (Button)findViewById(R.id.break_cart);
		break_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dfaddress)
				{
					String consignee = (String)myapp.getAddressmap().get(appdid).get("consignee");
					String textCity = (String)myapp.getAddressmap().get(appdid).get("textCity");
					String address = (String)myapp.getAddressmap().get(appdid).get("address");
					String pkid = (String)myapp.getAddressmap().get(appdid).get("pkid");
					Intent intent = new Intent();
					intent.putExtra("consignee", consignee);
					intent.putExtra("textCity", textCity);
					intent.putExtra("address", address);
					intent.putExtra("pkid", pkid);
					intent.putExtra("activitytype", "address");
					ReceivingAddressActivity.this.setResult(Activity.RESULT_OK, intent);
					ReceivingAddressActivity.this.finish();
				}
				else
				{
					Intent intent = new Intent();
					intent.putExtra("consignee", "");
					intent.putExtra("textCity", "");
					intent.putExtra("address", "");
					intent.putExtra("pkid", "");
					intent.putExtra("activitytype", "address");
					ReceivingAddressActivity.this.setResult(Activity.RESULT_OK, intent);
					ReceivingAddressActivity.this.finish();
				}
			}
		});
		
		if(myapp.getAddressmap() != null)
			myapp.getAddressmap().clear();
		
		showAddressItem();
	}
	
	public void showAddressItem()
	{
		try{
			Button add_btn = (Button)findViewById(R.id.add_btn);
			add_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(ReceivingAddressActivity.this, AddUpdateAddress.class);
				    Bundle bundle = new Bundle();
					bundle.putString("storeid", storeid);
					bundle.putString("aid", "");
					intent.putExtras(bundle);
//				    startActivity(intent);//开始界面的跳转函数
					startActivityForResult(intent, 1);  
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			listview = (ListView)findViewById(R.id.listAddr);
			loadThreadData();
			
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String addressid = (String)addresslist.get(arg2).get("pkid");
					upAddressDuftelThreadData(addressid);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(dfaddress)
			{
				String consignee = (String)myapp.getAddressmap().get(appdid).get("consignee");
				String textCity = (String)myapp.getAddressmap().get(appdid).get("textCity");
				String address = (String)myapp.getAddressmap().get(appdid).get("address");
				String pkid = (String)myapp.getAddressmap().get(appdid).get("pkid");
				Intent intent = new Intent();
				intent.putExtra("consignee", consignee);
				intent.putExtra("textCity", textCity);
				intent.putExtra("address", address);
				intent.putExtra("pkid", pkid);
				intent.putExtra("activitytype", "address");
				ReceivingAddressActivity.this.setResult(Activity.RESULT_OK, intent);
				ReceivingAddressActivity.this.finish();
			}
			else
			{
				Intent intent = new Intent();
				intent.putExtra("consignee", "");
				intent.putExtra("textCity", "");
				intent.putExtra("address", "");
				intent.putExtra("pkid", "");
				intent.putExtra("activitytype", "address");
				ReceivingAddressActivity.this.setResult(Activity.RESULT_OK, intent);
				ReceivingAddressActivity.this.finish();
			}
			return false;
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       if(resultCode == RESULT_OK){
	    	   pb.setVisibility(View.VISIBLE);
	    	   listview.setVisibility(View.GONE);
	    	   if(myapp.getAddressmap() != null)
	   				myapp.getAddressmap().clear();
	   		
	   			showAddressItem();
			}
	       
	     super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj = api.getPfAddressList();
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getAddressList(jArr);
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
	
	public void upAddressDuftelThreadData(final String addressid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try{
					JSONObject jobj = api.updateAddressDefult(addressid);
					if(jobj != null)
					{
						msg.obj = addressid;
					}
					else
						msg.obj = null;
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
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapterAddress listItemAdapter = new SpecialAdapterAddress(ReceivingAddressActivity.this, dlist,// 数据源
							R.layout.addresslistitem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "consignee", "textCity","address","zipcode","telephone","phoneNumber","isdefault" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textName, R.id.textCity,R.id.textCity_detail,R.id.textPostcode,R.id.textTele,R.id.textPhone,R.id.imgAddr_ok },"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					listview.setAdapter(listItemAdapter);
					pb.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
				}
				else
				{
					pb.setVisibility(View.GONE);
					textAddr_tip.setVisibility(View.VISIBLE);
				}
				break;
			case 1:
				String addressid = (String)msg.obj;
				if(addressid != null)
				{
					String consignee = (String)myapp.getAddressmap().get(addressid).get("consignee");
					String textCity = (String)myapp.getAddressmap().get(addressid).get("textCity");
					String address = (String)myapp.getAddressmap().get(addressid).get("address");
					String pkid = (String)myapp.getAddressmap().get(addressid).get("pkid");
					Intent intent = new Intent();
					intent.putExtra("consignee", consignee);
					intent.putExtra("textCity", textCity);
					intent.putExtra("address", address);
					intent.putExtra("pkid", pkid);
					intent.putExtra("activitytype", "address");
					ReceivingAddressActivity.this.setResult(Activity.RESULT_OK, intent);
					ReceivingAddressActivity.this.finish();
				}
				else
				{
					makeText(ReceivingAddressActivity.this.getString(R.string.task_failed));
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public List<Map<String,Object>> getAddressList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			dfaddress = false;
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String address = ""; 
				if(dobj.has("address"))
					address = (String) dobj.get("address"); 
				
				String area = ""; 
				if(dobj.has("area"))
					area = (String) dobj.get("area");
				
				String country = ""; 
				if(dobj.has("country"))
					country = (String) dobj.get("country");
				
				String telephone = ""; 
				if(dobj.has("telephone"))
					telephone = (String) dobj.get("telephone");
				
				String city = ""; 
				if(dobj.has("city"))
					city = (String) dobj.get("city");
				
				String phoneNumber = ""; 
				if(dobj.has("phoneNumber"))
					phoneNumber = (String) dobj.get("phoneNumber");
				
				String consignee = ""; 
				if(dobj.has("consignee"))
					consignee = (String) dobj.get("consignee");
				
				String zipcode = ""; 
				if(dobj.has("zipcode"))
					zipcode = (String) dobj.get("zipcode");
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String isdefault = ""; 
				if(dobj.has("isdefault"))
					isdefault = (String) dobj.get("isdefault");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("address", address);
				map.put("area", area);
				map.put("country", country);
				map.put("telephone", telephone);
				map.put("city", city);
				map.put("phoneNumber", phoneNumber);
				map.put("consignee", consignee);
				map.put("zipcode", zipcode);
				map.put("pkid", pkid);
				map.put("textCity", city+" "+country+" "+area);
				map.put("address2", city+" "+country+" "+area + " " + address);
				map.put("isdefault", isdefault);
				map.put("storeid", storeid);
				
				if(isdefault.equals("0"))
				{
					dfaddress = true;
					appdid = pkid;
				}
	
				list.add(map);
				
				addresslist.add(map);
				myapp.getAddressmap().put(pkid, map);
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
