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
	
	/*������ʶ����gallery��activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	
	/*������ʶ�������๦�ܵ�activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	
	/*���յ���Ƭ�洢λ��*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); 
    private File mCurrentPhotoFile;//��������յõ���ͼƬ
    
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
//		msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //���� 
//        msp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //б�� 
//		TextView txt1 = (TextView)findViewById(R.id.txt_f);
//		txt1.setText(msp);
//		SpannableString msp2 = new SpannableString("MAMIA");
//		msp2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //���� 
//        msp2.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //б�� 
//		TextView txt2 = (TextView)findViewById(R.id.txt_e);
//		txt2.setText(msp2);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(this, "��Ļ�ֱ���Ϊ:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
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
				    startActivity(intent);//��ʼ�������ת����
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
							//���汾�����ݿ�
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
							//���汾�����ݿ�
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
	 * ��ʾ��÷ֲ���ͼ
	 */
	public void showMyCards()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//��ʼ�������ת����
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    this.finish();//�ر���ʾ��Activity
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
//			builder.setTitle("ע��").setView(view).setPositiveButton("ע��",
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
//								makeText("�û�������Ϊ�գ�");
//							}
//							else if(emailstr == null || emailstr.equals(""))
//							{
//								b = false;
//								makeText("Email����Ϊ�գ�");
//							}
//							else if(pawstr == null || pawstr.equals(""))
//							{
//								b = false;
//								makeText("���벻��Ϊ�գ�");
//							}
//							else if(pawstr2 == null || pawstr2.equals(""))
//							{
//								b = false;
//								makeText("ȷ�����벻��Ϊ�գ�");
//							}
//							else if(!pawstr2.equals(pawstr))
//							{
//								b = false;
//								makeText("������ȷ�����벻һ�£�");
//							}
//							else if(!cbox.isChecked())
//							{
//								b = false;
//								makeText("��ѡ�������Ķ���ͬ�⣡");
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
//									makeText("ע��ɹ���");
//								}
//								else
//								{
//									mypDialog.dismiss();
//									b = false;
//									String msg = (String)jobj.get("msg");
//									makeText("ע��ʧ�ܣ�" + msg);
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
//								     //   ��mShowing������Ϊfalse����ʾ�Ի����ѹر�
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
//								     //   ��mShowing������Ϊfalse����ʾ�Ի����ѹر�
//								    field.set(dialog, false );
//								    dialog.dismiss();
//								}catch(Exception ex){
//									ex.printStackTrace();
//								}
//							}
//						}
//					}).setNegativeButton("ȡ��",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							try{
//								Field field = dialog.getClass()
//					            .getSuperclass().getDeclaredField(
//					                     "mShowing" );
//							    field.setAccessible( true );
//							     //   ��mShowing������Ϊfalse����ʾ�Ի����ѹر�
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
		    startActivity(intent);//��ʼ�������ת����
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			break;
		}
		return true;
	}
	
	/**
	 * �����ļ�
	 * @param str
	 */
	public void waboutSharedPerferences(String str)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString("ipadrees", str);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	/**
	 * �Ƿ���Ҫ��������ͼƬ
	 * @param str 1Ϊ��0Ϊ��
	 */
	public void isLoadWebImage(String str)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString("webimage", str);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	/**
	 * �Ƿ���Ҫÿ�ε�¼ʱͬ����������
	 * @param str 1Ϊ��0Ϊ��
	 */
	public void isLoadWebData(String str)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString("webdata", str);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	public void saveUserPerferences(String user,String pwa)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString("user", user);
		editor.putString("pwa", pwa);
		
		editor.commit();// �ύˢ������
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
//        double altitude =  location.getAltitude();     //����  
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
					
					
					String CountryName = ""; //��������
					String CountryNameCode = ""; //���Ҵ���
					String LocalityName = ""; //������
					String ThoroughfareName = ""; //·��
					String quhao = "";//����
					String menpai = ""; //���ƺ�
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
								System.out.println("��������===="+CountryName);
								myapp.setCountry(CountryName);
								System.out.println("���Ҵ���===="+CountryNameCode);
								System.out.println("������===="+LocalityName);
								myapp.setCity(LocalityName);
								System.out.println("·��===="+ThoroughfareName);
								myapp.setRoad(ThoroughfareName);
								System.out.println("����===="+quhao);
								myapp.setArea(quhao);
								System.out.println("���ƺ�===="+menpai);
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
		System.out.println("����ǰ��λ����:\n" + latLongString);
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
			httpConnection.disconnect(); // �ر�http����

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
//		        	if(status.equals(Environment.MEDIA_MOUNTED)){//�ж��Ƿ���SD��
//		        		
//		        	}
//		        	else
//		        	{
//		        		makeText("û��SDK");
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
	 * ���ջ�ȡͼƬ
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// �����յ���Ƭ�ļ�����
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
	 * �õ�ǰʱ���ȡ�õ�ͼƬ����
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
	
	// ��Ϊ������Camera��Gally����Ҫ�ж����Ǹ��Եķ������,��������ʱ��������startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        switch (requestCode) {  
            case PHOTO_PICKED_WITH_DATA: {// ����Gallery���ص�  
                final Bitmap photo = data.getParcelableExtra("data");  
                // ���������ʾ��Ƭ��  
                System.out.println(photo);  
                //�����û�ѡ���ͼƬ  
//                img = getBitmapByte(photo);  
//                mEditor.setPhotoBitmap(photo);
                mImageView.setImageBitmap(photo);
                break;  
            }  
            case CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ  
            	doCropPhoto(mCurrentPhotoFile);
                break;  
            }
            case 1:{
//            	if(uri != null)
//            		makeText("uri=="+uri);
//            	else
//            		makeText("uriΪ��");
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
    	* ����Camara���ص�������ͼ�����ǿ��Դ��ݸ���һ������EXTRA_OUTPUT,  
    	* ������Camera��ȡ����ͼƬ�洢��һ��ָ����URIλ�ô���  
    	* �����ָ��image�洢��SDCard�ϣ������ļ���Ϊ123.jpg  
    	* imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"123.jpg";  
    	* File file = new File(imageFilePath); //����һ���ļ�  
    	* Uri imageUri = Uri.fromFile(file);  
    	* Ȼ��Android�Ѿ��ṩ��һ����ý��⣬����ͳһ������豸�����еĶ�ý�����ݡ����ԣ�  
    	* ���ǿ��Խ���ȡ����ͼƬ������Ǹ���ý����С�  
    	* Android�ṩ��MediaStore�࣬������һ��ContentProvider���������豸���Դ��ĺ��ⲿ�Ķ�ý���ļ���  
    	* ͬʱ������ÿһ����ý���ļ���������Ϣ��  
    	* Ϊ�˽����ݴ洢�ڶ�ý��⣬ʹ��ContentResolver����������MediaStore����  
    	* ��MediaStore.Images.Media��������URI������һ���� EXTERNAL_CONTENT_URI,��һ����INTERNAL_CONTENT_URI  
    	* ��һ��URI��Ӧ���ⲿ�豸(SDCard)���ڶ���URI��Ӧ��ϵͳ�豸�ڲ��洢λ�á�  
    	* ���ڶ�ý���ļ���һ��Ƚϴ�����ѡ���ⲿ�洢��ʽ  
    	* ͨ��ʹ��ContentResolver�����insert�������ǿ�����MediaStore�в���һ������  
    	* �����ڼ�������ͼƬ��ʱ�򣬲���ʹ���ļ���·�������Ǹ���insert����ʱ���ص�URI����ȡһ��InputStream  
    	* ������BitmapFactory  
    	*/   
    	//����������Camera��   
    	//Camera�ж�����һ��Intent-Filter������Action��android.media.action.IMAGE_CAPTURE   
    	//����ʹ�õ�ʱ����ò�Ҫֱ��ʹ�������������MediaStore�еĳ���ACTION_IMAGE_CAPTURE.   
    	//����������Ƕ�Ӧ�������action   
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
    	//�������ǲ���һ�����ݣ�ContentValues������ϣ��������¼������ʱ������������Ϣ   
    	//��Щ���ݵ������Ѿ���Ϊ������MediaStore.Images.Media��,�еĴ洢��MediaStore.MediaColumn����   
    	//ContentValues values = new ContentValues();   
    	ContentValues values = new ContentValues(3);   
    	values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");   
    	values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");   
    	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");   
    	uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);   
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);   
    	//�����ͽ��ļ��Ĵ洢��ʽ��uriָ������CameraӦ����   
    	//����������Ҫ������Camera�󣬿��Է���Camera��ȡ����ͼƬ��   
    	//���ԣ�����ʹ��startActivityForResult������Camera   
    	startActivityForResult(intent, 1);   
	}
    
    protected void doCropPhoto(File f) {  
        try {  
            // ����galleryȥ���������Ƭ  
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
            // ����galleryȥ���������Ƭ  
            final Intent intent = getCropImageIntent(uri);
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
        } catch (Exception e) {  
            Toast.makeText(this, e.getMessage()+"doCropPhoto()2",  
                    Toast.LENGTH_LONG).show();  
        }  
    } 
    
    /**  
     * Constructs an intent for image cropping. ����ͼƬ��������  
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
             //ʵ����
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //���ý�������񣬷��ΪԲ�Σ���ת��
//             mypDialog.setTitle("�ȴ�");
             //����ProgressDialog ����
             mypDialog.setMessage(this.getString(R.string.login_lable_21));
             //����ProgressDialog ��ʾ��Ϣ
//             mypDialog.setIcon(R.drawable.wait_icon);
             //����ProgressDialog ����ͼ��
//             mypDialog.setButton("",this);
             //����ProgressDialog ��һ��Button
             mypDialog.setIndeterminate(false);
             //����ProgressDialog �Ľ������Ƿ���ȷ
             mypDialog.setCancelable(true);
             //����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
             mypDialog.show();
             //��ProgressDialog��ʾ
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
}
