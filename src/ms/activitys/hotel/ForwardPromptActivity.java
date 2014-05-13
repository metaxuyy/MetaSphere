package ms.activitys.hotel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.more.MoreActivity;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForwardPromptActivity extends Activity{

	//private MyDialog dialog;
		private LinearLayout layout;
		private MyApp myapp;
		private SharedPreferences  share;
		private static DBHelperMessage db;
		private String username;
		private String pfid;
		private String imgurl;
		private RoundAngleImageView userimg;
		private TextView usernametxt;
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.forward_prompt_dialog);
			
			myapp = (MyApp)this.getApplicationContext();
			share = getSharedPreferences("perference", MODE_PRIVATE);
			db = new DBHelperMessage(this, myapp);
			
			Bundle bunde = this.getIntent().getExtras();
			username = bunde.getString("username");
			pfid = bunde.getString("pfid");
			imgurl = bunde.getString("imgurl");
			
			userimg = (RoundAngleImageView)findViewById(R.id.user_img);
			usernametxt = (TextView)findViewById(R.id.user_name);
			
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
			
			usernametxt.setText(username);
			
			//dialog=new MyDialog(this);
			layout=(LinearLayout)findViewById(R.id.exit_layout);
			layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
							Toast.LENGTH_SHORT).show();	
				}
			});
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
				}
			}
		};

		@Override
		public boolean onTouchEvent(MotionEvent event){
			finish();
			return true;
		}
		
		public void exitbutton1(View v) {  
			ForwardContactActivity.instance.forwardMessage();
			this.finish();
	    }  
		
		public void exitbutton0(View v) {
	    	this.finish();
	    }
		
		public void saveSharedPerferences(String key,String value)
		{
			Editor editor = share.edit();// 取得编辑器
			editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
			editor.commit();// 提交刷新数据
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
