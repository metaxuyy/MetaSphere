package ms.activitys;



import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

import ms.activitys.hotel.ManagerPublishBoardActivity;
import ms.activitys.hotel.MomentsActivity;
import ms.activitys.more.MoreActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class myAnimation extends Activity {
    /** Called when the activity is first created. */
	private ImageView imgViews;
	private Animation myAnimation_Translate;
	private Animation myAlplha;
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private DBHelperLogin db;
	Timer timer = new Timer();
	
	private ImageView mShowPicture;
	private TextView mShowText;
	private Button mRegister;
	private Button mWhoIKnow;
	private Button mLogin;
	/**
	 * 三个切换的动画
	 */
	private Animation mFadeIn;
	private Animation mFadeInScale;
	private Animation mFadeOut;
	/**
	 * 三个图片
	 */
	private Drawable mPicture_1;
	private Drawable mPicture_2;
	private Drawable mPicture_3;
	
	private int tag = 0;
	public static FileUtils fileUtil = new FileUtils();
	
	private boolean isShortCut;
	private String title;
	private String type;
	private String bgImgUrl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //去掉任务条
        this.getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        Intent mIntent = getIntent();
        isShortCut = mIntent.getBooleanExtra("isShortCut", false);
		title = mIntent.getStringExtra("title");
		type = mIntent.getStringExtra("type");
		bgImgUrl = mIntent.getStringExtra("bgImg");
//		if(isShortCut){
//			Intent intent = new Intent(myAnimation.this, MomentsActivity.class);
//			intent.putExtra("title", title);
//			intent.putExtra("type", type);
//			intent.putExtra("bgImg", bgImgUrl);
//			startActivity(intent);
//			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
//			finish();
//		}
        
        myapp = (MyApp)this.getApplicationContext();
        
        String language = Locale.getDefault().getLanguage();
        
        myapp.setLanguage(language);
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperLogin(this);
		
		findViewById();
		
		String isloging = share.getString("isloging", "1");
		if(isloging.equals("0"))
		{
//			if(isShortCut){
//				makeText("还未登陆，需要先登陆系统");
//			}
//			
//			init();
//			setListener();
			
			saveSharedPerferences("isloging","1");
			
			Intent intent = new Intent();
			intent.setClass(myAnimation.this,Viewpager.class);
			startActivity(intent);
			this.finish();
		}
		else
		{
			boolean b = StreamTool.isNetworkVailable(myAnimation.this);
			if(!b)
			{
				String user = share.getString("user", "");
				String pwa = share.getString("pwa", "");
				String sex = share.getString("sex", "");
				String area = share.getString("area", "");
				String signature = share.getString("signature", "");
				String thembgurl = share.getString("thembgurl", "");
				String username = share.getString("username", "");
				String userimg = share.getString("userimg", "");
				String companyid = share.getString("companyid", "");
				boolean islogin = db.isLogin(user, pwa);
				String upmenustate = share.getString("upmenustate", "");
				if(islogin)
				{
					myapp.setPfprofileId(db.getLoginId(user, pwa));
//					if(userimg != null && !userimg.equals(""))
//					{
//						Bitmap bitmap = myapp.stringtoBitmap(userimg);
//						myapp.setUserimgbitmap(bitmap);
//					}
//					myapp.setUserName(user);
					
					myapp.setMyaccount(user);
					myapp.setUserName(username);
					myapp.setMysex(sex);
					myapp.setMyarea(area);
					myapp.setMySignature(signature);
					myapp.setThembgurl(thembgurl);
					myapp.setCompanyid(companyid);
					myapp.setUpMenuState(upmenustate);
					if(companyid != null && !companyid.equals(""))
						myapp.setIsServer(true);
					else
						myapp.setIsServer(false);
					
					makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+user+" " +myAnimation.this.getString(R.string.login_lable_4));
					
					showMyCards();
				}
				else
				{
					startLoginActivity();
				}
			}
			else
			{
				String user = share.getString("user", "");
				String pwa = share.getString("pwa", "");
				if(user != null && !user.equals(""))
				{
					loadThreadData(user,pwa);
				}
				else
				{
//					Intent intent=new Intent();
//					intent.setClass(myAnimation.this,LoginMain.class);
//					startActivity(intent);
//					myAnimation.this.finish();
					unrLoadThreadData("yin3","123");
				}
			}
		}
		
