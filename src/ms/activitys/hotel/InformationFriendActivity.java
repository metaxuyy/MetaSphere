package ms.activitys.hotel;

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

public class InformationFriendActivity extends Activity{
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static InformationFriendActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private ListView mylist;
	private List<Map<String, Object>> messageItem;
	private SpecialAdapter simperAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_friend_view);
		
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
		
		if(ContactActivity.instance != null)
			ContactActivity.instance.setNewNumberTxtVisibility();
		
		MainTabActivity.instance.new_yanzhen_number.setVisibility(View.GONE);
		
		showMyLoadingDialog();
		
		initView();
	}
	
	public void initView()
	{
		try{
			mylist = (ListView) findViewById(R.id.listvid);
			
			getMyVerificationMessageListData();
			
			Button clear_btn = (Button)findViewById(R.id.clear_btn);
			clear_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					deleteMyVerificationMessage();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void refreshListItem()
	{
		messageItem = myapp.getVerificationMessage();
		if(simperAdapter != null)
			simperAdapter.notifyDataSetChanged();
	}
	
	public void getMyVerificationMessageListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> list = null;
					
					JSONObject jobj = api.selectVerificationMessage();
					if(jobj != null && jobj.has("data"))
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
//						list = myapp.getMyVerificationMessageList(jArr);
						Map<String,Object> dmap = myapp.getMyVerificationMessageList(jArr);
						list = (List<Map<String,Object>>)dmap.get("datalist");
//						newnumber = (Integer)dmap.get("newnumber");
						myapp.setVerificationMessage(list);
					}
					
					msg.obj = list;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void deleteMyVerificationMessage()
	{
		showMyLoadingDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					boolean b = false;
					JSONObject jobj = api.deleteMyVerificationMessage();
					if(jobj != null)
					{
						if(jobj.getString("tag").equals("success"))
						{
							b = true;
						}
						
					}
					
					msg.obj = b;
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
					simperAdapter = new SpecialAdapter(InformationFriendActivity.this, messageItem,
							R.layout.information_friend_item, new String[] { "imgurl", "fromname","requestmessage","requeststart"},
							new int[] { R.id.head, R.id.name_txt,R.id.message_txt,R.id.start_txt},55,55,false,share,"max");
		
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							openFriendDetail(arg2);
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
			case 1:
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					mylist.setAdapter(null);
				}
				else
				{
					makeText(getString(R.string.hotel_label_103));
				}
				if(loadDialog != null)
				{
					loadDialog.dismiss();
				}
				break;
			}
		}
	};
	
	public void openFriendDetail(int index)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,VerificationFriendView.class);
		    Bundle bundle = new Bundle();
		    bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openContactView();
			return false;
		}
		return false;
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
