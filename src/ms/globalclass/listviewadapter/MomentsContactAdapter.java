package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.hotel.MomentsActivity;
import ms.activitys.hotel.PinLunDialog;
import ms.globalclass.AndroidFileOpenUtil;
import ms.globalclass.CollapsibleTextView;
import ms.globalclass.FileUtils;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

public class MomentsContactAdapter extends BaseAdapter{

	private Context mContext;
//	private String[] mNicks;
//	private String[] mImage;
	private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
	private AsyncImageLoaderMoments imageLoader = new AsyncImageLoaderMoments();
	private ViewBinder mViewBinder;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private String[] mFrom;
	private int[] mTo;
	private int mResource;
	private int mResource2;
	private LayoutInflater mInflater;
	private MyApp myapp;
	private Map<Integer,List<WeakReference<Bitmap>>> dateCache = new HashMap<Integer,List<WeakReference<Bitmap>>>();//图片资源缓存
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	final int TYPE_3 = 2;
	private ImageView bgimg;
	private LinearLayout bglayut;
	private DBHelperMessage db;
	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private Map<String, SoftReference<Bitmap>> userImageCache = new HashMap<String, SoftReference<Bitmap>>();
	private Map<String, Bitmap> imageCache2 = new HashMap<String, Bitmap>();
	private boolean isone = true;
	private Animation animationin;
	private Animation animationout;
	private Animation animationroe;
	public LinearLayout currLayout;
	private Map<Integer,List<Map<String,Object>>> imgmap = new HashMap<Integer,List<Map<String,Object>>>();//图片控件缓存
	private boolean SCROLL_STATE_TOUCH_SCROLL = false;//是否正在滚动
	public List<Map<String,String>> filelistImg = new ArrayList<Map<String,String>>();
	public static FileUtils fileUtil = new FileUtils();
	
	public boolean isSCROLL_STATE_TOUCH_SCROLL() {
		return SCROLL_STATE_TOUCH_SCROLL;
	}
	public void setSCROLL_STATE_TOUCH_SCROLL(boolean sCROLL_STATE_TOUCH_SCROLL) {
		SCROLL_STATE_TOUCH_SCROLL = sCROLL_STATE_TOUCH_SCROLL;
	}
	
