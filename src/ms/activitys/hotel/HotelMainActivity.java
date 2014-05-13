package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.map.BaiduMap;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.HotelMainAdapter;
import ms.globalclass.map.MyApp;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;

public class HotelMainActivity extends Activity{

	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ListView mylist;
	private LinearLayout progLayout;
	private LinearLayout msg_layout;
	private List<Map<String, Object>> messageItem;
	
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	private View layout;	
	private LinearLayout mClose;
    private LinearLayout mCloseBtn;
    public static HotelMainActivity instance = null;
    private String userimg;
    private DisplayMetrics dm;
    private float density;
    private float densityDPI;
    private float xdpi;
    private float ydpi;
    private int screenWidth;
    private int screenHeight;
    private MyLoadingDialog loadDialog;
    private LinearLayout net_stus_layout;
    public LinearLayout user_img_layout;
    private static DBHelperMessage db;
    /**地图引擎管理类*/ 
    private BMapManager mBMapManager = null;  
    private String BASE_URL;
    public Map<String,Integer> newmessageNumber = new HashMap<String,Integer>();
    private int zonshu = 0;
    private static FileUtils fileUtil = new FileUtils();
 
	/**  
     * 经研究发现在申请KEY时：应用名称一定要写成my_app_应用名（也就是说"my_app_"是必须要有的）。  
     * 百度地图SDK提供的服务是免费的，接口无使用次数限制。您需先申请密钥（key)，才可使用该套SDK。  
     * */ 
    public static final String BAIDU_MAP_KEY = "BBFDD59585B8518AAEB2AE3E0799F6BDF5D303A1";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		try{
//		 // 注意：请在调用setContentView前初始化BMapManager对象，否则会报错  
//        mBMapManager = new BMapManager(this.getApplicationContext());  
//        mBMapManager.init(BAIDU_MAP_KEY, new MKGeneralListener() {  
// 
//            @Override 
//            public void onGetNetworkState(int iError) {  
//                if (iError == MKEvent.ERROR_NETWORK_CONNECT) {  
//                    Toast.makeText(HotelMainActivity.this.getApplicationContext(),  
//                            "您的网络出错啦！",   
//                            Toast.LENGTH_LONG).show();  
//                }  
//            }  
// 
//            @Override 
//            public void onGetPermissionState(int iError) {  
//                if (iError == MKEvent.ERROR_PERMISSION_DENIED) {  
//                    // 授权Key错误：  
//                    Toast.makeText(HotelMainActivity.this.getApplicationContext(),   
//                            "请在 DemoApplication.java文件输入正确的授权Key！",   
//                            Toast.LENGTH_LONG).show();  
//                    myapp.setM_bKeyRight(false);
//                }  
//            }  
//        }); 
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
        
		setContentView(R.layout.main_home_page);
		
		myapp = (MyApp)this.getApplicationContext();
		
//		myapp.setmBMapManager(mBMapManager);
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		String ip = this.share.getString("ipadrees", "121.199.8.186");
		myapp.setHost(ip);
		BASE_URL = "http://"+ip+":80/upload/";
		
		db = new DBHelperMessage(this, myapp);
//		db.openDB();
//		db.deleteMessageDataAll();
//		db.closeDB();
		
		instance = this;
		
		showMyLoadingDialog();
		
		dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
          
        density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
        densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）  
        xdpi = dm.xdpi;           
        ydpi = dm.ydpi;  
          
        int screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）  
        int screenHeightDip = dm.heightPixels;      // 屏幕宽（dip，如：533dip）  
          
        screenWidth  = (int)(dm.widthPixels * density + 0.5f);      // 屏幕宽（px，如：480px）  
        screenHeight = (int)(dm.heightPixels * density + 0.5f);     // 屏幕高（px，如：800px）  
        myapp.setScreenWidth(screenWidthDip);
        myapp.setScreenHeight(screenHeightDip);
        
        progLayout = (LinearLayout)findViewById(R.id.progLayout);
		msg_layout = (LinearLayout)findViewById(R.id.msg_layout);
		net_stus_layout = (LinearLayout)findViewById(R.id.net_stus_layout);
		
		mylist = (ListView) findViewById(R.id.listvid);
		
		user_img_layout = (LinearLayout)findViewById(R.id.user_img_layout);
        
//		userimg = myapp.getUserimg();
//        if(userimg != null && !userimg.equals(""))
//			loadUserImageThread();
        String userimgBase64 = share.getString("userimg", "");
        if(userimgBase64 != null && !userimgBase64.equals(""))
        {
		    // 对Base64格式的字符串进行解码
		    byte[] base64Bytes = Base64.decodeBase64(userimgBase64.getBytes());
		    Bitmap bitmap = BitmapFactory.decodeByteArray(base64Bytes,0,base64Bytes.length);
		    bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
		    myapp.setUserimgbitmap(bitmap);
        }
        else
        {
        	userimg = myapp.getUserimg();
        	if(userimg != null && !userimg.equals(""))
        		loadUserImageThread();
        }
		
		initView();
	}
	
