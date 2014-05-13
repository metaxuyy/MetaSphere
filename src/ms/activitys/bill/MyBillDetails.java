package ms.activitys.bill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.map.BaiduMap;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyBillDetails extends Activity{
	
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// 首页
	private final int TOOLBAR_ITEM_MAP = 1;// 退后
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// 前进
	private final int TOOLBAR_ITEM_NFC = 3;// 创建
	
	AlertDialog menuDialog;// menu菜单Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //界面当前的view
	
	String cviewstr; //界面当前一个view
	String qviewstr; //界面前一个view
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_details_list_view_new);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = MyBillDetails.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		showBillListPage();
		
	}
	
	/**
	 * 显示我的卡片
	 */
	public void showMyCards()
	{
		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,HomePage.class);
//		    Bundle bundle = new Bundle();
////			bundle.putString("role", "Cleaner");
//			intent.putExtras(bundle);
//		    startActivity(intent);//开始界面的跳转函数
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
////		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 显示店得分布地图
	 */
	public void showMyMap()
	{
		try{
			Intent intent = new Intent();
//		    intent.setClass( this,MapPage.class);
			intent.setClass( this,BaiduMap.class);
		    Bundle bundle = new Bundle();
//			bundle.putString("role", "Cleaner");
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//关闭显示的Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private SpecialAdapter getServiceMenuAdapter(List<Map<String,Object>> data) {
		SpecialAdapter listItemAdapter = new SpecialAdapter(this, data,// 数据源
				R.layout.bill_details_view,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "no", "startTime","fee","contype"},
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.consumptionNo, R.id.ConsumerTime,R.id.totalConsume,R.id.ConsumeType },share,"ico");
		return listItemAdapter;
	}
	
	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemtext", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage","itemtext" },
				new int[] { R.id.item_image,R.id.item_text });
		return simperAdapter;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			new AlertDialog.Builder(this).setTitle("提示")
//			.setMessage("确定退出?").setIcon(R.drawable.error2)
//			.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
////							setResult(RESULT_OK);// 确定按钮事件
////							android.os.Process.killProcess(android.os.Process.myPid());
////							finish();
//							Intent startMain = new Intent(Intent.ACTION_MAIN);
//					         startMain.addCategory(Intent.CATEGORY_HOME);
//					         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					         startActivity(startMain);
//					         System.exit(0);
//						}
//					}).setNegativeButton("取消",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// 取消按钮事件
//						}
//					}).show();
//			return false;
//		}
//		return false;
//	}
	
	/**
	 * 显示账单详细列表
	 */
	public void showBillListPage()
	{
		try{
			ListView slistView = (ListView)findViewById(R.id.ListView_catalog);
			final List<Map<String,Object>> dlist = getBillListData();
			slistView.setAdapter(getServiceMenuAdapter(dlist));
			
			TextView totleItem = (TextView)findViewById(R.id.totalitem);
			totleItem.setText(myapp.getCustomerName() + "  "+this.getString(R.string.mybill_details_lable_1)+myapp.getTotalConsume());
			
			// 添加点击
			slistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
				}
			});
			
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//			lp.addRule(RelativeLayout.ABOVE, R.id.GridView_toolbar);
//			sview.setLayoutParams(lp);

			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getBillListData()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			
			jobj = api.getBillDetatil(myapp.getTotailId());
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getBillList(jArr);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getBillList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = ""; // 账单ID
				if(dobj.has("kConsumptionId"))
					pkid = (String) dobj.get("kConsumptionId"); 
				
				String no = ""; // 唱歌消费编号
				if(dobj.has("no"))
					no = (String) dobj.get("no"); 
				
				String startTime = ""; // 开始时间
				if(dobj.has("startTime"))
					startTime = (String) dobj.get("startTime"); 
				
				String endTime = ""; // 结束时间
				if(dobj.has("endTime"))
				{
					endTime = (String) dobj.get("endTime"); 
				}
				
				String fee = ""; // 消费金额
				if(dobj.has("fee"))
					fee = (String) dobj.get("fee"); 
				
				String contype = ""; // 费用类型
				if(dobj.has("contype"))
					contype = (String) dobj.get("contype"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("no", this.getString(R.string.mybill_details_lable_2)+no);
				String stime = "";
				if(startTime != null)
				{
					String [] stimes = startTime.split(" ");
					stime = stimes[0];
				}
				map.put("startTime", this.getString(R.string.mybill_details_lable_3)+stime);
				map.put("fee", fee);
				map.put("contype", contype);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
}
