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
     * ����URL�����ļ�,ǰ��������ļ����е��������ı�,�����ķ���ֵ�����ı����е�����  
     * 1.����һ��URL����  
     * 2.ͨ��URL����,����һ��HttpURLConnection����  
     * 3.�õ�InputStream  
     * 4.��InputStream���ж�ȡ����  
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
     *      -1:�ļ����س���  
     *       0:�ļ����سɹ�  
     *       1:�ļ��Ѿ�����  
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
     * ����URL�õ�������  
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
}
