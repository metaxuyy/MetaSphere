package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendInfoActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static FriendInfoActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private String username;
	private String addpfid;
	private String imgurl;
	private RoundAngleImageView userimg;
	private String tag;
	private String account;
	private String sex;
	private String area;
	private String signature;
	private String companyid;
	private String storeids;
	private static FileUtils fileUtil = new FileUtils();
	private Button send_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		username = bunde.getString("username");
		addpfid = bunde.getString("addpfid");
		imgurl = bunde.getString("imgurl");
		tag = bunde.getString("tag");
		
		account = bunde.getString("account");
		sex = bunde.getString("sex");
		area = bunde.getString("area");
		signature = bunde.getString("signature");
		companyid = bunde.getString("companyid");
		storeids = bunde.getString("storeids");
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tag.equals("phonecontact"))
				{
					openPhoneContactView();
				}
				else
					openFindFriendView();
			}
		});
		
		send_btn = (Button)findViewById(R.id.send_btn);
		if(addpfid.equals(myapp.getUserNameId()))
			send_btn.setVisibility(View.GONE);
		
		initView();
	}
	
	public void initView()
	{
		try{
			TextView nametxt = (TextView)findViewById(R.id.name_txt);
			nametxt.setText(username);
			
			TextView account_txt = (TextView)findViewById(R.id.account_txt);
			account_txt.setText(getString(R.string.hotel_label_76)+account);
			
			TextView area_txt = (TextView)findViewById(R.id.area_txt);
			area_txt.setText(area);
			
			TextView gexin_txt = (TextView)findViewById(R.id.gexin_txt);
			gexin_txt.setText(signature);
			
			ImageView sex_img = (ImageView)findViewById(R.id.sex_img);
			if(sex.equals("") || sex.equals("0"))
			{
				sex_img.setImageResource(R.drawable.ic_sex_male);
			}
			else
			{
				sex_img.setImageResource(R.drawable.ic_sex_female);
			}
			
			userimg = (RoundAngleImageView)findViewById(R.id.user_img);
			
			if(imgurl != null && !imgurl.equals(""))
			{
				if(imgurl.indexOf("http:") >= 0)
					loadUserImg();
				else
				{
					Bitmap bitmap = myapp.getLoacalBitmap(imgurl);
					userimg.setImageBitmap(bitmap);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void addFriend(View v)
	{
		showMyLoadingDialog();
		try{
			getAddFriend();
		}catch(Exception ex){
			ex.printStackTrace();
			if(loadDialog != null)
				loadDialog.dismiss();
		}
	}
	
	public void openMessageList(View v)
	{
		try{
			String nameid = myapp.getPfprofileId();
			String storeid = addpfid;
			String storeName = username;
			String typesMapping = "friend";
			String username = myapp.getUserName();
			fileUtil.createUserFile(storeid);
			getAddFriend();
			openMessageDetail(nameid,storeid,storeName,username,typesMapping);
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
				Drawable draw = userimg.getDrawable();
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
				bundle.putString("tag", "friendinfo");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//	public void getAddFriend()
//	{
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				msg.what = 0;
//				
//				try {
//					JSONObject jobj = api.addMyFriendData(addpfid);
//					boolean b = false;
//					if(jobj != null)
//					{
//						if(jobj.getString("tag").equals("success"))
//						{
//							b = true;
//							db.openDB();
//							Map<String,Object> map = new HashMap<String,Object>();
//							map.put("pkid", db.getCombID());
//							map.put("username", username);
//							map.put("pfid", addpfid);
//							map.put("imgurl", imgurl);
//							
//							map.put("account", account);
//							map.put("sex", sex);
//							map.put("area", area);
//							map.put("signature", signature);
//							
//							db.saveFriendAllData(map);
//							db.closeDB();
//						}
//					}
//					
//					msg.obj = b;
//				} catch (Exception ex) {
////					Log.i("erroyMessage", ex.getMessage());
//					ex.printStackTrace();
//				}
//				handler.sendMessage(msg);
//			}
//		}.start();
//	}
	
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
					map.put("start", "1");
					
					db.saveFriendAllData(map);
					db.closeDB();
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
			}
		}.start();
	}
	
	public void loadUserImg()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					Bitmap bitmap = null;
//					bitmap = getLoacalBitmap(imgurl);
					bitmap = returnBitMap(imgurl);
					
					msg.obj = bitmap;
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
				boolean b = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(b)
				{
					FindFriendActivity.instance.mykey = "";
					FindFriendActivity.instance.getFriendAll("");
					openFindFriendView();
				}
				else
				{
					makeText(getString(R.string.hotel_label_70));
				}
				break;
			case 1:
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					userimg.setImageBitmap(bitmap);
				break;
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(tag.equals("phonecontact"))
			{
				openPhoneContactView();
			}
			else
				openFindFriendView();
			return false;
		}
		return false;
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	public void openFindFriendView()
	{
		try{
			if(tag.equals("groupinfo"))
			{
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass( this,ChatMessageInfoActivity.class);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			    this.finish();
			}
			else
			{
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass( this,FindFriendActivity.class);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			    this.finish();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
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
	
	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			System.out.println("ssssssssurl===="+url);
			if(url == null || url.equals(""))
				return null;
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
