package ms.globalclass;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.globalclass.listviewadapter.AsyncImageLoader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class ListViewAdapter extends BaseAdapter{

	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private ViewBinder mViewBinder;
	private List<Map<String, Object>> mData; // List列表存放的数据
//	private List<Map<String, Object>> mDataTow = new ArrayList<Map<String,Object>>(); // List列表存放的数据
	private int mResource; // 绑定的页面 ,例如：R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // 绑定控件对应的数组里面的值名称
	private int[] mTo; // 绑定控件的ID
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	
	private static Context contexts;
	
	private boolean b = false;
	
	private SharedPreferences share;
	
	private boolean isGridView = false;
	
	public int count = 0;
	private String imgtype = "ico";
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void addMoreData(List<Map<String, Object>> adddata)
	{
//	  this.mData.addAll(adddata);
	  this.count = mData.size();
	  this.notifyDataSetChanged();
	}

	public ListViewAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,int counts,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
//		mDataTow.addAll(data);
//		mData = mDataTow;
		count = counts;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		imgtype = type;
		// 布局泵(LayoutInflater)根据XML布局文件来绘制视图(View)对象。这个类无法直接创建实例，要通过context对象的
		// getLayoutInflater()或getSystemService(String)方法来获得实例，这样获得的布局泵实例符合设备的环境配置。
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public ListViewAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,Boolean isgridview,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
//		mDataTow.addAll(data);
//		mData = mDataTow;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		isGridView = isgridview;
		imgtype = type;
		// 布局泵(LayoutInflater)根据XML布局文件来绘制视图(View)对象。这个类无法直接创建实例，要通过context对象的
		// getLayoutInflater()或getSystemService(String)方法来获得实例，这样获得的布局泵实例符合设备的环境配置。
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public ListViewAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to,int dstWidths,int dstHeights,boolean yuan,SharedPreferences  shares,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
//		mDataTow.addAll(data);
//		mData = mDataTow;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		dstWidth = dstWidths;
		dstHeight = dstHeights;
		b = yuan;
		share = shares;
		imgtype = type;
		// 布局泵(LayoutInflater)根据XML布局文件来绘制视图(View)对象。这个类无法直接创建实例，要通过context对象的
		// getLayoutInflater()或getSystemService(String)方法来获得实例，这样获得的布局泵实例符合设备的环境配置。
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
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
			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
//			Bitmap newimg = getRes();
//			bitmap = createBitmap(bitmap,newimg);
//			if(b)
//				bitmap = getRoundedCornerBitmap(bitmap,12);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
        
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap  
                .getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
   
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
   
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
   
        return output;  
    }  
	
