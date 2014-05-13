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
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.product.MyMenuListView;
import ms.activitys.product.ProductComments;
import ms.activitys.product.ProductImageSwitcher;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperCard;
import ms.globalclass.dbhelp.DBHelperShoppingCart;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.PageControlIconView;

public class ProductDetail extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private double diasePice;
	private double diasePiceNumber = 0;
	private String prodcurColor = "";
	private String prodcurColorpirc;
	private List<Map<String,String>> sideDisheslist = new ArrayList<Map<String,String>>();
	private String prodcurTaste;
	private double oldtakesPice = 0;
	private CharSequence[] coloritems = {};
	private CharSequence[] tasteitems = {};
	private List<Map<String,Object>> colorlist;
	private List<Map<String,String>> tastelist;
	private String prodcurTastepirc;
	private String pkid;
	private ProgressDialog mypDialog;
	private Gallery imageGallery;
	private PageControlIconView pageControl;
	private ProgressBar pb;
	private Map<String,Map<String,Object>> colorMap = new HashMap<String,Map<String,Object>>();
	private String colorid;
	private List<TextView> sizelist = new ArrayList<TextView>();
	private String productSize = "";
	
	private int index;
	private String maptag = "";
    private String advertiseNotification;
    private DBHelperShoppingCart db;
    private String storeid;
    private TextView nulltxt;
    private boolean isDodo = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_details);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperShoppingCart(this,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		maptag = bunde.getString("map");
		advertiseNotification = bunde.getString("advertiseNotification");
		storeid = bunde.getString("storeid");
		
		int pindex = this.getIntent().getExtras().getInt("pindex");
		Map<String,Object> map = null;
		if(bunde.containsKey("cart"))
		{
			map = myapp.getProductCartList().get(pindex);
			pkid = (String)map.get("pkid");
			colorid = pkid;
		}
		if(bunde.containsKey("dodo"))
		{
			map = myapp.getDodoMap();
			pkid = (String)map.get("pkid");
			colorid = pkid;
			isDodo = true;
		}
		else
		{
			map = myapp.getProductList().get(pindex);
			pkid = (String)map.get("pkid");
			colorid = pkid;
		}
		
		pb = (ProgressBar)findViewById(R.id.probar);
		nulltxt = (TextView)findViewById(R.id.nulltxt);
		
		imageGallery = (Gallery)findViewById(R.id.widget34);
		loadThreadData();
		pageControl=(PageControlIconView) findViewById(R.id.pageControl);
		
		showMenuStratWoid(map,pindex);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ProductDetail.this.setResult(RESULT_OK, getIntent());
			ProductDetail.this.finish();
			return false;
		}
		return false;
	}

	public void showMenuStratWoid(final Map<String,Object> map,final int pindex)
	{
		try{
			String imageurl = (String)map.get("imgurl");
			String cname = (String)map.get("cname");
			String price = (String)map.get("prices");
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
			String psize = (String)map.get("psize");
			
			setProductSize(psize);
			
			TextView title = (TextView)findViewById(R.id.widget35);
			title.setText(cname);
			
			RatingBar ratingBar = (RatingBar) findViewById(R.id.peng_ji_img); 
			ratingBar.setRating(fiveimg);
		
	        //设置Gallery的事件监听  
			imageGallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					showProductImage(colorid);
				}
			}); 
			
			imageGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					pageControl.generatePageControl(arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
//			Bitmap bitmap = getImageBitmap(imageurl);
//			image.setImageBitmap(bitmap);
			
			ImageGetter imgGetter = new Html.ImageGetter() {
				public Drawable getDrawable(String source) {
					Drawable drawable = null;
					Log.d("Image Path", source);
					URL url;
					try {
						url = new URL(source);
						drawable = Drawable.createFromStream(url.openStream(),"");
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
					if(isDodo)
					{
						makeText("请先登录");
					}
					else
					{
						Map<String,Object> cartmap = new HashMap<String,Object>();
						String uuid = UUID.randomUUID().toString();
						cartmap.put("uuid", uuid);
						cartmap.put("imgurl", map.get("imgurl"));
						cartmap.put("cname", map.get("cname"));
						cartmap.put("pkid", map.get("pkid"));
						cartmap.put("storesid", storeid);
						cartmap.put("tastes", "");//口味
						cartmap.put("productCode", map.get("productsNo"));
						if(prodcurColor != null && !prodcurColor.equals(""))
						{
							cartmap.put("numberText", ProductDetail.this.getString(R.string.mybill_list_lable_8) + number.getText().toString());
							cartmap.put("number", number.getText().toString());
							cartmap.put("pcolor", prodcurColor);
							double nbr = Double.valueOf(number.getText().toString());
							double price = Double.valueOf(prodcurColorpirc);
							price = price + diasePiceNumber;
							double totalPrice = nbr * price;
							cartmap.put("totalPrice", totalPrice);
							cartmap.put("price", diasePice + " ￥");
							cartmap.put("prices", String.valueOf(diasePice));
							cartmap.put("price2", price + " ￥");
							cartmap.put("prices2", String.valueOf(price));
	//						map.put("prices", diasePice);
	//						map.put("oldprices", diasePice);
							if(sideDisheslist.size() > 0)
								cartmap.put("sideDisheslist", sideDisheslist);
							else
								cartmap.put("sideDisheslist", null);
							cartmap.put("prodcurTaste", prodcurTaste);
							cartmap.put("productSize", productSize);
							myapp.getMymenulist().add(cartmap);
						}
						else
						{
							cartmap.put("numberText", ProductDetail.this.getString(R.string.mybill_list_lable_8) + number.getText().toString());
							cartmap.put("number", number.getText().toString());
							cartmap.put("pcolor", prodcurColor);
							double nbr = Double.valueOf(number.getText().toString());
							double price = Double.valueOf((String)map.get("prices"));
							price = price + diasePiceNumber;
							double totalPrice = nbr * price;
							cartmap.put("totalPrice", totalPrice);
							cartmap.put("price", diasePice + " ￥");
							cartmap.put("prices", String.valueOf(diasePice));
							cartmap.put("price2", price + " ￥");
							cartmap.put("prices2", String.valueOf(price));
	//						map.put("prices", diasePice);
	//						map.put("oldprices", diasePice);
							if(sideDisheslist.size() > 0)
								cartmap.put("sideDisheslist", sideDisheslist);
							else
								cartmap.put("sideDisheslist", null);
							cartmap.put("prodcurTaste", prodcurTaste);
							cartmap.put("productSize", productSize);
							myapp.getMymenulist().add(cartmap);
						}
	//					Toast.makeText(ProductDetail.this,ProductDetail.this.getString(R.string.menu_lable_3), Toast.LENGTH_SHORT).show();
						
						prodcurColor = null;
						prodcurColorpirc = null;
						
						diasePiceNumber = 0;
						oldtakesPice = 0;
						
						db.saveMyShoopingCart(cartmap);
						
						if(sideDisheslist != null)
							sideDisheslist.clear();
						
						//加入购物车
						makeText(getString(R.string.menu_lable_3));
						
						showShoppingCart();
					}
				}
			});
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isDodo)
					{
						makeText("请先登录");
					}
					else
					{
						ProductDetail.this.setResult(RESULT_OK, getIntent());
						ProductDetail.this.finish();
					}
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
			
