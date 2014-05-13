package ms.globalclass.listviewadapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class AsyncImageLoaderMoments {
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private Map<String, SoftReference<Bitmap>> imageCache2 = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();// �̳߳�  
	
	private int dstWidth = 480;
	
	private int dstHeight = 960;
	private boolean b;

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback callback,int dw,int dh,boolean start) {
		dstWidth = dw;
		dstHeight = dh;
		b = start;
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
	
	public Drawable loadDrawable2(final String imageUrl,
			final ImageCallback callback,int dw,int dh,boolean start) {
		dstWidth = dw;
		dstHeight = dh;
		b = start;
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
			                  
			                 Drawable drawable = loadImageFromUrl2(imageUrl);            //ʹ������ķ�����ȡDrawable  
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
			final ImageCallback2 callback,int dw,int dh,boolean start) {
		dstWidth = dw;
		dstHeight = dh;
		b = start;
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
		                  
			          if (imageCache2.containsKey(imageUrl)) {       
			              SoftReference<Bitmap> softReference = imageCache2.get(imageUrl);  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                 Bitmap bitmap = getLoacalBitmap(imageUrl,b);
			                 if(!b)
			                	 bitmap = adaptive(bitmap);
//			                 Drawable drawable = loadImageFromUrl(imageUrl);           
			                 imageCache2.put(imageUrl, new SoftReference<Bitmap>(bitmap)); 
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
	
	public Bitmap loadBitmap2(final String imageUrl,
			final ImageCallback2 callback,int dw,int dh,boolean start) {
		dstWidth = dw;
		dstHeight = dh;
		b = start;
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
		                  
			          if (imageCache2.containsKey(imageUrl)) {       
			              SoftReference<Bitmap> softReference = imageCache2.get(imageUrl);  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                 Bitmap bitmap = returnBitMap2(imageUrl);
			                 if(bitmap != null)
			                 {
				                 if(!b)
				                	 bitmap = adaptive(bitmap,dstWidth,dstHeight);
	//			                 Drawable drawable = loadImageFromUrl(imageUrl);           
				                 imageCache2.put(imageUrl, new SoftReference<Bitmap>(bitmap)); 
			                 }
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
	
	public Bitmap loadBitmap3(final String imageUrl,
			final ImageCallback2 callback,int dw,int dh,boolean start) {
		dstWidth = dw;
		dstHeight = dh;
		b = start;
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
		                  
			          if (imageCache2.containsKey(imageUrl)) {       
			              SoftReference<Bitmap> softReference = imageCache2.get(imageUrl);  
			              if (softReference.get() != null) {      
			                  Message message = handler.obtainMessage(0, softReference.get());    
			                  handler.sendMessage(message);  
			                  
			              }  
			          }else{  
			                 Bitmap bitmap = getLoacalBitmap2(imageUrl,b);
			                 if(!b)
			                	 bitmap = adaptive(bitmap);
//			                 Drawable drawable = loadImageFromUrl(imageUrl);           
			                 imageCache2.put(imageUrl, new SoftReference<Bitmap>(bitmap)); 
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
//			drawable = zoomDrawable(drawable,dstWidth,dstHeight);
			if(b)
			{
				Bitmap bitmimg = null;
				if(drawable != null)
				{
					bitmimg = ((BitmapDrawable)drawable).getBitmap();
					bitmimg = adaptive(bitmimg, dstWidth, dstHeight);
					bitmimg = adaptive(bitmimg);
					drawable = new BitmapDrawable(bitmimg);
				}
			}
			return drawable;
							
		} catch (Exception e) {

			// throw new RuntimeException(e);
			return  Drawable.createFromStream(null,"src");

		}
	}
	
	protected Drawable loadImageFromUrl2(String imageUrl) {
		try {
			Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(),
			"src");// ��URL����ȷ�������Ӳ��ϡ�new
			// URL(imageUrl).openStream()���״�������Ҫ���״��ʱ�򷵻�NULL��
//			drawable = zoomDrawable(drawable,dstWidth,dstHeight);
			if(b)
			{
				Bitmap bitmimg = null;
				if(drawable != null)
				{
					bitmimg = ((BitmapDrawable)drawable).getBitmap();
					bitmimg = Bitmap.createScaledBitmap(bitmimg, dstWidth, dstHeight,true);
					drawable = new BitmapDrawable(bitmimg);
				}
			}
			return drawable;
							
		} catch (Exception e) {

			// throw new RuntimeException(e);
			return  Drawable.createFromStream(null,"src");

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
	
	// �ȱ�������
	public Bitmap adaptive(Bitmap bgimage, int newWidth, int newHeight) {
		if (newHeight > 960) {
			newWidth = 720;
			newHeight = 960;
		}

		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();

		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		if (bgimage.getWidth() > bgimage.getHeight())
			matrix.postScale(scaleWidth, scaleWidth);
		else
			matrix.postScale(scaleHeight, scaleHeight);
		int x = 0;
		int y = 0;
		bgimage = Bitmap.createBitmap(bgimage, x, y, width, height, matrix,
				true);
		return compressImage(bgimage, 50);
	}

	// �ȱ�������
	public Bitmap adaptive(Bitmap bitmap) {
		int newWidth = 150;
		int newHeight = 150;
		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		if (bitmap.getWidth() > bitmap.getHeight())
			matrix.postScale(scaleHeight, scaleHeight);
		else
			matrix.postScale(scaleWidth, scaleWidth);
		int x = 0;
		int y = 0;
		bitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
		return compressImage(bitmap, 10);
	}

	private Bitmap compressImage(Bitmap image, int options) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		// int options = 100;
		// while ( baos.toByteArray().length / 1024>100) {
		// //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
		// baos.reset();//����baos�����baos
		// image.compress(Bitmap.CompressFormat.JPEG, options,
		// baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
		// options -= 10;//ÿ�ζ�����10
		// }
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
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
	
	public static Bitmap returnBitMap2(String url) {
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
