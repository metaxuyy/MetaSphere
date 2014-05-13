package ms.activitys.more;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.more.IPackageDataObserver;
import ms.activitys.LoginMain;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

//继承PreferenceActivity，并实现OnPreferenceChangeListener和OnPreferenceClickListener监听接口
public class Settings extends PreferenceActivity implements OnPreferenceClickListener,OnPreferenceChangeListener{
	
	private SharedPreferences  share;
	private ProgressDialog mypDialog;
	//定义相关变量
	private ListPreference switchLanguage;
	private ListPreference ipAddress;
	private CheckBoxPreference autoloadMore;
	private CheckBoxPreference loadImg;
	private CheckBoxPreference fastScrollBar;
	private ListPreference uploadImageSize;
	private ListPreference downloadImageSize;
	private Preference clearCache;
	private CheckBoxPreference autoRemind;
	private CheckBoxPreference synchronousData;
	private CheckBoxPreference saveUserinfo;
	private Preference remindCategory;
	private ListPreference interval;
	private CheckBoxPreference audio;
	private CheckBoxPreference vibrator;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从xml文件中添加Preference项
        addPreferencesFromResource(R.xml.setting);
        
        share = Settings.this.getSharedPreferences("perference", MODE_PRIVATE);
        PreferenceManager manager = getPreferenceManager();
        //获取各个Preference
        switchLanguage = (ListPreference) manager.findPreference("switch_language");
        if(switchLanguage != null)
        	switchLanguage.setOnPreferenceChangeListener(this);
        
        ipAddress = (ListPreference) manager.findPreference("ip_address");
        ipAddress.setOnPreferenceChangeListener(this);
        
        autoloadMore = (CheckBoxPreference) manager.findPreference("autoload_more");
        autoloadMore.setOnPreferenceChangeListener(this);
        
        loadImg = (CheckBoxPreference) manager.findPreference("loadimg");
        loadImg.setOnPreferenceChangeListener(this);
        
        fastScrollBar = (CheckBoxPreference) manager.findPreference("fast_scroll_bar");
        fastScrollBar.setOnPreferenceChangeListener(this);
        
        uploadImageSize = (ListPreference) manager.findPreference("upload_image_size");
        uploadImageSize.setOnPreferenceChangeListener(this);
        
        downloadImageSize = (ListPreference) manager.findPreference("download_image_size");
        downloadImageSize.setOnPreferenceChangeListener(this);
        
        clearCache = (Preference) manager.findPreference("clear_cache");
        clearCache.setOnPreferenceClickListener(this);
        
        autoRemind = (CheckBoxPreference) manager.findPreference("auto_remind");
        autoRemind.setOnPreferenceChangeListener(this);
        
        synchronousData = (CheckBoxPreference) manager.findPreference("synchronous_data");
        synchronousData.setOnPreferenceChangeListener(this);
        
        saveUserinfo = (CheckBoxPreference) manager.findPreference("save_userinfo");
        saveUserinfo.setOnPreferenceChangeListener(this);
        
        remindCategory = (Preference) manager.findPreference("remind_category");
        remindCategory.setOnPreferenceClickListener(this);
        
        interval = (ListPreference) manager.findPreference("interval");
        interval.setOnPreferenceChangeListener(this);
        
        audio = (CheckBoxPreference) manager.findPreference("audio");
        audio.setOnPreferenceChangeListener(this);
        
