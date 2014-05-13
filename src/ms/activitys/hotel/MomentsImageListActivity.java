package ms.activitys.hotel;

import java.util.List;

import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.httppost.Douban;
import ms.globalclass.imagegrid.HackyViewPager;
import ms.globalclass.imagegrid.PhotoView;
import ms.globalclass.imagegrid.PhotoViewAttacher.OnPhotoTapListener;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MomentsImageListActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	public static MomentsImageListActivity instance;
	
	private HackyViewPager mViewPager;
	private List<Bitmap> imglist;
	private List<String> imgNamelist;
	private LinearLayout headlayout;
	private TextView contentxt;
	public int total;
	public int index;
	private String content;
	private int curreIndex;
	public static FileUtils fileUtil = new FileUtils();
	private int zhansize;
	private String curPublishID;
	public Button zhan_btn;
	private MyLoadingDialog loadDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_images_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		instance = this;
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		content = bunde.getString("content");
		curPublishID = bunde.getString("curPublishID");
		zhansize = bunde.getInt("zhansize");
		curreIndex = index;
		
		headlayout = (LinearLayout)findViewById(R.id.head);
//		headlayout.getBackground().setAlpha(100);
//		headlayout.setVisibility(View.GONE);
		
		imglist = myapp.getMomentsimgs();
		imgNamelist = myapp.getMomentsimgnames();
		
		contentxt = (TextView)findViewById(R.id.content_txt);
		if(content != null && !content.equals(""))
		{
			contentxt.getBackground().setAlpha(100);
			contentxt.setText(content);
		}
		else
		{
			contentxt.setVisibility(View.GONE);
		}
		
		ImageButton save_btn = (ImageButton)findViewById(R.id.save_btn);
		save_btn.getBackground().setAlpha(100);
		save_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveImageLocal();
			}
		});
		
		zhan_btn = (Button)findViewById(R.id.zhan_btn);
		zhan_btn.setText(String.valueOf(zhansize));
		zhan_btn.getBackground().setAlpha(100);
		zhan_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMyLoadingDialog(getString(R.string.hotel_label_200));
				MomentsActivity.instance.saveCommentZhan(curPublishID);
			}
		});
		
		initView();
	}
	
	public void setZhanSize()
	{
		if(loadDialog != null)
			loadDialog.dismiss();
		zhan_btn.setText(String.valueOf(zhansize+1));
	}
	
	public void initView()
	{
		try{
			total = imglist.size();
			
			mViewPager = (HackyViewPager) findViewById(R.id.hackyViewPager);
			SamplePagerAdapter adapter = new SamplePagerAdapter();
			adapter.setMActivity(this);
			adapter.setImgList(imglist);
			mViewPager.setAdapter(adapter);
			mViewPager.setCurrentItem(index);
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					curreIndex = arg0;
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
	
	public void saveImageLocal()
	{
		try{
			Bitmap bitmap = imglist.get(curreIndex);
			String filename = imgNamelist.get(curreIndex);
			String furl = fileUtil.getSaveImageLocalPath(filename+".jpg");
			if(!fileUtil.isFileExist("hereimg"))
				fileUtil.createSDDir("/hereimg");
			
			if(!fileUtil.isFileExist3(furl))
			{
				fileUtil.saveMyBitmap(furl, bitmap);
				Toast.makeText(this, getString(R.string.hotel_label_199) + furl,  
	                    Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, getString(R.string.hotel_label_198),  
	                    Toast.LENGTH_LONG).show();  
			}
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
					MomentsImageListActivity.instance.openMomentsView();
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
			openMomentsView();
			return false;
		}
		return false;
	}
	
	public void openMomentsView()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass( this,MomentsActivity.class);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
	    this.finish();
	}
	
	public void showMyLoadingDialog(String message) {
		loadDialog = new MyLoadingDialog(this, message, R.style.MyDialog);
		loadDialog.show();
	}
}
