package ms.activitys.hotel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.imagegrid.BitmapUtil;
import ms.globalclass.listviewadapter.ImageAdapter;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class SelImageActivity extends Activity {
	
	public static SelImageActivity instance;

	private TextView folderNameTV;
	private Button backBtn;
	private Button cancleBtn;
	private Button showBtn;
	private Button saveBtn;
	private LinearLayout selPicNumLL;
	private TextView selPicNumTV;
	private MyApp myapp;

	private GridView gridView;
	private List<Map<String, Object>> list;
	private ImageAdapter adapter;
	private ProgressDialog mypDialog;

	private String folderName;
	private List<Map<String, Object>> picList;

	private List<Map<String, Object>> selPicList;

	private MyLoadingDialog loadDialog;

	// 用来保存GridView中每个Item的图片，以便释放
//	public static Map<String, Bitmap> gridviewBitmapCaches = new HashMap<String, Bitmap>();
	private static Map<String,WeakReference<Bitmap>> gridviewBitmapCaches = new HashMap<String,WeakReference<Bitmap>>();//图片资源缓存
	private int from;
	private int to;
	private int total;
	public int c = 0; // 记录gridview加载个数，小于30加载图片，即第一页
	public boolean isFirstLoad = true;// 记录是否第一次加载

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selimage);
		
		instance = this;

		myapp = (MyApp) this.getApplicationContext();
		
		Bundle extras = getIntent().getExtras();
		folderName = extras.getString("folderName");
		picList = ((MyApp) getApplicationContext()).getPicList();

		folderNameTV = (TextView) findViewById(R.id.selImageFolderName);
		folderNameTV.setText(folderName);

		backBtn = (Button) findViewById(R.id.selImageBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SelImageActivity.this, SelImageFolderActivity.class);
				startActivity(intent);// 开始界面的跳转函数
				overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
				finish();
			}
		});

