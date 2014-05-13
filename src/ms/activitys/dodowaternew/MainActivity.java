package ms.activitys.dodowaternew;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.LoginMain;
import ms.activitys.MainTabActivity;
import ms.activitys.RegisterAction;
import ms.activitys.dodowaternew.LazyScrollView.OnScrollListener;
import ms.activitys.product.ProductDetail;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private AssetManager asset_manager;
	private List<Map<String,Object>> image_filenames;
	private final String image_path = "images";
	private Handler handler;
	private int item_width;

	private int column_count = 3;// 显示列数
	private int page_count = 15;// 每次加载30张图片

	private int current_page = 0;// 当前页数

	private int[] topIndex;
	private int[] bottomIndex;
	private int[] lineIndex;
	private int[] column_height;// 每列的高度

	private HashMap<Integer, String> pins;

	private int loaded_count = 0;// 已加载数量

	private HashMap<Integer, Integer>[] pin_mark = null;

	private Context context;

	private HashMap<Integer, FlowView> iviews;
	int scroll_height;
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private int page = 0;
	private ProgressDialog mypDialog;
	private List<Map<String,Object>> alllist = new ArrayList<Map<String,Object>>();
	private int heightPixels;
	private int widthPixels;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dodomain);

		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		display = this.getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
		asset_manager = this.getAssets();

		column_height = new int[column_count];
		context = this;
		iviews = new HashMap<Integer, FlowView>();
		pins = new HashMap<Integer, String>();
		pin_mark = new HashMap[column_count];

		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
		this.topIndex = new int[column_count];

		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(this, "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
		heightPixels = dm.heightPixels;
		widthPixels = dm.widthPixels;

		Button zhuche = (Button)findViewById(R.id.create_btn);
		zhuche.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				showRegisterPage();
				Intent intent = new Intent();
			    intent.setClass( MainActivity.this,RegisterAction.class);
			    Bundle bundle = new Bundle();
//				bundle.putString("role", "Cleaner");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			}
		});
		
		Button loginbtn = (Button) findViewById(R.id.login_btn);
		loginbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProgressDialog();
				boolean autoLogin = share.getBoolean("autologin", true);
				if(autoLogin)
				{
					String user = share.getString("user", "");
					String pwa = share.getString("pwa", "");
					if(user != null && !user.equals(""))
					{
						loadThreadDataLogin(user,pwa);
					}
					else
					{
						Intent intent=new Intent();
						intent.setClass(MainActivity.this,LoginMain.class);
						startActivity(intent);
						MainActivity.this.finish();
					}
				}
				else
				{
					Intent intent=new Intent();
					intent.setClass(MainActivity.this,LoginMain.class);
					startActivity(intent);
					MainActivity.this.finish();
				}
			}
		});

		InitLayout();

	}

	private void InitLayout() {
		showProgressDialog();
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);

		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {

			}

			@Override
			public void onBottom() {
				// 滚动到最低端
//				AddItemToContainer(++current_page, page_count);
				page++;
				loadThreadData();
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {

				// Log.d("MainActivity",
				// String.format("%d  %d  %d  %d", l, t, oldl, oldt));

				// Log.d("MainActivity", "range:" + range);
				// Log.d("MainActivity", "range-t:" + (range - t));
				scroll_height = waterfall_scroll.getMeasuredHeight();
				Log.d("MainActivity", "scroll_height:" + scroll_height);

				if (t > oldt) {// 向下滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后

						for (int k = 0; k < column_count; k++) {

							LinearLayout localLinearLayout = waterfall_items
									.get(k);

							if (pin_mark[k].get(Math.min(bottomIndex[k] + 1,
									lineIndex[k])) <= t + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度

								((FlowView) waterfall_items.get(k).getChildAt(
										Math.min(1 + bottomIndex[k],
												lineIndex[k]))).Reload();

								bottomIndex[k] = Math.min(1 + bottomIndex[k],
										lineIndex[k]);

							}
							Log.d("MainActivity",
									"headIndex:" + topIndex[k] + "  footIndex:"
											+ bottomIndex[k] + "  headHeight:"
											+ pin_mark[k].get(topIndex[k]));
							if (pin_mark[k].get(topIndex[k]) < t - 2
									* scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = topIndex[k];
								topIndex[k]++;
								((FlowView) localLinearLayout.getChildAt(i1))
										.recycle();
								Log.d("MainActivity", "recycle,k:" + k
										+ " headindex:" + topIndex[k]);

							}
						}

					}
				} else {// 向上滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后
						for (int k = 0; k < column_count; k++) {
							LinearLayout localLinearLayout = waterfall_items
									.get(k);
							if (pin_mark[k].get(bottomIndex[k]) > t + 3
									* scroll_height) {
								((FlowView) localLinearLayout
										.getChildAt(bottomIndex[k])).recycle();

								bottomIndex[k]--;
							}

							if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= t
									- 2 * scroll_height) {
								((FlowView) localLinearLayout.getChildAt(Math
										.max(-1 + topIndex[k], 0))).Reload();
								topIndex[k] = Math.max(topIndex[k] - 1, 0);
							}
						}
					}

				}

			}
		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		handler = new Handler() {

			@Override
			public void dispatchMessage(Message msg) {

				super.dispatchMessage(msg);
			}

			@Override
			public void handleMessage(Message msg) {

				// super.handleMessage(msg);

				switch (msg.what) {
				case 1:

					FlowView v = (FlowView) msg.obj;
					int w = msg.arg1;
					int h = msg.arg2;
					// Log.d("MainActivity",
					// String.format(
					// "获取实际View高度:%d,ID：%d,columnIndex:%d,rowIndex:%d,filename:%s",
					// v.getHeight(), v.getId(), v
					// .getColumnIndex(), v.getRowIndex(),
					// v.getFlowTag().getFileName()));
					String f = v.getFlowTag().getFileName();

					// 此处计算列值
					int columnIndex = GetMinValue(column_height);

					v.setColumnIndex(columnIndex);

					column_height[columnIndex] += h;

					pins.put(v.getId(), f);
					iviews.put(v.getId(), v);
					waterfall_items.get(columnIndex).addView(v);

					lineIndex[columnIndex]++;

					pin_mark[columnIndex].put(lineIndex[columnIndex],
							column_height[columnIndex]);
					bottomIndex[columnIndex] = lineIndex[columnIndex];
					break;
				}

			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};

		waterfall_items = new ArrayList<LinearLayout>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

		// 加载所有图片路径

		try {
//			image_filenames = Arrays.asList(asset_manager.list(image_path));
			loadThreadData();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 第一次加载
//		AddItemToContainer(current_page, page_count);
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int currentIndex = pageindex * pagecount;

		int imagecount = 10000;// image_filenames.size();
		for (int i = currentIndex; i < pagecount * (pageindex + 1)
				&& i < imagecount; i++) {
			loaded_count++;
			Random rand = new Random();
			int r = rand.nextInt(image_filenames.size());
			AddImage(image_filenames.get(r),
					(int) Math.ceil(loaded_count / (double) column_count),
					loaded_count);
		}

	}

	private void AddImage(final Map<String,Object> objmap, int rowIndex, int id) {

		FlowView item = new FlowView(context);
		// item.setColumnIndex(columnIndex);

		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setViewHandler(this.handler);
		
		item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myapp.setDodoMap(objmap);
				showProductDetail();
			}
		});
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowId(id);
		param.setAssetManager(asset_manager);
