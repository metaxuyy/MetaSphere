package ms.fortuneJ;

import java.io.*;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.zip.*;

import javax.net.ssl.SSLHandshakeException;

import javax.net.ssl.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * 通用静态函数库,不可继承
 * 用于常用静态函数定义
 * @author nick
 * 
 */
@SuppressWarnings({ "unchecked", "unused" })
public class Usual
{

	/**
	 * 保存动态执行函数的静态Class对象
	 */
	private final static HashMap<String, Class> cht_st = new HashMap<String, Class>();
	/**
	 * 保存动态执行函数的New Class对象
	 */
	private final static HashMap<String, Object> cht_obj = new HashMap<String, Object>();
	/**
	 * 静态空字段,""
	 */
	public final static String mEmpty="";
	/**
	 * 静态空字符,' '
	 */
	public final static char mBlankSpaceChar=' ';
	/**
	 * 静态空字段," "
	 */
	public final static String mBlankSpace=" ";
	/**
	 * 双引号字符
	 */
	public final static String mDoubleQuote="\"";
	/**
	 * Http POST/GET连接超时时间,毫秒
	 */
	public final static int mUrlConTime=1000*60*3;
	/**
	 * Http POST/GET连接超时时间,毫秒
	 */
	public final static int mUrlReadTime=1000*60*30;
	/**
	 * UTF-8名称
	 */
	public final static String mUTF8Name="UTF-8";
	/**
	 * GB2312名称
	 */
	public final static String mGB2312Name="GB2312";
	/**
	 * 采用UTF-8作为基础字符格式
	 */
	public final static Charset mCharset_utf8=Charset.forName(mUTF8Name);
	/**
	 * 采用C#,Java,Window Mobile,Java Mobile都支持的字符加密格式
	 * 采用gb2312字符格式,也可以设定为unicode
	 */
	public final static Charset mCharset_gb2312=Charset.forName(mGB2312Name);
	/**
	 * 基础数据大小,4K
	 */
	public final static int mByteBaseSize=1024*4;
	/**
	 * GZip名称
	 */
	public final static String mGZipName="GZIP";
	
	
	/**
	  * nitobi Grid 页面大小字段
	  */
	 public static final String mgPageSize="PageSize";
	 /**
	  * nitobi Grid 排序字段
	  */
	 public static final String mgSortColumn="SortColumn";
	 /**
	  * nitobi Grid 排序顺序
	  */
	 public static final String mgSortDirection="SortDirection";
	 /**
	  * nitobi Grid 当前页面开始数目
	  */
	 public static final String mgStart="start";
	 /**
	  * nitobi Grid 当前页面开始数目,不确定
	  */
	 public static final String mgStartRecordIndex="StartRecordIndex";
	 
	 
	 /**
	  * SQL: select 
	  */
	 public static final String msSelect=" select ";
	 /**
	  * SQL: from
	  */
	 public static final String msFrom=" from ";
	 /**
	  * SQL: where 
	  */
	 public static final String msWhere=" where ";
	 /**
	  * SQL: group by 
	  */
	 public static final String msGroupBy=" group by ";
	 /**
	  * SQL: order by  
	  */
	 public static final String msOrderBy=" order by "; 

	 
	 /**
	  * 格式化Date,为yyyy-MM-dd HH:mm:ss格式
	  */
	 public final static DateFormat mfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 /**
	  * 格式化Date,为yyyy-MM-dd HH:mm:ss.SSS格式
	  */
	 public final static DateFormat mfAllMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	 /**
	  * 格式化Date,为yyyy-MM-dd格式
	  */
	 public final static DateFormat mfYMD=new SimpleDateFormat("yyyy-MM-dd");
	 /**
	  * 格式化Date,为yyyyMMdd格式
	  */
	 public final static DateFormat mfYMDSimple=new SimpleDateFormat("yyyyMMdd");
	  /**
	  * 格式化Date,为yyyy-MM-dd 格式
	  */
	 public final static DateFormat mfYMD_HM=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 /**
	  * 格式化Date,为Oracle日期yyyy-MM-dd HH:mm:ss SSS格式
	  */
	 public final static DateFormat mfYMD_Oracle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	 /**
	  * 格式化Date,为SQLServer日期yyyy-MM-dd HH:mm:ss.SSS格式
	  */
	 public final static DateFormat mfYMD_SQLServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	 
	/**
	 * 构造函数
	 */
	public Usual() 
	{
	}

	
	/**
	 * 动态执行静态函数,返回函数执行结果
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object f_evalMethodStatic(String clsName,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Class curClass = null;
		try 
		{
			//缓存Class对象,下次不用动态生成	
			if (cht_st.containsKey(clsName))
			{
				curClass = cht_st.get(clsName);
			} 
			else 
			{
				//生成Class静态函数调用对象
				curClass = Class.forName(clsName);
				if (curClass != null) 
				{
					cht_st.put(clsName, curClass);
				} 
				else 
				{
					return rObj;
				}
			}
			rObj=f_evalMethodStatic(curClass,methodName, types,objs);
		} 
		catch (Exception e) 
		{
			rObj = null;
		} 
		finally 
		{
			curClass = null;
		}
		return rObj;
	}
	/**
	 * 动态执行静态函数,返回函数执行结果
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 */
	public static Object f_evalMethodStatic(Class cls,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Method mMethod=null;
		try {
			// 生成对应函数
			mMethod = cls.getMethod(methodName, types);
			if (mMethod != null) 
			{
				// 执行函数
				rObj = mMethod.invoke(null, objs);
			} 
			else 
			{
				String clsNameString=cls.getName();
				throw new Exception("Init Failed from class" + clsNameString
						+ System.getProperty("line.seperator", "\n")
						+ "method " + methodName + "exists.");
			}
		} 
		catch (Exception e) 
		{
			rObj=null;
		}
		finally
		{
			mMethod=null;
		}
		return rObj;
	}
	/**
	 * 动态执行静态函数,返回函数执行结果
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 * @throws Exception
	 */
	public static Object f_evalMethod(String clsName,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Object obj=null;
		try 
		{
			Class curClass = null;
			//缓存Class对象,下次不用动态生成	
			if (cht_obj.containsKey(clsName))
			{
				obj = cht_obj.get(clsName);
			} 
			else 
			{
				curClass = Class.forName(clsName);
				obj=curClass.newInstance();
				if (obj != null) 
				{
					cht_obj.put(clsName, obj);
				} 
				else 
				{
					return rObj;
				}
			}
			rObj=f_evalMethod(obj,methodName, types,objs);
		} 
		catch (Exception e) 
		{
			rObj = null;
		} 
		finally 
		{
			obj = null;
		}
		return rObj;
	}
	/**
	 * 动态执行静态函数,返回函数执行结果
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return 执行函数后返回对象
	 */
	public static Object f_evalMethod(Object obj,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Method nMethod=null;
		Class clsClass=null;
		try {
			clsClass=obj.getClass();
			// 生成对应函数
			nMethod = clsClass.getMethod(methodName, types);
			if (nMethod != null) 
			{
				// 执行函数
				rObj = nMethod.invoke(obj, objs);
			} 
			else 
			{
				String clsNameString=clsClass.getName();
				throw new Exception("Init Failed from class" + clsNameString
						+ System.getProperty("line.seperator", "\n")
						+ "method " + methodName + "exists.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			rObj=null;
		}
		finally
		{
			nMethod=null;
			clsClass=null;
		}
		return rObj;
	}

	
	/**
	 * 获取Post后返回数据,Post采用Stream方式
	 * @param http路径
	 * @param 传递的Post参数
	 * @param charset 编码类型
	 * @return 返回数据
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars, Charset charset) 
	throws Exception
    {
		return f_getStringByPost(URI,pars,charset,"text/plain");
    }
	/**
	 * 获取Post后返回数据,Post采用Stream方式
	 * @param http路径
	 * @param 传递的Post参数
	 * @param charset 编码类型
	 * @param contentType
	 * @return 返回数据
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars, Charset charset,String contentType) 
	throws Exception
    {
		if(!Usual.f_isNullOrWhiteSpace(pars))
		{
			return Usual.mEmpty;
		}
		StringBuilder sb=new StringBuilder();
		HttpURLConnection httpUrlConnection =null;
		try 
		{
			// Make a HTTP connection to the server
			URL url = new URL(URI);
			httpUrlConnection = (HttpURLConnection)url.openConnection();
			// Set the request method as POST
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false); 
			httpUrlConnection.setReadTimeout(mUrlConTime);
			httpUrlConnection.setConnectTimeout(mUrlConTime);
			// 设定传送的内容类型是可序列化的java对象 
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) 
			//httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			//httpUrlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			//统一传递Stream
			if(f_isNullOrEmpty(contentType))
			{
				httpUrlConnection.setRequestProperty("Content-type", "text/plain");
			}
			else
			{
				httpUrlConnection.setRequestProperty("Content-type", contentType);
			}
//			httpUrlConnection.connect();
			OutputStream outstream = httpUrlConnection.getOutputStream();
			// XML are encoded in UTF-8 format
			OutputStreamWriter outstreamw = new OutputStreamWriter(outstream,charset);
			outstreamw.write(pars);
			outstreamw.flush();
			outstreamw.close();
			// 调用HttpURLConnection连接对象的getInputStream()函数, 
			// 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。 
			BufferedReader reader = new BufferedReader
			(
					new InputStreamReader(httpUrlConnection.getInputStream(),charset)
			);
	        char[] read = new char[Usual.mByteBaseSize];
	        int count = reader.read(read, 0, Usual.mByteBaseSize);
	        while (count > 0)
	        {
	            String str = new String(read, 0, count);
	            sb.append(str);
	            count = reader.read(read, 0, Usual.mByteBaseSize);
	        }
//			String lineStr;
//	        while ((lineStr = reader.readLine()) != null)
//	        {
//	        	sb.append(lineStr);
//	        }
	        //关闭数据读取
	        if(reader!=null)
	        {
	        	reader.close();
	        	reader=null;
	        }
	        //判断是否正常连接
			int sc=httpUrlConnection.getResponseCode();
			if(sc!=HttpStatus.SC_OK)
			{
				throw new IOException("HttpStatus异常:"+sc+","+sb.toString());
			}
		} 
		catch (Exception exp) 
		{
			exp.printStackTrace();
		}
		finally
		{
			 //断开连接
	        if(httpUrlConnection!=null)
	        {
	        	httpUrlConnection.disconnect();
	        	httpUrlConnection=null;
	        }
		}
		return sb.toString();
    }
	/**
	 * 获取Post后返回数据,Post采用Stream方式
	 * @param http路径
	 * @param 传递的Post参数
	 * @return 返回数据
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars) 
	throws Exception
    {
		return f_getStringByPost(URI,pars,mCharset_utf8);
    }
	/**
	 * 获取Post后返回数据,Post采用Stream方式
	 * @param URI
	 * @param pars
	 * @param contentType
	 * @return
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars,String contentType) 
	throws Exception
    {
		return f_getStringByPost(URI,pars,mCharset_utf8,contentType);
    }
	/**
	 * 获取GET后返回数据
	 * @param http路径
	 * @param charset
	 * @return 返回数据
	 * @throws Exception 
	 */
	public static String f_getStringByGet(String URI, Charset charset) 
	throws Exception
    {
		StringBuilder sb=new StringBuilder();
		// Make a HTTP connection to the server
		HttpURLConnection httpUrlConnection=null;
		try 
		{
			URL url = new URL(URI);
			httpUrlConnection = (HttpURLConnection)url.openConnection();
			// Set the request method as GET
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false); 
			httpUrlConnection.setReadTimeout(mUrlConTime);
			httpUrlConnection.setConnectTimeout(mUrlConTime);
			//httpUrlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpUrlConnection.setRequestProperty("Content-Type","text/plain");
			httpUrlConnection.connect();
			BufferedReader reader = new BufferedReader
			(
					new InputStreamReader(httpUrlConnection.getInputStream(),charset)
			);
	        char[] read = new char[Usual.mByteBaseSize];
	        int count = reader.read(read, 0, Usual.mByteBaseSize);
	        while (count > 0)
	        {
	            String str = new String(read, 0, count);
	            sb.append(str);
	            count = reader.read(read, 0, Usual.mByteBaseSize);
	        }
//	        String lineStr=null;
//	        while ((lineStr = reader.readLine()) != null)
//	        {
//	        	sb.append(lineStr);
//	        }
	        //关闭数据读取
	        if(reader!=null)
	        {
	        	reader.close();
	        	reader=null;
	        }
	        //判断是否正常连接
			int sc=httpUrlConnection.getResponseCode();
			if(sc!=HttpStatus.SC_OK)
			{
				throw new IOException("HttpStatus异常:"+sc+","+sb.toString());
			}
		} 
		catch (Exception exp) 
		{
			exp.printStackTrace();
		}
		finally
		{
			//断开连接
	        if(httpUrlConnection!=null)
	        {
	        	httpUrlConnection.disconnect();
	        	httpUrlConnection=null;
	        }
		}
		return sb.toString();
    }
	/**
	 * 获取GET后返回数据,UTF-8编码格式
	 * @param http路径
	 * @return 返回数据
	 */
	public static String f_getStringByGet(String URI)
	throws Exception
    {
		return f_getStringByGet(URI,mCharset_utf8);
    }
	/**
	 * 获取Http Put数据
	 * @param URI
	 * @param pars
	 * @param charset
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public static String f_getStringByPut(String URI, String pars, Charset charset,String contentType) 
	throws Exception 
    {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setTimeout(params, Usual.mUrlConTime);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, charset.name());
        HttpConnectionParams.setConnectionTimeout(params, mUrlConTime);
        StringBuilder sb=new StringBuilder();
        HttpClient httpclient=null;
        try 
        {
        	httpclient = new DefaultHttpClient(params);
//          BasicHttpContext localcontext = new BasicHttpContext();
            HttpPut obj = new HttpPut(URI);
            StringEntity strEntity=new StringEntity(pars);
    		strEntity.setContentType(contentType);
    		strEntity.setContentEncoding(charset.name());
    		obj.setEntity(strEntity);
    		//重试次数
    		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() 
    		{
    			public boolean retryRequest(IOException exception,int executionCount,HttpContext context) 
    			{
    				//重试3次后取消
    				if (executionCount > 3) 
    				{
    					// Do not retry if over max retry count
    					return false;
    				}
    				if (exception instanceof NoHttpResponseException) 
    				{
    					// Retry if the server dropped connection on us
    					return true;
    				}
    				if (exception instanceof SSLHandshakeException) 
    				{
    					// Do not retry on SSL handshake exception
    					return false;
    				}
    				HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
    				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
    				if (idempotent) 
    				{
    					// Retry if the request is considered idempotent
    					return true;
    				}
    				return false;
    			}
    		};
    		((AbstractHttpClient)httpclient).setHttpRequestRetryHandler(myRetryHandler);
    		HttpResponse response = httpclient.execute(obj);
    		//处理返回状态
            int sc = response.getStatusLine().getStatusCode();
            if(sc== HttpStatus.SC_OK)
            {
            	// Get hold of the response entity
                HttpEntity entity = response.getEntity();
                if (entity != null) 
        		{
                    BufferedReader reader = new BufferedReader(
                    		new InputStreamReader(entity.getContent(),charset));
                    try 
                    {
                        char[] read = new char[Usual.mByteBaseSize];
                        int count = reader.read(read, 0, Usual.mByteBaseSize);
                        while (count > 0)
                        {
                            String str = new String(read, 0, count);
                            sb.append(str);
                            count = reader.read(read, 0, Usual.mByteBaseSize);
                        }
//                    	String lineStr;
//            	        while ((lineStr = reader.readLine()) != null)
//            	        {
//            	        	sb.append(lineStr);
//            	        }
                    } 
                    catch (IOException ex) 
                    {
                        throw ex;
                    } 
                    catch (RuntimeException ex) 
                    {
                    	//异常终止
                    	obj.abort();
                        throw ex;
                    } 
                    finally 
                    {
                        reader.close();
                        reader=null;
                    }
                }
            }
            else
            {
            	throw new Exception("HttpStatus异常:"+sc);
            }
		} 
        catch (Exception exp) 
		{
        	exp.printStackTrace();
		}
        finally
        {
        	if(httpclient!=null)
        	{
        		//关闭连接
        		httpclient.getConnectionManager().shutdown();
        		httpclient=null;
        	}
        }
		return sb.toString();
    }
	/**
	 * 获取Http Put数据
	 * @param URI
	 * @param pars
	 * @return
	 * @throws Exception
	 */
	public static String f_getStringByPut(String URI, String pars) 
	throws Exception
    {
		return f_getStringByPut(URI,pars,mCharset_utf8,"text/plain");
    }
	/**
	 * 获取Http Put数据
	 * @param URI
	 * @param pars
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String f_getStringByPut(String URI, String pars,Charset charset) 
	throws Exception
    {
		//return f_getStringByPut(URI,pars,charset,"application/x-www-form-urlencoded");
		return f_getStringByPut(URI,pars,charset,"text/plain");
    }
	/**
	 * 获取Http Put数据
	 * @param URI
	 * @param pars
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public static String f_getStringByPut(String URI, String pars,String contentType) 
	throws Exception
    {
		return f_getStringByPut(URI,pars,mCharset_utf8,contentType);
    }
	
	
	/**
	 * String to Byte[],并进行编码
	 * @param str String数据
	 * @param charset 字符编码
	 * @return byte[]数组
	 */
	public static byte[] f_toBytes(String str, Charset charset)
	{
		byte[] bts=null;
		try 
		{
			bts=str.getBytes(charset);
		} 
		catch (Exception e) 
		{
			bts=null;
		}
		return bts;
	}
	/**
	 * String to Byte[],并进行编码,utf-8格式
	 * @param str String数据
	 * @return byte[]数组
	 */
	public static byte[] f_toBytes(String str)
	{
		byte[] bts=null;
		try 
		{
			bts=str.getBytes(mCharset_utf8);
		} 
		catch (Exception e) 
		{
			bts=null;
		}
		return bts;
	}
	/**
	 * byte[]转化为String数据
	 * @param bts byte[]数据
	 * @param charset 编码格式
	 * @return String数据
	 */
	public static String f_fromBytes(byte[] bts, Charset charset)
	{
		String str=Usual.mEmpty;
		try 
		{
			str=new String(bts,charset);
		} 
		catch (Exception e) 
		{
			str=Usual.mEmpty;
		}
		return str;
	}
	/**
	 * byte[]转化为String数据,默认utf-8字符格式
	 * @param bts byte[]数据
	 * @return String数据
	 */
	public static String f_fromBytes(byte[] bts)
	{
		String str=new String(bts);
		try 
		{
			str=new String(bts,mCharset_utf8);
		} 
		catch (Exception e) 
		{
			str=Usual.mEmpty;
		}
		return str;
	}
	/**
	 * byte[]数据转化为Base64String数据格式String
	 * @param bts byte[]数据
	 * @return Base64String数据格式String
	 */
	public static String f_toBase64String(byte[] bts)
	{
		String str=Usual.mEmpty;
		BASE64Encoder encoder=new BASE64Encoder();
		try 
		{
			str=encoder.encode(bts);
		} 
		catch (Exception e) 
		{
			str=Usual.mEmpty;
		}
		finally
		{
			encoder=null;
		}
		return str;
	}
	/**
	 * Base64String数据格式String转化为byte[]数据
	 * @param base64str Base64String数据格式String
	 * @return byte[]数据
	 */
	public static byte[] f_fromBase64String(String base64str)
	{
		byte[] bts=null;
		BASE64Decoder decoder=new BASE64Decoder();
		try 
		{
			bts=decoder.decodeBuffer(base64str);
		} 
		catch (Exception e) 
		{
			bts=null;
		}
		finally
		{
			decoder=null;
		}
		return bts;
	}
	
	
	/**
	 * 判断字符串为null或者为"",返回true;否则返回false
	 * @param instr
	 * @return
	 */
	public static Boolean f_isNullOrEmpty(String instr)
    {
		if(instr==null||instr.length()==0)
		{
			return true;
		}
		return false;

    }
	/**
	 * 判断字符串为null或者为""或者由空白组成,返回true;否则返回false
	 * @param instr
	 * @return
	 */
	public static Boolean f_isNullOrWhiteSpace(String instr)
    {
		if(instr==null||instr.trim().length()==0)
		{
			return true;
		}
		return false;

    }
	/**
	 * 判断byte[]数组是否为null或者长度为0
	 * @param bts
	 * @return
	 */
	public static Boolean f_isNullOrZeroBytes(byte[] bts)
    {
		if(bts==null||bts.length==0)
		{
			return true;
		}
		return false;
    }
	/**
	 * 判断String[]数组是否为null或者长度为0
	 * @param bts
	 * @return
	 */
	public static Boolean f_isNullOrZeroStrings(byte[] bts)
    {
		if(bts==null||bts.length==0)
		{
			return true;
		}
		return false;
    }
	/**
	 * 去除字符串空白
	 * @param instring
	 * @return
	 */
    public static String f_stringTrim(String instring)
    {
        String outstring = Usual.mEmpty;
        try
        {
            outstring = instring.trim();
        }
        catch(Exception e)
        {
            return Usual.mEmpty;
        }
        return outstring;
    }
    
    
    /**
	 * 对象转化为String
	 * @param obj
	 * @return
	 */
	public static String f_getString(Object obj)
    {
		try 
		{
			return obj == null ? Usual.mEmpty : obj.toString();
		} 
		catch (Exception exp) 
		{
			exp.printStackTrace();
			return Usual.mEmpty;
		}
    }
	/**
	 * 对象转化为Integer
	 * @param obj
	 * @return
	 */
	public static int f_getInteger(Object obj)
    {
		try 
		{
			return obj == null ? 0 : Integer.valueOf(obj.toString());
		}
		catch (NumberFormatException exp) 
		{
			exp.printStackTrace();
			return 0;
		}
    }
	/**
	 * 对象转化为Long
	 * @param obj
	 * @return
	 */
	public static long f_getDecimal(Object obj)
    {
		try 
		{
			return obj == null ? 0 : Long.valueOf(obj.toString());
		} 
		catch (NumberFormatException exp) 
		{
			exp.printStackTrace();
			return 0;
		}
    }
	/**
	 * 对象转化为Float
	 * @param obj
	 * @return
	 */
	public static float f_getFloat(Object obj)
    {
		try 
		{
			return obj == null ? 0 : Float.valueOf(obj.toString());
		} 
		catch (NumberFormatException exp) 
		{
			exp.printStackTrace();
			return 0;
		}
    }
	/**
	 * 对象转化为Double
	 * @param obj
	 * @return
	 */
	public static double f_getDouble(Object obj)
    {
		try 
		{
			return obj == null ? 0 : Double.valueOf(obj.toString());
		} 
		catch (NumberFormatException exp) 
		{
			exp.printStackTrace();
			return 0;
		}
    }
    
    
    /**
     * 格式化SQL字符串
     * @param instring
     * @return
     */
    public static String f_formatStringSQL(String instring)
    {
        String obj=instring.replace('\n', mBlankSpaceChar)
        	.replace('\r', mBlankSpaceChar)
//        	.replace('\"', mBlankSpaceChar)
        	.replace('\t', mBlankSpaceChar)
        	.trim();
        return obj;
    }
    /**
     * 格式化字符串为JSON可以接受的格式
     * 替换特殊符号 \n \r \t ""等
     * @param instring
     * @return
     */
    public static String f_formatStringJson(String instring)
    {
    	String obj=instring.replace("\n", "\\n")
    	.replace("\r", "\\r")
    	.replace("\t", "\\t")
    	.replace("\"", "\\\"")
    	.trim();
    	return obj;
    }
    /**
     * 消息提示格式处理
     * @param instr
     * @return
     */
    public static String f_formatMessage(String msg)
    {
        String obj=msg.replace("\"", "\'");
        return obj;
    }
}