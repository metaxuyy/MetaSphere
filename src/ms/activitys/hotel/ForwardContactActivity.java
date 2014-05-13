package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import ms.globalclass.listviewadapter.ContactAdapter2;
import ms.globalclass.listviewadapter.ForwardContactAdapter;
import ms.globalclass.listviewadapter.SideBar;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ForwardContactActivity extends Activity{

	private ListView lvContact;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
//	private static String[] nicks;
//	private static String[] nicks = {"阿雅","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
//	private static String[] userimgs;
	private static List<Map<String,Object>> dlist;
	public static FileUtils fileUtil = new FileUtils();
	
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static ForwardContactActivity instance = null;
	private ForwardContactAdapter contactAdapter;
	private static DBHelperMessage db;
	public String mykey = "";
	public static String isYn;//0加载内部员工，1加载外部好友
	private int activitystart = 0;
	private String tag;
	private String messageContent;
	private String imgpath;
	private String storeDoc;
	private String storename;
	private String link;
	private Map<String,Object> currentmap;
	private Map<String,Object> newmap;
	private String voicpath;
	private String voicname;
	private String imgname;
	private String imgtype;
	
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
		
		ImageButton add_btn = (ImageButton)findViewById(R.id.add_btn);
		add_btn.setVisibility(View.GONE);
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setVisibility(View.VISIBLE);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMessageListView();
			}
		});
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("tag"))
			tag = bunde.getString("tag");
		if(tag.equals("text"))
		{
			if(bunde.containsKey("messageContent"))
				messageContent = bunde.getString("messageContent");
		}
		else if(tag.equals("amr"))
		{
			if(bunde.containsKey("voicpath"))
				voicpath = bunde.getString("voicpath");
			if(bunde.containsKey("voicname"))
				voicname = bunde.getString("voicname");
		}
		else if(tag.equals("img"))
		{
			if(bunde.containsKey("imgpath"))
				imgpath = bunde.getString("imgpath");
			if(bunde.containsKey("imgname"))
				imgname = bunde.getString("imgname");
			if(bunde.containsKey("imgtype"))
				imgtype = bunde.getString("fileType");
		}
		else if(tag.equals("imgtext"))
		{
			if(bunde.containsKey("imgpath"))
				imgpath = bunde.getString("imgpath");
			if(bunde.containsKey("imgname"))
				imgname = bunde.getString("imgname");
			if(bunde.containsKey("storeDoc"))
				storeDoc = bunde.getString("storeDoc");
			if(bunde.containsKey("storename"))
				storename = bunde.getString("storename");
			if(bunde.containsKey("link"))
				link = bunde.getString("link");
		}
		
