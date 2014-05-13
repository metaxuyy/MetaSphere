package ms.activitys.hotel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostTextActivity extends Activity {
	private MyApp myapp;
	private Douban api;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;

	private EditText textET;
	private Button cancelBtn;
	private Button saveBtn;
	
	private String publishTypeID;// 发布的公告栏类型
	
	private String type;// 列表类型
	private String savedate;
	private String pkid;
	private static SharedPreferences share;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posttext);
		
		Bundle bunde = this.getIntent().getExtras();
		type = bunde.getString("type");

		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		db = new DBHelperMessage(this, myapp);

		cancelBtn = (Button) findViewById(R.id.postText_cancelBtn);
		saveBtn = (Button) findViewById(R.id.postText_saveBtn);
		textET = (EditText) findViewById(R.id.postTextET);

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PostTextActivity.this, MomentsActivity.class);
				startActivity(intent);// 开始界面的跳转函数
				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
				finish();
			}
		});

		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发表文字
				uploadMomentsImage();
			}
		});

		textET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				saveBtn.setEnabled(true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void uploadMomentsImage() {
		if (myapp.getIsServer()) {
			publishTypeID = type;
			saveMoments();
		} else {
			publishTypeID = "";
			saveMoments();
		}
	}

	public void saveMoments() {
		try {
			showMyLoadingDialog(getString(R.string.user_info_lable_1));
			final String context = textET.getText().toString();
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;

					boolean b = updateUserImg(context);
					Map<String, Object> map = new HashMap<String, Object>();
					if(type == null || type.equals(""))
					{
						if (b) {
							Map<String, Object> maps = new HashMap<String, Object>();
							maps.put("pkid", pkid);
							maps.put("content", context);
							maps.put("publishtime", myapp.getInterval(savedate));
							maps.put("publishuser", myapp.getUserName());
							maps.put("publishid", myapp.getPfprofileId());
							maps.put("publishtype", publishTypeID);
							maps.put("publishusertype", myapp.getIsServer() ? "1": "2");
	
							List<Map<String, String>> files = new ArrayList<Map<String, String>>();
							maps.put("filelist", files);
//							List<Map<String, Object>> dlist = new ArrayList<Map<String, Object>>();
//							dlist.add(maps);
//							db.openDB();
//							db.saveMomentsAll(dlist);
//							db.closeDB();
							map.put("mdata", maps);
						}
					}
					map.put("tag", b);
					// map.put("savedata", maps);
					msg.obj = map;
					handler.sendMessage(msg);
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				boolean b = (Boolean) map.get("tag");
				if (b) {
//					MomentsActivity.instance.showRefreshLayout();
//					MomentsActivity.instance.getMomentsDataAll3();
					if(map.containsKey("mdata"))
					{
						Map<String,Object> maps = (Map<String,Object>)map.get("mdata");
						MomentsActivity.instance.changPenyouDataList(maps);
					}
					if (loadDialog != null) {
						if (loadDialog.isShowing())
							loadDialog.dismiss();
					}
					openMomentsView();
				}
				break;
			}
		}
	};
	
	public void openMomentsView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MomentsActivity.class);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean updateUserImg(String context)
 	{
 		boolean tag = true;
 		try{
// 			showProgressDialog();
 			Map<String, String> params = new HashMap<String, String>();
 			params.put("pfid", myapp.getPfprofileId());
 			params.put("context", context);
 			params.put("publishtype", publishTypeID);
			params.put("publishusertype", myapp.getIsServer() ? "1" : "2");
			params.put("storeid", myapp.getAppstoreid());
			params.put("businessid", myapp.getBusinessid());
			params.put("position", "");
 			long fsize = 0;
 			Map<String, File> files = new HashMap<String, File>();
 			
 			if(tag)
 			{
 				JSONObject job = api.updateMomentsImg(params,files);
 				if(job != null)
 				{
 					if (job.getString("success").equals("true")) {
// 						myapp.setUserimg(job.getString("imgurl"));
// 						mypDialog.dismiss();
 						savedate = job.getString("savedate");
 						pkid = job.getString("pkid");
 						tag = true;
// 						makeText("头像更新成功！");
 					} else {
 						String msg = (String)job.get("msg");
// 						mypDialog.dismiss();
 						tag = false;
// 						makeText("失败原因："+msg);
 					}
 				}
 				else
 				{
 					loadDialog.dismiss();
 					tag = false;
 					Toast.makeText(PostTextActivity.this, "发布失败！",
							Toast.LENGTH_SHORT).show();
 				}
 			}
 		}catch(Exception ex){
 			ex.printStackTrace();
 			tag = false;
 		}
 		return tag;
 	}
	
	public void showMyLoadingDialog(String message)
    {
    	loadDialog = new MyLoadingDialog(this, message,R.style.MyDialog);
    	loadDialog.show();
    }
}
