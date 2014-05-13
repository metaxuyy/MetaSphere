package ms.activitys.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
import ms.activitys.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProductComments extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String productId;
	
	private ProgressBar pb;
	
	private List<Map<String, Object>> commentlist;
	
	private LinearLayout clistView;
	
	private int index;
	private String atage = "";
	private boolean isDodo = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_list_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = ProductComments.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		productId = bunde.getString("productId");
		index = bunde.getInt("index");
		if(bunde.containsKey("tage"))
			atage = bunde.getString("tage");
		else if(bunde.containsKey("dodo"))
		{
			isDodo = true;
		}
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		clistView = (LinearLayout)findViewById(R.id.comment_view);
		
		loadThreadData();
		
		addCommentView();
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				loadCommentListData();
				
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				setAdapter(commentlist);
				
				pb.setVisibility(View.GONE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	private void addCommentView()
	{
		try{
			Map<String,Object> map = null;
			if(atage.equals(""))
				map = myapp.getProductList().get(index);
			else
				map = myapp.getAdvertiseProducts().get(index);
			
			if(isDodo)
				map = myapp.getDodoMap();
			
			String cname = (String)map.get("cname");
			String price = (String)map.get("price");
			float fiveimg = (Float)map.get("fiveimg");
			
			String oldPrices = (String)map.get("oldPrices");
			String saving = (String)map.get("saving");
			String five = (String)map.get("five");
			String four = (String)map.get("four");
			String three = (String)map.get("three");
			String two = (String)map.get("two");
			String one = (String)map.get("one");
			String total = (String)map.get("total");
			
			TextView title = (TextView)findViewById(R.id.widget35);
			title.setText(cname);
			
			if(oldPrices != null && !oldPrices.equals("0.00"))
			{
				TextView oldPricestv = (TextView)findViewById(R.id.oldPricestv);
				oldPrices = oldPrices + " ￥";
				SpannableString ss = new SpannableString( oldPrices);
		        ss.setSpan(new StrikethroughSpan(), 0, oldPrices.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		        oldPricestv.setText(ss);
				
				TextView savingtv = (TextView)findViewById(R.id.savingtv);
				saving = saving + " ￥";
				savingtv.setText(saving);
			}
			else
			{
				TextView oldPricestv = (TextView)findViewById(R.id.oldPricestv);
				oldPricestv.setVisibility(View.GONE);
				
				TextView oldPricestvtxt = (TextView)findViewById(R.id.oldPricestvtxt);
				oldPricestvtxt.setVisibility(View.GONE);
				
				TextView savingtv = (TextView)findViewById(R.id.savingtv);
				savingtv.setVisibility(View.GONE);
				
				TextView savingtvtxt = (TextView)findViewById(R.id.savingtvtxt);
				savingtvtxt.setVisibility(View.GONE);
			}
			
			TextView pricetv = (TextView)findViewById(R.id.widget36);
			pricetv.setText(price);
			
			RatingBar ratingBar = (RatingBar) findViewById(R.id.peng_ji_img); 
			ratingBar.setRating(fiveimg);
			
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
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
	
	private void setAdapter(List<Map<String,Object>> data) {
		for(int i=0;i<data.size();i++)
		{
			Map<String,Object> map = data.get(i);
			String discusScore = (String)map.get("discusScore"); 
			String discusDesc = (String)map.get("discusDesc");
			String summaryInfor = (String)map.get("SummaryInfor");
			String dec = (String)map.get("dec");
			
			View view = View.inflate(this, R.layout.product_comment_item, null);
			RatingBar pj = (RatingBar) view.findViewById(R.id.peng_ji_img);
			pj.setRating(Float.valueOf(discusScore));
			
			TextView ctitle = (TextView) view.findViewById(R.id.comment_title);
			ctitle.setText(summaryInfor);
			
			TextView content = (TextView) view.findViewById(R.id.comment_content);
			content.setText(discusDesc);
			
			TextView dectv = (TextView) view.findViewById(R.id.comment_dec);
			dectv.setText(dec);
			
			clistView.addView(view);
		}
	}
	
	public void loadCommentListData()
	{
		commentlist = new ArrayList<Map<String, Object>>();
		try{
			JSONObject jobj = api.getProductCommentList(productId);
			JSONArray jArr = (JSONArray) jobj.get("data");
			
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String nameFirst = ""; 
				if(dobj.has("nameFirst"))
					nameFirst = (String) dobj.get("nameFirst"); 
				
				String discusScore = ""; 
				if(dobj.has("discusScore"))
					discusScore = (String) dobj.get("discusScore");
				
				String discusDesc = ""; 
				if(dobj.has("discusDesc"))
					discusDesc = (String) dobj.get("discusDesc");
				
				String createdTxStamp = ""; 
				if(dobj.has("createdTxStamp"))
					createdTxStamp = (String) dobj.get("createdTxStamp");
				
				String SummaryInfor = ""; 
				if(dobj.has("SummaryInfor"))
					SummaryInfor = (String) dobj.get("SummaryInfor");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("nameFirst", nameFirst);
				map.put("discusScore", discusScore); 
				map.put("discusDesc", discusDesc);
				map.put("createdTxStamp", createdTxStamp);
				map.put("SummaryInfor", SummaryInfor);
				
				map.put("dec", this.getString(R.string.menu_lable_22) + nameFirst + "  "+this.getString(R.string.menu_lable_22) + createdTxStamp);
				
				commentlist.add(map);
			
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
