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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class ProductType2Activity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressBar pb2;
	private String pagetag = "typepage";
	
	private ListView slistView2;
	private int index;
	private String appliconStoreId;
	private Button back_btn;
	private String menustr2;
	private String typeid;
	private String cname;
	private String step;
	private TextView titletxt;
	
	private String maptag = "";
    private String advertiseNotification;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_type_view2);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		String typeid = bunde.getString("typeid");
		menustr2 = bunde.getString("typename");
		maptag = bunde.getString("map");
		advertiseNotification = bunde.getString("advertiseNotification");
		appliconStoreId = bunde.getString("storeid");
		
		pb2 = (ProgressBar)findViewById(R.id.probar2);
		
		back_btn = (Button)findViewById(R.id.back_btn);
		titletxt = (TextView)findViewById(R.id.TextView01);
		
		showMenuTypePage2();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ProductType2Activity.this.setResult(RESULT_OK, getIntent());
			ProductType2Activity.this.finish();
			return false;
		}
		return false;
	}
	
	public void showMenuTypePage2()
	{
		pagetag = "typepage2";
		try{
//			mViewFlipper.showNext();
			back_btn.setVisibility(View.VISIBLE);
//			back_btn.setText(cname);
			titletxt.setText(menustr2);
			back_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ProductType2Activity.this.setResult(RESULT_OK, getIntent());
					ProductType2Activity.this.finish();
				}
			});
			
			slistView2 = (ListView)findViewById(R.id.ListView_catalog2);
			pb2.setVisibility(View.VISIBLE);
			slistView2.setVisibility(View.GONE);
			
			loadThreadData2(typeid);
			
			// 添加点击
			slistView2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					typeid = myapp.getMenuList2().get(arg2).get("pkid").toString();
					cname = myapp.getMenuList2().get(arg2).get("cname").toString();
					step = "2";
//					pb2.setVisibility(View.VISIBLE);
//					slistView2.setVisibility(View.GONE);
					loadThreadData3();
				}
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMenuTypePage3(String typeid,String typename)
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MyMenuListView.class);
			intent.setClass(this, ProductType3Activity.class);
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
		    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void loadThreadData2(final String typeid)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				List<Map<String,Object>> list = myapp.getMenuList2();
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void loadThreadData3()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				List<Map<String,Object>> list = null;
				try{
					JSONObject jobj;
					U.dout(0);
					
					jobj = api.getMenuTypeData3(appliconStoreId,typeid);
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
							myapp.setMenuList3(list);
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
			case 1:
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(ProductType2Activity.this, dlist,// 数据源
							R.layout.categorychilditem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] {"cname" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textContent },share,"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					slistView2.setAdapter(listItemAdapter);
					pb2.setVisibility(View.GONE);
					slistView2.setVisibility(View.VISIBLE);
				}
				break;
			case 2:
				if(msg.obj != null)
				{
					showMenuTypePage3(typeid,cname);
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
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
