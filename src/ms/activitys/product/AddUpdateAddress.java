package ms.activitys.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

public class AddUpdateAddress extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String addressid;
	private TextView textDelete;
	private Button savebtn;
	private ProgressDialog mypDialog;
	private String storeid;
	
	private List<Map<String,String>> cascade1;
	private List<Map<String,String>> cascade2;
	private List<Map<String,String>> cascade3;
	
	private Spinner mySpinner01;
	private Spinner mySpinner02;
	private Spinner mySpinner03;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addressmanageractivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		storeid = this.getIntent().getExtras().getString("storeid");
		
		addressid = this.getIntent().getExtras().getString("aid");
		savebtn = (Button)findViewById(R.id.save_btn);
		textDelete = (TextView)findViewById(R.id.textDelete);
		
		loadSpinner();
		
		Button break_cart = (Button)findViewById(R.id.break_cart);
		break_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddUpdateAddress.this.setResult(RESULT_OK, getIntent());
				AddUpdateAddress.this.finish();
			}
		});
		
		if(addressid.equals(""))
			showAddAddressManager();
		else
			showUpAddressManager();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AddUpdateAddress.this.setResult(RESULT_OK, getIntent());
			AddUpdateAddress.this.finish();
			return false;
		}
		return false;
	}
	
	public void loadSpinner()
	{
		try{
			mySpinner01 = (Spinner)findViewById(R.id.mySpinner01);
			cascadeThreadData1("Mted44338677gq4wlumk");
			mySpinner01.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Map<String,String> map = cascade1.get(arg2);
					String id = map.get("id");
					String name = map.get("name");
					cascadeThreadData2(id);
					
					TextView editArea = (TextView)findViewById(R.id.editArea);
					editArea.setText(name);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			mySpinner02 = (Spinner)findViewById(R.id.mySpinner02);
			mySpinner02.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Map<String,String> map = cascade2.get(arg2);
					String id = map.get("id");
					String name = map.get("name");
					cascadeThreadData3(id);
					
					TextView editArea = (TextView)findViewById(R.id.editArea);
					editArea.setText(mySpinner01.getSelectedItem().toString() + " " + name);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			mySpinner03 = (Spinner)findViewById(R.id.mySpinner03);
			mySpinner03.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Map<String,String> map = cascade3.get(arg2);
					String id = map.get("id");
					String name = map.get("name");
					
					TextView editArea = (TextView)findViewById(R.id.editArea);
					editArea.setText(mySpinner01.getSelectedItem().toString() + " " + mySpinner02.getSelectedItem().toString() + " " + name);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//添加地址
	public void showAddAddressManager()
	{
		try{
			textDelete.setVisibility(View.GONE);
			
			savebtn.setText(this.getString(R.string.save_info));
			savebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String mssage = fromVerification();
					if(mssage.equals(""))
					{
						showProgressDialog();
						EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
						String consignee = editConsignee.getText().toString();
						
						EditText editTel = (EditText)findViewById(R.id.editTel);
						String phoneNumber = editTel.getText().toString();
						
						EditText editPhone = (EditText)findViewById(R.id.editPhone);
						String telephone = editPhone.getText().toString();
						
						TextView editArea = (TextView)findViewById(R.id.editArea);
						String area2 = editArea.getText().toString();
						String [] areas = area2.split(" ");
						String city = "";
						String country = "";
						String area = "";
						if(areas.length > 0)
							city = areas[0];
						if(areas.length >= 2)
							country = areas[1];
						if(areas.length == 3)
							area = areas[2];
						
						EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
						String zipcode = editPostcode.getText().toString();
						
						EditText editAddr = (EditText)findViewById(R.id.editAddr);
						String address = editAddr.getText().toString();
						
						Map<String,String> map = new HashMap<String,String>();
						map.put("consignee", consignee);
						map.put("phoneNumber", phoneNumber);
						map.put("telephone", telephone);
						map.put("city", city);
						map.put("country", country);
						map.put("area", area);
						map.put("zipcode", zipcode);
						map.put("address", address);
						map.put("pfid", myapp.getPfprofileId());
						map.put("isdefault", "1");
						
						JSONObject job = new JSONObject(map);
						addThreadData(job.toString());
					}
					else
					{
						makeText(mssage);
					}
				}
			});
			final LinearLayout linPca_sellayout = (LinearLayout)findViewById(R.id.linPca_sellayout);
			
			TextView textConsignee = (TextView)findViewById(R.id.textConsignee);
			textConsignee.setVisibility(View.GONE);
			EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
			editConsignee.setVisibility(View.VISIBLE);
			editConsignee.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus)
					{
						linPca_sellayout.setVisibility(View.GONE);
					}
				}
			});
			
			TextView textTel = (TextView)findViewById(R.id.textTel);
			textTel.setVisibility(View.GONE);
			EditText editTel = (EditText)findViewById(R.id.editTel);
			editTel.setVisibility(View.VISIBLE);
			editTel.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus)
					{
						linPca_sellayout.setVisibility(View.GONE);
					}
				}
			});
			
			TextView textPhone = (TextView)findViewById(R.id.textPhone);
			textPhone.setVisibility(View.GONE);
			EditText editPhone = (EditText)findViewById(R.id.editPhone);
			editPhone.setVisibility(View.VISIBLE);
			editPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus)
					{
						linPca_sellayout.setVisibility(View.GONE);
					}
				}
			});
			
			TextView textArea = (TextView)findViewById(R.id.textArea);
			textArea.setVisibility(View.GONE);
			final TextView editArea = (TextView)findViewById(R.id.editArea);
			editArea.setVisibility(View.VISIBLE);
			editArea.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					linPca_sellayout.setVisibility(View.VISIBLE);
					
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm.hideSoftInputFromWindow(editArea.getWindowToken(),0);
				}
			});
			
			TextView textPostcode = (TextView)findViewById(R.id.textPostcode);
			textPostcode.setVisibility(View.GONE);
			EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
			editPostcode.setVisibility(View.VISIBLE);
			editPostcode.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus)
					{
						linPca_sellayout.setVisibility(View.GONE);
					}
				}
			});
			
			TextView textAddr = (TextView)findViewById(R.id.textAddr);
			textAddr.setVisibility(View.GONE);
			EditText editAddr = (EditText)findViewById(R.id.editAddr);
			editAddr.setVisibility(View.VISIBLE);
			editAddr.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(hasFocus)
					{
						linPca_sellayout.setVisibility(View.GONE);
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//更新地址
	public void showUpAddressManager()
	{
		try{
			savebtn.setText(this.getString(R.string.menu_lable_55));
			savebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					upAddressManager();
				}
			});
			
			textDelete.setVisibility(View.VISIBLE);
			textDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProgressDialog();
					deleteThreadData();
				}
			});
			
			Map<String,Object> map = myapp.getAddressmap().get(addressid);
			String address = (String)map.get("address");
			String area = (String)map.get("area");
			String country = (String)map.get("country");
			String telephone = (String)map.get("telephone");
			String city = (String)map.get("city");
			String phoneNumber = (String)map.get("phoneNumber");
			String consignee = (String)map.get("consignee");
			String zipcode = (String)map.get("zipcode");
			String isdefault = (String)map.get("isdefault");
			String textCity = (String)map.get("textCity");
			
			TextView textConsignee = (TextView)findViewById(R.id.textConsignee);
			textConsignee.setVisibility(View.VISIBLE);
			textConsignee.setText(consignee);
			EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
			editConsignee.setVisibility(View.GONE);
			editConsignee.setText(consignee);
			
			TextView textTel = (TextView)findViewById(R.id.textTel);
			textTel.setVisibility(View.VISIBLE);
			textTel.setText(phoneNumber);
			EditText editTel = (EditText)findViewById(R.id.editTel);
			editTel.setVisibility(View.GONE);
			editTel.setText(phoneNumber);
			
			TextView textPhone = (TextView)findViewById(R.id.textPhone);
			textPhone.setVisibility(View.VISIBLE);
			textPhone.setText(telephone);
			EditText editPhone = (EditText)findViewById(R.id.editPhone);
			editPhone.setVisibility(View.GONE);
			editPhone.setText(telephone);
			
			TextView textArea = (TextView)findViewById(R.id.textArea);
			textArea.setVisibility(View.VISIBLE);
			textArea.setText(textCity);
			TextView editArea = (TextView)findViewById(R.id.editArea);
			editArea.setVisibility(View.GONE);
			editArea.setText(textCity);
			
			TextView textPostcode = (TextView)findViewById(R.id.textPostcode);
			textPostcode.setVisibility(View.VISIBLE);
			textPostcode.setText(zipcode);
			EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
			editPostcode.setVisibility(View.GONE);
			editPostcode.setText(zipcode);
			
			TextView textAddr = (TextView)findViewById(R.id.textAddr);
			textAddr.setVisibility(View.VISIBLE);
			textAddr.setText(address);
			EditText editAddr = (EditText)findViewById(R.id.editAddr);
			editAddr.setVisibility(View.GONE);
			editAddr.setText(address);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//更新地址
	public void upAddressManager()
	{
		try{
				savebtn.setText(this.getString(R.string.save_info));
				savebtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String mssage = fromVerification();
						if(mssage.equals(""))
						{
							showProgressDialog();
							EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
							String consignee = editConsignee.getText().toString();
							
							EditText editTel = (EditText)findViewById(R.id.editTel);
							String phoneNumber = editTel.getText().toString();
							
							EditText editPhone = (EditText)findViewById(R.id.editPhone);
							String telephone = editPhone.getText().toString();
							
							TextView editArea = (TextView)findViewById(R.id.editArea);
							String area2 = editArea.getText().toString();
							String [] areas = area2.split(" ");
							String city = "";
							String country = "";
							String area = "";
							if(areas.length > 0)
								city = areas[0];
							if(areas.length >= 2)
								country = areas[1];
							if(areas.length == 3)
								area = areas[2];
							
							EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
							String zipcode = editPostcode.getText().toString();
							
							EditText editAddr = (EditText)findViewById(R.id.editAddr);
							String address = editAddr.getText().toString();
							
							Map<String,String> map = new HashMap<String,String>();
							map.put("consignee", consignee);
							map.put("phoneNumber", phoneNumber);
							map.put("telephone", telephone);
							map.put("city", city);
							map.put("country", country);
							map.put("area", area);
							map.put("zipcode", zipcode);
							map.put("address", address);
							map.put("pfid", myapp.getPfprofileId());
							
							
							JSONObject job = new JSONObject(map);
							updateThreadData(job.toString());
						}
						else
						{
							makeText(mssage);
						}
					}
				});
				
				textDelete.setVisibility(View.GONE);
				
				Map<String,Object> map = myapp.getAddressmap().get(addressid);
				String address = (String)map.get("address");
				String area = (String)map.get("area");
				String country = (String)map.get("country");
				String telephone = (String)map.get("telephone");
				String city = (String)map.get("city");
				String phoneNumber = (String)map.get("phoneNumber");
				String consignee = (String)map.get("consignee");
				String zipcode = (String)map.get("zipcode");
				String isdefault = (String)map.get("isdefault");
				String textCity = (String)map.get("textCity");
				
				final LinearLayout linPca_sellayout = (LinearLayout)findViewById(R.id.linPca_sellayout);
				
				TextView textConsignee = (TextView)findViewById(R.id.textConsignee);
				textConsignee.setVisibility(View.GONE);
				textConsignee.setText(consignee);
				EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
				editConsignee.setVisibility(View.VISIBLE);
				editConsignee.setText(consignee);
				editConsignee.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(hasFocus)
						{
							linPca_sellayout.setVisibility(View.GONE);
						}
					}
				});
				
				TextView textTel = (TextView)findViewById(R.id.textTel);
				textTel.setVisibility(View.GONE);
				textTel.setText(phoneNumber);
				EditText editTel = (EditText)findViewById(R.id.editTel);
				editTel.setVisibility(View.VISIBLE);
				editTel.setText(phoneNumber);
				editTel.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(hasFocus)
						{
							linPca_sellayout.setVisibility(View.GONE);
						}
					}
				});
				
				TextView textPhone = (TextView)findViewById(R.id.textPhone);
				textPhone.setVisibility(View.GONE);
				textPhone.setText(telephone);
				EditText editPhone = (EditText)findViewById(R.id.editPhone);
				editPhone.setVisibility(View.VISIBLE);
				editPhone.setText(telephone);
				editPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(hasFocus)
						{
							linPca_sellayout.setVisibility(View.GONE);
						}
					}
				});
				
				TextView textArea = (TextView)findViewById(R.id.textArea);
				textArea.setVisibility(View.GONE);
				textArea.setText(textCity);
				final TextView editArea = (TextView)findViewById(R.id.editArea);
				editArea.setVisibility(View.VISIBLE);
				editArea.setText(textCity);
				editArea.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						linPca_sellayout.setVisibility(View.VISIBLE);
						
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				        imm.hideSoftInputFromWindow(editArea.getWindowToken(),0);
					}
				});
				
				TextView textPostcode = (TextView)findViewById(R.id.textPostcode);
				textPostcode.setVisibility(View.GONE);
				textPostcode.setText(zipcode);
				EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
				editPostcode.setVisibility(View.VISIBLE);
				editPostcode.setText(zipcode);
				editPostcode.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(hasFocus)
						{
							linPca_sellayout.setVisibility(View.GONE);
						}
					}
				});
				
				TextView textAddr = (TextView)findViewById(R.id.textAddr);
				textAddr.setVisibility(View.GONE);
				textAddr.setText(address);
				EditText editAddr = (EditText)findViewById(R.id.editAddr);
				editAddr.setVisibility(View.VISIBLE);
				editAddr.setText(address);
				editAddr.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(hasFocus)
						{
							linPca_sellayout.setVisibility(View.GONE);
						}
					}
				});
			}catch(Exception ex){
				ex.printStackTrace();
			}
	}
	
	public void showCascadeData(String moduleid)
	{
		try{
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void deleteThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try{
					JSONObject jobj = api.deleteAddress(addressid);
					if(jobj != null)
					{
						msg.obj = true;
					}
					else
						msg.obj = false;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void cascadeThreadData1(final String moduleid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				
				try{
					JSONObject jobj = api.getCascadeData(moduleid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						Map<String,Object> map = getCascadeDataList(jArr);
						msg.obj = map;
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
	
	public void cascadeThreadData2(final String moduleid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 4;
				
				try{
					JSONObject jobj = api.getCascadeData(moduleid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						Map<String,Object> map = getCascadeDataList(jArr);
						msg.obj = map;
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
	
	public void cascadeThreadData3(final String moduleid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 5;
				
				try{
					JSONObject jobj = api.getCascadeData(moduleid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						Map<String,Object> map = getCascadeDataList(jArr);
						msg.obj = map;
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
	
	public void updateThreadData(final String jsonstr)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try{
					JSONObject jobj = api.updateAddress(addressid,jsonstr);
					if(jobj != null)
					{
						msg.obj = true;
					}
					else
						msg.obj = false;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	public void addThreadData(final String jsonstr)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try{
					JSONObject jobj = api.addAddress(addressid,jsonstr,storeid);
					if(jobj != null)
					{
						msg.obj = true;
					}
					else
						msg.obj = false;
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
				boolean a = (Boolean)msg.obj;
				if(a)
				{
//					Map<String,Object> map = myapp.getAddressmap().get(addressid);
//					myapp.getAddressmap().remove(map);
					mypDialog.dismiss();
					AddUpdateAddress.this.setResult(RESULT_OK, getIntent());
					AddUpdateAddress.this.finish();
				}
				else
				{
					makeText(AddUpdateAddress.this.getString(R.string.task_failed));
				}
				break;
			case 1:
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					mypDialog.dismiss();
					AddUpdateAddress.this.setResult(RESULT_OK, getIntent());
					AddUpdateAddress.this.finish();
				}
				else
				{
					makeText(AddUpdateAddress.this.getString(R.string.task_failed));
				}
				break;
			case 2:
				boolean c = (Boolean)msg.obj;
				if(c)
				{
					mypDialog.dismiss();
					AddUpdateAddress.this.setResult(RESULT_OK, getIntent());
					AddUpdateAddress.this.finish();
				}
				else
				{
					makeText(AddUpdateAddress.this.getString(R.string.task_failed));
				}
				break;
			case 3:
				if(msg.obj != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					cascade1 = (List<Map<String,String>>)map.get("list");
					String[] strs = (String[])map.get("strs");
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddUpdateAddress.this,android.R.layout.simple_spinner_item,strs);
					//设置下拉列表的风格  
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mySpinner01.setAdapter(adapter);
				}
				break;
			case 4:
				if(msg.obj != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					cascade2 = (List<Map<String,String>>)map.get("list");
					String[] strs = (String[])map.get("strs");
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddUpdateAddress.this,android.R.layout.simple_spinner_item,strs);
					//设置下拉列表的风格  
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mySpinner02.setAdapter(adapter);
				}
				break;
			case 5:
				if(msg.obj != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					cascade3 = (List<Map<String,String>>)map.get("list");
					String[] strs = (String[])map.get("strs");
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddUpdateAddress.this,android.R.layout.simple_spinner_item,strs);
					//设置下拉列表的风格  
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mySpinner03.setAdapter(adapter);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
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
	
	public Map<String,Object> getCascadeDataList(JSONArray jArr){
		Map<String,Object> dmap = new HashMap<String,Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try{
			String [] strs = new String[jArr.length()];
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String name = ""; 
				if(dobj.has("name"))
					name = (String) dobj.get("name"); 
				
				String id = ""; 
				if(dobj.has("id"))
					id = (String) dobj.get("id");
				
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("name", name);
				map.put("id", id);
				
				strs[i] = name;
				
				list.add(map);
			}
			dmap.put("list", list);
			dmap.put("strs", strs);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dmap;
	}
	
	public String fromVerification()
	{
		String str = "";
		try{
			EditText editConsignee = (EditText)findViewById(R.id.editConsignee);
			String consignee = editConsignee.getText().toString();
			if(consignee.equals(""))
				return getString(R.string.menu_lable_74);
			
			EditText editTel = (EditText)findViewById(R.id.editTel);
			String phoneNumber = editTel.getText().toString();
			
			EditText editPhone = (EditText)findViewById(R.id.editPhone);
			String telephone = editPhone.getText().toString();
			if(phoneNumber.equals("") && telephone.equals(""))
				return getString(R.string.menu_lable_75);
			
			TextView editArea = (TextView)findViewById(R.id.editArea);
			String area2 = editArea.getText().toString();
			if(area2.equals(""))
				return getString(R.string.menu_lable_76);
			
			EditText editPostcode = (EditText)findViewById(R.id.editPostcode);
			String zipcode = editPostcode.getText().toString();
			if(zipcode.equals(""))
				return getString(R.string.menu_lable_77);
			
			EditText editAddr = (EditText)findViewById(R.id.editAddr);
			String address = editAddr.getText().toString();
			if(address.equals(""))
				return getString(R.string.menu_lable_78);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
