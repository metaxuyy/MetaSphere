package ms.activitys.hotel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.more.MoreActivity;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GuestInfoActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String username;
	private String userimg;
	private RoundAngleImageView user_img;
	public static GuestInfoActivity instance = null; 
	
	/*用来标识请求gallery的activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/*用来标识请求照相功能的activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	/*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private String fileUrl = "";
    private ProgressDialog mypDialog;
    private File mCurrentPhotoFile;//照相机拍照得到的图片
    public static final String IMAGE_UNSPECIFIED = "image/*"; 
    public static final int PHOTORESOULT = 3;// 结果
    
    private static final String IMAGE_FILE_LOCATION = Environment.getExternalStorageDirectory()+"/temp.jpg";//temp file
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    private String myaccount;
    private String mysex;
    private String myarea;
    private String mySignature;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guest_info_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
		initView();
	}
	
	 /** 
     * 重新启动 
     */  
    @Override  
    protected void onResume() {  
        Log.i("TAG-onResume", "onResume()------------yin");
        super.onResume();
        
        username = myapp.getUserName();
		userimg = myapp.getUserimg();
		myaccount = myapp.getMyaccount();
		mysex = myapp.getMysex();
		myarea = myapp.getMyarea();
		mySignature = myapp.getMySignature();
		
		TextView name_txt = (TextView)findViewById(R.id.name_txt);
		name_txt.setText(username);
		
		TextView account_txt = (TextView)findViewById(R.id.account_txt);
		account_txt.setText(myaccount);
		
		TextView sex_txt = (TextView)findViewById(R.id.sex_txt);
		if(mysex.equals("0"))
			sex_txt.setText(getString(R.string.female));
		else
			sex_txt.setText(getString(R.string.male));
		
		TextView area_txt = (TextView)findViewById(R.id.area_txt);
		area_txt.setText(myarea);
		
		TextView signature_txt = (TextView)findViewById(R.id.signature_txt);
		signature_txt.setText(mySignature);
    }
	
	public void initView()
	{
		try{
			user_img = (RoundAngleImageView)findViewById(R.id.user_img);
			if(myapp.getUserimgbitmap() != null)
				user_img.setImageBitmap(myapp.getUserimgbitmap());
//				loadUserImageThread();
			
			Button break_btn = (Button)findViewById(R.id.break_btn);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openMainTabForm();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openMainTabForm()
	{
		Intent intent = new Intent();
	    intent.setClass( this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void settingAvatar(View v)
	{
		Intent intent = new Intent (this,SettingAvatarDialog.class);
		startActivity(intent);
	}
	
	public void openUpdateName(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "username");
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void updateAccount(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "account");
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void updateSex(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "sex");
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void updateArea(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "area");
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void updateSignature(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,UserSettingActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("tag", "signature");
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void loadUserImageThread()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Bitmap bitmap = getImageBitmap(userimg);
					msg.obj = bitmap;
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
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					user_img.setImageBitmap(bitmap);
				break;
			case 1:
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				boolean b = (Boolean)map.get("tag");
				Bitmap photo = (Bitmap)map.get("img");
				if(b)
				{
                	user_img.setImageBitmap(photo);
                	int size=photo.getWidth()*photo.getHeight()*1; 
					ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
					photo.compress(Bitmap.CompressFormat.PNG, 100, oss);
                	String productBase64 = new String(Base64.encodeBase64(oss.toByteArray()));
                	SharedPreferences.Editor editor = share.edit();
                	// 将编码后的字符串写到base64.xml文件中
                	editor.putString("userimg", productBase64);
                	editor.commit();
                	myapp.setUserimgbitmap(photo);
                	
                	if(HotelMainActivity.instance != null)
                		HotelMainActivity.instance.user_img_layout.setVisibility(View.GONE);
                	
                	if(MoreActivity.instance != null)
                		MoreActivity.instance.setUserimg();
				}
				else
				{
					makeText(getString(R.string.travel_lable_23));
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			}
		}
	};
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
//		    opt.inSampleSize = 2;
		    
			bitmap = BitmapFactory.decodeStream(is);
//			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void openImagePoto()
	{
//		final Intent intent = getPhotoPickIntent();
//    	startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//    	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		File sdcardTempFile = new File(IMAGE_FILE_LOCATION);
		Intent intent = new Intent("android.intent.action.PICK");
		intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
		intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);// 输出图片大小
		intent.putExtra("outputY", 300);
		startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}
	
	public void openImageCame()
	{
		 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
         intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"))); 
         startActivityForResult(intent, CAMERA_WITH_DATA);  
	}
	
	public Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
//		intent.putExtra("scale", true);
//		intent.putExtra("return-data", false);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;
	}
	
	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        switch (requestCode) {  
            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的  
            	final Bitmap photo = BitmapFactory.decodeFile(IMAGE_FILE_LOCATION);
//                final Bitmap photo = data.getParcelableExtra("data");
                // 下面就是显示照片了  
//                System.out.println(photo);  
                //缓存用户选择的图片  
//                img = getBitmapByte(photo);  
//                mEditor.setPhotoBitmap(photo);
                try{
//                	final Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
	                PHOTO_DIR.mkdirs();// 创建照片的存储目录
	                File myCaptureFile = new File( PHOTO_DIR,"mylog.jpg");
	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
	                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
	                bos.flush();
	                bos.close();
	                fileUrl = myCaptureFile.getPath();
	                mypDialog=ProgressDialog.show(this, null, this.getString(R.string.user_info_lable_1), true,
	    	                false);
	                new Thread() {
	    				public void run() {
	    					Message msg = new Message();
	    					msg.what = 1;
	    					
	    					boolean b = updateUserImg();
	    					Map<String,Object> map = new HashMap<String,Object>();
	    					map.put("tag", b);
	    					map.put("img",photo);
	    					msg.obj = map;
	    					handler.sendMessage(msg);
	    				}
	    			}.start();
                }catch(Exception ex){
                	ex.printStackTrace();
                }
                break;  
            }  
            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
//            	doCropPhoto(mCurrentPhotoFile);
            	 //设置文件保存路径这里放在跟目录下  
                File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg"); 
                startPhotoZoom(Uri.fromFile(picture));  
                break;  
            }
            case 1:{
            	final Intent intent = getPhotoPickIntent();
	        	startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
            	break;
            }
            case PHOTORESOULT:{
            	try{
	            	Bundle extras = data.getExtras(); 
//	                if (extras != null) { 
//	                    final Bitmap photo = extras.getParcelable("data"); 
	            		final Bitmap photo = BitmapFactory.decodeFile(IMAGE_FILE_LOCATION);
//	                	final Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
	//                    ByteArrayOutputStream stream = new ByteArrayOutputStream(); 
	//                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件  
	                    PHOTO_DIR.mkdirs();// 创建照片的存储目录
		                File myCaptureFile = new File( PHOTO_DIR,"mylog.jpg");
		                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
		                bos.flush();
		                bos.close();
		                fileUrl = myCaptureFile.getPath();
		                mypDialog=ProgressDialog.show(this, null, this.getString(R.string.user_info_lable_1), true,
		    	                false);
	                    new Thread() {
		    				public void run() {
		    					Message msg = new Message();
		    					msg.what = 1;
		    					
		    					boolean b = updateUserImg();
		    					Map<String,Object> map = new HashMap<String,Object>();
		    					map.put("tag", b);
		    					map.put("img",photo);
		    					msg.obj = map;
		    					handler.sendMessage(msg);
		    				}
		    			}.start();
//	                }
            	}catch(Exception ex){
            		ex.printStackTrace();
            	}
            	break;
            }
        }  
    }
    
    public void startPhotoZoom(Uri uri) { 
    	File sdcardTempFile = new File(IMAGE_FILE_LOCATION);
        Intent intent = new Intent("com.android.camera.action.CROP"); 
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED); 
        intent.putExtra("crop", "true");
        intent.putExtra("output", Uri.fromFile(sdcardTempFile));
        // aspectX aspectY 是宽高的比例  
        intent.putExtra("aspectX", 1); 
        intent.putExtra("aspectY", 1); 
        // outputX outputY 是裁剪图片宽高  
        intent.putExtra("outputX", 300); 
        intent.putExtra("outputY", 300); 
