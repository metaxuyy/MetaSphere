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
        //得到当前外部存储设备的目录( /SDCARD )  
        SDPATH = Environment.getExternalStorageDirectory() + "/";  
    }  
      
    /**  
     * 在SD卡上创建文件  
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
     * 在SD卡上创建目录  
     * @param dirName  
     * @return  
     */  
    public File createSDDir(String dirName){
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = SDPATH+dirName;
		File path1 = new File(path);
		if (!path1.exists()) {
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();
		}
        return path1;  
    } 
    
    /**  
     * 在SD卡上创建目录  
     * @param dirName  
     * @return  
     */  
    public File createSDDir2(String dirName){
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = SDPATH+rootName+dirName;
		File path1 = new File(path);
		if (!path1.exists()) {
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();
		}
        return path1;  
    }  
    
    /**  
     * 在SD卡上创建目录  
     * @param dirName  
     * @return  
     */  
    public File createSDDir(){
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = SDPATH+rootName;
		File path1 = new File(path);
		if (!path1.exists()) {
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();
		}
        return path1;  
    }  
      
    /**  
     * 判断SD卡上的文件夹是否存在  
     * @param fileName  
     * @return  
     */  
    public boolean isFileExist(String fileName){  
        File file = new File(SDPATH + fileName);  
        return file.exists();  
    }
    
    /**  
     * 判断SD卡上的文件夹是否存在  
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
     * 将一个InputStream里面的数据写入到SD卡中  
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
	 * 保存文件
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
     * 创建于该用户的文件夹 用了放与该用户的图片和音频文件
     * @param storeid
     */
    public void createUserFile(String storeid)
    {
    	createSDDir();
    	try{
    		createSDDir2("/" + storeid);
    		createSDDir2("/" + storeid + "/" + "image/1a");//存放对方上传的列表小图片房
    		createSDDir2("/" + storeid + "/" + "image/1b");//存放对方上传的原始大图片
    		createSDDir2("/" + storeid + "/" + "image/2a");//存放对方发来的卡片上的图片
    		
    		createSDDir2("/" + storeid + "/" + "image2/1a");//存放我自己上传的列表的小图片
    		createSDDir2("/" + storeid + "/" + "image2/1b");//存放我自己上传的原始大图片
    		createSDDir2("/" + storeid + "/" + "image2/2a");//存放我发去的卡片上的图片
    		
    		createSDDir2("/" + storeid + "/" + "voice");//存放对法发来的音频文件
    		createSDDir2("/" + storeid + "/" + "voice2");//存放我自己的音频文件
    		
    		createSDDir2("/" + storeid + "/" + "infoimage");//存放对方主页图片介绍
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
     * 获取该目录下的所有文件
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
     * 获取该目录下的所有图片Bitmap文件
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
     * 删除该文件夹下的所有所有文件
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
     * 根據資源URL地址取得資源輸入流 
     * @param url URL地址 
     * @return 資源輸入流 
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
     * 文件寫入 
     * @param in 數據源輸入流 
     * @param path 文件路徑 
     * @param listener 下載進度監聽器 
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
     * 根據文件路徑創建文件 
     * @param path 文件路徑 
     * @return 文件File實例 
     * @return IOException 
     * */  
    private File createFile(String path) throws IOException {  
        File file = new File(path);  
        file.createNewFile();  
        return file;  
    }  
    
    /**
   	* 转换本地图片为bitmap
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
//			this.FileName = fileName; // 取得文件名，如果输入新文件名，则使用新文件名
		
//		String filePath = SDPATH+rootName+"/" + storeid + "/" + "image/1a/" + this.FileName;

		URL Url = new URL(url);
		URLConnection conn = Url.openConnection();
		conn.setRequestProperty("Accept-Encoding", "identity");
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (this.fileSize <= 0) { // 获取内容长度为0
//			throw new RuntimeException("无法获知文件大小 ");
			return "";
		}
		if (is == null) { // 没有下载流
			System.out.println("要下载的文件不存在");
//			throw new RuntimeException("无法获取文件");
			return "";
		}
		FileOutputStream FOS = new FileOutputStream(filePath); // 创建写入文件内存流，通过此流向目标写文件
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
