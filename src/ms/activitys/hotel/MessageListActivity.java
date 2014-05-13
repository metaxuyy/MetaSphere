package ms.activitys.hotel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.coupon.MyCouponView;
import ms.activitys.hotel.MyListView.OnRefreshListener;
import ms.activitys.map.BaiduMapRouteSearch;
import ms.activitys.traffic.GetTicketsInfomationActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.FormFile;
import ms.globalclass.FriendlyScrollView;
import ms.globalclass.MYGestureListener;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.MessageBaseAdapter;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.PageControlIconView;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MessageListActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static MessageListActivity instance;
	
	private ProgressDialog mypDialog;
	private int index;
	private String fromname;
	public String toname;
	private String toneetyid;
	public String typesMapping;
	private String tname;
	private String fname;
	
	private String messageId;
	private String userid;
	private String sessionid;
	private String fromName;
	private String storeName;
	private String nameid;
	public List<Map<String, Object>> mymessageItem = new ArrayList<Map<String,Object>>();
	private EditText mssageEdit;
//	private ImageView mImageView;
	private ImageButton refreshbtn;
	private MyListView mymessagelistView;
//	private XListView mymessagelistView;
	private static String BASE_URL = "http://121.199.8.186:8078/upload/";
	private static String BASE_URL2 = "http://121.199.8.186:8078/customize/control/";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;  
	private Bitmap bitmap;
	private String fileUrl = "";
	private String fileName = "";
	private long fileSize;
	private Uri uri;
	private List<Map<String,Object>> smsMessageAll;
	private ImageButton chatting_mode_btn;
	private Button mBtnRcd;
	private boolean btn_vocie = false;
//	private RelativeLayout mBottom;
	private LinearLayout content_layout;
	private LinearLayout text_panel_ll;
	private LinearLayout del_re;
	private View rcChat_popup;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
	voice_rcd_hint_tooshort;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private ImageView sc_img1;
	private boolean isShosrt = false;
	private SoundMeter mSensor;
	private ImageView volume;
	private static MediaPlayer player;
	private static MediaPlayer playerend;
	private LinearLayout app_layout;
	private boolean isappOpen = false;
	//自定义的弹出框类
	private	SelectPicPopupWindow menuWindow;
	private	SelectBgPopupWindow bgmenuWindow;
	private List<String> pItems;
