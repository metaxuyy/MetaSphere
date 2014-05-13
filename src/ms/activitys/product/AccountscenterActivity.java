package ms.activitys.product;

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
import org.json.JSONObject;

import ms.activitys.R;
import ms.globalclass.dbhelp.DBHelperShoppingCart;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterAddress;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountscenterActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private DBHelperShoppingCart db;
	
	private String storeid;
	private List<String> shelfidlist = new ArrayList<String>();
	private TextView textStockoutTip;
	private TextView textStockout;
	private TextView textAllPrice;
	private ProgressDialog mypDialog;
	private TextView addressid;
	private int successtag = 0;
	private Double totlprice = 0.00;
	private Double logisticsCosts = 0.00;
	private Double gifMonenyCosts = 0.00;
	private List<Map<String,Object>> orderitemlist;
	private List<Map<String,Object>> orderitemlist2;
	
	private int index;
	private String maptag = "";
    private String advertiseNotification;
    private String activitytag = "padding";
    private String typesMapping;
    private int cardpoints;
    private double discountRate = 0.00;
    private int discountIntegral = 0;
    private String integralType;
    private int paymentPoints = 0;
    private String businessId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountscenteractivity);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		db = new DBHelperShoppingCart(this,myapp);
		
		storeid = this.getIntent().getExtras().getString("storeid");
		index = this.getIntent().getExtras().getInt("index");
		maptag = this.getIntent().getExtras().getString("map");
		advertiseNotification = this.getIntent().getExtras().getString("advertiseNotification");
		
		List<Map<String,Object>> list = myapp.getMyCardsAll();
		Map<String,Object> cardmap = list.get(index);
		typesMapping = (String)cardmap.get("typesMapping");
		cardpoints = (Integer)cardmap.get("points");
		businessId = (String)cardmap.get("businessId");
		
		textStockoutTip = (TextView)findViewById(R.id.textStockoutTip);
		textStockout = (TextView)findViewById(R.id.textStockout);
		textAllPrice = (TextView)findViewById(R.id.textAllPrice);
		
		Button break_cart = (Button)findViewById(R.id.break_cart);
		break_cart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AccountscenterActivity.this.setResult(RESULT_OK, getIntent());
				AccountscenterActivity.this.finish();
			}
		});
		
		Button textSubmitDown = (Button)findViewById(R.id.textSubmitDown);
		textSubmitDown.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
				TextView textBalance = (TextView)findViewById(R.id.textBalance);
				if(addressid.getText() == null || addressid.getText().equals(""))
				{
					makeText(getString(R.string.menu_lable_8));
				}
				else if(textBalance.getText() == null || textBalance.getText().equals(""))
				{
					makeText(getString(R.string.menu_lable_149));
				}
				else if(textLogisticspiceHidd.getText() == null || textLogisticspiceHidd.getText().equals(""))
				{
					makeText(getString(R.string.menu_lable_9));
				}
				else
				{
					addMyOrder();
				}
			}
		});
		
		Button submit_btn = (Button)findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
				if(addressid.getText() == null || addressid.getText().equals(""))
				{
					makeText(getString(R.string.menu_lable_8));
				}
				else if(textLogisticspiceHidd.getText() == null || textLogisticspiceHidd.getText().equals(""))
				{
					makeText(getString(R.string.menu_lable_9));
				}
				else
				{
					addMyOrder();
				}
			}
		});
		
		addressid = (TextView)findViewById(R.id.addressid);
		
		showProductDetitl();
		showAddressView();
		showPlayPage();
		showSendTime();
		showInvoice();
		showComment();
		showLogistics();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(activitytag.equals("padding"))
			{
				AccountscenterActivity.this.setResult(RESULT_OK, getIntent());
				AccountscenterActivity.this.finish();
			}
			else
			{
				Intent intent = new Intent();
				intent.setClass(AccountscenterActivity.this, ProductTypeActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putInt("index", index);
				bundle.putString("map", maptag);
				bundle.putString("advertiseNotification", advertiseNotification);
				intent.putExtras(bundle);
			    startActivity(intent);//开始界面的跳转函数
			    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			    AccountscenterActivity.this.finish();//关闭显示的Activity
			}
			return false;
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode == RESULT_OK){
	    	if(data != null)
	    	{
	    		if(data.hasExtra("activitytype"))
	    		{
		    		String activitytype = data.getStringExtra("activitytype");
		    		if(activitytype.equals("address"))
		    		{
				    	String consignee = data.getStringExtra("consignee");
				    	String textCity = data.getStringExtra("textCity");
				    	String address = data.getStringExtra("address");
				    	String pkid = data.getStringExtra("pkid");
				    	if(pkid != null && !pkid.equals(""))
				    	{
					    	String str = consignee + "\n" + textCity + "\n" + address;
					    	
					    	TextView textAddress = (TextView)findViewById(R.id.textAddress);
					    	textAddress.setVisibility(View.GONE);
					    	
					    	TextView textAddressValue = (TextView)findViewById(R.id.textAddressValue);
					    	textAddressValue.setText(str);
					    	
	//				    	TextView addressid = (TextView)findViewById(R.id.addressid);
					    	addressid.setText(pkid);
					    	
					    	TextView textBalance = (TextView)findViewById(R.id.textBalance);
					    	if(textBalance.getText().toString() == null || textBalance.getText().toString().equals(""))
					    	{
						    	TextView textPay = (TextView)findViewById(R.id.textPay);
				    			textPay.setText(getString(R.string.menu_lable_86)+" : ");
				    			
				    			TextView textPayValue = (TextView)findViewById(R.id.textPayValue);
				    			textPayValue.setText(getString(R.string.menu_lable_87));
				    			
				    			textBalance.setText("1");
					    	}
					    	
					    	TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
					    	if(textSendTimeHiddle.getText().toString() == null || textSendTimeHiddle.getText().toString().equals(""))
					    	{
						    	TextView textSendTime = (TextView)findViewById(R.id.textSendTime);
				    			textSendTime.setText(getString(R.string.menu_lable_88));
				    			
				    			TextView textSendTimeValue = (TextView)findViewById(R.id.textSendTimeValue);
				    			textSendTimeValue.setText(getString(R.string.menu_lable_89));
				    			
				    			
				    			textSendTimeHiddle.setText("1");
					    	}
					    	
					    	RelativeLayout relOrderDetail = (RelativeLayout)findViewById(R.id.relOrderDetail);
					    	relOrderDetail.setVisibility(View.VISIBLE);
				    	}
				    	else
				    	{
				    		TextView textAddress = (TextView)findViewById(R.id.textAddress);
					    	textAddress.setVisibility(View.VISIBLE);
					    	
					    	TextView textAddressValue = (TextView)findViewById(R.id.textAddressValue);
					    	textAddressValue.setText("");
					    	
					    	addressid.setText("");
					    	
					    	TextView textPay = (TextView)findViewById(R.id.textPay);
			    			textPay.setText(getString(R.string.menu_lable_58));
			    			
			    			TextView textPayValue = (TextView)findViewById(R.id.textPayValue);
			    			textPayValue.setText("");
			    			
			    			TextView textBalance = (TextView)findViewById(R.id.textBalance);
			    			textBalance.setText("");
			    			
			    			TextView textSendTime = (TextView)findViewById(R.id.textSendTime);
			    			textSendTime.setText(getString(R.string.menu_lable_59));
			    			
			    			TextView textSendTimeValue = (TextView)findViewById(R.id.textSendTimeValue);
			    			textSendTimeValue.setText("");
			    			
			    			TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
			    			textSendTimeHiddle.setText("");
			    			
			    			TextView textInvoiceTitle = (TextView)findViewById(R.id.textInvoiceTitle);
			    			textInvoiceTitle.setText(getString(R.string.menu_lable_60));
			    			
			    			TextView textInvoiceType = (TextView)findViewById(R.id.textInvoiceType);
			    			textInvoiceType.setVisibility(View.GONE);
			    			textInvoiceType.setText("");
			    			
			    			TextView textInvoiceTitleValue = (TextView)findViewById(R.id.textInvoiceTitleValue);
			    			textInvoiceTitleValue.setText("");
			    			
			    			TextView textInvoiceTypeValue = (TextView)findViewById(R.id.textInvoiceTypeValue);
			    			textInvoiceTypeValue.setVisibility(View.GONE);
			    			textInvoiceTypeValue.setText("");
			    			
			    			TextView textInvoiceHiddle = (TextView)findViewById(R.id.textInvoiceHiddle);
			    			textInvoiceHiddle.setText("");
			    			
			    			TextView textComment = (TextView)findViewById(R.id.textComment);
			    			textComment.setText(getString(R.string.menu_lable_61));
			    			
			    			TextView textCommentValue = (TextView)findViewById(R.id.textCommentValue);
			    			textCommentValue.setText("");
			    			
			    			TextView textLogistics = (TextView)findViewById(R.id.textLogistics);
		    				textLogistics.setText(getString(R.string.menu_lable_9));
			    			
			    			TextView textLogisticsValue = (TextView)findViewById(R.id.textLogisticsValue);
			    			textLogisticsValue.setText("");
			    			
			    			TextView textLogisticsHidd = (TextView)findViewById(R.id.textLogisticsHidd);
			    			textLogisticsHidd.setText("");
			    			
			    			TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
			    			textLogisticspiceHidd.setText("");
			    			
			    			TextView textFreightValue = (TextView)findViewById(R.id.textFreightValue);
			    			textFreightValue.setText("0.0￥");
			    			
			    			totlprice = totlprice - logisticsCosts;
			    			logisticsCosts = 0.00;
			    			
			    			RelativeLayout relOrderDetail = (RelativeLayout)findViewById(R.id.relOrderDetail);
					    	relOrderDetail.setVisibility(View.GONE);
				    	}
		    		}
		    		else if(activitytype.equals("pay"))
		    		{
		    			String value = data.getStringExtra("value");
		    			String paytag = data.getStringExtra("paytag");
		    			if(value != null && !value.equals(""))
		    			{
			    			TextView textPay = (TextView)findViewById(R.id.textPay);
			    			textPay.setText(getString(R.string.menu_lable_86)+" : ");
			    			
			    			TextView textPayValue = (TextView)findViewById(R.id.textPayValue);
			    			textPayValue.setText(value);
			    			
			    			TextView textBalance = (TextView)findViewById(R.id.textBalance);
			    			textBalance.setText(paytag);
		    			}
		    		}
		    		else if(activitytype.equals("sendtime"))
		    		{
		    			String value = data.getStringExtra("value");
		    			String sendtimetag = data.getStringExtra("sendtimetag");
		    			if(value != null && !value.equals(""))
		    			{
			    			TextView textSendTime = (TextView)findViewById(R.id.textSendTime);
			    			textSendTime.setText(getString(R.string.menu_lable_88));
			    			
			    			TextView textSendTimeValue = (TextView)findViewById(R.id.textSendTimeValue);
			    			textSendTimeValue.setText(value);
			    			
			    			TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
			    			textSendTimeHiddle.setText(sendtimetag);
		    			}
		    		}
		    		else if(activitytype.equals("invoice"))
		    		{
		    			String ititle = data.getStringExtra("ititle");
		    			String icontent = data.getStringExtra("icontent");
		    			String invoiceindex = data.getStringExtra("invoiceindex");
		    			
		    			if(ititle != null && !ititle.equals(""))
		    			{
			    			TextView textInvoiceTitle = (TextView)findViewById(R.id.textInvoiceTitle);
			    			textInvoiceTitle.setText(getString(R.string.menu_lable_90));
			    			
			    			TextView textInvoiceType = (TextView)findViewById(R.id.textInvoiceType);
			    			textInvoiceType.setVisibility(View.VISIBLE);
			    			textInvoiceType.setText(getString(R.string.menu_lable_91));
			    			
			    			TextView textInvoiceTitleValue = (TextView)findViewById(R.id.textInvoiceTitleValue);
			    			textInvoiceTitleValue.setText(ititle);
			    			
			    			TextView textInvoiceTypeValue = (TextView)findViewById(R.id.textInvoiceTypeValue);
			    			textInvoiceTypeValue.setVisibility(View.VISIBLE);
			    			textInvoiceTypeValue.setText(icontent);
			    			
			    			TextView textInvoiceHiddle = (TextView)findViewById(R.id.textInvoiceHiddle);
			    			textInvoiceHiddle.setText(invoiceindex);
		    			}
		    			else
		    			{
		    				TextView textInvoiceTitle = (TextView)findViewById(R.id.textInvoiceTitle);
			    			textInvoiceTitle.setText(getString(R.string.menu_lable_60));
			    			
			    			TextView textInvoiceType = (TextView)findViewById(R.id.textInvoiceType);
			    			textInvoiceType.setVisibility(View.GONE);
			    			textInvoiceType.setText("");
			    			
			    			TextView textInvoiceTitleValue = (TextView)findViewById(R.id.textInvoiceTitleValue);
			    			textInvoiceTitleValue.setText("");
			    			
			    			TextView textInvoiceTypeValue = (TextView)findViewById(R.id.textInvoiceTypeValue);
			    			textInvoiceTypeValue.setVisibility(View.GONE);
			    			textInvoiceTypeValue.setText("");
			    			
			    			TextView textInvoiceHiddle = (TextView)findViewById(R.id.textInvoiceHiddle);
			    			textInvoiceHiddle.setText("");
		    			}
		    		}
		    		else if(activitytype.equals("comment"))
		    		{
		    			String content = data.getStringExtra("content");
		    			
		    			if(content != null && !content.equals(""))
		    			{
			    			TextView textComment = (TextView)findViewById(R.id.textComment);
			    			textComment.setText(getString(R.string.hotel_reservation_lable_20)+" : ");
			    			
			    			TextView textCommentValue = (TextView)findViewById(R.id.textCommentValue);
			    			textCommentValue.setText(content);
		    			}
		    			else
		    			{
		    				TextView textComment = (TextView)findViewById(R.id.textComment);
			    			textComment.setText(getString(R.string.menu_lable_61));
			    			
			    			TextView textCommentValue = (TextView)findViewById(R.id.textCommentValue);
			    			textCommentValue.setText(content);
		    			}
		    		}
		    		else if(activitytype.equals("logistics"))
		    		{
		    			String textName = data.getStringExtra("textName");
		    			String pkid = data.getStringExtra("pkid");
		    			String lpice = data.getStringExtra("lpice");
		    			if(pkid != null && !pkid.equals(""))
		    			{
			    			TextView textLogistics = (TextView)findViewById(R.id.textLogistics);
			    			textLogistics.setText(getString(R.string.logistics_company));
			    			
			    			TextView textLogisticsValue = (TextView)findViewById(R.id.textLogisticsValue);
			    			textLogisticsValue.setText(textName);
			    			
			    			TextView textLogisticsHidd = (TextView)findViewById(R.id.textLogisticsHidd);
			    			textLogisticsHidd.setText(pkid);
			    			
			    			TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
			    			textLogisticspiceHidd.setText(lpice);
			    			
			    			TextView textFreightValue = (TextView)findViewById(R.id.textFreightValue);
			    			textFreightValue.setText(lpice+"￥");
			    			
			    			totlprice = totlprice - logisticsCosts + Double.valueOf(lpice);
			    			logisticsCosts = Double.valueOf(lpice);
			    			TextView textPayPriceValue = (TextView)findViewById(R.id.textPayPriceValue);
							textPayPriceValue.setText(totlprice+"￥");
		    			}
		    			else
		    			{
		    				TextView textLogistics = (TextView)findViewById(R.id.textLogistics);
		    				textLogistics.setText(getString(R.string.menu_lable_9));
			    			
			    			TextView textLogisticsValue = (TextView)findViewById(R.id.textLogisticsValue);
			    			textLogisticsValue.setText("");
			    			
			    			TextView textLogisticsHidd = (TextView)findViewById(R.id.textLogisticsHidd);
			    			textLogisticsHidd.setText("");
			    			
			    			TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
			    			textLogisticspiceHidd.setText("");
			    			
			    			TextView textFreightValue = (TextView)findViewById(R.id.textFreightValue);
			    			textFreightValue.setText("0.0￥");
			    			
			    			totlprice = totlprice - logisticsCosts;
			    			logisticsCosts = 0.00;
		    			}
		    		}
		    		else if(activitytype.equals("redeemPoints"))
		    		{
	    				DecimalFormat df = new DecimalFormat("######0.00");
		    			String points = data.getStringExtra("points");
		    			paymentPoints = Integer.valueOf(points);
		    			String ratenum = data.getStringExtra("ratenum");
		    			if(points != null && !points.equals(""))
		    			{
			    			TextView textGiftcardId = (TextView)findViewById(R.id.textGiftcardId);
			    			textGiftcardId.setText(getString(R.string.menu_lable_144)+" : ");
			    			
			    			TextView textGiftcardIdValue = (TextView)findViewById(R.id.textGiftcardIdValue);
			    			textGiftcardIdValue.setVisibility(View.VISIBLE);
			    			textGiftcardIdValue.setText(points);
			    			
			    			double gifmoney = Integer.valueOf(points)*Double.valueOf(ratenum);
			    			TextView textGiftcardMoney = (TextView)findViewById(R.id.textGiftcardMoney);
			    			textGiftcardMoney.setText(df.format(gifmoney));
			    			
			    			TextView textGiftcardPayValue = (TextView)findViewById(R.id.textGiftcardPayValue);
			    			textGiftcardPayValue.setText(df.format(gifmoney)+"￥");
			    			
			    			totlprice = totlprice + gifMonenyCosts - gifmoney;
			    			gifMonenyCosts = gifmoney;
			    			TextView textPayPriceValue = (TextView)findViewById(R.id.textPayPriceValue);
							textPayPriceValue.setText(totlprice+"￥");
		    			}
		    			else
		    			{
		    				TextView textGiftcardId = (TextView)findViewById(R.id.textGiftcardId);
			    			textGiftcardId.setText(getString(R.string.menu_lable_62)+" : ");
			    			
			    			TextView textGiftcardIdValue = (TextView)findViewById(R.id.textGiftcardIdValue);
			    			textGiftcardIdValue.setVisibility(View.GONE);
			    			textGiftcardIdValue.setText("0");
			    			
			    			TextView textGiftcardMoney = (TextView)findViewById(R.id.textGiftcardMoney);
			    			textGiftcardMoney.setText("");
			    			
			    			TextView textGiftcardPayValue = (TextView)findViewById(R.id.textGiftcardPayValue);
			    			textGiftcardPayValue.setText("0.0￥");
			    			
			    			totlprice = totlprice + gifMonenyCosts;
			    			gifMonenyCosts = 0.00;
			    			TextView textPayPriceValue = (TextView)findViewById(R.id.textPayPriceValue);
							textPayPriceValue.setText(totlprice+"￥");
		    			}
		    		}
		    	}
	    	}
		}
	       
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void showAddressView()
	{
		try{
			RelativeLayout relAddress = (RelativeLayout)findViewById(R.id.relAddress);
			relAddress.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(AccountscenterActivity.this, ReceivingAddressActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putString("storeid", storeid);
					intent.putExtras(bundle);
//				    startActivity(intent);//开始界面的跳转函数
					startActivityForResult(intent, 1);  
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				}
			});
			
			loadDefultAddress();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showPlayPage()
	{
		try{
			RelativeLayout relPay = (RelativeLayout)findViewById(R.id.relPay);
			relPay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String aid = addressid.getText().toString();
					if(aid != null && !aid.equals(""))
					{
						TextView textBalance = (TextView)findViewById(R.id.textBalance);
						Intent intent = new Intent();
						intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("activitytype", "pay");
						bundle.putString("storeid",storeid);
						bundle.putString("paytag", textBalance.getText().toString());
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
					}
					else
					{
						makeText(getString(R.string.menu_lable_92));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showSendTime()
	{
		try{
			RelativeLayout relSendTime = (RelativeLayout)findViewById(R.id.relSendTime);
			relSendTime.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String aid = addressid.getText().toString();
					if(aid != null && !aid.equals(""))
					{
						TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
						Intent intent = new Intent();
						intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("activitytype", "sendtime");
						bundle.putString("sendtimetag", textSendTimeHiddle.getText().toString());
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
					}
					else
					{
						makeText(getString(R.string.menu_lable_92));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showInvoice()
	{
		try{
			RelativeLayout relInvoice = (RelativeLayout)findViewById(R.id.relInvoice);
			relInvoice.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String aid = addressid.getText().toString();
					if(aid != null && !aid.equals(""))
					{
						TextView textInvoiceTitleValue = (TextView)findViewById(R.id.textInvoiceTitleValue);
						TextView textInvoiceHiddle = (TextView)findViewById(R.id.textInvoiceHiddle);
						Intent intent = new Intent();
						intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("activitytype", "invoice");
						bundle.putString("title", textInvoiceTitleValue.getText().toString());
						bundle.putString("contentindex", textInvoiceHiddle.getText().toString());
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
					}
					else
					{
						makeText(getString(R.string.menu_lable_92));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showComment()
	{
		try{
			RelativeLayout relComment = (RelativeLayout)findViewById(R.id.relComment);
			relComment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String aid = addressid.getText().toString();
					if(aid != null && !aid.equals(""))
					{
						TextView textCommentValue = (TextView)findViewById(R.id.textCommentValue);
						Intent intent = new Intent();
						intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("activitytype", "comment");
						bundle.putString("content", textCommentValue.getText().toString());
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
					}
					else
					{
						makeText(getString(R.string.menu_lable_92));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showRedeemPoints(final int points,final double ratenum)
	{
		try{
			RelativeLayout relGiftcard = (RelativeLayout)findViewById(R.id.relGiftcard);
			if(points > 0)
			{
				relGiftcard.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String aid = addressid.getText().toString();
						if(aid != null && !aid.equals(""))
						{
							TextView textGiftcardIdValue = (TextView)findViewById(R.id.textGiftcardIdValue);
							Intent intent = new Intent();
							intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
						    Bundle bundle = new Bundle();
							bundle.putString("activitytype", "redeemPoints");
							bundle.putInt("cpoints", Integer.valueOf(textGiftcardIdValue.getText().toString()));
							bundle.putString("storeid",storeid);
							bundle.putInt("points", points);
							bundle.putInt("cardpoints", cardpoints);
							bundle.putDouble("ratenum", ratenum);
							intent.putExtras(bundle);
							startActivityForResult(intent, 1);  
						    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
						}
						else
						{
							makeText(getString(R.string.menu_lable_92));
						}
					}
				});
			}
			else
			{
				relGiftcard.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showLogistics()
	{
		try{
			RelativeLayout relLogistics = (RelativeLayout)findViewById(R.id.relLogistics);
			relLogistics.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String aid = addressid.getText().toString();
					if(aid != null && !aid.equals(""))
					{
						TextView textLogisticsHidd = (TextView)findViewById(R.id.textLogisticsHidd);
						Intent intent = new Intent();
						intent.setClass(AccountscenterActivity.this, SettlementActivity.class);
					    Bundle bundle = new Bundle();
						bundle.putString("activitytype", "logistics");
						bundle.putString("logid", textLogisticsHidd.getText().toString());
						bundle.putString("storeid",storeid);
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);  
					    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
					}
					else
					{
						makeText(getString(R.string.menu_lable_92));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductDetitl()
	{
		DecimalFormat df = new DecimalFormat("######0.00");
		try{
			List<Map<String,Object>> dlist = db.loadMyShoopingCart(storeid);
			if(dlist != null && dlist.size() > 0)
			{
				showProductCart2(dlist);
			}
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
				msg.what = 1;
				
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
//				else
//				{
//					JSONArray jArr = (JSONArray) jobj.get("data");
//					list = ProductMenuListActivity.getMenuList2(jArr);
//				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return a;
	}
	
	public void loadDefultAddress()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				try{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					JSONObject jobj = api.loadAddressDefult();
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getAddressList(jArr);
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
	
	public void loadMypoints()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				try{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					JSONObject jobj = api.getMyPoints(storeid,String.valueOf(cardpoints),String.valueOf(totlprice));
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							int points = (Integer) jobj.get("points");//最大可兑换积分
							double ratenum = (Double) jobj.get("ratenum");//积分换钱的倍率
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("points", points);
							map.put("ratenum", ratenum);
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
	
	public void loadMyOrderpoints()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 4;
				try{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String cdate = sf.format(new Date());
					JSONObject jobj = api.getMyOrderPoints(storeid,String.valueOf(totlprice),cdate);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							String ptype = (String) jobj.get("event_types");//对换类型
							int totalScores = (Integer) jobj.get("totalScores");//对换积分
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("ptype", ptype);
							map.put("totalScores", totalScores);
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
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					Map<String,Object> map = dlist.get(0);
					String consignee = (String)map.get("consignee");
			    	String textCity = (String)map.get("textCity");
			    	String address = (String)map.get("address");
			    	String pkid = (String)map.get("pkid");
			    	if(pkid != null && !pkid.equals(""))
			    	{
				    	String str = consignee + "\n" + textCity + "\n" + address;
				    	
				    	TextView textAddress = (TextView)findViewById(R.id.textAddress);
				    	textAddress.setVisibility(View.GONE);
				    	
				    	TextView textAddressValue = (TextView)findViewById(R.id.textAddressValue);
				    	textAddressValue.setText(str);
				    	
				    	TextView addressid = (TextView)findViewById(R.id.addressid);
				    	addressid.setText(pkid);
				    	
				    	TextView textBalance = (TextView)findViewById(R.id.textBalance);
//				    	if(textBalance.getText().toString() == null || textBalance.getText().toString().equals(""))
//				    	{
//					    	TextView textPay = (TextView)findViewById(R.id.textPay);
//			    			textPay.setText(getString(R.string.menu_lable_86)+" : ");
//			    			
//			    			TextView textPayValue = (TextView)findViewById(R.id.textPayValue);
//			    			textPayValue.setText(getString(R.string.menu_lable_87));
//			    			
//			    			textBalance.setText("1");
//				    	}
				    	
				    	TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
				    	if(textSendTimeHiddle.getText().toString() == null || textSendTimeHiddle.getText().toString().equals(""))
				    	{
					    	TextView textSendTime = (TextView)findViewById(R.id.textSendTime);
			    			textSendTime.setText(getString(R.string.menu_lable_88));
			    			
			    			TextView textSendTimeValue = (TextView)findViewById(R.id.textSendTimeValue);
			    			textSendTimeValue.setText(getString(R.string.menu_lable_89));
			    			
			    			
			    			textSendTimeHiddle.setText("1");
				    	}
				    	
				    	RelativeLayout relOrderDetail = (RelativeLayout)findViewById(R.id.relOrderDetail);
				    	relOrderDetail.setVisibility(View.VISIBLE);
			    	}
			    	myapp.getAddressmap().put(pkid,map);
				}
				if(successtag < 3)
					successtag = successtag + 1;
				else
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 1:
				List<Map<String,Object>> mlist = (List<Map<String,Object>>)msg.obj;
				
				if(mlist.size() > 0)
				{
					DecimalFormat df = new DecimalFormat("######0.00");
					int totalNumber = 0;
					double tprice = 0;
					LinearLayout layout = (LinearLayout)findViewById(R.id.cart_item);
					LinearLayout layoutShelf = (LinearLayout)findViewById(R.id.shelf_item);
					orderitemlist = new ArrayList<Map<String,Object>>();
					orderitemlist2 = new ArrayList<Map<String,Object>>();
					for(int i=0;i<mlist.size();i++)
					{
						View view = LayoutInflater.from(AccountscenterActivity.this).inflate(R.layout.shopcaritem2, null);
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
						String pkid = (String)nmap.get("pkid");
						String prodcurTaste = (String)nmap.get("tastes");
						String productSize = (String)nmap.get("productSize");
						String productCode = (String)nmap.get("productCode");
						String dishestr = (String)nmap.get("dishestr");
						List<Map<String,String>> sideDisheslist = (List<Map<String,String>>)nmap.get("sideDisheslist");
						
						Map<String,Object> nmaps = new HashMap<String,Object>();
						nmaps.put("prices",price);
						nmaps.put("imgurl",imgurl);
						nmaps.put("pkid",pkid);
						nmaps.put("cname",cname);
						nmaps.put("number",number);
						nmaps.put("totalPrice",totalPrice);
						if(color != null && !color.equals(""))
							nmaps.put("color",color);
						if(prodcurTaste != null && !prodcurTaste.equals(""))
							nmaps.put("tastes",prodcurTaste);
						if(productSize != null && !productSize.equals(""))
							nmaps.put("productSize",productSize);
						if(productCode != null && !productCode.equals(""))
							nmaps.put("productCode",productCode);
						if(sideDisheslist != null)
							nmaps.put("sideDisheslist",sideDisheslist);
							
						if(isShelf)
						{
							orderitemlist2.add(nmaps);
							orderitemlist.add(nmap);
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
								textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_138)+color);
								
								TextView textdients = (TextView)view.findViewById(R.id.textdients);
								textdients.setVisibility(View.VISIBLE);
								textdients.setText(AccountscenterActivity.this.getString(R.string.produce_with4)+": "+dishestr);
							}
							else
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_41)+": "+color);
							}
							
							TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
							textBuyCount.setText(AccountscenterActivity.this.getString(R.string.mybill_list_lable_8)+number);
							
							EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
							editBuyCount.setText(number);
							
							TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
							textPrice.setText(df.format(Double.valueOf(price))+"￥");
							
							TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
							textTotalValue.setText(df.format(Double.valueOf(price)*Integer.valueOf(number))+"￥");
							
							layout.addView(view);
							
							ImageView lineimg = new ImageView(AccountscenterActivity.this);
							lineimg.setBackgroundResource(R.drawable.main_line);
							lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
							layout.addView(lineimg);
							
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
								textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_138)+color);
								
								TextView textdients = (TextView)view.findViewById(R.id.textdients);
								textdients.setVisibility(View.VISIBLE);
								textdients.setText(AccountscenterActivity.this.getString(R.string.produce_with4)+": "+dishestr);
							}
							else
							{
								TextView textSize = (TextView)view.findViewById(R.id.textSize);
								textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
								
								TextView textColor = (TextView)view.findViewById(R.id.textColor);
								textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_41)+": "+color);
							}
							
							TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
							textBuyCount.setText(AccountscenterActivity.this.getString(R.string.mybill_list_lable_8)+number);
							
							EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
							editBuyCount.setText(number);
							
							TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
							textPrice.setText(df.format(Double.valueOf(price))+"￥");
							
							TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
							textTotalValue.setText(df.format(totalPrice)+"￥");
							
							layoutShelf.addView(view);
							
							if(i < shelfidlist.size())
							{
								ImageView lineimg = new ImageView(AccountscenterActivity.this);
								lineimg.setBackgroundResource(R.drawable.main_line);
								lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
								layoutShelf.addView(lineimg);
							}
							
							db.udateMyShoopingCartShelf(uuid,"0");
						}
					}
					
					totlprice = tprice;
					
					TextView textCountValue = (TextView)findViewById(R.id.textCountValue);
					textCountValue.setText(totalNumber+getString(R.string.menu_lable_42));
					
					TextView textPointValue = (TextView)findViewById(R.id.textPointValue);
					textPointValue.setText((int)tprice+getString(R.string.menu_lable_43));
					
					TextView textPriceValue = (TextView)findViewById(R.id.textPriceValue);
					textPriceValue.setText(tprice+"￥");
					
					TextView textFreightValue = (TextView)findViewById(R.id.textFreightValue);
					textFreightValue.setText("0.0￥");
					
					TextView textGiftcardPayValue = (TextView)findViewById(R.id.textGiftcardPayValue);
					textGiftcardPayValue.setText("0.0￥");
					
					TextView textPayPriceValue = (TextView)findViewById(R.id.textPayPriceValue);
					textPayPriceValue.setText(tprice+"￥");
					
					textAllPrice.setText(getString(R.string.menu_lable_56)+" ：￥"+tprice);
				}
				
				loadMypoints();
				
				if(shelfidlist != null && shelfidlist.size() > 0)
				{
					textStockoutTip.setVisibility(View.VISIBLE);
					textStockout.setVisibility(View.VISIBLE);
				}
				else
				{
					textStockoutTip.setVisibility(View.GONE);
					textStockout.setVisibility(View.GONE);
				}
				if(successtag < 3)
					successtag = successtag + 1;
				else
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 2:
				Map<String,String> map = (Map<String,String>)msg.obj;
				String tag = map.get("tag");
				String orderNo = map.get("orderNo");
				String delivery = map.get("delivery");
				String payments = map.get("payments");
				String payment = map.get("payment");
				if(tag.equals("error"))
					makeText(AccountscenterActivity.this.getString(R.string.menu_lable_14));
				else
				{
					if(paymentPoints > 0)//减去本次订单所用积分
					{
						Map<String,Object> maps = myapp.getMyCardsAll().get(index);
						int points = (Integer)maps.get("points");
						maps.put("points",points - paymentPoints);
					}
					loadAccountsceOK(orderNo,delivery,payments,payment);
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			case 3:
				if(msg.obj != null)
				{
					Map<String,Object> pmap = (Map<String,Object>)msg.obj;
					int points = (Integer)pmap.get("points");
					double ratenum = (Double)pmap.get("ratenum");
//					discountRate = ratenum;
//					paymentPoints = points;
					showRedeemPoints(points,ratenum);
				}
				else
				{
					showRedeemPoints(0,0);
				}
				loadMyOrderpoints();
				if(successtag < 3)
					successtag = successtag + 1;
				else
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 4:
				if(msg.obj != null)
				{
					Map<String,Object> pmap = (Map<String,Object>)msg.obj;
					String ptype = (String)pmap.get("ptype");
					int totalScores = (Integer)pmap.get("totalScores");
					integralType = ptype;
					discountIntegral = totalScores; 
					TextView textPoint = (TextView)findViewById(R.id.textPoint);
					if(ptype.equals("1"))
					{
						textPoint.setText(AccountscenterActivity.this.getString(R.string.menu_lable_66));
					}
					else if(ptype.equals("2"))
					{
						textPoint.setText(AccountscenterActivity.this.getString(R.string.menu_lable_145));
					}
					else if(ptype.equals("3"))
					{
						textPoint.setText(AccountscenterActivity.this.getString(R.string.menu_lable_146));
					}
					TextView textPointValue = (TextView)findViewById(R.id.textPointValue);
					textPointValue.setText(String.valueOf(totalScores)+getString(R.string.menu_lable_43));
				}
				if(successtag < 3)
					successtag = successtag + 1;
				else
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void loadAccountsceOK(String orderNo,String delivery,String payments,String payment)
	{
		try{
			activitytag = "success";
			setContentView(R.layout.accountscenterokactivity);
			
			ProductShoppingCartActivity.instance.finish();
			db.delMyShoopingCartAll(storeid);
			
			TextView textZhiFuBao = (TextView)findViewById(R.id.textZhiFuBao);
			if(payment.equals("3"))
			{
				textZhiFuBao.setVisibility(View.VISIBLE);
				textZhiFuBao.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			else
			{
				textZhiFuBao.setVisibility(View.GONE);
			}
			
			TextView textShopping = (TextView)findViewById(R.id.textShopping);
			textShopping.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(AccountscenterActivity.this, ProductTypeActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putInt("index", index);
					bundle.putString("map", maptag);
					bundle.putString("advertiseNotification", advertiseNotification);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
				    AccountscenterActivity.this.finish();//关闭显示的Activity
				}
			});
			
			Button break_cart = (Button)findViewById(R.id.break_cart);
			break_cart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(AccountscenterActivity.this, ProductTypeActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putInt("index", index);
					bundle.putString("map", maptag);
					bundle.putString("advertiseNotification", advertiseNotification);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
				    AccountscenterActivity.this.finish();//关闭显示的Activity
				}
			});
			
			if(orderitemlist.size() > 0)
			{
				DecimalFormat df = new DecimalFormat("######0.00");
				int totalNumber = 0;
				double tprice = 0;
				LinearLayout layout = (LinearLayout)findViewById(R.id.cart_item);
				for(int i=0;i<orderitemlist.size();i++)
				{
					View view = LayoutInflater.from(AccountscenterActivity.this).inflate(R.layout.shopcaritem2, null);
					Map<String,Object> nmap = orderitemlist.get(i);
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
					String dishestr = (String)nmap.get("dishestr");
					
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
						textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
						
						TextView textColor = (TextView)view.findViewById(R.id.textColor);
						textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_138)+color);
						
						TextView textdients = (TextView)view.findViewById(R.id.textdients);
						textdients.setVisibility(View.VISIBLE);
						textdients.setText(AccountscenterActivity.this.getString(R.string.produce_with4)+": "+dishestr);
					}
					else
					{
						TextView textSize = (TextView)view.findViewById(R.id.textSize);
						textSize.setText(AccountscenterActivity.this.getString(R.string.menu_lable_30)+size);
						
						TextView textColor = (TextView)view.findViewById(R.id.textColor);
						textColor.setText(AccountscenterActivity.this.getString(R.string.menu_lable_41)+": "+color);
					}
					
					TextView textBuyCount = (TextView)view.findViewById(R.id.textBuyCount);
					textBuyCount.setText(AccountscenterActivity.this.getString(R.string.mybill_list_lable_8)+number);
					
					EditText editBuyCount = (EditText)view.findViewById(R.id.editBuyCount);
					editBuyCount.setText(number);
					
					TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
					textPrice.setText(df.format(Double.valueOf(price))+"￥");
					
					TextView textTotalValue = (TextView)view.findViewById(R.id.textTotalValue);
					textTotalValue.setText(df.format(Double.valueOf(price)*Integer.valueOf(number))+"￥");
					
					layout.addView(view);
					
					ImageView lineimg = new ImageView(AccountscenterActivity.this);
					lineimg.setBackgroundResource(R.drawable.main_line);
					lineimg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,2));
					layout.addView(lineimg);
					
				}
				
				TextView textOrderId = (TextView)findViewById(R.id.textOrderId);
				textOrderId.setText(orderNo);
				
				TextView textWay = (TextView)findViewById(R.id.textWay);
				textWay.setText(delivery);
				
				TextView textMoney = (TextView)findViewById(R.id.textMoney);
				textMoney.setText(payments+"￥");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void addMyOrder()
	{
		showProgressDialog();
		try{
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("data", orderitemlist2);
			
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
			
			String orderOn = "NO"+YY + MM +DD+HH+mm+SS+MI+tt;//订单编号
			
			String aid = addressid.getText().toString();
			Map<String,Object> addmap = myapp.getAddressmap().get(aid);
			String receiName = (String)addmap.get("consignee"); //收货人名字
			String phoneNumber = (String)addmap.get("phoneNumber");
			String telephone = (String)addmap.get("telephone");
			String phone = "";//收货人电话
			if(!phoneNumber.equals(""))
				phone = phoneNumber;
			else
				phone = telephone;
			String address = (String)addmap.get("address2");//送货地址
			String totalPrice = String.valueOf(totlprice);//商品总价
			TextView textCommentValue = (TextView)findViewById(R.id.textCommentValue);
			String desc = textCommentValue.getText().toString();//送货要求
			TextView textLogisticsHidd = (TextView)findViewById(R.id.textLogisticsHidd);
			String logid = textLogisticsHidd.getText().toString();//物流ID
			
			TextView textBalance = (TextView)findViewById(R.id.textBalance);
			String payment = textBalance.getText().toString();//支付方式
			TextView textSendTimeHiddle = (TextView)findViewById(R.id.textSendTimeHiddle);
			String sendtime = textSendTimeHiddle.getText().toString();//送货时间
			String zipcode = (String)addmap.get("zipcode");//邮政编码
			TextView textInvoiceTitleValue = (TextView)findViewById(R.id.textInvoiceTitleValue);
			String invoiceTitle = textInvoiceTitleValue.getText().toString();//发票抬头
			TextView textInvoiceTypeValue = (TextView)findViewById(R.id.textInvoiceTypeValue);
			String invoiceType = textInvoiceTypeValue.getText().toString();//发票类型
			TextView textLogisticspiceHidd = (TextView)findViewById(R.id.textLogisticspiceHidd);
			String freight = textLogisticspiceHidd.getText().toString();//运费金额
			String preferential = String.valueOf(gifMonenyCosts);//优惠金额
			String integral = String.valueOf(discountIntegral);//支付积分本次订单所所获积分
			String paymentPointsValue = String.valueOf(paymentPoints);//本次订单所用掉的积分
			
			map.put("orderNo", orderOn);
			map.put("storeid", storeid);
			map.put("receiName", receiName);
			map.put("phone", phone);
//			map.put("address", address);
			map.put("totalPrice", totalPrice);
//			map.put("desc", desc);
			map.put("logid", logid);
//			map.put("payment", payment);
//			map.put("sendtime", sendtime);
			map.put("zipcode", zipcode);
//			map.put("invoiceTitle", invoiceTitle);
//			map.put("invoiceType", invoiceType);
			map.put("freight", freight);
			map.put("preferential", preferential);
			map.put("integral", integral);
			map.put("orderPointsType", integralType);//本次订单获得的是积分还是经验值
			map.put("paymentPointsValue", paymentPointsValue);
			JSONObject job = new JSONObject(map);//商品集合
			
			saveOrder(job.toString(),address,desc,payment,sendtime,invoiceTitle,invoiceType);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void saveOrder(final String jsonstr,final String address,final String desc,final String payment,final String sendtime,final String invoiceTitle,final String invoiceType)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				try{
					JSONObject json = api.addMyOrderItem(jsonstr,address,desc,payment,sendtime,invoiceTitle,invoiceType,businessId);
					String requesttag = json.getString("tag");
					String orderNo = json.getString("orderNo");
					String delivery = json.getString("delivery");
					String payments = json.getString("payments");
					String payment = json.getString("payment");
					Map<String,String> map = new HashMap<String,String>();
					map.put("tag", requesttag);
					map.put("orderNo", orderNo);
					map.put("delivery", delivery);
					map.put("payments", payments);
					map.put("payment", payment);
					msg.obj = map;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public List<Map<String,Object>> getAddressList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String address = ""; 
				if(dobj.has("address"))
					address = (String) dobj.get("address"); 
				
				String area = ""; 
				if(dobj.has("area"))
					area = (String) dobj.get("area");
				
				String country = ""; 
				if(dobj.has("country"))
					country = (String) dobj.get("country");
				
				String telephone = ""; 
				if(dobj.has("telephone"))
					telephone = (String) dobj.get("telephone");
				
				String city = ""; 
				if(dobj.has("city"))
					city = (String) dobj.get("city");
				
				String phoneNumber = ""; 
				if(dobj.has("phoneNumber"))
					phoneNumber = (String) dobj.get("phoneNumber");
				
				String consignee = ""; 
				if(dobj.has("consignee"))
					consignee = (String) dobj.get("consignee");
				
				String zipcode = ""; 
				if(dobj.has("zipcode"))
					zipcode = (String) dobj.get("zipcode");
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String isdefault = ""; 
				if(dobj.has("isdefault"))
					isdefault = (String) dobj.get("isdefault");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("address", address);
				map.put("area", area);
				map.put("country", country);
				map.put("telephone", telephone);
				map.put("city", city);
				map.put("phoneNumber", phoneNumber);
				map.put("consignee", consignee);
				map.put("zipcode", zipcode);
				map.put("pkid", pkid);
				map.put("textCity", city+" "+country+" "+area);
				map.put("address2", city+" "+country+" "+area + " " + address);
				map.put("isdefault", isdefault);
				map.put("storeid", storeid);
	
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
