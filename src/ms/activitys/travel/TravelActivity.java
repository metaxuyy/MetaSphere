package ms.activitys.travel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import ms.activitys.R;
//import ms.activitys.map.AttractionsMapActivity;
import ms.activitys.product.ProductImageSwitcher;
import ms.globalclass.ListViewAdapter;
import ms.globalclass.TomorrowWeatherPullParse;
import ms.globalclass.TomorrowWeatherVO;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

public class TravelActivity extends Activity implements OnScrollListener{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private String pagetag = "list";
	private int index;
	
	private ProgressBar pb;
//	private ProgressBar pb2;
	private ViewFlipper mViewFlipper;
	private ListView slistView;
	private String clicktag = "1";
	private TextView tvShow;
    private List<List<String>> weatherList=null;
    private ImageView lbimgview;
    private String pkidsel;
    private TextView txtlbdoc;
    private ListViewAdapter listItemAdapter;
    private int listCount;
    private int page = 0;
    private boolean islast = false;
    private int istag = 0;
    private TextView textView;
    private TextView loadbutton;
    private List<Map<String,Object>> traveldatalist = new ArrayList<Map<String,Object>>();
    private Map<String,Object> storemap;
    private List<TomorrowWeatherVO> tvolist;
    
    /**
	 * 设置布局显示为目标有多大就多大
	 */
    private LayoutParams WClayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    /**
	 * 设置布局显示目标最大化
	 */
    private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    
    LinearLayout loadingLayout;
    private int lastItem = 0; 
    private ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_list_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		index = this.getIntent().getExtras().getInt("index");
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		storemap = list.get(index);
		
		pb = (ProgressBar)findViewById(R.id.probar);
//		pb2 = (ProgressBar)findViewById(R.id.probar2);
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		showTravelList();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("list"))
				{
					TravelActivity.this.setResult(RESULT_OK, getIntent());
					TravelActivity.this.finish();
				}
				else
				{
					pagetag = "list";
//					txtlbdoc.setVisibility(View.GONE);
					clicktag = "1";
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
	}
	
	public void showTravelList()
	{
		pagetag = "list";
		try{
			slistView = (ListView)findViewById(R.id.ListView_catalog);
			
			loadThreadData();
			
			//线性布局
//			LinearLayout layout = new LinearLayout(this);
//			layout.setLayoutParams(FFlayoutParams);
		   //设置布局 水平方向
//			layout.setOrientation(LinearLayout.HORIZONTAL);
//			layout.setBackgroundColor(Color.parseColor("#8f00000f"));
			
			RelativeLayout rlayout = new RelativeLayout(this);
			rlayout.setGravity(Gravity.CENTER);
//			rlayout.setBackgroundColor(Color.parseColor("#8f00000f"));
			rlayout.setBackgroundColor(Color.WHITE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			
			 //进度条
//			View view = View.inflate(this, R.layout.loading_process_dialog_anim, null);
			progressBar = new ProgressBar(this);
//			progressBar = (ProgressBar)view.findViewById(R.id.loading_process_dialog_progressBar);
			 //进度条显示位置
//			progressBar.setPadding(10, 0, 15, 0);
			progressBar.setId(1);
			progressBar.setIndeterminate(false);
			Resources res=getResources();
			progressBar.setIndeterminateDrawable(res.getDrawable(R.drawable.dialog_style_xml_icon));
//			layout.addView(progressBar, WClayoutParams);
			rlayout.addView(progressBar,lp);
//			rlayout.addView(view);
			
			textView = new TextView(this);
			textView.setText(this.getString(R.string.travel_lable_18));
			textView.setTextColor(Color.BLACK);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			
			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
			lp2.addRule(RelativeLayout.RIGHT_OF,1);
			
//			layout.addView(textView, FFlayoutParams);
//			layout.setGravity(Gravity.CENTER);
			rlayout.addView(textView,lp2);
			
			if(!TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				progressBar.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);
				
				loadbutton = new TextView(this);
				loadbutton.setText(this.getString(R.string.travel_lable_19));
				loadbutton.setTextSize(24);
				loadbutton.setTextColor(Color.BLACK);
				loadbutton.setGravity(Gravity.CENTER);
				loadbutton.layout(0, 20, 0, 20);
				loadbutton.setBackgroundResource(R.drawable.search_bg);
	            
//	            layout.addView(loadbutton,FFlayoutParams);
				RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80);
				rlayout.addView(loadbutton,lp3);
	            
	            loadbutton.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						progressBar.setVisibility(View.VISIBLE);
						textView.setVisibility(View.VISIBLE);
						loadbutton.setVisibility(View.GONE);
						
//						page++;
						loadThreadData2();
					}
				});
			}
			
			loadingLayout = new LinearLayout(this);
			loadingLayout.addView(rlayout, FFlayoutParams);
			loadingLayout.setGravity(Gravity.CENTER);
			//添加到脚页显示
			slistView.addFooterView(loadingLayout);
	        
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					Map<String,Object> map = myapp.getTravelList().get(arg2);
					showTravelDetailed(map);
				}
			});
			
			ImageView mapview = (ImageView)findViewById(R.id.map_view_btn);
			mapview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
