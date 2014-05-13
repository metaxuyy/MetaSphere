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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FindFriendActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static FindFriendActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	
	private ListView mylist;
	private List<Map<String, Object>> messageItem;
	public String mykey;
	private String tag;
	private String isYn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_friend_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		tag = bunde.getString("tag");
		isYn = bunde.getString("isYn");
		
		Button break_btn = (Button)findViewById(R.id.home);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tag.equals("main"))
					openMainView();
				else
					openFriendView();
			}
		});
		
		showMyLoadingDialog();
		
		initView();
	}
	
	public void initView()
	{
		try{
			mylist = (ListView) findViewById(R.id.listvid);
			getFriendAll("");
			EditText editText1 = (EditText)findViewById(R.id.editText1);
			editText1.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String str = s.toString();
					if(str != null && !str.equals(""))
					{
						if(!str.equals(mykey))
						{
							mykey = str;
							getFriendAll(str);
						}
					}
					else
					{
						if(!str.equals(mykey))
						{
							mykey = "";
							getFriendAll("");
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s,
						int start, int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getFriendAll(final String key)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> lists = null;
					JSONObject jobj = api.getStorepfprofileAll(key,isYn);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						lists = myapp.getFrindListData(jArr);
					}
					
					msg.obj = lists;
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
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
					SpecialAdapter simperAdapter = new SpecialAdapter(FindFriendActivity.this, messageItem,
							R.layout.find_friend_item, new String[] { "imgurl", "username"},
							new int[] { R.id.head, R.id.name_txt},55,55,false,share,"max");
		
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							Map<String,Object> map = messageItem.get(arg2);
							openFriendDetail(map);
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
	
	public void openFriendDetail(Map<String,Object> map)
	{
		try{
			String username = (String)map.get("username");
			String pfid = (String)map.get("pfid");
			String imgurl = (String)map.get("imgurl");
			String account = (String)map.get("account");
			String sex = (String)map.get("sex");
			String area = (String)map.get("area");
			String signature = (String)map.get("signature");
			String companyid = (String)map.get("companyid");
			String storeids = (String)map.get("storeids");
			
			Intent intent = new Intent();
		    intent.setClass( this,FriendInfoActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("username", username);
		    bundle.putString("addpfid", pfid);
		    bundle.putString("imgurl", imgurl);
		    bundle.putString("tag", "add");
		    
		    bundle.putString("account", account);
		    bundle.putString("sex", sex);
		    bundle.putString("area", area);
		    bundle.putString("signature", signature);
		    bundle.putString("companyid", companyid);
		    bundle.putString("storeids", storeids);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(tag.equals("main"))
				openMainView();
			else
				openFriendView();
			return false;
		}
		return false;
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,FriendsAddedActivity.class);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openFriendView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,FriendsContactActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
