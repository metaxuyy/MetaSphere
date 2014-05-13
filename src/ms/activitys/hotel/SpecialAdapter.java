package ms.activitys.hotel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.listviewadapter.AsyncImageLoader;
import ms.globalclass.listviewadapter.SpecialAdapterDufel;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SpecialAdapter extends SimpleAdapter{

	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoader2 imageLoader2 = new AsyncImageLoader2();
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
//	private Map<String, View> viewMap = new HashMap<String, View>();
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData;
	private int mResource;
	private int mResource2;
	private int mResource3;
	private int mResource4;
	private LayoutInflater mInflater;
	private String[] mFrom;
	private int[] mTo; 
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	
	private static Context contexts;
	
	private boolean b = false;
	
	private SharedPreferences share;
	
	private boolean isGridView = false;
	
	private String imgtype = "ico";
	private String userid;
	private HttpDownloader downloader;
	private static MediaPlayer onplayer;
	private static MediaPlayer offplayer;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private AnimationDrawable voiceanim;
	private AnimationDrawable currevoiceanim;
	private TextView curreTextView;
	private LinearLayout curreLayout;
	private TextView ptext;
	private LinearLayout playout;
	private int curretofrom;
	private String BASE_URL;
	private Map<String, Drawable> imageCache = new HashMap<String, Drawable>();
	private MyApp myapp;
	public static FileUtils fileUtil = new FileUtils();
	private Map<Integer,WeakReference<Bitmap>> dateCache = new HashMap<Integer,WeakReference<Bitmap>>();//图片资源缓存
	private Map<Integer,Map<String,Object>> imgmap = new HashMap<Integer,Map<String,Object>>();//图片控件缓存
	
	private Map<Integer,List<WeakReference<Bitmap>>> dateCacheItems = new HashMap<Integer,List<WeakReference<Bitmap>>>();//列表图片资源缓存
	private Map<Integer,List<Map<String,Object>>> imgmapItems = new HashMap<Integer,List<Map<String,Object>>>();//列表图片控件缓存
//	private View[] holder = null;
	
	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, int resource2,int resource3,int resource4,String[] from, int[] to,SharedPreferences  shares,String type,String userid) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mResource2 = resource2;
		mResource3 = resource3;
		mResource4 = resource4;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		imgtype = type;
		this.userid = userid;
		myapp = (MyApp)contexts.getApplicationContext();
		
		
