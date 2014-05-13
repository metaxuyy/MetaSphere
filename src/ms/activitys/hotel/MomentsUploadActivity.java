package ms.activitys.hotel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ms.activitys.MenuSetActivity;
import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.imagegrid.BitmapUtil;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MomentsUploadActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static MomentsUploadActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private List<String> filepathlist = new ArrayList<String>();
	private LinearLayout image_layout_1;
	private LinearLayout image_layout_2;
	private LinearLayout image_layout_3;
	private static final int PHOTO_PICKED_UPLOAD_WITH_DATA = 5021;
	private static final int CAMERA_UPLOAD_WITH_DATA = 5023;
	private static final int GET_MEDIA_DATA = 5024;
	private Uri uri;
	private	SelectPicPopupWindow menuWindow;
	private int deleteindex;
	private DeletePopupWindow deleteWindow;
	public static FileUtils fileUtil = new FileUtils();
	private EditText sns_desc_tv;
	private ProgressDialog mypDialog;
	private String savedate;

	private LinearLayout addAttachmentLL;
	private LinearLayout attachmentLL;
	private SelectMediaPopupWindow selMediaWindow;
	private String attachmentType = "3";// 附件类型，默认为文档
	private LinearLayout removeLL;// 长按要删除的附件
	private List<Map<String, Object>> publishTypeList;// 存放公告栏类型
	private String[] publishTypes;// 存放公告栏类型名称
	private String publishTypeID;// 发布的公告栏类型

	private String type;// 列表类型
	
	private LinearLayout addContactLL;
	private TextView addContactTV;
	private ImageView addContactImg;
	private String pkid;//保存完的朋友圈主键ID

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moments_upload_ui);

		Bundle bunde = this.getIntent().getExtras();
		type = bunde.getString("type");

		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
		instance = this;
		
