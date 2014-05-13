package ms.activitys.coupon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.globalclass.EnvironmentShare;
import ms.globalclass.FileUtils;
import ms.globalclass.StreamTool;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperCoupon;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;



public class CouponView extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// 首页
	private final int TOOLBAR_ITEM_MAP = 1;// 退后
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// 前进
	private final int TOOLBAR_ITEM_NFC = 3;// 创建
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	
	String index;
	private String sindex;
	
	private String tag = "1";
	
	private String isASttention;
	
	private Map<String,Object> nmap = null;
	
	private DBHelperCoupon dbc;
	
	private ProgressDialog mypDialog;
	
	private String accessToken = "";
    private String tokenSecret = "";
    private String verifier = "";
    private int weibotag = 0;
    
    private static final String RESTAPI_INTERFACE_POSTRECORD = "/records/add.json";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = CouponView.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		accessToken = share.getString("EXTRA_ACCESS_TOKEN", "");
	    tokenSecret = share.getString("EXTRA_TOKEN_SECRET", "");
	    verifier = share.getString("oauth_verifier", "");
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("cindex");
		tag = bunde.getString("tag");
		sindex = bunde.getString("sindex");
		
		if(tag.equals("download"))
			setContentView(R.layout.coupon_detailed);
		else
			setContentView(R.layout.coupen_detail_2);
		if(!StreamTool.isNetworkVailable(this))
			dbc = new DBHelperCoupon(this,myapp);
		
		showCouponView();
		
	}
	
	public void showCouponView()
	{
		try{
			
			 
			List<Map<String,Object>> list = myapp.getCouponAll();
			Map<String,Object> map = list.get(Integer.valueOf(index));
			final String couponId = (String)map.get("couponId");
			String endTime = (String)map.get("endTime");
			final String couponDesc = (String)map.get("couponDesc");
			Bitmap cimgb = (Bitmap)map.get("img");
			Bitmap simgb = (Bitmap)map.get("simg");
			String couponNumber = (String)map.get("couponNumber");
			String couponName = (String)map.get("couponName");
			final String storeid = (String)map.get("storeid");
			isASttention = (String)map.get("isASttention");
			final String imgurl = (String)map.get("imgurl");
			final String simgurl = (String)map.get("simgurl");
			final String sname = (String)map.get("sname");
			final String couponLoadid = (String)map.get("couponLoadid");
			final String isUser = (String)map.get("isUserStr");
			final String businessId = (String)map.get("businessId");
			
			ImageView simg = (ImageView)findViewById(R.id.coupon_image);
			if(StreamTool.isNetworkVailable(this))
			{
//				simg.setImageBitmap(simgb);
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 0;
						
						Bitmap bitm = getImageBitmap(simgurl);
						msg.obj = bitm;
						handler.sendMessage(msg);
					}
				}.start();
			}
			else
				simg.setImageBitmap(simgb);
			
			TextView cname = (TextView)findViewById(R.id.coupon_name);
			cname.setText(couponName);
			
			TextView endtime = (TextView)findViewById(R.id.end_time);
			endtime.setText(this.getString(R.string.coupon_lable_1)+endTime);
			
			if(tag.equals("download"))
			{
				TextView coupnumber = (TextView)findViewById(R.id.coupon_number);
				coupnumber.setText(this.getString(R.string.coupon_lable_2)+couponNumber+this.getString(R.string.coupon_lable_3));
			}
			
			TextView coupondoc = (TextView)findViewById(R.id.coupon_dec);
			coupondoc.setText(Html.fromHtml(couponDesc));
			
			ImageView cimg = (ImageView)findViewById(R.id.coupon_conentimg);
			if(StreamTool.isNetworkVailable(this))
			{
//				cimg.setImageBitmap(getImageBitmap(imgurl,500,500));
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;
						
						Bitmap bitm = getImageBitmap(imgurl);
						msg.obj = bitm;
						handler.sendMessage(msg);
					}
				}.start();
			}
			else
				cimg.setImageBitmap(cimgb);
			
			final ImageView ximg = (ImageView)findViewById(R.id.coupon_xinxin);
			if(isASttention.equals("1"))//1为否，0为是
			{
				ximg.setImageResource(R.drawable.xinxin1);
			}
			else
			{
				ximg.setImageResource(R.drawable.xinxin2);
			}
			ximg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProgressDialog();
					if(isASttention.equals("1"))//1为否，0为是
					{
						try{
							
							JSONObject jobj = api.isASttention(couponId,"0");
							if(jobj != null)
							{
								ximg.setImageResource(R.drawable.xinxin2);
								isASttention = "0";
//								
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						mypDialog.dismiss();
					}
					else
					{
						try{
							JSONObject jobj = api.notASttention(couponId,"0");
							if(jobj != null)
							{
								ximg.setImageResource(R.drawable.xinxin1);
								isASttention = "1";
//								
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						mypDialog.dismiss();
					}
				}
			});
			
			List<Map<String,Object>> dlist = myapp.getStorelist();
		    String idex = "";
		    if(dlist != null)
		    {
			      for(int i=0;i<dlist.size();i++)
			      {
			        	Map maps = dlist.get(i);
			        	String pid = (String)maps.get("pkid");
			        	
						if(pid.equals(storeid))
						{
							nmap = maps;
							idex = String.valueOf(i);
							break;
						}
			      }
		     }
		      
			Button btn = (Button)findViewById(R.id.vip_add);
			if(tag.equals("download"))
			{
				JSONObject jobj = api.isSelectCards(myapp.getPfprofileId(),storeid,businessId,"");
				String tags = jobj.getString("tag");
				if(tags.equals("1"))
				{
					btn.setText(this.getString(R.string.coupon_lable_4));
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showProgressDialog();
							try {
								JSONObject jobj = api.updateCoupon(couponId);
								if(jobj != null)
								{
									String msg = jobj.getString("tag");
									mypDialog.dismiss();
									if(msg.equals("success"))
									{
										makeText(CouponView.this.getString(R.string.coupon_lable_5));
										String newnum = jobj.getString("newnum");
										
										TextView coupnumber = (TextView)findViewById(R.id.coupon_number);
										coupnumber.setText(CouponView.this.getString(R.string.coupon_lable_2)+newnum+CouponView.this.getString(R.string.coupon_lable_3));
									}
									else
									{
										makeText(msg);
									}
									
								}
								else
								{
									mypDialog.dismiss();
									makeText(CouponView.this.getString(R.string.coupon_lable_6));
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				else
				{
					btn.setText(this.getString(R.string.coupon_lable_6));
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showProgressDialog();
							addVipCard();
						}
					});
				}
			}
			else
			{
				if(isUser.equals("1"))
				{
					btn.setText(this.getString(R.string.coupon_lable_8));
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showProgressDialog();
							try {
								if(StreamTool.isNetworkVailable(CouponView.this))
								{
									JSONObject jobj = api.updateCouponStart(couponLoadid);
									if(jobj != null)
										openCouponView();
									else
									{
										mypDialog.dismiss();
										makeText(CouponView.this.getString(R.string.coupon_lable_9));
									}
								}
								else
								{
									dbc.udateCouponStatr(couponId);
									openCouponView();
								}
							}  catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				else
				{
					btn.setBackgroundResource(R.drawable.other_btn_1);
					btn.setText(this.getString(R.string.coupon_lable_8));
					btn.setEnabled(false);
					makeText(this.getString(R.string.coupon_lable_10));
				}
			}
			
			Button sbtn = (Button)findViewById(R.id.share_btn);
			sbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String couponContent = couponDesc;
					try{
						String fliePath = downFile(imgurl,"fenxianpic.jpg");
						Intent intent = new Intent(Intent.ACTION_SEND);  
		                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fliePath)));  //传输图片或者文件 采用流的方式  
		                intent.putExtra(Intent.EXTRA_TEXT, couponContent);   //附带的说明信息  
		                intent.putExtra(Intent.EXTRA_SUBJECT, CouponView.this.getString(R.string.coupon_lable_13));  //标题
		                intent.setType("image/*");   //分享图片  