//		viewMap = myapp.getViewMap();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		String ip = this.share.getString("ipadrees", "121.199.8.186");
		BASE_URL = "http://"+ip+":80/upload/";
		
		downloader = new HttpDownloader();
		if(onplayer == null){
			onplayer = MediaPlayer.create(context, R.raw.on);
//			try {
//				onplayer.prepare();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		if(offplayer == null){
			offplayer = MediaPlayer.create(context, R.raw.off);
//			try {
//				offplayer.prepare();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
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
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
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
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
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
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void refreshData(int index)
	{
		mData.remove(index);
		this.notifyDataSetChanged();
	}
	
	public void removeItem(List<? extends Map<String, ?>> data)
	{
		mData = data;
		notifyDataSetChanged();
	}
	
	public void removeItem(int index)
	{
		mData.remove(index);
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
	
	protected Drawable loadImageFromUrl(String imageUrl) {
		try {
			Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(),"src");
			return drawable;
							
		} catch (Exception e) {
			return Drawable.createFromStream(null, "src");
		}
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
        
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap  
                .getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
   
        int color = 0xff424242;  
        Paint paint = new Paint();  
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        RectF rectF = new RectF(rect);  
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
   
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
   
        return output;  
    }  
	
	public void dataSetChanged(int index) {
		Map<String,Object> map = (Map<String,Object>)mData.get(index);
		map.put("checkbox", true);
		notifyDataSetChanged();
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

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		Canvas cv = new Canvas(newb);

		// draw src into

		cv.drawBitmap(src, 0, 0, null);

		// draw watermark into

//		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);
		
		cv.drawBitmap(watermark, w - ww + 5, 0-5, null);

		// save all clip

		cv.save(Canvas.ALL_SAVE_FLAG);

		// store

		cv.restore();

		return newb;
	}
	
	public void releaseBitmap(int start, int end) {
		
		start = start - 1;
		end = end + 1;

		WeakReference<Bitmap> delBitmap;
		List<WeakReference<Bitmap>> delBitmapList;
		for (int del = 0; del < start; del++) {
			delBitmap = dateCache.get(del);
			if (delBitmap != null) {

				dateCache.remove(del);
				if (!delBitmap.get().isRecycled()) {  
					delBitmap.get().recycle();
				}
//				System.out.println("sart==delete"+del);
			}
			
			delBitmapList = dateCacheItems.get(del);
			if(delBitmapList != null && delBitmapList.size() > 0)
			{
				for(int j=0;j<delBitmapList.size();j++)
				{
					Bitmap dbitmap = (Bitmap)delBitmapList.get(j).get();
					if(dbitmap != null)
					{
						if (!dbitmap.isRecycled()) {  
							dbitmap.recycle();
						}
					}
				}
				delBitmapList.removeAll(delBitmapList);
				dateCacheItems.remove(del);
			}
		}
		
		freeBitmapFromIndex(end,mData.size());
		
		loadVisibleItemImageData(start,end);
	}
	      
	/**
	 * 
	 * @param index
	 */
	private void freeBitmapFromIndex(int end,int count) {
		// 释放之外的bitmap资源
		WeakReference<Bitmap> delBitmap;
		List<WeakReference<Bitmap>> delBitmapList;
		for (int del = end + 1; del < count; del++) {
			delBitmap = dateCache.get(del);
			if (delBitmap != null) {
				dateCache.remove(del);
				if (!delBitmap.get().isRecycled()) {  
					delBitmap.get().recycle();
				}
//				System.out.println("sart==delete2-"+del);
			}
			
			delBitmapList = dateCacheItems.get(del);
			if(delBitmapList != null && delBitmapList.size() > 0)
			{
				for(int j=0;j<delBitmapList.size();j++)
				{
					Bitmap dbitmap = (Bitmap)delBitmapList.get(j).get();
					if(dbitmap != null)
					{
						if (!dbitmap.isRecycled()) {  
							dbitmap.recycle();
						}
					}
				}
				delBitmapList.removeAll(delBitmapList);
				dateCacheItems.remove(del);
			}
		}
		
		System.gc();
	}
	
	private void loadVisibleItemImageData(int sart,int end)
	{
		try{
			for(int i = sart;i<end;i++)
			{
				WeakReference<Bitmap> bitmap = dateCache.get(i);
				if(bitmap == null)
				{
					Map<String,Object> map = imgmap.get(i);
					if(map != null)
					{
						ImageView imgview = (ImageView)map.get("view");
						String data = (String)map.get("value");
						bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data));
						imgview.setImageBitmap(bitmap.get());
						dateCache.put(i, bitmap);

					}
				}
				
				List<WeakReference<Bitmap>> bitmaplist = dateCacheItems.get(i);
				if(bitmaplist == null)
				{
					bitmaplist = new ArrayList<WeakReference<Bitmap>>();
					List<Map<String,Object>> viewlist = imgmapItems.get(i);
					if(viewlist != null && viewlist.size() > 0)
					{
						for(int j=0;j<viewlist.size();j++)
						{
							Map<String,Object> map = viewlist.get(j);
							if(map != null)
							{
								ImageView imgview = (ImageView)map.get("view");
								String data = (String)map.get("value");
								bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data));
								imgview.setImageBitmap(bitmap.get());
								bitmaplist.add(bitmap);
	
							}
						}
						dateCacheItems.put(i, bitmaplist);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
		    imgmap = null;
		    
		    for(Map.Entry<Integer, List<WeakReference<Bitmap>>> entry : dateCacheItems.entrySet())   
		    {   
		        List<WeakReference<Bitmap>> wrflist = entry.getValue();
		        if(wrflist != null)
		        {
		        	for(int i=0;i<wrflist.size();i++)
		        	{
			        	Bitmap dbitmap = wrflist.get(i).get();
						if(dbitmap != null)
						{
							if (!dbitmap.isRecycled()) {  
								dbitmap.recycle();
							}
						}
		        	}
		        }
		    }
		    dateCacheItems = null;
		    imgmapItems = null;
		    
		    System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	    
	
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
		return createViewFromResource(position, convertView, parent, mResource,mResource2,mResource3,mResource4);    
	}
	
	
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource,int resource2,int resource3,int resource4) {

		View rowView = this.viewMap.get(position);
		Map dataSet = mData.get(position);
		String mysendname = (String)dataSet.get("mysendname");
		String messagetype = (String)dataSet.get("messagetype");
		String mid = (String)dataSet.get("mid");
//		View rowView = this.viewMap.get(mid);
		
		int tag = 0;
		if(mysendname.equals(userid))
		{
			tag = 2;
		}
		else
		{
			if(messagetype.equals("") || messagetype.equals("wh") || messagetype.equals("jf") || messagetype.equals("cpcx"))
			{
				tag = 1;
			}
			else if(messagetype.equals("zdy") || messagetype.equals("dz"))
			{
				tag = 4;
			}
			else
			{
				tag = 3;
			}
		}
		
		if (rowView == null) {
			System.out.println("dangqiansuoying===="+position);

			switch (tag) {
				case 1:
					rowView = mInflater.inflate(resource, null);
					break;
				case 2:
					rowView = mInflater.inflate(resource2, null);
					break;
				case 3:
					rowView = mInflater.inflate(resource3, null);
					break;
				case 4:
					rowView = mInflater.inflate(resource4, null);
					break;
			}

			int count = mTo.length;
			View[] holder = new View[count];
			
			for (int i = 0; i < count; i++) {

				holder[i] = rowView.findViewById(mTo[i]);
			}
			
			rowView.setTag(R.id.tag_first,holder);
			rowView.setTag(R.id.tag_second,tag);
			bindView(position, rowView);
//			holder = null;
			viewMap.put(position, rowView);
//			viewMap.put(mid, rowView);
//		
		}
		else
		{
//			Bitmap bitmap = dateCache.get(position);
//			if(bitmap == null)
//			{
//				Map<String,Object> map = imgmap.get(position);
//				if(map != null)
//				{
//					ImageView imgview = (ImageView)map.get("view");
//					String data = (String)map.get("value");
//					bitmap = getLoacalBitmap2(data);
//					imgview.setImageBitmap(bitmap);
//					dateCache.put(position, bitmap);
//			
//				}
//			}
			WeakReference<Bitmap> bitmap = dateCache.get(position);
			if(bitmap == null)
			{
				Map<String,Object> map = imgmap.get(position);
				if(map != null)
				{
					ImageView imgview = (ImageView)map.get("view");
					if(map.containsKey("with"))
					{
//						LinearLayout imglayout = (LinearLayout)map.get("imglayout");
						int with = (Integer)map.get("with");
						int high = (Integer)map.get("high");
//						imglayout.setLayoutParams(new RelativeLayout.LayoutParams(with, high));
						LayoutParams lp = imgview.getLayoutParams();
				        lp.width=with;
				        lp.height=high;        
//				        imglayout.setLayoutParams(lp);
//				        imglayout.setBackgroundColor(R.color.list_item_background);
				        imgview.setLayoutParams(lp);
						imgview.setImageResource(R.color.list_item_background);
					}
					else
					{
						imgview.setImageResource(R.color.list_item_background);
					}
//					imgview.setImageResource(R.color.list_item_background);
				}
			}
			
			List<WeakReference<Bitmap>> bitmaplist = dateCacheItems.get(position);
			if(bitmaplist == null)
			{
				List<Map<String,Object>> viewlist = imgmapItems.get(position);
				if(viewlist != null && viewlist.size() > 0)
				{
					for(int j=0;j<viewlist.size();j++)
					{
						Map<String,Object> map = viewlist.get(j);
						if(map != null)
						{
							ImageView imgview = (ImageView)map.get("view");
							imgview.setImageResource(R.color.list_item_background);
						}
					}
				}
			}
		}
//		else
//		{
//			int tag2 = (Integer)rowView.getTag(R.id.tag_second);
//			if(tag2 != tag)
//			{
//				viewMap.remove(position);
//				switch (tag) {
//					case 1:
//						rowView = mInflater.inflate(resource, null);
//						break;
//					case 2:
//						rowView = mInflater.inflate(resource2, null);
//						break;
//					case 3:
//						rowView = mInflater.inflate(resource3, null);
//						break;
//					case 4:
//						rowView = mInflater.inflate(resource4, null);
//						break;
//				}
//	
//				int count = mTo.length;
//				View[] holder = new View[count];
//				
//				for (int i = 0; i < count; i++) {
//	
//					holder[i] = rowView.findViewById(mTo[i]);
//				}
//				
//				rowView.setTag(1,holder);
//				rowView.setTag(2,tag);
//				bindView(position, rowView); 
////				holder = null;
//				viewMap.put(position, rowView);
//			}
//			
//		}
		return rowView;
	}
	
	public void setItemSendImgStart(final int index,boolean isShow)
	{
		View rowView = this.viewMap.get(index);
		if(rowView != null)
		{
			View[] holder = (View[]) rowView.getTag(R.id.tag_first);
			View v = holder[11];
			if(isShow)
			{
				v.setVisibility(View.VISIBLE);
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(index);
					}
				});
			}
			else
				v.setVisibility(View.GONE);
		}
	}
	
	public void setItemSendPressStart(int index,boolean isShow)
	{
		View rowView = this.viewMap.get(index);
		if(rowView != null)
		{
			View[] holder = (View[]) rowView.getTag(R.id.tag_first);
			View v = holder[12];
			if(isShow)
				v.setVisibility(View.VISIBLE);
			else
				v.setVisibility(View.GONE);
		}
	}
	
	public void setItemImageLayout(int index)
	{
		View rowView = this.viewMap.get(index);
		LinearLayout imglayout = (LinearLayout)rowView.findViewById(R.id.upload_img_layout);
		imglayout.setVisibility(View.VISIBLE);
	}
	
	
	@SuppressWarnings("unchecked")
	private void bindView(final int position, View view) {
		Map dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		ViewBinder binder = mViewBinder;
		View[] holder = (View[]) view.getTag(R.id.tag_first);
		int tags = (Integer)view.getTag(R.id.tag_second);
		String[] from = mFrom;
		int[] to = mTo;
		int count = to.length;

		for (int i = 0; i < count; i++) {
			View v = holder[i];
			if (v != null) {
				Object data = dataSet.get(from[i]);
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
					} else if(v instanceof ProgressBar){
						if(v.getId() == R.id.send_progressBar)
							setProgressBar((ProgressBar)v,dataSet);
					} else if (v instanceof TextView) {
						if(v.getId() == R.id.mymessagecontent)
						{
							if(urlText.contains(".amr"))
							{
								int tag = 0;
								if(view.getId() == R.id.left_message_layout)
									tag = 1;
								else
									tag = 2;
								setViewText((TextView) v, urlText,dataSet,view,tag);
							}
							else
								setViewText((TextView) v, urlText,dataSet);
						}
						else if(v.getId() == R.id.tv_time)
						{
							setViewText2((TextView) v, urlText,dataSet,view);
						}
						else
						{
							if(urlText != null && !urlText.equals(""))
								setViewText((TextView) v, urlText,dataSet);
							else
								v.setVisibility(View.GONE);
						}
						LinearLayout layout_text = (LinearLayout)view.findViewById(R.id.layout_text);
						LinearLayout content_layout = (LinearLayout)view.findViewById(R.id.content_layout);
						if(layout_text != null)
						{
							layout_text.setTag(position);
							layout_text.setOnLongClickListener(new OnLongClickListener() {
								
								@Override
								public boolean onLongClick(View v) {
									// TODO Auto-generated method stub
									int index = (Integer)v.getTag();
									myapp.setCurrentDelindex(index);
									MessageListActivity.instance.openListViewItemMuneView("");
									return true;
								}
							});
						}
						else
						{
							content_layout.setTag(position);
							content_layout.setOnLongClickListener(new OnLongClickListener() {
								
								@Override
								public boolean onLongClick(View v) {
									// TODO Auto-generated method stub
									int index = (Integer)v.getTag();
									myapp.setCurrentDelindex(index);
									MessageListActivity.instance.openListViewItemMuneView("");
									return true;
								}
							});
						}
					} else if (v instanceof RatingBar) {
						setRatingBar((RatingBar) v, (Float)data);
					} else if (v instanceof RelativeLayout) {
						setRelativeLayout((RelativeLayout) v, (String)data);
					} else if (v instanceof ListView) {
							setListView((ListView) v, (List<Map<String,Object>>)data,view,dataSet);
					} else if (v instanceof LinearLayout) {
							setLineartLayoutView((LinearLayout) v, (List<Map<String,Object>>)data,view,dataSet,position);
					} else if (v instanceof RoundAngleImageView) {
						if(data != null && !data.equals(""))
						{
							if (data instanceof Integer) {
								setViewRoundAngleImageView((RoundAngleImageView) v, (Integer) data,view);
							} else {
								if(data instanceof Bitmap)
									setViewRoundAngleImageView((RoundAngleImageView) v, (Bitmap) data);
								else
								{
									setViewRoundAngleImageView((RoundAngleImageView) v, urlText,dataSet);
								}
							}
						}
						else
						{
//							v.setVisibility(View.GONE);
//							((RoundAngleImageView) v).setImageResource(R.drawable.default_avatar_shadow);
						}
					} else if (v instanceof ImageView) {
						if(data != null && !data.equals(""))
						{
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data,view);
							} else {
								if(data instanceof String)
								{
									if(((String) data).indexOf("http://") >= 0)
									{
										if(v.getId() == R.id.uploadFileImg)
										{
											LinearLayout imglayout = (LinearLayout)view.findViewById(R.id.upload_img_layout);
											ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
											TextView progress_tv = (TextView)view.findViewById(R.id.progress_tv);
											TextView messagetext = (TextView)view.findViewById(R.id.mymessagecontent);
											setViewImage((ImageView) v, urlText,imglayout,progressBar,progress_tv,messagetext,view,position,dataSet);
											
											v.setTag(position);
											v.setOnLongClickListener(new OnLongClickListener() {
												
												@Override
												public boolean onLongClick(View v) {
													// TODO Auto-generated method stub
													int index = (Integer)v.getTag();
													myapp.setCurrentDelindex(index);
													MessageListActivity.instance.openListViewItemMuneView("");
													return true;
												}
											});
										}
										else if(v.getId() == R.id.start_img)
										{
											setSendImgStart((ImageView)v,dataSet,position);
										}
										else
										{
											setViewImage((ImageView) v, urlText,dataSet);
										}
									}
									else
									{
										if(v.getId() == R.id.uploadFileImg)
										{
											LinearLayout imglayout = (LinearLayout)view.findViewById(R.id.upload_img_layout);
											ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
											TextView messagetext = (TextView)view.findViewById(R.id.mymessagecontent);
											TextView progress_tv = (TextView)view.findViewById(R.id.progress_tv);
//											if(bmpimg != null)
//												((ImageView) v).setImageBitmap(bmpimg);
											setViewImage((ImageView) v, urlText,imglayout,progressBar,progress_tv,messagetext,view,position,dataSet);
											messagetext.setVisibility(View.GONE);
											
											v.setOnClickListener(new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													// TODO Auto-generated method stub
													Map dataSet = mData.get(position);
													String mid = (String)dataSet.get("mid");
													MessageListActivity.instance.showMessageImageDetails(mid);
												}
											});
											
											v.setTag(position);
											v.setOnLongClickListener(new OnLongClickListener() {
												
												@Override
												public boolean onLongClick(View v) {
													// TODO Auto-generated method stub
													int index = (Integer)v.getTag();
													myapp.setCurrentDelindex(index);
													MessageListActivity.instance.openListViewItemMuneView("");
													return true;
												}
											});
										}
										else if(v.getId() == R.id.start_img)
										{
											setSendImgStart((ImageView)v,dataSet,position);
										}
										else
										{
											Bitmap bmpimg = null;
//											Bitmap bmpimg = getLoacalBitmap2((String)data);
											WeakReference<Bitmap> current = dateCache.get(position);
											if(current == null)
											{
												bmpimg = getLoacalBitmap2((String)data);
												dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
											}
											else
											{
												bmpimg = current.get();
											}
											Map<String,Object> map = new HashMap<String,Object>();
											map.put("view", ((ImageView) v));
											map.put("value", (String)data);
											imgmap.put(position, map);
											if(bmpimg != null)
												setViewImage((ImageView) v, bmpimg);
										}
									}
								}
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
	
	public void setViewText(TextView v,String str,Map dataSet)
	{
		if(v.getId() == R.id.store_name)
		{
			if(v.getBackground() != null)
				v.getBackground().setAlpha(100);
			v.setText(str);
		}
		else if(v.getId() == R.id.store_doc_txt)
		{
			v.setVisibility(View.VISIBLE);
			v.setText(str);
		}
		else
			v.setText(str);
		
		String messagetype = (String)dataSet.get("messagetype");
		if(messagetype.equals("jf"))
		{
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MessageListActivity.instance.openCardView();
				}
			});
		}
		else if(messagetype.equals("cpcx"))
		{
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MessageListActivity.instance.showTrafficView();
				}
			});
		}
	}
	
	public void setSendImgStart(ImageView v,Map<String,Object> dataSet,final int index)
	{
		String tag = (String)dataSet.get("sendimg");
		if(tag.equals("0"))
		{
			v.setVisibility(View.VISIBLE);
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MessageListActivity.instance.openAgainSendMuneView(index);
				}
			});
		}
		else
			v.setVisibility(View.GONE);
	}
	
	public void setProgressBar(ProgressBar v,Map<String,Object> dataSet)
	{
		String tag = (String)dataSet.get("sendprogress");
		if(tag.equals("0"))
			v.setVisibility(View.VISIBLE);
		else
			v.setVisibility(View.GONE);
	}

	public void setViewImage(ImageView v, int value,View view) {
		v.setImageResource(value);
//		view.setLayoutParams( new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setViewImage(ImageView v, Bitmap value) {
//		v.setBackgroundColor(Color.WHITE);
		v.setImageBitmap(value);
	}
	
	public void setViewRoundAngleImageView(RoundAngleImageView v, int value,View view) {
		v.setImageResource(value);
//		view.setLayoutParams( new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setViewRoundAngleImageView(RoundAngleImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}
	
	public void setRatingBar (RatingBar  v, float value) {
		v.setRating(value);
	}
	
	public void setLinearLayout (LinearLayout v,JSONObject value,Map dataSet) {
		try{
			String messagetype = (String)dataSet.get("messagetype");
			if(messagetype.equals("fj"))
			{
				loadRoomLayout(v,value,dataSet);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setRelativeLayout (RelativeLayout v,String value)
	{
		if(!value.equals(""))
		{
			 if (imageCache.containsKey(BASE_URL+value)) {
				 Drawable drawable = imageCache.get(BASE_URL+value); 
				 v.setBackgroundDrawable(drawable);
			 }
			 else
			 {
				 downloadImageFile(BASE_URL+value,v);
			 }
		}
	}
	
	public void setListView(ListView v,final List<Map<String,Object>> dlist,View view,Map dataSet)
	{
		final String messagetype = (String)dataSet.get("messagetype");
		final String url = (String)dataSet.get("url");
		final String title = (String)dataSet.get("storename");
		
		LinearLayout content_layout = (LinearLayout)view.findViewById(R.id.content_layout);
		content_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(messagetype.equals("") || messagetype.equals("wh"))
				{
					
				}
				else
				{
					if(messagetype.equals("fj"))
					{
						MessageListActivity.instance.showBankRoomWindo();
					}
					else if(messagetype.equals("js"))
					{
						MessageListActivity.instance.openCardView();
					}
					else if(messagetype.equals("yh"))
					{
						MessageListActivity.instance.showDownloadCouponView();
					}
					else if(messagetype.equals("dz"))
					{
						MessageListActivity.instance.showStopAddress();
					}
					else if(messagetype.equals("zdy"))
					{
						if(url != null && !url.equals(""))
							MessageListActivity.instance.showWebHtml(url,title);
					}
				}
			}
		});
		
		if(dlist != null)
		{
			SpecialAdapterDufel sadapter = null;
			if(messagetype.equals("fj"))
			{
				sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
						new String[] {"sysImg","title","price","desc"}, 
						new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
						share,"max");
			}
			else if(messagetype.equals("yh"))
			{
				sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
						new String[] {"sysImg","title","price","desc"}, 
						new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
						share,"max");
			}
			else if(messagetype.equals("tq"))
			{
				sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
						new String[] {"sysImg","title","price","desc"}, 
						new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
						share,"max");
			}
			else if(messagetype.equals("ct"))
			{
				sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
						new String[] {"sysImg","title","price","desc"}, 
						new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
						share,"max");
			}
			else if(messagetype.equals("zb"))
			{
				sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
						new String[] {"sysImg","title","price","desc"}, 
						new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
						share,"max");
			}
			
			v.setDividerHeight(0);
			v.setAdapter(sadapter);
			
			v.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(messagetype.equals("") || messagetype.equals("wh"))
					{
						
					}
					else
					{
						if(messagetype.equals("fj"))
						{
							MessageListActivity.instance.showBankRoomWindo();
						}
						else if(messagetype.equals("yh"))
						{
							MessageListActivity.instance.showDownloadCouponView();
						}
						else if(messagetype.equals("zb") || messagetype.equals("ct"))
						{
							Map<String,Object> map = dlist.get(arg2);
							String pkid = (String)map.get("pkid");
							String title = (String)map.get("title");
							String desc = (String)map.get("desc");
							Object sysImg = map.get("sysImg");
							MessageListActivity.instance.showStoreinfo(pkid,title,desc,sysImg);
						}
					}
				}
			});
			
			if(dlist.size() > 0)
			{
	//			int listviewheiht = dlist.size() * 80;
	//			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) v.getLayoutParams();    
	//			linearParams.height = listviewheiht;       
	//			v.setLayoutParams(linearParams); 
				int totalHeight = 0;
				for (int i = 0, len = sadapter.getCount(); i < len; i++) {
	                View listItem = sadapter.getView(i, null, v);
	                listItem.measure(0, 0); 
	                int list_child_item_height = listItem.getMeasuredHeight()+v.getDividerHeight();
	                totalHeight += list_child_item_height; 
				}
				
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) v.getLayoutParams();   
				linearParams.height = totalHeight;        
				v.setLayoutParams(linearParams); 
			}
		}
		else
		{
			v.setVisibility(View.GONE);
		}
	}
	
	public void setLineartLayoutView(LinearLayout v,final List<Map<String,Object>> dlist,View view,Map dataSet,int position)
	{
		final String messagetype = (String)dataSet.get("messagetype");
		final String url = (String)dataSet.get("url");
		final String title = (String)dataSet.get("storename");
		
		LinearLayout content_layout = (LinearLayout)view.findViewById(R.id.content_layout);
		content_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(messagetype.equals("") || messagetype.equals("wh"))
				{
					
				}
				else
				{
					if(messagetype.equals("fj"))
					{
						MessageListActivity.instance.showBankRoomWindo();
					}
					else if(messagetype.equals("js"))
					{
						MessageListActivity.instance.openCardView();
					}
					else if(messagetype.equals("yh"))
					{
						MessageListActivity.instance.showDownloadCouponView();
					}
					else if(messagetype.equals("dz"))
					{
						MessageListActivity.instance.showStopAddress();
					}
					else if(messagetype.equals("zdy"))
					{
						if(url != null && !url.equals(""))
							MessageListActivity.instance.showWebHtml(url,title);
					}
				}
			}
		});
		
		
		List<WeakReference<Bitmap>> currents = dateCacheItems.get(position);
		if(currents == null || currents.size() == 0)
		{
			currents = new ArrayList<WeakReference<Bitmap>>();
		}
		List<Map<String,Object>> viewlist = new ArrayList<Map<String,Object>>();
		
		if(dlist != null)
		{
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> map = dlist.get(i);
				final String sysImg = (String)map.get("sysImg");
				String itemtitle = (String)map.get("title");
				final String price = (String)map.get("price");
				final String desc = (String)map.get("desc");
				final String pkid = (String)map.get("pkid");
				
				Bitmap bmpimg = null;
				if(sysImg != null && !sysImg.equals(""))
				{
					if(sysImg.indexOf("http:") >= 0)
					{
						
					}
					else
					{
						bmpimg = getLoacalBitmap2(sysImg);
					}
					currents.add(new WeakReference<Bitmap>(bmpimg));
				}
				
				
				LinearLayout rootview = (LinearLayout)v.getChildAt(i);
				rootview.setVisibility(View.VISIBLE);
				
				ImageView imgview = (ImageView)((LinearLayout)rootview.getChildAt(0)).getChildAt(0);
				imgview.setImageBitmap(bmpimg);
//				if(bmpimg != null)
//					imgview.setBackgroundColor(color.white);
				
				Map<String,Object> viewmap = new HashMap<String,Object>();
				viewmap.put("view", imgview);
				viewmap.put("value", sysImg);
				viewlist.add(viewmap);
				
				LinearLayout contlayout = (LinearLayout)rootview.getChildAt(1);
				LinearLayout contlayout0 = (LinearLayout)contlayout.getChildAt(0);
				TextView nametextview = (TextView)contlayout0.getChildAt(0);
				nametextview.setText(itemtitle);
				TextView pricetextview = (TextView)contlayout0.getChildAt(1);
				if(price != null && !price.equals(""))
					pricetextview.setText(price);
				else
					pricetextview.setVisibility(View.GONE);
				
				TextView textview = (TextView)contlayout.getChildAt(1);
				textview.setText(desc);
				
				rootview.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(messagetype.equals("") || messagetype.equals("wh"))
						{
							
						}
						else
						{
							if(messagetype.equals("fj"))
							{
								MessageListActivity.instance.showBankRoomWindo();
							}
							else if(messagetype.equals("yh"))
							{
								MessageListActivity.instance.showDownloadCouponView();
							}
							else if(messagetype.equals("zb") || messagetype.equals("ct"))
							{
								MessageListActivity.instance.showStoreinfo(pkid,title,desc,sysImg);
							}
						}
					}
				});
			}
			dateCacheItems.put(position,currents);
			imgmapItems.put(position, viewlist);
		}
		else
		{
			v.setVisibility(View.GONE);
		}
	}
	
	public void loadRoomLayout(LinearLayout v,JSONObject job,Map dataSet)
	{
		try{
			View itemview = mInflater.inflate(R.layout.message_room_layout, null);
			RelativeLayout rlayoutimg = (RelativeLayout) itemview.findViewById(R.id.store_img);
			TextView store_name = (TextView) itemview.findViewById(R.id.store_name);
			ListView roomlist = (ListView) itemview.findViewById(R.id.room_list);
			
			String storename = job.getString("name");
			String storeimg = job.getString("img");
			JSONArray jArr = (JSONArray) job.get("dlist");
			List<Map<String,Object>> dlist = getRoomDetialData(jArr);
			SpecialAdapterDufel sadapter = new SpecialAdapterDufel(contexts, dlist, R.layout.message_room_list_item,
					new String[] {"sysImg","roomStyle","roomPrice","roomDesc"}, 
					new int[] {R.id.head,R.id.name_txt,R.id.price_txt,R.id.doc_txt},
					share,"max");
			
			roomlist.setDividerHeight(0);
			roomlist.setAdapter(sadapter);
			
			store_name.setText(storename);
			if(!storeimg.equals(""))
			{
				 if (imageCache.containsKey(BASE_URL+storeimg)) {
					 Drawable drawable = imageCache.get(BASE_URL+storeimg);   
					 rlayoutimg.setBackgroundDrawable(drawable);
				 }
				 else
				 {
					 downloadImageFile(BASE_URL+storeimg,rlayoutimg);
				 }
			}
			
			v.addView(itemview);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setViewText(final TextView textview, String value,Map dataSet,View view,final int tag) {
		final String fileUrl2 = (String)dataSet.get("fileUrl2");
		final String fileType2 = (String)dataSet.get("fileType2");
		final String fileName2 = (String)dataSet.get("fileName2");
		final String time = (String)dataSet.get("time");
		final LinearLayout aim_layout = (LinearLayout)view.findViewById(R.id.aim_layout);
		LinearLayout layout_text = (LinearLayout)view.findViewById(R.id.layout_text);
		final String fusernameid = (String)dataSet.get("mysendname");
        final String touserid = (String)dataSet.get("toname");
		textview.setText("");
		if(fileType2 != null && !fileType2.equals(""))
		{
//			textview.setText("");
			int longwith = Integer.valueOf(time)*20;
			if(longwith > 150)
				aim_layout.setLayoutParams(new LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT));
			else
			{
				if(longwith < 70)
					aim_layout.setLayoutParams(new LinearLayout.LayoutParams(70, LayoutParams.WRAP_CONTENT));
				else
					aim_layout.setLayoutParams(new LinearLayout.LayoutParams(longwith, LayoutParams.WRAP_CONTENT));
			}
			if(tag == 1)
				textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
			else
				textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
			
//			textview.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					downloadArmData(fileUrl2,fileName2,textview,aim_layout,tag);
//				}
//			});
			
			if(fileUrl2.indexOf("http") >= 0)
			{
				downloadArmFile(fileUrl2,fileName2,textview,aim_layout,tag);
				
				layout_text.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
	//		            int result = downloader.downFile(fileUrl2, "voa/", fileName2); 
	//		            if(result != -1)
	//		            {
	//		            	playMusic(fileName2,textview,aim_layout,2);
	//		            }
			            downloadArmData(fileUrl2,fileName2,textview,aim_layout,tag,fusernameid,touserid);
					}
				});
			}
			else
			{
				layout_text.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
	//		            int result = downloader.downFile(fileUrl2, "voa/", fileName2); 
	//		            if(result != -1)
	//		            {
	//		            	playMusic(fileName2,textview,aim_layout,2);
	//		            }
						playMusic2(fileUrl2,textview,aim_layout,tag);
					}
				});
			}
		}
	}
	
	public void setViewText2(TextView textview, String value,Map dataSet,View view) {
		String fileType2 = (String)dataSet.get("fileType2");
		if(fileType2 != null && !fileType2.equals(""))
		{
			if(fileType2.equals("image/png"))
			{
				textview.setVisibility(View.GONE);
			}
			else
			{
				textview.setText(value);
			}
		}
		else
		{
			textview.setVisibility(View.GONE);
		}
	}
	
	public void downloadArmData(final String fileUrl,final String fileName,final TextView textview,final LinearLayout aim_layout,final int tag,final String fusernameid,final String touserid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONArray jArr;
				JSONObject jobj;
				try {
					String filePath = null;
					if(tag == 1)
					{
						filePath = fileUtil.createVoice2File1a(fusernameid, fileUrl, fileName);
					}
					else
					{
						filePath = fileUtil.createVoiceFile1a(touserid, fileUrl, fileName);
					}
					int result = 1;
					if(filePath == null)
						result = -1;
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("result", result);
					map.put("textview", textview);
					map.put("aim_layout", aim_layout);
					map.put("fileName", fileName);
					map.put("tag", tag);
					msg.obj = map;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void downloadArmFile(final String fileUrl,final String fileName,final TextView textview,final LinearLayout aim_layout,final int tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONArray jArr;
				JSONObject jobj;
				try {
					int result = downloader.downFile(fileUrl, "voa/", fileName); 
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("result", result);
					map.put("textview", textview);
					map.put("aim_layout", aim_layout);
					map.put("fileName", fileName);
					map.put("tag", tag);
					msg.obj = map;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void downloadImageFile(final String imgurl,final RelativeLayout imglayout)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					Drawable bitmap = loadImageFromUrl(imgurl);
					if(bitmap != null)
					{
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("img", bitmap);
						map.put("imglayout", imglayout);
						map.put("imgurl", imgurl);
						msg.obj = map;
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				int result = (Integer)map.get("result");
				String fileName = (String)map.get("fileName");
				TextView textview = (TextView)map.get("textview");
				LinearLayout aim_layout = (LinearLayout)map.get("aim_layout");
				int tag = (Integer)map.get("tag");
				if(result != -1)
	            {
	            	playMusic(fileName,textview,aim_layout,tag);
	            }
				break;
			case 1:
				break;
			case 2:
				Map<String,Object> imgmap = (Map<String,Object>)msg.obj;
				if(imgmap != null)
				{
					Drawable drawable = (Drawable)imgmap.get("img");
					String imgurl = (String)imgmap.get("imgurl");
					imageCache.put(imgurl, drawable);
					RelativeLayout imglayout = (RelativeLayout)imgmap.get("imglayout");
					imglayout.setBackgroundDrawable(drawable);
				}
				break;
			}
		}
	};
	
	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name,final TextView textview,LinearLayout amilay,final int tofrom) {
		try {
			onplayer.start();
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				if(curreTextView != null)
				{
					curreTextView.setBackgroundDrawable(null);
					if(curretofrom == 1)
						curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
					else
						curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
					voiceanim.stop();
				}
			}
			textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			textview.setBackgroundDrawable(null);
			if(tofrom == 1)
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_anim));
			else
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_anim_right));
			Object ob = textview.getBackground();
			voiceanim = (AnimationDrawable) ob;
			voiceanim.stop();
		    voiceanim.start();
			curreLayout = amilay;
			curreTextView = textview;
			currevoiceanim = voiceanim;
			curretofrom = tofrom;
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(android.os.Environment.getExternalStorageDirectory()+"/voa/"+name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					textview.setBackgroundDrawable(null);
					if(tofrom == 1)
						textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
					else
						textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
					voiceanim.stop();
					offplayer.start();
				}
			});

		} catch (Exception e) {
			if(curreTextView != null)
			{
				curreTextView.setBackgroundDrawable(null);
				if(curretofrom == 1)
					curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
				else
					curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
				voiceanim.stop();
			}
			makeText("ʧ��");
			File file = new File(android.os.Environment.getExternalStorageDirectory()+"/voa/"+name);
			file.delete();
			makeText("�ļ���ɾ��");
			e.printStackTrace();
			
		}

	}
	
	private void playMusic2(String filePath,final TextView textview,LinearLayout amilay,final int tofrom) {
		try {
			onplayer.start();
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				if(curreTextView != null)
				{
					curreTextView.setBackgroundDrawable(null);
					if(curretofrom == 1)
						curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
					else
						curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
					voiceanim.stop();
				}
			}
			textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			textview.setBackgroundDrawable(null);
			if(tofrom == 1)
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_anim));
			else
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_anim_right));
			Object ob = textview.getBackground();
			voiceanim = (AnimationDrawable) ob;
			voiceanim.stop();
		    voiceanim.start();
			curreLayout = amilay;
			curreTextView = textview;
			currevoiceanim = voiceanim;
			curretofrom = tofrom;
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					textview.setBackgroundDrawable(null);
					if(tofrom == 1)
						textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
					else
						textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
					voiceanim.stop();
					offplayer.start();
				}
			});

		} catch (Exception e) {
			if(curreTextView != null)
			{
				curreTextView.setBackgroundDrawable(null);
				if(curretofrom == 1)
					curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
				else
					curreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
				voiceanim.stop();
			}
			makeText("ʧ��");
			File file = new File(filePath);
			file.delete();
			makeText("�ļ���ɾ��");
			e.printStackTrace();
			
		}

	}

	public void setViewImage(final ImageView v, String url,LinearLayout imglayout,ProgressBar pb,TextView textview,TextView messagetext,View view,int rowindex,Map<String,Object> data) {
		String sendprogress = (String)data.get("sendprogress");
		String mid = (String)data.get("mid");
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
			if(imgtype.equals("ico"))
				url = str+"_ico"+str2;
			else if(imgtype.equals("zhong"))
				url = str+"_zhong"+str2;
		}
		
		final String imgurl = url;
		
		
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
					if(url.indexOf("http:") >= 0)
					{
						final ProgressBar pb2 = (ProgressBar)view.findViewById(R.id.progressBar2);
						pb2.setVisibility(View.VISIBLE);
						imageLoader2.loadDrawable(url, new AsyncImageLoader2.ImageCallback() {
							public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
								if (imageDrawable != null) {
									
									v.setImageBitmap(imageDrawable);
									pb2.setVisibility(View.GONE);
								}
							}
						},dstWidth,dstHeight);
						messagetext.setVisibility(View.GONE);
						v.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								showMessageDetails(imgurl);
							}
						});
					}
					else
					{
						if(!url.equals(""))
						{
							Bitmap bitmap = null;
							WeakReference<Bitmap> current = dateCache.get(rowindex);
							if(current == null)
							{
								url = url.replace("/1b/", "/1a/");
								bitmap = getLoacalBitmap2(url);
								dateCache.put(rowindex, new WeakReference<Bitmap>(bitmap));  
							}
							else
							{
								bitmap = current.get();
							}
							ptext = textview;
							playout = imglayout;
							if(sendprogress.equals("0"))
								imglayout.setVisibility(View.VISIBLE);
//							url = url.replace("/1b/", "/1a/");
//							Bitmap bitmap = getLoacalBitmap2(url);
							((ImageView) v).setImageBitmap(bitmap);
							int with = bitmap.getWidth();
							int high = bitmap.getHeight();
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", ((ImageView) v));
							map.put("value", url);
							map.put("with", with);
							map.put("high", high);
							map.put("imglayout", imglayout);
							imgmap.put(rowindex, map);
							
							if(sendprogress.equals("0"))
							{
								imglayout.setLayoutParams(new RelativeLayout.LayoutParams(with, high));
								LayoutParams lp = imglayout.getLayoutParams();        
						        lp.width=with;
						        lp.height=high;        
						        imglayout.setLayoutParams(lp);
								MessageListActivity.instance.sendMessagess(rowindex,mid);
							}
							messagetext.setVisibility(View.GONE);
							
							v.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									showMessageDetails(imgurl);
								}
							});
						}
