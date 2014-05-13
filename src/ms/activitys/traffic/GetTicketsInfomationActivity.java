package ms.activitys.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

public class GetTicketsInfomationActivity extends Activity {
    /** Called when the activity is first created. */
	private ProgressDialog progressDialog;
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
    private List<Map<String, Object>> mData;
    private boolean showsToast = false;
    private String [] typeitems;
    private String typetag = "0";
    private String startStation;
    private String endStaction;
    private String type;
    private TextView titletxt;
    private Animation mShowAction = null;  
    private Animation mHiddenAction = null;  
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_view);
        myapp = (MyApp)this.getApplicationContext();
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		typeitems = new String [3];
		typeitems[0] =	getString(R.string.traffic_lable_2);
		typeitems[1] =	getString(R.string.traffic_lable_3);
		typeitems[2] =	getString(R.string.traffic_lable_4);
		
		 // ����  
        mShowAction = AnimationUtils.loadAnimation(this,
				R.anim.menu_in);  
        
        mHiddenAction = AnimationUtils.loadAnimation(this,
				R.anim.menu_out);  
		
		titletxt = (TextView)findViewById(R.id.TextView01);
		titletxt.setText(getString(R.string.traffic_lable_2)+getString(R.string.traffic_lable_25));
		// TODO Auto-generated method stub
        startStation = "�Ϻ�";//����վ
        endStaction = "����" ;//�յ�վ
        type = "train";
        String[] station = {startStation,endStaction,type};
        new getdataTaskTrain().execute(station);
        
        final LinearLayout sch_layout = (LinearLayout)findViewById(R.id.sch_layout);
        
        ImageView sel_btn = (ImageView)findViewById(R.id.sel_btn);
        sel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showsToast)
				{
					showsToast = false;
//					sch_layout.startAnimation(mHiddenAction);
					sch_layout.setVisibility(View.GONE);
				}
				else
				{
					showsToast = true;
//					sch_layout.startAnimation(mShowAction);
					sch_layout.setVisibility(View.VISIBLE);
				}
			}
		});
        
        ImageButton type_btn = (ImageButton)findViewById(R.id.type_btn);
        type_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorDialog();
			}
		});
        
        Button imgbtnSh = (Button)findViewById(R.id.sch1);
        imgbtnSh.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText etxStart = (EditText)findViewById(R.id.txtS1);
				EditText etxEnd = (EditText)findViewById(R.id.txtS2);
				startStation = etxStart.getText().toString();
				endStaction = etxEnd.getText().toString();
				if(startStation.equals("")){
					startStation = etxStart.getHint().toString();
				}
				if(endStaction.equals("")){
					endStaction =  etxEnd.getHint().toString();					
				}
				
				if(typetag.equals("0"))
				{
					type = "train";
					String[] station = {startStation,endStaction,type};
				    new getdataTaskTrain().execute(station);
				}
				else if(typetag.equals("1"))
				{
					type = "fly";
					String[] station = {startStation,endStaction,type};
				    new getdataTaskAircraft().execute(station);
				}
				else if(typetag.equals("2"))
				{
					type = "bus";
					String[] station = {startStation,endStaction,type};
				    new getdataTaskBus().execute(station);
				}
			    
				showsToast = false;
