package ms.activitys.hotel;

import ms.activitys.R;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessageBackgroundActivity extends Activity{

	private Douban api;
	private MyApp myapp;
	private ProgressDialog mypDialog;
	private SharedPreferences  share;
	
	private TextView bg_txt;
	private TextView bg_txt2;
	private TextView bg_txt3;
	private TextView bg_txt4;
	private TextView bg_txt5;
	private TextView bg_txt6;
	private TextView bg_txt7;
	private TextView bg_txt8;
	private TextView bg_txt9;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION); 
        setContentView(R.layout.message_background_view);
        
        share = getSharedPreferences("perference", MODE_PRIVATE);
        api = new Douban(share,myapp);
        myapp = (MyApp)this.getApplicationContext();
        
        initView();
	}
	
	public void initView()
	{
		try{
			bg_txt = (TextView)findViewById(R.id.bg_txt);
			bg_txt2 = (TextView)findViewById(R.id.bg_txt2);
			bg_txt3 = (TextView)findViewById(R.id.bg_txt3);
			bg_txt4 = (TextView)findViewById(R.id.bg_txt4);
			bg_txt5 = (TextView)findViewById(R.id.bg_txt5);
			bg_txt6 = (TextView)findViewById(R.id.bg_txt6);
			bg_txt7 = (TextView)findViewById(R.id.bg_txt7);
			bg_txt8 = (TextView)findViewById(R.id.bg_txt8);
			bg_txt9 = (TextView)findViewById(R.id.bg_txt9);
			
			String fileimg = share.getString("bg_img", "");
			if(!fileimg.equals(""))
			{
	        	if(!fileimg.contains("sdcard"))
	        	{
	        		selectBg2(Integer.valueOf(fileimg));
	        	}
			}
			Button break_btn = (Button)findViewById(R.id.break_btn);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openMessageDetailForm();
				}
			});
			
			FrameLayout flyout = (FrameLayout)findViewById(R.id.select_bg);
			flyout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(1);
					selectBg2(1);
				}
			});
			FrameLayout flyout2 = (FrameLayout)findViewById(R.id.select_bg2);
			flyout2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(2);
					selectBg2(2);
				}
			});
			FrameLayout flyout3 = (FrameLayout)findViewById(R.id.select_bg3);
			flyout3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(3);
					selectBg2(3);
				}
			});
			FrameLayout flyout4 = (FrameLayout)findViewById(R.id.select_bg4);
			flyout4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(4);
					selectBg2(4);
				}
			});
			FrameLayout flyout5 = (FrameLayout)findViewById(R.id.select_bg5);
			flyout5.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(5);
					selectBg2(5);
				}
			});
			FrameLayout flyout6 = (FrameLayout)findViewById(R.id.select_bg6);
			flyout6.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(6);
					selectBg2(6);
				}
			});
			FrameLayout flyout7 = (FrameLayout)findViewById(R.id.select_bg7);
			flyout7.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(7);
					selectBg2(7);
				}
			});
			FrameLayout flyout8 = (FrameLayout)findViewById(R.id.select_bg8);
			flyout8.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(8);
					selectBg2(8);
				}
			});
			FrameLayout flyout9 = (FrameLayout)findViewById(R.id.select_bg9);
			flyout9.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectBg(9);
					selectBg2(9);
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void selectBg(int tag)
	{
		try{
			Intent intent = new Intent();
			intent.putExtra("tag",tag);
			intent.setAction("CHANGE_BACKGROUND");
			sendBroadcast(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void selectBg2(int tag)
	{
		try{
			changeTextViewAll();
			switch (tag) {
			case 1:
				bg_txt.setBackgroundDrawable(null);
				bg_txt.setText("");
				bg_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 2:
				bg_txt2.setBackgroundDrawable(null);
				bg_txt2.setText("");
				bg_txt2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 3:
				bg_txt3.setBackgroundDrawable(null);
				bg_txt3.setText("");
				bg_txt3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 4:
				bg_txt4.setBackgroundDrawable(null);
				bg_txt4.setText("");
				bg_txt4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 5:
				bg_txt5.setBackgroundDrawable(null);
				bg_txt5.setText("");
				bg_txt5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 6:
				bg_txt6.setBackgroundDrawable(null);
				bg_txt6.setText("");
				bg_txt6.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 7:
				bg_txt7.setBackgroundDrawable(null);
				bg_txt7.setText("");
				bg_txt7.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 8:
				bg_txt8.setBackgroundDrawable(null);
				bg_txt8.setText("");
				bg_txt8.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			case 9:
				bg_txt9.setBackgroundDrawable(null);
				bg_txt9.setText("");
				bg_txt9.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.confirm_dialog_successful_icon, 0);
				break;
			default:
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void changeTextViewAll()
	{
		try{
			bg_txt.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt.setText("选择");
			bg_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt2.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt2.setText("选择");
			bg_txt2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt3.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt3.setText("选择");
			bg_txt3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt4.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt4.setText("选择");
			bg_txt4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt5.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt5.setText("选择");
			bg_txt5.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt6.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt6.setText("选择");
			bg_txt6.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt7.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt7.setText("选择");
			bg_txt7.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt8.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt8.setText("选择");
			bg_txt8.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			
			bg_txt9.setBackgroundResource(R.drawable.app_msg_item_source_from_pressed);
			bg_txt9.setText("选择");
			bg_txt9.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMessageDetailForm();
			return false;
		}
		return false;
	}
	
	public void openMessageDetailForm()
	{
		Intent intent = new Intent();
	    intent.setClass( this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