//				    intent.setClass(TravelActivity.this,AttractionsMapActivity.class);
				    Bundle bundle = new Bundle();
				    bundle.putInt("page", page);
				    bundle.putBoolean("islast", islast);
				    bundle.putInt("index", index);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			try{
				slistView.setOnScrollListener(this);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void onScroll(AbsListView v, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int rowCount = totalItemCount - (page*10);
		if(rowCount > 0 && rowCount < 11)
		{
			if(!islast)
			{
				loadingLayout.setVisibility(View.GONE);
				slistView.removeFooterView(loadingLayout);
				islast = true;
				istag++;
			}
			else
			{
				if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
				{
					slistView.setSelection((traveldatalist.size())); 
					istag++;
				}
				else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
				{
					slistView.setSelection((traveldatalist.size())); 
					istag++;
				}
			}
		}
		else
		{
			if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
			{
				slistView.setSelection((traveldatalist.size())); 
				istag++;
			}
			else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
			{
				slistView.setSelection((traveldatalist.size())); 
				istag++;
			}
		}
	}

	public void onScrollStateChanged(AbsListView v, int state) {
		if (lastItem == listItemAdapter.count
				&& state == OnScrollListener.SCROLL_STATE_IDLE) {
			if(TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				if(listCount > 0)
				{
	//				listItemAdapter.count += listCount;
					
					loadThreadData2();
				}
			}
//			else
//			{
//				slistView.removeFooterView(loadingLayout);
//			}
//			Log.i(TAG, "lastItem:" + lastItem);
		}
	}
	
	public void showTravelDetailed(Map<String,Object> map)
	{
		pagetag = "detailed";
		try{
			mViewFlipper.showNext();
			pkidsel = (String) map.get("pkid"); 
			final String landscapeName = (String) map.get("landscapeName");
			String landscapeNo = (String) map.get("landscapeNo");
			String landscapeDesc = (String) map.get("landscapeDesc");
			final String img = (String) map.get("img");
			final String notesnumber = (String) map.get("notesnumber");
			final String pinlun = (String) map.get("pinlun");
			final String voidname = (String)map.get("voidname");
			final String voidpath = (String)map.get("voidpath");
			final String storeId = (String)map.get("storeId");
			final String longtude = (String)map.get("longtude");
			final String woof = (String)map.get("woof");
			final String flatlong = (String)map.get("flatlong");
			final String flatwoof = (String)map.get("flatwoof");
			final String address = (String)map.get("address");
			final String price = (String)map.get("price");
			final float score= (Float)map.get("score");
			String openHour = (String)map.get("openHours");
			
			
			lbimgview = (ImageView)findViewById(R.id.lbimg);
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					
					Bitmap bitm = getImageBitmap(img,true);
					msg.obj = bitm;
					handler.sendMessage(msg);
				}
			}.start();
			
			RatingBar rb = (RatingBar)findViewById(R.id.rb);
			rb.setRating(score);
			
			TextView areaTicket = (TextView)findViewById(R.id.txt_areaTicket);
			if(price == null)
				areaTicket.setText(this.getString(R.string.attractions_lable_3));
			else
				areaTicket.setText(price);
			
			TextView areaAddress = (TextView)findViewById(R.id.txt_areaAddress);
			areaAddress.setText(address);
			
			TextView hourtext = (TextView)findViewById(R.id.txt_openHour);
			hourtext.setText(openHour);
			
			TextView txttitle = (TextView)findViewById(R.id.txtv1);
			txttitle.setText(landscapeName);
			
//			TextView txtjanjie = (TextView)findViewById(R.id.txt_jianjie);
//			txtjanjie.setText(landscapeName+"简介");
			
			txtlbdoc = (TextView)findViewById(R.id.lbdoc);
			txtlbdoc.setText(landscapeDesc);
			
//			TextView txt_youji = (TextView)findViewById(R.id.txt_youji);
//			if(notesnumber.equals("0"))
//				txt_youji.setText("游记（暂无）");
//			txt_youji.setText("游记（"+notesnumber+"篇）");
//			
//			TextView txt_yiyou = (TextView)findViewById(R.id.txt_yiyou);
//			if(pinlun.equals("0"))
//				txt_yiyou.setText("到此一游和评论（暂无）");
//			else
//				txt_yiyou.setText("到此一游和评论（"+pinlun+"人）");
			
			tvShow = (TextView)findViewById(R.id.weather_txt);
			
			if(myapp.getTvolist() != null && myapp.getTvolist().size() > 0)
			{
				showWeather(myapp.getTvolist());
			}
			else
			{
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 2;
						
						String strData = searchWeather();
						msg.obj = strData;
						handler.sendMessage(msg);
					}
				}.start();
			}
			
