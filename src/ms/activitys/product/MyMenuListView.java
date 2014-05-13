package ms.activitys.product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import ms.activitys.R;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

/**
 * 此类准备放弃不用了
 * @author Acer
 *
 */
public class MyMenuListView extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// 首页
	private final int TOOLBAR_ITEM_MAP = 1;// 退后
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// 前进
	private final int TOOLBAR_ITEM_NFC = 3;// 创建
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	
	String index;
	
	String ptId = "";
	
	String tag = "0";
	
	private String pagetag = "typepage";
	
	private String appliconStoreId;
	
	private ViewFlipper mViewFlipper;
	
	private String orderNos;
	private double totlePice;
	private double oldtotlePice;
	
	private double diasePice;
	private double diasePiceNumber = 0;
	private double oldtakesPice = 0;
	
	private Dialog myDialog;
	
	private ProgressBar pb;
	
	private ListView slistView;
	
	private CharSequence[] coloritems = {};
	private CharSequence[] tasteitems = {};
	
	private CharSequence[] addressitems = {};
	private CharSequence[] logitems = {};
	
	private List<Map<String,Object>> colorlist;
	
	private List<Map<String,String>> tastelist;
	
	private List<Map<String,String>> sideDisheslist = new ArrayList<Map<String,String>>();
	
	private String prodcurColor;
	private String prodcurColorpirc;
	
	private String prodcurTaste;
	private String prodcurTastepirc;
	
	private List<JSONObject> logisticsids;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_list_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = MyMenuListView.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("index");
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		TabHost tabHost = (TabHost) this.findViewById(R.id.TabHost01);
		tabHost.setup();

		tabHost.addTab(tabHost.newTabSpec("tab_1").setContent(
				R.id.LinearLayout1).setIndicator(this.getString(R.string.menu_lable_1),
				this.getResources().getDrawable(R.drawable.c2)));

