package ms.activitys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ms.activitys.hotel.ContactActivity;
import ms.activitys.hotel.HotelMainActivity;
import ms.activitys.hotel.HotelServiceActivity;
import ms.activitys.hotel.LoginDialog;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.hotel.MomentsActivity;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuLoadActivity extends Activity{
	
	public static MenuLoadActivity instance;
	
	private WebView webview;
	private MyApp myapp;
	private static SharedPreferences share;
	private Douban api;
	private MyLoadingDialog loadDialog;
	
	private String pageStyle = "menu4";
	private String startUrl = "";//记录webview开始url
	
	private Button backBtn;
	private Button refreshBtn;
	private TextView titleTV;
	
	private String user;
    private String paw;
    
    private static String menuName = "metaspheresy";
    private static String storeid = "";
    private static String isShowCom = "No";//记录是否服务调用，默认不是 
    
    private boolean isPersonal = false; //记录是否个人主页，true是，false为公司主页
    private String menudata = null; //记录个人主页数据
    //private String localAddress = "/mnt/sdcard/metashpere"; //记录存放在本地的数据文件地址
    private String localAddress = "/sdcard-ext/metashpere";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuload);
		instance = this;
		
		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		
		user = share.getString("user", "");
		paw = share.getString("pwa", "");
		
		Intent intent = this.getIntent();       
		Bundle bundle = intent.getExtras();  
		if(bundle!=null){
			String sid = bundle.getString("storeid"); 
			String issc = bundle.getString("isShowCom");
			if(sid!=null && !sid.equals("")){
				storeid = sid;
			}
			if(issc!=null && !issc.equals("")){
				isShowCom = issc;
			}
		}else{
			storeid = myapp.getAppstoreid();
		}
		
		initView();
		refreshWebView(false);
	}
	
	public void refreshWebView(boolean isSetBack){
		showMyLoadingDialog();
		if(isSetBack){
			webview.reload();
		}else{
			loadWebData();
		}
	}
	
	private void loadWebData(){
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				try {
					String menuN = (isPersonal && (isShowCom.equals("No")))?"mymenupage_"+myapp.getUserNameId():menuName;
					JSONObject jobj = api.getPageStyleByName((menuN.indexOf("null")>-1)?menuName:menuN, storeid);
					if (jobj != null) {
						msg.obj = jobj;
					} else {
						msg.obj = null;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
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
				if(msg.obj != null){
					JSONObject jObj = (JSONObject) msg.obj;
					try {
						String t = jObj.has("pageType")?(String) jObj.get("pageType"):"";
						String n = jObj.has("pageTitle")?(String) jObj.get("pageTitle"):"";
						pageStyle = t;

						File f = new File(localAddress+"/menu_"+myapp.getUserNameId()+".htm");
						boolean isHasLocalHtml = f.exists();
						if(isHasLocalHtml){
							startUrl = "file://"+localAddress+"/menu_"+myapp.getUserNameId()+".htm";
						}else{
							startUrl = "http://"+myapp.getIpaddress()+"/customize/control/"+pageStyle+"?menuName="+menuName+"&storeid="+storeid;
						}
						
						if(isShowCom.equals("No") && isPersonal){
							String pageHtmlName = localAddress+"/menu_"+myapp.getUserNameId()+"_"+pageStyle+".htm";
							String pageCssName = localAddress+"/mymenu.css";
							boolean isExists1 = fileIsExists(pageHtmlName);
							boolean isExists2 = fileIsExists(pageCssName);
							if(!isExists1){
								if(myapp.getUserNameId() == null){
									startUrl = "http://"+myapp.getIpaddress()+"/customize/control/"+pageStyle+"?storeid="+storeid+"&userid="+myapp.getUserNameId()+"&menuName="+menuName+"&jsessionid="+myapp.getSessionId();
								}else{
									if(pageStyle.equals("")){//说明通过用户ID没有拿到主页的模版，即此用户还没有配置个人主页，默认显示mymenumode模版，数据暂为空。(暂不下载到本地，等用户配置选择好的个人主页展现模版后再下载到本地)
										pageStyle = "mymenumode";
										startUrl = "http://"+myapp.getIpaddress()+"/customize/control/"+pageStyle+"?storeid="+storeid+"&userid="+myapp.getUserNameId()+"&menuName=mymenupage_"+myapp.getUserNameId()+"&jsessionid="+myapp.getSessionId();
									}else{
										startUrl = "http://"+myapp.getIpaddress()+"/customize/control/"+pageStyle+"?storeid="+storeid+"&userid="+myapp.getUserNameId()+"&menuName=mymenupage_"+myapp.getUserNameId()+"&jsessionid="+myapp.getSessionId();
										downUserMenuPage("http://"+myapp.getIpaddress()+"/upload/"+pageStyle+".ftl", localAddress+"/", "menu_"+myapp.getUserNameId()+"_"+pageStyle+".htm");
									}
								}
								if(!isExists2){
									downUserMenuPage("http://"+myapp.getIpaddress()+"/upload/mymenu.css", localAddress+"/", "mymenu.css");
								}
							}else{
								//startUrl = "file:///android_asset/mymenumode.html";
								startUrl = "file:///" + pageHtmlName;
							}
						}
						
						System.out.println("startUrl=="+startUrl);
						webview.loadUrl(startUrl);
						
						titleTV.setText(n);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				if(msg.obj != null){
					if (loadDialog != null)
						loadDialog.dismiss();
					
					JSONObject jobj = (JSONObject) msg.obj;
					try {
						if(jobj.get("msg").equals("success")){
							menudata = null; //设置为null，重新加载服务器数据，获取最新配置的数据信息
							webview.loadUrl(startUrl);
							backBtn.setVisibility(View.INVISIBLE);
						}else{
							Toast.makeText(MenuLoadActivity.this, "保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 2:
				if(msg.obj != null){
					JSONObject jobj = (JSONObject) msg.obj;
					try {
						JSONObject json = jobj.getJSONObject("data");
						System.out.println("拿到数据json格式"+json.toString());
						menudata = json.toString();
						webview.loadUrl("javascript:addData('"+menudata+"')");
						
						writeFileSdcard(localAddress+"/menu_"+myapp.getUserNameId()+"_"+pageStyle+".txt", menudata);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 3:
				
				break;
			}
		}
	};
	
	private void initView(){
		backBtn = (Button) findViewById(R.id.menuload_backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isShowCom.equals("No")){
					if(webview.getUrl().indexOf("getMyServiceOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getServiceDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyServiceOrderListView?tag=all&username="+myapp.getUserName());
					}
					else if(webview.getUrl().indexOf("getMyInspectOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getInspectDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyInspectOrderListView?tag=all&username="+myapp.getUserName());
					}
					else
						webview.goBack();
				}
				else{
					if(webview.getUrl().equals(startUrl)){
						isShowCom = "No";
						finish();
					}else{
						if(webview.getUrl().indexOf("getMyServiceOrderListView") >= 0)
						{
							webview.loadUrl(startUrl);
						}
						else if(webview.getUrl().indexOf("getServiceDetailedView") >= 0)
						{
							webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyServiceOrderListView?tag=all&username="+myapp.getUserName());
						}
						else if(webview.getUrl().indexOf("getMyInspectOrderListView") >= 0)
						{
							webview.loadUrl(startUrl);
						}
						else if(webview.getUrl().indexOf("getInspectDetailedView") >= 0)
						{
							webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyInspectOrderListView?tag=all&username="+myapp.getUserName());
						}
						else
							webview.goBack();
					}
				}
			}
		});
		
		refreshBtn = (Button) findViewById(R.id.menuload_refreshBtn);
		refreshBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				showMyLoadingDialog();
//				loadWebData();
				
				if(!user.equals("") && !paw.equals("")){
					if(isPersonal){
						String setUrl  = "http://"+myapp.getIpaddress()+"/customize/control/mymenuset?storeid=" +myapp.getAppstoreid()+"&userid=" + myapp.getUserNameId() + "&jsessionid="+ myapp.getSessionId();
						webview.loadUrl(setUrl);
					}else{
						System.out.println("长按页面");
						String curUrl = webview.getUrl();
						String s = "menuName=";
						int sIndex = curUrl.indexOf(s) + s.length();
						int eIndex = curUrl.indexOf("&");
						menuName = curUrl.substring(sIndex, eIndex);
						
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						intent.setClass(MenuLoadActivity.this, MenuSetActivity.class);
						bundle.putString("menuName", menuName);
						intent.putExtras(bundle);
						startActivity(intent);//开始界面的跳转函数
						overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
					}
				}
				else
				{
					openLoginDialog();
				}
			}
		});
		
		titleTV = (TextView) findViewById(R.id.menuload_title);
		titleTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MenuLoadActivity.instance.update("keyword", "Mnus462fa764hts749ah", "", "", "周一十点例会");
//				try {
//					api.sendKeyword();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});
		
		webview = (WebView) findViewById(R.id.menuload_web);
		webview.getSettings().setJavaScriptEnabled(true); 
		webview.setHorizontalScrollBarEnabled(false);
		webview.setVerticalScrollBarEnabled(false);
		
        webview.setWebViewClient(new WebViewClient() {
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		if(!user.equals("") && !paw.equals("")){
        			showMyLoadingDialog();
        			//给Url加userid
        			if(url.indexOf("?")>-1){//说明url本身已带参数
        				if(url.indexOf("username=") < 0)
        					url = url + "&userid=" + myapp.getUserNameId() + "&jsessionid="+ myapp.getSessionId()+"&username="+myapp.getUserName();
        				else
        					url = url + "&userid=" + myapp.getUserNameId() + "&jsessionid="+ myapp.getSessionId();
        				if(url.indexOf("&storeid=")>-1){
        					//说明本身已有storeid，不做处理（针对菜单页面，为了微信访问已经配置了storeid）
        				}else{
        					url = url + "&storeid=" + storeid;
        				}
        			}else{
        				if(url.indexOf("username=") < 0)
        					url = url + "?userid=" + myapp.getUserNameId() + "&jsessionid="+ myapp.getSessionId() + "&storeid=" + storeid+"&username="+myapp.getUserName();
        				else
        					url = url + "?userid=" + myapp.getUserNameId() + "&jsessionid="+ myapp.getSessionId() + "&storeid=" + storeid;
        			}
        			System.out.println("跳转url"+url);
            	    view.loadUrl(url);
            	    return super.shouldOverrideUrlLoading(view, url);
//            	    return false;
				}
				else
				{
					openLoginDialog();
					return true;
				}
        	}
        	
        	@Override
            public void onPageFinished(WebView view, String url)
            {
				if (loadDialog != null)
					loadDialog.dismiss();
				if(isShowCom.equals("No") && isPersonal){
					refreshBtn.setVisibility(View.VISIBLE);
					if(!url.equals(startUrl)){
						backBtn.setVisibility(View.VISIBLE);
					}else{
						backBtn.setVisibility(View.INVISIBLE);
					}
				}else{
					//判断页面的url，是否需要配置
					if(myapp.getUpMenuState()!=null && myapp.getUpMenuState().equals("1") && url.indexOf("/customize/control/menu")>-1){
						refreshBtn.setVisibility(View.VISIBLE);
					}else{
						refreshBtn.setVisibility(View.GONE);
					}
					//判断页面的url，是否需要返回
					if(isShowCom.equals("No") && url.equals(startUrl)){
						backBtn.setVisibility(View.INVISIBLE);
					}else{
						backBtn.setVisibility(View.VISIBLE);
					}
				}
				
				super.onPageFinished(view, url);
            }
        });
        webview.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				System.out.println("长按页面");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(MenuLoadActivity.this, MenuSetActivity.class);
				bundle.putString("menuName", menuName);
				intent.putExtras(bundle);
				startActivity(intent);// 开始界面的跳转函数
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
				return true;
			}
		});
        webview.addJavascriptInterface(new Object() { 
            public void skipActivity(final String actName) {
            	if(!user.equals("") && !paw.equals("")){
            		System.out.println("跳转到Activity:"+actName);
                	
                	if(actName.equals("friend")){
                		Intent intent = new Intent();
                	    intent.setClass(MenuLoadActivity.this, MomentsActivity.class);
                	    Bundle bundle = new Bundle();
                	    bundle.putString("tag", "main");
                		intent.putExtras(bundle);
                	    MainTabActivity.instance.loadLeftActivity(intent);
                	}else if(actName.equals("service")){
                		Intent intent = new Intent();
                	    intent.setClass(MenuLoadActivity.this, HotelServiceActivity.class);
                	    Bundle bundle = new Bundle();
                	    bundle.putString("tag", "menuload");
                		intent.putExtras(bundle);
                	    MainTabActivity.instance.loadLeftActivity(intent);
                		//MainTabActivity.instance.mth.setCurrentTab(1);
                	}else if(actName.equals("message")){
                		Intent intent = new Intent();
                	    intent.setClass(MenuLoadActivity.this, HotelMainActivity.class);
                	    MainTabActivity.instance.loadLeftActivity(intent);
                	}else if(actName.equals("addresslist")){
                		Intent intent = new Intent();
                	    intent.setClass(MenuLoadActivity.this, ContactActivity.class);
                	    MainTabActivity.instance.loadLeftActivity(intent);
                	}
				}
				else
				{
					openLoginDialog();
				}
            } 
        }, "menu");
        
        webview.addJavascriptInterface(new Object() { 
            public void loadData() {
            	if(isPersonal){
            		if(menudata != null){
						webview.loadUrl("javascript:addData('"+menudata+"')");
            		}else{
            			String dataFile = localAddress+"/menu_"+myapp.getUserNameId()+"_"+pageStyle+".txt";
            			//boolean isExists = fileIsExists(dataFile);
            			boolean isExists = false;
            			if(isExists){
            				menudata = readFileSdcard(dataFile);
            				webview.loadUrl("javascript:addData('"+menudata+"')");
            			}else{
            				new Thread() {
                				public void run() {
                					Message msg = new Message();
                					msg.what = 2;

                					try {
                						JSONObject jobj = api.getMenuByName("mymenupage_"+myapp.getUserNameId());
                						if (jobj != null) {
                							msg.obj = jobj;
                						} else {
                							msg.obj = null;
                						}
                					} catch (Exception ex) {
                						ex.printStackTrace();
                					}
                					handler.sendMessage(msg);
                				}
                			}.start();
            			}
            		}
				}
            } 
        }, "mymenu1");
        
        webview.addJavascriptInterface(new Object() { 
            public void saveMyMenu(final String menuName, final String dataStr) {
            	System.out.println("页面保存数据1："+menuName);
            	System.out.println("页面保存数据2："+dataStr);
            	showMyLoadingDialog();
            	
            	new Thread() {
    				public void run() {
    					Message msg = new Message();
    					msg.what = 1;

    					try {
    						JSONObject jobj = api.saveMenuPageData(menuName, new JSONObject(dataStr), new HashMap<String, File>());
    						if (jobj != null) {
    							msg.obj = jobj;
    						} else {
    							msg.obj = null;
    						}
    					} catch (Exception ex) {
    						ex.printStackTrace();
    					}
    					handler.sendMessage(msg);
    				}
    			}.start();
            } 
        }, "mymenu");
        
        webview.addJavascriptInterface(this, "swfload");
        
        webview.setWebChromeClient(new WebChromeClient() {
        	@Override
        	public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
        		AlertDialog.Builder b2 = new AlertDialog.Builder(MenuLoadActivity.this)
        				.setTitle("提示").setMessage(message)
        				.setPositiveButton("ok",
        						new AlertDialog.OnClickListener() {
        							@Override
        							public void onClick(DialogInterface dialog,
        									int which) {
        								result.confirm();
        								// MyWebView.this.finish();
        							}
        						});

        		b2.setCancelable(false);
        		b2.create();
        		b2.show();
        		return true;
        	}
        });
	}
	
	public void update(String type, String menuId, String menuBgImg, String menuIcon, String keyword){
		//type:bgimg a icon keyword
		//onclick="updateBtn('icon', '1', '/js/hotel/menu/images/mymenu4.jpg', '/js/hotel/menu/images/mymenu5.png', 'hahahahah')"
		webview.loadUrl("javascript:updateBtn('"+type+"', '"+menuId+"', '"+menuBgImg+"', '"+menuIcon+"', '"+keyword+"')");
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(isShowCom.equals("No")){
				if(backBtn.getVisibility() == View.VISIBLE)
				{
					if(webview.getUrl().indexOf("getMyServiceOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getServiceDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyServiceOrderListView?tag=all&username="+myapp.getUserName());
					}
					else if(webview.getUrl().indexOf("getMyInspectOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getInspectDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyInspectOrderListView?tag=all&username="+myapp.getUserName());
					}
					else
						webview.goBack();
				}
				else
					MainTabActivity.instance.onMinimizeActivity();
				return true;
			}
			else{
				if(webview.getUrl().equals(startUrl)){
					isShowCom = "No";
					finish();
				}else{
					if(webview.getUrl().indexOf("getMyServiceOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getServiceDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyServiceOrderListView?tag=all&username="+myapp.getUserName());
					}
					else if(webview.getUrl().indexOf("getMyInspectOrderListView") >= 0)
					{
						webview.loadUrl(startUrl);
					}
					else if(webview.getUrl().indexOf("getInspectDetailedView") >= 0)
					{
						webview.loadUrl("http://"+myapp.getIpaddress()+":8088/customize/control/getMyInspectOrderListView?tag=all&username="+myapp.getUserName());
					}
					else
						webview.goBack();
				}
				return false;
			}
		}
		return false;
	}
	
	public String getUserName()
	{
		return myapp.getUserName();
	}
	
	public void chengKtsServiceOrderStart(String start)
	{
		
	}
	
	private void openLoginDialog(){
		Intent intent = new Intent();
    	intent.setClass(MenuLoadActivity.this,LoginDialog.class);
    	startActivity(intent);
	}
	
	private boolean fileIsExists(String filename) {
		try {
			File f = new File(filename);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	private void downUserMenuPage(final String fileurl, final String savePath, final String saveFileName){
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;

				try {  
		            URL url = new URL(fileurl);  
		          
		            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		            conn.connect();  
		            int length = conn.getContentLength();  
		            InputStream is = conn.getInputStream();  
		              
		            File file = new File(savePath);  
		            if(!file.exists()){  
		                file.mkdir();  
		            }  
		            File menuFile = new File(savePath + saveFileName); 
		            FileOutputStream fos = new FileOutputStream(menuFile);  
		              
		            int count = 0;  
		            byte buf[] = new byte[1024];  
		              
		            do{                   
		                int numread = is.read(buf);  
		                if(numread <= 0){      
		                    break;  
		                }  
		                fos.write(buf,0,numread);  
		            }while(true);//点击取消就停止下载.  
		              
		            fos.close();  
		            is.close();  
		        } catch (MalformedURLException e) {  
		            e.printStackTrace();  
		        } catch(IOException e){  
		            e.printStackTrace();  
		        }  
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private void writeFileSdcard(final String fileName, final String data) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;

				try {
					FileOutputStream fout = new FileOutputStream(fileName);
					byte[] bytes = data.getBytes();
					fout.write(bytes);
					fout.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private String readFileSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
