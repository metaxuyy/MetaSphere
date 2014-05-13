package ms.activitys.hotel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VerificationFriendView extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static VerificationFriendView instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private int index;
	private ImageView userimg;
	private String imgurl;
	private Map<String,Object> map;
	private Button tonguo_btn;
	private String currStart;
	private Button black_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verification_friend_info_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openInformationView();
			}
		});
		
		initView();
	}
	
	public void initView()
	{
		try{
			map = myapp.getVerificationMessage().get(index);
			String pkid = (String)map.get("pkid");
			String frompfid = (String)map.get("frompfid");
			String acceptpfid = (String)map.get("acceptpfid");
			String requestmessage = (String)map.get("requestmessage");
			String requeststart = (String)map.get("requeststart");
			String fromname = (String)map.get("fromname");
			String sex = (String)map.get("sex");
			String area = (String)map.get("area");
			String acceptname = (String)map.get("acceptname");
			imgurl = (String)map.get("imgurl");
			
			TextView nametxt = (TextView)findViewById(R.id.name_txt);
			nametxt.setText(fromname);
			
			TextView area_txt = (TextView)findViewById(R.id.area_txt);
			area_txt.setText(area);
			
			TextView message_txt = (TextView)findViewById(R.id.message_txt);
			message_txt.setText(requestmessage);
			
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
				loadUserImg();
			}
			
			tonguo_btn = (Button)findViewById(R.id.tonguo_btn);
			black_btn = (Button)findViewById(R.id.black_btn);
			if(requeststart.equals("0"))
			{
				tonguo_btn.setVisibility(View.GONE);
			}
			else if(requeststart.equals("1"))
			{
				black_btn.setVisibility(View.GONE);
				tonguo_btn.setText(getString(R.string.hotel_label_104));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateVerification(View v)
	{
		String frompfid = (String)map.get("frompfid");
		String fromname = (String)map.get("fromname");
		String acceptname = (String)map.get("acceptname");
		updateVerificationStart("0",frompfid,fromname,acceptname);
	}
	
	public void updateVerification2(View v)
	{
		String frompfid = (String)map.get("frompfid");
		String fromname = (String)map.get("fromname");
		String acceptname = (String)map.get("acceptname");
		updateVerificationStart("1",frompfid,fromname,acceptname);
	}
	
	public void updateVerificationStart(final String start,final String requestpfid,final String requestname,final String acceptname)
	{
		showMyLoadingDialog(getString(R.string.hotel_label_96));
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					boolean b = false;
					JSONObject jobj = api.upadateVerificationMessageStart(start, requestpfid, requestname, acceptname);
					if(jobj != null)
					{
						if(jobj.getString("tag").equals("success"))
						{
							currStart = start;
							b = true;
//							getAddFriend(start);
						}
					}
					
					msg.obj = b;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getAddFriend(final String start)
	{
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
				
				try {
					db.openDB();
					String account = (String)map.get("account");
					String frompfid = (String)map.get("frompfid");
					String signature = (String)map.get("signature");
					String fromname = (String)map.get("fromname");
					String sex = (String)map.get("sex");
					String area = (String)map.get("area");
					String companyid = (String)map.get("companyid");
					String storeids = (String)map.get("storeid");
					
					Map<String,Object> maps = new HashMap<String,Object>();
					maps.put("pkid", db.getCombID());
					maps.put("username", fromname);
					maps.put("pfid", frompfid);
					maps.put("imgurl", imgurl);
					
					maps.put("account", account);
					maps.put("sex", sex);
					maps.put("area", area);
					maps.put("signature", signature);
					maps.put("start", start);
					maps.put("companyid", companyid);
					maps.put("storeid", storeids);
					
					db.saveFriendAllData(maps);
					db.closeDB();
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
//			}
//		}.start();
	}
	
	public void loadUserImg()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Bitmap bitmap = null;
					bitmap = returnBitMap(imgurl);
					
					msg.obj = bitmap;
				} catch (Exception ex) {
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
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					userimg.setImageBitmap(bitmap);
				break;
			case 1:
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					if(loadDialog != null)
					{
						loadDialog.setSucceedDialog(getString(R.string.hotel_label_97));
						loadDialog.dismiss();
					}
					
					if(currStart.equals("0"))
					{
						tonguo_btn.setVisibility(View.GONE);
					}
					else if(currStart.equals("1"))
					{
						black_btn.setVisibility(View.GONE);
						tonguo_btn.setText(getString(R.string.hotel_label_104));
					}
				}
				else
				{
					if(loadDialog != null)
					{
						loadDialog.setErrorDialog(getString(R.string.hotel_label_98));
						loadDialog.dismiss();
					}
				}
				if(InformationFriendActivity.instance != null)
				{
//					InformationFriendActivity.instance.refreshListItem();
					InformationFriendActivity.instance.getMyVerificationMessageListData();
				}
				String isYn = "";
				String isYn2 = "";
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
				
				if(FriendsContactActivity.instance != null)
					FriendsContactActivity.instance.getMyStoreListDatas("",isYn2);
				else
				{
					if(ContactActivity.instance != null)
					{
						ContactActivity.instance.showMyLoadingDialog();
						ContactActivity.instance.getMyFriendDatas(isYn);
					}
				}
				break;
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openInformationView();
			return false;
		}
		return false;
	}
	
	public void openInformationView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,InformationFriendActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
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
	
	public void showMyLoadingDialog(String message)
    {
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
}
