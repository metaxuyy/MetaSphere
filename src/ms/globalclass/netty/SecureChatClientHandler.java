/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ms.globalclass.netty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import ms.activitys.MainTabActivity;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;

/**
 * Handles a client-side channel.
 */
public class SecureChatClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            SecureChatClientHandler.class.getName());
    private static Context context;
    private MyApp myapp;
    public static Set<String> pusids = new HashSet<String>();
    private Timer timer;
    final ClientBootstrap bootstrap;

    public SecureChatClientHandler(Context context,MyApp myapp,Timer timer,ClientBootstrap bootstrap)
    {
    	this.context = context;
    	this.myapp = myapp;
    	this.timer = timer;
    	this.bootstrap = bootstrap;
    }
    
    @Override
    public void handleUpstream(
            ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }
    
    @Override
    public void childChannelClosed(ChannelHandlerContext ctx,
      ChildChannelStateEvent e) throws Exception {
//    	System.out.println("已经关闭了netty。。。");
    	super.childChannelClosed(ctx, e);
    }
    
    @Override
    public void channelDisconnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	if(MainTabActivity.instance != null)
    	{
	    	MainTabActivity.instance.timer2.cancel();
	    	MainTabActivity.instance.timer2 = null;
	    	System.out.println("已经关闭了netty。。。");
	    	if(MainTabActivity.instance.isInetnState())//如果没有断网从新连接
	    	{
	    		context.stopService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    		
	    		context.startService(new Intent(context, MyService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    	}
    	}
    	super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        // Get the SslHandler from the pipeline
        // which were added in SecureChatPipelineFactory.
        SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
        
//        timer.newTimeout(new TimerTask() {
//            public void run(Timeout timeout) throws Exception {
//                bootstrap.connect();
//            }
//        }, SecureChatClientPipelineFactory.RECONNECT_DELAY, TimeUnit.SECONDS);
        
        // Begin handshake.
        sslHandler.handshake();
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {
        System.err.println("dddddd=========="+e.getMessage().toString());
        String jsonstr = e.getMessage().toString();
        try {
        	JSONObject jobobj = new JSONObject(jsonstr);
        	JSONObject job = null;
        	boolean b = true;
        	String pusid = null;
        	if(jobobj.has("uuid"))
        	{
        		pusid = jobobj.getString("uuid");
        		job = jobobj.getJSONObject("jsonstr");
//            	MainTabActivity.instance.confirmPusMessage(pusid);
            	if(pusids.contains(pusid))
            	{
            		System.out.println("size==========="+pusids.size());
            		b = false;
            	}
            	else
            	{
            		pusids.add(pusid);
            	}
        	}
        	else
        	{
        		job = new JSONObject(jsonstr); 
        	}
//			JSONObject job = new JSONObject(jsonstr);
        	
        	if(b)
        	{
				if(job.has("nettyid"))
				{
					int nettyid = job.getInt("nettyid");
					myapp.setNettyid(nettyid);
					
					JSONObject nettyjob = new JSONObject();
					nettyjob.put("online", String.valueOf(nettyid));
					nettyjob.put("userid", myapp.getUserNameId());
					nettyjob.put("username", myapp.getUserName());
					if(myapp.getIsServer())
					{
						nettyjob.put("role", myapp.getCompanyid());
						nettyjob.put("storeid", myapp.getAppstoreid());
					}
					String jsonstrs = nettyjob.toString();
					ChannelFuture lastWriteFuture = null;
					lastWriteFuture = myapp.getChannel().write(jsonstrs + "\r\n");
					
					if(nettyid != 0)
					{
						Intent intent = new Intent();
						intent.putExtra("pusid", pusid);
						intent.setAction("unread_message_pus_hua_meida");
						context.sendBroadcast(intent);
					}
				}
				else if(job.has("online"))//好友上线通知
				{
					String nettyid = job.getString("online");
					String userid = job.getString("userid");
					String username = job.getString("username");
					
					Map<String,Object> usermap = new HashMap<String,Object>();
					usermap.put("nettyid",nettyid);
					usermap.put("userid",userid);
					usermap.put("username",username);
					myapp.getOnlineUserList().put(nettyid, usermap);
					
					Intent intent = new Intent();
					intent.putExtra("nettyid",nettyid);
					intent.putExtra("userid",userid);
					intent.putExtra("username",username);
					intent.setAction("USER_ONLINE");
					context.sendBroadcast(intent);
				}
				else if(job.has("downline"))//好友下线通知
				{
					int id = job.getInt("downline");
					String nettyid = String.valueOf(id);
					Map<String,Object> usermap = myapp.getOnlineUserList().get(nettyid);
					if(usermap != null)
					{
						String username = (String)usermap.get("username");
						myapp.getOnlineUserList().remove(nettyid);
						
						Intent intent = new Intent();
						intent.putExtra("nettyid",nettyid);
						intent.putExtra("username", username);
						intent.setAction("USER_DOWNLINE");
						context.sendBroadcast(intent);
					}
				}
				else if(job.has("address"))//好友发来的信息
				{
					String msg = job.getString("msg");
					JSONObject job2 = job.getJSONObject("address");
					JSONObject addressjob = job2.getJSONObject("address");
					String hostName = job2.getString("hostName");
					String port = String.valueOf(job2.getInt("port"));
					String hostAddress = addressjob.getString("hostAddress");
					
					Intent intent = new Intent();
					intent.putExtra("msg",msg);
					intent.putExtra("hostName",hostName);
					intent.putExtra("port",port);
					intent.putExtra("hostAddress",hostAddress);
					intent.setAction("NEW_MESSAGE");
					context.sendBroadcast(intent);
				}
				else if(job.has("new_message"))
				{
					Intent intent = new Intent();
					intent.setAction("NEW_MESSAGE_LIST_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
				else if(job.has("automatic_message_pus"))
				{
					JSONArray jArry = new JSONArray();
					JSONObject mjob = job.getJSONObject("automatic_message_pus");
					jArry.put(mjob);
					Intent intent = new Intent();
					intent.putExtra("datastr",jArry.toString());
					intent.putExtra("pusid", pusid);
					intent.setAction("AUTOMATIC_MESSAGE_PUS_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
				else if(job.has("verification_message_pus"))
				{
					Intent intent = new Intent();
					intent.putExtra("pusid", pusid);
					intent.setAction("VERIFICATION_MESSAGE_PUS_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
				else if(job.has("friend_moments_pus"))
				{
					JSONArray momentfilearray = job.getJSONArray("friend_moments_files_pus");
					JSONObject momentsobj = job.getJSONObject("friend_moments_pus");
					String puspfid = job.getString("update_moments_pfid_pus");
					Intent intent = new Intent();
					intent.putExtra("moments",momentsobj.toString());
					intent.putExtra("momentsfiles",momentfilearray.toString());
					intent.putExtra("puspfid", puspfid);
					intent.putExtra("pusid", pusid);
					intent.setAction("MOMENTS_NEW_PUS_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
				else if(job.has("gongao_moments_pus"))
				{
					JSONArray momentfilearray = job.getJSONArray("friend_moments_files_pus");
					JSONObject momentsobj = job.getJSONObject("gongao_moments_pus");
					String puspfid = job.getString("update_moments_pfid_pus");
					Intent intent = new Intent();
					intent.putExtra("moments",momentsobj.toString());
					intent.putExtra("momentsfiles",momentfilearray.toString());
					intent.putExtra("puspfid", puspfid);
					intent.putExtra("pusid", pusid);
					intent.setAction("MOMENTS_GONGAO_NEW_PUS_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
				else if(job.has("confirm_message_pus"))//确认前一条发出去的信息对方有收到没
				{
					String mid = job.getString("confirm_message_pus");
					Intent intent = new Intent();
					intent.putExtra("mid", mid);
					intent.putExtra("pusid", pusid);
					intent.setAction("CONFIRM_MESSAGE_PUS_HUA_MEIDA");
					context.sendBroadcast(intent);
				}
	//			else if(job.has("unread_message_pus_hua_meida"))
	//			{
	//				JSONArray jArry = new JSONArray();
	//				jArry = job.getJSONArray("unread_message_pus_hua_meida");
	//				Intent intent = new Intent();
	//				intent.putExtra("datastr",jArry.toString());
	//				intent.setAction("unread_message_pus_hua_meida");
	//				context.sendBroadcast(intent);
	//			}
				else//接受到服务器推送来得我自己发的信息
				{
					String msg = job.getString("msg");
					Intent intent = new Intent();
					intent.putExtra("msg",msg);
					intent.setAction("MY_MESSAGE");
					context.sendBroadcast(intent);
				}
        	}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			if(jsonstr.equals("xintiao..."))
			{
				System.out.println("心跳。。。。");
			}
			else
			{
				e1.printStackTrace();
			}
		}
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                e.getCause());
        e.getChannel().close();
    }
}
