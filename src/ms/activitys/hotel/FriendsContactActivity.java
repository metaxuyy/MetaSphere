package ms.activitys.hotel;

import java.util.Arrays;
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
import ms.globalclass.listviewadapter.ContactAdapter2;
import ms.globalclass.listviewadapter.FriendContactAdapter;
import ms.globalclass.listviewadapter.PinyinComparator;
import ms.globalclass.listviewadapter.SideBar;
import ms.globalclass.map.MyApp;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class FriendsContactActivity extends Activity{

	private ListView lvContact;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private static List<Map<String,Object>> dlist;
	
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static FriendsContactActivity instance = null;
	private FriendContactAdapter contactAdapter;
	private static DBHelperMessage db;
	public String mykey = "";
	public static String isYn;//0加载内部员工，1加载外部好友
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_friend_contact_view);
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        myapp = (MyApp)this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		TextView title_lable = (TextView)findViewById(R.id.title_lable);
		if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
		{
			isYn = "1";
			title_lable.setText(getString(R.string.hotel_label_72));
		}
		else
		{
			isYn = "0";
			title_lable.setText(getString(R.string.hotel_label_71));
		}
		
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
    }  
    
    public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
    
    public void getMyStoreListData(final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					db.openDB();
					List<Map<String,Object>> storelist = db.getFriendAllData("",tag);
					if(storelist != null && storelist.size() > 0)
					{
						
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
    
    public void getMyStoreListDatas(final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
					storelist = db.getFriendAllData("",tag);
					if(storelist != null && storelist.size() > 0)
					{
						
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
    
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> list3 = (List<Map<String, Object>>)msg.obj;
				if(list3 != null)
				{
//					dlist = myapp.getMyCardsAll();
					dlist = list3;
					contactAdapter = new FriendContactAdapter(FriendsContactActivity.this,list3,R.layout.store_search_view,R.layout.contact_item,
							new String[] { "namePinyin", "userimg","username"},
							new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn);
					lvContact.setAdapter(contactAdapter);
					
					lvContact.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if(arg2 > 0)
							{
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
	
	public void listItemRefresh()
	{
		lvContact.setAdapter(null);
		contactAdapter = new FriendContactAdapter(FriendsContactActivity.this,myapp.getFriendlist(),R.layout.store_search_view,R.layout.contact_item,
				new String[] { "namePinyin", "userimg","username"},
				new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn);
		lvContact.setAdapter(contactAdapter);
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
		    bundle.putString("tag","contactlist");
		    bundle.putString("isYn",isYn);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    private void findView(){
    	try{
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
	        
	        
	        Button break_btn = (Button)findViewById(R.id.break_btn);
	        break_btn.setVisibility(View.VISIBLE);
	        break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openStroeContentView();
				}
			});
	
	        getMyStoreListDatas(isYn);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
    		openStroeContentView();
    	}
    	return false;
    }
    
    public void openStroeContentView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    instance = null;
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    public void add_friends(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,FindFriendActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("tag", "content");
		bundle.putString("isYn", isYn);
		intent.putExtras(bundle);
		startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
}
