package ms.activitys.hotel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.coupon.MyCouponView;
import ms.activitys.integral.GetIntegralHistoryActity;
import ms.activitys.map.BaiduMapRouteSearch;
import ms.activitys.map.PeripheryMapsActivity;
import ms.activitys.traffic.GetTicketsInfomationActivity;
import ms.activitys.travel.RealActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterHotel;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.MyScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



public class HotelActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
//	private int cardindex;
	private String hotelid;
	private ProgressBar pb;
//	private ScrollViewGroup viewGroup;
	private Gallery imageGallery;
	private Timer timer;
	private Animation myAnimation;
	private Animation myAnimation2;
	private Animation myAnimation3;
	private TableRow trow;
	private RelativeLayout hotelrl;
	private RelativeLayout gridrl;
	private boolean btntag = false;
	private GridView gridview;
	private Map<String,Object> hmap = new HashMap<String,Object>();
	private String isASttention;
//	private String maptag = "";
//	private String advertiseNotification;
	private ImageView mapimg;
	public static FileUtils fileUtil = new FileUtils();
	private SpecialAdapterHotel listItemAdapter;
	public static HotelActivity instance = null;
	public ScrollView scroll_view;
	public RelativeLayout gallery_layout;
	private int yuHeight;
	private boolean isOpen = false;
	private Animation animation;
	private Animation animation2;
	private LinearLayout content_layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
//		cardindex = this.getIntent().getExtras().getInt("index");
//		maptag = this.getIntent().getExtras().getString("tag");
//		advertiseNotification = this.getIntent().getExtras().getString("advertiseNotification");
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		hotelrl = (RelativeLayout)findViewById(R.id.hotel_rly);
		gridrl = (RelativeLayout)findViewById(R.id.itemgrid);
		gridview = (GridView)findViewById(R.id.gridview);
		
		imageGallery = (Gallery)findViewById(R.id.images_gallery);
//		viewGroup = (ScrollViewGroup) findViewById(R.id.scrollViewGroup);
		myAnimation= AnimationUtils.loadAnimation(this,R.anim.slide_left_in);
		myAnimation2= AnimationUtils.loadAnimation(this,R.anim.fade);
		
//		hmap = myapp.getMyCardsAll().get(cardindex);
//		hotelid = (String)hmap.get("storeid");
		hotelid = myapp.getAppstoreid();
		
		animation = AnimationUtils.loadAnimation(this, R.anim.scale_down);
		animation2 = AnimationUtils.loadAnimation(this, R.anim.scale_up);
		
		String[] card_menu_name_array2 = { getString(R.string.menu_lable_169), getString(R.string.menu_lable_170), getString(R.string.coupon_dowenloads), getString(R.string.menu_lable_171), getString(R.string.menu_lable_1), getString(R.string.menu_lable_2),getString(R.string.menu_lable_172),getString(R.string.menu_lable_39),
				  getString(R.string.cards_lable_2),getString(R.string.cards_lable_3),getString(R.string.cards_lable_4),getString(R.string.produce_discuss1),getString(R.string.menu_lable_173),getString(R.string.menu_lable_174),getString(R.string.menu_lable_175),getString(R.string.menu_lable_180),getString(R.string.menu_lable_156)};
		myapp.setCard_menu_name_array2(card_menu_name_array2);
//		myAnimation3= AnimationUtils.loadAnimation(this,R.anim.popup_about_show);
//		loadhotelPage();
//		loadHomeButton();
		loadScrollView();
		getMyCardListData();
