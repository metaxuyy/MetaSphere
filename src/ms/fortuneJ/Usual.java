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
 * ͨ�þ�̬������,���ɼ̳�
 * ���ڳ��þ�̬��������
 * @author nick
 * 
 */
@SuppressWarnings({ "unchecked", "unused" })
public class Usual
{

	/**
	 * ���涯ִ̬�к����ľ�̬Class����
	 */
	private final static HashMap<String, Class> cht_st = new HashMap<String, Class>();
	/**
	 * ���涯ִ̬�к�����New Class����
	 */
	private final static HashMap<String, Object> cht_obj = new HashMap<String, Object>();
	/**
	 * ��̬���ֶ�,""
	 */
	public final static String mEmpty="";
	/**
	 * ��̬���ַ�,' '
	 */
	public final static char mBlankSpaceChar=' ';
	/**
	 * ��̬���ֶ�," "
	 */
	public final static String mBlankSpace=" ";
	/**
	 * ˫�����ַ�
	 */
	public final static String mDoubleQuote="\"";
	/**
	 * Http POST/GET���ӳ�ʱʱ��,����
	 */
	public final static int mUrlConTime=1000*60*3;
	/**
	 * Http POST/GET���ӳ�ʱʱ��,����
	 */
	public final static int mUrlReadTime=1000*60*30;
	/**
	 * UTF-8����
	 */
	public final static String mUTF8Name="UTF-8";
	/**
	 * GB2312����
	 */
	public final static String mGB2312Name="GB2312";
	/**
	 * ����UTF-8��Ϊ�����ַ���ʽ
	 */
	public final static Charset mCharset_utf8=Charset.forName(mUTF8Name);
	/**
	 * ����C#,Java,Window Mobile,Java Mobile��֧�ֵ��ַ����ܸ�ʽ
	 * ����gb2312�ַ���ʽ,Ҳ�����趨Ϊunicode
	 */
	public final static Charset mCharset_gb2312=Charset.forName(mGB2312Name);
	/**
	 * �������ݴ�С,4K
	 */
	public final static int mByteBaseSize=1024*4;
	/**
	 * GZip����
	 */
	public final static String mGZipName="GZIP";
	
	
	/**
	  * nitobi Grid ҳ���С�ֶ�
	  */
	 public static final String mgPageSize="PageSize";
	 /**
	  * nitobi Grid �����ֶ�
	  */
	 public static final String mgSortColumn="SortColumn";
	 /**
	  * nitobi Grid ����˳��
	  */
	 public static final String mgSortDirection="SortDirection";
	 /**
	  * nitobi Grid ��ǰҳ�濪ʼ��Ŀ
	  */
	 public static final String mgStart="start";
	 /**
	  * nitobi Grid ��ǰҳ�濪ʼ��Ŀ,��ȷ��
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
	  * ��ʽ��Date,Ϊyyyy-MM-dd HH:mm:ss��ʽ
	  */
	 public final static DateFormat mfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 /**
	  * ��ʽ��Date,Ϊyyyy-MM-dd HH:mm:ss.SSS��ʽ
	  */
	 public final static DateFormat mfAllMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	 /**
	  * ��ʽ��Date,Ϊyyyy-MM-dd��ʽ
	  */
	 public final static DateFormat mfYMD=new SimpleDateFormat("yyyy-MM-dd");
	 /**
	  * ��ʽ��Date,ΪyyyyMMdd��ʽ
	  */
	 public final static DateFormat mfYMDSimple=new SimpleDateFormat("yyyyMMdd");
	  /**
	  * ��ʽ��Date,Ϊyyyy-MM-dd ��ʽ
	  */
	 public final static DateFormat mfYMD_HM=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 /**
	  * ��ʽ��Date,ΪOracle����yyyy-MM-dd HH:mm:ss SSS��ʽ
	  */
	 public final static DateFormat mfYMD_Oracle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	 /**
	  * ��ʽ��Date,ΪSQLServer����yyyy-MM-dd HH:mm:ss.SSS��ʽ
	  */
	 public final static DateFormat mfYMD_SQLServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	 
	/**
	 * ���캯��
	 */
	public Usual() 
	{
	}

	
	/**
	 * ��ִ̬�о�̬����,���غ���ִ�н��
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return ִ�к����󷵻ض���
	 * @throws Exception
	 */
	public static Object f_evalMethodStatic(String clsName,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Class curClass = null;
		try 
		{
			//����Class����,�´β��ö�̬����	
			if (cht_st.containsKey(clsName))
			{
				curClass = cht_st.get(clsName);
			} 
			else 
			{
				//����Class��̬�������ö���
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
	 * ��ִ̬�о�̬����,���غ���ִ�н��
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return ִ�к����󷵻ض���
	 */
	public static Object f_evalMethodStatic(Class cls,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Method mMethod=null;
		try {
			// ���ɶ�Ӧ����
			mMethod = cls.getMethod(methodName, types);
			if (mMethod != null) 
			{
				// ִ�к���
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
	 * ��ִ̬�о�̬����,���غ���ִ�н��
	 * 
	 * @param clsName
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return ִ�к����󷵻ض���
	 * @throws Exception
	 */
	public static Object f_evalMethod(String clsName,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Object obj=null;
		try 
		{
			Class curClass = null;
			//����Class����,�´β��ö�̬����	
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
	 * ��ִ̬�о�̬����,���غ���ִ�н��
	 * @param cls
	 * @param methodName
	 * @param types
	 * @param objs
	 * @return ִ�к����󷵻ض���
	 */
	public static Object f_evalMethod(Object obj,String methodName, Class[] types, Object[] objs)
	{
		Object rObj = null;
		Method nMethod=null;
		Class clsClass=null;
		try {
			clsClass=obj.getClass();
			// ���ɶ�Ӧ����
			nMethod = clsClass.getMethod(methodName, types);
			if (nMethod != null) 
			{
				// ִ�к���
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
	 * ��ȡPost�󷵻�����,Post����Stream��ʽ
	 * @param http·��
	 * @param ���ݵ�Post����
	 * @param charset ��������
	 * @return ��������
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars, Charset charset) 
	throws Exception
    {
		return f_getStringByPost(URI,pars,charset,"text/plain");
    }
	/**
	 * ��ȡPost�󷵻�����,Post����Stream��ʽ
	 * @param http·��
	 * @param ���ݵ�Post����
	 * @param charset ��������
	 * @param contentType
	 * @return ��������
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
			// �趨���͵����������ǿ����л���java���� 
			// (����������,�ڴ������л�����ʱ,��WEB����Ĭ�ϵĲ�����������ʱ������java.io.EOFException) 
			//httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			//httpUrlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			//ͳһ����Stream
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
			// ����HttpURLConnection���Ӷ����getInputStream()����, 
			// ���ڴ滺�����з�װ�õ�������HTTP������ķ��͵�����ˡ� 
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
	        //�ر����ݶ�ȡ
	        if(reader!=null)
	        {
	        	reader.close();
	        	reader=null;
	        }
	        //�ж��Ƿ���������
			int sc=httpUrlConnection.getResponseCode();
			if(sc!=HttpStatus.SC_OK)
			{
				throw new IOException("HttpStatus�쳣:"+sc+","+sb.toString());
			}
		} 
		catch (Exception exp) 
		{
			exp.printStackTrace();
		}
		finally
		{
			 //�Ͽ�����
	        if(httpUrlConnection!=null)
	        {
	        	httpUrlConnection.disconnect();
	        	httpUrlConnection=null;
	        }
		}
		return sb.toString();
    }
	/**
	 * ��ȡPost�󷵻�����,Post����Stream��ʽ
	 * @param http·��
	 * @param ���ݵ�Post����
	 * @return ��������
	 * @throws Exception 
	 */
	public static String f_getStringByPost(String URI, String pars) 
	throws Exception
    {
		return f_getStringByPost(URI,pars,mCharset_utf8);
    }
	/**
	 * ��ȡPost�󷵻�����,Post����Stream��ʽ
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
	 * ��ȡGET�󷵻�����
	 * @param http·��
	 * @param charset
	 * @return ��������
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
	        //�ر����ݶ�ȡ
	        if(reader!=null)
	        {
	        	reader.close();
	        	reader=null;
	        }
	        //�ж��Ƿ���������
			int sc=httpUrlConnection.getResponseCode();
			if(sc!=HttpStatus.SC_OK)
			{
				throw new IOException("HttpStatus�쳣:"+sc+","+sb.toString());
			}
		} 
		catch (Exception exp) 
		{
			exp.printStackTrace();
		}
		finally
		{
			//�Ͽ�����
	        if(httpUrlConnection!=null)
	        {
	        	httpUrlConnection.disconnect();
	        	httpUrlConnection=null;
	        }
		}
		return sb.toString();
    }
	/**
	 * ��ȡGET�󷵻�����,UTF-8�����ʽ
	 * @param http·��
	 * @return ��������
	 */
	public static String f_getStringByGet(String URI)
	throws Exception
    {
		return f_getStringByGet(URI,mCharset_utf8);
    }
	/**
	 * ��ȡHttp Put����
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
    		//���Դ���
    		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() 
    		{
    			public boolean retryRequest(IOException exception,int executionCount,HttpContext context) 
    			{
    				//����3�κ�ȡ��
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
    		//������״̬
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
                    	//�쳣��ֹ
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
            	throw new Exception("HttpStatus�쳣:"+sc);
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
        		//�ر�����
        		httpclient.getConnectionManager().shutdown();
        		httpclient=null;
        	}
        }
		return sb.toString();
    }
	/**
	 * ��ȡHttp Put����
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
	 * ��ȡHttp Put����
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
	 * ��ȡHttp Put����
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
	 * String to Byte[],�����б���
	 * @param str String����
	 * @param charset �ַ�����
	 * @return byte[]����
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
	 * String to Byte[],�����б���,utf-8��ʽ
	 * @param str String����
	 * @return byte[]����
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
	 * byte[]ת��ΪString����
	 * @param bts byte[]����
	 * @param charset �����ʽ
	 * @return String����
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
	 * byte[]ת��ΪString����,Ĭ��utf-8�ַ���ʽ
	 * @param bts byte[]����
	 * @return String����
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
	 * byte[]����ת��ΪBase64String���ݸ�ʽString
	 * @param bts byte[]����
	 * @return Base64String���ݸ�ʽString
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
	 * Base64String���ݸ�ʽStringת��Ϊbyte[]����
	 * @param base64str Base64String���ݸ�ʽString
	 * @return byte[]����
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
	 * �ж��ַ���Ϊnull����Ϊ"",����true;���򷵻�false
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
	 * �ж��ַ���Ϊnull����Ϊ""�����ɿհ����,����true;���򷵻�false
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
	 * �ж�byte[]�����Ƿ�Ϊnull���߳���Ϊ0
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
	 * �ж�String[]�����Ƿ�Ϊnull���߳���Ϊ0
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
	 * ȥ���ַ����հ�
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
	 * ����ת��ΪString
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
	 * ����ת��ΪInteger
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
	 * ����ת��ΪLong
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
	 * ����ת��ΪFloat
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
	 * ����ת��ΪDouble
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
     * ��ʽ��SQL�ַ���
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
     * ��ʽ���ַ���ΪJSON���Խ��ܵĸ�ʽ
     * �滻������� \n \r \t ""��
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
     * ��Ϣ��ʾ��ʽ����
     * @param instr
     * @return
     */
    public static String f_formatMessage(String msg)
    {
        String obj=msg.replace("\"", "\'");
        return obj;
    }
}