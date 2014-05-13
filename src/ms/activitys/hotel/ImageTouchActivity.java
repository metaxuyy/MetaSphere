package ms.activitys.hotel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ms.activitys.R;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.DragImageView;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class ImageTouchActivity extends Activity{

	private Douban api;
	private MyApp myapp;
	private ProgressDialog mypDialog;
	private SharedPreferences  share;
	
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
    private DisplayMetrics dm;
    private ImageView imgView;
    private Bitmap bitmap;
    
    private float minScaleR;// ��С���ű���
    private static final float MAX_SCALE = 4f;// ������ű���

    private static final int NONE = 0;// ��ʼ״̬
    private static final int DRAG = 1;// �϶�
    private static final int ZOOM = 2;// ����
    private int mode = NONE;

    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;
    private String fileUrl;
    private LinearLayout progLayout;
    private LinearLayout title_layout;
    private boolean menuShowed;
    private Animation showAction, hideAction;
    private DBHelperMessage db;
    private String messageid = null;
    
    private int window_width, window_height;// �ؼ����
	private DragImageView dragImageView;// �Զ���ؼ�
	private int state_height;// ״̬���ĸ߶�

	private ViewTreeObserver viewTreeObserver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scale);
        
        share = getSharedPreferences("perference", MODE_PRIVATE);
        api = new Douban(share,myapp);
        myapp = (MyApp)this.getApplicationContext();
        
        db = new DBHelperMessage(this,myapp);
        
        Bundle bunde = this.getIntent().getExtras();
        fileUrl = bunde.getString("flieUrls");
        if(bunde.containsKey("mid"))
        	messageid = bunde.getString("mid");
        
        Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMessageDetailView();
			}
		});
		
		title_layout = (LinearLayout) findViewById(R.id.title_layout);
//		title_layout.getBackground().setAlpha(150);
		
//		 // ������TranslateAnimation����
//        showAction = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//         // ������ScaleAnimation����
//        //showAction = new ScaleAnimation(
//        //    1.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//        showAction.setDuration(500);
//
//         // ������TranslateAnimation����        
//        hideAction = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
//         // ������ScaleAnimation����
//        //hideAction = new ScaleAnimation(
//        //        1.0f, 1.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//        hideAction.setDuration(500);
       
//        menuShowed = false;
//        title_layout.setVisibility(View.GONE);
		 
		progLayout = (LinearLayout)findViewById(R.id.progLayout);
        
//        imgView = (ImageView) findViewById(R.id.imag);// ��ȡ�ؼ�
//        imgView.setOnTouchListener(this);// ���ô�������
		
		/** ��ȡ��Ҋ����߶� **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
//		Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.huoying,
//				window_width, window_height);
//		// ����ͼƬ
//		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//ע��Activity.
		/** ����״̬���߶� **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// ��ȡ״�����߶�
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});
        
//        dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
        
        if(messageid != null && !messageid.equals(""))
        {
        	db.openDB();
        	bitmap = db.getImageBitmap(messageid);
        	db.closeDB();
        	if(bitmap != null)
        	{
        		bitmap = getBitmap(bitmap,window_width, window_height);
        		// ����ͼƬ
        		dragImageView.setImageBitmap(bitmap);
		        
		        progLayout.setVisibility(View.GONE);
        	}
        }
        else
        	loadImageData();
    }
    
    public void loadImageData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Bitmap bitmap = null;
					if(fileUrl.contains("http:"))
					{
						bitmap = returnBitMap(fileUrl);
					}
					else
					{
						bitmap = returnBitMap2(fileUrl);
					}
					msg.obj = bitmap;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    /***
	 * �ȱ���ѹ��ͼƬ
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Log.e("jj", "ͼƬ���" + w + ",screenWidth=" + screenWidth);
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		// ��֤ͼƬ������.
		matrix.postScale(scale, scale);
		// w,h��ԭͼ������.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Bitmap bitmaps = (Bitmap)msg.obj;
				if(bitmaps != null)
				{
					bitmaps = getBitmap(bitmaps,window_width, window_height);
					bitmap = bitmaps;// ��ȡͼƬ��Դ
					dragImageView.setImageBitmap(bitmap);
				}
				else
				{
					makeText("����ʧ��");
				}
				progLayout.setVisibility(View.GONE);
				break;
			}
		}
    };
    
    public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
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
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ,ֵԽ��ᵼ��ͼƬ������
			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
    
    public Bitmap returnBitMap2(String fileUrl) {
		Bitmap bitmap = null;
		try{
			FileInputStream fis = new FileInputStream(fileUrl);
			// �������ͼƬ �ڴ����������
			// Options ֻ����ͼƬ�ߴ��С��������ͼƬ���ڴ�
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ,ֵԽ��ᵼ��ͼƬ������
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
//			opts.inJustDecodeBounds = false;
			
			bitmap = BitmapFactory.decodeStream(fis,null,opts);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return bitmap;
	}

    /**
     * ����ľ���
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * ������е�
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMessageDetailView();
		}
		return false;
	}
    
    public void openMessageDetailView()
	{
    	if(bitmap != null)
    		bitmap.recycle();
		Intent intent = new Intent();
	    intent.setClass( this,MessageListActivity.class);//ǰ��һ����һ��Activity����һ����Ҫ��ת��Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//��ʼ�������ת����
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
    
    public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
