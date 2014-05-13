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
	private List<Map<String,Object>> mData; // List�б��ŵ�����
	private int mResource; // �󶨵�ҳ�� ,���磺R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // �󶨿ؼ���Ӧ�����������ֵ����
	private int[] mTo; // �󶨿ؼ���ID
	
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
		// ���ֱ�(LayoutInflater)����XML�����ļ���������ͼ(View)����������޷�ֱ�Ӵ���ʵ����Ҫͨ��context�����
		// getLayoutInflater()��getSystemService(String)���������ʵ����������õĲ��ֱ�ʵ�������豸�Ļ������á�
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
	 * SimpleAdapter������ʾÿ��Item����ͨ������������ɵ� ��getView(int position, View
	 * convertView, ViewGroup
	 * parent)���ֵ�����SimpleAdapter��˽�з���createViewFromResource
	 * ����װView����createViewFromResource�ж�SimpleAdapter�Ĳ���String[] from ��int[]
	 * to��������װ
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
		
//		return createViewFromResource(position, convertView, parent, mResource);    //�������淽��
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
	
	// ��createViewFromResource����������һ��bindView(position,
	// v)������item�еĸ���View��������װ��bindView(position, v)
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
			bindView(position, rowView); // �������淽����Item�е�
			viewMap.put(position, rowView);
		}
		return rowView;
	}
	
	// ��ViewImage������װ�Ĵ����ˡ�else if (v instanceof ImageView)��
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
		// ���ֻ�ǵ����İ�ͼƬ��ʾ���������л��档ֱ��������ķ����õ�URL��Bitmap������ʾ��OK
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
				// Ϊ���������������ļ� 
				mediaPlayer.setDataSource(path);
				// ׼����������������
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								try
					            {
					              /*�����Դ��MediaPlayer�ĸ�ֵ��ϵ
					               * ����Դ����Ϊ������������*/
					              mp.release();
					              playertag = false;
					              /*�ı�TextViewΪ���Ž���*/
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
             //ʵ����
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //���ý�������񣬷��ΪԲ�Σ���ת��
//             mypDialog.setTitle("�ȴ�");
             //����ProgressDialog ����
             mypDialog.setMessage(str);
             //����ProgressDialog ��ʾ��Ϣ
//             mypDialog.setIcon(R.drawable.wait_icon);
             //����ProgressDialog ����ͼ��
//             mypDialog.setButton("",this);
             //����ProgressDialog ��һ��Button
             mypDialog.setIndeterminate(false);
             //����ProgressDialog �Ľ������Ƿ���ȷ
             mypDialog.setCancelable(true);
             //����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
             mypDialog.show();
             //��ProgressDialog��ʾ
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
}
