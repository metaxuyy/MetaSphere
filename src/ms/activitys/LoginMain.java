package ms.activitys;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import ms.activitys.more.Settings;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LoginMain  extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private DBHelperLogin db;
	
	private ProgressDialog mypDialog;
	
	private ViewFlipper mViewFlipper;
	
	private String tag = "1";
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;  
	private String fileUrl = "";
	private String fileName = "";
	private long fileSize;
	private ImageView mImageView;
	private Bitmap bitmap;
	
	private File out;
	private Uri uri;
	
	private LocationManager locationManager;
	
	/*用来标识请求gallery的activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	
	/*用来标识请求照相功能的activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	
	/*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); 
    private File mCurrentPhotoFile;//照相机拍照得到的图片
    
    private int heightPixels;
    public static FileUtils fileUtil = new FileUtils();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = LoginMain.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
	
		db = new DBHelperLogin(this);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
//		SpannableString msp = new SpannableString("NOVO");
//		msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体 
//        msp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体 
//		TextView txt1 = (TextView)findViewById(R.id.txt_f);
//		txt1.setText(msp);
//		SpannableString msp2 = new SpannableString("MAMIA");
//		msp2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体 
//        msp2.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体 
//		TextView txt2 = (TextView)findViewById(R.id.txt_e);
//		txt2.setText(msp2);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(this, "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
		heightPixels = dm.heightPixels;
		
//		if(StreamTool.isNetworkVailable(this))
//		{
//			getGPSLocation();
//		}
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde != null)
		{
			int index = bunde.getInt("index");
			
			if(bunde.containsKey("nfc"))
			{
				Map map = myapp.getNfcStoreList().get(index);
				String username = (String)map.get("username");
				String password = (String)map.get("password");
				EditText loginname = (EditText)findViewById(R.id.loginName);
				EditText pasw = (EditText)findViewById(R.id.password);
				loginname.setText(username);
				pasw.setText(password);
				
				showProgressDialog();
				loadThreadDataNFC(username,password);
			}
			else
			{
				showMainHomePage();
			}
		}
		else
		{
			showMainHomePage();
		}
		
	}
	
	public void showMainHomePage()
	{
		try{
//			View view = findViewById(R.id.login_div);
//			view.getBackground().setAlpha(100);
			
			Button zhuche = (Button)findViewById(R.id.regedit_button);
//			String lable1 = this.getString(R.string.login_lable_1);
//			String lable2 = this.getString(R.string.login_lable_2);
//			String source = "<font color='#FFFFFF'>"+lable1+"<u><i>"+lable2+"</i></u></font>";
			   
//			zhuche.setText(Html.fromHtml(source));  
	        
			zhuche.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					showRegisterPage();
					Intent intent = new Intent();
				    intent.setClass( LoginMain.this,RegisterAction.class);
				    Bundle bundle = new Bundle();
//					bundle.putString("role", "Cleaner");
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			Button btn = (Button) findViewById(R.id.login_button);
			
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showProgressDialog();
					boolean b = StreamTool.isNetworkVailable(LoginMain.this);
					if(b)
					{
					
						// TODO Auto-generated method stub
						EditText loginname = (EditText)findViewById(R.id.loginName);
						EditText pasw = (EditText)findViewById(R.id.password);
						final String lname = loginname.getText().toString();
						final String paw = pasw.getText().toString();
						
						loadThreadData(lname,paw);
					}
					else
					{
						EditText loginname = (EditText)findViewById(R.id.loginName);
						EditText pasw = (EditText)findViewById(R.id.password);
						String lname = loginname.getText().toString();
						String paw = pasw.getText().toString();
						
						boolean islogin = db.isLogin(lname, paw);
						if(islogin)
						{
							myapp.setPfprofileId(db.getLoginId(lname, paw));
//							myapp.setUserName(lname);
							
							String sex = share.getString("sex", "");
							String area = share.getString("area", "");
							String signature = share.getString("signature", "");
							String thembgurl = share.getString("thembgurl", "");
							String username = share.getString("username", "");
							String companyid = share.getString("companyid", "");
							
//							String userimg = share.getString("userimg", "");
//							if(userimg != null && !userimg.equals(""))
//							{
//								Bitmap bitmap = myapp.stringtoBitmap(userimg);
//								myapp.setUserimgbitmap(bitmap);
//							}
							
							myapp.setMyaccount(lname);
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
							
							makeText(LoginMain.this.getString(R.string.login_lable_3)+ " " + lname + " "+LoginMain.this.getString(R.string.login_lable_4));
							
							showMyCards();
						}
						else
						{
							mypDialog.dismiss();
							makeText(LoginMain.this.getString(R.string.login_lable_5));
						}
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void onVisitorlogin(View v)
	{
		try{
			showProgressDialog();
			unrLoadThreadData("yin3","123");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
		         startMain.addCategory(Intent.CATEGORY_HOME);
		         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		         startActivity(startMain);
		         System.exit(0);
			return false;
		}
		return false;
	}
	
	public void loadThreadData(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
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
//							sScoket();
							//保存本地数据库
//							new Thread(){
//								public void run(){
//									db.save(lname, paw,thembgurl);
//								}
//							}.start();
							db.save(lname, paw,thembgurl);
							
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
				msg.what = 1;
				
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
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void lodaThemeImage(final String fileurl)
	{
		new Thread(){
			public void run(){
				Bitmap bitmap = myapp.getImageBitmap(fileurl);
				if(bitmap != null)
				{
					UUID uuid = UUID.randomUUID();
					if(!fileUtil.isFileExist2(myapp.getPfprofileId()))
						fileUtil.createMyFile(myapp.getPfprofileId());
					String furl = fileUtil.getImageFile1bPath(myapp.getPfprofileId(), uuid.toString());
					myapp.saveMyBitmap(furl, bitmap);
					myapp.setThembgurl(furl);
					saveSharedPerferences("thembgurl",furl);
					myapp.setThembgurl(furl);
				}
			}
		}.start();
	}
	
	public void loadThreadDataNFC(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
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
							myapp.setUserName(lname);
							if(jobj.has("userimg"))
							{
								String userimg = jobj.getString("userimg");
								myapp.setUserimg(userimg);
							}
//							sScoket();
							//保存本地数据库
							new Thread(){
								public void run(){
									db.save(lname, paw,"");
								}
							}.start();
							
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
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String str = (String)msg.obj;
				if(str.equals("0"))
				{
					mypDialog.dismiss();
					makeText(LoginMain.this.getString(R.string.login_lable_6));
				}
				else if(str.equals("1"))
				{
					mypDialog.dismiss();
					makeText(LoginMain.this.getString(R.string.login_lable_7));
				}
				else
				{
					makeText(LoginMain.this.getString(R.string.login_lable_3)+ " "+str + " "+LoginMain.this.getString(R.string.login_lable_4));
					mypDialog.dismiss();
					showMyCards();
				}
				break;
			case 1:
				String str2 = (String)msg.obj;
				if(str2.equals("0"))
				{
					mypDialog.dismiss();
					makeText(LoginMain.this.getString(R.string.login_lable_6));
				}
				else if(str2.equals("1"))
				{
					mypDialog.dismiss();
					makeText(LoginMain.this.getString(R.string.login_lable_7));
				}
				else
				{
//					makeText(myAnimation.this.getString(R.string.login_lable_3)+ " "+str + " "+myAnimation.this.getString(R.string.login_lable_4));
//					mypDialog.dismiss();
					mypDialog.dismiss();
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
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showRegisterPage()
	{
		tag = "2";
		try{
			mViewFlipper.showNext();
			
			final EditText loginname = (EditText)findViewById(R.id.loginName);
			
			final EditText email = (EditText)findViewById(R.id.email);
			
			final EditText paw = (EditText)findViewById(R.id.password);
			
			final EditText paw2 = (EditText)findViewById(R.id.password2);
			
			final CheckBox cbox = (CheckBox)findViewById(R.id.box2);
			
			mImageView = (ImageView)findViewById(R.id.info_image);
			
			Button imgbtn = (Button)findViewById(R.id.userPhotoBtn);
			imgbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openImageMenu();
				}
			});
			
			Button rbtn = (Button)findViewById(R.id.add_user_btn);
			rbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProgressDialog();
					String lname = loginname.getText().toString();
					String emailstr = email.getText().toString();
					String pawstr = paw.getText().toString();
					String pawstr2 = paw2.getText().toString();
					boolean b = true;
					
					if(lname == null || lname.equals(""))
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_8));
					}
					else if(emailstr == null || emailstr.equals(""))
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_9));
					}
					else if(pawstr == null || pawstr.equals(""))
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_10));
					}
					else if(pawstr2 == null || pawstr2.equals(""))
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_11));
					}
					else if(!pawstr2.equals(pawstr))
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_12));
					}
					else if(!cbox.isChecked())
					{
						b = false;
						makeText(LoginMain.this.getString(R.string.login_lable_13));
					}
					
					JSONObject jobj;
					U.dout(0);
					
					if(b)
					{
						try {
							jobj = api.saveRegister(lname,emailstr,pawstr);
							String tag = (String)jobj.get("success");
							if(tag.equals("true"))
							{
								mypDialog.dismiss();
								b = true;
								makeText(LoginMain.this.getString(R.string.login_lable_14));
							}
							else
							{
								mypDialog.dismiss();
								b = false;
								String msg = (String)jobj.get("msg");
								makeText(LoginMain.this.getString(R.string.login_lable_15) + msg);
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							b = false;
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							b = false;
							e.printStackTrace();
						}
					}
					
					
					
					if(b)
					{
						tag = "1";
						mViewFlipper.showPrevious();
					}
					else
					{
						mypDialog.dismiss();
					}
				}
			});
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tag = "1";
					mViewFlipper.showPrevious();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//	public void showRegisterPage(){
//		try{
//			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
//			final View view = LayoutInflater.from(this).inflate(R.layout.register,null);
//			
//			final EditText loginname = (EditText)view.findViewById(R.id.loginName);
//			
//			final EditText email = (EditText)view.findViewById(R.id.email);
//			
//			final EditText paw = (EditText)view.findViewById(R.id.password);
//			
//			final EditText paw2 = (EditText)view.findViewById(R.id.password2);
//			
//			final CheckBox cbox = (CheckBox)view.findViewById(R.id.box2);
//			
//			builder.setTitle("注册").setView(view).setPositiveButton("注册",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							showProgressDialog();
//							String lname = loginname.getText().toString();
//							String emailstr = email.getText().toString();
//							String pawstr = paw.getText().toString();
//							String pawstr2 = paw2.getText().toString();
//							boolean b = true;
//							
//							if(lname == null || lname.equals(""))
//							{
//								b = false;
//								makeText("用户名不能为空！");
//							}
//							else if(emailstr == null || emailstr.equals(""))
//							{
//								b = false;
//								makeText("Email不能为空！");
//							}
//							else if(pawstr == null || pawstr.equals(""))
//							{
//								b = false;
//								makeText("密码不能为空！");
//							}
//							else if(pawstr2 == null || pawstr2.equals(""))
//							{
//								b = false;
//								makeText("确认密码不能为空！");
//							}
//							else if(!pawstr2.equals(pawstr))
//							{
//								b = false;
//								makeText("密码与确认密码不一致！");
//							}
//							else if(!cbox.isChecked())
//							{
//								b = false;
//								makeText("请选择我已阅读并同意！");
//							}
//							
//							JSONObject jobj;
//							U.dout(0);
//							
//							try {
//								jobj = api.saveRegister(lname,emailstr,pawstr);
//								String tag = (String)jobj.get("success");
//								if(tag.equals("true"))
//								{
//									mypDialog.dismiss();
//									b = true;
//									makeText("注册成功！");
//								}
//								else
//								{
//									mypDialog.dismiss();
//									b = false;
//									String msg = (String)jobj.get("msg");
//									makeText("注册失败！" + msg);
//									
//								}
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								b = false;
//								e.printStackTrace();
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								b = false;
//								e.printStackTrace();
//							}
//							
//							
//							
//							if(b)
//							{
//								try{
//									Field field = dialog.getClass()
//						            .getSuperclass().getDeclaredField(
//						                     "mShowing" );
//								    field.setAccessible( true );
//								     //   将mShowing变量设为false，表示对话框已关闭
//								    field.set(dialog, true );
//								    dialog.dismiss();
//								}catch(Exception ex){
//									ex.printStackTrace();
//								}
//							}
//							else
//							{
//								try{
//									Field field = dialog.getClass()
//						            .getSuperclass().getDeclaredField(
//						                     "mShowing" );
//								    field.setAccessible( true );
//								     //   将mShowing变量设为false，表示对话框已关闭
//								    field.set(dialog, false );
//								    dialog.dismiss();
//								}catch(Exception ex){
//									ex.printStackTrace();
//								}
//							}
//						}
//					}).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							try{
//								Field field = dialog.getClass()
//					            .getSuperclass().getDeclaredField(
//					                     "mShowing" );
//							    field.setAccessible( true );
//							     //   将mShowing变量设为false，表示对话框已关闭
//							    field.set(dialog, true );
//							    dialog.dismiss();
//							}catch(Exception ex){
//								ex.printStackTrace();
//							}
//						}
//					});
//			builder.show();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 1, LoginMain.this.getString(R.string.login_lable_16)).setIcon(R.drawable.gongju);
//		menu.add(0, Menu.FIRST + 6, 7, "Void").setIcon(R.drawable.camera);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		U.dout(item.getItemId());
		switch (item.getItemId()) {		
		case 1:
			Intent intent = new Intent();
		    intent.setClass( LoginMain.this,Settings.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			break;
		}
		return true;
	}
	
	/**
	 * 配置文件
	 * @param str
	 */
	public void waboutSharedPerferences(String str)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString("ipadrees", str);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	/**
	 * 是否需要加载网络图片
	 * @param str 1为否，0为是
	 */
	public void isLoadWebImage(String str)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString("webimage", str);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	/**
	 * 是否需要每次登录时同步网络数据
	 * @param str 1为否，0为是
	 */
	public void isLoadWebData(String str)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString("webdata", str);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public void saveUserPerferences(String user,String pwa)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString("user", user);
		editor.putString("pwa", pwa);
		
		editor.commit();// 提交刷新数据
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void getGPSLocation()
    {
    	double latitude,longitude =0.0;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(serviceName);
//        String provider = LocationManager.GPS_PROVIDER;
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) { 
            return; 
        } 
        
        Location location = locationManager.getLastKnownLocation(provider);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
