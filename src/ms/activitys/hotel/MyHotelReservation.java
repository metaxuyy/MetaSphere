package ms.activitys.hotel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyHotelReservation extends Activity{

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
	
	private ListView list_tab;
	private ProgressBar pb;
	
	private ViewFlipper mViewFlipper;
	
	private String pagetag;
	
	private String storeName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_hotel_reservation_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = MyHotelReservation.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("index");

		pb = (ProgressBar)findViewById(R.id.probar);
//		pb.setBackgroundColor(Color.parseColor("#0080FF"));
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		loadMyReservationData();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("reservationlist"))
				{
					MyHotelReservation.this.setResult(RESULT_OK, getIntent());
					MyHotelReservation.this.finish();
				}
				else if(pagetag.equals("reservationDetails"))
				{
					pagetag = "reservationlist";
					mViewFlipper.showPrevious();
				}
			return false;
		}
		return false;
	}
	
	public void loadMyReservationData()
	{
		pagetag = "reservationlist";
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
					
					list_tab.setVisibility(View.GONE);
					loadData(checkin.getText().toString(),checkout.getText().toString());
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
			
			loadData(null,null);
			
			list_tab = (ListView)findViewById(R.id.list_tab);
			
			list_tab.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Map map = myapp.getMyReservationRooms().get(arg2);
					openMyReservationDetailed(map);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadData(final String stime,final String etime)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try{
					List<Map<String,Object>> cards = myapp.getMyCardsAll();
					Map map = cards.get(Integer.valueOf(index));
					String storeid = (String)map.get("storeid");
					storeName = (String)map.get("storeName");
					
					JSONObject jobj = api.getMyHotelReservation(storeid,stime,etime);
					JSONArray jArr = (JSONArray) jobj.get("data");
					List<Map<String,Object>> list = getMyRoomList(jArr);
					myapp.setMyReservationRooms(list);
					
					msg.obj = list;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public List<Map<String,Object>> getMyRoomList(JSONArray jArr){
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
				
				String arrivalTime = ""; 
				if(dobj.has("arrivalTime"))
					arrivalTime = (String) dobj.get("arrivalTime"); 
				
				String statreTime = ""; 
				if(dobj.has("statreTime"))
					statreTime = (String) dobj.get("statreTime"); 
				
				String endTime = ""; 
				if(dobj.has("endTime"))
					endTime = (String) dobj.get("endTime"); 
				
				String roomHotelStyle = ""; 
				if(dobj.has("roomHotelStyle"))
					roomHotelStyle = (String) dobj.get("roomHotelStyle"); 
				
				String orderRoomNum = ""; 
				if(dobj.has("orderRoomNum"))
					orderRoomNum = (String) dobj.get("orderRoomNum"); 
				
				String total = ""; 
				if(dobj.has("total"))
					total = (String) dobj.get("total"); 
				
				String payWay = ""; 
				if(dobj.has("payWay"))
					payWay = (String) dobj.get("payWay"); 
				
				String reviewStatus = ""; 
				if(dobj.has("reviewStatus"))
					reviewStatus = (String) dobj.get("reviewStatus"); 
				
				String returnMoney = ""; 
				if(dobj.has("returnMoney"))
					returnMoney = (String) dobj.get("returnMoney"); 
				
				String customerName = ""; 
				if(dobj.has("customerName"))
					customerName = (String) dobj.get("customerName"); 
				
				String tel = ""; 
				if(dobj.has("tel"))
					tel = (String) dobj.get("tel"); 
				
				String email = ""; 
				if(dobj.has("email"))
					email = (String) dobj.get("email"); 
				
				String message = ""; 
				if(dobj.has("message"))
					message = (String) dobj.get("message"); 
					
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid); //主键ID
				map.put("arrivalTime", arrivalTime);
				map.put("statreTime", statreTime);
				map.put("endTime", endTime);
				map.put("roomHotelStyle", roomHotelStyle);
				map.put("orderRoomNum", orderRoomNum);
				map.put("total", total);
				map.put("payWay", payWay);
				map.put("reviewStatus", reviewStatus);
				map.put("returnMoney", returnMoney);
				map.put("customerName", customerName);
				map.put("tel", tel);
				map.put("email", email);
				map.put("message", message);
			
				//1为未受理，2为已受理
				if(reviewStatus.equals("2"))
				{
					map.put("icon", null);
					map.put("room_full", R.drawable.review_status);
				}
				else
				{
					map.put("icon", R.drawable.button_right);
					map.put("room_full", null);
				}
				
				map.put("type", roomHotelStyle);
				String pricestr = "￥"+total + "\n"+this.getString(R.string.hotel_reservation_lable_3)+":￥"+returnMoney;
				map.put("price", pricestr);
				
				String infostr = this.getString(R.string.hotel_reservation_lable_1)+":"+statreTime+"~"+endTime+"\n"+this.getString(R.string.hotel_reservation_lable_8)+":"+arrivalTime;
				map.put("info", infostr);
				
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 接到从线程内传来的图片bitmap和imageView.
				// 这里只是将bitmap传到imageView中就行了。只所以不在线程中做是考虑到线程的安全性。
				List<Map<String,Object>> list = (List<Map<String,Object>>)msg.obj;
				pb.setVisibility(View.GONE);
				
				list_tab.setAdapter(getAdapter(list));
				list_tab.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	private SpecialAdapter getAdapter(List<Map<String,Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.room_price, new String[] { "icon","price","type","room_full","info" },
				new int[] { R.id.icon,R.id.price,R.id.type,R.id.room_full,R.id.info },share,"ico");
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
							showMark(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_5));
					}
					else
					{
						TextView checkout = (TextView)findViewById(R.id.checkout);
						
						TextView checkin = (TextView)findViewById(R.id.checkin);
						String datastr2 = checkin.getText().toString();
						
						if(dateCompare(datestr,datastr2))
						{
							showMark(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_6));
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
	
	public void openMyReservationDetailed(Map<String,Object> map)
	{
		try{
			pagetag = "reservationDetails";
			RelativeLayout roomsrl = (RelativeLayout)findViewById(R.id.rooms);
			TextView firstlabel = (TextView) roomsrl.findViewById(R.id.firstlabel);
			firstlabel.setText(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_7));
			TextView secondlabel = (TextView) roomsrl.findViewById(R.id.secondlabel);
			secondlabel.setText((String)map.get("orderRoomNum"));
			
			RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);
			TextView firstlabel2 = (TextView) timesrl.findViewById(R.id.firstlabel);
			firstlabel2.setText(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_8));
			TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
			secondlabel2.setText((String)map.get("arrivalTime"));
			
			RelativeLayout hotelnamerl = (RelativeLayout)findViewById(R.id.hotel_name);
			loadIncludeRelativeLayout(hotelnamerl,storeName,null);
			
			RelativeLayout checkinrl = (RelativeLayout)findViewById(R.id.checkinrl);
			loadIncludeRelativeLayout(checkinrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_1),(String)map.get("statreTime"));
			
			RelativeLayout checkoutrl = (RelativeLayout)findViewById(R.id.checkoutrl);
			loadIncludeRelativeLayout(checkoutrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_2),(String)map.get("endTime"));
			
			RelativeLayout hoteltyperl = (RelativeLayout)findViewById(R.id.hotel_type);
			loadIncludeRelativeLayout(hoteltyperl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_9),(String)map.get("roomHotelStyle"));
			
			RelativeLayout roomcountrl = (RelativeLayout)findViewById(R.id.room_count);
			loadIncludeRelativeLayout(roomcountrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_10),(String)map.get("orderRoomNum"));
			
			RelativeLayout payrl = (RelativeLayout)findViewById(R.id.pay);
			loadIncludeRelativeLayout(payrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_11),"￥"+(String)map.get("total"));
			
			RelativeLayout paytyperl = (RelativeLayout)findViewById(R.id.pay_type);
			loadIncludeRelativeLayout(paytyperl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_12),(String)map.get("payWay"));
			
			RelativeLayout backnowrl = (RelativeLayout)findViewById(R.id.back_now);
			loadIncludeRelativeLayout(backnowrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_3),"￥"+(String)map.get("returnMoney"));
			
			RelativeLayout guestnamerl = (RelativeLayout)findViewById(R.id.guest_name2);
			loadIncludeRelativeLayout(guestnamerl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_13),(String)map.get("customerName"));
			
			RelativeLayout mobilerl = (RelativeLayout)findViewById(R.id.mobile2);
			loadIncludeRelativeLayout(mobilerl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_15),(String)map.get("tel"));
			
			RelativeLayout emailrl = (RelativeLayout)findViewById(R.id.email2);
			loadIncludeRelativeLayout(emailrl,MyHotelReservation.this.getString(R.string.hotel_reservation_lable_16),(String)map.get("email"));
			
			TextView messagerl = (TextView)findViewById(R.id.message2);
			messagerl.setText(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_20)+":"+(String)map.get("message"));
			
			TextView stv = (TextView)findViewById(R.id.submit_text);
			stv.setText(MyHotelReservation.this.getString(R.string.hotel_reservation_lable_21));
			
			Button submit = (Button)findViewById(R.id.submit);
			submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pagetag = "reservationlist";
					mViewFlipper.showPrevious();
				}
			});
			
			mViewFlipper.showNext();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadIncludeRelativeLayout(RelativeLayout rl,String fstr,String rstr)
	{
		TextView firstlabel = (TextView) rl.findViewById(R.id.firstlabel);
		firstlabel.setText(fstr);
		TextView secondlabel = (TextView) rl.findViewById(R.id.secondlabel);
		secondlabel.setText(rstr);
		ImageView icon = (ImageView) rl.findViewById(R.id.icon);
		icon.setBackgroundDrawable(null);
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
