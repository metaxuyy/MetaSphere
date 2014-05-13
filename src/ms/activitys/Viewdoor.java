package ms.activitys;



import java.util.Locale;
import java.util.UUID;

import org.json.JSONObject;

import ms.activitys.hotel.MomentsActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class Viewdoor extends Activity {
	
	private ImageView mLeft;
	private ImageView mRight;
	
	private Douban api;
	private MyApp myapp;
	private DBHelperLogin db;
	private SharedPreferences  share;
	public static FileUtils fileUtil = new FileUtils();
	private boolean isShortCut;
	private String title;
	private String type;
	private String bgImgUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdoor);
        
        mLeft = (ImageView)findViewById(R.id.imageLeft);
        mRight = (ImageView)findViewById(R.id.imageRight);
        
        myapp = (MyApp)this.getApplicationContext();
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperLogin(this);
		
		Intent mIntent = getIntent();
        isShortCut = mIntent.getBooleanExtra("isShortCut", false);
		title = mIntent.getStringExtra("title");
		type = mIntent.getStringExtra("type");
		bgImgUrl = mIntent.getStringExtra("bgImg");
        
        AnimationSet anim = new AnimationSet(true);
		TranslateAnimation mytranslateanim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		mytranslateanim.setDuration(2000);
		anim.addAnimation(mytranslateanim);
		anim.setFillAfter(true);
		mLeft.startAnimation(anim);
		
		AnimationSet anim1 = new AnimationSet(true);
		TranslateAnimation mytranslateanim1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		mytranslateanim1.setDuration(1500);
		anim1.addAnimation(mytranslateanim1);
		anim1.setFillAfter(true);
		mRight.startAnimation(anim1);
		
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
//				Intent intent = new Intent (Viewdoor.this,Welcome.class);			
//				startActivity(intent);			
//				Viewdoor.this.finish();
				login();
			}
		}, 1500);
    }

    public void login()
    {
    	try{
    		boolean b = StreamTool.isNetworkVailable(Viewdoor.this);
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
					if(companyid != null && !companyid.equals(""))
						myapp.setIsServer(true);
					else
						myapp.setIsServer(false);
					
					makeText(Viewdoor.this.getString(R.string.login_lable_3)+ " "+user+" " +Viewdoor.this.getString(R.string.login_lable_4));
					
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
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
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
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
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
					makeText(Viewdoor.this.getString(R.string.login_lable_6));
					startLoginActivity();
				}
				else if(str.equals("1"))
				{
//					mypDialog.dismiss();
					makeText(Viewdoor.this.getString(R.string.login_lable_7));
					startLoginActivity();
				}
				else
				{
					makeText(Viewdoor.this.getString(R.string.login_lable_3)+ " "+str + " "+Viewdoor.this.getString(R.string.login_lable_4));
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
				Intent intent = new Intent(Viewdoor.this, MomentsActivity.class);
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
	
	private void startLoginActivity(){
		Intent intent=new Intent();
		intent.setClass(Viewdoor.this,LoginMain.class);
		intent.putExtra("isShortCut", true);
		intent.putExtra("title", title);
		intent.putExtra("type", type);
		intent.putExtra("bgImg", bgImgUrl);
		startActivity(intent);
		finish();
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
    
    public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
