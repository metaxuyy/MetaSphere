package ms.fortuneJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import ms.fortuneJ.Usual;

import org.bouncycastle.util.io.Streams;

import android.os.Build;

/**
 * Http高性能调用类
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("unused")
public class HttpFastUtil {
	/*
	 * 构造函数
	 */
	public HttpFastUtil() {

	}

	/**
	 * Http POST返回byte[]
	 * @param URI		调用Url
	 * @param data		传输数据byte[]
	 * @param isSSL		是否Https
	 * @return	byte[]
	 * @throws Exception
	 */
	public static byte[] f_httpPostByte(String URI,byte[] bts,boolean isSSL) 
	throws Exception
	{
		if(bts==null || bts.length==0)
		{
			return null;
		}
		return f_httpRestfulByte(URI,"POST",bts.toString(),Usual.mCharset_utf8,"text/plain",isSSL);
	}

	/**
	 * Http Restful返回byte[]数据
	 * 
	 * @param URI
	 *            调用Url
	 * @param method
	 *            POST,GET,PUT,DELETE
	 * @param data
	 *            传入数据String
	 * @param charset
	 *            Charset字符集
	 * @param contextType
	 *            数据传输格式
	 * @param isSSL
	 *            是否Https
	 * @return byte[]数组
	 * @throws Exception
	 */
	public static byte[] f_httpRestfulByte(String URI, String method,
			String data, Charset charset, String contextType, Boolean isSSL)
			throws Exception {
		// HTTP connection reuse which was buggy pre-froyo
//		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
//		    System.setProperty("http.keepAlive", "false");
//		}
		
		try {
			// Make a HTTP connection to the server
			URL url = new URL(URI);
			// if(isSSL)
			// {
			// Security.addProvider (new com.sun.net.ssl.internal.ssl.Provider
			// ());
			// SSLSocketFactory factory = (SSLSocketFactory)
			// SSLSocketFactory.getDefault ();
			// SSLSocket socket = (SSLSocket) factory.createSocket (url.getHost
			// (),443);
			// // socket.getOutputStream()
			// }
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url
					.openConnection();
			// Set the request method as POST
			if (Usual.f_isNullOrWhiteSpace(method)) {
				httpUrlConnection.setRequestMethod("POST");
			} else {
				httpUrlConnection.setRequestMethod(method.toUpperCase());
			}
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setReadTimeout(Usual.mUrlReadTime);
			httpUrlConnection.setConnectTimeout(Usual.mUrlConTime);
			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			// httpUrlConnection.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");
			// httpUrlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			// 统一传递Stream
			if (Usual.f_isNullOrWhiteSpace(contextType)) {
				httpUrlConnection.setRequestProperty("Content-type",
						"text/plain");
			} else {
				httpUrlConnection.setRequestProperty("Content-type",
						contextType);
			}
			OutputStream outstream = httpUrlConnection.getOutputStream();
			// XML are encoded in UTF-8 format
			OutputStreamWriter outstreamw = new OutputStreamWriter(outstream,
					charset);
			outstreamw.write(data);
			outstreamw.flush();
			outstreamw.close();
//			// 进行连接
			httpUrlConnection.connect();
			int mCode = httpUrlConnection.getResponseCode();
			if (mCode != HttpURLConnection.HTTP_OK) {
				throw new IOException("HttpStatus异常:" + mCode);
			}
			byte[] bts = null;
			try {
				InputStream istream = httpUrlConnection.getInputStream();
				if (istream != null) {
					// 如果支持GZip
					if (httpUrlConnection.getContentEncoding()!=null && httpUrlConnection.getContentEncoding().toUpperCase()
							.indexOf(Usual.mGZipName) > -1) {
						GZIPInputStream gzipStream = new GZIPInputStream(
								istream);
						bts = Streams.readAll(gzipStream);
					} else {
						bts = Streams.readAll(istream);
					}
				}
				istream.close();
				istream = null;
			} catch (Exception exp) {
				exp.printStackTrace();
			} finally {
				if (httpUrlConnection != null) {
					httpUrlConnection.disconnect();
					httpUrlConnection = null;
				}
			}
			return bts;
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}

	}
}