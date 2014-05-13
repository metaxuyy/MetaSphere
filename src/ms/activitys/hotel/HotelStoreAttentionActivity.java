package ms.activitys.hotel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.listviewadapter.PinyinComparator;
import ms.globalclass.map.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HotelStoreAttentionActivity extends Activity{
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static HotelStoreAttentionActivity instance = null; 
	private String title;
	private String desc;
	private String pkid;
	private RoundAngleImageView user_img;
	private MyLoadingDialog loadDialog;
	private static DBHelperMessage db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_info_attention);
		
		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
		
		Bundle bunde = this.getIntent().getExtras();
		title = bunde.getString("title");
		desc = bunde.getString("desc");
		pkid = bunde.getString("pkid");
		String type = bunde.getString("imgtype");
		if(type.equals("string"))
		{
			String imgurl = bunde.getString("sysImg");
			if(imgurl != null && !imgurl.equals(""))
				loadUserImageThread(imgurl);
		}
		else
		{
			byte[] sysimg = bunde.getByteArray("sysImg");
			Bitmap bmpsimg = BitmapFactory.decodeByteArray(sysimg,0,sysimg.length);
			bmpsimg = Bitmap.createScaledBitmap(bmpsimg,80,80,true);
			user_img.setImageBitmap(bmpsimg);
		}
		
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
					openMessageList();
				}
			});
			
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(title);
			
			TextView store_doc_txt = (TextView)findViewById(R.id.store_doc_txt);
			store_doc_txt.setText(desc);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openAttentionConfirm(View v)
	{
		try{
			showMyLoadingDialog();
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					JSONObject jobj = null;
					try {
						jobj = api.addCardsMessage(myapp.getPfprofileId(), pkid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.obj = jobj;
					handler.sendMessage(msg);
				}
			}.start();
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
					msg.obj = bitmap;
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
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					user_img.setImageBitmap(bitmap);
				break;
			case 1:
				try {
					JSONObject jobj = (JSONObject) msg.obj;
					String success = jobj.getString("success");
					if(success.equals("true"))
					{
						JSONObject stroeobj = jobj.getJSONObject("newstoreinfo");
						Map<String,Object> storemap = U.getNewStoreInfo(stroeobj);
						List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
						slist.add(storemap);
						db.openDB();
						db.saveStoreInfoAllData(slist);
						List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
						myapp.setMyCardsAll(storelist);
						if(ContactActivity.instance != null)
						{
							Collections.sort(myapp.getMyCardsAll(), new PinyinComparator());
//							ContactActivity.instance.getMyStoreListData();
							HotelServiceActivity.instance.getMyStoreListData();
						}
						db.closeDB();
						int index = 0;
						for(int i=0;i<myapp.getMyCardsAll().size();i++)
						{
							Map<String,Object> map = myapp.getMyCardsAll().get(i);
							String storeid = (String)map.get("storeid");
							if(pkid.equals(storeid))
							{
								index = i;
								break;
							}
						}
						
						myapp.setUpdatetag("0");
						
						if(loadDialog != null)
							loadDialog.dismiss();
						
						openStoreView(index);
					}
					else
					{
						if(loadDialog != null)
							loadDialog.dismiss();
						String msgstr = jobj.getString("msg");
						makeText(getString(R.string.coupon_lable_26)+msgstr);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			}
		}
	};
	
	public void openStoreView(int index)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,StoreViewActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("type", "message");
			bundle.putInt("index",index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		    finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
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
	
	public void openMessageList()
	{
		Intent intent = new Intent();
	    intent.setClass( this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMessageList();
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
}