//		                intent.setType("text/plain"); //分享文字
		                startActivity(Intent.createChooser(intent,CouponView.this.getString(R.string.coupon_lable_13)));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String str = "";
			switch (msg.what) {
			case 0:
				Bitmap bitm = (Bitmap)msg.obj;
				ImageView simg = (ImageView)findViewById(R.id.coupon_image);
				simg.setImageBitmap(bitm);
				
				List<Map<String,Object>> list = myapp.getCouponAll();
				Map<String,Object> map = list.get(Integer.valueOf(index));
				map.put("simg",bitm);
				
				break;
			case 1:
				Bitmap bitm2 = (Bitmap)msg.obj;
				ImageView cimg = (ImageView)findViewById(R.id.coupon_conentimg);
				cimg.setImageBitmap(bitm2);
				break;
			case 2:
				boolean b = (Boolean)msg.obj;
				if(b)
					str = CouponView.this.getString(R.string.coupon_lable_17);
				else
					str = CouponView.this.getString(R.string.coupon_lable_18);
				makeText(str);
				weibotag = weibotag - 1;
				if(weibotag == 0)
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 3:
				boolean b2 = (Boolean)msg.obj;
				if(b2)
					str = CouponView.this.getString(R.string.coupon_lable_19);
				else
					str = CouponView.this.getString(R.string.coupon_lable_20);
				makeText(str);
				weibotag = weibotag - 1;
				if(weibotag == 0)
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 4:
				boolean b3 = (Boolean)msg.obj;
				if(b3)
					str = CouponView.this.getString(R.string.coupon_lable_21);
				else
					str = CouponView.this.getString(R.string.coupon_lable_22);
				makeText(str);
				weibotag = weibotag - 1;
				if(weibotag == 0)
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			case 5:
				boolean b4 = (Boolean)msg.obj;
				if(b4)
					str = CouponView.this.getString(R.string.coupon_lable_23);
				else
					str = CouponView.this.getString(R.string.coupon_lable_24);
				makeText(str);
				weibotag = weibotag - 1;
				if(weibotag == 0)
				{
					if(mypDialog != null)
						mypDialog.dismiss();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void openSharePage(final String imgurl,final String title,final String content)
	{
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			AlertDialog alertDialog = null;
			
			final View view = LayoutInflater.from(this).inflate(R.layout.share_view,null);
			
			final CheckBox box1 = (CheckBox)view.findViewById(R.id.box1);
			box1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!share.getBoolean("isBindingSina", false))
						box1.setChecked(false);
				}
			});
			if(share.getBoolean("isBindingSina", false))
			{
				ImageView img = (ImageView)view.findViewById(R.id.image_sina);
				img.setImageResource(R.drawable.ic_sina_press_off);
				
				box1.setChecked(true);
				box1.setFocusable(true);
			}
			
			final CheckBox box2 = (CheckBox)view.findViewById(R.id.box2);
			box2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!share.getBoolean("isBindingRenren", false))
						box2.setChecked(false);
				}
			});
			if(share.getBoolean("isBindingRenren", false))
			{
				ImageView img = (ImageView)view.findViewById(R.id.image_renren);
				img.setImageResource(R.drawable.ic_renren_press_off);
				
				box2.setChecked(true);
				box2.setFocusable(true);
			}
			
			final CheckBox box3 = (CheckBox)view.findViewById(R.id.box3);
			box3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!share.getBoolean("isBindingQq", false))
						box3.setChecked(false);
				}
			});
			if(share.getBoolean("isBindingQq", false))
			{
				ImageView img = (ImageView)view.findViewById(R.id.image_qq);
				img.setImageResource(R.drawable.ic_tecent_press_off);
				
				box3.setChecked(true);
				box3.setFocusable(true);
			}
			
			final CheckBox box4 = (CheckBox)view.findViewById(R.id.box4);
			box4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!share.getBoolean("isBindingkx", false))
						box4.setChecked(false);
				}
			});
			if(share.getBoolean("isBindingkx", false))
			{
				ImageView img = (ImageView)view.findViewById(R.id.image_kx);
				img.setImageResource(R.drawable.ic_kaixin_press_off);
				
				box4.setChecked(true);
				box4.setFocusable(true);
			}
			
