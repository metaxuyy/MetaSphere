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
import ms.activitys.product.AddUpdateAddress;
import ms.activitys.product.ReceivingAddressActivity;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class SpecialAdapterAddress extends SimpleAdapter{
	
	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	
	private AsyncImageLoaderMessage imageLoader = new AsyncImageLoaderMessage();
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData; // List�б��ŵ�����
	private int mResource; // �󶨵�ҳ�� ,���磺R.layout.search_item,
	private LayoutInflater mInflater;
	private String[] mFrom; // �󶨿ؼ���Ӧ�����������ֵ����
	private int[] mTo; // �󶨿ؼ���ID
	
	private int dstWidth = 100;
	
	private int dstHeight = 100;
	private Context contexts;
	private String imgtype;
	private MediaPlayer mediaPlayer;
	private boolean playertag = false;
	
	public SpecialAdapterAddress(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,String type) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		contexts = context;
		imgtype = type;
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
		
		TextView textName = (TextView)view.findViewById(R.id.textName);
		textName.setText((String)map.get("consignee"));
		
		TextView textCity = (TextView)view.findViewById(R.id.textCity);
		textCity.setText((String)map.get("textCity"));
		
		TextView textCity_detail = (TextView)view.findViewById(R.id.textCity_detail);
		textCity_detail.setText((String)map.get("address"));
		
		TextView textPostcode = (TextView)view.findViewById(R.id.textPostcode);
		textPostcode.setText((String)map.get("zipcode"));
		
		TextView textTele = (TextView)view.findViewById(R.id.textTele);
		textTele.setText((String)map.get("telephone"));
		
		TextView textPhone = (TextView)view.findViewById(R.id.textPhone);
		textPhone.setText((String)map.get("phoneNumber"));
		
		ImageView imgAddr_ok = (ImageView)view.findViewById(R.id.imgAddr_ok);
		String isdefault = (String)map.get("isdefault");
		if(isdefault.equals("0"))
			imgAddr_ok.setVisibility(View.VISIBLE);
		else
			imgAddr_ok.setVisibility(View.GONE);
		
		final String pkid = (String)map.get("pkid");
		final String storeid = (String)map.get("storeid");
		RelativeLayout relAddress = (RelativeLayout)view.findViewById(R.id.relAddress);
		relAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(contexts, AddUpdateAddress.class);
			    Bundle bundle = new Bundle();
				bundle.putString("storeid", storeid);
				bundle.putString("aid", pkid);
				intent.putExtras(bundle);
//				contexts.startActivity(intent);//��ʼ�������ת����
				((Activity) contexts).startActivityForResult(intent, 1);  
				((Activity) contexts).overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
		});
		
		return view;
		
//		return createViewFromResource(position, convertView, parent, mResource);    //�������淽��
	}
	
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
	
	public void makeText(String str)
	{
		Toast.makeText(contexts, str, Toast.LENGTH_LONG).show();
	}
	
}
