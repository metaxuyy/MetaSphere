package ms.activitys.hotel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.listviewadapter.GroupContactAdapter;
import ms.globalclass.listviewadapter.SideBar;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupContactActivity extends Activity{

	private ListView lvContact;
	private SideBar indexBar;
//	private WindowManager mWindowManager;
	private TextView mDialogText;
//	private static String[] nicks;
//	private static String[] nicks = {"阿雅","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
//	private static String[] userimgs;
	private static List<Map<String,Object>> dlist;
	
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static GroupContactActivity instance = null;
	private GroupContactAdapter contactAdapter;
	private static DBHelperMessage db;
	public String mykey = "";
	public static String isYn;//0加载内部员工，1加载外部好友
	
	private Button addFriendBtn;
	private LinearLayout addFriendLayout;
	private ArrayList<Map<String, String>> selFriendArr;
	private ArrayList<CheckBox> selFriendCBox;
	private static FileUtils fileUtil = new FileUtils();
	private String groupid;
	private String actiontag = "";
	private String gids = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_contact_add);
//        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        myapp = (MyApp)this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
		{
			isYn = "0";
		}
		else
		{
			isYn = "1";
		}
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("tag"))
			actiontag = bunde.getString("tag");
		if(bunde.containsKey("groupid"));
			groupid = bunde.getString("groupid");
		if(bunde.containsKey("gids"))
			gids = bunde.getString("gids");
		if(bunde.containsKey("friendname"))
		{
			String friendname = bunde.getString("friendname");
			String friendid = bunde.getString("friendid");
			Map<String, String> map = new HashMap<String, String>();
	    	map.put("username", friendname);
	    	map.put("pfid", friendid);
	    	if(selFriendArr == null)
	    		selFriendArr = new ArrayList<Map<String,String>>();
	    	selFriendArr.add(map);
		}
		
		Button breakbtn = (Button)findViewById(R.id.home);
		breakbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(actiontag.equals("messageinfo"))
	    		{
	    			Intent intent = new Intent();
		        	intent.setClass(GroupContactActivity.this,ChatMessageInfoActivity.class);
		        	startActivity(intent);
		        	GroupContactActivity.this.finish();
		    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
	    		}
				else if(actiontag.equals("messageinfodan"))
	    		{
	    			Intent intent = new Intent();
		        	intent.setClass(GroupContactActivity.this,ChatMessageInfoActivity.class);
		        	startActivity(intent);
		        	GroupContactActivity.this.finish();
		    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
	    		}
				else
				{
					Intent intent = new Intent();
		        	intent.setClass(GroupContactActivity.this,MainTabActivity.class);
		        	startActivity(intent);
		    		overridePendingTransition(R.anim.slide_down_out,R.anim.fade);
		    		GroupContactActivity.this.finish();
				}
			}
		});
		
//		db.openDB();
//		db.deleteStroeInfoAll();
//		db.closeDB();
		showMyLoadingDialog();
		findView();
    }
    
    /** 
     * 暂停 
     */  
    @Override  
    protected void onPause() {  
        Log.i("TAG-onPause", "onPause()------------yins");  
//        contactAdapter.removeAllResources();
        System.gc();
        super.onPause();  
    }  
    
    @Override  
    public void onResume() {
    	super.onResume();
//    	showMyLoadingDialog();
//		findView();
    	if(contactAdapter != null)
    	{
	    	int viewibility = MainTabActivity.instance.new_yanzhen_number.getVisibility();
	    	if(viewibility == View.VISIBLE)
	    	{
	    		String number = MainTabActivity.instance.new_yanzhen_number.getText().toString();
	    		this.setNewNumberTxtValue(number);
	    	}
    	}
    }
    
    public void add_friends(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,FindFriendActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("tag", "main");
		bundle.putString("isYn", isYn);
		intent.putExtras(bundle);
//		startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		MainTabActivity.instance.loadLeftActivity(intent);
	}
    
    public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
    
    public void refreshStroeInfoList(View v)
    {
    	showMyLoadingDialog();
    	getMyCardListData();
    }
    
    public void getMyCardListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
						JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> list = myapp.getMyCardList(jArr);
							db.openDB();
							db.deleteStroeInfoAll();
							db.saveStoreInfoAllData(list);
							List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