//			final CheckBox box4 = (CheckBox)view.findViewById(R.id.box4);
//			box4.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(!share.getBoolean("isBindingsh", false))
//						box4.setChecked(false);
//				}
//			});
//			if(share.getBoolean("isBindingsh", false))
//			{
//				ImageView img = (ImageView)view.findViewById(R.id.image_sh);
//				img.setImageResource(R.drawable.ic_souhu_press_off);
//				
//				box4.setChecked(true);
//				box4.setFocusable(true);
//			}
			
//			final CheckBox box5 = (CheckBox)view.findViewById(R.id.box5);
//			box5.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(!share.getBoolean("isBindingwy", false))
//						box5.setChecked(false);
//				}
//			});
//			if(share.getBoolean("isBindingwy", false))
//			{
//				ImageView img = (ImageView)view.findViewById(R.id.image_wy);
//				img.setImageResource(R.drawable.ic_wanyi_press_off);
//				
//				box5.setChecked(true);
//				box5.setFocusable(true);
//			}
			
			builder.setTitle(this.getString(R.string.coupon_lable_13));
			builder.setView(view);
			builder.setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					dialog.dismiss();
					
					final List<Map<String,Object>> list = myapp.getMyCardsAll();
					
					if(list.size() > 0)
					{
						try{
							String fliePath = downFile(imgurl,"fenxianpic.jpg");
							Intent intent = new Intent(Intent.ACTION_SEND);  
			                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fliePath)));  //传输图片或者文件 采用流的方式  
			                intent.putExtra(Intent.EXTRA_TEXT, content);   //附带的说明信息  
			                intent.putExtra(Intent.EXTRA_SUBJECT, CouponView.this.getString(R.string.coupon_lable_13));  //标题
			                intent.setType("image/*");   //分享图片  
