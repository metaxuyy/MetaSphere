package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.hotel.MessageListActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class HotelMainAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
//	private String[] mNicks;
//	private String[] mImage;
	private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private ViewBinder mViewBinder;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private String[] mFrom;
	private int[] mTo;
	private int mResource;
	private int mResource1;
	private LayoutInflater mInflater;
	private MyApp myapp;
	private Map<Integer,WeakReference<Bitmap>> dateCache = new HashMap<Integer,WeakReference<Bitmap>>();//图片资源缓存
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	public TextView numbertxt;
	private static DBHelperMessage db;
	private static FileUtils fileUtil = new FileUtils();
//	private String isYn;
	
	@SuppressWarnings("unchecked")
	public HotelMainAdapter(Context context,List<Map<String,Object>> data,int resource,int resource1,String[] from, int[] to,MyApp myapp){
//		super(context, data, resource, from, to);
		this.mContext = context;
//		this.mNicks = nicks;
//		this.mImage = userimgs;
//		this.mDlist = data;
		this.mDlist.addAll(data);
		mResource = resource;
		mResource1 = resource1;
		mFrom = from;
		mTo = to;
		this.myapp = myapp;
		db = new DBHelperMessage(context, myapp);
		//排序(实现了中英文混排)
//		List datalist = mDlist;
//		datalist.remove(0);
//		Collections.sort(mDlist, new PinyinComparator());
//		mDlist.add(1,new HashMap<String,Object>());
		
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
	
	public void getRefresh(List<Map<String,Object>> list){  
		mDlist = list;
		this.notifyDataSetChanged();
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
	
	public void removeImageView(int start,int end)
	{
		try{
			start = start - 1;
			end = end + 1;
			WeakReference<Bitmap> delBitmap;
			WeakReference<Bitmap> delBitmap2;
			for (int del = 0; del < start; del++) {
				delBitmap = dateCache.get(del);
				if (delBitmap != null) {
					// 如果非空则表示有缓存的bitmap，需要清理
					// 从缓存中移除该del->bitmap的映射
					dateCache.remove(del);
					if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {  
						delBitmap.get().recycle();
					}
				}
			}
			
			for (int del = end + 1; del < getCount(); del++) {
				delBitmap2 = dateCache.get(del);
				if (delBitmap2 != null) {
					// 如果非空则表示有缓存的bitmap，需要清理
					// 从缓存中移除该del->bitmap的映射
					dateCache.remove(del);
					if (delBitmap2.get() != null && !delBitmap2.get().isRecycled()) {  
						delBitmap2.get().recycle();
					}
				}
			}
			
			System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public int getItemViewType(int position){
		Map dataSet = mDlist.get(position);
		String messagetype = (String)dataSet.get("typesMapping");
		if(messagetype.equals("group"))
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
		
		Map dataSet = mDlist.get(position);
		String storeName = (String)dataSet.get("storeName");
		Object imgurl = dataSet.get("imgurl");
		String lastmessagetime = (String)dataSet.get("lastmessagetime");
		String lastmessage = (String)dataSet.get("lastmessage");
		int newNumbers = (Integer)dataSet.get("newNumber");
		String newNumber = String.valueOf(newNumbers);
		String servicename = (String)dataSet.get("servicename");
		String watag = (String)dataSet.get("watag");
		
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
				viewHolder1.head1 = (ImageView)convertView.findViewById(R.id.head1);
				viewHolder1.head2 = (ImageView)convertView.findViewById(R.id.head2);
				viewHolder1.head3 = (ImageView)convertView.findViewById(R.id.head3);
				viewHolder1.head4 = (ImageView)convertView.findViewById(R.id.head4);
				viewHolder1.img_layout2 = (LinearLayout)convertView.findViewById(R.id.img_layout2);
				viewHolder1.name_txt = (TextView)convertView.findViewById(R.id.name_txt);
				viewHolder1.time_txt = (TextView)convertView.findViewById(R.id.time_txt);
				viewHolder1.doc_txt = (TextView)convertView.findViewById(R.id.doc_txt);
				viewHolder1.new_number = (TextView)convertView.findViewById(R.id.new_number);
				viewHolder1.info_txt = (TextView)convertView.findViewById(R.id.info_txt);
				viewHolder1.weixin_icon = (ImageView)convertView.findViewById(R.id.weixin_icon);
				convertView.setTag(viewHolder1);
				break;
			case TYPE_2:
				convertView = LayoutInflater.from(mContext).inflate(mResource1, null);
				viewHolder2 = new ViewHolder2();
				viewHolder2.head = (ImageView)convertView.findViewById(R.id.head);
				viewHolder2.name_txt = (TextView)convertView.findViewById(R.id.name_txt);
				viewHolder2.time_txt = (TextView)convertView.findViewById(R.id.time_txt);
				viewHolder2.doc_txt = (TextView)convertView.findViewById(R.id.doc_txt);
				viewHolder2.new_number = (TextView)convertView.findViewById(R.id.new_number);
				viewHolder2.info_txt = (TextView)convertView.findViewById(R.id.info_txt);
				viewHolder2.weixin_icon = (ImageView)convertView.findViewById(R.id.weixin_icon);
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
			if(storeName != null && !storeName.equals(""))
				viewHolder1.name_txt.setText(storeName);
			
			if(lastmessagetime != null && !lastmessagetime.equals(""))
				viewHolder1.time_txt.setText(lastmessagetime);
			
			if(lastmessage != null && !lastmessage.equals(""))
				viewHolder1.doc_txt.setText(getMessageContent(lastmessage));
			
			if(newNumber != null && !newNumber.equals(""))
			{
				if(newNumber.equals("0"))
					viewHolder1.new_number.setVisibility(View.GONE);
				else
					viewHolder1.new_number.setText(newNumber);
			}
			else
			{
				viewHolder1.new_number.setVisibility(View.GONE);
			}
			
			if(servicename != null && !servicename.equals(""))
			{
				viewHolder1.info_txt.setText(servicename);
				viewHolder1.info_txt.setVisibility(View.VISIBLE);
			}
			else
			{
				viewHolder1.info_txt.setVisibility(View.GONE);
			}
			
			if(watag != null && !watag.equals(""))
			{
				if(watag.equals("app"))
				{
					viewHolder1.weixin_icon.setVisibility(View.GONE);
				}
				else
				{
					viewHolder1.weixin_icon.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				viewHolder1.weixin_icon.setVisibility(View.GONE);
			}
			
			if(imgurl != null)
			{
				
				String [] imgs = ((String)imgurl).split(",");
				if(imgs.length == 3)
				{
					viewHolder1.img_layout2.setVisibility(View.GONE);
					viewHolder1.head3.setVisibility(View.GONE);
					viewHolder1.head4.setVisibility(View.GONE);
					
					
					String fid1 = "";
					String fid2 = "";
					for(int i=0;i<imgs.length;i++)
					{
						String fid = imgs[i];
						if(!fid.equals(myapp.getUserNameId()))
						{
							if(fid1.equals(""))
								fid1 = fid;
							else
								fid2 = fid;
						}
					}
					
//					db.openDB();
//					String imgpath1 = DBHelperMessage.getFriendImagePath(fid1);
//					String imgpath2 = DBHelperMessage.getFriendImagePath(fid2);
//					db.closeDB();
//					
//					if(imgpath1 != null)
//						setRoundAngleImageView(viewHolder1.head1,imgpath1);
//					if(imgpath2 != null)
//						setRoundAngleImageView(viewHolder1.head2,imgpath2);
					
					String furl = fileUtil.getImageFile2aPath(fid1, fid1);
	    			if(!fileUtil.isFileExist3(furl))
	    			{
	    				if(!fileUtil.isFileExist2(fid1))
	    					fileUtil.createUserFile(fid1);
//		    			String url = Douban.IMG_BASE_URL + fid1 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid1;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
							myapp.saveMyBitmap(furl, bmpsimg);
							setRoundAngleImageView(viewHolder1.head1,furl);
		    			}
		    			else
		    			{
		    				viewHolder1.head1.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    			{
	    				setRoundAngleImageView(viewHolder1.head1,furl);
	    			}
					
	    			String furl2 = fileUtil.getImageFile2aPath(fid2, fid2);
	    			if(!fileUtil.isFileExist3(furl2))
	    			{
	    				if(!fileUtil.isFileExist2(fid2))
	    					fileUtil.createUserFile(fid2);
//		    			String url = Douban.IMG_BASE_URL + fid2 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid2;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl2, bmpsimg);
							setRoundAngleImageView(viewHolder1.head2,furl2);
		    			}
		    			else
		    			{
		    				viewHolder1.head2.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    			{
	    				setRoundAngleImageView(viewHolder1.head2,furl2);
	    			}
	    			
	    			viewHolder1.head1.setVisibility(View.VISIBLE);
					viewHolder1.head2.setVisibility(View.VISIBLE);
				}
				else if(imgs.length == 4)
				{
					viewHolder1.img_layout2.setVisibility(View.VISIBLE);
					viewHolder1.head3.setVisibility(View.VISIBLE);
					viewHolder1.head4.setVisibility(View.VISIBLE);
					
					viewHolder1.head1.setVisibility(View.VISIBLE);
					viewHolder1.head2.setVisibility(View.GONE);
					
					String fid1 = "";
					String fid2 = "";
					String fid3 = "";
					for(int i=0;i<imgs.length;i++)
					{
						String fid = imgs[i];
						if(!fid.equals(myapp.getUserNameId()))
						{
							if(fid1.equals(""))
								fid1 = fid;
							else if(fid2.equals(""))
								fid2 = fid;
							else
								fid3 = fid;
						}
					}
					
//					db.openDB();
//					String imgpath1 = DBHelperMessage.getFriendImagePath(fid1);
//					String imgpath2 = DBHelperMessage.getFriendImagePath(fid2);
//					String imgpath3 = DBHelperMessage.getFriendImagePath(fid3);
//					db.closeDB();
					
//					if(imgpath1 != null)
//						setRoundAngleImageView(viewHolder1.head1,imgpath1);
//					if(imgpath2 != null)
//						setRoundAngleImageView(viewHolder1.head3,imgpath2);
//					if(imgpath3 != null)
//						setRoundAngleImageView(viewHolder1.head4,imgpath3);
					
					String furl = fileUtil.getImageFile2aPath(fid1, fid1);
	    			if(!fileUtil.isFileExist3(furl))
	    			{
	    				if(!fileUtil.isFileExist2(fid1))
	    					fileUtil.createUserFile(fid1);
//		    			String url = Douban.IMG_BASE_URL + fid1 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid1;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl, bmpsimg);
							setRoundAngleImageView(viewHolder1.head1,furl);
		    			}
		    			else
		    			{
		    				viewHolder1.head1.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    			{
	    				setRoundAngleImageView(viewHolder1.head1,furl);
	    			}
					
	    			String furl2 = fileUtil.getImageFile2aPath(fid2, fid2);
	    			if(!fileUtil.isFileExist3(furl2))
	    			{
	    				if(!fileUtil.isFileExist2(fid2))
	    					fileUtil.createUserFile(fid2);
//		    			String url = Douban.IMG_BASE_URL + fid2 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid2;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl2, bmpsimg);
							setRoundAngleImageView(viewHolder1.head3,furl2);
		    			}
		    			else
		    			{
		    				viewHolder1.head3.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head3,furl2);
	    			
	    			
	    			String furl3 = fileUtil.getImageFile2aPath(fid3, fid3);
	    			if(!fileUtil.isFileExist3(furl3))
	    			{
	    				if(!fileUtil.isFileExist2(fid3))
	    					fileUtil.createUserFile(fid3);
//		    			String url = Douban.IMG_BASE_URL + fid3 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid3;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl3, bmpsimg);
							setRoundAngleImageView(viewHolder1.head4,furl3);
		    			}
		    			else
		    			{
		    				viewHolder1.head4.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head4,furl3);
					
				}
				else
				{
					viewHolder1.img_layout2.setVisibility(View.VISIBLE);
					viewHolder1.head3.setVisibility(View.VISIBLE);
					viewHolder1.head4.setVisibility(View.VISIBLE);
					
					viewHolder1.head1.setVisibility(View.VISIBLE);
					viewHolder1.head2.setVisibility(View.VISIBLE);
					
					String fid1 = "";
					String fid2 = "";
					String fid3 = "";
					String fid4 = "";
					for(int i=0;i<imgs.length;i++)
					{
						String fid = imgs[i];
						if(!fid.equals(myapp.getUserNameId()))
						{
							if(fid1.equals(""))
								fid1 = fid;
							else if(fid2.equals(""))
								fid2 = fid;
							else if(fid3.equals(""))
								fid3 = fid;
							else
							{
								fid4 = fid;
								break;
							}
						}
					}
					
//					db.openDB();
//					String imgpath1 = DBHelperMessage.getFriendImagePath(fid1);
//					String imgpath2 = DBHelperMessage.getFriendImagePath(fid2);
//					String imgpath3 = DBHelperMessage.getFriendImagePath(fid3);
//					String imgpath4 = DBHelperMessage.getFriendImagePath(fid4);
//					db.closeDB();
					
					String furl = fileUtil.getImageFile2aPath(fid1, fid1);
	    			if(!fileUtil.isFileExist3(furl))
	    			{
	    				if(!fileUtil.isFileExist2(fid1))
	    					fileUtil.createUserFile(fid1);
//		    			String url = Douban.IMG_BASE_URL + fid1 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid1;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl, bmpsimg);
							setRoundAngleImageView(viewHolder1.head1,furl);
		    			}
		    			else
		    			{
		    				viewHolder1.head1.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head1,furl);
					
	    			String furl2 = fileUtil.getImageFile2aPath(fid2, fid2);
	    			if(!fileUtil.isFileExist3(furl2))
	    			{
	    				if(!fileUtil.isFileExist2(fid2))
	    					fileUtil.createUserFile(fid2);
//		    			String url = Douban.IMG_BASE_URL + fid2 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid2;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl2, bmpsimg);
							setRoundAngleImageView(viewHolder1.head2,furl2);
		    			}
		    			else
		    			{
		    				viewHolder1.head2.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head2,furl2);
	    			
	    			
	    			String furl3 = fileUtil.getImageFile2aPath(fid3, fid3);
	    			if(!fileUtil.isFileExist3(furl3))
	    			{
	    				if(!fileUtil.isFileExist2(fid3))
	    					fileUtil.createUserFile(fid3);
//		    			String url = Douban.IMG_BASE_URL + fid3 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid3;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl3, bmpsimg);
							setRoundAngleImageView(viewHolder1.head3,furl3);
		    			}
		    			else
		    			{
		    				viewHolder1.head3.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head3,furl3);
					
					
	    			String furl4 = fileUtil.getImageFile2aPath(fid4, fid4);
	    			if(!fileUtil.isFileExist3(furl4))
	    			{
	    				if(!fileUtil.isFileExist2(fid4))
	    					fileUtil.createUserFile(fid4);
//		    			String url = Douban.IMG_BASE_URL + fid4 +".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+fid4;
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
		    			if(bmpsimg != null)
		    			{
		    				myapp.saveMyBitmap(furl4, bmpsimg);
							setRoundAngleImageView(viewHolder1.head4,furl4);
		    			}
		    			else
		    			{
		    				viewHolder1.head4.setImageResource(R.drawable.default_avatar_shadow);
		    			}
	    			}
	    			else
	    				setRoundAngleImageView(viewHolder1.head4,furl4);
				}
					
			}
			break;
		case TYPE_2:
			if(storeName != null && !storeName.equals(""))
				viewHolder2.name_txt.setText(storeName);
			
			if(lastmessagetime != null && !lastmessagetime.equals(""))
				viewHolder2.time_txt.setText(lastmessagetime);
			
			if(lastmessage != null && !lastmessage.equals(""))
				viewHolder2.doc_txt.setText(getMessageContent(lastmessage));
			
			if(newNumber != null && !newNumber.equals(""))
			{
				if(newNumber.equals("0"))
					viewHolder2.new_number.setVisibility(View.GONE);
				else
					viewHolder2.new_number.setText(newNumber);
			}
			else
			{
				viewHolder2.new_number.setVisibility(View.GONE);
			}
			
			if(servicename != null && !servicename.equals(""))
			{
				viewHolder2.info_txt.setText(servicename);
				viewHolder2.info_txt.setVisibility(View.VISIBLE);
			}
			else
			{
				viewHolder2.info_txt.setVisibility(View.GONE);
			}
			
			if(watag != null && !watag.equals(""))
			{
				if(watag.equals("app"))
				{
					viewHolder2.weixin_icon.setVisibility(View.GONE);
				}
				else
				{
					viewHolder2.weixin_icon.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				viewHolder2.weixin_icon.setVisibility(View.GONE);
			}
			
			if(imgurl != null)
			{
				if (imgurl instanceof Integer)
				{
					viewHolder2.head.setImageResource((Integer)imgurl);
				}
				else if (imgurl instanceof Bitmap)
				{
					viewHolder2.head.setImageBitmap((Bitmap)imgurl);
				}
				else
				{
					setRoundAngleImageView2(viewHolder2.head,(String)imgurl);
				}
					
			}
			else
			{
				viewHolder2.head.setImageResource(R.drawable.default_avatar);
			}
			break;
		}
		
		return convertView;
	}
	
	static class ViewHolder1{
		ImageView head1;
		ImageView head2;
		ImageView head3;
		ImageView head4;
		LinearLayout img_layout2;
		TextView name_txt;
		TextView time_txt;
		TextView doc_txt;
		TextView new_number;
		TextView info_txt;
		ImageView weixin_icon;
	}
	
	static class ViewHolder2{
		ImageView head;
		TextView name_txt;
		TextView time_txt;
		TextView doc_txt;
		TextView new_number;
		TextView info_txt;
		ImageView weixin_icon;
	}
	
	
	@Override
	public int getPositionForSection(int section) {
		for (int i = 3; i < mDlist.size(); i++) {
			String l = ((String)(mDlist.get(i).get("namePinyin"))).substring(0, 1);
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == section) {  
                return i;  
            }  
        } 
		return -1;
	}
	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
	@Override
	public Object[] getSections() {
		return null;
	}
	
	public TextView getNewNumberTxt()
	{
		return numbertxt;
	}
	
	/**
	* 转换本地图片为bitmap
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
	
	public static Bitmap getLoacalBitmap(String url,int dstWidth,int dstHeight) {
	     try {
			FileInputStream fis = new FileInputStream(url);
		
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
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
	
	public void setRoundAngleImageView(final ImageView v, String url) {
		
		if(url.indexOf("http:") >= 0)
		{
			imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						
						v.setImageDrawable(imageDrawable);
					}
				}
			},55,55);
		}
		else
		{
			Bitmap bmpimg = getLoacalBitmap(url);
			if(bmpimg != null)
			{
				bmpimg = GetRoundedCornerBitmap(bmpimg);
				v.setImageBitmap(bmpimg);
			}
		}
	}
	
	public void setRoundAngleImageView2(final ImageView v, String url) {
		
		if(url.indexOf("http:") >= 0)
		{
			imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						
						v.setImageDrawable(imageDrawable);
					}
				}
			},55,55);
		}
		else
		{
			Bitmap bmpimg = getLoacalBitmap(url);
			if(bmpimg != null)
			{
				v.setImageBitmap(bmpimg);
			}
		}
	}
	
	//生成圆角图片
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
	    try {
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);                
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight());       
	        final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight()));
	        final float roundPx = 8;
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(Color.BLACK);       
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));            
	 
	        final Rect src = new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight());
	         
	        canvas.drawBitmap(bitmap, src, rect, paint);   
	        return output;
	    } catch (Exception e) {        
	        return bitmap;
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
            int id = mContext.getResources().getIdentifier(mContext.getPackageName()+":drawable/"+source,null,null);
            // 根据id从资源文件中获取图片对象
            Drawable d = mContext.getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
}
