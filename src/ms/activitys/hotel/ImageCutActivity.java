package ms.activitys.hotel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ms.activitys.R;
import ms.globalclass.imagegrid.BitmapUtil;
import ms.globalclass.imagegrid.MyDragImageView;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;

public class ImageCutActivity extends Activity {

	private int window_width, window_height;// 控件宽度
	private MyDragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度

	private ViewTreeObserver viewTreeObserver;

	private ImageView imgMask;
	private int imgMaskX;
	private int imgMaskY;
	private int imgMaskW;
	private int imgMaskH;

	private Button saveBtn;
	private Bitmap bitmap;
	
	private Bitmap picBmp;//原图

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagecut);

		Bundle extras = getIntent().getExtras();
		Uri uri = (Uri) extras.get("uri");
		picBmp = null;
		try {
			picBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
					uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		// 设置图片
		dragImageView = (MyDragImageView) findViewById(R.id.imagecut_img);
		dragImageView.setmActivity(ImageCutActivity.this);// 注入Activity.
		/** 测量状态栏高度 **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// 获取状况栏高度
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height
									- state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});

		imgMask = (ImageView) findViewById(R.id.imagecut_mask);
		ViewTreeObserver vto2 = imgMask.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int[] location = new int[2];
				imgMask.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				System.out.println("x:" + x + "y:" + y);
				imgMaskX = x + 10;
				imgMaskY = y + 10;

				imgMask.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				System.out.println("w:" + imgMask.getHeight() + "h:"
						+ imgMask.getWidth());
				imgMaskW = imgMask.getWidth() - 20;
				imgMaskH = imgMask.getHeight() - 20;

				if (picBmp != null){
					dragImageView.setImageBitmap(BitmapUtil.getBitmap(picBmp, imgMask.getWidth(), imgMask.getHeight()));
				}
			}
		});

		saveBtn = (Button) findViewById(R.id.imagecut_saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = getWindow().getDecorView();
				Bitmap b = getViewBitmap(view);
				bitmap = Bitmap.createBitmap(b, imgMaskX, imgMaskY, imgMaskW,
						imgMaskH);
				dragImageView.setDrawingCacheEnabled(false);

				((MyApp) getApplicationContext()).setCutImg(bitmap);

				MomentsActivity.instance.saveCutImg();
				overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
				finish();
			}
		});
	}

	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
}
