package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.imagegrid.HackyViewPager;
import ms.globalclass.imagegrid.PhotoView;
import ms.globalclass.imagegrid.PhotoViewAttacher.OnPhotoTapListener;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StoreImagesActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static StoreImagesActivity instance;
	
	private List<Map<String, Object>> dataList;
	private HackyViewPager mViewPager;
	public LinearLayout headLL;
//	public LinearLayout footBtnLL;
	public int total;
	public int index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_images_view);
		
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		String sortName = bunde.getString("sortName");
		index = bunde.getInt("index");
		
		TextView text = (TextView)findViewById(R.id.title_lable);
		text.setText(sortName);
		
		dataList = myapp.getStoreImages();
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openStoreMainView();
			}
		});
		
		initView();
	}
	
	public void initView()
	{
		try{
			total = dataList.size();
			
			headLL = (LinearLayout) findViewById(R.id.head);
			
			List<Bitmap> list = new ArrayList<Bitmap>();
			for(int i=0;i<dataList.size();i++)
			{
				Map<String,Object> map = dataList.get(i);
				Bitmap bitmap = (Bitmap)map.get("imgbitmap");
				list.add(bitmap);
			}
			
			mViewPager = (HackyViewPager) findViewById(R.id.hackyViewPager);
			SamplePagerAdapter adapter = new SamplePagerAdapter();
			adapter.setMActivity(this);
			adapter.setImgList(list);
			mViewPager.setAdapter(adapter);
			mViewPager.setCurrentItem(index);
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					Map<String, Object> map = dataList.get(arg0);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	static class SamplePagerAdapter extends PagerAdapter {
		private Activity mActivity;
		private List<Bitmap> imgList;

		public void setMActivity(Activity activity) {
			mActivity = activity;
		}

		public void setImgList(List<Bitmap> list) {
			imgList = list;
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			photoView.setImageBitmap(imgList.get(position));
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

				@Override
				public void onPhotoTap(View view, float x, float y) {
					// TODO Auto-generated method stub
					System.out.println("图片单击");
					if (((StoreImagesActivity) mActivity).headLL.getVisibility() == View.GONE) {
						((StoreImagesActivity) mActivity).headLL
								.setVisibility(View.VISIBLE);
//						((StoreImagesActivity) mActivity).footBtnLL
//								.setVisibility(View.VISIBLE);
					} else {
						((StoreImagesActivity) mActivity).headLL
								.setVisibility(View.GONE);
//						((StoreImagesActivity) mActivity).footBtnLL
//								.setVisibility(View.GONE);
					}
				}
			});

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openStoreMainView();
			return false;
		}
		return false;
	}
	
	public void openStoreMainView()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass( this,StoreMainActivity.class);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
}
