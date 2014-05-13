package ms.activitys.coupon;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ms.globalclass.StreamTool;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.activitys.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class UseCoupon  extends Activity{
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	
	String index;
	String sindex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
		
		setContentView(R.layout.use_coupon_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = UseCoupon.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getString("cindex");
		sindex = bunde.getString("sindex");
		
		showCouponView();
		
	}
	
	public void showCouponView()
	{
		try{
			List<Map<String,Object>> list = myapp.getCouponAll();
			Map<String,Object> map = list.get(Integer.valueOf(index));
			Bitmap simgb = (Bitmap)map.get("simg");
			String couponName = (String)map.get("couponName");
			String couponNo = (String)map.get("couponNo");
			
			System.out.println("couponName==========" +couponName);
			
			ImageView simg = (ImageView)findViewById(R.id.coupon_image);
			simg.setImageBitmap(simgb);
			
			TextView cname = (TextView)findViewById(R.id.coupon_name);
			cname.setText(couponName);
			
			Button cbtn = (Button)findViewById(R.id.close_btn);
			cbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
//					UseCoupon.this.setResult(RESULT_OK, getIntent());
//					UseCoupon.this.finish();
					closePage();
				}
			});
			
			ImageView noimg = (ImageView)findViewById(R.id.coupon_noimg);
			if(StreamTool.isNetworkVailable(this))
				noimg.setImageBitmap(getImageBitmap("http://"+myapp.getHost()+":80/customize/control/BarcodeServlet?msg="+couponNo));
			else
			{
				Bitmap bimg = (Bitmap)map.get("bimg");
				noimg.setImageBitmap(bimg);
			}
			
			TextView notext = (TextView)findViewById(R.id.coupon_no);
			notext.setText(couponNo);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			new AlertDialog.Builder(this).setTitle("提示")
			.setMessage(this.getString(R.string.coupon_lable_28)).setIcon(R.drawable.error2)
			.setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
//							UseCoupon.this.setResult(RESULT_OK, getIntent());
//							UseCoupon.this.finish();
							showMyCouponView();
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 取消按钮事件
						}
					}).show();
			return false;
		}
		return false;
	}
	
	public void closePage()
	{
		try{
			new AlertDialog.Builder(this).setTitle(this.getString(R.string.setting_title))
			.setMessage(this.getString(R.string.coupon_lable_28)).setIcon(R.drawable.error2)
			.setPositiveButton(this.getString(R.string.coupon_lable_14),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
//							UseCoupon.this.setResult(RESULT_OK, getIntent());
//							UseCoupon.this.finish();
							showMyCouponView();
						}
					}).setNegativeButton(this.getString(R.string.coupon_lable_16),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 取消按钮事件
						}
					}).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示我的优惠券界面
	 */
	public void showMyCouponView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", sindex);
			bundle.putString("tag", "my");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
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
//		    opt.inSampleSize = 8;
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
//			bitmap = Bitmap.createScaledBitmap(bitmap,300,188,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
}
