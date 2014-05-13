package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ms.activitys.R;
import ms.activitys.hotel.SelImageFolderActivity;
import ms.globalclass.StaggeredGridView.ImageLocalLoader;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFolderAdapter extends BaseAdapter {
	
	private Map<Integer,List<WeakReference<Bitmap>>> dateCache = new HashMap<Integer,List<WeakReference<Bitmap>>>();//图片资源缓存
	private Map<Integer,List<Map<String,Object>>> imgmap = new HashMap<Integer,List<Map<String,Object>>>();//图片控件缓存
	private MyApp myapp;
	private boolean SCROLL_STATE_TOUCH_SCROLL = false;//是否正在滚动
	ExecutorService executorService;
	private Map<Integer,ImageView> imagelist = new HashMap<Integer,ImageView>();
//	private ImageLocalLoader mLoader;
	
	public boolean isSCROLL_STATE_TOUCH_SCROLL() {
		return SCROLL_STATE_TOUCH_SCROLL;
	}
	public void setSCROLL_STATE_TOUCH_SCROLL(boolean sCROLL_STATE_TOUCH_SCROLL) {
		SCROLL_STATE_TOUCH_SCROLL = sCROLL_STATE_TOUCH_SCROLL;
	}

	private class GridHolder {
		ImageView folderImg;
		TextView folderNum;
		TextView folderName;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public ImageFolderAdapter(Context c,MyApp myapp) {
		super();
		this.context = c;
		this.myapp = myapp;
		executorService=Executors.newFixedThreadPool(15);
//		mLoader = new ImageLocalLoader(context);
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	
	@Override
	public int getViewTypeCount() {
		//因为有三种视图，所以返回3
		return 1;
	}
	
	public ImageView getImageView(int index)
	{
		return imagelist.get(index);
	}
	
//	public void removeAllResources()
//	{
//		try{
//		    for(Map.Entry<Integer, List<WeakReference<Bitmap>>> entry : dateCache.entrySet())   
//		    {   
//		        List<WeakReference<Bitmap>> wrfs = entry.getValue();
//		        for(int i=0;i<wrfs.size();i++)
//		        {
//		        	WeakReference<Bitmap> wrf = wrfs.get(i);
//			        if(wrf != null)
//			        {
//			        	Bitmap dbitmap = wrf.get();
//						if(dbitmap != null)
//						{
//							if (!dbitmap.isRecycled()) {  
//								dbitmap.recycle();
//							}
//						}
//			        }
//		        }
//		    }
//		    dateCache = null;
//		    
//		    System.gc();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//	
//	public void removeImageView(int start,int end)
//	{
//		try{
//			int start2 = start;
//			int end2 = end;
//			start = start - 1;
//			end = end + 2;
//			List<WeakReference<Bitmap>> delBitmaps;
//			List<WeakReference<Bitmap>> delBitmaps2;
//			for (int del = 0; del < start; del++) {
//				delBitmaps = dateCache.get(del);
//				List<Map<String,Object>> mapslist = imgmap.get(del);
//				if (delBitmaps != null && delBitmaps.size() > 0) {
//					for(int k=0;k<delBitmaps.size();k++)
//					{
//						WeakReference<Bitmap> delBitmap = delBitmaps.get(k);
//					// 如果非空则表示有缓存的bitmap，需要清理
//					// 从缓存中移除该del->bitmap的映射
//						if(delBitmap != null)
//						{
//							if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {  
//								Map<String,Object> maps = mapslist.get(k);
//								if(maps != null)
//								{
//									ImageView imgview = (ImageView)maps.get("view");
//									imgview.setImageBitmap(null);
////									imgview.setImageResource(R.color.list_item_background);
//								}
//								delBitmap.get().recycle();
//							}
//						}
//					}
//					dateCache.remove(del);
//				}
//			}
//			
//			for (int del = end + 1; del < getCount(); del++) {
//				delBitmaps2 = dateCache.get(del);
//				List<Map<String,Object>> mapslist = imgmap.get(del);
//				if (delBitmaps2 != null && delBitmaps2.size() > 0) {
//					for(int k=0;k<delBitmaps2.size();k++)
//					{
//						WeakReference<Bitmap> delBitmap2 = delBitmaps2.get(k);
//						if(delBitmap2 != null)
//						{
//							// 如果非空则表示有缓存的bitmap，需要清理
//							// 从缓存中移除该del->bitmap的映射
//							if (delBitmap2.get() != null && !delBitmap2.get().isRecycled()) {  
//								Map<String,Object> maps = mapslist.get(k);
//								if(maps != null)
//								{
//									ImageView imgview = (ImageView)maps.get("view");
//									imgview.setImageBitmap(null);
////									imgview.setImageResource(R.color.list_item_background);
//								}
//								delBitmap2.get().recycle();
//							}
//						}
//					}
//					dateCache.remove(del);
//				}
//			}
//			
//			loadVisibleItemImageData(start2-1,end2+2);
//			
//			System.gc();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//	      
//	private void loadVisibleItemImageData(int sart,int end)
//	{
//		try{
//			
//			for(int i = sart;i<end-1;i++)
//			{
//				List<WeakReference<Bitmap>> bitmaplist = new ArrayList<WeakReference<Bitmap>>();
//				WeakReference<Bitmap> bitmap = null;
//				List<Map<String,Object>> mapslist = imgmap.get(i);
//				if(mapslist != null && mapslist.size() > 0)
//				{
//					if(mapslist.size() == 1)
//					{
//						for(int j=0;j<mapslist.size();j++)
//						{
//							Map<String,Object> maps = mapslist.get(j);
//							if(maps != null)
//							{
//								ImageView imgview = (ImageView)maps.get("view");
//								String data = (String)maps.get("value");
////								bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap2(data,100,100,true));
////								bitmaplist.add(bitmap);
//////								imgview.setImageBitmap(null);
////								if(bitmap.get() != null)
////								{
////									imgview.setImageBitmap(bitmap.get());
////								}
////								else
////								{
////									imgview.setImageResource(R.drawable.image_download_fail_icon);
////								}
////		//						System.out.println("sart==重新赋值");
//								loadImage(imgview,(String) data,i);
//							}
//						}
//					}
//					else
//					{
//						for(int j=0;j<mapslist.size();j++)
//						{
//							Map<String,Object> maps = mapslist.get(j);
//							if(maps != null)
//							{
//								ImageView imgview = (ImageView)maps.get("view");
//								String data = (String)maps.get("value");
////								bitmap = new WeakReference<Bitmap>(myapp.getLoacalBitmap2(data,100,100,true));
////								bitmaplist.add(bitmap);
//////								imgview.setImageBitmap(null);
////								if(bitmap.get() != null)
////								{
////									imgview.setImageBitmap(bitmap.get());
////								}
////								else
////								{
////									imgview.setImageResource(R.drawable.image_download_fail_icon);
////								}
////		//						System.out.println("sart==重新赋值");
//								loadImage(imgview,(String) data,i);
//							}
//						}
//					}
////					dateCache.put(i, bitmaplist);
//					imgmap.put(i, mapslist);
//				}
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_imagefolder_item,
					null);
			holder = new GridHolder();
			holder.folderImg = (ImageView) convertView
					.findViewById(R.id.folderImg);
			holder.folderNum = (TextView) convertView
					.findViewById(R.id.folderNum);
			holder.folderName = (TextView) convertView
					.findViewById(R.id.folderName);
			convertView.setTag(holder);
		} else {
			holder = (GridHolder) convertView.getTag();
		}
		
		
//		//分类图片和附件
//		List<Map<String,Object>> maplist = maplist = new ArrayList<Map<String,Object>>();
//		List<WeakReference<Bitmap>> bitmaplist = new ArrayList<WeakReference<Bitmap>>();
		imagelist.put(index, holder.folderImg);
		Map<String, Object> info = list.get(index);
		if (info != null) {
			holder.folderNum.setText((String) info.get("folderNum"));
			holder.folderName.setText((String) info.get("folderName"));
			holder.folderImg.setImageBitmap(null);
			
//			String path = (String) info.get("folderImg");
			((SelImageFolderActivity) context).c++;
			if (((SelImageFolderActivity) context).isFirstLoad && (((SelImageFolderActivity) context).c < 30)){
				((SelImageFolderActivity) context).loadImage(
						(String) info.get("folderImg"), holder.folderImg, index);
			}
				
		}
		return convertView;
	}
	
