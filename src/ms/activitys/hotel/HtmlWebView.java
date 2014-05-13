package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.graphics.Bitmap;

public class HtmlWebView extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private WebView webview;
	private ImageView retreat_btn;
	private ImageView advance_btn;
	private ImageView refresh_btn;
	private ProgressBar load_progressBar;
	private LinearLayout bottom_layout;
	private String urlstr = "http://121.199.8.186/customize/control/selectHotel";//嘉汇华美达
//	private String urlstr = "http://121.199.8.186/customize/control/search";//太仓锦江国际大酒店
	private String currentUrl;
	private int loadindex = 0;
	private boolean advancestar = false;
	private Set<String> urllist = new HashSet<String>();
	private int pagesize = 0;
	private String url;
	private String title;
	private TextView textView01;
	private String tag;
	private Button call_btn;
	private static DBHelperMessage db;
	private String storeid;
	private MyLoadingDialog loadDialog;
	
	private int cindex = 0;
//	private int cardindex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.html_web_view);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		db = new DBHelperMessage(this, myapp);
		
		textView01 = (TextView)findViewById(R.id.TextView01);
		
		Bundle bunde = this.getIntent().getExtras();
		if(bunde.containsKey("url"))
			urlstr = bunde.getString("url");
		else
		{
			urlstr = urlstr + "?locales="+Locale.getDefault().getLanguage()+"&loginid="+myapp.getPfprofileId();
		}
		if(bunde.containsKey("title"))
		{
			title = bunde.getString("title");
			textView01.setText(title);
		}
		if(bunde.containsKey("hotel"))
		{
			tag = bunde.getString("hotel");
//			cardindex = bunde.getInt("index");
		}
		
		call_btn = (Button)findViewById(R.id.call_btn);
		if(tag != null && tag.equals("hotel"))
		{
			call_btn.setVisibility(View.VISIBLE);
			storeid = bunde.getString("storeid");
		}
		
		initView();
	}
	
	public void initView()
	{
		try{
			Button break_btn = (Button)findViewById(R.id.home);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					if(urlstr.equals(currentUrl) || loadindex == 0)
//					{
//						if(tag != null && tag.equals("hotel"))
//						{
//							Intent intent = new Intent();
//						    intent.setClass( HtmlWebView.this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
//						    Bundle bundle = new Bundle();
////						    bundle.putInt("index", cardindex);
//							intent.putExtras(bundle);
//						    startActivity(intent);//开始界面的跳转函数
//						    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//							finish();
//						}
//						else
//						{
//			//				setResult(RESULT_OK, getIntent());
//							Intent intent = new Intent();
//						    intent.setClass( HtmlWebView.this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
//						    Bundle bundle = new Bundle();
//							intent.putExtras(bundle);
//						    startActivity(intent);//开始界面的跳转函数
//							overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//							finish();
//						}
//					}
//					else
//					{
//						loadindex = 0;
//						webview.loadUrl(urlstr);
//						bottom_layout.setVisibility(View.GONE);
//					}
					if(tag != null && tag.equals("hotel"))
					{
						Intent intent = new Intent();
					    intent.setClass( HtmlWebView.this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
					    Bundle bundle = new Bundle();
//					    bundle.putInt("index", cardindex);
						intent.putExtras(bundle);
					    startActivity(intent);//开始界面的跳转函数
					    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
						finish();
					}
					else
					{
		//				setResult(RESULT_OK, getIntent());
						Intent intent = new Intent();
					    intent.setClass( HtmlWebView.this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
					    Bundle bundle = new Bundle();
						intent.putExtras(bundle);
					    startActivity(intent);//开始界面的跳转函数
						overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
						finish();
					}
				}
			});
			
			load_progressBar = (ProgressBar)findViewById(R.id.load_progressBar);
			bottom_layout = (LinearLayout)findViewById(R.id.bottom_layout);
			
			webview = (WebView)findViewById(R.id.webview1);
			
			webview.getSettings().setSupportZoom(true);//是否允许放大缩小
//			webview.getSettings().setPluginsEnabled(true);//允许使用flash插件
			webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//是否可以支持javaScript打开窗口
			webview.getSettings().setJavaScriptEnabled(true);//支持javascript
			webview.getSettings().setAllowFileAccess(true);//访问文件权限
			webview.getSettings().setLightTouchEnabled(true);//光标接触被启用
			webview.getSettings().setNeedInitialFocus(true);//webview控件里的内容是否可以得到焦点，false为不可以也就是里面的东西不能点
//			webview.loadUrl(urlstr);  
			webview.addJavascriptInterface(this, "swfload");//把RIAExample的一个实例添加到js的全局对象window中，
			webview.loadUrl(urlstr);
			webview.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					// Activity和Webview根据加载程度决定进度条的进度大小
					// 当加载到100%的时候 进度条自动消失
//					context.setProgress(progress * 100);
				}
			});
			
			webview.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					load_progressBar.setVisibility(View.GONE);
					if(url.indexOf("tel://") < 0)
					{
						currentUrl = url;
						String urls = url.substring(0,url.lastIndexOf("/"));
						if(urlstr.equals(currentUrl))
						{
							bottom_layout.setVisibility(View.GONE);
						}
						else
						{
							if(loadindex == 0)
							{
								urlstr = currentUrl;
							}
							loadindex++;
							if(loadindex == 1)
							{
								retreat_btn.setImageResource(R.drawable.webviewtab_back_disable);
	//							advance_btn.setImageResource(R.drawable.webviewtab_forward_disable);
	//							advancestar = false;
							}
							else if(loadindex > 1)
							{
								retreat_btn.setImageResource(R.drawable.webviewtab_back_normal);
							}
							if(!urllist.contains(urls))
							{
								advance_btn.setImageResource(R.drawable.webviewtab_forward_disable);
								advancestar = false;
							}
							else
							{
	//							makeText("size=="+urllist.size() + "==loadindex=="+loadindex);
								if(urllist.size() == loadindex-1)
								{
									advance_btn.setImageResource(R.drawable.webviewtab_forward_disable);
									advancestar = false;
								}
							}
								
							bottom_layout.setVisibility(View.VISIBLE);
						}
						urllist.add(urls);
					}
					// 结束
					super.onPageFinished(view, url);
				}

				@Override
				public void onPageStarted(WebView view, String url,Bitmap favicon) {
					load_progressBar.setVisibility(View.VISIBLE);
					
					// 开始
					super.onPageStarted(view, url, favicon);
					

				}
				
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
					if(url.indexOf("tel://") < 0)
					{
						view.loadUrl(url);
					}
					else
					{
						Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(url)); 
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			            startActivity(intent);
					}
					return true;
				}
			});
			
			
			retreat_btn = (ImageView)findViewById(R.id.retreat_btn);
			retreat_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(loadindex > 1)
					{
						loadindex = loadindex - 2;
//						makeText("按了后退");
						webview.goBack();   //后退  
						advance_btn.setImageResource(R.drawable.webviewtab_forward_normal);
						advancestar = true;
					}
				}
			});
			
			advance_btn = (ImageView)findViewById(R.id.advance_btn);
			advance_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(advancestar)
					{
//						makeText("按了前进");
						webview.goForward();//前进
					}
				}
			});
			
			refresh_btn = (ImageView)findViewById(R.id.refresh_btn);
			refresh_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					webview.reload();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void callService(View v)
	{
		showMyLoadingDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					db.openDB();
					db.deleteStroeInfo(storeid,myapp.getPfprofileId());
					db.closeDB();
					msg.obj = true;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = false;
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
				boolean bb = (Boolean)msg.obj;
				if(loadDialog != null)
					loadDialog.dismiss();
				if(bb)
				{
					if(HotelServiceActivity.instance != null)
					{
						HotelServiceActivity.instance.showMyLoadingDialog();
						HotelServiceActivity.instance.getMyStoreListDatas();
					}
					makeText(HtmlWebView.this.getString(R.string.send_msg_session_lable));
					
					openServiceListView();
				}
				else
				{
					makeText(HtmlWebView.this.getString(R.string.send_msg_error_lable));
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openServiceListView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private class MyWebViewClient extends WebViewClient { 
    	@Override 
    	// 在WebView中显示页面,而不是默认浏览器中显示页面 
    	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	    	view.loadUrl(url); 
	    	return true; 
    	} 
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(urlstr.equals(currentUrl))
			{
				if(tag != null && tag.equals("hotel"))
				{
					Intent intent = new Intent();
				    intent.setClass( HtmlWebView.this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
				    Bundle bundle = new Bundle();
//				    bundle.putInt("index", cardindex);
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
					finish();
				}
				else
				{
	//				setResult(RESULT_OK, getIntent());
					Intent intent = new Intent();
				    intent.setClass( HtmlWebView.this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
				    Bundle bundle = new Bundle();
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
					overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
					finish();
				}
			}
			else
			{
				loadindex = 0;
				webview.loadUrl(urlstr);
				bottom_layout.setVisibility(View.GONE);
			}
			return false;
		}
		return false;
	}
	
	public void breakButton()
	{
		if(tag != null && tag.equals("hotel"))
		{
			Intent intent = new Intent();
		    intent.setClass( HtmlWebView.this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
//		    bundle.putInt("index", cardindex);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
		}
		else
		{
//				setResult(RESULT_OK, getIntent());
			Intent intent = new Intent();
		    intent.setClass( HtmlWebView.this,MessageListActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
		    Bundle bundle = new Bundle();
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
		}
	}
	
	public void chengKtsServiceOrderStart(String start)
	{
		if(MessageListActivity.instance != null)
			MessageListActivity.instance.chengKtsServiceOrderStart(start);
		
//		breakButton();
	}
	
	public String getUserName()
	{
		return myapp.getUserName();
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
}
