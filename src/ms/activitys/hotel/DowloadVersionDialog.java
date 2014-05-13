package ms.activitys.hotel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DowloadVersionDialog extends Activity{

	//private MyDialog dialog;
		private LinearLayout dowlayout;
		private LinearLayout loadlayout;
		private TextView load_txt;
		private MyApp myapp;
		private SharedPreferences  share;
		private static DBHelperMessage db;
		private String tag;
		private String apkUrl;
		private String updataMsg;
		private TextView content_txt;
		private int progress;
		private Thread downLoadThread;
		/* 下载包安装路径 */  
	    private static final String savePath = "/sdcard/updatedemo/";
	    private static final String saveFileName = savePath + "MetaSphere.apk";
	    private static final int DOWN_UPDATE = 1;  
	    private static final int DOWN_OVER = 2;
	    private boolean interceptFlag = false;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.update_version_dialog);
			
			myapp = (MyApp)this.getApplicationContext();
			share = getSharedPreferences("perference", MODE_PRIVATE);
			db = new DBHelperMessage(this, myapp);
			
			Bundle bunde = this.getIntent().getExtras();
			updataMsg = bunde.getString("updataMsg");
			apkUrl = bunde.getString("apkUrl");
			tag = bunde.getString("tag");
			
			
			//dialog=new MyDialog(this);
			dowlayout=(LinearLayout)findViewById(R.id.dowload_layout);
			loadlayout=(LinearLayout)findViewById(R.id.jindutiao_layout);
			load_txt = (TextView)findViewById(R.id.load_txt);
			content_txt = (TextView)findViewById(R.id.content_txt);
			
			content_txt.setText(updataMsg);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event){
			finish();
			return true;
		}
		
		public void dowloadbtn(View v) {  
			if(tag.equals("more"))
			{
				MoreActivity.instance.dowloadData();
			}
			else
			{
				MainTabActivity.instance.dowloadData();
			}
			dowlayout.setVisibility(View.GONE);
			loadlayout.setVisibility(View.VISIBLE);
			downloadApk();
	    }  
		
		public void exitbutton(View v) {
			if(tag.equals("more"))
			{
				MoreActivity.instance.dowloadData2();
			}
			else
			{
				MainTabActivity.instance.dowloadData2();
			}
			this.finish();
	    }
		
		public void saveSharedPerferences(String key,String value)
		{
			Editor editor = share.edit();// 取得编辑器
			editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
			editor.commit();// 提交刷新数据
		}
		
		private Runnable mdownApkRunnable = new Runnable() {      
	        @Override  
	        public void run() {  
	            try {  
	                URL url = new URL(apkUrl);  
	              
	                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
	                conn.connect();  
	                int length = conn.getContentLength();  
	                InputStream is = conn.getInputStream();  
	                  
	                File file = new File(savePath);  
	                if(!file.exists()){  
	                    file.mkdir();  
	                }  
	                String apkFile = saveFileName;  
	                File ApkFile = new File(apkFile);  
	                FileOutputStream fos = new FileOutputStream(ApkFile);  
	                  
	                int count = 0;  
	                byte buf[] = new byte[1024];  
	                  
	                do{                   
	                    int numread = is.read(buf);  
	                    count += numread;  
	                    progress =(int)(((float)count / length) * 100);  
	                    //更新进度  
	                    mHandler.sendEmptyMessage(DOWN_UPDATE);  
	                    if(numread <= 0){      
	                        //下载完成通知安装  
	                        mHandler.sendEmptyMessage(DOWN_OVER);  
	                        break;  
	                    }  
	                    fos.write(buf,0,numread);  
	                }while(!interceptFlag);//点击取消就停止下载.  
	                  
	                fos.close();  
	                is.close();  
	            } catch (MalformedURLException e) {  
	                e.printStackTrace();  
	            } catch(IOException e){  
	                e.printStackTrace();  
	            }  
	              
	        }  
	    };  
	    
	    private Handler mHandler = new Handler(){  
	        public void handleMessage(Message msg) {  
	            switch (msg.what) {  
	            case DOWN_UPDATE:  
	            	load_txt.setText(String.valueOf(progress)+"%");  
	                break;  
	            case DOWN_OVER:  
	                  
	                installApk();  
	                break;  
	            default:  
	                break;  
	            }  
	        };  
	    };  
		
		/** 
	     * 下载apk 
	     * @param url 
	     */  
	      
	    private void downloadApk(){  
	        downLoadThread = new Thread(mdownApkRunnable);  
	        downLoadThread.start();  
	    }  
	     /** 
	     * 安装apk 
	     * @param url 
	     */  
	    private void installApk(){  
	        File apkfile = new File(saveFileName);  
	        if (!apkfile.exists()) {  
	            return;  
	        }      
	        Intent i = new Intent(Intent.ACTION_VIEW);  
	        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");   
	        startActivity(i);  
	    }  
}
