package ms.globalclass.pushmessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import ms.activitys.LoginMain;
import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.activitys.myAnimation;
import ms.globalclass.FileUtils;
import ms.globalclass.U;
import ms.globalclass.dbhelp.DBHelperLogin;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.globalclass.netty.SecureChatClientPipelineFactory;

public class MyService extends Service {
	private static final String TAG = "MyService"; 
	private SharedPreferences  share;
	private MyApp myapp;
	private Douban api;
	
	private ChannelFuture lastWriteFuture = null;
	private Channel channel;
	private ClientBootstrap bootstrap;
	private String ip;
	private MyBinder myBinder = new MyBinder();
	public static FileUtils fileUtil = new FileUtils();
	private DBHelperLogin db;
	
	@Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return myBinder;
    }
    
    public class MyBinder extends Binder{
        
        public MyService getService(){
            return MyService.this;
        }
    }
    
	@Override
	public void onCreate() { // 鍒涘缓鏈嶅姟
//        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show(); 
        Log.d(TAG, "onCreate");  
        share = this.getSharedPreferences("perference", MODE_PRIVATE);
        myapp = (MyApp)this.getApplicationContext();
        
        api = new Douban(share,myapp);
        db = new DBHelperLogin(this);
    }  
    @Override  
    public void onDestroy() {  //鍋滄鏈嶅姟
//        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();  
        Log.d(TAG, "onDestroy");  
//        channel.getCloseFuture().awaitUninterruptibly();
//        
//        if (lastWriteFuture != null) {
//            lastWriteFuture.awaitUninterruptibly();
//        }
//
//        // Close the connection.  Make sure the close operation ends because
//        // all I/O operations are asynchronous in Netty.
//        channel.close().awaitUninterruptibly();
//
//        // Shut down all thread pools to exit.
//        bootstrap.releaseExternalResources();
//        player.stop();  
    }  
	
    @Override
    public void onStart(Intent intent, int startid) {  //寮?鏈嶅姟
//        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        //鍒嗛厤
        try{
        	ip = this.share.getString("ipadrees", "121.199.8.186");
//    		if(this.share != null)
//    			ip = this.share.getString("ipadrees", "192.168.254.55");
//    	    Socket socket = new Socket(ip, 8089);
//    		new ClientOutputThread(socket,this,myapp).start();
        	
        	String user = share.getString("user", "");
			String pwa = share.getString("pwa", "");
			String sessionid = myapp.getSessionId();
			if(sessionid != null && !sessionid.equals(""))
			{
				run(ip,8663);
			}
//			if(user != null && !user.equals(""))
			else
			{
				loadThreadData(user,pwa,ip);
			}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        Log.d(TAG, "onStart");  
//        player.start();  
    } 
    
    public void run(String host,int port) throws IOException {
    	Timer timer = new HashedWheelTimer();
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new SecureChatClientPipelineFactory(this,myapp,timer,bootstrap));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        System.out.println("aaaaaaaaaaaa");
        bootstrap.setOption("remoteAddress", new InetSocketAddress(host, port));
        // Start the connection attempt.
//        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        ChannelFuture future = bootstrap.connect();
//        future.awaitUninterruptibly();
        // Wait until the connection attempt succeeds or fails.
        channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }
        if(channel != null)
        	System.out.println("dddddddddddddddd");
        myapp.setChannel(null);
        myapp.setChannel(channel);
        if(MainTabActivity.instance != null)
        	MainTabActivity.instance.loadNettyTrim();
    }
    
    public void loadThreadData(final String lname,final String paw,final String ip)
	{
				
		JSONObject jobj;
		U.dout(0);

		try {
			jobj = api.login(lname, paw);
			if (jobj != null) {
				String tag = (String)jobj.get("tag");
				if(tag.equals("Success"))
				{
					String sessionid = jobj.getString("sessionid");
					myapp.setSessionId(sessionid);
					String profileid = jobj.getString("profileid");
					myapp.setPfprofileId(profileid);
					String email = jobj.getString("email");
					saveSharedPerferences("email",email);
					saveSharedPerferences("sessionid",sessionid);
					saveSharedPerferences("sessionidnfc",sessionid);
					myapp.setUserNameId(profileid);
					if(jobj.has("userimg"))
					{
						String userimg = jobj.getString("userimg");
						myapp.setUserimg(userimg);
					}
					
					String username = jobj.getString("username");
					String sex = jobj.getString("sex");
					String area = jobj.getString("area");
					String signature = jobj.getString("signature");
					String thembgurl = jobj.getString("thembgurl");
					String companyid = jobj.getString("companyid");
					myapp.setMyaccount(lname);
					myapp.setUserName(username);
					myapp.setMysex(sex);
					myapp.setMyarea(area);
					myapp.setMySignature(signature);
					myapp.setCompanyid(companyid);
					String themeurl = db.getThembgurl(lname, paw);
					if(themeurl != null && !themeurl.equals(""))
					{
						thembgurl = themeurl;
					}
					else
					{
//						lodaThemeImage(thembgurl);
						Bitmap bitmap = myapp.getImageBitmap(thembgurl);
						if(bitmap != null)
						{
							UUID uuid = UUID.randomUUID();
							if(!fileUtil.isFileExist2(myapp.getPfprofileId()))
								fileUtil.createUserFile(myapp.getPfprofileId());
							String furl = fileUtil.getImageFile1bPath(myapp.getPfprofileId(), uuid.toString());
							myapp.saveMyBitmap(furl, bitmap);
							thembgurl = furl;
						}
					}
					
					myapp.setThembgurl(thembgurl);
					saveUserPerferences(lname, paw);
					saveSharedPerferences("username",username);
					saveSharedPerferences("sex",sex);
					saveSharedPerferences("area",area);
					saveSharedPerferences("signature",signature);
					saveSharedPerferences("thembgurl",thembgurl);
					saveSharedPerferences("companyid",companyid);
					
					if(companyid != null && !companyid.equals(""))
						myapp.setIsServer(true);
					else
						myapp.setIsServer(false);
					
					db.save(lname, paw,thembgurl);
					
					run(ip,8663);
				}
				else
				{
					Intent intent = new Intent();
					intent.setClass(this, LoginMain.class);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(this, LoginMain.class);
				startActivity(intent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
    public void saveSharedPerferences(String key,String value)
	{
		Editor editor = share.edit();// 取得编辑器
		editor.putString(key, value);// 存储配置 参数1 是key 参数2 是值
		editor.commit();// 提交刷新数据
	}
    
    public void saveUserPerferences(String user,String pwa)
	{
		Editor editor = share.edit();// 取得编辑器
		editor.putString("user", user);
		editor.putString("pwa", pwa);
		editor.commit();// 提交刷新数据
	}
    
    public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