//							storelist.add(0,new HashMap<String,Object>());
							myapp.setMyCardsAll(storelist);
//							System.out.println("datajson=="+storelist.toString());
//							Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());ddd
							db.closeDB();
							msg.obj = storelist;
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
    
    public void getMyStoreListData222222()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
//					List<Map<String,Object>> storelist = null;
//					db.openDB();
//					storelist = db.getStoreInfoAllData();
////					storelist.add(0,new HashMap<String,Object>());
//					myapp.setMyCardsAll(storelist);
//					db.closeDB();
					
//					Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());ddd
					db.openDB();
					List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
					myapp.setMyCardsAll(storelist);
					db.closeDB();
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void getMyStoreListDatas()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
					storelist = db.getStoreInfoAllData("");
//					storelist.add(0,new HashMap<String,Object>());
					db.closeDB();
					
					if(storelist != null && storelist.size() > 0)
					{
						myapp.setMyCardsAll(storelist);
//						Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());ddd
						msg.obj = storelist;
					}
					else
					{
						JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> list = myapp.getMyCardList(jArr);
							db.openDB();
							db.saveStoreInfoAllData(list);
							storelist = db.getStoreInfoAllData("");
//							storelist.add(0,new HashMap<String,Object>());
							myapp.setMyCardsAll(storelist);
//							Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());ddd
							db.closeDB();
							msg.obj = storelist;
						}
						else
						{
							msg.obj = null;
						}
					}
