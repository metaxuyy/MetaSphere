package ms.globalclass;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class StreamTool {

	/** 
     * ����������ȡ���� 
     * @param inStream 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len = inStream.read(buffer)) !=-1 ){  
            outSteam.write(buffer, 0, len);  
        }  
        outSteam.close();  
        inStream.close();  
        return outSteam.toByteArray();  
    }
    
    /** 
     * �жϵ�ǰ�Ƿ������� 
     * @param context Activity��this 
     * @return 
     */  
    public static boolean CheckNetwork( final Context context) {   
	     boolean flag = false;   
	     ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
	     if (cwjManager.getActiveNetworkInfo() != null)   
	    	 flag = cwjManager.getActiveNetworkInfo().isAvailable();   
	     if (!flag) {   
		      Builder b = new AlertDialog.Builder(context).setTitle("����").setMessage("��ǰû�п��õ����磡�Ƿ���Ҫ���ò��ҿ������磡");   
		      b.setPositiveButton("����", new DialogInterface.OnClickListener() {   
			       public void onClick(DialogInterface dialog, int whichButton) {   
			        Intent mIntent = new Intent("/");   
			        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");   
			        mIntent.setComponent(comp);   
			        mIntent.setAction("android.intent.action.VIEW");  
			        context.startActivity(mIntent);  
			       }  
		      }).setNeutralButton("ȡ��", new DialogInterface.OnClickListener() {   
			       public void onClick(DialogInterface dialog, int whichButton) {   
			        dialog.cancel();   
			       }   
		      }).create();   
		      b.show();   
	     }   
	     return flag;   
    }   
    
    public static boolean isNetworkVailable(Context context) {
	    ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cManager.getActiveNetworkInfo();
	    if (info != null && info.isAvailable()) {
	    	return true;
	    } else {
	    	return false;
	    }
    }                 
}
