package ms.globalclass.listviewadapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.LoginMain;
import ms.activitys.MyCommentView;
import ms.activitys.RegisterAction;
import ms.globalclass.U;
import ms.globalclass.onlineplay.VideoViewDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class SpecialAdapterMessage extends SimpleAdapter{
	
	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoaderMessage imageLoader = new AsyncImageLoaderMessage();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData; // List列表存放的数据
	private int mResource; // 绑定的页面 ,例如：R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // 绑定控件对应的数组里面的值名称
	private int[] mTo; // 绑定控件的ID
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	private Context contexts;
	private String imgtype;
	private MediaPlayer mediaPlayer;
	private boolean playertag = false;
	
	public SpecialAdapterMessage(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,String type) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		imgtype = type;
		// 布局泵(LayoutInflater)根据XML布局文件来绘制视图(View)对象。这个类无法直接创建实例，要通过context对象的
		// getLayoutInflater()或getSystemService(String)方法来获得实例，这样获得的布局泵实例符合设备的环境配置。
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
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
			bitmap = BitmapFactory.decodeStream(is);
			bitmap = Bitmap.createScaledBitmap(bitmap,50,50,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/*
	 * SimpleAdapter基类显示每个Item都是通过这个方法生成的 在getView(int position, View
	 * convertView, ViewGroup
	 * parent)中又调用了SimpleAdapter的私有方法createViewFromResource
	 * 来组装View，在createViewFromResource中对SimpleAdapter的参数String[] from 和int[]
	 * to进行了组装
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = super.getView(position, convertView, parent);
//		int colorPos = position % colors.length;
//		if(colorPos == 0)
//			view.setBackgroundColor(Color.argb(220, 211, 211, 211));
//		else
//			view.setBackgroundColor(Color.argb(220, 169, 169, 169));
		
		
//		Map map = list.get(position);
//		String cname = (String)map.get("cname");
//		String imageUrl = (String)map.get("mimg");
//		
//		TextView tv = (TextView)view.findViewById(R.id.menutype);
//		tv.setText(cname);
//		
//		ImageView imgview = (ImageView)view.findViewById(R.id.mimg);
//		imgview.setImageBitmap(returnBitMap(imageUrl));  
		View view = mInflater.inflate(mResource, null);
		
		final Map map = mData.get(position);
		
		TextView conten = (TextView)view.findViewById(R.id.mymessagecontent);
		conten.setText((Spanned)map.get("content"));
		
		TextView mysendname = (TextView)view.findViewById(R.id.mysendname);
		mysendname.setText((String)map.get("sendName"));
		
		TextView mysendtime = (TextView)view.findViewById(R.id.mysendtime);
		mysendtime.setText((String)map.get("sendTime"));
		
		ImageView yiman = (ImageView)view.findViewById(R.id.yiman);
//		yiman.setImageResource(R.drawable.add_card_24);
		String userimg = (String)map.get("img");
		if(userimg != null && !userimg.equals(""))
			setViewImage(yiman,userimg,100,100);
		
		String fileimg = (String)map.get("file");
		ImageView yimanmediaTool = (ImageView)view.findViewById(R.id.mediaTool);
		
		ImageView uploadimg = (ImageView)view.findViewById(R.id.uploadFileImg);
		if(fileimg != null && !fileimg.equals(""))
		{
			setViewImage(uploadimg,fileimg,100,100);
			uploadimg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String fileUrl = (String) map.get("filepath");
					if(fileUrl != null && !fileUrl.equals(""))
					{
						showMessageDetails(fileUrl);
					}
				}
			});
		}
		else
		{
			yimanmediaTool.setVisibility(View.GONE);
			uploadimg.setVisibility(View.GONE);
		}
		
		final String lupath = (String)map.get("lupath");
		ImageView luyinfile = (ImageView)view.findViewById(R.id.luyinfile);
		if(lupath != null && !lupath.equals(""))
		{
			luyinfile.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent();
//				    intent.setClass( contexts,VideoViewDemo.class);
//				    Bundle bundle = new Bundle();
//					bundle.putString("filePath", lupath);
//					intent.putExtras(bundle);
//					contexts.startActivity(intent);//开始界面的跳转函数
//					((Activity) contexts).overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
					if(!playertag)
						Broadcast(lupath);
					else
						makeText(contexts.getString(R.string.button_record_msg2));
				}
			});
		}
		else
		{
			luyinfile.setVisibility(View.GONE);
		}
		
		return view;
		
//		return createViewFromResource(position, convertView, parent, mResource);    //调用下面方法
	}
	
	// 在createViewFromResource方法中又有一个bindView(position,
	// v)方法对item中的各个View进行了组装，bindView(position, v)
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View rowView = this.viewMap.get(position);

		if (rowView == null) {
			rowView = mInflater.inflate(resource, null);

			final int[] to = mTo;
			final int count = to.length;
			final View[] holder = new View[count];

			for (int i = 0; i < count; i++) {

				holder[i] = rowView.findViewById(to[i]);
			}
			rowView.setTag(holder);
			bindView(position, rowView); // 调用下面方法对Item中的
			viewMap.put(position, rowView);
		}
		return rowView;
	}
	
	// 对ViewImage进行组装的代码了“else if (v instanceof ImageView)”
	@SuppressWarnings("unchecked")
	private void bindView(int position, View view) {
		final Map dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = mViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = holder[i];
			if (v != null) {
				final Object data = dataSet.get(from[i]);
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
						setViewText((TextView) v, urlText);
					} else if (v instanceof ImageView) {
						if(data == null)
						{
							v.setVisibility(View.GONE);
						}
						else if(urlText == null)
						{
							v.setVisibility(View.GONE);
						}
						else
						{
							if (data instanceof Integer) {
								setViewImage((ImageView) v, (Integer) data);
							} else {
								if(from[i].equals("file"))
								{
										ImageView img = (ImageView)view.findViewById(R.id.uploadFileImg);
										img.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
//											String fileUrl = (String) dataSet.get("filepath");
//											String message = (String)dataSet.get("content");
//											if(fileUrl != null && !fileUrl.equals(""))
//											{
////												System.out.println("fileUrl=="+fileUrl);
//												showMessageDetails(fileUrl,message);
//											}
											Toast.makeText(contexts, "Icon clicked, position is:" ,
												      Toast.LENGTH_SHORT).show();
										}
									});
									setViewImage((ImageView) v, urlText,150,150);
								}
								else
									setViewImage((ImageView) v, urlText,80,80);
							}
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

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewImage(final ImageView v, String url,int width,int height) {
		// 如果只是单纯的把图片显示，而不进行缓存。直接用下面的方法拿到URL的Bitmap就行显示就OK
		// Bitmap bitmap = WebImageBuilder.returnBitMap(url);
		// ((ImageView) v).setImageBitmap(bitmap);
		if(url.indexOf("http:") >= 0)
		{
			String str = url.substring(0,url.lastIndexOf("."));
			String str2 = url.substring(url.lastIndexOf("."),url.length());
			
			if(imgtype.equals("ico"))
				url = str+"_ico"+str2;
			else if(imgtype.equals("zhong"))
				url = str+"_zhong"+str2;
		}
		
		imageLoader.loadDrawable(url, new AsyncImageLoaderMessage.ImageCallback() {
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
				if (imageDrawable != null) {
					v.setImageBitmap(imageDrawable);
				}
			}
		},width,height);
	}
	
	public void showMessageDetails(String flieUrls){
		try{
			final View view = LayoutInflater.from(contexts).inflate(R.layout.messageview,null);
			ImageView imgview = (ImageView)view.findViewById(R.id.messageImage);
			Bitmap bitmap = U.getImageBitmap(flieUrls);
			imgview.setImageBitmap(bitmap);
//			TextView tview = (TextView)view.findViewById(R.id.messageInfo);
//			tview.setText(message);
			
			final AlertDialog adialog = new AlertDialog.Builder(contexts).setView(view).show();
			
			imgview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					adialog.dismiss();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void Broadcast(String path)
	{
		try{
			if (path != null) {
				playertag = true;
				mediaPlayer = new MediaPlayer();
				// 为播放器设置数据文件 
				mediaPlayer.setDataSource(path);
				// 准备并且启动播放器
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								try
					            {
					              /*解除资源与MediaPlayer的赋值关系
					               * 让资源可以为其它程序利用*/
					              mp.release();
					              playertag = false;
					              /*改变TextView为播放结束*/
					              makeText(contexts.getString(R.string.button_record_msg));
					            }
					            catch (Exception e)
					            {
					              e.printStackTrace();
					            } 
							}
						});
				makeText(contexts.getString(R.string.record_lable_6));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
	
}
