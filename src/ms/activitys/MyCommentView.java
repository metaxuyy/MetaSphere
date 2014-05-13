package ms.activitys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
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
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

//import ms.activitys.vipcards.HomePage;
import ms.activitys.vipcards.CardsView.CommonGestureListener;
import ms.globalclass.EnvironmentShare;
import ms.globalclass.FileUtils;
import ms.globalclass.FormFile;
import ms.globalclass.FriendlyScrollView;
import ms.globalclass.U;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.SpecialAdapterMessage;
import ms.globalclass.map.MyApp;
import ms.globalclass.scroll.PageControlIconView;
import ms.globalclass.scroll.PageControlView;
import ms.globalclass.scroll.ScrollViewGroup;
import ms.globalclass.scroll.ScrollViewGroup.OnScreenChangeListener;

public class MyCommentView extends Activity{
	
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
	
	// 多媒体播放器
	private MediaPlayer mediaPlayer;
	// 多媒体录制器
	private MediaRecorder mediaRecorder = new MediaRecorder();
	// 音频文件
	private File audioFile;

	// 传给Socket服务器端的上传和下载标志
	private final int UP_LOAD = 1;
	private final int DOWN_LOAD = 2;
	
	private String storeid;
	private String storeName;
	
	private String fileUrl = "";
	private String fileName = "";
	private long fileSize;
	private ImageView mImageView;
	private Bitmap bitmap;
	
	private File out;
	private Uri uri;
	
	private ProgressDialog mypDialog;
	
	private SpecialAdapterMessage myAdapter;
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;  
	
	private ScrollViewGroup viewGroup;
	private PageControlIconView pageControl;
	private LinearLayout gridSmilesContainer;
	private boolean smilestag = false;
	private ViewFlipper mViewFlipper;
	private static final int FLING_MIN_DISTANCE = 100;  
	private static final int FLING_MIN_VELOCITY = 200;
	private int layoutindex = 0;
	private GestureDetector mGestureDetector; 
	private View view1;
	private View view2;
	private List<String> browlist = new ArrayList<String>();
	private ImageButton imgbtn;
	private int [] imgs = {R.drawable.smiles1,R.drawable.smiles2,R.drawable.smiles3,R.drawable.smiles4,R.drawable.smiles5,R.drawable.smiles6
			,R.drawable.smiles7,R.drawable.smiles8,R.drawable.smiles9,R.drawable.smiles10,R.drawable.smiles11
			,R.drawable.smiles12,R.drawable.smiles13,R.drawable.smiles14,R.drawable.smiles15,R.drawable.smiles16
			,R.drawable.smiles17,R.drawable.smiles18,R.drawable.smiles19,R.drawable.smiles20,R.drawable.smiles21
			,R.drawable.smiles22,R.drawable.smiles23,R.drawable.smiles24};
	private int [] imgs2 = {R.drawable.smiles25,R.drawable.smiles26,R.drawable.smiles27,R.drawable.smiles28,R.drawable.smiles29,R.drawable.smiles30
			,R.drawable.smiles31,R.drawable.smiles32,R.drawable.smiles33,R.drawable.smiles34,R.drawable.smiles35
			,R.drawable.smiles36,R.drawable.smiles37,R.drawable.smiles38,R.drawable.smiles39,R.drawable.smiles40
			,R.drawable.smiles41,R.drawable.smiles42,R.drawable.smiles43,R.drawable.smiles44,R.drawable.smiles45
			,R.drawable.smiles46,R.drawable.smiles47,R.drawable.emoji041};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_view);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = MyCommentView.this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		storeid = bunde.getString("storeid");
		storeName = bunde.getString("storeName");
		
		gridSmilesContainer = (LinearLayout)findViewById(R.id.gridSmilesContainer);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
		
		view1 = View.inflate(this, R.layout.gridsmiles, null);
		
		view2 = View.inflate(this, R.layout.gridsmiles2, null);
		
		pageControl=(PageControlIconView) findViewById(R.id.pageControl);
		pageControl.bindScrollViewGroup(mViewFlipper);
		
		mGestureDetector = new GestureDetector(new CommonGestureListener());  
		
		loadScrollView();
		
		loadImgGridView();
		
		showMyMessageList(0);
	}
	
	public void loadScrollView()
	{
		FriendlyScrollView scroll = (FriendlyScrollView) view1.findViewById(R.id.ScrollView01);
		scroll.setOnTouchListener(onTouchListener);
		scroll.setGestureDetector(mGestureDetector);
		
		FriendlyScrollView scroll2 = (FriendlyScrollView) view2.findViewById(R.id.ScrollView02);
		scroll2.setOnTouchListener(onTouchListener);
		scroll2.setGestureDetector(mGestureDetector);
	}
	
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		 
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};
	
	ImageGetter imageGetter = new ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            int id = MyCommentView.this.getResources().getIdentifier(MyCommentView.this.getPackageName()+":drawable/"+source,null,null);
            // 根据id从资源文件中获取图片对象
            Drawable d = getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
	
	public void loadImgGridView()
	{
//		viewGroup = (ScrollViewGroup) findViewById(R.id.scrollLayout);
		
		
		
		//获取GridView对象  
        GridView gridview = (GridView) view1.findViewById(R.id.gridview);
        gridview.setNumColumns(8);// 设置每行列数
        gridview.setGravity(Gravity.CENTER);// 位置居中
        gridview.setVerticalSpacing(20);// 垂直间隔
        gridview.setHorizontalSpacing(20);// 水平间隔

        final EditText contText = (EditText)findViewById(R.id.messagecontent);
        contText.addTextChangedListener(watcher); 
        contText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gridSmilesContainer.setVisibility(View.GONE);
				imgbtn.setImageResource(R.drawable.content1);
				smilestag = false;
				
			    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   
			    imm.showSoftInput(contText, 0);
				return false;
			}
		});
        
        final String [] values = {"smiles1","smiles2","smiles3","smiles4","smiles5",
        		"smiles6","smiles7","smiles8","smiles9","smiles10",
        		"smiles11","smiles12","smiles13","smiles14","smiles15",
        		"smiles16","smiles17","smiles18","smiles19","smiles20",
        		"smiles21","smiles22","smiles23","smiles24"};
        
        gridview.setAdapter(getGridAdapter(values,imgs));
        
        //事件监听  
        gridview.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
                int index = contText.getSelectionStart();//获取光标所在位置
                
                Editable edit = contText.getEditableText();//获取EditText的文字
                String imgname = values[position];
                browlist.add("<img src='"+imgname+"'/>");
				if (index < 0 || index >= edit.length()) {
					edit.append(Html.fromHtml("<img src='"+imgname+"'/>", imageGetter, null));
				} else {
					edit.insert(index, Html.fromHtml("<img src='"+imgname+"'/>", imageGetter, null));// 光标所在位置插入文字
				}
            }  
        }); 
        
        LinearLayout tlayout = (LinearLayout)findViewById(R.id.layout_customize_view);
		tlayout.addView(view1);
		
