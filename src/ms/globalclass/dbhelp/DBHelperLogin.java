package ms.globalclass.dbhelp;

import android.content.ContentValues;
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.provider.SyncStateContract.Constants;
import android.util.Log;  
import android.widget.Toast;

public class DBHelperLogin {
	private static final String TAG = "UserDB_DBHelper.java";  
	    private static final String DataBaseName = "UserDB";  
	    SQLiteDatabase db;  
	    Context context;  
	  
	    public DBHelperLogin(Context context) {  
	        this.open(context);  
	    }  
	  
	    private void createTabel() {  
	        // TODO Auto-generated method stub  
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	        String sql = "";  
	        try {  
	            sql = "CREATE TABLE IF NOT EXISTS LoginData    (ID INTEGER PRIMARY KEY autoincrement,  loginName TEXT,  password TEXT, thembgurl TEXT)";  
	            this.db.execSQL(sql);  
	            Log.v(TAG, "Create Table LoginData ok");
	            System.out.println("Create Table LoginData ok");
	            db.close();
	        } catch (Exception e) {  
	            Log.v(TAG, "Create Table LoginData fail");  
	        } finally {  
	            //this.db.close();  
	            Log.v(TAG, "Create Table LoginData ");  
	        }  
	    }  
	  
	    public boolean save(String userName, String password,String themebgurl) {
	    	int tableid = getTableId();
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
//	    	delColumnAll();
//	    	Toast.makeText(context, "tableid "+tableid, Toast.LENGTH_LONG).show();
	       
	        try {
	        	if(!isLogin(userName,password))
		    	{
	        		 String sql = "insert into LoginData values("+tableid+",'" + userName + "','" + password  
	     	                + "','"+themebgurl+"')";  
		            this.db.execSQL(sql);
	//	            Toast.makeText(context, "insert  Table LocalData 1 record ok "+action, Toast.LENGTH_LONG).show();
		            Log.v(TAG, "insert  Table LoginData 1 record ok");  
		    	}
	            return true;  
	        } catch (Exception e) {  
//	        	Toast.makeText(context, "error:"+e.getMessage(), Toast.LENGTH_LONG).show();
	            Log.v(TAG, "insert  Table LoginData 1 record fail");  
	            return false;  
	        } finally {  
	            this.db.close();  
	            Log.v(TAG, "insert  Table LoginData ");  
	        }  
	    }  
	  
	    public void loadAlls() {  
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	        Cursor cur = db.query("LoginData", new String[] { "ID", "loginName","password"}, null,  
	                null, null, null, null);  
	        
	        db.close();
	        cur.close();  
	    }
	    
	    public boolean isLogin(String name,String pws)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	boolean b = false;
	    	Cursor cur = db.query("LoginData", new String[] { "ID", "loginName","password"}, "loginName = ? and password = ?", new String[] {name,pws}, null, null, null);
	    	if(cur.getCount() > 0)
	    		b = true;
	    	db.close();
	    	cur.close();
	    	return b;
	    }
	    
	    public String getThembgurl(String name,String pws)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	String thembgurl = "";
	    	Cursor c = null;
    		String sql = "SELECT * FROM LoginData WHERE loginName = ? and password = ?";
			c = db.rawQuery(sql, new String[]{name,pws});
			
            while (c.moveToNext()) {  
                thembgurl = c.getString(c.getColumnIndex("thembgurl"));
            }
	    	db.close();
	    	c.close();
	    	return thembgurl;
	    }
	    
	    public void updateThembgurl(String thembgurl,String id)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	ContentValues cv=new ContentValues();
	    	cv.put("thembgurl", thembgurl);
	    	
	    	int rowcount = db.update("LoginData", cv, "id=?", new String[] {id});
	    	db.close();
	    }
	    
	    public void updateLogin(String name,String pwa,String id)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	ContentValues cv=new ContentValues(); 
	    	cv.put("userName", name); 
	    	cv.put("password", pwa); 
	    	
	    	int rowcount = db.update("LoginData", cv, "id=?", new String[] {id});
	    	db.close();
	    }
	    
	    public void updateLogin(String name,String id)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	ContentValues cv=new ContentValues(); 
	    	cv.put("userName", name); 
	    	
	    	int rowcount = db.update("LoginData", cv, "id=?", new String[] {id});
	    	db.close();
	    }
	    
	    public String getLoginId(String name,String pws)
	    {
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	    	String id = "";
	    	Cursor cur = db.query("LoginData", new String[] { "ID"}, "loginName = ? and password = ?", new String[] {name,pws}, null, null, null);
	    	if(cur.getCount() > 0)
	    	{
	    		cur.moveToFirst(); 
	    	    while (!cur.isAfterLast()) { 
	    	        id=cur.getString(0); 
	    	        
	    	        // do something useful with these 
	    	        cur.moveToNext(); 
	    	      } 
	    	    db.close();
	    	    cur.close();
	    	}
	    	return id;
	    }
	    
	    public void delColumn(int id) { 
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	        db.delete("LoginData", "ID = "+id, null);
	    }
	    
	    public void delColumnAll() {  
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	        db.delete("LoginData", null, null);
	    }
	    
	    public int getTableId() {  
	    	if (null == db || !this.db.isOpen()){  
	    		this.db = context.openOrCreateDatabase(this.DataBaseName,  
	                    context.MODE_PRIVATE, null);  
	    	}
	        Cursor cur = db.query("LoginData", new String[] { "ID", "loginName","password"}, null,  
	                null, null, null, null);  
	        int tableid = cur.getCount() + 1;
	        db.close();
	        cur.close();
	        return tableid;  
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
	    public void close() {
	        db.close();  
	    }  
}
