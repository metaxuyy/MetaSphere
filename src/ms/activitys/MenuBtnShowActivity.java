package ms.activitys;

import org.json.JSONException;
import org.json.JSONObject;

import ms.activitys.hotel.SelImageActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuBtnShowActivity extends Activity{
	
	private Button backBtn;
	private TextView btnNameTV;
	private ImageView btnImg;
	private TextView btnInfoTV;
	private Button opeBtn;
	
	private String menuName;
	private String menuID;
	private String menuSkipID;
	private String menuInfo;
	private String tag;
	private Bitmap icon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menubtnshow);
		
		Bundle bundle = this.getIntent().getExtras();
		menuName = bundle.getString("menuName");
		menuID = bundle.getString("menuID");
		menuSkipID = bundle.getString("menuSkipID");
		menuInfo = bundle.getString("menuInfo");
		tag = bundle.getString("tag");
		icon = bundle.getParcelable("menuIcon");
		
		initView();
	}
	
	
	private void initView(){
		backBtn = (Button) findViewById(R.id.menubtn_show_backBtn);
		btnNameTV = (TextView) findViewById(R.id.menubtn_show_TV);
		btnImg = (ImageView) findViewById(R.id.menubtn_show_Img);
		btnInfoTV = (TextView) findViewById(R.id.menubtn_show_infoTV);
		opeBtn = (Button) findViewById(R.id.menubtn_show_BTN);
		
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnNameTV.setText(menuName);
		btnImg.setImageBitmap(icon);
		btnInfoTV.setText(menuInfo);
		if(tag.equals("isHad")){
			opeBtn.setBackgroundResource(R.drawable.button_red_ico);
			opeBtn.setText("从页面功能删除");
		}else if(tag.equals("isList")){
			opeBtn.setBackgroundResource(R.drawable.button_green_ico);
			opeBtn.setText("添加为页面功能");
		}
		
		opeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject jObj = new JSONObject();
				try {
					jObj.put("tag", tag);
					jObj.put("menuID", menuID);
					jObj.put("menuSkipID", menuSkipID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				MenuSetActivity.instance.updateMenuBtnShow(jObj);
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
			}
		});
	}
}
