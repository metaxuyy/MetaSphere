package ms.globalclass.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import ms.activitys.R;
import ms.activitys.travel.TravelNotesListActivity;
import ms.globalclass.FileUtils;


public class DisplayImage extends Activity implements OnTouchListener{

	/* 相关变量声明 */
	private Bitmap bmp;
//	private ImageZoomView mZoomView;
//	private ZoomState mZoomState;
//	private SimpleZoomListener mZoomListener;
	private String filepath;
	private boolean isDowload = false;
	private LinearLayout progLayout;
	private String path;
	public ProgressDialog pd;
	
	private static final String TAG = "Touch";
	private ImageView imgview;
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private int maxSize = 5;
	private int csize = 0;
	private ZoomControls zoomCtrl;
	private InputStream imgis;
	   
 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)    {
		super.onCreate(savedInstanceState);
		/* 加载display.xml Layout */
		setContentView(R.layout.image_view);
 
		/* 初始化相关变量 */
		path = this.getIntent().getExtras().getString("path");
 
		progLayout = (LinearLayout)findViewById(R.id.progLayout);
		
		
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
        		
				Bitmap bitm = getImageBitmap(path);
				msg.obj = bitm;
				handler.sendMessage(msg);
			}
		}.start();
		
		
		imgview = (ImageView)findViewById(R.id.pic);
		imgview.setOnTouchListener(this);
//		mZoomView=(ImageZoomView)findViewById(R.id.pic);
//		mZoomView.setOnTouchListener(this);
//		
//		mZoomState = new ZoomState();
//		mZoomView.setZoomState(mZoomState);
//        mZoomListener = new SimpleZoomListener();
//        mZoomListener.setZoomState(mZoomState);
        
//        mZoomView.setOnTouchListener(mZoomListener);
        
		
        zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);
        //图片放大  
        zoomCtrl.setOnZoomInClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
//                float z= mZoomState.getZoom()+0.25f;
//                mZoomState.setZoom(z);
//                mZoomState.notifyObservers();
//            	if(csize > maxSize)
//            	{
//            		zoomCtrl.setIsZoomInEnabled(false);
//            	}
//            	else
//            	{
//            		zoomCtrl.setIsZoomOutEnabled(true);
//            		csize++;
					int bmpWidth = bmp.getWidth();
					int bmpHeight = bmp.getHeight();
					// 设置图片放大但比例
					double scale = 1.25;
					// 计算这次要放大的比例
					scaleWidth = (float) (scaleWidth * scale);
					scaleHeight = (float) (scaleHeight * scale);
					// 产生新的大小但Bitmap对象
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					imgview.setImageBitmap(resizeBmp);
//            	}
            }
            
        });
        //图片减小  
        zoomCtrl.setOnZoomOutClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