//	private SpecialAdapter myAdapter;
	public MessageBaseAdapter myAdapter;
	private int page = 0;
	private boolean isnull = false;
	private int lastsize = 0;
	private int lastsize2 = 0;
	private String ip;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private String pictag;
	private RelativeLayout main_rlayout; 
	/*用来标识请求gallery的activity*/  
	private static final int PHOTO_PICKED_WITH_DATA = 3021;  
	/*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator01;
    private Timer mTimer;
    private Map<String, Object> newmap;
    private static DBHelperMessage db;
    private LinearLayout rl_layout;
    private String tag;
    public static FileUtils fileUtil = new FileUtils();
    private ImageButton mymassagesMenuButton;
    private ImageButton add_app_btn;
    private LinearLayout menu_bottom;
    private boolean ismunebtn = false;
    private View bottomMunelayout;
    private LayoutInflater inflater;
    private PopupWindow bottomMenuWindow;
    private TextView menu_btn1;
    private TextView menu_btn2;
    private TextView menu_btn3;
    private Map<String,Object> bottom_menu_txt = null;
    private Map<String,Object> bottom_menu_txt2 = null;
    private Map<String,Object> bottom_menu_txt3 = null;
    public TextView TextView01;
    private String munetag = "";
    private Dialog myDialogs;
    private View yanzhenview;
    private MyLoadingDialog loadDialog;
    private String filetype;
    private String customizeMenu = "";
    private String serviceid = "";
    private String servicename = "";
    private String watag = "";//表示是我们自己的app过来的还是微信过来的
    public Map<String,Integer> tempmap = new HashMap<String,Integer>();//保存需要确认对方是否收到信息的数据集
    private ImageButton card_btn;
    private String groupimg = "";
    private String [] messageMumeitems;
    private LinearLayout biaopin_layout;
    private ViewFlipper mViewFlipper;
    private PageControlIconView pageControl;
    private GestureDetector mGestureDetector; 
    private View view1;
	private View view2;
	private View view3;
	private View view4;
	private View view5;
	private int [] imgs = {R.drawable.smiles1,R.drawable.smiles2,R.drawable.smiles3,R.drawable.smiles4,R.drawable.smiles5,R.drawable.smiles6
			,R.drawable.smiles7,R.drawable.smiles8,R.drawable.smiles9,R.drawable.smiles10};
	private int [] imgs2 = {R.drawable.smiles11
			,R.drawable.smiles12,R.drawable.smiles13,R.drawable.smiles14,R.drawable.smiles15,R.drawable.smiles16
			,R.drawable.smiles17,R.drawable.smiles18,R.drawable.smiles19,R.drawable.smiles20};
	private int [] imgs3 = {R.drawable.smiles21
			,R.drawable.smiles22,R.drawable.smiles23,R.drawable.smiles24,R.drawable.smiles25,R.drawable.smiles26,R.drawable.smiles27,R.drawable.smiles28,R.drawable.smiles29,R.drawable.smiles30
			};
	private int [] imgs4 = {R.drawable.smiles31,R.drawable.smiles32,R.drawable.smiles33,R.drawable.smiles34,R.drawable.smiles35
			,R.drawable.smiles36,R.drawable.smiles37,R.drawable.smiles38,R.drawable.smiles39,R.drawable.smiles40
			};
	private int [] imgs5 = {R.drawable.smiles41,R.drawable.smiles42,R.drawable.smiles43,R.drawable.smiles44,R.drawable.smiles45
			,R.drawable.smiles46,R.drawable.smiles47,R.drawable.emoji041};
	
	private int [] tapeimgs1 = {R.drawable.tape1,R.drawable.tape2,R.drawable.tape3,R.drawable.tape4,R.drawable.tape5
			,R.drawable.tape6,R.drawable.tape7,R.drawable.tape8};
	private int [] tapeimgs2 = {R.drawable.tape9,R.drawable.tape10,R.drawable.tape11,R.drawable.tape12,R.drawable.tape13
			,R.drawable.tape14,R.drawable.tape15,R.drawable.tape16};
	private int [] tapeimgs3 = {R.drawable.tape17,R.drawable.tape18,R.drawable.tape19,R.drawable.tape20,R.drawable.tape21
			,R.drawable.tape22,R.drawable.tape23,R.drawable.tape24};
	private int [] tapeimgs4 = {R.drawable.tape25,R.drawable.tape26,R.drawable.tape27,R.drawable.tape28,R.drawable.tape29
			,R.drawable.tape30,R.drawable.tape31,R.drawable.tape32};
	private int [] tapeimgs5 = {R.drawable.tape33,R.drawable.tape34,R.drawable.tape35,R.drawable.tape36,R.drawable.tape37,R.drawable.tape38,R.drawable.tape39,R.drawable.tape40};
	private static final int FLING_MIN_DISTANCE = 100;  
	private static final int FLING_MIN_VELOCITY = 200;
	private int layoutindex = 0;
	private LinearLayout gridSmilesContainer;
	private List<String> browlist = new ArrayList<String>();
//	private ImageButton imgbtn;
	private boolean smilestag = false;
	private Button btnPhoto;
	private Button btnTape;
	public boolean isplay = false;//是否播放动画表情
	private MYGestureListener gestureListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperMessage(this, myapp);
		
//		String ip = this.share.getString("ipadrees", "121.199.8.186");
//		myapp.setHost(ip);
		String ip = myapp.getHost();
		BASE_URL = "http://"+ip+":80/upload/";
		BASE_URL2 = "http://"+ip+":80/customize/control/";
		
		instance = this;
		inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		showProgressDialog();
		
//		bottom_menu_txt = new String[]{getString(R.string.hotel_label_55),getString(R.string.hotel_label_56),getString(R.string.hotel_label_57),getString(R.string.hotel_label_58)};
//		bottom_menu_txt2 = new String[]{getString(R.string.hotel_label_59),getString(R.string.hotel_label_60)};
//		bottom_menu_txt3 = new String[]{getString(R.string.hotel_label_61),getString(R.string.hotel_label_62)};
//		Map<String,Object> user = myapp.getUserlist().get(index);
//		String contactName = (String)user.get("contactName");
//		String nettyid = (String)user.get("nettyid");
//		
//		fromname = myapp.getUserName();
//		toname = contactName;
//		toneetyid = nettyid;
		Bundle bunde = this.getIntent().getExtras();
		storeName = bunde.getString("storeName");
		nameid = bunde.getString("nameid");
		String storeid = bunde.getString("storeid");
		byte[] imgbyte = bunde.getByteArray("storeimg");
		typesMapping = bunde.getString("typesMapping");
		index = bunde.getInt("index");
		fromname = nameid;
		toname = storeid;
		toneetyid = "";
		tname = storeName;
		fname = myapp.getUserName();
		tag = bunde.getString("tag");
		if(bunde.containsKey("serviceid"))
			serviceid = bunde.getString("serviceid");
		if(bunde.containsKey("servicename"))
			servicename = bunde.getString("servicename");
		if(bunde.containsKey("watag"))
			watag = bunde.getString("watag");//表示是我们自己的app过来的还是微信过来的
		if(bunde.containsKey("groupimg"))
			groupimg = bunde.getString("groupimg");//群组图片
		
		loadCustomizeMenu();
		
		card_btn = (ImageButton)findViewById(R.id.card_btn);
		card_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openCardView();
			}
		});
		
		Button break_btn = (Button)findViewById(R.id.home);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMainView();
			}
		});
		
		TextView01 = (TextView)findViewById(R.id.TextView01);
		TextView01.setText(storeName);
		
//		hasSdcard();
		
		if(imgbyte != null)
		{
			Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte,0,imgbyte.length);
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
			if(bitmap != null)
				myapp.setStoreimgbitmap(bitmap);
			else
				myapp.setStoreimgbitmap(null);
		}
		else
		{
			myapp.setStoreimgbitmap(null);
		}
		
		rl_layout = (LinearLayout)findViewById(R.id.rl_layout);
		
		if(typesMapping.equals("group"))
		{
			synchronousGroupInfo();
		}
		initView();
		
//		if(imgurl != null && !imgurl.equals(""))
//			loadUserImageThread2(imgurl);
	}
	
	public void synchronousGroupInfo()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 11;
				
				try {
					Map<String,Object> umap = null;
					JSONObject jobj = api.synchronousGroupInfo(toname);
					if(jobj != null)
					{
						JSONArray jarry = jobj.getJSONArray("data");
						List<Map<String,Object>> dlist = myapp.geGroupInfoListData(jarry);
						if(dlist != null && dlist.size() > 0)
						{
							Map<String,Object> dmap = dlist.get(0);
							db.openDB();
							db.updateNewMessageGroupInfoData((String)dmap.get("groupName"), (String)dmap.get("groupids"), toname);
							db.closeDB();
							umap = dmap;
						}
					}
					
					msg.obj = umap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void setGroupName(String gname)
	{
		storeName = gname;
		TextView01.setText(storeName);
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			if(tag.equals("storeview"))
			{
				intent.setClass( this,StoreViewActivity.class);
				bundle.putInt("index", index);
			}
			else if(tag.equals("friendinfo"))
			{
				intent.setClass( this,FriendInfoActivity.class);
			}
			else if(tag.equals("myfriendinfo"))
			{
				intent.setClass( this,FriendInfoViewActivity.class);
			}
			else if(tag.equals("group"))
			{
				intent.setClass( this,MainTabActivity.class);
			}
			else
				intent.setClass( this,MainTabActivity.class);
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    instance = null;
		    tempmap = null;
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openCardView(String typesMapping,int index)
	{
		try{
			if(typesMapping.equals("09"))
			{
//				Intent intent = new Intent();
//			    intent.setClass( this,HotelActivity.class);
//			    Bundle bundle = new Bundle();
//				bundle.putInt("index", index);
//				bundle.putString("tag", "message");
//				intent.putExtras(bundle);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "message");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else if(typesMapping.equals("friend") || typesMapping.equals("qa"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,ChatMessageInfoActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("pfid", toname);
				bundle.putString("typesMapping", typesMapping);
				bundle.putString("storeName", storeName);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else
			{
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "message");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openCardView()
	{
		try{
			if(typesMapping.equals("09"))
			{
//				Intent intent = new Intent();
//			    intent.setClass( this,HotelActivity.class);
//			    Bundle bundle = new Bundle();
//				bundle.putInt("index", index);
//				bundle.putString("tag", "message");
//				intent.putExtras(bundle);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "message");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else if(typesMapping.equals("friend") || typesMapping.equals("qa"))
			{
				if(ChatMessageInfoActivity.instance != null)
					ChatMessageInfoActivity.instance.finish();
				Intent intent = new Intent();
			    intent.setClass( this,ChatMessageInfoActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("pfid", toname);
				bundle.putString("typesMapping", typesMapping);
				bundle.putString("storeName", storeName);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else if(typesMapping.equals("group"))
			{
				Intent intent = new Intent();
			    intent.setClass( this,ChatMessageInfoActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("pfid", toname);
				bundle.putString("typesMapping", typesMapping);
				bundle.putString("storeName", storeName);
				bundle.putString("groupimg", groupimg);
				bundle.putString("groupid", toname);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else
			{
				Intent intent = new Intent();
//			    intent.setClass( this,CardsView.class);
				intent.setClass(this, StoreMainActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("tag", "message");
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(android.os.Environment.getExternalStorageDirectory()+"/ktsvoices/");
			if (!file.exists()) {
				file.mkdir();
			}
		} else {
			makeText("No SDCard");
		}
	}
	
	public void loadCustomizeMenu()
	{
		try{
			if(!typesMapping.equals("friend"))
			{
				List<Map<String,Object>> datalist = myapp.getMyCardsAll();
				boolean isnull = false;
				int index = 0;
				String typesMapping = "";
				for(int i=0;i<datalist.size();i++)
				{
					Map<String,Object> map = datalist.get(i);
					String storeid = (String)map.get("storeid");
					if(toname.equals(storeid))
					{
						customizeMenu = (String)map.get("customizeMenu");
						break;
					}
				}
				
				if(customizeMenu != null && !customizeMenu.equals(""))
				{
					JSONArray jarry = new JSONArray(customizeMenu);
					for(int j=0;j<jarry.length();j++)
					{
						JSONObject job = jarry.getJSONObject(j);
						String name = job.getString("name");
						JSONArray cjarry = job.getJSONArray("clid");
						
						List<Map<String,String>> dlist = new ArrayList<Map<String,String>>();
						if(cjarry != null && cjarry.length() > 0)
						{
							for(int k=0;k<cjarry.length();k++)
							{
								JSONObject cjob = cjarry.getJSONObject(k);
								String itemname = cjob.getString("itemname");
								String keyword = cjob.getString("keyword");
								String type = cjob.getString("type");
								String link = cjob.getString("link");
								
								Map<String,String> map = new HashMap<String,String>();
								map.put("itemname", itemname);
								map.put("keyword", keyword);
								map.put("type", type);
								map.put("link", link);
								dlist.add(map);
 							}
						}
						if(j == 0)
						{
							bottom_menu_txt = new HashMap<String,Object>();
							bottom_menu_txt.put("name", name);
							bottom_menu_txt.put("list", dlist);
						}
						else if(j == 1)
						{
							bottom_menu_txt2 = new HashMap<String,Object>();
							bottom_menu_txt2.put("name", name);
							bottom_menu_txt2.put("list", dlist);
						}
						else if(j == 2)
						{
							bottom_menu_txt3 = new HashMap<String,Object>();
							bottom_menu_txt3.put("name", name);
							bottom_menu_txt3.put("list", dlist);
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void initView()
	{
		try{
			if(player == null){
				player = MediaPlayer.create(this, R.raw.qrcode_completed);
			}
			
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 为什么没有触发这个函数呢？
				@Override
				public void onCompletion(MediaPlayer arg0) {
					System.out.println(".......CompletionListener.......");
					player.release();
					start(voiceName);
					flag = 2;
				}
			});
				
			if(playerend == null){
				playerend = MediaPlayer.create(this, R.raw.after_upload_voice);
			}
//				try {
//					player.prepare();
//				} catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
//			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//			mMediaPlayer = new MediaPlayer();
//			mMediaPlayer.setDataSource(this, alert);
			if(mMediaPlayer == null){
				mMediaPlayer = MediaPlayer.create(this, R.raw.phonering);
//				try {
//					mMediaPlayer.prepare();
//				} catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
			app_layout = (LinearLayout)findViewById(R.id.app_layout);
			main_rlayout = (RelativeLayout)findViewById(R.id.main_rlayout);
			if(!share.getString("bg_img", "").equals(""))
	        {
	        	String fileimg = share.getString("bg_img", "");
	        	if(fileimg.contains("sdcard"))
	        	{
		        	Drawable drawable = getImageDrawable(fileimg);
		        	if(drawable != null)
		        		main_rlayout.setBackgroundDrawable(drawable);
	        	}
	        	else
	        	{
	        		selectBg(Integer.valueOf(fileimg));
	        	}
	        }
			
			mSensor = new SoundMeter();
			volume = (ImageView)findViewById(R.id.volume);
			voice_rcd_hint_rcding = (LinearLayout)findViewById(R.id.voice_rcd_hint_rcding);
			voice_rcd_hint_loading = (LinearLayout) findViewById(R.id.voice_rcd_hint_loading);
			voice_rcd_hint_tooshort = (LinearLayout) findViewById(R.id.voice_rcd_hint_tooshort);
			mymassagesMenuButton = (ImageButton)findViewById(R.id.mymassagesMenuButton);
			ImageButton chatting_mode_separator = (ImageButton)findViewById(R.id.chatting_mode_separator);
			mssageEdit = (EditText)findViewById(R.id.messagecontent);
			rcChat_popup = findViewById(R.id.rcChat_popup);
//			img1 = (ImageView) this.findViewById(R.id.img1);
			del_re = (LinearLayout) findViewById(R.id.del_re);
			
			menu_bottom = (LinearLayout)findViewById(R.id.menu_bottom);
//			spackButton();
			showVoiceLayout();
			
			if(typesMapping.equals("friend") || customizeMenu.equals(""))
			{
				mymassagesMenuButton.setVisibility(View.GONE);
				chatting_mode_separator.setVisibility(View.GONE);
			}
			
			else
			{
				mymassagesMenuButton.setImageDrawable(null);
				mymassagesMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.chatting_footjianpan_btn));
				menu_bottom.setVisibility(View.VISIBLE);
				content_layout.setVisibility(View.GONE);
				ismunebtn = true;
			}
			
			if(typesMapping.equals("group"))
			{
				card_btn.setImageResource(R.drawable.mm_title_btn_groupcontact_normal);
			}
			
//			mImageView = (ImageView)findViewById(R.id.mImage);
//			refreshbtn = (ImageButton)findViewById(R.id.refresh_btn);

//			TextView titleview = (TextView)view.findViewById(R.id.TextView01);
//			titleview.requestFocus();
//			titleview.setText(fromName);
			
			mymessagelistView = (MyListView)findViewById(R.id.mymessagelistviews);
			mymessagelistView.initlable(getString(R.string.hotel_label_33), getString(R.string.hotel_label_31), getString(R.string.hotel_label_34), getString(R.string.hotel_label_35));
//			mymessagelistView = (XListView)findViewById(R.id.mymessagelistviews);
//			mymessagelistView.setPullLoadEnable(true);
			//			getMessageListData("refresh");
			getLocalMessageListData("refresh");
			
			// 自定义adapter对象
//			myAdapter = new SpecialAdapter(this, mymessageItem,
//					R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,
//					new String[] {"fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc"}, 
//					new int[] {R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt},
//					share,"max",fromname);
//
//			// 添加并且显示
//			mymessagelistView.setAdapter(myAdapter);
////			mymessagelistView.setDividerHeight(0);
//			mymessagelistView.setOnItemClickListener(new OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//						long arg3) {
//					// TODO Auto-generated method stub
//					String fileUrl = (String)mymessageItem.get(arg2).get("fileUrl");
//					String messagetype = (String)mymessageItem.get(arg2).get("messagetype");
//					if(messagetype.equals("") || messagetype.equals("wh"))
//					{
//						if(fileUrl != null && !fileUrl.equals(""))
//						{
//							System.out.println("fileUrl=="+fileUrl);
//							showMessageDetails(fileUrl);
//						}
//					}
//					else
//					{
//						if(messagetype.equals("fj"))
//						{
//							showBankRoomWindo();
//						}
//					}
//				}
//			});
			
//			mymessagelistView.setXListViewListener(new IXListViewListener() {
//				
//				@Override
//				public void onRefresh() {
//					// TODO Auto-generated method stub
//					System.out.println("111===========================");
//					if(lastsize == 0)
//					{
//						mHandler.postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								try {
//									if(!isnull)
//									{
//										List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//										if(lastsize == 0)
//										{
////											System.out.println("=====数据库加载");
//											page = page + 1;
//											db.openDB();
//											list = db.getMessageData(toname, fromname, page);
//											db.closeDB();
//										}
//										if(list.size() > 0 )
//										{
//											mymessageItem.addAll(0,list);
//											if(list.size() < 20)
//											{
//												lastsize = list.size();
//												isnull = false;
//											}
//											else
//											{
//												lastsize = 0;
//												isnull = false;
//											}
//										}
//										else
//										{
//											isnull = true;
//										}
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								
//								
//	//							myAdapter.notifyDataSetChanged();
//	//							mymessagelistView.onRefreshComplete();
//								if(!isnull)
//								{
//									if(lastsize != lastsize2)
//										lastsize2 = 0;
//									if(lastsize2 > 0)
//									{
//										makeText("已经是全部信息");
//	//									mymessagelistView.onRefreshComplete();
//										mymessagelistView.setSelection(0);
//									}
//									else
//									{
//										lastsize2 = lastsize;
//										
//										myAdapter = new SpecialAdapter(MessageListActivity.this, mymessageItem,
//												R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,
//												new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress"}, 
//												new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar},
//												share,"max",fromname);
//				//
//				//						// 添加并且显示
//										mymessagelistView.setAdapter(myAdapter);
//	//									mymessagelistView.onRefreshComplete();
//				//						mymessagelistView.setSelection(page*20+1);
//										int[] location = new int[2];  
//										rl_layout.getLocationOnScreen(location);
//							            int x = location[0];  
//							            int y = location[1];  
//							            System.out.println("x:"+x+"y:"+y);
//							            
//							            if(lastsize2 == 0)
//							            	mymessagelistView.setSelectionFromTop(21, y+60);
//							            else
//							            	mymessagelistView.setSelectionFromTop(lastsize2+1, y+60);
//										
//									}
//								}
//								else
//								{
//									makeText("已经是全部信息");
//	//								mymessagelistView.onRefreshComplete();
//									mymessagelistView.setSelection(0);
//								}
//								mymessagelistView.stopRefresh();
//								mymessagelistView.stopLoadMore();
//								mymessagelistView.setRefreshTime("刚刚");
//							}
//						}, 2000);
//					}
//					else
//					{
//						mymessagelistView.stopRefresh();
//						mymessagelistView.stopLoadMore();
//						mymessagelistView.stopProgressBar();
//						mymessagelistView.setRefreshTime("刚刚");
//					}
//				}
//				
//				@Override
//				public void onLoadMore() {
//					// TODO Auto-generated method stub
//					System.out.println("222===========================");
//					mymessagelistView.stopRefresh();
//					mymessagelistView.stopLoadMore();
//					mymessagelistView.setRefreshTime("刚刚");
//				}
//			});
			
//			mymessagelistView.setonRefreshListener(new OnRefreshListener() {
//				public void onRefresh() {
//					new AsyncTask<Void, Void, Void>() {
//						protected Void doInBackground(Void... params) {
//							try {
//								if(!isnull)
//								{
//									List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//									if(lastsize == 0)
//									{
//										page = page + 1;
//										db.openDB();
//										list = db.getMessageData(toname, fromname, page);
//										db.closeDB();
//									}
//									if(list.size() > 0 )
//									{
//										mymessageItem.addAll(0,list);
//										if(list.size() < 20)
//										{
//											lastsize = list.size();
//											isnull = false;
//										}
//										else
//										{
//											lastsize = 0;
//											isnull = false;
//										}
//									}
//									else
//									{
//										isnull = true;
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							return null;
//						}
//
//						@Override
//						protected void onPostExecute(Void result) {
////							myAdapter.notifyDataSetChanged();
////							mymessagelistView.onRefreshComplete();
//							if(!isnull)
//							{
//								if(lastsize != lastsize2)
//									lastsize2 = 0;
//								if(lastsize2 > 0)
//								{
//									makeText("已经是全部信息");
//									mymessagelistView.onRefreshComplete();
//									mymessagelistView.setSelection(0);
//								}
//								else
//								{
//									lastsize2 = lastsize;
//									
////									myAdapter = new MessageBaseAdapter(MessageListActivity.this, mymessageItem,
////											R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,
////											new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress"}, 
////											new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar},
////											share,"max",fromname);
//////		
//////									// 添加并且显示
////									mymessagelistView.setAdapter(myAdapter);
//									myAdapter.notifyDataSetChanged();
//									mymessagelistView.onRefreshComplete();
////									mymessagelistView.setSelection(page*20+1);
//									int[] location = new int[2];  
//									rl_layout.getLocationOnScreen(location);
//						            int x = location[0];  
//						            int y = location[1];  
//						            System.out.println("x:"+x+"y:"+y);
//						            
//						            if(lastsize2 == 0)
//						            	mymessagelistView.setSelectionFromTop(21, y+60);
//						            else
//						            	mymessagelistView.setSelectionFromTop(lastsize2+1, y+60);
////									mymessagelistView.setSelectionFromTop(lastsize2+1, y+30);
//									
//								}
//							}
//							else
//							{
//								makeText("已经是全部信息");
//								mymessagelistView.onRefreshComplete();
//								mymessagelistView.setSelection(0);
//							}
//						}
//
//					}.execute(null,null,null);
//				}
//			});
			
			mymessagelistView.setonRefreshListener(new OnRefreshListener() {
				
				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					if(lastsize == 0)
					{
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									if(!isnull)
									{
										List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
										if(lastsize == 0)
										{
											page = page + 1;
											db.openDB();
											list = db.getMessageData(toname, fromname, page);
											db.closeDB();
										}
										if(list.size() > 0 )
										{
											mymessageItem.addAll(0,list);
											if(list.size() < 20)
											{
												lastsize = list.size();
												isnull = false;
											}
											else
											{
												lastsize = 0;
												isnull = false;
											}
										}
										else
										{
											isnull = true;
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								if (!isnull) {
									if (lastsize != lastsize2)
										lastsize2 = 0;
									if (lastsize2 > 0) {
										makeText("已经是全部信息");
										mymessagelistView.setSelection(0);
									} else {
										lastsize2 = lastsize;

										myAdapter.notifyDataSetChanged();
										mymessagelistView.onRefreshComplete();
										int[] location = new int[2];
										rl_layout.getLocationOnScreen(location);
										int x = location[0];
										int y = location[1];
										System.out.println("x:" + x + "y:" + y);

										if (lastsize2 == 0)
											mymessagelistView
													.setSelectionFromTop(21,
															y + 60);
										else
											mymessagelistView
													.setSelectionFromTop(
															lastsize2 + 1,
															y + 60);

									}
								}
								else
								{
									makeText("已经是全部信息");
									mymessagelistView.setSelection(0);
								}
								mymessagelistView.onRefreshComplete();
							}
						}, 2000);
					}
					else
					{
						mymessagelistView.onRefreshComplete();
					}
				}
			});
			
			
			Button sendbutton = (Button)findViewById(R.id.mymassagesendButton);
			sendbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
						String content = mssageEdit.getText().toString();
						int index = mymessageItem.size();
//						mssageEdit.setVisibility(View.INVISIBLE);
						if(content.equals(""))
						{
							makeText("请在文本框里输入数据");
							mssageEdit.requestFocus();
						}
						else
						{
							for(int i=0;i<browlist.size();i++)
							{
								content = content.replaceFirst("￼", browlist.get(i));
							}
							
							sendMessageText(content,true,index);
						}
				}
			});
			
//			refreshbtn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					getMessageListData("refresh");
//				}
//			});
			
			add_app_btn = (ImageButton)findViewById(R.id.add_app_btn);
			add_app_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isappOpen)
					{
						if(gridSmilesContainer.getVisibility() == View.VISIBLE && biaopin_layout.getVisibility() == View.VISIBLE)
						{
							gridSmilesContainer.setVisibility(View.GONE);
							biaopin_layout.setVisibility(View.GONE);
							app_layout.setVisibility(View.VISIBLE);
						}
						else
						{
							isappOpen = false;
							app_layout.setVisibility(View.GONE);
//							mssageEdit.setFocusable(true);
							mssageEdit.requestFocus();
							InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
							//得到InputMethodManager的实例
							if (imm.isActive()) {
								//如果开启
								imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
								//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
							}
							else
							{
								imm.showSoftInput(mssageEdit, 0);
							}
						}
					}
					else
					{
						isappOpen = true;
						app_layout.setVisibility(View.VISIBLE);
						mymessagelistView.setSelection(mymessagelistView.getMaxScrollAmount());
//						mssageEdit.setFocusable(false);
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
						//得到InputMethodManager的实例
						if (imm.isActive()) {
							//如果开启
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
							//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
						}
					}
				}
			});
			
			mViewFlipper = (ViewFlipper) findViewById(R.id.details);
			gridSmilesContainer = (LinearLayout)findViewById(R.id.gridSmilesContainer);
			pageControl=(PageControlIconView) findViewById(R.id.pageControl);
			pageControl.bindScrollViewGroup(mViewFlipper);
			view1 = View.inflate(this, R.layout.gridsmiles, null);
			view2 = View.inflate(this, R.layout.gridsmiles2, null);
			view3 = View.inflate(this, R.layout.gridsmiles, null);
			view4 = View.inflate(this, R.layout.gridsmiles, null);
			view5 = View.inflate(this, R.layout.gridsmiles, null);
			mGestureDetector = new GestureDetector(new CommonGestureListener());
			btnPhoto = (Button)findViewById(R.id.btnPhoto);
			btnTape = (Button)findViewById(R.id.btnTape);
			btnPhoto.setBackgroundResource(R.drawable.btncontentselected);
			btnTape.setBackgroundResource(R.drawable.send_bg_private);
//			btnPhoto.setFocusable(true);
//			btnPhoto.setPressed(true);
			
			btnPhoto.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					layoutindex = 0;
					pageControl.generatePageControl(layoutindex);
					mViewFlipper.setDisplayedChild(0);
					btnPhoto.setBackgroundResource(R.drawable.btncontentselected);
					btnTape.setBackgroundResource(R.drawable.send_bg_private);
					mssageEdit.setEnabled(true);
					mssageEdit.setCursorVisible(true);
			        mssageEdit.setFocusable(true);
			        mssageEdit.setFocusableInTouchMode(true);
					loadImgGridView2();
				}
			});
			
			btnTape.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					layoutindex = 0;
					pageControl.generatePageControl(layoutindex);
					mViewFlipper.setDisplayedChild(0);
					btnPhoto.setBackgroundResource(R.drawable.send_bg_private);
					btnTape.setBackgroundResource(R.drawable.btncontentselected);
					mssageEdit.setEnabled(false);
					mssageEdit.setCursorVisible(false);
			        mssageEdit.setFocusable(false);
			        mssageEdit.setFocusableInTouchMode(false);
			        loadTapeImgGridView();
				}
			});
			
			loadScrollView();
			loadImgGridView();
//			loadImgGridView2();
			
			biaopin_layout = (LinearLayout)findViewById(R.id.biaopin_layout);
			ImageButton add_biaoqin_btn = (ImageButton)findViewById(R.id.add_biaoqin_btn);
			add_biaoqin_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gridSmilesContainer.setVisibility(View.VISIBLE);
					biaopin_layout.setVisibility(View.VISIBLE);
					app_layout.setVisibility(View.GONE);
					layoutindex = 0;
					pageControl.generatePageControl(layoutindex);
					mViewFlipper.setDisplayedChild(0);
					loadImgGridView2();
					btnPhoto.setBackgroundResource(R.drawable.btncontentselected);
					btnTape.setBackgroundResource(R.drawable.send_bg_private);
					mssageEdit.setEnabled(true);
					mssageEdit.setCursorVisible(true);
			        mssageEdit.setFocusable(true);
			        mssageEdit.setFocusableInTouchMode(true);
				}
			});
			
			
			ImageButton add_img_btn = (ImageButton)findViewById(R.id.add_img_btn);
			add_img_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pictag = "uploadimg";
					//实例化SelectPicPopupWindow
					menuWindow = new SelectPicPopupWindow(MessageListActivity.this, itemsOnClick);
					//显示窗口
					menuWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
				}
			});
			
			ImageButton spack_btn = (ImageButton)findViewById(R.id.spack_btn);
			spack_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startVoiceRecognitionActivity();
				}
			});
			
			
			ImageButton tibg_btn = (ImageButton)findViewById(R.id.tibg_btn);
			tibg_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pictag = "bgimg";
					//实例化SelectPicPopupWindow
					bgmenuWindow = new SelectBgPopupWindow(MessageListActivity.this, bgitemsOnClick);
					//显示窗口
					bgmenuWindow.showAtLocation(findViewById(R.id.main_rlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
				}
			});
			
			mymassagesMenuButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(ismunebtn)
					{
						if(bottomMenuWindow.isShowing())
							bottomMenuWindow.dismiss();
						mymassagesMenuButton.setImageDrawable(null);
						mymassagesMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.chattiing_footmenu_btn));
//						chatting_mode_btn.setVisibility(View.VISIBLE);
//						add_app_btn.setVisibility(View.VISIBLE);
//						mBottom.setVisibility(View.VISIBLE);
						menu_bottom.setVisibility(View.GONE);
						content_layout.setVisibility(View.VISIBLE);
						ismunebtn = true;
						ismunebtn = false;
					}
					else
					{
						mymassagesMenuButton.setImageDrawable(null);
						mymassagesMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.chatting_footjianpan_btn));
//						chatting_mode_btn.setVisibility(View.GONE);
//						add_app_btn.setVisibility(View.GONE);
//						mBottom.setVisibility(View.GONE);
						menu_bottom.setVisibility(View.VISIBLE);
						content_layout.setVisibility(View.GONE);
						ismunebtn = true;
					}
				}
			});
			
			bottomMunelayout = inflater.inflate(R.layout.bootm_menu_list, null);
			LinearLayout menu_item = (LinearLayout)bottomMunelayout.findViewById(R.id.menu_item);
			menu_item.getBackground().setAlpha(200);
			bottomMenuWindow = new PopupWindow(bottomMunelayout,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); //后两个参数是width和height
//			bottomMenuWindow.setOutsideTouchable(true);
//			bottomMenuWindow.setFocusable(true);
			//menuWindow.showAsDropDown(layout); //设置弹出效果
			//menuWindow.showAsDropDown(null, 0, layout.getHeight());
//			bottomMenuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
			
			menu_btn1 = (TextView) findViewById(R.id.menu_btn1);
			if(bottom_menu_txt != null)
			{
				String name = (String)bottom_menu_txt.get("name");
				final List<Map<String,String>> mlist = (List<Map<String,String>>)bottom_menu_txt.get("list");
				menu_btn1.setText(name);
				menu_btn1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mlist != null && mlist.size() > 0)
						{
							boolean b = false;
							if(bottomMenuWindow.isShowing())
							{
								bottomMenuWindow.dismiss();
								b = true;
							}
							if(!munetag.equals("0") || !b)
							{
								munetag = "0";
								int[] location = new int[2];  
								menu_btn1.getLocationOnScreen(location);  
								int chattingw = chatting_mode_btn.getWidth();
								if(chattingw == 0)
									chattingw = menu_btn1.getWidth() / 2;
								int appw = add_app_btn.getWidth();
								if(appw == 0)
									appw = menu_bottom.getLeft() / 2;
		//			            int x = menu_btn1.getLeft() - (chatting_mode_btn.getWidth() + add_app_btn.getWidth()) - (menu_btn1.getWidth()/2);  
								int x = menu_btn1.getLeft() - (chattingw + appw); 
					            int y = menu_btn1.getBottom() + 20;  
								LinearLayout layout = (LinearLayout)bottomMunelayout.findViewById(R.id.menu_item);
								layout.removeAllViews();
								for(int i=0;i<mlist.size();i++)
								{
									Map<String,String> nmap = mlist.get(i);
									final String name = nmap.get("itemname");
									String keyword = nmap.get("keyword");
									final String type = nmap.get("type");
									final String link = nmap.get("link");
									View muenitemlayout = inflater.inflate(R.layout.bootm_menu_list_item, null);
									TextView text = (TextView)muenitemlayout.findViewById(R.id.menu_txt);
									text.setText(name);
									LinearLayout menulayout = (LinearLayout)muenitemlayout.findViewById(R.id.menu_list_item_btn);
									final String str = keyword;
									menulayout.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											if(type.equals("1"))
											{
												int index = mymessageItem.size();
												bottomMenuWindow.dismiss();
												sendMessageText(str,false,index);
											}
											else
											{
												String newlink = "";
												if(link.indexOf("?") >= 0)
													newlink = link + "&username="+myapp.getUserName();
												else
													newlink = link + "?username="+myapp.getUserName();
												openHtmlView(newlink,name);
											}
										}
									});
									layout.addView(muenitemlayout);
								}
								bottomMenuWindow.showAtLocation(MessageListActivity.this.findViewById(R.id.rl_bottom), Gravity.BOTTOM|Gravity.CENTER_VERTICAL, x, y);
							}
						}
					}
				});
			}
			else
			{
				menu_btn1.setVisibility(View.GONE);
			}
			menu_btn2 = (TextView) findViewById(R.id.menu_btn2);
			if(bottom_menu_txt2 != null)
			{
				String name = (String)bottom_menu_txt2.get("name");
				final List<Map<String,String>> mlist = (List<Map<String,String>>)bottom_menu_txt2.get("list");
				menu_btn2.setText(name);
				menu_btn2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mlist != null && mlist.size() > 0)
						{
							boolean b = false;
							if(bottomMenuWindow.isShowing())
							{
								bottomMenuWindow.dismiss();
								b = true;
							}
							if(!munetag.equals("1") || !b)
							{
								munetag = "1";
								int[] location = new int[2];  
								menu_btn2.getLocationOnScreen(location);  
		//			            int x = menu_btn2.getLeft() - (chatting_mode_btn.getWidth() + add_app_btn.getWidth()) - (menu_btn2.getWidth()/2);  
								int chattingw = chatting_mode_btn.getWidth();
								if(chattingw == 0)
									chattingw = menu_btn2.getWidth() / 2;
								int appw = add_app_btn.getWidth();
								if(appw == 0)
									appw = menu_bottom.getLeft() / 2;
								int x = menu_btn2.getLeft() - (chattingw + appw);  
					            int y = menu_btn2.getBottom() + 20;  
								LinearLayout layout = (LinearLayout)bottomMunelayout.findViewById(R.id.menu_item);
								layout.removeAllViews();
								for(int i=0;i<mlist.size();i++)
								{
									Map<String,String> nmap = mlist.get(i);
									final String name = nmap.get("itemname");
									String keyword = nmap.get("keyword");
									final String type = nmap.get("type");
									final String link = nmap.get("link");
									View muenitemlayout = inflater.inflate(R.layout.bootm_menu_list_item, null);
									TextView text = (TextView)muenitemlayout.findViewById(R.id.menu_txt);
									LinearLayout menulayout = (LinearLayout)muenitemlayout.findViewById(R.id.menu_list_item_btn);
									text.setText(name);
									final String str = keyword;
									menulayout.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											if(type.equals("1"))
											{
												int index = mymessageItem.size();
												bottomMenuWindow.dismiss();
												sendMessageText(str,false,index);
											}
											else
											{
												String newlink = "";
												if(link.indexOf("?") >= 0)
													newlink = link + "&username="+myapp.getUserName();
												else
													newlink = link + "?username="+myapp.getUserName();
												openHtmlView(newlink,name);
//												openHtmlView(link,name);
											}
										}
									});
									layout.addView(muenitemlayout);
								}
								bottomMenuWindow.showAtLocation(MessageListActivity.this.findViewById(R.id.rl_bottom), Gravity.BOTTOM|Gravity.CENTER_VERTICAL, x, y);
							}
						}
					}
				});
			}
			else
			{
				menu_btn2.setVisibility(View.GONE);
			}
			menu_btn3 = (TextView) findViewById(R.id.menu_btn3);
			if(bottom_menu_txt3 != null)
			{
				String name = (String)bottom_menu_txt3.get("name");
				final List<Map<String,String>> mlist = (List<Map<String,String>>)bottom_menu_txt3.get("list");
				menu_btn3.setText(name);
				menu_btn3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mlist != null && mlist.size() > 0)
						{
							boolean b = false;
							if(bottomMenuWindow.isShowing())
							{
								bottomMenuWindow.dismiss();
								b = true;
							}
							if(!munetag.equals("2") || !b)
							{
								munetag = "2";
								int[] location = new int[2];  
								menu_btn3.getLocationOnScreen(location); 
					            int x = menu_btn3.getLeft();  
					            int y = menu_btn3.getBottom()+20;
								LinearLayout layout = (LinearLayout)bottomMunelayout.findViewById(R.id.menu_item);
								layout.removeAllViews();
								for(int i=0;i<mlist.size();i++)
								{
									Map<String,String> nmap = mlist.get(i);
									final String name = nmap.get("itemname");
									String keyword = nmap.get("keyword");
									final String type = nmap.get("type");
									final String link = nmap.get("link");
									View muenitemlayout = inflater.inflate(R.layout.bootm_menu_list_item, null);
									TextView text = (TextView)muenitemlayout.findViewById(R.id.menu_txt);
									LinearLayout menulayout = (LinearLayout)muenitemlayout.findViewById(R.id.menu_list_item_btn);
									text.setText(name);
									final String str = keyword;
									menulayout.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											if(type.equals("1"))
											{
												int index = mymessageItem.size();
												bottomMenuWindow.dismiss();
												sendMessageText(str,false,index);
											}
											else
											{
												String newlink = "";
												if(link.indexOf("?") >= 0)
													newlink = link + "&username="+myapp.getUserName();
												else
													newlink = link + "?username="+myapp.getUserName();
												openHtmlView(newlink,name);
//												openHtmlView(link,name);
											}
										}
									});
									layout.addView(muenitemlayout);
								}
								bottomMenuWindow.showAtLocation(MessageListActivity.this.findViewById(R.id.rl_bottom), Gravity.BOTTOM|Gravity.CENTER_VERTICAL, x, y);
							}
						}
					}
				});
			}
			else
			{
				menu_btn3.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadScrollView()
	{
//		gestureListener = new MYGestureListener(this, null, mViewFlipper); 
		 
		FriendlyScrollView scroll = (FriendlyScrollView) view1.findViewById(R.id.ScrollView01);
		scroll.setOnTouchListener(onTouchListener);
//		scroll.setOnTouchListener(gestureListener);
		scroll.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll2 = (FriendlyScrollView) view2.findViewById(R.id.ScrollView02);
		scroll2.setOnTouchListener(onTouchListener);
//		scroll.setOnTouchListener(gestureListener);
		scroll2.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll3 = (FriendlyScrollView) view3.findViewById(R.id.ScrollView01);
		scroll3.setOnTouchListener(onTouchListener);
//		scroll.setOnTouchListener(gestureListener);
		scroll3.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll4 = (FriendlyScrollView) view4.findViewById(R.id.ScrollView01);
		scroll4.setOnTouchListener(onTouchListener);
//		scroll.setOnTouchListener(gestureListener);
		scroll4.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll5 = (FriendlyScrollView) view5.findViewById(R.id.ScrollView01);
		scroll5.setOnTouchListener(onTouchListener);
//		scroll.setOnTouchListener(gestureListener);
		scroll5.setGestureDetector(mGestureDetector);
	}
	
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		 
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};
	
	public void loadImgGridView()
	{
        LinearLayout tlayout = (LinearLayout)findViewById(R.id.layout_customize_view);
		tlayout.addView(view1);
		
        
        LinearLayout tlayout2 = (LinearLayout)findViewById(R.id.layout_customize_view2);
        tlayout2.addView(view2);
        
        
        LinearLayout tlayout3 = (LinearLayout)findViewById(R.id.layout_customize_view3);
        tlayout3.addView(view3);
        
        
        LinearLayout tlayout4 = (LinearLayout)findViewById(R.id.layout_customize_view4);
        tlayout4.addView(view4);
        
        
        LinearLayout tlayout5 = (LinearLayout)findViewById(R.id.layout_customize_view5);
        tlayout5.addView(view5);
	}
	
	public void loadImgGridView2()
	{
		//获取GridView对象  
        GridView gridview = (GridView) view1.findViewById(R.id.gridview);
        gridview.setNumColumns(5);// 设置每行列数
        gridview.setGravity(Gravity.CENTER);// 位置居中
        gridview.setVerticalSpacing(30);// 垂直间隔
        gridview.setHorizontalSpacing(30);// 水平间隔

//        final EditText contText = (EditText)findViewById(R.id.messagecontent);
        mssageEdit.addTextChangedListener(watcher); 
        mssageEdit.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gridSmilesContainer.setVisibility(View.GONE);
//				imgbtn.setImageResource(R.drawable.content1);
				smilestag = false;
				
			    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   
			    imm.showSoftInput(mssageEdit, 0);
				return false;
			}
		});
        
        final String [] values = {"smiles1","smiles2","smiles3","smiles4","smiles5",
        		"smiles6","smiles7","smiles8","smiles9","smiles10"};
        
        gridview.setAdapter(getGridAdapter(values,imgs));
        
        //事件监听  
        gridview.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                int index = mssageEdit.getSelectionStart();//获取光标所在位置
                
                Editable edit = mssageEdit.getEditableText();//获取EditText的文字
                String imgname = values[position];
                browlist.add("<img src=\""+imgname+"\"/>");
				if (index < 0 || index >= edit.length()) {
					edit.append(Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));
				} else {
					edit.insert(index, Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));// 光标所在位置插入文字
				}
            }  
        }); 
        
//		viewGroup.addView(view1);
		
		//获取GridView对象  
        GridView gridview2 = (GridView) view2.findViewById(R.id.gridview);
        gridview2.setNumColumns(5);// 设置每行列数
        gridview2.setGravity(Gravity.CENTER);// 位置居中
        gridview2.setVerticalSpacing(30);// 垂直间隔
        gridview2.setHorizontalSpacing(30);// 水平间隔


        final String [] values2 = {"smiles11","smiles12","smiles13","smiles14","smiles15",
        		"smiles16","smiles17","smiles18","smiles19","smiles20"};
        
        gridview2.setAdapter(getGridAdapter(values2,imgs2));
        //事件监听  
        gridview2.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
//            	EditText contText = (EditText)findViewById(R.id.messagecontent);
            	int index = mssageEdit.getSelectionStart();//获取光标所在位置
                 
                Editable edit = mssageEdit.getEditableText();//获取EditText的文字
                String imgname = values2[position];
                browlist.add("<img src=\""+imgname+"\"/>");
 				if (index < 0 || index >= edit.length()) {
 					edit.append(Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));
 				} else {
 					edit.insert(index, Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));// 光标所在位置插入文字
 				}
            }  
        });  
        
        
        GridView gridview3 = (GridView) view3.findViewById(R.id.gridview);
        gridview3.setNumColumns(5);// 设置每行列数
        gridview3.setGravity(Gravity.CENTER);// 位置居中
        gridview3.setVerticalSpacing(30);// 垂直间隔
        gridview3.setHorizontalSpacing(30);// 水平间隔


        final String [] values3 = {"smiles21","smiles22","smiles23","smiles24","smiles25","smiles26","smiles27","smiles28","smiles29",
        		"smiles30"};
        
        gridview3.setAdapter(getGridAdapter(values3,imgs3));
        //事件监听  
        gridview3.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
//            	EditText contText = (EditText)findViewById(R.id.messagecontent);
            	int index = mssageEdit.getSelectionStart();//获取光标所在位置
                 
                Editable edit = mssageEdit.getEditableText();//获取EditText的文字
                String imgname = values3[position];
                browlist.add("<img src=\""+imgname+"\"/>");
 				if (index < 0 || index >= edit.length()) {
 					edit.append(Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));
 				} else {
 					edit.insert(index, Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));// 光标所在位置插入文字
 				}
            }  
        });  
        
        
        GridView gridview4 = (GridView) view4.findViewById(R.id.gridview);
        gridview4.setNumColumns(5);// 设置每行列数
        gridview4.setGravity(Gravity.CENTER);// 位置居中
        gridview4.setVerticalSpacing(30);// 垂直间隔
        gridview4.setHorizontalSpacing(30);// 水平间隔


        final String [] values4 = {"smiles31","smiles32","smiles33","smiles34",
        		"smiles35","smiles36","smiles37","smiles38","smiles39",
        		"smiles40"};
        
        gridview4.setAdapter(getGridAdapter(values4,imgs4));
        //事件监听  
        gridview4.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
//            	EditText contText = (EditText)findViewById(R.id.messagecontent);
            	int index = mssageEdit.getSelectionStart();//获取光标所在位置
                 
                Editable edit = mssageEdit.getEditableText();//获取EditText的文字
                String imgname = values4[position];
                browlist.add("<img src=\""+imgname+"\"/>");
 				if (index < 0 || index >= edit.length()) {
 					edit.append(Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));
 				} else {
 					edit.insert(index, Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));// 光标所在位置插入文字
 				}
            }  
        });  
        
        
        GridView gridview5 = (GridView) view5.findViewById(R.id.gridview);
        gridview5.setNumColumns(5);// 设置每行列数
        gridview5.setGravity(Gravity.CENTER);// 位置居中
        gridview5.setVerticalSpacing(30);// 垂直间隔
        gridview5.setHorizontalSpacing(30);// 水平间隔


        final String [] values5 = {"smiles41","smiles42","smiles43","smiles44",
        		"smiles45","smiles46","smiles47","emoji041"};
        
        gridview5.setAdapter(getGridAdapter(values5,imgs5));
        //事件监听  
        gridview5.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
//            	EditText contText = (EditText)findViewById(R.id.messagecontent);
            	int index = mssageEdit.getSelectionStart();//获取光标所在位置
                 
                Editable edit = mssageEdit.getEditableText();//获取EditText的文字
                String imgname = values5[position];
                browlist.add("<img src=\""+imgname+"\"/>");
 				if (index < 0 || index >= edit.length()) {
 					edit.append(Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));
 				} else {
 					edit.insert(index, Html.fromHtml("<img src=\""+imgname+"\"/>", imageGetter, null));// 光标所在位置插入文字
 				}
            }  
        });  
        
	}
	
	public void loadTapeImgGridView()
	{
		//获取GridView对象  
        GridView gridview = (GridView) view1.findViewById(R.id.gridview);
        gridview.setNumColumns(4);// 设置每行列数
        gridview.setGravity(Gravity.CENTER);// 位置居中
        gridview.setVerticalSpacing(20);// 垂直间隔
        gridview.setHorizontalSpacing(50);// 水平间隔

//        final EditText contText = (EditText)findViewById(R.id.messagecontent);
        final String [] values = {"tape1","tape2","tape3","tape4","tape5",
        		"tape6","tape7","tape8"};
        
        gridview.setAdapter(getGridAdapter2(values,tapeimgs1));
        
        //事件监听  
        gridview.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                String imgname = values[position];
                String content = "//"+imgname;
				int index = mymessageItem.size();
				isplay = true;
//				
				sendMessageText2(content,true,index);
            }  
        }); 
        
		
		GridView gridview2 = (GridView) view2.findViewById(R.id.gridview);
        gridview2.setNumColumns(4);// 设置每行列数
        gridview2.setGravity(Gravity.CENTER);// 位置居中
        gridview2.setVerticalSpacing(20);// 垂直间隔
        gridview2.setHorizontalSpacing(50);// 水平间隔

        final String [] values2 = {"tape9","tape10","tape11","tape12","tape13",
        		"tape14","tape15","tape16"};
        
        gridview2.setAdapter(getGridAdapter2(values2,tapeimgs2));
        
        //事件监听  
        gridview2.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                String imgname = values2[position];
                String content = "//"+imgname;
				int index = mymessageItem.size();
				isplay = true;
				
				sendMessageText2(content,true,index);
            }  
        }); 
        
		GridView gridview3 = (GridView) view3.findViewById(R.id.gridview);
        gridview3.setNumColumns(4);// 设置每行列数
        gridview3.setGravity(Gravity.CENTER);// 位置居中
        gridview3.setVerticalSpacing(20);// 垂直间隔
        gridview3.setHorizontalSpacing(50);// 水平间隔

        final String [] values3 = {"tape17","tape18","tape19","tape20","tape21",
        		"tape22","tape23","tape24"};
        
        gridview3.setAdapter(getGridAdapter2(values3,tapeimgs3));
        
        //事件监听  
        gridview3.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                String imgname = values3[position];
                String content = "//"+imgname;
				int index = mymessageItem.size();
				isplay = true;
//				
				sendMessageText2(content,true,index);
            }  
        }); 
        
     
        GridView gridview4 = (GridView) view4.findViewById(R.id.gridview);
        gridview4.setNumColumns(4);// 设置每行列数
        gridview4.setGravity(Gravity.CENTER);// 位置居中
        gridview4.setVerticalSpacing(20);// 垂直间隔
        gridview4.setHorizontalSpacing(50);// 水平间隔

        final String [] values4 = {"tape25","tape26","tape27","tape28","tape29",
        		"tape30","tape31","tape32"};
        
        gridview4.setAdapter(getGridAdapter2(values4,tapeimgs4));
        
        //事件监听  
        gridview4.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                String imgname = values4[position];
                String content = "//"+imgname;
				int index = mymessageItem.size();
				isplay = true;
//				
				sendMessageText2(content,true,index);
            }  
        }); 
        
        GridView gridview5 = (GridView) view5.findViewById(R.id.gridview);
        gridview5.setNumColumns(4);// 设置每行列数
        gridview5.setGravity(Gravity.CENTER);// 位置居中
        gridview5.setVerticalSpacing(20);// 垂直间隔
        gridview5.setHorizontalSpacing(50);// 水平间隔

        final String [] values5 = {"tape33","tape34","tape35","tape36","tape37","tape38","tape39","tape40"};
        
        gridview5.setAdapter(getGridAdapter2(values5,tapeimgs5));
        
        //事件监听  
        gridview5.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                String imgname = values5[position];
                String content = "//"+imgname;
				int index = mymessageItem.size();
				isplay = true;
//				
				sendMessageText2(content,true,index);
            }  
        }); 
	}
	
	public CharSequence getMessageContent(String content)
	{
		CharSequence sp = null;
		try{
			sp = Html.fromHtml(content, imageGetter, null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return sp;
	}
	
	public ImageGetter imageGetter = new ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            int id = MessageListActivity.this.getResources().getIdentifier(MessageListActivity.this.getPackageName()+":drawable/"+source,null,null);
            // 根据id从资源文件中获取图片对象
            Drawable d = getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
	
	private SimpleAdapter getGridAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResourceArray[i]);
//			bitmap = Bitmap.createScaledBitmap(bitmap, 50,50,true);
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemtext", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_grid_menu, new String[] { "itemImage" },
				new int[] { R.id.item_image });
//		SpecialAdapter simperAdapter = new SpecialAdapter(this, data, R.layout.item_menu, new String[] { "itemImage" }, new int[] { R.id.item_image },share,"ico");
		return simperAdapter;
	}
	
	private SimpleAdapter getGridAdapter2(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResourceArray[i]);
//			bitmap = Bitmap.createScaledBitmap(bitmap, 80,80,true);
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemtext", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_grid_menu2, new String[] { "itemImage" },
				new int[] { R.id.item_image });
//		SpecialAdapter simperAdapter = new SpecialAdapter(this, data, R.layout.item_menu, new String[] { "itemImage" }, new int[] { R.id.item_image },share,"ico");
		return simperAdapter;
	}
	
	private TextWatcher watcher = new TextWatcher(){  
        
        @Override  
        public void afterTextChanged(Editable s) {  
            // TODO Auto-generated method stub 3 
        	 System.out.println("s="+s.toString());  
        }  
  
        @Override  
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
            // TODO Auto-generated method stub  1
        	if(count > 0)//减去字符
        	{
        		String str = s.toString();
        		String str2 = str.substring(start,start+count);
        		if(str2.equals("￼"))
        		{
        			int index = 0;
        			//如果字符串中有c
					while (str.indexOf("￼") != -1) {
						int listindex = str.indexOf("￼");
						// 将字符串出现c的位置之前的全部截取掉
						str = str.replaceFirst("￼", "*");
						if (listindex == start) {
							try{
								browlist.remove(index);
							}catch(Exception ex){
								ex.printStackTrace();
							}
							break;
						}
						index++;
					}
        		}
        	}
        	 System.out.println("s="+s.toString()+" start="+start+" before="+count);  
        }  
  
        @Override  
        public void onTextChanged(CharSequence s, int start, int before,  
                int count) {  //2
        	if(count > 0)//加上字符
        	{
        		String str = s.toString();
        		String str2 = str.substring(start,start+count);
        		if(str2.equals("￼"))
        		{
        			int index = 0;
        			//如果字符串中有c
					while (str.indexOf("￼") != -1) {
						int listindex = str.indexOf("￼");
						// 将字符串出现c的位置之前的全部截取掉
						str = str.replaceFirst("￼", "*");
						if (listindex == start) {
							String newadd = browlist.get(browlist.size()-1);
							browlist.remove(browlist.size() - 1);
							browlist.add(index, newadd);
							break;
						}
						index++;
					}
        		}
        	}
//            System.out.println("s="+s.toString()+" start="+start+" before="+count);
        }  
          
    };  
	
	public void openHtmlView(String link,String title)
	{
		Intent intent = new Intent();
	    intent.setClass( this,HtmlWebView.class);
	    Bundle bundle = new Bundle();
		bundle.putString("url", link);
		bundle.putString("title", title);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	private void sendMessageText(String content,boolean b,int index)
	{
		try{
			TextView01.setText(getString(R.string.hotel_label_54));
			String mid = myapp.getCombID();
			if(b)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				newmap = new HashMap<String, Object>();
				newmap.put("mid", mid);
				newmap.put("toid", toname);
				newmap.put("storeid", toname);
				newmap.put("serviceid", serviceid);
				newmap.put("servicename", servicename);
				newmap.put("sname", storeName);
				newmap.put("nameid", nameid);
				newmap.put("mysendname", fromname);
				newmap.put("userimg", myapp.getUserimgbitmap());
				newmap.put("fname", fname);
				newmap.put("tname", tname);
				newmap.put("yiman", R.drawable.yi_man);
				newmap.put("mymessagecontent", content);
				newmap.put("mysendtime",sdf.format(new Date()));
				newmap.put("fileUrl","");
				newmap.put("fileUrl2","");
				newmap.put("toname", toname);
				newmap.put("fileType", "");
				newmap.put("fileType2", "");
				newmap.put("fileName", "");
				newmap.put("fileName2", "");
				newmap.put("time", "");
				newmap.put("timetext", "");
				newmap.put("messagetype", typesMapping);
				newmap.put("sendimg", "1");
				newmap.put("sendprogress", "0");
				newmap.put("storename", "");  
				newmap.put("storeDoc", "");
				newmap.put("storeimg", "");
				newmap.put("storelist", null);
				newmap.put("isRead", "0");
				newmap.put("messagestart", "");
				newmap.put("groupimg", groupimg);
				
				List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
				dlist.add(newmap);
				saveMessageLocal(dlist);
				
				mymessageItem.add(newmap);
				myAdapter.notifyDataSetChanged();
				
				mymessagelistView.setSelection(mymessagelistView.getAdapter().getCount()-1); 
				
				mssageEdit.setText("");
			}
			tempmap.put(mid, index);
			if(typesMapping.equals("friend"))
			{
				sendFriendMessage(content,index,b,mid);
			}
			else if(typesMapping.equals("group"))
			{
				sendFriendGroupMessage(content,index,b,mid);
			}
			else if(typesMapping.equals("qa"))
			{
				sendServiceMessage(content,index,b,mid);
			}
			else
				sendMessage(content,index,b,"text","0",mid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void sendMessageText2(String content,boolean b,int index)
	{
		try{
			TextView01.setText(getString(R.string.hotel_label_54));
			String mid = myapp.getCombID();
			if(b)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				newmap = new HashMap<String, Object>();
				newmap.put("mid", mid);
				newmap.put("toid", toname);
				newmap.put("storeid", toname);
				newmap.put("serviceid", serviceid);
				newmap.put("servicename", servicename);
				newmap.put("sname", storeName);
				newmap.put("nameid", nameid);
				newmap.put("mysendname", fromname);
				newmap.put("userimg", myapp.getUserimgbitmap());
				newmap.put("fname", fname);
				newmap.put("tname", tname);
				newmap.put("yiman", R.drawable.yi_man);
				newmap.put("mymessagecontent", content);
				newmap.put("mysendtime",sdf.format(new Date()));
				newmap.put("fileUrl","");
				newmap.put("fileUrl2","");
				newmap.put("toname", toname);
				newmap.put("fileType", "");
				newmap.put("fileType2", "");
				newmap.put("fileName", "");
				newmap.put("fileName2", "");
				newmap.put("time", "");
				newmap.put("timetext", "");
				newmap.put("messagetype", typesMapping);
				newmap.put("sendimg", "1");
				newmap.put("sendprogress", "0");
				newmap.put("storename", "");  
				newmap.put("storeDoc", "");
				newmap.put("storeimg", "");
				newmap.put("storelist", null);
				newmap.put("isRead", "0");
				newmap.put("messagestart", "");
				newmap.put("groupimg", groupimg);
				newmap.put("isplay", true);
				
				List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
				dlist.add(newmap);
				saveMessageLocal(dlist);
				
				mymessageItem.add(newmap);
				myAdapter.notifyDataSetChanged();
				
				mymessagelistView.setSelection(mymessagelistView.getAdapter().getCount()-1); 
				
//				mssageEdit.setText("");
			}
			tempmap.put(mid, index);
			if(typesMapping.equals("friend"))
			{
				sendFriendMessage(content,index,b,mid);
			}
			else if(typesMapping.equals("group"))
			{
				sendFriendGroupMessage(content,index,b,mid);
			}
			else if(typesMapping.equals("qa"))
			{
				sendServiceMessage(content,index,b,mid);
			}
			else
				sendMessage(content,index,b,"text","0",mid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//为弹出窗口实现监听类
    private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				openImageCamera();
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				break;
			default:
				break;
			}
		}
    };
    
    //为弹出窗口实现监听类
    private OnClickListener  bgitemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			bgmenuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_bg_photo:
				showBackgroundWindo();
				break;
			case R.id.btn_take_photo:
				openImageCamera();
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
//				final Intent bgintent = getPhotoPickIntent();
//	        	startActivityForResult(bgintent, PHOTO_PICKED_WITH_DATA);
//	        	overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				break;
			default:
				break;
			}
		}
    };
    
    public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
		return intent;
	}
    
    public void getLocalMessageListData(final String tag)
    {
    	new Thread() {
			public void run() {
				Message msg = new Message();
				if(tag.equals("refresh"))
					msg.what = 0;
				else
					msg.what = 3;
				
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {
					db.openDB();
					list = db.getMessageData(toname, fromname, page);
					db.closeDB();
					msg.obj = list;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
    }
    
//	public void getMessageListData(final String tag)
//	{
////		showProgressDialog();
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				if(tag.equals("refresh"))
//					msg.what = 0;
//				else
//					msg.what = 3;
//				
//				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//				JSONArray jArr;
//				JSONObject jobj;
//				try {
//					if(isInetnState())
//					{
//						jobj = api.getMyMessageListData(fromname,toname,isWifistart(),isInetnState(),page,tag);
//						if(jobj == null)
//						{
//							msg.obj = null;
//						}
//						else
//						{
//							jArr = (JSONArray) jobj.get("data");
//							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							msg.obj = list;
//						}
//					}
//					else
//					{
//						msg.obj = null;
//					}
//				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
//					ex.printStackTrace();
//				}
//				handler.sendMessage(msg);
//			}
//		}.start();
//	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> list = (List<Map<String, Object>>)msg.obj;
				isnull = false;
				page = 0;
				if(list != null)
				{
					mymessageItem.removeAll(mymessageItem);
					mymessageItem.addAll(list);
					if(list.size() < 20)
						lastsize = list.size();
					else
						lastsize = 0;
					
					lastsize2 = lastsize;
					
//					myAdapter.notifyDataSetChanged();
					myAdapter = new MessageBaseAdapter(MessageListActivity.this, mymessageItem,
							R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,R.layout.mymessagelistnlink,R.layout.mymessagelistnlink2,
							new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress","messagestart"}, 
							new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar,R.id.message_start_txt},
							share,"max",fromname);

					// 添加并且显示
					mymessagelistView.setAdapter(myAdapter);
					
					mymessagelistView.setSelection(mymessagelistView.getCount() - 1);
				}
				else
				{
					myAdapter = new MessageBaseAdapter(MessageListActivity.this, mymessageItem,
							R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,R.layout.mymessagelistnlink,R.layout.mymessagelistnlink2,
							new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress","messagestart"}, 
							new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar,R.id.message_start_txt},
							share,"max",fromname);

					// 添加并且显示
					mymessagelistView.setAdapter(myAdapter);
					
					makeText(getString(R.string.hotel_label_124));
				}
				
				if(myapp.getIsServer())
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
				}
				else
				{
					if(typesMapping.equals("friend") || typesMapping.equals("yanzhenjieguo"))//如果是好友验证和好友信息都在信息栏里显示
					{
						if(HotelMainActivity.instance != null)
							HotelMainActivity.instance.loadeListItemData();
					}
					else
					{
						if(HotelServiceActivity.instance != null)
							HotelServiceActivity.instance.getMyStoreListDatas();
					}
				}
				
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 1:
//				List<Map<String, Object>> newmessagelist = (List<Map<String, Object>>)msg.obj;
				Map<String,Object> smap = (Map<String,Object>)msg.obj;
				boolean start = (Boolean)smap.get("start");
				int rowindex = (Integer)smap.get("index");
				boolean ismuen = (Boolean)smap.get("ismuen");
				String messagestart = (String)smap.get("messagestart");
				if(ismuen)
				{
					View view = myAdapter.getSelectView(rowindex);
					int viewtype = myAdapter.getItemViewType(rowindex);
					if(start)
					{
						TextView01.setText(storeName);
						mssageEdit.setText("");
						mssageEdit.setVisibility(View.VISIBLE);
						
						mymessageItem.get(rowindex).put("sendimg", "1");
						mymessageItem.get(rowindex).put("sendprogress", "1");
//						mymessageItem.get(rowindex).put("messagestart", messagestart);
						myAdapter.setItemSendPressStart(viewtype,view, false);
						
						updateMessageLocal(mymessageItem.get(rowindex));
						
	//					mymessageItem.addAll(newmessagelist);
						myAdapter.notifyDataSetChanged();
					}
					else
					{
						TextView01.setText(storeName);
						myAdapter.setItemSendImgStart(viewtype,view, true,rowindex);
						myAdapter.setItemSendPressStart(viewtype,view, false);
						mymessageItem.get(rowindex).put("sendimg", "0");
						mymessageItem.get(rowindex).put("sendprogress", "1");
//						mymessageItem.get(rowindex).put("messagestart", messagestart);
						updateMessageLocal(mymessageItem.get(rowindex));
						
						myAdapter.notifyDataSetChanged();
						
						mssageEdit.setVisibility(View.VISIBLE);
						
						if(typesMapping.equals("group"))
							makeText(getString(R.string.hotel_label_158));
						else
							makeText(getString(R.string.hotel_label_157));
					}
				}
				break;
			case 2:
				Map<String,Object> vmap = (Map<String,Object>)msg.obj;
				boolean issession = (Boolean)vmap.get("start");
				int indexs = (Integer)vmap.get("index");
				View view2 = myAdapter.getSelectView(indexs);
				int viewtype2 = myAdapter.getItemViewType(indexs);
				if(issession)
				{
//					getMessageListData("refresh");
//					mImageView.setImageBitmap(null);
					mymessageItem.get(indexs).put("sendimg", "1");
					mymessageItem.get(indexs).put("sendprogress", "1");
					myAdapter.setItemSendPressStart(viewtype2,view2, false);
					
					updateMessageLocal(mymessageItem.get(indexs));
					
					myAdapter.notifyDataSetChanged();
					rcChat_popup.setVisibility(View.GONE);
				}
				else
				{
					myAdapter.setItemSendImgStart(viewtype2,view2, true,indexs);
					myAdapter.setItemSendPressStart(viewtype2,view2, false);
					mymessageItem.get(indexs).put("sendimg", "0");
					mymessageItem.get(indexs).put("sendprogress", "1");
					updateMessageLocal(mymessageItem.get(indexs));
					
					myAdapter.notifyDataSetChanged();
					
					if(typesMapping.equals("group"))
						makeText(getString(R.string.hotel_label_158));
					else
						makeText(getString(R.string.hotel_label_157));
				}
				playerend.start();
				break;
			case 3:
				List<Map<String, Object>> list2 = (List<Map<String, Object>>)msg.obj;
				if(list2 != null)
				{
					mymessageItem.addAll(list2);
					
					myAdapter.notifyDataSetChanged();
				}
				else
				{
					makeText(getString(R.string.hotel_label_157));
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 4:
				Map<String,String> map = (Map<String,String>)msg.obj;
				if(map != null)
				{
					String fileUrls = map.get("fileUrl");
					fileUrl = fileUrls;
					String fileNames = map.get("fileName");
					updateMessageImage(fileUrls,fileNames);
				}
				else
					makeText(getString(R.string.attractions_lable_13));
				break;
			case 5:
				Drawable drawable = getImageDrawable(fileUrl);
				main_rlayout.setBackgroundDrawable(drawable);
				Editor editorshare = share.edit();
				editorshare.putString("bg_img", fileUrl);
				editorshare.commit();//提交
				break;
			case 6:
				boolean bf = (Boolean)msg.obj;
				if(bf)
				{
//					getMessageListData("refresh");
					getLocalMessageListData("refresh");
				}
				else
				{
					makeText(getString(R.string.hotel_label_157));
				}
				break;
			case 7:
//				makeText("数据保存本地成功");
				if(myapp.getIsServer())
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					else
						MainTabActivity.instance.loadeListItemData();
				}
				else
				{
					if(typesMapping.equals("friend") || typesMapping.equals("yanzhenjieguo"))//如果是好友验证和好友信息都在信息栏里显示
					{
						if(HotelMainActivity.instance != null)
							HotelMainActivity.instance.loadeListItemData();
						else
							MainTabActivity.instance.loadeListItemData();
					}
					else
					{
						if(HotelServiceActivity.instance != null)
							HotelServiceActivity.instance.getMyStoreListDatas();
						else
							MainTabActivity.instance.loadeServiceListItemData();
					}
				}
				break;
			case 9:
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					if(loadDialog != null)
					{
						loadDialog.setSucceedDialog(getString(R.string.hotel_label_97));
						loadDialog.dismiss();
					}
				}
				else
				{
					if(loadDialog != null)
					{
						loadDialog.setErrorDialog(getString(R.string.hotel_label_98));
						loadDialog.dismiss();
					}
				}
				break;
			case 10:
				String path = (String)msg.obj;
				Map<String, File> files = new HashMap<String, File>();
				File file2 = new File(path);
				files.put(file2.getName(), file2);
				voicendfinal(path,files,file2.getName());
				break;
			case 11:
				Map<String,Object> umap = (Map<String,Object>)msg.obj;
				if(umap != null)
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.getMyCardListData(null);
					groupimg = (String)umap.get("groupids");//群组图片
					String groupName = (String)umap.get("groupName");
					storeName = groupName;
					TextView01.setText(groupName);
				}
				break;
			}
		}
	};
	
//	public List<Map<String,Object>> getMessageDetialData(JSONArray jArr)
//	{
//		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
//		try{
////			for (int i = jArr.length()-1; i >= 0; i--) {
//			for (int i = 0; i < jArr.length(); i++) {
//				JSONObject dobj = (JSONObject) jArr.get(i);
//				
//				String storeid = ""; 
//				if(dobj.has("storeid"))
//					storeid = (String) dobj.get("storeid");
//				
//				String storeNames = ""; 
//				if(dobj.has("storeName"))
//					storeNames = (String) dobj.get("storeName");
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
//					if(content.equals("【图片】") || content.equals("【语音】"))
//						content = "";
//				}
//				
//				String sendTime = ""; 
//				if(dobj.has("sendTime2"))
//				{
//					sendTime = (String)dobj.get("sendTime2");
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
////				String userimg = ""; 
////				if(dobj.has("storeimg"))
////				{
////					userimg = (String)dobj.get("storeimg");
////					if(userimg != null && !userimg.equals(""))
////						userimg = BASE_URL + userimg;
////				}
//				
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("mid", mid);
//				if(!storeid.equals(""))
//					map.put("storeid", storeid);
//				else
//					map.put("storeid", toname);
//				if(!storeNames.equals(""))
//					map.put("sname", storeNames);
//				else
//					map.put("sname", storeName);
//				map.put("nameid", nameid);
//				map.put("toid", toid);
//				map.put("mysendname", sender);
//				if(fromname.equals(sender))
//					map.put("userimg", myapp.getUserimgbitmap());
//				else
//					map.put("userimg", myapp.getStoreimgbitmap());
//				map.put("fname", fname);
//				map.put("tname", tname);
//				map.put("yiman", R.drawable.yi_man);
//				map.put("mymessagecontent", content);
//				map.put("mysendtime",format.format(new Date()));
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
//							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
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
//							List<Map<String,Object>> dlists = getStoreCoupData(jArray);
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
//							List<Map<String,Object>> dlists = getCustomizeData(jArray);
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
//							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
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
//							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
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
//				if(reciver.equals(toname) || sender.equals(toname))//如果不等于当前商家的话需要响铃震动
//				{
//					map.put("isRead", "0");
//				}
//				else
//				{
//					shockAndRingtones();
//					map.put("isRead", "1");
//				}
//				dlist.add(map);
//			}
//			
////			saveMessageLocal(dlist);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return dlist;
//	}
	
	public void saveMessageLocal(final List<Map<String,Object>> dlist)
	{
		try{
			new Thread(){
				public void run(){
					Message msg = new Message();
					msg.what = 8;
					
					db.openDB();
					db.saveMessageData(dlist);
					db.closeDB();
					
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateMessageLocal(final Map<String,Object> map)
	{
		try{
			new Thread(){
				public void run(){
					Message msg = new Message();
					msg.what = 7;
					
					db.openDB();
					db.updateMessageData(map);
					db.closeDB();
					
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void deleteMessageLocal(final String mid)
	{
		try{
			new Thread(){
				public void run(){
					Message msg = new Message();
					msg.what = 8;
					
					db.openDB();
					db.deleteMessageData(mid);
					db.closeDB();
					
					handler.sendMessage(msg);
				}
			}.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static List<Map<String,Object>> getStoreCoupData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String couponName = ""; 
				if(dobj.has("couponName"))
					couponName = (String) dobj.get("couponName");
				
				String couponDesc = ""; 
				if(dobj.has("couponDesc"))
					couponDesc = (String) dobj.get("couponDesc");
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", couponName);
				map.put("desc", couponDesc);
				map.put("price", "");
				map.put("sysImg", sysImg);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public static List<Map<String,Object>> getCustomizeData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String title = ""; 
				if(dobj.has("title"))
					title = (String) dobj.get("title");
				
				String desc = ""; 
				if(dobj.has("desc"))
					desc = (String) dobj.get("desc");
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", title);
				map.put("desc", desc);
				map.put("price", "");
				map.put("sysImg", sysImg);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}

	public List<Map<String,Object>> getRoomDetialData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String roomStyle = ""; 
				if(dobj.has("roomStyle"))
					roomStyle = (String) dobj.get("roomStyle");
				
				String roomDesc = ""; 
				if(dobj.has("roomDesc"))
					roomDesc = (String) dobj.get("roomDesc");
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String roomPrice = ""; 
				if(dobj.has("roomPrice"))
				{
					roomPrice = (String) dobj.get("roomPrice");
					if(!roomPrice.equals(""))
					{
						String [] str = roomPrice.split("\\.");
						roomPrice = str[0] + "￥";
					}
				}
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", roomStyle);
				map.put("desc", roomDesc);
				map.put("price", roomPrice);
				map.put("sysImg", sysImg);
				map.put("pkid", pkid);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public List<Map<String,Object>> getMessageDetialData2(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		if(lastsize > 0)
		{
			for(int j=0;j<lastsize;j++)
			{
				mymessageItem.remove(0);
			}
		}
		try{
//			for (int i = jArr.length()-1; i >= 0; i--) {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String mid = ""; 
				if(dobj.has("mid"))
					mid = (String) dobj.get("mid");
				
				String toid = ""; 
				if(dobj.has("toid"))
					toid = (String) dobj.get("toid");
				
				String sender = ""; 
				if(dobj.has("sender"))
					sender = (String) dobj.get("sender");
				
				String fname = ""; 
				if(dobj.has("fname"))
					fname = (String) dobj.get("fname");
				
				String tname = ""; 
				if(dobj.has("tname"))
					tname = (String) dobj.get("tname");
				
				String reciver = ""; 
				if(dobj.has("reciver"))
					reciver = (String) dobj.get("reciver");
				
				String content = ""; 
				if(dobj.has("content"))
				{
					content = (String) dobj.get("content");
//					if(content.equals("【图片】") || content.equals("【语音】"))
//						content = "";
				}
				
				String sendTime = ""; 
				if(dobj.has("sendTime2"))
				{
					sendTime = (String)dobj.get("sendTime2");
				}
				
				String time = "1"; 
				if(dobj.has("time"))
				{
					time = (String)dobj.get("time");
					if(time.equals(""))
						time = "1";
				}
				
				String fileType = "";
				String fileType2 = "";
				if(dobj.has("fileType"))
				{
					String fileTypes = dobj.getString("fileType");
					if(fileTypes != null && !fileTypes.equals(""))
					{
						String [] filetypes = fileTypes.split(",");
						if(filetypes.length > 1)
						{
							String str = filetypes[0];
							String str2 = filetypes[1];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = str2;
							}
							else
							{
								fileType = str2;
								fileType2 = str;
							}
						}
						else
						{
							String str = filetypes[0];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = "";
							}
							else
							{
								fileType = "";
								fileType2 = str;
							}
						}
					}
				}
				
				String fileName = "";
				String fileName2 = "";
				String fileUrl = ""; 
				String fileUrl2 = "";
				if(dobj.has("fileName"))
				{
					String fileNames = dobj.getString("fileName");
					if(fileNames != null && !fileNames.equals(""))
					{
						String [] filenames = fileNames.split(",");
						if(filenames.length > 1)
						{
							String str = filenames[0];
							String str2 = filenames[1];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = filenames[1];
								fileUrl = BASE_URL + filenames[1];
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = filenames[1];
								fileUrl2 = BASE_URL + filenames[1];
							}
						}
						else
						{
							String str = filenames[0];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = "";
								fileUrl = "";
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = "";
								fileUrl2 = "";
							}
						}
					}
				}
				
				String messagetype = ""; 
				if(dobj.has("messagetype"))
				{
					messagetype = (String)dobj.get("messagetype");
				}
				
				JSONObject job = null;
				if(dobj.has("jsoncontent"))
				{
					job = dobj.getJSONObject("jsoncontent");
				}
				
//				String userimg = ""; 
//				if(dobj.has("storeimg"))
//				{
//					userimg = (String)dobj.get("storeimg");
//					if(userimg != null && !userimg.equals(""))
//						userimg = BASE_URL + userimg;
//				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mid", mid);
				map.put("storeid", toname);
				map.put("sname", storeName);
				map.put("nameid", nameid);
				map.put("toid", toid);
				map.put("mysendname", sender);
				if(fromname.equals(sender))
					map.put("userimg", myapp.getUserimgbitmap());
				else
					map.put("userimg", myapp.getStoreimgbitmap());
				map.put("fname", fname);
				map.put("tname", tname);
				map.put("yiman", R.drawable.yi_man);
				map.put("mymessagecontent", content);
				map.put("mysendtime",sendTime);
				map.put("fileUrl",fileUrl);
				map.put("fileUrl2",fileUrl2);
				map.put("toname", reciver);
				map.put("fileType", fileType);
				map.put("fileType2", fileType2);
				map.put("fileName", fileName);
				map.put("fileName2", fileName2);
				map.put("time", time);
				map.put("timetext", time+"″");
				map.put("messagetype", messagetype);
				map.put("sendimg", "1");
				map.put("sendprogress", "1");
				map.put("isRead", "0");
				if(job != null)
				{
					if(messagetype.equals("fj"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						map.put("storeimg", BASE_URL+storeimg);
						map.put("storename", storename);
						map.put("storeDoc", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("storelist", null);
						}
					}
					else if(messagetype.equals("js"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String storeDoc = job.getString("storeDoc");
						map.put("storeimg", BASE_URL+storeimg);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
					}
					else if(messagetype.equals("yh"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						map.put("storeimg", BASE_URL+storeimg);
						map.put("storename", storename);
						map.put("storeDoc", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getStoreCoupData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("storelist", null);
						}
					}
					else if(messagetype.equals("dz"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String addressInfomation = job.getString("addressInfomation");
						String longItude = job.getString("longItude");
						String woof = job.getString("woof");
						String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x250&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
						map.put("storeimg", url);
						map.put("storename", addressInfomation);
						map.put("storeDoc", null);
						map.put("storelist", null);
					}
					else if(messagetype.equals("zdy"))
					{
						String storeimg = job.getString("img");
						String storeDoc = job.getString("answer");
						map.put("storeimg", BASE_URL+storeimg);
						map.put("storename", "");
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
					}
				}
				
				dlist.add(map);
				mymessageItem.add(0, map);
			}
			
			saveMessageLocal(dlist);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public void sendMessage(final String content,final int indexs,final boolean ismuen,final String filetype,final String time,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
						List<Map<String,Object>> list = myapp.getMyCardsAll();
						final Map map = list.get(Integer.valueOf(index));
						Integer points = (Integer)map.get("points");
						
						jobj = api.sendAutomaticMessage(fromname,toname,fname,tname,toneetyid,"2",content,points,sessionid,isWifistart(),isInetnState(),filetype,time,mid);
						if(jobj == null)
						{
//							String messagestart = "";
//							if(jobj.has("isonline"))
//								messagestart = jobj.getString("isonline");
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", false);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", messagestart);
							msg.obj = smap;
						}
						else
						{
							jArr = (JSONArray) jobj.get("data");
							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							list = myapp.getMessageDetialData(jArr, toname, storeName, nameid, fromname,false);
//							msg.obj = list;
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", true);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", "");
							msg.obj = smap;
						}
					}
					else
					{
						Map<String,Object> smap = new HashMap<String,Object>();
						smap.put("start", false);
						smap.put("index", indexs);
						smap.put("ismuen", ismuen);
//						smap.put("messagestart", "");
						msg.obj = smap;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendFriendMessage(final String content,final int indexs,final boolean ismuen,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
//						List<Map<String,Object>> list = myapp.getMyCardsAll();
//						final Map map = list.get(Integer.valueOf(index));
//						Integer points = (Integer)map.get("points");
						
						jobj = api.sendFriendsMessage(fromname,toname,fname,tname,toneetyid,"2",content,0,sessionid,isWifistart(),isInetnState(),mid);
						if(jobj == null)
						{
//							String messagestart = "";
//							if(jobj.has("isonline"))
//								messagestart = jobj.getString("isonline");
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", false);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", messagestart);
							msg.obj = smap;
						}
						else
						{
//							jArr = (JSONArray) jobj.get("data");
//							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							msg.obj = list;
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", true);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", "");
							msg.obj = smap;
						}
					}
					else
					{
						Map<String,Object> smap = new HashMap<String,Object>();
						smap.put("start", false);
						smap.put("index", indexs);
						smap.put("ismuen", ismuen);
//						smap.put("messagestart", "");
						msg.obj = smap;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendFriendGroupMessage(final String content,final int indexs,final boolean ismuen,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
//						List<Map<String,Object>> list = myapp.getMyCardsAll();
//						final Map map = list.get(Integer.valueOf(index));
//						Integer points = (Integer)map.get("points");
						
						jobj = api.sendFriendGroupMessage(fromname,toname,fname,tname,toneetyid,"2",content,0,sessionid,isWifistart(),isInetnState(),mid);
						if(jobj == null)
						{
//							String messagestart = "";
//							if(jobj.has("isonline"))
//								messagestart = jobj.getString("isonline");
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", false);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", messagestart);
							msg.obj = smap;
						}
						else
						{
//							jArr = (JSONArray) jobj.get("data");
//							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							msg.obj = list;
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", true);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", "");
							msg.obj = smap;
						}
					}
					else
					{
						Map<String,Object> smap = new HashMap<String,Object>();
						smap.put("start", false);
						smap.put("index", indexs);
						smap.put("ismuen", ismuen);
//						smap.put("messagestart", "");
						msg.obj = smap;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendServiceMessage(final String content,final int indexs,final boolean ismuen,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
//						List<Map<String,Object>> list = myapp.getMyCardsAll();
//						final Map map = list.get(Integer.valueOf(index));
//						Integer points = (Integer)map.get("points");
						
						jobj = api.sendServiceMessage(serviceid,toname,servicename,tname,toneetyid,"2",content,0,sessionid,isWifistart(),isInetnState(),watag,mid,fromname);
						if(jobj == null)
						{
//							String messagestart = "";
//							if(jobj.has("isonline"))
//								messagestart = jobj.getString("isonline");
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", false);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", messagestart);
							msg.obj = smap;
						}
						else
						{
//							jArr = (JSONArray) jobj.get("data");
//							SimpleDateFormat sy = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//							list = getMessageDetialData(jArr);
//							msg.obj = list;
							Map<String,Object> smap = new HashMap<String,Object>();
							smap.put("start", true);
							smap.put("index", indexs);
							smap.put("ismuen", ismuen);
//							smap.put("messagestart", "");
							msg.obj = smap;
						}
					}
					else
					{
						Map<String,Object> smap = new HashMap<String,Object>();
						smap.put("start", false);
						smap.put("index", indexs);
						smap.put("ismuen", ismuen);
//						smap.put("messagestart", "");
						msg.obj = smap;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendMessageVoice(final Map<String, File> files,final int time,final int indexs,final String messagetype,final String filetype,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				boolean b = false;
				long filesize = 0;
				try {
					if(isInetnState())
					{
//						File f = new File(fileUrl);
//						if (f.exists()) {
//							FileInputStream fis = null;
//							fis = new FileInputStream(f);
//							filesize = fis.available();
//						} else {
//							f.createNewFile();
//							System.out.println("文件不存在");
//						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("fromname", fromname);
						params.put("toname", toname);
						params.put("fname", fname);
						params.put("tname", tname);
						params.put("toneetyid", toneetyid);
						params.put("sendstatus", "2");
						params.put("content", "【"+getString(R.string.button_recordAudio)+"】");
						params.put("messagetype", messagetype);
						params.put("filetype", filetype);
						params.put("time", String.valueOf(time));
						params.put("mid", mid);
//						b = api.uploadFiles(sessionid,fileUrl,voiceName,fromName,"2","",filesize);
						if(typesMapping.equals("friend"))
							b = api.uploadFilesmssage(params,files,sessionid);
						else if(typesMapping.equals("group"))
							b = api.uploadFilesGroupmssage(params,files,sessionid);
					}
					Map<String,Object> smap = new HashMap<String,Object>();
					smap.put("index", indexs);
					smap.put("start", b);
					msg.obj = smap;
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendServiceMessageVoice(final Map<String, File> files,final int time,final int indexs,final String messagetype,final String filetype,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				boolean b = false;
				long filesize = 0;
				try {
					if(isInetnState())
					{
//						File f = new File(fileUrl);
//						if (f.exists()) {
//							FileInputStream fis = null;
//							fis = new FileInputStream(f);
//							filesize = fis.available();
//						} else {
//							f.createNewFile();
//							System.out.println("文件不存在");
//						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("fromname", serviceid);
						params.put("toname", toname);
						params.put("fname", servicename);
						params.put("tname", tname);
						params.put("toneetyid", toneetyid);
						params.put("sendstatus", "2");
						params.put("content", "【"+getString(R.string.button_recordAudio)+"】");
						params.put("messagetype", messagetype);
						params.put("filetype", filetype);
						params.put("time", String.valueOf(time));
						params.put("watag", watag);
						params.put("mid", mid);
						params.put("formid", fromname);
//						b = api.uploadFiles(sessionid,fileUrl,voiceName,fromName,"2","",filesize);
						b = api.uploadFilesmssage(params,files,sessionid);
					}
					Map<String,Object> smap = new HashMap<String,Object>();
					smap.put("index", indexs);
					smap.put("start", b);
					msg.obj = smap;
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendAutomaticMessageVoice(final Map<String, File> files,final int time,final int indexs,final String messagetype,final String filetype,final String mid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				boolean b = false;
				long filesize = 0;
				try {
					if(isInetnState())
					{
//						File f = new File(fileUrl);
//						if (f.exists()) {
//							FileInputStream fis = null;
//							fis = new FileInputStream(f);
//							filesize = fis.available();
//						} else {
//							f.createNewFile();
//							System.out.println("文件不存在");
//						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("fromname", fromname);
						params.put("toname", toname);
						params.put("fname", fname);
						params.put("tname", tname);
						params.put("toneetyid", toneetyid);
						params.put("sendstatus", "2");
						params.put("content", "【"+getString(R.string.button_recordAudio)+"】");
						params.put("messagetype", messagetype);
						params.put("filetype", filetype);
						params.put("time", String.valueOf(time));
						params.put("mid", mid);
//						b = api.uploadFiles(sessionid,fileUrl,voiceName,fromName,"2","",filesize);
						b = api.uploadFilesAutomaticMssage(params,files,sessionid);
					}
					Map<String,Object> smap = new HashMap<String,Object>();
					smap.put("index", indexs);
					smap.put("start", b);
					msg.obj = smap;
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void openImageMenu()
	{
		final CharSequence [] items = { "本地图片" , "拍摄照片"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("图片菜单");
		builder.setItems ( items , new DialogInterface.OnClickListener () {
		    public void onClick ( DialogInterface dialog , int item ) {
		        if(item == 0)
		        {
		        	Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
		        }
		        else if(item == 1)
		        {
		        	openImageCamera();
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
//		values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
		values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		//这样就将文件的存储方式和uri指定到了Camera应用中

		//由于我们需要调用完Camera后，可以返回Camera获取到的图片，
		//所以，我们使用startActivityForResult来启动Camera
		startActivityForResult(intent, 1);
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
			overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			break;
		case 1:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent2, 1);
			overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			break;
		case 2:
			Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	startActivityForResult(intent3, 1);
        	overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			break;
		case 3:
			fileUrl = "";
//			mImageView.setImageBitmap(null);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			mssageEdit.setText(matches.get(0));
		}
		else if (resultCode == RESULT_OK) {
//			if(pictag.equals("bgimg"))
//			{
//				final Bitmap photo = data.getParcelableExtra("data");  
//	            // 下面就是显示照片了  
//	            System.out.println(photo);  
//	            //缓存用户选择的图片  
////	            img = getBitmapByte(photo);  
////	            mEditor.setPhotoBitmap(photo);
//	            try{
//	                PHOTO_DIR.mkdirs();// 创建照片的存储目录
//	                File myCaptureFile = new File( PHOTO_DIR,"mylog.jpg");
//	                BufferedOutputStream bos = new BufferedOutputStream(
//	                                                         new FileOutputStream(myCaptureFile));
//	                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//	                bos.flush();
//	                bos.close();
//	                fileUrl = myCaptureFile.getPath();
//	                loadbgImageData();
//	            }catch(Exception ex){
//	            	ex.printStackTrace();
//	            }
//			}
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(uri, null, null, null, null);
					String imageSize = "";
					// 查询本地图片库图片的字段
					// String[] str = cursor.getColumnNames();
					// for(int i=0; i<str.length; i++){
					// Log.i("ColumNames", str[i]);
					// }
					// 得到本地图片库中图片的 id、路径、大小、文件名
					while (cursor.moveToNext()) {
						Log.i("====_id", cursor.getString(0) + "");
						Log.i("====_path", cursor.getString(1) + "");
						fileUrl = cursor.getString(1);
						Log.i("====_size", cursor.getString(2) + "");
						imageSize = cursor.getString(2);
						Log.i("====_display_name", cursor.getString(3) + "");
						fileName = cursor.getString(3);
					}
					try {
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}

						Log.i("===============imgSize======", imageSize);
						fileSize = Long.valueOf(imageSize);
						if(pictag.equals("uploadimg"))
							loadImageData();
						else
							loadbgImageData();
//						bitmap = zoomImage(bitmap, Long.valueOf(imageSize),
//								fileUrl);
////						bitmap = zoomImage(bitmap, 800, 420);
////						mImageView.setImageBitmap(bitmap);
//						/** 
//						 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
//						 */  
//						int degree = readPictureDegree(fileUrl); 
//						/** 
//						 * 把图片旋转为正的方向 
//						 */  
//						bitmap = rotaingImageView(degree, bitmap);  
//						saveMyBitmap(fileUrl,bitmap);
//						bitmap.recycle();
//						updateMessageImage(fileUrl,fileName);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}
					}
				} else {
					Bitmap bmp = (Bitmap) data.getExtras().get("data");
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

					// fileUrl = data.getData().getPath();
					bitmap = zoomImage(bmp, 80, 80);
//					mImageView.setImageBitmap(bitmap);
				}

			} else {
				if (uri != null) {
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(uri, null, null, null, null);
					String imageSize = "";
					// 查询本地图片库图片的字段
					// String[] str = cursor.getColumnNames();
					// for(int i=0; i<str.length; i++){
					// Log.i("ColumNames", str[i]);
					// }
					// 得到本地图片库中图片的 id、路径、大小、文件名
					while (cursor.moveToNext()) {
						Log.i("====_id", cursor.getString(0) + "");
						Log.i("====_path", cursor.getString(1) + "");
						fileUrl = cursor.getString(1);
						Log.i("====_size", cursor.getString(2) + "");
						imageSize = cursor.getString(2);
						Log.i("====_display_name", cursor.getString(3) + "");
						fileName = cursor.getString(3);

						if (imageSize.equals("0")) {
							try {
								File dF = new File(fileUrl);
								FileInputStream fis = new FileInputStream(dF);
								int fileLen = fis.available(); // 这就是文件大小
								imageSize = String.valueOf(fileLen);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					try {
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}

						Log.i("===============imgSize======", imageSize);
						fileSize = Long.valueOf(imageSize);
						if(pictag.equals("uploadimg"))
							loadImageData();
						else
							loadbgImageData();
//						bitmap = zoomImage(bitmap, Long.valueOf(imageSize),
//								fileUrl);
////						bitmap = zoomImage(bitmap, 800, 420);
////						// Bitmap bmp = (Bitmap)data.getExtras().get("data");
////						mImageView.setImageBitmap(bitmap);
////						FileInputStream fis = new FileInputStream(fileUrl);
////				        bitmap = BitmapFactory.decodeStream(fis);
//						/** 
//						 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
//						 */  
//						int degree = readPictureDegree(fileUrl); 
//						/** 
//						 * 把图片旋转为正的方向 
//						 */  
//						bitmap = rotaingImageView(degree, bitmap);  
//						saveMyBitmap(fileUrl,bitmap);
//						bitmap.recycle();
//						updateMessageImage(fileUrl,fileName);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}
					}
				}
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void loadImageData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 4;
				
				try {
					Bitmap bitmap = null;
					bitmap = zoomImage(bitmap, Long.valueOf(fileSize),fileUrl);
					bitmap = adaptive(bitmap, myapp.getScreenWidth(), myapp.getScreenHeight());
					Bitmap iconbitmap = adaptive(bitmap);
					/** 
					 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
					 */  
					int degree = readPictureDegree(fileUrl);
					/** 
					 * 把图片旋转为正的方向 
					 */  
					bitmap = rotaingImageView(degree, bitmap); 
					iconbitmap = rotaingImageView(degree, iconbitmap); 
					fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
					String [] names = fileName.split("\\.");
					UUID uuid = UUID.randomUUID();
					String fileName = "";
					if(names.length < 2)
					{
						fileName = uuid.toString() + ".jpg";
						filetype = "jpg";
					}
					else
					{
						fileName = uuid.toString() + "." + names[1];
						filetype = names[1];
					}
//					fileUrl = fileUrl + "upload_temp_img." + names[1];
					String fileUrl = fileUtil.getImage2File1bPath(toname, uuid.toString());
					String iconfileUrl = fileUtil.getImage2File1aPath(toname, uuid.toString());
					saveMyBitmap(fileUrl,bitmap,iconfileUrl,iconbitmap);
					bitmap.recycle();
					
					Map<String,String> map = new HashMap<String,String>();
					map.put("fileUrl", fileUrl);
					map.put("fileName", fileName);
					
					msg.obj = map;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadbgImageData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 5;
				
				try {
					Bitmap bitmap = null;
					bitmap = zoomImage(bitmap, 0,fileUrl);
					bitmap = zoomImage2(bitmap, 420, 800);
					/** 
					 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
					 */  
					int degree = readPictureDegree(fileUrl);
					/** 
					 * 把图片旋转为正的方向 
					 */  
					bitmap = rotaingImageView(degree, bitmap);  
					fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
					String [] names = fileName.split("\\.");
					fileUrl = fileUrl + "bg_temp_img." + names[1];
					saveMyBitmap(fileUrl,bitmap);
					bitmap.recycle();
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(bitName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveMyBitmap(String bitName, Bitmap mBitmap,String iconfileUrl,Bitmap iconbitmap) {
		File f = new File(bitName);
		File f2 = new File(iconfileUrl);
		try {
			f.createNewFile();
			f2.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		FileOutputStream fOut = null;
		FileOutputStream fOut2 = null;
		try {
			fOut = new FileOutputStream(f);
			fOut2 = new FileOutputStream(f2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//		iconbitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut2);
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
		iconbitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut2);
		try {
			fOut.flush();
			fOut2.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
			fOut2.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public void updateMessageImage(String fileUrl,String filename)
	{
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Map<String, Object> map = new HashMap<String, Object>();
			String mid = myapp.getCombID();
			map.put("mid", mid);
			map.put("toid", toname);
			map.put("storeid", toname);
			map.put("serviceid", serviceid);
			map.put("servicename", servicename);
			map.put("sname", storeName);
			map.put("nameid", nameid);
			map.put("mysendname", fromname);
			map.put("userimg", myapp.getUserimgbitmap());
			map.put("fname", fname);
			map.put("tname", tname);
			map.put("yiman", R.drawable.yi_man);
			map.put("mymessagecontent", "【"+getString(R.string.button_takephoto)+"】");
			map.put("mysendtime",sdf.format(new Date()));
			map.put("fileUrl",fileUrl);
			map.put("fileUrl2","");
			map.put("toname", toname);
			map.put("fileType", "image/png");
			map.put("fileType2", "");
			map.put("fileName", filename);
			map.put("fileName2", "");
			map.put("time", "");
			map.put("timetext", "");
			map.put("messagetype", typesMapping);
			map.put("sendimg", "1");
			map.put("sendprogress", "0");
			map.put("storename", "");  
			map.put("storeDoc", "");
			map.put("storeimg", "");
			map.put("storelist", null);
			map.put("isRead", "0");
			map.put("messagestart", "");
			map.put("groupimg", groupimg);
			
			mymessageItem.add(map);
			myAdapter.notifyDataSetChanged();
			
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			dlist.add(map);
			saveMessageLocal(dlist);
			
			mymessagelistView.setSelection(mymessagelistView.getAdapter().getCount()-1); 
			
//			sendMessagess();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void sendMessagess(int rowindex,String mid)
	{
		try{
			tempmap.put(mid, rowindex);
			if(isInetnState())
			{
				if(typesMapping.equals("qa"))
				{
					mymessagelistView.setSelection(mymessagelistView.getCount() - 1);
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(serviceid, toname, servicename, tname,toneetyid, "2", "【"+getString(R.string.button_takephoto)+"】", filetype, typesMapping,watag,mid,fromname);
					String urlstr = BASE_URL2+ "sendSpackMessageFile;jsessionid="+ myapp.getSessionId() + "?" + pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext());
					task.setPlayout(myAdapter.getPlayout());
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
				else if(typesMapping.equals("friend") || typesMapping.equals("group"))
				{
					mymessagelistView.setSelection(mymessagelistView.getCount()-1);
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(fromname,toname,fname,tname,toneetyid,"2","【"+getString(R.string.button_takephoto)+"】",filetype,typesMapping,watag,mid,"");
					String urlstr = BASE_URL2 + "sendSpackMessageFile;jsessionid="+myapp.getSessionId()+"?"+pstr;
					if(typesMapping.equals("group"))
						urlstr = BASE_URL2 + "sendSpackGroupMessageFile;jsessionid="+myapp.getSessionId()+"?"+pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext());
					task.setPlayout(myAdapter.getPlayout());
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
				else
				{
					mymessagelistView.setSelection(mymessagelistView.getCount()-1);
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(fromname,toname,fname,tname,toneetyid,"2","【"+getString(R.string.button_takephoto)+"】",filetype,typesMapping,watag,mid,"");
					String urlstr = BASE_URL2 + "sendAutomaticMessage;jsessionid="+myapp.getSessionId()+"?"+pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext());
					task.setPlayout(myAdapter.getPlayout());
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
			}
			else
			{
				View view = myAdapter.getSelectView(rowindex);
				int viewtype = myAdapter.getItemViewType(rowindex);
				mymessageItem.get(rowindex).put("sendimg", "0");
				mymessageItem.get(rowindex).put("sendprogress", "1");
				updateMessageLocal(mymessageItem.get(rowindex));
				
				myAdapter.getPlayout().setVisibility(View.GONE);
				myAdapter.setItemSendImgStart(viewtype,view, true,rowindex);
				myAdapter.setItemSendPressStart(viewtype,view, false);
				mssageEdit.setVisibility(View.VISIBLE);
				myAdapter.notifyDataSetChanged();
				makeText("连接超时，可能你已经断网，请检查网咯连接！");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void sendMessagess(int rowindex,String fileUrl,String filetype,String mid)
	{
		try{
			tempmap.put(mid, rowindex);
			if(isInetnState())
			{
				View view = myAdapter.getSelectView(rowindex);
				int viewtype = myAdapter.getItemViewType(rowindex);
				myAdapter.setItemImageLayout(viewtype,view);
				
				Map<String,Object> map = myAdapter.getImageWH(rowindex);
				if(map != null && map.containsKey("with"))
				{
					int with = (Integer)map.get("with");
					int high = (Integer)map.get("high");
					LinearLayout imglayout = myAdapter.getPlayout(viewtype,view);
					imglayout.setLayoutParams(new RelativeLayout.LayoutParams(with, high));
					LayoutParams lp = imglayout.getLayoutParams();
			        lp.width=with;
			        lp.height=high;        
			        imglayout.setLayoutParams(lp);
				}
		        
//				mymessagelistView.setSelection(mymessagelistView.getCount()-1);
				if(typesMapping.equals("qa"))
				{
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(serviceid,toname,servicename,tname,toneetyid,"2","【"+getString(R.string.button_takephoto)+"】",filetype,typesMapping,watag,mid,fromname);
					String urlstr = BASE_URL2 + "sendSpackMessageFile;jsessionid="+myapp.getSessionId()+"?"+pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext(viewtype,view));
					task.setPlayout(myAdapter.getPlayout(viewtype,view));
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
				else if(typesMapping.equals("friend"))
				{
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(fromname,toname,fname,tname,toneetyid,"2","【"+getString(R.string.button_takephoto)+"】",filetype,typesMapping,watag,mid,"");
					String urlstr = BASE_URL2 + "sendSpackMessageFile;jsessionid="+myapp.getSessionId()+"?"+pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext(viewtype,view));
					task.setPlayout(myAdapter.getPlayout(viewtype,view));
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
				else
				{
					HttpMultipartPost task = new HttpMultipartPost(this);
					task.setPath(fileUrl);
					String pstr = api.addParam(fromname,toname,fname,tname,toneetyid,"2","【"+getString(R.string.button_takephoto)+"】",filetype,typesMapping,watag,mid,"");
					String urlstr = BASE_URL2 + "sendAutomaticMessage;jsessionid="+myapp.getSessionId()+"?"+pstr;
					task.setUrl(urlstr);
					task.setPtext(myAdapter.getPtext());
					task.setPlayout(myAdapter.getPlayout());
					task.setRowindex(rowindex);
					task.setMyapp(myapp);
					task.execute();
				}
			}
			else
			{
				updateImageUploadStart(false,rowindex);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateImageUploadStart(boolean start,int rowindex)
	{
		View view = myAdapter.getSelectView(rowindex);
		int viewtype = myAdapter.getItemViewType(rowindex);
		if(start)
		{
			mymessageItem.get(rowindex).put("sendimg", "1");
			mymessageItem.get(rowindex).put("sendprogress", "1");
			myAdapter.setItemSendPressStart(viewtype,view, false);
			myAdapter.setItemSendImgStart(viewtype,view, false,rowindex);

			updateMessageLocal(mymessageItem.get(rowindex));
			
			myAdapter.notifyDataSetChanged();
		}
		else
		{
			myAdapter.setItemSendImgStart(viewtype,view, true,rowindex);
			myAdapter.setItemSendPressStart(viewtype,view, false);
			
			mymessageItem.get(rowindex).put("sendimg", "0");
			mymessageItem.get(rowindex).put("sendprogress", "1");
			
			updateMessageLocal(mymessageItem.get(rowindex));
			
			myAdapter.notifyDataSetChanged();
			myAdapter.getPlayout(viewtype,view).setVisibility(View.GONE);
			if(typesMapping.equals("group"))
				makeText(getString(R.string.hotel_label_158));
			else
				makeText(getString(R.string.hotel_label_157));
		}
	}
	
	public void messageSend(final String content,final String mto,final String sendstatus)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 6;
				boolean tag = true;
				try{
					Map<String, String> params = new HashMap<String, String>();
					params.put("content", content);
					params.put("mto", mto);
					params.put("sendstatus", sendstatus);
					long fsize = 0;
					Map<String, File> files = new HashMap<String, File>();
					FormFile [] fromFiles = {};
					if(fileUrl != null && !fileUrl.equals(""))
					{
						File file = new File(fileUrl);
						long filesize = file.length();
						if(filesize > 358400) //filesize / 1024得到文件KB单位大小
						{
//							makeText(this.getString(R.string.image_sizeerroe_lable));
							tag = false;
						}
						fsize = fsize + filesize;
//						makeText("filesize==="+filesize);
						files.put(file.getName(), file);
		//	            FormFile formfile = new FormFile(file.getName(), file, "image", "image/jpeg");
		//	            fromFiles[0] = formfile;
					}
					
					boolean bf = false;
					if(tag)
					{
						bf = api.uploadFiles(params,files,sessionid);
					}
					msg.obj = bf;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void showMessageDetails(String flieUrl,String messages){
		try{
			final View view = LayoutInflater.from(this).inflate(R.layout.messageview,null);
			ImageView imgview = (ImageView)view.findViewById(R.id.messageImage);
			Bitmap bitmap = U.returnBitMap(flieUrl);
			imgview.setImageBitmap(bitmap);
//			TextView tview = (TextView)view.findViewById(R.id.messageInfo);
//			tview.setText(message);
			
			final AlertDialog adialog = new AlertDialog.Builder(this).setTitle("Image").setView(view).setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMessageDetails(String flieUrls){
		try{
			final View view = LayoutInflater.from(this).inflate(R.layout.messageview,null);
			ImageView imgview = (ImageView)view.findViewById(R.id.messageImage);
			Bitmap bitmap = U.getImageBitmap(flieUrls);
			imgview.setImageBitmap(bitmap);
//			TextView tview = (TextView)view.findViewById(R.id.messageInfo);
//			tview.setText(message);
			
			final AlertDialog adialog = new AlertDialog.Builder(this).setView(view).show();
			
			imgview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					adialog.dismiss();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getSMSMyMessage()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for(int i=0;i<smsMessageAll.size();i++)
			{
				Map<String,Object> map = smsMessageAll.get(i);
				String sendName = (String)map.get("mysendname");
				String toName = (String)map.get("toname");
				if(fromName.equals(sendName))
				{
					list.add(map);
				}
				else if(fromName.equals(toName))
				{
					list.add(map);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void smsMyMessage(JSONObject dobj)
	{
		try{
			String sender = ""; 
			if(dobj.has("sender"))
				sender = (String) dobj.get("sender");
			
			String reciver = ""; 
			if(dobj.has("reciver"))
				reciver = (String) dobj.get("reciver");
			
			String content = ""; 
			if(dobj.has("content"))
				content = (String) dobj.get("content");
			
			String sendTime = ""; 
			if(dobj.has("sendTime2"))
			{
				sendTime = (String)dobj.get("sendTime2");
			}
			
			String fileUrl = ""; 
			if(dobj.has("fileUrl"))
			{
				fileUrl = (String)dobj.get("fileUrl");
				if(fileUrl != null && !fileUrl.equals(""))
				{
					fileUrl = BASE_URL + fileUrl;
				}
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mysendname", sender);
			map.put("yiman", R.drawable.yi_man);
			map.put("mymessagecontent", content);
			map.put("mysendtime",sendTime);
			map.put("fileUrl",fileUrl);
			
			smsMessageAll = myapp.getSmsMessageAll();
			if(smsMessageAll == null)
				smsMessageAll = new ArrayList<Map<String,Object>>();
			smsMessageAll.add(map);
			myapp.setSmsMessageAll(smsMessageAll);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void spackButton()
	{
		final ImageView sbtn = (ImageView) findViewById(R.id.mymassagespackButton);
		sbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					startVoiceRecognitionActivity();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	public void showVoiceLayout()
	{
		try{
//			mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
			content_layout = (LinearLayout)findViewById(R.id.content_layout);
			text_panel_ll = (LinearLayout)findViewById(R.id.text_panel_ll);
			mBtnRcd = (Button) findViewById(R.id.btn_rcd);
			chatting_mode_btn = (ImageButton) findViewById(R.id.mymassagespackButton);
			
			//语音文字切换按钮
			chatting_mode_btn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					if (btn_vocie) {
						mBtnRcd.setVisibility(View.GONE);
						text_panel_ll.setVisibility(View.VISIBLE);
						btn_vocie = false;
						chatting_mode_btn.setImageDrawable(null);
						chatting_mode_btn.setImageDrawable(getResources().getDrawable(R.drawable.chatting_setmode_msg_btn));
					} else {
						mBtnRcd.setVisibility(View.VISIBLE);
						text_panel_ll.setVisibility(View.GONE);
						chatting_mode_btn.setImageDrawable(null);
						chatting_mode_btn.setImageDrawable(getResources().getDrawable(R.drawable.chatting_setmode_voice_btn));
						btn_vocie = true;
					}
				}
			});
			mBtnRcd.setOnTouchListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					//按下语音录制按钮时返回false执行父类OnTouch
//					player.start();
					if (!Environment.getExternalStorageDirectory().exists()) {
						Toast.makeText(MessageListActivity.this, "No SDCard", Toast.LENGTH_LONG).show();
						return false;
					}

					if (btn_vocie) {
						System.out.println("1");
						int[] location = new int[2];
						mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
						int btn_rc_Y = location[1];
						int btn_rc_X = location[0];
						int[] del_location = new int[2];
						del_re.getLocationInWindow(del_location);
						int del_Y = del_location[1];
						int del_x = del_location[0];
						if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
							if (!Environment.getExternalStorageDirectory().exists()) {
								Toast.makeText(MessageListActivity.this, "No SDCard", Toast.LENGTH_LONG).show();
								return false;
							}
							System.out.println("2");
//							if (event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
								System.out.println("3");
								player.start();
								mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
								rcChat_popup.setVisibility(View.VISIBLE);
								voice_rcd_hint_loading.setVisibility(View.VISIBLE);
								voice_rcd_hint_rcding.setVisibility(View.GONE);
								voice_rcd_hint_tooshort.setVisibility(View.GONE);
								mHandler.postDelayed(new Runnable() {
									public void run() {
										if (!isShosrt) {
											voice_rcd_hint_loading.setVisibility(View.GONE);
											voice_rcd_hint_rcding
													.setVisibility(View.VISIBLE);
										}
									}
								}, 300);
//								img1.setVisibility(View.VISIBLE);
								del_re.setVisibility(View.GONE);
								startVoiceT = SystemClock.currentThreadTimeMillis();
								voiceName = startVoiceT + ".amr";
//								start(voiceName);
//								flag = 2;
//							}
						} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
							System.out.println("4");
							String filepath = fileUtil.getVoice2File1aPath(toname,voiceName);
							mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
							if (event.getY() >= del_Y
									&& event.getY() <= del_Y + del_re.getHeight()
									&& event.getX() >= del_x
									&& event.getX() <= del_x + del_re.getWidth()) {
								rcChat_popup.setVisibility(View.GONE);
//								img1.setVisibility(View.VISIBLE);
								del_re.setVisibility(View.GONE);
								stop();
								flag = 1;
//								File file = new File(
//										android.os.Environment
//												.getExternalStorageDirectory()
//												+ "/ktsvoices/"
//												+ voiceName);
								File file = new File(filepath);
								if (file.exists()) {
									file.delete();
								}
							} else {

								voice_rcd_hint_rcding.setVisibility(View.GONE);
								stop();
								endVoiceT = SystemClock.currentThreadTimeMillis();
								flag = 1;
								int time = (int) Math.ceil((endVoiceT - startVoiceT) / 100);
								if (time < 1) {//time < 2
									isShosrt = true;
									voice_rcd_hint_loading.setVisibility(View.GONE);
									voice_rcd_hint_rcding.setVisibility(View.GONE);
									voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
									mHandler.postDelayed(new Runnable() {
										public void run() {
											voice_rcd_hint_tooshort
													.setVisibility(View.GONE);
											rcChat_popup.setVisibility(View.GONE);
											isShosrt = false;
										}
									}, 500);
									return false;
								}
//								String path = filepath.substring(0,filepath.lastIndexOf("/")+1);
//								scanSdCard(path);
//								ChatMsgEntity entity = new ChatMsgEntity();
//								entity.setDate(getDate());
//								entity.setName("高富帅");
//								entity.setMsgType(false);
//								entity.setTime(time + "\"");
//								entity.setText(voiceName);
//								mDataArrays.add(entity);
//								mAdapter.notifyDataSetChanged();
//								mListView.setSelection(mListView.getCount() - 1);
//								Map<String, File> files = new HashMap<String, File>();
////								if(fileUrl != null && !fileUrl.equals(""))
////								{
////									File file = new File(fileUrl);
////									files.put(file.getName(), file);
////								}
//								File file2 = new File(filepath);
//								files.put(file2.getName(), file2);
//								
//								requestScanFile(MessageListActivity.this,file2);
								requestScanFile2(MessageListActivity.this,filepath);
							}
						}
//						if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
//							System.out.println("5");
//							Animation mLitteAnimation = AnimationUtils.loadAnimation(getActivity(),
//									R.anim.cancel_rc);
//							Animation mBigAnimation = AnimationUtils.loadAnimation(getActivity(),
//									R.anim.cancel_rc2);
////							img1.setVisibility(View.GONE);
//							del_re.setVisibility(View.VISIBLE);
//							del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
//							if (event.getY() >= del_Y
//									&& event.getY() <= del_Y + del_re.getHeight()
//									&& event.getX() >= del_x
//									&& event.getX() <= del_x + del_re.getWidth()) {
//								del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
//								sc_img1.startAnimation(mLitteAnimation);
//								sc_img1.startAnimation(mBigAnimation);
//							}
//						} else {

//							img1.setVisibility(View.VISIBLE);
							del_re.setVisibility(View.GONE);
							del_re.setBackgroundResource(0);
//						}
					}
//					return super.onTouchEvent(event);
					return true;
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void voicendfinal(String filepath,Map<String, File> files,String voiceName)
	{
		String timestr = getRealPathFromURI(voiceName);
		
		int indexs = mymessageItem.size();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		String mid = myapp.getCombID();
		map.put("mid", mid);
		map.put("toid", toname);
		map.put("storeid", toname);
		map.put("serviceid", serviceid);
		map.put("servicename", servicename);
		map.put("sname", storeName);
		map.put("nameid", nameid);
		map.put("mysendname", fromname);
		map.put("userimg", myapp.getUserimgbitmap());
		map.put("fname", fname);
		map.put("tname", tname);
		map.put("yiman", R.drawable.yi_man);
		map.put("mymessagecontent", "【"+getString(R.string.button_recordAudio)+"】");
		map.put("mysendtime",sdf.format(new Date()));
		map.put("fileUrl","");
		map.put("fileUrl2",filepath);
		map.put("toname", toname);
		map.put("fileType", "");
		map.put("fileType2", "voice/amr");
		map.put("fileName", "");
		map.put("fileName2", voiceName);
		map.put("time", timestr);
		map.put("timetext", timestr+"″");
		map.put("messagetype", typesMapping);
		map.put("sendimg", "1");
		map.put("sendprogress", "0");
		map.put("storename", "");  
		map.put("storeDoc", "");
		map.put("storeimg", "");
		map.put("storelist", null);
		map.put("isRead", "0");
		map.put("messagestart", "");
		map.put("groupimg", groupimg);
		
		mymessageItem.add(map);
		myAdapter.notifyDataSetChanged();
		
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		dlist.add(map);
		saveMessageLocal(dlist);
		
		tempmap.put(mid, indexs);
		
		mymessagelistView.setSelection(mymessagelistView.getAdapter().getCount()-1); 
		
		try{
//			String jsonstr = voidpost(android.os.Environment.getExternalStorageDirectory()+ "/ktsvoices/"+voiceName);
//			String path = "";
//			path = filepath.substring(0,filepath.lastIndexOf("/")+1);
//			getAllFile(path);
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + path)));  
//			scanSdCard();
//			String timestr = getRealPathFromURI(voiceName);
			System.out.println(timestr);
			if(typesMapping.equals("friend") || typesMapping.equals("group"))
			{
				sendMessageVoice(files,Integer.valueOf(timestr),indexs,typesMapping,"voice/amr",mid);
			}
			else if(typesMapping.equals("qa"))
			{
				sendServiceMessageVoice(files,Integer.valueOf(timestr),indexs,typesMapping,"voice/amr",mid);
			}
			else 
			{
				sendAutomaticMessageVoice(files,Integer.valueOf(timestr),indexs,typesMapping,"voice/amr",mid);
//				String jsonstr = voidpost(filepath);
//				if(!jsonstr.equals(""))
//				{
//					JSONObject job = new JSONObject(jsonstr);
//					String content = "";
//					JSONArray array = job.getJSONArray("hypotheses");
//					if(array != null && array.length() > 0)
//					{
//						JSONObject jobs = array.getJSONObject(0);
//						content = jobs.getString("utterance");
//					}
////						sendMessageVoice(files,time+1,indexs);
//					sendMessage(content,indexs,true,"text","0");
//				}
//				else
//					sendMessageVoice(files,time+1,indexs,typesMapping,"voice/amr");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void requestScanFile(Context context, File file) {
		Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		i.setData(Uri.fromFile(file));
		context.sendBroadcast(i);
	}
	
	private void requestScanFile2(Context context, String file) {
		MediaScannerConnection.scanFile(context, new String[] { file }, null, // mime
																				// types，可不指定
				mListener);
	}

	MediaScannerConnection.OnScanCompletedListener mListener = new MediaScannerConnection.OnScanCompletedListener() {
		public void onScanCompleted(String path, Uri uri) {
			// TODO: 获取到该文件在多媒体数据库中的 uri，进行下一步动作
			final String paths = path;
			
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 10;
					
					try {
						msg.obj = paths;
					} catch (Exception ex) {
						Log.i("erroyMessage", ex.getMessage());
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
	
	/**
	 * 来源:
	 * http://stackoverflow.com/questions/3401579/get-filename-and-path-from-
	 * uri-from-mediastore
	 */
	public String getRealPathFromURI(String filename) {
//		filename = "11611.amr";
	  String[] proj = { MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,MediaStore.Video.Media.DISPLAY_NAME };

//	  ContentResolver mResolver = this.getContentResolver();
	  Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, "_display_name = ?", new String [] {filename}, null);
//	  Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
//	  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	  String time = "";
	  while (cursor.moveToNext()) {  
		  String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		  time = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		  int timeint = Integer.valueOf(time) / 1000;
		  time = String.valueOf(timeint);
//          break;
      }
//	  return cursor.getString(column_index);
	  return time;
	}
	
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"请说出你要说的话");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
	
	public Bitmap zoomImage(Bitmap bgimage, long imageSize, String fileUrl) {
//		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
//		if (imageSize < 20480) { // 0-20k
//			newOpts.inSampleSize = 1;
//       } else if (imageSize < 51200) { // 20-50k
//       	newOpts.inSampleSize = 2;
//       } else if (imageSize < 307200) { // 50-300k
//       	newOpts.inSampleSize = 4;
//       } else if (imageSize < 819200) { // 300-800k
//       	newOpts.inSampleSize = 6;
//       } else if (imageSize < 1048576) { // 800-1024k
//       	newOpts.inSampleSize = 8;
//       } else {
//       	newOpts.inSampleSize = 10;
//       }

		// inJustDecodeBounds设为false表示把图片读进内存中
//		newOpts.inJustDecodeBounds = false;
		// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
//		newOpts.outHeight = bgimage.getHeight();
//		newOpts.outWidth = bgimage.getWidth();
		
//		bgimage = BitmapFactory.decodeFile(fileUrl, newOpts);
		Bitmap bitmap = null;
		try{
			FileInputStream fis = new FileInputStream(fileUrl);
			// 解决加载图片 内存溢出的问题
			// Options 只保存图片尺寸大小，不保存图片到内存
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，其值表明缩放的倍数，SDK中建议其值是2的指数值,值越大会导致图片不清晰
			if (imageSize > 307200)
				opts.inSampleSize = 2;
			else
				opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
//			opts.inJustDecodeBounds = false;
			
			bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public Drawable getImageDrawable(String fileUrl) {
		Bitmap bitmap = null;
		Drawable drawable = null;
		try{
			FileInputStream fis = new FileInputStream(fileUrl);
			// 解决加载图片 内存溢出的问题
			// Options 只保存图片尺寸大小，不保存图片到内存
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，其值表明缩放的倍数，SDK中建议其值是2的指数值,值越大会导致图片不清晰
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
//			opts.inJustDecodeBounds = false;
			
			bitmap = BitmapFactory.decodeStream(fis,null,opts);
			drawable= new BitmapDrawable(getResources(), bitmap);
	//		bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return drawable;
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
		bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
		return bitmap;
	}
	
	public Bitmap zoomImage2(Bitmap bgimage, int newWidth, int newHeight) {
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
		int scaleWidth2 = (int)scaleWidth - newWidth;
		int x = 0;
		int y = 0;
		if(scaleWidth2>2)
		{
			x = scaleWidth2/2;
		}
		int scaleHeight2 = (int)scaleHeight - newHeight;
		if(scaleHeight2 > 2)
		{
			y = scaleHeight2/2;
		}
		bitmap = Bitmap.createBitmap(bgimage, x, y, width, height, matrix, true);
		return bitmap;
	}
	
	//等比例缩放
	public Bitmap adaptive(Bitmap bgimage, int newWidth, int newHeight) {
		if(newHeight > 960)
		{
			newWidth = 720;
			newHeight = 960;
		}
		
		// 获取这个图片的宽和高
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		if(bgimage.getWidth() > bgimage.getHeight())
			matrix.postScale(scaleWidth, scaleWidth);
		else
			matrix.postScale(scaleHeight, scaleHeight);
		int x = 0;
		int y = 0;
		bgimage = Bitmap.createBitmap(bgimage, x, y, width, height, matrix, true);
		return compressImage(bgimage,50);
	}
	
	//等比例缩放
	public Bitmap adaptive(Bitmap bitmap) {
		int newWidth = 150;
		int newHeight = 150;
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		if(bitmap.getWidth() > bitmap.getHeight())
			matrix.postScale(scaleHeight, scaleHeight);
		else
			matrix.postScale(scaleWidth, scaleWidth);
		int x = 0;
		int y = 0;
		bitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
		return compressImage(bitmap,10);
	}
	
    private Bitmap compressImage(Bitmap image,int options) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
//        int options = 100;  
//        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
//            baos.reset();//重置baos即清空baos  
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
//            options -= 10;//每次都减少10
//        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
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
	
	public void openMessageListForm()
	{
//		unregisterReceiver(mBroadcastReceiver2);//关闭广播监听
		Intent intent = new Intent();
//	    intent.setClass( getActivity(),MillenniumMessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    finish();
	}
	
	@Override
	public void onResume() {
		super.onResume();

//		MainTabActivity.instance.stopReceiver();
//		IntentFilter filter = new IntentFilter();
//
//		filter.addAction("NEW_MESSAGE_LIST_HUA_MEIDA");
//		filter.addAction("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA");
//		filter.addAction("unread_message_pus_hua_meida");
//		
//		registerReceiver(mBroadcastReceiver, filter);
		
		IntentFilter filter2 = new IntentFilter();

		filter2.addAction("CHANGE_BACKGROUND");
		
		registerReceiver(mBroadcastReceiver2, filter2);
	}
	
	/** 
     * 停止 
     */  
    @Override  
    protected void onStop() {  
        Log.i("TAG-onStop", "onStop()------------yin");  
//        unregisterReceiver(mBroadcastReceiver);//关闭广播监听
//        unregisterReceiver(mBroadcastReceiver2);//关闭广播监听
        super.onStop();  
    }  
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("NEW_MESSAGE_LIST_HUA_MEIDA")) {
//				getMessageListData("refresh");
				getLocalMessageListData("refresh");
			}
			else if(action.equals("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA"))
			{
				try{
					String datastr = intent.getStringExtra("datastr");
					JSONArray jArry = new JSONArray(datastr);
//					List<Map<String,Object>> newmessagelist = getMessageDetialData(jArry);
					List<Map<String,Object>> newmessagelist = myapp.getMessageDetialData(jArry, toname, storeName, nameid, fromname,false);
//					shockAndRingtones();
//					saveMessageLocal(newmessagelist);
					db.openDB();
					Map<String,Object> dmap = db.saveMessageData(newmessagelist.get(0));
					db.closeDB();
//					Map<String,Object> dmap = newmessagelist.get(0);
					String mysendname = (String)dmap.get("mysendname");
					if(mysendname.equals(toname))
					{
						TextView01.setText(storeName);
						mymessageItem.addAll(newmessagelist);
						myAdapter.notifyDataSetChanged();
					}
					
					if(myapp.getIsServer())
					{
						if(HotelMainActivity.instance != null)
							HotelMainActivity.instance.loadeListItemData();
					}
					else
					{
						if(typesMapping.equals("friend") || typesMapping.equals("yanzhenjieguo"))//如果是好友验证和好友信息都在信息栏里显示
						{
							if(HotelMainActivity.instance != null)
								HotelMainActivity.instance.loadeListItemData();
						}
						else
						{
							if(HotelServiceActivity.instance != null)
								HotelServiceActivity.instance.getMyStoreListDatas();
						}
					}
//					getMessageListData("refresh");
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(action.equals("unread_message_pus_hua_meida"))
			{
				try{
//					String datastr = intent.getStringExtra("datastr");
//					JSONArray jArry = new JSONArray(datastr);
//					List<Map<String,Object>> newmessagelist = HotelMainActivity.instance.getMessageDetialData(jArry);
//					saveMessageLocal(newmessagelist);
					MainTabActivity.instance.getMyUnreadMessageListData();
					MainTabActivity.instance.getMyUnreadRequestData();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	};
	
	public void loadGetNewMessage(final Intent intent)
    {
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 8;
				
				try{
					String datastr = intent.getStringExtra("datastr");
					JSONArray jArry = new JSONArray(datastr);
//					List<Map<String,Object>> newmessagelist = getMessageDetialData(jArry);
					List<Map<String,Object>> newmessagelist = myapp.getMessageDetialData(jArry, toname, storeName, nameid, fromname,false);
//					shockAndRingtones();
//					saveMessageLocal(newmessagelist);
					db.openDB();
					Map<String,Object> dmap = db.saveMessageData(newmessagelist.get(0));
					db.closeDB();
//					Map<String,Object> dmap = newmessagelist.get(0);
					String mysendname = (String)dmap.get("mysendname");
					if(mysendname.equals(toname))
					{
						TextView01.setText(storeName);
						mymessageItem.addAll(newmessagelist);
						myAdapter.notifyDataSetChanged();
					}
					if(myapp.getIsServer())
					{
						if(HotelMainActivity.instance != null)
							HotelMainActivity.instance.loadeListItemData();
					}
					else
					{
						if(typesMapping.equals("friend") || typesMapping.equals("yanzhenjieguo"))//如果是好友验证和好友信息都在信息栏里显示
						{
							if(HotelMainActivity.instance != null)
								HotelMainActivity.instance.loadeListItemData();
						}
						else
						{
							if(HotelServiceActivity.instance != null)
								HotelServiceActivity.instance.getMyStoreListDatas();
						}
					}
//					getMessageListData("refresh");
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}.start();
    }
	
	private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("CHANGE_BACKGROUND")) {
				int tag = intent.getIntExtra("tag",0);
				selectBg(tag);
			}
		}
	};
	
	public void selectBg(int tag)
	{
		try{
			switch (tag) {
			case 1:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg);
				break;
			case 2:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg2);
				break;
			case 3:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg3);
				break;
			case 4:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg4);
				break;
			case 5:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg5);
				break;
			case 6:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg6);
				break;
			case 7:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg7);
				break;
			case 8:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg8);
				break;
			case 9:
				main_rlayout.setBackgroundResource(R.drawable.chatting_bg9);
				break;
			default:
				break;
			}
			Editor editorshare = share.edit();
			editorshare.putString("bg_img", String.valueOf(tag));
			editorshare.commit();//提交
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {   
	    // TODO Auto-generated method stub   
	    super.onPause();
//	    unregisterReceiver(mBroadcastReceiver);//关闭广播监听
//	    if(MainTabActivity.instance != null)
//	    	MainTabActivity.instance.startBroadcastReceiver();
	} 
	
	// 新信息来时铃声加震动
	public void shockAndRingtones() {
		try {
//			mMediaPlayer.start();
//			if(mMediaPlayer.isPlaying())
//			{
//				mMediaPlayer.release();
//			}
//			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//				mMediaPlayer.setLooping(true);
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//			}

			mVibrator01 = (Vibrator) getApplication().getSystemService(
					Service.VIBRATOR_SERVICE);
			mVibrator01.vibrate(new long[] { 100, 100, 100, 1000, 100, 100,
					100, 1000, 100, 100, 100, 1000, 100, 100, 100, 1000, 100,
					100, 100, 1000, 100, 100, 100, 1000 }, -1);
			/* 用Toast显示震动启动 */

			/* 用Toast显示震动启动 */
			// Toast.makeText(this, "震动启动", Toast.LENGTH_SHORT).show();

			mTimer = new Timer(true);
			TimerTask mTimerTask = new TimerTask() {
				public void run() {
					mVibrator01.cancel();
					mTimer.cancel();
//					mMediaPlayer.stop();
				}
			};

			mTimer.schedule(mTimerTask, 1000, 1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
   
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isappOpen) {
				if(gridSmilesContainer.getVisibility() == View.VISIBLE && biaopin_layout.getVisibility() == View.VISIBLE)
				{
					gridSmilesContainer.setVisibility(View.GONE);
					biaopin_layout.setVisibility(View.GONE);
					app_layout.setVisibility(View.VISIBLE);
				}
				else
				{
					isappOpen = false;
					app_layout.setVisibility(View.GONE);
				}
			} else {
				openMainView();
			}
			return false;
		}
		return false;
	}
   
   
	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}
   
	// 按下语音录制按钮时
//	public boolean onTouchEvent(MotionEvent event) {
//
//		if (!Environment.getExternalStorageDirectory().exists()) {
//			Toast.makeText(MessageListActivity.this, "No SDCard", Toast.LENGTH_LONG).show();
//			return false;
//		}
//
//		if (btn_vocie) {
//			System.out.println("1");
//			int[] location = new int[2];
//			mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
//			int btn_rc_Y = location[1];
//			int btn_rc_X = location[0];
//			int[] del_location = new int[2];
//			del_re.getLocationInWindow(del_location);
//			int del_Y = del_location[1];
//			int del_x = del_location[0];
//			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
//				if (!Environment.getExternalStorageDirectory().exists()) {
//					Toast.makeText(MessageListActivity.this, "No SDCard", Toast.LENGTH_LONG).show();
//					return false;
//				}
//				System.out.println("2");
//				if (event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
//					System.out.println("3");
//					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
//					rcChat_popup.setVisibility(View.VISIBLE);
//					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
//					voice_rcd_hint_rcding.setVisibility(View.GONE);
//					voice_rcd_hint_tooshort.setVisibility(View.GONE);
//					mHandler.postDelayed(new Runnable() {
//						public void run() {
//							if (!isShosrt) {
//								voice_rcd_hint_loading.setVisibility(View.GONE);
//								voice_rcd_hint_rcding
//										.setVisibility(View.VISIBLE);
//							}
//						}
//					}, 300);
////					img1.setVisibility(View.VISIBLE);
//					del_re.setVisibility(View.GONE);
//					startVoiceT = SystemClock.currentThreadTimeMillis();
//					voiceName = startVoiceT + ".amr";
//					start(voiceName);
//					flag = 2;
//				}
//			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
//				System.out.println("4");
//				String filepath = fileUtil.getVoice2File1aPath(toname,voiceName);
//				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
//				if (event.getY() >= del_Y
//						&& event.getY() <= del_Y + del_re.getHeight()
//						&& event.getX() >= del_x
//						&& event.getX() <= del_x + del_re.getWidth()) {
//					rcChat_popup.setVisibility(View.GONE);
////					img1.setVisibility(View.VISIBLE);
//					del_re.setVisibility(View.GONE);
//					stop();
//					flag = 1;
////					File file = new File(
////							android.os.Environment
////									.getExternalStorageDirectory()
////									+ "/ktsvoices/"
////									+ voiceName);
//					File file = new File(filepath);
//					if (file.exists()) {
//						file.delete();
//					}
//				} else {
//
//					voice_rcd_hint_rcding.setVisibility(View.GONE);
//					stop();
//					endVoiceT = SystemClock.currentThreadTimeMillis();
//					flag = 1;
//					int time = (int) Math.ceil((endVoiceT - startVoiceT) / 100);
//					if (time < 1) {//time < 2
//						isShosrt = true;
//						voice_rcd_hint_loading.setVisibility(View.GONE);
//						voice_rcd_hint_rcding.setVisibility(View.GONE);
//						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
//						mHandler.postDelayed(new Runnable() {
//							public void run() {
//								voice_rcd_hint_tooshort
//										.setVisibility(View.GONE);
//								rcChat_popup.setVisibility(View.GONE);
//								isShosrt = false;
//							}
//						}, 500);
//						return false;
//					}
////					ChatMsgEntity entity = new ChatMsgEntity();
////					entity.setDate(getDate());
////					entity.setName("高富帅");
////					entity.setMsgType(false);
////					entity.setTime(time + "\"");
////					entity.setText(voiceName);
////					mDataArrays.add(entity);
////					mAdapter.notifyDataSetChanged();
////					mListView.setSelection(mListView.getCount() - 1);
//					Map<String, File> files = new HashMap<String, File>();
////					if(fileUrl != null && !fileUrl.equals(""))
////					{
////						File file = new File(fileUrl);
////						files.put(file.getName(), file);
////					}
//					File file2 = new File(filepath);
//					files.put(file2.getName(), file2);
//					
//					int indexs = mymessageItem.size();
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("mid", db.getCombID());
//					map.put("toid", toname);
//					map.put("storeid", toname);
//					map.put("sname", storeName);
//					map.put("nameid", nameid);
//					map.put("mysendname", fromname);
//					map.put("userimg", myapp.getUserimgbitmap());
//					map.put("fname", fname);
//					map.put("tname", tname);
//					map.put("yiman", R.drawable.yi_man);
//					map.put("mymessagecontent", "");
//					map.put("mysendtime",sdf.format(new Date()));
//					map.put("fileUrl","");
//					map.put("fileUrl2",filepath);
//					map.put("toname", toname);
//					map.put("fileType", "");
//					map.put("fileType2", "voice/amr");
//					map.put("fileName", "");
//					map.put("fileName2", voiceName);
//					map.put("time", String.valueOf(time+1));
//					map.put("timetext", String.valueOf(time+1)+"″");
//					map.put("messagetype", "");
//					map.put("sendimg", "1");
//					map.put("sendprogress", "0");
//					map.put("storename", "");  
//					map.put("storeDoc", "");
//					map.put("storeimg", "");
//					map.put("storelist", null);
//					map.put("isRead", "0");
//					
//					mymessageItem.add(map);
//					myAdapter.notifyDataSetChanged();
//					
//					List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
//					dlist.add(map);
//					saveMessageLocal(dlist);
//					
//					mymessagelistView.setSelection(mymessagelistView.getAdapter().getCount()-1); 
//					
//					try{
////						String jsonstr = voidpost(android.os.Environment.getExternalStorageDirectory()+ "/ktsvoices/"+voiceName);
//						String jsonstr = voidpost(filepath);
//						if(!jsonstr.equals(""))
//						{
//							JSONObject job = new JSONObject(jsonstr);
//							String content = "";
//							JSONArray array = job.getJSONArray("hypotheses");
//							if(array != null && array.length() > 0)
//							{
//								JSONObject jobs = array.getJSONObject(0);
//								content = jobs.getString("utterance");
//							}
//	//						sendMessageVoice(files,time+1,indexs);
//							sendMessage(content,indexs);
//						}
//						else
//							sendMessageVoice(files,time+1,indexs);
//					}catch(Exception ex){
//						ex.printStackTrace();
//					}
//				}
//			}
////			if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
////				System.out.println("5");
////				Animation mLitteAnimation = AnimationUtils.loadAnimation(getActivity(),
////						R.anim.cancel_rc);
////				Animation mBigAnimation = AnimationUtils.loadAnimation(getActivity(),
////						R.anim.cancel_rc2);
//////				img1.setVisibility(View.GONE);
////				del_re.setVisibility(View.VISIBLE);
////				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
////				if (event.getY() >= del_Y
////						&& event.getY() <= del_Y + del_re.getHeight()
////						&& event.getX() >= del_x
////						&& event.getX() <= del_x + del_re.getWidth()) {
////					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
////					sc_img1.startAnimation(mLitteAnimation);
////					sc_img1.startAnimation(mBigAnimation);
////				}
////			} else {
//
////				img1.setVisibility(View.VISIBLE);
//				del_re.setVisibility(View.GONE);
//				del_re.setBackgroundResource(0);
////			}
//		}
////		return super.onTouchEvent(event);
//		return true;
//	}
	
	public String voidpost(String filePath) throws Exception {
    	String response = "", brLine = "";
    	try{
	        URL url = new URL("http://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&lang=zh-CN&maxresults=1");  
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setConnectTimeout(8000);
	        conn.setReadTimeout(8000); 
	        conn.setDoOutput(true);  
	        conn.setDoInput(true);  
	        conn.setRequestMethod("POST");  
	        conn.setRequestProperty("Content-Type", "audio/amr; rate=8000");  
	        conn.setRequestProperty("user-agent","mozilla/5.0");  
	        DataOutputStream writer = new DataOutputStream(conn.getOutputStream());  
	        FileInputStream input = new FileInputStream(new File(filePath));
	        byte[] buffer = new byte[256]; //必须用户自己创建一个buffer。  
	        int read = input.read(buffer);  
	        while (read != -1) // 判断文件读完的条件  
	        {  
	            writer.write(buffer,0,read);  
	            read = input.read(buffer);  
	        }  
	        writer.flush();  
	        writer.close();  
	        input.close();  
	        InputStreamReader reder = new InputStreamReader(conn.getInputStream(),  "utf-8");  
	        BufferedReader breader = new BufferedReader(reder);  
	        while((brLine = breader.readLine())!=null)
	        {
	            response =(new StringBuilder(String.valueOf(response))).append(brLine).toString();
	            System.out.println("==="+response);
	        }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
        return response;
    }
	
	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};
	
	private void start(String name) {
		String filepath = fileUtil.getVoice2File1aPath(toname,name);
		mSensor.start(filepath);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {
		
		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}
	
	public void showBackgroundWindo()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass(this,MessageBackgroundActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showBankRoomWindo()
	{
		Intent intent = new Intent();
//	    intent.setClass(this,HotelReservation.class);
		intent.setClass(this,HtmlWebView.class);
	    Bundle bundle = new Bundle();
		bundle.putString("index", String.valueOf(index));
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void showDownloadCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag", "download");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showStopAddress()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,LocalMap.class);
			intent.setClass( this,BaiduMapRouteSearch.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag","message");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showWebHtml(String url,String title)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,HtmlWebView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("url", url);
			bundle.putString("title", title);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showStoreinfo(String pkid,String title,String desc,Object sysImg)
	{
//		makeText(pkid);
		List<Map<String,Object>> datalist = myapp.getMyCardsAll();
		boolean isnull = false;
		int index = 0;
		String typesMapping = "";
		for(int i=0;i<datalist.size();i++)
		{
			Map<String,Object> map = datalist.get(i);
			String storeid = (String)map.get("storeid");
			if(pkid.equals(storeid))
			{
				isnull = true;
				index = i;
				typesMapping = (String)map.get("typesMapping");
				break;
			}
		}
		if(isnull)
		{
			openCardView(typesMapping,index);
		}
		else
		{
//			makeText(pkid);
			Intent intent = new Intent();
		    intent.setClass(this,HotelStoreAttentionActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("title", title);
			bundle.putString("pkid", pkid);
			bundle.putString("desc",desc);
			if(sysImg instanceof Bitmap)
			{
				Bitmap bitmimg = (Bitmap)sysImg;
				int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("sysImg", oss.toByteArray());
				bundle.putString("imgtype", "byte");
			}
			else
			{
				bundle.putString("sysImg", (String)sysImg);
				bundle.putString("imgtype", "string");
			}
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}
	}
	
	public void showTrafficView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass(this,GetTicketsInfomationActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadUserImageThread2(final String storeimg)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 7;
				
				try {
					Bitmap bitmap = getImageBitmap2(storeimg);
					if(bitmap != null)
						myapp.setStoreimgbitmap(bitmap);
					else
						myapp.setStoreimgbitmap(null);
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public Bitmap getImageBitmap2(String value)
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
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
//			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void openAgainSendMuneView(final int index)
	{
		final String [] items = new String[]{getString(R.string.hotel_label_28),getString(R.string.hotel_label_22)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == 0)
				{
					Map<String,Object> datamap = mymessageItem.get(index);
					String mid = (String)datamap.get("mid");
					String mymessagecontent = (String)datamap.get("mymessagecontent");
					String messagetype = (String)datamap.get("messagetype");
					String fileType = (String)datamap.get("fileType");
					String fileType2 = (String)datamap.get("fileType2");
					View view = myAdapter.getSelectView(index);
					int viewtype = myAdapter.getItemViewType(index);
					if(mymessagecontent != null && !mymessagecontent.equals(""))
					{
						myAdapter.setItemSendImgStart(viewtype,view, false,index);
						myAdapter.setItemSendPressStart(viewtype,view, true);
						
						if(typesMapping.equals("friend"))
						{
							sendFriendMessage(mymessagecontent,index,true,mid);
						}
						else if(typesMapping.equals("group"))
						{
							sendFriendGroupMessage(mymessagecontent,index,true,mid);
						}
						else if(typesMapping.equals("qa"))
						{
							sendServiceMessage(mymessagecontent,index,true,mid);
						}
						else
							sendMessage(mymessagecontent,index,true,"text","0",mid);
					}
					else if(fileType2 != null && !fileType2.equals(""))
					{
						myAdapter.setItemSendImgStart(viewtype,view, false,index);
						myAdapter.setItemSendPressStart(viewtype,view, true);
						
						Map<String, File> files = new HashMap<String, File>();
						String fileurl = (String)datamap.get("fileUrl2");
						String time = (String)datamap.get("time");
						File file2 = new File(fileurl);
						files.put(file2.getName(), file2);
						
						if(typesMapping.equals("friend"))
						{
							sendMessageVoice(files,Integer.valueOf(time),index,typesMapping,"voice/amr",mid);
						}
						else if(typesMapping.equals("qa"))
						{
							sendServiceMessageVoice(files,Integer.valueOf(time),index,typesMapping,"voice/amr",mid);
						}
						else 
						{
							sendAutomaticMessageVoice(files,Integer.valueOf(time),index,typesMapping,"voice/amr",mid);
						}
						
					}
					else if(fileType != null && !fileType.equals(""))
					{
//						WeakReference<Bitmap> wrf = myAdapter.dateCache.get(index);
//						Bitmap bitmap = null;
//						if(wrf != null)
//							bitmap = wrf.get();
//						FileUtils fileutil = new FileUtils();
//						String fileurls = Environment.getExternalStorageDirectory()+"/temp";
//						try {
//							fileutil.saveFile(fileurls,bitmap,"tempimage.jpg");
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						sendMessagess(index,fileurls+"/tempimage.jpg");
						String filepath = (String)datamap.get("fileUrl");
						String fileName = (String)datamap.get("fileName");
						String[] str = fileName.split(".");
						String filetype = str[1];
						sendMessagess(index,filepath,filetype,mid);
					}
				}
				else
				{
					Map<String,Object> datamap = mymessageItem.get(index);
					String mid = (String)datamap.get("mid");
					deleteMessageLocal(mid);
					System.out.println("mid=="+mid+"==");
					mymessageItem.remove(index);
					myAdapter.notifyDataSetInvalidated();
////					myAdapter.refreshData(index);
////					mymessageItem.remove(index);
//					mymessagelistView.setAdapter(null);
//					myAdapter = new MessageBaseAdapter(MessageListActivity.this, mymessageItem,
//							R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,
//							new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress"}, 
//							new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar},
//							share,"max",fromname);
//
//					// 添加并且显示
//					mymessagelistView.setAdapter(myAdapter);
					
					int[] location = new int[2];  
					rl_layout.getLocationOnScreen(location);
		            int x = location[0];  
		            int y = location[1];  
		            System.out.println("x:"+x+"y:"+y);
		            
		            mymessagelistView.setSelectionFromTop(myapp.getCurrentDelindex(), y+60);
				}
			}
		});
		
		alertDialog = builder.create();
		alertDialog.setTitle(getString(R.string.app_name));
		alertDialog.show();
	}
	
	public void openListViewItemMuneView(final String tag)
	{
//		String [] items = new String[]{getString(R.string.hotel_label_22),getString(R.string.hotel_label_172),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
//		final String [] items2 = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
//		final String [] items3 = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward)};
		
		if(tag.equals("text"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.hotel_label_172),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
		}
		else if(tag.equals("img"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
		}
		else if(tag.equals("amr"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward)};
		}
		else if(tag.equals("imgtext"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
		}
		else if(tag.equals("listitem"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward)};
		}
		else if(tag.equals("link"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_22),getString(R.string.itemmenu_forward),getString(R.string.share_info)};
		}
		else if(tag.equals("tape"))
		{
			messageMumeitems = new String[]{getString(R.string.hotel_label_197)};
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(messageMumeitems, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Map<String,Object> datamap = mymessageItem.get(myapp.getCurrentDelindex());
				String mid = (String)datamap.get("mid");
				String mymessagecontent = (String)datamap.get("mymessagecontent");
				String src = messageMumeitems[which];
				if(src.equals(getString(R.string.hotel_label_22)))
				{
					deleteMessageLocal(mid);
//					Iterator<Map<String,Object>> iter = mymessageItem.iterator();  
//					while(iter.hasNext()){  
//						Map<String,Object> map = iter.next();
//					    String s = (String)map.get("mid");  
//					    if(s.equals(mid)){  
//					        iter.remove();
//					        break;
//					    }
//					}
					mymessageItem.remove(myapp.getCurrentDelindex());
					myAdapter.notifyDataSetChanged();
//					mymessagelistView.onRefreshComplete();
//					mymessagelistView.setAdapter(null);
//					myAdapter = new MessageBaseAdapter(MessageListActivity.this, mymessageItem,
//							R.layout.mymessagelistn, R.layout.mymessagelistn2,R.layout.mymessage_store_item,R.layout.mymessage_store_news,
//							new String[] {"userimg","fname","mysendtime","mymessagecontent","fileUrl","fileUrl2","timetext","storeimg","storename","storelist","storeDoc","sendimg","sendprogress"}, 
//							new int[] {R.id.iv_userhead,R.id.tv_username,R.id.mysendtime,R.id.mymessagecontent2,R.id.uploadFileImg,R.id.mymessagecontent,R.id.tv_time,R.id.store_img,R.id.store_name,R.id.store_item_list,R.id.store_doc_txt,R.id.start_img,R.id.send_progressBar},
//							share,"max",fromname);
//
//					// 添加并且显示
//					mymessagelistView.setAdapter(myAdapter);
					
//					int[] location = new int[2];  
//					rl_layout.getLocationOnScreen(location);
//		            int x = location[0];  
//		            int y = location[1];  
//		            System.out.println("x:"+x+"y:"+y);
//		            
//		            mymessagelistView.setSelectionFromTop(myapp.getCurrentDelindex(), y+60);
				}
				else if(src.equals(getString(R.string.hotel_label_197)))
				{
					deleteMessageLocal(mid);
					mymessageItem.remove(myapp.getCurrentDelindex());
					myAdapter.notifyDataSetChanged();
				}
				else if(src.equals(getString(R.string.hotel_label_172)))//复制
				{
					ClipboardManager c= (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
					c.setText(mymessagecontent);
				}
				else if(src.equals(getString(R.string.itemmenu_forward)))//转发
				{
					showContactActivity(tag,datamap);
				}
				else if(src.equals(getString(R.string.share_info)))//分享
				{
					if(tag.equals("text"))
					{
						Intent intent = new Intent(Intent.ACTION_SEND);  
		                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		                intent.putExtra(Intent.EXTRA_TEXT, mymessagecontent);   //附带的说明信息  
		                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.share_info));  //标题
//		                intent.setType("image/*");   //分享图片  
		                intent.setType("text/plain"); //分享文字
		                startActivity(Intent.createChooser(intent,getString(R.string.share_info)));
					}
					else if(tag.equals("img"))
					{
						String fliePath = (String)datamap.get("fileUrl");
						Intent intent = new Intent(Intent.ACTION_SEND);  
		                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fliePath)));  //传输图片或者文件 采用流的方式  
		                intent.putExtra(Intent.EXTRA_TEXT, mymessagecontent);   //附带的说明信息  
		                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_info));  //标题
		                intent.setType("image/*");   //分享图片  
//		                intent.setType("text/plain"); //分享文字
		                startActivity(Intent.createChooser(intent,getString(R.string.share_info)));
					}
					else if(tag.equals("imgtext"))
					{
						String storename = (String)datamap.get("storename");
						String storeDoc = (String)datamap.get("storeDoc");
						String url = (String)datamap.get("url");
						storeDoc = storeDoc + "\n<a href='"+url+"'>点击获取更多信息</a>";
						
						String fliePath = (String)datamap.get("storeimg");
						Intent intent = new Intent(Intent.ACTION_SEND);  
		                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fliePath)));  //传输图片或者文件 采用流的方式  
		                intent.putExtra(Intent.EXTRA_TEXT, storeDoc);   //附带的说明信息  
		                intent.putExtra(Intent.EXTRA_SUBJECT, storename);  //标题
		                intent.setType("image/*");   //分享图片  
//		                intent.setType("text/plain"); //分享文字
		                startActivity(Intent.createChooser(intent,storename));
					}
					else if(tag.equals("link"))
					{
						try{
							JSONObject job = new JSONObject(mymessagecontent);
							String storename = job.getString("storename");
							String storeDoc = job.getString("storeDoc");
							String url = job.getString("link");
	//						storeDoc = storeDoc + "\n<a href='"+url+"'>点击获取更多信息</a>";
							
							String fliePath = (String)datamap.get("storeimg");
							Intent intent = new Intent(Intent.ACTION_SEND);  
			                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fliePath)));  //传输图片或者文件 采用流的方式  
			                intent.putExtra(Intent.EXTRA_TEXT, storeDoc);   //附带的说明信息  
			                intent.putExtra(Intent.EXTRA_SUBJECT, storename);  //标题
			                intent.setType("image/*");   //分享图片  
	//		                intent.setType("text/plain"); //分享文字
			                startActivity(Intent.createChooser(intent,storename));
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setTitle(getString(R.string.app_name));
		alertDialog.show();
	}
	
    /** 
     * 分享多张照片 
     */  
//    private void sendMultiple(){  
//        Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);  
//        intent.setType("image/*");  
//        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getUriListForImages());  
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
//        intent.putExtra(Intent.EXTRA_TEXT, "你好 ");  
//        intent.putExtra(Intent.EXTRA_TITLE, "我是标题");  
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
//        startActivity(Intent.createChooser(intent, "请选择"));   
//    }  
      
    /**  
     * 设置需要分享的照片放入Uri类型的集合里  
     */  
    private ArrayList<Uri> getUriListForImages(String fliePath) {  
           ArrayList<Uri> myList = new ArrayList<Uri>();  
//           String imageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/100ANDRO/";  
           File imageDirectory = new File(fliePath);  
//           String[] fileList = imageDirectory.list();  
//           if(fileList.length != 0) {  
//               for(int i=0; i<5; i++){
                   try{  
                       ContentValues values = new ContentValues(7);  
                       values.put(Images.Media.TITLE, imageDirectory.getName());  
                       values.put(Images.Media.DISPLAY_NAME, imageDirectory.getName());  
                       values.put(Images.Media.DATE_TAKEN, new Date().getTime());  
                       values.put(Images.Media.MIME_TYPE, "image/jpeg");  
                       values.put(Images.ImageColumns.BUCKET_ID, fliePath.hashCode());  
                       values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, imageDirectory.getName());  
                       values.put("_data", fliePath);  
                       ContentResolver contentResolver = getApplicationContext().getContentResolver();  
                       Uri uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);  
                       myList.add(uri);  
                   } catch (Exception e) {  
                       e.printStackTrace();  
                   }  
//               }  
//           }  
           return myList;  
    }
    
    public void showContactActivity(String tag,Map<String,Object> datamap){
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ForwardContactActivity.class);
		    Bundle bundle = new Bundle();
		    if(tag.equals("text"))
			{
		    	String mymessagecontent = (String)datamap.get("mymessagecontent");
		    	bundle.putString("messageContent", mymessagecontent);
			}
		    else if(tag.equals("amr"))
		    {
		    	String fileUrl2 = (String)datamap.get("fileUrl2");
		    	String fileName2 = (String)datamap.get("fileName2");
		    	bundle.putString("voicpath", fileUrl2);
		    	bundle.putString("voicname", fileName2);
		    }
			else if(tag.equals("img"))
			{
				String fliePath = (String)datamap.get("fileUrl");
				String fileName = (String)datamap.get("fileName");
				String fileType = (String)datamap.get("fileType");
				bundle.putString("imgpath", fliePath);
				bundle.putString("imgname", fileName);
				bundle.putString("fileType",fileType);
			}
			else if(tag.equals("imgtext"))
			{
				String storename = (String)datamap.get("storename");
				String storeDoc = (String)datamap.get("storeDoc");
				String url = (String)datamap.get("url");
				String fliePath = (String)datamap.get("storeimg");
//				storeDoc = storeDoc + "\n<a href='"+url+"'>点击获取更多信息</a>";
				String imgname = fliePath.substring(fliePath.lastIndexOf("/")+1,fliePath.length());
				
				bundle.putString("storeDoc", storeDoc);
				bundle.putString("storename", storename);
				bundle.putString("imgpath", fliePath);
				bundle.putString("imgname", imgname);
				bundle.putString("link", url);
			}
			else if(tag.equals("link"))
			{
				try{
					String mymessagecontent = (String)datamap.get("mymessagecontent");
					JSONObject job = new JSONObject(mymessagecontent);
					String storename = job.getString("storename");
					String storeDoc = job.getString("storeDoc");
					String url = job.getString("link");
//						storeDoc = storeDoc + "\n<a href='"+url+"'>点击获取更多信息</a>";
					String fliePath = (String)datamap.get("storeimg");
					String imgname = fliePath.substring(fliePath.lastIndexOf("\\/")+1,fliePath.length());
					
					bundle.putString("storeDoc", storeDoc);
					bundle.putString("storename", storename);
					bundle.putString("imgpath", fliePath);
					bundle.putString("imgname", imgname);
					bundle.putString("link", url);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			bundle.putString("tag", tag);
			intent.putExtras(bundle);
		    startActivity(intent);
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMessageImageDetails(String mid){
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ImageTouchActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("mid", mid);
			bundle.putString("fileUrl", "");
			intent.putExtras(bundle);
		    startActivity(intent);
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void openVerificationWindo(String friendpfid)
	{
		try{
			yanzhenview = LayoutInflater.from(this).inflate(R.layout.friend_verification_windo, null);
			
			myDialogs = new Dialog(this,R.style.MyMapDialog);
			myDialogs.setContentView(yanzhenview);
			
			Window dialogWindow = myDialogs.getWindow();
			WindowManager m = getWindowManager();
	        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = (int) (d.getWidth() * 0.9);
			dialogWindow.setAttributes(lp);
			
			myDialogs.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void cleraWindo(View v)
	{
		myDialogs.dismiss();
	}
	
	public void sendVerification(View v)
	{
		EditText message_edit = (EditText)yanzhenview.findViewById(R.id.message_edit);
		String message = message_edit.getText().toString();
		myDialogs.dismiss();
		sendVerificationMessage(message);
	}
	
	public void sendVerificationMessage(final String message)
    {
		showMyLoadingDialog(getString(R.string.hotel_label_96));
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 9;
				
				try {
					boolean b = false;
					JSONObject jobj = api.addVerificationMessage(toname,message);
					if(jobj != null)
					{
						if(jobj.getString("tag").equals("success"))
						{
							b = true;
						}
					}
					msg.obj = b;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
    }
	
	public void showProgressDialog(){
 		try{
			mypDialog = new ProgressDialog(this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("数据加载中...");
			mypDialog.setIndeterminate(false);
			mypDialog.setCancelable(true);
			mypDialog.show();
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
	
	@Override
    protected void onDestroy(){
        super.onDestroy();
//        System.out.println("准备清理内存");
//        unregisterReceiver(mBroadcastReceiver);//关闭广播监听
        unregisterReceiver(mBroadcastReceiver2);//关闭广播监听
        if(myAdapter != null)
        	myAdapter.removeAllResources();
    }
	
	/**  
     * 定义从右侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromRightAnimation() {  
        Animation inFromRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromRight.setDuration(500);  
        inFromRight.setInterpolator(new AccelerateInterpolator());  
        return inFromRight;  
    }  
   
    /**  
     * 定义从左侧退出的动画效果  
     * @return  
     */  
    protected Animation outToLeftAnimation() {  
        Animation outtoLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoLeft.setDuration(500);  
        outtoLeft.setInterpolator(new AccelerateInterpolator());  
        return outtoLeft;  
    }  
   
    /**  
     * 定义从左侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromLeftAnimation() {  
        Animation inFromLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromLeft.setDuration(500);  
        inFromLeft.setInterpolator(new AccelerateInterpolator());  
        return inFromLeft;  
    }  
   
    /**  
     * 定义从右侧退出时的动画效果  
     * @return  
     */  
    protected Animation outToRightAnimation() {  
        Animation outtoRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoRight.setDuration(500);  
        outtoRight.setInterpolator(new AccelerateInterpolator());  
        return outtoRight;  
    } 
	
	public void showMyLoadingDialog(String message)
    {
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
	
	public void chengKtsServiceOrderStart(String start)
	{
		try{
			Map<String,Object> datamap = mymessageItem.get(myapp.getCurrentDelindex());
			String storeDoc = (String)datamap.get("storeDoc");
			if(storeDoc.indexOf("Service Status:") >= 0)
			{
				String [] strs = storeDoc.split("<br>");
				storeDoc = storeDoc.replaceAll(strs[0], "");
				String newdoc = "Service Status:"+start+"\n"+storeDoc;
				datamap.put("storeDoc", newdoc);
			}
			else
			{
				String newdoc = "Service Status:"+start+"\n<br>"+storeDoc;
				datamap.put("storeDoc", newdoc);
			}
			
			myAdapter.notifyDataSetChanged();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public class CommonGestureListener extends SimpleOnGestureListener {
	   	 
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onDown...");
			return false;
		}
 
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onShowPress...");
			super.onShowPress(e);
		}
 
		@Override
	    public void onLongPress(MotionEvent e) {
	        // TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "----> Jieqi: do onLongPress...");
	    }
 
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapConfirmed...");
			return false;
		}
 
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapUp...");
			return false;
		}
 
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY){
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onFling...");
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            // 当像左侧滑动的时候  
	            //设置View进入屏幕时候使用的动画  
				mViewFlipper.setInAnimation(inFromRightAnimation());  
	            //设置View退出屏幕时候使用的动画  
				mViewFlipper.setOutAnimation(outToLeftAnimation());
//				selectLisgBg("next");
				if(layoutindex == 0)
					layoutindex++;
				else
				{
					if (layoutindex % (mViewFlipper.getChildCount()-1) == 0) 
						layoutindex = 0;
					else
						layoutindex++;
				}
				pageControl.generatePageControl(layoutindex);
				mViewFlipper.showNext(); 
				return true;
	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
	            // 当像右侧滑动的时候  
	        	mViewFlipper.setInAnimation(inFromLeftAnimation());  
	        	mViewFlipper.setOutAnimation(outToRightAnimation());  
//	        	selectLisgBg("previous");
	        	if(layoutindex == 0)
	        	{
	        		layoutindex = mViewFlipper.getChildCount()-1;
	        	}
	        	else
	        	{
					layoutindex--;
	        	}
	        	pageControl.generatePageControl(layoutindex);
	        	mViewFlipper.showPrevious();  
	        	return true;
	        }  
			return false;
		}
		
 
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onScroll...");
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
 
    }
}
