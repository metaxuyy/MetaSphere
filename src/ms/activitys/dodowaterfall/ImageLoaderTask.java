package ms.activitys.dodowaterfall;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import ms.globalclass.listviewadapter.AsyncImageLoader;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageLoaderTask extends AsyncTask<TaskParam, Void, Bitmap> {

	private TaskParam param;
	private final WeakReference<ImageView> imageViewReference; // 防止内存溢出
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private ImageView img;

	public ImageLoaderTask(ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		img = imageView;
	}
	

	@Override
	protected Bitmap doInBackground(TaskParam... params) {

		param = params[0];
//		return loadImageFile(param.getFilename(), param.getAssetManager());
		Bitmap bitm = returnBitMap(param.getUrlstr(),param.getNotimg(),param.getItemWidth(),param.getHig());
		if(bitm == null)
			bitm = param.getNotimg();
		return bitm;
	}

	private Bitmap loadImageFile(final String filename,
			final AssetManager manager) {
		InputStream is = null;
		try {
			
			Bitmap bmp = BitmapCache.getInstance().getBitmap(filename,
					param.getAssetManager());
			return bmp;
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Bitmap returnBitMap(String url,Bitmap bmp,int wit,int hig) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		if(url != null && !url.equals(""))
		{
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
				
				BitmapFactory.Options opt = new BitmapFactory.Options();  
			    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
			    opt.inPurgeable = true;  
			    opt.inInputShareable = true;  
			    opt.inSampleSize = 2;
			    
				bitmap = BitmapFactory.decodeStream(is,null,opt);
				if(hig > 0)
					bitmap = Bitmap.createScaledBitmap(bitmap,wit,hig,true);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try{
			    bitmap = bmp; 
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				if (bitmap != null) {
					int width = bitmap.getWidth();// 获取真实宽高
					int height = bitmap.getHeight();
					LayoutParams lp = imageView.getLayoutParams();
					lp.height = (height * param.getItemWidth()) / width;// 调整高度

					imageView.setLayoutParams(lp);

					imageView.setImageBitmap(bitmap);

				}

			}
		}
	}
}