package ms.activitys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ms.activitys.hotel.SelectPicPopupWindow;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.BoardGridAdapter;
import ms.globalclass.listviewadapter.MenuBtnGridAdapter;
import ms.globalclass.listviewadapter.MenuBtnShowGridAdapter;
import ms.globalclass.listviewadapter.MenuPageModeAdapter;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.MyGridView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MenuSetActivity extends Activity {
	
	public static MenuSetActivity instance;
	private static FileUtils fileUtil = new FileUtils();
	
	private Button backBtn;
	private Button saveBtn;
	private TextView menuBtnTV;
	private LinearLayout menuBtnHadLL;
	private LinearLayout menuBtnListLL;
	private TextView pageImgInfoTV1;
	private TextView pageImgInfoTV2;
	
	private Spinner menuPageModeSp;
	private List<Map<String, Object>> menuPageModeList;
	private MenuPageModeAdapter menuPageModeAdapter;
	
	private MyGridView menuBtnHadGV;
	private List<Map<String, Object>> menuBtnHadList;
	private MenuBtnShowGridAdapter menuBtnHadAdapter;
	
	private MyGridView menuBtnListGV;
	private List<Map<String, Object>> menuBtnListList;
	private MenuBtnShowGridAdapter menuBtnListAdapter;

	private MyGridView menuBtnGV;
	private List<Map<String, Object>> menuBtnList;
	private MenuBtnGridAdapter menuBtnAdapter;

	private MyGridView pageImgGV;
	private List<Map<String, Object>> pageImgList;
	private BoardGridAdapter pageImgAdapter;

	private SelectPicPopupWindow menuWindow;
	private Uri uri;
	private static final int PHOTO_PICKED_UPLOAD_WITH_DATA = 5021;
	private static final int CAMERA_UPLOAD_WITH_DATA = 5023;

	private static SharedPreferences share;
	private Douban api;
	private MyApp myapp;

	private String menuName;
	private String menuPageID;
	private String menuPageStyle;
	private String menuPageStyleId;
	private String menuPageImgNum;
	
	private MyLoadingDialog loadDialog;
	
	private static final String ADDBTN = "addBtn";
	private static final String EDITBTN = "editBtn";
	private String tag = ADDBTN; //addBtn:仅添加删除功能按钮 editBtn:可编辑功能按钮
	private int menuModeSpSelIndex; //记录页面模板选中index
	//private boolean isBtnHadNo = true;//记录已有按钮Grid是否为空
	//private boolean isBtnListNo = true;//记录可添加按钮Grid是否为空
	
	private List<String> delPageImgArr;
	
	private int pageImgW = 0;
	private int pageImgH = 0;
	
	private static int selImgW = 0;
	private static int selImgH = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuset);
		
		instance = this;

		myapp = (MyApp) this.getApplicationContext();

		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		Bundle bunde = this.getIntent().getExtras();
		menuName = bunde.getString("menuName");
		
		menuBtnAdapter = new MenuBtnGridAdapter(MenuSetActivity.this);

		initView();
		showMyLoadingDialog();
		if(tag.equals(ADDBTN)){
			menuBtnTV.setVisibility(View.GONE);
			menuBtnGV.setVisibility(View.GONE);
			menuBtnHadLL.setVisibility(View.VISIBLE);
			menuBtnHadGV.setVisibility(View.VISIBLE);
			menuBtnListLL.setVisibility(View.VISIBLE);
			menuBtnListGV.setVisibility(View.VISIBLE);
			
			loadPageData();
		}else if(tag.equals(EDITBTN)){
			menuBtnTV.setVisibility(View.VISIBLE);
			menuBtnGV.setVisibility(View.VISIBLE);
			menuBtnHadLL.setVisibility(View.GONE);
			menuBtnHadGV.setVisibility(View.GONE);
			menuBtnListLL.setVisibility(View.GONE);
			menuBtnListGV.setVisibility(View.GONE);
			
			loadData();
		}
	}

	private void initView() {
		//页面展现模板选择
		menuPageModeSp = (Spinner) findViewById(R.id.menuset_pageShowSp);
		menuPageModeSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				System.out.println("页面模板选中的是：" + arg2);
				if(arg2 != menuModeSpSelIndex){
					Map<String, Object> mapSel = menuPageModeList.get(arg2);
					String styleId = (String) mapSel.get("pkid");
					String modeIden = (String) mapSel.get("modeIden");
					String imgNum = (String) mapSel.get("modeImgNum");
					mapSel.put("menuPageModeSel", "1");
					menuPageStyle = modeIden;
					menuPageStyleId = styleId;
					menuPageImgNum = imgNum;
					
					Map<String, Object> mapOldSel = menuPageModeList.get(menuModeSpSelIndex);
					mapOldSel.put("menuPageModeSel", "0");
					
					menuPageModeAdapter.notifyDataSetChanged();
					
					menuPageModeSp.setSelection(arg2);
					menuModeSpSelIndex = arg2;
				}
				
				//根据图片数量，更改提示信息
				if(menuPageImgNum!=null && menuPageImgNum.equals("1")){
					pageImgInfoTV1.setText("该模板最多上传一张图片作为页面背景，默认选择已有图片第一张");
				}else{
					pageImgInfoTV1.setText("该模板最多可上传五张图片，并且五张图片宽高必须统一");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 页面主图
		pageImgGV = (MyGridView) findViewById(R.id.menuset_pageImgGV);
		pageImgGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		pageImgGV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (position == (parent.getChildCount() - 1)) {
					if (pageImgList.size() == 6) {
						Toast.makeText(MenuSetActivity.this, "最多只可以上传5张图片",
								Toast.LENGTH_LONG).show();
						return;
					}

					menuWindow = new SelectPicPopupWindow(MenuSetActivity.this,
							itemsOnClick);
					// 显示窗口
					menuWindow.showAtLocation(findViewById(R.id.main_rlayout),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				} else {
					if(delPageImgArr == null){
						delPageImgArr = new ArrayList<String>();
					}
					Map<String, Object> delImgMap = pageImgList.get(position);
					delPageImgArr.add((String) delImgMap.get("imgname"));
					
					pageImgList.remove(position);
					pageImgAdapter.notifyDataSetChanged();
					
					if(pageImgList.size()==1){
						pageImgH = 0;
						pageImgW = 0;
						pageImgInfoTV2.setText("");
					}
				}
			}
		});

		Map<String, Object> mapAdd = new HashMap<String, Object>();
		Resources resources = this.getResources();
		Drawable btnDrawable = resources
				.getDrawable(R.drawable.board_photo_btn_add_selector);
		mapAdd.put("imgid", btnDrawable);
		mapAdd.put("imgname", "add");

		pageImgList = new ArrayList<Map<String, Object>>();
		pageImgList.add(mapAdd);
		pageImgAdapter = new BoardGridAdapter(this);
		pageImgAdapter.setList(pageImgList);
		pageImgGV.setAdapter(pageImgAdapter);

		// 页面按钮
		menuBtnTV = (TextView) findViewById(R.id.menuset_menuBtnTV);
		menuBtnGV = (MyGridView) findViewById(R.id.menuset_menuBtnGV);
		menuBtnGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		menuBtnGV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
			}
		});
		
		//保存
		saveBtn = (Button) findViewById(R.id.menuset_saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyLoadingDialog();
				//savePageHtml();
				
				savePageDataToServer();
			}
		});
		
		//已有功能按钮
		menuBtnHadLL = (LinearLayout) findViewById(R.id.menuset_menuBtnHad);
		menuBtnHadGV = (MyGridView) findViewById(R.id.menuset_menuBtnHadGV);
		menuBtnHadGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		menuBtnHadGV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Map<String, Object> map = menuBtnHadList.get(position);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuSetActivity.this, MenuBtnShowActivity.class);
				bundle.putString("menuName", (String) map.get("menuBtnSkipName"));
				bundle.putString("menuID", (String) map.get("pkid"));
				bundle.putString("menuSkipID", (String) map.get("menuBtnSkipID"));
				bundle.putParcelable("menuIcon", (Bitmap)map.get("menuBtnSkipIcon"));
				bundle.putString("menuInfo", (String) map.get("menuBtnSkipInfo"));
				bundle.putString("tag", "isHad");
				intent.putExtras(bundle);
				startActivity(intent);// 开始界面的跳转函数
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
		
