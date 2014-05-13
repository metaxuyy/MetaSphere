package ms.activitys;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ms.globalclass.FileUtils;
import ms.globalclass.FormFile;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RegisterAction extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private DBHelperMessage db;
	
//	private ProgressDialog mypDialog;
	private MyLoadingDialog loadDialog;
	
	private String tag = "1";
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;  
	private String fileUrl = "";
	private String fileName = "";
	private long fileSize;
	private RoundAngleImageView mImageView;
	private Bitmap bitmap;
	
	private File out;
	private Uri uri;
	
	private String userName;
	private String password;
	private DBHelperLogin logindb;
	public static FileUtils fileUtil = new FileUtils();
	
	/*用来标识请求gallery的activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	
	/*用来标识请求照相功能的activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	
	/*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); 
    private File mCurrentPhotoFile;//照相机拍照得到的图片
    
    private int heightPixels;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
	
		db = new DBHelperMessage(this,myapp);
		logindb = new DBHelperLogin(this);
		
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(this, "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
		heightPixels = dm.heightPixels;
		
		
		showRegisterPage();
		
	}
    
    public void showRegisterPage()
	{
		tag = "2";
		try{
			final EditText loginname = (EditText)findViewById(R.id.loginName);
			
			final EditText email = (EditText)findViewById(R.id.email);
			
			final EditText paw = (EditText)findViewById(R.id.password);
			
			final EditText paw2 = (EditText)findViewById(R.id.password2);
			
			final CheckBox cbox = (CheckBox)findViewById(R.id.box2);
			
			mImageView = (RoundAngleImageView)findViewById(R.id.info_image);
			
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
					final String lname = loginname.getText().toString();
					final String emailstr = email.getText().toString();
					final String pawstr = paw.getText().toString();
					String pawstr2 = paw2.getText().toString();
					boolean b = true;
					
					if(lname == null || lname.equals(""))
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_8));
					}
					else if(emailstr == null || emailstr.equals(""))
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_9));
					}
					else if(pawstr == null || pawstr.equals(""))
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_10));
					}
					else if(pawstr2 == null || pawstr2.equals(""))
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_11));
					}
					else if(!pawstr2.equals(pawstr))
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_12));
					}
					else if(!cbox.isChecked())
					{
						b = false;
						makeText(RegisterAction.this.getString(R.string.login_lable_13));
					}
					
					if(b)
					{
						showProgressDialog();
						new Thread() {
							public void run() {
								Message msg = new Message();
								msg.what = 0;
								
								Map<String,Object> map = register(lname,emailstr,pawstr);
								
								msg.obj = map;
								handler.sendMessage(msg);
							}
						}.start();
					}
				}
			});
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tag = "1";
					Intent intent = new Intent();
				    intent.setClass(RegisterAction.this,LoginMain.class);
				    Bundle bundle = new Bundle();
//					bundle.putString("role", "Cleaner");
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
				    RegisterAction.this.finish();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
    public Map<String,Object> register(String lname,String emailstr,String pawstr)
	{
		boolean tag = true;
		String msg = "";
		String pfid = "";
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phonnumber = tm.getLine1Number();
			if(phonnumber == null)
				phonnumber = "";
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("lname", lname);
			params.put("email", emailstr);
			params.put("pawstr", pawstr);
			params.put("phonnumber", phonnumber);
			params.put("businessid", myapp.getBusinessid());
			params.put("storeid", myapp.getAppstoreid());
			String isservice = "1";
			if(myapp.getIsServer())
				isservice = "0";
			params.put("isService", isservice);
			long fsize = 0;
			Map<String, File> files = new HashMap<String, File>();
			if(fileUrl != null && !fileUrl.equals(""))
			{
				File file = new File(fileUrl);
				long filesize = file.length();
				if(filesize > 358400) //filesize / 1024得到文件KB单位大小
				{
					makeText(RegisterAction.this.getString(R.string.image_sizeerroe_lable));
					tag = false;
				}
				fsize = fsize + filesize;
				makeText("filesize==="+filesize);
				files.put(file.getName(), file);
			}
			
			if(tag)
			{
				JSONObject job = api.uploadFilesRegister(params,files);
				if(job != null)
				{
					if (job.getString("success").equals("true")) {
//						mypDialog.dismiss();
						tag = true;
						pfid = job.getString("pkid");
						JSONArray jarry = job.getJSONArray("stroelist");
						List<Map<String,String>> slist = getStroeList(jarry);
						map.put("slist", slist);
						userName = lname;
						password = pawstr;
//						makeText("注册成功！");
					} else {
						msg = (String)job.get("msg");
//						mypDialog.dismiss();
						tag = false;
//						makeText("注册失败原因："+msg);
					}
				}
				else
				{
//					mypDialog.dismiss();
					tag = false;
//					makeText("注册失败！");
				}
			}
			map.put("tag", tag);
			map.put("message", msg);
			map.put("pfid", pfid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
    
    public void addCardListData(final List<Map<String,String>> dlist,final String pfid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
						int j = 0;
						String pfid = "";
						for(int i=0;i<dlist.size();i++)
						{
							Map<String,String> map = dlist.get(i);
							pfid = map.get("pfid");
							String pid = map.get("pid");
							String bid = map.get("bid");
							String storeTyle = map.get("storeTyle");
							String province = map.get("province");
							JSONObject jobj = api.addCards(pfid,pid,bid,storeTyle,province);
							if(jobj != null)
							{
								j++;
							}
						}
						if(j == dlist.size())
							msg.obj = true;
						else
							msg.obj = false;
//						JSONObject jobj = api.registereSendMessage(pfid,myapp.getAppstoreid());
//						if(jobj != null)
//						{
//							SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//							List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//							String mid = jobj.getString("mid");
//							String toid = jobj.getString("toid");
//							String sender = jobj.getString("sender");
//							String fname = jobj.getString("fname");
//							String tname = jobj.getString("tname");
//							String context = jobj.getString("context");
//							String datetime = sf.format(new Date());
//							Map<String, Object> map = new HashMap<String, Object>();
//							map.put("mid", mid);
//							map.put("toid", sender);
//							map.put("mysendname", toid);
//							map.put("fname", tname);
//							map.put("tname", fname);
//							map.put("yiman", R.drawable.yi_man);
//							map.put("mymessagecontent", context);
//							map.put("mysendtime",datetime);
//							map.put("fileUrl","");
//							map.put("fileUrl2","");
//							map.put("toname", "");
//							map.put("fileType", "");
//							map.put("fileType2", "");
//							map.put("fileName", "");
//							map.put("fileName2", "");
//							map.put("time", "");
//							map.put("timetext", "");
//							map.put("messagetype", "");
//							map.put("sendimg", "1");
//							map.put("sendprogress", "1");
//							list.add(map);
//							
//							db.openDB();
//							db.saveMessageData(list);
//							db.closeDB();
//							
//							msg.obj = true;
//						}
//						else
//							msg.obj = false;
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
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				boolean b = (Boolean)map.get("tag");
				String mssage = (String)map.get("message");
				String pfid = (String)map.get("pfid");
				if(b)
				{
					List<Map<String,String>> dlist = (List<Map<String,String>>)map.get("slist");
					
					addCardListData(dlist,pfid);
				}
				else
				{
					loadDialog.dismiss();
					makeText(RegisterAction.this.getString(R.string.login_lable_15)+mssage);
				}
				break;
			case 1:
				boolean bb = (Boolean)msg.obj;
				if(bb)
				{
					myapp.setIszhuche(true);
					makeText(RegisterAction.this.getString(R.string.login_lable_14));
					
					loadThreadData(userName,password);
//					tag = "1";
//					Intent intent = new Intent();
//				    intent.setClass(RegisterAction.this,LoginMain.class);
//				    Bundle bundle = new Bundle();
////					bundle.putString("role", "Cleaner");
//					intent.putExtras(bundle);
//				    startActivity(intent);//开始界面的跳转函数
//				    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//				    RegisterAction.this.finish();
				}
				else
				{
					loadDialog.dismiss();
					makeText(RegisterAction.this.getString(R.string.login_lable_15));
				}
				break;
			case 2:
				String str = (String)msg.obj;
				if(str.equals("0"))
				{
					loadDialog.dismiss();
					makeText(RegisterAction.this.getString(R.string.login_lable_6));
				}
				else if(str.equals("1"))
				{
					loadDialog.dismiss();
					makeText(RegisterAction.this.getString(R.string.login_lable_7));
				}
				else
				{
					makeText(RegisterAction.this.getString(R.string.login_lable_3)+ " "+str + " "+RegisterAction.this.getString(R.string.login_lable_4));
					loadDialog.dismiss();
					showMyCards();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
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
	
	public void loadThreadData(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
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
							myapp.saveSharedPerferences("upmenustate",upmenustate);
							String sessionid = jobj.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj.getString("email");
							myapp.saveSharedPerferences("email",email);
							myapp.saveSharedPerferences("sessionid",sessionid);
							myapp.saveSharedPerferences("sessionidnfc",sessionid);
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
							String themeurl = logindb.getThembgurl(lname, paw);
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
							myapp.saveUserPerferences(lname, paw);
							myapp.saveSharedPerferences("username",username);
							myapp.saveSharedPerferences("sex",sex);
							myapp.saveSharedPerferences("area",area);
							myapp.saveSharedPerferences("signature",signature);
							myapp.saveSharedPerferences("thembgurl",thembgurl);
							myapp.saveSharedPerferences("companyid",companyid);
							
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
							logindb.save(lname, paw,thembgurl);
							
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
	
	public static List<Map<String,String>> getStroeList(JSONArray jarry)
	{
		List<Map<String,String>> slist = new ArrayList<Map<String,String>>();
		try{
			for(int i=0;i<jarry.length();i++)
			{
				JSONObject dobj = jarry.getJSONObject(i);
				String pfid = ""; 
				if(dobj.has("pfid"))
					pfid = (String) dobj.get("pfid"); 
				
				String pid = ""; 
					if(dobj.has("pid"))
						pid = (String) dobj.get("pid");
					
				String bid = ""; 
					if(dobj.has("bid"))
						bid = (String) dobj.get("bid");
					
				String storeTyle = ""; 
					if(dobj.has("storeTyle"))
						storeTyle = (String) dobj.get("storeTyle");
					
				String province = ""; 
					if(dobj.has("province"))
						province = (String) dobj.get("province");
						
				Map<String, String> dmap = new HashMap<String, String>();
				dmap.put("pfid", pfid);
				dmap.put("pid", pid);
				dmap.put("bid", bid);
				dmap.put("storeTyle", storeTyle);
				dmap.put("province", province);
				slist.add(dmap);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return slist;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
		    intent.setClass(RegisterAction.this,LoginMain.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    RegisterAction.this.finish();
			return false;
		}
		return false;
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
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
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
                try{
	                PHOTO_DIR.mkdirs();// 创建照片的存储目录
	                File myCaptureFile = new File( PHOTO_DIR,"mylog.jpg");
	                BufferedOutputStream bos = new BufferedOutputStream(
	                                                         new FileOutputStream(myCaptureFile));
	                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	                bos.flush();
	                bos.close();
	                fileUrl = myCaptureFile.getPath();
	                mImageView.setImageBitmap(photo);
                }catch(Exception ex){
                	ex.printStackTrace();
                }
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
         intent.putExtra("outputX", 300);  
         intent.putExtra("outputY", 300);  
         intent.putExtra("return-data", true);  
         return intent;  
     }  
     
     public void makeText(String str)
 	{
 		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
 	}
     
     public void showProgressDialog(){
  		try{
//  			  mypDialog=new ProgressDialog(RegisterAction.this);
//              //实例化
//              mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//              //设置进度条风格，风格为圆形，旋转的
////              mypDialog.setTitle("等待");
//              //设置ProgressDialog 标题
//              mypDialog.setMessage(this.getString(R.string.login_lable_21));
//              //设置ProgressDialog 提示信息
////              mypDialog.setIcon(R.drawable.wait_icon);
//              //设置ProgressDialog 标题图标
////              mypDialog.setButton("",this);
//              //设置ProgressDialog 的一个Button
//              mypDialog.setIndeterminate(false);
//              //设置ProgressDialog 的进度条是否不明确
//              mypDialog.setCancelable(true);
//              //设置ProgressDialog 是否可以按退回按键取消
//              mypDialog.show();
//              //让ProgressDialog显示
  			loadDialog = new MyLoadingDialog(this, getString(R.string.login_lable_21),R.style.MyDialog);
  	    	loadDialog.show();
  		}catch(Exception ex){
  			ex.printStackTrace();
  		}
  	}
}