//		viewGroup.addView(view1);
		
		//获取GridView对象  
        GridView gridview2 = (GridView) view2.findViewById(R.id.gridview);
        gridview2.setNumColumns(8);// 设置每行列数
        gridview2.setGravity(Gravity.CENTER);// 位置居中
        gridview2.setVerticalSpacing(20);// 垂直间隔
        gridview2.setHorizontalSpacing(20);// 水平间隔


        final String [] values2 = {"smiles25","smiles26","smiles27","smiles28","smiles29",
        		"smiles30","smiles31","smiles32","smiles33","smiles34",
        		"smiles35","smiles36","smiles37","smiles38","smiles39",
        		"smiles40","smiles41","smiles42","smiles43","smiles44",
        		"smiles45","smiles46","smiles47","emoji041"};
        
        gridview2.setAdapter(getGridAdapter(values2,imgs2));
        //事件监听  
        gridview2.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                //此处的position为ID 所以要+1  
//            	EditText contText = (EditText)findViewById(R.id.messagecontent);
            	int index = contText.getSelectionStart();//获取光标所在位置
                 
                Editable edit = contText.getEditableText();//获取EditText的文字
                String imgname = values2[position];
                browlist.add("<img src='"+imgname+"'/>");
 				if (index < 0 || index >= edit.length()) {
 					edit.append(Html.fromHtml("<img src='"+imgname+"'/>", imageGetter, null));
 				} else {
 					edit.insert(index, Html.fromHtml("<img src='"+imgname+"'/>", imageGetter, null));// 光标所在位置插入文字
 				}
            }  
        });  
        
        LinearLayout tlayout2 = (LinearLayout)findViewById(R.id.layout_customize_view2);
        tlayout2.addView(view2);
		
//		viewGroup.addView(view2);
		
//		viewGroup.setCurrentScreenIndex(0);
		
//		pageControl=(PageControlIconView) findViewById(R.id.pageControl);
//		pageControl.bindScrollViewGroup(mViewFlipper);
		