//		loadGridView();
	}
	
	public void loadScrollView()
	{
		try{
			scroll_view = (ScrollView)findViewById(R.id.scroll_view);
			gallery_layout = (RelativeLayout)findViewById(R.id.gallery_layout);
			content_layout = (LinearLayout)findViewById(R.id.content_layout);
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) gallery_layout.getLayoutParams();
			yuHeight = linearParams.height;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getMyCardListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				
				try {
					JSONObject jobj = api.getMyOneCardInfo("1",hotelid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						List<Map<String,Object>> lists = myapp.getMyCardList(jArr);
						if(lists != null && lists.size() > 0)
						{
							hmap = lists.get(0);
							msg.obj = hmap;
							myapp.setHotelMap(hmap);
						}
						else
						{
							msg.obj = null;
						}
					}
					else
					{
						List<Map<String,Object>> lists = myapp.getMyCardsAll();
						if(lists != null && lists.size() > 0)
						{
							for(int i=0;i<lists.size();i++)
							{
								Map<String,Object> map = lists.get(i);
								String storeid = (String)map.get("storeid");
								if(hotelid.equals(storeid))
								{
									hmap = lists.get(0);
									msg.obj = hmap;
									myapp.setHotelMap(hmap);
									break;
								}
							}
						}
						else
						{
							msg.obj = null;
						}
//						msg.obj = null;
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			breakActivity();
			MainTabActivity.instance.onMinimizeActivity();
			return false;
		}
		return false;
	}
	
//	public void breakActivity()
//	{
//		if(maptag != null && !maptag.equals(""))
//		{
//			Intent intent = new Intent();
//			if(maptag.equals("storeinfo"))
//				intent.setClass( this,StoreViewActivity.class);
//			else if(maptag.equals("message"))
//				intent.setClass( this,MessageListActivity.class);
//			else if(maptag.equals("pmap"))
//				intent.setClass( this,PeripheryMapsActivity.class);
//			else
//				intent.setClass( this,MainTabActivity.class);
//		    Bundle bundle = new Bundle();
//		    bundle.putString("tag", "hotel");
////			bundle.putString("role", "Cleaner");
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//		    this.finish();//关闭显示的Activity
//		}
//		else if(advertiseNotification != null)
//		{
//			Intent intent = new Intent();
//		    intent.setClass( this,MainTabActivity.class);
//		    Bundle bundle = new Bundle();
////			bundle.putString("role", "Cleaner");
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//		    this.finish();//关闭显示的Activity
//		}
//		else
//		{
//		    HotelActivity.this.setResult(RESULT_OK, getIntent());
//		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//		    HotelActivity.this.finish();
//		}
//	}
	
	private void init() 
	{  
        timer = new Timer();  
        timer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                mHandler.sendEmptyMessage(1);  
            }  
        }, 5000, 5000); 
    } 
	
	private final Handler mHandler = new Handler() {  
        public void handleMessage(Message message) {  
            super.handleMessage(message);  
            switch (message.what) {  
                case 1:  
                	imageGallery.setSelection(imageGallery.getSelectedItemPosition() + 1);  
                	imageGallery.startAnimation(myAnimation);
                    break;  
            }  
        }  
    };
    
    @Override  
    protected void onDestroy() {  
        // TODO Auto-generated method stub  
    	if(timer != null)
    		timer.cancel();
    	timer = null;
    	imageGallery = null;
    	
    	Drawable dw = mapimg.getDrawable();
    	if(dw != null)
    	{
	    	Bitmap bitmap = ((BitmapDrawable)dw).getBitmap();
	    	mapimg.setImageBitmap(null);
	    	if (!bitmap.isRecycled()) {  
	    		bitmap.recycle();
			}
    	}
    	
    	if(listItemAdapter != null)
    		listItemAdapter.removeAllResources();
		System.gc();
        super.onDestroy();  
    }  
	
	public void loadhotelPage()
	{
		try{
			String storeName = (String)hmap.get("storeName");
			String periphery = (String)hmap.get("periphery");
			String storeDesc = (String)hmap.get("storeDesc");
			String addressInfomation = (String)hmap.get("addressInfomation");
			String roomIntroduction = (String)hmap.get("roomIntroduction");
			String trafficWay = (String)hmap.get("trafficWay");
			String startingPrice = (String)hmap.get("startingPrice");
			String score = (String)hmap.get("score");
			String comments = (String)hmap.get("comments");
			
			loadThreadImageData();
			
//			RatingBar rbar = (RatingBar)findViewById(R.id.user_rating_bar);
//			rbar.setRating(Float.valueOf(score));
//			
//			TextView userrating = (TextView)findViewById(R.id.user_rating_text_view);
//			userrating.setText(comments + " " + getString(R.string.menu_lable_159));
			
			TextView nametxt = (TextView)findViewById(R.id.name_text_view);
			nametxt.setText(storeName);
			
			trow = (TableRow)findViewById(R.id.amenities_table_row);
			loadThreadFacility();
			
			TextView periphery_txt = (TextView)findViewById(R.id.periphery_txt);
			periphery_txt.setText(periphery);
			
			TextView characteristic_txt = (TextView)findViewById(R.id.characteristic_txt);
			characteristic_txt.setText(storeDesc);
			
			TextView address_txt = (TextView)findViewById(R.id.address_txt);
			address_txt.setText(addressInfomation);
			
			TextView room_txt = (TextView)findViewById(R.id.room_txt);
			room_txt.setText(roomIntroduction);
			
			TextView traffic_txt = (TextView)findViewById(R.id.traffic_txt);
			traffic_txt.setText(trafficWay);
			
			TextView price_text_view = (TextView)findViewById(R.id.price_text_view);
			price_text_view.setText(startingPrice+"￥");
			
//			ImageButton addbtn = (ImageButton)findViewById(R.id.address_btn);
//			addbtn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showStopAddress();
//				}
//			});
			
			mapimg = (ImageView)findViewById(R.id.map_img);
			mapimg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showStopAddress();
				}
			});
			loadMapImage();
			
