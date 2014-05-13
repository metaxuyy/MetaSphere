package ms.activitys.hotel;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.more.MoreActivity;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Exit extends Activity{

	//private MyDialog dialog;
		private LinearLayout layout;
		private MyApp myapp;
		private SharedPreferences  share;
		private static DBHelperMessage db;
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.exit_dialog);
			
			myapp = (MyApp)this.getApplicationContext();
			share = getSharedPreferences("perference", MODE_PRIVATE);
			db = new DBHelperMessage(this, myapp);
			
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

		@Override
		public boolean onTouchEvent(MotionEvent event){
			finish();
			return true;
		}
		
		public void exitbutton1(View v) {  
	    	this.finish();    	
	    }  
		
		public void exitbutton0(View v) {
			saveSharedPerferences("user", "");
			saveSharedPerferences("pwa", "");
			myapp.setUserimg(null);
			myapp.setUserimgbitmap(null);
			myapp.getChannel().close();
			SharedPreferences.Editor editor = share.edit();
        	editor.putString("userimg", null);
        	editor.commit();
			stopService(new Intent(Exit.this, MyService.class));
			db.closeDB2();
	    	this.finish();
	    	MainTabActivity.instance.finish();//关闭Main 这个Activity
	    	System.exit(0);
	    }
		
		public void saveSharedPerferences(String key,String value)
		{
			Editor editor = share.edit();// 取得编辑器
			editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
			editor.commit();// 提交刷新数据
		}
}
