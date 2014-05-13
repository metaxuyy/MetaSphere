package ms.activitys.hotel;

import java.io.BufferedOutputStream;
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
  
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;  

public class FileUtils {

	private String SDPATH;  
    
    private int FILESIZE = 4 * 1024;   
      
    public String getSDPATH(){  
        return SDPATH;  
    }  
      
    public FileUtils(){  
        //�õ���ǰ�ⲿ�洢�豸��Ŀ¼( /SDCARD )  
        SDPATH = Environment.getExternalStorageDirectory() + "/";  
    }  
      
    /**  
     * ��SD���ϴ����ļ�  
     * @param fileName  
     * @return  
     * @throws IOException  
     */  
    public File createSDFile(String fileName) throws IOException{  
		File file = new File(SDPATH + fileName);
		file.createNewFile();
        return file;  
    }  
      
    /**  
     * ��SD���ϴ���Ŀ¼  
     * @param dirName  
     * @return  
     */  
    public File createSDDir(String dirName){  
		// �õ�һ��·����������sdcard���ļ���·��������
		String path = SDPATH+dirName;
		File path1 = new File(path);
		if (!path1.exists()) {
			// �������ڣ�����Ŀ¼��������Ӧ��������ʱ�򴴽�
			path1.mkdirs();
		}
        return path1;  
    }  
      
    /**  
     * �ж�SD���ϵ��ļ����Ƿ����  
     * @param fileName  
     * @return  
     */  
    public boolean isFileExist(String fileName){  
        File file = new File(SDPATH + fileName);  
        return file.exists();  
    }  
      
    /**  
     * ��һ��InputStream���������д�뵽SD����  
     * @param path  
     * @param fileName  
     * @param input  
     * @return  
     */  
    public File write2SDFromInput(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try {  
            createSDDir(path);  
            file = createSDFile(path + "/" + fileName);  
            output = new FileOutputStream(file);  
            byte[] buffer = new byte[FILESIZE];  
            while((input.read(buffer)) != -1){  
                output.write(buffer);  
            }  
            output.flush();  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                output.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file;  
    }
    
	/**
	 * �����ļ�
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public void saveFile(String path,Bitmap bm, String fileName) throws IOException {
		createSDDir(path);
		File myCaptureFile = createSDFile(path + "/" + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}
    
    public File write2SDFromInput2(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try {  
            createSDDir(path); 
            System.out.println("path+fileName==="+path + fileName);
            file = createSDFile(path + fileName);  
            output = new FileOutputStream(file);  
            byte[] buffer = new byte[FILESIZE];  
            while((input.read(buffer)) != -1){  
                output.write(buffer);  
            }  
            output.flush();  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                output.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file;  
    }  
}
