package ms.globalclass;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

/**
 * 
 * @author 
 *  at 2011-03-1
 *
 *  该类为 硬件检测的 公共类
 */
public class EnvironmentShare {
	
	// 存放录音文件夹的名称
	static String AUDIO_RECORD = "/AudioRecord";
	// 存放下载而来的录音文件夹名称
	static String DOWNLOAD_AUDIO_RECORD = "/AudioRecord/downLoad";

	/**
	 *  检测当前设备SD是否可用
	 *  
	 * @return  返回"true"表示可用，否则不可用
	 */
	public static boolean haveSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
	}
	
	/**
	 *  获得SD卡根目录路径 
	 *  
	 * @return String类型  SD卡根目录路径
	 */
	public static String getSdCardAbsolutePath(){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 获得存储 录音文件的文件夹
	 * 
	 * @return File类型 
	 *         存储 录音文件的文件夹
	 */
	public static File getAudioRecordDir(){
		File audioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() + AUDIO_RECORD);
		if (!audioRecordFile.exists()) {
			// 此处可能会创建失败，暂不考虑
			audioRecordFile.mkdir();
		}
		return audioRecordFile;
	}
	
	/**
	 * 获得存储 下载而来的录音文件的文件夹
	 * 
	 * @return File类型     
	 *         存储 下载而来的 录音文件的文件夹
	 */
	public static File getDownAudioRecordDir(){
		File audioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() + DOWNLOAD_AUDIO_RECORD);
		if (!audioRecordFile.exists()) {
			// 此处可能会创建失败，暂不考虑
			audioRecordFile.mkdir();
		}
		return audioRecordFile;
	}
	
	/**
	 *  用Toast显示指定信息
	 * 
	 * @param activity   Activity类型       要显示提示信息的页面上下文
	 * @param message    String类型            将显示的提示信息内容
	 * @param isLong     boolean类型         如果为"true"表示长时间显示，否则为短时间显示
	 */
	public static void showToast(Activity activity,String message,boolean isLong){
		if (message == null ||message.equals("")) 
			return ;
		int showTime = Toast.LENGTH_SHORT;
		if (isLong) {
			showTime = Toast.LENGTH_LONG;
		}
		
		Toast.makeText(activity, message, showTime).show();
	}
	
	
	/**
	 *  用Toast显示指定信息 并设置标题显示 信息
	 * 
	 * @param activity   Activity类型       要显示提示信息的页面上下文
	 * @param message    String类型            将显示的提示信息内容
	 * @param isLong     boolean类型         如果为"true"表示长时间显示，否则为短时间显示
	 */
	public static void showToastAndTitle(Activity activity,String message,boolean isLong){
		activity.setTitle(message);
		showToast(activity, message, isLong);
	}
	
	public boolean isValueNull(String str,JSONObject dobj)
	{
		boolean b = true;
		try{
			if(dobj.has(str))
			{
				if(dobj == null)
					b = false;
			}
			else
			{
				b = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return b;
	}
	
}
