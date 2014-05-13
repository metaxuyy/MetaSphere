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
	
	/*������ʶ����gallery��activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	
	/*������ʶ�������๦�ܵ�activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
	
	/*���յ���Ƭ�洢λ��*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); 
    private File mCurrentPhotoFile;//��������յõ���ͼƬ
    
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
//		Toast.makeText(this, "��Ļ�ֱ���Ϊ:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
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
			    startActivity(intent);//��ʼ�������ת����
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();//�ر���ʾ��Activity
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
			tvInforSex.setText("�Ա�δѡ��");
			
			TextView tvInforBloodType = (TextView)findViewById(R.id.tvInforBloodType);
			tvInforBloodType.setText("Ѫ�ͣ�δѡ��");
			
			TextView tvInforAge = (TextView)findViewById(R.id.tvInforAge);
			tvInforAge.setText("���䣺����");
			
			TextView tvInforBirth = (TextView)findViewById(R.id.tvInforBirth);
			tvInforBirth.setText("���գ�����");
			
			TextView tvInforZodiacSign = (TextView)findViewById(R.id.tvInforZodiacSign);
			tvInforZodiacSign.setText("��Ф��δѡ��");
			
			TextView tvInforHoroscope = (TextView)findViewById(R.id.tvInforHoroscope);
			tvInforHoroscope.setText("������δѡ��");
			
			TextView tvInforLoveStatus = (TextView)findViewById(R.id.tvInforLoveStatus);
			tvInforLoveStatus.setText("����״̬��δѡ��");
			
			TextView tvInforLocation = (TextView)findViewById(R.id.tvInforLocation);
			tvInforLocation.setText("���ڵأ�����");
			
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
                try{
	                PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
 				if(filesize > 358400) //filesize / 1024�õ��ļ�KB��λ��С
 				{
// 					makeText("ͼƬ�����������ϴ�");
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
// 						makeText("ͷ����³ɹ���");
 					} else {
 						String msg = (String)job.get("msg");
// 						mypDialog.dismiss();
 						tag = false;
// 						makeText("ʧ��ԭ��"+msg);
 					}
 				}
 				else
 				{
// 					mypDialog.dismiss();
 					tag = false;
// 					makeText("�ϴ�ʧ�ܣ�");
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
              //ʵ����
              mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
              //���ý�������񣬷��ΪԲ�Σ���ת��
//              mypDialog.setTitle("�ȴ�");
              //����ProgressDialog ����
              mypDialog.setMessage(this.getString(R.string.user_info_lable_2));
              //����ProgressDialog ��ʾ��Ϣ
//              mypDialog.setIcon(R.drawable.wait_icon);
              //����ProgressDialog ����ͼ��
//              mypDialog.setButton("",this);
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