//        double altitude =  location.getAltitude();     //海拔  
        locationManager.requestLocationUpdates(provider, 0, 1000,locationListener);
        updateWithNewLocation(location);
    }
	
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void updateWithNewLocation(Location location) {
		String latLongString = null;
		if (location != null) {
			final double lat = location.getLatitude();
			final double lng = location.getLongitude();
			new Thread() {
				public void run() {
					GeoPoint gp = null;
					String url = "http://www.anttna.com/goffset/goffset1.php?lat="+lat+"&lon="+lng;  

					System.out.println("mapurl===="+url);
					HttpGet get = new HttpGet(url);  
					String strResult = "";  
					try {  
					    HttpParams httpParameters = new BasicHttpParams();  
					    HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);  
					    HttpClient httpClient = new DefaultHttpClient(httpParameters);   
					      
					    HttpResponse httpResponse = null;  
					    httpResponse = httpClient.execute(get);  
					      
					    if (httpResponse.getStatusLine().getStatusCode() == 200){  
					        strResult = EntityUtils.toString(httpResponse.getEntity());  
					    }  
					} catch (Exception e) {  
					    e.printStackTrace();
					}  
					
					JSONObject json = null;
					if(strResult != null && !strResult.equals(""))
					{
						String [] strs = strResult.split(",");
						int lat2 = (int)(Double.valueOf(strs[0]) * 1E6);
						int lon2 = (int)(Double.valueOf(strs[1]) * 1E6);
						
						gp =  new GeoPoint(lat2, lon2);
						
						double lat = gp.getLatitudeE6() / 1E6;
						double lng = gp.getLongitudeE6() / 1E6;
						
						json = geocodeAddr(lat,lng);
						
						myapp.setLatitude(gp.getLatitudeE6());
						myapp.setLongitude(gp.getLongitudeE6());
						myapp.setLat(String.valueOf(lat));
						myapp.setLng(String.valueOf(lng));
					}
					else
					{
						myapp.setLat(String.valueOf(lat));
						myapp.setLng(String.valueOf(lng));
						json = geocodeAddr(lat,lng);
						int lats = (int)(lat * 1E6);
				        int lons = (int)(lng * 1E6);
				        myapp.setLatitude(lats);
						myapp.setLongitude(lons);
					}
					
					
					String CountryName = ""; //国家名字
					String CountryNameCode = ""; //国家代码
					String LocalityName = ""; //城市名
					String ThoroughfareName = ""; //路名
					String quhao = "";//区号
					String menpai = ""; //门牌号
					try {
						if(json != null)
						{
							JSONArray results = json.getJSONArray("results");
//							latLongString = results.getJSONObject(0).getString("formatted_address");
							if(results.length() > 0)
							{
								CountryName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name");
								CountryNameCode = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("short_name");
								LocalityName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");
								ThoroughfareName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
								quhao = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");
								menpai = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
								System.out.println("国家名字===="+CountryName);
								myapp.setCountry(CountryName);
								System.out.println("国家代码===="+CountryNameCode);
								System.out.println("城市名===="+LocalityName);
								myapp.setCity(LocalityName);
								System.out.println("路名===="+ThoroughfareName);
								myapp.setRoad(ThoroughfareName);
								System.out.println("区名===="+quhao);
								myapp.setArea(quhao);
								System.out.println("门牌号===="+menpai);
								myapp.setNumbers(menpai);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			latLongString = this.getString(R.string.login_lable_17);
		}
		System.out.println("您当前的位置是:\n" + latLongString);
	}
	
	private static JSONObject geocodeAddr(double lat, double lng) {
//		String urlString = "http://ditu.google.com/maps/geo?q=+" + lat + ","+ lng + "&output=json&oe=utf8&hl=zh-CN&sensor=false";
		String urlString = "http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&language=zh_CN&sensor=false";
		
		StringBuilder sTotalString = new StringBuilder();
		try {

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			InputStream urlStream = httpConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlStream));

			String sCurrentLine = "";
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				sTotalString.append(sCurrentLine);
			}
			bufferedReader.close();
			httpConnection.disconnect(); // 关闭http连接

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(sTotalString.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}
	
	public void openImageMenu()
	{
		final CharSequence [] items = { this.getString(R.string.login_lable_18) , this.getString(R.string.login_lable_19)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.login_lable_20));
		builder.setItems ( items , new DialogInterface.OnClickListener () {
		    public void onClick ( DialogInterface dialog , int item ) {
		        if(item == 0)
		        {
		        	final Intent intent = getPhotoPickIntent();
		        	startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		        }
		        else if(item == 1)
		        {
//		        	String status=Environment.getExternalStorageState();  
//		        	if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡
//		        		
//		        	}
//		        	else
//		        	{
//		        		makeText("没有SDK");
//		        	}
		        	if(heightPixels > 1000)
		        	{
		            	openImageCamera();
		        	}
		            else
		            {
		            	doTakePhoto();
		            }
		        }
		    }
		});
		builder.show();
	
	}
	
	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	
	public static Intent getTakePickIntent(File f) {  
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
        return intent;  
    }  
	
	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-ddHHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}
	
	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        switch (requestCode) {  
            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的  
                final Bitmap photo = data.getParcelableExtra("data");  
                // 下面就是显示照片了  
                System.out.println(photo);  
                //缓存用户选择的图片  
//                img = getBitmapByte(photo);  
//                mEditor.setPhotoBitmap(photo);
                mImageView.setImageBitmap(photo);
                break;  
            }  
            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
            	doCropPhoto(mCurrentPhotoFile);
                break;  
            }
            case 1:{
//            	if(uri != null)
//            		makeText("uri=="+uri);
//            	else
//            		makeText("uri为空");
//            	doCropPhoto2(uri);
            	final Intent intent = getPhotoPickIntent();
	        	startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
            	break;
            }
        }  
    } 
    
    public void openImageCamera()
	{
    	/**  
    	* 由于Camara返回的是缩略图，我们可以传递给他一个参数EXTRA_OUTPUT,  
    	* 来将用Camera获取到的图片存储在一个指定的URI位置处。  
    	* 下面就指定image存储在SDCard上，并且文件名为123.jpg  
    	* imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"123.jpg";  
    	* File file = new File(imageFilePath); //创建一个文件  
    	* Uri imageUri = Uri.fromFile(file);  
    	* 然而Android已经提供了一个多媒体库，那里统一存放了设备上所有的多媒体数据。所以，  
    	* 我们可以将获取到的图片存放在那个多媒体库中。  
    	* Android提供了MediaStore类，该类是一个ContentProvider，管理着设备上自带的和外部的多媒体文件，  
    	* 同时包含着每一个多媒体文件的数据信息。  
    	* 为了将数据存储在多媒体库，使用ContentResolver对象来操纵MediaStore对象  
    	* 在MediaStore.Images.Media中有两个URI常量，一个是 EXTERNAL_CONTENT_URI,另一个是INTERNAL_CONTENT_URI  
    	* 第一个URI对应着外部设备(SDCard)，第二个URI对应着系统设备内部存储位置。  
    	* 对于多媒体文件，一般比较大，我们选择外部存储方式  
    	* 通过使用ContentResolver对象的insert方法我们可以向MediaStore中插入一条数据  
    	* 这样在检索那张图片的时候，不再使用文件的路径，而是根据insert数据时返回的URI，获取一个InputStream  
    	* 并传给BitmapFactory  
    	*/   
    	//在这里启动Camera。   
    	//Camera中定义了一个Intent-Filter，其中Action是android.media.action.IMAGE_CAPTURE   
    	//我们使用的时候，最好不要直接使用这个，而是用MediaStore中的常量ACTION_IMAGE_CAPTURE.   
    	//这个常量就是对应的上面的action   
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
    	//这里我们插入一条数据，ContentValues是我们希望这条记录被创建时包含的数据信息   
    	//这些数据的名称已经作为常量在MediaStore.Images.Media中,有的存储在MediaStore.MediaColumn中了   
    	//ContentValues values = new ContentValues();   
    	ContentValues values = new ContentValues(3);   
    	values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");   
    	values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");   
    	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");   
    	uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);   
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);   
    	//这样就将文件的存储方式和uri指定到了Camera应用中   
    	//由于我们需要调用完Camera后，可以返回Camera获取到的图片，   
    	//所以，我们使用startActivityForResult来启动Camera   
    	startActivityForResult(intent, 1);   
	}
    
    protected void doCropPhoto(File f) {  
        try {  
            // 启动gallery去剪辑这个照片  
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
        } catch (Exception e) {  
            Toast.makeText(this, e.getMessage()+"doCropPhoto()",  
                    Toast.LENGTH_LONG).show();  
        }  
    } 
    
    protected void doCropPhoto2(Uri uri) {
        try {  
            // 启动gallery去剪辑这个照片  
            final Intent intent = getCropImageIntent(uri);
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
        } catch (Exception e) {  
            Toast.makeText(this, e.getMessage()+"doCropPhoto()2",  
                    Toast.LENGTH_LONG).show();  
        }  
    } 
    
    /**  
     * Constructs an intent for image cropping. 调用图片剪辑程序  
     */  
     public static Intent getCropImageIntent(Uri photoUri) {  
         Intent intent = new Intent("com.android.camera.action.CROP");  
         intent.setDataAndType(photoUri, "image/*");  
         intent.putExtra("crop", "true");  
         intent.putExtra("aspectX", 1);  
         intent.putExtra("aspectY", 1);  
         intent.putExtra("outputX", 80);  
         intent.putExtra("outputY", 80);  
         intent.putExtra("return-data", true);  
         return intent;  
     }  
     
     public void showProgressDialog(){
 		try{
 			mypDialog=new ProgressDialog(this);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(this.getString(R.string.login_lable_21));
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
             //设置ProgressDialog 的一个Button
             mypDialog.setIndeterminate(false);
             //设置ProgressDialog 的进度条是否不明确
             mypDialog.setCancelable(true);
             //设置ProgressDialog 是否可以按退回按键取消
             mypDialog.show();
             //让ProgressDialog显示
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
}