	public void loadeListItemData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> list = null;
					db.openDB();
					Map<String,Object> dmap = db.getNewMessageData("0");
					db.closeDB();
					list = (List<Map<String,Object>>)dmap.get("dlist");
					if(dmap != null && dmap.containsKey("zhonshu"))
						zonshu = (Integer)dmap.get("zhonshu");
					
					msg.obj = list;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void initView()
	{
		try{
			
			if(myapp.getUserimgbitmap() == null)
				user_img_layout.setVisibility(View.VISIBLE);
			getMyCardListData(null);
			
			ImageButton addmsgbtn = (ImageButton)findViewById(R.id.add_message);
			addmsgbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openAddGroup();
				}
			});
			
			
//			ImageButton card_btn = (ImageButton)findViewById(R.id.card_btn);
//			card_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					openCardView();
//				}
//			});
			
			changNetLayout();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void changNetLayout()
	{
		try{
			if(isInetnState())
			{
				net_stus_layout.setVisibility(View.GONE);
			}
			else
			{
				net_stus_layout.setVisibility(View.VISIBLE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openCardView()
	{
		try{
			int index = 0;
			for(int i=0;i< myapp.getMyCardsAll().size();i++)
			{
				Map<String,Object> map = myapp.getMyCardsAll().get(i);
				String sid = (String)map.get("storeid");
				if(sid.equals(myapp.getAppstoreid()))
				{
					index = i;
					break;
				}
			}
			Intent intent = new Intent();
		    intent.setClass( this,HotelActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			bundle.putString("tag", "main");
			intent.putExtras(bundle);
			MainTabActivity.instance.loadLeftActivity(intent);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadAllCard()
	{
		try{
			mylist.setVisibility(View.GONE);
			msg_layout.setVisibility(View.GONE);
			progLayout.setVisibility(View.VISIBLE);
			
			getMyCardListData(null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadguanzhucard()
	{
		try{
			mylist.setVisibility(View.GONE);
			msg_layout.setVisibility(View.GONE);
			progLayout.setVisibility(View.VISIBLE);
			
			getMyCardListData3();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openAddCardWind()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( HotelMainActivity.this,MapPage.class);
		    intent.setClass( HotelMainActivity.this,BaiduMap.class);
		    Bundle bundle = new Bundle();
			bundle.putString("homepage", "1");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    mypDialog.dismiss();
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadScanning()
	{
		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,CaptureActivity.class);
//		    Bundle bundle = new Bundle();
////			bundle.putString("tag", tag);
//			intent.putExtras(bundle);
////		    startActivity(intent);//开始界面的跳转函数
////		    overridePendingTransition(R.anim.slide_left_out,R.anim.slide_right_in);
//			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getMyCardListData(final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> list = null;
					List<Map<String,Object>> storelist = null;
					db.openDB();
					Map<String,Object> dmap = db.getNewMessageData("0");
					storelist = db.getStoreInfoAllData("");
					list = (List<Map<String,Object>>)dmap.get("dlist");
					zonshu = (Integer)dmap.get("zhonshu");
//					storelist.add(0,new HashMap<String,Object>());
					myapp.setMyCardsAll(storelist);
//					if(myapp.isIszhuche())
//					{
					if(storelist == null || storelist.size() == 0)
					{
						JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null && jobj.has("data"))
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> lists = myapp.getMyCardList(jArr);
							db.saveStoreInfoAllData(lists);
							storelist = db.getStoreInfoAllData("");
//							storelist.add(0,new HashMap<String,Object>());
							myapp.setMyCardsAll(storelist);
							
						}
					}
					if(tag == null)
					{
						if(list == null || list.size() == 0)
						{
							JSONObject jobj = api.getMyMessageListData(myapp.getPfprofileId(),myapp.getAppstoreid(),isWifistart(),isInetnState(),0,"0");
							if(jobj != null && jobj.has("data"))
							{
								JSONArray jArr = (JSONArray) jobj.get("data");
//								List lists = getMessageDetialData(jArr);
								List lists = myapp.getMessageDetialData(jArr);
								db.saveMessageData(lists);
								dmap = db.getNewMessageData("0");
								list = (List<Map<String,Object>>)dmap.get("dlist");
								zonshu = (Integer)dmap.get("zhonshu");
							}
						}
					}
//					}
					db.closeDB();
					
					msg.obj = list;
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyCardListData3()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
						JSONObject jobj = api.getMyCardsAll("0");
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							Map<String,Object> dmap = getMyCardList(jArr);
							List<Map<String,Object>> list = (List<Map<String,Object>>)dmap.get("list");
							List<Map<String,Object>> list2 = (List<Map<String,Object>>)dmap.get("list2");
							msg.obj = list;
							myapp.setMyCardsAll(list2);
						}
						else
						{
							msg.obj = null;
						}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void delMessageAll(final String nameid,final String storeid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					db.openDB();
					db.deletetNewMessageData(storeid, nameid);
					db.closeDB();
					
					msg.obj = "1";
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
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
				List<Map<String, Object>> list3 = (List<Map<String, Object>>)msg.obj;
				if(list3 != null)
				{
					progLayout.setVisibility(View.GONE);
					mylist.setVisibility(View.VISIBLE);
					
					messageItem = list3;
					// 生成适配器的Item和动态数组对应的元素
					HotelMainAdapter simperAdapter = new HotelMainAdapter(HotelMainActivity.this, messageItem,
							R.layout.hotel_main_group_list_item,R.layout.my_card_list_item, new String[] { "imgurl", "storeName","lastmessagetime","lastmessage","newNumber","servicename","watag" },
							new int[] { R.id.head, R.id.name_txt,R.id.time_txt,R.id.doc_txt,R.id.new_number,R.id.info_txt,R.id.weixin_icon },myapp);
		
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							String nameid = messageItem.get(arg2).get("nameid").toString();
							String storeid = messageItem.get(arg2).get("storeid").toString();
							openMuneView(nameid,storeid);
							return true;
						}
					});
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							String imgurl = (String)messageItem.get(arg2).get("imgurl");
							String typesMapping = messageItem.get(arg2).get("typesMapping").toString();
							Bitmap imgbitmap = null;
							if(imgurl != null && !imgurl.equals("") && !typesMapping.equals("group"))
							{
								imgbitmap = getLoacalBitmap(imgurl);
							}
							String nameid = messageItem.get(arg2).get("nameid").toString();
							String storeid = messageItem.get(arg2).get("storeid").toString();
							String storeName = messageItem.get(arg2).get("storeName").toString();
							String serviceid = messageItem.get(arg2).get("serviceid").toString();
							String servicename = messageItem.get(arg2).get("servicename").toString();
							String watag = messageItem.get(arg2).get("watag").toString();
							String username = myapp.getUserName();
							fileUtil.createUserFile(storeid);
							openMessageDetail(nameid,storeid,storeName,username,typesMapping,imgbitmap,serviceid,servicename,watag,imgurl);
						}
					});
					
					MainTabActivity.instance.setNewNumberValue(zonshu);
				}
				else
				{
					mylist.setAdapter(null);
					mylist.setVisibility(View.GONE);
					progLayout.setVisibility(View.GONE);
					msg_layout.setVisibility(View.VISIBLE);
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				String tag = (String)msg.obj;
				if(tag != null)
				{
					makeText(getString(R.string.setting_success_clear));
					getMyCardListData("1");
				}
				else
					makeText(getString(R.string.hotel_label_18));
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
				{
					user_img_layout.setVisibility(View.GONE);
				}
				else
				{
					user_img_layout.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	};
	
	public void openMuneView(final String nameid,final String storeid)
	{
		final String [] items = new String[]{getString(R.string.hotel_label_22)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == 0)
				{
					showMyLoadingDialog();
					delMessageAll(nameid,storeid);
					dialog.dismiss();
				}
			}
		});
		
		alertDialog = builder.create();
		alertDialog.setTitle(getString(R.string.app_name));
		alertDialog.show();
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	
	public static Map<String,Object> getMyCardList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		Map<String,Object> dmap = new HashMap<String,Object>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				Integer points = 0; // 当前积分
				if(dobj.has("points"))
					points = (Integer) dobj.get("points"); 
				
				String nameOnCard = ""; // 卡上的名字
				if(dobj.has("nameOnCard"))
					nameOnCard = (String) dobj.get("nameOnCard");
				
				String nameid = ""; // 
				if(dobj.has("nameid"))
					nameid = (String) dobj.get("nameid"); 
				
				String pfids = ""; // 
				if(dobj.has("pfids"))
					pfids = (String) dobj.get("pfids"); 
				
				String cardNo = ""; // 会员卡编号
				if(dobj.has("cardNo"))
					cardNo = (String) dobj.get("cardNo"); 
				
				String joinedDate = ""; // 加入日期
				if(dobj.has("joinedDate"))
				{
					joinedDate = (String) dobj.get("joinedDate"); 
					String str[] = joinedDate.split(" ");
					if(str.length>1)
						joinedDate = str[0];
				}
				
				String mdmType = ""; // 会员类型
				if(dobj.has("mdmType"))
					mdmType = (String) dobj.get("mdmType"); 
				
				String mdmLevel = ""; // 会员等级
				if(dobj.has("mdmLevel"))
					mdmLevel = (String) dobj.get("mdmLevel"); 
				
				String mdmstatus = ""; // 会员状态
				if(dobj.has("mdmstatus"))
					mdmstatus = (String) dobj.get("mdmstatus"); 
				
				String expDate = ""; // 失效日期
				if(dobj.has("expDate"))
					expDate = (String) dobj.get("expDate"); 
				
				String chainCode = ""; // 条形码号或硬卡号
				if(dobj.has("chainCode"))
					chainCode = (String) dobj.get("chainCode"); 
				
				String storeid = ""; // 门店id
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
				String storeName = ""; // 门店名字
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String img = ""; // 门店会员卡图片
				if(dobj.has("img"))
				{
					img = (String) dobj.get("img");
					img = img.replaceAll("mill.ms.cn", "223.4.115.110");
				}
				
				String isASttention = ""; 
				if(dobj.has("isASttention"))
					isASttention = (String) dobj.get("isASttention"); 
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String couponNumber = ""; 
				if(dobj.has("couponNumber"))
					couponNumber = (String) dobj.get("couponNumber"); 
				
				String storePhone = ""; 
				if(dobj.has("storePhone"))
					storePhone = (String) dobj.get("storePhone"); 
				
				String addressInfomation = ""; 
				if(dobj.has("addressInfomation"))
					addressInfomation = (String) dobj.get("addressInfomation"); 
				
				String storeDesc = ""; 
				if(dobj.has("storeDesc"))
					storeDesc = (String) dobj.get("storeDesc"); 
				
				String typeName = "";  //酒店类型
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				String storeType = "";  //酒店类型
				if(dobj.has("storeType"))
					storeType = (String) dobj.get("storeType"); 
				
				String typesMapping = "";  //酒店类型与客户端得映射
				if(dobj.has("typesMapping"))
					typesMapping = (String) dobj.get("typesMapping"); 
				
				String businessId = ""; 
				if(dobj.has("businessId"))
					businessId = (String) dobj.get("businessId"); 
				
				String woof = ""; 
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof"); 
				
				String longItude = ""; 
				if(dobj.has("longItude"))
					longItude = (String) dobj.get("longItude"); 
				
				String userimg = ""; 
				if(dobj.has("userimg"))
					userimg = (String) dobj.get("userimg"); 
				
				String isLu = ""; 
				if(dobj.has("isLu"))
					isLu = (String) dobj.get("isLu"); 
				
				String province = ""; 
					if(dobj.has("province"))
						province = (String) dobj.get("province");
					
				String roomIntroduction = ""; 
					if(dobj.has("roomIntroduction"))
						roomIntroduction = (String) dobj.get("roomIntroduction");
					
				String periphery = ""; 
					if(dobj.has("periphery"))
						periphery = (String) dobj.get("periphery");
					
				String trafficWay = ""; 
					if(dobj.has("trafficWay"))
						trafficWay = (String) dobj.get("trafficWay");
						
				String startingPrice = ""; 
					if(dobj.has("startingPrice"))
						startingPrice = (String) dobj.get("startingPrice");
					
				String score = ""; 
					if(dobj.has("score"))
						score = (String) dobj.get("score");
						
				String comments = ""; 
					if(dobj.has("comments"))
						comments = (String) dobj.get("comments");
					
				String username = ""; 
					if(dobj.has("username"))
						username = (String) dobj.get("username");
					
				String password = ""; 
					if(dobj.has("password"))
						password = (String) dobj.get("password");
					
				String lastmessage = ""; 
					if(dobj.has("lastmessage"))
						lastmessage = (String) dobj.get("lastmessage");
					
				String lastmessagetime = ""; 
					if(dobj.has("lastmessagetime"))
					{
						lastmessagetime = (String) dobj.get("lastmessagetime");
						if(lastmessagetime != null && !lastmessagetime.equals(""))
						{
							String str[] = lastmessagetime.split(" ");
							SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
							String datastr1 = formatter.format(new Date());
							if(str.length>1)
							{
								String datastr = str[0];
								if(datastr.equals(datastr1))
								{
									lastmessagetime = str[1].substring(0,5);
								}
								else
								{
									lastmessagetime = str[0].substring(5,str[0].length());
								}
							}
						}
					}
				
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("points", points);
					map.put("nameOnCard", nameOnCard);
					map.put("nameid", nameid);
					map.put("pfids", pfids);
					map.put("cardNo", cardNo);
					map.put("joinedDate", joinedDate);
					map.put("mdmType", mdmType);
					map.put("mdmLevel", mdmLevel);
					map.put("mdmstatus", mdmstatus);
					map.put("expDate", expDate);
					map.put("chainCode", chainCode);
					map.put("storeid", storeid);
					map.put("storeName", storeName);
					map.put("username", username);
					map.put("password", password);
	//				boolean loadimgTag = share.getBoolean("webimage", true);
	//				if(loadimgTag)
	//				{
	//					map.put("img", getImageDrawable(img));
	//					map.put("img2", getImageBitmap(img));
	//				}
	//				else
	//				{
	//					map.put("img", null);
	//					map.put("img2", BitmapFactory.decodeResource(this.getResources(), R.drawable.local_card_img));
	//				}
					map.put("imgurl", img);
					map.put("pkid", pkid);
					map.put("storePhone", storePhone);
					map.put("addressInfomation", addressInfomation);
					map.put("storeDesc", storeDesc);
					map.put("isASttention", isASttention);
					if(isASttention.equals("0"))
						map.put("xinxin", R.drawable.ic_star_small);
					else
						map.put("xinxin", null);
					map.put("couponNumber", couponNumber);
					map.put("typeName", typeName);
					map.put("typesMapping", typesMapping);
					map.put("businessId", businessId);
					map.put("woof", woof);
					map.put("longItude", longItude);
					map.put("userimg", userimg);
					map.put("isLu", isLu);
					map.put("storeType", storeType);
					map.put("province", province);
					map.put("roomIntroduction", roomIntroduction);
					map.put("periphery", periphery);
					map.put("trafficWay", trafficWay);
					map.put("startingPrice", startingPrice);
					map.put("score", score);
					map.put("comments", comments);
					map.put("lastmessage", lastmessage);
					map.put("lastmessagetime", lastmessagetime);
					
				if(lastmessage != null && !lastmessage.equals(""))
				{
					list.add(map);
					db.openDB();
					db.saveMessageData(list);
					db.closeDB();
					break;
				}
				list2.add(map);
			}
			dmap.put("list", list);
			dmap.put("list2", list2);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dmap;
	}
	
//	public List<Map<String,Object>> getMessageDetialData(JSONArray jArr)
//	{
//		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
//		try{
////			for (int i = jArr.length()-1; i >= 0; i--) {
//			for (int i = 0; i < jArr.length(); i++) {
//				JSONObject dobj = (JSONObject) jArr.get(i);
//				
//				String mid = ""; 
//				if(dobj.has("mid"))
//					mid = (String) dobj.get("mid");
//				
//				String toid = ""; 
//				if(dobj.has("toid"))
//					toid = (String) dobj.get("toid");
//				
//				String sender = ""; 
//				if(dobj.has("sender"))
//					sender = (String) dobj.get("sender");
//				
//				String fname = ""; 
//				if(dobj.has("fname"))
//					fname = (String) dobj.get("fname");
//				
//				String tname = ""; 
//				if(dobj.has("tname"))
//					tname = (String) dobj.get("tname");
//				
//				String reciver = ""; 
//				if(dobj.has("reciver"))
//					reciver = (String) dobj.get("reciver");
//				
//				String content = ""; 
//				if(dobj.has("content"))
//				{
//					content = (String) dobj.get("content");
////					if(content.equals("【图片】") || content.equals("【语音】"))
////						content = "";
//				}
//				
//				String sendTime = ""; 
//				if(dobj.has("sendTime"))
//				{
//					sendTime = (String)dobj.get("sendTime");
//				}
//				
//				String time = "1"; 
//				if(dobj.has("time"))
//				{
//					time = (String)dobj.get("time");
//					if(time.equals(""))
//						time = "1";
//				}
//				
//				String fileType = "";
//				String fileType2 = "";
//				if(dobj.has("fileType"))
//				{
//					String fileTypes = dobj.getString("fileType");
//					if(fileTypes != null && !fileTypes.equals(""))
//					{
//						String [] filetypes = fileTypes.split(",");
//						if(filetypes.length > 1)
//						{
//							String str = filetypes[0];
//							String str2 = filetypes[1];
//							if(str.equals("image/png"))
//							{
//								fileType = str;
//								fileType2 = str2;
//							}
//							else
//							{
//								fileType = str2;
//								fileType2 = str;
//							}
//						}
//						else
//						{
//							String str = filetypes[0];
//							if(str.equals("image/png"))
//							{
//								fileType = str;
//								fileType2 = "";
//							}
//							else
//							{
//								fileType = "";
//								fileType2 = str;
//							}
//						}
//					}
//				}
//				
//				String fileName = "";
//				String fileName2 = "";
//				String fileUrl = ""; 
//				String fileUrl2 = "";
//				if(dobj.has("fileName"))
//				{
//					String fileNames = dobj.getString("fileName");
//					if(fileNames != null && !fileNames.equals(""))
//					{
//						String [] filenames = fileNames.split(",");
//						if(filenames.length > 1)
//						{
//							String str = filenames[0];
//							String str2 = filenames[1];
//							if(str.contains(".amr"))
//							{
//								fileName2 = filenames[0];
//								fileUrl2 = BASE_URL + filenames[0];
//								
//								fileName = filenames[1];
//								fileUrl = BASE_URL + filenames[1];
//							}
//							else
//							{
//								fileName = filenames[0];
//								fileUrl = BASE_URL + filenames[0];
//								
//								fileName2 = filenames[1];
//								fileUrl2 = BASE_URL + filenames[1];
//							}
//						}
//						else
//						{
//							String str = filenames[0];
//							if(str.contains(".amr"))
//							{
//								fileName2 = filenames[0];
//								fileUrl2 = BASE_URL + filenames[0];
//								
//								fileName = "";
//								fileUrl = "";
//							}
//							else
//							{
//								fileName = filenames[0];
//								fileUrl = BASE_URL + filenames[0];
//								
//								fileName2 = "";
//								fileUrl2 = "";
//							}
//						}
//					}
//				}
//				
//				String messagetype = ""; 
//				if(dobj.has("messagetype"))
//				{
//					messagetype = (String)dobj.get("messagetype");
//				}
//				
//				JSONObject job = null;
//				if(dobj.has("jsoncontent"))
//				{
//					job = dobj.getJSONObject("jsoncontent");
//				}
//				
//				
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("mid", mid);
//				map.put("storeid", sender);
//				map.put("sname", fname);
//				map.put("nameid", toid);
//				map.put("toid", toid);
//				map.put("mysendname", sender);
//				if(toid.equals(sender))
//					map.put("userimg", myapp.getUserimgbitmap());
//				else
//				{
//					map.put("userimg", myapp.getStoreimgbitmap());
//				}
//				map.put("fname", fname);
//				map.put("tname", tname);
//				map.put("yiman", R.drawable.yi_man);
//				map.put("mymessagecontent", content);
//				map.put("mysendtime",sendTime);
//				map.put("fileUrl",fileUrl);
//				map.put("fileUrl2",fileUrl2);
//				map.put("toname", reciver);
//				map.put("fileType", fileType);
//				map.put("fileType2", fileType2);
//				map.put("fileName", fileName);
//				map.put("fileName2", fileName2);
//				map.put("time", time);
//				map.put("timetext", time+"″");
//				map.put("messagetype", messagetype);
//				map.put("sendimg", "1");
//				map.put("sendprogress", "1");
//				if(job != null)
//				{
//					if(messagetype.equals("fj"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", null);
//						map.put("url", null);
//						if(job.has("dlist"))
//						{
//							JSONArray jArray = (JSONArray) job.get("dlist");
//							List<Map<String,Object>> dlists = myapp.getRoomDetialData(jArray);
//							map.put("storelist", dlists);
//						}
//						else
//						{
//							map.put("messagetype", "");
//							map.put("mymessagecontent", "没有找到相应的房间");
//						}
//					}
//					else if(messagetype.equals("js"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						String storeDoc = job.getString("storeDoc");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", storeDoc);
//						map.put("storelist", null);
//						map.put("url", null);
//					}
//					else if(messagetype.equals("yh"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", null);
//						map.put("url", null);
//						if(job.has("dlist"))
//						{
//							JSONArray jArray = (JSONArray) job.get("dlist");
//							List<Map<String,Object>> dlists = myapp.getStoreCoupData(jArray);
//							map.put("storelist", dlists);
//						}
//						else
//						{
//							map.put("messagetype", "");
//							map.put("mymessagecontent", "没有找到相应的优惠券及活动");
//						}
//					}
//					else if(messagetype.equals("tq"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", null);
//						map.put("url", null);
//						if(job.has("dlist"))
//						{
//							JSONArray jArray = (JSONArray) job.get("dlist");
//							List<Map<String,Object>> dlists = myapp.getCustomizeData(jArray);
//							map.put("storelist", dlists);
//						}
//						else
//						{
//							map.put("messagetype", "");
//							map.put("mymessagecontent", "没有找到相应的天气信息");
//						}
//					}
//					else if(messagetype.equals("dz"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						String addressInfomation = job.getString("addressInfomation");
//						String longItude = job.getString("longItude");
//						String woof = job.getString("woof");
//						String storeDoc = job.getString("storeDoc");
//						String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x250&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
//						map.put("storeimg", url);
//						map.put("storename", storename);
//						map.put("storeDoc", storeDoc);
//						map.put("storelist", null);
//						map.put("url", null);
//					}
//					else if(messagetype.equals("zdy"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						String storeDoc = job.getString("storeDoc");
//						String url = job.getString("url");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", storeDoc);
//						map.put("storelist", null);
//						map.put("url", url);
//					}
//					else if(messagetype.equals("ct"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", null);
//						map.put("url", null);
//						if(job.has("dlist"))
//						{
//							JSONArray jArray = (JSONArray) job.get("dlist");
//							List<Map<String,Object>> dlists = myapp.getRoomDetialData(jArray);
//							map.put("storelist", dlists);
//						}
//						else
//						{
//							map.put("messagetype", "");
//							map.put("mymessagecontent", "没有找到相应的餐厅");
//						}
//					}
//					else if(messagetype.equals("zb"))
//					{
//						String storename = job.getString("name");
//						String storeimg = job.getString("img");
//						map.put("storeimg", BASE_URL+storeimg);
//						map.put("storename", storename);
//						map.put("storeDoc", null);
//						map.put("url", null);
//						if(job.has("dlist"))
//						{
//							JSONArray jArray = (JSONArray) job.get("dlist");
//							List<Map<String,Object>> dlists = myapp.getRoomDetialData(jArray);
//							map.put("storelist", dlists);
//						}
//						else
//						{
//							map.put("messagetype", "");
//							map.put("mymessagecontent", "该商家周边没有找到相应娱乐设施");
//						}
//					}
//				}
//				
//				map.put("isRead", "1");
//				
//				dlist.add(map);
//			}
////			saveMessageLocal(dlist);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return dlist;
//	}
	
	public void openMessageDetail(String nameid,String storeid,String storeName,String username,String typesMapping,Bitmap bitmimg,String serviceid,String servicename,String watag,String imgurl)
	{
		try{
			int index = 0;
			for(int i=0;i< myapp.getMyCardsAll().size();i++)
			{
				Map<String,Object> map = myapp.getMyCardsAll().get(i);
				if(map != null && map.containsKey("storeid"))
				{
					String sid = (String)map.get("storeid");
					if(sid.equals(storeid))
					{
						index = i;
						break;
					}
				}
			}
			Intent intent = new Intent();
		    intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("nameid", nameid);
			bundle.putString("storeid", storeid);
			bundle.putString("storeName", storeName);
			bundle.putString("username", username);
			bundle.putString("typesMapping", typesMapping);
			bundle.putString("serviceid", serviceid);
			bundle.putString("servicename", servicename);
			bundle.putString("watag", watag);
			bundle.putString("groupimg",imgurl);
			if(bitmimg != null)
			{
				int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			bundle.putInt("index", index);
			bundle.putString("tag", "main");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
        	if(menu_display){         //如果 Menu已经打开 ，先关闭Menu
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
//        		Intent intent = new Intent();
//            	intent.setClass(HotelMainActivity.this,Exit.class);
//            	startActivity(intent);
        		MainTabActivity.instance.onMinimizeActivity();
        	}
    	}
    	
//    	else if(keyCode == KeyEvent.KEYCODE_MENU){   //获取 Menu键			
//			if(!menu_display){
//				//获取LayoutInflater实例
//				inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
//				//这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
//				//该方法返回的是一个View的对象，是布局中的根
//				layout = inflater.inflate(R.layout.main_menu, null);
//				
//				//下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单
//				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); //后两个参数是width和height
//				//menuWindow.showAsDropDown(layout); //设置弹出效果
//				//menuWindow.showAsDropDown(null, 0, layout.getHeight());
//				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//				//如何获取我们main中的控件呢？也很简单
//				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
//				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
//				
//				
//				//下面对每一个Layout进行单击事件的注册吧。。。
//				//比如单击某个MenuItem的时候，他的背景色改变
//				//事先准备好一些背景图片或者颜色
//				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
//					@Override
//					public void onClick(View arg0) {						
//						//Toast.makeText(Main.this, "退出", Toast.LENGTH_LONG).show();
//						Intent intent = new Intent();
//			        	intent.setClass(HotelMainActivity.this,Exit.class);
//			        	startActivity(intent);
//			        	menuWindow.dismiss(); //响应点击事件之后关闭Menu
//					}
//				});				
//				menu_display = true;				
//			}else{
//				//如果当前已经为显示状态，则隐藏起来
//				menuWindow.dismiss();
//				menu_display = false;
//				}
//			
//			return false;
//		}
    	return false;
    }
	
	public void loadUserImageThread()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					Bitmap bitmap = getImageBitmap(userimg);
					if(bitmap != null)
					{
						int size=bitmap.getWidth()*bitmap.getHeight()*1; 
						ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, oss);
	                	String productBase64 = new String(Base64.encodeBase64(oss.toByteArray()));
	                	SharedPreferences.Editor editor = share.edit();
	                	// 将编码后的字符串写到base64.xml文件中
	                	editor.putString("userimg", productBase64);
	                	editor.commit();
						myapp.setUserimgbitmap(bitmap);
					}
					msg.obj = bitmap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
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
//		    opt.inSampleSize = 2;
		    
			bitmap = BitmapFactory.decodeStream(is);
//			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public boolean isInetnState() {
		boolean inetnState = true;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			inetnState = false;
		} else {
			inetnState = true;
		}
		return inetnState;
	}
	
	public boolean isWifistart() {
		boolean wifistart = false;
		ContentResolver cv = getContentResolver();
		String tmpS = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.BLUETOOTH_ON);
		tmpS = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.WIFI_ON);
		if (tmpS.equals("1")) {
			wifistart = true;
		} else {
			wifistart = false;
		}
		
		return wifistart;
	}
	
	
	//设置标题栏右侧按钮的作用
	public void btnmainright(View v) {  
		Intent intent = new Intent (HotelMainActivity.this,MainTopRightDialog.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "点击了功能按钮", Toast.LENGTH_LONG).show();
	}
	
	public void openAvatarView(View v) {
		Intent intent = new Intent();
	    intent.setClass( this,GuestInfoActivity.class);
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void openAddGroup() {
		Intent intent = new Intent(this, GroupContactActivity.class);
		Bundle bundle = new Bundle();
		intent.putExtras(bundle);
		startActivity(intent);
		MainTabActivity.instance.loadButtomActivity(intent);
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
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
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
}
