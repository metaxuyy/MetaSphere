package ms.activitys.hotel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.map.BaiduMap;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.StaggeredGridView.ImageLoader;
import ms.globalclass.StaggeredGridView.ScaleImageView;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class HotelServiceActivity extends Activity{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static HotelServiceActivity instance = null;
	private static DBHelperMessage db;
	
//	private StaggeredGridView gridview; 
	private GridView gridview; 
	private LinearLayout load_layout;
	private static FileUtils fileUtil = new FileUtils();
	
	private boolean isMenuLoadCall = false;//记录是页面调用
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_service_view);

		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		instance = this;

		db = new DBHelperMessage(this, myapp);
		
		Intent intent = this.getIntent();       
		Bundle bundle = intent.getExtras();  
		if(bundle!=null){
			String tag = bundle.getString("tag"); 
			if(tag!=null && tag.equals("menuload")){
				isMenuLoadCall = true;
			}
		}

		showMyLoadingDialog();
		initView();
	}
	
	public void initView()
	{
		try{
			load_layout = (LinearLayout)findViewById(R.id.load_layout);
			gridview = (GridView) findViewById(R.id.gridview);
			
			int margin = getResources().getDimensionPixelSize(R.dimen.margin);
			
//			gridview.setItemMargin(margin); // set the GridView margin
			
			gridview.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
			
			getMyStoreListDatas();
			
			Button backBtn = (Button) findViewById(R.id.break_btn);
			backBtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					HotelServiceActivity.this.finish();
				}
			});
			if(isMenuLoadCall){
				backBtn.setVisibility(View.VISIBLE);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void getMyStoreListData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					db.openDB();
					List<Map<String,Object>> storelist = db.getStoreInfoAllData("");
					myapp.setMyCardsAll(storelist);
					db.closeDB();
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void getMyStoreListDatas()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					List<Map<String,Object>> storelist = null;
					db.openDB();
					storelist = db.getStoreInfoAllData("");
					Map<String,Object> dmap = db.getNewMessageData("1");
					db.closeDB();
					
					List<Map<String,Object>> list = (List<Map<String,Object>>)dmap.get("dlist");
					int zonshu = (Integer)dmap.get("zhonshu");
					
					if(storelist != null && storelist.size() > 0)
					{
						if(list != null && list.size() > 0)
						{
							storelist = getStoreAndMessageList(storelist,list);
							myapp.setMyCardsAll(storelist);
						}
						else
						{
							myapp.setMyCardsAll(storelist);
						}
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("list", storelist);
						map.put("newnumber",zonshu);
						msg.obj = map;
					}
					else
					{
//						JSONObject jobj = api.getMyCardsAll("1");
//						if(jobj != null)
//						{
//							JSONArray jArr = (JSONArray) jobj.get("data");
//							List<Map<String,Object>> list = myapp.getMyCardList(jArr);
//							db.openDB();
//							db.saveStoreInfoAllData(list);
//							storelist = db.getStoreInfoAllData("");
//							myapp.setMyCardsAll(storelist);
//							db.closeDB();
//							msg.obj = storelist;
//						}
//						else
//						{
							msg.obj = null;
//						}
					}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public List<Map<String,Object>> getStoreAndMessageList(List<Map<String,Object>> storelist,List<Map<String,Object>> messagelist)
	{
		try{
			for(int i=0;i<storelist.size();i++)
			{
				Map<String,Object> smap = storelist.get(i);
				String storeid = (String)smap.get("storeid");
				for(int j=0;j<messagelist.size();j++)
				{
					Map<String,Object> mmap = messagelist.get(j);
					String sid = (String)mmap.get("storeid");
					int newNumber = (Integer)mmap.get("newNumber");
					if(storeid.equals(sid))
					{
						smap.put("newMessage", String.valueOf(newNumber));
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return storelist;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Map<String,Object> map = (Map<String,Object>)msg.obj;
				if(map != null)
				{
					List<Map<String, Object>> dlist = (List<Map<String, Object>>)map.get("list");
					int newnumber = (Integer)map.get("newnumber");
					load_layout.setVisibility(View.GONE);
					if(dlist != null)
					{
						ImageGridAdapter saImageItems = new ImageGridAdapter(HotelServiceActivity.this, dlist, R.layout.grid_hotel_service_item);
						
						gridview.setAdapter(saImageItems);
						gridview.setVisibility(View.VISIBLE);
//						gridview.setOnItemClickListener(new OnItemClickListener() {
//	
//							@Override
//							public void onItemClick(AdapterView<?> arg0, View arg1,
//									int arg2, long arg3) {
//								// TODO Auto-generated method stub
//								showMessage(arg2);
//							}
//						});
						gridview.setOnItemClickListener(new OnItemClickListener() {
							
//							@Override
//							public void onItemClick(StaggeredGridView parent, View view, int position,
//									long id) {
//								// TODO Auto-generated method stub
//								
//							}

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								showMessage(arg2);
							}
						});
						
						MainTabActivity.instance.setServiceNewNumberValue(newnumber);
					}
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			}
		}
	};
	
	public void onpenAddService(View v)
	{
		Intent intent = new Intent();
	    intent.setClass( this,ServiceAddListActivity.class);
	    Bundle bundle = new Bundle();
		
		intent.putExtras(bundle);
	    MainTabActivity.instance.loadLeftActivity(intent);
	}
	
	public void showMessage(int index)
    {
		Map<String,Object> dmap = myapp.getMyCardsAll().get(index);
		String nameid = dmap.get("nameid").toString();
		String storeid = dmap.get("storeid").toString();
		String storeName = dmap.get("storeName").toString();
		String typesMapping = dmap.get("typesMapping").toString();
		String imgurl = (String)dmap.get("imgurl");
		String servicetype = (String)dmap.get("servicetype");
		String linkurl = (String)dmap.get("linkurl");
		Bitmap storeimg = null;
		if(imgurl != null && !imgurl.equals(""))
		{
			if(imgurl.indexOf("http://") >= 0)
				storeimg = myapp.getImageBitmap(imgurl);
			else
				storeimg = getLoacalBitmap(imgurl);
		}
		String username = myapp.getUserName();
		if(!fileUtil.isFileExist2(storeid))
			fileUtil.createUserFile(storeid);
		if(linkurl != null && !linkurl.equals(""))
			showWebHtml(linkurl,storeName,storeid);
		else
			openMessageDetail(nameid,storeid,storeName,username,typesMapping,index,storeimg,servicetype,linkurl);
    }
	
	public void showWebHtml(String url,String title,String storeid)
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,HtmlWebView.class);
		    Bundle bundle = new Bundle();
			bundle.putString("url", url);
			bundle.putString("title", title);
			bundle.putString("hotel", "hotel");
			bundle.putString("storeid",storeid);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		    MainTabActivity.instance.loadLeftActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openMessageDetail(String nameid,String storeid,String storeName,String username,String typesMapping,int index,Bitmap storeimg,String servicetype,String linkurl)
	{
		try{
			if(MessageListActivity.instance != null)
				MessageListActivity.instance.finish();
			Intent intent = new Intent();
		    intent.setClass( this,MessageListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("nameid", nameid);
			bundle.putString("storeid", storeid);
			bundle.putString("storeName", storeName);
			bundle.putString("username", username);
			bundle.putString("typesMapping", typesMapping);
			bundle.putString("servicetype", servicetype);
			bundle.putString("linkurl", linkurl);
			if(storeimg != null)
			{
				int size=storeimg.getWidth()*storeimg.getHeight()*1; 
				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
				storeimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
				bundle.putByteArray("storeimg", oss.toByteArray());
			}
			else
			{
				bundle.putString("storeimg", null);
			}
			bundle.putInt("index", index);
			bundle.putString("tag", "serviceview");
			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			MainTabActivity.instance.loadLeftActivity(intent);
//		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		if(isMenuLoadCall){
    			HotelServiceActivity.this.finish();
    		}else{
    			MainTabActivity.instance.onMinimizeActivity();
    		}
    	}
    	return false;
    }
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
	
	private class ImageGridAdapter extends BaseAdapter{
        private Context context;
        private int resource;
        private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
        private ImageLoader mLoader;
        
        //步骤4.1：编写adapter的构造函数
        ImageGridAdapter( Context context,List<Map<String,Object>> data, int resource){
                this.context = context;
                this.resource = resource;
                this.mDlist = data;
                mLoader = new ImageLoader(context);
        }
        
        @Override
    	public int getViewTypeCount() {
    		//因为有三种视图，所以返回3
    		return 1;
    	}
        
        //步骤4.2：重写getView()，对每个单元的内容以及UI格式进行描述
        /*如果我们不使用TextView，则我们必须通过getView()对每一个gridview单元进行描述。这些单元可以是Button，ImageView，在这里我们使用Button和TextView分别作测试 重写override getView(int, View, ViewGroup)，返回任何我们所希望的view。*/
        public View getView  (int position, View  convertView, ViewGroup  parent){
        	ViewHolder1 viewHolder1 = null;
        	Map<String,Object> map = mDlist.get(position);
        	String imgurl = (String)map.get("imgurl");
        	String name = (String)map.get("storeName");
        	String newMessage = (String)map.get("newMessage");
            //我们测试发现，除第一个convertView外，其余的都是NULL，因此如果没有view，我们需要创建
            if(convertView == null){
            	convertView = LayoutInflater.from(context).inflate(resource, null);
            	viewHolder1 = new ViewHolder1();
				viewHolder1.img_item = (ScaleImageView)convertView.findViewById(R.id.item_img);
				viewHolder1.name_txt = (TextView)convertView.findViewById(R.id.name_txt);
				viewHolder1.new_number = (TextView)convertView.findViewById(R.id.new_number);
				convertView.setTag(viewHolder1);
            }else{
    			//有convertView，按样式，取得不用的布局
            	viewHolder1 = (ViewHolder1) convertView.getTag();
    		}
           
            if(imgurl != null && !imgurl.equals(""))
    		{
//    			Bitmap storeimg = HotelServiceActivity.instance.getLoacalBitmap(imgurl);
//    			viewHolder1.img_item.setImageBitmap(storeimg);
            	mLoader.DisplayImage(imgurl, viewHolder1.img_item);
    		}
            else
            {
            	viewHolder1.img_item.setImageResource(R.drawable.bg_photoframe);
            }
            viewHolder1.name_txt.setText(name);
//            viewHolder1.name_txt.getBackground().setAlpha(100);
            if(!newMessage.equals("") && !newMessage.equals("0"))
            {
            	viewHolder1.new_number.setVisibility(View.VISIBLE);
            	viewHolder1.new_number.setText(newMessage);
            }
            else
            {
            	viewHolder1.new_number.setVisibility(View.GONE);
            	viewHolder1.new_number.setText("0");
            }
            return convertView;
        }
        
        class ViewHolder1{
        	ScaleImageView img_item;
        	TextView name_txt;
    		TextView new_number;
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
}
