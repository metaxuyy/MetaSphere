package ms.activitys.notification;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.LoginMain;
import ms.activitys.vipcards.CardsView;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.MyGridView;

public class AdvertiseNotificationActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ImageView lbimgview;
	private ImageView advertiseimg;
	private String btntag;
	private String msg;
	private String type;
	private String storeName;
	private String storeId;
	private String stroeImg;
	private String title;
	private String uri;
	private String businessId;
	private Button vipbtn;
	private List<Map<String,Object>> adetillist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advertise_detail);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		msg = this.getIntent().getExtras().getString("msg");
		type = this.getIntent().getExtras().getString("type");
		storeName = this.getIntent().getExtras().getString("storeName");
		storeId = this.getIntent().getExtras().getString("storeId");
		stroeImg = this.getIntent().getExtras().getString("stroeImg");
		title = this.getIntent().getExtras().getString("title");
		uri = this.getIntent().getExtras().getString("uri");
		businessId = this.getIntent().getExtras().getString("businessId");
		int noid = this.getIntent().getExtras().getInt("notificationid");
		String adetail = this.getIntent().getExtras().getString("adetail");
		if(!adetail.equals(""))
		{
			adetillist = getAdvertiseDetil(adetail);
			myapp.setAdvertiseProducts(adetillist);
			MyGridView adetil_grid = (MyGridView)findViewById(R.id.adetil_grid);
			adetil_grid.setAdapter(getProuderMenuAdapter(adetillist));
			
			adetil_grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
//					Map<String,Object> map = adetillist.get(arg2);
//					String pname = (String)map.get("pname");
//					Toast.makeText(AdvertiseNotificationActivity.this, pname, Toast.LENGTH_LONG).show();
					if(btntag.equals("1"))
					{
						showProductDetails(arg2);
					}
					else
					{
						makeText(AdvertiseNotificationActivity.this.getString(R.string.menu_lable_147));
					}
				}
			});
		}
		
		System.out.println("notificationId==="+noid);
		System.out.println("title==="+title);
		