        vibrator = (CheckBoxPreference) manager.findPreference("vibrator");
        vibrator.setOnPreferenceChangeListener(this);
    }
    
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Log.v("SystemSetting", "preference is clicked");
		Log.v("Key_SystemSetting", preference.getKey());
		//判断是哪个Preference被点击了
		if(preference.getKey().equals("clear_cache")) //清除缓存
		{
			showClearnCrashDialog();
		}
		else if(preference.getKey().equals("remind_category")) //提醒设置
		{
			
		}
		else
		{
			//如果返回false表示不允许被改变
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,MainTabActivity.class);
//			bundle.putInt("index", index);
			intent.putExtras(bundle);
		    startActivity(intent);//开始界面的跳转函数
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
//			return false;
		}
		return false;
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		Log.v("SystemSetting", "preference is changed");
		Log.v("Key_SystemSetting", preference.getKey());
		//判断是哪个Preference改变了
		if(preference.getKey().equals("switch_language"))//语言
		{
			setPreferencesString("switch_language", (String)newValue);
		}
		else if(preference.getKey().equals("ip_address"))//服务器地址
		{
			setPreferencesString("ipadrees", (String)newValue);
		}
		else if(preference.getKey().equals("autoload_more"))//下拉框更多
		{
			setPreferencesBoolean("autoload_more", (Boolean)newValue);
		}
		else if(preference.getKey().equals("loadimg")) //是否加载图片
		{
			setPreferencesBoolean("webimage", (Boolean)newValue);
		}
		else if(preference.getKey().equals("fast_scroll_bar")) //下拉宽浏览是开启快速拖动
		{
			setPreferencesBoolean("fast_scroll_bar", (Boolean)newValue);
		}
		else if(preference.getKey().equals("upload_image_size"))//上传图片设置
		{
			setPreferencesString("upload_image_size", (String)newValue);
		}
		else if(preference.getKey().equals("download_image_size"))//下载图片设置
		{
			setPreferencesString("download_image_size", (String)newValue);
		}
		else if(preference.getKey().equals("auto_remind"))//自动提醒
		{
			setPreferencesBoolean("auto_remind", (Boolean)newValue);
		}
		else if(preference.getKey().equals("synchronous_data"))//是否同步本地数据库
		{
			setPreferencesBoolean("webdata", (Boolean)newValue);
		}
		else if(preference.getKey().equals("save_userinfo"))//自动登录
		{
			setPreferencesBoolean("autologin", (Boolean)newValue);
		}
		else if(preference.getKey().equals("interval"))//检测间隔
		{
			setPreferencesString("interval", (String)newValue);
		}
		else if(preference.getKey().equals("audio"))//铃声提醒
		{
			setPreferencesBoolean("audio", (Boolean)newValue);
		}
		else if(preference.getKey().equals("vibrator"))//震动提醒
		{
			setPreferencesBoolean("vibrator", (Boolean)newValue);
		}
		else
		{
			return false;
		}
		//返回true表示允许改变
		return true;
	}
	
	public void setPreferencesString(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public void setPreferencesInt(String key,int value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putInt(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public void setPreferencesBoolean(String key,boolean value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putBoolean(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public void showClearnCrashDialog()
	{
		new AlertDialog.Builder(this)
		         .setTitle(this.getString(R.string.record_lable_2))
		         .setMessage(this.getString(R.string.seting_lable_9))
		         .setPositiveButton(this.getString(R.string.coupon_lable_14), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	new Thread(){
						public void run(){
							Message msg = new Message();
							msg.what = 0;
							clearnCrash1();
							SystemClock.sleep(5000);
							handler.sendMessage(msg);
						}
					}.start();
		        	
		        	dialog.dismiss();
		        	
		        	mypDialog = ProgressDialog.show(Settings.this, null, Settings.this.getString(R.string.seting_lable_10), true,
			                false);
		         }
		         })
		         .setNegativeButton(this.getString(R.string.coupon_lable_16), new DialogInterface.OnClickListener() {
		        	 public void onClick(DialogInterface dialog, int whichButton) {
		        		 //取消按钮事件
		        	 }
		         })
		         .show(); 
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(mypDialog != null)
					mypDialog.dismiss();
				makeText(Settings.this.getString(R.string.seting_lable_11));
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	public void clearnCrash1(){
		try {
			PackageManager pm = getPackageManager();
			Method localMethod = pm.getClass().getMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);
			Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
			Object[] arrayOfObject = new Object[2];
		      arrayOfObject[0] = localLong;
		      localMethod.invoke(pm,localLong,new IPackageDataObserver.Stub(){
				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					// TODO Auto-generated method stub
				}});
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  long getEnvironmentSize()
    {
      File localFile = Environment.getDataDirectory();
      long l1;
      if (localFile == null)
        l1 = 0L;
      while (true)
      {
        
        String str = localFile.getPath();
        StatFs localStatFs = new StatFs(str);
        long l2 = localStatFs.getBlockSize();
        l1 = localStatFs.getBlockCount() * l2;
        return l1;
      }
    }
	
//	public void showProgressDialog(){
// 		try{
// 			mypDialog=new ProgressDialog(this);
//             //实例化
//             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//             //设置进度条风格，风格为圆形，旋转的
//             mypDialog.setTitle("等待");
//             //设置ProgressDialog 标题
//             mypDialog.setMessage("正在清除数据请稍等...");
//             //设置ProgressDialog 提示信息
//             mypDialog.setIcon(R.drawable.wait_icon);
//             //设置ProgressDialog 标题图标
////             mypDialog.setButton("",this);
//             //设置ProgressDialog 的一个Button
//             mypDialog.setIndeterminate(false);
//             //设置ProgressDialog 的进度条是否不明确
//             mypDialog.setCancelable(true);
//             //设置ProgressDialog 是否可以按退回按键取消
//             mypDialog.show();
//             //让ProgressDialog显示
// 		}catch(Exception ex){
// 			ex.printStackTrace();
// 		}
// 	}
}
