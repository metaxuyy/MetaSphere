package ms.globalclass.listviewadapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class SpecialAdapterHotel extends BaseAdapter{

	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData; // List�б��ŵ�����
	private int mResource; // �󶨵�ҳ�� ,���磺R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // �󶨿ؼ���Ӧ�����������ֵ����
	private int[] mTo; // �󶨿ؼ���ID
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	
	private static Context contexts;
	
	private boolean b = false;
	
	private SharedPreferences share;
	
	private boolean isGridView = false;
	
	private String imgtype = "ico";
	private Map<Integer,WeakReference<Bitmap>> dateCache = new HashMap<Integer,WeakReference<Bitmap>>();//ͼƬ��Դ����
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public SpecialAdapterHotel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		imgtype = type;
		// ���ֱ�(LayoutInflater)����XML�����ļ���������ͼ(View)����������޷�ֱ�Ӵ���ʵ����Ҫͨ��context�����
		// getLayoutInflater()��getSystemService(String)���������ʵ����������õĲ��ֱ�ʵ�������豸�Ļ������á�
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public SpecialAdapterHotel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,Boolean isgridview,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		isGridView = isgridview;
		imgtype = type;
		// ���ֱ�(LayoutInflater)����XML�����ļ���������ͼ(View)����������޷�ֱ�Ӵ���ʵ����Ҫͨ��context�����
		// getLayoutInflater()��getSystemService(String)���������ʵ����������õĲ��ֱ�ʵ�������豸�Ļ������á�
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public SpecialAdapterHotel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,int dstWidths,int dstHeights,boolean yuan,SharedPreferences  shares,String type) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		dstWidth = dstWidths;
		dstHeight = dstHeights;
		b = yuan;
		share = shares;
		imgtype = type;
		// ���ֱ�(LayoutInflater)����XML�����ļ���������ͼ(View)����������޷�ֱ�Ӵ���ʵ����Ҫͨ��context�����
		// getLayoutInflater()��getSystemService(String)���������ʵ����������õĲ��ֱ�ʵ�������豸�Ļ������á�
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
			if(dstWidth > 1)
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

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ

		Canvas cv = new Canvas(newb);

		// draw src into

		cv.drawBitmap(src, 0, 0, null);// �� 0��0���꿪ʼ����src

		// draw watermark into

//		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// ��src�����½ǻ���ˮӡ
		
		cv.drawBitmap(watermark, w - ww + 5, 0-5, null);

		// save all clip

		cv.save(Canvas.ALL_SAVE_FLAG);// ����

		// store

		cv.restore();// �洢

		return newb;
	}
	
	public void removeAllResources()
	{
		try{
		    for(Map.Entry<Integer, WeakReference<Bitmap>> entry : dateCache.entrySet())   
		    {   
		        WeakReference<Bitmap> wrf = entry.getValue();
		        if(wrf != null)
		        {
		        	Bitmap dbitmap = wrf.get();
					if(dbitmap != null)
					{
						if (!dbitmap.isRecycled()) {  
							dbitmap.recycle();
						}
					}
		        }
		    }
		    dateCache = null;
		    
		    System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/*
	 * SimpleAdapter������ʾÿ��Item����ͨ������������ɵ� ��getView(int position, View
	 * convertView, ViewGroup
	 * parent)���ֵ�����SimpleAdapter��˽�з���createViewFromResource
	 * ����װView����createViewFromResource�ж�SimpleAdapter�Ĳ���String[] from ��int[]
	 * to��������װ
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		final Map map = mData.get(position%mData.size());
		Bitmap bitm = (Bitmap)map.get("img");
		ImageView iv = new ImageView(contexts);
		iv.setImageBitmap(bitm);
		//����ͼƬ����ʾ��С
		iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		//����ͼƬ����ʾ������fit_center������ͼƬ,center������
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		return iv; 
//		return createViewFromResource(position, convertView, parent, mResource);    //�������淽��
	}
	
	// ��createViewFromResource����������һ��bindView(position,
	// v)������item�еĸ���View��������װ��bindView(position, v)
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
			bindView(position, rowView); // �������淽����Item�е�
			viewMap.put(position, rowView);
//			convertView.setLayoutParams( new Gallery.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));
		}
		return rowView;
	}
	
	// ��ViewImage������װ�Ĵ����ˡ�else if (v instanceof ImageView)��
	@SuppressWarnings("unchecked")
	private void bindView(int position, View view) {
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
						if(data != null)
						{
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data,view);
							} else {
								if(data instanceof Bitmap)
								{
									dateCache.put(position, new WeakReference<Bitmap>((Bitmap) data));
									setViewImage((ImageView) v, (Bitmap) data);
								}
								else
									setViewImage((ImageView) v, urlText,position);
							}
						}
						else
						{
							v.setVisibility(View.GONE);
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

	public void setViewText(TextView v, String value) {
		v.setText(value);
	}
	
	public void setViewImage(ImageView v, int value,View view) {
		v.setImageResource(value);
		view.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setViewImage(ImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}
	
	public void setRatingBar (RatingBar  v, float value) {
		v.setRating(value);
	}

	public void setViewImage(final ImageView v, String url,final int position) {
		
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
			if(imgtype.equals("ico"))
				url = str+"_ico"+str2;
			else if(imgtype.equals("zhong"))
				url = str+"_zhong"+str2;
		}
		
		// ���ֻ�ǵ����İ�ͼƬ��ʾ���������л��档ֱ��������ķ����õ�URL��Bitmap������ʾ��OK
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
								Bitmap bitmap = ((BitmapDrawable)imageDrawable).getBitmap();  
								dateCache.put(position, new WeakReference<Bitmap>(bitmap));
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
