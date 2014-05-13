package ms.activitys.more;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.CanNotAnswerListActivity;
import ms.activitys.hotel.DowloadVersionDialog;
import ms.activitys.hotel.Exit;
import ms.activitys.hotel.GuestInfoActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.activitys.hotel.MomentsActivity;
import ms.activitys.hotel.SearchFriendActivity;
import ms.activitys.hotel.TeachQuestionsAnsweredActivity;
import ms.activitys.hotel.UserOrderHistoryActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.MyDialog;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.UpdateManager;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoreActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	public static MoreActivity instance = null;
	public RoundAngleImageView user_img;
	private FrameLayout new_moments_fr;
	
	private RoundAngleImageView userimg;
	private MyDialog delmsgDialog;
	private DBHelperMessage db;
	private TextView lable_new;
	
	private UpdateManager mUpdateManager;
	private MyLoadingDialog loadDialog;
	public static FileUtils fileUtil = new FileUtils();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_main_page);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = MoreActivity.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("code"))
		{
			String code = this.getIntent().getExtras().getString("code");
			if(code != null && !code.equals(""))
				Toast.makeText(this, code, Toast.LENGTH_LONG).show();
		}
		
		lable_new = (TextView)findViewById(R.id.lable_new);
		String isUpdate = share.getString("isUpdate", "0");
		if(isUpdate.equals("1"))
		{
			lable_new.setVisibility(View.VISIBLE);
		}
		else
		{
			lable_new.setVisibility(View.GONE);
		}
		
		RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.LinearLayout1); //设置
		layout1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
			    intent.setClass( MoreActivity.this,Settings.class);
			    Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				MainTabActivity.instance.loadLeftActivity(intent);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
			}
		});
		
		RelativeLayout layout2 = (RelativeLayout)findViewById(R.id.LinearLayout2); //密码修改
		layout2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadSettings("uppaw");
			}
		});
		
		new_moments_fr = (FrameLayout)findViewById(R.id.new_moments);
		userimg = (RoundAngleImageView)findViewById(R.id.moments_user_img);
		
		if(MainTabActivity.instance.new_moments_img.getVisibility() == View.VISIBLE)
		{
			String userpath = myapp.getNewmomentsuserimg();
			String userid = myapp.getPuspfid();
			if(userpath != null && !userpath.equals(""))
			{
				Bitmap bitmap = getLoacalBitmap(userpath,true);
				if(bitmap != null)
					userimg.setImageBitmap(bitmap);
				else
				{
					String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+userid;
					dowloadUserImage(userpath,userpath);
				}
				new_moments_fr.setVisibility(View.VISIBLE);
			}
			else
			{
				userpath = fileUtil.getImageFile2aPath(userid, userid);
				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+userid;
				dowloadUserImage(url,userpath);
//				userimg.setImageResource(R.drawable.default_avatar_shadow);
				new_moments_fr.setVisibility(View.VISIBLE);
			}
		}
		
		if (myapp.getIsServer()) {
			RelativeLayout order_layout = (RelativeLayout)findViewById(R.id.LinearLayout13);
			order_layout.setVisibility(View.VISIBLE);
		}
		
//		RelativeLayout layout3 = (RelativeLayout)findViewById(R.id.LinearLayout3); //微博绑定
//		layout3.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				loadSettings("weibo");
//			}
//		});
		
//		RelativeLayout layout4 = (RelativeLayout)findViewById(R.id.LinearLayout4); //条码扫描
//		layout4.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				loadScanning();
//			}
//		});
		