//			LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout1);
//			layout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(clicktag.equals("1"))
//					{
//						txtlbdoc.setVisibility(View.VISIBLE);
//						clicktag = "2";
//					}
//					else
//					{
//						txtlbdoc.setVisibility(View.GONE);
//						clicktag = "1";
//					}
//				}
//			});
			
			Button layout2 = (Button)findViewById(R.id.youji_btn);
			if(notesnumber.equals("0"))
				layout2.setText(this.getString(R.string.attractions_lable_4));
			layout2.setText(this.getString(R.string.attractions_lable_4)+"（"+notesnumber+"）");
			layout2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
//					if(Integer.valueOf(notesnumber) > 0)
//					{
						Intent intent = new Intent();
					    intent.setClass( TravelActivity.this,TravelNotesListActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("lvid", pkidsel);
						bundle.putString("storeId", storeId);
						intent.putExtras(bundle);
					    startActivity(intent);//开始界面的跳转函数
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//				    }
//					else
//					{
//						makeText("该景点没有游记");
//					}
				}
			});
			
			Button layout3 = (Button)findViewById(R.id.yiyon_btn);
			if(pinlun.equals("0"))
				layout3.setText(this.getString(R.string.attractions_lable_5));
			else
				layout3.setText(this.getString(R.string.attractions_lable_5)+"（"+pinlun+"）");
			layout3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Intent intent = new Intent();
				    intent.setClass( TravelActivity.this,TravelCommentListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("lsid", pkidsel);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			Button layout4 = (Button)findViewById(R.id.void_btn);
			layout4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(voidname.equals(""))
						makeText(TravelActivity.this.getString(R.string.attractions_lable_6));
					else
					{
//						if(voidname.indexOf(".mp4") >= 0)
//						{
//							Intent intent = new Intent();
//						    intent.setClass( TravelActivity.this,VideoViewDemo.class);
//						    Bundle bundle = new Bundle();
//							bundle.putString("path", voidpath);
//							bundle.putString("name", landscapeName);
//							intent.putExtras(bundle);
//						    startActivity(intent);//开始界面的跳转函数
//						    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//						}
//						else
//						{
							Intent it = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(voidpath);
							it.setDataAndType(uri, "video/mp4");
							startActivity(it);
//						}
					}
				}
			});
			
			ImageView commentBtn = (ImageView)findViewById(R.id.comment_btn);
			commentBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelActivity.this,TravelNotesActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("pkid", pkidsel);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			ImageView locationbtn = (ImageView)findViewById(R.id.location_btn);
			locationbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelActivity.this,TravelCompassActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("longtude", flatlong);
					bundle.putString("woof", flatwoof);
					bundle.putString("name", landscapeName);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			RelativeLayout layWeather = (RelativeLayout)findViewById(R.id.layWeather);
			layWeather.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showWeatherWind();
				}
			});
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
					