//			Button book_now_button = (Button)findViewById(R.id.book_now_button);
//			book_now_button.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
////					Intent intent = new Intent();
////				    intent.setClass(HotelActivity.this,HotelReservation.class);
////				    Bundle bundle = new Bundle();
////					bundle.putString("index", String.valueOf(cardindex));
////					intent.putExtras(bundle);
////				    startActivity(intent);//开始界面的跳转函数
////				    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//					Intent intent = new Intent();
//					intent.setClass(HotelActivity.this,HtmlWebView.class);
//				    Bundle bundle = new Bundle();
////					bundle.putInt("index", cardindex);
//					bundle.putString("hotel", "hotel");
//					intent.putExtras(bundle);
////				    startActivity(intent);//开始界面的跳转函数
////				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//					MainTabActivity.instance.loadLeftActivity(intent);
//				}
//			});
			
			imageGallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(timer != null)
					{
			    		timer.cancel();
			    		timer = null;
					}
					if(isOpen)
					{
						RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) gallery_layout.getLayoutParams();
						linearParams.height = yuHeight;// 当控件的高强制设成75象素
						
//						 LayoutAnimationController controller = new LayoutAnimationController(animation2, 0.5f);
//			             gallery_layout.setLayoutAnimation(controller);
			             gallery_layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
			             content_layout.setVisibility(View.VISIBLE);
		        		 // 开启移动动画  
//		                TranslateAnimation ta = new TranslateAnimation(0, 0, scroll_view.getHeight(),yuHeight);
//						ScaleAnimation ta = new ScaleAnimation(0, 0, scroll_view.getHeight(),yuHeight, 0.5f, 0.5f);
//		                ta.setDuration(1000);  
//		                gallery_layout.startAnimation(animation);
//		                LayoutAnimationController controller = new LayoutAnimationController(ta, 0.5f);
//		                gallery_layout.setLayoutAnimation(controller);
//		                gallery_layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
		                isOpen = false;
					}
					else
					{
						RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) gallery_layout.getLayoutParams();
						linearParams.height = scroll_view.getHeight();// 当控件的高强制设成75象素
						
						
		        		 // 开启移动动画  
//		                TranslateAnimation ta = new TranslateAnimation(0, 0, yuHeight,scroll_view.getHeight());
//						ScaleAnimation ta = new ScaleAnimation(0, 0,yuHeight,scroll_view.getHeight(), 0, 0);
//		                ta.setDuration(1000);
		                gallery_layout.startAnimation(animation);
//		                LayoutAnimationController controller = new LayoutAnimationController(animation, 0.5f);
//		                gallery_layout.setLayoutAnimation(controller);
						gallery_layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
//						gallery_layout.startAnimation(animation);
		                content_layout.setVisibility(View.GONE);
		                isOpen = true;
					}
//					imageGallery.setSelection(arg2);
				}
			});
			
			imageGallery.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(timer != null)
					{
			    		timer.cancel();
			    		timer = null;
					}
					return false;
				}
			});
			
//			LinearLayout urlayout = (LinearLayout)findViewById(R.id.user_rating_layout);
//			urlayout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent();
//				    intent.setClass( HotelActivity.this,ReviewUserListActivity.class);
//				    Bundle bundle = new Bundle();
//					bundle.putInt("index", cardindex);
//					intent.putExtras(bundle);
//				    startActivity(intent);//开始界面的跳转函数
//				    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//				}
//			});
			