	@SuppressWarnings("unchecked")
	public MomentsContactAdapter(Context context,List<Map<String,Object>> data,int resource,int resource2,String[] from, int[] to,MyApp myapp){
//		super(context, data, resource, from, to);
		this.mContext = context;
//		this.mNicks = nicks;
//		this.mImage = userimgs;
//		this.mDlist = data;
		this.mDlist.addAll(data);
		mResource = resource;
		mResource2 = resource2;
		mFrom = from;
		mTo = to;
		this.myapp = myapp;
		//排序(实现了中英文混排)
//		List datalist = mDlist;
//		datalist.remove(0);
//		Collections.sort(mDlist, new PinyinComparator());
//		mDlist.add(1,new HashMap<String,Object>());
		
		mDlist.add(0,new HashMap<String,Object>());
		
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	public MomentsContactAdapter(Context context,List<Map<String,Object>> data,int resource,int resource2,MyApp myapp,DBHelperMessage db){
		this.mContext = context;
		this.mDlist.addAll(data);
		mResource = resource;
		mResource2 = resource2;
		this.myapp = myapp;
		this.db = db;
		animationin = AnimationUtils.loadAnimation(mContext, R.anim.sns_faded_in);
		animationout = AnimationUtils.loadAnimation(mContext, R.anim.sns_faded_out);
		animationroe = AnimationUtils.loadAnimation(mContext, R.anim.bottle_rotate);
		
//		mDlist.add(0,new HashMap<String,Object>());
		
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mDlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mDlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<Map<String,String>> getFileList(int position)
	{
		Map<String,Object> dataSet = mDlist.get(position);
		List<Map<String,String>> filelist = (List<Map<String,String>>)dataSet.get("filelist");
		return filelist;
	}
	
	public String getItemContent(int position)
	{
		Map<String,Object> dataSet = mDlist.get(position);
		String content = (String)dataSet.get("content");
		return content;
	}
	
	public int getCommentZhanSize(int position)
	{
		int zhansize = 0;
		try{
			Map dataSet = mDlist.get(position);
			List<Map<String,String>> commentList = (List<Map<String,String>>)dataSet.get("commentlist");
			if(commentList == null)
				commentList = new ArrayList<Map<String,String>>();
			
			//封装评论
			if(commentList.size()>0){
				for(int n=0; n<commentList.size(); n++){
					Map<String, String> map = commentList.get(n);
					String userName = map.get("userName");
					String commentText = map.get("discusDesc");
			        if(commentText.equals("/微笑"))
			        {
			        	zhansize++;
			        }
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return zhansize;
	}
	
	public String getCurPublishID(int position)
	{
		Map dataSet = mDlist.get(position);
		String pkid = (String)dataSet.get("pkid");
		return pkid;
	}
	
	public Bitmap getImageCache(String url)
	{
		Bitmap bitmap = null;
		if (imageCache2.containsKey(url)) {       
			bitmap = imageCache2.get(url);  
        }else{  
            bitmap = getLoacalBitmap(url,false);
            bitmap = myapp.adaptive(bitmap,myapp.getScreenWidth()/2,myapp.getScreenWidth()/2);
            imageCache2.put(url, bitmap);
         }  
		return bitmap;
	}
	
	public void getRefresh(List<Map<String,Object>> list){  
		mDlist = list;
		isone = false;
		this.notifyDataSetChanged();
    } 
	
	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		return false;
	}
	
	public void removeAllResources()
	{
		try{
		    for(Map.Entry<Integer, List<WeakReference<Bitmap>>> entry : dateCache.entrySet())   
		    {   
		        List<WeakReference<Bitmap>> wrfs = entry.getValue();
		        for(int i=0;i<wrfs.size();i++)
		        {
		        	WeakReference<Bitmap> wrf = wrfs.get(i);
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
		    }
		    dateCache = null;
		    
		    System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void removeImageView(int start,int end)
	{
		try{
			int start2 = start;
			int end2 = end;
			start = start - 1;
			end = end + 1;
			List<WeakReference<Bitmap>> delBitmaps;
			List<WeakReference<Bitmap>> delBitmaps2;
			for (int del = 0; del < start; del++) {
				delBitmaps = dateCache.get(del);
				if (delBitmaps != null && delBitmaps.size() > 0) {
					for(int k=0;k<delBitmaps.size();k++)
					{
						WeakReference<Bitmap> delBitmap = delBitmaps.get(k);
					// 如果非空则表示有缓存的bitmap，需要清理
					// 从缓存中移除该del->bitmap的映射
						if(delBitmap != null)
						{
							if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {  
								delBitmap.get().recycle();
							}
						}
					}
					dateCache.remove(del);
				}
			}
			
			for (int del = end + 1; del < getCount(); del++) {
				delBitmaps2 = dateCache.get(del);
				if (delBitmaps2 != null && delBitmaps2.size() > 0) {
					for(int k=0;k<delBitmaps2.size();k++)
					{
						WeakReference<Bitmap> delBitmap2 = delBitmaps2.get(k);
						if(delBitmap2 != null)
						{
							// 如果非空则表示有缓存的bitmap，需要清理
							// 从缓存中移除该del->bitmap的映射
							if (delBitmap2.get() != null && !delBitmap2.get().isRecycled()) {  
								delBitmap2.get().recycle();
							}
						}
					}
					dateCache.remove(del);
				}
			}
			
			loadVisibleItemImageData(start2-1,end2+1);
			
			System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	      
	private void loadVisibleItemImageData(int sart,int end)
	{
		try{
			List<WeakReference<Bitmap>> bitmaplist = new ArrayList<WeakReference<Bitmap>>();
			for(int i = sart;i<end-1;i++)
			{
				WeakReference<Bitmap> bitmap = null;
				List<Map<String,Object>> mapslist = imgmap.get(i);
				if(mapslist != null && mapslist.size() > 0)
				{
					if(mapslist.size() == 1)
					{
						for(int j=0;j<mapslist.size();j++)
						{
							Map<String,Object> maps = mapslist.get(j);
							if(maps != null)
							{
								ImageView imgview = (ImageView)maps.get("view");
								String data = (String)maps.get("value");
								bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(data));
								bitmaplist.add(bitmap);
								imgview.setImageBitmap(bitmap.get());
		//						System.out.println("sart==重新赋值");
							}
						}
					}
					else
					{
						for(int j=0;j<mapslist.size();j++)
						{
							Map<String,Object> maps = mapslist.get(j);
							if(maps != null)
							{
								ImageView imgview = (ImageView)maps.get("view");
								String data = (String)maps.get("value");
								bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(data,100,100,true));
								bitmaplist.add(bitmap);
								imgview.setImageBitmap(bitmap.get());
		//						System.out.println("sart==重新赋值");
							}
						}
					}
					dateCache.put(i, bitmaplist);
					imgmap.put(i, mapslist);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public int getItemViewType(int position){
		if(position == 0)
			return TYPE_1;
		else
			return TYPE_2;
	}
	
	@Override
	public int getViewTypeCount() {
		//因为有三种视图，所以返回3
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		return createViewFromResource(position, convertView, parent, mResource,mResource1,mResource2);
		
		ViewHolder1 viewHolder1 = null;
		ViewHolder2 viewHolder2 = null;
		
		int type = getItemViewType(position);
		
		//无convertView，需要new出各个控件
		if(convertView == null){
			//按当前所需的样式，确定new的布局
			switch(type)
			{
			case TYPE_1:
				convertView = LayoutInflater.from(mContext).inflate(mResource, null);
				viewHolder1 = new ViewHolder1();
				viewHolder1.setting_bg = (LinearLayout)convertView.findViewById(R.id.setting_bg);
				viewHolder1.sns_empty_view = (TextView)convertView.findViewById(R.id.sns_empty_view);
				viewHolder1.avatar_iv = (ImageView)convertView.findViewById(R.id.avatar_iv);
				viewHolder1.sign_tv = (TextView)convertView.findViewById(R.id.sign_tv);
				viewHolder1.nickname_tv = (TextView)convertView.findViewById(R.id.nickname_tv);
				viewHolder1.sns_back_ll = (ImageView)convertView.findViewById(R.id.sns_back_ll);
				viewHolder1.load_layout = (LinearLayout)convertView.findViewById(R.id.load_layout);
				convertView.setTag(viewHolder1);
				break;
			case TYPE_2:
				convertView = LayoutInflater.from(mContext).inflate(mResource2, null);
				viewHolder2 = new ViewHolder2();
				viewHolder2.sns_msg_avatar_iv = (ImageView)convertView.findViewById(R.id.sns_msg_avatar_iv);
				viewHolder2.sns_msg_nick_tv = (TextView)convertView.findViewById(R.id.sns_msg_nick_tv);
				viewHolder2.sns_msg_content_tv = (CollapsibleTextView)convertView.findViewById(R.id.sns_msg_content_tv);
//				viewHolder2.sns_msg_more_tv = (CollapsibleTextView)convertView.findViewById(R.id.sns_msg_more_tv);
				viewHolder2.sns_msg_time_tv = (TextView)convertView.findViewById(R.id.sns_msg_time_tv);
				viewHolder2.sns_msg_position_ll = (LinearLayout) convertView.findViewById(R.id.sns_msg_position_ll);
				viewHolder2.sns_msg_position_tv = (TextView) convertView.findViewById(R.id.sns_msg_position_tv);
//				viewHolder2.album_show_comment_tv = (ImageButton)convertView.findViewById(R.id.album_show_comment_tv);
				viewHolder2.image_layout_1 = (LinearLayout)convertView.findViewById(R.id.image_layout_1);
				viewHolder2.image_layout_2 = (LinearLayout)convertView.findViewById(R.id.image_layout_2);
				viewHolder2.image_layout_3 = (LinearLayout)convertView.findViewById(R.id.image_layout_3);
				viewHolder2.attachment_layout = (LinearLayout) convertView.findViewById(R.id.attachment_layout);
				viewHolder2.attachment_tag_imageview = (ImageView) convertView.findViewById(R.id.attachment_tag);
				viewHolder2.attachment_name_textview = (TextView) convertView.findViewById(R.id.attachment_name);
				viewHolder2.attachment_url_textview = (TextView) convertView.findViewById(R.id.attachment_url);
				viewHolder2.attachment_video_ll = (RelativeLayout) convertView.findViewById(R.id.attachment_video_layout);
				viewHolder2.voidimg = (ImageView) convertView.findViewById(R.id.imageView2);
//				viewHolder2.album_comment_container = (LinearLayout)convertView.findViewById(R.id.album_comment_container);
				viewHolder2.album_comment_like_ll = (LinearLayout) convertView.findViewById(R.id.album_like_img);
				viewHolder2.album_comment_text_ll = (LinearLayout) convertView.findViewById(R.id.album_comment_li);
				viewHolder2.album_comment_publishID = (TextView) convertView.findViewById(R.id.album_comment_publishID);
				viewHolder2.album_comment_listView = (LinearLayout) convertView.findViewById(R.id.comment_listView);
				viewHolder2.layout_bg = (LinearLayout)convertView.findViewById(R.id.layout_bg);
				viewHolder2.album_comment_tv = (TextView)convertView.findViewById(R.id.album_comment_tv);
				viewHolder2.album_like_tv = (TextView)convertView.findViewById(R.id.album_like_tv);
				viewHolder2.comment_count = (TextView)convertView.findViewById(R.id.comment_count);
				viewHolder2.comment_icon = (ImageView)convertView.findViewById(R.id.comment_icon);
				viewHolder2.like_count = (TextView)convertView.findViewById(R.id.like_count);
				viewHolder2.like_icon = (ImageView)convertView.findViewById(R.id.like_icon);
				viewHolder2.img_layout_one = (LinearLayout)convertView.findViewById(R.id.img_layout_one);
				viewHolder2.image_layout_one = (LinearLayout)convertView.findViewById(R.id.image_layout_one);
				viewHolder2.img_layout = (LinearLayout)convertView.findViewById(R.id.img_layout);
				convertView.setTag(viewHolder2);
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
			}
		}
		
		//设置资源
		switch(type)
		{
		case TYPE_1:
			lodaView(viewHolder1,position);
			break;
		case TYPE_2:
			loadMomentsItem(viewHolder2,position);
			break;
		}
		
		return convertView;
	}
	
	public void lodaView(ViewHolder1 viewHolder1,int position)
	{
		try{
			Map dataSet = mDlist.get(position);
			String momentsbgurl = (String)dataSet.get("momentsbgurl");
			Bitmap userbitmap = (Bitmap)dataSet.get("userbitmap");
			String username = (String)dataSet.get("username");
			
			if(isone)
				viewHolder1.load_layout.setVisibility(View.VISIBLE);
			else
			{
				if(mDlist.size()<2)
				{
					viewHolder1.sns_empty_view.setVisibility(View.VISIBLE);
					viewHolder1.load_layout.setVisibility(View.GONE);
				}
				else
				{
					viewHolder1.sns_empty_view.setVisibility(View.GONE);
					viewHolder1.load_layout.setVisibility(View.GONE);
				}
			}
			
			bgimg = viewHolder1.sns_back_ll;
			bglayut = viewHolder1.setting_bg;
			if(momentsbgurl != null && !momentsbgurl.equals(""))
			{
				setViewImage(viewHolder1.sns_back_ll,momentsbgurl);
				viewHolder1.setting_bg.setVisibility(View.GONE);
			}
			else
			{
				MomentsActivity.instance.sns_refresh_iv.startAnimation(animationout);
				MomentsActivity.instance.sns_refresh_iv.setVisibility(View.GONE);
				viewHolder1.setting_bg.setVisibility(View.VISIBLE);
			}
			viewHolder1.sns_back_ll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(MomentsActivity.instance.type==null || MomentsActivity.instance.type.equals("")){
						MomentsActivity.instance.openUpdateBg();
					}else{
						System.out.println("不是朋友圈，不可改变背景");
					}
				}
			});
			
			if(userbitmap != null)
				viewHolder1.avatar_iv.setImageBitmap(userbitmap);
			
			viewHolder1.nickname_tv.setText(username);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadMomentsItem(final ViewHolder2 viewHolder2,final int position)
	{
		try{
			Map dataSet = mDlist.get(position);
			String pkid = (String)dataSet.get("pkid");
			String content = (String)dataSet.get("content");
			String publishtime = (String)dataSet.get("publishtime");
			String publishuser = (String)dataSet.get("publishuser");
			String publishid = (String)dataSet.get("publishid");
			String userimg = (String)dataSet.get("userimg");
			String publishPosition = (String)dataSet.get("position");
			List<Map<String,String>> filelistImg = (List<Map<String,String>>)dataSet.get("filelist");
			if(filelistImg == null)
				filelistImg = new ArrayList<Map<String,String>>();
			List<Map<String,String>> filelistAttachment = (List<Map<String,String>>)dataSet.get("filelist2");
			if(filelistAttachment == null)
				filelistAttachment = new ArrayList<Map<String,String>>();
			List<Map<String,String>> commentList = (List<Map<String,String>>)dataSet.get("commentlist");
			if(commentList == null)
				commentList = new ArrayList<Map<String,String>>();
			
//			if(position == 1)
//			{
//				viewHolder2.layout_bg.setBackgroundResource(R.drawable.mm_chat_listitem_normal2);
//			}
//			else
//			{
//				viewHolder2.layout_bg.setBackgroundResource(R.drawable.mm_listitem_conv);
//			}
			
			if(publishid.equals(myapp.getPfprofileId()))
			{
				Bitmap bitmap = myapp.getUserimgbitmap();
				viewHolder2.sns_msg_avatar_iv.setImageBitmap(bitmap);
			}
			else
			{
				String furl = fileUtil.getImageFile2aPath(publishid, publishid);
//				if(userimg != null && !userimg.equals(""))
				if(fileUtil.isFileExist3(furl))
				{
					if(userImageCache.containsKey(furl))
					{
						SoftReference<Bitmap> srf = userImageCache.get(furl);
						if(srf.get() != null)
							viewHolder2.sns_msg_avatar_iv.setImageBitmap(srf.get());
						else
						{
							Bitmap bitmap = getLoacalBitmap(furl,false);
							viewHolder2.sns_msg_avatar_iv.setImageBitmap(bitmap);
							SoftReference<Bitmap> srf2 = new SoftReference<Bitmap>(bitmap);
							userImageCache.put(furl, srf2);
						}
					}
					else
					{
						Bitmap bitmap = getLoacalBitmap(furl,false);
						viewHolder2.sns_msg_avatar_iv.setImageBitmap(bitmap);
						SoftReference<Bitmap> srf = new SoftReference<Bitmap>(bitmap);
						userImageCache.put(furl, srf);
					}
				}
				else
				{
					String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+publishid;
//	    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
//	    			if(bmpsimg != null)
//	    			{
//						myapp.saveMyBitmap(furl, bmpsimg);
//						viewHolder2.sns_msg_avatar_iv.setImageBitmap(bmpsimg);
//	    			}
//	    			else
//	    			{
//	    				viewHolder2.sns_msg_avatar_iv.setImageResource(R.drawable.mini_avatar_shadow);
//	    			}
//					mLoader.DisplayImage(url, viewHolder2.sns_msg_avatar_iv);
					setMomentsUserImage(viewHolder2.sns_msg_avatar_iv,url,furl,publishid);
				}
			}
			viewHolder2.sns_msg_nick_tv.setText(publishuser);
			if(content != null && !content.equals(""))
			{
				viewHolder2.sns_msg_content_tv.setVisibility(View.VISIBLE);
				viewHolder2.sns_msg_content_tv.setDesc("",BufferType.NORMAL);
				viewHolder2.sns_msg_content_tv.setDesc(content,BufferType.NORMAL);
			}
			else
			{
				viewHolder2.sns_msg_content_tv.setVisibility(View.GONE);
			}
			viewHolder2.sns_msg_time_tv.setText(publishtime);
			if(publishPosition!=null && !publishPosition.equals("")){
				viewHolder2.sns_msg_position_ll.setVisibility(View.VISIBLE);
				viewHolder2.sns_msg_position_tv.setText(publishPosition);
			}else{
				viewHolder2.sns_msg_position_ll.setVisibility(View.GONE);
			}
			
			int size = filelistImg.size();
			//分类图片和附件
			List<Map<String,Object>> maplist = new ArrayList<Map<String,Object>>();
			List<WeakReference<Bitmap>> bitmaplist = new ArrayList<WeakReference<Bitmap>>();
			//放图片
			if(filelistImg.size() == 1)
			{
				viewHolder2.img_layout_one.setVisibility(View.VISIBLE);
				viewHolder2.img_layout.setVisibility(View.GONE);
				for(int i=0;i<filelistImg.size();i++)
				{
					final int index = i;
					Map<String,String> filemap = filelistImg.get(i);
					String fileurl = filemap.get("fileurl");
					String filewidth = filemap.get("filewidth");
					String fileheight = filemap.get("fileheight");
					if(filewidth==null)
						filewidth = "100";
					if(fileheight==null)
						fileheight = "100";
					if(fileurl.contains("http://"))
					{
						if(i == 0)
							viewHolder2.image_layout_1.setVisibility(View.VISIBLE);
						ImageView imgview = (ImageView)viewHolder2.image_layout_one.getChildAt(i);
//						imgview.setScaleType(ImageView.ScaleType.FIT_XY);
//					    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 350);  
//					    lp.setMargins(26, 0, 26, 0);  
//					    imgview.setLayoutParams(lp);  
						
						
				        setViewImage2(imgview,fileurl,position,i,publishid);
						
						imgview.setVisibility(View.VISIBLE);
						imgview.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								MomentsActivity.instance.getImageBitmaplist(position,index);
							}
						});
					}
					else
					{
						String url = fileurl.replace("/1b/", "/1a/");
						if(i == 0)
							viewHolder2.image_layout_one.setVisibility(View.VISIBLE);
						ImageView imgview = (ImageView)viewHolder2.image_layout_one.getChildAt(i);
//						imgview.setScaleType(ImageView.ScaleType.FIT_XY);
//					    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 350);  
//					    lp.setMargins(26, 0, 26, 0);  
//					    imgview.setLayoutParams(lp);  
					    
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("view", imgview);
						map.put("value", url);
						maplist.add(map);
////						setMomentsImage2(imgview,fileurl);
						android.view.ViewGroup.LayoutParams para = imgview.getLayoutParams();
//				        para.height = Integer.valueOf(fileheight);
//				        para.width = Integer.valueOf(filewidth);
						para.height = Integer.valueOf(fileheight);
				        para.width = myapp.getScreenWidth()-150;
				        imgview.setLayoutParams(para);
				        if(SCROLL_STATE_TOUCH_SCROLL)
				        	imgview.setImageResource(R.color.list_item_background);
				        else
				        {
				        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(url));
				        	bitmaplist.add(bitmap);
				        	imgview.setImageBitmap(bitmap.get());
				        }
						
						imgview.setVisibility(View.VISIBLE);
						imgview.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								MomentsActivity.instance.getImageBitmaplist(position,index);
							}
						});
					}
				}
			}
			else
			{
				viewHolder2.img_layout_one.setVisibility(View.GONE);
				viewHolder2.img_layout.setVisibility(View.VISIBLE);
				for(int i=0;i<filelistImg.size();i++)
				{
					Map<String,String> filemap = filelistImg.get(i);
					String fileurl = filemap.get("fileurl");
					String url = fileurl.replace("/1b/", "/1a/");
					final int index = i;
					if(i<3)
					{
						if(fileurl.contains("http://"))
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_1.getChildAt(i);
							if(i == 0)
							{
								viewHolder2.image_layout_1.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);  
//							    lp.setMargins(26, 0, 26, 0);  
//							    imgview.setLayoutParams(lp);  
							}
							
					        setViewImage2(imgview,fileurl,position,i,publishid);
							
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
						else
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_1.getChildAt(i);
							if(i == 0)
							{
								viewHolder2.image_layout_1.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);  
//							    lp.setMargins(26, 0, 26, 0);  
//							    imgview.setLayoutParams(lp);
							}
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", imgview);
							map.put("value", url);
							maplist.add(map);
	//						setMomentsImage(imgview,fileurl);
							android.view.ViewGroup.LayoutParams para = imgview.getLayoutParams();
//					        para.height = 100;  
//					        para.width = 100;  
//							if(imgview.getWidth() > 0 )
//							{
//								para.height = imgview.getWidth();  
//						        para.width = imgview.getWidth();  
//						        imgview.setLayoutParams(para);  
//							}
					        if(SCROLL_STATE_TOUCH_SCROLL)
					        	imgview.setImageResource(R.color.list_item_background);
					        else
					        {
					        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(url,100,100,true));
					        	bitmaplist.add(bitmap);
					        	imgview.setImageBitmap(bitmap.get());
					        }
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
					}
					else if(i<6)
					{
						if(fileurl.contains("http://"))
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_2.getChildAt(i-3);
							if(i == 3)
							{
								viewHolder2.image_layout_2.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);  
//							    lp.setMargins(26, 0, 0, 0);  
//							    imgview.setLayoutParams(lp);
							}
							
							setViewImage2(imgview,fileurl,position,i,publishid);
							
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
						else
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_2.getChildAt(i-3);
							if(i == 3)
							{
								viewHolder2.image_layout_2.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);  
//							    lp.setMargins(26, 0, 0, 0);  
//							    imgview.setLayoutParams(lp);
							}
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", imgview);
							map.put("value", url);
							maplist.add(map);
	//						setMomentsImage(imgview,fileurl);
							android.view.ViewGroup.LayoutParams para = imgview.getLayoutParams();
//					        para.height = 100;  
//					        para.width = 100;  
//							if(imgview.getWidth() > 0 )
//							{
//								para.height = imgview.getWidth();  
//						        para.width = imgview.getWidth();  
//						        imgview.setLayoutParams(para); 
//							}
					        if(SCROLL_STATE_TOUCH_SCROLL)
					        	imgview.setImageResource(R.color.list_item_background);
					        else
					        {
					        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(url,100,100,true));
					        	bitmaplist.add(bitmap);
					        	imgview.setImageBitmap(bitmap.get());
					        }
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
					}
					else
					{
						if(fileurl.contains("http://"))
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_3.getChildAt(i-6);
							if(i == 6)
							{
								viewHolder2.image_layout_3.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);  
//							    lp.setMargins(26, 0, 0, 0);  
//							    imgview.setLayoutParams(lp);
							}
							
							setViewImage2(imgview,fileurl,position,i,publishid);
							
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
						else
						{
							ImageView imgview = (ImageView)viewHolder2.image_layout_3.getChildAt(i-6);
							if(i == 6)
							{
								viewHolder2.image_layout_3.setVisibility(View.VISIBLE);
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);  
//							    lp.setMargins(26, 0, 0, 0);  
//							    imgview.setLayoutParams(lp);
							}
							
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("view", imgview);
							map.put("value", url);
							maplist.add(map);
	//						setMomentsImage(imgview,fileurl);
							android.view.ViewGroup.LayoutParams para = imgview.getLayoutParams();
//					        para.height = 100;  
//					        para.width = 100;
//							if(imgview.getWidth() > 0 )
//							{
//								para.height = imgview.getWidth();  
//						        para.width = imgview.getWidth();  
//						        imgview.setLayoutParams(para); 
//							}
					        if(SCROLL_STATE_TOUCH_SCROLL)
					        	imgview.setImageResource(R.color.list_item_background);
					        else
					        {
					        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(url,100,100,true));
					        	bitmaplist.add(bitmap);
					        	imgview.setImageBitmap(bitmap.get());
					        }
							imgview.setVisibility(View.VISIBLE);
							imgview.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MomentsActivity.instance.getImageBitmaplist(position,index);
								}
							});
						}
					}
				}
			}
			
			//放附件
			if(filelistAttachment.size() > 0)
				viewHolder2.attachment_layout.setVisibility(View.VISIBLE);
			else
				viewHolder2.attachment_layout.setVisibility(View.GONE);
			for(int i=0;i<filelistAttachment.size();i++)
			{
				final int index = i;
				Map<String,String> filemap = filelistAttachment.get(i);
				String fileurl = filemap.get("fileurl");
				String filename = filemap.get("filename");
				String filetype = filemap.get("filetype");
				String voidimg = filemap.get("voidimg");
				String url = fileurl.replace("/1b/", "/1a/");
				
				if(filetype.equals("2")){
					viewHolder2.attachment_name_textview.setVisibility(View.GONE);
					viewHolder2.attachment_tag_imageview.setVisibility(View.GONE);
					//获取视频缩略图
					final String videoUrl = fileurl;
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("view", viewHolder2.voidimg);
					map.put("value", voidimg);
					maplist.add(map);
					if(SCROLL_STATE_TOUCH_SCROLL)
					{
						viewHolder2.voidimg.setImageResource(R.color.list_item_background);
					}
			        else
			        {
			        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap(voidimg));
			        	bitmaplist.add(bitmap);
						viewHolder2.voidimg.setImageBitmap(bitmap.get());
			        }
					viewHolder2.voidimg.setVisibility(View.VISIBLE);
					viewHolder2.attachment_video_ll.setVisibility(View.VISIBLE);
					viewHolder2.attachment_video_ll.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("点击附件咯"+videoUrl);
							Intent intent = AndroidFileOpenUtil.openFile(videoUrl);
							try{
								MomentsActivity.instance.startActivity(intent);
							}catch(Exception e){
								System.out.println(e.getMessage());
								Toast.makeText(MomentsActivity.instance, "检测手机无法打开该文件，请安装相应软件！",
										Toast.LENGTH_SHORT).show();
							}
							
//							FileUtils fileUtil = new FileUtils();
//							String url = "";
//							try {
//								url = fileUtil.downFile(fileurl, publishid, filename);
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							System.out.println("点击附件咯"+url);
//							Intent intent = AndroidFileOpenUtil.openFile(url);
//							try{
//								MomentsActivity.instance.startActivity(intent);
//							}catch(Exception e){
//								System.out.println(e.getMessage());
//								Toast.makeText(MomentsActivity.instance, "检测手机无法打开该文件，请安装相应软件！",
//										Toast.LENGTH_SHORT).show();
//							}
						}
					});
				}else{
					viewHolder2.attachment_video_ll.setVisibility(View.GONE);
					Bitmap bitmap = viewHolder2.voidimg.getDrawingCache();
					if (bitmap != null && !bitmap.isRecycled()) {  
						bitmap.recycle();
					}
					viewHolder2.voidimg.setVisibility(View.GONE);
					
					viewHolder2.attachment_name_textview.setVisibility(View.VISIBLE);
					viewHolder2.attachment_name_textview.setText(filename);
					viewHolder2.attachment_url_textview.setText(fileurl);
					viewHolder2.attachment_tag_imageview.setVisibility(View.VISIBLE);
					String filenameArr[] = filename.split("\\.");
					String filetypeName = filenameArr[1];
					if(filetypeName.equals("txt")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.txt);
					}else if(filetypeName.equals("docx") || filetypeName.equals("doc")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.doc);
					}else if(filetypeName.equals("xlsx") || filetypeName.equals("xls")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.excel);
					}else if(filetypeName.equals("pptx")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.ppt);
					}else if(filetypeName.equals("pdf")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.pdf);
					}else if(filetypeName.equals("mp4") || filetypeName.equals("3gp")){
						viewHolder2.attachment_tag_imageview.setImageResource(R.drawable.mp4);
					}
					viewHolder2.attachment_layout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("点击附件咯"+viewHolder2.attachment_url_textview.getText());
							Intent intent = AndroidFileOpenUtil.openFile((String)viewHolder2.attachment_url_textview.getText());
							try{
								MomentsActivity.instance.startActivity(intent);
							}catch(Exception e){
								System.out.println(e.getMessage());
								Toast.makeText(MomentsActivity.instance, "检测手机无法打开该文件，请安装相应软件！",
										Toast.LENGTH_SHORT).show();
							}
							
