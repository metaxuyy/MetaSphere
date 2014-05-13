package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.hotel.ContactActivity;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {

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
	
	@SuppressWarnings("unchecked")
	public ContactAdapter(Context context,List<Map<String,Object>> data,int resource,int resource1,int resource2,String[] from, int[] to,MyApp myapp){
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
		//排序(实现了中英文混排)
//		List datalist = mDlist;
//		datalist.remove(0);
//		Collections.sort(mDlist, new PinyinComparator());
//		mDlist.add(1,new HashMap<String,Object>());
		
		mDlist.add(0,new HashMap<String,Object>());
		mDlist.add(0,new HashMap<String,Object>());
		
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource,mResource1,mResource2);
	}
	
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource,int resource1,int resource2) {
		View rowView = this.viewMap.get(position);
		Map dataSet = mDlist.get(position);
		
		if (rowView == null) {
			if(position == 0)
			{
				rowView = mInflater.inflate(resource1, null);
				final EditText et1 = (EditText) rowView.findViewById(R.id.editText1);
				et1.requestFocus();
				et1.setText(ContactActivity.instance.mykey);
				et1.setSelection(ContactActivity.instance.mykey.length());
				et1.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						String str = et1.getText().toString();
						if(str != null && !str.equals(""))
						{
							if(!str.equals(ContactActivity.instance.mykey))
							{
								ContactActivity.instance.mykey = str;
								ContactActivity.instance.getMyStoreListDatas(str);
							}
						}
						else
						{
							if(!str.equals(ContactActivity.instance.mykey))
							{
								ContactActivity.instance.mykey = "";
								ContactActivity.instance.getMyStoreListDatas();
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
				viewMap.put(position, rowView);
			}
			else if(position == 1)
			{
				rowView = mInflater.inflate(resource2, null);
				viewMap.put(position, rowView);
			}
			else
			{
				rowView = mInflater.inflate(resource, null);

				int count = mTo.length;
				View[] holder = new View[count];

				for (int i = 0; i < count; i++) {

					holder[i] = rowView.findViewById(mTo[i]);
				}
				rowView.setTag(holder);
				bindView(position, rowView); // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鑺ユ柟閿熸枻鎷烽敓鏂ゆ嫹Item閿熷彨纰夋嫹
				viewMap.put(position, rowView);
			}
		}
		return rowView;
	}
	
	@SuppressWarnings("unchecked")
	private void bindView(int position, View view) {
		Map dataSet = mDlist.get(position);
		if (dataSet == null) {
			return;
		}

		ViewBinder binder = mViewBinder;
		View[] holder = (View[]) view.getTag();
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
					} else if (v instanceof TextView) {
						if(urlText != null && !urlText.equals(""))
							setViewText((TextView) v, urlText,dataSet,position);
						else
							v.setVisibility(View.GONE);
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
									setViewRoundAngleImageView((RoundAngleImageView) v, urlText,dataSet,position);
								}
							}
						}
						else
						{
//							v.setVisibility(View.GONE);
//							((RoundAngleImageView) v).setImageResource(R.drawable.default_avatar_shadow);
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
	
	public void setViewText(TextView v,String str,Map dataSet,int position)
	{
		if(str != null && !str.equals(""))
		{
			if(v.getId() == R.id.contactitem_catalog)
			{
//				String catalog = converterToFirstSpell(str).substring(0, 1);
				String catalog = str.substring(0, 1);
				if(position == 2){
					if(catalog.equals("1"))
					{
						v.setVisibility(View.VISIBLE);
						v.setText(mContext.getString(R.string.hotel_label_14));
					}
					else
					{
						v.setVisibility(View.VISIBLE);
						v.setText(catalog);
					}
				}else{
					if(!(mDlist.get(position-1).get("sortPinyin")).equals(""))
					{
//						String lastCatalog = converterToFirstSpell((String)(mDlist.get(position-1).get("sortName"))).substring(0, 1);
						String lastCatalog = ((String)(mDlist.get(position-1).get("sortPinyin"))).substring(0, 1);
						if(catalog.equals(lastCatalog)){
							v.setVisibility(View.GONE);
						}else{
							v.setVisibility(View.VISIBLE);
							v.setText(catalog);
						}
					}
				}
			}
			else
				v.setText(str);
		}
	}
	
	public void setViewRoundAngleImageView(RoundAngleImageView v, int value,View view) {
		v.setImageResource(value);
//		view.setLayoutParams( new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setViewRoundAngleImageView(RoundAngleImageView v, Bitmap value) {
		v.setImageBitmap(value);
	}
	
	public void setViewRoundAngleImageView(final RoundAngleImageView v, String url,final Map dataSet,int position) {
		// 如果只是单纯的把图片显示，而不进行缓存。直接用下面的方法拿到URL的Bitmap就行显示就OK
		if(url.indexOf("http://") >= 0)
		{
			imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						
						v.setImageDrawable(imageDrawable);
					}
				}
			},80,80);
		}
		else
		{
			Bitmap bmpimg = null;
			WeakReference<Bitmap> current = dateCache.get(position);
			if(current == null)
			{
				bmpimg = getLoacalBitmap(url);
				dateCache.put(position, new WeakReference<Bitmap>(bmpimg));  
			}
			else
			{
				bmpimg = current.get();
			}
			
			if(bmpimg != null)
				v.setImageBitmap(dateCache.get(position).get());
		}
	}
	
//	static class ViewHolder{
//		TextView tvCatalog;//目录
//		RoundAngleImageView ivAvatar;//头像
//		TextView tvNick;//昵称
//	}

	@Override
	public int getPositionForSection(int section) {
//		for (int i = 0; i < mNicks.length; i++) {  
//            String l = converterToFirstSpell(mNicks[i]).substring(0, 1);  
//            char firstChar = l.toUpperCase().charAt(0);  
//            if (firstChar == section) {  
//                return i;  
//            }  
//        } 
		for (int i = 2; i < mDlist.size(); i++) {
//            String l = converterToFirstSpell((String)(mDlist.get(i).get("sortName"))).substring(0, 1);  
			String l = ((String)(mDlist.get(i).get("sortPinyin"))).substring(0, 1);
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
	
	public void setImageView(final RoundAngleImageView v, String url) {
		// 如果只是单纯的把图片显示，而不进行缓存。直接用下面的方法拿到URL的Bitmap就行显示就OK
//		if(url.indexOf("http:") >= 0)
//		{
//			String str = url.substring(0,url.lastIndexOf("."));
//			String str2 = url.substring(url.lastIndexOf("."),url.length());
//			
//			url = str+"_zhong"+str2;
//		}
		
		imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
					
					v.setImageDrawable(imageDrawable);
				}
			}
		},80,80);
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
}
