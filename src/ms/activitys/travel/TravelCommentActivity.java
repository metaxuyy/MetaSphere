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
		                    contText.setSelection(tempSelection);//���ù�������
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
							if(filesize > 358400) //filesize / 1024�õ��ļ�KB��λ��С
							{
//								makeText("ͼƬ�����������ϴ�");
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
//		        	openImageCamera();//ƽ��������Ƭ����
		        }
		    }
		});
		builder.show();
	
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
						//��ѯ����ͼƬ��ͼƬ���ֶ�
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//�õ�����ͼƬ����ͼƬ�� id��·������С���ļ���
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
						//��ѯ����ͼƬ��ͼƬ���ֶ�
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//�õ�����ͼƬ����ͼƬ�� id��·������С���ļ���
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
		// ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ�Ŀǰ��ֻ����ֻ��ͨ��inSampleSize���������ţ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ
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

		// inJustDecodeBounds��Ϊfalse��ʾ��ͼƬ�����ڴ���
		newOpts.inJustDecodeBounds = false;
		// ���ô�С�����һ���ǲ�׼ȷ�ģ�����inSampleSize��Ϊ׼���������������ȴ��������
//		newOpts.outHeight = bgimage.getHeight();
//		newOpts.outWidth = bgimage.getWidth();
		
		bgimage = BitmapFactory.decodeFile(fileUrl, newOpts);
		return bgimage;
	}
	
	/***
     * ͼƬ�����ŷ���
     *
     * @param bgimage
     *            ��ԴͼƬ��Դ
     * @param newWidth
     *            �����ź���
     * @param newHeight
     *            �����ź�߶�
     * @return
     */
    public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
            // ��ȡ���ͼƬ�Ŀ�͸�
            int width = bgimage.getWidth();
            int height = bgimage.getHeight();
            // ��������ͼƬ�õ�matrix����
            Matrix matrix = new Matrix();
            // ���������ʣ��³ߴ��ԭʼ�ߴ�
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // ����ͼƬ����
            matrix.postScale(scaleWidth, scaleHeight);
            bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                            matrix, true);
            return bitmap;
    }
	
	// �����˵���Ӧ����
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
