package ms.activitys.hotel;

import ms.activitys.R;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecommendMuneDialog extends Activity{

	private LinearLayout layout;
	private String isASttention;
	private String tag;
	private MyApp myapp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myapp = (MyApp)this.getApplicationContext();
		
		Bundle bunde = this.getIntent().getExtras();
		tag = bunde.getString("tag");
		if(tag.equals("tuijian"))
		{
			setContentView(R.layout.recommend_mune_dialog);
			initTuiJian();
		}
		else if(tag.equals("storetuijian"))
		{
			setContentView(R.layout.recommend_mune_dialog);
			String storeid = bunde.getString("storeid");
			initStoreTuiJian(storeid);
		}
		else if(tag.equals("UnfollowConfirm"))
		{
			setContentView(R.layout.unfollow_confirm_dialog);
		}
		else if(tag.equals("FriendUnfollowConfirm"))
		{
			setContentView(R.layout.friend_unfollow_confirm_dialog);
		}
		else if(tag.equals("chartmessageEmpty"))
		{
			setContentView(R.layout.chart_message_empty_confirm_dialog);
		}
		else if(tag.equals("momentsview"))
		{
			setContentView(R.layout.update_theme_background);
		}
		else if(tag.equals("momentsuploadview"))
		{
			setContentView(R.layout.update_moments_file);
		}
		
		//dialog=new MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.exit_layout2);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Tip: Click outside the window to close the window!", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}
	
	public void initTuiJian()
	{
		isASttention = StoreViewActivity.instance.isASttention;
		Button exitBtn2 = (Button)findViewById(R.id.exitBtn2);
		if(isASttention.equals("0"))
			exitBtn2.setText(getString(R.string.hotel_label_16));
		else
			exitBtn2.setText(getString(R.string.hotel_label_15));
	}
	
	public void initStoreTuiJian(String storeid)
	{
		Button call_store_btn = (Button)findViewById(R.id.call_store_btn);
		if(!storeid.equals(myapp.getAppstoreid()))
			call_store_btn.setVisibility(View.VISIBLE);
		else
			call_store_btn.setVisibility(View.GONE);
		isASttention = StoreMainActivity.instance.isASttention;
		Button exitBtn2 = (Button)findViewById(R.id.exitBtn2);
		if(isASttention.equals("0"))
			exitBtn2.setText(getString(R.string.hotel_label_16));
		else
			exitBtn2.setText(getString(R.string.hotel_label_15));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton0(View v) {
		this.finish();
		
	}
	
	public void exitbutton2(View v) {
		this.finish();
		if(tag.equals("tuijian"))
		{
			StoreViewActivity.instance.labeledStarredFriend();
		}
		else if(tag.equals("storetuijian"))
		{
			StoreMainActivity.instance.labeledStarredFriend();
		}
	}
	
	public void exitbutton3(View v) {
		this.finish();
		StoreViewActivity.instance.unfollowStore();
	}
	
	public void exitbutton33(View v) {
		this.finish();
		StoreMainActivity.instance.unfollowStore();
	}
	
	public void exitbutton4(View v) {
		this.finish();
		FriendInfoViewActivity.instance.unfollowFriend();
	}
	
	public void exitbutton5(View v) {
		this.finish();
		ChatMessageInfoActivity.instance.deleteChattingHistory();
	}
	
	public void exitbutton6(View v) {
		this.finish();
		MomentsActivity.instance.openImagePoto();
	}
	
	public void exitbutton7(View v) {
		this.finish();
		MomentsActivity.instance.openImageCamera();
	}
	
	public void exitbutton8(View v) {
		this.finish();
		MomentsActivity.instance.openUploadImageCamera();
	}
	
	public void exitbutton9(View v) {
		this.finish();
		MomentsActivity.instance.openUploadImagePoto();
	}
}