//                float z= mZoomState.getZoom()-0.25f;
//                mZoomState.setZoom(z);
//                mZoomState.notifyObservers();
//            	if(csize == 0)
//            	{
//            		zoomCtrl.setIsZoomOutEnabled(false);
//            	}
//            	else
//            	{
//            		zoomCtrl.setIsZoomInEnabled(true);
//            		csize--;
					int bmpWidth = bmp.getWidth();
					int bmpHeight = bmp.getHeight();
					// 设置图片放大但比例
					double scale = 0.8;
					// 计算这次要放大的比例
					scaleWidth = (float) (scaleWidth * scale);
					scaleHeight = (float) (scaleHeight * scale);
					// 产生新的大小但Bitmap对象
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					imgview.setImageBitmap(resizeBmp);
//            	}
            }
        }); 
        
        Button backbtn = (Button)findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
			    intent.setClass( DisplayImage.this,TravelNotesListActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("tag", "back");
				intent.putExtras(bundle);
				
				DisplayImage.this.setResult(1, intent);
				DisplayImage.this.finish();
			}
		});
        
        Button savebtn = (Button)findViewById(R.id.save_btn);
        savebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isDowload)
				{
					pd=ProgressDialog.show(DisplayImage.this, null, DisplayImage.this.getString(R.string.img_lable_1), true, false);
					new Thread() {
						public void run() {
							Message msg = new Message();
							msg.what = 1;
			        		
							boolean b = dowloadImage();
							msg.obj = b;
							handler.sendMessage(msg);
						}
					}.start();
				}
				else
					makeText(DisplayImage.this.getString(R.string.img_lable_2));
			}
		});
	}
	
	@Override
	   public boolean onTouch(View v, MotionEvent event) {
			ImageView view = (ImageView) v;

	      // Dump touch event to log
//	      dumpEvent(event);

	      // Handle touch events here...
	      switch (event.getAction() & MotionEvent.ACTION_MASK) 
	      {
	      	  //设置拖拉模式 
		      case MotionEvent.ACTION_DOWN:
		         savedMatrix.set(matrix);
		         //設置初始點位置
		         start.set(event.getX(), event.getY());
		         Log.d(TAG, "mode=DRAG");
		         mode = DRAG;
		         break;
		       //设置多点触摸模式 
		      case MotionEvent.ACTION_POINTER_DOWN:
		         oldDist = spacing(event);
		         Log.d(TAG, "oldDist=" + oldDist);
		         if (oldDist > 10f) {
		            savedMatrix.set(matrix);
		            midPoint(mid, event);
		            mode = ZOOM;
		            Log.d(TAG, "mode=ZOOM");
		         }
		         break;
		      case MotionEvent.ACTION_UP:
		      case MotionEvent.ACTION_POINTER_UP:
		         mode = NONE;
		         Log.d(TAG, "mode=NONE");
		         break;
		       //若为DRAG模式，则点击移动图片 
		      case MotionEvent.ACTION_MOVE:
		         if (mode == DRAG) {
		            // ...
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x,
		                  event.getY() - start.y);
		         }
		         else if (mode == ZOOM) {
		            float newDist = spacing(event);
		            Log.d(TAG, "newDist=" + newDist);
		            if (newDist > 10f) {
		               matrix.set(savedMatrix);
		               float scale = newDist / oldDist;
		               matrix.postScale(scale, scale, mid.x, mid.y);
		            }
		         }
		         break;
		      }

		      view.setImageMatrix(matrix);
		      return true; // indicate event was handled
	   }
	
	/** Determine the space between the first two fingers */
	   private float spacing(MotionEvent event) {
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return FloatMath.sqrt(x * x + y * y);
	   }

	   /** Calculate the mid point of the first two fingers */
	   private void midPoint(PointF point, MotionEvent event) {
	      float x = event.getX(0) + event.getX(1);
	      float y = event.getY(0) + event.getY(1);
	      point.set(x / 2, y / 2);
	   }
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				bmp = (Bitmap)msg.obj;
				isDowload = true;
				imgview.setImageBitmap(bmp);
//				resetZoomState();
				progLayout.setVisibility(View.GONE);
				break;
			case 1:
				boolean b = (Boolean)msg.obj;
				if(b)
				{
					pd.dismiss();
					makeText(DisplayImage.this.getString(R.string.img_lable_3));
				}
				else
				{
					pd.dismiss();
					makeText(DisplayImage.this.getString(R.string.img_lable_4));
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
		    intent.setClass( DisplayImage.this,TravelNotesListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("tag", "back");
			intent.putExtras(bundle);
			
			DisplayImage.this.setResult(1, intent);
			DisplayImage.this.finish();
			return false;
		}
		return false;
	}
	
	private boolean dowloadImage(){
		boolean b = true;
		try{
			String filepath = getSDPath();
			if(filepath == null)
			{
				b = false;
			}
			else
			{
				FileUtils futil = new FileUtils();
				String fileName = path.substring(path.lastIndexOf("/")+1,path.length());
				futil.saveFile("metasphereimg", bmp, fileName);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return b;
	}
	
	public String getSDPath(){
		String adpath = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			adpath = sdDir.toString();
		}
		return adpath;
	      
	} 

//	private void resetZoomState() {
//        mZoomState.setPanX(0.5f);
//        mZoomState.setPanY(0.5f);
//        
//        final int mWidth = bmp.getWidth();
//        final int vWidth= mZoomView.getWidth();
//        Log.e("iw:",vWidth+"");
//        mZoomState.setZoom(1f);
//        mZoomState.notifyObservers();
//        
//    }
	
	public Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		System.out.println("menulist_imageurl==="+value);
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
			imgis = is;

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
