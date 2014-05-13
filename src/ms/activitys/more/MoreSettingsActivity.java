package ms.activitys.more;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MoreSettingsActivity extends Activity {
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private ProgressDialog mypDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_paw_weibo);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = MoreSettingsActivity.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		String tag = this.getIntent().getExtras().getString("tag");
		
		loadPage(tag);
	}
	
	public void loadPage(String tag)
	{
		try{
			if(tag.equals("uppaw"))
			{
				TextView txt = (TextView)findViewById(R.id.TextView01);
				txt.setText(this.getString(R.string.seting_lable_1));
				updatePassword();
			}
			else if(tag.equals("weibo"))
			{
				TextView txt = (TextView)findViewById(R.id.TextView01);
				txt.setText(this.getString(R.string.seting_lable_2));
				
				bindingWeibo();
			}
			else if(tag.equals("setting"))
			{
				TextView txt = (TextView)findViewById(R.id.TextView01);
				txt.setText(this.getString(R.string.seting_lable_3));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showMainView();
//			return false;
		}
		return false;
	}
	
	public void showMainView()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass( this,MainTabActivity.class);
//		bundle.putInt("index", index);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void updatePassword()
	{
		try{
			RelativeLayout layout = (RelativeLayout)findViewById(R.id.paw_layout);
			layout.setVisibility(View.VISIBLE);
			
			final EditText oldpawet = (EditText)findViewById(R.id.oldpassword);
			
			final EditText paw = (EditText)findViewById(R.id.password);
			
			final EditText paw2 = (EditText)findViewById(R.id.password2);
			
			Button break_btn = (Button)findViewById(R.id.break_btn);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showMainView();
				}
			});
			
			Button btn = (Button)findViewById(R.id.btn_confirm);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProgressDialog();
					String oldpaw = oldpawet.getText().toString();
					String pawstr = paw.getText().toString();
					String pawstr2 = paw2.getText().toString();
					boolean b = true;
					
					if(oldpaw == null || oldpaw.equals(""))
					{
						b = false;
						makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_4));
					}
					else if(pawstr == null || pawstr.equals(""))
					{
						b = false;
						makeText(MoreSettingsActivity.this.getString(R.string.login_lable_10));
					}
					else if(pawstr2 == null || pawstr2.equals(""))
					{
						b = false;
						makeText(MoreSettingsActivity.this.getString(R.string.login_lable_11));
					}
					else if(!pawstr2.equals(pawstr))
					{
						b = false;
						makeText(MoreSettingsActivity.this.getString(R.string.login_lable_12));
					}
					
					JSONObject jobj;
					U.dout(0);
					
					if(b)
					{
						try {
							jobj = api.updateUserPassword(oldpaw,pawstr);
							String tag = (String)jobj.get("success");
							if(tag.equals("true"))
							{
								b = true;
								makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_5));
							}
							else
							{
								b = false;
								String msg = (String)jobj.get("msg");
								makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_6) + msg);
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							b = false;
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							b = false;
							e.printStackTrace();
						}
					}
					
					mypDialog.dismiss();
					
					if(b)
					{
						share.edit().putString("pwa", pawstr).commit();
						MoreSettingsActivity.this.setResult(RESULT_OK, getIntent());
						MoreSettingsActivity.this.finish();
					}
				}
			});
			