//		//定义splash   动画 
//        AlphaAnimation aa=new AlphaAnimation(0.1f,1.0f);
//        aa.setDuration(3000);
//        
//        this.findViewById(R.id.guide_picture).startAnimation(aa);
//        aa.setAnimationListener(new AnimationListener(){
//	
//				@Override
//				public void onAnimationEnd(Animation arg0) {
//					// TODO Auto-generated method stub
//					boolean autoLogin = share.getBoolean("autologin", true);
//					boolean b = StreamTool.isNetworkVailable(myAnimation.this);
//					if(!b)
//					{
//						String user = share.getString("user", "");
//						String pwa = share.getString("pwa", "");
//						boolean islogin = db.isLogin(user, pwa);
//						if(islogin)
//						{
//							myapp.setPfprofileId(db.getLoginId(user, pwa));
//							myapp.setUserName(user);
//							
//							makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+user+" " +myAnimation.this.getString(R.string.login_lable_4));
//							
//							showMyCards();
//						}
//						else
//						{
//							makeText(myAnimation.this.getString(R.string.login_lable_5));
//							Intent intent=new Intent();
//							intent.setClass(myAnimation.this,LoginMain.class);
//							startActivity(intent);
//							myAnimation.this.finish();
//						}
//					}
//					else
//					{
//						if(autoLogin)
//						{
//							String user = share.getString("user", "");
//							String pwa = share.getString("pwa", "");
//							if(user != null && !user.equals(""))
//							{
//								loadThreadData(user,pwa);
//							}
//							else
//							{
//								Intent intent=new Intent();
//								intent.setClass(myAnimation.this,LoginMain.class);
//								startActivity(intent);
//								myAnimation.this.finish();
//							}
//						}
//						else
//						{
//							Intent intent=new Intent();
//							intent.setClass(myAnimation.this,LoginMain.class);
//							startActivity(intent);
//							myAnimation.this.finish();
//						}
////						Intent intent=new Intent();
////						intent.setClass(myAnimation.this,MainActivity.class);
////						startActivity(intent);
////						myAnimation.this.finish();
//					}
//				}
//	
//				@Override
//				public void onAnimationRepeat(Animation arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//	
//				@Override
//				public void onAnimationStart(Animation arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//	        	
//	        }
//        );
        
//        timer.scheduleAtFixedRate(new TimerTask()  
//        {  
//            @Override  
//            public void run()  
//            {  
//                // TODO Auto-generated method stub  
//                Message mesasge = new Message();  
//                mesasge.what = 0;
//                handler.sendMessage(mesasge); 
//            }  
//        }, 3000, 3000);  
        
        
    }
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				timer.cancel();
				boolean autoLogin = share.getBoolean("autologin", true);
				if(autoLogin)
				{
					String user = share.getString("user", "");
					String pwa = share.getString("pwa", "");
					if(user != null && !user.equals(""))
					{
						loadThreadData(user,pwa);
					}
					else
					{
						startLoginActivity();
					}
				}
				else
				{
					startLoginActivity();
				}
				break;
			case 1:
				String str = (String)msg.obj;
				if(str.equals("0"))
				{
//					mypDialog.dismiss();
					makeText(myAnimation.this.getString(R.string.login_lable_6));
					startLoginActivity();
				}
				else if(str.equals("1"))
				{
//					mypDialog.dismiss();
					makeText(myAnimation.this.getString(R.string.login_lable_7));
					startLoginActivity();
				}
				else
				{
					makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+str + " "+myAnimation.this.getString(R.string.login_lable_4));