//					if(storelist == null)
//						storelist = new ArrayList<Map<String,Object>>();
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void getMyFriendDatas(final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
//					storelist = db.getFriendAllData("",tag);
//					if(storelist != null && storelist.size() > 0)
//					{
//						
//					}
					Map<String,Object> friendsidmap = db.getFriendAllIdData("",tag);
					if(friendsidmap != null)
					{
						JSONObject jobj = api.selectMyFriendList(tag);
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jarry = jobj.getJSONArray("data");
							List<Map<String,Object>> dlist = myapp.getMyFriendGuolvList(jarry,friendsidmap);
							db.saveFriendAllData(dlist);
							storelist = db.getFriendAllData("",tag);
						}
					}
					else
					{
						JSONObject jobj = api.selectMyFriendList(tag);
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jarry = jobj.getJSONArray("data");
							List<Map<String,Object>> dlist = myapp.getMyFriendList(jarry);
							db.saveFriendAllData(dlist);
							storelist = db.getFriendAllData("",tag);
						}
						
					}
					myapp.setFriendlist(storelist);
					db.closeDB();
					
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void getMyStoreListDatas(final String key,final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
					storelist = db.getFriendAllData(key,tag);
					myapp.setFriendlist(storelist);
					db.closeDB();
					
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void getMyStoreListDatas(final String key)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
					storelist = db.getStoreInfoAllData(key);
//					storelist.add(0,new HashMap<String,Object>());
//					myapp.setMyCardsAll(storelist);
//					if(storelist != null && storelist.size() > 0)
//						Collections.sort(storelist, new PinyinComparator());ddd
					db.closeDB();
					
//					if(storelist == null)
//						storelist = new ArrayList<Map<String,Object>>();
					msg.obj = storelist;
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
//					dlist = myapp.getMyCardsAll();
					contactAdapter = new GroupContactAdapter(GroupContactActivity.this,list3,R.layout.contact_group_item_view,R.layout.store_search_view,R.layout.group_contact_item,
							new String[] { "sortPinyin", "imgurl","storeName"},
							new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn,gids);
					lvContact.setAdapter(contactAdapter);
					
					lvContact.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if(arg2 > 1)
							{
//								Map<String,Object> map = (Map<String,Object>)contactAdapter.getItem(arg2);
//								String storeid = (String)map.get("storeid");
//								openStoreView(storeid);
								Map<String,Object> map = (Map<String,Object>)contactAdapter.getItem(arg2);
								String username = (String)map.get("username");
								String pfid = (String)map.get("pfid");
								String imgurl = (String)map.get("userimg");
								String pkid = (String)map.get("pkid");
								
								String account = (String)map.get("account");
								String sex = (String)map.get("sex");
								String area = (String)map.get("area");
								String signature = (String)map.get("signature");
								//openFriendView(username,pfid,imgurl,pkid,account,sex,area,signature);
								
								if(gids != null && !gids.equals(""))
								{
									String [] gidstrs = gids.split(",");
									if(gids.indexOf(pfid) >= 0)
									{
										
									}
									else
									{
										if(selFriendArr!=null && selFriendArr.size() + gidstrs.length >=10){
											Toast.makeText(GroupContactActivity.this, "最多只能增加10个好友群聊", Toast.LENGTH_LONG).show();
											return;
										}
										
										CheckBox selCBox = (CheckBox) ((LinearLayout)((LinearLayout)arg1).getChildAt(1)).getChildAt(2);
										if(selCBox.isChecked()){
											selCBox.setChecked(false);
											addSelFriend(false, pfid, username, null, selCBox);
										}else{
											selCBox.setChecked(true);
											RoundAngleImageView userImg = (RoundAngleImageView) ((LinearLayout)((LinearLayout)arg1).getChildAt(1)).getChildAt(0);
											userImg.setDrawingCacheEnabled(true);
											Bitmap bitmap = Bitmap.createBitmap(userImg.getDrawingCache());
			                                userImg.setDrawingCacheEnabled(false);
			                                addSelFriend(true, pfid, username, bitmap, selCBox);
										}
									}
								}
								else
								{
									if(selFriendArr!=null && selFriendArr.size()==10){
										Toast.makeText(GroupContactActivity.this, "最多只能增加10个好友群聊", Toast.LENGTH_LONG).show();
										return;
									}
									
									CheckBox selCBox = (CheckBox) ((LinearLayout)((LinearLayout)arg1).getChildAt(1)).getChildAt(2);
									if(selCBox.isChecked()){
										selCBox.setChecked(false);
										addSelFriend(false, pfid, username, null, selCBox);
									}else{
										selCBox.setChecked(true);
										RoundAngleImageView userImg = (RoundAngleImageView) ((LinearLayout)((LinearLayout)arg1).getChildAt(1)).getChildAt(0);
										userImg.setDrawingCacheEnabled(true);
										Bitmap bitmap = Bitmap.createBitmap(userImg.getDrawingCache());
		                                userImg.setDrawingCacheEnabled(false);
		                                addSelFriend(true, pfid, username, bitmap, selCBox);
									}
								}
							}
							else if(arg2 == 1)//打开新的朋友列表
							{
								openNewFriendView();
							}
//							else if(arg2 == 2)//打开好友列表
//							{
//								openFriendView();
//							}
						}
					});
					
					indexBar.setListView(lvContact);
				}
				else
				{
					
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				List<Map<String, Object>> list = (List<Map<String, Object>>)msg.obj;
				if(list != null)
				{
					contactAdapter.notifyDataSetChanged();
				}
				break;
			case 2:
				Map<String,Object> gmap = (Map<String,Object>)msg.obj;
				if(gmap != null)
				{
					String groupid = (String)gmap.get("gid");
					String gname = (String)gmap.get("gname");
					Map<String,Object> newmap = (Map<String,Object>)gmap.get("newmessage");
					if(selFriendArr.size() == 1)
					{
						showMessage(groupid,gname);
					}
					else
					{
						String groupids = (String)gmap.get("groupids");
						openMessageView(groupid,gname,groupids);
					}
					
					if(MessageListActivity.instance != null)
					{
						MessageListActivity.instance.mymessageItem.add(newmap);
						MessageListActivity.instance.myAdapter.notifyDataSetChanged();
					}
					
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 3:
				Map<String,Object> gmap2 = (Map<String,Object>)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(gmap2 != null)
				{
					Map<String,Object> newmap = (Map<String,Object>)gmap2.get("newmessage");
					
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					
					if(ChatMessageInfoActivity.instance != null)
						ChatMessageInfoActivity.instance.getGridViewDatas();
					
					if(MessageListActivity.instance != null)
					{
						MessageListActivity.instance.mymessageItem.add(newmap);
						MessageListActivity.instance.myAdapter.notifyDataSetChanged();
					}
					
					if(actiontag.equals("messageinfo"))
		    		{
		    			Intent intent = new Intent();
			        	intent.setClass(GroupContactActivity.this,ChatMessageInfoActivity.class);
			        	startActivity(intent);
			        	GroupContactActivity.this.finish();
			    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
		    		}
				}
				break;
			}
		}
	};
	
	public void setNewNumberTxtValue(String number)
	{
		contactAdapter.getNewNumberTxt().setVisibility(View.VISIBLE);
		contactAdapter.getNewNumberTxt().setText(number);
	}
	
	public void setNewNumberTxtVisibility()
	{
		contactAdapter.getNewNumberTxt().setVisibility(View.GONE);
	}
	
	public void listItemRefresh()
	{
//		dlist = myapp.getMyCardsAll();
//		((ContactAdapter) lvContact.getAdapter()).getRefresh(dlist);
//		myapp.getMyCardsAll().remove(0);
		lvContact.setAdapter(null);
		contactAdapter = new GroupContactAdapter(GroupContactActivity.this,myapp.getMyCardsAll(),R.layout.contact_group_item_view,R.layout.store_search_view,R.layout.group_contact_item,
				new String[] { "sortPinyin", "imgurl","storeName"},
				new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn,gids);
		lvContact.setAdapter(contactAdapter);
	}
	
	public void openStoreView(String storeids)
	{
		try{
			int index = 0;
			for(int i=0;i<myapp.getMyCardsAll().size();i++)
			{
				Map<String,Object> map = myapp.getMyCardsAll().get(i);
				String storeid = (String)map.get("storeid");
				if(storeids.equals(storeid))
				{
					index = i;
					break;
				}
			}
			Intent intent = new Intent();
		    intent.setClass( this,StoreViewActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
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
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openFriendView(String username,String addpfid,String imgurl,String pkid,String account,String sex,String area,String signature)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,FriendInfoViewActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("username", username);
		    bundle.putString("addpfid", addpfid);
		    bundle.putString("imgurl", imgurl);
		    bundle.putString("pkid", pkid);
		    
		    bundle.putString("account", account);
		    bundle.putString("sex", sex);
		    bundle.putString("area", area);
		    bundle.putString("signature", signature);
		    bundle.putString("tag","main");
		    bundle.putString("isYn",isYn);
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openNewFriendView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,InformationFriendActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    private void findView(){
    	lvContact = (ListView)this.findViewById(R.id.lvContact);
    	indexBar = (SideBar) findViewById(R.id.sideBar);  
//        indexBar.setListView(lvContact); 
//        mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
    	mDialogText = (TextView)findViewById(R.id.diogtext);
        mDialogText.setVisibility(View.INVISIBLE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        
        lvContact.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
				{
					int sart = lvContact.getFirstVisiblePosition();
					int end = lvContact.getLastVisiblePosition();
//					System.out.println("sart=="+sart+"end===="+end);
					if(contactAdapter != null)
						contactAdapter.removeImageView(sart,end);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
        
        addFriendLayout = (LinearLayout) this.findViewById(R.id.add_layout);
        addFriendBtn = (Button) this.findViewById(R.id.add_btn);
        addFriendBtn.setEnabled(false);
        addFriendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyLoadingDialog();
				if(actiontag == null || actiontag.equals(""))
				{
					addMessageGroup();
				}
				else if(actiontag.equals("messageinfo"))
				{
					updateMessageGroup();
				}
				else if(actiontag.equals("messageinfodan"))
				{
					addMessageGroup();
				}
			}
		});

//        getMyCardListData();
//        getMyStoreListDatas();//加载门店数据
        getMyFriendDatas(isYn);//加载公司员工数据
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		if(actiontag.equals("messageinfo"))
    		{
    			Intent intent = new Intent();
	        	intent.setClass(GroupContactActivity.this,ChatMessageInfoActivity.class);
	        	startActivity(intent);
	    		this.finish();
	    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
    		}
    		else if(actiontag.equals("messageinfodan"))
    		{
    			Intent intent = new Intent();
	        	intent.setClass(GroupContactActivity.this,ChatMessageInfoActivity.class);
	        	startActivity(intent);
	        	GroupContactActivity.this.finish();
	    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
    		}
    		else
    		{
	    		Intent intent = new Intent();
	        	intent.setClass(GroupContactActivity.this,MainTabActivity.class);
	        	startActivity(intent);
	    		this.finish();
	    		overridePendingTransition(R.anim.slide_down_out, R.anim.faded_out);
    		}
    	}
    	return false;
    }
    
    private void addSelFriend(boolean isAdd, String pfid, String userName, Bitmap bitmap, CheckBox cBox){
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("username", userName);
    	map.put("pfid", pfid);
    	
    	if(isAdd){
    		LinearLayout.LayoutParams sp_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		sp_params.leftMargin = 15;
    		ImageView imgChild = new ImageView(this);
        	imgChild.setImageBitmap(bitmap);
//        	imgChild.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	imgChild.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
	        		int delImgIndex = addFriendLayout.indexOfChild(v);
	        		int delIndex = selFriendArr.size()-1-delImgIndex;
	        		addFriendLayout.removeViewAt(delImgIndex);
	        		selFriendArr.remove(delIndex);
	        		
	        		((CheckBox)selFriendCBox.get(delIndex)).setChecked(false);
	        		selFriendCBox.remove(delIndex);
				}
			});
        	
        	if(selFriendArr==null){
        		selFriendArr = new ArrayList<Map<String, String>>();
        	}
        	
        	selFriendArr.add(map);
        	imgChild.setLayoutParams(sp_params);
        	addFriendLayout.addView(imgChild, 0);
        	
        	if(selFriendCBox==null){
        		selFriendCBox = new ArrayList<CheckBox>();
        	}
        	selFriendCBox.add(cBox);
    	}else{
    		if(selFriendArr!=null){
    			int delIndex = selFriendArr.indexOf(map);
    			int delImgIndex = addFriendLayout.getChildCount()-1-1-delIndex;
        		addFriendLayout.removeViewAt(delImgIndex);
        		selFriendArr.remove(delIndex);
        		selFriendCBox.remove(delIndex);
    		}
    	}
    	
    	if(selFriendArr.size()>0){
    		addFriendBtn.setEnabled(true);
    		addFriendBtn.setBackgroundResource(R.drawable.title_btn_blure);
    	}else{
    		addFriendBtn.setEnabled(false);
    		addFriendBtn.setBackgroundResource(R.drawable.mm_title_act_btn_disable);
    	}
    }
    
    public void addMessageGroup()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					List<Map<String,Object>> storelist = null;
					StringBuffer sbnames = new StringBuffer();
					StringBuffer sbids = new StringBuffer();
					if(selFriendArr.size() > 1)
					{
						Map<String, String> map = new HashMap<String, String>();
				    	map.put("username", myapp.getUserName());
				    	map.put("pfid", myapp.getUserNameId());
				    	selFriendArr.add(map);
				    	
				    	for(int i=0;i<selFriendArr.size();i++)
						{
							Map<String, String> maps = selFriendArr.get(i);
							String pfid = maps.get("pfid");
							String names = maps.get("username");
							if(i == 0)
							{
								sbnames.append(names);
								sbids.append(pfid);
							}
							else
							{
								sbnames.append(","+names);
								sbids.append(","+pfid);
							}
						}
						
						JSONObject jobj = api.andGroupMessageData(sbnames.toString(),sbids.toString());
						if(jobj != null && jobj.has("groupid"))
						{
							String groupid = jobj.getString("groupid");
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
//							mapdata.put("mymessagecontent", "你邀请了"+sbnames.toString()+"加入了群聊");
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
							newmap.put("mymessagecontent", "你邀请了"+sbnames.toString()+"加入了群聊");
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
							newmap.put("messagestart", "");
							newmap.put("isGroup", "0");
							newmap.put("watag", "app");
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
						for(int i=0;i<selFriendArr.size();i++)
						{
							Map<String, String> maps = selFriendArr.get(i);
							String pfid = maps.get("pfid");
							String names = maps.get("username");
							if(i == 0)
							{
								sbnames.append(names);
								sbids.append(pfid);
							}
							else
							{
								sbnames.append(","+names);
								sbids.append(","+pfid);
							}
						}
						
						Map<String,String> rmap = new HashMap<String,String>();
						rmap.put("gname", sbnames.toString());
						rmap.put("gid", sbids.toString());
						rmap.put("groupids", sbids.toString());
						msg.obj = rmap;
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
    
    public void updateMessageGroup()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				
				try {
					List<Map<String,Object>> storelist = null;
					StringBuffer sbnames = new StringBuffer();
					StringBuffer sbids = new StringBuffer();
					if(selFriendArr.size() > 0)
					{
//						Map<String, String> map = new HashMap<String, String>();
//				    	map.put("username", myapp.getUserName());
//				    	map.put("pfid", myapp.getUserNameId());
//				    	selFriendArr.add(map);
				    	
				    	for(int i=0;i<selFriendArr.size();i++)
						{
							Map<String, String> maps = selFriendArr.get(i);
							String pfid = maps.get("pfid");
							String names = maps.get("username");
							if(i == 0)
							{
								sbnames.append(names);
								sbids.append(pfid);
							}
							else
							{
								sbnames.append(","+names);
								sbids.append(","+pfid);
							}
						}
				    	String groupids = gids + "," + sbids.toString();
						
						JSONObject jobj = api.updateGroupMessageData(sbnames.toString(),groupids,groupid);
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
//							mapdata.put("mymessagecontent", "你邀请了"+sbnames.toString()+"加入了群聊");
//							mapdata.put("mysendtime", sdf.format(new Date()));
//							mapdata.put("watag", "app");
//							mapdata.put("isRead", "0");
//							mapdata.put("groupimg", groupids);
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
							newmap.put("mymessagecontent", "你邀请了"+sbnames.toString()+"加入了群聊");
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
							newmap.put("groupimg", groupids);
							
							List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
							dlist.add(newmap);
							db.saveMessageData(dlist);
							
							db.closeDB();
							Map<String,Object> rmap = new HashMap<String,Object>();
							rmap.put("gname", sbnames.toString());
							rmap.put("gid", groupid);
							rmap.put("groupids", groupids);
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
    
    public void openMessageView(String groupid,String groupname,String groupids)
    {
    	try{
			String nameid = myapp.getPfprofileId();
			String storeid = groupid;
			String storeName = groupname;
			String typesMapping = "group";
			String username = myapp.getUserName();
			openMessageDetail(nameid,storeid,storeName,username,typesMapping,groupids);
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
    public void showMessage(String addpfid,String storename)
	{
		try{
			String nameid = myapp.getPfprofileId();
			String storeid = addpfid;
			String storeName = storename;
			String typesMapping = "friend";
			String username = myapp.getUserName();
			fileUtil.createUserFile(storeid);
//			getAddFriend();
			openMessageDetail(nameid,storeid,storeName,username,typesMapping,"");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    public void openMessageDetail(String nameid,String storeid,String storeName,String username,String typesMapping,String imgurl)
	{
		try{
			if(MessageListActivity.instance != null)
				MessageListActivity.instance.finish();
			Intent intent = new Intent();
		    intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("nameid", nameid);
			bundle.putString("storeid", storeid);
			bundle.putString("storeName", storeName);
			bundle.putString("username", username);
			bundle.putString("typesMapping", typesMapping);
			bundle.putString("storeimg", null);
			bundle.putInt("index", 0);
			bundle.putString("tag", "group");
			bundle.putString("groupimg",imgurl);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
