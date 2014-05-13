package ms.globalclass.dbhelp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Date;

import ms.globalclass.map.MyApp;
import ms.activitys.R;


import android.content.ContentValues;
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.SyncStateContract.Constants;
import android.util.Log;  
import android.widget.Toast;

public class DBHelperCard {
	private static final String TAG = "UserDB_DBHelper.java";  
	    private static final String DataBaseName = "UserDB";  
	    SQLiteDatabase db;  
	    Context context;
	    private MyApp myapp;
	  
	    public DBHelperCard(Context context,MyApp myp) {
	    	if(db != null)
	    	{
	    		db.close();
	    		db = null;
	    	}
	        this.open(context);  
	        this.myapp = myp;
	    }  
	  
	    private void createTabel() {  
	        // TODO Auto-generated method stub  
	        String sql = "";  
	        try {  
	            sql = "CREATE TABLE IF NOT EXISTS MyCard    (ID TEXT PRIMARY KEY,  points INT,  nameOnCard TEXT, nameid TEXT, pfids TEXT, cardNo TEXT, joinedDate TEXT, mdmType TEXT, mdmLevel TEXT, mdmstatus TEXT, expDate TEXT, chainCode TEXT, storeid TEXT, storeName TEXT, img BLOB, isASttention TEXT, pkid TEXT, couponNumber TEXT, storePhone TEXT,addressInfomation TEXT, storeDesc TEXT,tiaoimg BLOB)";  
	            this.db.execSQL(sql);  
	            
	            Log.v(TAG, "Create Table MyCard ok");
	            System.out.println("Create Table MyCard ok");
//	            db.close();
	        } catch (Exception e) {  
	            Log.v(TAG, "Create Table MyCard fail");  
	        } finally {  
	            //this.db.close();  
	            Log.v(TAG, "Create Table MyCard ");  
	        }  
	    }  

	    public boolean saveMyCard(List<Map<String,Object>> dlist)
	    {
	    	boolean a = true;
	    	try{
	    		delColumnAll();
	    		if(!db.isOpen())
		    		open2(context);
	    		
				db.beginTransaction();  //手动设置开始事务
				for(int i=0;i<dlist.size();i++)
				{
					Map map = dlist.get(i);
					ContentValues cv=new ContentValues();
					String tableid = getTableId();
					cv.put("ID", tableid); 
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
					cv.put("storeid", (String)map.get("storeid")); 
					cv.put("storeName", (String)map.get("storeName"));
					String imgurl = (String)map.get("imgurl");
					Bitmap bitm = getImageBitmap(imgurl);
//					Bitmap bitm = (Bitmap)map.get("img2");
					if(bitm != null)
					{
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						bitm.compress(Bitmap.CompressFormat.PNG, 100, os); 
						cv.put("img", os.toByteArray()); 
					}
					else
						cv.put("img", ""); 
					cv.put("isASttention", (String)map.get("isASttention")); 
					cv.put("pkid", (String)map.get("pkid")); 
					cv.put("couponNumber", (String)map.get("couponNumber")); 
					cv.put("storePhone", (String)map.get("storePhone")); 
					cv.put("addressInfomation", (String)map.get("addressInfomation")); 
					cv.put("storeDesc", (String)map.get("storeDesc")); 
					Bitmap bitmTiao = getImageBitmap("http://"+myapp.getHost()+":80/customize/control/BarcodeServlet?msg="+(String)map.get("cardNo"));
					ByteArrayOutputStream oss = new ByteArrayOutputStream();
					bitmTiao.compress(Bitmap.CompressFormat.PNG, 100, oss);
					cv.put("tiaoimg", oss.toByteArray()); 
					
					db.insert("MyCard",null, cv);
				}
				db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交

				db.endTransaction(); //处理完成 
				
				db.close();
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    		db.close();
	    		a = false;
	    	}
	    	return a;
	    }
	    