//					mypDialog.dismiss();
					showMyCards();
				}
				break;
			case 2:
				String str2 = (String)msg.obj;
				if(str2.equals("0"))
				{
//					makeText(myAnimation.this.getString(R.string.login_lable_6));
					startLoginActivity();
				}
				else if(str2.equals("1"))
				{
//					mypDialog.dismiss();
//					makeText(myAnimation.this.getString(R.string.login_lable_7));
					startLoginActivity();
				}
				else
				{
//					makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+str + " "+myAnimation.this.getString(R.string.login_lable_4));
//					mypDialog.dismiss();
					showMyCards();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	/**
	 * 显示店得分布地图
	 */
	public void showMyCards()
	{
		try{
			if(isShortCut){
				Intent intent = new Intent(myAnimation.this, MomentsActivity.class);
				intent.putExtra("title", title);
				intent.putExtra("type", type);
				intent.putExtra("bgImg", bgImgUrl);
				startActivity(intent);
				overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
				finish();
			}else{
				Intent intent = new Intent();
			    intent.setClass( this,MainTabActivity.class);
			    Bundle bundle = new Bundle();
//				bundle.putString("role", "Cleaner");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//关闭显示的Activity
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
//	public void loadThreadData(final String lname,final String paw)
//	{
//				
//				JSONObject jobj;
//				U.dout(0);
//				
//				try {
//					jobj = api.login(lname,paw);
//					if(jobj != null)
//					{
//						String tag = (String)jobj.get("tag");
//						if(tag.equals("Success"))
//						{
//							String sessionid = jobj.getString("sessionid");
//							myapp.setSessionId(sessionid);
//							String profileid = jobj.getString("profileid");
//							myapp.setPfprofileId(profileid);
//							String email = jobj.getString("email");
//							saveSharedPerferences("email",email);
//							saveSharedPerferences("sessionid",sessionid);
//							if(jobj.has("userimg"))
//							{
//								String userimg = jobj.getString("userimg");
//								myapp.setUserimg(userimg);
//							}
//							
//							String username = jobj.getString("username");
//							String sex = jobj.getString("sex");
//							String area = jobj.getString("area");
//							String signature = jobj.getString("signature");
//							myapp.setMyaccount(lname);
//							myapp.setUserName(username);
//							myapp.setMysex(sex);
//							myapp.setMyarea(area);
//							myapp.setMySignature(signature);
//							
//							makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+lname+" " +myAnimation.this.getString(R.string.login_lable_4));
//							showMyCards();
//						}
//						else
//						{
//							makeText(myAnimation.this.getString(R.string.login_lable_6));
//							Intent intent=new Intent();
//							intent.setClass(myAnimation.this,LoginMain.class);
//							startActivity(intent);
//							myAnimation.this.finish();
//						}
//					}
//					else
//					{
//						makeText(myAnimation.this.getString(R.string.login_lable_7));
//						Intent intent=new Intent();
//						intent.setClass(myAnimation.this,LoginMain.class);
//						startActivity(intent);
//						myAnimation.this.finish();
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//	}
	
	public void loadThreadData(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.login(lname,paw);
					if(jobj != null)
					{
						String tag = (String)jobj.get("tag");
						if(tag.equals("Success"))
						{
							String storeid = jobj.getString("storeid");
							if(!storeid.equals("")){
								myapp.setAppstoreid(storeid);
							}
							String upmenustate = jobj.getString("upmenustate");
							myapp.setUpMenuState(upmenustate);
							saveSharedPerferences("upmenustate",upmenustate);
							String sessionid = jobj.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj.getString("email");
							saveSharedPerferences("email",email);
							saveSharedPerferences("sessionid",sessionid);
							saveSharedPerferences("sessionidnfc",sessionid);
							myapp.setUserNameId(profileid);
							if(jobj.has("userimg"))
							{
								String userimg = jobj.getString("userimg");
								myapp.setUserimg(userimg);
							}
							
							String username = jobj.getString("username");
							String sex = jobj.getString("sex");
							String area = jobj.getString("area");
							String signature = jobj.getString("signature");
							String thembgurl = jobj.getString("thembgurl");
							String companyid = jobj.getString("companyid");
							myapp.setMyaccount(lname);
							myapp.setUserName(username);
							myapp.setMysex(sex);
							myapp.setMyarea(area);
							myapp.setMySignature(signature);
							myapp.setCompanyid(companyid);
							String themeurl = db.getThembgurl(lname, paw);
							if(themeurl != null && !themeurl.equals(""))
							{
								thembgurl = themeurl;
							}
							else
							{
//								lodaThemeImage(thembgurl);
								Bitmap bitmap = myapp.getImageBitmap(thembgurl);
								if(bitmap != null)
								{
									UUID uuid = UUID.randomUUID();
									if(!fileUtil.isFileExist2(myapp.getPfprofileId()))
										fileUtil.createUserFile(myapp.getPfprofileId());
									String furl = fileUtil.getImageFile1bPath(myapp.getPfprofileId(), uuid.toString());
									myapp.saveMyBitmap(furl, bitmap);
									thembgurl = furl;
								}
							}
							
							myapp.setThembgurl(thembgurl);
							saveUserPerferences(lname, paw);
							saveSharedPerferences("username",username);
							saveSharedPerferences("sex",sex);
							saveSharedPerferences("area",area);
							saveSharedPerferences("signature",signature);
							saveSharedPerferences("thembgurl",thembgurl);
							saveSharedPerferences("companyid",companyid);
							
							if(companyid != null && !companyid.equals(""))
								myapp.setIsServer(true);
							else
								myapp.setIsServer(false);
							
							db.save(lname, paw,thembgurl);
							
//							saveUserPerferences(lname, paw);
//							sScoket();
							//保存本地数据库
//							new Thread(){
//								public void run(){
//									db.save(lname, paw);
//								}
//							}.start();
							
							msg.obj = lname;
						}
						else
						{
							msg.obj = "0";
						}
					}
					else
					{
						msg.obj = "1";
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void unrLoadThreadData(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.unregisteredLogin(lname,paw);
					if(jobj != null)
					{
						String tag = (String)jobj.get("tag");
						if(tag.equals("Success"))
						{
							String sessionid = jobj.getString("sessionid");
							myapp.setSessionId(sessionid);
							saveSharedPerferences("sessionid",sessionid);
							saveSharedPerferences("sessionidnfc",sessionid);
							String profileid = jobj.getString("profileid");
							myapp.setPfprofileId(profileid);
							
							
//							saveUserPerferences(lname, paw);
							
							db.save(lname, paw,"");
							
							msg.obj = lname;
						}
						else
						{
							msg.obj = "0";
						}
					}
					else
					{
						msg.obj = "1";
					}
				}catch(Exception ex){
					ex.printStackTrace();
					msg.obj = "1";
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override  
    protected void onDestroy()  
    {  
        // TODO Auto-generated method stub  
        timer.cancel();  
        super.onDestroy();  
    } 
	
	public void onStop()  
	{  
		timer.cancel();  
		super.onStop();  
	}  
	
    //退出后Kill掉这个Activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
//			// 在2.2及以上中引入了该api来退出，但并不能退出2.2及以上的（所以只适用于2.2一下的）  
//			manager.killBackgroundProcesses(getPackageName());  
		}
		return super.onKeyDown(keyCode, event);

	}
	
	public void saveUserPerferences(String user,String pwa)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString("user", user);
		editor.putString("pwa", pwa);
		
		editor.commit();// 提交刷新数据
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	/**
	 * 绑定UI
	 */
	private void findViewById() {
		mShowPicture = (ImageView) findViewById(R.id.guide_picture);
		mShowText = (TextView) findViewById(R.id.guide_content);
		
		mShowPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mShowPicture.clearAnimation();
				startNextActivity();
			}
		});
	}
	
	private void startNextActivity(){
		boolean autoLogin = share.getBoolean("autologin", true);
		boolean b = StreamTool.isNetworkVailable(myAnimation.this);
		if(!b)
		{
			String user = share.getString("user", "");
			String pwa = share.getString("pwa", "");
			boolean islogin = db.isLogin(user, pwa);
			if(islogin)
			{
				myapp.setPfprofileId(db.getLoginId(user, pwa));
				myapp.setUserName(user);
				
				makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+user+" " +myAnimation.this.getString(R.string.login_lable_4));
				
				showMyCards();
			}
			else
			{
				makeText(myAnimation.this.getString(R.string.login_lable_5));
				startLoginActivity();
			}
		}
		else
		{
			if(autoLogin)
			{
				String user = share.getString("user", "");
				String pwa = share.getString("pwa", "");
				if(user != null && !user.equals(""))
				{
					loadThreadData(user,pwa);
				}
				else
				{
//					Intent intent=new Intent();
//					intent.setClass(myAnimation.this,LoginMain.class);
//					startActivity(intent);
//					myAnimation.this.finish();
					unrLoadThreadData("yin3","123");
				}
			}
			else
			{
				startLoginActivity();
			}
		}
	}

	/**
	 * 监听事件
	 */
	private void setListener() {
		/**
		 * 动画切换原理:开始时是用第一个渐现动画,当第一个动画结束时开始第二个放大动画,当第二个动画结束时调用第三个渐隐动画,
		 * 第三个动画结束时修改显示的内容并且重新调用第一个动画,从而达到循环效果
		 */
		mFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeInScale);
			}
		});
		mFadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeOut);
			}
		});
		mFadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				/**
				 * 这里其实有些写的不好,还可以采用更多的方式来判断当前显示的是第几个,从而修改数据,
				 * 我这里只是简单的采用获取当前显示的图片来进行判断。
				 */
				tag++;
				if(tag > 2)
				{
					startNextActivity();
				}
				else
				{
					if (mShowPicture.getDrawable().equals(mPicture_1)) {
						mShowText.setText("感谢您的入住");
						mShowPicture.setImageDrawable(mPicture_2);
					} else if (mShowPicture.getDrawable().equals(mPicture_2)) {
						mShowText.setText("我们将真诚的为您服务");
						mShowPicture.setImageDrawable(mPicture_3);
					} else if (mShowPicture.getDrawable().equals(mPicture_3)) {
						mShowText.setText("将余下的事交给我们");
						mShowPicture.setImageDrawable(mPicture_1);
					}
					mShowPicture.startAnimation(mFadeIn);
				}
			}
		});
	}

	/**
	 * 初始化
	 */
	private void init() {
		initAnim();
		initPicture();
		/**
		 * 界面刚开始显示的内容
		 */
		mShowPicture.setImageDrawable(mPicture_1);
		mShowText.setText("将余下的事交给我们");
		mShowPicture.startAnimation(mFadeIn);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.v5_0_1_guide_welcome_fade_in);
		mFadeIn.setDuration(1000);
		mFadeInScale = AnimationUtils.loadAnimation(this,
				R.anim.v5_0_1_guide_welcome_fade_in_scale);
		mFadeInScale.setDuration(6000);
		mFadeOut = AnimationUtils.loadAnimation(this,
				R.anim.v5_0_1_guide_welcome_fade_out);
		mFadeOut.setDuration(1000);
	}

	/**
	 * 初始化图片
	 */
	private void initPicture() {
		mPicture_1 = getResources().getDrawable(R.drawable.start_bg);
		mPicture_2 = getResources().getDrawable(R.drawable.start_bg);
		mPicture_3 = getResources().getDrawable(R.drawable.start_bg);
	}
	
	private void startLoginActivity(){
		Intent intent=new Intent();
		intent.setClass(myAnimation.this,LoginMain.class);
		intent.putExtra("isShortCut", true);
		intent.putExtra("title", title);
		intent.putExtra("type", type);
		intent.putExtra("bgImg", bgImgUrl);
		startActivity(intent);
		myAnimation.this.finish();
	}

}