package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.integral.GetIntegralHistoryActity;
import ms.globalclass.ListViewAdapter;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterHotel;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

public class ReviewUserListActivity extends Activity implements OnScrollListener{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private Map<String,Object> hmap;
	private int cardindex;
	private String hotelid;
	private RelativeLayout reviewlist_ry;
	private ProgressBar pb;
	private ListView listview;
	
	private ProgressBar progressBar;
    private TextView textView;
    private TextView loadbutton;
    /**
   	 * 设置布局显示目标最大化
   	 */
    private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    private LinearLayout loadingLayout;
    private int page = 0;
    private boolean islast = false;
    private int istag = 0;
    private int lastItem = 0; 
    private List<Map<String,Object>> alllist = new ArrayList<Map<String,Object>>();
    private ListViewAdapter listItemAdapter;
    private int listCount;
    
    private String listtage = "1";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_user_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		cardindex = this.getIntent().getExtras().getInt("index");
		
		hmap = myapp.getMyCardsAll().get(cardindex);
		
		pb = (ProgressBar)findViewById(R.id.probar);
		reviewlist_ry = (RelativeLayout)findViewById(R.id.reviewlist_ry);
		listview = (ListView)findViewById(R.id.review_list);
		
		hotelid = (String)hmap.get("storeid");
		
		RadioButton rb1 = (RadioButton)findViewById(R.id.user_review_button_recent);
		rb1.setChecked(true);
		
		TextView book_now_button = (TextView)findViewById(R.id.book_now_button);
		book_now_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
			    intent.setClass(ReviewUserListActivity.this,HotelReservation.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(cardindex));
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			}
		});
		
		RadioGroup ugroup = (RadioGroup)findViewById(R.id.user_review_sort_group);
		ugroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				pb.setVisibility(View.VISIBLE);
				reviewlist_ry.setVisibility(View.GONE);
				
				page = 0;
				alllist = new ArrayList<Map<String, Object>>();
				lastItem = 0;
				istag = 0;
				
				listview.setAdapter(null);
				if(!islast)
				{
					listview.removeFooterView(loadingLayout);
				}
				islast = false;
				
				if (checkedId == R.id.user_review_button_recent) { 
					listtage = "1";
					loadReviewListView();
				}
				else if (checkedId == R.id.user_review_button_favorable){
					listtage = "2";
					loadReviewListView();
				}
				else if (checkedId == R.id.user_review_button_critical){
					listtage = "3";
					loadReviewListView();
				}
			}
		});
		
		String score = (String)hmap.get("score");
		RatingBar rbar = (RatingBar)findViewById(R.id.user_review_rating_bar_bottom);
		rbar.setRating(Float.valueOf(score));
		
		loadReviewListView();
	}
	
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
				try{
					listview.removeFooterView(loadingLayout);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				islast = true;
				istag++;
			}
			else
			{
				if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
				{
					listview.setSelection((alllist.size())); 
					istag++;
				}
				else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
				{
					listview.setSelection((alllist.size())); 
					istag++;
				}
			}
		}
		else
		{
			if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
			{
				listview.setSelection((alllist.size())); 
				istag++;
			}
			else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
			{
				listview.setSelection((alllist.size())); 
				istag++;
			}
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (lastItem == listItemAdapter.count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if(ReviewUserListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				if(listCount > 0)
				{
//					page++;
					loadThreadData2();
				}
			}
		}
	}
	
	public void loadReviewListView()
	{
		try{
			loadThreadReviewListData();
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
						
						loadThreadData2();
					}
				});
			}
			
			loadingLayout = new LinearLayout(this);
			loadingLayout.addView(rlayout, FFlayoutParams);
			loadingLayout.setGravity(Gravity.CENTER);
			//添加到脚页显示
			listview.addFooterView(loadingLayout);
			
			try{
				listview.setOnScrollListener(this);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadReviewListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getReviewList(hotelid,String.valueOf(page),listtage);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							String count = (String)jobj.get("count");
							String zhancount = (String)jobj.get("zhancount");
							list = getreviewList(jArr);
							listCount = list.size();
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("list", list);
							map.put("count", count);
							map.put("zhancount", zhancount);
							msg.obj = map;
						}
					}
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
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
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getReviewList(hotelid,String.valueOf(page + 1),listtage);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getreviewList(jArr);
							Map<String,Object> map = new HashMap<String,Object>();
							listCount = list.size();
							map.put("list", list);
							msg.obj = map;
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
				if(msg.obj != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)map.get("list");
					String count = (String)map.get("count");
					String zhancount = (String)map.get("zhancount"); 
					
					alllist.addAll(dlist);
					listItemAdapter = new ListViewAdapter(ReviewUserListActivity.this, alllist,// 数据源
							R.layout.review_user_list_item,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "title", "contents" ,"createdDate","score","username"},
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.user_review_title_text_view, R.id.user_review_body_text_view, R.id.user_review_date_text_view,R.id.user_review_rating_bar,R.id.user_review_name_and_location_text_view},share,alllist.size(),"ico");
					
					// 添加并且显示
					listview.setAdapter(listItemAdapter);
					listview.setDivider(null);  
					if(ReviewUserListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
						listview.setFastScrollEnabled(true);
					
					TextView userrating = (TextView)findViewById(R.id.user_review_total_reviews);
					userrating.setText(count + " " + getString(R.string.menu_lable_159));
					
					TextView urecommendation = (TextView)findViewById(R.id.user_reviews_recommendation_tag);
					urecommendation.setText( getString(R.string.menu_lable_160)+count +  getString(R.string.menu_lable_161)+zhancount + getString(R.string.menu_lable_162));
					
					pb.setVisibility(View.GONE);
					reviewlist_ry.setVisibility(View.VISIBLE);
				}
				else
				{
					pb.setVisibility(View.GONE);
				}
				break;
			case 1:
				if(msg.obj != null)
				{
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)map.get("list");
					
					alllist.addAll(dlist);
					page++;
					listDataMore(dlist);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void listDataMore(List<Map<String,Object>> dlist2)
	{
		try{
			HeaderViewListAdapter ha = (HeaderViewListAdapter) listview.getAdapter();
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
	
	public List<Map<String,Object>> getreviewList(JSONArray jArr)
	{
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String title = ""; 
				if(dobj.has("title"))
					title = (String) dobj.get("title");
				
				String contents = ""; 
				if(dobj.has("contents"))
					contents = (String) dobj.get("contents");
				
				String username = ""; 
				if(dobj.has("username"))
					username = (String) dobj.get("username");
				
				String score = ""; 
				if(dobj.has("score"))
					score = (String) dobj.get("score");
				
				String createdDate = ""; 
				if(dobj.has("createdDate"))
					createdDate = (String) dobj.get("createdDate");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("title", title);
				map.put("contents", contents);
				map.put("username", username);
				map.put("score", Float.valueOf(score));
				map.put("createdDate", createdDate);

				rlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return rlist;
	}
}
