package ms.globalclass.listviewadapter;

import java.io.FileInputStream;
import java.io.IOException;
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

public class AsyncImageLoader {
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private Map<String, SoftReference<Bitmap>> imageCache2 = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();// �̳߳�  
	
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
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
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
		                  
			          if (imageCache.containsKey(imageUrl)) {       //��黺��imageCache�Ƿ���ڶ�Ӧ��KEY  
			              SoftReference<Drawable> softReference = imageCache.get(imageUrl);   //���ھͻ�ȡ��Ӧ��ֵ  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                  
			        	     Drawable drawable = loadImageFromUrl(imageUrl);            //ʹ������ķ�����ȡDrawable  
			                 imageCache.put(imageUrl, new SoftReference<Drawable>(drawable)); //��ͼƬ�ŵ�HasMap��  
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
	
	public Bitmap loadBitmap(final String imageUrl,
			final ImageCallback2 callback,int dw,int dh) {
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
		                  
			          if (imageCache2.containsKey(imageUrl)) {       //��黺��imageCache�Ƿ���ڶ�Ӧ��KEY  
			              SoftReference<Bitmap> softReference = imageCache2.get(imageUrl);   //���ھͻ�ȡ��Ӧ��ֵ  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                  
			                 Bitmap drawable = returnBitMap(imageUrl);            //ʹ������ķ�����ȡDrawable  
			                 imageCache2.put(imageUrl, new SoftReference<Bitmap>(drawable)); //��ͼƬ�ŵ�HasMap��  
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
        Bitmap oldbmp = drawableToBitmap(drawable); // drawable ת���� bitmap
        Matrix matrix = new Matrix();   // ��������ͼƬ�õ� Matrix ����
        float scaleWidth = ((float)w / width);   // �������ű���
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidth, scaleHeight);         // �������ű���
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // �����µ� bitmap ���������Ƕ�ԭ bitmap �����ź��ͼ
        return new BitmapDrawable(newbmp);       // �� bitmap ת���� drawable ������
    }
	
	private static Bitmap drawableToBitmap(Drawable drawable) // drawable ת���� bitmap
    {
		int width = drawable.getIntrinsicWidth(); // ȡ drawable �ĳ���
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // ȡ drawable ����ɫ��ʽ
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // ������Ӧ
																	// bitmap
		Canvas canvas = new Canvas(bitmap); // ������Ӧ bitmap �Ļ���
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // �� drawable ���ݻ���������
		return bitmap;
    }

	//���巽������URL��ȡ��������Ȼ��ת����Drawable  
	protected Drawable loadImageFromUrl(String imageUrl) {
		try {
			Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(),
			"src");// ��URL����ȷ�������Ӳ��ϡ�new
			// URL(imageUrl).openStream()���״�������Ҫ���״��ʱ�򷵻�NULL��
			drawable = zoomDrawable(drawable,dstWidth,dstHeight);
			return drawable;
							
		} catch (Exception e) {

			// throw new RuntimeException(e);
			return Drawable.createFromStream(null, "src");

		}
	}
	
	/**
	* ת������ͼƬΪbitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url,boolean b) {
		Bitmap bitmap = null;
	     try {
			FileInputStream fis = new FileInputStream(url);
			if(fis.available() > 0)
			{
				BitmapFactory.Options opts = new BitmapFactory.Options();
				
				opts.inSampleSize = 1;
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				
				bitmap = BitmapFactory.decodeStream(fis,null,opts);
				if(b)
				{
					bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100,true);
				}
			}
	     } catch (Exception e) {
	          e.printStackTrace();
	          return null;
	     }
	     return bitmap;
	}
	
	public synchronized static Bitmap returnBitMap(String url) {
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
			
//			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	// call back interface
	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
	
	// call back interface
	public interface ImageCallback2 {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