	    public List<Map<String,Object>> loadMyCardAll() {
	    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
	    		
		        Cursor cur = db.query("MyCard", new String[] { "points","nameOnCard", "nameid", "pfids", "cardNo", "joinedDate", "mdmType", "mdmLevel", "mdmstatus", "expDate", "chainCode", "storeid", "storeName", "img", "isASttention", "pkid", "couponNumber", "storePhone", "addressInfomation", "storeDesc", "tiaoimg"}, null,  
		                null, null, null, null);  
		        if(cur.getCount() > 0)
		    	{
		    		cur.moveToFirst(); 
		    	    while (!cur.isAfterLast()) { 
		    	    	Map<String,Object> map = new HashMap<String,Object>();
		    	    	map.put("points", cur.getInt(0));
						map.put("nameOnCard", cur.getString(1));
						map.put("nameid", cur.getString(2));
						map.put("pfids", cur.getString(3));
						map.put("cardNo", cur.getString(4));
						map.put("joinedDate", cur.getString(5));
						map.put("mdmType", cur.getString(6));
						map.put("mdmLevel", cur.getString(7));
						map.put("mdmstatus", cur.getString(8));
						map.put("expDate", cur.getString(9));
						map.put("chainCode", cur.getString(10));
						map.put("storeid", cur.getString(11));
						map.put("storeName", cur.getString(12));
						map.put("img", null);
						byte[] in=cur.getBlob(cur.getColumnIndex("img"));
						if(in != null)
						{
							Bitmap bmpout = BitmapFactory.decodeByteArray(in,0,in.length);
							map.put("img2", bmpout);
							map.put("imgurl", bmpout);
						}
						else
						{
							map.put("img2", null);
							map.put("imgurl", null);
						}
						map.put("pkid", cur.getString(15));
						map.put("storePhone", cur.getString(17));
						map.put("addressInfomation", cur.getString(18));
						map.put("storeDesc", cur.getString(19));
						map.put("isASttention", cur.getString(14));
						if(cur.getString(14).equals("0"))
							map.put("xinxin", R.drawable.ic_star_small);
						else
							map.put("xinxin", null);
						map.put("couponNumber", cur.getString(16));
						byte[] ins=cur.getBlob(cur.getColumnIndex("tiaoimg"));
						Bitmap bmptiao = BitmapFactory.decodeByteArray(ins,0,ins.length);
						map.put("tiaoimg", bmptiao); 
			
						dlist.add(map); 
		    	        
		    	        // do something useful with these 
		    	        cur.moveToNext(); 
		    	      } 
		    	    db.close();
		    	    cur.close();
		    	}
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}
	        return dlist;  
	    }
	    
	    public void delColumnAll() {
	    	if(!db.isOpen())
	    		open2(context);
	    	
	        int rownumber = db.delete("MyCard", null, null);
	        System.out.println("delMycardTable======"+rownumber);
	        db.close();
	    }
	    
	    public static String getTableId() {
			UUID temp = UUID.randomUUID();
			String s = temp.toString();
			long date = new Date().getTime();

			return s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23)
					+ Long.toString(date, 36);

		}
	    
	   public void open(Context context){  
	       if (null == db || !this.db.isOpen()){
		           this.context = context; 
		            this.db = context.openOrCreateDatabase(this.DataBaseName,  
		                    context.MODE_PRIVATE, null);  
		            createTabel();  
		            Log.v(this.TAG, "create  or Open DataBase。。。");  
	       }  
	   } 
	   
	   public void open2(Context context){  
	       if (null == db || !this.db.isOpen()){
		           this.context = context; 
		            this.db = context.openOrCreateDatabase(this.DataBaseName,  
		                    context.MODE_PRIVATE, null);  
		            Log.v(this.TAG, "create  or Open DataBase。。。");  
	       }  
	   } 
	   
	   public Bitmap getImageBitmap(String value)
		{
		   if(value == null && value.equals(""))
			   return null;
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
//			    opt.inSampleSize = 8;
//				bitmap = BitmapFactory.decodeStream(is);
				bitmap = BitmapFactory.decodeStream(is, null, opt);
//				bitmap = Bitmap.createScaledBitmap(bitmap,300,188,true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return bitmap;
		}
	   
	    public void close() {
	        db.close();  
	    }  
}