//			ImageButton break_btn = (ImageButton)findViewById(R.id.mone_button);
//			break_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					breakActivity();
//				}
//			});
			
			ImageButton periphery_btn = (ImageButton)findViewById(R.id.periphery_btn);
	        periphery_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showPeripherMap();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//设置标题栏右侧按钮的作用
	public void btnmainright(View v) {  
		Intent intent = new Intent (HotelActivity.this,MainTopRightDialog.class);			
		startActivity(intent);	
	}
	
	public void loadMapImage()
	{
		List<Map<String,Object>> list = myapp.getMyCardsAll();
//		Map map = list.get(Integer.valueOf(cardindex));
		Map map = hmap;
		
		final String woof = (String)map.get("woof");
		final String longItude = (String)map.get("longItude");
		
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				try{
//				String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x250&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
				String url = "http://api.map.baidu.com/staticimage?width=470&height=350&center="+longItude+","+woof+"&markers="+longItude+","+woof+"&zoom=17";
				Bitmap bitmap = returnBitMap(url);
				if(bitmap != null)
					msg.obj = bitmap;
				else
					msg.obj = null;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public Bitmap returnBitMap(String url) {
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
			bitmap = BitmapFactory.decodeStream(is);
//			if(dstWidth > 1)
//				bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
//			Bitmap newimg = getRes();
//			bitmap = createBitmap(bitmap,newimg);
//			if(b)
//				bitmap = getRoundedCornerBitmap(bitmap,12);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public void loadHomeButton()
	{
		try{
//			final ImageButton btn = (ImageButton)findViewById(R.id.home_btn);
//			btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(btntag)
//					{
//						hotelrl.setVisibility(View.VISIBLE);
//						gridrl.setVisibility(View.GONE);
//						hotelrl.startAnimation(myAnimation2);
////						gridrl.setAnimation(myAnimation2);
//						btn.setImageResource(R.drawable.navigation_home_nm);
//						init();
//						btntag = false;
//					}
//					else
//					{
//						hotelrl.setVisibility(View.GONE);
//						gridrl.setVisibility(View.VISIBLE);
//						gridrl.setAnimation(myAnimation2);
////						hotelrl.setAnimation(myAnimation2);
//						btn.setImageResource(R.drawable.navigation_home_on);
//						if(timer != null)
//						{
//				    		timer.cancel();
//				    		timer = null;
//						}
//						btntag = true;
//					}
//				}
//			});
			
			String couponNumber = (String)hmap.get("couponNumber");
			final String businessId = (String)hmap.get("businessId");
			isASttention = (String)hmap.get("isASttention");
//			RelativeLayout orther_store = (RelativeLayout)findViewById(R.id.orther_store);
//			orther_store.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showOtherStore(businessId);
//				}
//			});
			
//			final ImageView imgc = (ImageView)findViewById(R.id.imgc);
//			RelativeLayout collect_rly = (RelativeLayout)findViewById(R.id.collect_rly);
//			if(isASttention.equals("1"))//1为否，0为是
//			{
//				imgc.setImageResource(R.drawable.ic_fave);
//			}
//			else
//			{
//				imgc.setImageResource(R.drawable.ic_fave_sel);
//			}
//			collect_rly.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					String pkid = (String)hmap.get("pkid");
//					if(StreamTool.isNetworkVailable(HotelActivity.this))
//					{
//						if(isASttention.equals("1"))//1为否，0为是
//						{
//							try{
//								JSONObject jobj = api.isASttention(pkid,"1");
//								if(jobj != null)
//								{
//									imgc.setImageResource(R.drawable.ic_fave_sel);
//									isASttention = "0";
//									hmap.put("isASttention", isASttention);
//									hmap.put("xinxin", R.drawable.ic_star_small);
//								}
//							}catch(Exception ex){
//								ex.printStackTrace();
//							}
//						}
//						else
//						{
//							try{
//								JSONObject jobj = api.notASttention(pkid,"1");
//								if(jobj != null)
//								{
//									imgc.setImageResource(R.drawable.ic_fave);
//									isASttention = "1";
//									hmap.put("isASttention", isASttention);
//									hmap.put("xinxin", null);
//								}
//							}catch(Exception ex){
//								ex.printStackTrace();
//							}
//						}
//					}
//					else
//					{
//						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
//					}
//				}
//			});
			
//			RelativeLayout barcode_rly = (RelativeLayout)findViewById(R.id.barcode_rly);
//			barcode_rly.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showCardTiaoXinMa();
//				}
//			});
			
			if(Integer.valueOf(couponNumber) > 0)
			{
				TextView couponnum = (TextView)findViewById(R.id.item_badge_text_coupon);
				couponnum.setVisibility(View.VISIBLE);
				couponnum.setText(couponNumber);
			}
			RelativeLayout coupon_rly = (RelativeLayout)findViewById(R.id.coupon_rly);
			coupon_rly.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(HotelActivity.this))
						showDownloadCouponView();
					else
						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			RelativeLayout phone_rly = (RelativeLayout)findViewById(R.id.phone_rly);
			phone_rly.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phoneNumber = (String)hmap.get("storePhone");
					phoneNumber = phoneNumber.replaceAll("-", "");
//					if(isPhoneNumberValid(phoneNumber))
//					{
//						 Intent myIntentDial = new Intent( Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));
//			             startActivity(myIntentDial);  
					Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber));
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		            startActivity(intent);
//					}
//					else
//					{
//						makeText(HotelActivity.this.getString(R.string.cards_lable_11));
//					}
				}
			});
			
