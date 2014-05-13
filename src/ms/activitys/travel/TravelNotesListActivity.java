package ms.activitys.travel;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import ms.activitys.R;
import ms.globalclass.ListViewAdapter;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.DisplayImage;
import ms.globalclass.map.MyApp;

public class TravelNotesListActivity extends Activity implements OnScrollListener{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private String pagetag = "list";
	private String lvid;
	
	private LinearLayout progLayout;
	private ListView slistView;
	private int page = 0;
	private int listCount;
	private List<Map<String,Object>> traveldatalist = new ArrayList<Map<String,Object>>();
	private ListViewAdapter listItemAdapter;
	private int lastItem = 0; 
	private boolean islast = false;
    private int istag = 0;
    private ViewFlipper mViewFlipper;
    private String storeId;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView loadbutton;
    private int mapindex;
    /**
	 * 设置布局显示目标最大化
	 */
    private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    
    LinearLayout loadingLayout;
    
    private ImageView profileimg;
    private ImageView uploadpic;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lv_comment_list_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		lvid = this.getIntent().getExtras().getString("lvid");
		storeId = this.getIntent().getExtras().getString("storeId");
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		pagetag = "list";
		showTravelNotesList();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("list"))
				{
					TravelNotesListActivity.this.setResult(RESULT_OK, getIntent());
					TravelNotesListActivity.this.finish();
				}
				else
				{
					pagetag = "list";
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
	}
	
//	public void onResume()  
//	{  
//		progLayout.setVisibility(View.VISIBLE);
//		slistView.setVisibility(View.GONE);
//		
//		page = 0;
//		traveldatalist = new ArrayList<Map<String,Object>>();
//		
//		loadThreadData();
//		super.onResume();  
//	}  
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
//		lastItem = firstVisibleItem + visibleItemCount - 1;
//		System.out.println("lastItem:" + lastItem);
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
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (lastItem == listItemAdapter.count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if(TravelNotesListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				if(listCount > 0)
				{
//					page++;
					loadThreadData2();
				}
			}
		}
	}
	
	@Override
	public void onRestart()  
	{  
		//返回触发事件
		super.onRestart();  
	} 
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // TODO Auto-generated method stub
       switch (resultCode) {
       case 1:
    	   	if(data.getExtras() != null)
	   		{
	   			if(data.getExtras().containsKey("tag"))
	   			{
	   				if(data.getExtras().getString("tag").equals("detailed"))
	   				{
	   					page = 0;
	   					traveldatalist = new ArrayList<Map<String, Object>>();
	   					lastItem = 0;
	   					islast = false;
	   					istag = 0;

	   					if (!islast) {
	   						slistView.removeFooterView(loadingLayout);
	   					}

	   					showTravelNotesList();
	   					
	   					pagetag = "list";
	   					mViewFlipper.showPrevious();
	   				}
	   				else if(data.getExtras().getString("tag").equals("back"))
	   				{
	   					pagetag = "detailed";
	   				}
	   			}
	   			
	   		}
           break;
       default:
           break;
       }
    }
	
	public void showTravelNotesList()
	{
		try{
			slistView = (ListView)findViewById(R.id.freelook_listview);
			progLayout = (LinearLayout)findViewById(R.id.progLayout);
			
			loadThreadData();
			
			showListMoreView();
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					Map<String,Object> map = myapp.getTravelNotesList().get(arg2);
					mapindex = arg2;
					showTravelDetailed(map);
				}
			});
			
			ImageView commentbtn = (ImageView)findViewById(R.id.comment_btn);
			commentbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelNotesListActivity.this,TravelNotesActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("pkid", lvid);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
					startActivityForResult(intent,1);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			ImageView refreshbtn = (ImageView)findViewById(R.id.refresh_btn);
			refreshbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					progLayout.setVisibility(View.VISIBLE);
					slistView.setVisibility(View.GONE);
					
					page = 0;
					traveldatalist = new ArrayList<Map<String, Object>>();
					lastItem = 0;
					islast = false;
					istag = 0;
					
					if(!islast)
					{
						slistView.removeFooterView(loadingLayout);
					}
					pagetag = "list";
					showTravelNotesList();
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
	
	public void showListMoreView()
	{
		try{
			RelativeLayout rlayout = new RelativeLayout(this);
			rlayout.setGravity(Gravity.CENTER);
//			rlayout.setBackgroundColor(Color.parseColor("#8f00000f"));
			rlayout.setBackgroundColor(Color.WHITE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			
			 //进度条
			progressBar = new ProgressBar(this);
			 //进度条显示位置
//			progressBar.setPadding(10, 0, 15, 0);
			progressBar.setId(1);
			progressBar.setIndeterminate(false);
			Resources res=getResources();
			progressBar.setIndeterminateDrawable(res.getDrawable(R.drawable.dialog_style_xml_icon));
//			layout.addView(progressBar, WClayoutParams);
			rlayout.addView(progressBar,lp);
			
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
			
			if(!this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
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
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showTravelDetailed(Map<String,Object> map)
	{
		pagetag = "detailed";
		try{
			mViewFlipper.showNext();
			final String pkid = (String)map.get("pkid");
			String pfname = (String)map.get("nameFirst");
			String sendTime = (String)map.get("sendTime");
//			String travelContent = (String)map.get("travelContent");
//			String travelNotesName = (String)map.get("travelNotesName");
			String content = (String)map.get("content");
			String pinluns = (String)map.get("pinluns");
			final String imgpath = (String)map.get("sysImg");
			final String pfimg = (String)map.get("fimg");
			
			profileimg = (ImageView)findViewById(R.id.tweet_profile_preview);
			if(pfimg != null)
			{
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 2;
						
						Bitmap bitm = getImageBitmap(pfimg,false);
						msg.obj = bitm;
						handler.sendMessage(msg);
					}
				}.start();
			}
			
			uploadpic = (ImageView)findViewById(R.id.tweet_upload_pic);
			if(imgpath != null)
			{
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 3;
						
						String str = imgpath.substring(0,imgpath.lastIndexOf("."));
		        		String str2 = imgpath.substring(imgpath.lastIndexOf("."),imgpath.length());
		        		
						Bitmap bitm = getImageBitmap(str+"_zhong"+str2,false);
						msg.obj = bitm;
						handler.sendMessage(msg);
					}
				}.start();
				
				uploadpic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(TravelNotesListActivity.this,
								DisplayImage.class);
						Bundle bundle = new Bundle();
						bundle.putString("path", imgpath);
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
						overridePendingTransition(R.anim.push_up_in,
								R.anim.push_up_out);
					}
				});
			}
			
			TextView nametxt = (TextView)findViewById(R.id.tweet_profile_name);
			nametxt.setText(pfname);
			
			TextView contenttxt = (TextView)findViewById(R.id.tweet_message);
			contenttxt.setText(content);
			
			TextView timetxt = (TextView)findViewById(R.id.tweet_updated);
			timetxt.setText(sendTime);
			
			Button redirect = (Button)findViewById(R.id.tweet_redirect);
			redirect.setText(pinluns);
			redirect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelNotesListActivity.this,TravelCommentListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("lsid", lvid);
					bundle.putString("tnid", pkid);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			Button backbtn = (Button)findViewById(R.id.back_btn);
			backbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pagetag = "list";
					mViewFlipper.showPrevious();
				}
			});
			
			Button commentbtn = (Button)findViewById(R.id.comment_btn2);
			commentbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelNotesListActivity.this,TravelCommentActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("lvid", lvid);
					bundle.putString("notesid",pkid);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);  
