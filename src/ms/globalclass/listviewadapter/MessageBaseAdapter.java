package ms.globalclass.listviewadapter;

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
import ms.activitys.hotel.AsyncImageLoader2;
import ms.activitys.hotel.HttpDownloader;
import ms.activitys.hotel.ImageTouchActivity;
import ms.activitys.hotel.MessageListActivity;
import ms.globalclass.EllipsizeText;
import ms.globalclass.FileUtils;
import ms.globalclass.PowerImageView;
import ms.globalclass.image.RoundAngleImageView;
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
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MessageBaseAdapter extends BaseAdapter{

	private AsyncImageLoader2 imageLoader2 = new AsyncImageLoader2();
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private static Map<Integer, View> viewMap = new HashMap<Integer, View>();
//	private Map<String, View> viewMap = new HashMap<String, View>();
	private static ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData; 
	private int mResource; 
	private int mResource2;
	private int mResource3;
	private int mResource4;
	private int mResource5;
	private int mResource6;
	private static LayoutInflater mInflater;
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
//	private static MediaPlayer onplayer;
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
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	final int TYPE_3 = 2;
	final int TYPE_4 = 3;
	final int TYPE_5 = 4;
	final int TYPE_6 = 5;
	final int TYPE_7 = 6;
	final int TYPE_8 = 7;
	public Map<Integer,WeakReference<Bitmap>> dateCache = new HashMap<Integer,WeakReference<Bitmap>>();//图片资源缓存
	private Map<Integer,Map<String,Object>> imgmap = new HashMap<Integer,Map<String,Object>>();//图片控件缓存
	
	private Map<Integer,List<WeakReference<Bitmap>>> dateCacheItems = new HashMap<Integer,List<WeakReference<Bitmap>>>();//列表图片资源缓存
	private Map<Integer,List<Map<String,Object>>> imgmapItems = new HashMap<Integer,List<Map<String,Object>>>();//列表图片控件缓存
	private boolean SCROLL_STATE_TOUCH_SCROLL = false;//是否正在滚动
	
	public boolean isSCROLL_STATE_TOUCH_SCROLL() {
		return SCROLL_STATE_TOUCH_SCROLL;
	}
	public void setSCROLL_STATE_TOUCH_SCROLL(boolean sCROLL_STATE_TOUCH_SCROLL) {
		SCROLL_STATE_TOUCH_SCROLL = sCROLL_STATE_TOUCH_SCROLL;
	}
	
	@SuppressWarnings("unchecked")
	public MessageBaseAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, int resource2,int resource3,int resource4,int resource5,int resource6,String[] from, int[] to,SharedPreferences  shares,String type,String userid){
		mData = data;
		mResource = resource;
		mResource2 = resource2;
		mResource3 = resource3;
		mResource4 = resource4;
		mResource5 = resource5;
		mResource6 = resource6;
		mFrom = from;
		mTo = to;
		contexts = context;
		share = shares;
		imgtype = type;
		this.userid = userid;
		myapp = (MyApp)contexts.getApplicationContext();
		
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		String ip = this.share.getString("ipadrees", "121.199.8.186");
		BASE_URL = "http://"+ip+":80/upload/";
		
		downloader = new HttpDownloader();
		
//		if(onplayer == null){
//			onplayer = MediaPlayer.create(context, R.raw.on);
//		}
		
		if(offplayer == null){
			offplayer = MediaPlayer.create(context, R.raw.play_completed);
		}
		
		if(viewMap == null)
			viewMap = new HashMap<Integer, View>();
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
	
	@Override
	public int getItemViewType(int position){
		Map dataSet = mData.get(position);
		String mysendname = (String)dataSet.get("mysendname");
		String messagetype = (String)dataSet.get("messagetype");
		String mymessagecontent = (String)dataSet.get("mymessagecontent");
		boolean b = true;
		JSONObject job = null;
		try{
			job = new JSONObject(mymessagecontent);
			if(job.has("type") && job.getString("type").equals("link"))
			{
				b = true;
			}
			else
			{
				b = false;
			}
		}catch(Exception ex){
			b = false;
		}
		if(b)
		{
			if(mysendname.equals(userid))
			{
				return TYPE_6;
			}
			else
			{
				return TYPE_5;
			}
		}
		else
		{
			if(mysendname.equals(userid) && !messagetype.equals("groupadddel"))
			{
				if(myapp.getTapeimgs1().contains(mymessagecontent))
				{
					return TYPE_8;
				}
				else
					return TYPE_2;
			}
			if(messagetype.equals("") || messagetype.equals("wh") || messagetype.equals("jf") || messagetype.equals("cpcx") || messagetype.equals("friend") || messagetype.equals("qa") || messagetype.equals("group"))
			{
				if(myapp.getTapeimgs1().contains(mymessagecontent))
				{
					return TYPE_7;
				}
				else
					return TYPE_1;
			}
			else if(messagetype.equals("zdy") || messagetype.equals("dz"))
			{
				return TYPE_4;
			}
			else if(messagetype.equals("yanzhen"))
				return TYPE_1;
			else if(messagetype.equals("yanzhenjieguo"))
				return TYPE_1;
			else if(messagetype.equals("groupadddel"))
				return TYPE_1;
			else
			{
				return TYPE_3;
			}
		}
	}
	
	@Override
	public int getViewTypeCount() {
		//因为有三种视图，所以返回3
		return 8;
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
		    viewMap = null;
		    
		    System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void releaseBitmap(int start, int end) {
		// 在这，我们分别预存储了第一个和最后一个可见位置之外的3个位置的bitmap
		// 即dataCache中始终只缓存了（M＝6＋Gallery当前可见view的个数）M个bitmap
		start = start - 1;
		end = end + 1;
		// 释放position<start之外的bitmap资源
		WeakReference<Bitmap> delBitmap;
		List<WeakReference<Bitmap>> delBitmapList;
		for (int del = 0; del < start; del++) {//释放当前屏幕往上不可见部分的图片资源
			delBitmap = dateCache.get(del);
			if (delBitmap != null) {
				// 如果非空则表示有缓存的bitmap，需要清理
				// 从缓存中移除该del->bitmap的映射
				dateCache.remove(del);
				if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {
					delBitmap.get().recycle();
				}
//				System.out.println("sart==delete"+del);
			}
			if(viewMap.get(del) != null)
				viewMap.remove(del);
			
			if(imgmap.get(del) != null)
				imgmap.remove(del);
			
			
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
		
		freeBitmapFromIndex(end,mData.size());//释放当前屏幕往下不可见部分的图片资源
		
		loadVisibleItemImageData(start,end);//加载当前屏幕可见部分的图片
	}
	      
	/**
	 * 从某一位置开始释放bitmap资源
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
				if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {  
					delBitmap.get().recycle();
				}
//				System.out.println("sart==delete2-"+del);
			}
			
			if(imgmap.get(del) != null)
				imgmap.remove(del);
			
			if(viewMap.get(del) != null)
				viewMap.remove(del);
			
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
			for(int i = sart;i<end-1;i++)
			{
				WeakReference<Bitmap> bitmap = null;
//				WeakReference<Bitmap> bitmap = dateCache.get(i);
//				if(bitmap == null)
//				{
					Map<String,Object> maps = imgmap.get(i);
					if(maps != null)
					{
						ImageView imgview = (ImageView)maps.get("view");
						String data = (String)maps.get("value");
						if(maps.containsKey("linkimg"))
						{
							bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data,60,60));
						}
						else
							bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data));
						imgview.setImageBitmap(bitmap.get());
						dateCache.put(i, bitmap);
						imgmap.put(i, maps);
//						System.out.println("sart==重新赋值");
					}
//				}
				
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
								if(maps.containsKey("linkimg"))
								{
									bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data,60,60));
								}
								else
									bitmap = new WeakReference<Bitmap>(getLoacalBitmap2(data));
								imgview.setImageBitmap(bitmap.get());
								bitmaplist.add(bitmap);
	//							System.out.println("sart==重新赋值");
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
	
	public void dataSetChanged(int index) {
		Map<String,Object> map = (Map<String,Object>)mData.get(index);
		map.put("checkbox", true);
		notifyDataSetChanged();
	}
	
	public View getSelectView(int index)
	{
		View view = null;
		try{
			view = viewMap.get(index);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return view;
	}
	
	public void setItemSendImgStart(int viewtype,View view,boolean isShow,final int index)
	{
//		View rowView = this.viewMap.get(index);
//		if(rowView != null)
//		{
//			final View[] holder = (View[]) rowView.getTag();
//			final View v = holder[11];
//			if(isShow)
//			{
//				v.setVisibility(View.VISIBLE);
//				v.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						MessageListActivity.instance.openAgainSendMuneView(index);
//					}
//				});
//			}
//			else
//				v.setVisibility(View.GONE);
//		}
		try{
			switch(viewtype)
			{
			case TYPE_1:
				ViewHolder1 viewHolder1 = (ViewHolder1)view.getTag();
				if(isShow)
				{
					viewHolder1.start_img.setVisibility(View.VISIBLE);
					viewHolder1.start_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							MessageListActivity.instance.openAgainSendMuneView(index);
						}
					});
				}
				else
					viewHolder1.start_img.setVisibility(View.GONE);
				break;
			case TYPE_2:
				ViewHolder2 viewHolder2 = (ViewHolder2)view.getTag();
				if(isShow)
				{
					viewHolder2.start_img.setVisibility(View.VISIBLE);
					viewHolder2.start_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							MessageListActivity.instance.openAgainSendMuneView(index);
						}
					});
				}
				else
					viewHolder2.start_img.setVisibility(View.GONE);
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setItemSendPressStart(int viewtype,View view,boolean isShow)
	{
//		View rowView = this.viewMap.get(index);
//		if(rowView != null)
//		{
//			final View[] holder = (View[]) rowView.getTag();
//			final View v = holder[12];
//			if(isShow)
//				v.setVisibility(View.VISIBLE);
//			else
//				v.setVisibility(View.GONE);
//		}
		try{
			switch(viewtype)
			{
			case TYPE_1:
				ViewHolder1 viewHolder1 = (ViewHolder1)view.getTag();
				if(isShow)
					viewHolder1.send_progressBar.setVisibility(View.VISIBLE);
				else
					viewHolder1.send_progressBar.setVisibility(View.GONE);
				break;
			case TYPE_2:
				ViewHolder2 viewHolder2 = (ViewHolder2)view.getTag();
				if(isShow)
					viewHolder2.send_progressBar.setVisibility(View.VISIBLE);
				else
					viewHolder2.send_progressBar.setVisibility(View.GONE);
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setItemImageLayout(int viewtype,View view)
	{
//		View rowView = this.viewMap.get(index);
//		LinearLayout imglayout = (LinearLayout)rowView.findViewById(R.id.upload_img_layout);
//		imglayout.setVisibility(View.VISIBLE);
		try{
			switch(viewtype)
			{
			case TYPE_1:
				ViewHolder1 viewHolder1 = (ViewHolder1)view.getTag();
				viewHolder1.upload_img_layout.setVisibility(View.VISIBLE);
				break;
			case TYPE_2:
				ViewHolder2 viewHolder2 = (ViewHolder2)view.getTag();
				viewHolder2.upload_img_layout.setVisibility(View.VISIBLE);
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Map<String,Object> getImageWH(int index)
	{
		Map<String,Object> map = null;
		try{
			map = imgmap.get(index);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try{
			Map dataSet = mData.get(position);
			
			ViewHolder1 viewHolder1 = null;
			ViewHolder2 viewHolder2 = null;
			ViewHolder3 viewHolder3 = null;
			ViewHolder4 viewHolder4 = null;
			ViewHolder5 viewHolder5 = null;
			ViewHolder6 viewHolder6 = null;
			ViewHolder7 viewHolder7 = null;
			ViewHolder8 viewHolder8 = null;
			
			int type = getItemViewType(position);
			
			//无convertView，需要new出各个控件
			if(convertView == null){
				//按当前所需的样式，确定new的布局
				switch(type)
				{
				case TYPE_1:
					convertView = LayoutInflater.from(contexts).inflate(mResource, null);
					viewHolder1 = new ViewHolder1();
					viewHolder1.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder1.message_rlayout = (RelativeLayout)convertView.findViewById(R.id.message_rlayout);
					viewHolder1.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder1.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder1.mymessagecontent = (TextView)convertView.findViewById(R.id.mymessagecontent);
					viewHolder1.mymessagecontent2 = (TextView)convertView.findViewById(R.id.mymessagecontent2);
					viewHolder1.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder1.progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBar2);
					viewHolder1.upload_img_layout = (LinearLayout)convertView.findViewById(R.id.upload_img_layout);
					viewHolder1.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
					viewHolder1.progress_tv = (TextView)convertView.findViewById(R.id.progress_tv);
					viewHolder1.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder1.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder1.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder1.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder1.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					convertView.setTag(viewHolder1);
					break;
				case TYPE_2:
					convertView = LayoutInflater.from(contexts).inflate(mResource2, null);
					viewHolder2 = new ViewHolder2();
					viewHolder2.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder2.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder2.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder2.mymessagecontent = (TextView)convertView.findViewById(R.id.mymessagecontent);
					viewHolder2.mymessagecontent2 = (TextView)convertView.findViewById(R.id.mymessagecontent2);
					viewHolder2.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder2.progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBar2);
					viewHolder2.upload_img_layout = (LinearLayout)convertView.findViewById(R.id.upload_img_layout);
					viewHolder2.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
					viewHolder2.progress_tv = (TextView)convertView.findViewById(R.id.progress_tv);
					viewHolder2.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder2.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder2.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder2.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder2.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					viewHolder2.message_start_txt = (TextView)convertView.findViewById(R.id.message_start_txt);
					convertView.setTag(viewHolder2);
					break;
				case TYPE_3:
					convertView = LayoutInflater.from(contexts).inflate(mResource3, null);
					viewHolder3 = new ViewHolder3();
					viewHolder3.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder3.content_layout = (LinearLayout)convertView.findViewById(R.id.content_layout);
					viewHolder3.store_img = (ImageView)convertView.findViewById(R.id.store_img);
					viewHolder3.store_name = (TextView)convertView.findViewById(R.id.store_name);
					viewHolder3.store_item_list = (LinearLayout)convertView.findViewById(R.id.store_item_list);
					convertView.setTag(viewHolder3);
					break;
				case TYPE_4:
					convertView = LayoutInflater.from(contexts).inflate(mResource4, null);
					viewHolder4 = new ViewHolder4();
					viewHolder4.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder4.content_layout = (LinearLayout)convertView.findViewById(R.id.content_layout);
					viewHolder4.store_img = (ImageView)convertView.findViewById(R.id.store_img);
					viewHolder4.store_name = (TextView)convertView.findViewById(R.id.store_name);
					viewHolder4.store_doc_txt = (TextView)convertView.findViewById(R.id.store_doc_txt);
					convertView.setTag(viewHolder4);
					break;
				case TYPE_5:
					convertView = LayoutInflater.from(contexts).inflate(mResource5, null);
					viewHolder5 = new ViewHolder5();
					viewHolder5.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder5.message_rlayout = (RelativeLayout)convertView.findViewById(R.id.message_rlayout);
					viewHolder5.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder5.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder5.title_txt = (TextView)convertView.findViewById(R.id.title_txt);
					viewHolder5.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder5.store_doc = (EllipsizeText)convertView.findViewById(R.id.store_doc);
					viewHolder5.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder5.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder5.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder5.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder5.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					convertView.setTag(viewHolder5);
					break;
				case TYPE_6:
					convertView = LayoutInflater.from(contexts).inflate(mResource6, null);
					viewHolder6 = new ViewHolder6();
					viewHolder6.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder6.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder6.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder6.title_txt = (TextView)convertView.findViewById(R.id.title_txt);
					viewHolder6.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder6.store_doc = (EllipsizeText)convertView.findViewById(R.id.store_doc);
					viewHolder6.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder6.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder6.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder6.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder6.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					viewHolder6.message_start_txt = (TextView)convertView.findViewById(R.id.message_start_txt);
					convertView.setTag(viewHolder6);
					break;
				case TYPE_7:
					convertView = LayoutInflater.from(contexts).inflate(mResource, null);
					viewHolder7 = new ViewHolder7();
					viewHolder7.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder7.message_rlayout = (RelativeLayout)convertView.findViewById(R.id.message_rlayout);
					viewHolder7.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder7.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder7.mymessagecontent = (TextView)convertView.findViewById(R.id.mymessagecontent);
					viewHolder7.mymessagecontent2 = (TextView)convertView.findViewById(R.id.mymessagecontent2);
					viewHolder7.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder7.progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBar2);
					viewHolder7.upload_img_layout = (LinearLayout)convertView.findViewById(R.id.upload_img_layout);
					viewHolder7.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
					viewHolder7.progress_tv = (TextView)convertView.findViewById(R.id.progress_tv);
					viewHolder7.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder7.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder7.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder7.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder7.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					viewHolder7.gif1 = (PowerImageView)convertView.findViewById(R.id.gif1);
					convertView.setTag(viewHolder7);
					break;
				case TYPE_8:
					convertView = LayoutInflater.from(contexts).inflate(mResource2, null);
					viewHolder8 = new ViewHolder8();
					viewHolder8.mysendtime = (TextView)convertView.findViewById(R.id.mysendtime);
					viewHolder8.layout_text = (LinearLayout)convertView.findViewById(R.id.layout_text);
					viewHolder8.iv_userhead = (RoundAngleImageView)convertView.findViewById(R.id.iv_userhead);
					viewHolder8.mymessagecontent = (TextView)convertView.findViewById(R.id.mymessagecontent);
					viewHolder8.mymessagecontent2 = (TextView)convertView.findViewById(R.id.mymessagecontent2);
					viewHolder8.uploadFileImg = (ImageView)convertView.findViewById(R.id.uploadFileImg);
					viewHolder8.progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBar2);
					viewHolder8.upload_img_layout = (LinearLayout)convertView.findViewById(R.id.upload_img_layout);
					viewHolder8.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
					viewHolder8.progress_tv = (TextView)convertView.findViewById(R.id.progress_tv);
					viewHolder8.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
					viewHolder8.aim_layout = (LinearLayout)convertView.findViewById(R.id.aim_layout);
					viewHolder8.send_progressBar = (ProgressBar)convertView.findViewById(R.id.send_progressBar);
					viewHolder8.start_img = (ImageView)convertView.findViewById(R.id.start_img);
					viewHolder8.tv_username = (TextView)convertView.findViewById(R.id.tv_username);
					viewHolder8.message_start_txt = (TextView)convertView.findViewById(R.id.message_start_txt);
					viewHolder8.gif1 = (PowerImageView)convertView.findViewById(R.id.gif1);
					convertView.setTag(viewHolder8);
					break;
				}
			}else{
				//有convertView，按样式，取得不用的布局
				switch(type)
				{
				case TYPE_1:
					viewHolder1 = (ViewHolder1) convertView.getTag();
					break;
				case TYPE_2:
					viewHolder2 = (ViewHolder2) convertView.getTag();
					break;
				case TYPE_3:
					viewHolder3 = (ViewHolder3) convertView.getTag();
					break;
				case TYPE_4:
					viewHolder4 = (ViewHolder4) convertView.getTag();
					break;
				case TYPE_5:
					viewHolder5 = (ViewHolder5) convertView.getTag();
					break;
				case TYPE_6:
					viewHolder6 = (ViewHolder6) convertView.getTag();
					break;
				case TYPE_7:
					viewHolder7 = (ViewHolder7) convertView.getTag();
					break;
				case TYPE_8:
					viewHolder8 = (ViewHolder8) convertView.getTag();
					break;
				}
			}
			
			viewMap.put(position, convertView);
			//设置资源
			switch(type)
			{
			case TYPE_1:
				setLeftMessageView(viewHolder1, dataSet, position);
				break;
			case TYPE_2:
				setRightMessageView(viewHolder2, dataSet, position);
				break;
			case TYPE_3:
				setMessageListItemView(viewHolder3, dataSet, position);
				break;
			case TYPE_4:
				setMessageOneItemView(viewHolder4, dataSet, position);
				break;
			case TYPE_5:
				setLeftMessageLinkView(viewHolder5, dataSet, position);
				break;
			case TYPE_6:
				setRightMessageLinkView(viewHolder6, dataSet, position);
				break;
			case TYPE_7:
				setLeftMessageTapeView(viewHolder7, dataSet, position);
				break;
			case TYPE_8:
				setRightMessageTapeView(viewHolder8, dataSet, position);
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return convertView;
	}
	
	static class ViewHolder1{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView mymessagecontent;
		TextView mymessagecontent2;
		ImageView uploadFileImg; 
		ProgressBar progressBar2;
		LinearLayout upload_img_layout;
		ProgressBar progressBar;
		TextView progress_tv;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		RelativeLayout message_rlayout;
	}
	
	static class ViewHolder2{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView mymessagecontent;
		TextView mymessagecontent2;
		ImageView uploadFileImg; 
		ProgressBar progressBar2;
		LinearLayout upload_img_layout;
		ProgressBar progressBar;
		TextView progress_tv;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		TextView message_start_txt;
	}
	
	static class ViewHolder3{
		TextView mysendtime;
		LinearLayout content_layout;
		ImageView store_img;
		TextView store_name;
		LinearLayout store_item_list;
	}
	
	static class ViewHolder4{
		TextView mysendtime;
		TextView store_name;
		ImageView store_img;
		TextView store_doc_txt;
		LinearLayout content_layout;
	}
	
	static class ViewHolder5{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView title_txt;
		ImageView uploadFileImg; 
		EllipsizeText store_doc;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		RelativeLayout message_rlayout;
	}
	
	static class ViewHolder6{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView title_txt;
		ImageView uploadFileImg; 
		EllipsizeText store_doc;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		TextView message_start_txt;
	}
	
	static class ViewHolder7{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView mymessagecontent;
		TextView mymessagecontent2;
		ImageView uploadFileImg; 
		ProgressBar progressBar2;
		LinearLayout upload_img_layout;
		ProgressBar progressBar;
		TextView progress_tv;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		RelativeLayout message_rlayout;
		PowerImageView gif1;
	}
	
	static class ViewHolder8{
		TextView mysendtime;
		LinearLayout layout_text;
		RoundAngleImageView iv_userhead;
		TextView mymessagecontent;
		TextView mymessagecontent2;
		ImageView uploadFileImg; 
		ProgressBar progressBar2;
		LinearLayout upload_img_layout;
		ProgressBar progressBar;
		TextView progress_tv;
		TextView tv_time;
		LinearLayout aim_layout;
		ProgressBar send_progressBar;
		ImageView start_img;
		TextView tv_username;
		TextView message_start_txt;
		PowerImageView gif1;
	}
	
	public void setLeftMessageView(final ViewHolder1 viewHolder1,Map<String,Object> dataSet,final int position)
	{
		try{
			String messagetype = (String)dataSet.get("messagetype");
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
//			CharSequence sps = MessageListActivity.instance.getMessageContent(mymessagecontent);
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			final String fileUrl2 = (String)dataSet.get("fileUrl2");
			String fileType2 = (String)dataSet.get("fileType2");
			final String fileName2 = (String)dataSet.get("fileName2");
			String time = (String)dataSet.get("time");
			final String fusernameid = (String)dataSet.get("mysendname");
	        final String touserid = (String)dataSet.get("toname");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
			
	        if(messagetype.equals("yanzhen"))
	        {
	        	String content = "<font color='#ffffff' size='16px'>"+contexts.getString(R.string.hotel_label_92)+"</font><br><font color='#0101DF' size='16px'>"+contexts.getString(R.string.hotel_label_93)+"</font>";
	        	viewHolder1.mysendtime.setText(Html.fromHtml(content));
	        	viewHolder1.mysendtime.setTextSize(13);
	        	viewHolder1.message_rlayout.setVisibility(View.GONE);
	        	viewHolder1.mysendtime.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openVerificationWindo(touserid);
					}
				});
	        }
	        else if(messagetype.equals("groupadddel"))
	        {
	        	viewHolder1.mysendtime.setText(mymessagecontent);
	        	viewHolder1.mysendtime.setTextSize(13);
	        	viewHolder1.message_rlayout.setVisibility(View.GONE);
	        }
	        else
	        {
//	        	if(messagetype.equals("yanzhenjieguo"))
//	        	{
//	        		if(mymessagecontent.equals("0"))
//	        		{
//	        			mymessagecontent = contexts.getString(R.string.hotel_label_108);
//	        		}
//	        		else
//	        		{
//	        			mymessagecontent = contexts.getString(R.string.hotel_label_109);
//	        		}
//	        	}
	        	
				viewHolder1.mysendtime.setText(mysendtime);
				viewHolder1.mysendtime.setTextSize(11);
				viewHolder1.message_rlayout.setVisibility(View.VISIBLE);
				if(userimg != null)
					viewHolder1.iv_userhead.setImageBitmap(userimg);
//				viewHolder1.layout_text.setOnLongClickListener(new OnLongClickListener() {
//					
//					@Override
//					public boolean onLongClick(View v) {
//						// TODO Auto-generated method stub
//						myapp.setCurrentDelindex(position);
//						MessageListActivity.instance.openListViewItemMuneView("text");
//						return true;
//					}
//				});
				
				if(fileType2 != null && !fileType2.equals(""))
				{
					if(fileType2.equals("image/png"))
					{
						viewHolder1.tv_time.setVisibility(View.GONE);
					}
					else
					{
						viewHolder1.tv_time.setVisibility(View.VISIBLE);
						viewHolder1.tv_time.setText(timetext);
					}
				}
				else
				{
					viewHolder1.tv_time.setVisibility(View.GONE);
				}
				
				if(fileUrl2 != null && fileUrl2.contains(".amr"))
				{
					viewHolder1.mymessagecontent.setVisibility(View.VISIBLE);
					viewHolder1.mymessagecontent.setText("");
					viewHolder1.mymessagecontent2.setVisibility(View.GONE);
					viewHolder1.upload_img_layout.setVisibility(View.GONE);
					
					viewHolder1.layout_text.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							myapp.setCurrentDelindex(position);
							MessageListActivity.instance.openListViewItemMuneView("amr");
							return true;
						}
					});
					
					if(fileType2 != null && !fileType2.equals(""))
					{
						int longwith = Integer.valueOf(time)*30;
						if(longwith > 150)
							viewHolder1.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT));
						else
						{
							if(longwith < 70)
								viewHolder1.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(70, LayoutParams.WRAP_CONTENT));
							else
								viewHolder1.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(longwith, LayoutParams.WRAP_CONTENT));
						}
						
						viewHolder1.mymessagecontent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
						
						if(fileUrl2.indexOf("http") >= 0)
						{
							downloadArmFile(fileUrl2,fileName2,viewHolder1.mymessagecontent,viewHolder1.aim_layout,1);
							
							viewHolder1.layout_text.setOnClickListener(new OnClickListener() {
								
								public void onClick(View v) {
						            downloadArmData(fileUrl2,fileName2,viewHolder1.mymessagecontent,viewHolder1.aim_layout,1,fusernameid,touserid);
								}
							});
						}
						else
						{
							viewHolder1.layout_text.setOnClickListener(new OnClickListener() {
								
								public void onClick(View v) {
									playMusic2(fileUrl2,viewHolder1.mymessagecontent,viewHolder1.aim_layout,1);
								}
							});
						}
					}
				}
				else
				{
					viewHolder1.mymessagecontent2.setVisibility(View.VISIBLE);
//					viewHolder1.mymessagecontent2.setText(mymessagecontent);
					viewHolder1.mymessagecontent2.setText(getMessageContent(mymessagecontent));
					viewHolder1.mymessagecontent2.setMovementMethod(LinkMovementMethod.getInstance());   
			        CharSequence text = viewHolder1.mymessagecontent2.getText();   
			        if(text instanceof Spannable){   
			            int end = text.length();   
			            Spannable sp = (Spannable)viewHolder1.mymessagecontent2.getText();   
			            URLSpan[] urls=sp.getSpans(0, end, URLSpan.class);    
			            SpannableStringBuilder style=new SpannableStringBuilder(text);   
			            style.clearSpans();//should clear old spans   
			            for(URLSpan url : urls){   
			                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
//			                style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			                ((Spannable) text).setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			            }   
			            viewHolder1.mymessagecontent2.setText(text);
			        }
					viewHolder1.mymessagecontent.setVisibility(View.GONE);
					viewHolder1.upload_img_layout.setVisibility(View.GONE);
					
					viewHolder1.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					
					viewHolder1.layout_text.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							
						}
					});
					
					viewHolder1.layout_text.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							myapp.setCurrentDelindex(position);
							MessageListActivity.instance.openListViewItemMuneView("text");
							return true;
						}
					});
					
					viewHolder1.mymessagecontent2.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							myapp.setCurrentDelindex(position);
							MessageListActivity.instance.openListViewItemMuneView("text");
							return true;
						}
					});
					
					if(messagetype.equals("jf"))
					{
						viewHolder1.mymessagecontent.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								MessageListActivity.instance.openCardView();
							}
						});
					}
					else if(messagetype.equals("cpcx"))
					{
						viewHolder1.mymessagecontent.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								MessageListActivity.instance.showTrafficView();
							}
						});
					}
				}
				
				if(tag.equals("0"))
					viewHolder1.send_progressBar.setVisibility(View.VISIBLE);
				else
					viewHolder1.send_progressBar.setVisibility(View.GONE);
				
				if(sendtag.equals("0"))
				{
					viewHolder1.start_img.setVisibility(View.VISIBLE);
					viewHolder1.start_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							MessageListActivity.instance.openAgainSendMuneView(position);
						}
					});
				}
				else
					viewHolder1.start_img.setVisibility(View.GONE);
				
				if(fileUrl != null && !fileUrl.equals(""))
				{
					viewHolder1.mymessagecontent2.setVisibility(View.GONE);
					viewHolder1.uploadFileImg.setVisibility(View.VISIBLE);
					viewHolder1.uploadFileImg.setImageBitmap(null);
					viewHolder1.upload_img_layout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					viewHolder1.uploadFileImg.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					
					setViewImage(viewHolder1.uploadFileImg, fileUrl,viewHolder1.upload_img_layout,viewHolder1.progressBar,viewHolder1.progress_tv,viewHolder1.mymessagecontent,viewHolder1.progressBar2,position,dataSet);
					viewHolder1.mymessagecontent.setVisibility(View.GONE);
					
					viewHolder1.layout_text.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							myapp.setCurrentDelindex(position);
							MessageListActivity.instance.openListViewItemMuneView("img");
							return true;
						}
					});
					
					viewHolder1.uploadFileImg.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Map dataSet = mData.get(position);
							String mid = (String)dataSet.get("mid");
							MessageListActivity.instance.showMessageImageDetails(mid);
						}
					});
					
					viewHolder1.uploadFileImg.setTag(position);
					viewHolder1.uploadFileImg.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							int index = (Integer)v.getTag();
							myapp.setCurrentDelindex(index);
							MessageListActivity.instance.openListViewItemMuneView("img");
							return true;
						}
					});
				}
				else
				{
					viewHolder1.uploadFileImg.setVisibility(View.GONE);
				}
	        }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setLeftMessageLinkView(final ViewHolder5 viewHolder5,Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			String fileType2 = (String)dataSet.get("fileType2");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
	        
	        JSONObject job = new JSONObject(mymessagecontent);
	        String storeDoc = job.getString("storeDoc");
	        final String storename = job.getString("storename");
	        final String link = job.getString("link");
			
	        viewHolder5.title_txt.setText(storename);
	        viewHolder5.store_doc.setText(storeDoc);
			viewHolder5.mysendtime.setText(mysendtime);
			viewHolder5.mysendtime.setTextSize(11);
			viewHolder5.message_rlayout.setVisibility(View.VISIBLE);
			if(userimg != null)
				viewHolder5.iv_userhead.setImageBitmap(userimg);
			
			if(fileType2 != null && !fileType2.equals(""))
			{
				if(fileType2.equals("image/png"))
				{
					viewHolder5.tv_time.setVisibility(View.GONE);
				}
				else
				{
					viewHolder5.tv_time.setVisibility(View.VISIBLE);
					viewHolder5.tv_time.setText(timetext);
				}
			}
			else
			{
				viewHolder5.tv_time.setVisibility(View.GONE);
			}
			
			viewHolder5.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			viewHolder5.layout_text.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					MessageListActivity.instance.showWebHtml(link,storename);
				}
			});
			
			viewHolder5.layout_text.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("link");
					return true;
				}
			});
			
			if(tag.equals("0"))
				viewHolder5.send_progressBar.setVisibility(View.VISIBLE);
			else
				viewHolder5.send_progressBar.setVisibility(View.GONE);
			
			if(sendtag.equals("0"))
			{
				viewHolder5.start_img.setVisibility(View.VISIBLE);
				viewHolder5.start_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(position);
					}
				});
			}
			else
				viewHolder5.start_img.setVisibility(View.GONE);
			
			if(fileUrl != null && !fileUrl.equals(""))
			{
				viewHolder5.uploadFileImg.setVisibility(View.VISIBLE);
				viewHolder5.uploadFileImg.setImageBitmap(null);
//				viewHolder5.uploadFileImg.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				setViewImage(viewHolder5.uploadFileImg, fileUrl,position,dataSet);
				
				viewHolder5.layout_text.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("link");
						return true;
					}
				});
				
				viewHolder5.uploadFileImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Map dataSet = mData.get(position);
						String mid = (String)dataSet.get("mid");
						MessageListActivity.instance.showMessageImageDetails(mid);
					}
				});
				
				viewHolder5.uploadFileImg.setTag(position);
				viewHolder5.uploadFileImg.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();
						myapp.setCurrentDelindex(index);
						MessageListActivity.instance.openListViewItemMuneView("link");
						return true;
					}
				});
			}
			else
			{
				viewHolder5.uploadFileImg.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setLeftMessageTapeView(final ViewHolder7 viewHolder7,Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			String fileType2 = (String)dataSet.get("fileType2");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
	        boolean isplay = false;
	        if(dataSet.containsKey("isplay"))
	        	isplay = (Boolean)dataSet.get("isplay");
	        
	        String gifname = mymessagecontent.replaceAll("//", "");
	        int id = contexts.getResources().getIdentifier(contexts.getPackageName()+":drawable/"+gifname,null,null);
	        InputStream is = contexts.getResources().openRawResource(id);
			
//	        viewHolder7.title_txt.setText(storename);
//	        viewHolder7.store_doc.setText(storeDoc);
			viewHolder7.mysendtime.setText(mysendtime);
			viewHolder7.mysendtime.setTextSize(11);
			viewHolder7.message_rlayout.setVisibility(View.VISIBLE);
			if(userimg != null)
				viewHolder7.iv_userhead.setImageBitmap(userimg);
			
			if(fileType2 != null && !fileType2.equals(""))
			{
				if(fileType2.equals("image/png"))
				{
					viewHolder7.tv_time.setVisibility(View.GONE);
				}
				else
				{
					viewHolder7.tv_time.setVisibility(View.VISIBLE);
					viewHolder7.tv_time.setText(timetext);
				}
			}
			else
			{
				viewHolder7.tv_time.setVisibility(View.GONE);
			}
			
			viewHolder7.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			viewHolder7.layout_text.setVisibility(View.GONE);
			viewHolder7.gif1.setVisibility(View.VISIBLE);
			viewHolder7.gif1.setImageResource(id);
			viewHolder7.gif1.start(is,isplay);
			
			viewHolder7.gif1.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("tape");
					return true;
				}
			});
			
			if(tag.equals("0"))
				viewHolder7.send_progressBar.setVisibility(View.VISIBLE);
			else
				viewHolder7.send_progressBar.setVisibility(View.GONE);
			
			if(sendtag.equals("0"))
			{
				viewHolder7.start_img.setVisibility(View.VISIBLE);
				viewHolder7.start_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(position);
					}
				});
			}
			else
				viewHolder7.start_img.setVisibility(View.GONE);
			
			viewHolder7.uploadFileImg.setVisibility(View.GONE);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setRightMessageView(final ViewHolder2 viewHolder2,Map<String,Object> dataSet,final int position)
	{
		try{
			String messagetype = (String)dataSet.get("messagetype");
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
//			CharSequence sps = MessageListActivity.instance.getMessageContent(mymessagecontent);
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			final String fileUrl2 = (String)dataSet.get("fileUrl2");
			String fileType2 = (String)dataSet.get("fileType2");
			final String fileName2 = (String)dataSet.get("fileName2");
			String time = (String)dataSet.get("time");
			final String fusernameid = (String)dataSet.get("mysendname");
	        final String touserid = (String)dataSet.get("toname");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
	        String messagestart = (String)dataSet.get("messagestart");//1为未读，0.为已读
			
	        if(messagestart != null && !messagestart.equals(""))
	        {
	        	viewHolder2.message_start_txt.setVisibility(View.VISIBLE);
	        	if(messagestart.equals("1"))
		        {
	        		viewHolder2.message_start_txt.setText(contexts.getString(R.string.hotel_label_147));
		        }
		        else
		        {
		        	viewHolder2.message_start_txt.setText(contexts.getString(R.string.hotel_label_146));
		        }
	        }
	        else
	        {
	        	viewHolder2.message_start_txt.setVisibility(View.GONE);
	        }
	        
			viewHolder2.mysendtime.setText(mysendtime);
			if(userimg != null)
				viewHolder2.iv_userhead.setImageBitmap(userimg);
//			viewHolder2.layout_text.setOnLongClickListener(new OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View v) {
//					// TODO Auto-generated method stub
//					myapp.setCurrentDelindex(position);
//					MessageListActivity.instance.openListViewItemMuneView("text");
//					return false;
//				}
//			});
			
			if(fileType2 != null && !fileType2.equals(""))
			{
				if(fileType2.equals("image/png"))
				{
					viewHolder2.tv_time.setVisibility(View.GONE);
				}
				else
				{
					viewHolder2.tv_time.setVisibility(View.VISIBLE);
					viewHolder2.tv_time.setText(timetext);
				}
			}
			else
			{
				viewHolder2.tv_time.setVisibility(View.GONE);
			}
			
			if(fileUrl != null && fileUrl2.contains(".amr"))
			{
				viewHolder2.mymessagecontent.setVisibility(View.VISIBLE);
				viewHolder2.mymessagecontent.setText("");
				viewHolder2.mymessagecontent2.setVisibility(View.GONE);
				viewHolder2.upload_img_layout.setVisibility(View.GONE);
				
				viewHolder2.layout_text.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("amr");
						return false;
					}
				});
				
				if(fileType2 != null && !fileType2.equals(""))
				{
					int longwith = Integer.valueOf(time)*30;
					if(longwith > 150)
						viewHolder2.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT));
					else
					{
						if(longwith < 70)
							viewHolder2.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(70, LayoutParams.WRAP_CONTENT));
						else
							viewHolder2.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(longwith, LayoutParams.WRAP_CONTENT));
					}
					
					viewHolder2.mymessagecontent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing_right, 0);
					
					if(fileUrl2.indexOf("http") >= 0)
					{
						downloadArmFile(fileUrl2,fileName2,viewHolder2.mymessagecontent,viewHolder2.aim_layout,1);
						
						viewHolder2.layout_text.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
					            downloadArmData(fileUrl2,fileName2,viewHolder2.mymessagecontent,viewHolder2.aim_layout,2,fusernameid,touserid);
							}
						});
					}
					else
					{
						viewHolder2.layout_text.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								playMusic2(fileUrl2,viewHolder2.mymessagecontent,viewHolder2.aim_layout,2);
							}
						});
					}
				}
			}
			else
			{
				viewHolder2.mymessagecontent2.setVisibility(View.VISIBLE);
//				viewHolder2.mymessagecontent2.setText(mymessagecontent);
				viewHolder2.mymessagecontent2.setText(getMessageContent(mymessagecontent));
				viewHolder2.mymessagecontent2.setMovementMethod(LinkMovementMethod.getInstance());   
		        CharSequence text = viewHolder2.mymessagecontent2.getText();
//				CharSequence text = getMessageContent(mymessagecontent);
		        if(text instanceof Spannable){   
		            int end = text.length();   
		            Spannable sp = (Spannable)viewHolder2.mymessagecontent2.getText();   
		            URLSpan[] urls=sp.getSpans(0, end, URLSpan.class);    
		            SpannableStringBuilder style=new SpannableStringBuilder(text);   
		            style.clearSpans();//should clear old spans   
		            for(URLSpan url : urls){   
		                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
//		                style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		                ((Spannable) text).setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		            }
		            viewHolder2.mymessagecontent2.setText(text);
		        }
				viewHolder2.mymessagecontent.setVisibility(View.GONE);
				viewHolder2.upload_img_layout.setVisibility(View.GONE);
				
				viewHolder2.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				viewHolder2.layout_text.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						
					}
				});
				
				viewHolder2.layout_text.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("text");
						return false;
					}
				});
				
				viewHolder2.mymessagecontent2.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("text");
						return true;
					}
				});
				
				if(messagetype.equals("jf"))
				{
					viewHolder2.mymessagecontent2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							MessageListActivity.instance.openCardView();
						}
					});
				}
				else if(messagetype.equals("cpcx"))
				{
					viewHolder2.mymessagecontent2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							MessageListActivity.instance.showTrafficView();
						}
					});
				}
			}
			
			if(tag.equals("0"))
				viewHolder2.send_progressBar.setVisibility(View.VISIBLE);
			else
				viewHolder2.send_progressBar.setVisibility(View.GONE);
			
			if(sendtag.equals("0"))
			{
				viewHolder2.start_img.setVisibility(View.VISIBLE);
				viewHolder2.start_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(position);
					}
				});
			}
			else
				viewHolder2.start_img.setVisibility(View.GONE);
			
			if(fileUrl != null && !fileUrl.equals(""))
			{
				viewHolder2.mymessagecontent2.setVisibility(View.GONE);
				viewHolder2.uploadFileImg.setVisibility(View.VISIBLE);
				viewHolder2.uploadFileImg.setImageBitmap(null);
//				viewHolder2.uploadFileImg.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				setViewImage(viewHolder2.uploadFileImg, fileUrl,viewHolder2.upload_img_layout,viewHolder2.progressBar,viewHolder2.progress_tv,viewHolder2.mymessagecontent,viewHolder2.progressBar2,position,dataSet);
				viewHolder2.mymessagecontent.setVisibility(View.GONE);
				
				viewHolder2.layout_text.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("img");
						return false;
					}
				});
				
				viewHolder2.uploadFileImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Map dataSet = mData.get(position);
						String mid = (String)dataSet.get("mid");
						MessageListActivity.instance.showMessageImageDetails(mid);
					}
				});
				
				viewHolder2.uploadFileImg.setTag(position);
				viewHolder2.uploadFileImg.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();
						myapp.setCurrentDelindex(index);
						MessageListActivity.instance.openListViewItemMuneView("img");
						return true;
					}
				});
			}
			else
			{
				viewHolder2.uploadFileImg.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setRightMessageLinkView(final ViewHolder6 viewHolder6,Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			String fileType2 = (String)dataSet.get("fileType2");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
	        String messagestart = (String)dataSet.get("messagestart");//1为未读，0.为已读
			
	        JSONObject job = new JSONObject(mymessagecontent);
	        String storeDoc = job.getString("storeDoc");
	        final String storename = job.getString("storename");
	        final String link = job.getString("link");
			
	        viewHolder6.title_txt.setText(storename);
	        viewHolder6.store_doc.setText(storeDoc);
	        
//	        if(messagestart != null && !messagestart.equals(""))
//	        {
//	        	viewHolder6.message_start_txt.setVisibility(View.VISIBLE);
//	        	if(messagestart.equals("1"))
//		        {
//	        		viewHolder6.message_start_txt.setText(contexts.getString(R.string.hotel_label_147));
//		        }
//		        else
//		        {
//		        	viewHolder6.message_start_txt.setText(contexts.getString(R.string.hotel_label_146));
//		        }
//	        }
//	        else
//	        {
	        	viewHolder6.message_start_txt.setVisibility(View.GONE);
//	        }
	        
			viewHolder6.mysendtime.setText(mysendtime);
			if(userimg != null)
				viewHolder6.iv_userhead.setImageBitmap(userimg);
			
			if(fileType2 != null && !fileType2.equals(""))
			{
				if(fileType2.equals("image/png"))
				{
					viewHolder6.tv_time.setVisibility(View.GONE);
				}
				else
				{
					viewHolder6.tv_time.setVisibility(View.VISIBLE);
					viewHolder6.tv_time.setText(timetext);
				}
			}
			else
			{
				viewHolder6.tv_time.setVisibility(View.GONE);
			}
			

			viewHolder6.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			viewHolder6.layout_text.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					MessageListActivity.instance.showWebHtml(link,storename);
				}
			});
			
			viewHolder6.layout_text.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("link");
					return false;
				}
			});
			
			if(tag.equals("0"))
				viewHolder6.send_progressBar.setVisibility(View.VISIBLE);
			else
				viewHolder6.send_progressBar.setVisibility(View.GONE);
			
			if(sendtag.equals("0"))
			{
				viewHolder6.start_img.setVisibility(View.VISIBLE);
				viewHolder6.start_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(position);
					}
				});
			}
			else
				viewHolder6.start_img.setVisibility(View.GONE);
			
			if(fileUrl != null && !fileUrl.equals(""))
			{
				viewHolder6.uploadFileImg.setVisibility(View.VISIBLE);
				viewHolder6.uploadFileImg.setImageBitmap(null);
//				viewHolder6.uploadFileImg.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				setViewImage(viewHolder6.uploadFileImg, fileUrl,position,dataSet);
				
				viewHolder6.layout_text.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						myapp.setCurrentDelindex(position);
						MessageListActivity.instance.openListViewItemMuneView("link");
						return false;
					}
				});
				
				viewHolder6.uploadFileImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Map dataSet = mData.get(position);
						String mid = (String)dataSet.get("mid");
						MessageListActivity.instance.showMessageImageDetails(mid);
					}
				});
				
				viewHolder6.uploadFileImg.setTag(position);
				viewHolder6.uploadFileImg.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();
						myapp.setCurrentDelindex(index);
						MessageListActivity.instance.openListViewItemMuneView("link");
						return true;
					}
				});
			}
			else
			{
				viewHolder6.uploadFileImg.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setRightMessageTapeView(final ViewHolder8 viewHolder8,Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String mymessagecontent = (String)dataSet.get("mymessagecontent");
			Bitmap userimg = (Bitmap)dataSet.get("userimg");
			String timetext = (String)dataSet.get("timetext");
			String fileType2 = (String)dataSet.get("fileType2");
	        String tag = (String)dataSet.get("sendprogress");
	        String fileUrl = (String)dataSet.get("fileUrl");
	        String sendtag = (String)dataSet.get("sendimg");
	        String messagestart = (String)dataSet.get("messagestart");//1为未读，0.为已读
	        boolean isplay = false;
	        if(dataSet.containsKey("isplay"))
	        	isplay = (Boolean)dataSet.get("isplay");
			
	        String gifname = mymessagecontent.replaceAll("//", "");
	        int id = contexts.getResources().getIdentifier(contexts.getPackageName()+":drawable/"+gifname,null,null);
	        InputStream is = contexts.getResources().openRawResource(id);
//	        Drawable d = contexts.getResources().getDrawable(id);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			
	        viewHolder8.message_start_txt.setVisibility(View.GONE);
	        
			viewHolder8.mysendtime.setText(mysendtime);
			if(userimg != null)
				viewHolder8.iv_userhead.setImageBitmap(userimg);
			
			viewHolder8.tv_time.setVisibility(View.GONE);
			

			viewHolder8.aim_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			viewHolder8.layout_text.setVisibility(View.GONE);
			
			viewHolder8.gif1.setVisibility(View.VISIBLE);
			viewHolder8.gif1.setImageResource(id);
			viewHolder8.gif1.start(is,isplay);
			
			viewHolder8.gif1.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("tape");
					return false;
				}
			});
			
			if(tag.equals("0"))
				viewHolder8.send_progressBar.setVisibility(View.VISIBLE);
			else
				viewHolder8.send_progressBar.setVisibility(View.GONE);
			
			if(sendtag.equals("0"))
			{
				viewHolder8.start_img.setVisibility(View.VISIBLE);
				viewHolder8.start_img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MessageListActivity.instance.openAgainSendMuneView(position);
					}
				});
			}
			else
				viewHolder8.start_img.setVisibility(View.GONE);
			
			viewHolder8.uploadFileImg.setVisibility(View.GONE);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setMessageListItemView(final ViewHolder3 viewHolder3,final Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String storeimg = (String)dataSet.get("storeimg");
			String storename = (String)dataSet.get("storename");
			List<Map<String,Object>> dlist = (List<Map<String,Object>>)dataSet.get("storelist");
			
			viewHolder3.mysendtime.setText(mysendtime);
			viewHolder3.content_layout.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("listitem");
					return false;
				}
			});
			viewHolder3.content_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String messagetype = (String)dataSet.get("messagetype");
					String url = (String)dataSet.get("url");
					String storename = (String)dataSet.get("storename");
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
								MessageListActivity.instance.showWebHtml(url,storename);
						}
					}
				}
			});
			
			if(storeimg != null && !storeimg.equals(""))
			{
				if(SCROLL_STATE_TOUCH_SCROLL)
				{
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("view", viewHolder3.store_img);
					map.put("value", storeimg);
					imgmap.put(position, map);
					viewHolder3.store_img.setImageResource(R.color.list_item_background);
				}
				else
				{
					Bitmap bmpimg = null;
					WeakReference<Bitmap> current = dateCache.get(position);
					if(current == null)
					{
						bmpimg = getLoacalBitmap2(storeimg);
						dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
					}
					else
					{
						bmpimg = current.get();
					}
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("view", viewHolder3.store_img);
					map.put("value", storeimg);
					imgmap.put(position, map);
					if(bmpimg != null)
						viewHolder3.store_img.setImageBitmap(bmpimg);
					else
						viewHolder3.store_img.setImageResource(R.color.list_item_background);
				}
			}
			else
			{
				viewHolder3.store_img.setImageResource(R.color.list_item_background);
			}
			
			if(viewHolder3.store_name.getBackground() != null)
				viewHolder3.store_name.getBackground().setAlpha(100);
			viewHolder3.store_name.setText(storename);
			
			List<WeakReference<Bitmap>> currents = dateCacheItems.get(position);
			if(currents == null || currents.size() == 0)
			{
				currents = new ArrayList<WeakReference<Bitmap>>();
			}
			List<Map<String,Object>> viewlist = new ArrayList<Map<String,Object>>();
			
			for(int i=0;i<10;i++)
			{
				LinearLayout rootview = (LinearLayout)viewHolder3.store_item_list.getChildAt(i);
				rootview.setVisibility(View.GONE);
				ImageView imgview = (ImageView)((LinearLayout)rootview.getChildAt(0)).getChildAt(0);
				imgview.setImageBitmap(null);
			}
			if(dlist != null)
			{
				boolean b = false;
				if(SCROLL_STATE_TOUCH_SCROLL)
				{
					b = true;
				}
				else
				{
					b = false;
				}
				for(int i=0;i<dlist.size();i++)
				{
					Map<String,Object> map = dlist.get(i);
					final String sysImg = (String)map.get("sysImg");
					String itemtitle = (String)map.get("title");
					final String price = (String)map.get("price");
					final String desc = (String)map.get("desc");
					final String pkid = (String)map.get("pkid");
					
					LinearLayout rootview = (LinearLayout)viewHolder3.store_item_list.getChildAt(i);
					rootview.setVisibility(View.VISIBLE);
					
					if(b)
					{
						currents = null;
						ImageView imgview = (ImageView)((LinearLayout)rootview.getChildAt(0)).getChildAt(0);
//						imgview.setImageBitmap(null);
						imgview.setImageResource(R.color.list_item_background);
						
						Map<String,Object> viewmap = new HashMap<String,Object>();
						viewmap.put("view", imgview);
						viewmap.put("value", sysImg);
						viewlist.add(viewmap);
					}
					else
					{
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
						}
						
						if(bmpimg != null)
						{
							currents.add(new WeakReference<Bitmap>(bmpimg));
							
							ImageView imgview = (ImageView)((LinearLayout)rootview.getChildAt(0)).getChildAt(0);
							imgview.setImageBitmap(null);
							imgview.setImageBitmap(bmpimg);
							
							Map<String,Object> viewmap = new HashMap<String,Object>();
							viewmap.put("view", imgview);
							viewmap.put("value", sysImg);
							viewlist.add(viewmap);
						}
					}
					
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
							String messagetype = (String)dataSet.get("messagetype");
							String storename = (String)dataSet.get("storename");
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
									MessageListActivity.instance.showStoreinfo(pkid,storename,desc,sysImg);
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
				viewHolder3.store_item_list.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setMessageOneItemView(final ViewHolder4 viewHolder4,final Map<String,Object> dataSet,final int position)
	{
		try{
			String mysendtime = (String)dataSet.get("mysendtime");
			String storeimg = (String)dataSet.get("storeimg");
			String storename = (String)dataSet.get("storename");
			String storeDoc = (String)dataSet.get("storeDoc");
			
			if(viewHolder4.store_name.getBackground() != null)
				viewHolder4.store_name.getBackground().setAlpha(100);
			viewHolder4.store_name.setText(storename);
			
			viewHolder4.mysendtime.setText(mysendtime);
			viewHolder4.content_layout.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					myapp.setCurrentDelindex(position);
					MessageListActivity.instance.openListViewItemMuneView("imgtext");
					return false;
				}
			});
			viewHolder4.content_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String messagetype = (String)dataSet.get("messagetype");
					String url = (String)dataSet.get("url");
					String storename = (String)dataSet.get("storename");
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
								myapp.setCurrentDelindex(position);
								MessageListActivity.instance.showWebHtml(url,storename);
						}
					}
				}
			});
			
			if(storeimg != null && !storeimg.equals(""))
			{
				if(SCROLL_STATE_TOUCH_SCROLL)
				{
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("view", viewHolder4.store_img);
					map.put("value", storeimg);
					imgmap.put(position, map);
					viewHolder4.store_img.setImageResource(R.color.list_item_background);
				}
				else
				{
					Bitmap bmpimg = null;
					WeakReference<Bitmap> current = dateCache.get(position);
					if(current == null)
					{
						bmpimg = getLoacalBitmap2(storeimg);
						dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
					}
					else
					{
						bmpimg = current.get();
					}
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("view", viewHolder4.store_img);
					map.put("value", storeimg);
					imgmap.put(position, map);
					if(bmpimg != null)
						viewHolder4.store_img.setImageBitmap(bmpimg);
					else
						viewHolder4.store_img.setImageResource(R.color.list_item_background);
				}
			}
			else
			{
				viewHolder4.store_img.setImageResource(R.color.list_item_background);
			}
			
			viewHolder4.store_doc_txt.setText(Html.fromHtml(storeDoc));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public TextView getPtext()
	{
		return ptext;
	}
	
	public TextView getPtext(int viewtype,View view)
	{
//		View rowView = this.viewMap.get(rowindex);
//		TextView progress_tv = (TextView)rowView.findViewById(R.id.progress_tv);
//		ptext = progress_tv;
//		return ptext;
		LinearLayout imglayout = null;
		try{
			switch(viewtype)
			{
			case TYPE_1:
				ViewHolder1 viewHolder1 = (ViewHolder1)view.getTag();
				ptext = viewHolder1.progress_tv;
				break;
			case TYPE_2:
				ViewHolder2 viewHolder2 = (ViewHolder2)view.getTag();
				ptext = viewHolder2.progress_tv;
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ptext;
	}
	
	public LinearLayout getPlayout()
	{
		return playout;
	}
	
	public LinearLayout getPlayout(int viewtype,View view)
	{
//		View rowView = this.viewMap.get(rowindex);
//		LinearLayout imglayout = (LinearLayout)rowView.findViewById(R.id.upload_img_layout);
//		playout = imglayout;
//		return playout;
		try{
			switch(viewtype)
			{
			case TYPE_1:
				ViewHolder1 viewHolder1 = (ViewHolder1)view.getTag();
				playout = viewHolder1.upload_img_layout;
				break;
			case TYPE_2:
				ViewHolder2 viewHolder2 = (ViewHolder2)view.getTag();
				playout = viewHolder2.upload_img_layout;
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return playout;
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
	
	public Bitmap getLoacalBitmap2(String url,int with,int high) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			bitmap = Bitmap.createScaledBitmap(bitmap,with,high,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
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
//			onplayer.start();
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
			makeText("播放失败");
			File file = new File(android.os.Environment.getExternalStorageDirectory()+"/voa/"+name);
			file.delete();
			makeText("已经删除该文件");
			e.printStackTrace();
			
		}
	}
	
	private void playMusic2(String filePath,final TextView textview,LinearLayout amilay,final int tofrom) {
		try {
//			onplayer.start();
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
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_from_icon_anim));
			else
				textview.setBackgroundDrawable(contexts.getResources().getDrawable(R.anim.voice_to_icon_anim));
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
			makeText("播放失败");
			File file = new File(filePath);
			file.delete();
			makeText("已经删除该文件");
			e.printStackTrace();
			
		}
	}
	
	public void setViewImage(final ImageView v, String url,int rowindex,Map<String,Object> data) {
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
						imageLoader2.loadDrawable(url, new AsyncImageLoader2.ImageCallback() {
							public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
								if (imageDrawable != null) {
									
									v.setImageBitmap(imageDrawable);
								}
							}
						},60,60);
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
							
							url = url.replace("/1b/", "/1a/");
							bitmap = getLoacalBitmap2(url);
							bitmap = Bitmap.createScaledBitmap(bitmap,60,60,true);
							if(!SCROLL_STATE_TOUCH_SCROLL)
								dateCache.put(rowindex, new WeakReference<Bitmap>(bitmap));
							
							System.out.println("filepath==="+url);
							int with = bitmap.getWidth();
							int high = bitmap.getHeight();
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", ((ImageView) v));
							map.put("value", url);
							map.put("with", with);
							map.put("high", high);
							map.put("linkimg", "");
//							map.put("imglayout", imglayout);
							imgmap.put(rowindex, map);
							if(SCROLL_STATE_TOUCH_SCROLL)
							{
//								LayoutParams lp = ((ImageView) v).getLayoutParams();
//						        lp.width=with;
//						        lp.height=high;        
//						        ((ImageView) v).setLayoutParams(lp);
								((ImageView) v).setImageResource(R.color.list_item_background);
							}
							else
							{
								((ImageView) v).setImageBitmap(bitmap);
							}
							
							v.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									showMessageDetails(imgurl);
								}
							});
						}
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
	
	public void setViewImage(final ImageView v, String url,LinearLayout imglayout,ProgressBar pb,TextView textview,TextView messagetext,final ProgressBar progressBar2,int rowindex,Map<String,Object> data) {
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
						progressBar2.setVisibility(View.VISIBLE);
						imageLoader2.loadDrawable(url, new AsyncImageLoader2.ImageCallback() {
							public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
								if (imageDrawable != null) {
									
									v.setImageBitmap(imageDrawable);
									progressBar2.setVisibility(View.GONE);
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
//							WeakReference<Bitmap> current = dateCache.get(rowindex);
//							if(current == null)
//							{
								url = url.replace("/1b/", "/1a/");
								bitmap = getLoacalBitmap2(url);
								if(!SCROLL_STATE_TOUCH_SCROLL)
									dateCache.put(rowindex, new WeakReference<Bitmap>(bitmap));
//							}
//							else
//							{
//								bitmap = current.get();
//							}
							ptext = textview;
							playout = imglayout;
							if(sendprogress.equals("0"))
								imglayout.setVisibility(View.VISIBLE);
//							url = url.replace("/1b/", "/1a/");
//							Bitmap bitmap = getLoacalBitmap2(url);
							
							System.out.println("filepath==="+url);
							int with = bitmap.getWidth();
							int high = bitmap.getHeight();
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", ((ImageView) v));
							map.put("value", url);
							map.put("with", with);
							map.put("high", high);
							map.put("imglayout", imglayout);
							imgmap.put(rowindex, map);
							if(SCROLL_STATE_TOUCH_SCROLL)
							{
								LayoutParams lp = ((ImageView) v).getLayoutParams();
						        lp.width=with;
						        lp.height=high;        
						        ((ImageView) v).setLayoutParams(lp);
								((ImageView) v).setImageResource(R.color.list_item_background);
							}
							else
							{
								((ImageView) v).setImageBitmap(bitmap);
							}
							
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
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
	
	private static class MyURLSpan extends ClickableSpan{   
        
        private String mUrl;   
        MyURLSpan(String url) {   
            mUrl =url;   
        }   
        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
            MessageListActivity.instance.showWebHtml(mUrl, "");
        }   
    }
	
	public CharSequence getMessageContent(String content)
	{
		CharSequence sp = null;
		try{
			sp = Html.fromHtml(content, imageGetter, null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return sp;
	}
	
	ImageGetter imageGetter = new ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            int id = contexts.getResources().getIdentifier(contexts.getPackageName()+":drawable/"+source,null,null);
            // 根据id从资源文件中获取图片对象
            Drawable d = contexts.getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
}
