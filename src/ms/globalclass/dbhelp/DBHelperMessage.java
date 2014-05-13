package ms.globalclass.dbhelp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ms.activitys.R;
import ms.activitys.hotel.HttpDownloader;
import ms.globalclass.FileUtils;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class DBHelperMessage extends SQLiteOpenHelper{

	private static final String DataBaseName = "mydb";
	private static SQLiteDatabase db = null;  
	private static Context context;
    private static MyApp myapp;
    private static HttpDownloader downloader;
    public static FileUtils fileUtil = new FileUtils();
    
    public static final String TAG = "DatabaseHelper";
    private static final int DB_VERSION = 1;
    
    public DBHelperMessage(Context context,MyApp myp) {
    	super(context, DataBaseName, null, DB_VERSION);
    	this.context = context;
    	this.myapp = myp;
    	this.downloader = new HttpDownloader();
    	
    	if(db == null)
    	{
    		openDB();
    		if(!this.tabbleIsExist("messagelist"))
    			createMessageTable();//创建信息表
//    		createMessageCardTable();//创建信息卡片主表
    		if(!tabbleIsExist("messageCardList"))
    			createMessageCardListTable();//创建信息卡片子表
    		if(!this.tabbleIsExist("messagenewlist"))
    			createNewMessageList();//最新消息列表
    		if(!this.tabbleIsExist("storeinfo"))
    			createStoreInfoAll();//所有关注用户信息
    		if(!this.tabbleIsExist("friendlist"))
    			createFriendAll();//我的好友列表
    		if(!this.tabbleIsExist("moments"))
    			createMomentsAll();
    		if(!this.tabbleIsExist("momentsfiles"))
    			createMomentsFileAll();
    		if(!this.checkColumnExist1("messagenewlist", "isGroup"))
    			db.execSQL("ALTER TABLE messagenewlist ADD COLUMN isGroup VARCHAR");
    		if(!this.checkColumnExist1("storeinfo", "profileid"))
    			db.execSQL("ALTER TABLE storeinfo ADD COLUMN profileid VARCHAR");
    		closeDB();
    	}
    }
    
    public synchronized void openDB()
    {
    	try{
	    	//打开或创建test.db数据库  
    		if(db == null)
    				db = context.openOrCreateDatabase(DataBaseName, Context.MODE_PRIVATE, null);
    		else
    		{
    			if(db.isDbLockedByCurrentThread())
    				db.endTransaction();
    		}
//    		else if(db.isDbLockedByCurrentThread())
//    		{
//    			db = context.openOrCreateDatabase(DataBaseName, Context.MODE_PRIVATE, null);
//    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void openDB2()
    {
    	try{
	    	//打开或创建test.db数据库  
    		if(db == null)
    				db = context.openOrCreateDatabase(DataBaseName, Context.MODE_PRIVATE, null);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void closeDB()
    {
    	try{
	    	//关闭当前数据库  
    		if(db != null)
    		{
    			if(db.isDbLockedByCurrentThread() && db.isDbLockedByOtherThreads())
    				db.endTransaction();
    			db.close();
    			db = null;
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized  static void closeDB2()
    {
    	try{
	    	//关闭当前数据库  
    		if(db != null)
    		{
    			db.endTransaction();
    			db.close();
    			db = null;
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized  void createMessageTable()
    {
		db.execSQL("DROP TABLE IF EXISTS messagelist");
		// 创建person表
		db.execSQL("CREATE TABLE messagelist (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "mid VARCHAR, "
				+ "toid VARCHAR, "
				+ "mysendname VARCHAR, "
				+ "fname VARCHAR,"
				+ "tname VARCHAR,"
				+ "toname VARCHAR,"
				+ "mymessagecontent VARCHAR,"
				+ "mysendtime VARCHAR,"
				+ "fileUrl VARCHAR,"
				+ "fileUrl2 VARCHAR,"
				+ "fileType VARCHAR,"
				+ "fileType2 VARCHAR,"
				+ "fileName VARCHAR,"
				+ "fileName2 VARCHAR,"
				+ "time VARCHAR,"
				+ "timetext VARCHAR,"
				+ "messagetype VARCHAR,"
				+ "sendimg VARCHAR,"
				+ "sendprogress VARCHAR,"
				+ "storename VARCHAR,"
				+ "storeDoc VARCHAR,"
				+ "url VARCHAR,"
				+ "storeimg BLOB,"
				+ "storeimgpath VARCHAR,"
				+ "isRead VARCHAR,"
				+ "loginname VARCHAR,"
				+ "messagestart VARCHAR,"
				+ "image BLOB)");
    }
    
//    public static void createMessageCardTable()
//    {
//    	db.execSQL("DROP TABLE IF EXISTS messageCard");
//        //创建person表  
//        db.execSQL("CREATE TABLE messageCard (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//        		"storeimg BLOB, " +
//        		"storename VARCHAR, " +
//        		"storeDoc VARCHAR, " +
//        		"mid VARCHAR)");
//    }
    
    public synchronized  void createMessageCardListTable()
    {
		db.execSQL("DROP TABLE IF EXISTS messageCardList");
		// 创建person表
		db.execSQL("CREATE TABLE messageCardList (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "sysImg BLOB, "
				+ "imgpath VARCHAR, "
				+ "title VARCHAR, "
				+ "desc VARCHAR, "
				+ "pkid VARCHAR, "
				+ "loginname VARCHAR,"
				+ "price VARCHAR, " + "mid VARCHAR)");
    }
    
    public synchronized  void createNewMessageList()
    {
		db.execSQL("DROP TABLE IF EXISTS messagenewlist");
		// 创建person表
		db.execSQL("CREATE TABLE messagenewlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "storeid VARCHAR, "
				+ "storename VARCHAR, "
				+ "newmessage VARCHAR, "
				+ "sendtime VARCHAR,"
				+ "senddatetime VARCHAR,"
				+ "loginname VARCHAR,"
				+ "typesMapping VARCHAR,"
				+ "newNumber int,"
				+ "nameid VARCHAR,"
				+ "isTop VARCHAR,"
				+ "serviceid VARCHAR,"
				+ "servicename VARCHAR,"
				+ "watag VARCHAR,"
				+ "isGroup VARCHAR,"
				+ "storeimg VARCHAR)");
    }
    
    public synchronized  void createStoreInfoAll()
    {
		db.execSQL("DROP TABLE IF EXISTS storeinfo");
		// 创建person表
		db.execSQL("CREATE TABLE storeinfo (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "storeid VARCHAR, "
				+ "points INTEGER, "
				+ "nameOnCard VARCHAR, "
				+ "nameid VARCHAR,"
				+ "pfids VARCHAR,"
				+ "cardNo VARCHAR,"
				+ "joinedDate VARCHAR,"
				+ "mdmType VARCHAR,"
				+ "mdmLevel VARCHAR,"
				+ "mdmstatus VARCHAR,"
				+ "expDate VARCHAR,"
				+ "chainCode VARCHAR,"
				+ "storeName VARCHAR,"
				+ "sortName VARCHAR,"
				+ "sortPinyin VARCHAR,"
				+ "username VARCHAR,"
				+ "password VARCHAR,"
				+ "imgurl VARCHAR,"
				+ "pkid VARCHAR,"
				+ "storePhone VARCHAR,"
				+ "addressInfomation VARCHAR,"
				+ "storeDesc VARCHAR,"
				+ "isASttention VARCHAR,"
				+ "xinxin INTEGER,"
				+ "couponNumber VARCHAR,"
				+ "typeName VARCHAR,"
				+ "typesMapping VARCHAR,"
				+ "businessId VARCHAR,"
				+ "woof VARCHAR,"
				+ "longItude VARCHAR,"
				+ "isLu VARCHAR,"
				+ "storeType VARCHAR,"
				+ "province VARCHAR,"
				+ "roomIntroduction VARCHAR,"
				+ "periphery VARCHAR,"
				+ "trafficWay VARCHAR,"
				+ "startingPrice VARCHAR,"
				+ "score VARCHAR,"
				+ "comments VARCHAR,"
				+ "lastmessage VARCHAR,"
				+ "language VARCHAR,"
				+ "lastmessagetime VARCHAR,"
				+ "loginname VARCHAR,"
				+ "customizeMenu VARCHAR,"
				+ "linkurl VARCHAR,"
				+ "servicetype VARCHAR,"
				+ "storeNo VARCHAR,"
				+ "userimg BLOB)");
    }
    
    public synchronized  void createFriendAll()
    {
    	db.execSQL("DROP TABLE IF EXISTS friendlist");
		// 创建person表
		db.execSQL("CREATE TABLE friendlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "pkid VARCHAR, "
				+ "username VARCHAR, "
				+ "pfid VARCHAR, "
				+ "namePinyin VARCHAR,"
				+ "loginname VARCHAR,"
				+ "account VARCHAR,"
				+ "sex VARCHAR,"
				+ "area VARCHAR,"
				+ "signature VARCHAR,"
				+ "start VARCHAR,"
				+ "companyid VARCHAR,"
				+ "storeid VARCHAR,"
				+ "userimg VARCHAR)");
    }
    
    public synchronized  void createMomentsAll()
    {
    	db.execSQL("DROP TABLE IF EXISTS moments");
		// 创建person表
		db.execSQL("CREATE TABLE moments (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "pkid VARCHAR, "
				+ "content VARCHAR, "
				+ "publishtime VARCHAR, "
				+ "publishuser VARCHAR,"
				+ "userimg VARCHAR,"
				+ "loginname VARCHAR,"
				+ "publishtype VARCHAR,"
				+ "publishusertype VARCHAR,"
				+ "publishid VARCHAR)");
    }
    
    public synchronized  void createMomentsFileAll()
    {
    	db.execSQL("DROP TABLE IF EXISTS momentsfiles");
		// 创建person表
		db.execSQL("CREATE TABLE momentsfiles (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "pkid VARCHAR, "
				+ "filename VARCHAR, "
				+ "fileurl VARCHAR, "
				+ "filetype VARCHAR,"
				+ "loginname VARCHAR,"
				+ "filewidth VARCHAR,"
				+ "fileheight VARCHAR,"
				+ "momentsid VARCHAR)");
    }
    
    //判断数据库表是否存在
	public synchronized  boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from  sqlite_master "
					+ " where type ='table' and name ='" + tableName.trim()
					+ "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	* 方法1：检查某表列是否存在
	* @param db
	* @param tableName 表名
	* @param columnName 列名
	* @return
	*/
	private boolean checkColumnExist1(String tableName, String columnName) {
	    boolean result = false ;
	    Cursor cursor = null ;
	    try{
	        //查询一行
	        cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
	            , null );
	        result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
	    }catch (Exception e){
	         Log.e(TAG,"checkColumnExists1..." + e.getMessage()) ;
	    }finally{
	        if(null != cursor && !cursor.isClosed()){
	            cursor.close() ;
	        }
	    }

	    return result ;
	}
    
    public synchronized void saveMessageData(List<Map<String,Object>> dataList)
    {
    	try{
    		openDB2();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		for(int i=0;i<dataList.size();i++)
    		{
    			Map<String,Object> map = dataList.get(i);
    			String groupid = (String)map.get("groupid");
	    		ContentValues cv = new ContentValues();
	    		cv.put("mid", (String)map.get("mid"));
	    		if(groupid != null && !groupid.equals(""))
	    		{
	    			cv.put("mysendname", groupid);
//	    			String storeimg = getFriendImagePath((String)map.get("mysendname"));
	    			String furl = fileUtil.getImageFile2aPath((String)map.get("mysendname"), (String)map.get("mysendname"));
	    			if(!fileUtil.isFileExist3(furl))
	    			{
	    				if(!fileUtil.isFileExist2((String)map.get("mysendname")))
	    					fileUtil.createUserFile((String)map.get("mysendname"));
//		    			String url = Douban.IMG_BASE_URL + (String)map.get("mysendname")+".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+(String)map.get("mysendname");
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
						myapp.saveMyBitmap(furl, bmpsimg);
						cv.put("storeimgpath", furl);
	    			}
	    			else
	    				cv.put("storeimgpath", furl);
	    		}
	    		else
	    		{
	    			cv.put("mysendname", (String)map.get("mysendname"));
	    		}
	    		cv.put("toid", (String)map.get("toid"));  
//	            cv.put("mysendname", (String)map.get("mysendname")); 
	            cv.put("fname", (String)map.get("fname"));
	            cv.put("tname", (String)map.get("tname"));  
	            cv.put("toname", (String)map.get("toname"));  
	            cv.put("mymessagecontent", (String)map.get("mymessagecontent"));  
	            Date datatime = format.parse((String)map.get("mysendtime"));
	            cv.put("mysendtime", format.format(datatime));  
	            cv.put("fileUrl", (String)map.get("fileUrl"));  
	            cv.put("fileUrl2", (String)map.get("fileUrl2"));  
	            cv.put("fileType", (String)map.get("fileType"));  
	            cv.put("fileType2", (String)map.get("fileType2"));
	            cv.put("fileName", (String)map.get("fileName"));
	            cv.put("fileName2", (String)map.get("fileName2"));
	            cv.put("time", (String)map.get("time"));
	            cv.put("timetext", (String)map.get("timetext"));
	            cv.put("messagetype", (String)map.get("messagetype"));
	            cv.put("sendimg", (String)map.get("sendimg"));
	            cv.put("sendprogress", (String)map.get("sendprogress"));
	            cv.put("storename", (String)map.get("storename"));  
	            cv.put("storeDoc", (String)map.get("storeDoc"));
	            cv.put("url", (String)map.get("url"));
	            cv.put("loginname", myapp.getMyaccount());
	            cv.put("isRead", (String)map.get("isRead"));
	            cv.put("messagestart", (String)map.get("messagestart"));
	            
	            if(!((String)map.get("messagetype")).equals("group"))
	            {
		            String storeimg = (String)map.get("storeimg");
		            if(storeimg != null && !storeimg.equals(""))
		            {
		            	if(storeimg.indexOf("http") >= 0)
		            	{
				            Bitmap bmpsimg = returnBitMap((String)map.get("storeimg"));
				            if(bmpsimg != null)
				            {
	//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
	//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
	//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
	//							cv.put("storeimg", oss.toByteArray());
				            	int width = bmpsimg.getWidth();
								int height = bmpsimg.getHeight();
								int newwidth = 0;
								int newheight = 140;
								if(height > newheight || height < newheight)
								{
									BigDecimal b = new BigDecimal((float)height / (float)width);  
									float bili = b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();;
									newwidth = (int)(newheight/bili);
									if(newwidth >= 300)
										bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
									else
									{
										newwidth = 300;
										BigDecimal wb = new BigDecimal((float)width / (float)height);
										float wbili = wb.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
										newheight = (int)(newwidth/wbili);
										bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
									}
								}
								
								int mScreenWidth = (bmpsimg.getWidth() - 300) / 2;
								int mScreenHeight = (bmpsimg.getHeight() - 140) / 2;
								if(mScreenWidth < 0)
									mScreenWidth = 0;
								if(mScreenHeight < 0)
									mScreenHeight = 0;
								bmpsimg = Bitmap.createBitmap(bmpsimg, mScreenWidth, mScreenHeight, 300, 140);
								
								String username = (String)map.get("mysendname");
								UUID uuid = UUID.randomUUID();
								String furl = fileUtil.getImageFile2aPath(username, uuid.toString());
								myapp.saveMyBitmap(furl, bmpsimg);
								cv.put("storeimgpath", furl);
				            }
		            	}
		            	else
		            	{
		            		cv.put("storeimgpath", storeimg);
		            	}
	//	            	else
	//	            	{
	//	            		Bitmap bitmimg = getLoacalBitmap(storeimg);
	//	            		if(bitmimg != null)
	//			            {
	//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
	//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
	//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
	//							cv.put("storeimg", oss.toByteArray());
	//			            }
	//	            	}
		            }
	            }
	            
	            String fileUrl = (String)map.get("fileUrl");
	            if(fileUrl != null && !fileUrl.equals(""))
	            {
	            	if(fileUrl.indexOf("http") >= 0)
	            	{
			            Bitmap bitmimg = returnBitMap((String)map.get("fileUrl"));
			            if(bitmimg != null)
			            {
			            	String fileName = (String)map.get("fileName");
			            	fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
							String [] names = fileName.split("\\.");
			            	String username = (String)map.get("mysendname");
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("image", oss.toByteArray());
			            	bitmimg = myapp.adaptive(bitmimg, myapp.getScreenWidth(), myapp.getScreenHeight());
							Bitmap iconbitmap = myapp.adaptive(bitmimg);
							UUID uuid = UUID.randomUUID();
							String fileNames = uuid.toString() + "." + names[1];
							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
							String iconfurl = fileUtil.getImageFile1aPath(username, uuid.toString());
							myapp.saveMyBitmap(furl, bitmimg,iconfurl,iconbitmap);
							cv.put("fileName", fileNames);
							cv.put("fileUrl", furl);
			            }
	            	}
	            	else
	            	{
	            		cv.put("fileName", (String)map.get("fileName"));
						cv.put("fileUrl", (String)map.get("fileUrl"));
	            	}
//	            	else
//	            	{
//	            		Bitmap bitmimg = getLoacalBitmap(fileUrl);
//			            if(bitmimg != null)
//			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("image", oss.toByteArray());
//			            }
//	            	}
	            }
	            
	            if(map.get("fileUrl2") != null && !map.get("fileUrl2").equals(""))
	            {
		            String fileUrl2 = (String)map.get("fileUrl2");
		            String fileName = (String)map.get("fileName2");
		            String username = (String)map.get("mysendname");
		            String touserid = (String)map.get("toname");
		            if(fileUrl2.indexOf("http") >= 0)
		            {
//		            	int result = downloader.downFile(fileUrl2, "voa/", fileName);
//		            	if(result >= 0)
//		            	{
//		            		fileUrl2 = Environment.getExternalStorageDirectory() + "/voa/"+fileName;
//		            		cv.put("fileUrl2", fileUrl2);
//		            	}
		            	String filePath = null;
		            	if(username.equals(myapp.getPfprofileId()))
		            	{
		            		filePath = fileUtil.createVoice2File1a(touserid, fileUrl2, fileName);
		            	}
		            	else
		            	{
		            		filePath = fileUtil.createVoiceFile1a(username, fileUrl2, fileName);
		            	}
		            	
						if (filePath != null) {
							cv.put("fileUrl2", filePath);
						}
		            }
		            else
		            {
		            	cv.put("fileUrl2", (String)map.get("fileUrl2"));
		            }
	            }
	            
//	            Map<String,Object> cardmap = (Map<String,Object>)map.get("messagecard");
//	            if(cardmap != null)
//	            {
//	            	saveMessageCardData(cardmap,(String)map.get("mid"));
//	            }
	            
	            List<Map<String,Object>> cardlist = (List<Map<String,Object>>)map.get("storelist");
	            if(cardlist != null)
	            {
	            	 String username = (String)map.get("mysendname");
			         String touserid = (String)map.get("toname");
			         if(username.equals(myapp.getPfprofileId()))
			        	 saveMessageCardListData(cardlist,(String)map.get("mid"),touserid);
			         else
			        	 saveMessageCardListData(cardlist,(String)map.get("mid"),username);
	            }
	            
	            if(groupid != null && !groupid.equals(""))
	    		{
	            	map.put("storeid", groupid);
					map.put("sname", (String)map.get("groupName"));
					map.put("isGroup", "0");
					map.put("groupimg", (String)map.get("groupStaff"));
	    		}
	            
				
	            saveNewMessageData(map);
				
	            //插入ContentValues中的数据  
	            db.insert("messagelist", null, cv);
    		}
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized Map<String,Object> saveMessageData(Map<String,Object> map)
    {
    	try{
    		openDB();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		
    			ContentValues cv = new ContentValues();
    			String groupid = (String)map.get("groupid");
	    		cv.put("mid", (String)map.get("mid"));  
	    		if(groupid != null && !groupid.equals(""))
	    		{
	    			cv.put("mysendname", groupid);
//	    			String storeimg = getFriendImagePath((String)map.get("mysendname"));
	    			String furl = fileUtil.getImageFile2aPath((String)map.get("mysendname"), (String)map.get("mysendname"));
	    			if(!fileUtil.isFileExist3(furl))
	    			{
	    				if(!fileUtil.isFileExist2((String)map.get("mysendname")))
	    					fileUtil.createUserFile((String)map.get("mysendname"));
//		    			String url = Douban.IMG_BASE_URL + (String)map.get("mysendname")+".jpg";
	    				String url = Douban.BASE_URL + "getUserImagePathData;jsessionid="+myapp.getSessionId()+"?userid="+(String)map.get("mysendname");
		    			Bitmap bmpsimg = myapp.returnUserImgBitMap(url);
						myapp.saveMyBitmap(furl, bmpsimg);
						cv.put("storeimgpath", furl);
	    			}
	    			else
	    				cv.put("storeimgpath", furl);
	    		}
	    		else
	    		{
	    			cv.put("mysendname", (String)map.get("mysendname"));
	    		}
	    		cv.put("toid", (String)map.get("toid"));  
//	            cv.put("mysendname", (String)map.get("mysendname"));
	    		if(((String)map.get("toid")).equals(cv.get("mysendname")))
					map.put("userimg", myapp.getUserimgbitmap());
				else
				{
					if(((String)map.get("messagetype")).equals("group"))
					{
						if(cv.get("storeimgpath") != null)
						{
							Bitmap userimg = getLoacalBitmap(cv.getAsString("storeimgpath"));
							map.put("userimg", userimg);
						}
						else
						{
							map.put("userimg", null);
						}
					}
					else
					{
						map.put("userimg", myapp.getStoreimgbitmap());
					}
				}
	            cv.put("fname", (String)map.get("fname"));
	            cv.put("tname", (String)map.get("tname"));  
	            cv.put("toname", (String)map.get("toname")); 
	            if(map.get("messagetype").equals("yanzhen"))
	            	map.put("mymessagecontent", context.getString(R.string.hotel_label_92));
	            else if(map.get("messagetype").equals("yanzhenjieguo"))
	            {
	            	if(map.get("mymessagecontent").equals("0"))
	            	{
	            		map.put("mymessagecontent", context.getString(R.string.hotel_label_108));
	            		updateFriendAllData((String)map.get("mysendname"),"0");
	            	}
	            	else
	            	{
	            		map.put("mymessagecontent", context.getString(R.string.hotel_label_109));
	            		updateFriendAllData((String)map.get("mysendname"),"1");
	            	}
	            }
	            cv.put("mymessagecontent", (String)map.get("mymessagecontent"));  
	            Date datatime = format.parse((String)map.get("mysendtime"));
	            cv.put("mysendtime", format.format(datatime));  
	            cv.put("fileUrl", (String)map.get("fileUrl"));  
	            cv.put("fileUrl2", (String)map.get("fileUrl2"));  
	            cv.put("fileType", (String)map.get("fileType"));  
	            cv.put("fileType2", (String)map.get("fileType2"));
	            cv.put("fileName", (String)map.get("fileName"));
	            cv.put("fileName2", (String)map.get("fileName2"));
	            cv.put("time", (String)map.get("time"));
	            cv.put("timetext", (String)map.get("timetext"));
	            cv.put("messagetype", (String)map.get("messagetype"));
	            cv.put("sendimg", (String)map.get("sendimg"));
	            cv.put("sendprogress", (String)map.get("sendprogress"));
	            cv.put("storename", (String)map.get("storename"));  
	            cv.put("storeDoc", (String)map.get("storeDoc"));
	            cv.put("url", (String)map.get("url"));
	            cv.put("loginname", myapp.getMyaccount());
	            cv.put("isRead", (String)map.get("isRead"));
	            
	            
	            String storeimg = (String)map.get("storeimg");
	            if(storeimg != null && !storeimg.equals(""))
	            {
	            	if(storeimg.indexOf("http") >= 0)
	            	{
			            Bitmap bmpsimg = returnBitMap((String)map.get("storeimg"));
			            if(bmpsimg != null)
			            {
			            	int width = bmpsimg.getWidth();
							int height = bmpsimg.getHeight();
							int newwidth = 0;
							int newheight = 140;
							if(height > newheight || height < newheight)
							{
								BigDecimal b = new BigDecimal((float)height / (float)width);  
								float bili = b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();;
								newwidth = (int)(newheight/bili);
								if(newwidth >= 300)
									bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
								else
								{
									newwidth = 300;
									BigDecimal wb = new BigDecimal((float)width / (float)height);
									float wbili = wb.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
									newheight = (int)(newwidth/wbili);
									bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
								}
							}
							
							int mScreenWidth = (bmpsimg.getWidth() - 300) / 2;
							int mScreenHeight = (bmpsimg.getHeight() - 140) / 2;
							if(mScreenWidth < 0)
								mScreenWidth = 0;
							if(mScreenHeight < 0)
								mScreenHeight = 0;
							bmpsimg = Bitmap.createBitmap(bmpsimg, mScreenWidth, mScreenHeight, 300, 140);
							
							String username = (String)map.get("mysendname");
							UUID uuid = UUID.randomUUID();
							String furl = fileUtil.getImageFile2aPath(username, uuid.toString());
							myapp.saveMyBitmap(furl, bmpsimg);
							cv.put("storeimgpath", furl);
							map.put("storeimg", furl);
			            }
	            	}
	            	else
	            	{
	            		cv.put("storeimgpath", storeimg);
						map.put("storeimg", storeimg);
	            	}
	            }
	            
	            String fileUrl = (String)map.get("fileUrl");
	            if(fileUrl != null && !fileUrl.equals(""))
	            {
	            	if(fileUrl.indexOf("http") >= 0)
	            	{
			            Bitmap bitmimg = getGossipImage((String)map.get("fileUrl"));
			            if(bitmimg != null)
			            {
			            	String fileName = (String)map.get("fileName");
			            	fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
							String [] names = fileName.split("\\.");
			            	String username = (String)map.get("mysendname");
			            	bitmimg = myapp.adaptive(bitmimg, myapp.getScreenWidth(), myapp.getScreenHeight());
							Bitmap iconbitmap = myapp.adaptive(bitmimg);
							UUID uuid = UUID.randomUUID();
							String fileNames = uuid.toString() + "." + names[1];
							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
							String iconfurl = fileUtil.getImageFile1aPath(username, uuid.toString());
							myapp.saveMyBitmap(furl, bitmimg,iconfurl,iconbitmap);
							cv.put("fileName", fileNames);
							cv.put("fileUrl", furl);
							map.put("fileName", fileNames);
							map.put("fileUrl", furl);
			            }
	            	}
	            	else
	            	{
	            		cv.put("fileName", (String)map.get("fileName"));
						cv.put("fileUrl", (String)map.get("fileUrl"));
	            		map.put("fileName", (String)map.get("fileName"));
						map.put("fileUrl", (String)map.get("fileUrl"));
	            	}
	            }
	            
	            if(map.get("fileUrl2") != null && !map.get("fileUrl2").equals(""))
	            {
		            String fileUrl2 = (String)map.get("fileUrl2");
		            String fileName = (String)map.get("fileName2");
		            String username = (String)map.get("mysendname");
		            String touserid = (String)map.get("toname");
		            if(fileUrl2.indexOf("http") >= 0)
		            {
		            	String filePath = null;
		            	if(username.equals(myapp.getPfprofileId()))
		            	{
		            		filePath = fileUtil.createVoice2File1a(touserid, fileUrl2, fileName);
		            	}
		            	else
		            	{
		            		filePath = fileUtil.createVoiceFile1a(username, fileUrl2, fileName);
		            	}
		            	
						if (filePath != null) {
							cv.put("fileUrl2", filePath);
							map.put("fileUrl2", filePath);
						}
		            }
		            else
		            {
		            	cv.put("fileUrl2", fileUrl2);
						map.put("fileUrl2", fileUrl2);
		            }
	            }
	            
	            List<Map<String,Object>> cardlist = (List<Map<String,Object>>)map.get("storelist");
	            if(cardlist != null)
	            {
	            	 String username = (String)map.get("mysendname");
			         String touserid = (String)map.get("toname");
			         if(username.equals(myapp.getPfprofileId()))
			        	 cardlist = saveMessageCardListData(cardlist,(String)map.get("mid"),touserid);
			         else
			        	 cardlist = saveMessageCardListData(cardlist,(String)map.get("mid"),username);
			         map.put("storelist", cardlist);
	            }
	            
	            if(groupid != null && !groupid.equals(""))
	    		{
	            	map.put("storeid", groupid);
					map.put("sname", (String)map.get("groupName"));
					map.put("isGroup", "0");
					map.put("groupimg", (String)map.get("groupStaff"));
	    		}
	            
	            saveNewMessageData(map);
				
	            //插入ContentValues中的数据  
	            db.insert("messagelist", null, cv);
    		
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return map;
    }
    
//    public static void saveMessageCardData(Map<String,Object> map,String mid)
//    {
//    	try{
//    		ContentValues cv = new ContentValues();  
//    		cv.put("mid",mid);  
//    		cv.put("storename", (String)map.get("storename"));  
//            cv.put("storeDoc", (String)map.get("storeDoc"));
//            
//            if((String)map.get("storeimg") != null && !map.get("storeimg").equals(""))
//            {
//	            Bitmap bitmimg = returnBitMap((String)map.get("storeimg"));
//	            int size=bitmimg.getWidth()*bitmimg.getHeight()*4; 
//				ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//				bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//				cv.put("storeimg", oss.toByteArray());
//            }
//            
//            //插入ContentValues中的数据  
//            db.insert("messageCard", null, cv);
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    }
    
    public synchronized List<Map<String,Object>> saveMessageCardListData(List<Map<String,Object>> datalist,String mid,String username)
    {
    	try{
//    		db.beginTransaction();  //手动设置开始事务
    		for(int i=0;i<datalist.size();i++)
    		{
    			Map<String,Object> map = datalist.get(i);
	    		ContentValues cv = new ContentValues();  
	    		cv.put("mid", mid); 
	    		cv.put("loginname", myapp.getMyaccount());
	    		cv.put("title", (String)map.get("title"));  
	            cv.put("desc", (String)map.get("desc"));
	            cv.put("price", (String)map.get("price"));
	            if(map.containsKey("pkid"))
	            	cv.put("pkid", (String)map.get("pkid"));
	            else
	            	cv.put("pkid", "");
	            
	            String sysImg = (String)map.get("sysImg");
	            if((String)map.get("sysImg") != null && !map.get("sysImg").equals(""))
	            {
	            	if(sysImg.indexOf("http") >= 0)
	            	{
			            Bitmap bitmimg = returnBitMap((String)map.get("sysImg"));
			            if(bitmimg != null)
			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("sysImg", oss.toByteArray());wwwww
			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
							UUID uuid = UUID.randomUUID();
							String furl = fileUtil.getImageFile2aPath(username, uuid.toString());
							myapp.saveMyBitmap(furl, bitmimg);
							cv.put("imgpath", furl);
							map.put("sysImg", furl);
			            }
	            	}
//	            	else
//	            	{
//	            		Bitmap bitmimg = getLoacalBitmap(sysImg);
//	            		if(bitmimg != null)
//			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("sysImg", oss.toByteArray());
//			            }
//	            	}
	            }
	            
	            //插入ContentValues中的数据  
	            db.insert("messageCardList", null, cv);
    		}
//    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
//			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return datalist;
    }
    
    public synchronized void saveNewMessageData(Map<String,Object> map)
    {
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
    		String sql = "SELECT * FROM messagenewlist WHERE storeid = ? and loginname = ? ";
    		Cursor c = db.rawQuery(sql, new String[]{(String)map.get("storeid"),myapp.getMyaccount()});  
    		boolean b = false;
    		int number = 0;
    		String imageurl = "";
            while (c.moveToNext()) {  
            	number = c.getInt(c.getColumnIndex("newNumber"));
            	imageurl = c.getString(c.getColumnIndex("storeimg"));
            	b = true;
            	break;
            }
            if(b)
            {
        		String storeid = (String)map.get("storeid");
    			ContentValues cv = new ContentValues();
    			cv.put("storeid", (String)map.get("storeid"));  
    			cv.put("serviceid", (String)map.get("serviceid"));
    			cv.put("servicename", (String)map.get("servicename"));
    			cv.put("isGroup", (String)map.get("isGroup"));
//	    		cv.put("storename", (String)map.get("sname"));
//	    		if(MessageListActivity.instance != null)
//	    		{
//	    			cv.put("typesMapping", MessageListActivity.instance.typesMapping);
//	    		}
//	    		else
//	    		{
//	    			cv.put("typesMapping", "09");
	    			
//	    		}
    			if(!((String)map.get("messagetype")).equals("yanzhenjieguo") && !((String)map.get("messagetype")).equals("groupadddel"))
    				cv.put("typesMapping", (String)map.get("messagetype"));
	    		cv.put("nameid", (String)map.get("nameid"));
	    		if((String)map.get("mymessagecontent") != null && !map.get("mymessagecontent").equals(""))
	    		{
	    			String jobstr = (String)map.get("mymessagecontent");
	    			try{
	    				JSONObject job = new JSONObject(jobstr);
	    				if(job.has("type") && job.getString("type").equals("link"))
	    				{
	    					cv.put("newmessage", context.getString(R.string.hotel_label_176));
	    				}
	    				else
	    				{
	    					cv.put("newmessage", (String)map.get("mymessagecontent"));
	    				}
	    			}catch(Exception ex){
	    				if(myapp.getTapeimgs1().contains((String)map.get("mymessagecontent")))
						{
							cv.put("newmessage", "【"+context.getString(R.string.hotel_label_196)+"】");
						}
	    				else
	    					cv.put("newmessage", (String)map.get("mymessagecontent"));
	    			}
	    		}
	    		else
	    		{
	    			if(myapp.getTapeimgs1().contains((String)map.get("mymessagecontent")))
					{
						cv.put("newmessage", "【"+context.getString(R.string.hotel_label_196)+"】");
					}
	    			else
	    				cv.put("newmessage", context.getString(R.string.hotel_label_175));
	    		}
	            Date datatime = format.parse((String)map.get("mysendtime"));
	            cv.put("sendtime", format2.format(datatime));
	            cv.put("senddatetime", (String)map.get("mysendtime"));
	            cv.put("loginname", myapp.getMyaccount());
//	            cv.put("watag", (String)map.get("watag"));
	            if(map.get("isRead").equals("1"))
	            {
	            	cv.put("newNumber", number+1);
	            }
	            
//	            String imgpath = getStoreImagePath(myapp.getAppstoreid());
	            if(map.get("messagetype").equals("friend") || map.get("messagetype").equals("yanzhenjieguo") || map.get("messagetype").equals("forward"))
	            {
	            	String imgpath = getFriendImagePath(storeid);
	    			cv.put("storeimg", imgpath);
	            }
	            else if(map.get("messagetype").equals("qa"))
	            {
	            	if(map.get("fuserimg") != null && !map.get("fuserimg").equals(""))
	            	{
	            		cv.put("storeimg", (String)map.get("fuserimg"));
	            	}
	            }
	            else if(map.get("messagetype").equals("group") || map.get("messagetype").equals("groupadddel"))
	            {
	            	cv.put("storeimg", (String)map.get("groupimg"));
	            }
	            else
	            {
	            	String imgpath = getStoreImagePath(storeid);
	    			cv.put("storeimg", imgpath);
	            }
	            
//	            Bitmap bitmimg = myapp.getStoreimgbitmap();
//        		if(bitmimg != null)
//	            {
//        			if(!fileUtil.isFileExist2(storeid))
//	            	{
//	            		fileUtil.createUserFile(storeid);
//	            	}
//        			
//        			if(imageurl != null && !imageurl.equals(""))
//        			{
//        				fileUtil.deleteFileDrid(imageurl);
//        			}
//        			
//        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//					UUID uuid = UUID.randomUUID();
//					String furl = fileUtil.getImageFile1aPath(storeid, uuid.toString());
//					fileUtil.saveMyBitmap(furl, bitmimg);
//					cv.put("storeimg", furl);
//	            }
//        		else
//        		{
//        			bitmimg = getStoreImage(myapp.getAppstoreid());
//        			if(bitmimg == null)
//        				cv.put("storeimg", "");
//        			else
//        			{
////        				int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
////    					ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
////    					bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
////    					cv.put("storeimg", oss.toByteArray());
//        				if(!fileUtil.isFileExist2(storeid))
//		            	{
//		            		fileUtil.createUserFile(storeid);
//		            	}
//        				
//        				if(imageurl != null && !imageurl.equals(""))
//            			{
//            				fileUtil.deleteFileDrid(imageurl);
//            			}
//        				bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//    					UUID uuid = UUID.randomUUID();
//    					String furl = fileUtil.getImageFile1aPath(storeid, uuid.toString());
//    					fileUtil.saveMyBitmap(furl, bitmimg);
//    					cv.put("storeimg", furl);
//        			}
//        		}

    			// 更新数据
    			db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount() });
            }
            else
            {
            	String storeid = (String)map.get("storeid");
	    		ContentValues cv = new ContentValues();  
	    		cv.put("storeid", (String)map.get("storeid"));  
	    		cv.put("storename", (String)map.get("sname")); 
	    		cv.put("serviceid", (String)map.get("serviceid"));
    			cv.put("servicename", (String)map.get("servicename"));
    			cv.put("isGroup", (String)map.get("isGroup"));
//	    		if(MessageListActivity.instance != null)
//	    			cv.put("typesMapping", MessageListActivity.instance.typesMapping);
//	    		else
//	    		{
//	    			cv.put("typesMapping", "09");
    			if(map.get("messagetype").equals("groupadddel"))
	    			cv.put("typesMapping", "group");
    			else if(map.get("messagetype").equals("forward"))
    				cv.put("typesMapping", "friend");
    			else
    				cv.put("typesMapping", (String)map.get("messagetype"));
//	    		}
	    		cv.put("nameid", (String)map.get("nameid"));
	    		if((String)map.get("mymessagecontent") != null && !map.get("mymessagecontent").equals(""))
	    		{
	    			String jobstr = (String)map.get("mymessagecontent");
	    			try{
	    				JSONObject job = new JSONObject(jobstr);
	    				if(job.has("type") && job.getString("type").equals("link"))
	    				{
	    					cv.put("newmessage", context.getString(R.string.hotel_label_176));
	    				}
	    				else
	    				{
	    					cv.put("newmessage", (String)map.get("mymessagecontent"));
	    				}
	    			}catch(Exception ex){
	    				cv.put("newmessage", (String)map.get("mymessagecontent"));
	    			}
	    		}
	    		else
	    			cv.put("newmessage", (String)map.get("mymessagecontent")); 
	            Date datatime = format.parse((String)map.get("mysendtime"));
	            cv.put("sendtime", format2.format(datatime));
	            cv.put("senddatetime", (String)map.get("mysendtime"));
	            cv.put("loginname", myapp.getMyaccount());
	            cv.put("watag", (String)map.get("watag"));
	            
	            if(map.get("isRead").equals("1"))
	            {
	            	cv.put("newNumber", 1);
	            }
	            else
	            {
	            	cv.put("newNumber", 0);
	            }
	            
//	            String imgpath = getStoreImagePath(myapp.getAppstoreid());
	            if(map.get("messagetype").equals("friend") || map.get("messagetype").equals("yanzhenjieguo") || map.get("messagetype").equals("forward"))
	            {
	            	String imgpath = getFriendImagePath(storeid);
	    			cv.put("storeimg", imgpath);
	            }
	            else if(map.get("messagetype").equals("qa"))
	            {
	            	if(map.get("fuserimg") != null && !map.get("fuserimg").equals(""))
	            	{
	            		cv.put("storeimg", (String)map.get("fuserimg"));
	            	}
	            }
	            else if(map.get("messagetype").equals("group") || map.get("messagetype").equals("groupadddel"))
	            {
	            	cv.put("storeimg", (String)map.get("groupimg"));
	            }
	            else
	            {
	            	String imgpath = getStoreImagePath(storeid);
	    			cv.put("storeimg", imgpath);
	            }
//	            Bitmap bitmimg = myapp.getStoreimgbitmap();
//        		if(bitmimg != null)
//	            {
//        			if(!fileUtil.isFileExist2(storeid))
//	            	{
//	            		fileUtil.createUserFile(storeid);
//	            	}
//        			
//        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//					UUID uuid = UUID.randomUUID();
//					String furl = fileUtil.getImageFile1aPath(storeid, uuid.toString());
//					fileUtil.saveMyBitmap(furl, bitmimg);
//					cv.put("storeimg", furl);
//	            }
//        		else
//        		{
//        			bitmimg = getStoreImage(myapp.getAppstoreid());
//        			if(bitmimg == null)
//        				cv.put("storeimg", "");
//        			else
//        			{
////        				int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
////    					ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
////    					bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
////    					cv.put("storeimg", oss.toByteArray());
//        				if(!fileUtil.isFileExist2(storeid))
//		            	{
//		            		fileUtil.createUserFile(storeid);
//		            	}
//        				
//        				bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//    					UUID uuid = UUID.randomUUID();
//    					String furl = fileUtil.getImageFile1aPath(storeid, uuid.toString());
//    					fileUtil.saveMyBitmap(furl, bitmimg);
//    					cv.put("storeimg", furl);
//        			}
//        		}
	            
	            //插入ContentValues中的数据  
	            db.insert("messagenewlist", null, cv);
            }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void saveStoreInfoAllData(List<Map<String,Object>> dataList)
    {
    	try{
    		openDB();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		for(int i=0;i<dataList.size();i++)
    		{
    			Map<String,Object> map = dataList.get(i);
	    		ContentValues cv = new ContentValues();  
	    		cv.put("storeid", (String)map.get("storeid"));
	    		cv.put("storeNo", (String)map.get("storeNo"));
	    		cv.put("points", (Integer)map.get("points"));  
	            cv.put("nameOnCard", (String)map.get("nameOnCard"));
	            cv.put("nameid", (String)map.get("nameid"));
	            cv.put("pfids", (String)map.get("pfids"));  
	            cv.put("cardNo", (String)map.get("cardNo"));  
	            cv.put("joinedDate", (String)map.get("joinedDate"));  
	            cv.put("mdmType", (String)map.get("mdmType"));
	            cv.put("mdmLevel", (String)map.get("mdmLevel"));  
	            cv.put("mdmstatus", (String)map.get("mdmstatus"));  
	            cv.put("expDate", (String)map.get("expDate"));  
	            cv.put("chainCode", (String)map.get("chainCode"));
	            cv.put("storeName", (String)map.get("storeName"));
	            cv.put("sortName", (String)map.get("sortName"));
	            cv.put("sortPinyin", converterToFirstSpell((String)map.get("sortName")));
	            cv.put("username", (String)map.get("username"));
	            cv.put("password", (String)map.get("password"));
	            cv.put("pkid", (String)map.get("pkid"));
	            cv.put("storePhone", (String)map.get("storePhone"));
	            cv.put("addressInfomation", (String)map.get("addressInfomation"));
	            cv.put("storeDesc", (String)map.get("storeDesc"));  
	            cv.put("isASttention", (String)map.get("isASttention"));
	            cv.put("xinxin", (Integer)map.get("xinxin"));
	            cv.put("loginname", myapp.getMyaccount());
	            cv.put("couponNumber", (String)map.get("couponNumber"));
	            cv.put("typeName", (String)map.get("typeName"));
	            cv.put("typesMapping", (String)map.get("typesMapping"));
	            cv.put("businessId", (String)map.get("businessId"));
	            cv.put("woof", (String)map.get("woof"));
	            cv.put("longItude", (String)map.get("longItude"));
	            cv.put("isLu", (String)map.get("isLu"));
	            cv.put("storeType", (String)map.get("storeType"));
	            cv.put("province", (String)map.get("province"));
	            cv.put("roomIntroduction", (String)map.get("roomIntroduction"));
	            cv.put("periphery", (String)map.get("periphery"));
	            cv.put("trafficWay", (String)map.get("trafficWay"));
	            cv.put("startingPrice", (String)map.get("startingPrice"));
	            cv.put("score", (String)map.get("score"));
	            cv.put("comments", (String)map.get("comments"));
	            cv.put("lastmessage", (String)map.get("lastmessage"));
	            cv.put("lastmessagetime", (String)map.get("lastmessagetime"));
	            cv.put("language", (String)map.get("language"));
	            cv.put("customizeMenu", (String)map.get("customizeMenu"));
	            cv.put("linkurl", (String)map.get("linkurl"));
	            cv.put("servicetype", (String)map.get("servicetype"));
	            cv.put("profileid", (String)map.get("profileId"));
	            
	            String storeid = (String)map.get("storeid");
	            String imgurl = (String)map.get("imgurl");
	            cv.put("imgurl", imgurl);
//	            if(imgurl != null && !imgurl.equals(""))
//	            {
//	            	if(!fileUtil.isFileExist2(storeid))
//	            	{
//	            		fileUtil.createUserFile(storeid);
//	            	}
//	            	if(imgurl.indexOf("http") >= 0)
//	            	{
//	            		String [] strs = imgurl.split("\\/");
//		            	String filename = strs[strs.length-1];
//		            	String [] names = filename.split("\\.");
//		            	filename = names[0];
//		            	String furl = fileUtil.getImageFile1aPath(storeid, filename);
//						File file = new File(furl);
//						Bitmap bitmimg = null;
//						if(!file.exists())
//							bitmimg = returnBitMap((String)map.get("imgurl"));
//						else
//							bitmimg = getLoacalBitmap(furl);
//			            if(bitmimg != null)
//			            {
////				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
////							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
////							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
////							cv.put("imgurl", oss.toByteArray());
//			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							if(!file.exists())
//								fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("imgurl", furl);
//			            }
//	            	}
//	            	else
//	            	{
//	            		Bitmap bitmimg = getLoacalBitmap(imgurl);
//	            		if(bitmimg != null)
//			            {
////				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
////							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
////							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
////							cv.put("imgurl", oss.toByteArray());
//	            			String [] strs = imgurl.split("\\/");
//			            	String filename = strs[strs.length];
//	            			
//	            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							UUID uuid = UUID.randomUUID();
//							String furl = fileUtil.getImageFile1aPath(storeid, filename);
//							File file = new File(furl);
//							if(!file.exists())
//								fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("imgurl", furl);
//			            }
//	            	}
//	            }
	            
	            //插入ContentValues中的数据  
	            db.insert("storeinfo", null, cv);
    		}
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void saveFriendAllData(Map<String,Object> map)
    {
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		
    		Cursor c = null;
    		String sql = "SELECT * FROM friendlist WHERE loginname = ? and pfid = ? order by namePinyin COLLATE LOCALIZED";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount(),(String)map.get("pfid")});
			boolean b = false;
            while (c.moveToNext()) {  
            	b = true;
            }
    		
            if(!b)
            {
	    		ContentValues cv = new ContentValues();  
	    		cv.put("pkid", (String)map.get("pkid"));  
	    		cv.put("username", (String)map.get("username"));  
	    		cv.put("namePinyin", converterToFirstSpell((String)map.get("username")));
	    		cv.put("pfid", (String)map.get("pfid"));
	    		
	    		cv.put("account", (String)map.get("account"));
	    		cv.put("sex", (String)map.get("sex"));
	    		cv.put("area", (String)map.get("area"));
	    		cv.put("signature", (String)map.get("signature"));
	    		cv.put("start", (String)map.get("start"));
	    		cv.put("companyid", (String)map.get("companyid"));
	    		cv.put("storeid", (String)map.get("storeid"));
	    		
	    		cv.put("loginname", myapp.getMyaccount());
	            String imgurl = (String)map.get("imgurl");
	            String pfid = (String)map.get("pfid");
	            if(imgurl != null && !imgurl.equals(""))
	            {
//	            	if(imgurl.indexOf("http") >= 0)
//	            	{
//			            Bitmap bitmimg = returnBitMap((String)map.get("imgurl"));
//			            if(bitmimg != null)
//			            {
//			            	if(!fileUtil.isFileExist2(pfid))
//			            	{
//			            		fileUtil.createUserFile(pfid);
//			            	}
//			            	
//			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							UUID uuid = UUID.randomUUID();
//							String furl = fileUtil.getImageFile1aPath(pfid, uuid.toString());
//							fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("userimg", furl);
//			            }
//	            	}
//	            	else
//	            	{
//	            		Bitmap bitmimg = getLoacalBitmap(imgurl);
//	            		if(bitmimg != null)
//			            {
//	            			if(!fileUtil.isFileExist2(pfid))
//			            	{
//			            		fileUtil.createUserFile(pfid);
//			            	}
//	            			
//	            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							UUID uuid = UUID.randomUUID();
//							String furl = fileUtil.getImageFile1aPath(pfid, uuid.toString());
//							fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("userimg", furl);
//			            }
//	            	}
	            	cv.put("userimg", imgurl);
	            }
	            
	            //插入ContentValues中的数据  
	            db.insert("friendlist", null, cv);
            }
            else
            {
            	ContentValues cv = new ContentValues();
	    		cv.put("username", (String)map.get("username"));  
	    		cv.put("namePinyin", converterToFirstSpell((String)map.get("username")));
	    		cv.put("pfid", (String)map.get("pfid"));
	    		
	    		cv.put("account", (String)map.get("account"));
	    		cv.put("sex", (String)map.get("sex"));
	    		cv.put("area", (String)map.get("area"));
	    		cv.put("signature", (String)map.get("signature"));
	    		cv.put("start", (String)map.get("start"));
	    		cv.put("companyid", (String)map.get("companyid"));
	    		cv.put("storeid", (String)map.get("storeid"));
	    		
	    		cv.put("loginname", myapp.getMyaccount());
	            String imgurl = (String)map.get("imgurl");
	            String pfid = (String)map.get("pfid");
//	            if(imgurl != null && !imgurl.equals(""))
//	            {
//	            	if(imgurl.indexOf("http") >= 0)
//	            	{
//			            Bitmap bitmimg = returnBitMap((String)map.get("imgurl"));
//			            if(bitmimg != null)
//			            {
//			            	if(!fileUtil.isFileExist2(pfid))
//			            	{
//			            		fileUtil.createUserFile(pfid);
//			            	}
//			            	
//			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							UUID uuid = UUID.randomUUID();
//							String furl = fileUtil.getImageFile1aPath(pfid, uuid.toString());
//							fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("userimg", furl);
//			            }
//	            	}
//	            	else
//	            	{
//	            		Bitmap bitmimg = getLoacalBitmap(imgurl);
//	            		if(bitmimg != null)
//			            {
//	            			if(!fileUtil.isFileExist2(pfid))
//			            	{
//			            		fileUtil.createUserFile(pfid);
//			            	}
//	            			
//	            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							UUID uuid = UUID.randomUUID();
//							String furl = fileUtil.getImageFile1aPath(pfid, uuid.toString());
//							fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("userimg", furl);
//			            }
//	            	}
//	            }
	            
	            db.update("friendlist", cv, "loginname = ? and pfid = ?", new String[] { myapp.getMyaccount(),(String)map.get("pfid") });
            }
            
            c.close();
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void saveFriendAllData(List<Map<String,Object>> dlist)
    {
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		
    		for(int i=0;i<dlist.size();i++)
    		{
    			Map<String,Object> map = dlist.get(i);
    			ContentValues cv = new ContentValues();  
        		cv.put("pkid", (String)map.get("pkid"));  
        		cv.put("username", (String)map.get("username"));  
        		cv.put("namePinyin", converterToFirstSpell((String)map.get("username")));
        		cv.put("pfid", (String)map.get("pfid"));
        		
        		cv.put("account", (String)map.get("account"));
        		cv.put("sex", (String)map.get("sex"));
        		cv.put("area", (String)map.get("area"));
        		cv.put("signature", (String)map.get("signature"));
        		cv.put("start", (String)map.get("start"));
        		cv.put("companyid", (String)map.get("companyid"));
        		cv.put("storeid", (String)map.get("storeid"));
        		
        		cv.put("loginname", myapp.getMyaccount());
                String imgurl = (String)map.get("imgurl");
                String pfid = (String)map.get("pfid");
                if(imgurl != null && !imgurl.equals(""))
                {
//                	if(imgurl.indexOf("http") >= 0)
//                	{
//    		            Bitmap bitmimg = returnBitMap((String)map.get("imgurl"));
//    		            if(bitmimg != null)
//    		            {
//    		            	if(!fileUtil.isFileExist2(pfid))
//    		            	{
//    		            		fileUtil.createUserFile(pfid);
//    		            	}
//    		            	
//    		            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
////    						UUID uuid = UUID.randomUUID();
//    						String furl = fileUtil.getImageFile1aPath(pfid, (String)map.get("pfid"));
//    						fileUtil.saveMyBitmap(furl, bitmimg);
//    						cv.put("userimg", furl);
//    		            }
//                	}
//                	else
//                	{
//                		Bitmap bitmimg = getLoacalBitmap(imgurl);
//                		if(bitmimg != null)
//    		            {
//                			if(!fileUtil.isFileExist2(pfid))
//    		            	{
//    		            		fileUtil.createUserFile(pfid);
//    		            	}
//                			
//                			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
////    						UUID uuid = UUID.randomUUID();
//    						String furl = fileUtil.getImageFile1aPath(pfid, (String)map.get("pfid"));
//    						fileUtil.saveMyBitmap(furl, bitmimg);
//    						cv.put("userimg", furl);
//    		            }
//                	}
                	cv.put("userimg", imgurl);
                }
                
                //插入ContentValues中的数据  
                db.insert("friendlist", null, cv);
    		}
            
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    //判断朋友圈信息是否存在
    public synchronized boolean isMomentHas(String momentid)
    {
    	if(db==null)
			openDB();
    	
    	boolean b = false;
    	try{
    		String sql = "SELECT * FROM moments WHERE pkid = '"+momentid+"'";
    		Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {  
                b = true;
                break;
            }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return b;
    }
    
    public synchronized void saveMomentsAll(List<Map<String,Object>> dlist)
    {
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		
    		for(int i=0;i<dlist.size();i++)
    		{
    			Map<String,Object> map = dlist.get(i);
    			ContentValues cv = new ContentValues();  
        		cv.put("pkid", (String)map.get("pkid"));  
        		cv.put("content", (String)map.get("content"));  
        		cv.put("publishtime", (String)map.get("publishtime"));
        		cv.put("publishuser", (String)map.get("publishuser"));
        		if(map.get("publishtype") != null)
        			cv.put("publishtype", (String)map.get("publishtype"));
        		else
        			cv.put("publishtype", "");
        		cv.put("publishusertype", (String)map.get("publishusertype"));
        		
        		cv.put("publishid", (String)map.get("publishid"));
        		cv.put("loginname", myapp.getMyaccount());
        		
				String userimg = getFriendImagePath((String)map.get("publishid"));
				cv.put("userimg", userimg);
				
        		List<Map<String,String>> filelist = (List<Map<String,String>>)map.get("filelist");
        		if(filelist != null)
        			saveMomentsFileAll(filelist,(String)map.get("publishid"));
                
                //插入ContentValues中的数据  
                db.insert("moments", null, cv);
    		}
            
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void saveMomentsFileAll(List<Map<String,String>> dlist,String pfid)
    {
    	try{
    		for(int i=0;i<dlist.size();i++)
    		{
    			Map<String,String> map = dlist.get(i);
    			ContentValues cv = new ContentValues();  
        		cv.put("pkid", (String)map.get("pkid"));
        		cv.put("filename", (String)map.get("filename"));  
        		cv.put("filetype", (String)map.get("filetype"));
        		cv.put("filewidth", (String)map.get("filewidth"));
        		cv.put("fileheight", (String)map.get("fileheight"));
        		
        		cv.put("momentsid", (String)map.get("momentsid"));
        		cv.put("loginname", myapp.getMyaccount());
        		String fileurl = (String)map.get("fileurl");
        		if(fileurl.contains("http://"))
        		{
        			if(map.get("filetype").equals("4")){
        				Bitmap bitmimg = returnBitMap(fileurl);
    	        		if(bitmimg != null)
    		            {
    	        			if(!fileUtil.isFileExist2(pfid))
    		            	{
    		            		fileUtil.createUserFile(pfid);
    		            	}
    	        			
    	//        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
    						UUID uuid = UUID.randomUUID();
    						String furl = fileUtil.getImageFile1aPath(pfid, uuid.toString());
    						fileUtil.saveMyBitmap(furl, bitmimg);
    						cv.put("fileurl", furl);
    		            }
        			}
        		}
        		else
        		{
        			cv.put("fileurl", fileurl);
        		}
                
                //插入ContentValues中的数据  
                db.insert("momentsfiles", null, cv);
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized static void updateMessageData(Map<String,Object> map)
    {
    	try{
    		db.beginTransaction();  //手动设置开始事务
    		String mid = (String)map.get("mid");
			ContentValues cv = new ContentValues();
			cv.put("mid", (String)map.get("mid"));
			cv.put("toid", (String)map.get("toid"));  
			cv.put("mysendname", (String) map.get("mysendname"));
			cv.put("fname", (String) map.get("fname"));
			cv.put("tname", (String) map.get("tname"));
			cv.put("toname", (String) map.get("toname"));
			cv.put("mymessagecontent", (String) map.get("mymessagecontent"));
			cv.put("mysendtime", (String) map.get("mysendtime"));
//			cv.put("fileUrl", (String) map.get("fileUrl"));
//			cv.put("fileUrl2", (String) map.get("fileUrl2"));
			cv.put("fileType", (String) map.get("fileType"));
			cv.put("fileType2", (String) map.get("fileType2"));
			cv.put("fileName", (String) map.get("fileName"));
			cv.put("fileName2", (String) map.get("fileName2"));
			cv.put("time", (String) map.get("time"));
			cv.put("timetext", (String) map.get("timetext"));
			cv.put("messagetype", (String) map.get("messagetype"));
			cv.put("sendimg", (String) map.get("sendimg"));
			cv.put("sendprogress", (String) map.get("sendprogress"));

			// 更新数据
			db.update("messagelist", cv, "mid = ?", new String[] { mid });
			
			db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateNewMessageData(String groupname,String groupid)
    {
    	try{
			ContentValues cv = new ContentValues();
			cv.put("storename", groupname);
            

			// 更新数据
			db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { groupid,myapp.getMyaccount() });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateNewMessageUserNameImageData(String storename,String imgpath,String storeid)
    {
    	try{
			ContentValues cv = new ContentValues();
			cv.put("storename", storename);
            cv.put("storeimg",imgpath);

			// 更新数据
			db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount() });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateNewMessageGroupInfoData(String groupName,String storeimg,String storeid)
    {
    	try{
			ContentValues cv = new ContentValues();
			cv.put("storename", groupName);
			cv.put("storeimg", storeimg);

			// 更新数据
			db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount() });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized static void updateMessageData(String mid,String messagestart)
    {
    	try{
    		db.beginTransaction();  //手动设置开始事务
    		ContentValues cv = new ContentValues();
			cv.put("messagestart", messagestart);

			// 更新数据
			db.update("messagelist", cv, "mid = ?", new String[] { mid });
			
			db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateNewMessageIsTop(String storeid,String istop)
    {
    	try{
			ContentValues cv = new ContentValues();
            cv.put("isTop", istop);

			// 更新数据
			db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount() });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void saveOrUpdateStoreInfo(List<Map<String,Object>> dataList)
    {
    	try{
    		for(int i=0;i<dataList.size();i++)
    		{
    			Map<String,Object> map = dataList.get(i);
    			String storeid = (String)map.get("storeid");
    			if(isStoreInfo(storeid))
    			{
    				updateStoreInfoData(map);
    			}
    			else
    			{
    				saveStoreInfoOneData(map);
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized boolean isStoreInfo(String storeid)
    {
    	boolean b = false;
    	try{
    		String sql = "SELECT * FROM storeinfo WHERE loginname = ? and storeid = '"+storeid+"'";
    		Cursor c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
            while (c.moveToNext()) {  
                b = true;
                break;
            }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return b;
    }
    
    public synchronized void saveStoreInfoOneData(Map<String,Object> map)
    {
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		db.beginTransaction();  //手动设置开始事务
    		ContentValues cv = new ContentValues();  
    		cv.put("storeid", (String)map.get("storeid"));
    		cv.put("points", (Integer)map.get("points"));  
            cv.put("nameOnCard", (String)map.get("nameOnCard"));
            cv.put("nameid", (String)map.get("nameid"));
            cv.put("pfids", (String)map.get("pfids"));  
            cv.put("cardNo", (String)map.get("cardNo"));  
            cv.put("joinedDate", (String)map.get("joinedDate"));  
            cv.put("mdmType", (String)map.get("mdmType"));
            cv.put("mdmLevel", (String)map.get("mdmLevel"));  
            cv.put("mdmstatus", (String)map.get("mdmstatus"));  
            cv.put("expDate", (String)map.get("expDate"));  
            cv.put("chainCode", (String)map.get("chainCode"));
            cv.put("storeName", (String)map.get("storeName"));
            cv.put("sortName", (String)map.get("sortName"));
            cv.put("sortPinyin", converterToFirstSpell((String)map.get("sortName")));
            cv.put("username", (String)map.get("username"));
            cv.put("password", (String)map.get("password"));
            cv.put("pkid", (String)map.get("pkid"));
            cv.put("storePhone", (String)map.get("storePhone"));
            cv.put("addressInfomation", (String)map.get("addressInfomation"));
            cv.put("storeDesc", (String)map.get("storeDesc"));  
            cv.put("isASttention", (String)map.get("isASttention"));
            cv.put("xinxin", (Integer)map.get("xinxin"));
            cv.put("loginname", myapp.getMyaccount());
            cv.put("couponNumber", (String)map.get("couponNumber"));
            cv.put("typeName", (String)map.get("typeName"));
            cv.put("typesMapping", (String)map.get("typesMapping"));
            cv.put("businessId", (String)map.get("businessId"));
            cv.put("woof", (String)map.get("woof"));
            cv.put("longItude", (String)map.get("longItude"));
            cv.put("isLu", (String)map.get("isLu"));
            cv.put("storeType", (String)map.get("storeType"));
            cv.put("province", (String)map.get("province"));
            cv.put("roomIntroduction", (String)map.get("roomIntroduction"));
            cv.put("periphery", (String)map.get("periphery"));
            cv.put("trafficWay", (String)map.get("trafficWay"));
            cv.put("startingPrice", (String)map.get("startingPrice"));
            cv.put("score", (String)map.get("score"));
            cv.put("comments", (String)map.get("comments"));
            cv.put("lastmessage", (String)map.get("lastmessage"));
            cv.put("lastmessagetime", (String)map.get("lastmessagetime"));
            cv.put("language", (String)map.get("language"));
            cv.put("customizeMenu", (String)map.get("customizeMenu"));
            
            String storeid = (String)map.get("storeid");
            String imgurl = (String)map.get("imgurl");
            if(imgurl != null && !imgurl.equals(""))
            {
            	if(!fileUtil.isFileExist2(storeid))
            	{
            		fileUtil.createUserFile(storeid);
            	}
            	if(imgurl.indexOf("http") >= 0)
            	{
            		String [] strs = imgurl.split("\\/");
	            	String filename = strs[strs.length-1];
	            	String [] names = filename.split("\\.");
	            	filename = names[0];
	            	String furl = fileUtil.getImageFile1aPath(storeid, filename);
					File file = new File(furl);
					Bitmap bitmimg = null;
					if(!file.exists())
						bitmimg = returnBitMap((String)map.get("imgurl"));
					else
						bitmimg = getLoacalBitmap(furl);
		            if(bitmimg != null)
		            {
//			            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//						ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//						bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//						cv.put("imgurl", oss.toByteArray());
		            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
						if(!file.exists())
							fileUtil.saveMyBitmap(furl, bitmimg);
						cv.put("imgurl", furl);
		            }
            	}
            	else
            	{
            		Bitmap bitmimg = getLoacalBitmap(imgurl);
            		if(bitmimg != null)
		            {
//			            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//						ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//						bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//						cv.put("imgurl", oss.toByteArray());
            			String [] strs = imgurl.split("\\/");
		            	String filename = strs[strs.length];
            			
            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
						UUID uuid = UUID.randomUUID();
						String furl = fileUtil.getImageFile1aPath(storeid, filename);
						File file = new File(furl);
						if(!file.exists())
							fileUtil.saveMyBitmap(furl, bitmimg);
						cv.put("imgurl", furl);
		            }
            	}
            }
            
            //插入ContentValues中的数据  
            db.insert("storeinfo", null, cv);
    		db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateStoreInfoData(Map<String,Object> map)
    {
    	try{
    		db.beginTransaction();  //手动设置开始事务
    		String storeid = (String)map.get("storeid");
    		ContentValues cv = new ContentValues();  
    		cv.put("storeid", (String)map.get("storeid"));  
    		cv.put("points", (Integer)map.get("points"));  
            cv.put("nameOnCard", (String)map.get("nameOnCard"));  
            cv.put("nameid", (String)map.get("nameid"));
            cv.put("pfids", (String)map.get("pfids"));  
            cv.put("cardNo", (String)map.get("cardNo"));  
            cv.put("joinedDate", (String)map.get("joinedDate"));  
            cv.put("mdmType", (String)map.get("mdmType"));  
            cv.put("mdmLevel", (String)map.get("mdmLevel"));  
            cv.put("mdmstatus", (String)map.get("mdmstatus"));  
            cv.put("expDate", (String)map.get("expDate"));  
            cv.put("chainCode", (String)map.get("chainCode"));
            cv.put("storeName", (String)map.get("storeName"));
            cv.put("sortName", (String)map.get("sortName"));
            cv.put("sortPinyin", converterToFirstSpell((String)map.get("sortName")));
            cv.put("username", (String)map.get("username"));
            cv.put("password", (String)map.get("password"));
            cv.put("pkid", (String)map.get("pkid"));
            cv.put("storePhone", (String)map.get("storePhone"));
            cv.put("addressInfomation", (String)map.get("addressInfomation"));
            cv.put("storeDesc", (String)map.get("storeDesc"));  
            cv.put("isASttention", (String)map.get("isASttention"));
            cv.put("xinxin", (Integer)map.get("xinxin"));
            cv.put("loginname", myapp.getMyaccount());
            cv.put("couponNumber", (String)map.get("couponNumber"));
            cv.put("typeName", (String)map.get("typeName"));
            cv.put("typesMapping", (String)map.get("typesMapping"));
            cv.put("businessId", (String)map.get("businessId"));
            cv.put("woof", (String)map.get("woof"));
            cv.put("longItude", (String)map.get("longItude"));
            cv.put("isLu", (String)map.get("isLu"));
            cv.put("storeType", (String)map.get("storeType"));
            cv.put("province", (String)map.get("province"));
            cv.put("roomIntroduction", (String)map.get("roomIntroduction"));
            cv.put("periphery", (String)map.get("periphery"));
            cv.put("trafficWay", (String)map.get("trafficWay"));
            cv.put("startingPrice", (String)map.get("startingPrice"));
            cv.put("score", (String)map.get("score"));
            cv.put("comments", (String)map.get("comments"));
            cv.put("lastmessage", (String)map.get("lastmessage"));
            cv.put("lastmessagetime", (String)map.get("lastmessagetime"));
            cv.put("language", (String)map.get("language"));
            cv.put("customizeMenu", (String)map.get("customizeMenu"));
            
            
            Object img = map.get("imgurl");
            if(img instanceof String)
            {
            	String imgurl = (String)img;
            	if(imgurl != null && !imgurl.equals(""))
	            {
	            	if(!fileUtil.isFileExist2(storeid))
	            	{
	            		fileUtil.createUserFile(storeid);
	            	}
	            	if(imgurl.indexOf("http") >= 0)
	            	{
	            		String [] strs = imgurl.split("\\/");
		            	String filename = strs[strs.length-1];
		            	String [] names = filename.split("\\.");
		            	filename = names[0];
		            	String furl = fileUtil.getImageFile1aPath(storeid, filename);
						File file = new File(furl);
						Bitmap bitmimg = null;
						if(!file.exists())
							bitmimg = returnBitMap((String)map.get("imgurl"));
						else
							bitmimg = getLoacalBitmap(furl);
			            if(bitmimg != null)
			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("imgurl", oss.toByteArray());
			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
							if(!file.exists())
								fileUtil.saveMyBitmap(furl, bitmimg);
							cv.put("imgurl", furl);
			            }
	            	}
	            	else
	            	{
	            		Bitmap bitmimg = getLoacalBitmap(imgurl);
	            		if(bitmimg != null)
			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("imgurl", oss.toByteArray());
	            			String [] strs = imgurl.split("\\/");
			            	String filename = strs[strs.length-1];
	            			
	            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
							UUID uuid = UUID.randomUUID();
							String furl = fileUtil.getImageFile1aPath(storeid, filename);
							File file = new File(furl);
							if(!file.exists())
								fileUtil.saveMyBitmap(furl, bitmimg);
							cv.put("imgurl", furl);
			            }
	            	}
	            }
            }

			// 更新数据
			db.update("storeinfo", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount()});
			
			db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); //处理完成 
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateFriendAllData(String pfid,String start)
    {
    	try{
    		
    		ContentValues cv = new ContentValues();  
    		cv.put("start", start);
    		
            db.update("friendlist", cv, "loginname = ? and pfid = ?", new String[] { myapp.getMyaccount(),pfid });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized void updateFriendAllFileUrlData(String pfid,String furl)
    {
    	try{
    		
    		ContentValues cv = new ContentValues();  
    		cv.put("userimg", furl);
    		
            db.update("friendlist", cv, "loginname = ? and pfid = ?", new String[] { myapp.getMyaccount(),pfid });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized Map<String,Object> updateFriendAllData(Map<String,Object> map)
    {
    	try{
    		
    		ContentValues cv = new ContentValues();  
    		cv.put("username", (String)map.get("username"));  
    		cv.put("namePinyin", converterToFirstSpell((String)map.get("username")));
    		cv.put("pfid", (String)map.get("pfid"));
    		
    		cv.put("account", (String)map.get("account"));
    		cv.put("sex", (String)map.get("sex"));
    		cv.put("area", (String)map.get("area"));
    		cv.put("signature", (String)map.get("signature"));
    		cv.put("companyid", (String)map.get("companyid"));
    		cv.put("storeid", (String)map.get("storeid"));
    		
    		cv.put("loginname", myapp.getMyaccount());
            String imgurl = (String)map.get("imgurl");
            String pfid = (String)map.get("pfid");
            if(imgurl != null && !imgurl.equals(""))
            {
            	if(imgurl.indexOf("http") >= 0)
            	{
		            Bitmap bitmimg = returnBitMap((String)map.get("imgurl"));
		            if(bitmimg != null)
		            {
		            	if(!fileUtil.isFileExist2(pfid))
		            	{
		            		fileUtil.createUserFile(pfid);
		            	}
		            	
		            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//						UUID uuid = UUID.randomUUID();
						String furl = fileUtil.getImageFile2aPath(pfid, (String)map.get("pfid"));
						fileUtil.saveMyBitmap(furl, bitmimg);
						cv.put("userimg", furl);
						map.put("imgurl", furl);
		            }
            	}
            	else
            	{
            		Bitmap bitmimg = getLoacalBitmap(imgurl);
            		if(bitmimg != null)
		            {
            			if(!fileUtil.isFileExist2(pfid))
		            	{
		            		fileUtil.createUserFile(pfid);
		            	}
            			
            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//						UUID uuid = UUID.randomUUID();
						String furl = fileUtil.getImageFile2aPath(pfid, (String)map.get("pfid"));
						fileUtil.saveMyBitmap(furl, bitmimg);
						cv.put("userimg", furl);
						map.put("imgurl", furl);
		            }
            	}
            }
    		
            db.update("friendlist", cv, "loginname = ? and pfid = ?", new String[] { myapp.getMyaccount(),(String)map.get("pfid") });
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return map;
    }
    
    public synchronized static void deleteMessageData(String mid)
    {
    	try{
    		String sql = "SELECT * FROM messagelist WHERE mid = ?  and loginname = ? ";
    		Cursor c = db.rawQuery(sql, new String[]{mid,myapp.getMyaccount()});  
    		String tid = "";
    		String fid = "";
            while (c.moveToNext()) {  
            	String fileUrl = c.getString(c.getColumnIndex("fileUrl"));
            	tid = c.getString(c.getColumnIndex("toid"));
            	fid = c.getString(c.getColumnIndex("mysendname")); 
//            	if(fileUrl != null && !fileUrl.equals(""))
//            	{
//            		fileUtil.deleteFile(fileUrl);
//            		String iconfileurl = fileUrl.replace("/1b/", "/1a/");
//            		fileUtil.deleteFile(iconfileurl);
//            	}
//            	String fileUrl2 = c.getString(c.getColumnIndex("fileUrl2"));
//            	if(fileUrl2 != null && !fileUrl2.equals(""))
//            	{
//            		fileUtil.deleteFile(fileUrl2);
//            		String iconfileurl = fileUrl2.replace("/1b/", "/1a/");
//            		fileUtil.deleteFile(iconfileurl);
//            	}
//            	String storeimgpath = c.getString(c.getColumnIndex("storeimgpath"));
//            	if(storeimgpath != null && !storeimgpath.equals(""))
//            	{
//            		fileUtil.deleteFile(storeimgpath);
//            	}
            }
            c.close();
            
//            String sql2 = "SELECT * FROM messageCardList WHERE mid = ?  and loginname = ?";
//    		Cursor c2 = db.rawQuery(sql2, new String[]{mid,myapp.getMyaccount()});  
//            while (c2.moveToNext()) {  
//            	String imgpath = c2.getString(c2.getColumnIndex("imgpath"));
//            	if(imgpath != null && !imgpath.equals(""))
//            	{
//            		fileUtil.deleteFile(imgpath);
//            	}
//            }
//            c2.close();
            
            String storeid = "";
            String nameid = "";
            if(tid.equals(myapp.getPfprofileId()))
            	storeid = fid;
            else
            	storeid = tid;
            nameid = myapp.getPfprofileId();
            
          //删除数据  
            db.delete("messagelist", "mid = ? and loginname = ?", new String[]{mid,myapp.getMyaccount()});
            
            boolean b = false;
            String sql3 = "SELECT * FROM messagelist WHERE (toid = ? and mysendname = ?) or (toid = ? and mysendname = ?)  and loginname = ? ";
    		Cursor c3 = db.rawQuery(sql3, new String[]{storeid,nameid,nameid,storeid,myapp.getMyaccount()});  
            while (c3.moveToNext()) {  
            	b = true;
            }
            c3.close();
            
            if(!b)
            {
            	db.delete("messagenewlist", "loginname = ? and storeid = ?", new String[]{myapp.getMyaccount(),storeid});
            }
            
//            db.delete("messageCard", "mid = ?", new String[]{mid});
            db.delete("messageCardList", "mid = ? and loginname = ?", new String[]{mid,myapp.getMyaccount()});
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized static void deleteMessageDataAll()
    {
    	try{
    		//删除数据  
            db.delete("messagelist", "loginname = ?", new String[]{myapp.getMyaccount()});
//            db.delete("messageCard", "mid = ?", new String[]{mid});
            db.delete("messageCardList","loginname = ?", new String[]{myapp.getMyaccount()});
            
            db.delete("messagenewlist", "loginname = ?", new String[]{myapp.getMyaccount()});
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized static void deletetNewMessageData(String storeid,String nameid)
    {
    	db.delete("messagenewlist", "loginname = ? and storeid = ?", new String[]{myapp.getMyaccount(),storeid});
    	
    	String sql = "SELECT * FROM messagelist WHERE (toid = ? and mysendname = ?) or (toid = ? and mysendname = ?)  and loginname = ? ";
		Cursor c = db.rawQuery(sql, new String[]{storeid,nameid,nameid,storeid,myapp.getMyaccount()});  
        while (c.moveToNext()) {  
        	String mid = c.getString(c.getColumnIndex("mid"));
//        	String fileUrl = c.getString(c.getColumnIndex("fileUrl"));
//        	if(fileUrl != null && !fileUrl.equals(""))
//        		fileUtil.deleteFile(fileUrl);
//        	String fileUrl2 = c.getString(c.getColumnIndex("fileUrl2"));
//        	if(fileUrl2 != null && !fileUrl2.equals(""))
//        		fileUtil.deleteFile(fileUrl2);
//        	
//        	String sql2 = "SELECT * FROM messageCardList WHERE mid = ?  and loginname = ?";
//    		Cursor c2 = db.rawQuery(sql2, new String[]{mid,myapp.getMyaccount()});  
//            while (c2.moveToNext()) {  
//            	String imgpath = c2.getString(c2.getColumnIndex("imgpath"));
//            	if(imgpath != null && !imgpath.equals(""))
//            	{
//            		fileUtil.deleteFile(imgpath);
//            	}
//            }
//            c2.close();
        	db.delete("messageCardList", "mid = ? and loginname = ?", new String[]{mid,myapp.getMyaccount()});
        }
    	
    	db.delete("messagelist", "(toid = ? and mysendname = ?) or (toid = ? and mysendname = ?)  and loginname = ?", new String[]{storeid,nameid,nameid,storeid,myapp.getMyaccount()});
    	c.close();
    }
    
    
    public synchronized void deleteStroeInfo(String storeid,String nameid)
    {
    	db.delete("storeinfo", "loginname = ? and storeid = ?", new String[]{myapp.getMyaccount(),storeid});
    	
    	deletetNewMessageData(storeid,nameid);
    }
    
    public synchronized void deleteStroeInfoAll()
    {
    	db.delete("storeinfo", "loginname = ?", new String[]{myapp.getMyaccount()});
    }
    
    public synchronized static void deleteFriendData(String pkid)
    {
    	try{
    		String sql = "SELECT * FROM friendlist WHERE pkid = ?  and loginname = ? ";
    		Cursor c = db.rawQuery(sql, new String[]{pkid,myapp.getMyaccount()});  
            while (c.moveToNext()) {  
            	String fileUrl = c.getString(c.getColumnIndex("userimg"));
            	if(fileUrl != null && !fileUrl.equals(""))
            	{
            		fileUtil.deleteFile(fileUrl);
            		String iconfileurl = fileUrl.replace("/1b/", "/1a/");
            		fileUtil.deleteFile(iconfileurl);
            	}
            }
            c.close();
            
    		//删除数据  
            db.delete("friendlist", "pkid = ? and loginname = ?", new String[]{pkid,myapp.getMyaccount()});
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public synchronized List<Map<String,Object>> getMessageData(String tonameid,String fromnameid,int page)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		int pageNo = page * 20;
    		String sql = "SELECT * FROM messagelist WHERE (toid = ? and mysendname = ?) or (toid = ? and mysendname = ?)  and loginname = ? order by datetime(mysendtime) desc limit "+pageNo+",20";
    		Cursor c = db.rawQuery(sql, new String[]{tonameid,fromnameid,fromnameid,tonameid,myapp.getMyaccount()});  
            while (c.moveToNext()) {  
                String mid = c.getString(c.getColumnIndex("mid"));
                String toid = c.getString(c.getColumnIndex("toid"));
                String mysendname = c.getString(c.getColumnIndex("mysendname"));
                String fname = c.getString(c.getColumnIndex("fname"));
                String tname = c.getString(c.getColumnIndex("tname"));
                String toname = c.getString(c.getColumnIndex("toname"));
                String mymessagecontent = c.getString(c.getColumnIndex("mymessagecontent"));
                String mysendtime = c.getString(c.getColumnIndex("mysendtime"));
                String fileUrl = c.getString(c.getColumnIndex("fileUrl"));
                String fileUrl2 = c.getString(c.getColumnIndex("fileUrl2"));
                String fileType = c.getString(c.getColumnIndex("fileType"));
                String fileType2 = c.getString(c.getColumnIndex("fileType2"));
                String fileName = c.getString(c.getColumnIndex("fileName"));
                String fileName2 = c.getString(c.getColumnIndex("fileName2"));
                String time = c.getString(c.getColumnIndex("time"));
                String timetext = c.getString(c.getColumnIndex("timetext"));
                String messagetype = c.getString(c.getColumnIndex("messagetype"));
                String sendimg = c.getString(c.getColumnIndex("sendimg"));
                String sendprogress = c.getString(c.getColumnIndex("sendprogress"));
                String messagestart = c.getString(c.getColumnIndex("messagestart"));
//                byte[] ins = c.getBlob(c.getColumnIndex("image"));
//                Bitmap bmpimg = null;
//                if(ins != null)
//                {
//                	bmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                	if(bmpimg != null)
//                		bmpimg = Bitmap.createScaledBitmap(bmpimg,100,100,true);
//                }
                if(fileUrl != null && !fileUrl.equals(""))
                {
//	                String iconfileUrl = fileUrl.replace("/1b/", "/1a/");
//	                bmpimg = getLoacalBitmap(iconfileUrl);
                	fileUrl = fileUrl.replace("/1b/", "/1a/");
                }
                
				String storename = c.getString(c.getColumnIndex("storename"));
				String storeDoc = c.getString(c.getColumnIndex("storeDoc"));
				String url = c.getString(c.getColumnIndex("url"));
				String storeimgpath = c.getString(c.getColumnIndex("storeimgpath"));
//				byte[] ins2 = c.getBlob(c.getColumnIndex("storeimg"));
//				Bitmap bmpsimg = null;
//				if(ins2 != null)
//				{
//					bmpsimg = BitmapFactory.decodeByteArray(ins2,0,ins2.length);
//					if(bmpsimg != null)
//					{
//	//					bmpsimg = Bitmap.createScaledBitmap(bmpsimg,250,120,true);
//						int width = bmpsimg.getWidth();
//						int height = bmpsimg.getHeight();
//						int newwidth = 0;
//						int newheight = 140;
//						if(height > newheight || height < newheight)
//						{
//							BigDecimal b = new BigDecimal((float)height / (float)width);  
//							float bili = b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();;
//							newwidth = (int)(newheight/bili);
//							if(newwidth >= 300)
//								bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
//							else
//							{
//								newwidth = 300;
//								BigDecimal wb = new BigDecimal((float)width / (float)height);
//								float wbili = wb.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
//								newheight = (int)(newwidth/wbili);
//								bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
//							}
//						}
//						
//						int mScreenWidth = (bmpsimg.getWidth() - 300) / 2;
//						int mScreenHeight = (bmpsimg.getHeight() - 140) / 2;
//						if(mScreenWidth < 0)
//							mScreenWidth = 0;
//						if(mScreenHeight < 0)
//							mScreenHeight = 0;
//						bmpsimg = Bitmap.createBitmap(bmpsimg, mScreenWidth, mScreenHeight, 300, 140);
//					}
//				}
//				Bitmap bmpsimg = null;
//				if(storeimgpath != null && !storeimgpath.equals(""))
//				{
//					bmpsimg = getLoacalBitmap(storeimgpath);
//				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("mid", mid);
				map.put("toid", toid);
				map.put("mysendname", mysendname);
				if(fromnameid.equals(mysendname))
					map.put("userimg", myapp.getUserimgbitmap());
				else
				{
					if(messagetype.equals("group"))
					{
						if(storeimgpath != null)
						{
							Bitmap userimg = getLoacalBitmap(storeimgpath);
							map.put("userimg", userimg);
						}
						else
						{
							map.put("userimg", null);
						}
					}
					else
					{
//						if(fromnameid.equals(mysendname))
//							map.put("userimg", myapp.getUserimgbitmap());
//						else
							map.put("userimg", myapp.getStoreimgbitmap());
					}
				}
				map.put("fname", fname);
				map.put("tname", tname);
				map.put("yiman", R.drawable.yi_man);
				map.put("mymessagecontent", mymessagecontent);
				map.put("mysendtime",mysendtime);
//				if(bmpimg != null)
//					map.put("fileUrl",bmpimg);
//				else
					map.put("fileUrl",fileUrl);
				map.put("fileUrl2",fileUrl2);
				map.put("toname", toname);
				map.put("fileType", fileType);
				map.put("fileType2", fileType2);
				map.put("fileName", fileName);
				map.put("fileName2", fileName2);
				map.put("time", time);
				map.put("timetext", timetext);
				map.put("messagetype", messagetype);
				map.put("sendimg", sendimg);
				map.put("sendprogress", sendprogress);
//				map.put("storeimg", bmpsimg);
				map.put("storeimg", storeimgpath);
				map.put("storename", storename);
				map.put("storeDoc", storeDoc);
				map.put("url", url);
				map.put("messagestart", messagestart);
//				Map<String,Object> cardmap = getMessageCardData(mid);
//				if(cardmap != null)
//				{
//					
//				}
				List<Map<String,Object>> cardlist = getMessageCardListData(mid);
				map.put("storelist",cardlist);
				
				dlist.add(0,map);
            }
            
            if(dlist.size() > 0)
            {
	            ContentValues messagecv = new ContentValues();  
	            messagecv.put("isRead", "0");  
	    		db.update("messagelist", messagecv, "(toid = ? and mysendname = ?) or (toid = ? and mysendname = ?)  and loginname = ?", new String[] { tonameid,fromnameid,fromnameid,tonameid,myapp.getMyaccount() });
	    		
	    		ContentValues newmessagecv = new ContentValues();  
	    		newmessagecv.put("newNumber", 0);  
	    		db.update("messagenewlist", newmessagecv, "storeid = ? and loginname = ?", new String[] { tonameid,myapp.getMyaccount() });
            }
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
    
//    public static Map<String,Object> getMessageCardData(String mid)
//    {
//    	Map<String,Object> map = null;
//    	try{
//    		String sql = "SELECT * FROM messageCard WHERE mid = ?";
//    		Cursor c = db.rawQuery(sql, new String[]{mid});  
//            while (c.moveToNext()) {  
//                String storename = c.getString(c.getColumnIndex("storename"));
//                String storeDoc = c.getString(c.getColumnIndex("storeDoc"));
//                byte[] ins = c.getBlob(c.getColumnIndex("storeimg"));
//				Bitmap bmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
//				
//				map = new HashMap<String,Object>();
//				map.put("storeimg", bmpimg);
//				map.put("storename", storename);
//				map.put("storeDoc", storeDoc);
//            }  
//            c.close();
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//    	return map;
//    }
    
    public synchronized List<Map<String,Object>> getMessageCardListData(String mid)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		String sql = "SELECT * FROM messageCardList WHERE mid = ? and loginname = ?";
    		Cursor c = db.rawQuery(sql, new String[]{mid,myapp.getMyaccount()});
    		int i = 0;
            while (c.moveToNext()) { 
                String title = c.getString(c.getColumnIndex("title"));
                String desc = c.getString(c.getColumnIndex("desc"));
                String price = c.getString(c.getColumnIndex("price"));
                String pkid = c.getString(c.getColumnIndex("pkid"));
                String imgpath = c.getString(c.getColumnIndex("imgpath"));
//                byte[] ins = c.getBlob(c.getColumnIndex("sysImg"));
//                Bitmap bmpimg = null;
//                if(ins != null)
//                {
//                	bmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                	if(bmpimg != null)
//                		bmpimg = Bitmap.createScaledBitmap(bmpimg,80,80,true);
//                }
//                Bitmap bmpimg = null;
//                if(imgpath != null && !imgpath.equals(""))
//                {
//                	bmpimg = getLoacalBitmap(imgpath);
//                }
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("sysImg", imgpath);
				map.put("title", title);
				map.put("desc", desc);
				map.put("price", price);
				map.put("pkid", pkid);
				
				dlist.add(map);
				i++;
            }
            if(i == 0)
            	dlist = null;
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
    
    //0表示在信息列表里加载数据，1表示在服务栏里加载数据,2表示加载所有数据
    public synchronized Map<String,Object> getNewMessageData(String tag)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	Map<String,Object> dmap = new HashMap<String,Object>();
    	try{
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String sql = "";
    		if(myapp.getIsServer() || tag.equals("2"))
    		{
    			sql = "SELECT * FROM messagenewlist WHERE loginname = ? order by datetime(senddatetime) desc";
    		}
    		else
    		{
    			if(tag.equals("0"))
    				sql = "SELECT * FROM messagenewlist WHERE loginname = ? and (typesMapping = 'friend' or typesMapping = 'group' or typesMapping = 'forward')  order by datetime(senddatetime) desc";
    			else
    				sql = "SELECT * FROM messagenewlist WHERE loginname = ? and typesMapping != 'friend' and typesMapping != 'group' and typesMapping != 'forward' order by datetime(senddatetime) desc";
    		}
    		if(db == null)
    			openDB();
    		Cursor c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});  
    		int i = 0;
    		int zonshu = 0;
            while (c.moveToNext()) {  
            	String storeid = c.getString(c.getColumnIndex("storeid"));
                String storename = c.getString(c.getColumnIndex("storename"));
                String newmessage = c.getString(c.getColumnIndex("newmessage"));
                String sendtime = c.getString(c.getColumnIndex("sendtime"));
                String typesMapping = c.getString(c.getColumnIndex("typesMapping"));
                String nameid = c.getString(c.getColumnIndex("nameid"));
                int newNumber = c.getInt(c.getColumnIndex("newNumber"));
                String storeimg = c.getString(c.getColumnIndex("storeimg"));
                String isTop = c.getString(c.getColumnIndex("isTop"));
                String serviceid = c.getString(c.getColumnIndex("serviceid"));
                if(serviceid == null)
                	serviceid = "";
                String servicename = c.getString(c.getColumnIndex("servicename"));
                if(servicename == null)
                	servicename = "";
                String watag = c.getString(c.getColumnIndex("watag"));
                if(watag == null)
                	watag = "";
//                byte[] ins = c.getBlob(c.getColumnIndex("storeimg"));
//                Bitmap bmpimg = null;
//                if(ins != null)
//                {
//                	bmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                	if(bmpimg != null)
//                		bmpimg = Bitmap.createScaledBitmap(bmpimg,80,80,true);
//                }
				
				Map<String,Object> map = new HashMap<String,Object>();
//				map.put("imgurl", bmpimg);
				if(storeimg == null || storeimg.equals(""))
				{
					String imgpath = getStoreImagePath(myapp.getAppstoreid());
        			if(imgpath == null)
        				map.put("imgurl", "");
        			else
        			{
        				ContentValues cv = new ContentValues();
    					cv.put("storeimg", imgpath);
    					map.put("imgurl", imgpath);
    					db.update("messagenewlist", cv, "storeid = ? and loginname = ?", new String[] { storeid,myapp.getMyaccount() });
        			}
				}
				else
				{
					map.put("imgurl", storeimg);
				}
				map.put("storeName", storename);
				map.put("serviceid", serviceid);
				map.put("servicename", servicename);
				map.put("lastmessagetime", sendtime);
				map.put("lastmessage", newmessage);
				map.put("storeid", storeid);
				map.put("typesMapping", typesMapping);
				map.put("nameid", nameid);
				map.put("watag", watag);
//				if(newNumber == 0)
//					map.put("newNumber", 0);
//				else
					map.put("newNumber", newNumber);
				
				zonshu = zonshu + newNumber;
				if(isTop != null && isTop.equals("0"))
					dlist.add(0,map);
				else
					dlist.add(map);
				i++;
            }
            dmap.put("zhonshu", zonshu);
            dmap.put("dlist", dlist);
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dmap;
    }
    
    public synchronized static List<Map<String,Object>> getStoreInfoAllData(String key)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		String sql = "";
    		Cursor c = null;
    		if(key != null && !key.equals(""))
    		{
    			sql = "SELECT * FROM storeinfo WHERE loginname = ? and storeName like '"+key+"%' and businessId = '"+myapp.getBusinessid()+"' order by storeNo COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
    		else
    		{
    			sql = "SELECT * FROM storeinfo WHERE loginname = ? and businessId = '"+myapp.getBusinessid()+"' order by storeNo COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
            while (c.moveToNext()) {  
                String storeid = c.getString(c.getColumnIndex("storeid"));
                int points = c.getInt(c.getColumnIndex("points"));
                String nameOnCard = c.getString(c.getColumnIndex("nameOnCard"));
                String nameid = c.getString(c.getColumnIndex("nameid"));
                String pfids = c.getString(c.getColumnIndex("pfids"));
                String cardNo = c.getString(c.getColumnIndex("cardNo"));
                String joinedDate = c.getString(c.getColumnIndex("joinedDate"));
                String mdmType = c.getString(c.getColumnIndex("mdmType"));
                String mdmLevel = c.getString(c.getColumnIndex("mdmLevel"));
                String mdmstatus = c.getString(c.getColumnIndex("mdmstatus"));
                String expDate = c.getString(c.getColumnIndex("expDate"));
                String chainCode = c.getString(c.getColumnIndex("chainCode"));
                String storeName = c.getString(c.getColumnIndex("storeName"));
                String sortName = c.getString(c.getColumnIndex("sortName"));
                String sortPinyin = c.getString(c.getColumnIndex("sortPinyin"));
                String username = c.getString(c.getColumnIndex("username"));
                String password = c.getString(c.getColumnIndex("password"));
                String pkid = c.getString(c.getColumnIndex("pkid"));
                String storePhone = c.getString(c.getColumnIndex("storePhone"));
                String addressInfomation = c.getString(c.getColumnIndex("addressInfomation"));
                String storeDesc = c.getString(c.getColumnIndex("storeDesc"));
                String isASttention = c.getString(c.getColumnIndex("isASttention"));
                if(isASttention.equals(""))
                	isASttention = "1";
                int xinxin = c.getInt(c.getColumnIndex("xinxin"));
                String couponNumber = c.getString(c.getColumnIndex("couponNumber"));
                String typeName = c.getString(c.getColumnIndex("typeName"));
                String typesMapping = c.getString(c.getColumnIndex("typesMapping"));
                String businessId = c.getString(c.getColumnIndex("businessId"));
                String woof = c.getString(c.getColumnIndex("woof"));
                String longItude = c.getString(c.getColumnIndex("longItude"));
                String isLu = c.getString(c.getColumnIndex("isLu"));
                String storeType = c.getString(c.getColumnIndex("storeType"));
                String province = c.getString(c.getColumnIndex("province"));
                String roomIntroduction = c.getString(c.getColumnIndex("roomIntroduction"));
                String periphery = c.getString(c.getColumnIndex("periphery"));
                String trafficWay = c.getString(c.getColumnIndex("trafficWay"));
                String startingPrice = c.getString(c.getColumnIndex("startingPrice"));
                String score = c.getString(c.getColumnIndex("score"));
                String comments = c.getString(c.getColumnIndex("comments"));
                String lastmessage = c.getString(c.getColumnIndex("lastmessage"));
                String lastmessagetime = c.getString(c.getColumnIndex("lastmessagetime"));
                String loginname = c.getString(c.getColumnIndex("loginname"));
                String imgurl = c.getString(c.getColumnIndex("imgurl"));
                String customizeMenu = c.getString(c.getColumnIndex("customizeMenu"));
                String linkurl = c.getString(c.getColumnIndex("linkurl"));
                String servicetype = c.getString(c.getColumnIndex("servicetype"));
                String storeNo = c.getString(c.getColumnIndex("storeNo"));
                String profileid = c.getString(c.getColumnIndex("profileid"));
//                byte[] ins = c.getBlob(c.getColumnIndex("imgurl"));
//                Bitmap bmpimg = null;
//                Bitmap yubmpimg = null;
//                if(ins != null)
//                {
//                	yubmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                	if(yubmpimg != null)
//                		bmpimg = Bitmap.createScaledBitmap(yubmpimg,100,100,true);
//                }
                
                
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("storeid", storeid);
				map.put("storeNo", storeNo);
				map.put("points", points);
				map.put("nameOnCard", nameOnCard);
				map.put("nameid", nameid);
				map.put("pfids", pfids);
				map.put("cardNo", cardNo);
				map.put("joinedDate", joinedDate);
				map.put("mdmType",mdmType);
				map.put("mdmLevel",mdmLevel);
				map.put("mdmstatus", mdmstatus);
				map.put("expDate", expDate);
				map.put("chainCode", chainCode);
				map.put("storeName", storeName);
				map.put("linkurl", linkurl);
				map.put("servicetype", servicetype);
				if(isASttention.equals("0"))
					map.put("sortName", "1"+sortName);
				else
					map.put("sortName", sortName);
				map.put("sortPinyin", sortPinyin);
//				map.put("sortName", sortName);
				map.put("username", username);
				map.put("password", password);
//				map.put("imgurl", bmpimg);
//				map.put("imgbitmap", yubmpimg);
				map.put("imgurl", imgurl);
				map.put("imgbitmap", imgurl);
				map.put("pkid", pkid);
				map.put("storePhone", storePhone);
				map.put("addressInfomation", addressInfomation);
				map.put("storeDesc", storeDesc);
				map.put("isASttention", isASttention);
				map.put("xinxin", xinxin);
				map.put("couponNumber",couponNumber);
				map.put("typeName",typeName);
				map.put("typesMapping",typesMapping);
				map.put("businessId",businessId);
				map.put("woof",woof);
				map.put("longItude",longItude);
				map.put("isLu",isLu);
				map.put("storeType",storeType);
				map.put("province",province);
				map.put("roomIntroduction",roomIntroduction);
				map.put("periphery",periphery);
				map.put("trafficWay",trafficWay);
				map.put("startingPrice",startingPrice);
				map.put("score",score);
				map.put("comments",comments);
				map.put("lastmessage",lastmessage);
				map.put("lastmessagetime",lastmessagetime);
				map.put("loginname",loginname);
				map.put("customizeMenu", customizeMenu);
				map.put("newMessage", "0");
				map.put("profileid", profileid);
				
				if(isASttention.equals("0"))
					dlist.add(0,map);
				else
					dlist.add(map);
//				dlist.add(0,map);
				
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
    
    public synchronized static Map<String,Object> getStoreInfoAllDataKey()
    {
    	Map<String,Object> map = new HashMap<String,Object>();
    	try{
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM storeinfo WHERE loginname = ? and businessId = '"+myapp.getBusinessid()+"' order by storeNo COLLATE LOCALIZED";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
            while (c.moveToNext()) {  
                String storeid = c.getString(c.getColumnIndex("storeid"));
				
				map.put(storeid, storeid);
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return map;
    }
    
    public synchronized static List<Map<String,Object>> getFriendAllData(String key,String tag)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		String sql = "";
    		Cursor c = null;
    		String wherestr = "";
    		if(tag.equals("0"))//0为内部员工，1为外部用户好友
    		{
    			wherestr = " and companyid != '' ";
    		}
    		else
    		{
    			wherestr = " and companyid = '' ";
    		}
    		if(key != null && !key.equals(""))
    		{
    			sql = "SELECT * FROM friendlist WHERE loginname = ? and username like '"+key+"%' and start = '0' "+wherestr+" order by namePinyin COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
    		else
    		{
    			sql = "SELECT * FROM friendlist WHERE loginname = ? and start = '0' "+wherestr+" order by namePinyin COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
            while (c.moveToNext()) {  
                String pkid = c.getString(c.getColumnIndex("pkid"));
                String username = c.getString(c.getColumnIndex("username"));
                String pfid = c.getString(c.getColumnIndex("pfid"));
                String namePinyin = c.getString(c.getColumnIndex("namePinyin"));
                String loginname = c.getString(c.getColumnIndex("loginname"));
                String userimg = c.getString(c.getColumnIndex("userimg"));
                String account = c.getString(c.getColumnIndex("account"));
                String sex = c.getString(c.getColumnIndex("sex"));
                String area = c.getString(c.getColumnIndex("area"));
                String signature = c.getString(c.getColumnIndex("signature"));
                String companyid = c.getString(c.getColumnIndex("companyid"));
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("username", username);
				map.put("pfid", pfid);
				map.put("namePinyin", namePinyin);
				map.put("userimg", userimg);
				
				map.put("account", account);
				map.put("sex", sex);
				map.put("area", area);
				map.put("signature", signature);
				map.put("companyid", companyid);
				
				dlist.add(map);
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
    
    public synchronized static Map<String,Object> getFriendAllIdData(String key,String tag)
    {
    	Map<String,Object> dlist = new HashMap<String,Object>();
    	boolean b = false;
    	try{
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		String sql = "";
    		Cursor c = null;
    		String wherestr = "";
    		if(tag.equals("0"))//0为内部员工，1为外部用户好友
    		{
    			wherestr = " and companyid != '' ";
    		}
    		else
    		{
    			wherestr = " and companyid = '' ";
    		}
    		if(key != null && !key.equals(""))
    		{
    			sql = "SELECT * FROM friendlist WHERE loginname = ? and username like '"+key+"%' and start = '0' "+wherestr+" order by namePinyin COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
    		else
    		{
    			sql = "SELECT * FROM friendlist WHERE loginname = ? and start = '0' "+wherestr+" order by namePinyin COLLATE LOCALIZED";
    			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
    		}
            while (c.moveToNext()) {  
                String pkid = c.getString(c.getColumnIndex("pkid"));
                
                dlist.put(pkid, pkid);
                b = true;
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	if(b)
    		return dlist;
    	else
    		return null;
    }
    
    public synchronized static Map<String,Object> getFriendpfidData(String key)
    {
    	Map<String,Object> map = null;
    	try{
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM friendlist WHERE loginname = ? and pfid = '"+key+"' and start = '0' ";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
			
            while (c.moveToNext()) {  
                String pkid = c.getString(c.getColumnIndex("pkid"));
                String username = c.getString(c.getColumnIndex("username"));
                String pfid = c.getString(c.getColumnIndex("pfid"));
                String namePinyin = c.getString(c.getColumnIndex("namePinyin"));
                String loginname = c.getString(c.getColumnIndex("loginname"));
                String userimg = c.getString(c.getColumnIndex("userimg"));
                String account = c.getString(c.getColumnIndex("account"));
                String sex = c.getString(c.getColumnIndex("sex"));
                String area = c.getString(c.getColumnIndex("area"));
                String signature = c.getString(c.getColumnIndex("signature"));
				
                map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("username", username);
				map.put("pfid", pfid);
				map.put("namePinyin", namePinyin);
				map.put("userimg", userimg);
				
				map.put("account", account);
				map.put("sex", sex);
				map.put("area", area);
				map.put("signature", signature);
				
				break;
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return map;
    }
    
    public synchronized static List<Map<String,Object>> getMomentsAllData(int page, String type)
    {
    	List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
    	try{
    		int pageNo = page * 20;
    		String language = Locale.getDefault().getLanguage();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    		String sql = "";
    		Cursor c = null;
    		if(type != null && !type.equals(""))
    			sql = "SELECT * FROM moments WHERE loginname = ? and publishtype = '"+type+"' order by datetime(publishtime) desc limit "+pageNo+",20 ";
    		else
    			sql = "SELECT * FROM moments WHERE loginname = ? and publishtype = '' order by datetime(publishtime) desc limit "+pageNo+",20 ";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
			
            while (c.moveToNext()) {  
                String pkid = c.getString(c.getColumnIndex("pkid"));
                String content = c.getString(c.getColumnIndex("content"));
                String publishtime = c.getString(c.getColumnIndex("publishtime"));
                String userimg = c.getString(c.getColumnIndex("userimg"));
                String publishtype = c.getString(c.getColumnIndex("publishtype"));
                Date date1 = sf.parse(publishtime);
                String datestr = sf.format(new Date());
                String datestr2 = sf.format(date1);
                int dates = (datestr).compareTo(datestr2);
                if(dates == 0)
					publishtime = context.getString(R.string.fmt_pre_nowday);
				else
					publishtime = String.format(context.getString(R.string.fmt_indayh), dates);
                String publishuser = c.getString(c.getColumnIndex("publishuser"));
                String publishid = c.getString(c.getColumnIndex("publishid"));
                List<Map<String,String>> filelist = new ArrayList<Map<String,String>>();
                Cursor c2 = null;
        		sql = "SELECT * FROM momentsfiles WHERE loginname = ? and momentsid = ?";
    			c2 = db.rawQuery(sql, new String[]{myapp.getMyaccount(),pkid});
    			while (c2.moveToNext()) {  
    				String fileurl = c2.getString(c2.getColumnIndex("fileurl"));
                    String filetype = c2.getString(c2.getColumnIndex("filetype"));
                    String filename = c2.getString(c2.getColumnIndex("filename"));
                    String filewidth = c2.getString(c2.getColumnIndex("filewidth"));
                    String fileheight = c2.getString(c2.getColumnIndex("fileheight"));
                    
                    Map<String,String> filemap = new HashMap<String,String>();
                    filemap.put("filename", filename);
                    filemap.put("fileurl", fileurl);
                    filemap.put("filetype", filetype);
                    filemap.put("filewidth", filewidth);
                    filemap.put("fileheight", fileheight);
                    filelist.add(filemap);
    			}
    			c2.close();
				
    			Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("content", content);
				map.put("publishtime", publishtime);
				map.put("publishuser", publishuser);
				map.put("publishid", publishid);
				map.put("userimg", userimg);
				map.put("publishtype", publishtype);
				
				map.put("filelist", filelist);
				listmap.add(map);
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return listmap;
    }
    
    public synchronized static Bitmap getStoreImages(String key)
    {
    	Bitmap bmpimg = null;
    	try{
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM storeinfo WHERE loginname = ? and storeid = '"+key+"' ";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
            while (c.moveToNext()) {  
                byte[] ins = c.getBlob(c.getColumnIndex("imgurl"));
                Bitmap yubmpimg = null;
                if(ins != null)
                {
                	yubmpimg = BitmapFactory.decodeByteArray(ins,0,ins.length);
                	if(yubmpimg != null)
                		bmpimg = Bitmap.createScaledBitmap(yubmpimg,100,100,true);
                }
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return bmpimg;
    }
    
    public synchronized static String getStoreImagePath(String key)
    {
    	String imgpath = null;
    	try{
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM storeinfo WHERE loginname = ? and storeid = '"+key+"' ";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
            while (c.moveToNext()) {  
            	imgpath = c.getString(c.getColumnIndex("imgurl"));
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return imgpath;
    }
    
    public synchronized static String getFriendImagePath(String key)
    {
    	String imgpath = null;
    	try{
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM friendlist WHERE loginname = ? and pfid = '"+key+"' ";
			c = db.rawQuery(sql, new String[]{myapp.getMyaccount()});
            while (c.moveToNext()) {  
            	imgpath = c.getString(c.getColumnIndex("userimg"));
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return imgpath;
    }
    
    public synchronized Bitmap getImageBitmap(String mid)
    {
    	Bitmap bitmap = null;
    	try{
    		String sql = "SELECT * FROM messagelist WHERE mid = ?";
    		Cursor c = db.rawQuery(sql, new String[]{mid});  
            while (c.moveToNext()) {
            	String fileUrl = c.getString(c.getColumnIndex("fileUrl"));
            	bitmap = getLoacalBitmap(fileUrl);
//            	byte[] ins = c.getBlob(c.getColumnIndex("image"));
//                if(ins != null)
//                {
//                	bitmap = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                }
            }
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return bitmap;
    }
    
    public synchronized String selectNewMessageIsTop(String storeid)
    {
    	String istop = "";
    	try{
    		String sql = "";
    		Cursor c = null;
    		sql = "SELECT * FROM messagenewlist WHERE storeid = ? and loginname = ? ";
			c = db.rawQuery(sql, new String[]{storeid,myapp.getMyaccount()});
            while (c.moveToNext()) {  
            	istop = c.getString(c.getColumnIndex("isTop"));
            }  
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return istop;
    }
    
    public synchronized String getImageFilePath(String mid)
    {
    	String path = "";
    	try{
    		String sql = "SELECT * FROM messagelist WHERE mid = ?";
    		Cursor c = db.rawQuery(sql, new String[]{mid});  
            while (c.moveToNext()) {
            	String fileUrl = c.getString(c.getColumnIndex("fileUrl"));
            	path = fileUrl.substring(0,fileUrl.lastIndexOf("/"));
//            	byte[] ins = c.getBlob(c.getColumnIndex("image"));
//                if(ins != null)
//                {
//                	bitmap = BitmapFactory.decodeByteArray(ins,0,ins.length);
//                }
            }
            c.close();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return path;
    }
    
    public synchronized static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			System.out.println("ssssssssurl===="+url);
			if(url == null || url.equals(""))
				return null;
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
		
//			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
    
    public synchronized Bitmap getGossipImage(String httpUrl){      
        Bitmap bitmap = null;            
        HttpGet httpRequest = new HttpGet(httpUrl);    
        //取得HttpClient 对象    
        HttpClient httpclient = new DefaultHttpClient();    
        try {    
            //请求httpClient ，取得HttpRestponse    
            HttpResponse httpResponse = httpclient.execute(httpRequest);    
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){    
                //取得相关信息 取得HttpEntiy    
                HttpEntity httpEntity = httpResponse.getEntity();    
                InputStream is = httpEntity.getContent();
                bitmap = BitmapFactory.decodeStream(is);    
                is.close();     
            }else{    
                 Toast.makeText(context, "连接失败!", Toast.LENGTH_SHORT).show(); 
            }     
                
        } catch (ClientProtocolException e) {    
            e.printStackTrace();    
        } catch (IOException e) {     
            e.printStackTrace();    
        }      
        return bitmap;  
    }  
    
    /**
	* 转换本地图片为bitmap
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public synchronized Bitmap getLoacalBitmap(String url) {
	     try {
			FileInputStream fis = new FileInputStream(url);
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
		
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(fis,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,true);
			return bitmap;
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
    
    public synchronized static String getCombID() {
		UUID temp = UUID.randomUUID();
		String s = temp.toString();
		long date = new Date().getTime();

		return s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23)
				+ Long.toString(date, 36);

	}
    
    // 缩放图片
    public synchronized static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
       // 获得图片的宽高
       int width = bm.getWidth();
       int height = bm.getHeight();
       // 计算缩放比例
       float scaleWidth = ((float) newWidth) / width;
       float scaleHeight = ((float) newHeight) / height;
       // 取得想要缩放的matrix参数
       Matrix matrix = new Matrix();
       matrix.postScale(scaleWidth, scaleHeight);
       // 得到新的图片
       Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    } 
    
    /**
     * 昵称
     */
//    private static String[] nicks = {"阿雅","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
    /**  
     * 汉字转换位汉语拼音首字母，英文字符不变  
     * @param chines 汉字  
     * @return 拼音  
     */     
    public synchronized static String converterToFirstSpell(String chines){
         String pinyinName = "";      
         char[] nameChar = chines.toCharArray();      
         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
         defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
        for (int i = 0; i < nameChar.length; i++) {      
            if (nameChar[i] > 128) {      
                try {      
                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
                 } catch (BadHanyuPinyinOutputFormatCombination e) {      
                     e.printStackTrace();      
                 }      
             }else{      
                 pinyinName += nameChar[i];      
             }      
         }      
        return pinyinName;      
     }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}  

}
