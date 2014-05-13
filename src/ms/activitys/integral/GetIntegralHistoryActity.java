package ms.activitys.integral;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.hotel.MyHotelReservation;
import ms.globalclass.ListViewAdapter;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

public class GetIntegralHistoryActity extends Activity implements OnScrollListener{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressBar progressBar;
    private TextView textView;
    private TextView loadbutton;
    /**
	 * 设置布局显示目标最大化
	 */
    private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    
    private LinearLayout loadingLayout;
    private ListView listview;
    private int page = 0;
    private List<Map<String,Object>> alllist = new ArrayList<Map<String,Object>>();
    private boolean islast = false;
    private int istag = 0;
    private int lastItem = 0; 
    private ListViewAdapter listItemAdapter;
    private int listCount;
    private LinearLayout progLayout;
    private String startime;
    private String endtime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inteal);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		listview = (ListView)findViewById(R.id.ListView02);
		progLayout = (LinearLayout)findViewById(R.id.progLayout);
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		String cdate = sf.format(calendar.getTime());
		
		calendar.add(Calendar.DAY_OF_MONTH, -7);//取当前日期的后7天. 
		String hdate = sf.format(calendar.getTime());
		
		final TextView checkin = (TextView)findViewById(R.id.checkin);
		checkin.setText(hdate);
		
		final TextView checkout = (TextView)findViewById(R.id.checkout);
		checkout.setText(cdate);
		
		startime = checkin.getText().toString();
		endtime = checkout.getText().toString();
		
		
		LinearLayout btn_ll = (LinearLayout)findViewById(R.id.start_end_time);
		btn_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDateSelection();
			}
		});
		
		Button search_btn = (Button)findViewById(R.id.change_date);
		search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progLayout.setVisibility(View.VISIBLE);
				
				listview.setVisibility(View.GONE);
				
				page = 0;
				alllist = new ArrayList<Map<String, Object>>();
				lastItem = 0;
				islast = false;
				istag = 0;
				
				listview.setAdapter(null);
				if(!islast)
				{
					listview.removeFooterView(loadingLayout);
				}
				
				startime = checkin.getText().toString();
				endtime = checkout.getText().toString();
				showListView();
			}
		});
		showListView();
	} 
	
	public void showDateSelection()
	{
		try{
			final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			
			View view = LayoutInflater.from(this).inflate(R.layout.datepicker_view,null);
			
			final RadioButton b1 = (RadioButton)view.findViewById(R.id.b1);
			b1.setText(this.getString(R.string.mybill_list_lable_9));
			b1.setChecked(true);
			
			final RadioButton b2 = (RadioButton)view.findViewById(R.id.b2);
			b2.setText(this.getString(R.string.mybill_list_lable_10));
			
			TextView checkin = (TextView)findViewById(R.id.checkin);
			String dstr = checkin.getText().toString();
			
			final DatePicker dp = (DatePicker)view.findViewById(R.id.date_picker);
			if(dstr != null && !dstr.equals(""))
			{
				String [] strs = dstr.split("-");
				int y = Integer.valueOf(strs[0]);
				int m = Integer.valueOf(strs[1]);
				int d = Integer.valueOf(strs[2]);
				
				dp.init(y,m-1,d, null);
			}
			
			RadioGroup rgroup = (RadioGroup)view.findViewById(R.id.radioGroup);
			rgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					if (checkedId == b1.getId()) {  
		                //获得按钮的名称  
						TextView checkin = (TextView)findViewById(R.id.checkin);
						String dstr = checkin.getText().toString();
						
						String [] strs = dstr.split("-");
						int y = Integer.valueOf(strs[0]);
						int m = Integer.valueOf(strs[1]);
						int d = Integer.valueOf(strs[2]);
						
						dp.init(y,m-1,d, null);
		            } else if (checkedId == b2.getId()) {  
		            	TextView checkout = (TextView)findViewById(R.id.checkout);
						String dstr = checkout.getText().toString();
						
						String [] strs = dstr.split("-");
						int y = Integer.valueOf(strs[0]);
						int m = Integer.valueOf(strs[1]);
						int d = Integer.valueOf(strs[2]);
						
						dp.init(y,m-1,d, null);
		            }   
				}
			});
			
			
			final Dialog myDialog = new Dialog(this, R.style.AliDialog);
			myDialog.setContentView(view);
			myDialog.show();
			
			Button dbtn = (Button)view.findViewById(R.id.determine_btn);
			dbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int month = dp.getMonth() + 1;
					String monthstr = String.valueOf(month);
					if(month < 10)
						monthstr = "0"+month;
					
					int dayof = dp.getDayOfMonth();
					String dayofstr = String.valueOf(dayof);
					if(dayof < 10)
						dayofstr = "0"+dayof;
					
					String datestr = dp.getYear() + "-" + monthstr + "-" + dayofstr;
					if(b1.isChecked())
					{
						TextView checkin = (TextView)findViewById(R.id.checkin);
						
						TextView checkout = (TextView)findViewById(R.id.checkout);
						String datastr2 = checkout.getText().toString();
						
						if(dateCompare(datestr,datastr2))
						{
							Calendar calendar = Calendar.getInstance();
							String cdate = sf.format(calendar.getTime());
							
							checkin.setText(datestr);
							myDialog.dismiss();
						}
						else
							showMark(GetIntegralHistoryActity.this.getString(R.string.menu_lable_47));
					}
					else
					{
						TextView checkout = (TextView)findViewById(R.id.checkout);
						
						TextView checkin = (TextView)findViewById(R.id.checkin);
						String datastr2 = checkin.getText().toString();
						
						if(dateCompare(datestr,datastr2))
						{
							showMark(GetIntegralHistoryActity.this.getString(R.string.menu_lable_47));
						}
						else
						{
							checkout.setText(datestr);
							myDialog.dismiss();
						}
							
					}
				}
			});
			
			Button btn2 = (Button)view.findViewById(R.id.clear_btn);
			btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog.dismiss();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GetIntegralHistoryActity.this.setResult(RESULT_OK, getIntent());
			GetIntegralHistoryActity.this.finish();
			return false;
		}
		return false;
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
			if(GetIntegralHistoryActity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				if(listCount > 0)
				{
//					page++;
					loadThreadData2();
				}
			}
		}
	}
	
	public void showListView()
	{
		try{
			loadThreadData();
			RelativeLayout rlayout = new RelativeLayout(GetIntegralHistoryActity.this);
			rlayout.setGravity(Gravity.CENTER);
//			rlayout.setBackgroundColor(Color.parseColor("#8f00000f"));
			rlayout.setBackgroundColor(Color.WHITE);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			
			 //进度条
			progressBar = new ProgressBar(GetIntegralHistoryActity.this);
			 //进度条显示位置
//			progressBar.setPadding(10, 0, 15, 0);
			progressBar.setId(1);
			progressBar.setIndeterminate(false);
			Resources res=getResources();
			progressBar.setIndeterminateDrawable(res.getDrawable(R.drawable.dialog_style_xml_icon));
//			layout.addView(progressBar, WClayoutParams);
			rlayout.addView(progressBar,lp);
			
			textView = new TextView(GetIntegralHistoryActity.this);
			textView.setText(GetIntegralHistoryActity.this.getString(R.string.travel_lable_18));
			textView.setTextColor(Color.BLACK);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			
			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
			lp2.addRule(RelativeLayout.RIGHT_OF,1);
			
//			layout.addView(textView, FFlayoutParams);
//			layout.setGravity(Gravity.CENTER);
			rlayout.addView(textView,lp2);
			
			if(!GetIntegralHistoryActity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				progressBar.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);
				
				loadbutton = new TextView(GetIntegralHistoryActity.this);
				loadbutton.setText(GetIntegralHistoryActity.this.getString(R.string.travel_lable_19));
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
			
			loadingLayout = new LinearLayout(GetIntegralHistoryActity.this);
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
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj = api.outIntergralHistory(String.valueOf(page),startime,endtime);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getListDate(jArr);
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
					JSONObject jobj = api.outIntergralHistory(String.valueOf(page+1),startime,endtime);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getListDate(jArr);
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
				if(dlist != null)
				{
					alllist.addAll(dlist);
					// 生成适配器的Item和动态数组对应的元素
					listItemAdapter = new ListViewAdapter(GetIntegralHistoryActity.this, alllist,// 数据源
							R.layout.itemspoints,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "vipId", "store" ,"updateTimes","curintear","curintears","desc" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.vip_num, R.id.vip_store, R.id.vip_times,R.id.vip_points,R.id.vip_cur,R.id.vip_desc },share,alllist.size(),"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					listview.setAdapter(listItemAdapter);
					progLayout.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					if(GetIntegralHistoryActity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
						listview.setFastScrollEnabled(true);
				}
				else
				{
					progLayout.setVisibility(View.GONE);
				}
				break;
			case 1:
				List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
				alllist.addAll(dlist2);
				page++;
				listDataMore(dlist2);
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
	
	public List<Map<String,Object>> getListDate(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject json = (JSONObject)jArr.get(i);
				String updateTimes = json.getString("updateTimes");
				String desc = json.getString("desc");
				String curintear = json.getString("curintear");
				String curintears = json.getString("curintears");
				String store = json.getString("store");
				String vipId = json.getString("vipId");
				Map<String, Object> maps = new HashMap<String, Object>();
				maps.put("vipId",vipId );//会员卡
				maps.put("store",store );//门店名字
				maps.put("updateTimes",updateTimes );//变更时间
				maps.put("curintear",curintear );//更新积分
				maps.put("curintears",curintears );//当前积分
				maps.put("desc",desc );//积分变更描述
				list.add(maps);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public static boolean dateCompare(String datstr1, String datstr2) {
		boolean dateComPareFlag = true;
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date dat1 = sf.parse(datstr1);
			Date dat2 = sf.parse(datstr2);
			if (dat2.compareTo(dat1) != 1) {
				dateComPareFlag = false; //
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dateComPareFlag;
	}
	
	public void showMark(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
