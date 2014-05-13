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
	/* ������ʶ����gallery��activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int PHOTO_PICKED_UPLOAD_WITH_DATA = 5021;
	public static final int PICCUT_BACK_DATA = 9999;
	private static final int PICCUT_SHOW_DATA = 9998;
	/* ������ʶ�������๦�ܵ�activity */
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int CAMERA_UPLOAD_WITH_DATA = 5023;
	private static final String IMAGE_FILE_LOCATION = Environment
			.getExternalStorageDirectory() + "/themeimg.jpg";// temp file
	/* ���յ���Ƭ�洢λ�� */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/metashpere");
	private String fileUrl;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final int PHOTORESOULT = 3;// ���
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

	private String title;// ����
	public String type;// ����
	private String bgImgUrl;// ����ͼԶ������
	private Bitmap bgBitmap;// ����ͼ

	private String curPhotoName = "curphoto.jpg";// ��ǰ��������
	public boolean isloading = false;//�Ƿ������ϣ����û�м�����ϾͲ����ٵ�ˢ��
	
	private AddCommentWindow addCommentWindow;
	private String curPublishID;//��¼��������ʱ��ǰ�����ID
	private String curMomentStr;//��ǰ�������۵�����

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
			titleTV.setText("����Ȧ");
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
					// ��������¼�ư�ťʱ����falseִ�и���OnTouch

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

			// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
			simperAdapter = new MomentsContactAdapter(MomentsActivity.this,
					messageItem, R.layout.moments_list_item_head,
					R.layout.moments_list_item, myapp, db);

			mylist.setDividerHeight(0);
			// ��Ӳ�����ʾ
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
						getMomentsDataAll3();//ˢ���б�
					}
					mylist.stopRefresh();
					mylist.stopLoadMore();
//					mylist.stopProgressBar();
					mylist.setRefreshTime("�ո�");
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
											// System.out.println("=====���ݿ����");
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
										makeText("�Ѿ���ȫ����Ϣ");
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
									makeText("�Ѿ���ȫ����Ϣ");
									// mymessagelistView.onRefreshComplete();
									// mylist.setSelection(mylist.getAdapter().getCount());
								}
								mylist.stopRefresh();
								mylist.stopLoadMore();
								mylist.stopfootProgressBar();
								mylist.setRefreshTime("�ո�");
							}
						}, 2000);
					} else {
						mylist.stopRefresh();
						mylist.stopLoadMore();
						mylist.stopfootProgressBar();
						mylist.setRefreshTime("�ո�");
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
				
				//�����һ�ζ���������С��20�������ǣ��ٴ�ˢ��ʱ���ٻ�ȡ����
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
					makeText("ͼƬ�����������ϴ�");
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
		// makeText("���");
		Intent intent = new Intent(this, RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "momentsuploadview");
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void openAddMomentsText() {
		flag = 2;
		//makeText("�������");
		Intent intent = new Intent(this, PostTextActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}

	public void openUpdateBg() {
		// ��������ͼƬ
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
		// intent.putExtra("aspectX", 1);// �ü������
		// intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX", 300);// ���ͼƬ��С
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
		// // �����ͽ��ļ��Ĵ洢��ʽ��uriָ������CameraӦ����
		//
		// // ����������Ҫ������Camera�󣬿��Է���Camera��ȡ����ͼƬ��
		// // ���ԣ�����ʹ��startActivityForResult������Camera
		// startActivityForResult(intent, CAMERA_UPLOAD_WITH_DATA);

		Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// �������ָ������������պ����Ƭ�洢��·��
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
	
	//���������ۿ�
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
			System.out.println("���۱༭��"+hasFocus);
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
		final String commentStr = "/΢Ц";
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
					Toast.makeText(MomentsActivity.this, "�����ɹ���", Toast.LENGTH_SHORT).show();
					
					//ˢ������
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
					Toast.makeText(MomentsActivity.this, "����ʧ�ܣ����Ժ�����...", Toast.LENGTH_SHORT).show();
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
		case PHOTO_PICKED_WITH_DATA: {// ����Gallery���ص�
			final Bitmap photo = BitmapFactory.decodeFile(IMAGE_FILE_LOCATION);
			try {
				// final Bitmap photo =
				// BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
				PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
		case CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ
			// doCropPhoto(mCurrentPhotoFile);
			// �����ļ�����·��������ڸ�Ŀ¼��
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/themeimg.jpg");
			startPhotoZoom(Uri.fromFile(picture));
			break;
		}
		case CAMERA_UPLOAD_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ
			System.out.println("���� uri=" + uri);
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
				// - 100)ѹ���ļ�
				PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
				// - 100)ѹ���ļ�
				PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
			PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
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
		// // aspectX aspectY �ǿ�ߵı���
		// intent.putExtra("aspectX", 1);
		// intent.putExtra("aspectY", 1);
		// // outputX outputY �ǲü�ͼƬ���
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
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
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
				// if(filesize > 358400) //filesize / 1024�õ��ļ�KB��λ��С
				// {
				// makeText("ͼƬ�����������ϴ�");
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
			startActivity(intent);// ��ʼ�������ת����
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
			startActivity(intent);// ��ʼ�������ת����
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
			startActivity(intent);// ��ʼ�������ת����
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
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString(key, value);// �洢���� ����1 ��key ����2 ��ֵ

		editor.commit();// �ύˢ������
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
