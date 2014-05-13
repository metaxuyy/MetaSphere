package ms.activitys.travel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.FormFile;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TravelCommentActivity extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String lvid;
	private String notesid;
	private Uri uri;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;  
	private String fileUrl = "";
	private Bitmap bitmap;
	private ImageView mImageView;
	private TextView tvCmtLabel;
	public ProgressDialog pd;
	private String storeId;
	private String tnid;
	private int num = 140;
	private EditText contText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_lv_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		lvid = this.getIntent().getExtras().getString("lvid");
		if(this.getIntent().getExtras().containsKey("notesid"))
			notesid = this.getIntent().getExtras().getString("notesid");
		else
			notesid = "";
		
		if(this.getIntent().getExtras().containsKey("tnid"))
			tnid = this.getIntent().getExtras().getString("tnid");
		else
			tnid = null;
		storeId = this.getIntent().getExtras().getString("storeId");
		
		initUI();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
		    intent.setClass( TravelCommentActivity.this,TravelNotesListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("tag", "back");
			if(tnid != null)
				bundle.putString("tnid", tnid);
			intent.putExtras(bundle);
			
			TravelCommentActivity.this.setResult(1, intent);
			TravelCommentActivity.this.finish();
			return false;
		}
		return false;
	}
	
	public void initUI()
	{
		try{
			Button backBtn = (Button)findViewById(R.id.brack_btn);
			backBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelCommentActivity.this,TravelNotesListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("tag", "back");
					if(tnid != null)
						bundle.putString("tnid", tnid);
					intent.putExtras(bundle);
					
					TravelCommentActivity.this.setResult(1, getIntent());
					TravelCommentActivity.this.finish();
				}
			});
			
			Button postBtn = (Button)findViewById(R.id.comment_btn);
			postBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					messageSend();
				}
			});
			
			Button btGallery = (Button)findViewById(R.id.btGallery);
			btGallery.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openImageMenu();
				}
			});
			
			mImageView = (ImageView)findViewById(R.id.ivCameraPic);
			mImageView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
				public void onCreateContextMenu(ContextMenu arg0, View arg1,
						ContextMenuInfo arg2) {
					// TODO Auto-generated method stub
					if(!fileUrl.equals(""))
					{
						arg0.setHeaderTitle(TravelCommentActivity.this.getString(R.string.image_menu_lable));
						arg0.add(0, 0, 0, TravelCommentActivity.this.getString(R.string.image_view_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 1, 0, TravelCommentActivity.this.getString(R.string.image_replace_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 2, 0, TravelCommentActivity.this.getString(R.string.phone_screen_label)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 3, 0, TravelCommentActivity.this.getString(R.string.image_dellent_lable)).setIcon(R.drawable.delete_icon);
						
					}
				}
			});
			
			contText = (EditText)findViewById(R.id.etBlog);
			contText.addTextChangedListener(new TextWatcher() {
	            private CharSequence temp;
	            private int selectionStart;
	            private int selectionEnd;

	            @Override
	            public void onTextChanged(CharSequence s, int start, int before,
	                    int count) {
	                temp = s;
	            }

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count,
	                    int after) {
	            }

	            @Override
	            public void afterTextChanged(Editable s) {
	            	try{
		                int number = num - s.length();
		                tvCmtLabel.setText(TravelCommentActivity.this.getString(R.string.travel_lable_20)+number);
		                selectionStart = contText.getSelectionStart();
		                selectionEnd = contText.getSelectionEnd();
		                //System.out.println("start="+selectionStart+",end="+selectionEnd);
		                if (temp.length() > num) {
		                    s.delete(selectionStart - 1, selectionEnd);
		                    int tempSelection = selectionStart;
		                    contText.setText(s);
		                    contText.setSelection(tempSelection);//设置光标在最后
		                    makeText(TravelCommentActivity.this.getString(R.string.travel_lable_21));
		                }
	            	}catch(Exception ex){
	            		ex.printStackTrace();
	            	}
	            }
	        });
			
			tvCmtLabel =(TextView)findViewById(R.id.tvCmtLabel);
			tvCmtLabel.setText(this.getString(R.string.travel_lable_20)+num);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void messageSend()
	{
		try{
			pd=ProgressDialog.show(this, null, this.getString(R.string.travel_lable_22), true,
	                false);
			
//			LinearLayout linearProgress = (LinearLayout)findViewById(R.id.linear_progress);
//			linearProgress.setVisibility(View.VISIBLE);
			
			EditText contText = (EditText)findViewById(R.id.etBlog);
			final String content = contText.getText().toString();
			
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					boolean tag = true;
					try{
						Map<String, String> params = new HashMap<String, String>();
						params.put("content", content);
						params.put("lvid",lvid);
						params.put("notesid", notesid);
						params.put("storeId",storeId);
						params.put("pfid", myapp.getPfprofileId());
						long fsize = 0;
						Map<String, File> files = new HashMap<String, File>();
						FormFile [] fromFiles = {};
						if(fileUrl != null && !fileUrl.equals(""))
						{
							File file = new File(fileUrl);
							long filesize = file.length();
							if(filesize > 358400) //filesize / 1024得到文件KB单位大小
							{
//								makeText("图片过大请重新上传");
								tag = false;
							}
							fsize = fsize + filesize;
//							makeText("filesize==="+filesize);
							files.put(file.getName(), file);
						}
						
						if(tag)
						{
							boolean bf = api.uploadFiles(params,files,"sendTravelComment");
							msg.obj = bf;
						}
						else
						{
							msg.obj = tag;
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
//					SystemClock.sleep(5000);
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				boolean bf = (Boolean)msg.obj;
				if(bf)
				{
					pd.dismiss();
					
					Intent intent = new Intent();
				    intent.setClass( TravelCommentActivity.this,TravelNotesListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("tag", "detailed");
					if(tnid != null)
						bundle.putString("tnid", tnid);
					intent.putExtras(bundle);
					
					TravelCommentActivity.this.setResult(1, intent);
					TravelCommentActivity.this.finish();
					makeText(TravelCommentActivity.this.getString(R.string.send_msg_session_lable));
				} else {
					pd.dismiss();
					makeText(TravelCommentActivity.this.getString(R.string.travel_lable_23));
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openImageMenu()
	{
		final CharSequence [] items = { this.getString(R.string.login_lable_18) , this.getString(R.string.login_lable_19)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.login_lable_20));
		builder.setItems ( items , new DialogInterface.OnClickListener () {
		    public void onClick ( DialogInterface dialog , int item ) {
		        if(item == 0)
		        {
		        	Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		        }
		        else if(item == 1)
		        {
		        	Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        	startActivityForResult(intent3, 1);
		        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
//		        	openImageCamera();//平板拍摄照片方法
		        }
		    }
		});
		builder.show();
	
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	           // Fill the list view with the strings the recognizer thought it could have heard
	           ArrayList<String> matches = data.getStringArrayListExtra(
	                   RecognizerIntent.EXTRA_RESULTS);
	           EditText eText = (EditText) findViewById(R.id.messagecontent);
	           eText.setText(matches.get(0));
	       }
	       
	       if(resultCode == RESULT_OK){
	    	   if(data != null)
	    	   {
					Uri uri = data.getData();
					if(uri != null)
					{
						ContentResolver cr = getContentResolver();
						Cursor cursor = cr.query(uri, null, null, null, null);
						String imageSize = "";
						//查询本地图片库图片的字段
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//得到本地图片库中图片的 id、路径、大小、文件名
						while(cursor.moveToNext()){
							Log.i("====_id", cursor.getString(0)+ "");
							Log.i("====_path", cursor.getString(1)+ "");
							fileUrl = cursor.getString(1);
							Log.i("====_size", cursor.getString(2)+ "");
							imageSize = cursor.getString(2);
							Log.i("====_display_name", cursor.getString(3)+ "");
						}
						try {
							if (bitmap != null) {
			                    bitmap.recycle();
			                    bitmap = null;
							 }
			
							Log.i("===============imgSize======" , imageSize);
							bitmap = zoomImage(bitmap,Long.valueOf(imageSize),fileUrl);
							bitmap = zoomImage(bitmap,80,80);
							mImageView.setImageBitmap(bitmap);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
	    	   }
	    	   else
	    	   {
	    		   if(uri != null)
					{
						ContentResolver cr = getContentResolver();
						Cursor cursor = cr.query(uri, null, null, null, null);
						String imageSize = "";
						//查询本地图片库图片的字段
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//得到本地图片库中图片的 id、路径、大小、文件名
						while(cursor.moveToNext()){
							Log.i("====_id", cursor.getString(0)+ "");
							Log.i("====_path", cursor.getString(1)+ "");
							fileUrl = cursor.getString(1);
							Log.i("====_size", cursor.getString(2)+ "");
							imageSize = cursor.getString(2);
							Log.i("====_display_name", cursor.getString(3)+ "");
						}
						try {
							if (bitmap != null) {
			                    bitmap.recycle();
			                    bitmap = null;
							 }
			
							Log.i("===============imgSize======" , imageSize);
							bitmap = zoomImage(bitmap,Long.valueOf(imageSize),fileUrl);
							bitmap = zoomImage(bitmap,80,80);
							mImageView.setImageBitmap(bitmap);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
	    	   }
				
			}
	       
	       
	       super.onActivityResult(requestCode, resultCode, data);
	   }
	
	public Bitmap zoomImage(Bitmap bgimage, long imageSize, String fileUrl) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		if (imageSize < 20480) { // 0-20k
			newOpts.inSampleSize = 1;
        } else if (imageSize < 51200) { // 20-50k
        	newOpts.inSampleSize = 2;
        } else if (imageSize < 307200) { // 50-300k
        	newOpts.inSampleSize = 4;
        } else if (imageSize < 819200) { // 300-800k
        	newOpts.inSampleSize = 6;
        } else if (imageSize < 1048576) { // 800-1024k
        	newOpts.inSampleSize = 8;
        } else {
        	newOpts.inSampleSize = 10;
        }

		// inJustDecodeBounds设为false表示把图片读进内存中
		newOpts.inJustDecodeBounds = false;
		// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
//		newOpts.outHeight = bgimage.getHeight();
//		newOpts.outWidth = bgimage.getWidth();
		
		bgimage = BitmapFactory.decodeFile(fileUrl, newOpts);
		return bgimage;
	}
	
	/***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
            // 获取这个图片的宽和高
            int width = bgimage.getWidth();
            int height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                            matrix, true);
            return bitmap;
    }
	
	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int index = item.getItemId();
		switch (index) {
		case 0:
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileUrl)), "image/*");
			startActivity(intent);
			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			break;
		case 1:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent2, 1);
			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			break;
		case 2:
			Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	startActivityForResult(intent3, 1);
        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
//			openImageCamera();
			break;
		case 3:
			fileUrl = "";
			mImageView.setImageBitmap(null);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