//			Button btn2 = (Button)findViewById(R.id.btn_cancel);
//			btn2.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					MoreSettingsActivity.this.setResult(RESULT_OK, getIntent());
//					MoreSettingsActivity.this.finish();
//				}
//			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onRestart() {
	// TODO Auto-generated method stub
		try{
			weiboBinding();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		super.onPause();
	}
	
	public void bindingWeibo()
	{
		try{
			RelativeLayout layout = (RelativeLayout)findViewById(R.id.weibo_layout);
			layout.setVisibility(View.VISIBLE);
			
			weiboBinding();
			
			final LinearLayout binding_sina = (LinearLayout)findViewById(R.id.binding_sina);// 新浪微博绑定
			binding_sina.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(MoreSettingsActivity.this))
					{
						if(share.getBoolean("isBindingSina", false))
						{
							notBinding("sina");
						}
						else
						{
							bindingSinaWeibo();
						}
					}
					else
						makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			final LinearLayout binding_renren = (LinearLayout)findViewById(R.id.binding_renren);// 人人网绑定
			binding_renren.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(MoreSettingsActivity.this))
					{
						if(share.getBoolean("isBindingRenren", false))
						{
							notBinding("renren");
						}
						else
							bindingRenren();
					}
					else
						makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			final LinearLayout binding_qq = (LinearLayout)findViewById(R.id.binding_qq);// 腾讯微博绑定
			binding_qq.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(MoreSettingsActivity.this))
					{
						if(share.getBoolean("isBindingQq", false))
						{
							notBinding("qq");
						}
						else
							bindingQQ();
					}
					else
						makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			final LinearLayout binding_kx = (LinearLayout)findViewById(R.id.binding_kx);// 开心网绑定
			binding_kx.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(StreamTool.isNetworkVailable(MoreSettingsActivity.this))
					{
						if(share.getBoolean("isBindingkx", false))
						{
							notBinding("kx");
						}
						else
							bindingKx();
					}
					else
						makeText(MoreSettingsActivity.this.getString(R.string.seting_lable_7));
				}
			});
			
			Button btn2 = (Button)findViewById(R.id.btn_cancel2);
			btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MoreSettingsActivity.this.setResult(RESULT_OK, getIntent());
					MoreSettingsActivity.this.finish();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void weiboBinding()
	{
		ImageView img = (ImageView)findViewById(R.id.image_sina);
		if(share.getBoolean("isBindingSina",false))
		{
			img.setImageResource(R.drawable.ic_sina_press_off);
		}
		else
		{
			img.setImageResource(R.drawable.ic_sina_press_on);
		}
		
		ImageView img2 = (ImageView)findViewById(R.id.image_renren);
		if(share.getBoolean("isBindingRenren",false))
		{
			img2.setImageResource(R.drawable.ic_renren_press_off);
		}
		else
		{
			img2.setImageResource(R.drawable.ic_renren_press_on);
		}
		
		ImageView img3 = (ImageView)findViewById(R.id.image_qq);
		if(share.getBoolean("isBindingQq",false))
		{
			img3.setImageResource(R.drawable.ic_tecent_press_off);
		}
		else
		{
			img3.setImageResource(R.drawable.ic_tecent_press_on);
		}
		
		ImageView img4 = (ImageView)findViewById(R.id.image_kx);
		if(share.getBoolean("isBindingkx",false))
		{
			img4.setImageResource(R.drawable.ic_kaixin_press_off);
		}
		else
		{
			img4.setImageResource(R.drawable.ic_kaixin_press_on);
		}
	}
	
	public void notBinding(final String tag)
	{
		new AlertDialog.Builder(this).setTitle(this.getString(R.string.setting_title))
		.setMessage(this.getString(R.string.seting_lable_8)).setIcon(R.drawable.error2)
		.setPositiveButton(this.getString(R.string.coupon_lable_14),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						if(tag.equals("sina"))
						{
							myapp.setBindingSina(false);
							share.edit().putString("sina_token", "").putString("sina_secret", "").putString("sina_userId", "").putBoolean("isBindingSina", false).commit();
							weiboBinding();
						}
						else if(tag.equals("renren"))
						{
							share.edit().putString("accessTokenRenren", "").putBoolean("isBindingRenren", false).commit();
							weiboBinding();
						}
						else if(tag.equals("qq"))
						{
							share.edit().putString("accessTokenQq", "").putString("accessTokenSecret", "").putString("name", "").putBoolean("isBindingQq", false).commit();
							weiboBinding();
						}
						else if(tag.equals("kx"))
						{
							share.edit().putString("accessTokenkx", "").putString("accessTokenSecretkx", "").putBoolean("isBindingkx", false).commit();
							weiboBinding();
						}
						dialog.dismiss();
					}
				}).setNegativeButton(this.getString(R.string.coupon_lable_16),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// 取消按钮事件
					}
				}).show();
	}
	
	public void bindingKx()
	{
//		//初始化ConfigUtil信息
//		Kaixin kaixin = Kaixin.getInstance();
//		String[] permissions = {"basic",
//				"friends_records",
//				"create_records", 
//				"user_records",
//				"create_rgroup",
//				"user_rgroup",
//				"create_talk",
//				"create_repaste",
//				"user_repaste",
//				"friends_repaste",
//				"create_album",
//				"user_photo",
//				"friends_photo",
//				"upload_photo",
//				"send_message",
//				"user_messagebox",
//				"user_birthday",
//				"friends_birthday",
//				"user_bodyform",
//				"friends_bodyform",
//				"user_blood",
//				"friends_blood",
//				"user_marriage",
//				"friends_marriage",
//				"user_intro",
//				"friends_intro",
//				"user_education",
//				"friends_education",
//				"user_career",
//				"friends_career"
//				};
//		String url = "";
//		try {
//			if (kaixin.getRequestToken(MoreSettingsActivity.this,
//			"http://www.17k.com", permissions)) {
//				url = Kaixin.KX_AUTHORIZE_URL
//				+ "?oauth_token=" + kaixin.getRequestToken()
//				+ "&oauth_client=1";
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "kx");
//		bundle.putString("url", url);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingSinaWeibo()
	{
//		Intent intent = new Intent();
//	    intent.setClass( this,AndroidSinaExample.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "sina");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingRenren()
	{
//		Intent intent = new Intent();
//	    intent.setClass( this,OAuthRenRen.class);
//	    Bundle bundle = new Bundle();
////		bundle.putString("tag", "sina");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void bindingQQ()
	{
		//初始化ConfigUtil信息
//		ConfigUtil conf = ConfigUtil.getInstance();
//		String curWeibo = String.valueOf(ConfigUtil.QQW);
//		conf.setCurWeibo(curWeibo);
//		conf.initQqData();
//		
//		Intent intent = new Intent();
//	    intent.setClass( this,AuthorizationAct.class);
//	    Bundle bundle = new Bundle();
//		bundle.putString("tag", "qq");
////		bundle.putString("storeName", storeName);
//		intent.putExtras(bundle);
//	    startActivity(intent);//开始界面的跳转函数
//	    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showProgressDialog(){
		try{
			mypDialog=new ProgressDialog(this);
            //实例化
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置进度条风格，风格为圆形，旋转的
//            mypDialog.setTitle("等待");
            //设置ProgressDialog 标题
            mypDialog.setMessage(this.getString(R.string.login_lable_21));
            //设置ProgressDialog 提示信息
//            mypDialog.setIcon(R.drawable.wait_icon);
            //设置ProgressDialog 标题图标
//            mypDialog.setButton("",this);
            //设置ProgressDialog 的一个Button
            mypDialog.setIndeterminate(false);
            //设置ProgressDialog 的进度条是否不明确
            mypDialog.setCancelable(true);
            //设置ProgressDialog 是否可以按退回按键取消
            mypDialog.show();
            //让ProgressDialog显示
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