//							FileUtils fileUtil = new FileUtils();
//							String url = "";
//							try {
//								url = fileUtil.downFile(fileurl, publishid, filename);
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							System.out.println("点击附件咯"+url);
//							Intent intent = AndroidFileOpenUtil.openFile(url);
//							try{
//								MomentsActivity.instance.startActivity(intent);
//							}catch(Exception e){
//								System.out.println(e.getMessage());
//								Toast.makeText(MomentsActivity.instance, "检测手机无法打开该文件，请安装相应软件！",
//										Toast.LENGTH_SHORT).show();
//							}
						}
					});
				}
			}
			imgmap.put(position, maplist);
			dateCache.put(position, bitmaplist);
			
			for(int j=size;j<9;j++)
			{
				if(j<3)
				{
					if(j == 0)
						viewHolder2.image_layout_1.setVisibility(View.GONE);
					ImageView imgview = (ImageView)viewHolder2.image_layout_1.getChildAt(j);
					Bitmap bitmap = imgview.getDrawingCache();
					if (bitmap != null && !bitmap.isRecycled()) {  
						bitmap.recycle();
					}
					imgview.setImageBitmap(null);
					imgview.setVisibility(View.GONE);
					if(j > 1)
						imgview.setVisibility(View.VISIBLE);
				}
				else if(j < 6)
				{
					if(j == 3)
						viewHolder2.image_layout_2.setVisibility(View.GONE);
					ImageView imgview = (ImageView)viewHolder2.image_layout_2.getChildAt(j-3);
					Bitmap bitmap = imgview.getDrawingCache();
					if (bitmap != null && !bitmap.isRecycled()) {  
						bitmap.recycle();
					}
					imgview.setImageBitmap(null);
					imgview.setVisibility(View.GONE);
					if(j > 3)
						imgview.setVisibility(View.VISIBLE);
				}
				else
				{
					if(j == 6)
						viewHolder2.image_layout_3.setVisibility(View.GONE);
					ImageView imgview = (ImageView)viewHolder2.image_layout_3.getChildAt(j-6);
					Bitmap bitmap = imgview.getDrawingCache();
					if (bitmap != null && !bitmap.isRecycled()) {  
						bitmap.recycle();
					}
					imgview.setImageBitmap(null);
					imgview.setVisibility(View.GONE);
					if(j > 6)
						imgview.setVisibility(View.VISIBLE);
				}
			}
			
			if(filelistAttachment == null || filelistAttachment.size()==0){
				viewHolder2.attachment_video_ll.setVisibility(View.GONE);
				Bitmap bitmap = viewHolder2.voidimg.getDrawingCache();
				if (bitmap != null && !bitmap.isRecycled()) {  
					bitmap.recycle();
				}
				viewHolder2.voidimg.setVisibility(View.GONE);
				viewHolder2.attachment_name_textview.setVisibility(View.GONE);
				viewHolder2.attachment_tag_imageview.setVisibility(View.GONE);
			}
			
