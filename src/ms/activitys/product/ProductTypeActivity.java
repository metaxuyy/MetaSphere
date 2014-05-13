package ms.activitys.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.R;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class ProductTypeActivity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressBar pb;
	private String pagetag = "typepage";
	
	private ListView slistView;
	private int index;
	private String appliconStoreId;
	private Button back_btn;
	private String typeid;
	private String cname;
	private String step;
	private TextView titletxt;
	
	private String maptag = "";
    private String advertiseNotification;
    private TextView nulltxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_type_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		maptag = bunde.getString("map");
		advertiseNotification = bunde.getString("advertiseNotification");
		
		pb = (ProgressBar)findViewById(R.id.probar);
		nulltxt = (TextView)findViewById(R.id.nulltxt);
		
		back_btn = (Button)findViewById(R.id.back_btn);
		titletxt = (TextView)findViewById(R.id.TextView01);
		
		showMenuTypePage();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ProductTypeActivity.this.setResult(RESULT_OK, getIntent());
			ProductTypeActivity.this.finish();
			return false;
		}
		return false;
	}
	
	/**
	 * 显示菜单类型列表
	 */
	public void showMenuTypePage()
	{
		pagetag = "typepage";
		try{
			back_btn.setVisibility(View.GONE);
			slistView = (ListView)findViewById(R.id.ListView_catalog);
			pb.setVisibility(View.VISIBLE);
			slistView.setVisibility(View.GONE);
			
			loadThreadData();
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					typeid = myapp.getMenuList().get(arg2).get("pkid").toString();
					cname = myapp.getMenuList().get(arg2).get("cname").toString();
					step = "1";
//					pb.setVisibility(View.VISIBLE);
//					slistView.setVisibility(View.GONE);
					loadThreadData2(typeid);
				}
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadThreadData()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					List<Map<String,Object>> lists = myapp.getMyCardsAll();
					Map map = lists.get(index);
					
					String storeid = (String)map.get("storeid");
					appliconStoreId = storeid;
					
					jobj = api.getMenuTypeData(storeid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getMenuList(jArr);
							myapp.setMenuList(list);
							msg.obj = list;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData2(final String typeid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getMenuTypeData2(appliconStoreId,typeid);
					if(jobj != null)
					{
						if(jobj.has("error"))
						{
							msg.obj = null;
						}
						else
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							list = getMenuList(jArr);
							myapp.setMenuList2(list);
							msg.obj = list;
						}
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
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(ProductTypeActivity.this, dlist,// 数据源
							R.layout.categoryitem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "mimg", "cname" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.imgIcon, R.id.textContent },share,"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					slistView.setAdapter(listItemAdapter);
					pb.setVisibility(View.GONE);
					slistView.setVisibility(View.VISIBLE);
				}
				else
				{
					pb.setVisibility(View.GONE);
					nulltxt.setVisibility(View.VISIBLE);
				}
				break;
			case 1:
				if(msg.obj != null)
				{
					showMenuTypePage2();
				}
				else
				{
					showProductListMume(typeid,cname);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void showMenuTypePage2()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductType2Activity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", appliconStoreId);
			bundle.putString("typeid", typeid);
			bundle.putString("typename", cname);
			bundle.putString("step",step);
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showProductListMume(String typeid,String typename)
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductMenuListActivity.class);
		    Bundle bundle = new Bundle();
			bundle.putString("storeid", appliconStoreId);
			bundle.putString("typeid", typeid);
			bundle.putString("typename", typename);
			bundle.putString("step",step);
			bundle.putInt("index", index);
			bundle.putString("map", maptag);
			bundle.putString("advertiseNotification", advertiseNotification);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getMenuList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; 
				if(dobj.has("pId"))
					pkid = (String) dobj.get("pId"); 
				
				String cname = ""; 
				if(dobj.has("tname"))
					cname = (String) dobj.get("tname");
				
				String imgurl = ""; 
				if(dobj.has("sysImg"))
					imgurl = (String) dobj.get("sysImg");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("mimg", imgurl);
				map.put("cname", cname);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