//		viewGroup.setOnScreenChangeListener(new OnScreenChangeListener() {
//			
//			@Override
//			public void onScreenChange(int currentIndex) {
//				// TODO Auto-generated method stub
//				generatePageControl(currentIndex);
//			}
//		});
		
	}
	
    private TextWatcher watcher = new TextWatcher(){  
        
        @Override  
        public void afterTextChanged(Editable s) {  
            // TODO Auto-generated method stub 3 
        	 System.out.println("s="+s.toString());  
        }  
  
        @Override  
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
            // TODO Auto-generated method stub  1
        	if(count > 0)//减去字符
        	{
        		String str = s.toString();
        		String str2 = str.substring(start,start+count);
        		if(str2.equals("￼"))
        		{
        			int index = 0;
        			//如果字符串中有c
					while (str.indexOf("￼") != -1) {
						int listindex = str.indexOf("￼");
						// 将字符串出现c的位置之前的全部截取掉
						str = str.replaceFirst("￼", "*");
						if (listindex == start) {
							browlist.remove(index);
							break;
						}
						index++;
					}
        		}
        	}
        	 System.out.println("s="+s.toString()+" start="+start+" before="+count);  
        }  
  
        @Override  
        public void onTextChanged(CharSequence s, int start, int before,  
                int count) {  //2
        	if(count > 0)//加上字符
        	{
        		String str = s.toString();
        		String str2 = str.substring(start,start+count);
        		if(str2.equals("￼"))
        		{
        			int index = 0;
        			//如果字符串中有c
					while (str.indexOf("￼") != -1) {
						int listindex = str.indexOf("￼");
						// 将字符串出现c的位置之前的全部截取掉
						str = str.replaceFirst("￼", "*");
						if (listindex == start) {
							String newadd = browlist.get(browlist.size()-1);
							browlist.remove(browlist.size() - 1);
							browlist.add(index, newadd);
							break;
						}
						index++;
					}
        		}
        	}
//            System.out.println("s="+s.toString()+" start="+start+" before="+count);
        }  
          
    };  
	
	private SimpleAdapter getGridAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemtext", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage" },
				new int[] { R.id.item_image });
		return simperAdapter;
	}
	
	private void generatePageControl(int currentIndex) {
		pageControl.generatePageControl(currentIndex);
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
	
//	public void showCommentView()
//	{
//		try{
////			View view = LayoutInflater.from(this).inflate(R.layout.comment_detil_view,null);
//			Button btnStart = (Button) findViewById(R.id.btnStart);
//			Button btnStop = (Button) findViewById(R.id.btnStop);
//			Button btnPlay = (Button) findViewById(R.id.btnPlay);
//			Button btnUpLoad = (Button) findViewById(R.id.btnUpLoad);
//			Button btnDownLoad = (Button) findViewById(R.id.btnDownLoad);
////			btnStart.setOnClickListener(this);
////			btnStop.setOnClickListener(this);
////			btnPlay.setOnClickListener(this);
////			btnUpLoad.setOnClickListener(this);
////			btnDownLoad.setOnClickListener(this);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
	
//	public void onClick(View view) {
//		try {
//			String msg = "";
//			switch (view.getId()) {
//			// 开始录音
//			case R.id.btnStart:
//				if (!EnvironmentShare.haveSdCard()) {
//					Toast.makeText(this, "SD不存在，不正常录音！！", Toast.LENGTH_LONG).show();
//				}else{
//					// 设置音频来源(一般为麦克风)
//					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//					// 设置音频输出格式（默认的输出格式）
//					mediaRecorder
//					.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//					// 设置音频编码方式（默认的编码方式）
//					mediaRecorder
//					.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//					// 创建一个临时的音频输出文件
//					audioFile = File.createTempFile("record_", ".amr",EnvironmentShare.getAudioRecordDir());
//					// 设置录制器的文件保留路径
//					mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
//					// 准备并且开始启动录制器
//					mediaRecorder.prepare();
//					mediaRecorder.start();
//					msg = "正在录音...";
//				}
//				break;
//			// 停止录音	
//			case R.id.btnStop:
//				if (audioFile != null) {
//					mediaRecorder.stop();
//				}
//				msg = "已经停止录音.";
//				break;
//			// 录音文件的播放	
//			case R.id.btnPlay:
//				if (audioFile != null) {
//					mediaPlayer = new MediaPlayer();
//					// 为播放器设置数据文件 
//					mediaPlayer.setDataSource(audioFile.getAbsolutePath());
//					// 准备并且启动播放器
//					mediaPlayer.prepare();
//					mediaPlayer.start();
//					mediaPlayer
//							.setOnCompletionListener(new OnCompletionListener() {
//								@Override
//								public void onCompletion(MediaPlayer mp) {
//									setTitle("录音播放完毕.");
//
//								}
//							});
//					msg = "正在播放录音...";
//				}
//				break;
//			// 上传录音文件	
//			case R.id.btnUpLoad:
//				// 开始上传录音文件
//				if (audioFile != null) {
//					msg = "正在上传录音文件...";
//					audioUpLoad();
//				}
//				break;
//		    // 下载录音文件	
//			case R.id.btnDownLoad:
//				// 开始下载录音文件
//					msg = "正在下载录音文件...";
//					downLoadDFile();
//				break;	
//			}
//			// 更新标题栏 并用 Toast弹出信息提示用户
//			if (!msg.equals("")) {
//				setTitle(msg);
//				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//			}
//		} catch (Exception e) {
//			setTitle(e.getMessage());
//		}
//
//	}
	
	/**
	 * 上传 录音文件
	 */
	private void audioUpLoad(){
		new Thread(){
			public void run() {
				DataInputStream reader = null;
				DataOutputStream out = null;
				Socket socket = null;
				byte[] buf = null;
				try {
					// 连接Socket
					socket = new Socket(myapp.getHost(),myapp.getPort());
					// 1. 读取文件输入流
					reader = new DataInputStream(new BufferedInputStream(new FileInputStream(audioFile)));   
					// 2. 将文件内容写到Socket的输出流中   
					out = new DataOutputStream(socket.getOutputStream());   
					out.writeInt(UP_LOAD);
					out.writeUTF(audioFile.getName()); //附带文件名   
					
					int bufferSize = 2048; //2K   
					buf = new byte[bufferSize];   
					int read = 0;   
					// 将文件输入流 循环 读入 Socket的输出流中 
					while((read = reader.read(buf)) != -1){   
						out.write(buf, 0, read);   
					}   
					handler.sendEmptyMessage(UPLOAD_SUCCESS);
				} catch (Exception e) {
					handler.sendEmptyMessage(UPLOAD_FAIL);
				}finally{
					try {
						// 善后处理
						buf = null;
						out.close();
						reader.close();
						socket.close();
					} catch (Exception e) {
						
					}
				}
			};
		}.start();
		
	}
	
	/**
	 * 下载录音文件
	 */
	private void downLoadDFile(){
		new Thread(){
			public void run() {
				DataOutputStream writer = null;
				DataOutputStream socketOut = null;
				
				DataInputStream inPutStream = null;
				Socket socket = null;
				byte[] buf = null;
				try {
					// 连接Socket
					socket = new Socket(myapp.getHost(),myapp.getPort());
					// 向服务端发送请求及数据
					socketOut = new DataOutputStream(socket.getOutputStream());
					socketOut.writeInt(DOWN_LOAD);
					socketOut.writeUTF(audioFile.getName());
					
					// 1. 读取Socket的输入流   
					inPutStream = new DataInputStream(socket.getInputStream());   
					File downLoadFile  = new File(EnvironmentShare.getDownAudioRecordDir().getAbsolutePath()+ "/" + audioFile.getName());
					downLoadFile.createNewFile();
//            File downLoadFile = File.createTempFile( fileName, ".amr", EnvironmentShare.getDownAudioRecordDir());
					// 2. 获得文件的输出流
					writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(downLoadFile)));   
					
					int bufferSize = 2048; //2K   
					buf = new byte[bufferSize];   
					int read = 0;   
					// 将文件输入流 循环 读入 Socket的输出流中 
					while((read = inPutStream.read(buf)) != -1){   
						writer.write(buf, 0, read);   
					}   
					handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
				} catch (Exception e) {
					handler.sendEmptyMessage(DOWNLOAD_FAIL);
				}finally{
					try {
						// 善后处理
						buf = null;
						inPutStream.close();
						writer.close();
						socket.close();
					} catch (Exception e) {
						
					}
				}
			};
		}.start();
		
	}
	

	// Socket上传下载 结果标志
	private final int UPLOAD_SUCCESS = 1;
	private final int UPLOAD_FAIL = 2;
	private final int DOWNLOAD_SUCCESS = 3;
	private final int DOWNLOAD_FAIL = 4;
	
	// Socket 上传下载 结果 Handler处理类
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String showMessage = "";
			switch (msg.what) {
			case UPLOAD_SUCCESS:
				showMessage = MyCommentView.this.getString(R.string.record_upload_session);
				break;
			case UPLOAD_FAIL:
				showMessage = MyCommentView.this.getString(R.string.record_upload_error);
				break;
			case DOWNLOAD_SUCCESS:
				showMessage = MyCommentView.this.getString(R.string.record_download_session);
				break;
			case DOWNLOAD_FAIL:
				showMessage = MyCommentView.this.getString(R.string.record_download_error);;
				break;
			case 6:
				boolean bf = (Boolean)msg.obj;
				if(bf)
				{
					gridSmilesContainer.setVisibility(View.GONE);
					imgbtn.setImageResource(R.drawable.content1);
					smilestag = false;
					
					EditText contText = (EditText)findViewById(R.id.messagecontent);
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm.hideSoftInputFromWindow(contText.getWindowToken(),0);
			         
					makeText(MyCommentView.this.getString(R.string.send_msg_session_lable));
					mypDialog.dismiss();
					browlist.clear();
					showMyMessageList(1);
				}
				else
				{
					makeText(MyCommentView.this.getString(R.string.send_msg_error_lable));
				}
				break;
			default:
				break;
			}
			// 显示提示信息并 设置标题
			EnvironmentShare.showToastAndTitle(MyCommentView.this, showMessage, true);
		};
	};
	
	private SpecialAdapterMessage getStoreCommentAdapter2(List<Map<String,Object>> data) {
		SpecialAdapterMessage simperAdapter = new SpecialAdapterMessage(this, data,
				R.layout.mymessagelist, new String[] { "sendName", "img","content","file","sendTime" },
				new int[] { R.id.mysendname,R.id.yiman, R.id.mymessagecontent,R.id.uploadFileImg,R.id.mysendtime },"ico");
		return simperAdapter;
	}
	
	private SpecialAdapterMessage getStoreCommentAdapter(List<Map<String,Object>> data) {
		SpecialAdapterMessage simperAdapter = new SpecialAdapterMessage(this, data,
				R.layout.mymessagelist2, new String[] { "sendName", "img","content","file","sendTime","mediaTool" },
				new int[] { R.id.mysendname,R.id.yiman, R.id.mymessagecontent,R.id.uploadFileImg,R.id.mysendtime,R.id.mediaTool },"ico");
		return simperAdapter;
	}
	
	public void showMyMessageList(int tag)
	{
		try{
//			View view = LayoutInflater.from(this).inflate(R.layout.comment_detil_view,null);
			TextView snametext = (TextView)findViewById(R.id.srot_name);
			snametext.setText(storeName);
			
			ListView clistView = (ListView)findViewById(R.id.comment_list);
			final List<Map<String,Object>> dlist = getCommentListData();
			myAdapter = getStoreCommentAdapter(dlist);
			clistView.setAdapter(myAdapter);
			
			clistView.setSelection(dlist.size());
			// 添加点击
			clistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
//					String fileUrl = (String)dlist.get(arg2).get("filepath");
//					String message = (String)dlist.get(arg2).get("content");
//					if(fileUrl != null && !fileUrl.equals(""))
//					{
////						System.out.println("fileUrl=="+fileUrl);
//						showMessageDetails(fileUrl,message);
//					}
					Toast.makeText(MyCommentView.this, "Item clicked, position is:" + arg2,
						      Toast.LENGTH_SHORT).show();
				}
			});
			
			Button imgbut = (Button)findViewById(R.id.btnTape);
			
			imgbut.setOnClickListener(new Button.OnClickListener(){  
	            public void onClick(View v) {  
	            	openRecordingMenu();
	            }  
	        });  
			
			Button imgbut2 = (Button)findViewById(R.id.btnPhoto);
			
			imgbut2.setOnClickListener(new Button.OnClickListener(){  
	            public void onClick(View v) {  
	            	openImageMenu();
	            }  
	        }); 
			
