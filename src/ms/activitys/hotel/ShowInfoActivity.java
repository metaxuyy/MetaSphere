package ms.activitys.hotel;

import java.net.URL;

import ms.activitys.MainTabActivity;
import ms.activitys.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class ShowInfoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showinfo);

		Bundle bunde = this.getIntent().getExtras();
		String title = bunde.getString("title");
		String content = bunde.getString("content");

		TextView titleTV = (TextView) findViewById(R.id.showinfo_title);
		titleTV.setText(title);

//		TextView contentTV = (TextView) findViewById(R.id.showinfoContent);
//		contentTV.setMovementMethod(ScrollingMovementMethod.getInstance());// 滚动
//		contentTV.setText(Html.fromHtml(content));
//		
//		//网络图片
//		ImageGetter imgGetter = new Html.ImageGetter() {
//	        public Drawable getDrawable(String source) {
//	              Drawable drawable = null;
//	              URL url;  
//	              try {   
//	                  url = new URL(source);  
//	                  drawable = Drawable.createFromStream(url.openStream(), "");  //获取网路图片
//	              } catch (Exception e) {  
//	                  return null;  
//	              }  
//	              drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
//	                            .getIntrinsicHeight());
//	              return drawable; 
//	        }
//		};
//		
//		//本地图片
//		ImageGetter imgGetterLocal = new Html.ImageGetter() {
//		        public Drawable getDrawable(String source) {
//		              Drawable drawable = null;
//		               
//		              drawable = Drawable.createFromPath(source); //显示本地图片
//		              drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
//		                            .getIntrinsicHeight());
//		              return drawable; 
//		        }
//		};
//		
//		contentTV.setText(Html.fromHtml(content, imgGetter, null));
		
		Button backBtn = (Button) findViewById(R.id.showinfo_cancelBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShowInfoActivity.this.finish();
				overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			}
		});
		
		WebView webView = (WebView) findViewById(R.id.showinfoWebView);
		webView.loadDataWithBaseURL(null, content, "text/html",  "utf-8", null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ShowInfoActivity.this.finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			return false;
		}
		return false;
	}
}
