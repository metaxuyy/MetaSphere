package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.hotel.ContactActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class ContactAdapter2 extends BaseAdapter implements SectionIndexer {

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
	private int mResource2;
	private LayoutInflater mInflater;
	private MyApp myapp;
	private Map<Integer,WeakReference<Bitmap>> dateCache = new HashMap<Integer,WeakReference<Bitmap>>();//图片资源缓存
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	final int TYPE_3 = 2;
	public TextView numbertxt;
	private String isYn;
	public static FileUtils fileUtil = new FileUtils();
	
	@SuppressWarnings("unchecked")
	public ContactAdapter2(Context context,List<Map<String,Object>> data,int resource,int resource1,int resource2,String[] from, int[] to,MyApp myapp,String isYn){
//		super(context, data, resource, from, to);
		this.mContext = context;
//		this.mNicks = nicks;
//		this.mImage = userimgs;
//		this.mDlist = data;
		this.mDlist.addAll(data);
		mResource = resource;
		mResource1 = resource1;
		mResource2 = resource2;
		mFrom = from;
		mTo = to;
		this.myapp = myapp;
		this.isYn = isYn;
		//排序(实现了中英文混排)
//		List datalist = mDlist;
//		datalist.remove(0);
//		Collections.sort(mDlist, new PinyinComparator());
//		mDlist.add(1,new HashMap<String,Object>());
		
		mDlist.add(0,new HashMap<String,Object>());
		mDlist.add(0,new HashMap<String,Object>());
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
		if(position == 0)
			return TYPE_1;
		else if(position == 1)
			return TYPE_2;
//		else if(position == 2)
//			return TYPE_2;
		else
			return TYPE_3;
	}
	
	@Override
	public int getViewTypeCount() {
		//因为有三种视图，所以返回3
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		return createViewFromResource(position, convertView, parent, mResource,mResource1,mResource2);
		
		Map dataSet = mDlist.get(position);
//		String sortPinyin = (String)dataSet.get("sortPinyin");
//		String imgurl = (String)dataSet.get("imgurl");
//		String storeName = (String)dataSet.get("storeName");
		String sortPinyin = (String)dataSet.get("namePinyin");
		String imgurl = (String)dataSet.get("userimg");
		String storeName = (String)dataSet.get("username");
		String pfid = (String)dataSet.get("pfid");
		
		ViewHolder1 viewHolder1 = null;
		ViewHolder2 viewHolder2 = null;
		ViewHolder3 viewHolder3 = null;
		
		int type = getItemViewType(position);
		
		//无convertView，需要new出各个控件
		if(convertView == null){
			//按当前所需的样式，确定new的布局
			switch(type)
			{
			case TYPE_1:
				convertView = LayoutInflater.from(mContext).inflate(mResource1, null);
				viewHolder1 = new ViewHolder1();
				viewHolder1.et1 = (EditText) convertView.findViewById(R.id.editText1);
				convertView.setTag(viewHolder1);
				break;
			case TYPE_2:
				convertView = LayoutInflater.from(mContext).inflate(mResource2, null);
				viewHolder2 = new ViewHolder2();
				viewHolder2.textview = (TextView)convertView.findViewById(R.id.text_title);
				viewHolder2.numbertxt = (TextView)convertView.findViewById(R.id.new_number);
				viewHolder2.imgview = (ImageView)convertView.findViewById(R.id.img_icon);
				convertView.setTag(viewHolder2);
				break;
			case TYPE_3:
				convertView = LayoutInflater.from(mContext).inflate(mResource, null);
				viewHolder3 = new ViewHolder3();
				viewHolder3.contactitem_catalog = (TextView)convertView.findViewById(R.id.contactitem_catalog);
				viewHolder3.contactitem_nick = (TextView)convertView.findViewById(R.id.contactitem_nick);
				viewHolder3.contactitem_avatar_iv = (ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
				convertView.setTag(viewHolder3);
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
			}
		}
		
		//设置资源
		switch(type)
		{
		case TYPE_1:
			viewHolder1.et1.requestFocus();
			viewHolder1.et1.setText(ContactActivity.instance.mykey);
			viewHolder1.et1.setSelection(ContactActivity.instance.mykey.length());
			viewHolder1.et1.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String str = s.toString();
					if(str != null && !str.equals(""))
					{
						if(!str.equals(ContactActivity.instance.mykey))
						{
							ContactActivity.instance.mykey = str;
//							ContactActivity.instance.getMyStoreListDatas(str);
							ContactActivity.instance.getMyStoreListDatas(str,isYn);
						}
					}
					else
					{
						if(!str.equals(ContactActivity.instance.mykey))
						{
							ContactActivity.instance.mykey = "";
//							ContactActivity.instance.getMyStoreListDatas();
							ContactActivity.instance.getMyFriendDatas(isYn);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s,
						int start, int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case TYPE_2:
			if(position == 1)
			{
				viewHolder2.textview.setText(mContext.getString(R.string.hotel_label_99));
//				viewHolder2.numbertxt.setVisibility(View.VISIBLE);
				numbertxt = viewHolder2.numbertxt;
				int viewibility = MainTabActivity.instance.new_yanzhen_number.getVisibility();
		    	if(viewibility == View.VISIBLE)
		    	{
		    		String number = MainTabActivity.instance.new_yanzhen_number.getText().toString();
		    		viewHolder2.numbertxt.setVisibility(View.VISIBLE);
		    		viewHolder2.numbertxt.setText(number);
		    	}
				viewHolder2.imgview.setImageResource(R.drawable.new_friend_icon);
			}
			else if(position == 2)
			{
				if(isYn.equals("0"))
					viewHolder2.textview.setText(mContext.getString(R.string.hotel_label_8));
				else
					viewHolder2.textview.setText(mContext.getString(R.string.hotel_label_71));
				viewHolder2.numbertxt.setVisibility(View.GONE);
				viewHolder2.imgview.setImageResource(R.drawable.my_friend_icon);
			}
			break;
		case TYPE_3:
			String catalog = sortPinyin.substring(0, 1);
			if(position == 2){
				if(catalog.equals("1"))
				{
					viewHolder3.contactitem_catalog.setVisibility(View.VISIBLE);
					viewHolder3.contactitem_catalog.setText(mContext.getString(R.string.hotel_label_14));
				}
				else
				{
					viewHolder3.contactitem_catalog.setVisibility(View.VISIBLE);
					viewHolder3.contactitem_catalog.setText(catalog);
				}
			}else{
				if(!(mDlist.get(position-1).get("namePinyin")).equals(""))
				{
					String lastCatalog = ((String)(mDlist.get(position-1).get("namePinyin"))).substring(0, 1);
					if(catalog.equals(lastCatalog)){
						viewHolder3.contactitem_catalog.setVisibility(View.GONE);
					}else{
						viewHolder3.contactitem_catalog.setVisibility(View.VISIBLE);
						viewHolder3.contactitem_catalog.setText(catalog);
					}
				}
			}
			
			Bitmap bmpimg = null;
			WeakReference<Bitmap> current = dateCache.get(position);
			if(current == null)
			{
				if(imgurl != null && !imgurl.equals(""))
				{
					if(imgurl.indexOf("http://") >= 0)
					{
						String furl = fileUtil.getImageFile2aPath(pfid, pfid);
						if(!fileUtil.isFileExist3(furl))
							setUserImage(viewHolder3.contactitem_avatar_iv,imgurl,pfid,position);
						else
						{
							bmpimg = getLoacalBitmap(furl);
							dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
						}
					}
					else
					{
						bmpimg = getLoacalBitmap(imgurl);
						dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
					}
				}
			}
			else
			{
				bmpimg = current.get();
			}
			
			if(bmpimg != null)
			{
				viewHolder3.contactitem_avatar_iv.setImageBitmap(bmpimg);
			}
//			else
//			{
//				viewHolder3.contactitem_avatar_iv.setImageResource(R.drawable.default_avatar);
//			}
			
			viewHolder3.contactitem_nick.setText(storeName);
			break;
		}
		
		return convertView;
	}
	
	static class ViewHolder1{
		EditText et1;
	}
	
	static class ViewHolder2{
		TextView textview;
		ImageView imgview;
		TextView numbertxt;
	}
	
	static class ViewHolder3{
		TextView contactitem_catalog;
		TextView contactitem_nick;
		ImageView contactitem_avatar_iv;
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
	
	public void setUserImage(final ImageView v, String url,final String pfid,final int position) {
//		v.setImageBitmap(null);
		if(url.indexOf("http:") >= 0)
		{
			imageLoader.loadBitmap(url, new AsyncImageLoader.ImageCallback2() {
				public void imageLoaded(Bitmap imageDrawable2, String imageUrl) {
					if (imageDrawable2 != null) {
						String furl = fileUtil.getImageFile2aPath(pfid, pfid);
						if(!fileUtil.isFileExist2(pfid))
							fileUtil.createUserFile(pfid);
						
						fileUtil.saveMyBitmap(furl, imageDrawable2);
						v.setImageBitmap(imageDrawable2);
						dateCache.put(position, new WeakReference<Bitmap>(imageDrawable2)); 
						if(ContactActivity.instance != null)
							ContactActivity.instance.updateFriendAllFileUrlData(pfid,furl);
					}
				}
			}, 100, 100);
		}
	}
}
