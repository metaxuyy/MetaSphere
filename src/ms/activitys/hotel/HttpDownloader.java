package ms.activitys.hotel;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.net.HttpURLConnection;  
import java.net.MalformedURLException;  
import java.net.URL;  

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Environment;

public class HttpDownloader {

	private URL url = null;   
    
    /**  
     * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容  
     * 1.创建一个URL对象  
     * 2.通过URL对象,创建一个HttpURLConnection对象  
     * 3.得到InputStream  
     * 4.从InputStream当中读取数据  
     * @param urlStr  
     * @return  
     */  
    public String download(String urlStr){  
        StringBuffer sb = new StringBuffer();  
        String line = null;  
        BufferedReader buffer = null;  
        try {  
            url = new URL(urlStr);  
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();  
            buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));  
            while( (line = buffer.readLine()) != null){  
                sb.append(line);  
            }  
              
        }   
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                buffer.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return sb.toString();  
    }  
  
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
            	File file = new File(path + fileName);
            	long filesize = file.length();
            	if(filesize > 0)
            		return 1;
            	else
            	{
            		file.delete();
            		inputStream = requestInputStream(urlStr);  
//                    File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream); 
            		File resultFile = fileWrite(inputStream,Environment.getExternalStorageDirectory() + "/"+path+fileName);
                    if(resultFile == null){  
                        return -1;  
                    }  
            	}
            } else {  
                inputStream = requestInputStream(urlStr);  
//                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
                File resultFile = fileWrite(inputStream,Environment.getExternalStorageDirectory() + "/"+path+fileName);
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
            	if(inputStream != null)
            		inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return 0;  
    }  
      
    /**  
     * 根据URL得到输入流  
     * @param urlStr  
     * @return  
     */  
    public InputStream getInputStreamFromURL(String urlStr) {  
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null;  
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
}
