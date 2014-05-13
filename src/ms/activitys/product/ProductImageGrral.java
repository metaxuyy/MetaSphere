package ms.activitys.product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.globalclass.httppost.Douban;
import ms.globalclass.image.DisplayImage;
import ms.globalclass.map.MyApp;
import ms.activitys.R;
import ms.activitys.travel.TravelNotesListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class ProductImageGrral extends Activity implements
		OnItemSelectedListener, ViewFactory {
//	private ImageSwitcher is;
	private Gallery gallery;

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String productId;
	
	private List<Map<String, Object>> imglist;
	private Bitmap resizeBmp;
	
	private ProgressBar pb;
	
	private String imageurl;
	private int width;
	private int height;
	private ImageView pic;
	private ZoomControls zoomCtrl;
	private Bitmap bmp;
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private int size = 1;

	Matrix matrix = new Matrix();
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_images_page);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = ProductImageGrral.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
            
        width = (int) dm.widthPixels;
        height = (int) (dm.heightPixels * 0.8);
		
		Bundle bunde = this.getIntent().getExtras();
		productId = bunde.getString("productId");
		
		pb = (ProgressBar)findViewById(R.id.probar);
		
		pic = (ImageView)findViewById(R.id.pic);

		loadThreadData();
		
		showImageList();
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				loadImageListData();
				
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				gallery.setAdapter(new ImageAdapter(ProductImageGrral.this));
				
				pb.setVisibility(View.GONE);
				gallery.setVisibility(View.VISIBLE);
//				is.setVisibility(View.VISIBLE);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void loadImageListData()
	{
		imglist = new ArrayList<Map<String, Object>>();
		try{
			JSONObject jobj = api.getProductImageList(productId);
			JSONArray jArr = (JSONArray) jobj.get("data");
			
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String localeString = ""; 
				if(dobj.has("localeString"))
					localeString = (String) dobj.get("localeString"); 
				
				String imageUrl = ""; 
				if(dobj.has("imageUrl"))
					imageUrl = (String) dobj.get("imageUrl");
				
				String typeid = ""; 
				if(dobj.has("typeid"))
					typeid = (String) dobj.get("typeid");
				
				String dataName = ""; 
				if(dobj.has("dataName"))
					dataName = (String) dobj.get("dataName");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("localeString", localeString);
				map.put("imageUrl", imageUrl);
				map.put("typeid", typeid);
				map.put("dataName", dataName);
				if(typeid.indexOf("image") >= 0)
				{
					map.put("img", getImageBitmap(imageUrl));
					imglist.add(map);
				}
			
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
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
			int filesize = is.available();
			int zoom = zoomImage(filesize);
			
			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    opt.inSampleSize = zoom;
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
//			bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public int zoomImage(int imageSize) {
		int zoom = 0;
		if (imageSize < 20480) { // 0-20k
			zoom = 0;
        } else if (imageSize < 71680) { // 20-70k
        	zoom = 0;
        } else if (imageSize < 307200) { // 50-300k
        	zoom = 2;
        } else if (imageSize < 819200) { // 300-800k
        	zoom = 4;
        } else if (imageSize < 1048576) { // 800-1024k
        	zoom = 6;
        } else {
        	zoom = 10;
        }

		return zoom;
	}
	
	public void showImageList()
	{
		zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);
        //图片放大  
        zoomCtrl.setOnZoomInClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
            	if(size < 5)
            	{
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
					
					pic.destroyDrawingCache() ;
					
					if(resizeBmp != null)
					{
						if(!resizeBmp.isRecycled() && resizeBmp != bmp){
		                    resizeBmp.recycle() ;
						}
						resizeBmp = null ;
						System.gc() ;
					}
		            
//		            VMRuntime.getRuntime().gcSoftReferences() ;
		            
					resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					pic.setImageBitmap(resizeBmp);
					size++;
            	}
            }
            
        });
        //图片减小  
        zoomCtrl.setOnZoomOutClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
            	if(size > 1)
            	{
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
					
					pic.destroyDrawingCache() ;
					
					if(resizeBmp != null)
					{
						if(!resizeBmp.isRecycled() && resizeBmp != bmp){
		                    resizeBmp.recycle() ;
						}
						resizeBmp = null ;
						System.gc() ;
					}
					
					resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					pic.setImageBitmap(resizeBmp);
					size--;
            	}
            }
        }); 
        
		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setOnItemSelectedListener(this);
	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return imglist.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			
			Map<String,Object> map = imglist.get(position);
			Bitmap bitm = (Bitmap)map.get("img");
			
			i.setImageBitmap(bitm);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			i.setBackgroundResource(R.drawable.e);
			return i;
		}

		private Context mContext;

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String,Object> map = imglist.get(position);
		bmp = (Bitmap)map.get("img");
		imageurl = (String)map.get("imageUrl");
		
		
//		Drawable drawable = new BitmapDrawable(bitm);
//		is.setImageDrawable(drawable);
		size = 1;
		scaleWidth = 1;
		scaleHeight = 1;
		pic.setImageBitmap(bmp);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