//	public static Bitmap getRes() 
//	{
////		ApplicationInfo appInfo = context.getApplicationInfo();
////		int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
////		return BitmapFactory.decodeResource(context.getResources(), resID);
//		Resources res=contexts.getResources();
//		return BitmapFactory.decodeResource(res, R.drawable.grid_jie_mian_24); 
//	}
	
	private static Bitmap createBitmap(Bitmap src, Bitmap watermark) 
	{
		String tag = "createBitmap";
		if (src == null)
		{
			return null;
		}
		
		int w = src.getWidth();

		int h = src.getHeight();

		int ww = watermark.getWidth();

		int wh = watermark.getHeight();

		// create the new blank bitmap

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

		Canvas cv = new Canvas(newb);

		// draw src into

		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src

		// draw watermark into

//		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
		
		cv.drawBitmap(watermark, w - ww + 5, 0-5, null);

		// save all clip

		cv.save(Canvas.ALL_SAVE_FLAG);// 保存

		// store

		cv.restore();// 存储

		return newb;
	}

	/*
	 * SimpleAdapter基类显示每个Item都是通过这个方法生成的 在getView(int position, View
	 * convertView, ViewGroup
	 * parent)中又调用了SimpleAdapter的私有方法createViewFromResource
	 * 来组装View，在createViewFromResource中对SimpleAdapter的参数String[] from 和int[]
	 * to进行了组装
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = super.getView(position, convertView, parent);
//		int colorPos = position % colors.length;
//		if(colorPos == 0)
//			view.setBackgroundColor(Color.argb(220, 211, 211, 211));
//		else
//			view.setBackgroundColor(Color.argb(220, 169, 169, 169));
		
		
//		Map map = list.get(position);
//		String cname = (String)map.get("cname");
//		String imageUrl = (String)map.get("mimg");
//		
//		TextView tv = (TextView)view.findViewById(R.id.menutype);
//		tv.setText(cname);
//		
//		ImageView imgview = (ImageView)view.findViewById(R.id.mimg);
//		imgview.setImageBitmap(returnBitMap(imageUrl));  
		
		return createViewFromResource(position, convertView, parent, mResource);    //调用下面方法
	}
	
	// 在createViewFromResource方法中又有一个bindView(position,
	// v)方法对item中的各个View进行了组装，bindView(position, v)
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View rowView = this.viewMap.get(position);

		if (rowView == null) {
			rowView = mInflater.inflate(resource, null);

			final int[] to = mTo;
			final int count = to.length;
			final View[] holder = new View[count];

			for (int i = 0; i < count; i++) {

				holder[i] = rowView.findViewById(to[i]);
			}
			rowView.setTag(holder);
			bindView(position, rowView); // 调用下面方法对Item中的
			viewMap.put(position, rowView);
		}
		return rowView;
	}
	
	// 对ViewImage进行组装的代码了“else if (v instanceof ImageView)”
	@SuppressWarnings("unchecked")
	private void bindView(int position, View view) {
		if(position < mData.size())
		{
		final Map dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = mViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = holder[i];
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String urlText = null;

				if (data == null) {
					urlText = "";
				} else {
					urlText = data.toString();
				}

				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, urlText);
				}

				if (!bound) {
					if (v instanceof Checkable) {
						if (data instanceof Boolean) {
							((Checkable) v).setChecked((Boolean) data);
						} else {
							throw new IllegalStateException(v.getClass()
									.getName()
									+ " should be bound to a Boolean, not a "
									+ data.getClass());
						}
					} else if (v instanceof TextView) {
						if(urlText != null && !urlText.equals(""))
							setViewText((TextView) v, urlText);
						else
							v.setVisibility(View.GONE);
					} else if (v instanceof RatingBar) {
						setRatingBar((RatingBar) v, (Float)data);
					} else if (v instanceof ImageView) {
						if(data == null)
						{
							v.setVisibility(View.GONE);
						}
						else if(urlText == null)
						{
							v.setVisibility(View.GONE);
						}
						else
						{
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data);
							} else {
								if(data instanceof Bitmap)
									setViewImage((ImageView) v, (Bitmap) data);
								else
									setViewImage((ImageView) v, urlText);
							}
						}
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}
		}
	}

	private void setViewText(TextView v, String urlText) {
		// TODO Auto-generated method stub
		v.setText(urlText);
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}
	
	public void setViewImage(ImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}
	
	public void setRatingBar (RatingBar  v, float value) {
		v.setRating(value);
	}

	public void setViewImage(final ImageView v, String url) {
		// 如果只是单纯的把图片显示，而不进行缓存。直接用下面的方法拿到URL的Bitmap就行显示就OK
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
			if(imgtype.equals("ico"))
				url = str+"_ico"+str2;
			else if(imgtype.equals("zhong"))
				url = str+"_zhong"+str2;
		}
		
		boolean loadimgTag = share.getBoolean("webimage", true);
		if(loadimgTag)
		{
			if(isGridView)
			{
				int imgid = contexts.getResources().getIdentifier(contexts.getPackageName()+":drawable/"+url,null,null);
				v.setImageResource(imgid);
			}
			else
			{
				if(b)
				{
					Bitmap bitmap = returnBitMap(url);
					 ((ImageView) v).setImageBitmap(bitmap);
				}
				else
				{
					imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
							if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
								
								v.setImageDrawable(imageDrawable);
							}
						}
					},dstWidth,dstHeight);
				}
			}
		}
		else
		{
			if(isGridView)
			{
				int imgid = contexts.getResources().getIdentifier(contexts.getPackageName()+":drawable/"+url,null,null);
				v.setImageResource(imgid);
			}
		}
	}
}
