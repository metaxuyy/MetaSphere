package ms.activitys.hotel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatMessageInfoActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static ChatMessageInfoActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	
	private String pfid;
	private Map<String,Object> mapdata;
	private CheckBox check_top;
	private String typesMapping;
	private RelativeLayout order_layout;
	public String storeName;
	private String groupimg;
	private GridView gridview;
	private RelativeLayout group_name_rl;
	private Button exit_btn;
	private LinearLayout friend_layout;
	private LinearLayout friend_layout2;
	public static FileUtils fileUtil = new FileUtils();
	private String groupid;
	private TextView group_name_txt;
	private static List<Map<String,Object>> grouplist = new ArrayList<Map<String,Object>>();
	public ImageGridAdapter saImageItems;
	private LinearLayout img_groud_layout;
	private int isshow = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_message_info);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		pfid = bunde.getString("pfid");
		typesMapping = bunde.getString("typesMapping");
		storeName = bunde.getString("storeName");
		if(typesMapping.equals("group"))
		{
			groupimg = bunde.getString("groupimg");
			groupid = bunde.getString("groupid");
		}
		else if(typesMapping.equals("qa"))
		{
			if (myapp.getIsServer()) {
				RelativeLayout order_layout = (RelativeLayout)findViewById(R.id.order_layout);
				order_layout.setVisibility(View.VISIBLE);
			}
		}
		
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMessageList();
			}
		});
		
		order_layout = (RelativeLayout)findViewById(R.id.order_layout);
		if(typesMapping.equals("qa"))
		{
			order_layout.setVisibility(View.VISIBLE);
		}
		else
		{
			order_layout.setVisibility(View.GONE);
		}
		
		initView();
	}
	
	public void initView()
	{
		try{
			gridview = (GridView)findViewById(R.id.gridview);
			group_name_rl = (RelativeLayout)findViewById(R.id.group_name_rl);
			exit_btn = (Button)findViewById(R.id.exit_btn);
			friend_layout = (LinearLayout)findViewById(R.id.friend_layout);
			friend_layout2 = (LinearLayout)findViewById(R.id.friend_layout2);
			img_groud_layout = (LinearLayout)findViewById(R.id.img_groud_layout);
			
			if(typesMapping.equals("friend"))
			{
				group_name_rl.setVisibility(View.GONE);
				exit_btn.setVisibility(View.GONE);
				gridview.setVisibility(View.GONE);
				
				db.openDB();
				mapdata = db.getFriendpfidData(pfid);
				db.closeDB();
				
				String isTop = "";
				if(mapdata != null)
				{
					String pkid = (String)mapdata.get("pkid");
					String username = (String)mapdata.get("username");
					String pfid = (String)mapdata.get("pfid");
					String namePinyin = (String)mapdata.get("namePinyin");
					String userimg = (String)mapdata.get("userimg");
					String account = (String)mapdata.get("account");
					String sex = (String)mapdata.get("sex");
					String area = (String)mapdata.get("area");
					String signature = (String)mapdata.get("signature");
					
					RoundAngleImageView imgview = (RoundAngleImageView)findViewById(R.id.iv_userhead);
					TextView nametxt = (TextView)findViewById(R.id.name_txt);
					if(userimg != null && !userimg.equals(""))
					{
						Bitmap bitmap = getLoacalBitmap(userimg);
						imgview.setImageBitmap(bitmap);
					}
					
					nametxt.setText(username);
				}
				
				db.openDB();
				isTop = db.selectNewMessageIsTop(pfid);
				db.closeDB();
				
				check_top = (CheckBox)findViewById(R.id.check_top);
				if(isTop.equals("0"))
					check_top.setChecked(true);
				else
					check_top.setChecked(false);
			}
			else if(typesMapping.equals("group"))
			{
				friend_layout.setVisibility(View.GONE);
				friend_layout2.setVisibility(View.GONE);
				
				group_name_txt = (TextView)findViewById(R.id.group_name_txt);
				group_name_txt.setText(storeName);
				
				group_name_rl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						openGroupNameSetting();
					}
				});
				
				exit_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}
				});
				
				img_groud_layout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						saImageItems.hidedeletebutton();
					}
				});
				
