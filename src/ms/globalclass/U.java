package ms.globalclass;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class U {
	public static boolean debugFlag = true;
	public static String nLine = System.getProperty( "line.separator" );
	
	public static String getDisplayDateStr(String dateStr){
		//dateStr = "Thu Apr 30 01:33:41 +0000 2009";
		Date dd = new Date(dateStr);
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒"); 
		String str = "";
		try{
			str = myFmt.format(dd);			
		}catch(Exception e){
			e.printStackTrace();
		}
		dout(str);
		return str;
	}
	
	public static String getDateStr(String dateStr){
		Date date = str2Date(dateStr);
		return getHowLongStr(date);		
	}
	
	/**输入"Thu Apr 30 01:33:41 +0000 2009" 返回Date*/
	public static Date str2Date(String dateStr){
		return new Date(dateStr);
	}
	
	/**传入一个Date，判断是多久前，返回示例"4小时前"*/
	public static String getHowLongStr(Date date){
		String rs = "";
		Long i = date.getTime();
		Date now = new Date();
		Long j = now.getTime();
		dout(j - i);
		long day = 1000 * 60 * 60 * 24;
		long hour = 1000 * 60 * 60;
		long min = 1000 * 60;
		long sec = 1000;
		if (((j-i)/day)>0)
			rs = ((j-i)/day)+"天前";
		else if (((j-i)/hour)>0)
			rs = ((j-i)/hour)+"小时前";
		else if (((j-i)/min)>0)
			rs = ((j-i)/min)+"分钟前";
		else if (((j-i)/sec)>0)
			rs = ((j-i)/sec)+"秒前";
		return rs;
	}
	
	/**根据一个网络上的图片url，返回一个Bitmap*/
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap getImageBitmap(String value)
	{
		URL imageUrl = null;
		Bitmap bitmap = null;
		Drawable drawable = null;
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
//		    opt.inSampleSize = 2;
		    
//			bitmap = BitmapFactory.decodeStream(is);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
//			bitmap = Bitmap.createScaledBitmap(bitmap,100,80,true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
		
	public static void dout(Object obj){
		if(debugFlag)
		Log.d("[dout]","obj>>>>>>>>>>>>>"+obj.getClass().getName()+">>"+obj.toString());
	}
	
	public  static void dout(String str){
		if(debugFlag)
		Log.d("[dout]","str>>>>>>>>>>>>>"+str);
	}
	
	public  static void dout(String[] str){
		if(debugFlag){
		for(int i=0; i<str.length; i++)
			Log.d("[dout]","str["+i+"]>>>>>>>>>>>>>"+str[i]);
		}
	}		
	
	public static String str2Abc(String str){
		String rs = "";
		if (str.equals("1"))
			rs = "A";
		if (str.equals("2"))
			rs = "B";
		if (str.equals("3"))
			rs = "C";
		if (str.equals("4"))
			rs = "D";
		return rs;
	}
	
	/**把1234转换成ABCD*/
	public static String int2Abc(int num){
		String rs = "";
		if (num==1)
			rs = "A";
		if (num==2)
			rs = "B";
		if (num==3)
			rs = "C";
		if (num==4)
			rs = "D";
		return rs;
	}
	
	/**
	 * 去除字符串中的空格,回车,换行符,制表符
	 * */
	public static String rmStrBlank(String str){
		str = str.trim();
		return str.replaceAll("\\s*|\t|\r|\n","");
	}
	
	public static void saveSharedPerferences(SharedPreferences share,String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器

		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		
		editor.commit();// 提交刷新数据
	}
	
	public static Map<String,Object> getNewStoreInfo(JSONObject dobjs){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String jsonstr = dobjs.toString();
			JSONObject dobj = new JSONObject(jsonstr);
			
			Integer points = 0; // 当前积分
			if(dobj.has("points"))
				points = (Integer) dobj.get("points"); 
			
			String nameOnCard = ""; // 卡上的名字
			if(dobj.has("nameOnCard"))
				nameOnCard = (String) dobj.get("nameOnCard");
			
			String nameid = ""; // 
			if(dobj.has("nameid"))
				nameid = (String) dobj.get("nameid"); 
			
			String pfids = ""; // 
			if(dobj.has("pfids"))
				pfids = (String) dobj.get("pfids"); 
			
			String cardNo = ""; // 会员卡编号
			if(dobj.has("cardNo"))
				cardNo = (String) dobj.get("cardNo"); 
			
			String joinedDate = ""; // 加入日期
			if(dobj.has("joinedDate"))
			{
				joinedDate = (String) dobj.get("joinedDate"); 
				String str[] = joinedDate.split(" ");
				if(str.length>1)
					joinedDate = str[0];
			}
			
			String mdmType = ""; // 会员类型
			if(dobj.has("mdmType"))
				mdmType = (String) dobj.get("mdmType"); 
			
			String mdmLevel = ""; // 会员等级
			if(dobj.has("mdmLevel"))
				mdmLevel = (String) dobj.get("mdmLevel"); 
			
			String mdmstatus = ""; // 会员状态
			if(dobj.has("mdmstatus"))
				mdmstatus = (String) dobj.get("mdmstatus"); 
			
			String expDate = ""; // 失效日期
			if(dobj.has("expDate"))
				expDate = (String) dobj.get("expDate"); 
			
			String chainCode = ""; // 条形码号或硬卡号
			if(dobj.has("chainCode"))
				chainCode = (String) dobj.get("chainCode"); 
			
			String storeid = ""; // 门店id
			if(dobj.has("storeid"))
				storeid = (String) dobj.get("storeid"); 
			
			String storeName = ""; // 门店名字
			if(dobj.has("storeName"))
				storeName = (String) dobj.get("storeName"); 
			
			String img = ""; // 门店会员卡图片
			if(dobj.has("img"))
			{
				img = (String) dobj.get("img");
				img = img.replaceAll("mill.ms.cn", "223.4.115.110");
			}
			
			String isASttention = ""; 
			if(dobj.has("isASttention"))
				isASttention = (String) dobj.get("isASttention"); 
			
			String pkid = ""; 
			if(dobj.has("pkid"))
				pkid = (String) dobj.get("pkid"); 
			
			String couponNumber = ""; 
			if(dobj.has("couponNumber"))
				couponNumber = (String) dobj.get("couponNumber"); 
			
			String storePhone = ""; 
			if(dobj.has("storePhone"))
				storePhone = (String) dobj.get("storePhone"); 
			
			String addressInfomation = ""; 
			if(dobj.has("addressInfomation"))
				addressInfomation = (String) dobj.get("addressInfomation"); 
			
			String storeDesc = ""; 
			if(dobj.has("storeDesc"))
				storeDesc = (String) dobj.get("storeDesc"); 
			
			String typeName = "";  //酒店类型
			if(dobj.has("typeName"))
				typeName = (String) dobj.get("typeName"); 
			
			String storeType = "";  //酒店类型
			if(dobj.has("storeType"))
				storeType = (String) dobj.get("storeType"); 
			
			String typesMapping = "";  //酒店类型与客户端得映射
			if(dobj.has("typesMapping"))
				typesMapping = (String) dobj.get("typesMapping"); 
			
			String businessId = ""; 
			if(dobj.has("businessId"))
				businessId = (String) dobj.get("businessId"); 
			
			String woof = ""; 
			if(dobj.has("woof"))
				woof = (String) dobj.get("woof"); 
			
			String longItude = ""; 
			if(dobj.has("longItude"))
				longItude = (String) dobj.get("longItude"); 
			
			String userimg = ""; 
			if(dobj.has("userimg"))
				userimg = (String) dobj.get("userimg"); 
			
			String isLu = ""; 
			if(dobj.has("isLu"))
				isLu = (String) dobj.get("isLu"); 
			
			String province = ""; 
				if(dobj.has("province"))
					province = (String) dobj.get("province");
				
			String roomIntroduction = ""; 
				if(dobj.has("roomIntroduction"))
					roomIntroduction = (String) dobj.get("roomIntroduction");
				
			String periphery = ""; 
				if(dobj.has("periphery"))
					periphery = (String) dobj.get("periphery");
				
			String trafficWay = ""; 
				if(dobj.has("trafficWay"))
					trafficWay = (String) dobj.get("trafficWay");
					
			String startingPrice = ""; 
				if(dobj.has("startingPrice"))
					startingPrice = (String) dobj.get("startingPrice");
				
			String score = ""; 
				if(dobj.has("score"))
					score = (String) dobj.get("score");
					
			String comments = ""; 
				if(dobj.has("comments"))
					comments = (String) dobj.get("comments");
				
			String username = ""; 
				if(dobj.has("username"))
					username = (String) dobj.get("username");
				
			String password = ""; 
				if(dobj.has("password"))
					password = (String) dobj.get("password");
				
			String lastmessage = ""; 
				if(dobj.has("lastmessage"))
					lastmessage = (String) dobj.get("lastmessage");
				
			String lastmessagetime = ""; 
				if(dobj.has("lastmessagetime"))
				{
					lastmessagetime = (String) dobj.get("lastmessagetime");
					if(lastmessagetime != null && !lastmessagetime.equals(""))
					{
						String str[] = lastmessagetime.split(" ");
						SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
						String datastr1 = formatter.format(new Date());
						if(str.length>1)
						{
							String datastr = str[0];
							if(datastr.equals(datastr1))
							{
								lastmessagetime = str[1].substring(0,5);
							}
							else
							{
								lastmessagetime = str[0].substring(5,str[0].length());
							}
						}
					}
				}
				
			map.put("points", points);
			map.put("nameOnCard", nameOnCard);
			map.put("nameid", nameid);
			map.put("pfids", pfids);
			map.put("cardNo", cardNo);
			map.put("joinedDate", joinedDate);
			map.put("mdmType", mdmType);
			map.put("mdmLevel", mdmLevel);
			map.put("mdmstatus", mdmstatus);
			map.put("expDate", expDate);
			map.put("chainCode", chainCode);
			map.put("storeid", storeid);
			map.put("storeName", storeName);
			String sortName = storeName.replaceAll("\\(", "@").replaceAll("\\)", "#").replaceAll("\\（", "@").replaceAll("\\）", "#");
			if(isASttention.equals("0"))
				map.put("sortName", "0"+sortName);
			else
				map.put("sortName", sortName);
			map.put("username", username);
			map.put("password", password);
//			boolean loadimgTag = share.getBoolean("webimage", true);
//			if(loadimgTag)
//			{
//				map.put("img", getImageDrawable(img));
//				map.put("img2", getImageBitmap(img));
//			}
//			else
//			{
//				map.put("img", null);
//				map.put("img2", BitmapFactory.decodeResource(this.getResources(), R.drawable.local_card_img));
//			}
			map.put("imgurl", img);
//			userimgs[i] = img;
			map.put("pkid", pkid);
			map.put("storePhone", storePhone);
			map.put("addressInfomation", addressInfomation);
			map.put("storeDesc", storeDesc);
			map.put("isASttention", isASttention);
			if(isASttention.equals("0"))
				map.put("xinxin", R.drawable.ic_star_small);
			else
				map.put("xinxin", null);
			map.put("couponNumber", couponNumber);
			map.put("typeName", typeName);
			map.put("typesMapping", typesMapping);
			map.put("businessId", businessId);
			map.put("woof", woof);
			map.put("longItude", longItude);
			map.put("userimg", userimg);
			map.put("isLu", isLu);
			map.put("storeType", storeType);
			map.put("province", province);
			map.put("roomIntroduction", roomIntroduction);
			map.put("periphery", periphery);
			map.put("trafficWay", trafficWay);
			map.put("startingPrice", startingPrice);
			map.put("score", score);
			map.put("comments", comments);
			map.put("lastmessage", lastmessage);
			map.put("lastmessagetime", lastmessagetime);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
	public static boolean isStoreExist(String storeids,List<Map<String,Object>> dlist)
	{
		boolean b = false;
		try{
			for(int i=0;i<dlist.size();i++)
			{
				Map<String,Object> map = dlist.get(i);
				String storeid = (String)map.get("storeid");
				if(storeid.equals(storeids))
				{
					b = true;
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return b;
	}
}