//					if(myapp.getTravelList() != null && myapp.getTravelList().size() > 0)
//						list = myapp.getTravelList();
//					else
//					{
						List<Map<String,Object>> lists = myapp.getMyCardsAll();
						Map map = lists.get(index);
						
						String storeid = (String)map.get("storeid");
						
						jobj = api.getTravelList(storeid,page);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getTravelListDate(jArr);
							listCount = list.size();
						}
//					}
					
//					myapp.setTravelList(list);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData2()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
						List<Map<String,Object>> lists = myapp.getMyCardsAll();
						Map map = lists.get(index);
						
						String storeid = (String)map.get("storeid");
						
						jobj = api.getTravelList(storeid,page+1);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getTravelListDate(jArr);
							listCount = list.size();
						}
					
//					myapp.setTravelList(list);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				traveldatalist.addAll(dlist);
				myapp.setTravelList(traveldatalist);
				// 生成适配器的Item和动态数组对应的元素
				listItemAdapter = new ListViewAdapter(TravelActivity.this, traveldatalist,// 数据源
						R.layout.poi_item,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "img", "landscapeName","pnumber","score","price","pimg" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.img, R.id.lblName,R.id.lblNote,R.id.five_star_img,R.id.price,R.id.pimg },share,traveldatalist.size(),"ico");
				
				slistView.setDividerHeight(0);
				// 添加并且显示
				slistView.setAdapter(listItemAdapter);
				pb.setVisibility(View.GONE);
				slistView.setVisibility(View.VISIBLE);
				if(TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
					slistView.setFastScrollEnabled(true);
				break;
			case 1:
				Bitmap bitm = (Bitmap)msg.obj;
				if(bitm != null)
				{
//					pb2.setVisibility(View.GONE);
					lbimgview.setImageBitmap(bitm);
					lbimgview.setVisibility(View.VISIBLE);
					lbimgview.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
						    intent.setClass( TravelActivity.this,ProductImageSwitcher.class);
						    Bundle bundle = new Bundle();
							bundle.putString("productId", pkidsel);
							intent.putExtras(bundle);
						    startActivity(intent);//开始界面的跳转函数
						    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
						}
					});
				}
				else
				{
					lbimgview.setImageBitmap(null);
					makeText(TravelActivity.this.getString(R.string.attractions_lable_13));
				}
				break;
			case 2:
				String strData = (String)msg.obj;
				if(!strData.equals(""))
					showWeather(strData);
				break;
			case 3:
				List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
				traveldatalist.addAll(dlist2);
				myapp.setTravelList(traveldatalist);
				page++;
				// 生成适配器的Item和动态数组对应的元素
//				listItemAdapter = new ListViewAdapter(TravelActivity.this, traveldatalist,// 数据源
//						R.layout.poi_item,// ListItem的XML实现
//						// 动态数组与ImageItem对应的子项
//						new String[] { "img", "landscapeName","pnumber","score","price","pimg" },
//						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
//						new int[] { R.id.img, R.id.lblName,R.id.lblNote,R.id.five_star_img,R.id.price,R.id.pimg },share,traveldatalist.size());
				
//				slistView.setDividerHeight(0);
				// 添加并且显示
//				slistView.setAdapter(listItemAdapter);
				listDataMore(dlist2);
