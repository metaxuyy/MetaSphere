package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.imagegrid.HackyViewPager;
import ms.globalclass.imagegrid.PhotoView;
import ms.globalclass.imagegrid.PhotoViewAttacher.OnPhotoTapListener;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelImageShowActivity extends Activity {
	
	public static SelImageShowActivity instance;

	private List<Map<String, Object>> dataList;
	private HackyViewPager mViewPager;
	private TextView timeTV;
	private Button imgSelBtn;
	private Button saveBtn;
	private Button backBtn;
	private LinearLayout headLL;
	private RelativeLayout footLL;
	private LinearLayout selPicNumLL;
	private TextView selPicNumTV;
	private ProgressDialog mypDialog;
	private List<String> list;

	private int index;
	private List<Map<String, Object>> picList;
	private List<Map<String, Object>> selPicList;

	public Map<String, Bitmap> curBitmap = new HashMap<String, Bitmap>();
	public boolean isFirstLoad = true;
	public static int window_width;// 控件宽度
	public static int window_height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selimageshow);
		
		instance = this;

		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		Bundle extras = getIntent().getExtras();
		index = extras.getInt("index");
		picList = ((MyApp) getApplicationContext()).getPicList();
		selPicList = ((MyApp) getApplicationContext()).getSelPicList();

		dataList = new ArrayList<Map<String, Object>>();
		list = new ArrayList<String>();

		for (int i = 0; i < picList.size(); i++) {
			Map<String, Object> map1 = addImgData(
					(String) ((Map<String, Object>) picList.get(i)).get("time"),
					(String) ((Map<String, Object>) picList.get(i)).get("path"));
			dataList.add(map1);
			list.add((String) map1.get("imgurl"));
		}

		mViewPager = (HackyViewPager) findViewById(R.id.selimgshow_hackyViewPager);
		headLL = (LinearLayout) findViewById(R.id.selimgshow_head);
		footLL = (RelativeLayout) findViewById(R.id.selimgshow_foot);
		timeTV = (TextView) findViewById(R.id.selimgshow_timeTV);
		saveBtn = (Button) findViewById(R.id.selimgshow_saveBtn);
		imgSelBtn = (Button) findViewById(R.id.selimgshow_selBtn);
		selPicNumLL = (LinearLayout) findViewById(R.id.selimgshow_selNumLL);
		selPicNumTV = (TextView) findViewById(R.id.selimgshow_selNumTV);
		backBtn = (Button) findViewById(R.id.selimgshow_backBtn);

		if (selPicList.size() > 0) {
			selPicNumLL.setVisibility(View.VISIBLE);
			selPicNumTV.setText(selPicList.size() + "");
		}

		SamplePagerAdapter adapter = new SamplePagerAdapter();
		adapter.setMActivity(this);
		adapter.setImgList(list);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				Map<String, Object> map = dataList.get(arg0);
				timeTV.setText((String) map.get("time"));
				if (selPicList.contains(picList.get(arg0))) {
					imgSelBtn
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					imgSelBtn.setSelected(true);
				} else {
					imgSelBtn
							.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					imgSelBtn.setSelected(false);
				}

				isFirstLoad = false;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0);
				if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
					int curIndex = mViewPager.getCurrentItem();
					String curImgPath = list.get(curIndex);
					String beforePath = "";
					String afterPath = "";
					if (curIndex > 0) {
						beforePath = list.get(mViewPager.getCurrentItem() - 1);
					}
					if (curIndex < (list.size() - 1)) {
						afterPath = list.get(mViewPager.getCurrentItem() + 1);
					}

					if (!isFirstLoad) {
						List<String> temp = new ArrayList<String>();
						for (String key : curBitmap.keySet()) {
							if (!key.equals(curImgPath)
									&& !key.equals(beforePath)
									&& !key.equals(afterPath)) {
								temp.add(key);
							}
						}
						if (temp.size() > 0) {
							for (int i = 0; i < temp.size(); i++) {
								Bitmap b = curBitmap.get(temp.get(i));
								curBitmap.remove(temp.get(i));
								if (b != null) {
									b.recycle();
									b = null;
								}
							}
						}
					}
				}
			}
		});
		mViewPager.setCurrentItem(index);
		if (selPicList.contains(picList.get(index))) {
			imgSelBtn
					.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
			imgSelBtn.setSelected(true);
		}

		imgSelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selPicList.size() == 9) {
					Toast.makeText(SelImageShowActivity.this, "一次最多上传9张图片",
							Toast.LENGTH_SHORT).show();
					return;
				}

				int curIndex = mViewPager.getCurrentItem();
				Map<String, Object> selMap = picList.get(curIndex);
				if (v.isSelected()) {
					v.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					selPicList.remove(selMap);
				} else {
					v.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					selPicList.add(selMap);
				}

				if (selPicList.size() > 0) {
					selPicNumLL.setVisibility(View.VISIBLE);
					selPicNumTV.setText(selPicList.size() + "");
				} else {
					selPicNumLL.setVisibility(View.GONE);
				}
			}
		});

		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MyApp) getApplicationContext()).setSelPicList(selPicList);

