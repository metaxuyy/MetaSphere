package ms.globalclass.listviewadapter;

import java.io.InputStream;
import java.lang.ref.SoftReference;  
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

public class AsyncImageLoaderMessage {
	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();// 线程池  
	
	private int dstWidth = 60;
	
	private int dstHeight = 60;

	public Drawable loadDrawable(final String imageUrl,
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
		                  
			          if (imageCache.containsKey(imageUrl)) {       //检查缓冲imageCache是否存在对应的KEY  
			              SoftReference<Bitmap> softReference = imageCache.get(imageUrl);   //存在就获取对应的值  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                  
			                 Bitmap drawable = getImageBitmap(imageUrl,dstWidth,dstHeight);            //使用下面的方法获取Drawable  
			                 imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable)); //把图片放到HasMap中  
			                 Message message = handler.obtainMessage(0, drawable);    
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
        Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
        Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象
        float scaleWidth = ((float)w / width);   // 计算缩放比例
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
        return new BitmapDrawable(newbmp);       // 把 bitmap 转换成 drawable 并返回
    }
	
	private static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成 bitmap
    {
		int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应
																	// bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把 drawable 内容画到画布中
		return bitmap;
    }

	//定义方法链接URL获取输入流，然后转换成Drawable  
	protected Drawable loadImageFromUrl(String imageUrl) {
		try {
			Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(),
			"src");// 当URL不正确或者链接不上。new
			// URL(imageUrl).openStream()会抛错。所以需要在抛错的时候返回NULL。
			drawable = zoomDrawable(drawable,dstWidth,dstHeight);
			return drawable;
							
		} catch (Exception e) {

			// throw new RuntimeException(e);
			return Drawable.createFromStream(null, "src");

		}
	}

	// call back interface
	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
	
	public Bitmap getImageBitmap(String value,int w,int h)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		System.out.println("menulist_imageurl==="+value);
		if(value == null)
			return null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
}
