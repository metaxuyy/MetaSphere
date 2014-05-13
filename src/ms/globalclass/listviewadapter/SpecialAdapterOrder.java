package ms.globalclass.listviewadapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.orders.MyOrederListActivity;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class SpecialAdapterOrder extends BaseAdapter{
	
	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoaderMessage imageLoader = new AsyncImageLoaderMessage();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private ViewBinder mViewBinder;
	private List<Map<String,Object>> mData; // List列表存放的数据
	private int mResource; // 绑定的页面 ,例如：R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // 绑定控件对应的数组里面的值名称
	private int[] mTo; // 绑定控件的ID
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	private Context contexts;
	private MyOrederListActivity orderlist;
	private String imgtype;
	private MediaPlayer mediaPlayer;
	private boolean playertag = false;
	private Douban api;
	private MyApp myapp;
	private String storeid;
	private ProgressDialog mypDialog;
	private String ordertag;
	public int count = 0;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void addMoreData(List<Map<String, Object>> adddata)
	{
//	  this.mData.addAll(adddata);
	  this.count = mData.size();
	  this.notifyDataSetChanged();
	}
	
	public void delMoreData(List<Map<String, Object>> adddata)
	{
	  this.count = adddata.size();
	  this.notifyDataSetChanged();
	}
	
	public SpecialAdapterOrder(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,String type,Douban douban,MyApp mapp,String appliescStoreid,String otag) {
//		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = (List<Map<String,Object>>)data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		count = data.size();
		orderlist = (MyOrederListActivity) context;
		imgtype = type;
		api = douban;
		myapp = mapp;
		storeid = appliescStoreid;
		ordertag = otag;
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
		View view = mInflater.inflate(mResource, null);
		
		final Map map = mData.get(position);
		final int mapindex = position;
		
		TextView textOrderID = (TextView)view.findViewById(R.id.textOrderID);
		textOrderID.setText((String)map.get("orderNo"));
		
		TextView textPrice = (TextView)view.findViewById(R.id.textPrice);
		textPrice.setText((String)map.get("totalPrice"));
		
		TextView textTime = (TextView)view.findViewById(R.id.textTime);
		textTime.setText((String)map.get("orderTime"));
		
		TextView textState = (TextView)view.findViewById(R.id.textState);
		textState.setText((String)map.get("orderStatus"));
		
		TextView textZhiFuBaoPay = (TextView)view.findViewById(R.id.textZhiFuBaoPay);
		textZhiFuBaoPay.setText((String)map.get("paymentvalue"));
		
		if(ordertag.equals("handle"))
		{
			String payment = (String)map.get("payment");
			TextView textZhiFuBao = (TextView)view.findViewById(R.id.textZhiFuBao);
			
			String paycode = (String)map.get("paycode");
			if(payment.equals("3"))
			{
				if(paycode.equals(""))
				{
					textZhiFuBao.setVisibility(View.VISIBLE);
					textZhiFuBao.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
			
			TextView textCancelOrder = (TextView)view.findViewById(R.id.textCancelOrder);
			textCancelOrder.setVisibility(View.VISIBLE);
			textCancelOrder.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showProgressDialog(contexts.getString(R.string.menu_lable_150));
					String orderid = (String)map.get("orderid");
					cancelOrder(orderid,mapindex);
				}
			});
		}
		return view;
		
//		return createViewFromResource(position, convertView, parent, mResource);    //调用下面方法
	}
	
	public void cancelOrder(final String orderid,final int position)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try{
					JSONObject jobj = api.getOrderStatusChange(orderid,"cancel",storeid);
					if(jobj.has("error"))
					{
						msg.obj = null;
					}
					else
					{
						msg.obj = position;
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(msg.obj != null)
				{
					int position = (Integer)msg.obj;
					mData.remove(position);
					delMoreData(mData);
					makeText(contexts.getString(R.string.menu_lable_152));
				}
				else
				{
					makeText(contexts.getString(R.string.menu_lable_151));
				}
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
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
	
	private void setViewText(TextView v, String urlText) {
		// TODO Auto-generated method stub
		v.setText(urlText);
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
	
	public void showProgressDialog(String str){
 		try{
 			mypDialog=new ProgressDialog(contexts);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(str);
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
             //设置ProgressDialog 的一个Button
             mypDialog.setIndeterminate(false);
             //设置ProgressDialog 的进度条是否不明确
             mypDialog.setCancelable(true);
             //设置ProgressDialog 是否可以按退回按键取消
             mypDialog.show();
             //让ProgressDialog显示
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
}
