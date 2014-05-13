package ms.activitys.hotel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.MenuLoadActivity;
import ms.activitys.R;
import ms.activitys.coupon.MyCouponView;
import ms.activitys.map.BaiduMapRouteSearch;
import ms.activitys.map.PeripheryMapsActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.listviewadapter.AsyncImageLoader;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StoreMainActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private int index;
	
	public static StoreMainActivity instance = null;
	private String maptag;
	private String advertiseNotification;
	public static String isASttention;
	private Map<String,Object> dmap;
	private String pkid;
	private String storeid;
	private String profileid;
	private ImageView straimg;
	private MyLoadingDialog loadDialog;
	private String typesMapping;
	private String sortName;
	private static DBHelperMessage db;
	private RoundAngleImageView user_img;
	private Bitmap storeimg = null;
	private ImageView mapimg;
	private RelativeLayout address_rlayout;
	private LinearLayout image_list_layout;
	private LinearLayout load_layout;
	public static FileUtils fileUtil = new FileUtils();
	private AsyncImageLoader imageLoader = new AsyncImageLoader();
	private GridView gridview; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_main_view);
		
		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);

		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = Integer.valueOf(bunde.getString("index"));
		maptag = bunde.getString("tag");
		advertiseNotification = bunde.getString("advertiseNotification");
		
		dmap = myapp.getMyCardsAll().get(index);
		isASttention = (String)dmap.get("isASttention");
		pkid = (String)dmap.get("pkid");
		storeid = (String)dmap.get("storeid");
		profileid = (String)dmap.get("profileid");
		typesMapping = (String)dmap.get("typesMapping");
		sortName = (String)dmap.get("sortName");
		
//		Bitmap imgurl = (Bitmap)dmap.get("imgurl");
		String imgurl = (String)dmap.get("imgurl");
		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
		if(imgurl != null && !imgurl.equals(""))
		{
			if(imgurl.indexOf("http://") >= 0)
			{
				setImageData(user_img,imgurl);
			}
			else
			{
				storeimg = getLoacalBitmap(imgurl);
				user_img.setImageBitmap(storeimg);
			}
//			loadUserImageThread(imgurl);
		}
		else
		{
			storeimg = null;
		}
//		user_img = (RoundAngleImageView)findViewById(R.id.user_img);
//		if(imgurl != null && !imgurl.equals(""))
//		{
//			storeimg = imgurl;
//			user_img.setImageBitmap(imgurl);
//		}
//		else
//		{
//			storeimg = null;
//		}
		
		initView();
	}
	
	public void setImageData(final ImageView v,String url)
	{
		try{
			imageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable != null && imageDrawable.getIntrinsicWidth() > 0) {
						
						v.setImageDrawable(imageDrawable);
					}
				}
			},60,60);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void initView()
	{
		try{
			Button break_btn = (Button)findViewById(R.id.break_btn);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					breakActivity();
				}
			});
			
			String storeName = (String)dmap.get("storeName");
			String storeDesc = (String)dmap.get("storeDesc");
			String storePhone = (String)dmap.get("storePhone");
			String couponNumber = (String)dmap.get("couponNumber");
			String addressInfomation = (String)dmap.get("addressInfomation");
			TextView title_lable = (TextView)findViewById(R.id.title_lable);
			title_lable.setText(storeName);
			TextView name_txt = (TextView)findViewById(R.id.name_txt);
			name_txt.setText(storeName);
			TextView address_txt = (TextView)findViewById(R.id.address_txt);
			address_txt.setText(addressInfomation);
			TextView store_doc_txt = (TextView)findViewById(R.id.store_doc_txt);
			store_doc_txt.setText(Html.fromHtml(storeDesc));
			TextView store_hone_txt = (TextView)findViewById(R.id.store_hone_txt);
			store_hone_txt.setText(storePhone);
			TextView couponnum = (TextView)findViewById(R.id.item_badge_text_coupon);
			if(couponNumber != null && !couponNumber.equals("") && Integer.valueOf(couponNumber) > 0)
			{
				couponnum.setVisibility(View.VISIBLE);
				couponnum.setText(couponNumber);
			}
