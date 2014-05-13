package ms.activitys.restaurant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.vipcards.CardsView;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

public class RestaurantDestine extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String storeid;
	private String storename;
	private String [] sexitem = new String[2];
	private String [] roomitem = new String[4];
	
	private EditText sexet;
	private EditText roomet;
	private EditText ytime;
	private EditText ydate;
	private int index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant_detail);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		index = this.getIntent().getExtras().getInt("index");
		Map map = myapp.getMyCardsAll().get(index);
		
		storeid = (String)map.get("storeid");
		storename = (String)map.get("storeName");
		
		sexitem[0] = getString(R.string.male);
		sexitem[1] = getString(R.string.female);
		
		roomitem[0] = getString(R.string.room_type_1);
		roomitem[1] = getString(R.string.room_type_2);
		roomitem[2] = getString(R.string.room_type_3);
		roomitem[3] = getString(R.string.room_type_4);
		
		Button breakbtn = (Button)findViewById(R.id.break_btn);
		breakbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RestaurantDestine.this.setResult(RESULT_OK, getIntent());
				RestaurantDestine.this.finish();
			}
		});
		
		loadRestaurantView();
	}
	
	public void loadRestaurantView()
	{
		try{
			TextView nametxt = (TextView)findViewById(R.id.write_order_restaurant_name);
			nametxt.setText(storename);
			
			sexet = (EditText)findViewById(R.id.write_order_sex_et);
			sexet.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						showSexDialog();  
					}
					return false;
				}
			});
			
			ydate = (EditText)findViewById(R.id.write_order_mealdate_et);
			ydate.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						showDateSelection();
					}
					return false;
				}
			});
			
			ytime = (EditText)findViewById(R.id.write_order_mealtime_et);
			ytime.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						showTimeDialog();
					}
					return false;
				}
			});
			
			roomet = (EditText)findViewById(R.id.write_order_roomrequest_et);
			roomet.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						showRoomTypeDialog();
					}
					return false;
				}
			});
			
			Button submitbtn = (Button)findViewById(R.id.write_order_ok);
			submitbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ordersubmit();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void ordersubmit()
	{
		try{
			TextView usertxt = (TextView)findViewById(R.id.write_order_person_et);
			String user = usertxt.getText().toString();
			
			String sex = sexet.getText().toString();
			
			EditText phoneet = (EditText)findViewById(R.id.write_order_phone_et);
			String phone = phoneet.getText().toString();
			
			String datestr = ydate.getText().toString();
			String timestr = ytime.getText().toString();
			
			EditText mublet = (EditText)findViewById(R.id.write_order_totalperson_et);
			String muble = mublet.getText().toString();
			
			String roomtype = roomet.getText().toString();
			
			EditText contentet = (EditText)findViewById(R.id.write_order_other_et);
			String content = contentet.getText().toString();
			
			boolean b = true;
			if(user.equals(""))
			{
				b = false;
				makeText(getString(R.string.order_null_error_notice));
			}
			else if(sex.equals(""))
			{
				b = false;
				makeText(getString(R.string.sex_null_error_notice));
			}
			else if(phone.equals(""))
			{
				b = false;
				makeText(getString(R.string.phonenum_null_error_notice));
			}
			else if(datestr.equals(""))
			{
				b = false;
				makeText(getString(R.string.meal_time_null_error_notice));
			}
			else if(timestr.equals(""))
			{
				b = false;
				makeText(getString(R.string.meal_date_null_error_notice));
			}
			else if(muble.equals(""))
			{
				b = false;
				makeText(getString(R.string.count_people_null_error_notice));
			}
			else if(roomtype.equals(""))
			{
				b = false;
				makeText(getString(R.string.room_type_null_error_notice));
			}
			
			
			if(b)
			{
				JSONObject job = api.saveOrUpdateOrders(storeid,user,sex,phone,datestr+" "+timestr+":00",muble,roomtype,content);
				if(job != null)
				{
					if(job.getString("success").equals("true"))
					{
						makeText(getString(R.string.order_restaurant_success));
						Intent intent = new Intent();
					    intent.setClass( this,CardsView.class);
					    Bundle bundle = new Bundle();
						bundle.putString("index", String.valueOf(index));
						intent.putExtras(bundle);
					    startActivity(intent);//开始界面的跳转函数
					    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
					    this.finish();
					}
					else
					{
						makeText(getString(R.string.order_restaurant_error));
					}
				}
				else
				{
					makeText(getString(R.string.order_restaurant_error));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showSexDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(sexitem, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String sex = sexitem[which];
				sexet.setText(sex);
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showRoomTypeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(roomitem, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String room = roomitem[which];
				roomet.setText(room);
			}
			
			
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showDateSelection()
	{
		try{
			final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			
			View view = LayoutInflater.from(this).inflate(R.layout.datepicker_view,null);
			
			RadioButton b1 = (RadioButton)view.findViewById(R.id.b1);
			b1.setVisibility(View.GONE);
			RadioButton b2 = (RadioButton)view.findViewById(R.id.b2);
			b2.setVisibility(View.GONE);
			
			String dstr = ydate.getText().toString();
			
			final DatePicker dp = (DatePicker)view.findViewById(R.id.date_picker);
			if(dstr != null && !dstr.equals(""))
			{
				String [] strs = dstr.split("-");
				int y = Integer.valueOf(strs[0]);
				int m = Integer.valueOf(strs[1]);
				int d = Integer.valueOf(strs[2]);
				
				dp.init(y,m-1,d, null);
			}
			
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
					ydate.setText(datestr);
					myDialog.dismiss();
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
	
	public void showTimeDialog(){
		//用来获取日期和时间的  
        Calendar calendar = Calendar.getInstance();   
        
		String datestr = ytime.getText().toString();
		
		String[] strs = new String[2];
		if(datestr.equals(""))
		{
			strs[0] = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));  
			strs[1] = String.valueOf(calendar.get(Calendar.MINUTE));  
		}
		else
		{
			strs = datestr.split(":");
		}
		
		
		TimePickerDialog.OnTimeSetListener timeListener =   
            new TimePickerDialog.OnTimeSetListener() {  
                  
                @Override  
                public void onTimeSet(TimePicker timerPicker,  
                        int hourOfDay, int minute) {  
                    
                    String hodstr = frometTime(hourOfDay) + ":" + frometTime(minute);
                    ytime.setText(hodstr);
                }  
            };  
            TimePickerDialog  dialog = new TimePickerDialog(this, timeListener,  
                    Integer.valueOf(strs[0]),  
                    Integer.valueOf(strs[1]),  
                    false);   //是否为二十四制 
            dialog.show();
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
