package ms.activitys.user;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.travel.TravelDetatilActivity;

public class UserInfoDetailed extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private DBHelperLogin db;
	
	private ProgressDialog mypDialog;
	
	private int index;
	
	/*用来标识请求gallery的activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	
	/*用来标识请求照相功能的activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	
	/*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); 
    private File mCurrentPhotoFile;//照相机拍照得到的图片
    
    private int heightPixels;
    
    private String fileUrl = "";
	private ImageView mImageView;
	private Uri uri;
	private Map map;
	private boolean isnfc = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_myinfo);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
	
		db = new DBHelperLogin(this);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		
		if(bunde.containsKey("nfc"))
		{
			map = myapp.getNfcStoreList().get(index);
			isnfc = true;
		}
		else
		{
			List<Map<String,Object>> list = myapp.getMyCardsAll();
			map = list.get(index);
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(this, "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
		heightPixels = dm.heightPixels;
		
		showCardDetails();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(isnfc)
			{
				Intent intent = new Intent();
			    intent.setClass( this,MainTabActivity.class);
			    Bundle bundle = new Bundle();
//				bundle.putString("role", "Cleaner");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//关闭显示的Activity
			}
			else
			{
				UserInfoDetailed.this.setResult(RESULT_OK, getIntent());
				UserInfoDetailed.this.finish();
			}
			return false;
		}
		return false;
	}
	
	public void showCardDetails()
	{
		try{
			Integer points = (Integer)map.get("points");
			String nameOnCard = (String)map.get("nameOnCard");
			String nameid = (String)map.get("nameid");
			String pfids = (String)map.get("pfids");
			String cardno = (String)map.get("cardNo");
			String joineDate = (String)map.get("joinedDate");
			String mdmType = (String)map.get("mdmType");
			String mdmlevel = (String)map.get("mdmLevel");
			String mdmstatus = (String)map.get("mdmstatus");
			String expdate = (String)map.get("expDate");
			String chainCode = (String)map.get("chainCode");
			String storeid = (String)map.get("storeid");
			String storeName = (String)map.get("storeName");
			Drawable img = (Drawable)map.get("img");
			Bitmap bitimg = (Bitmap)map.get("img2");
			String userimg = (String)map.get("userimg");
			
			TextView account = (TextView)findViewById(R.id.info_account);
			account.setText(myapp.getUserName());
			
			mImageView = (ImageView)findViewById(R.id.icon);
			if(userimg != null && !userimg.equals(""))
			{
				Bitmap bitm = getImageBitmap(userimg);
				if(bitm != null)
					mImageView.setImageBitmap(bitm);
			}
			
//			TextView tview = (TextView)findViewById(R.id.store_name);
//			tview.setText(storeName);
			TextView tview = (TextView)findViewById(R.id.lay_row_left);
			tview.setText(getString(R.string.vip_info_btn3)+storeName);
			
//			TextView tview2 = (TextView)findViewById(R.id.card_no);
//			tview2.setText(cardno);
			TextView tview2 = (TextView)findViewById(R.id.lay_row_reg);
			tview2.setText(getString(R.string.vip_info_btn4)+cardno);
			
//			TextView tview3 = (TextView)findViewById(R.id.joined_Date);
//			tview3.setText(joineDate);
			TextView tview3 = (TextView)findViewById(R.id.lay_row_left2);
			tview3.setText(getString(R.string.vip_info_btn5)+joineDate);
			
//			TextView tview4 = (TextView)findViewById(R.id.points);
//			tview4.setText(String.valueOf(points));
			TextView tview4 = (TextView)findViewById(R.id.lay_row_reg2);
			tview4.setText(getString(R.string.vip_info_btn6)+String.valueOf(points));
			
//			TextView tview5 = (TextView)findViewById(R.id.mdm_Type);
//			tview5.setText(mdmType);
			TextView tview5 = (TextView)findViewById(R.id.lay_row_left3);
			tview5.setText(getString(R.string.vip_info_btn7)+mdmType);
			
//			TextView tview6 = (TextView)findViewById(R.id.mdm_Level);
//			tview6.setText(mdmlevel);
			TextView tview6 = (TextView)findViewById(R.id.tvLevel);
			tview6.setText(mdmlevel);
			
//			TextView tview7 = (TextView)findViewById(R.id.mdm_status);
//			tview7.setText(mdmstatus);
			TextView tview7 = (TextView)findViewById(R.id.lay_row_reg3);
			tview7.setText(getString(R.string.vip_info_btn9)+mdmstatus);
			
//			TextView tview8 = (TextView)findViewById(R.id.exp_Date);
//			tview8.setText(expdate);
			TextView tview8 = (TextView)findViewById(R.id.lay_row_left4);
			tview8.setText(getString(R.string.vip_info_btn11)+expdate);
			
//			TextView tview9 = (TextView)findViewById(R.id.chain_Code);
//			tview9.setText(chainCode);
			TextView tview9 = (TextView)findViewById(R.id.lay_row_reg4);
			tview9.setText(getString(R.string.vip_info_btn12)+chainCode);
			
//			TextView tview10 = (TextView)findViewById(R.id.name_On_Card);
//			tview10.setText(nameOnCard);
			TextView tview10 = (TextView)findViewById(R.id.tvInforLocation2);
			tview10.setText(getString(R.string.vip_info_btn13)+nameOnCard);
			
			TextView tvInforSex = (TextView)findViewById(R.id.tvInforSex);
			tvInforSex.setText("性别：未选择");
			
			TextView tvInforBloodType = (TextView)findViewById(R.id.tvInforBloodType);
			tvInforBloodType.setText("血型：未选择");
			
			TextView tvInforAge = (TextView)findViewById(R.id.tvInforAge);
			tvInforAge.setText("年龄：保密");
			
			TextView tvInforBirth = (TextView)findViewById(R.id.tvInforBirth);
			tvInforBirth.setText("生日：保密");
			
			TextView tvInforZodiacSign = (TextView)findViewById(R.id.tvInforZodiacSign);
			tvInforZodiacSign.setText("生肖：未选择");
			
			TextView tvInforHoroscope = (TextView)findViewById(R.id.tvInforHoroscope);
			tvInforHoroscope.setText("星座：未选择");
			
			TextView tvInforLoveStatus = (TextView)findViewById(R.id.tvInforLoveStatus);
			tvInforLoveStatus.setText("恋爱状态：未选择");
			
			TextView tvInforLocation = (TextView)findViewById(R.id.tvInforLocation);
			tvInforLocation.setText("所在地：保密");
			
//			Button imgbtn = (Button)findViewById(R.id.userPhotoBtn);
			mImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openImageMenu();
				}
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
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
		    opt.inSampleSize = 2;
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
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
                try{
	                PHOTO_DIR.mkdirs();// 创建照片的存储目录
	                File myCaptureFile = new File( PHOTO_DIR,"mylog.jpg");
	                BufferedOutputStream bos = new BufferedOutputStream(
	                                                         new FileOutputStream(myCaptureFile));
	                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	                bos.flush();
	                bos.close();
	                fileUrl = myCaptureFile.getPath();
	                mypDialog=ProgressDialog.show(this, null, this.getString(R.string.user_info_lable_1), true,
	    	                false);
	                new Thread() {
	    				public void run() {
	    					Message msg = new Message();
	    					msg.what = 0;
	    					
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
    
    private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				boolean b = (Boolean)map.get("tag");
				Bitmap photo = (Bitmap)map.get("img");
				if(b)
				{
                	mImageView.setImageBitmap(photo);
				}
				mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
    
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
     
    public void makeText(String str)
 	{
 		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
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
 						List<Map<String,Object>> list = myapp.getMyCardsAll();
 						Map map = list.get(index);
 						map.put("userimg", job.getString("imgurl"));
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
    
    public void showProgressDialog(){
  		try{
  			mypDialog=new ProgressDialog(this);
              //实例化
              mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
              //设置进度条风格，风格为圆形，旋转的
//              mypDialog.setTitle("等待");
              //设置ProgressDialog 标题
              mypDialog.setMessage(this.getString(R.string.user_info_lable_2));
              //设置ProgressDialog 提示信息
//              mypDialog.setIcon(R.drawable.wait_icon);
              //设置ProgressDialog 标题图标
//              mypDialog.setButton("",this);
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
