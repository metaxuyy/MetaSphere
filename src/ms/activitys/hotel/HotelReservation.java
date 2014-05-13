package ms.activitys.hotel;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.globalclass.StreamTool;
import ms.globalclass.dbhelp.DBHelperCoupon;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;
import ms.activitys.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HotelReservation extends Activity{
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
	
	private double total;
	private double totals;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_reservation_view);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = HotelReservation.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("index");

		pb = (ProgressBar)findViewById(R.id.probar);
//		pb.setBackgroundColor(Color.parseColor("#0080FF"));
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		loadReservationData();
	}
	
	public void loadReservationData()
	{
		try{
			pagetag = "reservationlist";
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			String cdate = sf.format(calendar.getTime());
			
			calendar.add(Calendar.DAY_OF_MONTH, +1);//取当前日期的后一天. 
			String hdate = sf.format(calendar.getTime());
			
			TextView checkin = (TextView)findViewById(R.id.checkin);
			checkin.setText(this.getString(R.string.hotel_reservation_lable_1)+":"+cdate);
			
			TextView checkout = (TextView)findViewById(R.id.checkout);
			checkout.setText(this.getString(R.string.hotel_reservation_lable_2)+":"+hdate);
			
			Button change_date = (Button)findViewById(R.id.change_date);
			change_date.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDateSelection();
				}
			});
			
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					
					try{
						List<Map<String,Object>> cards = myapp.getMyCardsAll();
						Map map = cards.get(Integer.valueOf(index));
						String storeid = (String)map.get("storeid");
						
						JSONObject jobj = api.getHotelReservation(storeid);
						JSONArray jArr = (JSONArray) jobj.get("data");
						List<Map<String,Object>> list = getRoomList(jArr);
						myapp.setReservationRooms(list);
						
						msg.obj = list;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
			
			
			list_tab = (ListView)findViewById(R.id.list_tab);
			
			list_tab.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Map map = myapp.getReservationRooms().get(arg2);
					String roomStatus = (String)map.get("roomStatus");
					if(roomStatus.equals("2"))
						openReservationDetailed(map);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if(pagetag.equals("reservationlist"))
				{
					HotelReservation.this.setResult(RESULT_OK, getIntent());
					HotelReservation.this.finish();
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
	
	private SpecialAdapter getAdapter(List<Map<String,Object>> data) {
		SpecialAdapter simperAdapter = new SpecialAdapter(this, data,
				R.layout.room_price, new String[] { "icon","price","type","room_full","info" },
				new int[] { R.id.icon,R.id.price,R.id.type,R.id.room_full,R.id.info },share,"ico");
		return simperAdapter;
	}
	
	public List<Map<String,Object>> getRoomList(JSONArray jArr){
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
				
				String roomStyle = ""; 
				if(dobj.has("roomStyle"))
					roomStyle = (String) dobj.get("roomStyle"); 
				
				String bed = ""; 
				if(dobj.has("bed"))
					bed = (String) dobj.get("bed"); 
				
				String choose = ""; 
				if(dobj.has("choose"))
					choose = (String) dobj.get("choose"); 
				
				String smoking = ""; 
				if(dobj.has("smoking"))
					smoking = (String) dobj.get("smoking"); 
				
				String windows = ""; 
				if(dobj.has("windows"))
					windows = (String) dobj.get("windows"); 
				
				String addbed = ""; 
				if(dobj.has("addbed"))
					addbed = (String) dobj.get("addbed"); 
				
				String roomStatus = ""; 
				if(dobj.has("roomStatus"))
					roomStatus = (String) dobj.get("roomStatus"); 
				
				String roomPrice = ""; 
				if(dobj.has("roomPrice"))
					roomPrice = (String) dobj.get("roomPrice");
				
				String returnMoney = ""; 
				if(dobj.has("returnMoney"))
					returnMoney = (String) dobj.get("returnMoney");
				
				String payName = ""; 
				if(dobj.has("payName"))
					payName = (String) dobj.get("payName");
					
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid); //主键ID
				map.put("roomStyle", roomStyle); //房间类型
				map.put("bed", bed); //床类型
				map.put("choose", choose); //是否可上网
				map.put("smoking", smoking); //是否可吸烟
				map.put("windows", windows); //是否有窗户
				map.put("addbed", addbed); //是否可以加床
				map.put("roomStatus", roomStatus); //房间状态1为客满2为可预定
				map.put("roomPrice", roomPrice); //房间价钱
				map.put("returnMoney", returnMoney); //返现
				map.put("payName", payName); //支付方式
			
				if(roomStatus.equals("1"))
				{
					map.put("icon", R.drawable.button_right);
					map.put("room_full", R.drawable.room_full);
				}
				else
				{
					map.put("icon", R.drawable.button_right);
					map.put("room_full", null);
				}
				
				map.put("type", roomStyle);
				String pricestr = "￥"+roomPrice + "\n"+this.getString(R.string.hotel_reservation_lable_3)+":"+"￥"+returnMoney;
				map.put("price", pricestr);
				
				String infostr = bed+"|"+choose+"|"+smoking+"|"+windows+"|"+addbed;
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
	
	public void showDateSelection()
	{
		try{
			final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			
			View view = LayoutInflater.from(this).inflate(R.layout.datepicker_view,null);
			
			final RadioButton b1 = (RadioButton)view.findViewById(R.id.b1);
			b1.setChecked(true);
			
			final RadioButton b2 = (RadioButton)view.findViewById(R.id.b2);
			
			TextView checkin = (TextView)findViewById(R.id.checkin);
			String [] dstrs = checkin.getText().toString().split(":"); 
			String dstr = dstrs[1];
			
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
						String [] dstrs = checkin.getText().toString().split(":"); 
						String dstr = dstrs[1];
						
						String [] strs = dstr.split("-");
						int y = Integer.valueOf(strs[0]);
						int m = Integer.valueOf(strs[1]);
						int d = Integer.valueOf(strs[2]);
						
						dp.init(y,m-1,d, null);
		            } else if (checkedId == b2.getId()) {  
		            	TextView checkout = (TextView)findViewById(R.id.checkout);
						String [] dstrs = checkout.getText().toString().split(":"); 
						String dstr = dstrs[1];
						
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
						String str = checkout.getText().toString();
						String [] strs = str.split(":");
						String datastr2 = strs[1];
						
						if(dateCompare(datestr,datastr2))
						{
							Calendar calendar = Calendar.getInstance();
							String cdate = sf.format(calendar.getTime());
							
							if(dateCompare(datestr,cdate))
							{
								showMark(HotelReservation.this.getString(R.string.hotel_reservation_lable_4));
							}
							else
							{
								checkin.setText(HotelReservation.this.getString(R.string.hotel_reservation_lable_1)+":"+datestr);
								myDialog.dismiss();
							}
						}
						else
							showMark(HotelReservation.this.getString(R.string.hotel_reservation_lable_5));
					}
					else
					{
						TextView checkout = (TextView)findViewById(R.id.checkout);
						
						TextView checkin = (TextView)findViewById(R.id.checkin);
						String str = checkin.getText().toString();
						String [] strs = str.split(":");
						String datastr2 = strs[1];
						
						if(dateCompare(datestr,datastr2))
						{
							showMark(HotelReservation.this.getString(R.string.hotel_reservation_lable_6));
						}
						else
						{
							checkout.setText(HotelReservation.this.getString(R.string.hotel_reservation_lable_2)+":"+datestr);
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
	
	public void openReservationDetailed(Map<String,Object> map)
	{
		try{
			pagetag = "reservationDetails";
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			
			String pkid = (String)map.get("pkid"); //主键ID
			String roomStyle = (String)map.get("roomStyle"); //房间类型
			String roomPrice = (String)map.get("roomPrice"); //房间价钱
			String returnMoney = (String)map.get("returnMoney"); //返现
			String payName = (String)map.get("payName"); //支付方式
			
			List<Map<String,Object>> cards = myapp.getMyCardsAll();
			Map cmap = cards.get(Integer.valueOf(index));
			final String storeName = (String)cmap.get("storeName");
			
			TextView checkin = (TextView)findViewById(R.id.checkin);
			String [] dstrs = checkin.getText().toString().split(":"); 
			String dstr = dstrs[1];
			
			TextView checkout = (TextView)findViewById(R.id.checkout);
			String [] dstrs2 = checkout.getText().toString().split(":"); 
			String dstr2 = dstrs2[1];
			
			RelativeLayout roomsrl = (RelativeLayout)findViewById(R.id.rooms);
			TextView firstlabel = (TextView) roomsrl.findViewById(R.id.firstlabel);
			firstlabel.setText(this.getString(R.string.hotel_reservation_lable_7));
			TextView secondlabel = (TextView) roomsrl.findViewById(R.id.secondlabel);
			secondlabel.setText("1");
			
			roomsrl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showRoomDialog();
				}
			});
			
			RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);
			TextView firstlabel2 = (TextView) timesrl.findViewById(R.id.firstlabel);
			firstlabel2.setText(this.getString(R.string.hotel_reservation_lable_8));
			TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
			secondlabel2.setText("14:00~16:00");
			
			timesrl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showTimeDialog();
				}
			});
			
			RelativeLayout hotelnamerl = (RelativeLayout)findViewById(R.id.hotel_name);
			loadIncludeRelativeLayout(hotelnamerl,storeName,null);
			
			RelativeLayout checkinrl = (RelativeLayout)findViewById(R.id.checkinrl);
			loadIncludeRelativeLayout(checkinrl,this.getString(R.string.hotel_reservation_lable_1),dstr);
			
			RelativeLayout checkoutrl = (RelativeLayout)findViewById(R.id.checkoutrl);
			loadIncludeRelativeLayout(checkoutrl,this.getString(R.string.hotel_reservation_lable_2),dstr2);
			
			RelativeLayout hoteltyperl = (RelativeLayout)findViewById(R.id.hotel_type);
			loadIncludeRelativeLayout(hoteltyperl,this.getString(R.string.hotel_reservation_lable_9),roomStyle);
			
			RelativeLayout roomcountrl = (RelativeLayout)findViewById(R.id.room_count);
			loadIncludeRelativeLayout(roomcountrl,this.getString(R.string.hotel_reservation_lable_10),"1");
			
			DecimalFormat df = new DecimalFormat("######0.00");
			RelativeLayout payrl = (RelativeLayout)findViewById(R.id.pay);
			int days = getIntervalDays(sf.parse(dstr2),sf.parse(dstr));
			total = days * Double.valueOf(roomPrice);
			loadIncludeRelativeLayout(payrl,this.getString(R.string.hotel_reservation_lable_11),"￥"+df.format(total));
			
			RelativeLayout paytyperl = (RelativeLayout)findViewById(R.id.pay_type);
			loadIncludeRelativeLayout(paytyperl,this.getString(R.string.hotel_reservation_lable_12),payName);
			
			RelativeLayout backnowrl = (RelativeLayout)findViewById(R.id.back_now);
			loadIncludeRelativeLayout(backnowrl,this.getString(R.string.hotel_reservation_lable_3),"￥"+returnMoney);
			
			RelativeLayout guestnamerl = (RelativeLayout)findViewById(R.id.guest_name);
			EditText guestnameet = getIncludeGuestRelativeLayout(guestnamerl,this.getString(R.string.hotel_reservation_lable_13),null,this.getString(R.string.hotel_reservation_lable_14));
			
			RelativeLayout mobilerl = (RelativeLayout)findViewById(R.id.mobile);
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phonnumber = tm.getLine1Number();
			EditText mobileet = getIncludeGuestRelativeLayout(mobilerl,this.getString(R.string.hotel_reservation_lable_15),phonnumber,this.getString(R.string.hotel_reservation_lable_14));
			
			RelativeLayout emailrl = (RelativeLayout)findViewById(R.id.email);
			EditText emailet = getIncludeGuestRelativeLayout(emailrl,this.getString(R.string.hotel_reservation_lable_16),null,this.getString(R.string.hotel_reservation_lable_17));
			
			RelativeLayout messagerl = (RelativeLayout)findViewById(R.id.message);
			EditText messageet = getIncludeGuestRelativeLayout(messagerl,this.getString(R.string.hotel_reservation_lable_18),null,this.getString(R.string.hotel_reservation_lable_17));
			
			Button submit = (Button)findViewById(R.id.submit);
			submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					submitData(storeName);
				}
			});
			
			mViewFlipper.showNext();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void submitData(String storeName)
	{
		try{
			Map<String,Object> data = new HashMap<String,Object>();
			
			List<Map<String,Object>> cards = myapp.getMyCardsAll();
			Map cmap = cards.get(Integer.valueOf(index));
			String storeid = (String)cmap.get("storeid");
			
			RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);
			TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
			String arrivalTime = secondlabel2.getText().toString();
			
			RelativeLayout checkinrl = (RelativeLayout)findViewById(R.id.checkinrl);
			TextView checktext = (TextView) checkinrl.findViewById(R.id.secondlabel);
			String checkin = checktext.getText().toString();
			
			RelativeLayout checkoutrl = (RelativeLayout)findViewById(R.id.checkoutrl);
			TextView checkouttext = (TextView) checkoutrl.findViewById(R.id.secondlabel);
			String checkout = checkouttext.getText().toString();
			
			RelativeLayout hoteltyperl = (RelativeLayout)findViewById(R.id.hotel_type);
			TextView roomtypetext = (TextView) hoteltyperl.findViewById(R.id.secondlabel);
			String roomtype = roomtypetext.getText().toString();
			
			RelativeLayout roomcountrl = (RelativeLayout)findViewById(R.id.room_count);
			TextView roomcounttext = (TextView) roomcountrl.findViewById(R.id.secondlabel);
			String roomcount = roomcounttext.getText().toString();
			
			RelativeLayout paytyperl = (RelativeLayout)findViewById(R.id.pay_type);
			TextView paytypetext = (TextView) paytyperl.findViewById(R.id.secondlabel);
			String paytype = paytypetext.getText().toString();
			
			RelativeLayout backnowrl = (RelativeLayout)findViewById(R.id.back_now);
			TextView backnowtext = (TextView) backnowrl.findViewById(R.id.secondlabel);
			String backnow = backnowtext.getText().toString().replaceAll("￥", "");
			
			RelativeLayout guestnamerl = (RelativeLayout)findViewById(R.id.guest_name);
			EditText guestnameet = (EditText) guestnamerl.findViewById(R.id.secondlabel);
			String guestname = guestnameet.getText().toString();
			
			RelativeLayout mobilerl = (RelativeLayout)findViewById(R.id.mobile);
			EditText mobileet = (EditText) mobilerl.findViewById(R.id.secondlabel);
			String mobile = mobileet.getText().toString();
			
			RelativeLayout emailrl = (RelativeLayout)findViewById(R.id.email);
			EditText emailet = (EditText) emailrl.findViewById(R.id.secondlabel);
			String email = emailet.getText().toString();
			
			RelativeLayout messagerl = (RelativeLayout)findViewById(R.id.message);
			EditText messageet = (EditText) messagerl.findViewById(R.id.secondlabel);
			String message = messageet.getText().toString();
			
			data.put("storeid", storeid);
			data.put("arrivalTime", arrivalTime);
			data.put("checkin", checkin);
			data.put("checkout", checkout);
			data.put("roomtype", roomtype);
			data.put("roomcount", roomcount);
			data.put("total", totals);
			data.put("paytype", paytype);
			data.put("backnow", backnow);
			data.put("guestname", guestname);
			data.put("mobile", mobile);
			data.put("email", email);
			data.put("message", message);
			
			JSONObject jbo = new JSONObject(data);
			String jbostr = jbo.toString();
			
			JSONObject jobj = api.addHotolReservation(jbostr);
			String reservationid = jobj.getString("reservtionid");
			
			if(reservationid != null)
			{
				showMark(this.getString(R.string.hotel_reservation_lable_19));
				openReservationHistoryDetailed(data,storeName);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openReservationHistoryDetailed(Map<String,Object> map,String storeName)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,ReservationHistoryDetailed.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", (String)map.get("storeid"));
			bundle.putString("storeName", storeName);
			bundle.putString("arrivalTime", (String)map.get("arrivalTime"));
			bundle.putString("checkin", (String)map.get("checkin"));
			bundle.putString("checkout", (String)map.get("checkout"));
			bundle.putString("roomtype", (String)map.get("roomtype"));
			bundle.putString("roomcount", (String)map.get("roomcount"));
			bundle.putString("total", String.valueOf((Double)map.get("total")));
			bundle.putString("paytype", (String)map.get("paytype"));
			bundle.putString("backnow", (String)map.get("backnow"));
			bundle.putString("guestname", (String)map.get("guestname"));
			bundle.putString("mobile", (String)map.get("mobile"));
			bundle.putString("email", (String)map.get("email"));
			bundle.putString("message", (String)map.get("message"));
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showRoomDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
		
		builder.setItems(R.array.roomnumber, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int roomnumber = which + 1;
				updateRoomNumber(roomnumber);
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void updateRoomNumber(int roomnumber)
	{
		RelativeLayout roomsrl = (RelativeLayout)findViewById(R.id.rooms);
		TextView firstlabel = (TextView) roomsrl.findViewById(R.id.firstlabel);
		firstlabel.setText(this.getString(R.string.hotel_reservation_lable_7));
		TextView secondlabel = (TextView) roomsrl.findViewById(R.id.secondlabel);
		secondlabel.setText(String.valueOf(roomnumber));
		
		RelativeLayout roomcountrl = (RelativeLayout)findViewById(R.id.room_count);
		loadIncludeRelativeLayout(roomcountrl,this.getString(R.string.hotel_reservation_lable_10),String.valueOf(roomnumber));
		
		RelativeLayout payrl = (RelativeLayout)findViewById(R.id.pay);
		
		totals = total;
		totals = totals * roomnumber;
		loadIncludeRelativeLayout(payrl,this.getString(R.string.hotel_reservation_lable_11),"￥"+String.valueOf(totals));
	}
	
	public void showTimeDialog(){
		//用来获取日期和时间的  
        Calendar calendar = Calendar.getInstance();   
        
        RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);

		TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
		String datestr = secondlabel2.getText().toString();
		
		String[] datestrs = datestr.split("~");
		String[] strs = datestrs[0].split(":");
		
		TimePickerDialog.OnTimeSetListener timeListener =   
            new TimePickerDialog.OnTimeSetListener() {  
                  
                @Override  
                public void onTimeSet(TimePicker timerPicker,  
                        int hourOfDay, int minute) {  
                    
                    String hodstr = frometTime(hourOfDay) + ":" + frometTime(minute);
                    
                    int hourOfDay2 = hourOfDay + 2;
                    String hodstr2 = frometTime(hourOfDay2) + ":" + frometTime(minute);
                    
                    updateReachTime(hodstr,hodstr2);
                }  
            };  
            TimePickerDialog  dialog = new TimePickerDialog(this, timeListener,  
                    Integer.valueOf(strs[0]),  
                    Integer.valueOf(strs[1]),  
                    false);   //是否为二十四制 
            dialog.show();
	}
	
	public void updateReachTime(String hodstr,String hodstr2)
	{
		RelativeLayout timesrl = (RelativeLayout)findViewById(R.id.times);
		TextView firstlabel2 = (TextView) timesrl.findViewById(R.id.firstlabel);
		firstlabel2.setText(this.getString(R.string.hotel_reservation_lable_8));
		TextView secondlabel2 = (TextView) timesrl.findViewById(R.id.secondlabel);
		secondlabel2.setText(hodstr+"~"+hodstr2);
	}
	
	public String frometTime(int time)
	{
		String str = "";
		try{
			if(time < 10)
				str = "0" + time;
			else
				str = String.valueOf(time);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
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
	
	public EditText getIncludeGuestRelativeLayout(RelativeLayout rl,String fstr,String rstr,String hintstr)
	{
		TextView firstlabel = (TextView) rl.findViewById(R.id.firstlabel);
		firstlabel.setText(fstr);
		EditText secondlabel = (EditText) rl.findViewById(R.id.secondlabel);
		if(rstr != null)
			secondlabel.setText(rstr);
		secondlabel.setHint(hintstr);
		return secondlabel;
	}
	
	/**
	  * @param args
	  */
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
	
	public static int getIntervalDays(Date enddate, Date begindate)   {  
        long millisecond = enddate.getTime() - begindate.getTime();  
        int day = (int)(millisecond / 24L / 60L / 60L / 1000L);  
        return day;  
    }  
}