//		cancleBtn = (Button) findViewById(R.id.selImageCancleBtn);
//		cancleBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(SelImageActivity.this, SelImageFolderActivity.class);
//				startActivity(intent);// 开始界面的跳转函数
//				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
//				finish();
//			}
//		});

		showBtn = (Button) findViewById(R.id.selImageShowBtn);
		showBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Map<String, Object>> picListTemp = new ArrayList<Map<String, Object>>();
				picListTemp.addAll(selPicList);
				((MyApp) getApplicationContext()).setPicList(picListTemp);
				((MyApp) getApplicationContext()).setSelPicList(selPicList);

				Intent intent = new Intent(SelImageActivity.this,
						SelImageShowActivity.class);
				intent.putExtra("index", 0);
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
			}
		});

		saveBtn = (Button) findViewById(R.id.selImageSaveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				
				((MyApp) getApplicationContext()).setSelPicList(selPicList);
				MomentsActivity.instance.openMomentsUploadView("");
				
				recycleAllBitmapCaches();
			}
		});

		selPicNumLL = (LinearLayout) findViewById(R.id.selImageNumLL);
		selPicNumTV = (TextView) findViewById(R.id.selImageNumTV);

		gridView = (GridView) findViewById(R.id.image_gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_IDLE:// 滑动后静止
					isFirstLoad = false;
					from = view.getFirstVisiblePosition();
					to = view.getLastVisiblePosition();
					total = gridView.getAdapter().getCount();
					System.out.println("滑动结束" + from + "~" + to + "~" + total);

					for (int i = from, j = 0; i <= to; i++, j++) {
						if (list.size() <= i)
							break;
						Map<String, Object> map = list.get(i);
						String path = (String) map.get("imgPath");

						RelativeLayout rl = null;
						RelativeLayout rlc = null;
						ImageView imgView = null;
						rl = (RelativeLayout) gridView.getChildAt(i - from);
						rlc = (rl != null ? (RelativeLayout) rl.getChildAt(0)
								: null);
						imgView = (rlc != null ? (ImageView) rlc.getChildAt(0)
								: null);
						if (imgView == null)
							continue;

						if (gridviewBitmapCaches.containsKey(map.get("imgPath"))) {
							Bitmap b = gridviewBitmapCaches.get(map.get("imgPath")).get();
							if(b != null)
								imgView.setImageBitmap(b);
							else
								loadImage((String) map.get("imgPath"), imgView, i);
						} else {
							loadImage((String) map.get("imgPath"), imgView, i);
						}
					}

					recycleBitmapCaches(0, from - 1);
					recycleBitmapCaches(to + 1, total);
					break;
				case SCROLL_STATE_FLING: // 手指离开屏幕后，惯性滑动

					break;
				case SCROLL_STATE_TOUCH_SCROLL:// 手指在屏幕上滑动

					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				recycleBitmapCaches(0, firstVisibleItem - 1);
				recycleBitmapCaches(firstVisibleItem + visibleItemCount + 1,
						totalItemCount);
			}
		});

		selPicList = new ArrayList<Map<String, Object>>();

		showMyLoadingDialog();
		setImages();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(SelImageActivity.this, SelImageFolderActivity.class);
			startActivity(intent);// 开始界面的跳转函数
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
			return false;
		}
		return false;
	}
	
	@Override
	public void finish(){
		super.finish();
		recycleAllBitmapCaches();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		recycleAllBitmapCaches();
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,
				getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}

	private void setImages() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				list = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < picList.size(); i++) {
					String bitmapPath = (String) ((Map<String, Object>) picList
							.get(i)).get("path");

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("imgPath", bitmapPath);
					if (selPicList.contains(picList.get(i))) {
						map.put("isSel", "1");
					} else {
						map.put("isSel", "0");
					}
					list.add(map);
				}

				msg.obj = "1";

				setImagesHandler.sendMessage(msg);
			};
		}.start();
	}

	private Handler setImagesHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String tag = (String) msg.obj;
				if (tag != null && tag.equals("1")) {
					adapter = new ImageAdapter(SelImageActivity.this);
					adapter.setList(list);
					adapter.setSelBtnClick(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (selPicList.size() == 9) {
								Toast.makeText(SelImageActivity.this,
										"一次最多上传9张图片", Toast.LENGTH_SHORT)
										.show();
								return;
							}

							RelativeLayout rl = (RelativeLayout) v.getParent()
									.getParent();
							Map<String, Object> selMap = picList.get(gridView
									.getPositionForView(rl));

							if (v.isSelected()) {
								v.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
								v.setSelected(false);
								selPicList.remove(selMap);
								((Map<String, Object>) list.get(gridView
										.getPositionForView(rl))).put("isSel",
										"0");
							} else {
								v.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
								v.setSelected(true);
								selPicList.add(selMap);
								((Map<String, Object>) list.get(gridView
										.getPositionForView(rl))).put("isSel",
										"1");
							}

							if (selPicList.size() > 0) {
								showBtn.setEnabled(true);
								saveBtn.setEnabled(true);
								selPicNumLL.setVisibility(View.VISIBLE);
								selPicNumTV.setText(selPicList.size() + "");
							} else {
								showBtn.setEnabled(false);
								saveBtn.setEnabled(false);
								selPicNumLL.setVisibility(View.GONE);
							}
						}
					});
					adapter.setImgClick(new OnClickListener() {

						@Override
						public void onClick(View v) {
							RelativeLayout rl = (RelativeLayout) v.getParent()
									.getParent();
							int index = gridView.getPositionForView(rl);

							((MyApp) getApplicationContext())
									.setPicList(picList);
							((MyApp) getApplicationContext())
									.setSelPicList(selPicList);

							Intent intent = new Intent(SelImageActivity.this,
									SelImageShowActivity.class);
							intent.putExtra("index", index);
							//startActivityForResult(intent, 1);
							startActivity(intent);
						}
					});
					gridView.setAdapter(adapter);

					if (loadDialog != null)
						loadDialog.dismiss();
				}
				break;
			default:
				break;
			}
		}

	};

	// 卸载图片的函数
	private void recycleBitmapCaches(int fromPosition, int toPosition) {
		Bitmap delBitmap = null;
		for (int del = fromPosition; del < toPosition; del++) {
			Map<String, Object> map = list.get(del);
//			ImageView imgview = adapter.getImageView(del);
			WeakReference<Bitmap> wrfbitmp = gridviewBitmapCaches.get(map.get("imgPath"));
			if (wrfbitmp != null) {
				delBitmap = wrfbitmp.get();
				// 如果非空则表示有缓存的bitmap，需要清理
				// 从缓存中移除该del->bitmap的映射
				if(delBitmap != null)
				{
					gridviewBitmapCaches.remove(map.get("imgPath"));
					delBitmap.recycle();
					delBitmap = null;
				}
//				imgview.setImageBitmap(null);
			}
		}
	}
	
	// 清空全部图片
	private void recycleAllBitmapCaches() {
//		List<String> temp = new ArrayList<String>();
//		for (String key : gridviewBitmapCaches.keySet()) {
//				temp.add(key);
//		}
//		if (temp.size() > 0) {
//			for (int i = 0; i < temp.size(); i++) {
//				WeakReference<Bitmap> wrfb = gridviewBitmapCaches.get(temp.get(i));
//				if (wrfb != null) {
//					gridviewBitmapCaches.remove(temp.get(i));
//					Bitmap b = wrfb.get();
//					if(b != null && !b.isRecycled())
//					{
//						b.recycle();
//						b = null;
//					}
//				}
//			}
//		}
		gridviewBitmapCaches.clear();
		for(int i=0;i<list.size();i++)
		{
			ImageView imgview = adapter.getImageView(i);
			if(imgview != null)
			{
				Bitmap bitmap = imgview.getDrawingCache();
				if(bitmap != null && !bitmap.isRecycled())
				{
					bitmap.recycle();
					bitmap = null;
					imgview.setImageBitmap(null);
				}
			}
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case 1:
//			refreshList();
//			break;
//		default:
//			break;
//
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
	
	public void refreshList(){
		selPicList = ((MyApp) getApplicationContext()).getSelPicList();
		if (selPicList.size() > 0) {
			showBtn.setEnabled(true);
			saveBtn.setEnabled(true);
			selPicNumLL.setVisibility(View.VISIBLE);
			selPicNumTV.setText(selPicList.size() + "");
		} else {
			showBtn.setEnabled(false);
			saveBtn.setEnabled(false);
			selPicNumLL.setVisibility(View.GONE);
		}

		int from = gridView.getFirstVisiblePosition();
		int to = gridView.getLastVisiblePosition();
		for (int i = 0; i < picList.size(); i++) {
			Map<String, Object> map = list.get(i);

			RelativeLayout rl = null;
			RelativeLayout rlc = null;
			Button btn = null;
			if (i >= from && i <= to) {
				rl = (RelativeLayout) gridView.getChildAt(i - from);
				rlc = (rl != null ? (RelativeLayout) rl.getChildAt(0)
						: null);
				btn = (rlc != null ? (Button) rlc.getChildAt(1) : null);
			}

			if (selPicList.contains(picList.get(i))) {
				map.put("isSel", "1");
				if (btn != null) {
					btn.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_selected);
					btn.setSelected(true);
				}
			} else {
				map.put("isSel", "0");
				if (btn != null) {
					btn.setBackgroundResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					btn.setSelected(false);
				}
			}
		}
		adapter.setList(list);
	}

	/************* 异步加载图片 ************/

	public void loadImage(String url, ImageView imgView, int index) {
		// 首先我们先通过cancelPotentialLoad方法去判断imageview是否有线程正在为它加载图片资源，
		// 如果有现在正在加载，那么判断加载的这个图片资源（url）是否和现在的图片资源一样，不一样则取消之前的线程（之前的下载线程作废）。
		// 见下面cancelPotentialLoad方法代码
		if (cancelPotentialLoad(url, imgView)) {
			AsyncLoadImageTask task = new AsyncLoadImageTask(imgView);
			LoadedDrawable loadedDrawable = new LoadedDrawable(task);
			imgView.setImageDrawable(loadedDrawable);
			task.execute(index);
		}
	}

	// 加载图片的异步任务
	private class AsyncLoadImageTask extends AsyncTask<Integer, Void, Bitmap> {
		private String url = null;
		private final WeakReference<ImageView> imageViewReference;

		public AsyncLoadImageTask(ImageView imageview) {
			super();
			// TODO Auto-generated constructor stub
			imageViewReference = new WeakReference<ImageView>(imageview);
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			Map<String, Object> info = list.get(params[0]);
			this.url = (String) info.get("imgPath");
			//bitmap = getBitmapByPath(url);
//			bitmap = BitmapUtil.compressImage(url, 100.0f, 100.0f);
			bitmap = myapp.getLoacalBitmap2(url, 100, 100,true);
			if(bitmap == null)
			{
				bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image_download_fail_icon);
			}
			WeakReference<Bitmap> bmp = new WeakReference<Bitmap>(bitmap);
			SelImageActivity.gridviewBitmapCaches.put(url, bmp);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap resultBitmap) {
			// TODO Auto-generated method stub
			if (isCancelled()) {
				resultBitmap = null;
			}
			if (imageViewReference != null) {
				ImageView imageview = imageViewReference.get();
				AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
				// Change bitmap only if this process is still associated with
				// it
				if (this == loadImageTask && resultBitmap != null
						&& !resultBitmap.isRecycled()) {

					imageview.setImageBitmap(resultBitmap);
					// imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				}
			}
			super.onPostExecute(resultBitmap);
		}
	}

	// 该类功能为：记录imageview加载任务并且为imageview设置默认的drawable
	public static class LoadedDrawable extends ColorDrawable {
		private final WeakReference<AsyncLoadImageTask> loadImageTaskReference;

		public LoadedDrawable(AsyncLoadImageTask loadImageTask) {
			super(Color.TRANSPARENT);
			loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(
					loadImageTask);
		}

		public AsyncLoadImageTask getLoadImageTask() {
			return loadImageTaskReference.get();
		}

	}

	private boolean cancelPotentialLoad(String url, ImageView imageview) {
		AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);

		if (loadImageTask != null) {
			String bitmapUrl = loadImageTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				loadImageTask.cancel(true);
			} else {
				// 相同的url已经在加载中.
				return false;
			}
		}
		return true;

	}

	private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview) {
		if (imageview != null) {
			Drawable drawable = imageview.getDrawable();
			if (drawable instanceof LoadedDrawable) {
				LoadedDrawable loadedDrawable = (LoadedDrawable) drawable;
				return loadedDrawable.getLoadImageTask();
			}
		}
		return null;
	}

//	private Bitmap getBitmapByPath(String bitmapPath) {
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		// 获取这个图片的宽和高
//		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options); // 此时返回bm为空
//		options.inJustDecodeBounds = false;
//		// 计算缩放比
//		int be = (int) (options.outHeight / (float) 60);
//		if (be <= 0)
//			be = 1;
//		options.inSampleSize = be;
//		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
//		bitmap = BitmapFactory.decodeFile(bitmapPath, options);
//
//		Bitmap rBitmap = ms.globalclass.imagegrid.BitmapUtil
//				.rotateBitmap(bitmapPath, bitmap);
//		if (rBitmap != null) {
//			return rBitmap;
//		} else {
//			return bitmap;
//		}
//	}
}
