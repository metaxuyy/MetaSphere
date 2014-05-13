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
	
	private final int TOOLBAR_ITEM_MYCARD = 0;// ��ҳ
	private final int TOOLBAR_ITEM_MAP = 1;// �˺�
	private final int TOOLBAR_ITEM_CAOMIAO = 2;// ǰ��
	private final int TOOLBAR_ITEM_NFC = 3;// ����
	
	AlertDialog menuDialog;// menu�˵�Dialog
//	ListView listView;
	GridView listView;
	GridView menuGrid, toolbarGrid;
	View menuView;
	View cview; //���浱ǰ��view
	
	String cviewstr; //���浱ǰһ��view
	String qviewstr; //����ǰһ��view
	

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
	 * ��ʾ�ҵĿ�Ƭ
	 */
	public void showMyCards()
	{
		try{
//			Intent intent = new Intent();
//		    intent.setClass( this,HomePage.class);
//		    Bundle bundle = new Bundle();
////			bundle.putString("role", "Cleaner");
//			intent.putExtras(bundle);
//		    startActivity(intent);//��ʼ�������ת����
//		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
////		    this.finish();//�ر���ʾ��Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * ��ʾ��÷ֲ���ͼ
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
		    startActivity(intent);//��ʼ�������ת����
		    overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
//		    this.finish();//�ر���ʾ��Activity
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private SpecialAdapter getServiceMenuAdapter(List<Map<String,Object>> data) {
		SpecialAdapter listItemAdapter = new SpecialAdapter(this, data,// ����Դ
				R.layout.bill_details_view,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "no", "startTime","fee","contype"},
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.consumptionNo, R.id.ConsumerTime,R.id.totalConsume,R.id.ConsumeType },share,"ico");
		return listItemAdapter;
	}
	
	/**
	 * ����˵�Adapter
	 * 
	 * @param menuNameArray
	 *            ����
	 * @param imageResourceArray
	 *            ͼƬ
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
//			new AlertDialog.Builder(this).setTitle("��ʾ")
//			.setMessage("ȷ���˳�?").setIcon(R.drawable.error2)
//			.setPositiveButton("ȷ��",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
////							setResult(RESULT_OK);// ȷ����ť�¼�
////							android.os.Process.killProcess(android.os.Process.myPid());
////							finish();
//							Intent startMain = new Intent(Intent.ACTION_MAIN);
//					         startMain.addCategory(Intent.CATEGORY_HOME);
//					         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					         startActivity(startMain);
//					         System.exit(0);
//						}
//					}).setNegativeButton("ȡ��",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//							// ȡ����ť�¼�
//						}
//					}).show();
//			return false;
//		}
//		return false;
//	}
	
	/**
	 * ��ʾ�˵���ϸ�б�
	 */
	public void showBillListPage()
	{
		try{
			ListView slistView = (ListView)findViewById(R.id.ListView_catalog);
			final List<Map<String,Object>> dlist = getBillListData();
			slistView.setAdapter(getServiceMenuAdapter(dlist));
			
			TextView totleItem = (TextView)findViewById(R.id.totalitem);
			totleItem.setText(myapp.getCustomerName() + "  "+this.getString(R.string.mybill_details_lable_1)+myapp.getTotalConsume());
			
			// ��ӵ��
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
				
				String pkid = ""; // �˵�ID
				if(dobj.has("kConsumptionId"))
					pkid = (String) dobj.get("kConsumptionId"); 
				
				String no = ""; // �������ѱ��
				if(dobj.has("no"))
					no = (String) dobj.get("no"); 
				
				String startTime = ""; // ��ʼʱ��
				if(dobj.has("startTime"))
					startTime = (String) dobj.get("startTime"); 
				
				String endTime = ""; // ����ʱ��
				if(dobj.has("endTime"))
				{
					endTime = (String) dobj.get("endTime"); 
				}
				
				String fee = ""; // ���ѽ��
				if(dobj.has("fee"))
					fee = (String) dobj.get("fee"); 
				
				String contype = ""; // ��������
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
