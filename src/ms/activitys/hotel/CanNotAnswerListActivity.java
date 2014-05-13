package ms.activitys.hotel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import ms.activitys.R;
import ms.activitys.MainTabActivity;
import ms.activitys.hotel.MyListView.OnRefreshListener;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapter;
import ms.globalclass.map.MyApp;

public class CanNotAnswerListActivity extends Activity implements OnScrollListener{

	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private ListView mylist;
	private LinearLayout progLayout;
	private LinearLayout msg_layout;
	private List<Map<String, Object>> messageItem;
	private int page = 0;
	private SpecialAdapter simperAdapter;
	private MyLoadingDialog loadDialog;
	private boolean islast = false;
    private int istag = 0;
    private int lastItem = 0; 
    private int listCount = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.can_not_answer_list);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		messageItem = new ArrayList<Map<String,Object>>();
		
		initView();
	}
	
	public void initView()
	{
		try{
			progLayout = (LinearLayout)findViewById(R.id.progLayout);
			msg_layout = (LinearLayout)findViewById(R.id.msg_layout);
			
			Button break_btn = (Button)findViewById(R.id.home);
			break_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openMainView();
				}
			});
			
			mylist = (ListView) findViewById(R.id.listvid);
			mylist.setOnScrollListener(this);
//			mylist.setOnScrollListener(new OnScrollListener() {
//				
//				@Override
//				public void onScrollStateChanged(AbsListView view, int scrollState) {
//					// TODO Auto-generated method stub
//					//滚动到底部   
//		            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {   
//		                View v=(View) view.getChildAt(view.getChildCount()-1);   
//		                int[] location = new  int[2] ;   
//		                v.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标   
//		                int y=location [1];   
//		   
//		                Log.e("x"+location[0],"y"+location[1]);   
//		                if (view.getLastVisiblePosition()!=getLastVisiblePosition && lastVisiblePositionY!=y)//第一次拖至底部   
//		                {  
//		                    Toast.makeText(view.getContext(), "再次拖至底部，即可翻页",5000).show();   
//		                    getLastVisiblePosition=view.getLastVisiblePosition();   
//		                    lastVisiblePositionY=y;   
//		                    return;   
//		                }   
//		                else if (view.getLastVisiblePosition()==getLastVisiblePosition && lastVisiblePositionY==y)//第二次拖至底部   
//		                {   
////		                    mCallback.execute(">>>>>拖至底部");   
//		                	getNotAnswerListData(String.valueOf(page++),"fy");
//		                }   
//		            }   
//		               
//		            //未滚动到底部，第二次拖至底部都初始化   
//		            getLastVisiblePosition=0;      
//		            lastVisiblePositionY=0;   
//				}
//				
//				@Override
//				public void onScroll(AbsListView view, int firstVisibleItem,
//						int visibleItemCount, int totalItemCount) {
//					// TODO Auto-generated method stub
////					if(totalItemCount > 0)
////					{
////						if(visibleItemCount+firstVisibleItem==totalItemCount){
////		                    Log.e("log", "滑到底部");
////		                    getNotAnswerListData(String.valueOf(page++),"fy");
////		                }
////					}
//				}
//			});
			
			
			getNotAnswerListData(String.valueOf(page),null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lastItem = firstVisibleItem + visibleItemCount;
//		System.out.println("lastItem:" + lastItem);
		int rowCount = totalItemCount - (page*10);
		if(rowCount > 0 && rowCount < 11)
		{
			if(!islast)
			{
//				loadingLayout.setVisibility(View.GONE);
//				try{
//					listview.removeFooterView(loadingLayout);
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
				islast = true;
				istag++;
			}
			else
			{
				if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
				{
					mylist.setSelection((messageItem.size())); 
					istag++;
				}
				else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
				{
					mylist.setSelection((messageItem.size())); 
					istag++;
				}
			}
		}
		else
		{
			if(istag == 1 && totalItemCount == firstVisibleItem + visibleItemCount)
			{
				mylist.setSelection((messageItem.size())); 
				istag++;
			}
			else if(istag == 1 && totalItemCount < firstVisibleItem + visibleItemCount)
			{
				mylist.setSelection((messageItem.size())); 
				istag++;
			}
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (lastItem == simperAdapter.getCount() && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if(listCount > 9)
			{
				page = page + 1;
				getNotAnswerListData(String.valueOf(page),"fy");
			}
		}
	}
	
	public void getNotAnswerListData(final String pageNo,final String type)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				if(type != null)
					msg.what = 2;
				else
					msg.what = 0;
				
				try {
						JSONObject jobj = api.findLCanNotAnswerucene(pageNo);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
							List<Map<String,Object>> list = getNotAnswerList(jArr);
							listCount = list.size();
							msg.obj = list;
						}
						else
						{
							msg.obj = null;
						}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void delNotAnswerAll(final String id)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
						JSONObject jobj = api.delLCanNotAnsweruceneIndex(id);
						if(jobj != null)
						{
							String tag = jobj.getString("tag");
							if(tag.equals("success"))
								msg.obj = "1";
							else
								msg.obj = null;
						}
						else
						{
							msg.obj = null;
						}
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
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
				List<Map<String, Object>> list = (List<Map<String, Object>>)msg.obj;
				if(list != null)
				{
					progLayout.setVisibility(View.GONE);
					mylist.setVisibility(View.VISIBLE);
					
//					messageItem = list3;
					messageItem.addAll(list);
					// 生成适配器的Item和动态数组对应的元素
					simperAdapter = new SpecialAdapter(CanNotAnswerListActivity.this, messageItem,
							R.layout.not_answer_list_item, new String[] { "answert"},
							new int[] { R.id.doc_txt },195,165,false,share,"ico");
					
					mylist.setDividerHeight(0);
					// 添加并且显示
					mylist.setAdapter(simperAdapter);
					
					mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							String id = messageItem.get(arg2).get("id").toString();
			
							openMuneView(id);
							return true;
						}
					});
					
					mylist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							String id = messageItem.get(arg2).get("id").toString();
							String answert = messageItem.get(arg2).get("answert").toString();
							
							openNotAnswersView(answert);
						}
					});
				}
				else
				{
					mylist.setAdapter(null);
					mylist.setVisibility(View.GONE);
					progLayout.setVisibility(View.GONE);
					msg_layout.setVisibility(View.VISIBLE);
				}
				break;
			case 1:
				String tag = (String)msg.obj;
				if(tag != null)
				{
					page = 0;
					messageItem = new ArrayList<Map<String,Object>>();
					makeText(getString(R.string.setting_success_clear));
					getNotAnswerListData(String.valueOf(page),null);
				}
				else
					makeText(getString(R.string.hotel_label_18));
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				List<Map<String, Object>> lists = (List<Map<String, Object>>)msg.obj;
				if(lists != null)
				{
					messageItem.addAll(lists);
					if(simperAdapter != null)
						simperAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	};
	
	public void openNotAnswersView(String answert)
	{
		Intent intent = new Intent();
	    intent.setClass( this,TeachQuestionsAnsweredActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putString("answert", answert);
		intent.putExtras(bundle);
	    startActivity(intent);//开始界面的跳转函数
	    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
	    this.finish();
	}
	
	public void openMuneView(final String id)
	{
		final String [] items = new String[]{getString(R.string.hotel_label_22)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == 0)
				{
					showMyLoadingDialog();
					delNotAnswerAll(id);
					dialog.dismiss();
				}
			}
		});
		
		alertDialog = builder.create();
		alertDialog.setTitle(getString(R.string.app_name));
		alertDialog.show();
	}
	
	public List<Map<String,Object>> getNotAnswerList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String id = "";
				if(dobj.has("id"))
					id = (String) dobj.get("id");
				
				String answert = "";
				if(dobj.has("answert"))
					answert = (String) dobj.get("answert"); 
			
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", id);
				map.put("answert", answert);
		
//				messageItem.add(map);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
		    intent.setClass( this,MainTabActivity.class);
		    Bundle bundle = new Bundle();
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMyLoadingDialog() {
		loadDialog = new MyLoadingDialog(this,getString(R.string.map_lable_11), R.style.MyDialog);
		loadDialog.show();
	}
	
	public boolean isInetnState() {
		boolean inetnState = true;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			inetnState = false;
		} else {
			inetnState = true;
		}
		return inetnState;
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainView();
			return false;
		}
		return false;
	}
}
