package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.ContactAdapter2;
import ms.globalclass.listviewadapter.SideBar;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends Activity{

	private ListView lvContact;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
//	private static String[] nicks;
//	private static String[] nicks = {"阿雅","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
//	private static String[] userimgs;
	private static List<Map<String,Object>> dlist;
	
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static ContactActivity instance = null;
	private ContactAdapter2 contactAdapter;
	private static DBHelperMessage db;
	public String mykey = "";
	public static String isYn;//0加载内部员工，1加载外部好友
	private int activitystart = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_contact_view);
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        myapp = (MyApp)this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		TextView title_lable = (TextView)findViewById(R.id.title_lable);
		if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
		{
			isYn = "0";
			title_lable.setText(getString(R.string.hotel_label_71));
		}
		else
		{
			isYn = "1";
			title_lable.setText(getString(R.string.hotel_label_72));
		}
		
//		db.openDB();
//		db.deleteStroeInfoAll();
//		db.closeDB();
		
		activitystart++;
		
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
    	activitystart++;
    	
    	if(activitystart > 2)
    	{
    		Log.e("TAG-onPause", "onResume()------------yinliang");
    		getMyFriendDatas(isYn);//加载公司员工数据
    	}
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
	    intent.setClass( this,FriendsAddedActivity.class);
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
						else if(jobj.getString("tag").equals("success"))
						{
							storelist = new ArrayList<Map<String,Object>>();
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
						else if(jobj.getString("tag").equals("success"))
						{
							storelist = new ArrayList<Map<String,Object>>();
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
    
    public void updateFriendAllFileUrlData(final String pfid,final String furl)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				
				try {
					db.openDB();
					db.updateFriendAllFileUrlData(pfid,furl);
					db.closeDB();
					
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
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
					contactAdapter = new ContactAdapter2(ContactActivity.this,list3,R.layout.contact_item,R.layout.store_search_view,R.layout.contact_friend_item,
							new String[] { "sortPinyin", "imgurl","storeName"},
							new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn);
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
								openFriendView(username,pfid,imgurl,pkid,account,sex,area,signature);
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
		contactAdapter = new ContactAdapter2(ContactActivity.this,myapp.getMyCardsAll(),R.layout.contact_item,R.layout.store_search_view,R.layout.contact_friend_item,
				new String[] { "sortPinyin", "imgurl","storeName"},
				new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn);
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
        mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
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
//					if(contactAdapter != null)
//						contactAdapter.removeImageView(sart,end);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});

//        getMyCardListData();
//        getMyStoreListDatas();//加载门店数据
        getMyFriendDatas(isYn);//加载公司员工数据
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
//    		Intent intent = new Intent();
//        	intent.setClass(ContactActivity.this,Exit.class);
//        	startActivity(intent);
    		MainTabActivity.instance.onMinimizeActivity();
    	}
    	return false;
    }
    
}