//			RelativeLayout real_rly = (RelativeLayout)findViewById(R.id.real_rly);
//			real_rly.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					String woof = (String)hmap.get("woof");
//					String longItude = (String)hmap.get("longItude");
//					String storeName = (String)hmap.get("storeName");
//					showRealActivity(woof,longItude,storeName);
//				}
//			});
			
//			RelativeLayout userinfo_rly = (RelativeLayout)findViewById(R.id.userinfo_rly);
//			userinfo_rly.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(StreamTool.isNetworkVailable(HotelActivity.this))
//						showCardDetails();
//					else
//						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
//				}
//			});
			
//			RelativeLayout comment_rly = (RelativeLayout)findViewById(R.id.comment_rly);
//			comment_rly.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//				}
//			});
			
			RelativeLayout map_rly = (RelativeLayout)findViewById(R.id.map_rly);
			map_rly.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(HotelActivity.this))
						showStopAddress();
					else
						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			RelativeLayout peripher_rly = (RelativeLayout)findViewById(R.id.peripher_rly);
			peripher_rly.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(HotelActivity.this))
						showPeripherMap();
					else
						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			RelativeLayout traffic_rly = (RelativeLayout)findViewById(R.id.traffic_rly);
			traffic_rly.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(HotelActivity.this))
						showTrafficView();
					else
						makeText(HotelActivity.this.getString(R.string.seting_lable_7));
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadGridView()
	{
		try{
			gridview.setAdapter(getMenuAdapter(myapp.getCard_menu_name_array2(), myapp.getCard_menu_image_array2()));
			
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					switch (arg2) {
						case 0:// 电话
							String phoneNumber = (String)hmap.get("storePhone");
							phoneNumber = phoneNumber.replaceAll("-", "");
							if(isPhoneNumberValid(phoneNumber))
							{
//								 Intent myIntentDial = new Intent( Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));  
//					             startActivity(myIntentDial);  
								Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber)); 
					            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					            startActivity(intent);
							}
							else
							{
								makeText(HotelActivity.this.getString(R.string.cards_lable_11));
							}
							break;
						case 1:// 预订记录
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showMyReservationsView();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 2:// 优惠券下载
							if(StreamTool.isNetworkVailable(HotelActivity.this))
								showDownloadCouponView();
							else
								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 3:// 我的优惠券
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showMyCouponView();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 4:// 商品
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showStoreMume();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 5:// 购物车
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showShoppingCart();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 6:// 订单
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showOrderStatus();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 7:// 积分历史
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showIntegralHistory();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 8:// 积分换物
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showIntegralType();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 9:// 积分交易
							
							break;
						case 10:// 捐献积分
							
							break;
						case 11:// 发表评论
							
							break;
						case 12:// 个人信息
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//								showCardDetails();
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 13:// 交通查询
							if(StreamTool.isNetworkVailable(HotelActivity.this))
								showTrafficView();
							else
								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 14:// 实景导航
//							if(StreamTool.isNetworkVailable(HotelActivity.this))
//							{
//								String woof = (String)hmap.get("woof");
//								String longItude = (String)hmap.get("longItude");
//								String storeName = (String)hmap.get("storeName");
//								showRealActivity(woof,longItude,storeName);
//							}
//							else
//								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 15:// 地图
							if(StreamTool.isNetworkVailable(HotelActivity.this))
								showStopAddress();
							else
								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
						case 16:// 周边
							if(StreamTool.isNetworkVailable(HotelActivity.this))
								showPeripherMap();
							else
								makeText(HotelActivity.this.getString(R.string.seting_lable_7));
							break;
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadImageData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getProductImageList(hotelid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							List<Map<String,Object>> imglist = fileUtil.getStoreImageListBitmap(hotelid);
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("list", imglist);
							map.put("cid", hotelid);
							msg.obj = map;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getImageList(jArr);
//							List<Map<String,Object>> imglist = fileUtil.createStoreImageFilePath(hotelid,list);
//							loadSaveImage(list);
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("list", list);
							map.put("cid", hotelid);
							msg.obj = map;
						}
					}
					else
					{
						List<Map<String,Object>> imglist = fileUtil.getStoreImageListBitmap(hotelid);
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("list", imglist);
						map.put("cid", hotelid);
						msg.obj = map;
					}
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
//	public void loadSaveImage(final List<Map<String,Object>> list)
//	{
//		new Thread() {
//			public void run() {
//				
//				try{
//					List<Map<String,Object>> imglist = fileUtil.createStoreImageFilePath(hotelid,list);
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		}.start();
//	}
	
	public void loadThreadFacility()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getHotelFacilityList(hotelid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getFacilityList(jArr);
							msg.obj = list;
						}
					}
					
				}catch(Exception ex){
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
				if(msg.obj != null && imageGallery != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)map.get("list");
					if(dlist2 != null && dlist2.size() > 0)
					{
						String cid = (String)map.get("cid");
						// 生成适配器的Item和动态数组对应的元素
						listItemAdapter = new SpecialAdapterHotel(HotelActivity.this, dlist2,// 数据源
								R.layout.item_menu,// ListItem的XML实现
								// 动态数组与ImageItem对应的子项
								new String[] { "img" },
								// ImageItem的XML文件里面的一个ImageView,两个TextView ID
								new int[] { R.id.item_image },900,551,false,share,"max");
						
						// 添加并且显示
						if(listItemAdapter != null)
						{
							imageGallery.setAdapter(listItemAdapter);
						}
//						pb.setVisibility(View.GONE);
						imageGallery.setVisibility(View.VISIBLE);
						init();
					}
					pb.setVisibility(View.GONE);
				}
				else
				{
					pb.setVisibility(View.GONE);
				}
				break;
			case 1:
				if(msg.obj != null)
				{
					List<Map<String,String>> dlist = (List<Map<String,String>>)msg.obj;
					for(int i=0;i<dlist.size();i++)
					{
						Map<String,String> map = dlist.get(i);
						String fname = map.get("fname");
						String fmaping = map.get("fmaping");
						View view = LayoutInflater.from(HotelActivity.this).inflate(R.layout.item_menu3,null);
						TextView item_text = (TextView)view.findViewById(R.id.item_text);
						item_text.setText(fname);
						ImageView img = (ImageView)view.findViewById(R.id.item_image);
						int imgid = HotelActivity.this.getResources().getIdentifier(HotelActivity.this.getPackageName()+":drawable/"+fmaping,null,null);
						img.setImageResource(imgid);
						trow.addView(view);
					}
				}
				break;
			case 2:
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					mapimg.setImageBitmap(bitmap);
				break;
			case 3:
				Map<String,Object> maps = (Map<String,Object>)msg.obj;