//			//如果是朋友圈暂不去评论
////			if(!MomentsActivity.instance.type.equals("")){
//				viewHolder2.album_show_comment_tv.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						if(currLayout != viewHolder2.album_comment_container)
//							closePinlunlayout();
//						currLayout = viewHolder2.album_comment_container;
//						if(viewHolder2.album_comment_container.getVisibility() == View.VISIBLE)
//						{
//							viewHolder2.album_comment_container.setAnimation(animationout);
//							viewHolder2.album_comment_container.setVisibility(View.GONE);
//						}
//						else
//						{
//							viewHolder2.album_comment_container.setAnimation(animationin);
//							viewHolder2.album_comment_container.setVisibility(View.VISIBLE);
//						}
//					}
//				});
////			}
			
			//点击评论赞
			viewHolder2.album_like_tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					viewHolder2.album_comment_container.setVisibility(View.GONE);
//					System.out.println("赞一个");
					MomentsActivity.instance.saveCommentZhan(viewHolder2.album_comment_publishID.getText().toString());
				}
			});
			
			//点击评论内容
			viewHolder2.album_comment_tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) { 
					// TODO Auto-generated method stub
//					viewHolder2.album_comment_container.setVisibility(View.GONE);
					MomentsActivity.instance.openAddComment(viewHolder2.album_comment_publishID.getText().toString());
				}
			});
			
			//保存当前公告ID
			viewHolder2.album_comment_publishID.setText(pkid);
			
			int zhansize = 0;
			//封装评论
			if(commentList.size()>0){
				viewHolder2.album_comment_listView.removeAllViews();
				for(int n=0; n<commentList.size(); n++){
					Map<String, String> map = commentList.get(n);
					String userName = map.get("userName");
					String commentText = map.get("discusDesc");
			        View layout = mInflater.inflate(R.layout.adapter_comment_item, null);
			        TextView userNameTV = (TextView) layout.findViewById(R.id.comment_name);
			        TextView commentTextTV = (TextView) layout.findViewById(R.id.comment_text);
			        ImageView zhanicon = (ImageView)layout.findViewById(R.id.zhan_icon);
			        userNameTV.setText(userName+":");
			        commentTextTV.setText(commentText);
			        if(commentText.equals("/微笑"))
			        {
			        	zhansize++;
			        	commentTextTV.setVisibility(View.GONE);
			        	zhanicon.setVisibility(View.VISIBLE);
			        }
			        
			        viewHolder2.album_comment_listView.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				}
				viewHolder2.album_comment_listView.setVisibility(View.VISIBLE);
				
				if(commentList.size()-zhansize > 0)
				{
					viewHolder2.comment_count.setText(String.valueOf(commentList.size()-zhansize));
					viewHolder2.comment_count.setVisibility(View.VISIBLE);
					viewHolder2.comment_icon.setVisibility(View.VISIBLE);
				}
				else
				{
					viewHolder2.comment_count.setVisibility(View.GONE);
					viewHolder2.comment_icon.setVisibility(View.GONE);
				}
				
				if(zhansize > 0)
				{
					viewHolder2.like_count.setText(String.valueOf(zhansize));
					viewHolder2.like_count.setVisibility(View.VISIBLE);
					viewHolder2.like_icon.setVisibility(View.VISIBLE);
				}
				else
				{
					viewHolder2.like_count.setVisibility(View.GONE);
					viewHolder2.like_icon.setVisibility(View.GONE);
				}
			}else{
				viewHolder2.album_comment_listView.setVisibility(View.GONE);
				viewHolder2.comment_count.setVisibility(View.GONE);
				viewHolder2.comment_icon.setVisibility(View.GONE);
				
				viewHolder2.like_count.setVisibility(View.GONE);
				viewHolder2.like_icon.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	static class ViewHolder1{
		LinearLayout setting_bg;//设置背景文字
		TextView sns_empty_view;//没有数据文字
		ImageView avatar_iv;//用户头像
		TextView sign_tv;
		TextView nickname_tv;//用户名
		ImageView sns_back_ll;//主题背景图片
		LinearLayout load_layout;//加载
	}
	
	static class ViewHolder2{
		ImageView sns_msg_avatar_iv;//用户头像
		TextView sns_msg_nick_tv;//用户名字
		CollapsibleTextView sns_msg_content_tv;//用户发表的内容
//		CollapsibleTextView sns_msg_more_tv;//显示更多
		TextView sns_msg_time_tv;//发布时间
		LinearLayout sns_msg_position_ll;//发布地理位置布局
		TextView sns_msg_position_tv;//发布地理位置
//		ImageButton album_show_comment_tv;//发表评论
		TextView album_comment_tv;//发表评论
		TextView album_like_tv;//发表赞
		LinearLayout image_layout_1;
		LinearLayout image_layout_2;
		LinearLayout image_layout_3;//图片显示区域
		LinearLayout attachment_layout;//附件显示区域
		ImageView attachment_tag_imageview;//附件类型标志
		TextView attachment_name_textview;//附件名称
		TextView attachment_url_textview;//附件路径
		RelativeLayout attachment_video_ll;//附件针对视频
		ImageView voidimg;//视频截图
//		LinearLayout album_comment_container;//评论框
		LinearLayout album_comment_like_ll;//评论赞
		LinearLayout album_comment_text_ll;//评论内容
		TextView album_comment_publishID;//评论所属公告ID
		LinearLayout album_comment_listView;//评论列表
		LinearLayout layout_bg;
		TextView comment_count;//评论数量
		ImageView comment_icon;//评论数量icon
		TextView like_count;//赞数量
		ImageView like_icon;//赞数量icon
		LinearLayout img_layout_one;
		LinearLayout image_layout_one;
		LinearLayout img_layout;
	}
	
	public void setThemebg(Bitmap bitmap)
	{
		if(bitmap != null)
		{
			Map dataSet = mDlist.get(0);
			dataSet.put("momentsbgurl",myapp.getThembgurl());
			bgimg.setImageBitmap(bitmap);
			bglayut.setVisibility(View.GONE);
		}
	}
	
	public void closePinlunlayout()
	{
		if(currLayout != null)
		{
			currLayout.setAnimation(animationout);
			currLayout.setVisibility(View.GONE);
		}
	}
	
	public void showPinlunwindo(int x,int y)
	{
		PinLunDialog selectDialog = new PinLunDialog(mContext,R.style.MyDialogStyle2);//创建Dialog并设置样式主题
		Window win = selectDialog.getWindow();
		LayoutParams params = new LayoutParams();
		params.x = x;//设置x坐标
		params.y = y;//设置y坐标
		win.setAttributes(params);
		selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
		selectDialog.show();
	}
	
	public void setViewImage(final ImageView v, String url) {
		v.setImageBitmap(null);
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
//			url = str+"_zhong"+str2;
			
			imageLoader.loadDrawable(url, new AsyncImageLoaderMoments.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						MomentsActivity.instance.sns_refresh_iv.startAnimation(animationout);
						MomentsActivity.instance.sns_refresh_iv.setVisibility(View.GONE);
						v.setImageDrawable(imageDrawable);
					}
				}
			}, myapp.getScreenWidth(), myapp.getScreenHeight(),false);
		}
		else
		{
			
			if (imageCache2.containsKey(url)) {       
	            Bitmap softReference = imageCache2.get(url);  
	            if (softReference != null) {   
	                v.setImageBitmap(softReference);
	            }  
	        }else{  
	        	Bitmap bmpimg = getLoacalBitmap(url,false);
	            imageCache2.put(url, bmpimg);
	            v.setImageBitmap(bmpimg);
	         } 
		}
	}
	
	public void setViewImage2(final ImageView v, final String url,final int position,final int listindex,final String publishid) {
		v.setImageBitmap(null);
		if(url.indexOf("http:") >= 0)
		{
			imageLoader.loadBitmap2(url, new AsyncImageLoaderMoments.ImageCallback2() {
				public void imageLoaded(Bitmap imageDrawable2, String imageUrl) {
					if (imageDrawable2 != null) {
						String fname = url.substring(url.lastIndexOf("/")+1,url.length());
						String [] fnames = fname.split("\\.");
						fname = fnames[0];
						
						String furl = fileUtil.getImageFile1aPath(publishid, fname);
						String furl2 = fileUtil.getImageFile1bPath(publishid, fname);
						
						if(!fileUtil.isFileExist2(publishid))
							fileUtil.createUserFile(publishid);
						
						Map dataSet = mDlist.get(position);
						List<Map<String,String>> filelistImg = (List<Map<String,String>>)dataSet.get("filelist");
						Bitmap imageDrawable = null;
						int width = myapp.getScreenWidth()-150;
						if(filelistImg.size() == 1)
							imageDrawable = myapp.adaptive(imageDrawable2,width,width);
						else
							imageDrawable = Bitmap.createScaledBitmap(imageDrawable2,100,100,true);
						
						fileUtil.saveMyBitmap(furl, imageDrawable);
						fileUtil.saveMyBitmap(furl2, imageDrawable2);
						
						Map<String,String> filemap = filelistImg.get(listindex);
						filemap.put("fileurl",furl2);
						filemap.put("filewidth",String.valueOf(imageDrawable.getWidth()));
						filemap.put("fileheight",String.valueOf(imageDrawable.getHeight()));
						
						List<Map<String,Object>> maplist = imgmap.get(position);
						List<WeakReference<Bitmap>> bitmaplist = dateCache.get(position);
						
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("view", v);
						map.put("value", furl2);
						maplist.add(map);

						android.view.ViewGroup.LayoutParams para = v.getLayoutParams();
//				        para.height = Integer.valueOf(imageDrawable.getHeight());
//				        para.width = Integer.valueOf(imageDrawable.getWidth());  
						if(filelistImg.size() == 1)
						{
							para.height = imageDrawable.getHeight();
					        para.width = myapp.getScreenWidth()-150;
					        v.setLayoutParams(para);
						}
//						else
//						{
//							para.height = v.getWidth();
//					        para.width = v.getWidth();
//						}
				        
				        if(SCROLL_STATE_TOUCH_SCROLL)
				        	v.setImageResource(R.color.list_item_background);
				        else
				        {
				        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(imageDrawable);
				        	bitmaplist.add(bitmap);
				        	v.setImageBitmap(bitmap.get());
				        }
					}
				}
			}, 300, 300,true);
		}
	}
	
	public void setMomentsUserImage(final ImageView v, String url,final String furl,final String publishid) {
		v.setImageBitmap(null);
		if(url.indexOf("http:") >= 0)
		{
			imageLoader.loadBitmap2(url, new AsyncImageLoaderMoments.ImageCallback2() {
				public void imageLoaded(Bitmap imageDrawable2, String imageUrl) {
					if (imageDrawable2 != null) {
						if(!fileUtil.isFileExist2(publishid))
							fileUtil.createUserFile(publishid);
						fileUtil.saveMyBitmap(furl, imageDrawable2);
						v.setImageBitmap(imageDrawable2);
						SoftReference<Bitmap> srf = new SoftReference<Bitmap>(imageDrawable2);
						userImageCache.put(furl, srf);
					}
					else
					{
						v.setImageResource(R.drawable.mini_avatar_shadow);
					}
				}
			}, 100, 100,true);
		}
	}
	
	public void setMomentsImage(final ImageView v, String url) {
		v.setImageDrawable(null);
		v.setVisibility(View.VISIBLE);
		if (imageCache2.containsKey(url)) {       
            Bitmap softReference = imageCache2.get(url);  
            if (softReference != null) {   
            	softReference = Bitmap.createScaledBitmap(softReference, 100, 100,true);
                v.setImageBitmap(softReference);
            }  
        }else{
        	loadImageViewBitmap(v,url);
        }
	}
	
	public void setMomentsImage2(final ImageView v, String url) {
		v.setImageBitmap(null);
		v.setVisibility(View.VISIBLE);
		if (imageCache2.containsKey(url)) {       
            Bitmap softReference = imageCache2.get(url);
            if (softReference != null) {
            	softReference = myapp.adaptive(softReference,myapp.getScreenWidth()/2,myapp.getScreenWidth()/2);
                v.setImageBitmap(softReference);
            }  
        }else{  
        	loadImageViewBitmap2(v,url);
        }
	}
	
	public void loadImageViewBitmap(final ImageView v,final String url)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Map<String,Object> map = new HashMap<String,Object>();
					if (imageCache2.containsKey(url)) {       
			            Bitmap softReference = imageCache2.get(url);  
			            if (softReference != null) {   
			            	softReference = Bitmap.createScaledBitmap(softReference, 100, 100,true);
//			                v.setImageBitmap(softReference);
			            	map.put("bitmap", softReference);
			            }  
			        }else{  
			            Bitmap bitmap = getLoacalBitmap(url,false);
			            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
			            imageCache2.put(url, bitmap);
//			            v.setImageBitmap(bitmap2);
			            map.put("bitmap", bitmap2);
			         } 
					map.put("view", v);
					msg.obj = map;
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadImageViewBitmap2(final ImageView v,final String url)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Map<String,Object> map = new HashMap<String,Object>();
					if (imageCache2.containsKey(url)) {       
			            Bitmap softReference = imageCache2.get(url);
			            if (softReference != null) {
			            	softReference = myapp.adaptive(softReference,myapp.getScreenWidth()/2,myapp.getScreenWidth()/2);
//			                v.setImageBitmap(softReference);
			            	map.put("bitmap", softReference);
			            }  
			        }else{  
			            Bitmap bitmap = getLoacalBitmap(url,false);
			            Bitmap bitmap2 = myapp.adaptive(bitmap,myapp.getScreenWidth()/2,myapp.getScreenWidth()/2);
			            imageCache2.put(url, bitmap);
//			            v.setImageBitmap(bitmap2);
			            map.put("bitmap", bitmap2);
			         } 
					map.put("view", v);
					msg.obj = map;
				} catch (Exception ex) {
//					Log.i("erroyMessage", ex.getMessage());
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
				Map<String, Object> map = (Map<String, Object>)msg.obj;
				if(map != null)
				{
					Bitmap bitmap = (Bitmap)map.get("bitmap");
					ImageView v = (ImageView)map.get("view");
					if(v != null)
					{
						v.setImageBitmap(bitmap);
					}
				}
				break;
			}
		}
	};
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url,boolean b) {
	     try {
			FileInputStream fis = new FileInputStream(url);
		
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
			if(b)
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
}
