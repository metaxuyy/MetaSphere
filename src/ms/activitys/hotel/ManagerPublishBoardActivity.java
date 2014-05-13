package ms.activitys.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.myAnimation;
import ms.globalclass.StaggeredGridView.ImageLoader;
import ms.globalclass.StaggeredGridView.RImageLoader;
import ms.globalclass.StaggeredGridView.ScaleImageView;
import ms.globalclass.StaggeredGridView.StaggeredGridView;
import ms.globalclass.httppost.Douban;
import ms.globalclass.image.RoundAngleImageView;
import ms.globalclass.listviewadapter.ManagerGridAdapter;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class ManagerPublishBoardActivity extends Activity {
//	private GridView gridView;
	private ListView gridView;
	private List<Map<String, Object>> list;
//	private ManagerGridAdapter adapter;
	private MyApp myapp;
	private Douban api;
	private ProgressDialog mypDialog;
	private static SharedPreferences share;
	private ImageGridAdapter saImageItems;
	public static ManagerPublishBoardActivity instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_publishboard);
		myapp = (MyApp) this.getApplication();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share, myapp);
		
		instance = this;

		initView();
	}

	// ��ʼ���ؼ�
	private void initView() {
		gridView = (ListView) findViewById(R.id.manager_gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View v,
//					int position, long id) {
//				Map<String, Object> map = list.get(position);
//				String appName = (String) map.get("name");
//				String appUrl = (String) map.get("url");
//				String appType = (String) map.get("type");
//				String appBgImg = (String) map.get("bgImg");
//				String appImg = (String) map.get("imgid");
//				Intent intent = new Intent(ManagerPublishBoardActivity.this,
//						MomentsActivity.class);
//				intent.putExtra("title", appName);
//				intent.putExtra("url", appUrl);
//				intent.putExtra("type", appType);
//				intent.putExtra("bgImg", appBgImg);
//				intent.putExtra("imgid", appImg);
//				startActivity(intent);
//				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
//			}

//			@Override
//			public void onItemClick(StaggeredGridView parent, View view,
//					int position, long id) {
//				Map<String, Object> map = list.get(position);
//				String appName = (String) map.get("name");
//				String appUrl = (String) map.get("url");
//				String appType = (String) map.get("type");
//				String appBgImg = (String) map.get("bgImg");
//				String appImg = (String) map.get("imgid");
//				Intent intent = new Intent(ManagerPublishBoardActivity.this,
//						MomentsActivity.class);
//				intent.putExtra("title", appName);
//				intent.putExtra("url", appUrl);
//				intent.putExtra("type", appType);
//				intent.putExtra("bgImg", appBgImg);
//				intent.putExtra("imgid", appImg);
//				startActivity(intent);
//				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
//			}

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String, Object> map = list.get(arg2);
				String pkid = (String)map.get("pkid");
				String appName = (String) map.get("name");
				String appUrl = (String) map.get("url");
				String appType = (String) map.get("type");
				String appBgImg = (String) map.get("bgImg");
				String appImg = (String) map.get("imgid");
				changListData2(appType);
				Intent intent = new Intent(ManagerPublishBoardActivity.this,
						MomentsActivity.class);
				intent.putExtra("title", appName);
				intent.putExtra("url", appUrl);
				intent.putExtra("type", appType);
				intent.putExtra("bgImg", appBgImg);
				intent.putExtra("imgid", appImg);
				startActivity(intent);
				
//				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
				MainTabActivity.instance.loadLeftActivity(intent);
			}
		});