//			final ImageButton sbtn = (ImageButton) findViewById(R.id.mymassagespackButton);
//			sbtn.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					try {
//						startVoiceRecognitionActivity();
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//				}
//			});
			
			imgbtn = (ImageButton) findViewById(R.id.mymassagespackButton);
			imgbtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					EditText contText = (EditText)findViewById(R.id.messagecontent);
					contText.requestFocus();
					if(!smilestag)
					{
						gridSmilesContainer.setVisibility(View.VISIBLE);
						imgbtn.setImageResource(R.drawable.keyboard);
						smilestag = true;
						
						//关闭系统键盘
//					    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);    
//			            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);  
//						contText.setInputType(InputType.TYPE_NULL); // 关闭软键盘      
						
						 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				         imm.hideSoftInputFromWindow(contText.getWindowToken(),0);
					}
					else
					{
						gridSmilesContainer.setVisibility(View.GONE);
						imgbtn.setImageResource(R.drawable.content1);
						smilestag = false;
						
					    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   
					    imm.showSoftInput(contText, 0);
					}
				}
			});
			
//			Button btnAudio = (Button)findViewById(R.id.btnAudio);
//			btnAudio.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					startVoiceRecognitionActivity();
//				}
//			});
			
			final Button sendbtn = (Button) findViewById(R.id.mymassagesendButton);
			sendbtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						showProgressDialog();
						EditText contText = (EditText)findViewById(R.id.messagecontent);
						String content = contText.getText().toString();
						
						for(int i=0;i<browlist.size();i++)
						{
							content = content.replaceFirst("￼", browlist.get(i));
						}
						messageSend(content);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			
			mImageView = (ImageView)findViewById(R.id.mImage);
			mImageView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
				public void onCreateContextMenu(ContextMenu arg0, View arg1,
						ContextMenuInfo arg2) {
					// TODO Auto-generated method stub
					if(!fileUrl.equals(""))
					{
						arg0.setHeaderTitle(MyCommentView.this.getString(R.string.image_menu_lable));
						arg0.add(0, 0, 0, MyCommentView.this.getString(R.string.image_view_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 1, 0, MyCommentView.this.getString(R.string.image_replace_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 2, 0, MyCommentView.this.getString(R.string.phone_screen_label)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 3, 0, MyCommentView.this.getString(R.string.image_dellent_lable)).setIcon(R.drawable.delete_icon);
						
					}
				}
			});
			
			if(tag == 1)
			{
				EditText etext = (EditText)findViewById(R.id.messagecontent);
				etext.setText("");
				
				fileUrl = "";
				mImageView.setImageBitmap(null);
				
				audioFile = null;
			}