//				gridview.setOnItemClickListener(new OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1,
//							int arg2, long arg3) {
//						// TODO Auto-generated method stub
//						Map<String,Object> map = grouplist.get(arg2);
//						String userid = (String)map.get("userid");
//						if(userid.equals("add") || userid.equals("delete") || userid.equals(""))
//						{
//							saImageItems.hidedeletebutton();
//						}
//					}
//				});
				
				getGridViewDatas();
			}
			else
			{
				Bitmap bitmap = myapp.getStoreimgbitmap();
				
				RoundAngleImageView imgview = (RoundAngleImageView)findViewById(R.id.iv_userhead);
				TextView nametxt = (TextView)findViewById(R.id.name_txt);
				if(bitmap != null)
				{
					imgview.setImageBitmap(bitmap);
				}
				
				nametxt.setText(storeName);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateView(Map<String,Object> umap)
	{
		try{
			if(typesMapping.equals("friend"))
			{
				mapdata = umap;
				
				String isTop = "";
				if(mapdata != null)
				{
					String pkid = (String)mapdata.get("pkid");
					String username = (String)mapdata.get("username");
					String pfid = (String)mapdata.get("pfid");
					String namePinyin = (String)mapdata.get("namePinyin");
					String userimg = (String)mapdata.get("userimg");
					String account = (String)mapdata.get("account");
					String sex = (String)mapdata.get("sex");
					String area = (String)mapdata.get("area");
					String signature = (String)mapdata.get("signature");
					
					RoundAngleImageView imgview = (RoundAngleImageView)findViewById(R.id.iv_userhead);
					TextView nametxt = (TextView)findViewById(R.id.name_txt);
					if(userimg != null && !userimg.equals(""))
					{
						Bitmap bitmap = getLoacalBitmap(userimg);
						imgview.setImageBitmap(bitmap);
					}
					
					nametxt.setText(username);
				}
			}
			else if(typesMapping.equals("group"))
			{
				group_name_txt = (TextView)findViewById(R.id.group_name_txt);
				group_name_txt.setText(storeName);
				
				getGridViewDatas();
			}
			else
			{
				Bitmap bitmap = myapp.getStoreimgbitmap();
				
				RoundAngleImageView imgview = (RoundAngleImageView)findViewById(R.id.iv_userhead);
				TextView nametxt = (TextView)findViewById(R.id.name_txt);
				if(bitmap != null)
				{
					imgview.setImageBitmap(bitmap);
				}
				
				nametxt.setText(storeName);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setGroupName(String gname)
	{
		storeName = gname;
		group_name_txt.setText(gname);
	}
	
	public void getGridViewDatas()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					List<Map<String,Object>> userlist = null;
					
					JSONObject jobj = api.getGroupNameIdData(groupid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						List<Map<String,Object>> list = myapp.getGroupNameIdListData(jArr);
						msg.obj = list;
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void user_info(View v)
	{
		//打开用户信息信息
		try{
			String pkid = (String)mapdata.get("pkid");
			String username = (String)mapdata.get("username");
			String pfid = (String)mapdata.get("pfid");
			String userimg = (String)mapdata.get("userimg");
			String account = (String)mapdata.get("account");
			String sex = (String)mapdata.get("sex");
			String area = (String)mapdata.get("area");
			String signature = (String)mapdata.get("signature");
			
			Intent intent = new Intent();
		    intent.setClass( this,FriendInfoViewActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("username", username);
		    bundle.putString("addpfid", pfid);
		    bundle.putString("imgurl", userimg);
		    bundle.putString("pkid", pkid);
		    
		    bundle.putString("account", account);
		    bundle.putString("sex", sex);
		    bundle.putString("area", area);
		    bundle.putString("signature", signature);
		    bundle.putString("tag","chatmessage");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void add_user(View v)
	{
		//添加用户
		String username = (String)mapdata.get("username");
		String pfid = (String)mapdata.get("pfid");
		Intent intent = new Intent(this, GroupContactActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "messageinfodan");
		bundle.putString("groupid", groupid);
		bundle.putString("gids", pfid);
		bundle.putString("friendid", pfid);
		bundle.putString("friendname", username);
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up_in,R.anim.faded_out);
	}
	
	public void always_on_top(View v)
	{
		String pfid = (String)mapdata.get("pfid");
		//该用户置顶
		if(check_top.isChecked())
		{
			check_top.setChecked(false);
			db.openDB();
			db.updateNewMessageIsTop(pfid, "1");
			db.closeDB();
		}
		else
		{
			check_top.setChecked(true);
			db.openDB();
			db.updateNewMessageIsTop(pfid, "0");
			db.closeDB();
			
		}
		if(HotelMainActivity.instance != null)
			HotelMainActivity.instance.loadeListItemData();
	}
	
	public void search_chatting_content(View v)
	{
		//搜索信息内容
	}
	
	public void clear_chatting_history(View v)
	{
		//删除信息记录
		Intent intent = new Intent (this,RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "chartmessageEmpty");
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	public void deleteChattingHistory()
	{
		try{
			String pfid = (String)mapdata.get("pfid");
			sendVerificationMessage(pfid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void deleteGroupUser(final String userid,final String name)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					List<Map<String,Object>> storelist = null;
					StringBuffer sbnames = new StringBuffer();
					StringBuffer sbids = new StringBuffer();
					if(grouplist.size() > 0)
					{
//						Map<String, String> map = new HashMap<String, String>();
//				    	map.put("username", myapp.getUserName());
//				    	map.put("pfid", myapp.getUserNameId());
//				    	selFriendArr.add(map);
				    	
				    	for(int i=0;i<grouplist.size();i++)
						{
							Map<String, Object> maps = grouplist.get(i);
							String pfid = (String)maps.get("userid");
							if(userid.equals(pfid))
							{
								grouplist.remove(i);
								break;
							}
						}
				    	
				    	for(int i=0;i<grouplist.size();i++)
						{
							Map<String, Object> maps = grouplist.get(i);
							String pfid = (String)maps.get("userid");
							if(i == 0)
								sbids.append(pfid);
							else
							{
								if(pfid.equals("add"))
									break;
								else
									sbids.append(","+pfid);
							}
						}
				    	
						JSONObject jobj = api.updateGroupMessageData(sbnames.toString(),sbids.toString(),groupid);
						if(jobj != null && jobj.has("success"))
						{
							db.openDB();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							Map<String,Object> mapdata = new HashMap<String,Object>();
//							mapdata.put("storeid", groupid);
//							mapdata.put("sname", sbnames.toString());
//							mapdata.put("serviceid", "");
//							mapdata.put("servicename", "");
//							mapdata.put("isGroup", "0");
//							mapdata.put("messagetype", "group");
//							mapdata.put("nameid", myapp.getPfprofileId());
//							mapdata.put("mymessagecontent", "你把"+name+"从该群聊中删除了");
//							mapdata.put("mysendtime", sdf.format(new Date()));
//							mapdata.put("watag", "app");
//							mapdata.put("isRead", "0");
//							mapdata.put("groupimg", sbids.toString());
//							db.saveNewMessageData(mapdata);
							
							Map<String,Object> newmap = new HashMap<String, Object>();
							String mid = myapp.getCombID();
							newmap.put("mid", mid);
							newmap.put("toid", groupid);
							newmap.put("storeid", groupid);
							newmap.put("serviceid", "");
							newmap.put("servicename", "");
							newmap.put("sname", sbnames.toString());
							newmap.put("nameid", myapp.getPfprofileId());
							newmap.put("mysendname", myapp.getPfprofileId());
							newmap.put("userimg", myapp.getUserimgbitmap());
							newmap.put("fname", "");
							newmap.put("tname", "");
							newmap.put("yiman", R.drawable.yi_man);
							newmap.put("mymessagecontent", "你把"+name+"从该群聊中删除了");
							newmap.put("mysendtime",sdf.format(new Date()));
							newmap.put("fileUrl","");
							newmap.put("fileUrl2","");
							newmap.put("toname", groupid);
							newmap.put("fileType", "");
							newmap.put("fileType2", "");
							newmap.put("fileName", "");
							newmap.put("fileName2", "");
							newmap.put("time", "");
							newmap.put("timetext", "");
							newmap.put("messagetype", "groupadddel");
							newmap.put("sendimg", "1");
							newmap.put("sendprogress", "0");
							newmap.put("storename", "");  
							newmap.put("storeDoc", "");
							newmap.put("storeimg", "");
							newmap.put("storelist", null);
							newmap.put("isRead", "0");
							newmap.put("isGroup", "0");
							newmap.put("watag", "app");
							newmap.put("messagestart", "");
							newmap.put("groupimg", sbids.toString());
							
							List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
							dlist.add(newmap);
							db.saveMessageData(dlist);
							
							db.closeDB();
							Map<String,Object> rmap = new HashMap<String,Object>();
							rmap.put("gname", sbnames.toString());
							rmap.put("gid", groupid);
							rmap.put("groupids", sbids.toString());
							rmap.put("newmessage", newmap);
							msg.obj = rmap;
						}
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendVerificationMessage(final String storeid)
    {
		showMyLoadingDialog(getString(R.string.hotel_label_115));
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					boolean b = true;
					db.openDB();
					db.deletetNewMessageData(storeid, myapp.getPfprofileId());
					db.closeDB();
					msg.obj = b;
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
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					if(loadDialog != null)
					{
						loadDialog.setSucceedDialog(getString(R.string.hotel_label_118));
						loadDialog.dismiss();
					}
					if(MessageListActivity.instance != null)
						MessageListActivity.instance.getLocalMessageListData("refresh");
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
				}
				else
				{
					if(loadDialog != null)
					{
						loadDialog.setErrorDialog(getString(R.string.hotel_label_119));
						loadDialog.dismiss();
					}
				}
				break;
			case 1:
				List<Map<String, Object>> dlist = (List<Map<String, Object>>)msg.obj;
				if(dlist != null)
				{
					grouplist = dlist;
					saImageItems = new ImageGridAdapter(ChatMessageInfoActivity.this, dlist, R.layout.user_add_img_grid_item,isshow);
					
					gridview.setAdapter(saImageItems);
					gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
					gridview.setVisibility(View.VISIBLE);
//					gridview.setOnItemClickListener(new OnItemClickListener() {
//
//						@Override
//						public void onItemClick(AdapterView<?> arg0, View arg1,
//								int arg2, long arg3) {
//							// TODO Auto-generated method stub
//							Map<String,Object> map = grouplist.get(arg2);
//							openFriendDetail(map);
//						}
//					});
					
					int totalHeight = 0;
					int numColumns = 4;
					int demoIntValue = 0;
					int list_child_item_height = 0;
					int numRow = 0;
					int adapter_Count = saImageItems.getCount();
					if(adapter_Count % numColumns != 0){
	                        demoIntValue = 1;
	                }
					View listItem = saImageItems.getView(0, null, gridview);
		            listItem.measure(0, 0); 
		            list_child_item_height = listItem.getMeasuredHeight()+5;
					
					numRow = saImageItems.getCount()/4 + demoIntValue;
					totalHeight = numRow * list_child_item_height; 
					
					LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridview.getLayoutParams();
					linearParams.height = totalHeight;
					gridview.setLayoutParams(linearParams);
				}
				break;
			case 2:
				Map<String,Object> gmap2 = (Map<String,Object>)msg.obj;
				if(gmap2 != null)
				{
					Map<String,Object> newmap = (Map<String,Object>)gmap2.get("newmessage");
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					
					if(MessageListActivity.instance != null)
					{
						MessageListActivity.instance.mymessageItem.add(newmap);
						MessageListActivity.instance.myAdapter.notifyDataSetChanged();
					}
					
					getGridViewDatas();
				}
				break;
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMessageList();
			return false;
		}
		return false;
	}
	
	public void openGroupNameSetting()
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "groupname");
	    bundle.putString("groupname", storeName);
	    bundle.putString("groupid", groupid);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void openMessageList()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openOrderList(View v)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,UserOrderHistoryActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("openid", pfid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openAddGroup() {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<grouplist.size();i++)
		{
			Map<String,Object> map = grouplist.get(i);
			String pfid = (String)map.get("userid");
			if(i == 0)
				sb.append(pfid);
			else
			{
				if(pfid.equals("add"))
					break;
				else
					sb.append(","+pfid);
			}
		}
		Intent intent = new Intent(this, GroupContactActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "messageinfo");
		bundle.putString("groupid", groupid);
		bundle.putString("gids", sb.toString());
		intent.putExtras(bundle);
		startActivity(intent);
		MainTabActivity.instance.loadButtomActivity(intent);
	}
	
	public void openFriendDetail(Map<String,Object> map)
	{
		try{
			String username = (String)map.get("username");
			String pfid = (String)map.get("userid");
			String imgurl = fileUtil.getImageFile2aPath(pfid, pfid);
			String account = (String)map.get("account");
			String sex = (String)map.get("sex");
			String area = (String)map.get("area");
			String signature = (String)map.get("signature");
			String companyid = (String)map.get("companyid");
			String storeids = (String)map.get("storeids");
			
			db.openDB();
			Map<String,Object> mapdata = db.getFriendpfidData(pfid);
			db.closeDB();
			if(mapdata == null)
			{
				Intent intent = new Intent();
			    intent.setClass( this,FriendInfoActivity.class);
			    Bundle bundle = new Bundle();
			    bundle.putString("username", username);
			    bundle.putString("addpfid", pfid);
			    bundle.putString("imgurl", imgurl);
			    bundle.putString("tag", "groupinfo");
			    
			    bundle.putString("account", account);
			    bundle.putString("sex", sex);
			    bundle.putString("area", area);
			    bundle.putString("signature", signature);
			    bundle.putString("companyid", companyid);
			    bundle.putString("storeids", storeids);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			}
			else
			{
				String pkid = (String)mapdata.get("pkid");
				Intent intent = new Intent();
			    intent.setClass( this,FriendInfoViewActivity.class);
			    Bundle bundle = new Bundle();
			    bundle.putString("username", username);
			    bundle.putString("addpfid", pfid);
			    bundle.putString("imgurl", imgurl);
			    bundle.putString("pkid", pkid);
			    bundle.putString("tag", "groupinfo");
			    
			    bundle.putString("account", account);
			    bundle.putString("sex", sex);
			    bundle.putString("area", area);
			    bundle.putString("signature", signature);
			    String isYn = "";
			    if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
				{
					isYn = "0";
				}
				else
				{
					isYn = "1";
				}
			    bundle.putString("isYn",isYn);
				intent.putExtras(bundle);
				startActivity(intent);//开始界面的跳转函数
			}
			overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	private class ImageGridAdapter extends BaseAdapter{
        private Context context;
        private int resource;
        private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
        private List<ImageView> deletelist = new ArrayList<ImageView>();
        private RoundAngleImageView delimg;
        private RoundAngleImageView addimg;
        private int isshow = 1;
        
        //步骤4.1：编写adapter的构造函数
        ImageGridAdapter( Context context,List<Map<String,Object>> data, int resource,int isshow){
                this.context = context;
                this.resource = resource;
                this.mDlist = data;
                this.isshow = isshow;
                
                Map<String,Object> map = new HashMap<String,Object>();
				map.put("userid", "add");
				map.put("username", "");
				mDlist.add(map);
				
				Map<String,Object> map2 = new HashMap<String,Object>();
				map2.put("userid", "delete");
				map2.put("username", "");
				mDlist.add(map2);
				
				int size = mDlist.size();
				int yushu = 4 - (size % 4);
				if(yushu != 4)
				for(int j=0; j<yushu;j++)
				{
					Map<String,Object> maps = new HashMap<String,Object>();
					maps.put("userid", "");
					maps.put("username", "");
					mDlist.add(maps);
				}
				
        }
        
        public void showdeletebutton()
        {
        	for(int i=0;i<deletelist.size();i++)
        	{
        		ImageView img = deletelist.get(i);
        		img.setVisibility(View.VISIBLE);
        	}
        	
        	addimg.setImageResource(R.drawable.tongming_img);
        	delimg.setImageResource(R.drawable.tongming_img);
        	isshow = 0;
        	ChatMessageInfoActivity.instance.isshow = 0;
        }
        
        public void hidedeletebutton()
        {
        	for(int i=0;i<deletelist.size();i++)
        	{
        		ImageView img = deletelist.get(i);
        		img.setVisibility(View.INVISIBLE);
        	}
        	
        	delimg.setImageResource(R.drawable.roominfo_delete_btn);
        	addimg.setImageResource(R.drawable.roominfo_add_btn);
        	isshow = 1;
        	ChatMessageInfoActivity.instance.isshow = 1;
        }
        
        //步骤4.2：重写getView()，对每个单元的内容以及UI格式进行描述
        /*如果我们不使用TextView，则我们必须通过getView()对每一个gridview单元进行描述。这些单元可以是Button，ImageView，在这里我们使用Button和TextView分别作测试 重写override getView(int, View, ViewGroup)，返回任何我们所希望的view。*/
        public View getView  (final int position, View  convertView, ViewGroup  parent){
        	ViewHolder1 viewHolder1 = null;
        	Map<String,Object> map = mDlist.get(position);
        	final String userid = (String)map.get("userid");
        	final String name = (String)map.get("username");
            //我们测试发现，除第一个convertView外，其余的都是NULL，因此如果没有view，我们需要创建
            if(convertView == null){
            	convertView = LayoutInflater.from(context).inflate(resource, null);
            	viewHolder1 = new ViewHolder1();
				viewHolder1.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
				viewHolder1.name_txt = (TextView)convertView.findViewById(R.id.name_txt);
				viewHolder1.delete_btn = (ImageView)convertView.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder1);
            }else{
    			//有convertView，按样式，取得不用的布局
            	viewHolder1 = (ViewHolder1) convertView.getTag();
    		}
            
           
            if(userid.equals("add"))
            {
            	viewHolder1.iv_userhead.setImageResource(R.drawable.roominfo_add_btn);
            	addimg = viewHolder1.iv_userhead;
            	if(isshow == 0)
            		viewHolder1.iv_userhead.setImageResource(R.drawable.tongming_img);
            	viewHolder1.iv_userhead.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(isshow == 1)
						{
							ChatMessageInfoActivity.instance.openAddGroup();
						}
						else
							ChatMessageInfoActivity.instance.saImageItems.hidedeletebutton();
					}
				});
            }
            else if(userid.equals("delete"))
            {
            	viewHolder1.iv_userhead.setImageResource(R.drawable.roominfo_delete_btn);
            	delimg = viewHolder1.iv_userhead;
            	if(isshow == 0)
            		viewHolder1.iv_userhead.setImageResource(R.drawable.tongming_img);
            	viewHolder1.iv_userhead.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(isshow == 1)
						{
							if(mDlist.size() == 4)
							{
								ChatMessageInfoActivity.instance.makeText(getString(R.string.hotel_label_170));
							}
							else
								ChatMessageInfoActivity.instance.saImageItems.showdeletebutton();
						}
						else
							ChatMessageInfoActivity.instance.saImageItems.hidedeletebutton();
					}
				});
            }
            else if(userid.equals(""))
            {
            	viewHolder1.iv_userhead.setImageResource(R.drawable.tongming_img);
            	viewHolder1.iv_userhead.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ChatMessageInfoActivity.instance.saImageItems.hidedeletebutton();
					}
				});
            }
            else
            {
            	if(isshow == 0)
                {
                	viewHolder1.delete_btn.setVisibility(View.VISIBLE);
                }
            	
            	deletelist.add(viewHolder1.delete_btn);
	            String furl = fileUtil.getImageFile2aPath(userid, userid);
				if(!fileUtil.isFileExist3(furl))
				{
					if(!fileUtil.isFileExist2(userid))
						fileUtil.createUserFile(userid);
//	    			String url = Douban.IMG_BASE_URL + (String)map.get("mysendname")+".jpg";
					String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+userid;
	    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
	    			if(bmpsimg != null)
	    			{
						myapp.saveMyBitmap(furl, bmpsimg);
						viewHolder1.iv_userhead.setImageBitmap(bmpsimg);
	    			}
				}
				else
				{
					Bitmap bmpsimg = myapp.getLoacalBitmap(furl);
					viewHolder1.iv_userhead.setImageBitmap(bmpsimg);
				}
				viewHolder1.iv_userhead.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Map<String,Object> map = mDlist.get(position);
						if(map != null)
							ChatMessageInfoActivity.instance.openFriendDetail(map);
					}
				});
				
				viewHolder1.iv_userhead.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						ChatMessageInfoActivity.instance.saImageItems.showdeletebutton();
						return true;
					}
				});
				
				viewHolder1.name_txt.setText(name);
				
				viewHolder1.delete_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(userid.equals(myapp.getPfprofileId()))
						{
							ChatMessageInfoActivity.instance.makeText(getString(R.string.hotel_label_171));
						}
						else
							deleteGroupUser(userid,name);
					}
				});
            }
            return convertView;
        }
        
        class ViewHolder1{
        	RoundAngleImageView iv_userhead;
        	TextView name_txt;
        	ImageView delete_btn;
    	}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mDlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
    }// End of class FunnyLookingAdapter
	
	public void showMyLoadingDialog(String message)
    {
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
