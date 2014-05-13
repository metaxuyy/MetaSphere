package ms.activitys.hotel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.XListView.IXListViewListener;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.MomentsContactAdapter;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MomentsActivity extends Activity {

	private static SharedPreferences share;
	private Douban api;
	private MyApp myapp;
	public static MomentsActivity instance;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;
	private DBHelperLogin logindb;

	private XListView mylist;
	public List<Map<String, Object>> messageItem = new ArrayList<Map<String, Object>>();
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int PHOTO_PICKED_UPLOAD_WITH_DATA = 5021;
	public static final int PICCUT_BACK_DATA = 9999;
	private static final int PICCUT_SHOW_DATA = 9998;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int CAMERA_UPLOAD_WITH_DATA = 5023;
	private static final String IMAGE_FILE_LOCATION = Environment
			.getExternalStorageDirectory() + "/themeimg.jpg";// temp file
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/metashpere");
	private String fileUrl;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final int PHOTORESOULT = 3;// 结果
	public static final int PHOTORESOULT_UPLOAD = 5;
	private MomentsContactAdapter simperAdapter;
	public ProgressBar sns_refresh_iv;
	private ImageButton fabu_btn;
	private boolean btn_vocie = false;
	private int flag = 1;
	private long startVoiceT, endVoiceT;
	private Timer timer;
	private Uri uri;
	private Animation animationin;
	private Animation animationout;
	private Animation animationoutRefresh;
	private int lastsize = 0;
	private int lastsize2 = 0;
	private Handler mHandler = new Handler();
	private boolean isnull = false;
	private int page = 0;
	public static FileUtils fileUtil = new FileUtils();

	private String title;// 标题
	public String type;// 类型
	private String bgImgUrl;// 背景图远程链接
	private Bitmap bgBitmap;// 背景图

	private String curPhotoName = "curphoto.jpg";// 当前拍照名称
	public boolean isloading = false;//是否加载完毕，如果没有加载完毕就不能再调刷新
	
	private AddCommentWindow addCommentWindow;
	private String curPublishID;//记录发表评论时当前公告的ID
	private String curMomentStr;//当前发表评论的内容

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moments_list_ui);

		Intent mIntent = getIntent();
		title = mIntent.getStringExtra("title");
		type = mIntent.getStringExtra("type");
		bgImgUrl = mIntent.getStringExtra("imgid");

