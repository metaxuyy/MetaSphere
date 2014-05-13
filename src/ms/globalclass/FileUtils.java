package ms.globalclass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {

	private String SDPATH;  
    
    private int FILESIZE = 4 * 1024;   
    
    public String rootName = "metashpere";
    
    public String dirpath = SDPATH+rootName;
      
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
     * ��SD���ϴ���Ŀ¼  
     * @param dirName  
     * @return  
     */  
    public File createSDDir2(String dirName){
		// �õ�һ��·����������sdcard���ļ���·��������
		String path = SDPATH+rootName+dirName;
		File path1 = new File(path);
		if (!path1.exists()) {
			// �������ڣ�����Ŀ¼��������Ӧ��������ʱ�򴴽�
			path1.mkdirs();
		}
        return path1;  
    }  
    
    /**  
     * ��SD���ϴ���Ŀ¼  
     * @param dirName  
     * @return  
     */  
    public File createSDDir(){
		// �õ�һ��·����������sdcard���ļ���·��������
		String path = SDPATH+rootName;
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
     * �ж�SD���ϵ��ļ����Ƿ����  
     * @param fileName  
     * @return  
     */  
    public boolean isFileExist2(String fileName){
    	String path = "";
    	boolean b = false;
    	if(fileName != null)
    	{
	    	if(fileName.indexOf("/") == 0)
	    		path = SDPATH + rootName + fileName;
	    	else
	    		path = SDPATH + rootName + "/" + fileName;
	        File file = new File(path);
	        b = file.exists();
    	}
        return b;
    }
    
    public boolean isFileExist3(String path){
        File file = new File(path);
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
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
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
    
    /**
     * �����ڸ��û����ļ��� ���˷�����û���ͼƬ����Ƶ�ļ�
     * @param storeid
     */
    public void createUserFile(String storeid)
    {
    	createSDDir();
    	try{
    		createSDDir2("/" + storeid);
    		createSDDir2("/" + storeid + "/" + "image/1a");//��ŶԷ��ϴ����б�СͼƬ��
    		createSDDir2("/" + storeid + "/" + "image/1b");//��ŶԷ��ϴ���ԭʼ��ͼƬ
    		createSDDir2("/" + storeid + "/" + "image/2a");//��ŶԷ������Ŀ�Ƭ�ϵ�ͼƬ
    		
    		createSDDir2("/" + storeid + "/" + "image2/1a");//������Լ��ϴ����б��СͼƬ
    		createSDDir2("/" + storeid + "/" + "image2/1b");//������Լ��ϴ���ԭʼ��ͼƬ
    		createSDDir2("/" + storeid + "/" + "image2/2a");//����ҷ�ȥ�Ŀ�Ƭ�ϵ�ͼƬ
    		
    		createSDDir2("/" + storeid + "/" + "voice");//��ŶԷ���������Ƶ�ļ�
    		createSDDir2("/" + storeid + "/" + "voice2");//������Լ�����Ƶ�ļ�
    		
    		createSDDir2("/" + storeid + "/" + "infoimage");//��ŶԷ���ҳͼƬ����
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createMyFile(String storeid)
    {
    	createSDDir();
    	try{
    		createSDDir2("/" + storeid);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImageFile1a(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image/1a/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImageFile1b(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image/1b/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImageFile2a(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image/2a/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImage2File1a(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image2/1a/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImage2File1b(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image2/1b/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public void createImage2File2a(String storeid,String fileName)
    {
    	try{
    		createSDDir2("/" + storeid + "/" + "image2/2a/"+fileName);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public String createVoiceFile1a(String storeid,String urlStr,String fileName)
    {
    	String filePath = "";
    	InputStream inputStream = null;
    	String path = SDPATH+rootName;
    	try{
//    		createSDDir2("/" + storeid + "/" + "voice/"+fileName);
            if(isFileExist("/" + storeid + "/" + "voice/"+fileName)){  
            	File file = new File("/" + storeid + "/" + "voice/"+fileName);
            	long filesize = file.length();
            	if(filesize > 0)
            	{
            		filePath = path + "/" + storeid + "/" + "voice/"+fileName;
            		return filePath;
            	}
            	else
            	{
            		file.delete();
            		inputStream = requestInputStream(urlStr);  
//                    File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream); 
            		File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "voice/"+fileName);
                    if(resultFile == null){  
                        return null;  
                    }
                    else
                    {
                    	filePath = path + "/" + storeid + "/" + "voice/"+fileName;
                		return filePath;
                    }
            	}
            } else {  
                inputStream = requestInputStream(urlStr);  
//                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
                File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "voice/"+fileName);
                if(resultFile == null){  
                    return null;  
                }
                else
                {
                	filePath = path + "/" + storeid + "/" + "voice/"+fileName;
            		return filePath;
                }
            }  
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return null;
    	}finally{  
            try {  
            	if(inputStream != null)
            		inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
    
    public String createVoice2File1a(String storeid,String urlStr,String fileName)
    {
    	String filePath = "";
    	InputStream inputStream = null;
    	String path = SDPATH+rootName;
    	try{
//    		createSDDir2("/" + storeid + "/" + "voice2/"+fileName);
    		if(isFileExist("/" + storeid + "/" + "voice2/"+fileName)){  
            	File file = new File("/" + storeid + "/" + "voice2/"+fileName);
            	long filesize = file.length();
            	if(filesize > 0)
            	{
            		filePath = path + "/" + storeid + "/" + "voice2/"+fileName;
            		return filePath;
            	}
            	else
            	{
            		file.delete();
            		inputStream = requestInputStream(urlStr);  
//                    File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream); 
            		File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "voice2/"+fileName);
                    if(resultFile == null){  
                        return null;  
                    }
                    else
                    {
                    	filePath = path + "/" + storeid + "/" + "voice2/"+fileName;
                		return filePath;
                    }
            	}
            } else {  
                inputStream = requestInputStream(urlStr);  
//                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
                File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "voice2/"+fileName);
                if(resultFile == null){  
                    return null;  
                }
                else
                {
                	filePath = path + "/" + storeid + "/" + "voice2/"+fileName;
            		return filePath;
                }
            }  
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return null;
    	}finally{  
            try {  
            	if(inputStream != null)
            		inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
    
    public String getVoice2File1aPath(String storeid,String fileName)
    {
    	String path = "";
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "voice2/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getImage2File1aPath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "image2/1a/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getImage2File1bPath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "image2/1b/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getImageFile1aPath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "image/1a/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getImageFile1bPath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "image/1b/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getImageFile2aPath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/" + "image/2a/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public String getStoreImageFilePath(String storeid,String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+rootName+"/" + storeid + "/infoimage/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public List<Map<String,Object>> createStoreImageFilePath(String storeid,List<Map<String,Object>> imglist)
    {
    	String filePath = "";
    	InputStream inputStream = null;
    	String path = SDPATH+rootName;
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		if(imglist != null && imglist.size() > 0)
    			deleteFileDrid(path + "/" + storeid + "/" + "infoimage");
//    		createSDDir2("/" + storeid + "/" + "voice2/"+fileName);
    		for(int i=0;i<imglist.size();i++)
    		{
    			String urlStr = (String)imglist.get(i).get("imageUrl");
    			UUID uuid = UUID.randomUUID();
    			String fileName = uuid.toString().replaceAll("-", "");
	    		if(isFileExist("/" + storeid + "/" + "infoimage/"+fileName)){  
	            	File file = new File("/" + storeid + "/" + "infoimage/"+fileName);
	            	long filesize = file.length();
	            	if(filesize > 0)
	            	{
	            		filePath = path + "/" + storeid + "/" + "infoimage/"+fileName;
//	            		return filePath;
	            	}
	            	else
	            	{
	            		file.delete();
	            		inputStream = requestInputStream(urlStr);  
	//                    File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream); 
	            		File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "infoimage/"+fileName);
	                    if(resultFile == null){  
//	                        return null;  
	                    }
	                    else
	                    {
	                    	filePath = path + "/" + storeid + "/" + "infoimage/"+fileName;
//	                		return filePath;
	                    }
	            	}
	            } else {  
	                inputStream = requestInputStream(urlStr);  
	//                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
	                File resultFile = fileWrite(inputStream,path + "/" + storeid + "/" + "infoimage/"+fileName);
	                if(resultFile == null){  
//	                    return null;  
	                }
	                else
	                {
	                	filePath = path + "/" + storeid + "/" + "infoimage/"+fileName;
//	            		return filePath;
	                }
	            }  
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
//    		return null;
    	}finally{  
            try {  
            	if(inputStream != null)
            		inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
    	return getImageListFileBitmap(path + "/" + storeid + "/" + "infoimage/");
    }
    
    
    public List<Map<String,Object>> getStoreImageListBitmap(String storeid)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	String path = SDPATH+rootName;
    	try{
    		dlist = getImageListFileBitmap(path + "/" + storeid + "/" + "infoimage/");
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
    
    public void deleteFile(String filePath)
    {
    	try{
    		File file = new File(filePath);
    		if(file != null)
    			file.delete();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    /**
     * ��ȡ��Ŀ¼�µ������ļ�
     * @param driPath
     * @return
     */
    public List<String> getImageListFilePath(String driPath)
    {
    	List<String> filelist = new ArrayList<String>();
    	try{
    			File file = new File(driPath);
    			File[] files = file.listFiles();
    			for (int i = 0; i < files.length; i++) {
    			  if(!files[i].isDirectory()){
    			     String path = files[i].getPath();
    			     filelist.add(path);
    			  }
    			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return filelist;
    }
    
    /**
     * ��ȡ��Ŀ¼�µ�����ͼƬBitmap�ļ�
     * @param driPath
     * @return
     */
    public List<Map<String,Object>> getImageListFileBitmap(String driPath)
    {
    	List<Map<String,Object>> filelist = new ArrayList<Map<String,Object>>();
    	try{
			File file = new File(driPath);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					String path = files[i].getPath();
					Map<String,Object> map = new HashMap<String,Object>();
					Bitmap bmpimg = getLoacalBitmap(path);
					map.put("img", bmpimg);
					filelist.add(map);
				}
			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return filelist;
    }
    
    /**
     * ɾ�����ļ����µ����������ļ�
     * @param driPath
     * @return
     */
    public void deleteFileDrid(String driPath)
    {
    	try{
			File file = new File(driPath);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					files[i].delete();
				}
			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    /** 
     * �����YԴURL��ַȡ���YԴݔ���� 
     * @param url URL��ַ 
     * @return �YԴݔ���� 
     * @throws ClientProtocolException 
     * @throws IOException 
     * */  
    private InputStream requestInputStream(String url) throws ClientProtocolException, IOException {  
        InputStream result = null;  
        HttpGet httpGet = new HttpGet(url);  
        HttpClient httpClient = new DefaultHttpClient();  
        HttpResponse httpResponse = httpClient.execute(httpGet);  
        int httpStatusCode = httpResponse.getStatusLine().getStatusCode();  
        if(httpStatusCode == HttpStatus.SC_OK) {  
            HttpEntity httpEntity = httpResponse.getEntity();  
            int size = (int) httpEntity.getContentLength();  
            result = httpEntity.getContent();  
        }  
        return result;  
    }
    
    /** 
     * �ļ����� 
     * @param in ����Դݔ���� 
     * @param path �ļ�·�� 
     * @param listener ���d�M�ȱO �� 
     * */  
    private File fileWrite(InputStream in, String path) throws IOException {  
        File file = createFile(path);  
        FileOutputStream fileOutputStream = new FileOutputStream(file);  
        byte buffer[] = new byte[1024];  
        int readBytes = 0;  
        while ((readBytes = in.read(buffer)) != -1) {  
            fileOutputStream.write(buffer, 0, readBytes);  
        }  
        in.close();  
        fileOutputStream.close();  
        return file;
    }  
    
    /** 
     * �����ļ�·�������ļ� 
     * @param path �ļ�·�� 
     * @return �ļ�File���� 
     * @return IOException 
     * */  
    private File createFile(String path) throws IOException {  
        File file = new File(path);  
        file.createNewFile();  
        return file;  
    }  
    
    /**
   	* ת������ͼƬΪbitmap
   	* http://bbs.3gstdy.com
   	* @param url
   	* @return
   	*/
   	public Bitmap getLoacalBitmap(String url) {
   	     try {
   			FileInputStream fis = new FileInputStream(url);
   			BitmapFactory.Options opts = new BitmapFactory.Options();
   			opts.inSampleSize = 1;
   			opts.inPreferredConfig = Bitmap.Config.RGB_565;
   			opts.inPurgeable = true;
   			opts.inInputShareable = true;
   			
   			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//   			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
   			return bitmap;
   	     } catch (FileNotFoundException e) {
   	          e.printStackTrace();
   	          return null;
   	     }
   	}
   	
   	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(bitName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   	
   	String FileName;
   	int fileSize;
   	int downLoadFilePosition;
	public String downFile(String url, String filePath)
			throws IOException {
//		if (fileName == null || fileName == "")
//			this.FileName = url.substring(url.lastIndexOf("/") + 1);
//		else
//			this.FileName = fileName; // ȡ���ļ���������������ļ�������ʹ�����ļ���
		
//		String filePath = SDPATH+rootName+"/" + storeid + "/" + "image/1a/" + this.FileName;

		URL Url = new URL(url);
		URLConnection conn = Url.openConnection();
		conn.setRequestProperty("Accept-Encoding", "identity");
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();// ������Ӧ��ȡ�ļ���С
		if (this.fileSize <= 0) { // ��ȡ���ݳ���Ϊ0
//			throw new RuntimeException("�޷���֪�ļ���С ");
			return "";
		}
		if (is == null) { // û��������
			System.out.println("Ҫ���ص��ļ�������");
//			throw new RuntimeException("�޷���ȡ�ļ�");
			return "";
		}
		FileOutputStream FOS = new FileOutputStream(filePath); // ����д���ļ��ڴ�����ͨ��������Ŀ��д�ļ�
		byte buf[] = new byte[1024];
		downLoadFilePosition = 0;
		int numread;
		while ((numread = is.read(buf)) != -1) {
			FOS.write(buf, 0, numread);
			downLoadFilePosition += numread;
		}
		try {
			is.close();
		} catch (Exception ex) {
			;
		}
		
		return filePath;
	}
	
	public String getSaveImageLocalPath(String fileName)
    {
    	String path = null;
    	try{
    		path = SDPATH+"hereimg/"+fileName;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
}