//			LinearLayout layout = (LinearLayout)this.findViewById(R.id.comment_layout);
//			layout.addView(view);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showMessageDetails(String flieUrls,String message){
		try{
			final View view = LayoutInflater.from(this).inflate(R.layout.messageview,null);
			ImageView imgview = (ImageView)view.findViewById(R.id.messageImage);
			Bitmap bitmap = U.getImageBitmap(flieUrls);
			imgview.setImageBitmap(bitmap);
//			TextView tview = (TextView)view.findViewById(R.id.messageInfo);
//			tview.setText(message);
			
			final AlertDialog adialog = new AlertDialog.Builder(this).setView(view).show();
			
			imgview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					adialog.dismiss();
				}
			});
			
//			makeText("FileUrl==="+flieUrls);  
//			Intent intent = new Intent();
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.fromFile(new File(flieUrls)), "image/*");
//			startActivity(intent);
//			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openRecordingMenu()
	{
		final CharSequence [] items = { this.getString(R.string.start_record_lable) , this.getString(R.string.play_record_lable)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.manage_record_lable));
		builder.setItems ( items , new DialogInterface.OnClickListener () {
		    public void onClick ( DialogInterface dialog , int item ) {
		        if(item == 0)
		        {
		        	Recording();
		        }
		        else if(item == 1)
		        {
		        	Broadcast();
		        }
		    }
		});
		builder.show();
	
	}
	
	public void Recording()
	{
		try{
			if (!EnvironmentShare.haveSdCard()) {
        		makeText(this.getString(R.string.record_winn_lable));
			}else{
				// 设置音频来源(一般为麦克风)
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// 设置音频输出格式（默认的输出格式）
				mediaRecorder
				.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				// 设置音频编码方式（默认的编码方式）
				mediaRecorder
				.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				// 创建一个临时的音频输出文件
				audioFile = File.createTempFile("record_", ".amr",EnvironmentShare.getAudioRecordDir());
				// 设置录制器的文件保留路径
				mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
				// 准备并且开始启动录制器
				mediaRecorder.prepare();
				mediaRecorder.start();
				makeText(this.getString(R.string.record_lable_1));
				
				Builder dialog = new AlertDialog.Builder(MyCommentView.this);  
                dialog.setTitle(this.getString(R.string.record_lable_2));  
                dialog.setMessage(this.getString(R.string.record_lable_1));  
                dialog.setPositiveButton(this.getString(R.string.record_lable_3), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {  
                        //好了我成功把Button3的图标掠夺过来  
                    	if (audioFile != null) {
        					mediaRecorder.stop();
        				}
                    	makeText(MyCommentView.this.getString(R.string.record_lable_4));
                    }  
                }).create();//创建按钮  
                dialog.show();  
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void Broadcast()
	{
		try{
			if (audioFile != null) {
				mediaPlayer = new MediaPlayer();
				// 为播放器设置数据文件 
				mediaPlayer.setDataSource(audioFile.getAbsolutePath());
				// 准备并且启动播放器
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								setTitle(MyCommentView.this.getString(R.string.record_lable_5));

							}
						});
				makeText(this.getString(R.string.record_lable_6));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public List<Map<String,Object>> getCommentListData()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			JSONObject jobj;
			U.dout(0);
			
			jobj = api.getSrotCommentData(storeid);
			if(jobj != null)
			{
				JSONArray jArr = (JSONArray) jobj.get("data");
				list = getCommentList(jArr);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getCommentList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat from2 = new SimpleDateFormat("MM-dd hh:mm");
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String content = ""; 
				if(dobj.has("content"))
					content = (String) dobj.get("content"); 
				
				String userimg = ""; 
				if(dobj.has("userimg"))
					userimg = (String) dobj.get("userimg");
				
				String uname = ""; 
				if(dobj.has("uname"))
					uname = (String) dobj.get("uname");
				
				String filepath = ""; 
				if(dobj.has("filepath"))
					filepath = (String) dobj.get("filepath");
				
				String sendtime = ""; 
				if(dobj.has("sendtime"))
					sendtime = (String) dobj.get("sendtime");
				
				String lupath = ""; 
				if(dobj.has("lupath"))
					lupath = (String) dobj.get("lupath");
				
				String storename = ""; 
				if(dobj.has("storename"))
					storename = (String) dobj.get("storename");
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("sendName", uname);
				if(userimg != null && !userimg.equals(""))
					map.put("img", userimg);
				else
					map.put("img", R.drawable.yi_man);
				map.put("content", Html.fromHtml(content, imageGetter, null));
				if(filepath != null && !filepath.equals(""))
				{
					map.put("file", filepath);
					map.put("filepath", filepath);
					map.put("mediaTool", R.drawable.chat_media_record_media_tool);
				}
				else
				{
					map.put("file", null);
					map.put("filepath", filepath);
					map.put("mediaTool", null);
				}
				Date sendt = from.parse(sendtime);
				map.put("sendTime", from2.format(sendt));
				map.put("storename", storename);
				map.put("lupath", lupath);
				
	
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
	
	private void startVoiceRecognitionActivity() {
	       Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	       intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	               RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	       intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话......");
	       startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	       overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	           // Fill the list view with the strings the recognizer thought it could have heard
	           ArrayList<String> matches = data.getStringArrayListExtra(
	                   RecognizerIntent.EXTRA_RESULTS);
	           EditText eText = (EditText) findViewById(R.id.messagecontent);
	           eText.setText(matches.get(0));
	       }
	       
	       if(resultCode == RESULT_OK){
	    	   if(data != null)
	    	   {
					Uri uri = data.getData();
					if(uri != null)
					{
						ContentResolver cr = getContentResolver();
						Cursor cursor = cr.query(uri, null, null, null, null);
						String imageSize = "";
						//查询本地图片库图片的字段
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//得到本地图片库中图片的 id、路径、大小、文件名
						while(cursor.moveToNext()){
							Log.i("====_id", cursor.getString(0)+ "");
							Log.i("====_path", cursor.getString(1)+ "");
							fileUrl = cursor.getString(1);
							Log.i("====_size", cursor.getString(2)+ "");
							imageSize = cursor.getString(2);
							Log.i("====_display_name", cursor.getString(3)+ "");
							fileName = cursor.getString(3);
						}
						try {
							if (bitmap != null) {
			                    bitmap.recycle();
			                    bitmap = null;
							 }
			
							Log.i("===============imgSize======" , imageSize);
							fileSize = Long.valueOf(imageSize);
							bitmap = zoomImage(bitmap,Long.valueOf(imageSize),fileUrl);
							bitmap = zoomImage(bitmap,80,80);
							mImageView.setImageBitmap(bitmap);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
	    	   }
	    	   else
	    	   {
	    		   if(uri != null)
					{
						ContentResolver cr = getContentResolver();
						Cursor cursor = cr.query(uri, null, null, null, null);
						String imageSize = "";
						//查询本地图片库图片的字段
			//			String[] str = cursor.getColumnNames();
			//			for(int i=0; i<str.length; i++){
			//				Log.i("ColumNames", str[i]);
			//			}
						//得到本地图片库中图片的 id、路径、大小、文件名
						while(cursor.moveToNext()){
							Log.i("====_id", cursor.getString(0)+ "");
							Log.i("====_path", cursor.getString(1)+ "");
							fileUrl = cursor.getString(1);
							Log.i("====_size", cursor.getString(2)+ "");
							imageSize = cursor.getString(2);
							Log.i("====_display_name", cursor.getString(3)+ "");
							fileName = cursor.getString(3);
						}
						try {
							if (bitmap != null) {
			                    bitmap.recycle();
			                    bitmap = null;
							 }
			
							Log.i("===============imgSize======" , imageSize);
							fileSize = Long.valueOf(imageSize);
							bitmap = zoomImage(bitmap,Long.valueOf(imageSize),fileUrl);
							bitmap = zoomImage(bitmap,80,80);
							mImageView.setImageBitmap(bitmap);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
	    	   }
				
			}
	       
	       
	       super.onActivityResult(requestCode, resultCode, data);
	   }
	
	public void openImageMenu()
	{
		final CharSequence [] items = { this.getString(R.string.login_lable_18) , this.getString(R.string.login_lable_19)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.login_lable_20));
		builder.setItems ( items , new DialogInterface.OnClickListener () {
		    public void onClick ( DialogInterface dialog , int item ) {
		        if(item == 0)
		        {
		        	Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
		        }
		        else if(item == 1)
		        {
		        	Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        	startActivityForResult(intent3, 1);
		        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
//		        	openImageCamera();//平板拍摄照片方法
		        }
		    }
		});
		builder.show();
	
	}
	
	public void openImageCamera()
	{
		/**
		* 由于Camara返回的是缩略图，我们可以传递给他一个参数EXTRA_OUTPUT,
		* 来将用Camera获取到的图片存储在一个指定的URI位置处。
		* 下面就指定image存储在SDCard上，并且文件名为123.jpg
		* imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"123.jpg";
		* File file = new File(imageFilePath); //创建一个文件
		* Uri imageUri = Uri.fromFile(file);
		* 然而Android已经提供了一个多媒体库，那里统一存放了设备上所有的多媒体数据。所以，
		* 我们可以将获取到的图片存放在那个多媒体库中。
		* Android提供了MediaStore类，该类是一个ContentProvider，管理着设备上自带的和外部的多媒体文件，
		* 同时包含着每一个多媒体文件的数据信息。
		* 为了将数据存储在多媒体库，使用ContentResolver对象来操纵MediaStore对象
		* 在MediaStore.Images.Media中有两个URI常量，一个是 EXTERNAL_CONTENT_URI,另一个是INTERNAL_CONTENT_URI
		* 第一个URI对应着外部设备(SDCard)，第二个URI对应着系统设备内部存储位置。
		* 对于多媒体文件，一般比较大，我们选择外部存储方式
		* 通过使用ContentResolver对象的insert方法我们可以向MediaStore中插入一条数据
		* 这样在检索那张图片的时候，不再使用文件的路径，而是根据insert数据时返回的URI，获取一个InputStream
		* 并传给BitmapFactory
		*/
		//在这里启动Camera。
		//Camera中定义了一个Intent-Filter，其中Action是android.media.action.IMAGE_CAPTURE
		//我们使用的时候，最好不要直接使用这个，而是用MediaStore中的常量ACTION_IMAGE_CAPTURE.
		//这个常量就是对应的上面的action

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		//这里我们插入一条数据，ContentValues是我们希望这条记录被创建时包含的数据信息
		//这些数据的名称已经作为常量在MediaStore.Images.Media中,有的存储在MediaStore.MediaColumn中了
		//ContentValues values = new ContentValues();

		ContentValues values = new ContentValues(3);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
		values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		uri = MyCommentView.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		//这样就将文件的存储方式和uri指定到了Camera应用中

		//由于我们需要调用完Camera后，可以返回Camera获取到的图片，
		//所以，我们使用startActivityForResult来启动Camera
		startActivityForResult(intent, 1);
	}
	
	public Bitmap zoomImage(Bitmap bgimage, long imageSize, String fileUrl) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		if (imageSize < 20480) { // 0-20k
			newOpts.inSampleSize = 1;
        } else if (imageSize < 51200) { // 20-50k
        	newOpts.inSampleSize = 2;
        } else if (imageSize < 307200) { // 50-300k
        	newOpts.inSampleSize = 4;
        } else if (imageSize < 819200) { // 300-800k
        	newOpts.inSampleSize = 6;
        } else if (imageSize < 1048576) { // 800-1024k
        	newOpts.inSampleSize = 8;
        } else {
        	newOpts.inSampleSize = 10;
        }

		// inJustDecodeBounds设为false表示把图片读进内存中
		newOpts.inJustDecodeBounds = false;
		// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
//		newOpts.outHeight = bgimage.getHeight();
//		newOpts.outWidth = bgimage.getWidth();
		
		bgimage = BitmapFactory.decodeFile(fileUrl, newOpts);
		return bgimage;
	}
	
	/***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
            // 获取这个图片的宽和高
            int width = bgimage.getWidth();
            int height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                            matrix, true);
            return bitmap;
    }
    
 // 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int index = item.getItemId();
		switch (index) {
		case 0:
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileUrl)), "image/*");
			startActivity(intent);
			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			break;
		case 1:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent2, 1);
			overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			break;
		case 2:
			Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	startActivityForResult(intent3, 1);
        	overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
//			openImageCamera();
			break;
		case 3:
			fileUrl = "";
			mImageView.setImageBitmap(null);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	//获取图片bitmap
	public void setImageBitap()
	{
		try {
			/**
			 * 然而为了节约内存的消耗，这里返回的图片是一个121*162的缩略图。 那么如何返回我们需要的大图呢？看上面
			 * 然而存储了图片。有了图片的存储位置，能不能直接将图片显示出来呢》
			 * 这个问题就设计到对于图片的处理和显示，是非常消耗内存的，对于PC来说可能不算什么，但是对于手机来说
			 * 很可能使你的应用因为内存耗尽而死亡。不过还好，Android为我们考虑到了这一点
			 * Android中可以使用BitmapFactory类和他的一个内部类BitmapFactory
			 * .Options来实现图片的处理和显示
			 * BitmapFactory是一个工具类，里面包含了很多种获取Bitmap的方法
			 * 。BitmapFactory.Options类中有一个inSampleSize
			 * ，比如设定他的值为8，则加载到内存中的图片的大小将 是原图片的1/8大小。这样就远远降低了内存的消耗。
			 * BitmapFactory.Options op = new BitmapFactory.Options();
			 * op.inSampleSize = 8; Bitmap pic =
			 * BitmapFactory.decodeFile(imageFilePath, op);
			 * 这是一种快捷的方式来加载一张大图，因为他不用考虑整个显示屏幕的大小和图片的原始大小
			 * 然而有时候，我需要根据我们的屏幕来做相应的缩放，如何操作呢？
			 * 
			 */

			// 首先取得屏幕对象
			Display display = this.getWindowManager()
					.getDefaultDisplay();
			// 获取屏幕的宽和高
			int dw = display.getWidth();
			int dh = display.getHeight();
			/**
			 * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
			 * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds
			 * ，将其设置为true 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
			 * 当我们设置这个值的时候，我们接着就可以从BitmapFactory
			 * .Options的outWidth和outHeight中获取到值
			 */

			BitmapFactory.Options op = new BitmapFactory.Options();
			// op.inSampleSize = 8;
			op.inJustDecodeBounds = true;
			// Bitmap pic = BitmapFactory.decodeFile(imageFilePath,
			// op);//调用这个方法以后，op中的outWidth和outHeight就有值了
			// 由于使用了MediaStore存储，这里根据URI获取输入流的形式
			Bitmap pic = BitmapFactory.decodeStream(this
					.getContentResolver().openInputStream(uri), null,
					op);

			int wRatio = (int) Math.ceil(op.outWidth / (float) dw); // 计算宽度比例
			int hRatio = (int) Math.ceil(op.outHeight / (float) dh); // 计算高度比例
			Log.v("Width Ratio:", wRatio + "");
			Log.v("Height Ratio:", hRatio + "");

			/**
			 * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 如果高和宽不是全都超出了屏幕，那么无需缩放。
			 * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 这需要判断wRatio和hRatio的大小
			 * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 缩放使用的还是inSampleSize变量
			 */

			if (wRatio > 1 && hRatio > 1) {
				if (wRatio > hRatio) {
					op.inSampleSize = wRatio;
				} else {
					op.inSampleSize = hRatio;
				}
			}

			op.inJustDecodeBounds = false;
			// 注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
			pic = BitmapFactory.decodeStream(this.getContentResolver()
					.openInputStream(uri), null, op);
			pic = zoomImage(pic, 150, 150);
			
			mImageView.setImageBitmap(pic);
			mImageView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
				public void onCreateContextMenu(ContextMenu arg0, View arg1,
						ContextMenuInfo arg2) {
					// TODO Auto-generated method stub
					if(!fileUrl.equals(""))
					{
						arg0.setHeaderTitle(MyCommentView.this.getString(R.string.image_menu_lable));
						arg0.add(0, 0, 0, MyCommentView.this.getString(R.string.image_view_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 1, 0, MyCommentView.this.getString(R.string.image_replace_lable)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 2, 0, MyCommentView.this.getString(R.string.phone_screen_label)).setIcon(R.drawable.tupian_btn);
						arg0.add(0, 3, 0, MyCommentView.this.getString(R.string.image_dellent_lable)).setIcon(R.drawable.delete_icon);
						
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void messageSend(final String content)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 6;
				boolean tag = true;
				try{
					Map<String, String> params = new HashMap<String, String>();
					params.put("content", content);
					params.put("pfid", myapp.getPfprofileId());
					params.put("storeid", storeid);
					params.put("sendName", myapp.getUserName());
					long fsize = 0;
					Map<String, File> files = new HashMap<String, File>();
					FormFile [] fromFiles = {};
					if(fileUrl != null && !fileUrl.equals(""))
					{
						File file = new File(fileUrl);
						long filesize = file.length();
						if(filesize > 358400) //filesize / 1024得到文件KB单位大小
						{
//							makeText(this.getString(R.string.image_sizeerroe_lable));
							tag = false;
						}
						fsize = fsize + filesize;
//						makeText("filesize==="+filesize);
						files.put(file.getName(), file);
		//	            FormFile formfile = new FormFile(file.getName(), file, "image", "image/jpeg");
		//	            fromFiles[0] = formfile;
					}
					if(audioFile != null)
					{
						files.put(audioFile.getName(), audioFile);
						long filesize = audioFile.length();
						fsize = fsize + filesize;
		//				makeText("音频文件大小==="+filesize);
		//				FormFile formfile = new FormFile(audioFile.getName(), audioFile, "arm", "arm");
		//				if(fromFiles.length == 1)
		//					fromFiles[1] = formfile;
		//				else
		//					fromFiles[0] = formfile;
					}
					
					boolean bf = false;
					if(tag)
					{
						bf = api.uploadFiles(params,files);
					}
					msg.obj = bf;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**  
     * 根据URL得到输入流  
     * @param urlStr  
     * @return  
     */  
    public InputStream getInputStreamFromURL(String urlStr) {  
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null; 
        URL url = null;
        try {  
            url = new URL(urlStr);  
            urlConn = (HttpURLConnection)url.openConnection();  
            inputStream = urlConn.getInputStream();  
              
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return inputStream;  
    } 
    
//    /** 
//     * 从网上下载 
//     *@param url 下载路径 
//     *@param outputFile 创建本地保存流的文件 
//     *@return 
//     
//     * @return 下载失败返回1（比如没有网络等情况）下载成功返回0 
//     */  
//    public static int downloadFile(String urlPsth, File outputFile) 
//    {  
//		int result = 0;
//		try {
//			File downLoadFile  = new File(EnvironmentShare.getDownAudioRecordDir().getAbsolutePath()+ "/" + audioFile.getName());
//			URL url = new URL(urlPsth);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoInput(true);
//			conn.connect();
//			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//				InputStream is = conn.getInputStream();
//				FileOutputStream fos = new FileOutputStream(outputFile);
//				byte[] bt = new byte[1024];
//				int i = 0;
//				while ((i = is.read(bt)) > 0) {
//					fos.write(bt, 0, i);
//				}
//				fos.flush();
//				fos.close();
//				is.close();
//
//			} else {
//				result = 1;
//			}
//
//		} catch (FileNotFoundException e) {
//			result = 1;
//		} catch (IOException e) {
//			result = 1;
//		}
//		return result;
//	}
    
    /**  
     *   
     * @param urlStr  
     * @param path  
     * @param fileName  
     * @return   
     *      -1:文件下载出错  
     *       0:文件下载成功  
     *       1:文件已经存在  
     */  
    public int downFile(String urlStr, String path, String fileName){  
        InputStream inputStream = null;  
        try {  
            FileUtils fileUtils = new FileUtils();  
              
            if(fileUtils.isFileExist(path + fileName)){  
                return 1;  
            } else {  
                inputStream = getInputStreamFromURL(urlStr);  
                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
                if(resultFile == null){  
                    return -1;  
                }  
            }  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
            return -1;  
        }  
        finally{  
            try {  
                inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return 0;  
    }  
    
    public void showProgressDialog(){
 		try{
 			mypDialog=new ProgressDialog(this);
             //实例化
             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
             //设置ProgressDialog 标题
             mypDialog.setMessage(this.getString(R.string.travel_lable_22));
             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
             //设置ProgressDialog 标题图标
//             mypDialog.setButton("",this);
             //设置ProgressDialog 的一个Button
             mypDialog.setIndeterminate(false);
             //设置ProgressDialog 的进度条是否不明确
             mypDialog.setCancelable(true);
             //设置ProgressDialog 是否可以按退回按键取消
             mypDialog.show();
             //让ProgressDialog显示
 		}catch(Exception ex){
 			ex.printStackTrace();
 		}
 	}
    
    /**  
     * 定义从右侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromRightAnimation() {  
        Animation inFromRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromRight.setDuration(500);  
        inFromRight.setInterpolator(new AccelerateInterpolator());  
        return inFromRight;  
    }  
   
    /**  
     * 定义从左侧退出的动画效果  
     * @return  
     */  
    protected Animation outToLeftAnimation() {  
        Animation outtoLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoLeft.setDuration(500);  
        outtoLeft.setInterpolator(new AccelerateInterpolator());  
        return outtoLeft;  
    }  
   
    /**  
     * 定义从左侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromLeftAnimation() {  
        Animation inFromLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromLeft.setDuration(500);  
        inFromLeft.setInterpolator(new AccelerateInterpolator());  
        return inFromLeft;  
    }  
   
    /**  
     * 定义从右侧退出时的动画效果  
     * @return  
     */  
    protected Animation outToRightAnimation() {  
        Animation outtoRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoRight.setDuration(500);  
        outtoRight.setInterpolator(new AccelerateInterpolator());  
        return outtoRight;  
    } 
    
    public class CommonGestureListener extends SimpleOnGestureListener {
   	 
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onDown...");
			return false;
		}
 
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onShowPress...");
			super.onShowPress(e);
		}
 
		@Override
	    public void onLongPress(MotionEvent e) {
	        // TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "----> Jieqi: do onLongPress...");
	    }
 
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapConfirmed...");
			return false;
		}
 
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onSingleTapUp...");
			return false;
		}
 
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY){
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onFling...");
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            // 当像左侧滑动的时候  
	            //设置View进入屏幕时候使用的动画  
				mViewFlipper.setInAnimation(inFromRightAnimation());  
	            //设置View退出屏幕时候使用的动画  
				mViewFlipper.setOutAnimation(outToLeftAnimation());
//				selectLisgBg("next");
				if(layoutindex == 0)
					layoutindex++;
				else
				{
					if (layoutindex % (mViewFlipper.getChildCount()-1) == 0) 
						layoutindex = 0;
					else
						layoutindex++;
				}
				pageControl.generatePageControl(layoutindex);
				mViewFlipper.showNext(); 
	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            // 当像右侧滑动的时候  
	        	mViewFlipper.setInAnimation(inFromLeftAnimation());  
	        	mViewFlipper.setOutAnimation(outToRightAnimation());  
//	        	selectLisgBg("previous");
	        	if(layoutindex == 0)
	        	{
	        		layoutindex = mViewFlipper.getChildCount()-1;
	        	}
	        	else
	        	{
					layoutindex--;
	        	}
	        	pageControl.generatePageControl(layoutindex);
	        	mViewFlipper.showPrevious();  
	        }  
			return true;
		}
		
 
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.d("QueryViewFlipper", "====> Jieqi: do onScroll...");
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
 
    }
}
