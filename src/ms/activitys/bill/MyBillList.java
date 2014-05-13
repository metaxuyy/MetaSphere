package ms.activitys.bill;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import ms.activitys.R;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

public class MyBillList extends Activity{

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
	
	private String index;
	private String appliescStoreid;
	
	private ViewFlipper mViewFlipper; 
	
	private String pagetag;
	
	private ProgressBar pb;
	private ListView slistView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_list_view_new);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("index");
		appliescStoreid = bunde.getString("appliescStoreid");
		
		share = MyBillList.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		pb = (ProgressBar)findViewById(R.id.probar);
		
		showBillListPage();
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("orderlist"))
				{
					MyBillList.this.setResult(RESULT_OK, getIntent());
					MyBillList.this.finish();
				}
				else if(pagetag.equals("orderDetails"))
				{
					pagetag = "orderlist";
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
	}
	
	/**
	 * 显示账单列表
	 */
	public void showBillListPage()
	{
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			String cdate = sf.format(calendar.getTime());
			
			calendar.add(Calendar.DAY_OF_MONTH, +1);//取当前日期的后一天. 
			String hdate = sf.format(calendar.getTime());
			
			final TextView checkin = (TextView)findViewById(R.id.checkin);
			checkin.setText(cdate);
			
			final TextView checkout = (TextView)findViewById(R.id.checkout);
			checkout.setText(hdate);
			
			Button change_date = (Button)findViewById(R.id.change_date);
			change_date.setText(this.getString(R.string.mybill_list_lable_1));
			change_date.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pb.setVisibility(View.VISIBLE);
					
					slistView.setVisibility(View.GONE);
					List<Map<String,Object>> lists = myapp.getMyCardsAll();
					Map map = lists.get(Integer.valueOf(index));
					
					String storeid = (String)map.get("storeid");
					loadData(checkin.getText().toString(),checkout.getText().toString(),storeid);
				}
			});
			
			LinearLayout btn_ll = (LinearLayout)findViewById(R.id.start_end_time);
			btn_ll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDateSelection();
				}
			});
			
			showOrderList(null,null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadData(final String stime,final String etime,final String storeid)
	{
		System.out.println("stime========="+stime+"======="+etime);
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					jobj = api.getBillListData(stime,etime,storeid);
					if(jobj != null)
					{
						if(!jobj.has("error"))
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getBillList(jArr);
							myapp.setMyOrderHitList(list);
						}
					}
					
					msg.obj = list;
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
			case 0:// 接到从线程内传来的图片bitmap和imageView.
				// 这里只是将bitmap传到imageView中就行了。只所以不在线程中做是考虑到线程的安全性。
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				if(dlist != null)
					slistView.setAdapter(getServiceMenuAdapter(dlist));
				pb.setVisibility(View.GONE);
				
				slistView.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void showOrderList(String sdate,String edate)
	{
		pagetag = "orderlist";
		try{
			slistView = (ListView)findViewById(R.id.ListView_catalog);
//			final List<Map<String,Object>> dlist = getBillListData(sdate,edate);
			List<Map<String,Object>> lists = myapp.getMyCardsAll();
			Map map = lists.get(Integer.valueOf(index));
			
			String storeid = (String)map.get("storeid");
			loadData(sdate,edate,storeid);
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					Map map = myapp.getMyOrderHitList().get(arg2);
					showOrderDetails(map);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void showOrderDetails(Map<String,Object> map)
	{
		pagetag = "orderDetails";
		try{
			final String orderhid = (String)map.get("orderhid");
			String orderNo = (String)map.get("orderNo");
			String totalPrice = (String)map.get("totalPrices");
			String sendTime = (String)map.get("receviceTime");
			
			String receiName = (String)map.get("receiName");
			String phone = (String)map.get("phone");
			String address = (String)map.get("address");
			String logisticsNo = (String)map.get("logisticsNo");
			String logisticsComp = (String)map.get("logisticsComp");
			
			TextView ordernoText = (TextView)findViewById(R.id.order_nod);
			ordernoText.setText(orderNo);
			
			TextView orderstatusText = (TextView)findViewById(R.id.order_statusd);
			orderstatusText.setText(this.getString(R.string.mybill_list_lable_2));
			
			TextView totalpriceText = (TextView)findViewById(R.id.total_priced);
			totalpriceText.setText(totalPrice);
			
			TextView confirmTimeText = (TextView)findViewById(R.id.send_timed);
			confirmTimeText.setText(sendTime);
			
			TextView receinameText = (TextView)findViewById(R.id.recei_name_true);
			receinameText.setText(receiName);
			
			TextView phonetext = (TextView)findViewById(R.id.phone_true);
			phonetext.setText(phone);
			
			TextView logisticsnoText = (TextView)findViewById(R.id.logistics_no_true);
			logisticsnoText.setText(logisticsNo);
			
			TextView logisticscompanyText = (TextView)findViewById(R.id.logistics_company_true);
			logisticscompanyText.setText(logisticsComp);
			
			TextView sendtimeText = (TextView)findViewById(R.id.send_timed);
			sendtimeText.setText(sendTime);
			
			TextView addressText = (TextView)findViewById(R.id.address_true);
			addressText.setText(address);
				
			
			loadOrderItemDetailed(orderhid);
			
			Button cbtn = (Button)findViewById(R.id.clear_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pagetag = "orderlist";
					mViewFlipper.showPrevious();
				}
			});
			
			mViewFlipper.showNext();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadOrderItemDetailed(final String orderhid)
	{
		List<Map<String,Object>> dlist = getOrderDetailsItems(orderhid);
		LinearLayout layoutview = (LinearLayout)findViewById(R.id.LinearLayout07);
		if(dlist != null && dlist.size()>0)
		{
			layoutview.removeAllViews();
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> maps = dlist.get(i);
				String productName = (String)maps.get("productName"); 
				String typeName = (String)maps.get("typeName");
				String productPrices = (String)maps.get("productPrices");
				String buyNumber = (String)maps.get("buyNumber");
				String productImg = (String)maps.get("productImg");
				String productColor = (String)maps.get("productColor");
				String totalPrices = (String)maps.get("totalPrices");
				final String productId = (String)maps.get("productId");
				String isDiscuss = (String)maps.get("isDiscuss");
				final String ohdid = (String)maps.get("ohdid");

				View view = View.inflate(this, R.layout.order_detailed_list_item, null);
				
				TextView pname = (TextView) view.findViewById(R.id.product_name);
				pname.setText(productName);
				
				TextView prices = (TextView) view.findViewById(R.id.product_price);
				prices.setText(productPrices);
				
				TextView pnumber = (TextView) view.findViewById(R.id.product_number);
				pnumber.setText(buyNumber);
				
				TextView pcolor = (TextView) view.findViewById(R.id.product_color);
				if(productColor != null && !productColor.equals(""))
					pcolor.setText(productColor);
				else
					pcolor.setVisibility(View.GONE);
				
				TextView tprices = (TextView) view.findViewById(R.id.product_total_price);
				tprices.setText(totalPrices);
				
				Button commentbtn = (Button)view.findViewById(R.id.comment_btn);
				if(isDiscuss.equals("1"))
				{
					makeText(this.getString(R.string.mybill_list_lable_3));
					commentbtn.setVisibility(View.VISIBLE);
					commentbtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showProudectCommentpoupon(productId,ohdid,orderhid);
						}
					});
				}
				else
				{
					commentbtn.setVisibility(View.GONE);
				}
				
				layoutview.addView(view);
			}
		}
	}
	
	public void showProudectCommentpoupon(final String productId,final String ohdid,final String orderhid)
	{
		try{
			
			final View view = LayoutInflater.from(this).inflate(R.layout.proudect_comment_popup, null);
			
			WindowManager m = getWindowManager();    
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
                
            int width = (int) (d.getWidth() * 0.9);    //宽度设置为屏幕的0.8   
                
            LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT);
            topPanel.setLayoutParams(lp);
            
			final Dialog myDialog = new Dialog(this, R.style.AliDialog);
			myDialog.setContentView(view);
			myDialog.show();
			
			RatingBar rbscore = (RatingBar)view.findViewById(R.id.peng_score);
			rbscore.setNumStars(5);
			rbscore.setStepSize(1);
			rbscore.setRating(1);
			
			final TextView summarytv = (TextView)view.findViewById(R.id.summary);
			
			
			final TextView detailedtv = (TextView)view.findViewById(R.id.detailed);
			
			
			Button fbtn = (Button)view.findViewById(R.id.comment_btn);
			fbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try{
						RatingBar rbscore = (RatingBar)view.findViewById(R.id.peng_score);
						float score = rbscore.getRating();
						String detailed = detailedtv.getText().toString();
						String summary = summarytv.getText().toString();
						JSONObject jsob = api.addProudectComment(appliescStoreid, String.valueOf(score), detailed, summary, productId,ohdid);
						if(jsob != null)
						{
							makeText(MyBillList.this.getString(R.string.mybill_list_lable_4));
							loadOrderItemDetailed(orderhid);
							myDialog.dismiss();
						}
						else
						{
							makeText(MyBillList.this.getString(R.string.mybill_list_lable_5));
						}
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
	
	public void makeText(String str){
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 显示消费详细
	 */
	public void showBillDetatil()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyBillDetails.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("roomNo", "");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getBillListData(String sdate,String edate)
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			List<Map<String,Object>> lists = myapp.getMyCardsAll();
			Map map = lists.get(Integer.valueOf(index));
			
			String storeid = (String)map.get("storeid");
			jobj = api.getBillListData(sdate,edate,storeid);
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getBillList(jArr);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getBillList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String orderNo = ""; 
				if(dobj.has("orderNo"))
					orderNo = (String) dobj.get("orderNo"); 
				
				String orderhid = ""; 
				if(dobj.has("orderhid"))
					orderhid = (String) dobj.get("orderhid");
				
				String totalPrices = ""; 
				if(dobj.has("totalPrices"))
					totalPrices = (String) dobj.get("totalPrices");
				
				String storeName = ""; 
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String receviceTime = ""; 
				if(dobj.has("receviceTime"))
					receviceTime = (String) dobj.get("receviceTime"); 
				
				String receiName = ""; 
				if(isValueNull("receiName",dobj))
					receiName = (String) dobj.get("receiName");  
				
				String phone = ""; 
				if(isValueNull("phone",dobj))
					phone = (String) dobj.get("phone"); 
				
				
				String address = ""; 
				if(isValueNull("address",dobj))
					address = (String) dobj.get("address"); 
				
				String logisticsNo = ""; 
				if(isValueNull("logisticsNo",dobj))
					logisticsNo = (String) dobj.get("logisticsNo"); 
				
				String logisticsComp = ""; 
				if(isValueNull("logisticsComp",dobj))
					logisticsComp = (String) dobj.get("logisticsComp"); 
				
				String isDiscuss = "1";  //0是是，1是否没有发表过评论
				if(isValueNull("isDiscuss",dobj))
					isDiscuss = (String) dobj.get("isDiscuss"); 
				
				String payment = ""; 
				if(isValueNull("payment",dobj))
					payment = (String) dobj.get("payment"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderNo", orderNo);
				map.put("orderhid", orderhid);
				map.put("totalPrices", totalPrices + " ￥");
				map.put("storeName", storeName);
				map.put("receviceTime", receviceTime);
				
				map.put("receiName", receiName);
				map.put("phone", phone);
				map.put("address", address);
				map.put("logisticsNo", logisticsNo);
				map.put("logisticsComp", logisticsComp);
				map.put("isDiscuss", isDiscuss);
				map.put("payment", payment);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public boolean isValueNull(String str,JSONObject dobj)
	{
		boolean b = true;
		try{
			if(dobj.has(str))
			{
				Object obj = dobj.get(str);
				if(obj.equals("null"))
					b = false;
			}
			else
			{
				b = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return b;
	}
	
	public List<Map<String,Object>> getOrderDetailsItems(String orderhid)
	{
		List<Map<String,Object>> list = null;
		try{
			JSONObject jobj = api.getOrderDetailsHItem(orderhid);
			JSONArray jArr = (JSONArray) jobj.get("data");
			list = getOrderDetailsItem(jArr);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getOrderDetailsItem(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String productName = ""; 
				if(dobj.has("productName"))
					productName = (String) dobj.get("productName"); 
				
				String typeName = "";
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				
				String productPrices = ""; 
				if(dobj.has("productPrices"))
					productPrices = (String) dobj.get("productPrices"); 
				
				String totalPrices = ""; 
				if(dobj.has("totalPrices"))
					totalPrices = (String) dobj.get("totalPrices"); 
				
				String buyNumber = ""; 
				if(dobj.has("buyNumber"))
					buyNumber = (String) dobj.get("buyNumber"); 
				
				String productImg = ""; 
				if(dobj.has("productImg"))
					productImg = (String) dobj.get("productImg"); 
				
				String productColor = ""; 
				if(dobj.has("productColor"))
					productColor = (String) dobj.get("productColor"); 
				
				String productId = ""; 
				if(dobj.has("productId"))
					productId = (String) dobj.get("productId"); 
				
				String isDiscuss = "1"; 
				if(dobj.has("isDiscuss"))
					isDiscuss = (String) dobj.get("isDiscuss"); 
				
				String ohdid = ""; 
				if(dobj.has("ohdid"))
					ohdid = (String) dobj.get("ohdid"); 
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("productName", productName);
				map.put("typeName", typeName);
				map.put("productPrices", this.getString(R.string.mybill_list_lable_6)+productPrices + " ￥");
				map.put("totalPrices", this.getString(R.string.mybill_list_lable_7)+totalPrices + " ￥");
				map.put("buyNumber", this.getString(R.string.mybill_list_lable_8)+buyNumber);
				map.put("productImg", productImg);
				map.put("productColor", productColor);
				map.put("productId", productId);
				map.put("isDiscuss", isDiscuss);
				map.put("ohdid", ohdid);
				
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private SpecialAdapter getServiceMenuAdapter(List<Map<String,Object>> data) {
		SpecialAdapter listItemAdapter = new SpecialAdapter(this, data,// 数据源
				R.layout.bill_view_new,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "storeName","receviceTime","totalPrices" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.customerName,R.id.realStartTime,R.id.totalConsume },share,"ico");
		return listItemAdapter;
	}
	
	private SimpleAdapter getAdapter2(List<Map<String,Object>> data) {
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.order_details_item, new String[] { "productName","productPrices","buyNumber","totalPrices" },
				new int[] { R.id.product_name,R.id.product_price,R.id.buy_number,R.id.total_prices });
		return simperAdapter;
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
							showMark(MyBillList.this.getString(R.string.mybill_list_lable_11));
					}
					else
					{
						TextView checkout = (TextView)findViewById(R.id.checkout);
						
						TextView checkin = (TextView)findViewById(R.id.checkin);
						String datastr2 = checkin.getText().toString();
						
						if(dateCompare(datestr,datastr2))
						{
							showMark(MyBillList.this.getString(R.string.mybill_list_lable_12));
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