//			LinearLayout topContainer = (LinearLayout)findViewById(R.id.topContainer);
//			topContainer.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showProductImage(pkid);
//				}
//			});
			
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
	
	public void setProductSize(String psize)
	{
		try{
			if(!psize.equals(""))
			{
				LinearLayout layout = (LinearLayout)findViewById(R.id.size_layout);
				LinearLayout layout2 = (LinearLayout)findViewById(R.id.size_layout2);
				
				String [] strs = psize.split(",");
				for(int i=0;i<strs.length;i++)
				{
					String size = strs[i];
					View view = LayoutInflater.from(this).inflate(R.layout.product_size_text, null);
					TextView txt = (TextView)view.findViewById(R.id.txt);
					txt.setText(size);
					txt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							setSizeSelected(v);
						}
					});
					if(i==0)
					{
						txt.setTextColor(Color.parseColor("#DF7401"));
						txt.setBackgroundResource(R.drawable.product_size_style_sel);
						productSize = size;
					}
					if(i<4)
					{
						layout.addView(view);
					}
					else
					{
						layout2.addView(view);
					}
					sizelist.add(txt);
				}
			}
			else
			{
				ImageView line = (ImageView)findViewById(R.id.seperate_line1);
				line.setVisibility(View.GONE);
				LinearLayout sizelayout = (LinearLayout)findViewById(R.id.product_size_layout);
				sizelayout.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setSizeSelected(View v)
	{
		try{
			TextView txt = (TextView)v;
			for(int i=0;i<sizelist.size();i++)
			{
				TextView txtview = sizelist.get(i);
				txtview.setTextColor(Color.parseColor("#6E6E6E"));
				txtview.setBackgroundResource(R.drawable.product_size_style);
			}
			txt.setTextColor(Color.parseColor("#DF7401"));
			txt.setBackgroundResource(R.drawable.product_size_style_sel);
			productSize = txt.getText().toString();
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
			}
		});
		
		alertDialog = builder.create();
		alertDialog.show();
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
		    intent.setClass( this,ProductImageGrral.class);
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
			if(isDodo)
				bundle.putString("dodo", "");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
				String cid = (String)cmap.get("pkid");
				String originalPrice = (String)cmap.get("originalPrice");
				String colorprice = (String)cmap.get("colorprice");
				String colorImg = (String)cmap.get("colorImg");
				String defaultColor = (String)cmap.get("defaultColor");
				
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
				
				if(!colorImg.equals(""))
				{
					pb.setVisibility(View.VISIBLE);
					imageGallery.setVisibility(View.GONE);
					colorid = cid;
					loadThreadData2(colorid);
				}
				else
				{
					if(defaultColor.equals("0"))
					{
						pb.setVisibility(View.VISIBLE);
						imageGallery.setVisibility(View.GONE);
						colorid = pkid;
						loadThreadData2(pkid);
					}
				}
				
				prodcurColor = colorName;
				prodcurColorpirc = colorprice;
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
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
				
				String colorImg = ""; 
				if(dobj.has("colorImg"))
					colorImg = (String) dobj.get("colorImg"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("defaultColor", defaultColor); //0是是，1是否
				map.put("colorName", colorName);
				map.put("originalPrice", originalPrice);
				map.put("colorprice", colorprice);
				map.put("colorImg", colorImg);
				
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
	
	private static Bitmap createBitmap(String total, String number) 
	{

		// create the new blank bitmap
		double bai = Double.valueOf(total) / Double.valueOf(number);
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
		cv.restore();// 存储
		return newb;
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
					
					jobj = api.getProductImageList(pkid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getImageList(jArr);
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("list", list);
							map.put("cid", pkid);
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
	
	public void loadThreadData2(final String cid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					if(colorMap.containsKey(cid))
					{
						msg.obj = colorMap.get(cid);
					}
					else
					{
						jobj = api.getProductImageList(cid);
						if(jobj != null)
						{
							if(jobj.has("error"))
							{
								msg.obj = null;
							}
							else
							{
								JSONArray jArr = (JSONArray) jobj.get("data");
								list = getImageList(jArr);
								Map<String,Object> map = new HashMap<String,Object>();
								map.put("list", list);
								map.put("cid", cid);
								msg.obj = map;
							}
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
					Map<String,Object> map = (Map<String,Object>)msg.obj;
					List<Map<String,Object>> dlist2 = (List<Map<String,Object>>)map.get("list");
					String cid = (String)map.get("cid");
					pageControl.bindScrollViewGroup(dlist2.size());
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(ProductDetail.this, dlist2,// 数据源
							R.layout.item_menu,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "imageUrl" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.item_image },300,300,false,share,"zhong");
					
					// 添加并且显示
					if(listItemAdapter != null)
					{
						imageGallery.setAdapter(listItemAdapter);
						colorMap.put(cid, map);
					}
					pb.setVisibility(View.GONE);
					imageGallery.setVisibility(View.VISIBLE);
				}
				else
				{
					nulltxt.setVisibility(View.VISIBLE);
					pb.setVisibility(View.GONE);
				}
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
					imageUrl = (String) dobj.get("imageUrl");
				
				String typeid = ""; 
				if(dobj.has("typeid"))
					typeid = (String) dobj.get("typeid");
				
				String dataName = ""; 
				if(dobj.has("dataName"))
					dataName = (String) dobj.get("dataName");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("localeString", localeString);
				map.put("imageUrl", imageUrl);
				map.put("typeid", typeid);
				map.put("dataName", dataName);
				if(typeid.indexOf("image") >= 0)
				{
					map.put("img", getImageBitmap(imageUrl));
					imglist.add(map);
				}
			
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return imglist;
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
	
	public void showShoppingCart()
	{
		try{
			new AlertDialog.Builder(this).setTitle(this.getString(R.string.menu_lable_34))
			.setMessage(this.getString(R.string.menu_lable_3))
			.setPositiveButton(this.getString(R.string.menu_lable_35),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							
							Intent intent = new Intent();
							intent.setClass(ProductDetail.this, ProductShoppingCartActivity.class);
						    Bundle bundle = new Bundle();
						    bundle.putInt("index", index);
							bundle.putString("map", maptag);
							bundle.putString("advertiseNotification", advertiseNotification);
							bundle.putString("storeid", storeid);
							intent.putExtras(bundle);
						    startActivity(intent);//开始界面的跳转函数
						    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
						}
					}).setNegativeButton(this.getString(R.string.menu_lable_36),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 取消按钮事件
							ProductDetail.this.setResult(RESULT_OK, getIntent());
							ProductDetail.this.finish();
						}
					}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
