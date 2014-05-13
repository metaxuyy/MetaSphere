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
		                  
			          if (imageCache.containsKey(imageUrl)) {       //��黺��imageCache�Ƿ���ڶ�Ӧ��KEY  
			              SoftReference<Bitmap> softReference = imageCache.get(imageUrl);   //���ھͻ�ȡ��Ӧ��ֵ  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                  
			                 Bitmap drawable = getImageBitmap(imageUrl,dstWidth,dstHeight);            //ʹ������ķ�����ȡDrawable  
			                 imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable)); //��ͼƬ�ŵ�HasMap��  
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
