package ms.activitys.dodowaternew;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {

	private AnimationDrawable loadingAnimation;
	private FlowTag flowTag;
	private Context context;
	public Bitmap bitmap;
	private ImageLoaderTask task;
	private int columnIndex;// ͼƬ���ڵڼ���
	private int rowIndex;// ͼƬ���ڵڼ���
	private Handler viewHandler;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {

		setOnClickListener(this);
		this.setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("FlowView", "LongClick");
		Toast.makeText(context, "������" + this.flowTag.getFlowId(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d("FlowView", "Click");
		Toast.makeText(context, "������" + this.flowTag.getFlowId(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * ����ͼƬ
	 */
	public void LoadImage() {
		if (getFlowTag() != null) {

			new LoadImageThread().start();
		}
	}

	/**
	 * ���¼���ͼƬ
	 */
	public void Reload() {
		if (this.bitmap == null && getFlowTag() != null) {

			new ReloadImageThread().start();
		}
	}

	/**
	 * �����ڴ�
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public FlowTag getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag) {
		this.flowTag = flowTag;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}
	
	public Bitmap returnBitMap(String url,Bitmap bmp) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		if(url != null && !url.equals(""))
		{
			try {
				System.out.println("ssssssssurl===="+url);
				if(url == null || url.equals(""))
					return null;
				myFileUrl = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				
				BitmapFactory.Options opt = new BitmapFactory.Options();  
			    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
			    opt.inPurgeable = true;  
			    opt.inInputShareable = true;  
			    opt.inSampleSize = 2;
			    
				bitmap = BitmapFactory.decodeStream(is,null,opt);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try{
			    bitmap = bmp; 
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return bitmap;
	}

	class ReloadImageThread extends Thread {

		@Override
		public void run() {
			if (flowTag != null) {

				BufferedInputStream buf;
				try {
					buf = new BufferedInputStream(flowTag.getAssetManager()
							.open(flowTag.getFileName()));
					bitmap = BitmapFactory.decodeStream(buf);

				} catch (IOException e) {

					e.printStackTrace();
				}

				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null) {// �˴����̹߳���ʱ����Ϊnull
							setImageBitmap(bitmap);
						}
					}
				});
			}

		}
	}
	

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}
		public void run() {

			if (flowTag != null) {

//				BufferedInputStream buf;
				try {
//					buf = new BufferedInputStream(flowTag.getAssetManager()
//							.open(flowTag.getFileName()));
//					bitmap = BitmapFactory.decodeStream(buf);ssss
					bitmap = returnBitMap(flowTag.getUrlstr(),flowTag.getNotimg());

				} catch (Exception e) {

					e.printStackTrace();
				}
				// if (bitmap != null) {

				// �˴�����ֱ�Ӹ���UI������ᷢ���쳣��
				// CalledFromWrongThreadException: Only the original thread that
				// created a view hierarchy can touch its views.
				// Ҳ����ʹ��Handler����Looper����Message����������

				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null) {// �˴����̹߳���ʱ����Ϊnull
							int width = bitmap.getWidth();// ��ȡ��ʵ���
							int height = bitmap.getHeight();

							LayoutParams lp = getLayoutParams();

							int layoutHeight = (height * flowTag.getItemWidth())
									/ width;// �����߶�
							if (lp == null) {
								lp = new LayoutParams(flowTag.getItemWidth(),
										layoutHeight);
							}
							setLayoutParams(lp);

							setImageBitmap(bitmap);
							Handler h = getViewHandler();
							Message m = h.obtainMessage(flowTag.what, width,
									layoutHeight, FlowView.this);
							h.sendMessage(m);
						}
					}
				});

				// }

			}

		}
	}
}