//		param.setFileName(image_path + "/" + filename);
		String filename = (String)objmap.get("imgurl");
		if(filename != null && !filename.equals(""))
		{
		    param.setUrlstr(filename);
		}
		else
		{
//			Resources res= getResources();  
//		    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.dodonotimg);
//		    param.setNotimg(bmp);
		}
		param.setItemWidth(item_width);

		item.setFlowTag(param);
		item.LoadImage();
		// waterfall_items.get(columnIndex).addView(item);

	}

	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {

			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}
	
	public void showProgressDialog(){
 		try{
 			mypDialog=new ProgressDialog(this);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(this.getString(R.string.map_lable_11));
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
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
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getMenuData("","","","",page);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getMenuList2(jArr);
//							listCount = list.size();
							msg.obj = list;
						}
					}
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler2.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadDataLogin(final String lname,final String paw)
	{
				
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.login(lname,paw);
					if(jobj != null)
					{
						String tag = (String)jobj.get("tag");
						if(tag.equals("Success"))
						{
							String sessionid = jobj.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj.getString("email");
							saveSharedPerferences("email",email);
							saveSharedPerferences("sessionid",sessionid);
							myapp.setUserName(lname);
							if(jobj.has("userimg"))
							{
								String userimg = jobj.getString("userimg");
								myapp.setUserimg(userimg);
							}
							
							makeText(MainActivity.this.getString(R.string.login_lable_3)+ lname +MainActivity.this.getString(R.string.login_lable_4));
							showMyCards();
						}
						else
						{
							makeText(MainActivity.this.getString(R.string.login_lable_6));
							Intent intent=new Intent();
							intent.setClass(MainActivity.this,LoginMain.class);
							startActivity(intent);
							MainActivity.this.finish();
						}
					}
					else
					{
						makeText(MainActivity.this.getString(R.string.login_lable_7));
						Intent intent=new Intent();
						intent.setClass(MainActivity.this,LoginMain.class);
						startActivity(intent);
						MainActivity.this.finish();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
	}
	
	public static List<Map<String,Object>> getMenuList2(JSONArray jArr){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pId"))
					pkid = (String) dobj.get("pId"); 
				
				String cname = ""; 
				if(dobj.has("pname"))
					cname = (String) dobj.get("pname");
				
				String imgurl = ""; 
				if(dobj.has("sysImg"))
					imgurl = (String) dobj.get("sysImg");
				
				String price = ""; 
				if(dobj.has("price"))
					price = (String) dobj.get("price");
				
				String fiveimg = ""; 
				if(dobj.has("score"))
					fiveimg = (String) dobj.get("score");
				
				String pDesc = ""; 
				if(dobj.has("score"))
					pDesc = (String) dobj.get("pDesc");
				
				String productType = ""; 
				if(dobj.has("productType"))
					productType = (String) dobj.get("productType");
				
				String oldPrices = "0.00"; 
				if(dobj.has("oldPrices"))
					oldPrices = (String) dobj.get("oldPrices");
				
				String productInfo = ""; 
				if(dobj.has("productInfo"))
					productInfo = (String) dobj.get("productInfo");
				
				String peoductDescp = ""; 
				if(dobj.has("peoductDescp"))
					peoductDescp = (String) dobj.get("peoductDescp");
				
				String five = "0"; 
				if(dobj.has("five"))
					five = (String) dobj.get("five");
				
				String four = "0"; 
				if(dobj.has("four"))
					four = (String) dobj.get("four");
				
				String three = "0"; 
				if(dobj.has("three"))
					three = (String) dobj.get("three");
				
				String two = "0"; 
				if(dobj.has("two"))
					two = (String) dobj.get("two");
				
				String one = "0"; 
				if(dobj.has("one"))
					one = (String) dobj.get("one");
				
				String timenew = "false"; 
				if(dobj.has("new"))
					timenew = (String) dobj.get("new");
				
				String special = "1"; 
				if(dobj.has("special"))
					special = (String) dobj.get("special");
				
				String psize = ""; 
				if(dobj.has("psize"))
					psize = (String) dobj.get("psize");
				
				String productsNo = ""; 
				if(dobj.has("productsNo"))
					productsNo = (String) dobj.get("productsNo");
					
				int paycount = 0; 
				if(dobj.has("paycount"))
					paycount = (Integer) dobj.get("paycount");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("imgurl", imgurl);
				map.put("cname", cname);
				map.put("price", price + " ￥");
				map.put("prices", price);
				map.put("pdesc", pDesc);
				map.put("productType", productType);
//				if(fiveimg.equals("1"))
//					map.put("fiveimg", R.drawable.five_star1);
//				else if(fiveimg.equals("2"))
//					map.put("fiveimg", R.drawable.five_star2);
//				else if(fiveimg.equals("3"))
//					map.put("fiveimg", R.drawable.five_star3);
//				else if(fiveimg.equals("4"))
//					map.put("fiveimg", R.drawable.five_star4);
//				else if(fiveimg.equals("5"))
//					map.put("fiveimg", R.drawable.five_star5);
				if(!fiveimg.equals(""))
					map.put("fiveimg", Float.valueOf(fiveimg));
				else
					map.put("fiveimg", Float.valueOf(3));
				map.put("oldPrices", oldPrices);
				map.put("productInfo", productInfo);
				map.put("peoductDescp", peoductDescp);
				DecimalFormat r=new DecimalFormat();  
				r.applyPattern("#0.00");//保留小数位数，不足会补零  
				double saving = Double.valueOf(oldPrices) - Double.valueOf(price);
				map.put("saving", r.format(saving));
				map.put("five", five);
				map.put("four", four);
				map.put("three", three);
				map.put("two", two);
				map.put("one", one);
				int zon = Integer.valueOf(five) + Integer.valueOf(four) + Integer.valueOf(three) + Integer.valueOf(two) + Integer.valueOf(one);
				map.put("total", String.valueOf(zon));
				if(timenew.equals("true"))
					map.put("timenew", R.drawable.isnew);
				else
					map.put("timenew", null);
				if(special.equals("0"))
					map.put("special", R.drawable.issale);
				else
					map.put("special", null);
				map.put("psize", psize);
				map.put("productsNo", productsNo);
				map.put("paycount", String.valueOf(paycount));
	
//				if(imgurl.indexOf("http:") >= 0)
//				{
//					String str = imgurl.substring(0,imgurl.lastIndexOf("."));
//					String str2 = imgurl.substring(imgurl.lastIndexOf("."),imgurl.length());
//					
//					imgurl = str+"_zhong"+str2;
//				}
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(mypDialog != null)
					mypDialog.dismiss();
				if(msg.obj != null)
				{
					image_filenames = (List<Map<String,Object>>)msg.obj;
					alllist.addAll(image_filenames);
					myapp.setProductList(alllist);
					AddItemToContainer(current_page, page_count);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
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
	
	public void showProductDetail()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductDetail.class);
		    Bundle bundle = new Bundle();
			bundle.putString("dodo", "");
			bundle.putInt("index", 0);
			bundle.putString("map", null);
			bundle.putString("advertiseNotification", null);
			bundle.putString("storeid",null);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
