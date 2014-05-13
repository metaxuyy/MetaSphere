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
 *  ����Ϊ Ӳ������ ������
 */
public class EnvironmentShare {
	
	// ���¼���ļ��е�����
	static String AUDIO_RECORD = "/AudioRecord";
	// ������ض�����¼���ļ�������
	static String DOWNLOAD_AUDIO_RECORD = "/AudioRecord/downLoad";

	/**
	 *  ��⵱ǰ�豸SD�Ƿ����
	 *  
	 * @return  ����"true"��ʾ���ã����򲻿���
	 */
	public static boolean haveSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
	}
	
	/**
	 *  ���SD����Ŀ¼·�� 
	 *  
	 * @return String����  SD����Ŀ¼·��
	 */
	public static String getSdCardAbsolutePath(){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * ��ô洢 ¼���ļ����ļ���
	 * 
	 * @return File���� 
	 *         �洢 ¼���ļ����ļ���
	 */
	public static File getAudioRecordDir(){
		File audioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() + AUDIO_RECORD);
		if (!audioRecordFile.exists()) {
			// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			audioRecordFile.mkdir();
		}
		return audioRecordFile;
	}
	
	/**
	 * ��ô洢 ���ض�����¼���ļ����ļ���
	 * 
	 * @return File����     
	 *         �洢 ���ض����� ¼���ļ����ļ���
	 */
	public static File getDownAudioRecordDir(){
		File audioRecordFile = new File(EnvironmentShare.getSdCardAbsolutePath() + DOWNLOAD_AUDIO_RECORD);
		if (!audioRecordFile.exists()) {
			// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			audioRecordFile.mkdir();
		}
		return audioRecordFile;
	}
	
	/**
	 *  ��Toast��ʾָ����Ϣ
	 * 
	 * @param activity   Activity����       Ҫ��ʾ��ʾ��Ϣ��ҳ��������
	 * @param message    String����            ����ʾ����ʾ��Ϣ����
	 * @param isLong     boolean����         ���Ϊ"true"��ʾ��ʱ����ʾ������Ϊ��ʱ����ʾ
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
	 *  ��Toast��ʾָ����Ϣ �����ñ�����ʾ ��Ϣ
	 * 
	 * @param activity   Activity����       Ҫ��ʾ��ʾ��Ϣ��ҳ��������
	 * @param message    String����            ����ʾ����ʾ��Ϣ����
	 * @param isLong     boolean����         ���Ϊ"true"��ʾ��ʱ����ʾ������Ϊ��ʱ����ʾ
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
