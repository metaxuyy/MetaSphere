package ms.globalclass.httppost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;

public class HttpClientToServer {
	
	String urlAddress = "http://10.0.0.10:8080/AndroidServer/login.do";
	private static SchemeRegistry registry;
	private static ClientConnectionManager connectionManager;
	/** 
     * 最大连接数 
     */  
    public final static int MAX_TOTAL_CONNECTIONS = 800;  
    /** 
     * 获取连接的最大等待时间 
     */  
    public final static int WAIT_TIMEOUT = 60000;  
    /** 
     * 每个路由最大连接数 
     */  
    public final static int MAX_ROUTE_CONNECTIONS = 400;  
    /** 
     * 连接超时时间 
     */  
    public final static int CONNECT_TIMEOUT = 10000;  
    /** 
     * 读取超时时间 
     */  
    public final static int READ_TIMEOUT = 10000;  
    private static HttpParams httpParams;
    
	public HttpClientToServer(){
		httpParams = new BasicHttpParams();  
        // 设置最大连接数  
        ConnManagerParams.setMaxTotalConnections(httpParams, MAX_TOTAL_CONNECTIONS);  
        // 设置获取连接的最大等待时间  
        ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);  
        // 设置每个路由最大连接数  
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);  
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,connPerRoute);  
        // 设置连接超时时间  
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);  
        // 设置读取超时时间  
        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);  
  
        SchemeRegistry registry = new SchemeRegistry();  
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8086));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
  
        connectionManager = new ThreadSafeClientConnManager(httpParams, registry);  
	}
		
	public String doGet(String url,List<NameValuePair> paramss){
		
//		String getUrl = urlAddress + "?username="+username+"&password="+password;
		Map<String,String> params = new HashMap<String,String>();
		for(int i=0;i<paramss.size();i++)
		{
			NameValuePair nv = paramss.get(i);
			params.put(nv.getName(),nv.getValue());
		}
		String str = "";
		if(paramss != null)
		 str = Douban.addParam2(params);
		String actionUrl = url+"?"+str;
		HttpGet httpGet = new HttpGet(actionUrl);
		//HttpParams hp = httpGet.getParams();
		//hp.
		//httpGet.setp
//		AndroidHttpClient hc = AndroidHttpClient.newInstance("Mozilla/5.0");
		HttpClient hc = new DefaultHttpClient(connectionManager, httpParams);
		try {
			HttpResponse ht = hc.execute(httpGet);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity he = ht.getEntity();
				InputStream is = he.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					//response = br.readLine();
					response = response + readLine;
				}
				is.close();
				br.close();
				
				//String str = EntityUtils.toString(he);
				System.out.println("========="+response);
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "exception";
		} finally {  
//            abortConnection(httpGet, hc);  
        } 
		
		
		
		
	}
	
	public String doPost(String url,List<NameValuePair> params){
		//String getUrl = urlAddress + "?username="+username+"&password="+password;
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Accept", "text/json"); //认证token  
//		httpPost.addHeader("Accept-Encoding", "gzip, deflate");  
//	    httpPost.addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");  
	    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");  
	    httpPost.addHeader("Host", "121.199.8.186");  
	    httpPost.addHeader("User-Agent", "Mozilla/5.0");  
//		List params = new ArrayList();
//		NameValuePair pair1 = new BasicNameValuePair("username", username);
//		NameValuePair pair2 = new BasicNameValuePair("password", password);
//		params.add(pair1);
//		params.add(pair2);
		BasicHttpParams httpParameters = new BasicHttpParams();
		// Set the default socket timeout (SO_TIMEOUT) 
		HttpConnectionParams.setConnectionTimeout(httpParameters, 8000);
		// in milliseconds which is the timeout for waiting for data.  
		HttpConnectionParams.setSoTimeout(httpParameters, 8000);
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		
//		AndroidHttpClient hc = AndroidHttpClient.newInstance("Mozilla/5.0");
		HttpClient hc = new DefaultHttpClient(connectionManager, httpParams);
//		AndroidHttpClient  hc = AndroidHttpClient.newInstance("");
//		HttpHost proxy = new HttpHost("119.147.213.172", 8086);
//		hc.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,proxy);
		try {
			HttpResponse ht = hc.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
//				InputStream is = het.getContent();
//				BufferedReader br = new BufferedReader(new InputStreamReader(is));
//				String response = "";
//				String readLine = null;
//				while((readLine =br.readLine()) != null){
//					//response = br.readLine();
//					response = response + readLine;
//				}
//				is.close();
//				br.close();
				
				String json =EntityUtils.toString(het,"UTF-8");
				
				//String str = EntityUtils.toString(he);
				System.out.println("========="+json);
				return json;
			}else{
				return "timeout";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "timeout";
		} finally {  
//            abortConnection(httpPost, hc);  
//            hc = null;
        } 
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "exception";
//		}
		
	}
	
	/** 
     * 释放HttpClient连接 
     *  
     * @param hrb 
     * @param httpclient 
     */  
    private static void abortConnection(final HttpRequestBase httpRequestBase,  
            final AndroidHttpClient httpclient) {  
        if (httpRequestBase != null) {  
            httpRequestBase.abort();  
        }  
        if (httpclient != null) {  
            httpclient.close();
        }  
    }  

    private static void abortConnection(final HttpRequestBase httpRequestBase,  
            final HttpClient httpclient) {  
        if (httpRequestBase != null) {  
            httpRequestBase.abort();  
        }  
        if (httpclient != null) {  
            httpclient.getConnectionManager().shutdown();
        }  
    }  

}
