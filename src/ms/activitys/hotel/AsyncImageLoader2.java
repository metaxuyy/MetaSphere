package ms.activitys.hotel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;  
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;  
import android.os.Handler;  
import android.os.Message;  

public class AsyncImageLoader2 {
	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();// 閿熺绋嬬鎷� 
	
	private int dstWidth = 60;
	
	private int dstHeight = 60;

	public Bitmap loadDrawable(final String imageUrl,
			final ImageCallback callback,int dw,int dh) {
		dstWidth = dw;
		dstHeight = dh;
//		if (imageCache.containsKey(imageUrl)) {
//			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
//			if (softReference.get() != null) {
//				return softReference.get();
//			}
//		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl);
			}
		};
		// load data
//		new Thread() {
//			public void run() {
//				Drawable drawable = loadImageFromUrl(imageUrl);
//				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
//				handler.sendMessage(handler.obtainMessage(0, drawable));
//			};
		    try {     
		        threadPool.execute(new Runnable() {     
		      
		            @Override    
		            public synchronized  void run() {     
		                  
			          if (imageCache.containsKey(imageUrl)) {       
			              SoftReference<Bitmap> softReference = imageCache.get(imageUrl);   
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                 Bitmap bitmap = returnBitMap(imageUrl); 
//			                 Drawable drawable = loadImageFromUrl(imageUrl);            
			                 imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap)); 
			                 Message message = handler.obtainMessage(0, bitmap);    
			                 handler.sendMessage(message);  
			           }     
			          }  
		        });     
		    } catch (Exception e) {     
		        e.printStackTrace();     
		    }   
//		}.start();

		return null;
	}
	
	private static Drawable zoomDrawable(Drawable drawable, int w, int h)
    {
        int width = drawable.getIntrinsicWidth();
        int height= drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float)w / width);
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(newbmp);
    }
	
	private static Bitmap drawableToBitmap(Drawable drawable)
    {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
																
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
    }

	protected Drawable loadImageFromUrl(String imageUrl) {
		try {
			Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(),"src");
			return drawable;
							
		} catch (Exception e) {

			// throw new RuntimeException(e);
			return Drawable.createFromStream(null, "src");

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
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int newwidth = 0;
			int newheight = 140;
			if(height > newheight || height < newheight)
			{
				BigDecimal b = new BigDecimal((float)height / (float)width);  
				float bili = b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();;
				newwidth = (int)(newheight/bili);
				if(newwidth >= dstWidth)
					bitmap = Bitmap.createScaledBitmap(bitmap,newwidth,newheight,true);
				else
				{
					newwidth = dstWidth;
					BigDecimal wb = new BigDecimal((float)width / (float)height);
					float wbili = wb.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
					newheight = (int)(newwidth/wbili);
					bitmap = Bitmap.createScaledBitmap(bitmap,newwidth,newheight,true);
				}
			}
			
			int mScreenWidth = (bitmap.getWidth() - dstWidth) / 2;
			int mScreenHeight = (bitmap.getHeight() - dstHeight) / 2;
			if(mScreenWidth < 0)
				mScreenWidth = 0;
			if(mScreenHeight < 0)
				mScreenHeight = 0;
			bitmap = Bitmap.createBitmap(bitmap, mScreenWidth, mScreenHeight, dstWidth, dstHeight);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	// 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
       // 获得图片的宽高
       int width = bm.getWidth();
       int height = bm.getHeight();
       // 计算缩放比例
       float scaleWidth = ((float) newWidth) / width;
       float scaleHeight = ((float) newHeight) / height;
       // 取得想要缩放的matrix参数
       Matrix matrix = new Matrix();
       matrix.postScale(scaleWidth, scaleHeight);
       // 得到新的图片
       Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    } 

	// call back interface
	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
