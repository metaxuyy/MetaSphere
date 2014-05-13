package ms.activitys.hotel;


import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.coupon.MyCouponView;
import ms.globalclass.StreamTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainTopRightDialog extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_top_right_dialog);
		//dialog=new MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.main_dialog_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	public void showCoupe(View v)
	{
		finish();
		if(StreamTool.isNetworkVailable(this))
		{
			HotelActivity.instance.showDownloadCouponView();
		}
		else
			makeText(this.getString(R.string.seting_lable_7));
	}
	public void showHone(View v)
	{
		finish();
		HotelActivity.instance.openHoneView();
	}
	public void showMap(View v)
	{
		finish();
		if(StreamTool.isNetworkVailable(this))
			HotelActivity.instance.showStopAddress();
		else
			makeText(this.getString(R.string.seting_lable_7));
	}
	public void showLocal(View v)
	{
		finish();
		if(StreamTool.isNetworkVailable(this))
			HotelActivity.instance.showPeripherMap();
		else
			makeText(this.getString(R.string.seting_lable_7));
	}
	public void showTrain(View v)
	{
		finish();
		if(StreamTool.isNetworkVailable(this))
			HotelActivity.instance.showTrafficView();
		else
			makeText(this.getString(R.string.seting_lable_7));
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	/*
	public void exitbutton1(View v) {  
    	this.finish();    	
      }  
	public void exitbutton0(View v) {  
    	this.finish();
    	MainWeixin.instance.finish();//关闭Main 这个Activity
      }  
	*/
}