//				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			TextView ptxt = (TextView)findViewById(R.id.tvComment);
			ptxt.setText(this.getString(R.string.travel_lable_27)+" " + pinluns);
			ptxt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
				    intent.setClass( TravelNotesListActivity.this,TravelCommentListActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("lsid", lvid);
					bundle.putString("tnid", pkid);
					bundle.putString("storeId", storeId);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
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
					JSONObject jobj = api.getTravelNotesList(lvid,page);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getTravelListDate(jArr);
						listCount = list.size();
					}
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
				msg.what = 1;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj = api.getTravelNotesList(lvid,page+1);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getTravelListDate(jArr);
						listCount = list.size();
					}
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
				myapp.setTravelNotesList(traveldatalist);
				// 生成适配器的Item和动态数组对应的元素
				listItemAdapter = new ListViewAdapter(TravelNotesListActivity.this, traveldatalist,// 数据源
						R.layout.itemview,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "nameFirst", "sendTime","content","sysImg","pinluns","fimg" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.tvItemName, R.id.tvItemDate,R.id.tvItemContent,R.id.contentPic,R.id.luntans,R.id.ivItemPortrait},share,traveldatalist.size(),"ico");
				
//				slistView.setDividerHeight(0);
				// 添加并且显示
				slistView.setAdapter(listItemAdapter);
				progLayout.setVisibility(View.GONE);
				slistView.setVisibility(View.VISIBLE);
				if(TravelNotesListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
					slistView.setFastScrollEnabled(true);
				break;
			case 1:
				List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
				traveldatalist.addAll(dlist2);
				myapp.setTravelNotesList(traveldatalist);
				page++;
				listDataMore(dlist2);
				break;
			case 2:
				Bitmap bitm = (Bitmap)msg.obj;
				profileimg.setImageBitmap(bitm);
				break;
			case 3:
				Bitmap bitm2 = (Bitmap)msg.obj;
				uploadpic.setImageBitmap(bitm2);
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
			if(!this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
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
				
				String nameFirst = ""; 
				if(dobj.has("nameFirst"))
					nameFirst = (String) dobj.get("nameFirst");
				
				String sendTime = ""; 
				if(dobj.has("sendTime"))
					sendTime = (String) dobj.get("sendTime");
				
				String travelContent = ""; 
				if(dobj.has("travelContent"))
					travelContent = (String) dobj.get("travelContent");
				
				String travelNotesName = ""; 
				if(dobj.has("travelNotesName"))
					travelNotesName = (String) dobj.get("travelNotesName");
				
				String pinluns = ""; 
				if(dobj.has("pinluns"))
					pinluns = (String) dobj.get("pinluns");
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
					sysImg = (String) dobj.get("sysImg");
				
				String fimg = ""; 
				if(dobj.has("fimg"))
					fimg = (String) dobj.get("fimg");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("nameFirst", nameFirst);
				map.put("sendTime", sendTime);
				map.put("travelContent", travelContent);
				map.put("travelNotesName", travelNotesName);
				map.put("content", "["+travelNotesName+"]"+travelContent);
				map.put("pinluns", pinluns+" "+this.getString(R.string.travel_lable_28));
				if(sysImg.equals(""))
				{
					map.put("sysImg", null);
					map.put("sysImg_ico", null);
				}
				else
				{
					String str = sysImg.substring(0,sysImg.lastIndexOf("."));
	        		String str2 = sysImg.substring(sysImg.lastIndexOf("."),sysImg.length());
	        		
					map.put("sysImg", sysImg);
					map.put("sysImg_ico", str+"_ico"+str2);
				}
				if(fimg.equals(""))
				{
					map.put("fimg", null);
				}
				else
				{
					map.put("fimg", fimg);
				}
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public Bitmap getImageBitmap(String value,boolean b)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
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
				bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
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
