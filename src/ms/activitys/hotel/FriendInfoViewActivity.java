package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

public class FriendInfoViewActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private int index;
	private RoundAngleImageView user_img; 
	public static FriendInfoViewActivity instance = null; 
	public static String isASttention;
	private String pkid;
	private ImageView straimg;
	private MyLoadingDialog loadDialog;
	private Button clect_gz_btn;
	private Bitmap storeimg = null;
	private Bitmap yuanstoreimg = null;
	private static DBHelperMessage db;
	private static FileUtils fileUtil = new FileUtils();
	private String username;
	private String addpfid;
	private String imgurl;
	private String account;
	private String sex;
	private String area;
	private String signature;
	private String tag;
	private String isYn;
	private String isYn2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_view);
		
		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		username = bunde.getString("username");
		addpfid = bunde.getString("addpfid");
		imgurl = bunde.getString("imgurl");
		pkid = bunde.getString("pkid");
		
		account = bunde.getString("account");
		sex = bunde.getString("sex");
		area = bunde.getString("area");
		signature = bunde.getString("signature");
		tag = bunde.getString("tag");
		if(myapp.getCompanyid() != null && !myapp.getCompanyid().equals(""))
		{
			isYn = "0";
			isYn2 = "1";
		}
		else
		{
			isYn = "1";
			isYn2 = "0";
		}
