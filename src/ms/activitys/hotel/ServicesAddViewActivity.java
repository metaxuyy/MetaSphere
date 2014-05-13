package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.RegisterAction;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.AsyncImageLoader;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ServicesAddViewActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static ServicesAddViewActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private String imgurl;
	private String storename;
	private String storetype;
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private int index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_add_info_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		imgurl = bunde.getString("imgurl");
		storename = bunde.getString("storename");
		storetype = bunde.getString("storetype");
		index = bunde.getInt("index");
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openServiceListView();
			}
		});
		
//		showMyLoadingDialog();
		
		initView();
	}
	
	public void initView()
	{
		try{
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(storename);
			TextView type_txt = (TextView)findViewById(R.id.type_txt);
			type_txt.setText(storetype);
			ImageView img_icon = (ImageView)findViewById(R.id.img_icon);
			setImageData(img_icon,imgurl);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void addService(View v)
	{
		try{
			showMyLoadingDialog();
			Map<String,Object> map = ServiceAddListActivity.instance.getServiceInfoMap(index);
			if(map.containsKey("storeTyle"))
			{
				addCardListData(map,myapp.getPfprofileId());
			}
			else
			{
				List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
				lists.add(map);
				addServiceListData(lists);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void addServiceListData(final List<Map<String,Object>> lists)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					db.openDB();
					db.saveStoreInfoAllData(lists);
					db.closeDB();
					
					msg.obj = true;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void addCardListData(final Map<String,Object> map,final String pfid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				
				try {
						int j = 0;
						String pfid = "";
						pfid = (String)map.get("pfid");
						String pid = (String)map.get("pid");
						String bid = (String)map.get("bid");
						String storeTyle = (String)map.get("storeTyle");
						String province = (String)map.get("province");
						JSONObject jobj = api.addCards(pfid,pid,bid,storeTyle,province);
						if(jobj != null && jobj.has("newstoreinfo"))
						{
							JSONObject job = jobj.getJSONObject("newstoreinfo");
							JSONArray jArr = new JSONArray();
							jArr.put(job);
							List<Map<String,Object>> lists = myapp.getMyCardList(jArr);
							db.saveStoreInfoAllData(lists);
							
							msg.obj = true;
						}
						else
							msg.obj = false;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = false;
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
				boolean bb = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(bb)
				{
					if(ServiceAddListActivity.instance != null)
					{
						ServiceAddListActivity.instance.showMyLoadingDialog();
						ServiceAddListActivity.instance.getServiceListData();
					}
					
					if(HotelServiceActivity.instance != null)
					{
						HotelServiceActivity.instance.showMyLoadingDialog();
						HotelServiceActivity.instance.getMyStoreListDatas();
					}
					makeText(ServicesAddViewActivity.this.getString(R.string.send_msg_session_lable));
					
					openServiceListView();
				}
				else
				{
					makeText(ServicesAddViewActivity.this.getString(R.string.send_msg_error_lable));
				}
				break;
			case 1:
				boolean start = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(start)
				{
					if(ServiceAddListActivity.instance != null)
					{
						ServiceAddListActivity.instance.showMyLoadingDialog();
						ServiceAddListActivity.instance.getServiceListData();
					}
					
					if(HotelServiceActivity.instance != null)
					{
						HotelServiceActivity.instance.showMyLoadingDialog();
						HotelServiceActivity.instance.getMyStoreListDatas();
					}
					makeText(ServicesAddViewActivity.this.getString(R.string.send_msg_session_lable));
					
					openServiceListView();
				}
				else
				{
					makeText(ServicesAddViewActivity.this.getString(R.string.send_msg_error_lable));
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void setImageData(final ImageView v,String url)
	{
		try{
			imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						
						v.setImageDrawable(imageDrawable);
					}
				}
			},60,60);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openServiceListView();
			return false;
		}
		return false;
	}
	
	public void openServiceListView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ServiceAddListActivity.class);
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
