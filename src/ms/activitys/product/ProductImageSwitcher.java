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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class ProductImageSwitcher extends Activity implements
		OnItemSelectedListener, ViewFactory {
	private ImageSwitcher is;
	private Gallery gallery;

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private String productId;
	
	private List<Map<String, Object>> imglist;
	
	private ProgressBar pb;
	
	private String imageurl;
	private int width;
	private int height;

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
		setContentView(R.layout.product_imageswitcherpage);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = ProductImageSwitcher.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
            
        width = (int) dm.widthPixels;
        height = (int) (dm.heightPixels * 0.8);
		
		Bundle bunde = this.getIntent().getExtras();
		productId = bunde.getString("productId");
		
		pb = (ProgressBar)findViewById(R.id.probar);

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
				gallery.setAdapter(new ImageAdapter(ProductImageSwitcher.this));
				
				pb.setVisibility(View.GONE);
				gallery.setVisibility(View.VISIBLE);
				is.setVisibility(View.VISIBLE);
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

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
//			bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public void showImageList()
	{
		is = (ImageSwitcher) findViewById(R.id.switcher);
		is.setFactory(this);

		is.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		is.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		
		is.setBackgroundResource(R.drawable.home_backgroud);
		
		is.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ProductImageSwitcher.this,
						DisplayImage.class);
				Bundle bundle = new Bundle();
				bundle.putString("path", imageurl);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);  
				overridePendingTransition(R.anim.push_up_in,
						R.anim.push_up_out);
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
		Bitmap bitm = (Bitmap)map.get("img");
		imageurl = (String)map.get("imageUrl");
		
		Drawable drawable = new BitmapDrawable(bitm);
		is.setImageDrawable(drawable);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
