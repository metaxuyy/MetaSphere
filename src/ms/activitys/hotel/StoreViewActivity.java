package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreViewActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private int index;
	private Map<String,Object> dmap;
	private RoundAngleImageView user_img; 
	public static StoreViewActivity instance = null; 
	public static String isASttention;
	private String pkid;
	private String storeid;
	private ImageView straimg;
	private MyLoadingDialog loadDialog;
	private String typesMapping;
	private String sortName;
	private Button clect_gz_btn;
	private String type = "";
	private Bitmap storeimg = null;
	private Bitmap yuanstoreimg = null;
	private static DBHelperMessage db;
	private String businessId;
	private static FileUtils fileUtil = new FileUtils();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_view);
		
		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("index"))
			index = bunde.getInt("index");
		if(bunde.containsKey("type"))
		{
			type = bunde.getString("type");
		}
		
		dmap = myapp.getMyCardsAll().get(index);
		
		String imgurl = (String)dmap.get("imgurl");
		String imgbitmap = (String)dmap.get("imgbitmap");
		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
		if(imgurl != null && !imgurl.equals(""))
		{
			storeimg = getLoacalBitmap(imgurl);
			yuanstoreimg = getLoacalBitmap(imgbitmap);
			user_img.setImageBitmap(storeimg);
//			loadUserImageThread(imgurl);
		}
		else
		{
			storeimg = null;
		}
		
		isASttention = (String)dmap.get("isASttention");
		pkid = (String)dmap.get("pkid");
		storeid = (String)dmap.get("storeid");
		typesMapping = (String)dmap.get("typesMapping");
		sortName = (String)dmap.get("sortName");
		businessId = (String)dmap.get("businessId");
		
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
					if(type.equals(""))
						openMainTabForm();
					else
						openMessageList();
				}
			});
			
			clect_gz_btn = (Button)findViewById(R.id.clect_gz_btn);
			if(businessId.equals(myapp.getBusinessid()))
				clect_gz_btn.setVisibility(View.GONE);
			
			String storeName = (String)dmap.get("storeName");
			String storeDesc = (String)dmap.get("storeDesc");
			String addressInfomation = (String)dmap.get("addressInfomation");
			TextView title_lable = (TextView)findViewById(R.id.title_lable);
			title_lable.setText(storeName);
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(storeName);
			TextView address_txt = (TextView)findViewById(R.id.address_txt);
			address_txt.setText(addressInfomation);
//			TextView store_doc_txt = (TextView)findViewById(R.id.store_doc_txt);
//			store_doc_txt.setText(storeDesc);
			
			straimg = (ImageView)findViewById(R.id.stra_img);
			if(isASttention.equals("0"))
				straimg.setImageResource(R.drawable.star_mark);
				
			user_img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					loadUserImgeBig();
				}
			});
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
				String tag = (String)msg.obj;
				if(tag != null)
				{
					loadDialog.setSucceedDialog(getString(R.string.hotel_label_17));
					if (isASttention.equals("1"))// 1为否，0为是
					{
						straimg.setImageResource(R.drawable.star_mark);
						isASttention = "0";
						dmap.put("isASttention", isASttention);
						dmap.put("sortName",  "1"+sortName);
						dmap.put("xinxin", R.drawable.ic_star_small);
					} else {
						straimg.setImageBitmap(null);
						isASttention = "1";
						dmap.put("isASttention", isASttention);
						dmap.put("sortName", sortName);
						dmap.put("xinxin", null);
					}
					db.openDB();
					db.updateStoreInfoData(dmap);
					db.closeDB();
//					ContactActivity.instance.getMyStoreListData();
					HotelServiceActivity.instance.getMyStoreListData();
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				String tags = (String)msg.obj;
				if(tags.equals("success"))
				{
					myapp.getMyCardsAll().remove(index);
//					ContactActivity.instance.getMyStoreListData();
					HotelServiceActivity.instance.getMyStoreListData();
					HotelMainActivity.instance.loadeListItemData();
					makeText(getString(R.string.hotel_label_19));
					openMainTabForm();
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				if(loadDialog != null)
					loadDialog.dismiss();
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
	
	public void openMainTabForm()
	{
		Intent intent = new Intent();
	    intent.setClass( this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
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
	
	public void openMoveMume(View v)
	{
		Intent intent = new Intent (this,RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "tuijian");
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	public void openUnfollowConfirm(View v)
	{
		Intent intent = new Intent (this,RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "UnfollowConfirm");
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	public void showMessage(View v)
    {
		String nameid = dmap.get("nameid").toString();
		String storeid = dmap.get("storeid").toString();
		String storeName = dmap.get("storeName").toString();
		String typesMapping = dmap.get("typesMapping").toString();
		String username = myapp.getUserName();
		if(!fileUtil.isFileExist2(storeid))
			fileUtil.createUserFile(storeid);
		openMessageDetail(nameid,storeid,storeName,username,typesMapping);
    }
    
    public void toHomePage(View v)
    {
    	try{
			if(typesMapping.equals("09"))
			{
//				Intent intent = new Intent();
//			    intent.setClass( this,HotelActivity.class);
//			    Bundle bundle = new Bundle();
//				bundle.putInt("index", index);
//				bundle.putString("tag", "storeinfo");
//				intent.putExtras(bundle);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
////			    this.finish();
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "storeinfo");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else
			{
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "storeinfo");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//			    this.finish();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
    public void unfollowStore()
    {
    	showMyLoadingDialog();
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					JSONObject jobj = api.notStoreUnfollow(pkid,storeid);
					if(jobj != null)
					{
						String tag = jobj.getString("tag");
						db.openDB();
						db.deleteStroeInfo(storeid,dmap.get("nameid").toString());
						db.closeDB();
						msg.obj = tag;
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
				}
				handler.sendMessage(msg);
			}
		}.start();
    }
    
    public void labeledStarredFriend()
    {
    	showMyLoadingDialog();
    	try{
    		new Thread() {
    			public void run() {
    				Message msg = new Message();
    				msg.what = 1;
    				
    				try {
    					JSONObject jobj;
    					if(isASttention.equals("1"))//1为否，0为是
    						jobj = api.isASttention(pkid,"1");
    					else
    						jobj = api.notASttention(pkid,"1");
    					if(jobj != null)
    						msg.obj = "1";
    					else
    						msg.obj = null;
    				} catch (Exception ex) {
    					Log.i("erroyMessage", ex.getMessage());
    					ex.printStackTrace();
    				}
    				handler.sendMessage(msg);
    			}
    		}.start();
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
			if(storeimg != null)
			{
				int size=storeimg.getWidth()*storeimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				storeimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			bundle.putInt("index", index);
			bundle.putString("tag", "storeview");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(type.equals(""))
				openMainTabForm();
			else
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