//		Map<String, Object> menuMap = new HashMap<String, Object>();
//		menuMap.put("menuBtnSkipName", "");
//		menuMap.put("menuBtnSkipIcon", null);
		
		menuBtnHadList = new ArrayList<Map<String, Object>>();
		//menuBtnHadList.add(menuMap);
		menuBtnHadAdapter = new MenuBtnShowGridAdapter(MenuSetActivity.this);
		menuBtnHadAdapter.setList(menuBtnHadList);
		menuBtnHadGV.setAdapter(menuBtnHadAdapter);
		
		//可添加功能按钮
		menuBtnListLL = (LinearLayout) findViewById(R.id.menuset_menuBtnList);
		menuBtnListGV = (MyGridView) findViewById(R.id.menuset_menuBtnListGV);
		menuBtnListGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		menuBtnListGV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Map<String, Object> map = menuBtnListList.get(position);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuSetActivity.this, MenuBtnShowActivity.class);
				bundle.putString("menuName", (String) map.get("menuBtnSkipName"));
				bundle.putString("menuSkipID", (String) map.get("pkid"));
				bundle.putParcelable("menuIcon", (Bitmap)map.get("menuBtnSkipIcon"));
				bundle.putString("menuInfo", (String) map.get("menuBtnSkipInfo"));
				bundle.putString("tag", "isList");
				intent.putExtras(bundle);
				startActivity(intent);// 开始界面的跳转函数
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
		
		menuBtnListList = new ArrayList<Map<String, Object>>();
		//menuBtnListList.add(menuMap);
		menuBtnListAdapter = new MenuBtnShowGridAdapter(MenuSetActivity.this);
		menuBtnListAdapter.setList(menuBtnListList);
		menuBtnListGV.setAdapter(menuBtnListAdapter);
		
		pageImgInfoTV1 = (TextView) findViewById(R.id.menuset_pageImgInfoTV1);
		pageImgInfoTV2 = (TextView) findViewById(R.id.menuset_pageImgInfoTV2);
		backBtn = (Button) findViewById(R.id.menuset_backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void loadMenuSkipList(){
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;

				try {
					JSONObject jobj = api.getPageSkip();
					if (jobj != null) {
						msg.obj = jobj;
					} else {
						msg.obj = null;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private void loadPageModeList(){
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 4;

				try {
					JSONObject jobj = api.getPageModeList();
					msg.obj = jobj;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private void loadPageData(){
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;

				try {
					JSONObject jobj = api.getMenuByName(menuName);
					if (jobj != null) {
						msg.obj = jobj;
					} else {
						msg.obj = null;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private void loadData() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					JSONObject jobjIS = api.getPageIconSkip();
					JSONArray iconArr = null;
					JSONArray skipArr = null;
					if(jobjIS.has("icon")){
						iconArr = (JSONArray) jobjIS.get("icon");
					}
					if(jobjIS.has("skip")){
						skipArr = (JSONArray) jobjIS.get("skip");
					}
					
					String menuPageId = menuName + "_" + myapp.getUserNameId();
					boolean isHas = fileUtil.isFileExist2(menuPageId);
					if(isHas){
						String dataFileName = fileUtil.getStoreImageFilePath(menuPageId, "data.txt");
						FileInputStream fin = new FileInputStream(dataFileName);
						int length = fin.available();
						byte[] buffer = new byte[length];
						fin.read(buffer);
						String fileData = EncodingUtils.getString(buffer, "UTF-8"); 
				        fin.close();  
				        
				        System.out.println("读取本地数据："+fileData);
						JSONObject jobj = new JSONObject(fileData);
						if(iconArr!=null)
							jobj.put("icon", iconArr);
						if(skipArr!=null)
							jobj.put("skip", skipArr);
						msg.obj = jobj;
					}else{
						JSONObject jobj = api.getMenuByName(menuName);
						if (jobj != null) {
							if(iconArr!=null)
								jobj.put("icon", iconArr);
							if(skipArr!=null)
								jobj.put("skip", skipArr);
							msg.obj = jobj;
						} else {
							msg.obj = null;
						}
					}
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
				if(msg.obj != null){
					JSONObject jobj = (JSONObject) msg.obj;
					
					//图标数据
					JSONArray iconArr = null;
					if(jobj.has("icon")){
						try {
							iconArr = (JSONArray) jobj.get("icon");
							System.out.println("图标数据："+iconArr.toString());
							if(!fileUtil.isFileExist2("menuIcon"))
			            	{
			            		fileUtil.createUserFile("menuIcon");
			            	}
							String menufurl = fileUtil.getStoreImageFilePath("menuIcon", "data.txt");
							try {
								FileOutputStream fout = new FileOutputStream(menufurl);
								byte[] bytes = iconArr.toString().getBytes();
								fout.write(bytes);
								fout.close();
							} catch (Exception e) {
								e.printStackTrace();
							} 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						//读取本地数据
						boolean isHas = fileUtil.isFileExist2("menuIcon");
						if(isHas){
							try {
								String iconFileName = fileUtil.getStoreImageFilePath("menuIcon", "data.txt");
								FileInputStream fin = new FileInputStream(iconFileName);
								int length = fin.available();
								byte[] buffer = new byte[length];
								fin.read(buffer);
								String iconfileData = EncodingUtils.getString(buffer, "UTF-8"); 
						        fin.close();
						        
						        iconArr = new JSONArray(iconfileData);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					//跳转数据
					JSONArray skipArr = null;
					if(jobj.has("skip")){
						try {
							skipArr = (JSONArray) jobj.get("skip");
							System.out.println("跳转数据："+skipArr.toString());
							if(!fileUtil.isFileExist2("menuSkip"))
			            	{
			            		fileUtil.createUserFile("menuSkip");
			            	}
							String menufurl = fileUtil.getStoreImageFilePath("menuSkip", "data.txt");
							try {
								FileOutputStream fout = new FileOutputStream(menufurl);
								byte[] bytes = skipArr.toString().getBytes();
								fout.write(bytes);
								fout.close();
							} catch (Exception e) {
								e.printStackTrace();
							} 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						//读取本地数据
						boolean isHas = fileUtil.isFileExist2("menuSkip");
						if(isHas){
							try {
								String skipFileName = fileUtil.getStoreImageFilePath("menuSkip", "data.txt");
								FileInputStream fin = new FileInputStream(skipFileName);
								int length = fin.available();
								byte[] buffer = new byte[length];
								fin.read(buffer);
								String skipfileData = EncodingUtils.getString(buffer, "UTF-8"); 
						        fin.close();
						        
						        skipArr = new JSONArray(skipfileData);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if(skipArr!=null){
						menuBtnAdapter.skipArr1 = new JSONArray();
						menuBtnAdapter.skipArr2 = new JSONArray();
						menuBtnAdapter.skipStrArr1 = new ArrayList<String>();
						menuBtnAdapter.skipStrArr2 = new ArrayList<String>();
						for(int skipn=0; skipn<skipArr.length(); skipn++){
							try {
								JSONObject skipJObj = (JSONObject) skipArr.get(skipn);
								if(skipJObj.getString("menuBtnSkipType").equals("1")){
									menuBtnAdapter.skipArr1.put(skipJObj);
									int index1 = menuBtnAdapter.skipArr1.length()-1;
									menuBtnAdapter.skipStrArr1.add(skipJObj.getString("menuBtnSkipName"));
								}else if(skipJObj.getString("menuBtnSkipType").equals("2")){
									menuBtnAdapter.skipArr2.put(skipJObj);
									int index2 = menuBtnAdapter.skipArr2.length()-1;
									menuBtnAdapter.skipStrArr2.add(skipJObj.getString("menuBtnSkipName"));
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					//页面数据
					if(jobj.has("data") || jobj.has("menuTitle")){
						String menuPageId = menuName+"_"+myapp.getUserNameId();
						try {
							JSONObject pageObj = jobj.has("data")?(JSONObject) jobj.get("data"):jobj;
							JSONArray pageImgArr = pageObj
									.getJSONArray("menuBgImgArr");
							for (int i = 0; i < pageImgArr.length(); i++) {
								String imgurl = (String) pageImgArr.get(i);
								if(imgurl.indexOf("http://")>-1){
									System.out.println("获取远程图片："+imgurl);
									Bitmap bitmap = null;
									while(bitmap==null){
										bitmap = returnBitMap(imgurl);
									}
									if(bitmap != null)
						            {
					        			if(!fileUtil.isFileExist2(menuPageId))
						            	{
						            		fileUtil.createUserFile(menuPageId);
						            	}
					        			String furl = fileUtil.getImageFile1aPath(menuPageId, "menuImg"+i);
										String furl2 = fileUtil.getImageFile1bPath(menuPageId, "menuImg"+i);
										fileUtil.saveMyBitmap(furl, bitmap);
										bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,
												true);
										fileUtil.saveMyBitmap(furl2, bitmap);
										Drawable drawable = new BitmapDrawable(bitmap);
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("imgid", drawable);
										map.put("imgname", imgurl);
										map.put("imgData", furl);
										
										pageImgArr.put(i, furl);

										pageImgList.add(0, map);
										pageImgAdapter.notifyDataSetChanged();
						            }
								}else{
									String furl = imgurl.replace("/1a/", "/1b/");
									Bitmap bitmap = myapp.getLoacalBitmap(furl);
									Drawable drawable = new BitmapDrawable(bitmap);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("imgid", drawable);
									map.put("imgname", imgurl);
									map.put("imgData", imgurl);
									
									pageImgList.add(0, map);
									pageImgAdapter.notifyDataSetChanged();
								}
								
							}

							JSONArray menuArr = pageObj.getJSONArray("menuBtnArr");
							menuBtnList = new ArrayList<Map<String, Object>>();
							for(int j=0; j<menuArr.length(); j++){
								JSONObject menuObj = (JSONObject) menuArr.get(j);
								Map<String, Object> menuMap = new HashMap<String, Object>();
								String menuIcon = (String)menuObj.get("menuBtnIcon");
								String menuBtnID = (String)menuObj.get("pkid");
								if(menuIcon.indexOf("null")>-1){
									menuMap.put("menuBtnIcon", null);
									menuMap.put("menuBtnIconUrl", null);
								}else{
									if(menuIcon.indexOf("http://")>-1){
										System.out.println("获取远程图片："+menuIcon);
										Bitmap bitmap = null;
										while(bitmap==null){
											bitmap = returnBitMap(menuIcon);
										}
										bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,true);
										if(bitmap != null)
							            {
						        			if(!fileUtil.isFileExist2(menuPageId))
							            	{
							            		fileUtil.createUserFile(menuPageId);
							            	}
						        			String furl = fileUtil.getImageFile1aPath(menuPageId, menuBtnID);
											fileUtil.saveMyBitmap(furl, bitmap);
											
											menuObj.put("menuBtnIcon", furl);

											Drawable drawable = new BitmapDrawable(bitmap);
											menuMap.put("menuBtnIcon", drawable);
											menuMap.put("menuBtnIconUrl", furl);
							            }
									}else{
										Bitmap bitmap = myapp.getLoacalBitmap(menuIcon);
										Drawable drawable = new BitmapDrawable(bitmap);
										menuMap.put("menuBtnIcon", drawable);
										menuMap.put("menuBtnIconUrl", menuIcon);
									}
								}
								menuMap.put("menuBtnName", menuObj.get("menuName"));
								menuMap.put("menuBtnType", menuObj.get("menuBtnType"));
								menuMap.put("menuBtnUrl", menuObj.get("menuBtnUrl"));
								menuMap.put("menuBtnSkipName", menuObj.get("menuBtnSkipName"));
								menuMap.put("menuBtnSkipContent", menuObj.get("menuBtnSkipContent"));
								menuMap.put("menuBtnSkipInfo", menuObj.get("menuBtnSkipInfo"));
								menuBtnList.add(menuMap);
							}
							
							System.out.println("本地数据："+pageObj.toString());
							String menufurl = fileUtil.getStoreImageFilePath(menuPageId, "data.txt");
							try {
								FileOutputStream fout = new FileOutputStream(menufurl);
								byte[] bytes = pageObj.toString().getBytes();
								fout.write(bytes);
								fout.close();
							} catch (Exception e) {
								e.printStackTrace();
							} 
							menuBtnAdapter.setList(menuBtnList);
							menuBtnGV.setAdapter(menuBtnAdapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				if (loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				if(msg.obj != null){
					JSONObject skipObj = (JSONObject) msg.obj;
					try {
						JSONArray skipArr = skipObj.getJSONArray("skip");
						for(int i=0; i<skipArr.length(); i++){
							JSONObject skipItem = (JSONObject) skipArr.get(i);
							Map<String, Object> skipMap = new HashMap<String, Object>();
							String pkid = (String) skipItem.get("pkid");
							skipMap.put("pkid", pkid);
							skipMap.put("menuBtnSkipName", skipItem.get("menuBtnSkipName"));
							skipMap.put("menuBtnSkipType", skipItem.get("menuBtnSkipType"));
							skipMap.put("menuBtnSkipContent", skipItem.get("menuBtnSkipContent"));
							skipMap.put("menuBtnSkipInfo", skipItem.get("menuBtnSkipInfo"));
							skipMap.put("menuBtnSkipBusinessID", skipItem.get("menuBtnSkipBusinessID"));
							skipMap.put("menuBtnSkipBusinessName", skipItem.get("menuBtnSkipBusinessName"));
							skipMap.put("menuBtnSkipIconId", skipItem.get("menuBtnSkipIconId"));
							
							boolean isHad = false;
							for(int j=0; j<menuBtnHadList.size(); j++){
								Map<String, Object> tempMap = menuBtnHadList.get(j);
								if(tempMap.get("menuBtnSkipID").equals(pkid)){
									isHad = true;
									break;
								}
							}
							if(!isHad){
								String skipIconStr = (String) skipItem.get("menuBtnSkipIcon");
								if(skipIconStr.indexOf("null")>-1){
									skipMap.put("menuBtnSkipIcon", null);
								}else{
									if(skipIconStr.indexOf("http://")>-1){
										Bitmap bitmap = null;
										while(bitmap==null){
											bitmap = returnBitMap(skipIconStr);
										}
										bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,true);
										if(bitmap != null)
							            {
											skipMap.put("menuBtnSkipIcon", bitmap);
							            }
									}
								}
								
								menuBtnListList.add(skipMap);
							}
						}
						
//						if(menuBtnListList.size()>1){
//							isBtnListNo = false;
//							menuBtnListList.remove(0);
//						}
						
						menuBtnListAdapter.notifyDataSetChanged();
						
						loadPageModeList();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 2:
				if(msg.obj != null){
					JSONObject jobj = (JSONObject) msg.obj;
					try {
						JSONObject pageObj = (JSONObject) jobj.get("data");
						if(!pageObj.has("pkid")){
							break;
						}
						menuPageID = pageObj.getString("pkid");
						menuPageStyle = pageObj.getString("menuPageStyle");
						menuPageStyleId = pageObj.getString("menuPageStyleId");
						JSONArray pageImgArr = pageObj.getJSONArray("menuBgImgArr");
						for (int i = 0; i < pageImgArr.length(); i++) {
							String imgurl = (String) pageImgArr.get(i);
							if(imgurl.indexOf("http://")>-1){
								Bitmap bitmap = null;
								while(bitmap==null){
									bitmap = returnBitMap(imgurl);
								}
								if(bitmap != null)
					            {
									if(pageImgH==0 || pageImgW==0){
										pageImgW = bitmap.getWidth();
										pageImgH = bitmap.getHeight();
										pageImgInfoTV2.setText("已有图片宽:"+pageImgW+" 高:"+pageImgH);
										System.out.println("页面图片宽："+pageImgW+"###高："+pageImgH);
									}
									bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,
											true);
									Drawable drawable = new BitmapDrawable(bitmap);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("imgid", drawable);
									map.put("imgname", imgurl);
									pageImgList.add(0, map);
									pageImgAdapter.notifyDataSetChanged();
					            }
							}
						}

						JSONArray menuArr = pageObj.getJSONArray("menuBtnArr");
						for(int j=0; j<menuArr.length(); j++){
							JSONObject menuObj = (JSONObject) menuArr.get(j);
							Map<String, Object> menuMap = new HashMap<String, Object>();
							menuMap.put("pkid", (String)menuObj.get("pkid"));
							menuMap.put("menuBtnSkipName", menuObj.get("menuName"));
							menuMap.put("menuBtnSkipID", menuObj.getString("menuBtnSkipID"));
							menuMap.put("menuBtnSkipInfo", menuObj.getString("menuBtnSkipInfo"));
							String menuIconStr = (String) menuObj.get("menuBtnIcon");
							if(menuIconStr.indexOf("null")>-1){
								menuMap.put("menuBtnSkipIcon", null);
							}else{
								if(menuIconStr.indexOf("http://")>-1){
									Bitmap bitmap = null;
									while(bitmap==null){
										bitmap = returnBitMap(menuIconStr);
									}
									bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,true);
									if(bitmap != null)
						            {
										menuMap.put("menuBtnSkipIcon", bitmap);
						            }
								}
							}
							menuBtnHadList.add(menuMap);
						}
						
//						if(menuBtnHadList.size()>1){
//							isBtnHadNo = false;
//							menuBtnHadList.remove(0);
//						}
						
						menuBtnHadAdapter.notifyDataSetChanged();
						
						loadMenuSkipList();
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 3:
				if(msg.obj != null){
					if (loadDialog != null)
						loadDialog.dismiss();
					
					JSONObject jobj = (JSONObject) msg.obj;
					try {
						if(jobj.get("msg").equals("success")){
							MenuLoadActivity.instance.refreshWebView(false);
							finish();
						}else{
							Toast.makeText(MenuSetActivity.this, "保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 4:
				if(msg.obj != null){
					if (loadDialog != null)
						loadDialog.dismiss();
					
					JSONObject jobj = (JSONObject) msg.obj;
					try {
						if(jobj.get("msg").equals("success")){
							JSONArray arr = jobj.getJSONArray("list");
							menuPageModeList = new ArrayList<Map<String,Object>>();
							for(int i=0; i<arr.length(); i++){
								JSONObject obj = (JSONObject) arr.get(i);
								Map<String, Object> map = new HashMap<String, Object>();
								String pkid = (String) obj.get("pkid");
								String modeIden = (String) obj.get("modeIden");
								map.put("pkid", pkid);
								map.put("modeIden", modeIden);
								if(modeIden.equals(menuPageStyle)){
									map.put("menuPageModeSel", "1");
									menuPageImgNum = (String) obj.get("modeImgNum");
									menuModeSpSelIndex = i;
								}else{
									map.put("menuPageModeSel", "0");
								}
								map.put("modeName", obj.get("modeName"));
								map.put("modeIden", obj.get("modeIden"));
								map.put("modeImgNum", obj.get("modeImgNum"));
								map.put("modeImg", obj.get("modeImg"));
								String modeImgStr = (String) obj.get("modeImg");
								if(modeImgStr.indexOf("null")>-1){
									map.put("modeImgData", null);
								}else{
									if(modeImgStr.indexOf("http://")>-1){
										Bitmap bitmap = null;
										while(bitmap==null){
											bitmap = returnBitMap(modeImgStr);
										}
										bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60,true);
										if(bitmap != null)
							            {
											map.put("modeImgData", bitmap);
							            }
									}
								}
								menuPageModeList.add(map);
							}
					        menuPageModeAdapter = new MenuPageModeAdapter(MenuSetActivity.this);
							menuPageModeAdapter.setList(menuPageModeList);
							menuPageModeSp.setAdapter(menuPageModeAdapter);
							menuPageModeSp.setSelection(menuModeSpSelIndex);
						}else{
							Toast.makeText(MenuSetActivity.this, "保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(loadDialog!=null)
						loadDialog.dismiss();
				}
				break;
			}
				
		}
	};
	
	public void updateMenuBtnShow(JSONObject jObj){
		try {
			String tag = jObj.getString("tag");
			String menuID = jObj.optString("menuID");
			String menuSkipID = jObj.getString("menuSkipID");
			
			Map<String, Object> menuMap = new HashMap<String, Object>();
			menuMap.put("menuBtnSkipName", "");
			menuMap.put("menuBtnSkipIcon", null);
			
			if(tag.equals("isHad")){//是从已有进入的，表示是从页面删除
				int delIndex = -1;
				Map<String, Object> delMap = new HashMap<String, Object>();
				for(int i=0; i<menuBtnHadList.size(); i++){
					Map<String, Object> map = menuBtnHadList.get(i);
					if(map.get("pkid").equals(menuID)){
						delIndex = i;
						delMap.put("pkid", menuSkipID);
						delMap.put("menuBtnSkipName", map.get("menuBtnSkipName"));
						delMap.put("menuBtnSkipInfo", map.get("menuBtnSkipInfo"));
						delMap.put("menuBtnSkipIcon", map.get("menuBtnSkipIcon"));
						break;
					}
				}
				if(delIndex != -1){
					menuBtnHadList.remove(delIndex);
//					if(menuBtnHadList.size() == 0){
//						isBtnHadNo = true;
//						menuBtnHadList.add(menuMap);
//					}
					menuBtnHadAdapter.notifyDataSetChanged();
					
					menuBtnListList.add(delMap);
//					if(isBtnListNo){
//						menuBtnListList.remove(0);
//						isBtnListNo = false;
//					}
					menuBtnListAdapter.notifyDataSetChanged();
				}
				
				
			}else if(tag.equals("isList")){//是从列表进入的，表示是从添加到页面
				int delIndex = -1;
				Map<String, Object> addMap = new HashMap<String, Object>();
				for(int i=0; i<menuBtnListList.size(); i++){
					Map<String, Object> map = menuBtnListList.get(i);
					if(map.get("pkid").equals(menuSkipID)){
						delIndex = i;
						addMap.put("pkid", "");
						addMap.put("menuBtnSkipName", map.get("menuBtnSkipName"));
						addMap.put("menuBtnSkipContent", map.get("menuBtnSkipContent"));
						addMap.put("menuBtnSkipInfo", map.get("menuBtnSkipInfo"));
						addMap.put("menuBtnSkipIcon", map.get("menuBtnSkipIcon"));
						addMap.put("menuBtnSkipIconId", map.get("menuBtnSkipIconId"));
						addMap.put("menuBtnSkipType", map.get("menuBtnSkipType"));
						addMap.put("menuBtnSkipID", menuSkipID);
						
						break;
					}
				}
				if(delIndex != -1){
					menuBtnListList.remove(delIndex);
//					if(menuBtnListList.size() == 0){
//						isBtnListNo = true;
//						menuBtnListList.add(menuMap);
//					}
					menuBtnListAdapter.notifyDataSetChanged();
					
					menuBtnHadList.add(addMap);
//					if(isBtnHadNo){
//						menuBtnHadList.remove(0);
//						isBtnHadNo = false;
//					}
					menuBtnHadAdapter.notifyDataSetChanged();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_take_photo:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				ContentValues values = new ContentValues(3);
				// values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
				values.put(MediaStore.Images.Media.DESCRIPTION,
						"this is description");
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
				uri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				// 这样就将文件的存储方式和uri指定到了Camera应用中

				// 由于我们需要调用完Camera后，可以返回Camera获取到的图片，
				// 所以，我们使用startActivityForResult来启动Camera
				startActivityForResult(intent, CAMERA_UPLOAD_WITH_DATA);
				break;
			case R.id.btn_pick_photo:
				Intent intent2 = new Intent();
				intent2.setType("image/*");
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent2, PHOTO_PICKED_UPLOAD_WITH_DATA);
				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_bottom_out);
				break;
			default:
				break;
			}
			
			if(menuWindow != null)
				menuWindow.dismiss();
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
			String fileName = "";
			while (cursor.moveToNext()) {
				Log.i("====_id", cursor.getString(0) + "");
				Log.i("====_path", cursor.getString(1) + "");
				fileUrl = cursor.getString(1);
				Log.i("====_size", cursor.getString(2) + "");
				imageSize = cursor.getString(2);
				Log.i("====_display_name", cursor.getString(3) + "");
				fileName = cursor.getString(3);
			}

			// 保存图片信息
			Bitmap bitmap = getLoacalBitmap(fileUrl);
			int bmW = selImgW;
			int bmH = selImgH;
			System.out.println("图库 选择图片宽："+bmW+";;高："+bmH);
			if(pageImgH==0 || pageImgW==0){
				pageImgH = bmH;
				pageImgW = bmW;
				pageImgInfoTV2.setText("已有图片宽:"+pageImgW+" 高:"+pageImgH);
			}else{
				if(bmW!=pageImgW || bmH!=pageImgH){
					Toast.makeText(MenuSetActivity.this, "选择的图片宽高不统一", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
			int degree = readPictureDegree(fileUrl);
			bitmap = rotaingImageView(degree, bitmap);
			bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
			Drawable drawable = new BitmapDrawable(bitmap);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("imgid", drawable);
			map.put("imgname", fileName);
			map.put("imgData", fileUrl);
			map.put("isNew", "yes");

			pageImgList.add(0, map);
			pageImgAdapter.notifyDataSetChanged();
			break;
		}
		case CAMERA_UPLOAD_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			if (uri != null) {
				ContentResolver cr = getContentResolver();
				Cursor cursor = cr.query(uri, null, null, null, null);
				String fileUrl = "";
				String fileName = "";
				while (cursor.moveToNext()) {
					fileUrl = cursor.getString(1);
					fileName = cursor.getString(3);
				}

				// 保存图片信息
				Bitmap bitmap = getLoacalBitmap(fileUrl);
				int bmW = selImgW;
				int bmH = selImgH;
				System.out.println("相机 选择图片宽："+bmW+";;高："+bmH);
				if(pageImgH==0 || pageImgW==0){
					pageImgH = bmH;
					pageImgW = bmW;
					pageImgInfoTV2.setText("已有图片宽:"+pageImgW+" 高:"+pageImgH);
				}else{
					if(bmW!=pageImgW || bmH!=pageImgH){
						Toast.makeText(MenuSetActivity.this, "选择的图片宽高不统一", Toast.LENGTH_SHORT).show();
						break;
					}
				}
				int degree = readPictureDegree(fileUrl);
				bitmap = rotaingImageView(degree, bitmap);
				bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
				Drawable drawable = new BitmapDrawable(bitmap);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("imgid", drawable);
				map.put("imgname", fileName);
				map.put("imgData", fileUrl);
				map.put("isNew", "yes");

				pageImgList.add(0, map);
				pageImgAdapter.notifyDataSetChanged();
			}
			break;
		}
		}
	}

	/**
	 * 转换本地图片为bitmap http://bbs.3gstdy.com
	 * 
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

			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, opts);
			selImgW = bitmap.getWidth();
			selImgH = bitmap.getHeight();
			bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
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
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		Bitmap resizedBitmap = null;
		try {
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			System.out.println("angle2=" + angle);
			// 创建新的图片
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resizedBitmap;
	}
	
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			System.out.println("ssssssssurl===="+url);
			if(url == null || url.equals(""))
				return null;
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
//			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	private void savePageDataToServer(){
		try {
			JSONObject jObj = new JSONObject();
			jObj.put("menuPageID", menuPageID);
			jObj.put("menuPageStyle", menuPageStyleId);//保存到后台数据库的是模板ID
			jObj.put("menuPageImgNum", menuPageImgNum);

			JSONArray delImgArr = new JSONArray();
			if(delPageImgArr != null){
				for (int n = 0; n < delPageImgArr.size(); n++) {
					JSONObject delImgObj = new JSONObject();
					System.out.println("删除的图片:" + delPageImgArr.get(n));
					delImgObj.put("imgUrl", delPageImgArr.get(n));
					delImgArr.put(delImgObj);
				}
			}
			jObj.put("delImg", delImgArr);

			JSONArray addImgArr = new JSONArray();
			Map<String, File> files = new HashMap<String, File>();
			for (int j = 0; j < pageImgList.size(); j++) {
				Map<String, Object> imgMap = pageImgList.get(j);
				String imgName = (String) imgMap.get("imgname");
				String imgUrl = (String) imgMap.get("imgData");
				String isNew = imgMap.containsKey("isNew") ? (String) imgMap
						.get("isNew") : "";
				System.out.println("图片是否新加：" + isNew);
				if (isNew.equals("yes")) {
					JSONObject addImgObj = new JSONObject();
					System.out.println("新加图片：" + imgName + "==" + imgUrl);
					addImgObj.put("imgUrl", imgUrl);
					addImgArr.put(addImgObj);
					
					File file = new File(imgUrl);
	 				files.put(file.getName(), file);
				}
				
				if(j==0){
					jObj.put("menuPageImgName", imgName);
				}
			}
			jObj.put("addImg", addImgArr);

			JSONArray btnArr = new JSONArray();
			for (int i = 0; i < menuBtnHadList.size(); i++) {
				JSONObject btnObj = new JSONObject();
				Map<String, Object> map = menuBtnHadList.get(i);
				String hadPkid = (String) map.get("pkid");
				String hadSkipId = (String) map.get("menuBtnSkipID");
				System.out.println("页面功能按钮：" + map.get("menuBtnSkipName"));
				btnObj.put("pkId", hadPkid);
				btnObj.put("skipId", hadSkipId);
				if(hadPkid.equals("")){
					btnObj.put("menuBtnSkipName", map.get("menuBtnSkipName"));
					btnObj.put("menuBtnSkipContent", map.get("menuBtnSkipContent"));
					btnObj.put("menuBtnSkipInfo", map.get("menuBtnSkipInfo"));
					btnObj.put("menuBtnSkipIconId", map.get("menuBtnSkipIconId"));
					btnObj.put("menuBtnSkipType", "2");
				}
				
				btnArr.put(btnObj);
			}
			jObj.put("menuBtn", btnArr);
			
			System.out.println("保存数据："+jObj.toString());
			
			final JSONObject dataObj = jObj;
			final Map<String, File> imgFiles = files;
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 3;

					try {
						JSONObject jobj = api.saveMenuPageData(menuName, dataObj, imgFiles);
						if (jobj != null) {
							msg.obj = jobj;
						} else {
							msg.obj = null;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void savePageHtml(){
		String pageHtmlName = "/mnt/sdcard/metashpere/menu_"+myapp.getUserNameId()+".htm";
		String pageHtml = "";
		
		try {
			InputStream in = this.getAssets().open("menu4.htm");
			pageHtml = InputStreamTOString(in);
			//页面主图
			String imgAllHtmStr = "";
			String imgAllCHtmStr = "";
			for(int imgc=0; imgc<pageImgList.size(); imgc++){
				Map<String, Object> imgObj = pageImgList.get(imgc);
				if(imgObj.containsKey("imgData")){
					String imgHtmStr = "<li><p>图片</p><img src=\""+imgObj.get("imgData")+"\"></li>";
					String imgCHtmStr = "<li class=\""+(imgc==0?"active":"")+"\">"+imgc+"</li>";
					imgAllHtmStr = imgAllHtmStr + imgHtmStr;
					imgAllCHtmStr = imgAllCHtmStr + imgCHtmStr;
				}
			}
			if(!imgAllHtmStr.equals(""))
				pageHtml = pageHtml.replace("${pageImgLi}", imgAllHtmStr);
			if(!imgAllCHtmStr.equals(""))
				pageHtml = pageHtml.replace("${pageImgCLi}", imgAllCHtmStr);
			
			//页面按钮1
			String menuBtn1HtmStr = "";
			if(menuBtnList.size()>0){
				Map<String, Object> menuMap1 = menuBtnList.get(0);
				if(menuMap1.get("menuBtnType").equals("1")){
					menuBtn1HtmStr = menuBtn1HtmStr + "<a href=\""+menuMap1.get("menuBtnUrl")+"\">";
				}else if(menuMap1.get("menuBtnType").equals("2")){
					menuBtn1HtmStr = menuBtn1HtmStr + "<a href=\""+menuMap1.get("menuBtnSkipContent")+"\">";
				}else if(menuMap1.get("menuBtnType").equals("3")){
					menuBtn1HtmStr = menuBtn1HtmStr + "<a onclick=\"skipActivity('"+menuMap1.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap1.get("menuBtnIconUrl")==null){
					menuBtn1HtmStr = menuBtn1HtmStr + "<div class=\"menuimg\" style=\"line-height:105px\">";
				}else{
					menuBtn1HtmStr = menuBtn1HtmStr + "<div class=\"menuimg\">";
					menuBtn1HtmStr = menuBtn1HtmStr + "<img src=\""+menuMap1.get("menuBtnIconUrl")+"\" class=\"img80\">";
				}
				menuBtn1HtmStr = menuBtn1HtmStr + "<p class=\"ptext\">"+menuMap1.get("menuBtnName")+"</p></div>";
				if(menuMap1.containsKey("menuBtnType")){
					menuBtn1HtmStr = menuBtn1HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn1}", menuBtn1HtmStr);
			
			//页面按钮2
			String menuBtn2HtmStr = "";
			if(menuBtnList.size()>1){
				Map<String, Object> menuMap2 = menuBtnList.get(1);
				if(menuMap2.get("menuBtnType").equals("1")){
					menuBtn2HtmStr = menuBtn2HtmStr + "<a href=\""+menuMap2.get("menuBtnUrl")+"\">";
				}else if(menuMap2.get("menuBtnType").equals("2")){
					menuBtn2HtmStr = menuBtn2HtmStr + "<a href=\""+menuMap2.get("menuBtnSkipContent")+"\">";
				}else if(menuMap2.get("menuBtnType").equals("3")){
					menuBtn2HtmStr = menuBtn2HtmStr + "<a onclick=\"skipActivity('"+menuMap2.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap2.get("menuBtnIconUrl")==null){
					menuBtn2HtmStr = menuBtn2HtmStr + "<div class=\"menuimg\" style=\"line-height:50px\">";
				}else{
					menuBtn2HtmStr = menuBtn2HtmStr + "<div class=\"menuimg\">";
					menuBtn2HtmStr = menuBtn2HtmStr + "<img src=\""+menuMap2.get("menuBtnIconUrl")+"\" class=\"img25\">";
				}
				menuBtn2HtmStr = menuBtn2HtmStr + "<p class=\"ptext\">"+menuMap2.get("menuBtnName")+"</p></div>";
				if(menuMap2.containsKey("menuBtnType")){
					menuBtn2HtmStr = menuBtn2HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn2}", menuBtn2HtmStr);
			
			//页面按钮3
			String menuBtn3HtmStr = "";
			if(menuBtnList.size()>2){
				Map<String, Object> menuMap3 = menuBtnList.get(2);
				if(menuMap3.get("menuBtnType").equals("1")){
					menuBtn3HtmStr = menuBtn3HtmStr + "<a href=\""+menuMap3.get("menuBtnUrl")+"\">";
				}else if(menuMap3.get("menuBtnType").equals("2")){
					menuBtn3HtmStr = menuBtn3HtmStr + "<a href=\""+menuMap3.get("menuBtnSkipContent")+"\">";
				}else if(menuMap3.get("menuBtnType").equals("3")){
					menuBtn3HtmStr = menuBtn3HtmStr + "<a onclick=\"skipActivity('"+menuMap3.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap3.get("menuBtnIconUrl")==null){
					menuBtn3HtmStr = menuBtn3HtmStr + "<div class=\"menuimg\" style=\"line-height:50px\">";
				}else{
					menuBtn3HtmStr = menuBtn3HtmStr + "<div class=\"menuimg\">";
					menuBtn3HtmStr = menuBtn3HtmStr + "<img src=\""+menuMap3.get("menuBtnIconUrl")+"\" class=\"img25\">";
				}
				menuBtn3HtmStr = menuBtn3HtmStr + "<p class=\"ptext\">"+menuMap3.get("menuBtnName")+"</p></div>";
				if(menuMap3.containsKey("menuBtnType")){
					menuBtn3HtmStr = menuBtn3HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn3}", menuBtn3HtmStr);
			
			//页面按钮4
			String menuBtn4HtmStr = "";
			if(menuBtnList.size()>3){
				Map<String, Object> menuMap4 = menuBtnList.get(3);
				if(menuMap4.get("menuBtnType").equals("1")){
					menuBtn4HtmStr = menuBtn4HtmStr + "<a href=\""+menuMap4.get("menuBtnUrl")+"\">";
				}else if(menuMap4.get("menuBtnType").equals("2")){
					menuBtn4HtmStr = menuBtn4HtmStr + "<a href=\""+menuMap4.get("menuBtnSkipContent")+"\">";
				}else if(menuMap4.get("menuBtnType").equals("3")){
					menuBtn4HtmStr = menuBtn4HtmStr + "<a onclick=\"skipActivity('"+menuMap4.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap4.get("menuBtnIconUrl")==null){
					menuBtn4HtmStr = menuBtn4HtmStr + "<div class=\"menuimg\" style=\"line-height:50px\">";
				}else{
					menuBtn4HtmStr = menuBtn4HtmStr + "<div class=\"menuimg\">";
					menuBtn4HtmStr = menuBtn4HtmStr + "<img src=\""+menuMap4.get("menuBtnIconUrl")+"\" class=\"img25\">";
				}
				menuBtn4HtmStr = menuBtn4HtmStr + "<p class=\"ptext\">"+menuMap4.get("menuBtnName")+"</p></div>";
				if(menuMap4.containsKey("menuBtnType")){
					menuBtn4HtmStr = menuBtn4HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn4}", menuBtn4HtmStr);
			
			//页面按钮5
			String menuBtn5HtmStr = "";
			if(menuBtnList.size()>4){
				Map<String, Object> menuMap5 = menuBtnList.get(4);
				if(menuMap5.get("menuBtnType").equals("1")){
					menuBtn5HtmStr = menuBtn5HtmStr + "<a href=\""+menuMap5.get("menuBtnUrl")+"\">";
				}else if(menuMap5.get("menuBtnType").equals("2")){
					menuBtn5HtmStr = menuBtn5HtmStr + "<a href=\""+menuMap5.get("menuBtnSkipContent")+"\">";
				}else if(menuMap5.get("menuBtnType").equals("3")){
					menuBtn5HtmStr = menuBtn5HtmStr + "<a onclick=\"skipActivity('"+menuMap5.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap5.get("menuBtnIconUrl")==null){
					menuBtn5HtmStr = menuBtn5HtmStr + "<div class=\"menuimg\" style=\"line-height:50px\">";
				}else{
					menuBtn5HtmStr = menuBtn5HtmStr + "<div class=\"menuimg\">";
					menuBtn5HtmStr = menuBtn5HtmStr + "<img src=\""+menuMap5.get("menuBtnIconUrl")+"\" class=\"img25\">";
				}
				menuBtn5HtmStr = menuBtn5HtmStr + "<p class=\"ptext\">"+menuMap5.get("menuBtnName")+"</p></div>";
				if(menuMap5.containsKey("menuBtnType")){
					menuBtn5HtmStr = menuBtn5HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn5}", menuBtn5HtmStr);
			
			//页面按钮6
			String menuBtn6HtmStr = "";
			if(menuBtnList.size()>5){
				Map<String, Object> menuMap6 = menuBtnList.get(5);
				if(menuMap6.get("menuBtnType").equals("1")){
					menuBtn6HtmStr = menuBtn6HtmStr + "<a href=\""+menuMap6.get("menuBtnUrl")+"\">";
				}else if(menuMap6.get("menuBtnType").equals("2")){
					menuBtn6HtmStr = menuBtn6HtmStr + "<a href=\""+menuMap6.get("menuBtnSkipContent")+"\">";
				}else if(menuMap6.get("menuBtnType").equals("3")){
					menuBtn6HtmStr = menuBtn6HtmStr + "<a onclick=\"skipActivity('"+menuMap6.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap6.get("menuBtnIconUrl")==null){
					menuBtn6HtmStr = menuBtn6HtmStr + "<div class=\"menuimg\" style=\"line-height:50px\">";
					menuBtn6HtmStr = menuBtn6HtmStr + "<p class=\"ptext\">"+menuMap6.get("menuBtnName")+"</p>";
				}else{
					menuBtn6HtmStr = menuBtn6HtmStr + "<div class=\"menuimg\">";
					menuBtn6HtmStr = menuBtn6HtmStr + "<img src=\""+menuMap6.get("menuBtnIconUrl")+"\" class=\"img30\" style=\"position:relative; margin-top:10px; margin-right:60px;\">";
					menuBtn6HtmStr = menuBtn6HtmStr + "<p class=\"ptext ptexth50\">"+menuMap6.get("menuBtnName")+"</p>";
				}
				menuBtn6HtmStr = menuBtn6HtmStr + "</div>";
				if(menuMap6.containsKey("menuBtnType")){
					menuBtn6HtmStr = menuBtn6HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn6}", menuBtn6HtmStr);
			
			//页面按钮7
			String menuBtn7HtmStr = "";
			if(menuBtnList.size()>6){
				Map<String, Object> menuMap7 = menuBtnList.get(6);
				if(menuMap7.get("menuBtnType").equals("1")){
					menuBtn7HtmStr = menuBtn7HtmStr + "<a href=\""+menuMap7.get("menuBtnUrl")+"\">";
				}else if(menuMap7.get("menuBtnType").equals("2")){
					menuBtn7HtmStr = menuBtn7HtmStr + "<a href=\""+menuMap7.get("menuBtnSkipContent")+"\">";
				}else if(menuMap7.get("menuBtnType").equals("3")){
					menuBtn7HtmStr = menuBtn7HtmStr + "<a onclick=\"skipActivity('"+menuMap7.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap7.get("menuBtnIconUrl")==null){
					menuBtn7HtmStr = menuBtn7HtmStr + "<div class=\"menuimg\" style=\"line-height:80px\">";
					menuBtn7HtmStr = menuBtn7HtmStr + "<p class=\"ptext\">"+menuMap7.get("menuBtnName")+"</p>";
				}else{
					menuBtn7HtmStr = menuBtn7HtmStr + "<div>";
					menuBtn7HtmStr = menuBtn7HtmStr + "<img src=\""+menuMap7.get("menuBtnIconUrl")+"\" class=\"img60\"  style=\"position:relative; margin-top:10px;\">";
					menuBtn7HtmStr = menuBtn7HtmStr + "<p class=\"ptext ptexth80\">"+menuMap7.get("menuBtnName")+"</p>";
				}
				menuBtn7HtmStr = menuBtn7HtmStr + "</div>";
				if(menuMap7.containsKey("menuBtnType")){
					menuBtn7HtmStr = menuBtn7HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn7}", menuBtn7HtmStr);
			
			//页面按钮8
			String menuBtn8HtmStr = "";
			if(menuBtnList.size()>7){
				Map<String, Object> menuMap8 = menuBtnList.get(7);
				if(menuMap8.get("menuBtnType").equals("1")){
					menuBtn8HtmStr = menuBtn8HtmStr + "<a href=\""+menuMap8.get("menuBtnUrl")+"\">";
				}else if(menuMap8.get("menuBtnType").equals("2")){
					menuBtn8HtmStr = menuBtn8HtmStr + "<a href=\""+menuMap8.get("menuBtnSkipContent")+"\">";
				}else if(menuMap8.get("menuBtnType").equals("3")){
					menuBtn8HtmStr = menuBtn8HtmStr + "<a onclick=\"skipActivity('"+menuMap8.get("menuBtnSkipContent")+"')\">";
				}
				if(menuMap8.get("menuBtnIconUrl")==null){
					menuBtn8HtmStr = menuBtn8HtmStr + "<div class=\"menuimg\" style=\"line-height:80px\">";
					menuBtn8HtmStr = menuBtn8HtmStr + "<p class=\"ptext\">"+menuMap8.get("menuBtnName")+"</p>";
				}else{
					menuBtn8HtmStr = menuBtn8HtmStr + "<div>";
					menuBtn8HtmStr = menuBtn8HtmStr + "<img src=\""+menuMap8.get("menuBtnIconUrl")+"\" class=\"img60\"  style=\"position:relative; margin-top:10px;\">";
					menuBtn8HtmStr = menuBtn8HtmStr + "<p class=\"ptext ptexth80\">"+menuMap8.get("menuBtnName")+"</p>";
				}
				menuBtn8HtmStr = menuBtn8HtmStr + "</div>";
				if(menuMap8.containsKey("menuBtnType")){
					menuBtn8HtmStr = menuBtn8HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn8}", menuBtn8HtmStr);
			
			//页面按钮9
			String menuBtn9HtmStr = "";
			if(menuBtnList.size()>8){
				Map<String, Object> menuMap9 = menuBtnList.get(8);
				if(menuMap9.get("menuBtnType").equals("1")){
					menuBtn9HtmStr = menuBtn9HtmStr + "<a href=\""+menuMap9.get("menuBtnUrl")+"\">";
				}else if(menuMap9.get("menuBtnType").equals("2")){
					menuBtn9HtmStr = menuBtn9HtmStr + "<a href=\""+menuMap9.get("menuBtnSkipContent")+"\">";
				}else if(menuMap9.get("menuBtnType").equals("3")){
					menuBtn9HtmStr = menuBtn9HtmStr + "<a onclick=\"skipActivity('"+menuMap9.get("menuBtnSkipContent")+"')\">";
				}
				menuBtn9HtmStr = menuBtn9HtmStr + "<div class=\"menuimg\" style=\"line-height:40px\">";
				menuBtn9HtmStr = menuBtn9HtmStr + "<p class=\"ptext\">"+menuMap9.get("menuBtnName")+"</p>";
				menuBtn9HtmStr = menuBtn9HtmStr + "</div>";
				if(menuMap9.containsKey("menuBtnType")){
					menuBtn9HtmStr = menuBtn9HtmStr + "</a>";
				}
			}
			pageHtml = pageHtml.replace("${menuBtn9}", menuBtn9HtmStr);
			
			//写js css
			File f1 = new File("/mnt/sdcard/metashpere/iscroll.css");
			if(!f1.exists()){
				InputStream fosfrom1 = this.getAssets().open("iscroll.css");
				OutputStream fosto1 = new FileOutputStream("/mnt/sdcard/metashpere/iscroll.css");
				byte bt1[] = new byte[1024];
				int c1;
				while ((c1 = fosfrom1.read(bt1)) > 0)
				{
				fosto1.write(bt1, 0, c1);
				}
				fosfrom1.close();
				fosto1.close();
			}
			
			File f2 = new File("/mnt/sdcard/metashpere/iscroll.js");
			if(!f2.exists()){
				InputStream fosfrom2 = this.getAssets().open("iscroll.js");
				OutputStream fosto2 = new FileOutputStream("/mnt/sdcard/metashpere/iscroll.js");
				byte bt2[] = new byte[1024];
				int c2;
				while ((c2 = fosfrom2.read(bt2)) > 0)
				{
				fosto2.write(bt2, 0, c2);
				}
				fosfrom2.close();
				fosto2.close();
			}
			
			File f3 = new File("/mnt/sdcard/metashpere/menu4.css");
			if(!f3.exists()){
				InputStream fosfrom3 = this.getAssets().open("menu4.css");
				OutputStream fosto3 = new FileOutputStream("/mnt/sdcard/metashpere/menu4.css");
				byte bt3[] = new byte[1024];
				int c3;
				while ((c3 = fosfrom3.read(bt3)) > 0)
				{
				fosto3.write(bt3, 0, c3);
				}
				fosfrom3.close();
				fosto3.close();
			}
			
			//写htm
			FileOutputStream fout = new FileOutputStream(pageHtmlName);
			byte[] bytes = pageHtml.getBytes();
			fout.write(bytes);
			fout.close();
			
			Toast.makeText(MenuSetActivity.this, "保存页面配置成功！", Toast.LENGTH_SHORT).show();
			if (loadDialog != null)
				loadDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
			
			Toast.makeText(MenuSetActivity.this, "保存失败，请稍后重试...", Toast.LENGTH_SHORT).show();
			if (loadDialog != null)
				loadDialog.dismiss();
		} 
	}
	
	public static String InputStreamTOString(InputStream in) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[1024];  
        int count = -1;  
        while((count = in.read(data,0,1024)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return new String(outStream.toByteArray(),"ISO-8859-1");  
    } 
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
}