//			                intent.setType("text/plain"); //分享文字
			                startActivity(Intent.createChooser(intent,CouponView.this.getString(R.string.coupon_lable_13)));
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}).setNegativeButton(this.getString(R.string.coupon_lable_16),
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					// 取消按钮事件
				}
			});
			
			alertDialog = builder.create();
			alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private long getRecordID(String jsonResult) throws JSONException {
		JSONObject jsonObj = new JSONObject(jsonResult);
		if (jsonObj == null) {
			return 0;
		}

		long rid = jsonObj.optInt("rid");
		return rid;
	}
	
	
	public static byte[] readFileImage(String filename)throws IOException{
		URL imageUrl = null;
		byte[] bytes = null;
		try {
			imageUrl = new URL(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			
			BufferedInputStream bufferedInputStream=new BufferedInputStream(is);
			
			int len =bufferedInputStream.available();
			bytes=new byte[len];
			int r=bufferedInputStream.read(bytes);
			if(len !=r){
				bytes=null;
				throw new IOException("读取文件不正确");
			}
			bufferedInputStream.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return bytes;
	}
	
	public static InputStream readFileImageIs(String filename)throws IOException{
        InputStream in = null;
        try {
            in = new BufferedInputStream(new URL(filename).openStream(),
            		4 * 1024);
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
		return in;
	}
	
//	private String upload(Weibo weibo, String source, String file,
//			String status, String lon, String lat) throws WeiboException {
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("source", source);
//		bundle.add("pic", file);
//		bundle.add("status", status);
//		if (!TextUtils.isEmpty(lon)) {
//			bundle.add("lon", lon);
//		}
//		if (!TextUtils.isEmpty(lat)) {
//			bundle.add("lat", lat);
//		}
//		String rlt = "";
//		String url = Weibo.SERVER + "statuses/upload.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST);
//
//		return rlt;
//	}
//
//	private String update(Weibo weibo, String source, String status,
//			String lon, String lat) throws MalformedURLException, IOException,
//			WeiboException {
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("source", source);
//		bundle.add("status", status);
//		if (!TextUtils.isEmpty(lon)) {
//			bundle.add("lon", lon);
//		}
//		if (!TextUtils.isEmpty(lat)) {
//			bundle.add("lat", lat);
//		}
//		String rlt = "";
//		String url = Weibo.SERVER + "statuses/update.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST);
//		return rlt;
//	}
	
	
	public void openSharePage2(String imgurl,String title,String content)
	{
		try{
			String fliePath = downFile2(imgurl,"temp.jpg");
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, title);//主题
			intent.putExtra(Intent.EXTRA_TEXT, content);//内容
//			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/DCIM/100MEDIA/IMAG0162.jpg"));//图片
			if(fliePath != null)
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fliePath));
		    startActivity(Intent.createChooser(intent, title));//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,UseCoupon.class);
		    Bundle bundle = new Bundle();
			bundle.putString("cindex", index);
			bundle.putString("sindex", sindex);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    mypDialog.dismiss();
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean addVipCard()
	{
		boolean b = false;
		JSONObject jobj;
		U.dout(0);
		if(nmap != null)
	    {
	    	  	final String pid = (String)nmap.get("pkid");
	    	  	final String bid = (String)nmap.get("bid");
				final String province = (String)nmap.get("province");
				final String storeTyle = (String)nmap.get("storeType");
	    	  	
	    	  	try {
					jobj = api.addCards(myapp.getPfprofileId(),pid,bid,storeTyle,province);
					String success = jobj.getString("success");
					mypDialog.dismiss();
					if(success.equals("true"))
					{
						makeText(this.getString(R.string.coupon_lable_25));
						b = true;
					}
					else
					{
						String msg = jobj.getString("msg");
						makeText(this.getString(R.string.coupon_lable_26)+msg);
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
	    }
		else
		{
			mypDialog.dismiss();
			makeText(this.getString(R.string.coupon_lable_26));
		}
		
		return b;
	}

	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	/**  
     *   
     * @param urlStr  
     * @param path  
     * @param fileName  
     * @return   
     *      -1:文件下载出错  
     *       0:文件下载成功  
     *       1:文件已经存在  
     */  
    public int downFile(String urlStr, String path, String fileName){  
        InputStream inputStream = null;  
        try {  
            FileUtils fileUtils = new FileUtils();  
              
            if(fileUtils.isFileExist(path + fileName)){  
                return 1;  
            } else {  
                inputStream = getInputStreamFromURL(urlStr);
                System.out.println("urlStr===="+urlStr);
                if(inputStream != null)
                {
	                File resultFile = fileUtils.write2SDFromInput2(path, fileName, inputStream);  
	                if(resultFile == null){  
	                    return -1;  
	                }
                }
                else
                {
                	return 2;
                }
            }  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
            return -1;  
        }
        finally{  
            try {  
                inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return 0;  
    } 
    
    /**  
     * 根据URL得到输入流  
     * @param urlStr  
     * @return  
     */  
    public InputStream getInputStreamFromURL(String urlStr) {  
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null; 
        URL url = null;
        try {  
            url = new URL(urlStr);  
            urlConn = (HttpURLConnection)url.openConnection();  
            urlConn.connect();
            inputStream = urlConn.getInputStream();  
              
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return inputStream;  
    } 
	
	public String downFile2(String imgurl,String fileName) throws Exception{  
		DataOutputStream writer = null;
		String path = null;
		byte[] buf = null;
		InputStream inPutStream = null;
		try{
			inPutStream = getInputStreamFromURL(imgurl);
			path = EnvironmentShare.getDownAudioRecordDir().getAbsolutePath()+ "/" + fileName;
			System.out.println("filepath==="+path);
			File downLoadFile  = new File(path);
			downLoadFile.createNewFile();
	//File downLoadFile = File.createTempFile( fileName, ".amr", EnvironmentShare.getDownAudioRecordDir());
			// 2. 获得文件的输出流
			writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(downLoadFile)));   
			
			int bufferSize = 2048; //2K   
			buf = new byte[bufferSize];   
			int read = 0;   
			// 将文件输入流 循环 读入 Socket的输出流中 
			while((read = inPutStream.read(buf)) != -1){   
				writer.write(buf, 0, read);   
			} 
			path = "file://"+path;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				buf = null;
				inPutStream.close();
				writer.close();
			} catch (Exception e) {
				
			}
		}
		return path;
	}
	
	public String downFile(String imgurl,String fileName) throws Exception{  
		DataOutputStream writer = null;
		String path = null;
		byte[] buf = null;
		InputStream inPutStream = null;
		try{
			if(imgurl == null)
				return null;
			inPutStream = getInputStreamFromURL(imgurl);
			path = EnvironmentShare.getDownAudioRecordDir().getAbsolutePath()+ "/" + fileName;
			System.out.println("filepath==="+path);
			File downLoadFile  = new File(path);
			downLoadFile.createNewFile();
	//File downLoadFile = File.createTempFile( fileName, ".amr", EnvironmentShare.getDownAudioRecordDir());
			// 2. 获得文件的输出流
			writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(downLoadFile)));   
			
			int bufferSize = 2048; //2K   
			buf = new byte[bufferSize];   
			int read = 0;   
			// 将文件输入流 循环 读入 Socket的输出流中 
			while((read = inPutStream.read(buf)) != -1){   
				writer.write(buf, 0, read);   
			} 
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				buf = null;
				inPutStream.close();
				writer.close();
			} catch (Exception e) {
				
			}
		}
		return path;
	}
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		if(value == null)
			return null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    opt.inSampleSize = 2;
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
//			bitmap = Bitmap.createScaledBitmap(bitmap,wit,hig,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
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