//		isYn = bunde.getString("isYn");
		
		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
		if(imgurl != null && !imgurl.equals(""))
		{
			storeimg = getLoacalBitmap(imgurl);
			if(storeimg != null)
			{
				user_img.setImageBitmap(storeimg);
				
				user_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						loadUserImgeBig();
					}
				});
			}
		}
		else
		{
			storeimg = null;
		}
		
		updateUserInfoThread();//同步服务器用户信息
		initView();
	}
	
	public void initView()
	{
		try{
			Button break_btn = (Button)findViewById(R.id.break_btn);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(tag.equals("contactlist"))
						openFriendList();
					else if(tag.equals("main"))
						openMainView();
					else if(tag.equals("phonecontact"))
						openPhoneContactView();
					else
						openChatMessage();
				}
			});
			
			clect_gz_btn = (Button)findViewById(R.id.clect_gz_btn);
			
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(username);
			
			TextView account_txt = (TextView)findViewById(R.id.account_txt);
			account_txt.setText(getString(R.string.hotel_label_76)+account);
			
			TextView area_txt = (TextView)findViewById(R.id.area_txt);
			area_txt.setText(area);
			
			TextView gexin_txt = (TextView)findViewById(R.id.gexin_txt);
			gexin_txt.setText(signature);
			
			ImageView sex_img = (ImageView)findViewById(R.id.sex_img);
			if(sex.equals("") || sex.equals("1"))
			{
				sex_img.setImageResource(R.drawable.ic_sex_male);
			}
			else
			{
				sex_img.setImageResource(R.drawable.ic_sex_female);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateUserInfoView(Map<String,Object> umap)
	{
		try{
			imgurl = (String)umap.get("imgurl");
			if(imgurl != null && !imgurl.equals(""))
			{
				storeimg = getLoacalBitmap(imgurl);
				if(storeimg != null)
					user_img.setImageBitmap(storeimg);
			}
			
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(myapp.getValues((String)umap.get("username")));
			
			TextView account_txt = (TextView)findViewById(R.id.account_txt);
			account_txt.setText(getString(R.string.hotel_label_76)+myapp.getValues((String)umap.get("account")));
			
			TextView area_txt = (TextView)findViewById(R.id.area_txt);
			area_txt.setText(myapp.getValues((String)umap.get("area")));
			
			TextView gexin_txt = (TextView)findViewById(R.id.gexin_txt);
			gexin_txt.setText(myapp.getValues((String)umap.get("signature")));
			
			ImageView sex_img = (ImageView)findViewById(R.id.sex_img);
			String sex = myapp.getValues((String)umap.get("sex"));
			if(sex.equals("") || sex.equals("1"))
			{
				sex_img.setImageResource(R.drawable.ic_sex_male);
			}
			else
			{
				sex_img.setImageResource(R.drawable.ic_sex_female);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadUserImgeBig()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ImageShower.class);
		    Bundle bundle = new Bundle();
			if(yuanstoreimg != null)
			{
				int size=yuanstoreimg.getWidth()*yuanstoreimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				yuanstoreimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadUserImageThread(final String userimg)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Bitmap bitmap = getImageBitmap(userimg);
					storeimg = bitmap;
					msg.obj = bitmap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void updateUserInfoThread()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					Map<String,Object> umap = null;
					JSONObject jobj = api.synchronousFriendInfo(addpfid);
					if(jobj != null)
					{
						JSONArray jarry = jobj.getJSONArray("data");
						List<Map<String,Object>> dlist = myapp.getMyFriendList(jarry);
						if(dlist != null && dlist.size() > 0)
						{
							Map<String,Object> dmap = dlist.get(0);
							db.openDB();
							umap = db.updateFriendAllData(dmap);
							db.updateNewMessageUserNameImageData((String)dmap.get("username"), (String)dmap.get("imgurl"), addpfid);
							db.closeDB();
						}
					}
					
					msg.obj = umap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void unfollowFriend()
    {
    	showMyLoadingDialog();
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					boolean b = false;
					JSONObject jobj = api.deleteMyFriendData(addpfid);
					if(jobj != null)
					{
						String tag = jobj.getString("tag");
						if(tag.equals("success"))
						{
							db.openDB();
							db.deleteFriendData(pkid);
							db.closeDB();
							b = true;
						}
					}
					
					msg.obj = b;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
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
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					user_img.setImageBitmap(bitmap);
				break;
			case 1:
				Map<String,Object> umap = (Map<String,Object>)msg.obj;
				if(umap != null)
				{
					updateUserInfoView(umap);
					if(ContactActivity.instance != null)
						ContactActivity.instance.getMyFriendDatas(isYn);
					if(FriendsContactActivity.instance != null)
						FriendsContactActivity.instance.getMyStoreListDatas("",isYn2);
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.getMyCardListData(null);
					if(MessageListActivity.instance != null)
					{
						if(!tag.equals("groupinfo"))
							MessageListActivity.instance.TextView01.setText((String)umap.get("username"));
						String imgurl = (String)umap.get("imgurl");
						Bitmap bitmimg = getLoacalBitmap(imgurl);
						if(bitmimg != null)
						{
							myapp.setStoreimgbitmap(bitmimg);
							MessageListActivity.instance.getLocalMessageListData("refresh");
						}
					}
					if(!tag.equals("groupinfo"))
					{
						if(ChatMessageInfoActivity.instance != null)
						{
							ChatMessageInfoActivity.instance.storeName = (String)umap.get("username");
							ChatMessageInfoActivity.instance.updateView(umap);
						}
					}
				}
				break;
			case 2:
				boolean b = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(b)
				{
//					FriendsContactActivity.instance.getMyStoreListData(isYn);
					if(FriendsContactActivity.instance != null)
						FriendsContactActivity.instance.getMyStoreListDatas("",isYn2);
					else
					{
						if(ContactActivity.instance != null)
							ContactActivity.instance.getMyFriendDatas(isYn);
					}
					
					if(tag.equals("contactlist"))
						openFriendList();
					else if(tag.equals("main"))
						openMainView();
//					else
//						openChatMessage();
//					openFriendList();
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				break;
			}
		}
	};
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
//		    opt.inSampleSize = 2;
		    
			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
//			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void openFriendList()
	{
		Intent intent = new Intent();
	    intent.setClass( this,FriendsContactActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void openChatMessage()
	{
		Intent intent = new Intent();
	    intent.setClass( this,ChatMessageInfoActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void openPhoneContactView()
	{
		Intent intent = new Intent();
	    intent.setClass( this,PhoneContactsActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void openUnfollowConfirm(View v)
	{
		Intent intent = new Intent (this,RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "FriendUnfollowConfirm");
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMessage(View v)
	{
		try{
			String nameid = myapp.getPfprofileId();
			String storeid = addpfid;
			String storeName = username;
			String typesMapping = "friend";
			String username = myapp.getUserName();
			fileUtil.createUserFile(storeid);
//			getAddFriend();
			openMessageDetail(nameid,storeid,storeName,username,typesMapping);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getAddFriend()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				
				try {
					db.openDB();
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("pkid", db.getCombID());
					map.put("username", username);
					map.put("pfid", addpfid);
					map.put("imgurl", imgurl);
					
					map.put("account", account);
					map.put("sex", sex);
					map.put("area", area);
					map.put("signature", signature);
					map.put("start", "0");
					
					db.saveFriendAllData(map);
					db.closeDB();
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
			}
		}.start();
	}
	
	public void openMessageDetail(String nameid,String storeid,String storeName,String username,String typesMapping)
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
			if(imgurl != null && !imgurl.equals(""))
			{
				Drawable draw = user_img.getDrawable();
				Bitmap storeimg = ((BitmapDrawable)draw).getBitmap();
				int size=storeimg.getWidth()*storeimg.getHeight()*1;
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				storeimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			bundle.putInt("index", 0);
			if(tag.equals("groupinfo"))
				bundle.putString("tag", "main");
			else
				bundle.putString("tag", "myfriendinfo");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(tag.equals("contactlist"))
				openFriendList();
			else if(tag.equals("main"))
				openMainView();
			else if(tag.equals("phonecontact"))
				openPhoneContactView();
			else
				openChatMessage();
			return false;
		}
		return false;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
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
}