//		NotificationService.nonumber = NotificationService.nonumber - 1;
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(noid);
//		m_NotificationManager.cancelAll();
		
		lbimgview = (ImageView)findViewById(R.id.img_areaImg);
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				Bitmap bitm = getImageBitmap(stroeImg,true,60,60);
				msg.obj = bitm;
				handler.sendMessage(msg);
			}
		}.start();
		
		TextView titleLable = (TextView)findViewById(R.id.txtv1);
		if(type.equals("waiting"))
			titleLable.setText(this.getString(R.string.notification_lable_1)+storeName+this.getString(R.string.order_restaurant_title));
		else
			titleLable.setText(this.getString(R.string.notification_lable_1)+storeName+this.getString(R.string.notification_lable_2));
		
		TextView txtstorename = (TextView)findViewById(R.id.txt_store_name);
		txtstorename.setText(storeName);
		
		TextView txt_title = (TextView)findViewById(R.id.txt_title);
		txt_title.setText(title);
		
		TextView txt_message = (TextView)findViewById(R.id.txt_message);
		txt_message.setText(msg);
		
		advertiseimg = (ImageView)findViewById(R.id.advertise_img);
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				Bitmap bitm = getImageBitmap(uri,false,110,110);
				msg.obj = bitm;
				handler.sendMessage(msg);
			}
		}.start();
		
		Button closebtn = (Button)findViewById(R.id.btn_colse);
		closebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(myapp.getSessionId() == null)
				{
					Intent startMain = new Intent(Intent.ACTION_MAIN);
			         startMain.addCategory(Intent.CATEGORY_HOME);
			         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			         startActivity(startMain);
			         System.exit(0);
				}
				else
				{
					AdvertiseNotificationActivity.this.setResult(RESULT_OK, getIntent());
					AdvertiseNotificationActivity.this.finish();
				}
			}
		});
		
		vipbtn = (Button)findViewById(R.id.btn_vip_add_in);
		if(myapp.getSessionId() == null)
		{
			vipbtn.setVisibility(View.GONE);
			String user = share.getString("user", "");
			String pwa = share.getString("pwa", "");
			if(user != null && !user.equals(""))
			{
				loadThreadDataLogin3(user,pwa);
			}
			else
			{
				Intent intent=new Intent();
				intent.setClass(this,LoginMain.class);
				startActivity(intent);
				this.finish();
			}
			
		}
		else
		{
			JSONObject jobj;
			try {
				jobj = api.isSelectCards(myapp.getPfprofileId(),storeId,businessId,null);
				btntag = jobj.getString("tag");
				if(btntag.equals("1"))
				{
					vipbtn.setText(this.getString(R.string.map_lable_14));
				}
				else
				{
					vipbtn.setText(this.getString(R.string.notification_lable_3));
				}
				
				vipbtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(btntag.equals("1"))
						{
							inVipCard();
						}
						else
						{
							addVipCard();
						}
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showProductDetails(int pindex)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,AdvertiseProductDetail.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("index", pindex);
			bundle.putString("storeid",storeId);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getAdvertiseDetil(String jsonstr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			JSONObject json = new JSONObject(jsonstr);
			JSONArray array = json.getJSONArray("advertiseDetail");
			for(int i=0;i<array.length();i++)
			{
				JSONObject dobj = array.getJSONObject(i);

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
				
				String timenew = "false"; 
				if(dobj.has("new"))
					timenew = (String) dobj.get("new");
				
				String two = "0"; 
				if(dobj.has("two"))
					two = (String) dobj.get("two");
				
				String one = "0"; 
				if(dobj.has("one"))
					one = (String) dobj.get("one");
				
				String special = "1"; 
				if(dobj.has("special"))
					special = (String) dobj.get("special");
				
				String psize = ""; 
				if(dobj.has("psize"))
					psize = (String) dobj.get("psize");
				
				String productsNo = ""; 
				if(dobj.has("productsNo"))
					productsNo = (String) dobj.get("productsNo");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("imgurl", imgurl);
				map.put("cname", cname);
				map.put("price", price + " ￥");
				map.put("prices", price);
				map.put("pdesc", pDesc);
				map.put("productType", productType);
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
				map.put("txt", cname + " " + price + " ￥");
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
	
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	private SimpleAdapter getProuderMenuAdapter(List<Map<String,Object>> list) {
		SpecialAdapter simperAdapter = null;
		simperAdapter = new SpecialAdapter(this, list,
				R.layout.item_menu2, new String[] { "imgurl", "txt" },
				new int[] { R.id.item_image, R.id.item_text},200,200,false,share,"zhong");
		return simperAdapter;
	}
	
	public void inVipCard()
	{
		try{
			if(myapp.getSessionId() == null)
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
					intent.setClass(this,LoginMain.class);
					startActivity(intent);
					this.finish();
				}
			}
			else
			{
				loadThreadData();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void addVipCard()
	{
		try{
			if(myapp.getSessionId() == null)
			{
				String user = share.getString("user", "");
				String pwa = share.getString("pwa", "");
				if(user != null && !user.equals(""))
				{
					loadThreadDataLogin2(user,pwa);
				}
				else
				{
					Intent intent=new Intent();
					intent.setClass(this,LoginMain.class);
					startActivity(intent);
					this.finish();
				}
			}
			else
			{
				loadThreadAddCards();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadAddCards()
	{
		new Thread(){
			public void run(){
				Message msg = new Message();
				msg.what = 4;
				JSONObject jobj = null;
				try {
					
					JSONObject jobj2 = api.getCards(storeId);
					Map<String,Object> nmap = new HashMap<String,Object>();
					String storeTyle = "";
					String province = "";
					if(jobj2 != null)
					{
						JSONArray jArr = (JSONArray) jobj2.get("data");
//						List<Map<String,Object>> list = HomePage.getMyCardList(jArr);
//						nmap = list.get(0);
						storeTyle = (String)nmap.get("storeType");
						province = (String)nmap.get("province");
					}
					
					jobj = api.addCards(myapp.getPfprofileId(),storeId,businessId,storeTyle,province);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg.obj = jobj;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadDataLogin(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;	
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.login(lname,paw);
					
					msg.obj = jobj;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadDataLogin2(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 5;	
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.login(lname,paw);
					
					msg.obj = jobj;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadDataLogin3(final String lname,final String paw)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 6;	
				JSONObject jobj;
				U.dout(0);
				
				try {
					jobj = api.login(lname,paw);
					
					msg.obj = jobj;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try{
					List<Map<String,Object>> list = null;
					if(myapp.getUpdatetag().equals("1"))
					{
						if(myapp.getMyCardsAll() != null)
						{
							list = myapp.getMyCardsAll();
						}
						else
						{
							JSONObject jobj = api.getMyCardsAll("1");
							if(jobj != null)
							{
								JSONArray jArr = (JSONArray) jobj.get("data");
//								list = HomePage.getMyCardList(jArr);
								myapp.setMyCardsAll(list);
							}
						}
					}
					else
					{
						JSONObject jobj = api.getMyCardsAll("1");
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
//							list = HomePage.getMyCardList(jArr);
							myapp.setMyCardsAll(list);
						}
					}
					
					msg.obj = list;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(myapp.getSessionId() == null)
			{
				Intent startMain = new Intent(Intent.ACTION_MAIN);
		         startMain.addCategory(Intent.CATEGORY_HOME);
		         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		         startActivity(startMain);
		         System.exit(0);
			}
			else
			{
				this.setResult(RESULT_OK, getIntent());
				this.finish();
			}
			return false;
		}
		return false;
	}
	
	private Handler handler = new Handler() {
		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Bitmap bitm = (Bitmap)msg.obj;
				lbimgview.setImageBitmap(bitm);
				break;
			case 1:
				Bitmap bitm2 = (Bitmap)msg.obj;
				advertiseimg.setImageBitmap(bitm2);
				break;
			case 2:
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				openVipCard(dlist);
				break;
			case 3:
				JSONObject jobj = (JSONObject)msg.obj;
				try{
					String tag = "";
					if(jobj != null)
					{
						tag = (String)jobj.get("tag");
						if(tag.equals("Success"))
						{
							String sessionid = jobj.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj.getString("email");
							saveSharedPerferences("email",email);
							String lname = share.getString("user", "");
							String username = jobj.getString("username");
							String sex = jobj.getString("sex");
							String area = jobj.getString("area");
							String signature = jobj.getString("signature");
							myapp.setMyaccount(lname);
							myapp.setUserName(username);
							myapp.setMysex(sex);
							myapp.setMyarea(area);
							myapp.setMySignature(signature);
							if(jobj.has("userimg"))
							{
								String userimg = jobj.getString("userimg");
								myapp.setUserimg(userimg);
								loadThreadData();
							}
							
						}
						else
						{
							Intent intent=new Intent();
							intent.setClass(AdvertiseNotificationActivity.this,LoginMain.class);
							startActivity(intent);
							AdvertiseNotificationActivity.this.finish();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 4:
				try {
					JSONObject job = (JSONObject) msg.obj;
					String success = job.getString("success");
					if(success.equals("true"))
					{
						JSONObject jobjs = api.getMyCardsAll("1");
						if(jobjs != null)
						{
							JSONArray jArr = (JSONArray) jobjs.get("data");
//							List<Map<String,Object>> list = HomePage.getMyCardList(jArr);
//							myapp.setMyCardsAll(list);
						}
						
						makeText(AdvertiseNotificationActivity.this.getString(R.string.coupon_lable_25));
						myapp.setUpdatetag("0");
						btntag = "1";
						vipbtn.setText(AdvertiseNotificationActivity.this.getString(R.string.map_lable_14));
					}
					else
					{
						String msgstr = job.getString("msg");
						makeText(AdvertiseNotificationActivity.this.getString(R.string.coupon_lable_26)+msgstr);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 5:
				JSONObject jobj3 = (JSONObject)msg.obj;
				try{
					String tag = "";
					if(jobj3 != null)
					{
						tag = (String)jobj3.get("tag");
						if(tag.equals("Success"))
						{
							String sessionid = jobj3.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj3.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj3.getString("email");
							saveSharedPerferences("email",email);
							String lname = share.getString("user", "");
							String username = jobj3.getString("username");
							String sex = jobj3.getString("sex");
							String area = jobj3.getString("area");
							String signature = jobj3.getString("signature");
							myapp.setMyaccount(lname);
							myapp.setUserName(username);
							myapp.setMysex(sex);
							myapp.setMyarea(area);
							myapp.setMySignature(signature);
							if(jobj3.has("userimg"))
							{
								String userimg = jobj3.getString("userimg");
								myapp.setUserimg(userimg);
								loadThreadAddCards();
							}
							
						}
						else
						{
							Intent intent=new Intent();
							intent.setClass(AdvertiseNotificationActivity.this,LoginMain.class);
							startActivity(intent);
							AdvertiseNotificationActivity.this.finish();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 6:
				JSONObject jobj4 = (JSONObject)msg.obj;
				try{
					String tag = "";
					if(jobj4 != null)
					{
						tag = (String)jobj4.get("tag");
						if(tag.equals("Success"))
						{
							JSONObject jobjs = api.getMyCardsAll("1");
							if(jobjs != null)
							{
								JSONArray jArr = (JSONArray) jobjs.get("data");
//								List<Map<String,Object>> list = HomePage.getMyCardList(jArr);
//								myapp.setMyCardsAll(list);
							}
							String sessionid = jobj4.getString("sessionid");
							myapp.setSessionId(sessionid);
							String profileid = jobj4.getString("profileid");
							myapp.setPfprofileId(profileid);
							String email = jobj4.getString("email");
							saveSharedPerferences("email",email);
							String lname = share.getString("user", "");
							String username = jobj4.getString("username");
							String sex = jobj4.getString("sex");
							String area = jobj4.getString("area");
							String signature = jobj4.getString("signature");
							myapp.setMyaccount(lname);
							myapp.setUserName(username);
							myapp.setMysex(sex);
							myapp.setMyarea(area);
							myapp.setMySignature(signature);
							if(jobj4.has("userimg"))
							{
								String userimg = jobj4.getString("userimg");
								myapp.setUserimg(userimg);
								
								JSONObject jobj6 = api.isSelectCards(myapp.getPfprofileId(),storeId,businessId,null);
								vipbtn.setVisibility(View.VISIBLE);
								btntag = jobj6.getString("tag");
								if(btntag.equals("1"))
								{
									vipbtn.setText(AdvertiseNotificationActivity.this.getString(R.string.map_lable_14));
								}
								else
								{
									vipbtn.setText(AdvertiseNotificationActivity.this.getString(R.string.notification_lable_3));
								}
								vipbtn.setVisibility(View.VISIBLE);
								vipbtn.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										if(btntag.equals("1"))
										{
											inVipCard();
										}
										else
										{
											addVipCard();
										}
									}
								});
							}
							
						}
						else
						{
							Intent intent=new Intent();
							intent.setClass(AdvertiseNotificationActivity.this,LoginMain.class);
							startActivity(intent);
							AdvertiseNotificationActivity.this.finish();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openVipCard(List<Map<String,Object>> dlist)
	{
		try{
			JSONObject jobj = api.getCards(storeId);
			Map<String,Object> nmap = new HashMap<String,Object>();
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
//				List<Map<String,Object>> list = HomePage.getMyCardList(jArr);
//				nmap = list.get(0);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			int index = 0;
			for(int i=0;i<dlist.size();i++)
			{
				map = dlist.get(i);
				String bid = (String)map.get("businessId");
				if(businessId.equals(bid))
				{
					index = i;
					break;
				}
			}
			getMyCardMap(map,nmap);
			
			Intent intent = new Intent();
		    intent.setClass( this,CardsView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("advertiseNotification", "true");
			intent.putExtras(bundle);
			this.startActivity(intent);//开始界面的跳转函数
		    this.overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Bitmap getImageBitmap(String value,boolean b,int w,int h)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		System.out.println("menulist_imageurl==="+value);
		if(value == null)
			return null;
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
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			if(b)
				bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public Map<String,Object> getMyCardMap(Map<String,Object> map,Map<String,Object> oldmap){
		try{
				
				map.put("storeid", oldmap.get("storeid"));
				map.put("storeName", oldmap.get("storeName"));
				map.put("storePhone", oldmap.get("storePhone"));
				map.put("addressInfomation", oldmap.get("addressInfomation"));
				map.put("storeDesc", oldmap.get("storeDesc"));
				map.put("imgurl", oldmap.get("imgurl"));
				map.put("couponNumber", oldmap.get("couponNumber"));
				map.put("typeName", oldmap.get("typeName"));
				map.put("typesMapping", oldmap.get("typesMapping"));
				map.put("businessId", oldmap.get("businessId"));
				map.put("woof", oldmap.get("woof"));
				map.put("longItude", oldmap.get("longItude"));
	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
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
}
