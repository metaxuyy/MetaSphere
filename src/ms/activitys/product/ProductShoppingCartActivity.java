package ms.activitys.product;

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
import org.json.JSONObject;

import ms.activitys.R;
import ms.activitys.hotel.HotelActivity;
import ms.activitys.vipcards.CardsView;
import ms.globalclass.dbhelp.DBHelperShoppingCart;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ProductShoppingCartActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ScrollView scroll;
	private ImageView imgShopcar;
	private TextView textShopcarIsNullInfo;
	private List<View> viewlist = new ArrayList<View>();
	private boolean editcart = false;
	private List<ImageView> linelist = new ArrayList<ImageView>();
	
	private int index;
	private String maptag = "";
    private String advertiseNotification;
    private DBHelperShoppingCart db;
    private String storeid;
    private ProgressDialog mypDialog;
    private List<Map<String,Object>> prodoutList = new ArrayList<Map<String,Object>>();
    private Button edit_cart;
    private Button real_btn;
    
    private TextView textOffSellInfo1;
    private TextView textOffSellInfo2;
    private String typesMapping;
    
    private List<String> shelfidlist = new ArrayList<String>();
    public static ProductShoppingCartActivity instance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_shopping_cart);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperShoppingCart(this,myapp);
		
		instance = this;
		
		index = this.getIntent().getExtras().getInt("index");
		maptag = this.getIntent().getExtras().getString("map");
		advertiseNotification = this.getIntent().getExtras().getString("advertiseNotification");
		storeid = this.getIntent().getExtras().getString("storeid");
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		Map<String,Object> cardmap = list.get(index);
		typesMapping = (String)cardmap.get("typesMapping");
		
		
		textOffSellInfo1 = (TextView)findViewById(R.id.textOffSellInfo1);
		textOffSellInfo2 = (TextView)findViewById(R.id.textOffSellInfo2);
		
		scroll = (ScrollView)findViewById(R.id.scrBody);
		imgShopcar = (ImageView)findViewById(R.id.imgShopcar);
		textShopcarIsNullInfo = (TextView)findViewById(R.id.textShopcarIsNullInfo);
		
		edit_cart = (Button)findViewById(R.id.edit_cart);
		edit_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!editcart)
				{
					showEditCart(editcart);
					edit_cart.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_40));
					editcart = true;
				}
				else
				{
					showEditCart(editcart);
					edit_cart.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_32));
					editcart = false;
				}
			}
		});
		
		real_btn = (Button)findViewById(R.id.real_btn);
		real_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAccountscenterActivity();
			}
		});
		
		showProductCart();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Map<String,Object> map = myapp.getMyCardsAll().get(index);
			String typesMapping = (String)map.get("typesMapping");
			if(typesMapping.equals("09"))
			{
				Intent intent = new Intent();
				intent.setClass(this, HotelActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putInt("index", index);
				bundle.putString("map", maptag);
				bundle.putString("advertiseNotification", advertiseNotification);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();
			}
			else
			{
				Intent intent = new Intent();
				intent.setClass(this, CardsView.class);
			    Bundle bundle = new Bundle();
				bundle.putString("index", String.valueOf(index));
				bundle.putString("map", maptag);
				bundle.putString("advertiseNotification", advertiseNotification);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
			    this.finish();
			}
			return false;
		}
		return false;
	}
	
	public void showAccountscenterActivity()
	{
		try{
			Intent intent = new Intent();
			intent.setClass(this, AccountscenterActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", storeid);
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductCart()
	{
		try{
			DecimalFormat df = new DecimalFormat("######0.00");   
//			List<Map<String,Object>> dlist = myapp.getMymenulist();
			List<Map<String,Object>> dlist = db.loadMyShoopingCart(storeid);
//			boolean isplist = false;
//			if(myapp.getProductList() != null && myapp.getProductList().size()>0)
//				isplist = true;
			if(dlist != null && dlist.size() > 0)
			{
//				scroll.setVisibility(View.VISIBLE);
//				edit_cart.setVisibility(View.VISIBLE);
//				real_btn.setVisibility(View.VISIBLE);
//				imgShopcar.setVisibility(View.GONE);
//				textShopcarIsNullInfo.setVisibility(View.GONE);
				
				showProductCart2(dlist);
//				TextView textTotalBuyCount = (TextView)findViewById(R.id.textTotalBuyCount);
//				TextView textTotalPoint = (TextView)findViewById(R.id.textTotalPoint);
//				TextView textTotalPayMoney = (TextView)findViewById(R.id.textTotalPayMoney);
//				
//				TextView textTotalBuyCount2 = (TextView)findViewById(R.id.textTotalBuyCount2);
//				TextView textTotalPoint2 = (TextView)findViewById(R.id.textTotalPoint2);
//				TextView textTotalPayMoney2 = (TextView)findViewById(R.id.textTotalPayMoney2);
//				
//				int totalNumber = 0;
//				double tprice = 0;
//				LinearLayout layout = (LinearLayout)findViewById(R.id.cart_item);
//				LinearLayout layoutShelf = (LinearLayout)findViewById(R.id.shelf_item);
//				List<String> pidlist = new ArrayList<String>();
//				for(int i=0;i<dlist.size();i++)
//				{
//					View view = LayoutInflater.from(this).inflate(R.layout.shopcaritem, null);
//					Map<String,Object> map = dlist.get(i);
//					String imgurl = (String)map.get("imgurl");
//					Bitmap img = (Bitmap)map.get("imgbitm");
//					String cname = (String)map.get("cname");
//					String size = (String)map.get("productSize");
//					String color = (String)map.get("pcolor");
//					String number = (String)map.get("number");
//					String price = (String)map.get("prices");
//					double totalPrice = (Double)map.get("totalPrice");
//					final String pId = (String)map.get("pkid");
//					String uuid = (String)map.get("uuid");
//					String isshelf = (String)map.get("isshelf");
//					
////					if(!isplist)
////						pidlist.add(pId);
//					boolean isShelf = true;
////					if(isshelf.equals("1"))//如果这样的话性能会比较好，当时当该货物上架时就不知道了
//						isShelf = loadProductDetail(pId);
////					else
////						shelfidlist.add(pId);
//					if(isShelf)
//					{
//						totalNumber = totalNumber + Integer.valueOf(number);
//						tprice = tprice + totalPrice;
//						
//						ImageView pimg = (ImageView)view.findViewById(R.id.imgProductImage);
//	//					String str = imgurl.substring(0,imgurl.lastIndexOf("."));
//	//					String str2 = imgurl.substring(imgurl.lastIndexOf("."),imgurl.length());
//	//					imgurl = str+"_zhong"+str2;
//	//					loadThreadData(imgurl,pimg);
//						pimg.setImageBitmap(img);
//						
//						TextView pidtxt = (TextView)view.findViewById(R.id.productid);
//						pidtxt.setText(pId);
//						
//						TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
//						uuidtxt.setText(uuid);
//						
//						TextView nametxt = (TextView)view.findViewById(R.id.textProductName);
//						nametxt.setText(cname);
//						
//						TextView textSize = (TextView)view.findViewById(R.id.textSize);
//						textSize.setText(this.getString(R.string.menu_lable_30)+size);
//						
//						TextView textColor = (TextView)view.findViewById(R.id.textColor);
//						textColor.setText(this.getString(R.string.menu_lable_41)+": "+color);
//						
//						TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
//						textBuyCount.setText(this.getString(R.string.mybill_list_lable_8)+number);
//						
//						EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
//						editBuyCount.setText(number);
//						
//						TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
//						textPrice.setText(df.format(Double.valueOf(price))+"￥");
//						
//						TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
//						textTotalValue.setText(df.format(totalPrice)+"￥");
//						
//						view.setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								showProductDetail(pId);
//							}
//						});
//						
//						layout.addView(view);
//						
//						ImageView lineimg = new ImageView(this);
//						lineimg.setBackgroundResource(R.drawable.main_line);
//						lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
//						layout.addView(lineimg);
//						
//						linelist.add(lineimg);
//						viewlist.add(view);
//						
//						db.udateMyShoopingCartShelf(uuid,"1");
//					}
//					else
//					{
//						ImageView pimg = (ImageView)view.findViewById(R.id.imgProductImage);
//						pimg.setImageBitmap(img);
//						
//						TextView pidtxt = (TextView)view.findViewById(R.id.productid);
//						pidtxt.setText(pId);
//						
//						TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
//						uuidtxt.setText(uuid);
//						
//						TextView nametxt = (TextView)view.findViewById(R.id.textProductName);
//						nametxt.setText(cname);
//						
//						TextView textSize = (TextView)view.findViewById(R.id.textSize);
//						textSize.setText(this.getString(R.string.menu_lable_30)+size);
//						
//						TextView textColor = (TextView)view.findViewById(R.id.textColor);
//						textColor.setText(this.getString(R.string.menu_lable_41)+": "+color);
//						
//						TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
//						textBuyCount.setText(this.getString(R.string.mybill_list_lable_8)+number);
//						
//						EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
//						editBuyCount.setText(number);
//						
//						TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
//						textPrice.setText(df.format(Double.valueOf(price))+"￥");
//						
//						TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
//						textTotalValue.setText(df.format(totalPrice)+"￥");
//						
//						layoutShelf.addView(view);
//						
//						ImageView lineimg = new ImageView(this);
//						lineimg.setBackgroundResource(R.drawable.main_line);
//						lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
//						layoutShelf.addView(lineimg);
//						
//						db.udateMyShoopingCartShelf(uuid,"0");
//					}
//				}
//				
////				if(!isplist)
////					loadProductDetail(pidlist);
//				
//				textTotalBuyCount.setText(String.valueOf(totalNumber)+this.getString(R.string.menu_lable_42));
//				textTotalPoint.setText(String.valueOf((int)tprice)+this.getString(R.string.menu_lable_43));
//				textTotalPayMoney.setText("￥"+df.format(tprice));
//				
//				textTotalBuyCount2.setText(String.valueOf(totalNumber)+this.getString(R.string.menu_lable_42));
//				textTotalPoint2.setText(String.valueOf((int)tprice)+this.getString(R.string.menu_lable_43));
//				textTotalPayMoney2.setText("￥"+df.format(tprice));
			}
			
//			if(shelfidlist != null && shelfidlist.size() > 0)
//			{
//				textOffSellInfo1.setVisibility(View.VISIBLE);
//				textOffSellInfo2.setVisibility(View.VISIBLE);
//				
//			}
//			else
//			{
//				textOffSellInfo1.setVisibility(View.GONE);
//				textOffSellInfo2.setVisibility(View.GONE);
//			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductCart2(final List<Map<String,Object>> dlist)
	{
		showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				List<Map<String,Object>> mdlist = new ArrayList<Map<String,Object>>();
				for(int i=0;i<dlist.size();i++)
				{
					Map<String,Object> map = dlist.get(i);
					String pId = (String)map.get("pkid");
					
					boolean isShelf = loadProductDetail(pId);
					map.put("tag", isShelf);
					
					mdlist.add(map);
				}
				msg.obj = mdlist;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData(final String imgurl,final ImageView pimg)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				Bitmap bitmap = getImageBitmap(imgurl);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("bitm", bitmap);
				map.put("view", pimg);
				msg.obj = map;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String, Object> map = (Map<String,Object>)msg.obj;
				Bitmap bitimg = (Bitmap)map.get("bitm");
				ImageView imgview = (ImageView)map.get("view");
				imgview.setImageBitmap(bitimg);
				break;
			case 1:
				try{
					int indexs = (Integer)msg.obj;
					mypDialog.dismiss();
					Intent intent = new Intent();
//				    intent.setClass( this,MyMenuListView.class);
					intent.setClass(ProductShoppingCartActivity.this, ProductDetail.class);
				    Bundle bundle = new Bundle();
					bundle.putInt("pindex", indexs);
					bundle.putString("cart", "cart");
					bundle.putString("index", String.valueOf(index));
					bundle.putString("map", maptag);
					bundle.putString("advertiseNotification", advertiseNotification);
					bundle.putString("storeid", storeid);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 2:
				List<Map<String,Object>> mlist = (List<Map<String,Object>>)msg.obj;
				
				if(mlist.size() > 0)
				{
					scroll.setVisibility(View.VISIBLE);
					edit_cart.setVisibility(View.VISIBLE);
					real_btn.setVisibility(View.VISIBLE);
					imgShopcar.setVisibility(View.GONE);
					textShopcarIsNullInfo.setVisibility(View.GONE);
					
					TextView textTotalBuyCount = (TextView)findViewById(R.id.textTotalBuyCount);
					TextView textTotalPoint = (TextView)findViewById(R.id.textTotalPoint);
					TextView textTotalPayMoney = (TextView)findViewById(R.id.textTotalPayMoney);
					
					TextView textTotalBuyCount2 = (TextView)findViewById(R.id.textTotalBuyCount2);
					TextView textTotalPoint2 = (TextView)findViewById(R.id.textTotalPoint2);
					TextView textTotalPayMoney2 = (TextView)findViewById(R.id.textTotalPayMoney2);
					
					DecimalFormat df = new DecimalFormat("######0.00");
					int totalNumber = 0;
					double tprice = 0;
					LinearLayout layout = (LinearLayout)findViewById(R.id.cart_item);
					LinearLayout layoutShelf = (LinearLayout)findViewById(R.id.shelf_item);
					for(int i=0;i<mlist.size();i++)
					{
						View view = LayoutInflater.from(ProductShoppingCartActivity.this).inflate(R.layout.shopcaritem, null);
						Map<String,Object> nmap = mlist.get(i);
						String imgurl = (String)nmap.get("imgurl");
						Bitmap img = (Bitmap)nmap.get("imgbitm");
						String cname = (String)nmap.get("cname");
						String size = (String)nmap.get("productSize");
						String color = (String)nmap.get("pcolor");
						String number = (String)nmap.get("number");
						String price = (String)nmap.get("prices");
						double totalPrice = (Double)nmap.get("totalPrice");
						final String pId = (String)nmap.get("pkid");
						String uuid = (String)nmap.get("uuid");
						boolean isShelf = (Boolean)nmap.get("tag");
						String dishestr = (String)nmap.get("dishestr");
						
						if(isShelf)
						{
							totalNumber = totalNumber + Integer.valueOf(number);
							tprice = tprice + Double.valueOf(price)*Integer.valueOf(number);
							
							ImageView pimg = (ImageView)view.findViewById(R.id.imgProductImage);
							pimg.setImageBitmap(img);
							
							TextView pidtxt = (TextView)view.findViewById(R.id.productid);
							pidtxt.setText(pId);
							
							TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
							uuidtxt.setText(uuid);
							
							TextView nametxt = (TextView)view.findViewById(R.id.textProductName);
							nametxt.setText(cname);
							
							if(typesMapping.equals("10"))
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_138)+color);
								
								TextView textdients = (TextView)view.findViewById(R.id.textdients);
								textdients.setVisibility(View.VISIBLE);
								textdients.setText(ProductShoppingCartActivity.this.getString(R.string.produce_with4)+": "+dishestr);
							}
							else
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_41)+": "+color);
							}
							
							TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
							textBuyCount.setText(ProductShoppingCartActivity.this.getString(R.string.mybill_list_lable_8)+number);
							
							EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
							editBuyCount.setText(number);
							
							TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
							textPrice.setText(df.format(Double.valueOf(price))+"￥");
							
							TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
							textTotalValue.setText(df.format(Double.valueOf(price)*Integer.valueOf(number))+"￥");
							
							view.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									showProductDetail(pId);
								}
							});
							
							layout.addView(view);
							
							ImageView lineimg = new ImageView(ProductShoppingCartActivity.this);
							lineimg.setBackgroundResource(R.drawable.main_line);
							lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
							layout.addView(lineimg);
							
							linelist.add(lineimg);
							viewlist.add(view);
							
							db.udateMyShoopingCartShelf(uuid,"1");
						}
						else
						{
							ImageView pimg = (ImageView)view.findViewById(R.id.imgProductImage);
							pimg.setImageBitmap(img);
							
							TextView pidtxt = (TextView)view.findViewById(R.id.productid);
							pidtxt.setText(pId);
							
							TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
							uuidtxt.setText(uuid);
							
							TextView nametxt = (TextView)view.findViewById(R.id.textProductName);
							nametxt.setText(cname);
							
							if(typesMapping.equals("10"))
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_138)+color);
								
								TextView textdients = (TextView)view.findViewById(R.id.textdients);
								textdients.setVisibility(View.VISIBLE);
								textdients.setText(ProductShoppingCartActivity.this.getString(R.string.produce_with4)+": "+dishestr);
							}
							else
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(ProductShoppingCartActivity.this.getString(R.string.menu_lable_41)+": "+color);
							}
							
							TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
							textBuyCount.setText(ProductShoppingCartActivity.this.getString(R.string.mybill_list_lable_8)+number);
							
							EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
							editBuyCount.setText(number);
							
							TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
							textPrice.setText(df.format(Double.valueOf(price))+"￥");
							
							TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
							textTotalValue.setText(df.format(totalPrice)+"￥");
							
							layoutShelf.addView(view);
							
							ImageView lineimg = new ImageView(ProductShoppingCartActivity.this);
							lineimg.setBackgroundResource(R.drawable.main_line);
							lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
							layoutShelf.addView(lineimg);
							
							db.udateMyShoopingCartShelf(uuid,"0");
						}
					}
					
					textTotalBuyCount.setText(String.valueOf(totalNumber)+ProductShoppingCartActivity.this.getString(R.string.menu_lable_42));
					textTotalPoint.setText(String.valueOf((int)tprice)+ProductShoppingCartActivity.this.getString(R.string.menu_lable_43));
					textTotalPayMoney.setText("￥"+df.format(tprice));
					
					textTotalBuyCount2.setText(String.valueOf(totalNumber)+ProductShoppingCartActivity.this.getString(R.string.menu_lable_42));
					textTotalPoint2.setText(String.valueOf((int)tprice)+ProductShoppingCartActivity.this.getString(R.string.menu_lable_43));
					textTotalPayMoney2.setText("￥"+df.format(tprice));
				}
				
				if(shelfidlist != null && shelfidlist.size() > 0)
				{
					textOffSellInfo1.setVisibility(View.VISIBLE);
					textOffSellInfo2.setVisibility(View.VISIBLE);
					
				}
				else
				{
					textOffSellInfo1.setVisibility(View.GONE);
					textOffSellInfo2.setVisibility(View.GONE);
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
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
//		    opt.inSampleSize = 8;
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void showEditCart(boolean editcart)
	{
		try{
			DecimalFormat df = new DecimalFormat("######0.00");   
			final LinearLayout layout = (LinearLayout)findViewById(R.id.cart_item);
			if(editcart)
			{
				TextView textTotalBuyCount = (TextView)findViewById(R.id.textTotalBuyCount);
				TextView textTotalPoint = (TextView)findViewById(R.id.textTotalPoint);
				TextView textTotalPayMoney = (TextView)findViewById(R.id.textTotalPayMoney);
				
				TextView textTotalBuyCount2 = (TextView)findViewById(R.id.textTotalBuyCount2);
				TextView textTotalPoint2 = (TextView)findViewById(R.id.textTotalPoint2);
				TextView textTotalPayMoney2 = (TextView)findViewById(R.id.textTotalPayMoney2);
				
				int totalNumber = 0;
				double tprice = 0;
				for(int i=0;i<viewlist.size();i++)
				{
					View view = viewlist.get(i);
					EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
					editBuyCount.setVisibility(View.GONE);
					int nubmer = Integer.valueOf(editBuyCount.getText().toString());
					
					TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
					textBuyCount.setVisibility(View.VISIBLE);
					textBuyCount.setText(this.getString(R.string.mybill_list_lable_8)+nubmer);
					
					TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
					double price = Double.valueOf(textPrice.getText().toString().replaceAll("￥", ""));
					
					TextView textDelete = (TextView)view.findViewById(R.id.textDelete);
					textDelete.setVisibility(View.GONE);
					
					TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
					textTotalValue.setText(df.format(nubmer*price)+"￥");
					
					final TextView pidtxt = (TextView)view.findViewById(R.id.productid);
					
					TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
					final String uuid = uuidtxt.getText().toString();
					
					totalNumber = totalNumber + Integer.valueOf(nubmer);
					tprice = tprice + (nubmer*price);
					
					db.udateMyShoopingCart(uuid, String.valueOf(nubmer));
					
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showProductDetail(pidtxt.getText().toString());
						}
					});
				}
				
				if(totalNumber == 0)
				{
					scroll.setVisibility(View.GONE);
					edit_cart.setVisibility(View.GONE);
					real_btn.setVisibility(View.GONE);
					imgShopcar.setVisibility(View.VISIBLE);
					textShopcarIsNullInfo.setVisibility(View.VISIBLE);
				}
				else
				{
					textTotalBuyCount.setText(String.valueOf(totalNumber)+this.getString(R.string.menu_lable_42));
					textTotalPoint.setText(String.valueOf((int)tprice)+this.getString(R.string.menu_lable_43));
					textTotalPayMoney.setText("￥"+df.format(tprice));
					
					textTotalBuyCount2.setText(String.valueOf(totalNumber)+this.getString(R.string.menu_lable_42));
					textTotalPoint2.setText(String.valueOf((int)tprice)+this.getString(R.string.menu_lable_43));
					textTotalPayMoney2.setText("￥"+df.format(tprice));
				}
			}
			else
			{
				for(int i=0;i<viewlist.size();i++)
				{
					final View view = viewlist.get(i);
					final int index = i;
					view.setOnClickListener(null);
					TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
					textBuyCount.setVisibility(View.GONE);
					
					EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
					editBuyCount.setVisibility(View.VISIBLE);
					
					TextView textDelete = (TextView)view.findViewById(R.id.textDelete);
					textDelete.setVisibility(View.VISIBLE);
					
					TextView uuidtxt = (TextView)view.findViewById(R.id.uuid);
					final String uuid = uuidtxt.getText().toString();
					
					final ImageView line = linelist.get(i);
					textDelete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dettelCart(index,view,line,layout,uuid);
						}
					});
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductDetail(final String pid)
	{
		showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				int index = 0;
				try{
					List<Map<String,Object>> list = myapp.getProductCartList();
					if(list != null && list.size()>0)
					{
						for(int i=0;i<list.size();i++)
						{
							Map<String,Object> map = list.get(i);
							String pId = (String)map.get("pkid");
							if(pId.equals(pid))
							{
								index = i;
								break;
							}
						}
					}
//					else
//					{
//						JSONObject jobj = api.getProductsDetil(pid);
//						if(jobj != null)
//						{
//							JSONArray jArr = (JSONArray) jobj.get("data");
//							list = ProductMenuListActivity.getMenuList2(jArr);
//							myapp.setProductList(list);
//						}
//					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				msg.obj = index;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadProductDetail(final List<String> pids)
	{
		new Thread() {
			public void run() {
				try{
					for(int i=0;i<pids.size();i++)
					{
						List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
						JSONObject jobj = api.getProductsDetil(pids.get(i));
						if(jobj != null)
						{
							if(jobj.has("error"))
							{
								shelfidlist.add(pids.get(i));
							}
							else
							{
								JSONArray jArr = (JSONArray) jobj.get("data");
								list = ProductMenuListActivity.getMenuList2(jArr);
								prodoutList.addAll(list);
								myapp.setProductCartList(prodoutList);
							}
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}.start();
	}
	
	public boolean loadProductDetail(String pid)
	{
		boolean a = true;
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			JSONObject jobj = api.getProductsDetil(pid);
			if(jobj != null)
			{
				if(jobj.has("error"))
				{
					shelfidlist.add(pid);
					a = false;
				}
				else
				{
					JSONArray jArr = (JSONArray) jobj.get("data");
					list = ProductMenuListActivity.getMenuList2(jArr);
					prodoutList.addAll(list);
					myapp.setProductCartList(prodoutList);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return a;
	}
	
	public void dettelCart(final int index,final View view,final ImageView line,final LinearLayout layout,final String uuid)
	{
		try{
			new AlertDialog.Builder(this)
			.setMessage(this.getString(R.string.menu_lable_37))
			.setPositiveButton(this.getString(R.string.isture_info),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							
//							myapp.getMymenulist().remove(index);
							db.delMyShoopingCart(uuid);
							viewlist.remove(view);
							layout.removeView(view);
							linelist.remove(line);
							layout.removeView(line);
							showEditCart(false);
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
}
