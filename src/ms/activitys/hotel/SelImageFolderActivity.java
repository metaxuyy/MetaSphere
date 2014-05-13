package ms.activitys.hotel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ms.activitys.R;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.imagegrid.BitmapUtil;
import ms.globalclass.listviewadapter.ImageFolderAdapter;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SelImageFolderActivity extends Activity {

	private GridView gridView;
	private List<Map<String, Object>> list;
	private ImageFolderAdapter adapter;
	private Button backBtn;
	private MyLoadingDialog loadDialog;
	private MyApp myapp;

	private Cursor cursor;
	private int photoIndex;
	private int photoSize;
	private int photoAddDate;
	private int photoUpdateDate;
	private int photoTitle;
	private Map<String, List<Map<String, Object>>> picFolder_map;// 图片文件夹数组(文件夹路径：所属图片集合)

	// 用来保存GridView中每个Item的图片，以便释放
	public static Map<String, Bitmap> gridviewBitmapCaches = new HashMap<String, Bitmap>();
	private int from;
	private int to;
	private int total;
	public int c = 0; // 记录gridview加载个数，小于30加载图片，即第一页
	public boolean isFirstLoad = true; // 记录是否第一次加载

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selimagefolder);
		
		myapp = (MyApp) this.getApplicationContext();
		
		Button backBtn = (Button) findViewById(R.id.selImageBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SelImageFolderActivity.this, MomentsActivity.class);
				startActivity(intent);// 开始界面的跳转函数
//				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
				overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
				finish();
			}
		});

		gridView = (GridView) findViewById(R.id.imageFolder_gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Map<String, Object> map = list.get(position);
				Intent intent = new Intent(SelImageFolderActivity.this,
						SelImageActivity.class);
				intent.putExtra("folderName", (String) map.get("folderName"));
				((MyApp) getApplicationContext())
						.setPicList((List<Map<String, Object>>) map
								.get("folderList"));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
		});
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
						String path = (String) map.get("folderImg");

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

						if (gridviewBitmapCaches.containsKey(map.get("folderImg"))) {
							Bitmap b = gridviewBitmapCaches.get(map.get("folderImg"));
							imgView.setImageBitmap(b);
						} else {
							loadImage((String) map.get("folderImg"), imgView, i);
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

//		backBtn = (Button) findViewById(R.id.imageFolder_cancleBtn);
//		backBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(SelImageFolderActivity.this, MomentsActivity.class);
//				startActivity(intent);// 开始界面的跳转函数
////				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
//				overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
//				finish();
//			}
//		});

		showMyLoadingDialog();
		getImagesFolder();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(SelImageFolderActivity.this, MomentsActivity.class);
			startActivity(intent);// 开始界面的跳转函数
//			overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
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

	private void getImagesFolder() {
		final Activity a = this;
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;

				String columns[] = new String[] { Media.DATA, Media.SIZE,
						Media.DATE_ADDED, Media.DATE_MODIFIED, Media.TITLE,
						Media._ID };
				cursor = a.getContentResolver().query(
						Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
				photoIndex = cursor.getColumnIndexOrThrow(Media.DATA);
				photoSize = cursor.getColumnIndexOrThrow(Media.SIZE);
				photoAddDate = cursor.getColumnIndexOrThrow(Media.DATE_ADDED);
				photoUpdateDate = cursor
						.getColumnIndexOrThrow(Media.DATE_MODIFIED);
				photoTitle = cursor.getColumnIndexOrThrow(Media.TITLE);

				String path = "";
				String size = "";
				String addDate = "";
				String updateDate = "";
				String title = "";
				File file = new File("");
				File parentFile = new File("");
				picFolder_map = new HashMap<String, List<Map<String, Object>>>();
				while (cursor.moveToNext()) {
					path = cursor.getString(photoIndex);
					if(path==null || path.equals("")){
						continue;
					}
					size = cursor.getString(photoSize);
					addDate = getDateTimeByMillisecond(cursor
							.getString(photoAddDate));
					updateDate = getDateTimeByMillisecond(cursor
							.getString(photoUpdateDate));
					title = cursor.getString(photoTitle);

					file = new File(path);
					parentFile = file.getParentFile();
					String folderPath = parentFile.getPath();
					boolean isHas = false;// 遍历是否已有这个文件夹
					if (picFolder_map.containsKey(folderPath)) {
						isHas = true;
					} else {
						isHas = false;
					}

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("path", path);
					map.put("title", title);
					map.put("size", size);
					map.put("time", updateDate);
					if (!isHas) {
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						list.add(map);

						picFolder_map.put(folderPath, list);
					} else {
						List<Map<String, Object>> list = picFolder_map.get(folderPath);
						list.add(map);
					}
				}

				msg.obj = "1";

				getImagesFolderHandler.sendMessage(msg);
			};
		}.start();
	}

	private Handler getImagesFolderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String tag = (String) msg.obj;
				if (tag != null && tag.equals("1")) {
					list = new ArrayList<Map<String, Object>>();
					Set<Entry<String, List<Map<String, Object>>>> set = picFolder_map
							.entrySet();
					for (Iterator<Map.Entry<String, List<Map<String, Object>>>> it = set
							.iterator(); it.hasNext();) {
						Map.Entry<String, List<Map<String, Object>>> entry = (Map.Entry<String, List<Map<String, Object>>>) it
								.next();
						String folderPathTotal = entry.getKey();
						String[] strArr = folderPathTotal.split("/");
						String folderPath = strArr[strArr.length - 1];
						List<Map<String, Object>> folderList = entry.getValue();
						String bitmapPath = (String) ((Map<String, Object>) folderList
								.get(0)).get("path");

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("folderList", folderList);
						map.put("folderNum", folderList.size() + "");
						map.put("folderName", folderPath);
						map.put("folderImg", bitmapPath);
						list.add(map);
					}
					getFileList();
				}
				if (loadDialog != null)
					loadDialog.dismiss();
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
			delBitmap = gridviewBitmapCaches.get(map.get("imgPath"));
			if (delBitmap != null) {
				// 如果非空则表示有缓存的bitmap，需要清理
				// 从缓存中移除该del->bitmap的映射
				gridviewBitmapCaches.remove(map.get("imgPath"));
				delBitmap.recycle();
				delBitmap = null;
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
//				Bitmap b = gridviewBitmapCaches.get(temp.get(i));
//				gridviewBitmapCaches.remove(temp.get(i));
//				if (b != null) {
//					b.recycle();
//					b = null;
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

	private void getFileList() {
		adapter = new ImageFolderAdapter(SelImageFolderActivity.this,myapp);
		adapter.setList(list);
		gridView.setAdapter(adapter);
	}

	public String getDateTimeByMillisecond(String str) {
		if (str == null)
			return "";
		Date date = new Date(Long.valueOf(str) * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = format.format(date);
		return time;
	}

	/********* 异步加载图片 *********/

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
				this.url = (String) info.get("folderImg");
				//bitmap = getBitmapByPath(url);
//				bitmap = BitmapUtil.compressImage(url, 100.0f, 100.0f);
				bitmap = myapp.getLoacalBitmap2(url, 100, 100,true);
				if(bitmap == null)
				{
					bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image_download_fail_icon);
				}
				SelImageFolderActivity.gridviewBitmapCaches.put(url, bitmap);
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
}
