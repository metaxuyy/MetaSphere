package ms.activitys.hotel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.map.BaiduMap;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SearchFriendActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private FrameLayout new_moments_fr;
	
	public static SearchFriendActivity instance;
	private RoundAngleImageView userimg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_friends);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
		new_moments_fr = (FrameLayout)findViewById(R.id.new_moments);
		userimg = (RoundAngleImageView)findViewById(R.id.user_img);
		
		if(MainTabActivity.instance.new_moments_img.getVisibility() == View.VISIBLE)
		{
			String userpath = myapp.getNewmomentsuserimg();
			Bitmap bitmap = getLoacalBitmap(userpath,true);
			userimg.setImageBitmap(bitmap);
			new_moments_fr.setVisibility(View.VISIBLE);
		}
	}
	
	public void btn_shake(View v) {                                   //手机摇一摇
//		Intent intent = new Intent (this,ShakeActivity.class);			
//		startActivity(intent);	
	}
	
	public void add_friends(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,FindFriendActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "main");
		intent.putExtras(bundle);
	    MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void add_store(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,BaiduMap.class);
	    Bundle bundle = new Bundle();
		
		intent.putExtras(bundle);
	    MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void openMoments(View v)
	{
		new_moments_fr.setVisibility(View.GONE);
		MainTabActivity.instance.new_moments_img.setVisibility(View.GONE);
		Intent intent = new Intent();
	    intent.setClass( this,MomentsActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "main");
		intent.putExtras(bundle);
	    MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			Intent intent = new Intent();
//        	intent.setClass(BaiduMap.this,Exit.class);
//        	startActivity(intent);
			MainTabActivity.instance.onMinimizeActivity();
			return false;
		}
		return false;
	}
	
	public void updateMomentsImageStart(int start,String userpath)
	{
		new_moments_fr.setVisibility(start);
		Bitmap bitmap = getLoacalBitmap(userpath,true);
		userimg.setImageBitmap(bitmap);
	} 
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url,boolean b) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			if(b)
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
}