//				if(TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
//					slistView.setFastScrollEnabled(true);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void listDataMore(List<Map<String,Object>> dlist2)
	{
		try{
			HeaderViewListAdapter ha = (HeaderViewListAdapter) slistView.getAdapter();
			ListViewAdapter ad = (ListViewAdapter) ha.getWrappedAdapter();
			ad.addMoreData(dlist2);
//			listItemAdapter.notifyDataSetChanged(); 
//          slistView.setSelection((traveldatalist.size()-listCount-6)); 
			if(!TravelActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				progressBar.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);
				loadbutton.setVisibility(View.VISIBLE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getTravelListDate(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String landscapeName = ""; 
				if(dobj.has("landscapeName"))
					landscapeName = (String) dobj.get("landscapeName");
				
				String landscapeNo = ""; 
				if(dobj.has("landscapeNo"))
					landscapeNo = (String) dobj.get("landscapeNo");
				
				String landscapeDesc = ""; 
				if(dobj.has("landscapeDesc"))
					landscapeDesc = (String) dobj.get("landscapeDesc");
				
				String img = ""; 
				if(dobj.has("img"))
					img = (String) dobj.get("img");
				
				String notesnumber = ""; 
				if(dobj.has("notesnumber"))
					notesnumber = (String) dobj.get("notesnumber");
				
				String pinlun = ""; 
				if(dobj.has("pinlun"))
					pinlun = (String) dobj.get("pinlun");
				
				int score = 0; 
				if(dobj.has("score"))
					score = (Integer) dobj.get("score");
				
				String price = "0.00"; 
				if(dobj.has("price"))
					price = (String)dobj.get("price");
				
				String longtude = ""; 
				if(dobj.has("longtude"))
					longtude = (String) dobj.get("longtude");
				
				String woof = ""; 
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof");
				
				String address = ""; 
				if(dobj.has("address"))
					address = (String) dobj.get("address");
				
				String voidname = ""; 
				if(dobj.has("voidname"))
					voidname = (String) dobj.get("voidname");
				
				String voidpath = ""; 
				if(dobj.has("voidpath"))
					voidpath = (String) dobj.get("voidpath");
				
				String storeId = ""; 
				if(dobj.has("storeId"))
					storeId = (String) dobj.get("storeId");
				
				String flatlong = ""; 
				if(dobj.has("flatlong"))
					flatlong = (String) dobj.get("flatlong");
				
				String flatwoof = ""; 
				if(dobj.has("flatwoof"))
					flatwoof = (String) dobj.get("flatwoof");
				
				String openHours = ""; 
				if(dobj.has("flatwoof"))
					openHours = (String) dobj.get("openHours");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("landscapeName", landscapeName);
				map.put("landscapeNo", landscapeNo);
				map.put("landscapeDesc", landscapeDesc);
				map.put("img", img);
				map.put("notesnumber", notesnumber);
				map.put("score", Float.valueOf(score));
				if(price.equals("0.00"))
				{
					map.put("pimg", null);
					map.put("price", null);
				}
				else
				{
					map.put("pimg", R.drawable.price);
					map.put("price", price);
				}
				map.put("pinlun", pinlun);
				map.put("pnumber", this.getString(R.string.attractions_lable_14)+" " + pinlun +" "+this.getString(R.string.attractions_lable_15));
				map.put("longtude", longtude);
				map.put("woof", woof);
				map.put("flatlong", flatlong);
				map.put("flatwoof", flatwoof);
				map.put("address", address);
				map.put("voidname", voidname);
				map.put("voidpath", voidpath);
				map.put("storeId", storeId);
				map.put("openHours",openHours);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private String searchWeather() {
		List<Map<String,Object>> lists = myapp.getMyCardsAll();
		Map<String,Object> map = lists.get(index);
		String storelat = (String)map.get("woof");
		String storelng = (String)map.get("longItude");
		
		int a=(int)(Double.valueOf(storelat) * 1E6);
		int b=(int)(Double.valueOf(storelng) * 1E6);
		
		String strUrl ="http://www.google.com/ig/api?hl=zh-cn&weather=,,,"+a+","+b;
		System.out.println(strUrl);
		
		String strData = getResponse(strUrl);

		return strData;
		// SAX解析xml
//        try {
//            showWeather(strData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

	}
	
	//根据地址 获得xml的String
	protected String getResponse(String queryURL) {
        URL url;
        try {
            url = new URL(queryURL.replace(" ", "%20"));
            URLConnection urlconn = url.openConnection();
            urlconn.connect();

            InputStream is = urlconn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer buf = new ByteArrayBuffer(50);

            int read_data = -1;
            while ((read_data = bis.read()) != -1) {
                buf.append(read_data);
            }
            // String resp = buf.toString();
            String resp = EncodingUtils.getString(buf.toByteArray(), "GBK");
            return resp;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (Exception ex){
        	ex.printStackTrace();
        	return "";
        }
    }
	
	//显示天气
	private void showWeather(String strData) {
		String strTemp = "";
		
		TomorrowWeatherPullParse tom = new TomorrowWeatherPullParse();
		
		tvolist = tom.getGoogleWeatherList(strData);
		String iconimg = "";
//		for(int i=0;i<tvolist.size();i++)
//		{
//			TomorrowWeatherVO tvo = tvolist.get(i);
//			if(i == 0)
//			{
//				iconimg = tvo.getIcon();
//				strTemp += "明天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//			else if(i == 1)
//			{
//				strTemp += "后天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//			else if(i == 2)
//			{
//				strTemp += "大后天天气情况："+tvo.getCondition() + ",最高气温："+tvo.getHigh()+"°C,最低气温："+tvo.getLow()+"°C  ";
//			}
//		}
		TomorrowWeatherVO tvo = tvolist.get(0);
		strTemp += tvo.getWeek() + " "+this.getString(R.string.attractions_lable_7)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
		iconimg = tvo.getIcon();
		
		Bitmap img_off = getImageBitmap("http://www.google.com"+iconimg,false);
		ImageView imgicon = (ImageView)findViewById(R.id.weather_icon);
		imgicon.setImageBitmap(img_off);
//		tvShow.setCompoundDrawables(img_off, null, null, null); //设置左图标
		tvShow.setText(strTemp);
	}
	
	//显示天气
	private void showWeather(List<TomorrowWeatherVO> tvolist) {
		String strTemp = "";
		
		TomorrowWeatherPullParse tom = new TomorrowWeatherPullParse();
		
		String iconimg = "";
		for(int i=0;i<tvolist.size();i++)
		{
			TomorrowWeatherVO tvo = tvolist.get(i);
			if(i == 0)
			{
				iconimg = tvo.getIcon();
				strTemp += this.getString(R.string.attractions_lable_10)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
			else if(i == 1)
			{
				strTemp += this.getString(R.string.attractions_lable_11)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
			else if(i == 2)
			{
				strTemp += this.getString(R.string.attractions_lable_12)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
			}
		}
		
		Bitmap img_off = getImageBitmap("http://www.google.com"+iconimg,false);
		ImageView imgicon = (ImageView)findViewById(R.id.weather_icon);
		imgicon.setImageBitmap(img_off);
//		tvShow.setCompoundDrawables(img_off, null, null, null); //设置左图标
		tvShow.setText(strTemp);
	}
	
	public void showWeatherWind()
	{
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			
			final ListView view = new ListView(this);
			
			List<Map<String,String>> weatherList = new ArrayList<Map<String,String>>();
			for(int i=0;i<tvolist.size();i++)
			{
				TomorrowWeatherVO tvo = tvolist.get(i);
				String iconimg = "http://www.google.com"+tvo.getIcon();
				String context = tvo.getWeek() + " "+this.getString(R.string.attractions_lable_7)+"："+tvo.getCondition() + ","+this.getString(R.string.attractions_lable_8)+"："+tvo.getHigh()+"°C,"+this.getString(R.string.attractions_lable_9)+"："+tvo.getLow()+"°C  ";
				
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("iconimg", iconimg);
				map.put("context", context);
				
				weatherList.add(map);
			}
			
			SpecialAdapter listItemAdapter = new SpecialAdapter(this, weatherList,// 数据源
					R.layout.menu_item_menu,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "iconimg", "context" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.mimg, R.id.menutype },share,"YU");
			
			view.setDividerHeight(0);
			// 添加并且显示
			view.setAdapter(listItemAdapter);
			
			builder.setView(view).setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); //设置宽和高
			alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public Bitmap getImageBitmap(String value,boolean b)
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
				bitmap = Bitmap.createScaledBitmap(bitmap,130,130,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