//		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		m_NotificationManager.cancel(0);

		myapp = (MyApp) this.getApplicationContext();

		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		db = new DBHelperMessage(this, myapp);
		logindb = new DBHelperLogin(this);

		instance = this;

		animationin = AnimationUtils.loadAnimation(this, R.anim.sns_faded_in);
		animationout = AnimationUtils.loadAnimation(this, R.anim.sns_faded_out);
		animationoutRefresh = AnimationUtils.loadAnimation(this, R.anim.shake_report_dlg_translate_in);

		// Bundle bunde = this.getIntent().getExtras();
		// tag = bunde.getString("tag");

		Button break_btn = (Button) findViewById(R.id.home);
		break_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMainView();
			}
		});

		TextView titleTV = (TextView) findViewById(R.id.moments_title);
		if (title != null && !title.equals("")) {
			titleTV.setText(title);
			loadBgImg();
		} else {
			titleTV.setText("朋友圈");
			type = "";
		}

		initView();
	}
	
	private void loadBgImg() {
		try {
			if (bgImgUrl != null && !bgImgUrl.equals("")) {
				URL url = new URL(bgImgUrl);
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream in = conn.getInputStream();
				bgBitmap = BitmapFactory.decodeStream(in);

				UUID uuid = UUID.randomUUID();
				if (!fileUtil.isFileExist2(myapp.getPfprofileId()))
					fileUtil.createUserFile(myapp.getPfprofileId());
				String furl = fileUtil.getImageFile1bPath(
						myapp.getPfprofileId(), uuid.toString());
				myapp.saveMyBitmap(furl, bgBitmap);
				bgImgUrl = furl;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initView() {
		try {
			mylist = (XListView) findViewById(R.id.listvid);
			sns_refresh_iv = (ProgressBar) findViewById(R.id.sns_refresh_iv);

			// sns_refresh_iv.startAnimation(animationin);
			// sns_refresh_iv.setVisibility(View.VISIBLE);
			fabu_btn = (ImageButton) findViewById(R.id.fabu_btn);
			fabu_btn.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// 按下语音录制按钮时返回false执行父类OnTouch

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						startVoiceT = SystemClock.currentThreadTimeMillis();
						flag = 1;
						openAddMoments();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						endVoiceT = SystemClock.currentThreadTimeMillis();
						int time = (int) Math
								.ceil((endVoiceT - startVoiceT) / 100);
						if (flag == 1) {
							if (time < 1) {// time < 2
								timer.cancel();
								openAddMomentsImage();
							} else {
								timer.cancel();
								openAddMomentsText();
							}
						}
					}
					return true;
				}
			});

//			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			if (type != null && !type.equals("")) {
				map.put("momentsbgurl", bgImgUrl);
			} else {
				map.put("momentsbgurl", myapp.getThembgurl());
			}
			map.put("userbitmap", myapp.getUserimgbitmap());
			map.put("username", myapp.getUserName());
			messageItem.add(0, map);

			mylist.setVisibility(View.VISIBLE);
			mylist.setPullLoadEnable(true);

			// 生成适配器的Item和动态数组对应的元素
			simperAdapter = new MomentsContactAdapter(MomentsActivity.this,
					messageItem, R.layout.moments_list_item_head,
					R.layout.moments_list_item, myapp, db);

			mylist.setDividerHeight(0);
			// 添加并且显示
			mylist.setAdapter(simperAdapter);

			mylist.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					simperAdapter.closePinlunlayout();
					return false;
				}
			});

			mylist.setXListViewListener(new IXListViewListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					System.out.println("111===========================");
					if(!isloading)
					{
						showRefreshLayout();
						getMomentsDataAll3();//刷新列表
					}
					mylist.stopRefresh();
					mylist.stopLoadMore();
//					mylist.stopProgressBar();
					mylist.setRefreshTime("刚刚");
				}

				@Override
				public void onLoadMore() {
					// TODO Auto-generated method stub
					System.out.println("222===========================");
					mylist.startfootProgressBar();
					if (lastsize == 0) {
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									if (!isnull) {
										List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
										if (lastsize == 0) {
											// System.out.println("=====数据库加载");
											page = page + 1;
//											if(type == null || type.equals(""))
//											{
//												db.openDB();
//												list = db.getMomentsAllData(page,type);
//												db.closeDB();
//											}
//											else
//											{
//												JSONObject jobj = api.getMyFriendsMoments("0", String.valueOf(page),type);
//												if (jobj != null && jobj.has("data")) {
//													JSONArray jArr = (JSONArray) jobj.get("data");
//													list = myapp.getMyFriendMomentsList(jArr);
//												}
//											}
											
											JSONObject jobj = api.getMyFriendsMoments("0", String.valueOf(page),type);
											if (jobj != null && jobj.has("data")) {
												JSONArray jArr = (JSONArray) jobj.get("data");
												list = myapp.getMyFriendMomentsList(jArr);
											}
										}
										if (list.size() > 0) {
											String key = "";
											if(type.equals(""))
												key = "frierd";
											else
												key = type;
											if(myapp.getMomentsAllData().containsKey(key))
											{
												List<Map<String, Object>> alldata =  (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
												int yushu = alldata.size() % 20;
												if(yushu == 0)
												{
													alldata.addAll(list);
													myapp.getMomentsAllData().put(key, alldata);
												}
												else
												{
													list = removeDuplicate(list,yushu,key);
													alldata.addAll(list);
													myapp.getMomentsAllData().put(key, alldata);
												}
											}
											else
											{
												myapp.getMomentsAllData().put(key, list);
											}
											messageItem.addAll(list);
											if (list.size() < 20) {
												lastsize = list.size();
												isnull = false;
											} else {
												lastsize = 0;
												isnull = false;
											}
										} else {
											isnull = true;
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								// myAdapter.notifyDataSetChanged();
								// mymessagelistView.onRefreshComplete();
								if (!isnull) {
									if (lastsize != lastsize2)
										lastsize2 = 0;
									if (lastsize2 > 0) {
										makeText("已经是全部信息");
										// mymessagelistView.onRefreshComplete();
										// mylist.setSelection(mylist.getAdapter().getCount());
									} else {
										lastsize2 = lastsize;

										simperAdapter.getRefresh(messageItem);
										// int[] location = new int[2];
										// rl_layout.getLocationOnScreen(location);
										// int x = location[0];
										// int y = location[1];
										// System.out.println("x:"+x+"y:"+y);
										//
										// if(lastsize2 == 0)
										// mylist.setSelectionFromTop(21, y+60);
										// else
										// mylist.setSelectionFromTop(lastsize2+1,
										// y+60);
										// mylist.setSelection(mylist.getAdapter().getCount());

									}
								} else {
									makeText("已经是全部信息");
									// mymessagelistView.onRefreshComplete();
									// mylist.setSelection(mylist.getAdapter().getCount());
								}
								mylist.stopRefresh();
								mylist.stopLoadMore();
								mylist.stopfootProgressBar();
								mylist.setRefreshTime("刚刚");
							}
						}, 2000);
					} else {
						mylist.stopRefresh();
						mylist.stopLoadMore();
						mylist.stopfootProgressBar();
						mylist.setRefreshTime("刚刚");
					}
				}
			});

			getMomentsDataAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void changPenyouDataList(Map<String,Object> map)
	{
		try{
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			dlist.add(map);
			if(myapp.getMomentsAllData().containsKey("frierd"))
			{
				List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get("frierd");
				alldata.addAll(0,dlist);
				myapp.getMomentsAllData().put("frierd", alldata);
			}
			chanegDataList(dlist);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void chanegDataList(List<Map<String,Object>> list)
	{
		try{
			messageItem.addAll(1,list);
			simperAdapter.getRefresh(messageItem);
			page = messageItem.size() / 20 - 1;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void getMomentsDataAll() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
//					if(type == null || type.equals(""))
//					{
//						db.openDB();
//						lists = db.getMomentsAllData(0, type);
//						db.closeDB();
////						if (lists.size() == 0) {
////							JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
////							if (jobj != null && jobj.has("data")) {
////								JSONArray jArr = (JSONArray) jobj.get("data");
////								lists = myapp.getMyFriendMomentsList(jArr);
////								db.openDB();
////								db.saveMomentsAll(lists);
////								lists = db.getMomentsAllData(0, type);
////								db.closeDB();
////							}
////						}
//					}
//					else
//					{
//						JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
//						if (jobj != null && jobj.has("data")) {
//							JSONArray jArr = (JSONArray) jobj.get("data");
//							lists = myapp.getMyFriendMomentsList(jArr);
////							db.openDB();
////							db.saveMomentsAll(lists);
////							lists = db.getMomentsAllData(0, type);
////							db.closeDB();
//						}
//					}
					String key = "";
					if(type.equals(""))
						key = "frierd";
					else
						key = type;
					if(myapp.getMomentsAllData().containsKey(key))
					{
						lists =  (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
						page = lists.size() / 20 - 1;
					}
					else
					{
						JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
						if (jobj != null && jobj.has("data")) {
							JSONArray jArr = (JSONArray) jobj.get("data");
							lists = myapp.getMyFriendMomentsList(jArr);
							
							if(myapp.getMomentsAllData().containsKey(key))
							{
								List<Map<String, Object>> alldata = (List<Map<String, Object>>)myapp.getMomentsAllData().get(key);
								alldata.addAll(lists);
								myapp.getMomentsAllData().put(key, alldata);
							}
							else
							{
								myapp.getMomentsAllData().put(key, lists);
//								myapp.getMomentsAllDataPage().put(key, page);
							}
	//						db.openDB();
	//						db.saveMomentsAll(lists);
	//						lists = db.getMomentsAllData(0, type);
	//						db.closeDB();
						}
					}
					
					msg.obj = lists;
				} catch (Exception ex) {
					// Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMomentsDataAll3() {
//		showRefreshLayout();
		isloading = true;
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;

				try {
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
//					if(type == null || type.equals(""))
//					{
//						db.openDB();
//						lists = db.getMomentsAllData(0, type);
//						db.closeDB();
//						if (lists.size() == 0) {
//							JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
//							if (jobj != null && jobj.has("data")) {
//								JSONArray jArr = (JSONArray) jobj.get("data");
//								lists = myapp.getMyFriendMomentsList(jArr);
//								db.openDB();
//								db.saveMomentsAll(lists);
//								lists = db.getMomentsAllData(0, type);
//								db.closeDB();
//							}
//						}
//					}
//					else
//					{
//						JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
//						if (jobj != null && jobj.has("data")) {
//							JSONArray jArr = (JSONArray) jobj.get("data");
//							lists = myapp.getMyFriendMomentsList(jArr);
////							db.openDB();
////							db.saveMomentsAll(lists);
////							lists = db.getMomentsAllData(0, type);
////							db.closeDB();
//						}
//					}
					
					JSONObject jobj = api.getMyFriendsMoments("0", "0",type);
					if (jobj != null && jobj.has("data")) {
						JSONArray jArr = (JSONArray) jobj.get("data");
						lists = myapp.getMyFriendMomentsList(jArr);
//						db.openDB();
//						db.saveMomentsAll(lists);
//						lists = db.getMomentsAllData(0, type);
//						db.closeDB();
					}
					
					msg.obj = lists;
				} catch (Exception ex) {
					// Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	public void getMomentsDataAll2() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;

				try {
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					db.openDB();
					lists = db.getMomentsAllData(0, type);
					db.closeDB();
					msg.obj = lists;
				} catch (Exception ex) {
					// Log.i("erroyMessage", ex.getMessage());
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
				List<Map<String, Object>> list3 = (List<Map<String, Object>>) msg.obj;
				if (list3 != null) {
					messageItem.addAll(list3);
					simperAdapter.getRefresh(messageItem);
				}
				
				//如果第一次读到的数据小于20条，设标记，再次刷的时候不再获取数据
				if(messageItem.size()<20){
					isnull = true;
				}
				break;
			case 1:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				String fileurl = (String) map.get("fileurl");
				Bitmap photo = (Bitmap) map.get("img");
				if (fileurl != null && !fileurl.equals("")) {
					simperAdapter.setThemebg(photo);
					UUID uuid = UUID.randomUUID();
					if (!fileUtil.isFileExist2(myapp.getPfprofileId()))
						fileUtil.createUserFile(myapp.getPfprofileId());
					String furl = fileUtil.getImageFile1bPath(
							myapp.getPfprofileId(), uuid.toString());
					myapp.saveMyBitmap(furl, photo);
					myapp.setThembgurl(furl);
					saveSharedPerferences("thembgurl", furl);
					String user = share.getString("user", "");
					String pwa = share.getString("pwa", "");
					String loginid = logindb.getLoginId(user, pwa);
					logindb.updateThembgurl(furl, loginid);
				} else {
					makeText("图片过大请重新上传");
				}
				loadDialog.dismiss();
				break;
			case 2:
				timer.cancel();
				// fabu_btn.setOnTouchListener(null);
				openAddMomentsText();
				break;
			case 3:
				List<Map<String, Object>> list4 = (List<Map<String, Object>>) msg.obj;
				if (list4 != null) {
					Map<String, Object> map2 = messageItem.get(0);
					messageItem.removeAll(messageItem);
					messageItem.add(0, map2);
					messageItem.addAll(list4);
					simperAdapter.setSCROLL_STATE_TOUCH_SCROLL(false);
					simperAdapter.getRefresh(messageItem);
				}
				endRefreshLayout();
				isloading = false;
				break;
			}
		}
	};

	public void openAddMoments() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message mesasge = new Message();
				mesasge.what = 2;
				handler.sendMessage(mesasge);
			}
		}, 500, 500);
	}

	public void openAddMomentsImage() {
		flag = 2;
		// makeText("点击");
		Intent intent = new Intent(this, RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "momentsuploadview");
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void openAddMomentsText() {
		flag = 2;
		//makeText("长按点击");
		Intent intent = new Intent(this, PostTextActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void openUpdateBg() {
		// 更换背景图片
		Intent intent = new Intent(this, RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "momentsview");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void openImageCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), "themeimg.jpg")));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	public void openImagePoto() {
		// File sdcardTempFile = new File(IMAGE_FILE_LOCATION);
		// Intent intent = new Intent("android.intent.action.PICK");
		// intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
		// "image/*");
		// intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		// intent.putExtra("crop", "true");
		// intent.putExtra("aspectX", 1);// 裁剪框比例
		// intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX", 300);// 输出图片大小
		// intent.putExtra("outputY", 300);
		// intent.putExtra("noFaceDetection", true);
		// intent.putExtra("return-data", true);
		// startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICCUT_SHOW_DATA);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void openUploadImageCamera() {
		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// ContentValues values = new ContentValues(3);
		// // values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
		// values.put(MediaStore.Images.Media.DESCRIPTION,
		// "this is description");
		// values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		// uri = getContentResolver().insert(
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		//
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// System.out.println("uri=="+uri);
		// // 这样就将文件的存储方式和uri指定到了Camera应用中
		//
		// // 由于我们需要调用完Camera后，可以返回Camera获取到的图片，
		// // 所以，我们使用startActivityForResult来启动Camera
		// startActivityForResult(intent, CAMERA_UPLOAD_WITH_DATA);

		Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 下面这句指定调用相机拍照后的照片存储的路径
		System.out.println("***curPhotoName==" + curPhotoName);
		intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), curPhotoName)));
		startActivityForResult(intent1, CAMERA_UPLOAD_WITH_DATA);
	}

	public void openUploadImagePoto() {
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intent, PHOTO_PICKED_UPLOAD_WITH_DATA);
		// overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);

		Intent intent = new Intent(MomentsActivity.this,
				SelImageFolderActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}
	
	//打开增加评论框
	public void openAddComment(String publishID){
		curPublishID = publishID;
		addCommentWindow = new AddCommentWindow(
				MomentsActivity.this, itemsOnClickForAddComment, etFocusChange);
		addCommentWindow.showAtLocation(MomentsActivity.this
				.findViewById(R.id.main_rlayout), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	private OnClickListener itemsOnClickForAddComment = new OnClickListener() {

		public void onClick(View v) {
			addCommentWindow.dismiss();
			showMyLoadingDialog(getString(R.string.hotel_label_200));
			saveComment();
		}

	};
	
	private OnFocusChangeListener etFocusChange = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			System.out.println("评论编辑框"+hasFocus);
			if(hasFocus){
//				addCommentWindow.showAtLocation(MomentsActivity.this
//						.findViewById(R.id.main_rlayout), Gravity.CENTER_HORIZONTAL, 0, 300);
			}
		}
	};
	
	private void saveComment() {
		final String commentStr = addCommentWindow.addCommentET.getText().toString();
		final String publishID = curPublishID;
		curMomentStr = commentStr;
		try {
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					try {
						JSONObject jsonObj = api.sendComment(commentStr, publishID);
						if (jsonObj != null) {
							String rsstr = (String) jsonObj.get("message");
							if (rsstr != null && rsstr.equals("0")) {
								msg.obj = "1";
							}
						} else {
							msg.obj = "0";
						}
					} catch (Exception ex) {
						msg.obj = "0";
						ex.printStackTrace();
					}
					handlerSave.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveCommentZhan(String publishIDs) {
		curPublishID = publishIDs;
		showMyLoadingDialog(getString(R.string.hotel_label_200));
		final String commentStr = "/微笑";
		final String publishID = curPublishID;
		curMomentStr = commentStr;
		try {
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					try {
						JSONObject jsonObj = api.sendComment(commentStr, publishID);
						if (jsonObj != null) {
							String rsstr = (String) jsonObj.get("message");
							if (rsstr != null && rsstr.equals("0")) {
								msg.obj = "1";
							}
						} else {
							msg.obj = "0";
						}
					} catch (Exception ex) {
						msg.obj = "0";
						ex.printStackTrace();
					}
					handlerSave.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Handler handlerSave = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String tag = (String) msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if (tag != null && tag.equals("1")) {
					Toast.makeText(MomentsActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
					
					//刷新评论
					for(int i=0;i<messageItem.size();i++){
						Map<String, Object> map = messageItem.get(i);
						if(map.containsKey("pkid") && map.get("pkid").equals(curPublishID)){
							Map<String,String> commentmap = new HashMap<String,String>();
							commentmap.put("userName", myapp.getUserName());
							commentmap.put("discusDesc", curMomentStr);
							((List<Map<String,String>>)map.get("commentlist")).add(commentmap);
							
							break;
						}
					}
					simperAdapter.getRefresh(messageItem);
					if(MomentsImageListActivity.instance != null)
						MomentsImageListActivity.instance.setZhanSize();
				} else {
					Toast.makeText(MomentsActivity.this, "发布失败，请稍后重试...", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != PICCUT_BACK_DATA) {
			if (resultCode != RESULT_OK)
				return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			final Bitmap photo = BitmapFactory.decodeFile(IMAGE_FILE_LOCATION);
			try {
				// final Bitmap photo =
				// BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
				File myCaptureFile = new File(PHOTO_DIR, "mylog.jpg");
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
				fileUrl = myCaptureFile.getPath();
				showMyLoadingDialog(getString(R.string.user_info_lable_1));
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;

						String fileurl = updateThemeImg();
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("fileurl", fileurl);
						map.put("img", photo);
						msg.obj = map;
						handler.sendMessage(msg);
					}
				}.start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		case PICCUT_SHOW_DATA: {
			Uri uri = data.getData();
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(uri, null, null, null, null);
			while (cursor.moveToNext()) {
				fileUrl = cursor.getString(1);
			}

			Intent intent = new Intent(MomentsActivity.this,
					ImageCutActivity.class);
			File sdcardTempFile = new File(fileUrl);
			intent.putExtra("uri", Uri.fromFile(sdcardTempFile));
			startActivityForResult(intent, PICCUT_BACK_DATA);
			overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			break;
		}
		case PICCUT_BACK_DATA: {
			try {
				final Bitmap photo = myapp.getCutImg();
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
				File myCaptureFile = new File(PHOTO_DIR, "mylog.jpg");
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
				fileUrl = myCaptureFile.getPath();
				showMyLoadingDialog(getString(R.string.user_info_lable_1));
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;

						String fileurl = updateThemeImg();
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("fileurl", fileurl);
						map.put("img", photo);
						msg.obj = map;
						handler.sendMessage(msg);
					}
				}.start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		case PHOTO_PICKED_UPLOAD_WITH_DATA: {
			Uri uri = data.getData();
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(uri, null, null, null, null);
			String imageSize = "";
			while (cursor.moveToNext()) {
				Log.i("====_id", cursor.getString(0) + "");
				Log.i("====_path", cursor.getString(1) + "");
				fileUrl = cursor.getString(1);
				Log.i("====_size", cursor.getString(2) + "");
				imageSize = cursor.getString(2);
				Log.i("====_display_name", cursor.getString(3) + "");
				// fileName = cursor.getString(3);
			}
			openMomentsUploadView(fileUrl);
			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			// doCropPhoto(mCurrentPhotoFile);
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/themeimg.jpg");
			startPhotoZoom(Uri.fromFile(picture));
			break;
		}
		case CAMERA_UPLOAD_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			System.out.println("返回 uri=" + uri);
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/" + curPhotoName);
			// uri = Uri.fromFile(temp);
			// if (uri != null) {
			// ContentResolver cr = getContentResolver();
			// Cursor cursor = cr.query(uri, null, null, null, null);
			// String imageSize = "";
			// while (cursor.moveToNext()) {
			// fileUrl = cursor.getString(1);
			// imageSize = cursor.getString(2);
			// // fileName = cursor.getString(3);
			// }
			// openMomentsUploadView(fileUrl);
			// }

			if (temp != null){
				fileUrl = temp.getPath();
			}
			openMomentsUploadView(fileUrl);

			break;
		}
		case 1: {
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;
		}
		case PHOTORESOULT: {
			try {
				Bundle extras = data.getExtras();
				// if (extras != null) {
				// final Bitmap photo = extras.getParcelable("data");
				final Bitmap photo = BitmapFactory
						.decodeFile(IMAGE_FILE_LOCATION);
				// final Bitmap photo =
				// BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				// ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0
				// - 100)压缩文件
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
				File myCaptureFile = new File(PHOTO_DIR, "mylog.jpg");
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
				fileUrl = myCaptureFile.getPath();
				showMyLoadingDialog(getString(R.string.user_info_lable_1));
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;

						String fileurl = updateThemeImg();
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("fileurl", fileurl);
						map.put("img", photo);
						msg.obj = map;
						handler.sendMessage(msg);
					}
				}.start();
				// }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		case PHOTORESOULT_UPLOAD: {
			try {
				Bundle extras = data.getExtras();
				// if (extras != null) {
				// final Bitmap photo = extras.getParcelable("data");
				final Bitmap photo = BitmapFactory
						.decodeFile(IMAGE_FILE_LOCATION);
				// final Bitmap photo =
				// BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				// ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0
				// - 100)压缩文件
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
				UUID uuid = UUID.randomUUID();
				File myCaptureFile = new File(PHOTO_DIR, uuid + ".jpg");
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
				fileUrl = myCaptureFile.getPath();
				openMomentsUploadView(fileUrl);
				// }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		}
	}
	
	public void saveCutImg(){
		try {
			final Bitmap photo = myapp.getCutImg();
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			File myCaptureFile = new File(PHOTO_DIR, "mylog.jpg");
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
			fileUrl = myCaptureFile.getPath();
			showMyLoadingDialog(getString(R.string.user_info_lable_1));
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;

					String fileurl = updateThemeImg();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("fileurl", fileurl);
					map.put("img", photo);
					msg.obj = map;
					handler.sendMessage(msg);
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void startPhotoZoom(Uri uri) {
		// File sdcardTempFile = new File(IMAGE_FILE_LOCATION);
		// Intent intent = new Intent("com.android.camera.action.CROP");
		// intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		// intent.putExtra("crop", "true");
		// intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		// // aspectX aspectY 是宽高的比例
		// intent.putExtra("aspectX", 1);
		// intent.putExtra("aspectY", 1);
		// // outputX outputY 是裁剪图片宽高
		// intent.putExtra("outputX", 300);
		// intent.putExtra("outputY", 300);
		//
		// intent.putExtra("scale", false);
		// //
		// startActivityForResult(intent, PHOTORESOULT);

		Intent intent = new Intent(MomentsActivity.this, ImageCutActivity.class);
		File sdcardTempFile = new File(IMAGE_FILE_LOCATION);
		intent.putExtra("uri", Uri.fromFile(sdcardTempFile));
		startActivityForResult(intent, PICCUT_BACK_DATA);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void startUploadPhotoZoom(Uri uri) {
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

		intent.putExtra("scale", false);
		//
		startActivityForResult(intent, PHOTORESOULT_UPLOAD);
	}

	public Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);

		intent.putExtra("scale", false);
		intent.putExtra("return-data", true);
		return intent;
	}

	public String updateThemeImg() {
		String imgurl = "";
		boolean tag = true;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("pfid", myapp.getPfprofileId());
			long fsize = 0;
			Map<String, File> files = new HashMap<String, File>();
			if (fileUrl != null && !fileUrl.equals("")) {
				File file = new File(fileUrl);
				long filesize = file.length();
				// if(filesize > 358400) //filesize / 1024得到文件KB单位大小
				// {
				// makeText("图片过大请重新上传");
				// tag = false;
				// }
				fsize = fsize + filesize;
				files.put(file.getName(), file);
			}

			if (tag) {
				JSONObject job = api.updatThemeImg(params, files);
				if (job != null) {
					if (job.getString("success").equals("true")) {
						imgurl = job.getString("imgurl");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return imgurl;
	}

	public void getImageBitmaplist(int position, int listindex) {
		try {
			List<Map<String, String>> filelist = simperAdapter.getFileList(position);
			String content = simperAdapter.getItemContent(position);
			int zhansize = simperAdapter.getCommentZhanSize(position);
			String curPublishID = simperAdapter.getCurPublishID(position);
			List<Bitmap> imglist = new ArrayList<Bitmap>();
			List<String> imgNamelist = new ArrayList<String>();
			for (int i = 0; i < filelist.size(); i++) {
				Map<String, String> filemap = filelist.get(i);
				String fileurl = filemap.get("fileurl");
				String name = fileurl.substring(fileurl.lastIndexOf("/")+1,fileurl.length());
				imgNamelist.add(name);
//				Bitmap bitmap = simperAdapter.getImageCache(fileurl);
				Bitmap bitmap = myapp.getLoacalBitmap(fileurl);
				imglist.add(bitmap);
			}
			openMomentsImagesView(imglist, listindex,content,imgNamelist,zhansize,curPublishID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainView();
			return false;
		}
		return false;
	}

	public void openMainView() {
		try {
//			if(simperAdapter != null)
//				simperAdapter.removeAllResources();
			instance = null;
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass(this, MainTabActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
			this.finish();
//			if(simperAdapter != null)
//				simperAdapter.removeAllResources();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void openMomentsUploadView(String filepath) {
		try {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass(this, MomentsUploadActivity.class);
			bundle.putString("filepath", filepath);
			bundle.putString("type", type);
			intent.putExtras(bundle);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void openMomentsImagesView(List<Bitmap> imglist, int indext,String content,List<String> imgNamelist,int zhansize,String curPublishID) {
		try {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass(this, MomentsImageListActivity.class);
			bundle.putInt("index", indext);
			bundle.putString("content", content);
			bundle.putString("curPublishID", curPublishID);
			bundle.putInt("zhansize", zhansize);
			myapp.setMomentsimgs(imglist);
			myapp.setMomentsimgnames(imgNamelist);
			intent.putExtras(bundle);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void endRefreshLayout()
	{
//		if(sns_refresh_iv.getVisibility() == View.VISIBLE)
//		{
//			sns_refresh_iv.startAnimation(animationoutRefresh);
//			sns_refresh_iv.setVisibility(View.GONE);
//		}
		mylist.stopProgressBar();
	}
	
	public void showRefreshLayout()
	{
		if(sns_refresh_iv.getVisibility() == View.GONE)
		{
			sns_refresh_iv.startAnimation(animationoutRefresh);
			sns_refresh_iv.setVisibility(View.VISIBLE);
		}
	}

	public void showMyLoadingDialog(String message) {
		loadDialog = new MyLoadingDialog(this, message, R.style.MyDialog);
		loadDialog.show();
	}

	public void saveSharedPerferences(String key, String value) {
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值

		editor.commit();// 提交刷新数据
	}

	public void makeText(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	/** List order not maintained **/  
    public List<Map<String,Object>> removeDuplicate(List<Map<String,Object>> arlList,int yushu,String key)  
    {  
    	List<Map<String,Object>> dlist = (List<Map<String,Object>>)myapp.getMomentsAllData().get(key);
    	int size = dlist.size();
    	size = size - yushu;
    	for(int i=size;i<dlist.size();i++)
    	{
    		Map<String,Object> map = dlist.get(i);
    		String pkid = (String)map.get("pkid");
    		Iterator<Map<String,Object>> stuIter = arlList.iterator();  
            while (stuIter.hasNext()) {  
            	Map<String,Object> map2 = stuIter.next();  
            	String pkid2 = (String)map2.get("pkid");
                if (pkid2.equals(pkid))  
                    stuIter.remove();  
            }  
    	}
    	return arlList;
//	     HashSet h = new HashSet(arlList);
//	     arlList.clear();  
//	     arlList.addAll(h);
    }
}