//			mapimg = (ImageView)findViewById(R.id.map_img);
//			mapimg.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					showStopAddress();
//				}
//			});
//			address_rlayout = (RelativeLayout)findViewById(R.id.address_rlayout);
//			loadMapImage();
			gridview = (GridView) findViewById(R.id.gridview);
			image_list_layout = (LinearLayout)findViewById(R.id.image_list_layout);
			load_layout = (LinearLayout)findViewById(R.id.load_layout);
			loadStoreImageList();
			
			straimg = (ImageView)findViewById(R.id.stra_img);
			if(isASttention.equals("0"))
				straimg.setImageResource(R.drawable.star_mark);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openphone(View v)
	{
		String phoneNumber = (String)dmap.get("storePhone");
		if(phoneNumber != null && !phoneNumber.equals(""))
		{
			phoneNumber = phoneNumber.replaceAll("-", "");
//			Intent myIntentDial = new Intent( Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));  
//	        startActivity(myIntentDial);  
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber)); 
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
		}
	}
	
	public void openMoveMume(View v)
	{
		Intent intent = new Intent (this,RecommendMuneDialog.class);
		Bundle bundle = new Bundle();
		bundle.putString("tag", "storetuijian");
		bundle.putString("storeid", storeid);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	/**
	 * 显示下载优惠券界面
	 */
	public void showDownloadCouponView(View v)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MyCouponView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag", "download");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showStopAddress()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,LocalMap.class);
			intent.setClass( this,BaiduMapRouteSearch.class);
		    Bundle bundle = new Bundle();
			bundle.putString("index", String.valueOf(index));
			bundle.putString("tag","storemain");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadMapImage()
	{
		final String woof = (String)dmap.get("woof");
		final String longItude = (String)dmap.get("longItude");
		
		if(woof != null && !woof.equals(""))
		{
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					try{
//					String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x350&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
					String url = "http://api.map.baidu.com/staticimage?width=470&height=350&center="+longItude+","+woof+"&markers="+longItude+","+woof+"&zoom=17";
					Bitmap bitmap = returnBitMap(url);
					if(bitmap != null)
						msg.obj = bitmap;
					else
						msg.obj = null;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
		else
		{
			address_rlayout.setVisibility(View.GONE);
		}
	}
	
	public void labeledStarredFriend()
    {
    	showMyLoadingDialog();
    	try{
    		new Thread() {
    			public void run() {
    				Message msg = new Message();
    				msg.what = 1;
    				
    				try {
    					JSONObject jobj;
    					if(isASttention.equals("1"))//1为否，0为是
    						jobj = api.isASttention(pkid,"1");
    					else
    						jobj = api.notASttention(pkid,"1");
    					if(jobj != null)
    						msg.obj = "1";
    					else
    						msg.obj = null;
    				} catch (Exception ex) {
    					Log.i("erroyMessage", ex.getMessage());
    					ex.printStackTrace();
    				}
    				handler.sendMessage(msg);
    			}
    		}.start();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
	
	public void loadStoreImageList()
    {
    	try{
    		new Thread() {
    			public void run() {
    				Message msg = new Message();
    				msg.what = 3;
    				
    				try {
    					JSONObject jobj;
    					jobj = api.getStoreImageListData(storeid);
    					List<Map<String,Object>> dlist = null;
    					if(jobj != null)
    					{
    						if(jobj.getString("tag").equals("success"))
    						{
    							JSONArray jarry = jobj.getJSONArray("data");
    							if(jarry != null && jarry.length() > 0)
    							{
    								dlist = loadStroeImags(jarry);
    								myapp.setStoreImages(dlist);
    							}
    						}
    					}
    					
    					msg.obj = dlist;
    				} catch (Exception ex) {
    					Log.i("erroyMessage", ex.getMessage());
    					ex.printStackTrace();
    				}
    				handler.sendMessage(msg);
    			}
    		}.start();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
	
	public void unfollowStore()
    {
    	showMyLoadingDialog();
    	new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					JSONObject jobj = api.notStoreUnfollow(pkid,storeid);
					if(jobj != null)
					{
						String tag = jobj.getString("tag");
						if(!tag.equals("error"))
						{
							db.openDB();
							db.deleteStroeInfo(storeid,dmap.get("nameid").toString());
							db.closeDB();
						}
						msg.obj = tag;
					}
					else
					{
						msg.obj = null;
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
					msg.obj = null;
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
				Bitmap bitmap = (Bitmap)msg.obj;
				if(bitmap != null)
					mapimg.setImageBitmap(bitmap);
				break;
			case 1:
				String tag = (String)msg.obj;
				if(tag != null)
				{
					loadDialog.setSucceedDialog(getString(R.string.hotel_label_17));
					if (isASttention.equals("1"))// 1为否，0为是
					{
						straimg.setImageResource(R.drawable.star_mark);
						isASttention = "0";
						dmap.put("isASttention", isASttention);
						dmap.put("sortName",  "0"+sortName);
						dmap.put("xinxin", R.drawable.ic_star_small);
					} else {
						straimg.setImageBitmap(null);
						isASttention = "1";
						dmap.put("isASttention", isASttention);
						dmap.put("sortName", sortName);
						dmap.put("xinxin", null);
					}
					db.openDB();
					db.updateStoreInfoData(dmap);
					db.closeDB();
//					ContactActivity.instance.getMyStoreListData();
					HotelServiceActivity.instance.getMyStoreListData();
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				String tags = (String)msg.obj;
				if(tags != null && tags.equals("success") || tags.equals("meiyou"))
				{
					myapp.getMyCardsAll().remove(index);
//					ContactActivity.instance.getMyStoreListData();
					if(HotelServiceActivity.instance != null)
					{
						HotelServiceActivity.instance.showMyLoadingDialog();
						HotelServiceActivity.instance.getMyStoreListDatas();
					}
					if(HotelMainActivity.instance != null)
						HotelMainActivity.instance.loadeListItemData();
					makeText(getString(R.string.hotel_label_19));
					openMainTabForm();
				}
				else
				{
					makeText(getString(R.string.hotel_label_18));
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 3:
				List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
				if(dlist != null)
				{
					ImageGridAdapter saImageItems = new ImageGridAdapter(StoreMainActivity.this, dlist, R.layout.grid_images_item);
					
					gridview.setAdapter(saImageItems);
					gridview.setVisibility(View.VISIBLE);
					gridview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							openStoreImageView(arg2);
						}
					});
					
					int totalHeight = 0;
					int rowlen = saImageItems.getCount()/3;
					if(saImageItems.getCount()  % 3 != 0)
						rowlen = rowlen + 1;
					for (int i = 0, len = rowlen; i < len; i++) {
		                View listItem = saImageItems.getView(i, null, gridview);
		                listItem.measure(0, 0); 
		                int list_child_item_height = listItem.getMeasuredHeight()+10;
		                totalHeight += list_child_item_height;
					}
					
					LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gridview.getLayoutParams();    
					linearParams.height = totalHeight;        
					gridview.setLayoutParams(linearParams);
				}
				else
				{
					image_list_layout.setVisibility(View.GONE);
				}
				load_layout.setVisibility(View.GONE);
				break;
			}
		}
	};
	
	public void openMainTabForm()
	{
		Intent intent = new Intent();
	    intent.setClass( this,MainTabActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity
	    Bundle bundle = new Bundle();
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	    this.finish();
	}
	
	public void openStoreImageView(int index)
	{
		Intent intent = new Intent();
		intent.setClass( this,StoreImagesActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("sortName", sortName);
	    bundle.putInt("index", index);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	}
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			breakActivity();
			return false;
		}
		return false;
	}
	
	public void breakActivity()
	{
		if(maptag != null && !maptag.equals(""))
		{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			if(maptag.equals("message"))
				intent.setClass( this,MessageListActivity.class);
			else if(maptag.equals("storeinfo"))
			{
				intent.setClass( this,StoreViewActivity.class);
				bundle.putInt("index", index);
			}
			else if(maptag.equals("pmap"))
				intent.setClass( this,PeripheryMapsActivity.class);
			else
				intent.setClass( this,MainTabActivity.class);
		    
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();//关闭显示的Activity
		}
		else if(advertiseNotification != null)
		{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();//关闭显示的Activity
		}
		else
		{
		    setResult(RESULT_OK, getIntent());
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
		}
	}
	
	public List<Map<String,Object>> loadStroeImags(JSONArray jarry)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			if(!fileUtil.isFileExist2(storeid))
				fileUtil.createUserFile(storeid);
			for(int i=0;i<jarry.length();i++)
			{
				JSONObject dobj = jarry.getJSONObject(i);
				
				String filename = "";
				if(dobj.has("filename"))
				{
					filename = (String) dobj.get("filename");
					String [] strs = filename.split("\\.");
					filename = strs[0];
				}
				
				String fileurl = ""; 
				if(dobj.has("fileurl"))
				{
					fileurl = (String) dobj.get("fileurl"); 
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				if(!fileUtil.isFileExist2("/" + storeid + "/" + "infoimage/"+filename))
				{
					Bitmap bitmap = myapp.getImageBitmap(fileurl);
					String furl = fileUtil.getStoreImageFilePath(storeid, filename);
					myapp.saveMyBitmap(furl, bitmap);
					fileurl = furl;
					map.put("imgbitmap", bitmap);
				}
				else
				{
					String furl = fileUtil.getStoreImageFilePath(storeid, filename);
					fileurl = furl;
					Bitmap bitmap = getLoacalBitmap(fileurl);
					map.put("imgbitmap", bitmap);
				}
				map.put("filename", filename);
				map.put("filepath", fileurl);
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public void loadImageListLayout(List<Map<String,Object>> dlist)
	{
		try{
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> map = dlist.get(i);
				Bitmap bitmap = (Bitmap)map.get("imgbitmap");
				
				if(i < 3)
				{
					if(i == 0)
					{
						
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
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
			bitmap = BitmapFactory.decodeStream(is);
//			if(dstWidth > 1)
//				bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
//			Bitmap newimg = getRes();
//			bitmap = createBitmap(bitmap,newimg);
//			if(b)
//				bitmap = getRoundedCornerBitmap(bitmap,12);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	/**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
		
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	private class ImageGridAdapter extends BaseAdapter{
        private Context context;
        private int resource;
        private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
        
        //步骤4.1：编写adapter的构造函数
        ImageGridAdapter( Context context,List<Map<String,Object>> data, int resource){
                this.context = context;
                this.resource = resource;
                this.mDlist = data;
        }
       
        //步骤4.2：重写getView()，对每个单元的内容以及UI格式进行描述
        /*如果我们不使用TextView，则我们必须通过getView()对每一个gridview单元进行描述。这些单元可以是Button，ImageView，在这里我们使用Button和TextView分别作测试 重写override getView(int, View, ViewGroup)，返回任何我们所希望的view。*/
        public View getView  (int position, View  convertView, ViewGroup  parent){
        	ViewHolder1 viewHolder1 = null;
        	Map<String,Object> map = mDlist.get(position);
        	Bitmap bitmap = (Bitmap)map.get("imgbitmap");
            //我们测试发现，除第一个convertView外，其余的都是NULL，因此如果没有view，我们需要创建
            if(convertView == null){
            	convertView = LayoutInflater.from(context).inflate(resource, null);
            	viewHolder1 = new ViewHolder1();
				viewHolder1.img_item = (ImageView)convertView.findViewById(R.id.img_item);
				convertView.setTag(viewHolder1);
            }else{
    			//有convertView，按样式，取得不用的布局
            	viewHolder1 = (ViewHolder1) convertView.getTag();
    		}
           
            viewHolder1.img_item.setImageBitmap(bitmap);     
            return convertView;
        }
        
        class ViewHolder1{
    		ImageView img_item;
    	}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mDlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
    }// End of class FunnyLookingAdapter
	
	/**
	 * 显示公司主页
	 * @param v
	 */
	public void showStorePage(View v){
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MenuLoadActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", storeid);
			bundle.putString("isShowCom", "Yes");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
