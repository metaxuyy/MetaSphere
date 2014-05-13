package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SpecialAdapterDufel extends SimpleAdapter{

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
	
	public SpecialAdapterDufel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,String type) {
		super(context, data, resource, from, to);
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
	
	public SpecialAdapterDufel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,SharedPreferences  shares,Boolean isgridview,String type) {
		super(context, data, resource, from, to);
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
	
	public SpecialAdapterDufel(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,int dstWidths,int dstHeights,boolean yuan,SharedPreferences  shares,String type) {
		super(context, data, resource, from, to);
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

	/*
	 * SimpleAdapter������ʾÿ��Item����ͨ������������ɵ� ��getView(int position, View
	 * convertView, ViewGroup
	 * parent)���ֵ�����SimpleAdapter��˽�з���createViewFromResource
	 * ����װView����createViewFromResource�ж�SimpleAdapter�Ĳ���String[] from ��int[]
	 * to��������װ
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
		return createViewFromResource(position, convertView, parent, mResource);    //�������淽��
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
									setViewImage((ImageView) v, (Bitmap) data);
								else
									setViewImage((ImageView) v, urlText);
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

	public void setViewImage(ImageView v, int value,View view) {
		v.setImageResource(value);
//		view.setLayoutParams( new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setViewImage(ImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}
	
	public void setRatingBar (RatingBar  v, float value) {
		v.setRating(value);
	}

	public void setViewImage(final ImageView v, String url) {
		
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
			if(imgtype.equals("ico"))
				url = str+"_ico"+str2;
			else if(imgtype.equals("zhong"))
				url = str+"_zhong"+str2;
			
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
		else
		{
			Bitmap bmpimg = getLoacalBitmap(url);
			if(bmpimg != null)
				v.setImageBitmap(bmpimg);
		}
		
	}
	
	/**
	* ת������ͼƬΪbitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
}