//		RelativeLayout layout5 = (RelativeLayout)findViewById(R.id.LinearLayout5); //NFC
//		layout5.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		RelativeLayout layout6 = (RelativeLayout)findViewById(R.id.LinearLayout6); //意见反馈
		layout6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
			    intent.setClass( MoreActivity.this,ResponseActivity.class);
			    Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				MainTabActivity.instance.loadLeftActivity(intent);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
			}
		});
		
		RelativeLayout layout7 = (RelativeLayout)findViewById(R.id.LinearLayout7); //检查新版本
		layout7.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyLoadingDialog();
				checkVersion();
			}
		});
		
		RelativeLayout layout8 = (RelativeLayout)findViewById(R.id.LinearLayout8); //关于
		layout8.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
		if(myapp.getUserimgbitmap() != null)
			user_img.setImageBitmap(myapp.getUserimgbitmap());
	
	}
	
	private void checkVersion() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					Map<String,Object> map = MainTabActivity.instance.getVersion(MoreActivity.this);
					String curVersion = (String)map.get("versionName");
					int versionCode = (Integer)map.get("versionCode");
					String packageNames = (String)map.get("packageNames");
					msg.obj = api.checkVersion(curVersion,versionCode,packageNames);
				} catch (Exception ex) {
					msg.obj = null;
					ex.printStackTrace();
				}

				handlerForCheckVersion.sendMessage(msg);
			}
		}.start();
	}
	
	private void dowloadUserImage(final String url,final String furl) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;

				try {
					Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
					if(bmpsimg != null)
					{
						myapp.saveMyBitmap(furl, bmpsimg);
						msg.obj = bmpsimg;
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
					msg.obj = null;
					ex.printStackTrace();
				}

				handlerForCheckVersion.sendMessage(msg);
			}
		}.start();
	}

	private Handler handlerForCheckVersion = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (msg.obj != null) {
					JSONObject jsonObj = (JSONObject) msg.obj;
					try {
						if (jsonObj.get("code") != null && !jsonObj.get("code").equals("null")) {
							String curCode = (String) jsonObj.get("code");
							String updataMsg = (String) jsonObj.get("des");
							String apkUrl = (String) jsonObj.get("url");
							if(!apkUrl.equals(""))
								initUpdate(updataMsg, apkUrl);
							else
								makeText(getString(R.string.hotel_label_168));
						} else {
							makeText(getString(R.string.hotel_label_168));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("检查版本失败，请检查网络是否工作正常！");
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					userimg.setImageBitmap(bitmap);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void dowloadData()
	{
		myapp.saveSharedPerferences("isUpdate", "0");
		lable_new.setVisibility(View.GONE);
		MainTabActivity.instance.new_moments_txt.setVisibility(View.GONE);
	}
	
	public void dowloadData2()
	{
		myapp.saveSharedPerferences("isUpdate", "1");
		lable_new.setVisibility(View.VISIBLE);
		MainTabActivity.instance.new_moments_txt.setVisibility(View.VISIBLE);
	}

	// 检测版本是否需要更新
	private void initUpdate(String updataMsg, String apkUrl) {
//		mUpdateManager = new UpdateManager(this);
//		mUpdateManager.apkUrl = apkUrl;
//		AlertDialog.Builder builder = new Builder(MoreActivity.this,R.style.MyDialog);
//		builder.setTitle(getString(R.string.hotel_label_148));
//		builder.setMessage(updataMsg);
//		builder.setPositiveButton(getString(R.string.hotel_label_150), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				myapp.saveSharedPerferences("isUpdate", "0");
//				lable_new.setVisibility(View.GONE);
//				MainTabActivity.instance.new_moments_txt.setVisibility(View.GONE);
//				mUpdateManager.showDownloadDialog();
//			}
//		});
//		builder.setNegativeButton(getString(R.string.hotel_label_167),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						myapp.saveSharedPerferences("isUpdate", "1");
//						lable_new.setVisibility(View.VISIBLE);
//						MainTabActivity.instance.new_moments_txt.setVisibility(View.VISIBLE);
//					}
//				});
//		Dialog noticeDialog = builder.create();
//		noticeDialog.show();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("updataMsg", updataMsg);
		bundle.putString("apkUrl", apkUrl);
		bundle.putString("tag", "more");
		intent.putExtras(bundle);
    	intent.setClass(MoreActivity.this,DowloadVersionDialog.class);
    	startActivity(intent);
	}
	
	public void setUserimg()
	{
		user_img.setImageBitmap(myapp.getUserimgbitmap());
	}
	
	public void loadSettings(String tag)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MoreSettingsActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("tag", tag);
			intent.putExtras(bundle);
//			 startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadScanning()
	{
		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,CaptureActivity.class);
//		    Bundle bundle = new Bundle();
////			bundle.putString("tag", tag);
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			new AlertDialog.Builder(this).setTitle(this.getString(R.string.setting_title))
//			.setMessage(this.getString(R.string.map_lable_7)).setIcon(R.drawable.error2)
//			.setPositiveButton(this.getString(R.string.coupon_lable_14),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
////							setResult(RESULT_OK);// 确定按钮事件
////							android.os.Process.killProcess(android.os.Process.myPid());
////							finish();
//							try {
//								api.destroySession();
//								myapp.setSessionId(null);
//								U.saveSharedPerferences(share,"sessionid","");
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}//销毁session
//							
//							Intent startMain = new Intent(Intent.ACTION_MAIN);
//					         startMain.addCategory(Intent.CATEGORY_HOME);
//					         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					         startActivity(startMain);
//					         System.exit(0);
//						}
//					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// 取消按钮事件
//						}
//					}).show();
//			Intent intent = new Intent();
//        	intent.setClass(MoreActivity.this,Exit.class);
//        	startActivity(intent);
			MainTabActivity.instance.onMinimizeActivity();
			return false;
		}
		return false;
	}
	
	public void exit_settings(View v)
	{
		Intent intent = new Intent();
    	intent.setClass(MoreActivity.this,Exit.class);
    	startActivity(intent);
//		saveSharedPerferences("user", "");
//		saveSharedPerferences("pwa", "");
//		myapp.setUserimgbitmap(null);
//		MainTabActivity.instance.finish();
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器
		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		editor.commit();// 提交刷新数据
	}
	
	public void openGuesinfo(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,GuestInfoActivity.class);
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
//		 startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
		MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void openAddAnswersQuestions(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,TeachQuestionsAnsweredActivity.class);
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
		MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void openNotAnswersView(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,CanNotAnswerListActivity.class);
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
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
	
	public void openOrderList(View v)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,UserOrderHistoryActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("openid", "");
		    bundle.putString("tag", "xieyi");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateMomentsImageStart(int start,String userpath,String userid)
	{
		new_moments_fr.setVisibility(start);
		if(userpath != null)
		{
			Bitmap bitmap = getLoacalBitmap(userpath,true);
			if(bitmap != null)
				userimg.setImageBitmap(bitmap);
			else
			{
				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+userid;
				dowloadUserImage(userpath,userpath);
			}
		}
		else
		{
			userpath = fileUtil.getImageFile2aPath(userid, userid);
			String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+userid;
			dowloadUserImage(url,userpath);
		}
	}
	
	public void deleteMessages(View v)
	{
		try{
			delmsgDialog = new MyDialog(this,R.style.MyDialogStyle);//创建Dialog并设置样式主题

	        final LayoutInflater inflater = LayoutInflater.from(this);  
	        View windov = inflater.inflate(R.layout.delete_message_dialog, null);  
	        
	        Button btn = (Button)windov.findViewById(R.id.exitBtn0);
	        btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					db.deleteMessageDataAll();
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					delmsgDialog.dismiss();
					makeText(getString(R.string.hotel_label_118));
				}
			});
	        
	        Button cleabtn = (Button)windov.findViewById(R.id.exitBtn1);
	        cleabtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					delmsgDialog.dismiss();  
				}
			});
	        
//	        delmsgDialog.setView(windov);
	        delmsgDialog.setContentView(windov);
	        delmsgDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog  
	        
	        Window dialogWindow = delmsgDialog.getWindow();
			WindowManager m = getWindowManager();
	        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
	        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//	        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
	        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
	        dialogWindow.setAttributes(p);
	        
			delmsgDialog.show();  
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url,boolean b) {
		Bitmap bitmap = null; 
		try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(fis,null,opts);
			if(b)
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
		return bitmap;
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
