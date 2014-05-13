package ms.activitys.hotel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

public class TeachQuestionsAnsweredActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private EditText questions_txt;
	private EditText answered_txt;
	private String answert = null;
	private String [] itemsid;
	private String [] itemsvalue;
	private int typeindex = 0;
	private Button tooth_et;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teach_questrions_answer_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("answert"))
			answert = bunde.getString("answert");
		
		loadAnswerTypeList();
		
		initView();
	}
	
	public void initView()
	{
		try{
			questions_txt = (EditText)findViewById(R.id.questions_txt);
			if(answert != null && !answert.equals(""))
				questions_txt.setText(answert);
			answered_txt = (EditText)findViewById(R.id.answered_txt);
			
			tooth_et = (Button)findViewById(R.id.tooth_et);
			
			Button break_btn = (Button)findViewById(R.id.home);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openMainView();
				}
			});
			
			Button clear_btn = (Button)findViewById(R.id.clear_btn);
			clear_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openMainView();
				}
			});
			
			Button add_btn = (Button)findViewById(R.id.add_btn);
			add_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String q = questions_txt.getText().toString();
					String a = answered_txt.getText().toString();
					if(q.equals(""))
						makeText(getString(R.string.hotel_label_27) + getString(R.string.hotel_label_25));
					else if(a.equals(""))
						makeText(getString(R.string.hotel_label_27) + getString(R.string.hotel_label_26));
					else
						saveQA();
				}
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void saveQA()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
						String q = questions_txt.getText().toString();
						String a = answered_txt.getText().toString();
						String type = itemsid[typeindex];
						
						jobj = api.createQuestionsAnswered(q,a,type,"Moed435aa6e6hff3k8yn");
						if(jobj == null)
						{
							msg.obj = null;
						}
						else
						{
							String tag = jobj.getString("tag");
							if(tag.equals("success"))
								msg.obj = "1";
							else
								msg.obj = null;
						}
					}
					else
					{
						msg.obj = null;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadAnswerTypeList()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				JSONObject jobj = null;
				JSONArray jArr;
				try {
					if(isInetnState())
					{
						jobj = api.getAnswerTypeList();
						if(jobj == null)
						{
							msg.obj = null;
						}
						else
						{
							JSONArray array = jobj.getJSONArray("data");
							if(array != null)
							{
								itemsid = new String[array.length()];
								itemsvalue = new String[array.length()];
								for(int i=0;i<array.length();i++)
								{
									JSONObject object = array.getJSONObject(i);
									
									String id = "";
									if(object.has("id"))
										id = object.getString("id");
									
									String value = "";
									if(object.has("value"))
										value = object.getString("value");
									
									itemsid[i] = id;
									itemsvalue[i] = value;
								}
								msg.obj = "1";
							}
							else
								msg.obj = null;
						}
					}
					else
					{
						msg.obj = null;
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				String tag = (String)msg.obj;
				if(tag != null)
				{
					makeText(getString(R.string.hotel_label_17));
					questions_txt.setText("");
					answered_txt.setText("");
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				break;
			case 1:
				
				break;
			}
		}
	};
	
	public void openListViewItemMuneView(View v)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(itemsvalue, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				typeindex = which;
				tooth_et.setText(itemsvalue[which]);
			}
		});
		
		alertDialog = builder.create();
		alertDialog.setTitle(getString(R.string.app_name));
		alertDialog.show();
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean isInetnState() {
		boolean inetnState = true;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			inetnState = false;
		} else {
			inetnState = true;
		}
		return inetnState;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainView();
			return false;
		}
		return false;
	}
}
