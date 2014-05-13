package ms.globalclass.httppost;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import ms.fortuneJ.HttpFastUtil;
import ms.fortuneJ.Usual;
import ms.globalclass.FileAccessI;
import ms.globalclass.map.MyApp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class HttpFileUpTool {
	/**
	 * ͨ��ƴ�ӵķ�ʽ�����������ݣ�ʵ�ֲ��������Լ��ļ�����
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws IOException {

		StringBuilder sb2 = new StringBuilder();
		
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(60 * 1000); // ������ʱ��
		conn.setDoInput(true);// ��������
		conn.setDoOutput(true);// �������
		conn.setUseCaches(false); // ������ʹ�û���
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// ������ƴ�ı����͵Ĳ���
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
//			sb.append(PREFIX);
//			sb.append(BOUNDARY);
//			sb.append(LINEND);
//			sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"" + LINEND);
//			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
//			sb.append(LINEND);
//			sb.append(entry.getValue());
//			sb.append(LINEND);
			
			sb.append("--");  
			sb.append("---------------------------7da2137580612");  
			sb.append("/r/n");  
			sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"/r/n/r/n");  
			sb.append(entry.getValue());  
			sb.append("/r/n");  
		}

		DataOutputStream outStream = new DataOutputStream(conn
				.getOutputStream());
		System.out.println("sb===="+sb.toString());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// �����ļ�����
		if (files != null){
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1
						.append("Content-Disposition: form-data; name=\"file\"; filename=\""
								+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// ���������־
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// �õ���Ӧ��
		int res = conn.getResponseCode();
		if (res == 200) {
			in = conn.getInputStream();
			int ch;
			String data = "";  
			
			BufferedReader br = new BufferedReader(  
                    new InputStreamReader(in,"UTF-8"));  
			
//			while ((ch = in.read()) != -1) {
//				sb2.append((char) ch);
//			}
			while ((data = br.readLine()) != null) {  
                sb2.append(data);  
            }  
		}
		outStream.close();
		conn.disconnect();
		}
		return sb2.toString();
//		return in.toString();
	}
	
	public static Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
		System.out.println("menulist_imageurl==="+value);
		if(value == null)
			return null;
		try {
			imageUrl = new URL(value);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			BitmapFactory.Options opt = new BitmapFactory.Options();  
		    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;  
		    
			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	// �ļ��ָ��ϴ�
	public static void cutFileUpload(String fileType, String filePath, String url) {
		try {
			FileAccessI fileAccessI = new FileAccessI(filePath, 0);
			Long nStartPos = 0l;
			Long length = fileAccessI.getFileLength();
			int mBufferSize = 1024 * 100; // ÿ�δ���1024 * 100�ֽ�
			byte[] buffer = new byte[mBufferSize];
			FileAccessI.Detail detail;
			long nRead = 0l;
			UUID uuid=UUID.randomUUID();
			String str = uuid.toString();
			String vedioFileName = str; // ����һ���ļ���
			long nStart = nStartPos;
			int i = 0;
			while (nStart < length) {
				detail = fileAccessI.getContent(nStart);
				nRead = detail.length;
				buffer = detail.b;
				JSONObject mInDataJson = new JSONObject();
				mInDataJson.put("a", "282");
				mInDataJson.put("FileName", vedioFileName);
				System.out.println("nStart=="+nStart);
				mInDataJson.put("start", nStart); // ����˻�ȡ��ʼ���½���д�ļ�
				mInDataJson.put("filetype", fileType);
				nStart += nRead;
				nStartPos = nStart;
//				String url = UsualA.f_getXmlSOAUrl(UsualA.mServiceFastByteUrl,
//						"n.uploadvedio", mInDataJson.toString(), "282");
				url = url + "&FileName="+vedioFileName+"&start="+nStart+"&filetype="+fileType;
				System.out.println("url:"+url);
				HttpFastUtil.f_httpPostByte(url, buffer, false);
			}
		} catch (Exception e) {
		}
	}
}
