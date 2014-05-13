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

import org.json.JSONArray;
import org.json.JSONObject;

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

public class DBHelperShoppingCart {
	private static final String TAG = "UserDB_DBHelper.java";  
	    private static final String DataBaseName = "UserDB";  
	    SQLiteDatabase db;  
	    Context context;
	    private MyApp myapp;
	  
	    public DBHelperShoppingCart(Context context,MyApp myp) {
//	    	if(db != null)
//	    	{
//	    		db.close();
//	    		db = null;
//	    	}
	        this.open(context);  
	        this.myapp = myp;
	    }  
	  
	    private void createTabel() {  
	        // TODO Auto-generated method stub  
	        String sql = "";  
	        try {  
	            sql = "CREATE TABLE IF NOT EXISTS MyShoopingCart (ID TEXT PRIMARY KEY,  imgurl TEXT,  cname TEXT, productSize TEXT, pcolor TEXT, number TEXT, prices TEXT, totalPrice DOUBLE, pkid TEXT,storesid TEXT, imgbitm BLOB,uuid TEXT,isshelf TEXT,ingredients TEXT,tastes TEXT,productCode TEXT)";  
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

	    public boolean saveMyShoopingCart(List<Map<String,Object>> dlist)
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
					cv.put("imgurl", (String)map.get("imgurl")); 
					cv.put("cname", (String)map.get("cname")); 
					cv.put("productSize", (String)map.get("productSize")); 
					cv.put("pcolor", (String)map.get("pcolor")); 
					cv.put("number", (String)map.get("number")); 
					cv.put("prices", (String)map.get("prices2")); 
					cv.put("totalPrice", (Double)map.get("totalPrice")); 
					cv.put("pkid", (String)map.get("pkid")); 
					cv.put("storesid", (String)map.get("storesid")); 
					cv.put("uuid", (String)map.get("uuid"));
					
					String imgurl = (String)map.get("imgurl");
					String str = imgurl.substring(0,imgurl.lastIndexOf("."));
					String str2 = imgurl.substring(imgurl.lastIndexOf("."),imgurl.length());
					imgurl = str+"_zhong"+str2;
					Bitmap bitm = getImageBitmap(imgurl);
					
					ByteArrayOutputStream oss = new ByteArrayOutputStream();
					bitm.compress(Bitmap.CompressFormat.PNG, 100, oss);
					cv.put("imgbitm", oss.toByteArray()); 
					
					cv.put("isshelf", "1");
					List<Map<String,String>> sideDisheslist = (List<Map<String,String>>)map.get("sideDisheslist");
					if(sideDisheslist != null)
					{
						JSONArray job = new JSONArray(sideDisheslist);
						cv.put("ingredients", job.toString());
					}
					else
					{
						cv.put("ingredients", "");
					}
					cv.put("tastes", (String)map.get("tastes"));
					cv.put("productCode", (String)map.get("productCode"));
					
					db.insert("MyShoopingCart",null, cv);
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
	    
	    public boolean saveMyShoopingCart(Map<String,Object> map)
	    {
	    	boolean a = true;
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
	    		
				db.beginTransaction();  //手动设置开始事务
				
				ContentValues cv=new ContentValues();
				String tableid = getTableId();
				cv.put("ID", tableid); 
				cv.put("imgurl", (String)map.get("imgurl")); 
				cv.put("cname", (String)map.get("cname")); 
				cv.put("productSize", (String)map.get("productSize")); 
				cv.put("pcolor", (String)map.get("pcolor")); 
				cv.put("number", (String)map.get("number")); 
				cv.put("prices", (String)map.get("prices2")); 
				cv.put("totalPrice", (Double)map.get("totalPrice")); 
				cv.put("pkid", (String)map.get("pkid")); 
				cv.put("storesid", (String)map.get("storesid")); 
				cv.put("uuid", (String)map.get("uuid")); 
				
				String imgurl = (String)map.get("imgurl");
				String str = imgurl.substring(0,imgurl.lastIndexOf("."));
				String str2 = imgurl.substring(imgurl.lastIndexOf("."),imgurl.length());
				imgurl = str+"_zhong"+str2;
				Bitmap bitm = getImageBitmap(imgurl);
				
				ByteArrayOutputStream oss = new ByteArrayOutputStream();
				bitm.compress(Bitmap.CompressFormat.PNG, 100, oss);
				cv.put("imgbitm", oss.toByteArray()); 
				
				cv.put("isshelf", "1");
				List<Map<String,String>> sideDisheslist = (List<Map<String,String>>)map.get("sideDisheslist");
				if(sideDisheslist != null)
				{
					JSONArray job = new JSONArray(sideDisheslist);
					cv.put("ingredients", job.toString());
				}
				else
				{
					cv.put("ingredients", "");
				}
				cv.put("tastes", (String)map.get("tastes"));
				cv.put("productCode", (String)map.get("productCode"));
				
				db.insert("MyShoopingCart",null, cv);
				
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
	    
	    public List<Map<String,Object>> loadMyShoopingCart(String storesid) {  
	    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
	    		
		        Cursor cur = db.query("MyShoopingCart", new String[] { "imgurl","cname", "productSize", "pcolor", "number", "prices", "totalPrice", "pkid", "storesid", "imgbitm","uuid","isshelf","ingredients","tastes","productCode"}, "storesid=?",  
		        		new String[] {storesid}, null, null, null);  
		        if(cur.getCount() > 0)
		    	{
		    		cur.moveToFirst(); 
		    	    while (!cur.isAfterLast()) { 
		    	    	Map<String,Object> map = new HashMap<String,Object>();
		    	    	map.put("imgurl", cur.getString(0));
						map.put("cname", cur.getString(1));
						map.put("productSize", cur.getString(2));
						map.put("pcolor", cur.getString(3));
						map.put("number", cur.getString(4));
						map.put("prices", cur.getString(5));
						map.put("totalPrice", cur.getDouble(6));
						map.put("pkid", cur.getString(7));
						map.put("storesid", cur.getString(8));
						
						byte[] ins=cur.getBlob(cur.getColumnIndex("imgbitm"));
						Bitmap bmptiao = BitmapFactory.decodeByteArray(ins,0,ins.length);
						map.put("imgbitm", bmptiao); 
						
						map.put("uuid", cur.getString(10));
						map.put("isshelf", cur.getString(11));
						String dientstr = "";
						String jsonstr = cur.getString(12);
						if(!jsonstr.equals(""))
						{
							JSONArray jarr = new JSONArray(jsonstr);
							List<Map<String,String>> dislist = new ArrayList<Map<String,String>>();
							for(int i=0;i<jarr.length();i++)
							{
								Map<String,String> dmap = new HashMap<String,String>();
								String jsonstrs = (String) jarr.get(i);
								jsonstrs = jsonstrs.replaceAll(" ", "");
								JSONObject dobj = new JSONObject(jsonstrs);
								dmap.put("pkid", dobj.getString("pkid"));
								dmap.put("ingredientsName", dobj.getString("ingredientsName"));
								dientstr = dientstr + dobj.getString("ingredientsName") + ",";
								dmap.put("price", dobj.getString("price"));
								dislist.add(dmap);
							}
							map.put("sideDisheslist", dislist);
						}
						else
						{
							map.put("sideDisheslist", null);
						}
						if(!dientstr.equals(""))
						{
							dientstr = dientstr.substring(0,dientstr.length() - 1);
						}
						map.put("dishestr", dientstr);
						map.put("tastes", cur.getString(13));
						map.put("productCode", cur.getString(14));
			
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
	    
	    public List<Map<String,Object>> loadMyShoopingCartShelf(String storesid) {  
	    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
	    	try{
	    		if(!db.isOpen())
		    		open2(context);
	    		
		        Cursor cur = db.query("MyShoopingCart", new String[] { "imgurl","cname", "productSize", "pcolor", "number", "prices", "totalPrice", "pkid", "storesid", "imgbitm","uuid","isshelf","ingredients","tastes","productCode"}, "storesid=? and isshelf=?",  
		        		new String[] {storesid,"0"}, null, null, null);  
		        if(cur.getCount() > 0)
		    	{
		    		cur.moveToFirst(); 
		    	    while (!cur.isAfterLast()) { 
		    	    	Map<String,Object> map = new HashMap<String,Object>();
		    	    	map.put("imgurl", cur.getString(0));
						map.put("cname", cur.getString(1));
						map.put("productSize", cur.getString(2));
						map.put("pcolor", cur.getString(3));
						map.put("number", cur.getString(4));
						map.put("prices", cur.getString(5));
						map.put("totalPrice", cur.getDouble(6));
						map.put("pkid", cur.getString(7));
						map.put("storesid", cur.getString(8));
						
						byte[] ins=cur.getBlob(cur.getColumnIndex("imgbitm"));
						Bitmap bmptiao = BitmapFactory.decodeByteArray(ins,0,ins.length);
						map.put("imgbitm", bmptiao); 
						
						map.put("uuid", cur.getString(10));
						String jsonstr = cur.getString(12);
						String dientstr = "";
						if(!jsonstr.equals(""))
						{
							JSONArray jarr = new JSONArray(jsonstr);
							List<Map<String,String>> dislist = new ArrayList<Map<String,String>>();
							for(int i=0;i<jarr.length();i++)
							{
								Map<String,String> dmap = new HashMap<String,String>();
								String jsonstrs = (String) jarr.get(i);
								jsonstrs = jsonstrs.replaceAll(" ", "");
								JSONObject dobj = new JSONObject(jsonstrs);
								dmap.put("pkid", dobj.getString("pkid"));
								dmap.put("ingredientsName", dobj.getString("ingredientsName"));
								dientstr = dientstr + dobj.getString("ingredientsName") + ",";
								dmap.put("price", dobj.getString("price"));
								dislist.add(dmap);
							}
							map.put("sideDisheslist", dislist);
						}
						else
						{
							map.put("sideDisheslist", null);
						}
						if(!dientstr.equals(""))
						{
							dientstr = dientstr.substring(0,dientstr.length() - 1);
						}
						map.put("dishestr", dientstr);
						map.put("tastes", cur.getString(13));
						map.put("productCode", cur.getString(14));
			
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
	    
	    public void delMyShoopingCart(String uuid) {
	    	if(!db.isOpen())
	    		open2(context);
	    	
	        int rownumber = db.delete("MyShoopingCart", "uuid=?", new String[] {uuid});
	        System.out.println("delMycardTable======"+rownumber);
	        db.close();
	    }
	    
	    public void delMyShoopingCartAll(String storesid) {
	    	if(!db.isOpen())
	    		open2(context);
	    	
	        int rownumber = db.delete("MyShoopingCart", "storesid=?", new String[] {storesid});
	        System.out.println("delMycardTable======"+rownumber);
	        db.close();
	    }
	    
	    public void udateMyShoopingCart(String uuid,String number){
	    	if(!db.isOpen())
	    		open2(context);
	    	ContentValues cv=new ContentValues();
			cv.put("number", number); 
	    	db.update("MyShoopingCart", cv, "uuid=?", new String[] {uuid});
	    	
	    	db.close();
	    }
	    
	    public void udateMyShoopingCartShelf(String uuid,String shelf){
	    	if(!db.isOpen())
	    		open2(context);
	    	ContentValues cv=new ContentValues();
			cv.put("isshelf", shelf); 
	    	db.update("MyShoopingCart", cv, "uuid=?", new String[] {uuid});
	    	
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
