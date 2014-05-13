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

public class DBHelperCoupon {
	private static final String TAG = "UserDB_DBHelper.java";  
	    private static final String DataBaseName = "UserDB";  
	    SQLiteDatabase db;  
	    Context context;
	    private MyApp myapp;
	  
	    public DBHelperCoupon(Context context,MyApp myp) {  
	        this.open(context); 
	        this.myapp = myp;
	    }  
	  
	    private void createTabel() {  
	        // TODO Auto-generated method stub  
	        String sql = "";  
	        try {  
	            sql = "CREATE TABLE IF NOT EXISTS MyCoupon    (ID TEXT PRIMARY KEY,  couponId TEXT,  startTime TEXT, endTime TEXT, couponDesc TEXT,  img BLOB, simg BLOB, imgurl TEXT, couponType TEXT, sname TEXT,gongli TEXT, couponNumber TEXT,couponName TEXT, simgurl TEXT, storeid TEXT, isASttention TEXT, couponNo TEXT, isUser TEXT, couponLoadid TEXT, businessId TEXT, bimg BLOB)";  
	            this.db.execSQL(sql);
	            
	            Log.v(TAG, "Create Table MyCoupon ok");
	            System.out.println("Create Table MyCoupon ok");
	            
	            db.close();
	        } catch (Exception e) {  
	            Log.v(TAG, "Create Table MyCoupon fail");  
	        } finally {  
	            //this.db.close();  
	            Log.v(TAG, "Create Table MyCoupon ");  
	        }  
	    }  

	    
	    public boolean saveMyCoupon(List<Map<String,Object>> dlist)
	    {
	    	boolean a = true;
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
				db.beginTransaction();  //手动设置开始事务
				for(int i=0;i<dlist.size();i++)
				{
					Map map = dlist.get(i);
					ContentValues cv=new ContentValues();
					String tableid = getTableId();
					cv.put("ID", tableid); 
					cv.put("couponId", (String)map.get("couponId")); 
					cv.put("startTime", (String)map.get("startTime")); 
					cv.put("endTime", (String)map.get("endTime")); 
					cv.put("couponDesc", (String)map.get("couponDesc")); 
					Bitmap bitm = (Bitmap)map.get("img");
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					bitm.compress(Bitmap.CompressFormat.PNG, 100, os); 
					cv.put("img", os.toByteArray()); 
					Bitmap sbitm = (Bitmap)map.get("simg");
					ByteArrayOutputStream sos = new ByteArrayOutputStream();
					sbitm.compress(Bitmap.CompressFormat.PNG, 100, sos); 
					cv.put("simg", sos.toByteArray()); 
					Bitmap bbitm = getImageBitmap("http://"+myapp.getHost()+":80/customize/control/BarcodeServlet?msg="+(String)map.get("couponNo"));
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bbitm.compress(Bitmap.CompressFormat.PNG, 100, bos); 
					cv.put("bimg", bos.toByteArray()); 
					cv.put("imgurl", (String)map.get("imgurl")); 
					cv.put("couponType", (String)map.get("couponType")); 
					cv.put("sname", (String)map.get("sname")); 
					cv.put("gongli", (String)map.get("gongli")); 
					cv.put("couponNumber", (String)map.get("couponNumber")); 
					cv.put("couponName", (String)map.get("couponName")); 
					cv.put("simgurl", (String)map.get("simgurl")); 
					cv.put("storeid", (String)map.get("storeid")); 
					cv.put("isASttention", (String)map.get("isASttention")); 
					cv.put("couponNo", (String)map.get("couponNo")); 
					cv.put("isUser", (String)map.get("isUserStr")); 
					cv.put("couponLoadid", (String)map.get("couponLoadid")); 
					cv.put("businessId", (String)map.get("businessId")); 
					
					db.insert("MyCoupon",null, cv);
				}
				db.setTransactionSuccessful();  //设置事务处理成功，不设置会自动回滚不提交

				db.endTransaction(); //处理完成 
				
				db.close();
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    		a = false;
	    	}
	    	return a;
	    }
	  
	    public List<Map<String,Object>> loadMyCoupon(String storeid) {  
	    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
	    		
		        Cursor cur = db.query("MyCoupon", new String[] { "couponId","startTime", "endTime", "couponDesc", "img", "simg", "imgurl", "couponType", "sname", "gongli", "couponNumber", "couponName", "simgurl", "storeid", "isASttention", "couponNo", "isUser", "couponLoadid", "businessId", "bimg"}, "storeid=?",  
		                new String[] {storeid}, null, null, null);  
		        if(cur.getCount() > 0)
		    	{
		    		cur.moveToFirst(); 
		    	    while (!cur.isAfterLast()) { 
		    	    	Map<String,Object> map = new HashMap<String,Object>();
		    	    	map.put("couponId", cur.getString(cur.getColumnIndex("couponId")));
						map.put("startTime", cur.getString(cur.getColumnIndex("startTime")));
						map.put("endTime", cur.getString(cur.getColumnIndex("endTime")));
						map.put("couponDesc", cur.getString(cur.getColumnIndex("couponDesc")));
						byte[] in=cur.getBlob(cur.getColumnIndex("img"));
						Bitmap bmpout = BitmapFactory.decodeByteArray(in,0,in.length);
						map.put("img", bmpout);
						byte[] in2=cur.getBlob(cur.getColumnIndex("simg"));
						Bitmap bmpout2 = BitmapFactory.decodeByteArray(in2,0,in2.length);
						map.put("simg", bmpout2);
						byte[] in3=cur.getBlob(cur.getColumnIndex("bimg"));
						Bitmap bmpout3 = BitmapFactory.decodeByteArray(in3,0,in3.length);
						map.put("bimg", bmpout3);
						map.put("imgurl", cur.getString(cur.getColumnIndex("imgurl")));
						map.put("couponType", cur.getString(cur.getColumnIndex("couponType")));
						map.put("sname", cur.getString(cur.getColumnIndex("sname")));
						map.put("gongli", cur.getString(cur.getColumnIndex("gongli")));
						map.put("couponNumber", cur.getString(cur.getColumnIndex("couponNumber")));
						map.put("couponName", cur.getString(cur.getColumnIndex("couponName")));
						map.put("simgurl", cur.getString(cur.getColumnIndex("simgurl")));
						map.put("storeid", cur.getString(cur.getColumnIndex("storeid")));
						map.put("isASttention", cur.getString(cur.getColumnIndex("isASttention")));
						map.put("couponNo", cur.getString(cur.getColumnIndex("couponNo")));
						String isUser = cur.getString(cur.getColumnIndex("isUser"));
						if(isUser.equals("1"))
						{
							map.put("isUser", R.drawable.check);
							map.put("isUserStr", "1");
						}
						else
						{
							map.put("isUser", R.drawable.close);
							map.put("isUserStr", "0");
						}
						map.put("couponLoadid", cur.getString(cur.getColumnIndex("couponLoadid")));
						map.put("businessId", cur.getString(cur.getColumnIndex("businessId")));
			
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
	    
	    public void udateCouponStatr(String couponId){
	    	if(!db.isOpen())
	    		open2(context);
	    	ContentValues cv=new ContentValues();
			cv.put("isUser", 0); 
	    	db.update("MyCoupon", cv, "couponId=?", new String[] {couponId});
	    	
	    	db.close();
	    }
	    
	    public void delCouponColumnAll() {
	    	if(!db.isOpen())
	    		open2(context);
	        int rownumber = db.delete("MyCoupon", null, null);
	        System.out.println("delMyCouponTable======"+rownumber);
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
