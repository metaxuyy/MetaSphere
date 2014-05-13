package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ServiceAddListActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static ServiceAddListActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private ListView mylist;
	private List<Map<String, Object>> messageItem;
	private SpecialAdapter simperAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_list_add_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		
		
		Button break_btn = (Button)findViewById(R.id.home);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openContactView();
			}
		});
		
		showMyLoadingDialog();
		
		initView();
	}
	
	public void initView()
	{
		try{
			mylist = (ListView)findViewById(R.id.listvid);
			
			getServiceListData();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getServiceListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String, Object>> listall = null;
					
					JSONObject jobj = api.getServiceAndHoteleList();
					db.openDB();
					Map<String,Object> storekeylist = db.getStoreInfoAllDataKey();
					db.closeDB();
					if(jobj != null && jobj.has("data"))
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						JSONArray jArr2 = (JSONArray) jobj.get("data2");
						List<Map<String,Object>> slist = myapp.getStroeList(jArr2,storekeylist);
						List<Map<String,Object>> dlist = myapp.getMyCardList(jArr,storekeylist);
						listall = new ArrayList<Map<String,Object>>();
						listall.addAll(slist);
						listall.addAll(dlist);
					}
					
					msg.obj = listall;
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
					
					messageItem = list3;
					// 生成适配器的Item和动态数组对应的元素
					simperAdapter = new SpecialAdapter(ServiceAddListActivity.this, messageItem,
							R.layout.service_add_list_item, new String[] { "imgurl", "storeName","servicetype"},
							new int[] { R.id.img_icon, R.id.name_txt,R.id.message_txt},55,55,false,share,"max");
		
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
//							openFriendDetail(arg2);
							String imgurl = (String)messageItem.get(arg2).get("imgurl");
							String storeName = (String)messageItem.get(arg2).get("storeName");
							String servicetype = (String)messageItem.get(arg2).get("servicetype");
							openServiceInfoView(imgurl,storeName,servicetype,arg2);
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
	
	public Map<String,Object> getServiceInfoMap(int index)
	{
		Map<String,Object> map = null;
		try{
			map = messageItem.get(index);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openContactView();
			return false;
		}
		return false;
	}
	
	public void openServiceInfoView(String imgurl,String storename,String storetype,int index)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ServicesAddViewActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("imgurl", imgurl);
		    bundle.putString("storename", storename);
		    bundle.putString("storetype", storetype);
		    bundle.putInt("index", index);
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openContactView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
