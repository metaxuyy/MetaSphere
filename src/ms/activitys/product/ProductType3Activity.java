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

public class ProductType3Activity extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ProgressBar pb3;
	private String pagetag = "typepage";
	
	private ListView slistView3;
	private int index;
	private String appliconStoreId;
	private Button back_btn;
	private String menustr2;
	private String typeId;
	private String step;
	private TextView titletxt;
	
	private String maptag = "";
    private String advertiseNotification;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_type_view3);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		index = bunde.getInt("index");
		String typeid = bunde.getString("typeid");
		String typename = bunde.getString("typename");
		maptag = bunde.getString("map");
		advertiseNotification = bunde.getString("advertiseNotification");
		appliconStoreId = bunde.getString("storeid");
		
		pb3 = (ProgressBar)findViewById(R.id.probar3);
		
		
		back_btn = (Button)findViewById(R.id.back_btn);
		titletxt = (TextView)findViewById(R.id.TextView01);
		
		showMenuTypePage3(typeid,typename);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ProductType3Activity.this.setResult(RESULT_OK, getIntent());
			ProductType3Activity.this.finish();
			return false;
		}
		return false;
	}
	
	public void showMenuTypePage3(String typeid,String cname)
	{
		pagetag = "typepage3";
		try{
//			mViewFlipper.showNext();
			back_btn.setVisibility(View.VISIBLE);
//			back_btn.setText(cname);
			menustr2 = cname;
			titletxt.setText(cname);
			back_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ProductType3Activity.this.setResult(RESULT_OK, getIntent());
					ProductType3Activity.this.finish();
				}
			});
			
			slistView3 = (ListView)findViewById(R.id.ListView_catalog3);
			pb3.setVisibility(View.VISIBLE);
			slistView3.setVisibility(View.GONE);
			
			loadThreadData3(typeid);
			
			// 添加点击
			slistView3.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					String typeid = myapp.getMenuList3().get(arg2).get("pkid").toString();
					String cname = myapp.getMenuList3().get(arg2).get("cname").toString();
					step = "3";
					showProductListMume(typeid,cname);
				}
			});
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void loadThreadData3(final String typeid)
	{
		typeId = typeid;
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				List<Map<String,Object>> list = myapp.getMenuList3();
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				if(msg.obj != null)
				{
					List<Map<String,Object>> dlist = (List<Map<String,Object>>)msg.obj;
					// 生成适配器的Item和动态数组对应的元素
					SpecialAdapter listItemAdapter = new SpecialAdapter(ProductType3Activity.this, dlist,// 数据源
							R.layout.categorychilditem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] {"cname" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textContent},share,"ico");
					
	//				slistView.setDividerHeight(0);
					// 添加并且显示
					slistView3.setAdapter(listItemAdapter);
					pb3.setVisibility(View.GONE);
					slistView3.setVisibility(View.VISIBLE);
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
