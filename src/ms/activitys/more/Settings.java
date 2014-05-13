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

//�̳�PreferenceActivity����ʵ��OnPreferenceChangeListener��OnPreferenceClickListener�����ӿ�
public class Settings extends PreferenceActivity implements OnPreferenceClickListener,OnPreferenceChangeListener{
	
	private SharedPreferences  share;
	private ProgressDialog mypDialog;
	//������ر���
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
        //��xml�ļ������Preference��
        addPreferencesFromResource(R.xml.setting);
        
        share = Settings.this.getSharedPreferences("perference", MODE_PRIVATE);
        PreferenceManager manager = getPreferenceManager();
        //��ȡ����Preference
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
		//�ж����ĸ�Preference�������
		if(preference.getKey().equals("clear_cache")) //�������
		{
			showClearnCrashDialog();
		}
		else if(preference.getKey().equals("remind_category")) //��������
		{
			
		}
		else
		{
			//�������false��ʾ�������ı�
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
		    startActivity(intent);//��ʼ�������ת����
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
		//�ж����ĸ�Preference�ı���
		if(preference.getKey().equals("switch_language"))//����
		{
			setPreferencesString("switch_language", (String)newValue);
		}
		else if(preference.getKey().equals("ip_address"))//��������ַ
		{
			setPreferencesString("ipadrees", (String)newValue);
		}
		else if(preference.getKey().equals("autoload_more"))//���������
		{
			setPreferencesBoolean("autoload_more", (Boolean)newValue);
		}
		else if(preference.getKey().equals("loadimg")) //�Ƿ����ͼƬ
		{
			setPreferencesBoolean("webimage", (Boolean)newValue);
		}
		else if(preference.getKey().equals("fast_scroll_bar")) //����������ǿ��������϶�
		{
			setPreferencesBoolean("fast_scroll_bar", (Boolean)newValue);
		}
		else if(preference.getKey().equals("upload_image_size"))//�ϴ�ͼƬ����
		{
			setPreferencesString("upload_image_size", (String)newValue);
		}
		else if(preference.getKey().equals("download_image_size"))//����ͼƬ����
		{
			setPreferencesString("download_image_size", (String)newValue);
		}
		else if(preference.getKey().equals("auto_remind"))//�Զ�����
		{
			setPreferencesBoolean("auto_remind", (Boolean)newValue);
		}
		else if(preference.getKey().equals("synchronous_data"))//�Ƿ�ͬ���������ݿ�
		{
			setPreferencesBoolean("webdata", (Boolean)newValue);
		}
		else if(preference.getKey().equals("save_userinfo"))//�Զ���¼
		{
			setPreferencesBoolean("autologin", (Boolean)newValue);
		}
		else if(preference.getKey().equals("interval"))//�����
		{
			setPreferencesString("interval", (String)newValue);
		}
		else if(preference.getKey().equals("audio"))//��������
		{
			setPreferencesBoolean("audio", (Boolean)newValue);
		}
		else if(preference.getKey().equals("vibrator"))//������
		{
			setPreferencesBoolean("vibrator", (Boolean)newValue);
		}
		else
		{
			return false;
		}
		//����true��ʾ����ı�
		return true;
	}
	
	public void setPreferencesString(String key,String value)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putString(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	public void setPreferencesInt(String key,int value)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putInt(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
	}
	
	public void setPreferencesBoolean(String key,boolean value)
	{
		Editor editor = share.edit();// ȡ�ñ༭��

		editor.putBoolean(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		
		editor.commit();// �ύˢ������
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
		        		 //ȡ����ť�¼�
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
//             //ʵ����
//             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//             //���ý�������񣬷��ΪԲ�Σ���ת��
//             mypDialog.setTitle("�ȴ�");
//             //����ProgressDialog ����
//             mypDialog.setMessage("��������������Ե�...");
//             //����ProgressDialog ��ʾ��Ϣ
//             mypDialog.setIcon(R.drawable.wait_icon);
//             //����ProgressDialog ����ͼ��
////             mypDialog.setButton("",this);
//             //����ProgressDialog ��һ��Button
//             mypDialog.setIndeterminate(false);
//             //����ProgressDialog �Ľ������Ƿ���ȷ
//             mypDialog.setCancelable(true);
//             //����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
//             mypDialog.show();
//             //��ProgressDialog��ʾ
// 		}catch(Exception ex){
// 			ex.printStackTrace();
// 		}
// 	}
}