//		Bundle bunde = this.getIntent().getExtras();
//		String filepath = bunde.getString("filepath");
//		filepathlist.add(filepath);
		List<Map<String, Object>> selImgList = myapp.getSelPicList();
		if(selImgList!=null){
			for(int i=0; i<selImgList.size(); i++){
				Map<String, Object> map = selImgList.get(i);
				filepathlist.add((String) map.get("path"));
			}
		}else{
			String filepath = bunde.getString("filepath");
			filepathlist.add(filepath);
		}
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMomentsView();
			}
		});
		
		initView();
		getPublishBoardType();
	}

	public void initView() {
		try {
			image_layout_1 = (LinearLayout) findViewById(R.id.image_layout_1);
			image_layout_2 = (LinearLayout) findViewById(R.id.image_layout_2);
			image_layout_3 = (LinearLayout) findViewById(R.id.image_layout_3);

			sns_desc_tv = (EditText) findViewById(R.id.sns_desc_tv);

			addAttachmentLL = (LinearLayout) findViewById(R.id.add_attachment);
			addAttachmentLL.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (attachmentLL.getChildCount() == 1) {
						Toast.makeText(MomentsUploadActivity.this,
								"一次只能上传一个附件", Toast.LENGTH_LONG).show();
						return;
					}

					selMediaWindow = new SelectMediaPopupWindow(
							MomentsUploadActivity.this, itemsOnClickForSelMedia);
					selMediaWindow.showAtLocation(MomentsUploadActivity.this
							.findViewById(R.id.main_rlayout), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			});
			if(type==null || type.equals("")){
				addAttachmentLL.setVisibility(View.GONE);
			}else{
				addAttachmentLL.setVisibility(View.VISIBLE);
			}
			
			attachmentLL = (LinearLayout) findViewById(R.id.publishBoardAttLL);
			
			addContactLL = (LinearLayout) findViewById(R.id.add_contact);
			addContactImg = (ImageView) findViewById(R.id.share_people);
			addContactTV = (TextView) findViewById(R.id.with_local_tv);
			addContactLL.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addContactTV.setText("正在获取地理位置...");
					new Thread() {
						public void run() {
							Message msg = new Message();
							msg.what = 2;
							JSONObject json = null;
							json = geocodeAddr(Double.parseDouble(myapp.getLat()), Double.parseDouble(myapp.getLng()));
							
							String CountryName = ""; 
							String CountryNameCode = ""; 
							String LocalityName = ""; 
							String ThoroughfareName = "";
							String quhao = "";
							String menpai = ""; 
							try {
								if(json != null)
								{
									JSONArray results = json.getJSONArray("results");
//									latLongString = results.getJSONObject(0).getString("formatted_address");
									if(results.length() > 0)
									{
										CountryName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name");
										CountryNameCode = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("short_name");
										LocalityName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");
										ThoroughfareName = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
										quhao = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");
										menpai = results.getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
										myapp.setCountry(CountryName);
										myapp.setCity(LocalityName);
										myapp.setRoad(ThoroughfareName);
										myapp.setArea(quhao);
										myapp.setNumbers(menpai);
										
										msg.obj = 1;
									}else{
										msg.obj = 0;
									}
								}else{
									msg.obj = 0;
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							handler.sendMessage(msg);
						}
					}.start();
				}
			});

			loadImageLayout();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadImageLayout()
	{
		try{
			int size = filepathlist.size();
			for(int i=0;i<filepathlist.size();i++)
			{
				String filepath = filepathlist.get(i);
				Bitmap bitmap = getLoacalBitmap(filepath);
				/** 
				 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
				 */  
				int degree = readPictureDegree(filepath);
				/** 
				 * 把图片旋转为正的方向 
				 */  
				bitmap = rotaingImageView(degree, bitmap); 
				bitmap = Bitmap.createScaledBitmap(bitmap,60,60,true);
				if(i<4)
				{
					ImageView imgview = (ImageView)image_layout_1.getChildAt(i);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageBitmap(bitmap);
					final int index = i;
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							deleteindex = index;
							// TODO Auto-generated method stub
							deleteWindow = new DeletePopupWindow(MomentsUploadActivity.this, itemsDeleteOnClick);
							//显示窗口
							deleteWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
				else if(i < 8)
				{
					ImageView imgview = (ImageView)image_layout_2.getChildAt(i-4);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageBitmap(bitmap);
					final int index = i;
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							deleteindex = index;
							// TODO Auto-generated method stub
							deleteWindow = new DeletePopupWindow(MomentsUploadActivity.this, itemsDeleteOnClick);
							//显示窗口
							deleteWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
				else
				{
					ImageView imgview = (ImageView)image_layout_3.getChildAt(i-8);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageBitmap(bitmap);
					final int index = i;
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							deleteindex = index;
							// TODO Auto-generated method stub
							deleteWindow = new DeletePopupWindow(MomentsUploadActivity.this, itemsDeleteOnClick);
							//显示窗口
							deleteWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
			}
			
			if(size < 9)
			{
				if(size < 4)
				{
					ImageView imgview = (ImageView)image_layout_1.getChildAt(size);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageResource(R.drawable.roominfo_add_btn);
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//实例化SelectPicPopupWindow
							menuWindow = new SelectPicPopupWindow(MomentsUploadActivity.this, itemsOnClick);
							//显示窗口
							menuWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
				else if(size < 8)
				{
					ImageView imgview = (ImageView)image_layout_2.getChildAt(size-4);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageResource(R.drawable.roominfo_add_btn);
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//实例化SelectPicPopupWindow
							menuWindow = new SelectPicPopupWindow(MomentsUploadActivity.this, itemsOnClick);
							//显示窗口
							menuWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
				else
				{
					ImageView imgview = (ImageView)image_layout_3.getChildAt(size-8);
					imgview.setVisibility(View.VISIBLE);
					imgview.setImageResource(R.drawable.roominfo_add_btn);
					imgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//实例化SelectPicPopupWindow
							menuWindow = new SelectPicPopupWindow(MomentsUploadActivity.this, itemsOnClick);
							//显示窗口
							menuWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
						}
					});
				}
			
				for(int j=size+1;j<9;j++)
				{
					if(j<4)
					{
						ImageView imgview = (ImageView)image_layout_1.getChildAt(j);
						imgview.setVisibility(View.GONE);
					}
					else if(j < 8)
					{
						ImageView imgview = (ImageView)image_layout_2.getChildAt(j-4);
						imgview.setVisibility(View.GONE);
					}
					else
					{
						ImageView imgview = (ImageView)image_layout_3.getChildAt(j-8);
						imgview.setVisibility(View.GONE);
					}
				}
			}
			if(menuWindow != null)
				menuWindow.dismiss();
			if(deleteWindow != null)
				deleteWindow.dismiss();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//为弹出窗口实现监听类
    private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_take_photo:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				ContentValues values = new ContentValues(3);
//				values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
				values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
				uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				//这样就将文件的存储方式和uri指定到了Camera应用中

				//由于我们需要调用完Camera后，可以返回Camera获取到的图片，
				//所以，我们使用startActivityForResult来启动Camera
				startActivityForResult(intent, CAMERA_UPLOAD_WITH_DATA);
				break;
			case R.id.btn_pick_photo:
				Intent intent2 = new Intent();
				intent2.setType("image/*");
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent2, PHOTO_PICKED_UPLOAD_WITH_DATA);
				overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);
				break;
			default:
				break;
			}
		}
    };
    
  //为弹出窗口实现监听类
    private OnClickListener  itemsDeleteOnClick = new OnClickListener(){

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_delete_photo:
				filepathlist.remove(deleteindex);
				loadImageLayout();
				break;
			default:
				break;
			}
		}
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        switch (requestCode) {  
            case PHOTO_PICKED_UPLOAD_WITH_DATA: {
            	Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
				Cursor cursor = cr.query(uri, null, null, null, null);
				String imageSize = "";
				String fileUrl = "";
				while (cursor.moveToNext()) {
					Log.i("====_id", cursor.getString(0) + "");
					Log.i("====_path", cursor.getString(1) + "");
					fileUrl = cursor.getString(1);
					Log.i("====_size", cursor.getString(2) + "");
					imageSize = cursor.getString(2);
					Log.i("====_display_name", cursor.getString(3) + "");
//					fileName = cursor.getString(3);
				}
				filepathlist.add(fileUrl);
				loadImageLayout();
                break;  
            }  
            case CAMERA_UPLOAD_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
				if (uri != null) {
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(uri, null, null, null, null);
					String fileUrl = "";
					while (cursor.moveToNext()) {
						fileUrl = cursor.getString(1);
//						fileName = cursor.getString(3);
					}
					filepathlist.add(fileUrl);
					loadImageLayout();
				}
                break;  
            }
            case GET_MEDIA_DATA: {
    			Bundle bundle = null;
    			if (data != null && (bundle = data.getExtras()) != null) {
    				System.out.println("选择文件夹为：" + bundle.getString("file"));
    				String attachmentUrl = bundle.getString("file");
    				String[] urlArr = attachmentUrl.split("/");
    				addAttachment(urlArr[urlArr.length - 1], attachmentUrl);
    				attachmentLL.setVisibility(View.VISIBLE);
    			}
    			break;
    		}
        }  
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMomentsView();
			return false;
		}
		return false;
	}
	
	public void openMomentsUploadView(String filepath)
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MomentsUploadActivity.class);
			bundle.putString("filepath", filepath);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openMomentsView()
	{
		myapp.setSelPicList(null);
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MomentsActivity.class);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void uploadMomentsImage(View v) {
		if (myapp.getIsServer()) {
			final String[] types = publishTypes;
			publishTypeID = type;
			int index = 0;
			for (int i = 0; i < publishTypeList.size(); i++) {
				Map<String, Object> map = publishTypeList.get(i);
				if (map.get("typeid").equals(type)) {
					index = i;
					break;
				}
			}
//			new AlertDialog.Builder(this)
//					.setTitle("选择发布类型")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setSingleChoiceItems(types, index,
//							new DialogInterface.OnClickListener() {
//
//								public void onClick(DialogInterface dialog,
//										int which) {
//									publishTypeID = (String) ((Map) publishTypeList
//											.get(which)).get("typeid");
//									System.out.println("发布类型：" + types[which]);
//									System.out.println("发布类型ID："
//											+ publishTypeID);
//								}
//							})
//					.setPositiveButton("确定",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									saveMoments();
//									dialog.dismiss();
//								}
//							}).setNegativeButton("取消", null).show();
			saveMoments();
		} else {
			publishTypeID = "";
			saveMoments();
		}
	}
	
	public void saveMoments()
	{
		try{
			showMyLoadingDialog(getString(R.string.user_info_lable_1));
			final String context = sns_desc_tv.getText().toString();
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					
					List<Map<String, String>> uploadfile = new ArrayList<Map<String, String>>();
					List<Bitmap> uploadfilebitmap = new ArrayList<Bitmap>();
					for(int i=0;i<filepathlist.size();i++)
					{
						String filepath = filepathlist.get(i);
//						Bitmap bitmap = getLoacalBitmapMax(filepath);
//						/** 
//						 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
//						 */  
//						int degree = readPictureDegree(filepath);
//						/** 
//						 * 把图片旋转为正的方向 
//						 */  
//						bitmap = rotaingImageView(degree, bitmap); 
//						bitmap = myapp.adaptive(bitmap, myapp.getScreenWidth(), myapp.getScreenHeight());
						
						Bitmap bitmap = BitmapUtil.compressImage(filepath, 1204.0f, 800.0f);
						
						Bitmap bitmap2 = null;
						if(filepathlist.size() > 1)
						{
//							bitmap2 = myapp.adaptive(bitmap, 100, 100);
							bitmap2 = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
						}
						else
						{
							bitmap2 = myapp.adaptive(bitmap, 300, 300);
						}
						UUID uuid = UUID.randomUUID();
						String [] names = filepath.split("\\.");
						String fileNames = uuid.toString() + "." + names[names.length-1];
						if(!fileUtil.isFileExist2(myapp.getPfprofileId()))
							fileUtil.createUserFile(myapp.getPfprofileId());
						String furl = fileUtil.getImageFile1bPath(myapp.getPfprofileId(), fileNames);
						String furl2 = fileUtil.getImageFile1aPath(myapp.getPfprofileId(), fileNames);
						myapp.saveMyBitmap(furl, bitmap);
						myapp.saveMyBitmap(furl2, bitmap2);
						uploadfilebitmap.add(bitmap2);
						
						//语音(1)、视频(2)、文档(3)、图片(4)
						Map<String, String> mapImg = new HashMap<String, String>();
						mapImg.put("url", furl);
						mapImg.put("type", "4");
						uploadfile.add(mapImg);
					}
					
					// 附件
					int len = attachmentLL.getChildCount();
					for (int i = 0; i < len; i++) {
						LinearLayout ll = (LinearLayout) attachmentLL
								.getChildAt(i);
						TextView tv = (TextView) ll.getChildAt(1);// 显示名称
						TextView tvEName = (TextView) ll.getChildAt(2);// 保存名称
						TextView tvUrl = (TextView) ll.getChildAt(3);// url

						Map<String, String> mapWord = new HashMap<String, String>();
						mapWord.put("url", tvUrl.getText().toString());
						mapWord.put("type", attachmentType); //暂时支持一个附件上传，直接获取附件类型
						uploadfile.add(mapWord);
					}
					
					boolean b = updateUserImg(uploadfile,context);
					Map<String,Object> map = new HashMap<String,Object>();
					if(type == null || type.equals(""))
					{
						if(b)
						{
							Map<String,Object> maps = new HashMap<String,Object>();
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							UUID uuid = UUID.randomUUID();
//							String pkid = uuid.toString();
							maps.put("pkid", pkid);
							maps.put("content", context);
//							String publishtime = format.format(new Date());
							maps.put("publishtime", myapp.getInterval(savedate));
							maps.put("publishuser", myapp.getUserName());
							maps.put("publishid", myapp.getPfprofileId());
							maps.put("publishtype", publishTypeID);
							maps.put("publishusertype", type.equals("")?"2":"1");
							
							List<Map<String,String>> files = new ArrayList<Map<String,String>>();
							for(int j=0;j<uploadfile.size();j++)
							{
	//							String fileurl = uploadfile.get(j);
	//							Bitmap bitmap = uploadfilebitmap.get(j);
								Map<String, String> tempmap = uploadfile.get(j);
	//							fileUtil.deleteFile(fileurl);
								File file = new File(tempmap.get("url"));
								Map<String,String> filemap = new HashMap<String,String>();
								UUID uuids = UUID.randomUUID();
								
								//如果是图片获取bitmap，用于保存图片的宽高
								Bitmap bitmap = null;
								if(tempmap.get("type").equals("4")){
									bitmap = uploadfilebitmap.get(j);
									filemap.put("filewidth", String.valueOf(bitmap.getWidth()));
									filemap.put("fileheight", String.valueOf(bitmap.getHeight()));
								}else{
									filemap.put("filewidth", null);
									filemap.put("fileheight", null);
								}
								filemap.put("pkid", uuids.toString());
								filemap.put("filename", file.getName());
								filemap.put("fileurl", tempmap.get("url"));
								filemap.put("filetype", tempmap.get("type"));
								
								filemap.put("momentsid", pkid);
								files.add(filemap);
							}
							maps.put("filelist", files);
//							List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
//							if(type != null && !type.equals(""))
//							{
//								msg.what = 1;
//								map.put("mdata", maps);
//							}
//							else
//							{
//								dlist.add(maps);
//								db.openDB();
//								db.saveMomentsAll(dlist);
//								db.closeDB();
//							}
							map.put("mdata", maps);
						}
					}
					
					map.put("tag", b);
//					map.put("savedata", maps);
					msg.obj = map;
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
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				boolean b = (Boolean)map.get("tag");
				if(b)
				{
					if(map.containsKey("mdata"))
					{
						Map<String,Object> maps = (Map<String,Object>)map.get("mdata");
						MomentsActivity.instance.changPenyouDataList(maps);
					}
//					MomentsActivity.instance.getMomentsDataAll2();
					if(loadDialog != null)
					{
						if(loadDialog.isShowing())
							loadDialog.dismiss();
					}
					openMomentsView();
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				Map<String,Object> maps = (Map<String,Object>)msg.obj;
				boolean bb = (Boolean)maps.get("tag");
				if(bb)
				{
					MomentsActivity.instance.showRefreshLayout();
					MomentsActivity.instance.getMomentsDataAll3();
					openMomentsView();
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				int tag = (Integer) msg.obj;
				if(tag == 1){
					addContactImg.setImageResource(R.drawable.sns_shoot_location_pressed);
					addContactTV.setText(myapp.getCountry()+myapp.getCity()+myapp.getArea()+myapp.getRoad()+myapp.getNumbers());
				}else if(tag == 0){
					Toast.makeText(MomentsUploadActivity.this, "获取地理位置信息失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	public void saveMomentsData(String jsonstr)
	{
		try{
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean updateUserImg(List<Map<String, String>> filelist,String context)
 	{
 		boolean tag = true;
 		try{
// 			showProgressDialog();
 			Map<String, String> params = new HashMap<String, String>();
 			params.put("pfid", myapp.getPfprofileId());
 			params.put("context", context);
 			params.put("publishtype", publishTypeID);
			params.put("publishusertype", myapp.getIsServer() ? "1" : "2");
			params.put("storeid", myapp.getAppstoreid());
			params.put("businessid", myapp.getBusinessid());
			String positionStr = (String) addContactTV.getText();
			if(positionStr.equals(getString(R.string.sns_not_get_location))){
				params.put("position", "");
			}else{
				params.put("position", positionStr);
			}
 			long fsize = 0;
 			Map<String, File> files = new HashMap<String, File>();
 			for(int i=0;i<filelist.size();i++)
 			{
// 				String fileurl = filelist.get(i);
 				Map<String, String> map = filelist.get(i);
 				File file = new File(map.get("url"));
 				files.put(file.getName(), file);
 			}
 			
 			if(tag)
 			{
 				JSONObject job = api.updateMomentsImg(params,files);
 				if(job != null)
 				{
 					if (job.getString("success").equals("true")) {
// 						myapp.setUserimg(job.getString("imgurl"));
// 						mypDialog.dismiss();
 						savedate = job.getString("savedate");
 						pkid = job.getString("pkid");
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
 			tag = false;
 		}
 		return tag;
 	}
	
	public void showMyLoadingDialog(String message)
    {
//		if(loadDialog != null)
//		{
//			if(loadDialog.isShowing())
//				loadDialog.dismiss();
//		}
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	/** 
     * 读取图片属性：旋转的角度 
     * @param path 图片绝对路径 
     * @return degree旋转的角度 
     */  
	public int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/** 
     * 旋转图片 
     * @param angle 
     * @param bitmap 
     * @return Bitmap 
     */  
    public Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
    	Bitmap resizedBitmap = null;
    	try{
	    	//旋转图片 动作  
	        Matrix matrix = new Matrix(); 
	        matrix.postRotate(angle);  
	        System.out.println("angle2=" + angle);
	        // 创建新的图片  
	        resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
	                bitmap.getWidth(), bitmap.getHeight(), matrix, true); 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
        return resizedBitmap;  
    }
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmapMax(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	/*************** 添加附件处理 ************/
	private OnClickListener itemsOnClickForSelMedia = new OnClickListener() {

		public void onClick(View v) {
			selMediaWindow.dismiss();
			switch (v.getId()) {
			case R.id.addSoundBtn:
				attachmentType = "1";
				break;
			case R.id.addVideoBtn:
				attachmentType = "2";

				Intent intent = new Intent(MomentsUploadActivity.this,
						MyFileManagerActivity.class);
				intent.putExtra("isVideo", true);
				startActivityForResult(intent, GET_MEDIA_DATA);
				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_bottom_out);
				break;
			case R.id.addWordBtn:
				attachmentType = "3";

				Intent intent1 = new Intent(MomentsUploadActivity.this,
						MyFileManagerActivity.class);
				intent1.putExtra("isVideo", false);
				startActivityForResult(intent1, GET_MEDIA_DATA);
				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_bottom_out);
				break;
			default:
				break;
			}
		}

	};

	private void addAttachment(String tvText, String url) {
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setGravity(Gravity.CENTER_VERTICAL);
		ll.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				removeLL = (LinearLayout) v;
				new Handler().post(new Runnable() {
					public void run() {
						attachmentLL.removeView(removeLL);
					}
				});
				return false;
			}
		});

		ImageView img = new ImageView(this);
		img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		img.setPadding(5, 5, 5, 5);
		img.setImageResource(R.drawable.sns_shoot_attachment_pressed);

		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		tv.setText(tvText);

		TextView tvEName = new TextView(this);
		tvEName.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		tvEName.setText(getFileName(tvText));
		tvEName.setVisibility(View.GONE);

		TextView tvUrl = new TextView(this);
		tvUrl.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		tvUrl.setText(url);
		tvUrl.setVisibility(View.GONE);

		TextView tvType = new TextView(this);
		tvType.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		tvType.setText(attachmentType);
		tvType.setVisibility(View.GONE);

		ll.addView(img);
		ll.addView(tv);
		ll.addView(tvEName);
		ll.addView(tvUrl);
		ll.addView(tvType);

		attachmentLL.addView(ll);
	}

	private String getFileName(String fileName) {
		int index = fileName.indexOf(".");
		String fileType = fileName.substring(index + 1, fileName.length());

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'Attachment'_yyyy-MM-ddHHmmss");
		return dateFormat.format(date) + "." + fileType;
	}

	/********** 获取公告栏类型 *********/
	private void getPublishBoardType() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				try {
					JSONObject jObj = api.getPublishBoardType();
					if (jObj != null) {
						msg.obj = jObj;
					}
				} catch (Exception e) {
					msg.obj = null;
					e.printStackTrace();
				}
				getPublishBoardTypeHandler.sendMessage(msg);
			};
		}.start();
	}

	private Handler getPublishBoardTypeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				if (msg.obj != null) {
					JSONObject jObj = (JSONObject) msg.obj;
					try {
						if (jObj.has("msg")
								&& jObj.getString("msg").equals("success")) {
							publishTypeList = new ArrayList<Map<String, Object>>();

							JSONArray arr = jObj.getJSONArray("data");
							publishTypes = new String[arr.length()];
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = (JSONObject) arr.get(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("typename", obj.get("typename"));
								map.put("typeid", obj.get("pkid"));
								publishTypeList.add(map);
								publishTypes[i] = (String) obj.get("typename");
							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(MomentsUploadActivity.this, "获取公告栏类型失败！",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}

	};
	
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
			httpConnection.disconnect(); 

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
}