//	public void setViewImage2(final ImageView v, final String furl2,final int position) {
//		v.setImageBitmap(null);
//		imageLoader.loadBitmap3(furl2, new AsyncImageLoaderMoments.ImageCallback2() {
//			public void imageLoaded(Bitmap imageDrawable2, String imageUrl) {
//				if (imageDrawable2 != null) {
//					List<Map<String,Object>> maplist = imgmap.get(position);
//					if(maplist == null)
//						maplist = new ArrayList<Map<String,Object>>();
//					List<WeakReference<Bitmap>> bitmaplist = dateCache.get(position);
//					if(bitmaplist == null)
//						bitmaplist = new ArrayList<WeakReference<Bitmap>>();
//					
//					Map<String,Object> map = new HashMap<String,Object>();
//					map.put("view", v);
//					map.put("value", furl2);
//					maplist.add(map);
//
//			        if(SCROLL_STATE_TOUCH_SCROLL)
//			        	v.setImageResource(R.color.list_item_background);
//			        else
//			        {
//			        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(imageDrawable2);
//			        	bitmaplist.add(bitmap);
//			        	v.setImageBitmap(bitmap.get());
//			        }
//			        
//			        imgmap.put(position, maplist);
//			        dateCache.put(position, bitmaplist);
//				}
//				else
//				{
//					v.setImageResource(R.drawable.image_download_fail_icon);
//				}
//			}
//		}, 100, 100,true);
//	}
	
    /** 
     * 异步加载图片 
     * @param curr_item 
     */  
    private void loadImage(final ImageView v, final String furl2,final int position) {  
        executorService.submit(new Runnable() {  
            public void run() {  
                try {  
                	Bitmap imageDrawable2 = getLoacalBitmap2(furl2,true);
                	if (imageDrawable2 != null) {
                		imageDrawable2 = myapp.adaptive(imageDrawable2, 100, 100);
    					List<Map<String,Object>> maplist = imgmap.get(position);
    					if(maplist == null)
    						maplist = new ArrayList<Map<String,Object>>();
    					List<WeakReference<Bitmap>> bitmaplist = dateCache.get(position);
    					if(bitmaplist == null)
    						bitmaplist = new ArrayList<WeakReference<Bitmap>>();
    					
    					Map<String,Object> map = new HashMap<String,Object>();
    					map.put("view", v);
    					map.put("value", furl2);
    					maplist.add(map);

    			        if(SCROLL_STATE_TOUCH_SCROLL)
    			        	v.setImageResource(R.color.list_item_background);
    			        else
    			        {
    			        	WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(imageDrawable2);
    			        	bitmaplist.add(bitmap);
    			        	v.setImageBitmap(bitmap.get());
    			        }
    			        
    			        imgmap.put(position, maplist);
    			        dateCache.put(position, bitmaplist);
    				}
    				else
    				{
    					v.setImageResource(R.drawable.image_download_fail_icon);
    				}
                } catch (Exception e) {  
                    throw new RuntimeException(e);  
                }  
            }  
        });  
    }
	
	public Bitmap getLoacalBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			FileInputStream fis = new FileInputStream(pathString);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 5;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;

			bitmap = BitmapFactory.decodeStream(fis, null, opts);
			// bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
			// dstHeight,true);
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	public static Bitmap getLoacalBitmap2(String url,boolean b) {
		Bitmap bitmap = null;
	     try {
			FileInputStream fis = new FileInputStream(url);
			if(fis.available() > 0)
			{
				BitmapFactory.Options opts = new BitmapFactory.Options();
				
				opts.inSampleSize = 4;
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				
				bitmap = BitmapFactory.decodeStream(fis,null,opts);
//				if(b)
//				{
//					bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
//				}
			}
	     } catch (Exception e) {
	          e.printStackTrace();
	          return null;
	     }
	     return bitmap;
	}
}