//				sch_layout.startAnimation(mHiddenAction);
			    sch_layout.setVisibility(View.GONE);
			}
		});
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_OK, getIntent());
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
			finish();
			return false;
		}
		return false;
	}
	
	public void showColorDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		AlertDialog alertDialog = null;
	
		builder.setItems(typeitems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == 0)
				{
					typetag = "0";
					type = "train";
					titletxt.setText(getString(R.string.traffic_lable_2)+getString(R.string.traffic_lable_25));
					String[] station = {startStation,endStaction,type};
				    new getdataTaskTrain().execute(station);
				}
				else if(which == 1)
				{
					typetag = "1";
					type = "fly";
					titletxt.setText(getString(R.string.traffic_lable_3)+getString(R.string.traffic_lable_25));
					String[] station = {startStation,endStaction,type};
				    new getdataTaskAircraft().execute(station);
				}
				else if(which == 2)
				{
					typetag = "2";
					type = "bus";
					titletxt.setText(getString(R.string.traffic_lable_4)+getString(R.string.traffic_lable_25));
					String[] station = {startStation,endStaction,type};
				    new getdataTaskBus().execute(station);
				}
			}
		});
		
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	//��
	public class getdataTaskTrain extends AsyncTask<String, Void, Integer >{
	
	//ִ�в���Ԥ׼��
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		progressDialog = new ProgressDialog(GetTicketsInfomationActivity.this);
//		progressDialog.setTitle("���Ժ�");
		progressDialog.setMessage(getString(R.string.map_lable_11));
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	//ִ�м�������
	@Override
	protected Integer doInBackground(String... station) {
		// TODO Auto-generated method stub
		String start = station[0];
		String end = station[1];
		String type = station[2];
		JSONObject jsons;
		
		try {
			mData =new ArrayList<Map<String, Object>>();
			jsons = api.getTicketsInfomation(start,end,type);
		    JSONArray json = jsons.getJSONArray("data");
			System.out.println(json.length()+"-----sssssssssssss\\\\\\\\");
			if(json.length()>0){
				for(int i = 0; i < json.length(); i++){
					JSONObject jsb = (JSONObject)json.get(i);
					String data0 = jsb.getString("data0");//����
					String data1 = jsb.getString("data1");//����
					String data2 = jsb.getString("data2");//����վ
					String data3 = jsb.getString("data3");//����ʱ��
					String data4 = jsb.getString("data4");//����վ
					String data5 = jsb.getString("data5");//����ʱ��
					String data6 = jsb.getString("data6");//��ʱ
					String data7 = jsb.getString("data7");//���
					String data8 = jsb.getString("data8");//Ӳ��
					String data9 = jsb.getString("data9");//����
					String data10 = jsb.getString("data10");//Ӳ����
					String data11 = jsb.getString("data11");//������
					Map<String, Object> maps = new HashMap<String, Object>();
					maps.put("checi",data0 );
					maps.put("type",data1 );
					maps.put("start",data2+" : "+data3+"/"+getString(R.string.traffic_lable_5) );
					maps.put("end",data4+" : "+data5+"/"+getString(R.string.traffic_lable_6));
					maps.put("licheng",getString(R.string.traffic_lable_7)+" ��"+data7+"km" );
//					maps.put("yingzuo",getString(R.string.traffic_lable_8)+" ��"+data8+"��" );
//					maps.put("ruanzuo",getString(R.string.traffic_lable_9)+" ��"+data9+"��" );
					maps.put("yingzuo",data8);
					maps.put("ruanzuo",data9);
					maps.put("yingxia",getString(R.string.traffic_lable_10)+" ��"+data10+"");
					maps.put("ruanxia",getString(R.string.traffic_lable_11)+" �� "+data11+"");
					maps.put("usetimes",getString(R.string.traffic_lable_12)+" ��"+data6);
					mData.add(maps);
				}
			}else{
				progressDialog.cancel();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	//���ݼ��غ�ִ�и���UI
	@Override
	protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
		progressDialog.cancel();
		if(mData.size()==0){
			displayText(getString(R.string.traffic_lable_13));
		}else{
			displayText(getString(R.string.traffic_lable_14));
			ListView listview=(ListView)findViewById(R.id.ListView01);
    		SimpleAdapter listItemAdapter = new SimpleAdapter(GetTicketsInfomationActivity.this, mData,
    		    		R.layout.itemsdatas, 
    		    		new String[]{
    				   "checi","type","start","end","yingzuo","ruanzuo","usetimes","licheng"},
    		    		new int[]{ 
    				    R.id.station_num_TextView,R.id.station_typeName_TextView,
    	        		R.id.station_startName_TextView,R.id.station_arriveName_TextView,
    	        		R.id.station_p1_TextView,R.id.station_p2_TextView,
    	        		R.id.station_p4_TextView,R.id.station_p5_TextView,});
   			 listview.setAdapter(listItemAdapter);	
   			 listview.setOnItemClickListener(new OnItemClickListener() {
	             
					public void onItemClick(AdapterView<?> arg0, View convertView,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
		               final View view = LayoutInflater.from(GetTicketsInfomationActivity.this).inflate(R.layout.custdialog, null);
						
		               TextView txt_infors = (TextView)view.findViewById(R.id.txt_infors);
		               txt_infors.setText("021-3363-2222 ת 18");
		               
		               TextView yutime = (TextView)view.findViewById(R.id.yutime);
		               yutime.setVisibility(View.VISIBLE);
		               yutime.setText(getString(R.string.traffic_lable_29));
		               
		               WindowManager m = getWindowManager();    
		               Display d = m.getDefaultDisplay();  //Ϊ��ȡ��Ļ����    
		                   
		               int width = (int) (d.getWidth() * 0.9);    //�������Ϊ��Ļ��0.8   
		                   
		               LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
		               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,57);
		               topPanel.setLayoutParams(lp);
		               
		               final Dialog myDialog = new Dialog(GetTicketsInfomationActivity.this, R.style.AliDialog);
			   		   myDialog.setContentView(view);
			   		   myDialog.show();
		   			
		               Button btnenter = (Button)view.findViewById(R.id.enter);
		               btnenter.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
//							    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:021-3363-2222"));
//							    GetTicketsInfomationActivity.this.startActivity(intent);
							    Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:021-3363-2222"));
					            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					            startActivity(intent);
							}
						});
		               
		               Button btncls = (Button)view.findViewById(R.id.cancel);
		               btncls.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								myDialog.cancel();
							}
						});
					}
   			 	});
			}
		}
	}

	/**
	 * �첽�������ݣ�����UI�ɻ�
	 * */
	public class getdataTaskAircraft extends AsyncTask<String, Void, Integer >{
		
		//ִ�в���Ԥ׼��
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog=new ProgressDialog(GetTicketsInfomationActivity.this);
//			progressDialog.setTitle("���Ժ�");
			progressDialog.setMessage(getString(R.string.map_lable_11));
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		//ִ�м�������
		@Override
		protected Integer doInBackground(String... station) {
			// TODO Auto-generated method stub
			String start=station[0];
			String end=station[1];
			String type=station[2];
			JSONObject jsons;
			try {
				mData =new ArrayList<Map<String, Object>>();
				jsons = api.getTicketsInfomation(start,end,type);
				JSONArray json = jsons.getJSONArray("data");
				System.out.println(json.length()+"-----");
				if(json != null){
					for(int i = 0; i < json.length(); i++){
							JSONObject jsb = (JSONObject)json.get(i);
							String data0 = jsb.getString("data0");//����
							String data1 = jsb.getString("data1");//���չ�˾
							String data2 = jsb.getString("data2");//����վ
							String data3 = jsb.getString("data3");//��������
							String data4 = jsb.getString("data4");//����վ
							String data5 = jsb.getString("data5");//�۸�
							String data6 = jsb.getString("data6");//�ۿ�
							String data7 = jsb.getString("data7");//�Ǽ�ʱ��
							Map<String, Object> maps = new HashMap<String, Object>();
							maps.put("nums", data0);
							maps.put("busname", data1);
							maps.put("start", getString(R.string.traffic_lable_15)+" : "+data2);
							maps.put("end", getString(R.string.traffic_lable_16)+" : "+data4);
							maps.put("prices", getString(R.string.traffic_lable_17)+" : "+data5+"��");
							maps.put("zhek", getString(R.string.traffic_lable_18)+" ��"+data6);
							maps.put("starttime", getString(R.string.traffic_lable_19)+" ��"+data3);
							maps.put("endtime", getString(R.string.traffic_lable_20)+" ��"+data7);
							mData.add(maps);
					}
				}else{
					progressDialog.cancel();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				progressDialog.cancel();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				progressDialog.cancel();
				e.printStackTrace();
			}
			return null;
		}
		//���ݼ��غ�ִ�и���UI
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			progressDialog.cancel();
			if(mData.size()==0){
				displayText(getString(R.string.traffic_lable_13));
			}else{
				displayText(getString(R.string.traffic_lable_14));	
			}
			ListView listview=(ListView)findViewById(R.id.ListView01);
			SimpleAdapter listItemAdapter = new SimpleAdapter(GetTicketsInfomationActivity.this, mData,
			    		R.layout.itemsairdatas, 
			    		new String[]{
					   "nums","busname","start","end","prices","zhek","starttime","endtime"},
			    		new int[]{ 
				    R.id.station_airtypeName_TextView,R.id.station_ariName,
	        		R.id.station_startName_TextView,R.id.station_endName_TextView,
	        		R.id.airprices,R.id.arikilometer,R.id.airstrtTime,
	        		R.id.airother});
			
				 listview.setAdapter(listItemAdapter);
				 listview.setOnItemClickListener(new OnItemClickListener() {
	             
					public void onItemClick(AdapterView<?> arg0, View convertView,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
		               final View view = LayoutInflater.from(GetTicketsInfomationActivity.this).inflate(R.layout.custdialog, null);
						
		               TextView txt_infors = (TextView)view.findViewById(R.id.txt_infors);
		               txt_infors.setText("021-3363-2222ת18");
		               
		               TextView yutime = (TextView)view.findViewById(R.id.yutime);
		               yutime.setVisibility(View.GONE);
		               
		               WindowManager m = getWindowManager();    
		               Display d = m.getDefaultDisplay();  //Ϊ��ȡ��Ļ����    
		                   
		               int width = (int) (d.getWidth() * 0.9);    //�������Ϊ��Ļ��0.8   
		               
		               LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
		               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,57);
		               topPanel.setLayoutParams(lp);
		               
		               final Dialog myDialog = new Dialog(GetTicketsInfomationActivity.this, R.style.AliDialog);
			   		   myDialog.setContentView(view);
			   		   myDialog.show();
		   			
		               Button btnenter = (Button)view.findViewById(R.id.enter);
		               btnenter.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
//							    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:021-3363-2222"));
//							    GetTicketsInfomationActivity.this.startActivity(intent);
								Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:021-3363-2222"));
					            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					            startActivity(intent);
							}
						});
		               
		               Button btncls = (Button)view.findViewById(R.id.cancel);
		               btncls.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								myDialog.cancel();
							}
						});
					}
			});
		}
	}	

	public class getdataTaskBus extends AsyncTask<String, Void, Integer >{
		
		//ִ�в���Ԥ׼��
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog=new ProgressDialog(GetTicketsInfomationActivity.this);
//			progressDialog.setTitle("���Ժ�");
			progressDialog.setMessage(getString(R.string.map_lable_11));
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		//ִ�м�������
		@Override
		protected Integer doInBackground(String... station) {
			// TODO Auto-generated method stub
			String start=station[0];
			String end=station[1];
			String type=station[2];
			JSONObject jsons;
			try {
				   jsons = api.getTicketsInfomation(start,end,type);
					mData =new ArrayList<Map<String, Object>>();
					JSONArray json = jsons.getJSONArray("data");
					if(json != null){
						for(int i = 0; i < json.length(); i++){
							JSONObject jsb = (JSONObject)json.get(i);
						    String data0 = jsb.getString("data0");//����վ
							String data1 = jsb.getString("data1");//����վ
							String data2 = jsb.getString("data2");//����ʱ��
							String data3 = jsb.getString("data3");//���
							String data4 = jsb.getString("data4");//����
							String data5 = jsb.getString("data5");//ȫ��Ʊ��
							String data7 = jsb.getString("data7");//����
							if(data7==null||data7.equals("")){
								data7="-- --";
							}
							Map<String, Object> maps = new HashMap<String, Object>();
							maps.put("start", getString(R.string.traffic_lable_15)+" : "+data0);
							maps.put("end", getString(R.string.traffic_lable_16)+" : "+data1);
							maps.put("starttime", getString(R.string.traffic_lable_21)+" ��"+data2);
							maps.put("prices", getString(R.string.traffic_lable_22)+" : "+data5+"��");
							maps.put("meter", getString(R.string.traffic_lable_7)+" : "+data3+"km");
							maps.put("other", getString(R.string.traffic_lable_23)+" : "+data7);
							maps.put("cartype", data4);
							mData.add(maps);
						}
					}else {
						progressDialog.cancel();
					}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				progressDialog.cancel();
				e.printStackTrace();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				progressDialog.cancel();
				e.printStackTrace();
			}
			return null;
		}
		//���ݼ��غ�ִ�и���UI
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			progressDialog.cancel();
			if(mData.size()==0){
				displayText(getString(R.string.traffic_lable_13));
			}else{
				displayText(getString(R.string.traffic_lable_14));	
			}
			ListView listview=(ListView)findViewById(R.id.ListView01);
    		SimpleAdapter listItemAdapter = new SimpleAdapter(GetTicketsInfomationActivity.this, mData,
    		    		R.layout.itemsbusdatas, 
    		    		new String[]{
    				   "cartype","start","end","prices","meter","starttime","other"},
    		    		new int[]{ 
    				    R.id.station_typeName_TextView,R.id.station_startName_TextView,
    	        		R.id.station_endName_TextView,R.id.prices,
    	        		R.id.kilometer,R.id.strtTime,
    	        		R.id.other});
    		
   			 listview.setAdapter(listItemAdapter);	
	   			listview.setOnItemClickListener(new OnItemClickListener() {
		             
					public void onItemClick(AdapterView<?> arg0, View convertView,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
		               final View view = LayoutInflater.from(GetTicketsInfomationActivity.this).inflate(R.layout.custdialog, null);
						
		               TextView txt_infors = (TextView)view.findViewById(R.id.txt_infors);
		               txt_infors.setText("021-58952234");
		               
		               TextView yutime = (TextView)view.findViewById(R.id.yutime);
		               yutime.setVisibility(View.GONE);
		               
		               WindowManager m = getWindowManager();    
		               Display d = m.getDefaultDisplay();  //Ϊ��ȡ��Ļ����    
		                   
		               int width = (int) (d.getWidth() * 0.9);    //�������Ϊ��Ļ��0.8   
		               
		               LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
		               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,57);
		               topPanel.setLayoutParams(lp);
		               
		               final Dialog myDialog = new Dialog(GetTicketsInfomationActivity.this, R.style.AliDialog);
			   		   myDialog.setContentView(view);
			   		   myDialog.show();
		   			
		               Button btnenter = (Button)view.findViewById(R.id.enter);
		               btnenter.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
//							    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:021-58952234"));
//							    GetTicketsInfomationActivity.this.startActivity(intent);
								Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:021-3363-2222"));
					            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					            startActivity(intent);
							}
						});
		               
		               Button btncls = (Button)view.findViewById(R.id.cancel);
		               btncls.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								myDialog.cancel();
							}
						});
					}
			});
		}
	}
	
	public void displayText(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}