//		db.openDB();
//		db.deleteStroeInfoAll();
//		db.closeDB();
		
		activitystart++;
		
		showMyLoadingDialog();
		findView();
    }
    
    public void forwardMessage()
    {
    	try{
    		String username = (String)currentmap.get("username");
			String pfid = (String)currentmap.get("pfid");
			String imgurl = (String)currentmap.get("userimg");
			String pkid = (String)currentmap.get("pkid");
			
			String account = (String)currentmap.get("account");
			String sex = (String)currentmap.get("sex");
			String area = (String)currentmap.get("area");
			String signature = (String)currentmap.get("signature");
			
			showMyLoadingDialog();
			if(tag.equals("text"))
			{
				sendMessageText(messageContent,pfid,username);
			}
			else if(tag.equals("amr"))
			{
				sendMessageVoice(pfid,username);
			}
			else if(tag.equals("img"))
			{
				sendMessageImage(pfid,username);
			}
			else if(tag.equals("imgtext"))
			{
				Map<String,String> map = new HashMap<String,String>();
				map.put("type", "link");
				map.put("storeDoc", storeDoc);
				map.put("storename", storename);
				map.put("link", link);
				JSONObject job = new JSONObject(map);
				String messagecontent = job.toString();
				sendMessageLink(pfid,username,messagecontent);
			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    private void sendMessageText(String content,String toname,String tname)
	{
		try{
			String mid = myapp.getCombID();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			newmap = new HashMap<String, Object>();
			newmap.put("mid", mid);
			newmap.put("toid", toname);
			newmap.put("storeid", toname);
			newmap.put("serviceid", "");
			newmap.put("servicename", "");
			newmap.put("sname", tname);
			newmap.put("nameid", myapp.getPfprofileId());
			newmap.put("mysendname", myapp.getPfprofileId());
			newmap.put("userimg", myapp.getUserimgbitmap());
			newmap.put("fname", myapp.getUserName());
			newmap.put("tname", tname);
			newmap.put("yiman", R.drawable.yi_man);
			newmap.put("mymessagecontent", content);
			newmap.put("mysendtime",sdf.format(new Date()));
			newmap.put("fileUrl","");
			newmap.put("fileUrl2","");
			newmap.put("toname", toname);
			newmap.put("fileType", "");
			newmap.put("fileType2", "");
			newmap.put("fileName", "");
			newmap.put("fileName2", "");
			newmap.put("time", "");
			newmap.put("timetext", "");
			newmap.put("messagetype", "friend");
			newmap.put("sendimg", "1");
			newmap.put("sendprogress", "0");
			newmap.put("storename", "");  
			newmap.put("storeDoc", "");
			newmap.put("storeimg", "");
			newmap.put("storelist", null);
			newmap.put("isRead", "0");
			newmap.put("messagestart", "");
			newmap.put("groupimg", "");
			
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			dlist.add(newmap);
			saveMessageLocal(dlist);
			
			sendFriendMessage(content,mid,myapp.getPfprofileId(), myapp.getUserName(),toname,tname);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    public void sendMessageVoice(String toname,String tname)
    {
    	try{
    		if(voicpath.indexOf("http") >= 0)
			{
    			voicpath = downloadArmData(voicpath,voicname,2,myapp.getPfprofileId(),toname);
			}
    		
    		String timestr = getRealPathFromURI(voicname);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		newmap = new HashMap<String, Object>();
    		String mid = myapp.getCombID();
    		newmap.put("mid", mid);
    		newmap.put("toid", toname);
    		newmap.put("storeid", toname);
    		newmap.put("serviceid", "");
    		newmap.put("servicename", "");
    		newmap.put("sname", tname);
    		newmap.put("nameid", myapp.getPfprofileId());
    		newmap.put("mysendname", myapp.getPfprofileId());
    		newmap.put("userimg", myapp.getUserimgbitmap());
    		newmap.put("fname", myapp.getUserName());
    		newmap.put("tname", tname);
    		newmap.put("yiman", R.drawable.yi_man);
    		newmap.put("mymessagecontent", "【"+getString(R.string.button_recordAudio)+"】");
    		newmap.put("mysendtime",sdf.format(new Date()));
    		newmap.put("fileUrl","");
    		newmap.put("fileUrl2",voicpath);
    		newmap.put("toname", toname);
    		newmap.put("fileType", "");
    		newmap.put("fileType2", "voice/amr");
    		newmap.put("fileName", "");
    		newmap.put("fileName2", voicname);
    		newmap.put("time", timestr);
    		newmap.put("timetext", timestr+"″");
    		newmap.put("messagetype", "friend");
    		newmap.put("sendimg", "1");
    		newmap.put("sendprogress", "0");
    		newmap.put("storename", "");  
    		newmap.put("storeDoc", "");
    		newmap.put("storeimg", "");
    		newmap.put("storelist", null);
    		newmap.put("isRead", "0");
    		newmap.put("messagestart", "");
    		newmap.put("groupimg", "");
    		
    		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    		dlist.add(newmap);
    		saveMessageLocal(dlist);
    		
    		Map<String, File> files = new HashMap<String, File>();
			File file2 = new File(voicpath);
			files.put(file2.getName(), file2);
			
    		sendMessageVoice(files,Integer.valueOf(timestr),"friend","voice/amr",mid,myapp.getPfprofileId(), myapp.getUserName(),toname,tname);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void sendMessageImage(String toname,String tname)
	{
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			newmap = new HashMap<String, Object>();
			String mid = myapp.getCombID();
			newmap.put("mid", mid);
			newmap.put("toid", toname);
			newmap.put("storeid", toname);
			newmap.put("serviceid", "");
			newmap.put("servicename", "");
			newmap.put("sname", tname);
			newmap.put("nameid", myapp.getPfprofileId());
			newmap.put("mysendname", myapp.getPfprofileId());
			newmap.put("userimg", myapp.getUserimgbitmap());
			newmap.put("fname", myapp.getUserName());
			newmap.put("tname", tname);
			newmap.put("yiman", R.drawable.yi_man);
			newmap.put("mymessagecontent", "【"+getString(R.string.button_takephoto)+"】");
			newmap.put("mysendtime",sdf.format(new Date()));
			newmap.put("fileUrl",imgpath);
			newmap.put("fileUrl2","");
			newmap.put("toname", toname);
			String type = imgname.substring(imgname.lastIndexOf(".")+1,imgname.length());
			newmap.put("fileType", type);
			newmap.put("fileType2", "");
			newmap.put("fileName", imgname);
			newmap.put("fileName2", "");
			newmap.put("time", "");
			newmap.put("timetext", "");
			newmap.put("messagetype", "friend");
			newmap.put("sendimg", "1");
			newmap.put("sendprogress", "0");
			newmap.put("storename", "");  
			newmap.put("storeDoc", "");
			newmap.put("storeimg", "");
			newmap.put("storelist", null);
			newmap.put("isRead", "0");
			newmap.put("messagestart", "");
			newmap.put("groupimg", "");
			
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			dlist.add(newmap);
			saveMessageLocal(dlist);
			
			Map<String, File> files = new HashMap<String, File>();
			File file2 = new File(imgpath);
			files.put(file2.getName(), file2);
			
			sendMessageImage(files,"friend",type,mid,myapp.getPfprofileId(), myapp.getUserName(),toname,tname,"【"+getString(R.string.button_takephoto)+"】");
		}catch(Exception ex){
			ex.printStackTrace();
			if(loadDialog != null)
				loadDialog.dismiss();
		}
	}
    
    public void sendMessageLink(String toname,String tname,String messageContent)
	{
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			newmap = new HashMap<String, Object>();
			String mid = myapp.getCombID();
			newmap.put("mid", mid);
			newmap.put("toid", toname);
			newmap.put("storeid", toname);
			newmap.put("serviceid", "");
			newmap.put("servicename", "");
			newmap.put("sname", tname);
			newmap.put("nameid", myapp.getPfprofileId());
			newmap.put("mysendname", myapp.getPfprofileId());
			newmap.put("userimg", myapp.getUserimgbitmap());
			newmap.put("fname", myapp.getUserName());
			newmap.put("tname", tname);
			newmap.put("yiman", R.drawable.yi_man);
			newmap.put("mymessagecontent", messageContent);
			newmap.put("mysendtime",sdf.format(new Date()));
			newmap.put("fileUrl",imgpath);
			newmap.put("fileUrl2","");
			newmap.put("toname", toname);
			String type = "";
			if(imgname.lastIndexOf(".") > 0)
				type = imgname.substring(imgname.lastIndexOf(".")+1,imgname.length());
			else
				type = "jpg";
			newmap.put("fileType", type);
			newmap.put("fileType2", "");
			newmap.put("fileName", imgname);
			newmap.put("fileName2", "");
			newmap.put("time", "");
			newmap.put("timetext", "");
			newmap.put("messagetype", "friend");
			newmap.put("sendimg", "1");
			newmap.put("sendprogress", "0");
			newmap.put("storename", "");  
			newmap.put("storeDoc", "");
			newmap.put("storeimg", "");
			newmap.put("storelist", null);
			newmap.put("isRead", "0");
			newmap.put("messagestart", "");
			newmap.put("groupimg", "");
			
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			dlist.add(newmap);
			saveMessageLocal(dlist);
			
			Map<String, File> files = new HashMap<String, File>();
			File file2 = new File(imgpath);
			files.put(file2.getName(), file2);
			
			sendMessageImage(files,"friend",type,mid,myapp.getPfprofileId(), myapp.getUserName(),toname,tname,messageContent);
		}catch(Exception ex){
			ex.printStackTrace();
			if(loadDialog != null)
				loadDialog.dismiss();
		}
	}
    
    public void sendFriendMessage(final String content,final String mid,final String fromname,final String fname,final String toname,final String tname)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(myapp.isInetnState())
					{
//						List<Map<String,Object>> list = myapp.getMyCardsAll();
//						final Map map = list.get(Integer.valueOf(index));
//						Integer points = (Integer)map.get("points");
						
						jobj = api.sendFriendsMessage(fromname,toname,fname,tname,"","2",content,0,"",myapp.isWifistart(),myapp.isInetnState(),mid);
						if(jobj == null)
						{
//							String messagestart = "";
//							if(jobj.has("isonline"))
//								messagestart = jobj.getString("isonline");
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", false);
							msg.obj = smap;
						}
						else
						{
//							jArr = (JSONArray) jobj.get("data");
//							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							msg.obj = list;
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", true);
							msg.obj = smap;
						}
					}
					else
					{
						Map<String,Object> smap = new HashMap<String,Object>();
						smap.put("start", false);
						msg.obj = smap;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void sendMessageVoice(final Map<String, File> files,final int time,final String messagetype,final String filetype,final String mid,final String fromname,final String fname,final String toname,final String tname)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				boolean b = false;
				long filesize = 0;
				try {
					if(myapp.isInetnState())
					{
						Map<String,String> params = new HashMap<String,String>();
						params.put("fromname", fromname);
						params.put("toname", toname);
						params.put("fname", fname);
						params.put("tname", tname);
						params.put("toneetyid", "");
						params.put("sendstatus", "2");
						params.put("content", "【"+getString(R.string.button_recordAudio)+"】");
						params.put("messagetype", messagetype);
						params.put("filetype", filetype);
						params.put("time", String.valueOf(time));
						params.put("mid", mid);
						b = api.uploadFilesmssage(params,files,"");
					}
					Map<String,Object> smap = new HashMap<String,Object>();
					smap.put("start", b);
					msg.obj = smap;
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void sendMessageImage(final Map<String, File> files,final String messagetype,final String filetype,final String mid,final String fromname,final String fname,final String toname,final String tname,final String content)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				boolean b = false;
				long filesize = 0;
				try {
					if(myapp.isInetnState())
					{
						Map<String,String> params = new HashMap<String,String>();
						params.put("fromname", fromname);
						params.put("toname", toname);
						params.put("fname", fname);
						params.put("tname", tname);
						params.put("toneetyid", "");
						params.put("sendstatus", "2");
						params.put("content", content);
						params.put("messagetype", messagetype);
						params.put("filetype", filetype);
						params.put("time", "");
						params.put("mid", mid);
						b = api.uploadFilesmssage(params,files,"");
					}
					Map<String,Object> smap = new HashMap<String,Object>();
					smap.put("start", b);
					msg.obj = smap;
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    public void updateMessageLocal(final Map<String,Object> map)
	{
		try{
//			new Thread(){
//				public void run(){
//					Message msg = new Message();
//					msg.what = 7;
					
					db.openDB();
					db.updateMessageData(map);
					db.closeDB();
					
//					handler.sendMessage(msg);
//				}
//			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    public String downloadArmData(final String fileUrl,final String fileName,final int tag,final String fusernameid,final String touserid)
	{
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				msg.what = 0;
				
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONArray jArr;
				JSONObject jobj;
				String filePath = null;
				try {
					if(tag == 1)
					{
						filePath = fileUtil.createVoice2File1a(fusernameid, fileUrl, fileName);
					}
					else
					{
						filePath = fileUtil.createVoiceFile1a(touserid, fileUrl, fileName);
					}
//					int result = 1;
//					if(filePath == null)
//						result = -1;
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("result", result);
//					map.put("textview", textview);
//					map.put("aim_layout", aim_layout);
//					map.put("fileName", fileName);
//					map.put("tag", tag);
//					msg.obj = map;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				return filePath;
//				handler.sendMessage(msg);
//			}
//		}.start();
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
    
//    @Override  
//    public void onResume() {
//    	super.onResume();
////    	showMyLoadingDialog();
////		findView();
//    	activitystart++;
//    	
//    	if(activitystart > 2)
//    	{
//    		Log.e("TAG-onPause", "onResume()------------yinliang");
//    		getMyFriendDatas(isYn);//加载公司员工数据
//    	}
//    	if(contactAdapter != null)
//    	{
//	    	int viewibility = MainTabActivity.instance.new_yanzhen_number.getVisibility();
//	    	if(viewibility == View.VISIBLE)
//	    	{
//	    		String number = MainTabActivity.instance.new_yanzhen_number.getText().toString();
//	    		this.setNewNumberTxtValue(number);
//	    	}
//    	}
//    }
    
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
    
    public void saveMessageLocal(final List<Map<String,Object>> dlist)
	{
		try{
//			new Thread(){
//				public void run(){
//					Message msg = new Message();
//					msg.what = 2;
					
					db.openDB();
					db.saveMessageData(dlist);
					db.closeDB();
					
//					handler.sendMessage(msg);
//				}
//			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
					contactAdapter = new ForwardContactAdapter(ForwardContactActivity.this,list3,R.layout.contact_item,R.layout.store_search_view,R.layout.contact_friend_item,
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
								currentmap = (Map<String,Object>)contactAdapter.getItem(arg2);
								String username = (String)currentmap.get("username");
								String pfid = (String)currentmap.get("pfid");
								String imgurl = (String)currentmap.get("userimg");
								String pkid = (String)currentmap.get("pkid");
								
								String account = (String)currentmap.get("account");
								String sex = (String)currentmap.get("sex");
								String area = (String)currentmap.get("area");
								String signature = (String)currentmap.get("signature");
								openFriendView(username,pfid,imgurl,pkid,account,sex,area,signature);
							}
							else if(arg2 == 1)//选择一个群组
							{
//								openNewFriendView();
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
				Map<String,Object> smap = (Map<String,Object>)msg.obj;
				boolean start = (Boolean)smap.get("start");
				if(start)
				{
					newmap.put("sendimg", "1");
					newmap.put("sendprogress", "1");
					updateMessageLocal(newmap);
				}
				else
				{
					newmap.put("sendimg", "0");
					newmap.put("sendprogress", "1");
					updateMessageLocal(newmap);
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				if(MessageListActivity.instance != null)
					MessageListActivity.instance.finish();
				String typesMapping = newmap.get("messagetype").toString();
				String nameid = newmap.get("nameid").toString();
				String storeid = newmap.get("storeid").toString();
				String storeName = newmap.get("sname").toString();
				String serviceid = newmap.get("serviceid").toString();
				String servicename = newmap.get("servicename").toString();
				String watag = "";
				String username = myapp.getUserName();
				openMessageDetail(nameid,storeid,storeName,username,typesMapping,null,serviceid,servicename,watag,"");
				break;
			}
		}
	};
	
	public void openMessageDetail(String nameid,String storeid,String storeName,String username,String typesMapping,Bitmap bitmimg,String serviceid,String servicename,String watag,String imgurl)
	{
		try{
			int index = 0;
			for(int i=0;i< myapp.getMyCardsAll().size();i++)
			{
				Map<String,Object> map = myapp.getMyCardsAll().get(i);
				if(map != null && map.containsKey("storeid"))
				{
					String sid = (String)map.get("storeid");
					if(sid.equals(storeid))
					{
						index = i;
						break;
					}
				}
			}
			Intent intent = new Intent();
		    intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("nameid", nameid);
			bundle.putString("storeid", storeid);
			bundle.putString("storeName", storeName);
			bundle.putString("username", username);
			bundle.putString("typesMapping", typesMapping);
			bundle.putString("serviceid", serviceid);
			bundle.putString("servicename", servicename);
			bundle.putString("watag", watag);
			bundle.putString("groupimg",imgurl);
			if(bitmimg != null)
			{
				int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			bundle.putInt("index", index);
			bundle.putString("tag", "main");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
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
		contactAdapter = new ForwardContactAdapter(ForwardContactActivity.this,myapp.getMyCardsAll(),R.layout.contact_item,R.layout.store_search_view,R.layout.contact_friend_item,
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
	
	public void openFriendView(String username,String pfid,String imgurl,String pkid,String account,String sex,String area,String signature)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ForwardPromptActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("username", username);
		    bundle.putString("pfid", pfid);
		    bundle.putString("imgurl", imgurl);
		    bundle.putString("pkid", pkid);
		    
		    bundle.putString("account", account);
		    bundle.putString("sex", sex);
		    bundle.putString("area", area);
		    bundle.putString("signature", signature);
		    bundle.putString("tag","main");
		    bundle.putString("isYn",isYn);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
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

//        getMyCardListData();
//        getMyStoreListDatas();//加载门店数据
        getMyFriendDatas(isYn);//加载公司员工数据
    }
    
    /**
	 * 来源:
	 * http://stackoverflow.com/questions/3401579/get-filename-and-path-from-
	 * uri-from-mediastore
	 */
	public String getRealPathFromURI(String filename) {
//		filename = "11611.amr";
	  String[] proj = { MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,MediaStore.Video.Media.DISPLAY_NAME };

//	  ContentResolver mResolver = this.getContentResolver();
	  Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, "_display_name = ?", new String [] {filename}, null);
//	  Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
//	  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	  String time = "";
	  while (cursor.moveToNext()) {  
		  String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		  time = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		  int timeint = Integer.valueOf(time) / 1000;
		  time = String.valueOf(timeint);
//          break;
      }
//	  return cursor.getString(column_index);
	  return time;
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		openMessageListView();
    	}
    	return false;
    }
    
    public void openMessageListView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MessageListActivity.class);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
}
