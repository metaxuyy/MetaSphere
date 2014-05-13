package ms.activitys.hotel;

import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserSettingActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	public String tag;
	private TextView title_lable;
	private LinearLayout user_name_layout;
	private EditText user_name_edit;
	private LinearLayout account_layout;
	private EditText account_edit;
	private MyLoadingDialog loadDialog;
	private LinearLayout sex_layout;
	private ImageView img_1;
	private ImageView img_2;
	private LinearLayout area_layout;
	private EditText area_edit;
	private LinearLayout gexin_layout;
	private EditText gexin_edit;
	private int maxLen = 20;
	private int maxLen2 = 30;
	private DBHelperLogin db;
	private DBHelperMessage dbm;
	private String groupName;
	private EditText group_name_edit;
	private String groupid;
	
	private boolean isMale = true; //性别记录标记
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setting_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		title_lable = (TextView)findViewById(R.id.title_lable);
		
		db = new DBHelperLogin(this);
		dbm = new DBHelperMessage(this, myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		tag = bunde.getString("tag");
		if(tag.equals("username"))
		{
			initUserNameView();
		}
		else if(tag.equals("account"))
		{
			initAccountView();
		}
		else if(tag.equals("sex"))
		{
			initSexView();
		}
		else if(tag.equals("area"))
		{
			initAreaView();
		}
		else if(tag.equals("signature"))
		{
			initSignatureView();
		}
		else if(tag.equals("groupname"))
		{
			groupName = bunde.getString("groupname");
			groupid = bunde.getString("groupid");
			initGroupNameView();
		}
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openGuestInfo();
			}
		});
	}
	
	public  void initUserNameView()
	{
		try{
			title_lable.setText(getString(R.string.hotel_label_82));
			user_name_layout = (LinearLayout)findViewById(R.id.user_name_layout);
			user_name_layout.setVisibility(View.VISIBLE);
			
			user_name_edit = (EditText)findViewById(R.id.user_name_edit);
			user_name_edit.setText(myapp.getUserName());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public  void initAccountView()
	{
		try{
			title_lable.setText(getString(R.string.hotel_label_84));
			account_layout = (LinearLayout)findViewById(R.id.account_layout);
			account_layout.setVisibility(View.VISIBLE);
			
			account_edit = (EditText)findViewById(R.id.account_edit);
			account_edit.setText(myapp.getMyaccount());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public  void initSexView()
	{
		try{
			title_lable.setText(getString(R.string.hotel_label_86));
			sex_layout = (LinearLayout)findViewById(R.id.sex_layout);
			sex_layout.setVisibility(View.VISIBLE);
			
			img_1 = (ImageView)findViewById(R.id.img_1);
			img_2 = (ImageView)findViewById(R.id.img_2);
			if(myapp.getMysex().equals("0"))
			{
				img_1.setImageResource(R.drawable.sns_shoot_select_normal);
				img_2.setImageResource(R.drawable.sns_shoot_select_checked);
				isMale = false;
			}
			else
			{
				img_1.setImageResource(R.drawable.sns_shoot_select_checked);
				img_2.setImageResource(R.drawable.sns_shoot_select_normal);
				isMale = true;
			}
			
			img_1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img_1.setImageResource(R.drawable.sns_shoot_select_checked);
					img_2.setImageResource(R.drawable.sns_shoot_select_normal);
					isMale = true;
				}
			});
			
			img_2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img_2.setImageResource(R.drawable.sns_shoot_select_checked);
					img_1.setImageResource(R.drawable.sns_shoot_select_normal);
					isMale = false;
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public  void initAreaView()
	{
		try{
			title_lable.setText(getString(R.string.hotel_label_87));
			area_layout = (LinearLayout)findViewById(R.id.area_layout);
			area_layout.setVisibility(View.VISIBLE);
			
			area_edit = (EditText)findViewById(R.id.area_edit);
			area_edit.setText(myapp.getMyarea());
			
			area_edit.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					Editable editable = area_edit.getText();  
			        int len = editable.length();  
			          
			        if(len > maxLen)  
			        {  
			            int selEndIndex = Selection.getSelectionEnd(editable);  
			            String str = editable.toString();  
			            //截取新字符串  
			            String newStr = str.substring(0,maxLen);  
			            area_edit.setText(newStr);  
			            editable = area_edit.getText();  
			              
			            //新字符串的长度  
			            int newLen = editable.length();  
			            //旧光标位置超过字符串长度  
			            if(selEndIndex > newLen)  
			            {  
			                selEndIndex = editable.length();  
			            }  
			            //设置新光标所在的位置  
			            Selection.setSelection(editable, selEndIndex);  
			              
			        }  
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void initSignatureView()
	{
		try{
			title_lable.setText(getString(R.string.hotel_label_88));
			gexin_layout = (LinearLayout)findViewById(R.id.gexin_layout);
			gexin_layout.setVisibility(View.VISIBLE);
			
			gexin_edit = (EditText)findViewById(R.id.gexin_edit);
			gexin_edit.setText(myapp.getMySignature());
			
			gexin_edit.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					Editable editable = gexin_edit.getText();  
			        int len = editable.length();  
			          
			        if(len > maxLen2)  
			        {  
			            int selEndIndex = Selection.getSelectionEnd(editable);  
			            String str = editable.toString();  
			            //截取新字符串  
			            String newStr = str.substring(0,maxLen2);  
			            gexin_edit.setText(newStr);  
			            editable = gexin_edit.getText();
			              
			            //新字符串的长度  
			            int newLen = editable.length();  
			            //旧光标位置超过字符串长度  
			            if(selEndIndex > newLen)  
			            {  
			                selEndIndex = editable.length();  
			            }  
			            //设置新光标所在的位置  
			            Selection.setSelection(editable, selEndIndex);  
			              
			        }  
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void saveData(View v){
		try{
			if(tag.equals("username"))
			{
				String username = user_name_edit.getText().toString();
				upadateUserInfo(username,"","","","");
			}
			else if(tag.equals("account"))
			{
				String account = account_edit.getText().toString();
				upadateUserInfo("",account,"","","");
			}
			else if(tag.equals("sex"))
			{
				//Drawable drawble = getResources().getDrawable(R.drawable.sns_shoot_select_checked);
				//if(img_1.getDrawable().equals(drawble))
				if(isMale)
					upadateUserInfo("","","1","","");
				else
					upadateUserInfo("","","0","","");
			}
			else if(tag.equals("area"))
			{
				String area = area_edit.getText().toString();
				upadateUserInfo("","","",area,"");
			}
			else if(tag.equals("signature"))
			{
				String gexin = gexin_edit.getText().toString();
				upadateUserInfo("","","","",gexin);
			}
			else if(tag.equals("groupname"))
			{
				String gname = group_name_edit.getText().toString();
				upadateGroupName(gname);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void initGroupNameView()
	{
		try{
			LinearLayout group_name_layout = (LinearLayout)findViewById(R.id.group_name_layout);
			group_name_layout.setVisibility(View.VISIBLE);
			
			group_name_edit = (EditText)findViewById(R.id.group_name_edit);
			group_name_edit.setText(groupName);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void upadateUserInfo(final String username,final String account,final String sex,final String area,final String signature)
    {
    	showMyLoadingDialog();
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					boolean b = false;
					JSONObject jobj = api.upadateUserInfo(username, account, sex, area, signature);
					if(jobj != null)
					{
						String tags = jobj.getString("tag");
						if(tags.equals("success"))
						{
							if(tag.equals("username"))
							{
								myapp.setUserName(username);
								saveSharedPerferences("username",username);
							}
							else if(tag.equals("account"))
							{
								String user = share.getString("user", "");
								String pwa = share.getString("pwa", "");
								String id = db.getLoginId(user, pwa);
								myapp.setMyaccount(account);
								Editor editor = share.edit();// 取得编辑器
								editor.putString("user", account);
								editor.commit();// 提交刷新数据
								db.updateLogin(account, id);
							}
							else if(tag.equals("sex"))
							{
								myapp.setMysex(sex);
								saveSharedPerferences("sex",sex);
							}
							else if(tag.equals("area"))
							{
								myapp.setMyarea(area);
								saveSharedPerferences("area",area);
							}
							else if(tag.equals("signature"))
							{
								myapp.setMySignature(signature);
								saveSharedPerferences("signature",signature);
							}
							b = true;
						}
					}
					
					msg.obj = b;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
				}
				handler.sendMessage(msg);
			}
		}.start();
    }
	
	public void upadateGroupName(final String gname)
    {
    	showMyLoadingDialog();
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					boolean b = false;
					JSONObject jobj = api.updateGroupName(groupid,gname);
					if(jobj != null)
					{
						String tags = jobj.getString("tag");
						if(tags.equals("success"))
						{
							b = true;
							dbm.openDB();
							dbm.updateNewMessageData(gname,groupid);
							dbm.closeDB();
							groupName = gname;
						}
					}
					
					msg.obj = b;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
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
				boolean b = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(b)
				{
					openGuestInfo();
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				break;
			case 1:
				boolean bb = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(bb)
				{
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.getMyCardListData(null);
					if(ChatMessageInfoActivity.instance != null)
						ChatMessageInfoActivity.instance.setGroupName(groupName);
					if(MessageListActivity.instance != null)
						MessageListActivity.instance.setGroupName(groupName);
					openGuestInfo();
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				break;
			}
		}
	};
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openGuestInfo();
			return false;
		}
		return false;
	}
	
	public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器
		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		editor.commit();// 提交刷新数据
	}
	
	public void openGuestInfo()
	{
		if(tag.equals("groupname"))
		{
			Intent intent = new Intent();
		    intent.setClass( this,ChatMessageInfoActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}
		else
		{
			Intent intent = new Intent();
		    intent.setClass( this,GuestInfoActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}
	}
}
