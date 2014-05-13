package ms.activitys.product;

import java.text.DecimalFormat;
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
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ProductMenuListActivity extends Activity implements OnScrollListener{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String typeid;
	private ListView slistView;
	private ProgressBar pb;
	private String storeid;
	private String step;
	private TextView textNull;
	private String sortStr = "new";
	private TextView textRankNew;
	private TextView textRankSale;
	private TextView textRankPrice;
	private ProgressDialog mypDialog;
	
	private int index;
	private String maptag = "";
    private String advertiseNotification;
    
    private ProgressBar progressBar;
    private TextView textView;
    private TextView loadbutton;
    /**
	 * 设置布局显示目标最大化
	 */
    private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    
    private LinearLayout loadingLayout;
    private int page = 0;
    private List<Map<String,Object>> alllist = new ArrayList<Map<String,Object>>();
    private boolean islast = false;
    private int istag = 0;
    private int lastItem = 0; 
    private ListViewAdapter listItemAdapter;
    private int listCount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_menu_list);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		typeid = this.getIntent().getExtras().getString("typeid");
		storeid = this.getIntent().getExtras().getString("storeid");
		step = this.getIntent().getExtras().getString("step");
		String typename = this.getIntent().getExtras().getString("typename");
		index = this.getIntent().getExtras().getInt("index");
		maptag = this.getIntent().getExtras().getString("map");
		advertiseNotification = this.getIntent().getExtras().getString("advertiseNotification");
		
		slistView = (ListView)findViewById(R.id.listProduct);
		pb = (ProgressBar)findViewById(R.id.probar);
		TextView title = (TextView)findViewById(R.id.TextView01);
		title.setText(typename);
		textNull = (TextView)findViewById(R.id.textNull);
		
		loadButton();
		
		showProductList();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("step",step);
			intent.putExtras(bundle);
			ProductMenuListActivity.this.setResult(RESULT_OK, intent);
			ProductMenuListActivity.this.finish();
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
					slistView.removeFooterView(loadingLayout);
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
					slistView.setSelection((alllist.size())); 
					istag++;
				}
				else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
				{
					slistView.setSelection((alllist.size())); 
					istag++;
				}
			}
		}
		else
		{
			if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
			{
				slistView.setSelection((alllist.size())); 
				istag++;
			}
			else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
			{
				slistView.setSelection((alllist.size())); 
				istag++;
			}
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (lastItem == listItemAdapter.count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if(ProductMenuListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("autoload_more", false))
			{
				if(listCount > 0)
				{
//					page++;
					loadThreadData2();
				}
			}
		}
	}
	
	public void loadButton()
	{
		try{
			textRankNew = (TextView)findViewById(R.id.textRankNew);
			textRankSale = (TextView)findViewById(R.id.textRankSale);
			textRankPrice = (TextView)findViewById(R.id.textRankPrice);
			
			textRankNew.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textRankSale.setBackgroundResource(R.drawable.segment_normal_2_bg);
					textRankSale.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankPrice.setBackgroundResource(R.drawable.segment_normal_3_bg);
					textRankPrice.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankNew.setBackgroundResource(R.drawable.segment_selected_1_bg);
					textRankNew.setTextColor(Color.parseColor("#ffffffff"));
					
					if(!sortStr.equals("new"))
					{
						showProgressDialog();
						sortStr = "new";
						
						page = 0;
						alllist = new ArrayList<Map<String, Object>>();
						lastItem = 0;
						islast = false;
						istag = 0;
						
						if(!islast)
						{
							slistView.removeFooterView(loadingLayout);
						}
						
						showProductList();
					}
				}
			});
			
			textRankSale.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textRankNew.setBackgroundResource(R.drawable.segment_normal_1_bg);
					textRankNew.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankPrice.setBackgroundResource(R.drawable.segment_normal_3_bg);
					textRankPrice.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankSale.setBackgroundResource(R.drawable.segment_selected_2_bg);
					textRankSale.setTextColor(Color.parseColor("#ffffffff"));
					
					if(!sortStr.equals("score"))
					{
						showProgressDialog();
						sortStr = "score";
						
						page = 0;
						alllist = new ArrayList<Map<String, Object>>();
						lastItem = 0;
						islast = false;
						istag = 0;
						
						if(!islast)
						{
							slistView.removeFooterView(loadingLayout);
						}
						
						showProductList();
					}
				}
			});
			
			textRankPrice.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					textRankNew.setBackgroundResource(R.drawable.segment_normal_1_bg);
					textRankNew.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankSale.setBackgroundResource(R.drawable.segment_normal_2_bg);
					textRankSale.setTextColor(Color.parseColor("#ff4b4b4b"));
					textRankPrice.setBackgroundResource(R.drawable.segment_selected_3_bg);
					textRankPrice.setTextColor(Color.parseColor("#ffffffff"));
					
					if(sortStr.indexOf("price") >= 0)
					{
						if(sortStr.equals("priceasc"))
						{
							showProgressDialog();
							sortStr = "pricedesc";
							textRankPrice.setText(getString(R.string.menu_lable_28));
							page = 0;
							alllist = new ArrayList<Map<String, Object>>();
							lastItem = 0;
							islast = false;
							istag = 0;
							
							if(!islast)
							{
								slistView.removeFooterView(loadingLayout);
							}
							
							showProductList();
						}
						else
						{
							showProgressDialog();
							sortStr = "priceasc";
							textRankPrice.setText(getString(R.string.menu_lable_27));
							page = 0;
							alllist = new ArrayList<Map<String, Object>>();
							lastItem = 0;
							islast = false;
							istag = 0;
							
							if(!islast)
							{
								slistView.removeFooterView(loadingLayout);
							}
							
							showProductList();
						}
					}
					else
					{
						showProgressDialog();
						sortStr = "priceasc";
						textRankPrice.setText(getString(R.string.menu_lable_27));
						page = 0;
						alllist = new ArrayList<Map<String, Object>>();
						lastItem = 0;
						islast = false;
						istag = 0;
						
						if(!islast)
						{
							slistView.removeFooterView(loadingLayout);
						}
						
						showProductList();
					}
				}
			});
			
			Button back_btn = (Button)findViewById(R.id.back_btn);
			back_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ProductMenuListActivity.this.setResult(RESULT_OK, getIntent());
					ProductMenuListActivity.this.finish();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductList()
	{
		try{
			loadThreadData();
			slistView.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			
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
			slistView.addFooterView(loadingLayout);
			
			try{
				slistView.setOnScrollListener(this);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Map<String,Object> map = myapp.getProductList().get(arg2);
					
					showProductDetail(arg2);
					
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductDetail(int pindex)
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductDetail.class);
		    Bundle bundle = new Bundle();
			bundle.putInt("pindex", pindex);
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			bundle.putString("storeid",storeid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
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
					
					jobj = api.getMenuData(typeid,storeid,step,sortStr,page);
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
							listCount = list.size();
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
					
					jobj = api.getMenuData(typeid,storeid,step,sortStr,page+1);
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
							listCount = list.size();
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
				if(mypDialog != null)
					mypDialog.dismiss();
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
					alllist.addAll(dlist2);
					myapp.setProductList(alllist);
					// 生成适配器的Item和动态数组对应的元素
					listItemAdapter = new ListViewAdapter(ProductMenuListActivity.this, alllist,// 数据源
							R.layout.productlistitems,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "imgurl", "cname" ,"price","fiveimg","timenew","special","paycount" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.imgClothesIcon, R.id.textClothesName, R.id.textClothesPrice,R.id.five_star_img,R.id.imgIsNew,R.id.imgIsBargain,R.id.sales },share,alllist.size(),"zhong");
					
					// 添加并且显示
					if(listItemAdapter != null)
						slistView.setAdapter(listItemAdapter);
					pb.setVisibility(View.GONE);
					slistView.setVisibility(View.VISIBLE);
					
					if(ProductMenuListActivity.this.getSharedPreferences("perference", MODE_PRIVATE).getBoolean("fast_scroll_bar", false))
						slistView.setFastScrollEnabled(true);
				}
				else
				{
					textNull.setVisibility(View.VISIBLE);
					pb.setVisibility(View.GONE);
				}
				break;
			case 1:
				if(mypDialog != null)
					mypDialog.dismiss();
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
					alllist.addAll(dlist2);
					page++;
					listDataMore(dlist2);
				}
				else
				{
					pb.setVisibility(View.GONE);
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
	
	public static List<Map<String,Object>> getMenuList2(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
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
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