//		tabHost.addTab(tabHost.newTabSpec("tab_2").setContent(
//				R.id.LinearLayout2).setIndicator(this.getString(R.string.menu_lable_2),
//				this.getResources().getDrawable(R.drawable.diancai)));
		tabHost.setCurrentTab(0);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
                public void onTabChanged(String tabId) {
                	if(tabId.equals("tab_2"))
                		showMyMenuPage();
                }
        });
		
		showMenuTypePage();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("typepage"))
				{
					MyMenuListView.this.setResult(RESULT_OK, getIntent());
					MyMenuListView.this.finish();
				}
				else if(pagetag.equals("mlist"))
				{
					showMenuTypePage();
				}
				else if(pagetag.equals("pdetit"))
				{
					pagetag = "mlist";
//					diasePiceNumber = 0;
//					oldtakesPice = 0;
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
	}
	
	/**
	 * 显示菜单类型列表
	 */
	public void showMenuTypePage()
	{
		cviewstr = "mtype";
		pagetag = "typepage";
		try{
			slistView = (ListView)findViewById(R.id.ListView_catalog);
			
			loadThreadData();
//			final List<Map<String,Object>> dlist = getMenuTypeListData();
//			final SpecialAdapter listItemAdapter;
//			if(dlist != null)
//			{
//				
//			}
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					String typeid = myapp.getMenuList().get(arg2).get("pkid").toString();
					ptId = typeid;
					showMenuPage(typeid);
				}
			});
			
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//			lp.addRule(RelativeLayout.ABOVE, R.id.GridView_toolbar);
//			sview.setLayoutParams(lp);

			
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
					
					List<Map<String,Object>> lists = myapp.getMyCardsAll();
					Map map = lists.get(Integer.valueOf(index));
					
					String storeid = (String)map.get("storeid");
					appliconStoreId = storeid;
					
					jobj = api.getMenuTypeData(storeid);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getMenuList(jArr);
					}
					
					myapp.setMenuList(list);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData2(final String pid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					List<Map<String,Object>> lists = myapp.getMyCardsAll();
					Map map = lists.get(Integer.valueOf(index));
					
					String storeid = (String)map.get("storeid");
					
					jobj = api.getMenuData(pid,storeid,"1","",0);
					if(jobj != null)
					{
						JSONArray jArr = (JSONArray) jobj.get("data");
						list = getMenuList2(jArr);
					}
					myapp.setMenuList(list);
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
				// 生成适配器的Item和动态数组对应的元素
				SpecialAdapter listItemAdapter = new SpecialAdapter(MyMenuListView.this, dlist,// 数据源
						R.layout.menu_item_menu,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "mimg", "cname" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.mimg, R.id.menutype },share,"ico");
				
				slistView.setDividerHeight(0);
				// 添加并且显示
				slistView.setAdapter(listItemAdapter);
				pb.setVisibility(View.GONE);
				slistView.setVisibility(View.VISIBLE);
				break;
			case 1:
				List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)msg.obj;
				// 生成适配器的Item和动态数组对应的元素
				listItemAdapter = new SpecialAdapter(MyMenuListView.this, dlist2,// 数据源
						R.layout.menu_list,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "imgurl", "cname" ,"price","fiveimg" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.menu_image, R.id.menu_name, R.id.menu_price,R.id.five_star_img },share,"ico");
				
				slistView.setDividerHeight(0);
				// 添加并且显示
				if(listItemAdapter != null)
					slistView.setAdapter(listItemAdapter);
				pb.setVisibility(View.GONE);
				slistView.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	/**
	 * 显示菜单列表
	 */
	public void showMenuPage(String pid)
	{
		cviewstr = "mlist";
		pagetag = "mlist";
		try{
			slistView = (ListView)findViewById(R.id.ListView_catalog);
			
			slistView.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			
			loadThreadData2(pid);
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
//					String proudid = (String)myapp.getMenuList().get(arg2).get("pkid");
//					String imgurl = (String)myapp.getMenuList().get(arg2).get("imgurl");
//					String cname = (String)myapp.getMenuList().get(arg2).get("cname");
//					String price = (String)myapp.getMenuList().get(arg2).get("price");
//					String prices = (String)myapp.getMenuList().get(arg2).get("prices");
//					float fiveimg = (Float)myapp.getMenuList().get(arg2).get("fiveimg");
//					String pdesc = (String)myapp.getMenuList().get(arg2).get("pdesc");
//					String productType = (String)myapp.getMenuList().get(arg2).get("productType");
					
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("pkid", proudid);
//					map.put("imgurl", imgurl);
//					map.put("cname", cname);
//					map.put("price", price);
//					map.put("fiveimg", fiveimg);
//					map.put("pdesc", pdesc);
//					map.put("prices", prices);
//					map.put("productType",productType);
					
					Map<String,Object> map = myapp.getMenuList().get(arg2);
					
					showMenuStratWoid(map,arg2);
					
				}
			});
			
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//			lp.addRule(RelativeLayout.ABOVE, R.id.GridView_toolbar);
//			sview.setLayoutParams(lp);

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMenuStratWoid(final Map<String,Object> map,final int pindex)
	{
		pagetag = "pdetit";
		try{
//			RelativeLayout layout = (RelativeLayout)findViewById(R.id.RelativeLayout_catalog);
//			layout.setBackgroundResource(R.drawable.popup_full);
			
//			mViewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);    
			mViewFlipper.showNext();
			
//			LinearLayout pdlayout = (LinearLayout)findViewById(R.id.product_details_layout);
//			pdlayout.setScrollBarStyle(R.style.AliDialog);
			
			
			final String pkid = (String)map.get("pkid");
			String imageurl = (String)map.get("imgurl");
			String cname = (String)map.get("cname");
			String price = (String)map.get("price");
//			if(map.containsKey("oldprices"))
//				diasePice = Double.valueOf((String)map.get("oldprices"));
//			else
				diasePice = Double.valueOf((String)map.get("prices"));
			float fiveimg = (Float)map.get("fiveimg");
			String pdesc = (String)map.get("pdesc");
			
			String oldPrices = (String)map.get("oldPrices");
			String productInfo = (String)map.get("productInfo");
			String peoductDescp = (String)map.get("peoductDescp");
			String saving = (String)map.get("saving");
			String five = (String)map.get("five");
			String four = (String)map.get("four");
			String three = (String)map.get("three");
			String two = (String)map.get("two");
			String one = (String)map.get("one");
			String total = (String)map.get("total");
			
			TextView title = (TextView)findViewById(R.id.widget35);
			title.setText(cname);
			
//			ImageView imagef = (ImageView)findViewById(R.id.peng_ji_img);
//			imagef.setImageResource(fiveimg);
			RatingBar ratingBar = (RatingBar) findViewById(R.id.peng_ji_img); 
			ratingBar.setRating(fiveimg);
		
			ImageView image = (ImageView)findViewById(R.id.widget34);
			Bitmap bitmap = getImageBitmap(imageurl);
			image.setImageBitmap(bitmap);
			
			ImageGetter imgGetter = new Html.ImageGetter() {
				public Drawable getDrawable(String source) {
					Drawable drawable = null;
					Log.d("Image Path", source);
					URL url;
					try {
						url = new URL(source);
						drawable = Drawable.createFromStream(url.openStream(),
								"");
					} catch (Exception e) {
						return null;
					}
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					return drawable;
				}
			};
        
			TextView desc = (TextView)findViewById(R.id.product_dec);
			desc.setText(Html.fromHtml(pdesc, imgGetter, null));
			
			TextView pinfo = (TextView)findViewById(R.id.product_info);
			pinfo.setText(Html.fromHtml(peoductDescp));
			
			TextView pstatus = (TextView)findViewById(R.id.product_status);
			pstatus.setText(Html.fromHtml(productInfo));
			
			TextView pricetv = (TextView)findViewById(R.id.widget36);
			pricetv.setText(price);
			
			TextView oldPricestv = (TextView)findViewById(R.id.oldPricestv);
			oldPrices = oldPrices + " ￥";
			SpannableString ss = new SpannableString( oldPrices);
	        ss.setSpan(new StrikethroughSpan(), 0, oldPrices.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	        oldPricestv.setText(ss);
	        
	        TextView savingtv = (TextView)findViewById(R.id.savingtv);
			saving = saving + " ￥";
			savingtv.setText(saving);
			
			if(oldPrices != null && !oldPrices.equals("0.00 ￥"))
			{
				TableRow ltr = (TableRow)findViewById(R.id.row_lodprice);
				ltr.setVisibility(View.VISIBLE);
				
				TableRow ltr2 = (TableRow)findViewById(R.id.row_savingtv);
				ltr2.setVisibility(View.VISIBLE);
			}
			
			final TextView number = (TextView)findViewById(R.id.p_numbre);
			number.setEnabled(false);
			number.setText("1");
			
			TextView totaltx = (TextView)findViewById(R.id.total_comments);
			totaltx.setText(total + " "+this.getString(R.string.itemmenu_comment));
			
			ImageView planimg5 = (ImageView)findViewById(R.id.comments_plan5);
			planimg5.setImageBitmap(createBitmap(total,five));
			TextView number5 = (TextView)findViewById(R.id.comments_numbers5);
			number5.setText("("+five+")");
			
			ImageView planimg4 = (ImageView)findViewById(R.id.comments_plan4);
			planimg4.setImageBitmap(createBitmap(total,four));
			TextView number4 = (TextView)findViewById(R.id.comments_numbers4);
			number4.setText("("+four+")");
			
			ImageView planimg3 = (ImageView)findViewById(R.id.comments_plan3);
			planimg3.setImageBitmap(createBitmap(total,three));
			TextView number3 = (TextView)findViewById(R.id.comments_numbers3);
			number3.setText("("+three+")");
			
			ImageView planimg2 = (ImageView)findViewById(R.id.comments_plan2);
			planimg2.setImageBitmap(createBitmap(total,two));
			TextView number2 = (TextView)findViewById(R.id.comments_numbers2);
			number2.setText("("+two+")");
			
			ImageView planimg1 = (ImageView)findViewById(R.id.comments_plan1);
			planimg1.setImageBitmap(createBitmap(total,one));
			TextView number1 = (TextView)findViewById(R.id.comments_numbers1);
			number1.setText("("+one+")");
			
			ImageView pbtn = (ImageView)findViewById(R.id.plus_number);
			pbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int n = Integer.valueOf(number.getText().toString());
					n = n+1;
					number.setText(String.valueOf(n));
				}
			});
			
			ImageView ubtn = (ImageView)findViewById(R.id.cut_number);
			ubtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int n = Integer.valueOf(number.getText().toString());
					if(n > 1)
						n = n-1;
					number.setText(String.valueOf(n));
				}
			});
			
			Button abtn = (Button)findViewById(R.id.add_shopping_cart);
			abtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {//222
					// TODO Auto-generated method stub
					if(prodcurColor != null)
					{
						map.put("numberText", MyMenuListView.this.getString(R.string.mybill_list_lable_8) + number.getText().toString());
						map.put("number", number.getText().toString());
						map.put("pcolor", prodcurColor);
						double nbr = Double.valueOf(number.getText().toString());
						double price = Double.valueOf(prodcurColorpirc);
						price = price + diasePiceNumber;
						double totalPrice = nbr * price;
						map.put("totalPrice", totalPrice);
						map.put("price", diasePice + " ￥");
						map.put("prices", String.valueOf(diasePice));
						map.put("price2", price + " ￥");
						map.put("prices2", prodcurColorpirc);
//						map.put("prices", diasePice);
//						map.put("oldprices", diasePice);
						map.put("sideDisheslist", sideDisheslist);
						map.put("prodcurTaste", prodcurTaste);
						myapp.getMymenulist().add(map);
					}
					else
					{
						map.put("numberText", MyMenuListView.this.getString(R.string.mybill_list_lable_8) + number.getText().toString());
						map.put("number", number.getText().toString());
						map.put("pcolor", prodcurColor);
						double nbr = Double.valueOf(number.getText().toString());
						double price = Double.valueOf((String)map.get("prices"));
						price = price + diasePiceNumber;
						double totalPrice = nbr * price;
						map.put("totalPrice", totalPrice);
						map.put("price", diasePice + " ￥");
						map.put("prices", String.valueOf(diasePice));
						map.put("price2", price + " ￥");
						map.put("prices2", String.valueOf(price));
//						map.put("prices", diasePice);
//						map.put("oldprices", diasePice);
						map.put("sideDisheslist", sideDisheslist);
						map.put("prodcurTaste", prodcurTaste);
						myapp.getMymenulist().add(map);
					}
					Toast.makeText(MyMenuListView.this,MyMenuListView.this.getString(R.string.menu_lable_3), Toast.LENGTH_SHORT).show();
					pagetag = "mlist";
					
					prodcurColor = null;
					prodcurColorpirc = null;
					
					diasePiceNumber = 0;
					oldtakesPice = 0;
					
					mViewFlipper.showPrevious();
				}
			});
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pagetag = "mlist";
					mViewFlipper.showPrevious();
				}
			});
			
			JSONObject job = api.getProductColorList(pkid);
			JSONArray jarray = job.getJSONArray("data");
			if(jarray != null && jarray.length() > 0)
			{
				Map<String,String> colormap = loadColorItemsData(job);
				String defaultstr = "";
				String originalPrice = "";
				String colorprice = "";
				String colorName = "";
				
				if(defaultstr == null)
				{
					Map<String,Object> cmap = colorlist.get(0);
					colorName = (String)cmap.get("colorName");
					originalPrice = (String)cmap.get("originalPrice");
					colorprice = (String)cmap.get("colorprice");
					defaultstr = colorName + " " + colorprice + "￥";
				}
				else
				{
					defaultstr = colormap.get("defaultstr");
					originalPrice = colormap.get("originalPrice");
					colorprice = colormap.get("colorprice");
					colorName = colormap.get("colorName");
				}
				
				prodcurColor = colorName;
				prodcurColorpirc = colorprice;
				
				pricetv.setText(colorprice+" ￥");
				
				String oldprice = originalPrice + " ￥";
				SpannableString ss2 = new SpannableString( oldprice);
		        ss2.setSpan(new StrikethroughSpan(), 0, oldprice.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		        oldPricestv.setText(ss2);
				
				double save = Double.valueOf(originalPrice) - Double.valueOf(colorprice);
				DecimalFormat r=new DecimalFormat();  
				r.applyPattern("#0.00");//保留小数位数，不足会补零  
				saving = r.format(save) + " ￥";
				savingtv.setText(saving);
				
				TextView colortv = (TextView) findViewById(R.id.color_name);
				colortv.setText(defaultstr);
				
				LinearLayout colorly = (LinearLayout) findViewById(R.id.comment_layout6);
				colorly.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showColorDialog();
					}
				});
				
				colorly.setVisibility(View.VISIBLE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line7);
				linimg.setVisibility(View.VISIBLE);
			}
			else
			{
				LinearLayout colorly = (LinearLayout) findViewById(R.id.comment_layout6);
				colorly.setVisibility(View.GONE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line7);
				linimg.setVisibility(View.GONE);
			}
			
			LinearLayout topContainer = (LinearLayout)findViewById(R.id.topContainer);
			topContainer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProductImage(pkid);
				}
			});
			
			LinearLayout commentlayout = (LinearLayout)findViewById(R.id.comment_layout);
			commentlayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProductComments(pkid,pindex);
				}
			});
			
			showLayoutSideDishes(pkid,map);
			
			showLayoutTaste(pkid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showLayoutTaste(String pkid)
	{
		try{
			JSONObject job = api.getProductTasteList(pkid);
			JSONArray jarray = job.getJSONArray("data");
			tastelist = new ArrayList<Map<String,String>>();
			if(jarray != null && jarray.length() > 0)
			{
				String keyid = "";
				String tasteName = "";
				String tastepic = "";
				tasteitems = new String [jarray.length()];
				for(int i=0;i<jarray.length();i++)
				{
					JSONObject dobjs = (JSONObject) jarray.get(i);
					Log.i("dobjString====", dobjs.toString());
					String jsonstr = dobjs.toString();
					JSONObject dobj = new JSONObject(jsonstr);
					
					String keyids = "";
					if(dobj.has("pkid"))
						keyids = dobj.getString("pkid");
					
					String tasteNames = "";
					if(dobj.has("tasteName"))
						tasteNames = dobj.getString("tasteName");
					
					String defaultTaste = "1";
					if(dobj.has("defaultTaste"))
						defaultTaste = dobj.getString("defaultTaste");
					
					String prices = "";
					if(dobj.has("prices"))
						prices = dobj.getString("prices");
					
					if(i == jarray.length() - 1)
					{
						if(tasteName.equals(""))
							tasteName = tasteNames;
					}
					
					if(defaultTaste.equals("1"))
					{
						tasteName = tasteNames;
						tastepic = prices;
					}
					
					Map<String,String> map = new HashMap<String,String>();
					map.put("tasteName", tasteNames);
					map.put("tastePic", prices);
					tastelist.add(map);
					
					tasteitems[i] = tasteNames + "  "+prices+"￥";
				}
				
				prodcurTaste = tasteName;
				prodcurTastepirc = tastepic;
				
				TextView taste_name = (TextView)findViewById(R.id.taste_name);
				taste_name.setText(tasteName);
				
				LinearLayout layout_taste = (LinearLayout)findViewById(R.id.comment_layout_taste);
				layout_taste.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showTasteDialog();
					}
				});
				
				layout_taste.setVisibility(View.VISIBLE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line9);
				linimg.setVisibility(View.VISIBLE);
			}
			else
			{
				LinearLayout layout_taste = (LinearLayout) findViewById(R.id.comment_layout_taste);
				layout_taste.setVisibility(View.GONE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line9);
				linimg.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showLayoutSideDishes(String pkid,final Map<String,Object> dmap)
	{
		try{
			LinearLayout lside = (LinearLayout)findViewById(R.id.layout_side);
			lside.removeAllViews();
			JSONObject job = api.getProductSideDishesList(pkid);
			JSONArray jarray = job.getJSONArray("data");
			if(jarray != null && jarray.length() > 0)
			{
				for(int i=0;i<jarray.length();i++)
				{
					JSONObject dobjs = (JSONObject) jarray.get(i);
					Log.i("dobjString====", dobjs.toString());
					String jsonstr = dobjs.toString();
					JSONObject dobj = new JSONObject(jsonstr);
					
					String keyids = "";
					if(dobj.has("pkid"))
						keyids = dobj.getString("pkid");
					
					String ingredientsName = "";
					if(dobj.has("ingredientsName"))
						ingredientsName = dobj.getString("ingredientsName");
					
					String price = "";
					if(dobj.has("price"))
						price = dobj.getString("price");
					
					final Map<String,String> map = new HashMap<String,String>();
					map.put("pkid", keyids);
					map.put("ingredientsName", ingredientsName);
					map.put("price", price);
					
					
					final CheckBox cb = new CheckBox(this);
					cb.setText(ingredientsName + " " + price + " ￥");
					cb.setTextColor(Color.BLACK);
					cb.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(cb.isChecked())
							{
								sideDisheslist.add(map);
								String sprice = map.get("price");
								
								double sidprice = Double.valueOf(sprice);
								DecimalFormat df = new DecimalFormat( "#.00 ");
								TextView pricetv = (TextView)findViewById(R.id.widget36);
								diasePiceNumber = diasePiceNumber + sidprice;
								pricetv.setText(df.format(diasePice+diasePiceNumber)+" ￥");
							}
							else
							{
								sideDisheslist.remove(map);
								
								String sprice = map.get("price");
								
								double sidprice = Double.valueOf(sprice);
								DecimalFormat df = new DecimalFormat( "#.00 ");
								TextView pricetv = (TextView)findViewById(R.id.widget36);
								diasePiceNumber = diasePiceNumber - sidprice;
								pricetv.setText(df.format(diasePice+diasePiceNumber)+" ￥");
							}
						}
					});
					
					lside.addView(cb);
				}
				
				LinearLayout layout_side = (LinearLayout)findViewById(R.id.comment_layout_side_dishes);
				layout_side.setVisibility(View.VISIBLE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line10);
				linimg.setVisibility(View.VISIBLE);
			}
			else
			{
				LinearLayout layout_side = (LinearLayout) findViewById(R.id.comment_layout_side_dishes);
				layout_side.setVisibility(View.GONE);
				ImageView linimg = (ImageView) findViewById(R.id.seperate_line10);
				linimg.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductImage(String productId)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ProductImageSwitcher.class);
		    Bundle bundle = new Bundle();
			bundle.putString("productId", productId);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductComments(String productId,int pindex)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ProductComments.class);
		    Bundle bundle = new Bundle();
			bundle.putString("productId", productId);
			bundle.putInt("index", pindex);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Map loadColorItemsData(JSONObject job)
	{
		Map<String,String> returnmap = new HashMap<String,String>();
		String defaultstr = "";
		try{
			colorlist = new ArrayList<Map<String,Object>>();
			JSONArray jarray = job.getJSONArray("data");
			
			coloritems = new String[jarray.length()];
			
			for(int i=0;i<jarray.length();i++)
			{
				JSONObject dobjs = (JSONObject) jarray.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String defaultColor = ""; 
				if(dobj.has("defaultColor"))
					defaultColor = (String) dobj.get("defaultColor"); 
				
				String colorName = ""; 
				if(dobj.has("colorName"))
					colorName = (String) dobj.get("colorName"); 
				
				String originalPrice = ""; 
				if(dobj.has("originalPrice"))
					originalPrice = (String) dobj.get("originalPrice"); 
				
				String colorprice = ""; 
				if(dobj.has("colorprice"))
					colorprice = (String) dobj.get("colorprice"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("defaultColor", defaultColor); //0是是，1是否
				map.put("colorName", colorName);
				map.put("originalPrice", originalPrice);
				map.put("colorprice", colorprice);
				
				coloritems[i] = colorName + " " + colorprice + "￥";
				if(defaultColor.equals("0"))
				{
					defaultstr = colorName + " " + colorprice + "￥";
				
					returnmap.put("defaultstr", defaultstr);
					returnmap.put("originalPrice", originalPrice);
					returnmap.put("colorprice", colorprice);
					returnmap.put("colorName", colorName);
				}
				
				colorlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return returnmap;
	}
	
	public void showColorDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(coloritems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Map<String,Object> cmap = colorlist.get(which);
				String colorName = (String)cmap.get("colorName");
				String originalPrice = (String)cmap.get("originalPrice");
				String colorprice = (String)cmap.get("colorprice");
				
				TextView pricetv = (TextView)findViewById(R.id.widget36);
				pricetv.setText(colorprice+" ￥");
				
				TextView oldPricestv = (TextView)findViewById(R.id.oldPricestv);
				String olaprice = originalPrice + " ￥";
				SpannableString ss = new SpannableString( olaprice);
		        ss.setSpan(new StrikethroughSpan(), 0, olaprice.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		        oldPricestv.setText(ss);
			
				TextView savingtv = (TextView)findViewById(R.id.savingtv);
				double save = Double.valueOf(originalPrice) - Double.valueOf(colorprice);
				DecimalFormat r=new DecimalFormat();  
				r.applyPattern("#0.00");//保留小数位数，不足会补零  
				String saving = r.format(save) + " ￥";
				savingtv.setText(saving);
				
				TextView colortv = (TextView) findViewById(R.id.color_name);
				colortv.setText(colorName + " " + colorprice + "￥");
				
				prodcurColor = colorName;
				prodcurColorpirc = colorprice;
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showTasteDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(tasteitems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String tasteName = (String)tasteitems [which];
				
				TextView tname = (TextView)findViewById(R.id.taste_name);
				tname.setText(tasteName);
				
//				Map<String,String> map = tastelist.get(which);
//				String tasteName = map.get("tasteName");
//				String tastePic = map.get("tastePic");
//				if(tastePic.equals(""))
//					tastePic = "0";
//				
//				prodcurTaste = tasteName;
//				prodcurTastepirc = tastePic;
//				
//				DecimalFormat df = new DecimalFormat( "#.00 ");
//				TextView pricetv = (TextView)findViewById(R.id.widget36);
//				diasePiceNumber = Double.valueOf(tastePic) + diasePiceNumber - oldtakesPice;
//				pricetv.setText(df.format(diasePiceNumber + diasePice)+" ￥");
//				
//				TextView tname = (TextView)findViewById(R.id.taste_name);
//				tname.setText(tasteName + "  "+tastePic+"￥");
				
//				oldtakesPice = Double.valueOf(tastePic);
			}
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	private static Bitmap createBitmap(String total, String number) 
	{

		// create the new blank bitmap
		
		double bai = Double.valueOf(total) / Double.valueOf(number);
		
//		double ww = 200 * (bai / 10);
		
		double ww = 200 / bai;

		Bitmap newb = Bitmap.createBitmap(200, 30, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

		Canvas cv = new Canvas(newb);

		 Paint mPaint = new Paint();     
	        
	        //设置画笔颜色     
	        mPaint.setColor(Color.parseColor("#FFFF93"));     
	        //设置填充     
	        mPaint.setStyle(Style.FILL);     
	             
	        //画一个矩形,前俩个是矩形左上角坐标，后面俩个是右下角坐标     
	        cv.drawRect(new Rect(0, 0, 200, 30), mPaint);  
	        
	        Paint mPaint2 = new Paint();     
	        
	        //设置画笔颜色     
	        mPaint2.setColor(Color.parseColor("#ffa500"));     
	        //设置填充     
	        mPaint2.setStyle(Style.FILL);     
	             
	        //画一个矩形,前俩个是矩形左上角坐标，后面俩个是右下角坐标     
	        cv.drawRect(new Rect(0, 0, (int)ww, 30), mPaint2);     

		// save all clip

		cv.save(Canvas.ALL_SAVE_FLAG);// 保存

		// store

		cv.restore();// 存储

		return newb;
	}
	
//	public void showMenuStratWoid(final Map<String,Object> map)
//	{
//		try{
//			new AlertDialog.Builder(this).setTitle("提示")
//			.setMessage("是否确定将该商品放入我的购物车中?").setIcon(R.drawable.error2)
//			.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							try{
//								if(tag.equals("0"))
//									myapp.getMymenulist().add(map);
//								else
//								{
//									String roomNo = myapp.getRoomNo();
//									List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
//									dlist.add(map);
//									Map<String,Object> map = new HashMap<String,Object>();
//									map.put("roomNo", roomNo);
//									map.put("data", dlist);
//									
//									JSONObject job = new JSONObject(map);
//									System.out.println("=====================jsonStr==========="+job.toString());
//									
//									
//									api.addMyMenuData(job.toString());
//								}
//									
//							}catch(Exception ex){
//								ex.printStackTrace();
//							}
//							
//							Toast.makeText(MyMenuListView.this,"该商品已经放入我的购物车", Toast.LENGTH_SHORT)
//									.show();
//						}
//					}).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// 取消按钮事件
//						}
//					}).show();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * 显示我的购物车菜单列表
	 */
	public void showMyMenuPage()
	{
		try{
			ListView slistView = (ListView)findViewById(R.id.ListView_catalog2);
			final List<Map<String,Object>> dlist = myapp.getMymenulist();
			final SpecialAdapter listItemAdapter;
			if(dlist != null)
			{
				if(tag.equals("0"))//222
				{
					// 生成适配器的Item和动态数组对应的元素
					listItemAdapter = new SpecialAdapter(this, dlist,// 数据源
							R.layout.menu_list_cart,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "imgurl", "cname" ,"price2","fiveimg","numberText","pcolor" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.menu_image, R.id.menu_name, R.id.menu_price,R.id.five_star_img,R.id.p_number,R.id.p_color },share,"ico");
					
					slistView.setDividerHeight(0);
					// 添加并且显示
					slistView.setAdapter(listItemAdapter);
				}
				else
				{
					// 生成适配器的Item和动态数组对应的元素
					listItemAdapter = new SpecialAdapter(this, getMyMenuListData(),// 数据源
							R.layout.my_menu_list,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "imgurl", "cname" ,"price2","status","amount" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.menu_image, R.id.menu_name, R.id.menu_price,R.id.menu_status,R.id.menu_amount },share,"ico");
					
					slistView.setDividerHeight(0);
					// 添加并且显示
					slistView.setAdapter(listItemAdapter);
				}
			}
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
//					String proudid = (String)dlist.get(arg2).get("pkid");
//					String imgurl = (String)dlist.get(arg2).get("imgurl");
//					String number = (String)dlist.get(arg2).get("number");
//					String cname = (String)dlist.get(arg2).get("cname");
//					String price = (String)dlist.get(arg2).get("price");
//					String fiveimg = (String)dlist.get(arg2).get("score");
//					String pdesc = (String)dlist.get(arg2).get("pdesc");
//					String prices = (String)dlist.get(arg2).get("prices");
//					
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("pkid", proudid);
//					map.put("imgurl", imgurl);
//					map.put("cname", cname);
//					map.put("number", number);
//					map.put("price", price);
//					map.put("fiveimg", fiveimg);
//					map.put("pdesc", pdesc);
//					map.put("prices", prices);
					
					Map<String,Object> map = dlist.get(arg2);
					
					if(tag.equals("0"))
					{
//						showCancelMenuStratWoid(map,arg2);
						showOperateDialog(map,arg2);
					}
				}
			});
			
			double totle = 0;
			RelativeLayout titleLayout = (RelativeLayout)findViewById(R.id.title_lable_layout);
			if(dlist.size() > 0)
			{
				titleLayout.setVisibility(View.VISIBLE);
				for(int i=0;i<dlist.size();i++)
				{
					Map map = dlist.get(i);
					double prices = (Double)map.get("totalPrice");
					totle = totle + prices;
				}
				DecimalFormat df = new DecimalFormat("######0.00");   
				TextView ltxt = (TextView)findViewById(R.id.text_lable);
				ltxt.setText(this.getString(R.string.menu_lable_4) + df.format(totle) + " ￥");
				
				Button pbtn = (Button)findViewById(R.id.payment_btn);
				pbtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showPaymentWind();
					}
				});
			}
			else
			{
				titleLayout.setVisibility(View.GONE);
			}
			
			
			
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//			lp.addRule(RelativeLayout.ABOVE, R.id.GridView_toolbar);
//			sview.setLayoutParams(lp);

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showPaymentWind(){
		try{
			final View view = LayoutInflater.from(this).inflate(R.layout.payment_page_pupopo, null);
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String cdate = sf.format(new Date());
			Calendar Cld = Calendar.getInstance();
			int YY = Cld.get(Calendar.YEAR) ;
			int MM = Cld.get(Calendar.MONTH)+1;
			int DD = Cld.get(Calendar.DATE);
			int HH = Cld.get(Calendar.HOUR_OF_DAY);
			int mm = Cld.get(Calendar.MINUTE);
			int SS = Cld.get(Calendar.SECOND);
			int MI = Cld.get(Calendar.MILLISECOND);
			
			Random x = new Random();
			int t = x.nextInt(1000);
			int leng = String.valueOf(t).length();
			String tt = String.valueOf(t);
			for(int j=leng;j<4;j++)
			{
				tt += "0";
			}
			
			String orderOn = "NO"+YY + MM +DD+HH+mm+SS+MI+tt;
			TextView title = (TextView)view.findViewById(R.id.widget35);
			title.setText(this.getString(R.string.menu_lable_5) + orderOn);
			orderNos = orderOn;
			
			List<Map<String,Object>> dlist = myapp.getMymenulist();
			double totle = 0;
			if(dlist.size() > 0)
			{
				for(int i=0;i<dlist.size();i++)
				{
					Map map = dlist.get(i);
					double prices = (Double)map.get("totalPrice");
					totle = totle + prices;
				}
			}
			
			final TextView tprice = (TextView)view.findViewById(R.id.total_price);
			tprice.setText(this.getString(R.string.mybill_list_lable_7) + totle + " ￥");
			totlePice = totle;
			
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNumber = "";
			if (tm.getLine1Number() != null) {
				phoneNumber = tm.getLine1Number();
			}
			final EditText pnumber = (EditText)view.findViewById(R.id.payment_phone);
			pnumber.setText(phoneNumber);
			
			String userName = myapp.getUserName();
			final EditText receiName = (EditText)view.findViewById(R.id.payment_recei_name);
			receiName.setText(userName);
			
			final EditText addressEd = (EditText)view.findViewById(R.id.payment_address);
//			String dfaddress = myapp.getCountry() + myapp.getCity() + myapp.getArea() + myapp.getRoad() + myapp.getNumbers(); 
//			addressEd.setText(dfaddress);
			
			Button seladdress = (Button)view.findViewById(R.id.sel_address_btn);
			seladdress.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showSelAddressDialog(addressEd);
				}
			});
			
			final TextView logisticstv = (TextView)view.findViewById(R.id.logistics_id);
			
			final TextView sellogistics = (TextView)view.findViewById(R.id.sel_logistics_btn);
			sellogistics.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showSelLogisticsDialog(sellogistics,logisticstv,tprice);
				}
			});
			
			WindowManager m = getWindowManager();    
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
                
            int width = (int) (d.getWidth() * 0.9);    //宽度设置为屏幕的0.8   
                
            LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT);
            topPanel.setLayoutParams(lp);
            
			myDialog = new Dialog(this, R.style.AliDialog);
			myDialog.setContentView(view);
			myDialog.show();
			
			Button btn = (Button)view.findViewById(R.id.add_order_btn);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phonenumber = pnumber.getText().toString();
					String username = receiName.getText().toString();
					String address = addressEd.getText().toString();
					EditText descEd = (EditText)view.findViewById(R.id.payment_desc);
					String desc = descEd.getText().toString();
					String logid = logisticstv.getText().toString();
					boolean b = true;
					if(phonenumber.equals(""))
					{
						b = false;
						makeText(MyMenuListView.this.getString(R.string.menu_lable_6));
					}
					if(username.equals(""))
					{
						b = false;
						makeText(MyMenuListView.this.getString(R.string.menu_lable_7));
					}
					if(address.equals(""))
					{
						b = false;
						makeText(MyMenuListView.this.getString(R.string.menu_lable_8));
					}
					if(logid.equals(""))
					{
						b = false;
						makeText(MyMenuListView.this.getString(R.string.menu_lable_9));
					}
					if(b)
						setPayment(username,phonenumber,address,desc,logid);
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
	
	public void showSelAddressDialog(final EditText addressEd){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
		
		try{
			JSONObject jobn = api.getPfAddressList();
			JSONArray jarr = jobn.getJSONArray("data");
			if(jarr != null)
			{
				addressitems = new String[jarr.length()];
				for(int i=0;i<jarr.length();i++)
				{
					JSONObject job = jarr.getJSONObject(i);
					String address = job.getString("address");
					
					addressitems[i] = address;
				}
				
				builder.setItems(addressitems, new DialogInterface.OnClickListener() {
		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						addressEd.setText(addressitems[which]);
					}
					
					
				});
				
				alertDialog = builder.create();
				alertDialog.show();
			}
			else
			{
				makeText(MyMenuListView.this.getString(R.string.menu_lable_10));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showSelLogisticsDialog(final TextView sellogistics,final TextView logisticstv,final TextView tprice){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
		
		try{
			JSONObject jobn = api.getLogisticsList(appliconStoreId);
			JSONArray jarr = jobn.getJSONArray("data");
			if(jarr != null)
			{
				logitems = new String[jarr.length()];
				logisticsids = new ArrayList<JSONObject>();
				for(int i=0;i<jarr.length();i++)
				{
					JSONObject job = jarr.getJSONObject(i);
					String lpice = job.getString("lpice");
					String lname = job.getString("lname");
					String pkid = job.getString("pkid");
					
					logitems[i] = lname + " " + lpice + "￥";
					logisticsids.add(job);
				}
				
				builder.setItems(logitems, new DialogInterface.OnClickListener() {
		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						JSONObject job = logisticsids.get(which);
						try {
							String lpice = job.getString("lpice");
							String lname = job.getString("lname");
							String pkid = job.getString("pkid");
							
							sellogistics.setText(logitems[which]);
							logisticstv.setText(pkid);
							
							double newtotle = totlePice + Double.valueOf(lpice);
							oldtotlePice = Double.valueOf(lpice);
							DecimalFormat form = new DecimalFormat( "#.00 "); 
							tprice.setText(MyMenuListView.this.getString(R.string.mybill_list_lable_7) + form.format(newtotle) + " ￥");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
				});
				
				alertDialog = builder.create();
				alertDialog.show();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showOperateDialog(final Map map,final int arg2 ){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
				
		CharSequence[] operateitems = {this.getString(R.string.menu_lable_11),this.getString(R.string.menu_lable_12)};
		builder.setItems(operateitems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				if(which == 0)
					showChangePnumberWoid(map,arg2);
				else if(which == 1)
					showCancelMenuStratWoid(map,arg2);
			}

		});

		alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void setPayment(String rName,String pho,String addr,String de,String logid)
	{
		try{
			List<Map<String,Object>> dlist = myapp.getMymenulist();
			List<Map<String,Object>> jsondatas = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = new HashMap<String,Object>();
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> dmap = dlist.get(i);
				
				Map<String,Object> maps = new HashMap<String,Object>();
				maps.put("imgurl", (String)dmap.get("imgurl"));
				maps.put("cname", (String)dmap.get("cname"));
				maps.put("prices", Double.valueOf((String)dmap.get("prices2")));//222
				maps.put("fiveimg", String.valueOf((Float)dmap.get("fiveimg")));
				maps.put("pkid", (String)dmap.get("pkid"));
				maps.put("productType",(String)dmap.get("productType"));
				maps.put("totalPrice", (Double)dmap.get("totalPrice"));
				maps.put("number", Integer.valueOf((String)dmap.get("number")));
				maps.put("pcolor", (String)dmap.get("pcolor"));
				if(dmap.get("sideDisheslist") != null)
					maps.put("sideDisheslist", dmap.get("sideDisheslist"));
				if(dmap.get("prodcurTaste") != null)
					maps.put("prodcurTaste", (String)dmap.get("prodcurTaste"));
				
				jsondatas.add(maps);
			}
			map.put("data", jsondatas);
			
			JSONObject job = new JSONObject(map);
			System.out.println("=====================jsonStr==========="+job.toString());
			
			String orderNo = orderNos;
//			String pfid = myapp.getPfprofileId();
			String storeInfoId = appliconStoreId;
//			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String cdate = sf.format(new Date());
//			String orderTime = cdate;
//			String orderStatus = "1";
			String receiName = rName;
			String phone = pho;
			String address = addr;
			totlePice = totlePice + oldtotlePice;
			String totalPrice = String.valueOf(totlePice);
			String desc = de; 
			
//			JSONObject json = api.addMyMenuData(job.toString(),orderNo,storeInfoId,receiName,phone,address,totalPrice,desc,logid);
//			if(json.has("tag"))
//			{
//				String requesttag = json.getString("tag");
//				if(requesttag.equals("success"))
//				{
//					makeText(this.getString(R.string.menu_lable_13));
//					List<Map<String,Object>> nlist = new ArrayList<Map<String,Object>>();
//					myapp.setMymenulist(nlist);
//					myDialog.dismiss();
//					showMyMenuPage();
////					mViewFlipper.showPrevious();
//				}
//				else
//				{
//					makeText(this.getString(R.string.menu_lable_14));
//				}
//			}
//			else
//			{
//				makeText(this.getString(R.string.menu_lable_15));
//			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showCancelMenuStratWoid(final Map<String,Object> map,final int index)
	{
		try{
			new AlertDialog.Builder(this).setTitle(this.getString(R.string.record_lable_2))
			.setMessage(this.getString(R.string.menu_lable_16)).setIcon(R.drawable.error2)
			.setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							try{
								myapp.getMymenulist().remove(index);
								
								showMyMenuPage();
							}catch(Exception ex){
								ex.printStackTrace();
							}
							
							Toast.makeText(MyMenuListView.this,MyMenuListView.this.getString(R.string.menu_lable_17), Toast.LENGTH_SHORT)
									.show();
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 取消按钮事件
						}
					}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showChangePnumberWoid(final Map<String,Object> map,final int index)
	{
		try{
			final View view = LayoutInflater.from(this).inflate(R.layout.proudect_number_popup, null);
			
			final TextView numbertv = (TextView)view.findViewById(R.id.p_numbre);
			numbertv.setText("1");
			
			ImageView pbtn = (ImageView)view.findViewById(R.id.plus_number);
			pbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int n = Integer.valueOf(numbertv.getText().toString());
					n = n+1;
					numbertv.setText(String.valueOf(n));
				}
			});
			
			ImageView ubtn = (ImageView)view.findViewById(R.id.cut_number);
			ubtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int n = Integer.valueOf(numbertv.getText().toString());
					if(n > 1)
						n = n-1;
					numbertv.setText(String.valueOf(n));
				}
			});
			
			myDialog = new Dialog(this, R.style.AliDialog);
			myDialog.setContentView(view);
			myDialog.show();
			
			Button dbtn = (Button)view.findViewById(R.id.determine_btn);
			dbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try{
						myapp.getMymenulist().remove(index);
						
						map.put("numberText", MyMenuListView.this.getString(R.string.mybill_list_lable_8) + numbertv.getText().toString());
						map.put("number", numbertv.getText().toString());
						double nbr = Double.valueOf(numbertv.getText().toString());
						System.out.println("price======2"+map.get("prices"));
						double price = Double.valueOf((String)map.get("prices"));
						double totalPrice = nbr * price;
						map.put("totalPrice", totalPrice);
						myapp.getMymenulist().add(map);
						
						showMyMenuPage();
						
						myDialog.dismiss();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
			
			Button cbtn = (Button)view.findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
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
	
	public List<Map<String,Object>> getMenuListData(String pid)
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			
			List<Map<String,Object>> lists = myapp.getMyCardsAll();
			Map map = lists.get(Integer.valueOf(index));
			
			String storeid = (String)map.get("storeid");
			
			jobj = api.getMenuData(pid,storeid,"1","",0);
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getMenuList2(jArr);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMenuList2(JSONArray jArr){
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
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMenuTypeListData()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			
			List<Map<String,Object>> lists = myapp.getMyCardsAll();
			Map map = lists.get(Integer.valueOf(index));
			
			String storeid = (String)map.get("storeid");
			appliconStoreId = storeid;
			
			jobj = api.getMenuTypeData(storeid);
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getMenuList(jArr);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMenuList(JSONArray jArr){
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
				if(dobj.has("tname"))
					cname = (String) dobj.get("tname");
				
				String imgurl = ""; 
				if(dobj.has("sysImg"))
					imgurl = (String) dobj.get("sysImg");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("mimg", imgurl);
				map.put("cname", cname);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 结算窗口
	 * @param map
	 */
	public void showSettlementWoid(String pice)
	{
		try{
			AlertDialog dialog = new AlertDialog.Builder(this).setTitle(this.getString(R.string.setting_title))
			.setMessage(this.getString(R.string.menu_lable_18)+ pice +" ￥ "+this.getString(R.string.menu_lable_19)).setIcon(R.drawable.error2)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							try{
								String roomNo = myapp.getRoomNo();
								List<Map<String,Object>> dlist = myapp.getMymenulist();
								Map<String,Object> map = new HashMap<String,Object>();
								map.put("roomNo", roomNo);
								map.put("data", dlist);
								
								JSONObject job = new JSONObject(map);
								System.out.println("=====================jsonStr==========="+job.toString());
								
								
//								api.addMyMenuData(job.toString());
								
								tag = "1";
								
								showMyMenuPage();
							}catch(Exception ex){
								ex.printStackTrace();
							}
							
							Toast.makeText(MyMenuListView.this,MyMenuListView.this.getString(R.string.menu_lable_20), Toast.LENGTH_SHORT)
									.show();
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 取消按钮事件
						}
					}).show();
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();  
			params.width = 500;  
			params.height = 300 ;
			dialog.getWindow().setAttributes(params);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String, Object>> getMyMenuListData()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			if(tag.equals("1"))
			{
				JSONObject jobj;
				U.dout(0);
				
				jobj = api.getMyMenuDataList(myapp.getRoomNo());
				if(jobj != null)
				{
					JSONArray jArr = (JSONArray) jobj.get("data");
					list = getMyMenuList(jArr);
					
					deleteMyMenulist();
//					myapp.setMymenulist(list);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMyMenuList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("eId"))
					pkid = (String) dobj.get("eId"); 
				
				String cpkid = ""; 
				if(dobj.has("pName"))
					cpkid = (String) dobj.get("pName");
				
				String cname = ""; 
				if(dobj.has("pNames"))
					cname = (String) dobj.get("pNames");
				
				String imgurl = ""; 
				if(dobj.has("sysImg"))
					imgurl = (String) dobj.get("sysImg");
				
				String price = ""; 
				if(dobj.has("tota"))
					price = (String) dobj.get("tota") + " ￥";
				
				String amount = this.getString(R.string.mybill_list_lable_8);
				if(dobj.has("amount"))
					amount += (String) dobj.get("amount");
				
				String status = ""; 
				if(dobj.has("status"))
					status += (String) dobj.get("status");
				
				String statusName = this.getString(R.string.menu_lable_21); 
				if(dobj.has("statusName"))
					statusName += (String) dobj.get("statusName");
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("imgurl", imgurl);
				map.put("cname", cname);
				map.put("cpkid", cpkid);
				map.put("price", price);
				map.put("status", statusName);
				map.put("amount", amount);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void deleteMyMenulist()
	{
		try{
			List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> map = dlist.get(i);
				dlist.remove(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
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