//		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
////			@Override
////			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
////					int arg2, long arg3) {
////				Map<String, Object> map = list.get(arg2);
////				String appName = (String) map.get("name");
////				String appType = (String) map.get("type");
////				String appBgImg = (String) map.get("bgImg");
////				
////				createShortCut(appName, appType, appBgImg);
////				return false;
////			}
//
////			@Override
////			public boolean onItemLongClick(StaggeredGridView parent, View view,
////					int position, long id) {
////				Map<String, Object> map = list.get(position);
////				String appName = (String) map.get("name");
////				String appType = (String) map.get("type");
////				String appBgImg = (String) map.get("bgImg");
////				
////				createShortCut(appName, appType, appBgImg);
////				return false;
////			}
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				Map<String, Object> map = list.get(arg2);
//				String appName = (String) map.get("name");
//				String appType = (String) map.get("type");
//				String appBgImg = (String) map.get("bgImg");
//				
//				createShortCut(appName, appType, appBgImg);
//				return false;
//			}
//		});

		getManagerApps();
	}

	private void getManagerApps() {
		showProgressDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				try {
					JSONObject jObj = api.getManagerApp();
					if (jObj != null) {
						msg.obj = jObj;
					}
				} catch (Exception e) {
					msg.obj = null;
					e.printStackTrace();
				}
				uploadPicHandler.sendMessage(msg);
			};
		}.start();
	}

	private Handler uploadPicHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				mypDialog.dismiss();
				if (msg.obj != null) {
					JSONObject jObj = (JSONObject) msg.obj;
					try {
						// ������ģ���б�:{"data":[{"appName":"�������","appUrl":"","appImg":"manager_room.png"},
						// {"appName":"������","appUrl":"http://192.168.254.15:8085/customize/control/billboard","appImg":"manager_board.png"}],
						// "msg":"success"}
						if (jObj.has("msg")
								&& jObj.getString("msg").equals("success")) {
							list = new ArrayList<Map<String, Object>>();

							JSONArray arr = jObj.getJSONArray("data");
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = (JSONObject) arr.get(i);
								Map<String, Object> map = new HashMap<String, Object>();
								String pkid = obj.getString("pkid");
								String appType = obj.getString("appType");
								map.put("pkid", pkid);
								map.put("name", obj.get("appName"));
								map.put("imgid", obj.get("appImg"));
								map.put("url", obj.get("appUrl"));
								map.put("type", obj.get("appType"));
								map.put("bgImg", obj.get("appBgImg"));
								if(myapp.getMomentsNewNumber().containsKey(appType))
								{
									String newnmber = myapp.getMomentsNewNumber().get(appType);
									map.put("newnumber", newnmber);
								}
								else
								{
									myapp.getMomentsNewNumber().put(appType,"0");
									map.put("newnumber", "0");
								}
								
								list.add(map);
//								if(i == 1)
//									break;
							}

//							adapter = new ManagerGridAdapter(
//									ManagerPublishBoardActivity.this);
//							adapter.setList(list);
							//gridView.setAdapter(adapter);
							
							saImageItems = new ImageGridAdapter(ManagerPublishBoardActivity.this, list, R.layout.manager_grid_item);
							gridView.setAdapter(saImageItems);
							
							Map<String,Object> newGonGaomap = MainTabActivity.instance.newGonGaomap;
							if(newGonGaomap != null)
							{
								changListData(newGonGaomap);
								MainTabActivity.instance.newGonGaomap = null;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(ManagerPublishBoardActivity.this,
							"���ع���ģ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}

	};
	
	public void changListData(String appType)
	{
		try{
			for(int i=0;i<list.size();i++)
			{
				Map<String,Object> map = list.get(i);
				String type = (String)map.get("type");
				String newnumber2 = (String)map.get("newnumber");
				if(appType.equals(type))
				{
					map.put("newnumber", String.valueOf(Integer.valueOf(newnumber2)+1));
					myapp.getMomentsNewNumber().put(appType, String.valueOf(Integer.valueOf(newnumber2)+1));
					break;
				}
			}
			saImageItems.notifyDataSetChanged();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void changListData(Map<String,Object> dmap)
	{
		try{
			for(int i=0;i<list.size();i++)
			{
				Map<String,Object> map = list.get(i);
				String type = (String)map.get("type");
				String newnumber2 = (String)map.get("newnumber");
				if(dmap.containsKey(type))
				{
					List<Map<String, Object>> dlist = (List<Map<String, Object>>)dmap.get(type);
					map.put("newnumber", String.valueOf(Integer.valueOf(newnumber2)+dlist.size()));
					myapp.getMomentsNewNumber().put(type, String.valueOf(Integer.valueOf(newnumber2)+dlist.size()));
					break;
				}
			}
			saImageItems.notifyDataSetChanged();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void changListData2(String appType)
	{
		try{
			for(int i=0;i<list.size();i++)
			{
				Map<String,Object> map = list.get(i);
				String type = (String)map.get("type");
				if(appType.equals(type))
				{
					map.put("newnumber", "0");
					myapp.getMomentsNewNumber().put(appType, "0");
					break;
				}
			}
			saImageItems.notifyDataSetChanged();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// breakActivity();
			MainTabActivity.instance.onMinimizeActivity();
			return false;
		}
		return false;
	}

	private void showProgressDialog() {
		try {
			mypDialog = new ProgressDialog(ManagerPublishBoardActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("���ڼ��ع����б����Ե�...");
			mypDialog.setIndeterminate(false);
			mypDialog.setCancelable(true);
			mypDialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createShortCut(String appName, String appType, String appBgImg) {
		// ������ݷ�ʽ��Intent
		Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// �������ظ�����
		shortcutintent.putExtra("duplicate", false);
		// ��Ҫ��ʵ������
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
		// ���ͼƬ
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), R.drawable.icon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// ������ͼƬ�����еĳ��������
		Intent intent = new Intent(getApplicationContext(), myAnimation.class);
		// ��������������Ϊ�˵�Ӧ�ó���ж��ʱ���� �ϵĿ�ݷ�ʽ��ɾ��
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		//������Ҫ�Ĳ���
		intent.putExtra("isShortCut", true);
		intent.putExtra("title", appName);
		intent.putExtra("type", appType);
		intent.putExtra("bgImg", appBgImg);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// ���͹㲥��OK
		sendBroadcast(shortcutintent);
	}
	
	private class ImageGridAdapter extends BaseAdapter{
        private Context context;
        private int resource;
        private List<Map<String,Object>> mDlist = new ArrayList<Map<String,Object>>();
        private RImageLoader mLoader;
        
        //����4.1����дadapter�Ĺ��캯��
        ImageGridAdapter( Context context,List<Map<String,Object>> data, int resource){
                this.context = context;
                this.resource = resource;
                this.mDlist = data;
                mLoader = new RImageLoader(context);
        }
        
        @Override
    	public int getViewTypeCount() {
    		//��Ϊ��������ͼ�����Է���3
    		return 1;
    	}
       
        //����4.2����дgetView()����ÿ����Ԫ�������Լ�UI��ʽ��������
        /*������ǲ�ʹ��TextView�������Ǳ���ͨ��getView()��ÿһ��gridview��Ԫ������������Щ��Ԫ������Button��ImageView������������ʹ��Button��TextView�ֱ������� ��дoverride getView(int, View, ViewGroup)�������κ�������ϣ����view��*/
        public View getView  (int position, View  convertView, ViewGroup  parent){
        	ViewHolder1 viewHolder1 = null;
        	Map<String,Object> map = mDlist.get(position);
        	String imgurl = (String)map.get("imgid");
        	String name = (String)map.get("name");
        	String newnumber = (String)map.get("newnumber");
            //���ǲ��Է��֣�����һ��convertView�⣬����Ķ���NULL��������û��view��������Ҫ����
            if(convertView == null){
            	convertView = LayoutInflater.from(context).inflate(resource, null);
            	viewHolder1 = new ViewHolder1();
				viewHolder1.img_item = (RoundAngleImageView)convertView.findViewById(R.id.itemImage);
				viewHolder1.name_txt = (TextView)convertView.findViewById(R.id.itemText);
				viewHolder1.new_txt = (TextView)convertView.findViewById(R.id.new_txt);
				convertView.setTag(viewHolder1);
            }else{
    			//��convertView������ʽ��ȡ�ò��õĲ���
            	viewHolder1 = (ViewHolder1) convertView.getTag();
    		}
           
            if(imgurl != null && !imgurl.equals(""))
    		{
//    			Bitmap storeimg = myapp.returnBitMap(imgurl);
//    			viewHolder1.img_item.setImageBitmap(storeimg);
            	mLoader.DisplayImage(imgurl, viewHolder1.img_item);
    		}
            
            if(newnumber != null && !newnumber.equals("") && !newnumber.equals("0"))
            {
            	viewHolder1.new_txt.setVisibility(View.VISIBLE);
            	viewHolder1.new_txt.setText(newnumber);
            }
            else
            {
            	viewHolder1.new_txt.setVisibility(View.GONE);
            	viewHolder1.new_txt.setText("");
            }
            viewHolder1.name_txt.setText(name);
//            viewHolder1.name_txt.getBackground().setAlpha(100);
            return convertView;
        }
        
        class ViewHolder1{
        	RoundAngleImageView img_item;
        	TextView name_txt;
        	TextView new_txt;
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
}