//						else
//						{
//							((ImageView) v).setVisibility(View.GONE);
//						}
					}
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
	
	public void setViewImage(final ImageView v, String url,Map dataSet) {
		
//		if(v.getId() == R.id.store_img)
//		{
//			v.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					String messagetype = (String)dataSet.get("messagetype");
//					if(messagetype.equals("") || messagetype.equals("wh"))
//					{
//						
//					}
//					else
//					{
//						if(messagetype.equals("fj"))
//						{
//							MessageListActivity.instance.showBankRoomWindo();
//						}
//					}
//				}
//			});
//		}
		
//		v.setClickable(false);
		
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
//		v.setScaleType(ImageView.ScaleType.CENTER);
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
					imageLoader2.loadDrawable(url, new AsyncImageLoader2.ImageCallback() {
						public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
							if (imageDrawable != null && imageDrawable.getWidth() > 0) {
								
								v.setImageBitmap(imageDrawable);
							}
						}
					},300,140);
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
	
	public void setViewRoundAngleImageView(final RoundAngleImageView v, String url,Map dataSet) {
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
					 ((RoundAngleImageView) v).setImageBitmap(bitmap);
				}
				else
				{
					imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
							if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
								
								v.setImageDrawable(imageDrawable);
							}
						}
					},80,80);
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
	
	public void showMessageDetails(String flieUrls){
		try{
			Intent intent = new Intent();
		    intent.setClass( contexts,ImageTouchActivity.class);
		    Bundle bundle = new Bundle();
//		    flieUrls = flieUrls.replace("/1a/", "/1b/");
			bundle.putString("flieUrls", flieUrls);
			intent.putExtras(bundle);
		    contexts.startActivity(intent);
		    ((Activity)contexts).overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public TextView getPtext()
	{
		return ptext;
	}
	
	public TextView getPtext(int rowindex)
	{
		View rowView = this.viewMap.get(rowindex);
		TextView progress_tv = (TextView)rowView.findViewById(R.id.progress_tv);
		ptext = progress_tv;
		return ptext;
	}
	
	public LinearLayout getPlayout()
	{
		return playout;
	}
	
	public LinearLayout getPlayout(int rowindex)
	{
		View rowView = this.viewMap.get(rowindex);
		LinearLayout imglayout = (LinearLayout)rowView.findViewById(R.id.upload_img_layout);
		playout = imglayout;
		return playout;
	}
	
	/**
	* 
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 4;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	public Bitmap getLoacalBitmap2(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	public List<Map<String,Object>> getRoomDetialData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String roomStyle = ""; 
				if(dobj.has("roomStyle"))
					roomStyle = (String) dobj.get("roomStyle");
				
				String roomDesc = ""; 
				if(dobj.has("roomDesc"))
					roomDesc = (String) dobj.get("roomDesc");
				
				String roomPrice = ""; 
				if(dobj.has("roomPrice"))
				{
					roomPrice = (String) dobj.get("roomPrice");
					if(!roomPrice.equals(""))
					{
						String [] str = roomPrice.split("\\.");
						roomPrice = str[0] + "";
					}
				}
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("roomStyle", roomStyle);
				map.put("roomDesc", roomDesc);
				map.put("roomPrice", roomPrice);
				map.put("sysImg", sysImg);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
}