//				if(maps != null)
				loadHomeButton();
				loadhotelPage();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public List<Map<String,Object>> getImageList(JSONArray jArr)
	{
		List<Map<String,Object>> imglist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String localeString = ""; 
				if(dobj.has("localeString"))
					localeString = (String) dobj.get("localeString"); 
				
				String imageUrl = ""; 
				if(dobj.has("imageUrl"))
				{
					imageUrl = (String) dobj.get("imageUrl");
//					imageUrl = imageUrl.replaceAll("mill.ms.cn", "223.4.115.110");
				}
				
				String typeid = ""; 
				if(dobj.has("typeid"))
					typeid = (String) dobj.get("typeid");
				
				String dataName = ""; 
				if(dobj.has("dataName"))
				{
					dataName = (String) dobj.get("dataName");
					String [] strs = dataName.split("\\.");
					dataName = strs[0];
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("localeString", localeString);
				map.put("imageUrl", imageUrl);
				map.put("typeid", typeid);
				map.put("dataName", dataName);
				if(typeid.indexOf("image") >= 0)
				{
//					String str = imageUrl.substring(0,imageUrl.lastIndexOf("."));
//					String str2 = imageUrl.substring(imageUrl.lastIndexOf("."),imageUrl.length());
//					
//					imageUrl = str+"_zhong"+str2;
//					Bitmap bimp = getImageBitmap(imageUrl);
//					map.put("img", bimp);
//					imglist.add(map);
					
					if(!fileUtil.isFileExist2("/" + hotelid + "/" + "infoimage"))
							fileUtil.createUserFile("/" + hotelid + "/" + "infoimage");
					
					if(!fileUtil.isFileExist2("/" + hotelid + "/" + "infoimage/"+dataName))
					{
						Bitmap bitmap = myapp.getImageBitmap(imageUrl);
						String furl = fileUtil.getStoreImageFilePath(hotelid, dataName);
						myapp.saveMyBitmap(furl, bitmap);
						map.put("img", bitmap);
					}
					else
					{
						String furl = fileUtil.getStoreImageFilePath(hotelid, dataName);
						Bitmap bitmap = getLoacalBitmap(furl);
						map.put("img", bitmap);
					}
					imglist.add(map);
				}
			
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return imglist;
	}
	
	public List<Map<String,Object>> getFacilityList(JSONArray jArr)
	{
		List<Map<String,Object>> imglist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String fname = ""; 
				if(dobj.has("fname"))
					fname = (String) dobj.get("fname"); 
				
				String fmaping = ""; 
				if(dobj.has("fmaping"))
					fmaping = (String) dobj.get("fmaping");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("fname", fname);
				map.put("fmaping", fmaping);

				imglist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return imglist;
	}
	
	public Bitmap getImageBitmap(String value)
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
		    
			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu2, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}
	
	public void showStopAddress()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,LocalMap.class);
			intent.setClass( this,BaiduMapRouteSearch.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("index", String.valueOf(cardindex));
			bundle.putString("tag","hotel");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showPeripherMap()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,PeripheryMapActivity.class);
			intent.setClass( this,PeripheryMapsActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", cardindex);
			bundle.putString("tag", "hotel");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//	public void showCardDetails()
//	{
//		try{
//			Intent intent = new Intent();
//		    intent.setClass(this,UserInfoDetailed.class);
//		    Bundle bundle = new Bundle();
//			bundle.putInt("index", cardindex);
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
//	public void showOtherStore(String businessId)
//	{
//		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,OtherStore.class);
//		    Bundle bundle = new Bundle();
//			bundle.putString("businessId",businessId);
//			bundle.putInt("index", cardindex);
//			intent.putExtras(bundle);
////		    startActivity(intent);//开始界面的跳转函数
//			startActivityForResult(intent, 1); 
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 显示下载优惠券界面
	 */
	public void showDownloadCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("index", String.valueOf(cardindex));
			bundle.putString("tag", "download");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showRealActivity(String woof,String longItude,String storeName)
	{
		Intent intent = new Intent();
	    intent.setClass( this,RealActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("longtude", longItude);
		bundle.putString("woof", woof);
		bundle.putString("name", storeName);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	/**
	 * 显示我的预定页面
	 */
//	public void showMyReservationsView()
//	{
//		try{
//			List<Map<String,Object>> list = myapp.getMyCardsAll();
//			Map map = list.get(Integer.valueOf(cardindex));
//		
//			String typesMapping = (String)map.get("typesMapping");
//			if(typesMapping == null)
//				typesMapping = "01";
//			String typeName = (String)map.get("typeName");
//			if(typesMapping.equals("09"))
//			{
//				Intent intent = new Intent();
//			    intent.setClass( this,MyHotelReservation.class);
//			    Bundle bundle = new Bundle();
//				bundle.putString("index", String.valueOf(cardindex));
//				intent.putExtras(bundle);
//			    startActivity(intent);//开始界面的跳转函数
//			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//			}
//			else
//			{
//				makeText(typeName+this.getString(R.string.cards_lable_18));
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 显示我的优惠券界面
	 */
//	public void showMyCouponView()
//	{
//		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,MyCouponView.class);
//		    Bundle bundle = new Bundle();
//			bundle.putString("index", String.valueOf(cardindex));
//			bundle.putString("tag", "my");
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
////		    this.finish();//关闭显示的Activity
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 该门店下的菜单
	 */
//	public void showStoreMume()
//	{
//		try{
//			Intent intent = new Intent();
////		    intent.setClass( this,MyMenuListView.class);
//			intent.setClass(this, ProductTypeActivity.class);
//		    Bundle bundle = new Bundle();
//			bundle.putInt("index", cardindex);
//			bundle.putString("map", maptag);
//			bundle.putString("advertiseNotification", advertiseNotification);
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
////		    this.finish();//关闭显示的Activity
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 我的购物车
	 */
//	public void showShoppingCart()
//	{
//		try{
//			Intent intent = new Intent();
////		    intent.setClass( this,MyMenuListView.class);
//			intent.setClass(this, ProductShoppingCartActivity.class);
//		    Bundle bundle = new Bundle();
//			bundle.putInt("index", cardindex);
//			bundle.putString("map", maptag);
//			bundle.putString("advertiseNotification", advertiseNotification);
//			bundle.putString("storeid", hotelid);
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
////		    this.finish();//关闭显示的Activity
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 显示我在该店的订单状态
	 */
//	public void showOrderStatus()
//	{
//		try{
//			Intent intent = new Intent();
////		    intent.setClass( this,MyOrderStatus.class);
//			intent.setClass( this,MyOrederListActivity.class);
//		    Bundle bundle = new Bundle();
//			bundle.putString("appliescStoreid", hotelid);
//			bundle.putInt("index", cardindex);
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 我的积分换物
	 */
	public void showIntegralType()
	{
//		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,GetIntegralTypeActivity.class);
//		    Bundle bundle = new Bundle();
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
	}
	
	public void showTrafficView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass(this,GetTicketsInfomationActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", cardindex);
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 我的积分历史
	 */
	public void showIntegralHistory()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,GetIntegralHistoryActity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showCardTiaoXinMa(){
		try{
			String cardNo = (String)hmap.get("cardNo");
			View view = LayoutInflater.from(this).inflate(R.layout.card_tiaoxinma_view,null);
			
			ImageView imgview = (ImageView)view.findViewById(R.id.tiaoma_img);
			if(StreamTool.isNetworkVailable(this))
				imgview.setImageBitmap(getImageBitmap("http://"+myapp.getHost()+":80/customize/control/BarcodeServlet?msg="+cardNo));
			else
			{
				Bitmap bitm = (Bitmap)hmap.get("tiaoimg");
				imgview.setImageBitmap(bitm);
			}
			
			new AlertDialog.Builder(this).setTitle(this.getString(R.string.cards_lable_16)).setView(view).setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static boolean isPhoneNumberValid(String phoneNumber){  
        boolean isValid = false;  
      //-------------手机号码，以1开始，13,15,18,19,为合法，后根9位数字------
        String expression = "[1]{1}[3,5,8,6]{1}[0-9]{9}";  
      //-------------电话号码,以0开始,不含括号,不含横杠------------
        String expression2 = "[0]{1}[0-9]{2,3}[0-9]{7,8}";  
        CharSequence inputStr = phoneNumber;  
        Pattern pattern = Pattern.compile(expression);  
        Matcher matcher = pattern.matcher(inputStr);  
        Pattern pattern2 = Pattern.compile(expression2);  
        Matcher matcher2 = pattern2.matcher(inputStr);  
        if(matcher.matches()||matcher2.matches()){  
            isValid = true;  
        }  
          
        return isValid;  
    }  
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void openHoneView()
	{
		String phoneNumber = (String)hmap.get("storePhone");
		phoneNumber = phoneNumber.replaceAll("-", "");
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        startActivity(intent);
	}
	
	public void onpenBooking(View v)
	{
		Intent intent = new Intent();
		intent.setClass(HotelActivity.this,HtmlWebView.class);
	    Bundle bundle = new Bundle();
//		bundle.putInt("index", cardindex);
		bundle.putString("hotel", "hotel");
		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		MainTabActivity.instance.loadLeftActivity(intent);
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
			// 瑙ｅ喅鍔犺浇鍥剧墖 鍐呭瓨婧㈠嚭鐨勯棶棰�
			// Options 鍙繚瀛樺浘鐗囧昂瀵稿ぇ灏忥紝涓嶄繚瀛樺浘鐗囧埌鍐呭瓨
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 缂╂斁鐨勬瘮渚嬶紝缂╂斁鏄緢闅炬寜鍑嗗鐨勬瘮渚嬭繘琛岀缉鏀剧殑锛屽叾鍊艰〃鏄庣缉鏀剧殑鍊嶆暟锛孲DK涓缓璁叾鍊兼槸2鐨勬寚鏁板�,鍊艰秺澶т細瀵艰嚧鍥剧墖涓嶆竻鏅�
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
	
	 /** 
     * 停止 
     */  
    @Override  
    protected void onStop() {  
        Log.i("TAG-onStop", "onStop()------------yin");  
//        imageGallery.setAdapter(null);
        super.onStop();  
    } 
    
}