//				Intent intent = new Intent();
//				setResult(1, intent);
				SelImageActivity.instance.refreshList();
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
			}
		});

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
				recycleAllBitmapCaches();
			}
		});
	}
	
	@Override
	public void finish(){
		super.finish();
		//recycleAllBitmapCaches();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		recycleAllBitmapCaches();
	}
	
	// 清空全部图片
	public void recycleAllBitmapCaches() {
		List<String> temp = new ArrayList<String>();
		for (String key : curBitmap.keySet()) {
				temp.add(key);
		}
		if (temp.size() > 0) {
			for (int i = 0; i < temp.size(); i++) {
				Bitmap b = curBitmap.get(temp.get(i));
				curBitmap.remove(temp.get(i));
				if (b != null) {
					b.recycle();
					b = null;
				}
			}
		}
	}

	private Map<String, Object> addImgData(String time, String imgUrl) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", time);
		map.put("imgdata", null);
		map.put("imgurl", imgUrl);

		return map;
	}

	public void showMenu() {
		TranslateAnimation mShowActionUp = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		mShowActionUp.setDuration(500);
		headLL.startAnimation(mShowActionUp);
		headLL.setVisibility(View.VISIBLE);

		TranslateAnimation mShowActionDown = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		mShowActionDown.setDuration(500);
		footLL.startAnimation(mShowActionDown);
		footLL.setVisibility(View.VISIBLE);
	}

	public void hideMenu() {
		TranslateAnimation mHiddenActionUp = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);
		mHiddenActionUp.setDuration(500);
		headLL.startAnimation(mHiddenActionUp);
		headLL.setVisibility(View.GONE);

		TranslateAnimation mHiddenActionDown = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		mHiddenActionDown.setDuration(500);
		footLL.startAnimation(mHiddenActionDown);
		footLL.setVisibility(View.INVISIBLE);
	}

	private void showProgressDialog() {
		try {
			mypDialog = new ProgressDialog(SelImageShowActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("正在加载图片...");
			mypDialog.setIndeterminate(false);
			mypDialog.setCancelable(true);
			mypDialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static class SamplePagerAdapter extends PagerAdapter {
		private Activity mActivity;
		private List<String> imgList;

		public void setMActivity(Activity activity) {
			mActivity = activity;
		}

		public void setImgList(List<String> list) {
			imgList = list;
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			String curImgPath = imgList.get(position);
			Bitmap curImgBitmap = getBitmapByPath(curImgPath);
			if (curImgBitmap != null && !curImgBitmap.isRecycled())
				photoView.setImageBitmap(curImgBitmap);
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

				@Override
				public void onPhotoTap(View view, float x, float y) {
					if (((SelImageShowActivity) mActivity).headLL
							.getVisibility() == View.GONE) {
						((SelImageShowActivity) mActivity).showMenu();
					} else {
						((SelImageShowActivity) mActivity).hideMenu();
					}
				}
			});

			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			SelImageShowActivity act = (SelImageShowActivity) mActivity;
			act.curBitmap.put(curImgPath, curImgBitmap);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			System.out.println("destroyItem");
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		private Bitmap getBitmapByPath(String bitmapPath) {
			Bitmap bitmap = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			bitmap = BitmapFactory.decodeFile(bitmapPath, options); // 此时返回bm为空
			options.inJustDecodeBounds = false;
			// 计算缩放比
			int be = (int) (options.outHeight / window_height);
			if (be <= 0)
				be = 1;
			options.inSampleSize = be;
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
			bitmap = BitmapFactory.decodeFile(bitmapPath, options);

			Bitmap rBitmap = ms.globalclass.imagegrid.BitmapUtil
					.rotateBitmap(bitmapPath, bitmap);
			if (rBitmap != null) {
				return rBitmap;
			} else {
				return bitmap;
			}
		}
	}
}
