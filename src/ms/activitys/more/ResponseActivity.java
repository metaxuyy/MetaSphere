package ms.activitys.more;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.PostTextActivity;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResponseActivity extends Activity {
	private MyApp myapp;
	private Douban api;
	private MyLoadingDialog loadDialog;
	private DBHelperMessage db;

	private EditText textET;
	private Button cancelBtn;
	private Button saveBtn;

	private static SharedPreferences share;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);

		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		db = new DBHelperMessage(this, myapp);

		cancelBtn = (Button) findViewById(R.id.response_cancelBtn);
		saveBtn = (Button) findViewById(R.id.response_saveBtn);
		textET = (EditText) findViewById(R.id.responseET);

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMainView();
			}
		});

		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendResponse();
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

	public void sendResponse() {
		showMyLoadingDialog(getString(R.string.user_info_lable_1));
		final String context = textET.getText().toString();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					msg.obj = api.sendResponse(context);
				} catch (Exception ex) {
					msg.obj = null;
					ex.printStackTrace();
				}

				handlerForSendResponse.sendMessage(msg);
			}
		}.start();
	}

	private Handler handlerForSendResponse = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadDialog.dismiss();
				if (msg.obj != null) {
					JSONObject jsonObj = (JSONObject) msg.obj;
					try {
						if (jsonObj.get("msg") != null && jsonObj.get("msg").equals("success")) {
							Toast.makeText(ResponseActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(ResponseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
	 					Toast.makeText(ResponseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
					}
				} else {
 					Toast.makeText(ResponseActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	public void showMyLoadingDialog(String message) {
		loadDialog = new MyLoadingDialog(this, message, R.style.MyDialog);
		loadDialog.show();
	}
}