//        intent.putExtra("return-data", true); 
//        intent.putExtra("scale", true);
//		intent.putExtra("return-data", false);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTORESOULT); 
    }  
    
    public boolean updateUserImg()
 	{
 		boolean tag = true;
 		try{
// 			showProgressDialog();
 			Map<String, String> params = new HashMap<String, String>();
 			params.put("pfid", myapp.getPfprofileId());
 			long fsize = 0;
 			Map<String, File> files = new HashMap<String, File>();
 			if(fileUrl != null && !fileUrl.equals(""))
 			{
 				File file = new File(fileUrl);
 				long filesize = file.length();
 				if(filesize > 358400) //filesize / 1024得到文件KB单位大小
 				{
// 					makeText("图片过大请重新上传");
 					tag = false;
 				}
 				fsize = fsize + filesize;
// 				makeText("filesize==="+filesize);
 				files.put(file.getName(), file);
 			}
 			
 			if(tag)
 			{
 				JSONObject job = api.updateUserImg(params,files);
 				if(job != null)
 				{
 					if (job.getString("success").equals("true")) {
 						myapp.setUserimg(job.getString("imgurl"));
// 						mypDialog.dismiss();
 						tag = true;
// 						makeText("头像更新成功！");
 					} else {
 						String msg = (String)job.get("msg");
// 						mypDialog.dismiss();
 						tag = false;
// 						makeText("失败原因："+msg);
 					}
 				}
 				else
 				{
// 					mypDialog.dismiss();
 					tag = false;
// 					makeText("上传失败！");
 				}
 			}
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 		return tag;
 	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainTabForm();
			return false;
		}
		return false;
	}
    
    public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
