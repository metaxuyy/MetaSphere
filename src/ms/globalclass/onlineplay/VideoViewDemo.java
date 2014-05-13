/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ms.globalclass.onlineplay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ms.activitys.R;
import ms.activitys.MyCommentView;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;



public class VideoViewDemo extends Activity {
	private static final String TAG = "VideoViewDemo";

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	private VideoView mVideoView;
	//private AudioPlayer mVideoView;
//	private EditText mPath;
	private ImageButton mPlay;
	private ImageButton mPause;
	private ImageButton mReset;
	private ImageButton mStop;
	private String current;
	
	////////////////////////////////
	private boolean wasPlaying;
    private MediaPlayer mPlayer;
    private static final int MIN_BUFF = 2000 * 1024;
    private static final int REC_BUFF = 500 * 1024;
    private Handler handler = new Handler();
    private File DLTempFile;
    private final String TEMP_DOWNLOAD_FILE_NAME = "tempMediaData";
    private final String TEMP_BUFF_FILE_NAME = "tempBufferData";
    private final String FILE_POSTFIX = ".mp4";
    private final int PER_READ = 1024;
    private boolean   pause=false;
    private boolean   stop;
    private final int UNKNOWN_LENGTH = -1;
    private Handler   mHandler = null;	
    private TextView    playedTextView=null;
    private TextView    downtext      =null;
    

    private int VideoDuraton = 1; //总时间
    private int curPosition;
    private int mediaLength  = 1; //文件总长度
    private int totalKbRead = 0;  //已经下载的长度
    private boolean downloadOver = false;
    private boolean wasPlayed = false;
    private SeekBar seekbar = null;
    private boolean localfile = false;
    private double  downper   = 0.00;  // 下载百分比
    private double  playper   = 0.00;  // 播放比例
    private double  loadper   = 0.00; 
    
    private String filePath;
	////////////////////////////////

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.online_play);
		
		myapp = (MyApp)this.getApplicationContext();
		myapp.setRoomNo("102");
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		filePath = this.getIntent().getExtras().getString("filePath");
		
		mVideoView = (VideoView) findViewById(R.id.surface_view);

//		mPath = (EditText) findViewById(R.id.path);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setMax(100);
		
		playedTextView = (TextView) findViewById(R.id.has_played);
		downtext       = (TextView) findViewById(R.id.downtext);

//		mPath.setText("http://192.168.254.55:8088/upload/1321517427218_0.amr");

		mPlay  = (ImageButton) findViewById(R.id.play);
		mPause = (ImageButton) findViewById(R.id.pause);
		mReset = (ImageButton) findViewById(R.id.reset);
		mStop  = (ImageButton) findViewById(R.id.stop);
		
		mVideoView.setOnPreparedListener(prepareListener);
		mVideoView.setOnCompletionListener(CompletionListener);
		mVideoView.setOnErrorListener(ErrorListener);
		

		mPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
//				final String path = mPath.getText().toString();
				localfile = false;
				playnew(filePath);
				//playVideo();
				
				/*
				try {
					copyFile("/sdcard/0.mp4","/sdcard/a.mp4");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				playsdcardfile("/sdcard/0.mp4");
				*/
				
			}
		});
		mPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					localfile = true;
					mVideoView.setVideoPath("/sdcard/0.mp4");
					myHandler.sendEmptyMessage(PROGRESS_CHANGED);
					//mVideoView.pause();
				}
			}
		});
		mReset.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					//mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
					//mVideoView.seekTo(0);
					mVideoView.stopPlayback();
					DLTempFile.delete();
				}
			}
		});
		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				stop = true;
				/*
				if (mVideoView != null) {
					current = null;
					mVideoView.stopPlayback();
				}
				*/
			}
		});
		runOnUiThread(new Runnable(){
			public void run() {
				//playVideo();
			}
			
		});
	}
	
    //播放本地文件
	private void playsdcardfile(String Source){
		
		mVideoView.setVideoPath(Source);
    	mVideoView.start();
    }
    
	
///////////////////////////////////////////
//线程下载部分
///////////////////////////////////////////
	private MediaPlayer.OnPreparedListener prepareListener = new MediaPlayer.OnPreparedListener(){   
	    public void onPrepared(MediaPlayer mp){   
	    	Toast.makeText(VideoViewDemo.this, "准备完成"+curPosition,Toast.LENGTH_SHORT).show(); 
		    VideoDuraton=mVideoView.getDuration()+1; //获取总时间
		    loadper = downper; //装置的部分，就是下载的部分
		    myHandler.sendEmptyMessage(PROGRESS_CHANGED);
		    mVideoView.seekTo(curPosition);
		    mp.start(); //开始播放 
	    }   
	};
	
	//视频播放完成
	private MediaPlayer.OnCompletionListener CompletionListener=new MediaPlayer.OnCompletionListener(){
		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			curPosition = 0;
			mVideoView.stopPlayback();
		}
	};

	//视频播放完成
	private MediaPlayer.OnErrorListener ErrorListener=new MediaPlayer.OnErrorListener(){
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			mVideoView.stopPlayback();
			Toast.makeText(VideoViewDemo.this, "发现错误:"+curPosition+"错误编号:"+what,Toast.LENGTH_SHORT).show();
			pause = true;
			return true;
		}
	};
	
	
	
    public void playnew(final String path) {
        downloadOver = false;
        totalKbRead = 0;
        try {
             Log.v(TAG, "playing: " + path);
             if (path.equals(current) && mPlayer != null) {
                      mPlayer.start();
                      return;
             }
             current = path;
             mPlayer = null;
             new PlayThread(current).start();
        } catch (Exception e) {
        }
     }	
	
    public void setHandler(Handler handler) {
    	mHandler = handler;
    }
    
    //创建一个消息处理
	private final static int PROGRESS_CHANGED = 0;
    private final static int HIDE_CONTROLER = 1;
    private final static int CHANGE_FULLSCREEN =2;
    private final static int VIDEO_READY =3;
    private final static int VIDEO_BUFFERFULL =4;
    private final static int DOWN_REFRESH = 5;
    Handler myHandler = new Handler(){
 		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			    case VIDEO_READY:
			    	if (!wasPlayed)
			        {
			   		   wasPlayed = true; 
			   		    Toast.makeText(VideoViewDemo.this, "原始播放"+curPosition,Toast.LENGTH_SHORT).show();
					    mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
			        }			    	
			    	break;
			    case DOWN_REFRESH:
			    {
			    	downtext.setText(String.format("[%.4f]",downper));	
			    }
			    break;
			    case PROGRESS_CHANGED:
					if (mVideoView.isPlaying() && !pause)
					{
					  pause = false;
					  if (mVideoView.getCurrentPosition()!=0)
			    		  curPosition = mVideoView.getCurrentPosition();
					  downtext.setText(String.format("[%.4f]",downper));
					  
					  playper= curPosition*100.00/VideoDuraton*1.0;
					  seekbar.setProgress((int)playper);
					  int i=curPosition;
					  i/=1000;
					  int minute = i/60;
					  int hour = minute/60;
					  int second = i%60;
					  minute %= 60;
					  playedTextView.setText(String.format("%02d:%02d:%02d[%.4f][%.4f]", hour,minute,second,playper,loadper));
					}
					else
					{
						downtext.setText(String.format("[%.4f]",downper));	 
						 if (downper > (loadper+16) && !mVideoView.isPlaying() && pause)
						  {
							  Toast.makeText(VideoViewDemo.this, "开始播放"+curPosition,Toast.LENGTH_SHORT).show();
							  pause = false;
							  mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
						  }						
					}
					
					sendEmptyMessage(PROGRESS_CHANGED);
					
					break;
				case HIDE_CONTROLER:
					break;
				case VIDEO_BUFFERFULL:
					pause = true;
					mVideoView.stopPlayback(); //暂停播放
					Toast.makeText(VideoViewDemo.this, "下载完成", Toast.LENGTH_SHORT).show();
					mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
					pause = false;
					break;
			}
			super.handleMessage(msg);
		}	
    };   
    

    
    private void dealWithBufferData() {
        if (mVideoView == null || !wasPlayed) {
           //if (totalKbRead >= MIN_BUFF)
           {
              try {
                    startMediaPlayer();
              } catch (Exception e) {
              }
           }
        } else  {//if (mVideoView.getDuration() - mVideoView.getCurrentPosition() <= 1000)
        	//mVideoView.pause(); 
        	//transferBufferToMediaPlayer();
        }
      }
    
    //移动文件
	public void moveFile(File	oldLocation, File	newLocation)
	throws IOException {

		if ( oldLocation.exists( )) {
			BufferedInputStream  reader = new BufferedInputStream( new FileInputStream(oldLocation) );
			BufferedOutputStream  writer = new BufferedOutputStream( new FileOutputStream(newLocation, false));
            try {
		        byte[]  buff = new byte[8192];
		        int numChars;
		        while ( (numChars = reader.read(  buff, 0, buff.length ) ) != -1) {
		        	writer.write( buff, 0, numChars );
      		    }
            } catch( IOException ex ) {
				throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
            } finally {
                try {
                    if ( reader != null ){
                    	writer.close();
                        reader.close();
                    }
                } catch( IOException ex ){
				    Log.e(getClass().getName(),"Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() ); 
				}
            }
        } else {
			throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() );
        }
	}
	
	//复制文件
    private void copyFile(File fSource,File fdest) throws IOException{
    	FileOutputStream fileOS = null;
		fileOS = new FileOutputStream(fdest);
		
        byte[] buffer = new byte[8192];
        try{ 
           FileInputStream fileIS = new FileInputStream(fSource); 
           int countread=0;
           int readlen=fileIS.read(buffer);
           countread +=readlen;

           while(readlen>0){ 
        	   fileOS.write(buffer,0,readlen);
   	           readlen=fileIS.read(buffer);
   	           countread +=readlen;
            }
           buffer.clone();
           fileOS.close();
           fileIS.close();
           
         } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
          } catch (IOException e){ 
           e.printStackTrace(); 
         }		
    }
    
    //删除临时文件
    private void deleteTempFile(boolean isAll){
		File file[] = android.os.Environment.getExternalStorageDirectory().listFiles();   
        //这里我们只是取得列表中的第二个文件的绝对路径
        for (int i=0;i<file.length-1;i++){
    		//删除SD卡中的文件
    		if (file[i].isFile())
    		{
        		String filename=file[i].getName();
        		//
        		if (filename.length()>10)
        		{
            		String tmpstr = filename.substring(0,10);
            		if (tmpstr.equals("tempBuffer")){
            			File file1= new File(file[i].getAbsolutePath());
            			boolean isdelte=file1.delete();
            		}
        		}
    		}
        }
    }
    
    //文件复制
    private void copyFile(String sourcefile,String fdest) throws IOException{
    	//android.os.Environment.getExternalStorageDirectory()
    	File f = new File(fdest); 
    	FileOutputStream fileOS = null;
		fileOS = new FileOutputStream(f);

		//File file[] = android.os.Environment.getExternalStorageDirectory().listFiles();   
        //这里我们只是取得列表中的第二个文件的绝对路径
        //String path=file[1].getAbsolutePath();
    
		String path=sourcefile;
        byte[] buffer = new byte[8192];
        try{ 
           FileInputStream fileIS = new FileInputStream(path); 
           int countread=0;
           int readlen=fileIS.read(buffer);
           countread +=readlen;

           while(readlen>0){ 
        	   fileOS.write(buffer,0,readlen);
   	           readlen=fileIS.read(buffer);
   	           countread +=readlen;
            }
           buffer.clone();
           fileIS.close();
           fileOS.close();
         } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
          } catch (IOException e){ 
           e.printStackTrace(); 
         }
    }
    
    //文件上传到FTP服务器
    public void uploadFile(){
    	//FTPClient ftp= FTPClicent();
    	
    }
    
    private void startMediaPlayer() {
    	myHandler.sendEmptyMessage(VIDEO_READY);
    	/*
        try {
             deleteTempFile(true);
             BUFFTempFile = File.createTempFile(TEMP_BUFF_FILE_NAME,
                                FILE_POSTFIX);
             
             moveFile(DLTempFile, BUFFTempFile);
             
        	 //Toast.makeText(VideoViewDemo.this, "文件复制",Toast.LENGTH_SHORT).show();
             //copyFile(android.os.Environment.getExternalStorageDirectory()+"/"+TEMP_DOWNLOAD_FILE_NAME+FILE_POSTFIX,android.os.Environment.getExternalStorageDirectory()+"/"+TEMP_BUFF_FILE_NAME+FILE_POSTFIX);
        	 //mVideoView = new MediaPlayer();
             //setListener();
             //mVideoView.setDataSource(BUFFTempFile.getAbsolutePath());
             //mVideoView.prepare();
             
             //Toast.makeText(VideoViewDemo.this, "开始播放",Toast.LENGTH_SHORT).show();
             
             
             
             //if (!wasPlayed)
             //{
             //    mVideoView.setVideoPath(BUFFTempFile.getAbsolutePath());
             //   mVideoView.start();
             //    wasPlayed = true;          	 
             //} 
        } catch (IOException e) {
        }
        */
      }

     private void transferBufferToMediaPlayer() {
        try {
             myHandler.sendEmptyMessageDelayed(VIDEO_BUFFERFULL, 1000);
        } catch (Exception e) {
        }
      }
  
     private void dealWithLastData() {
        Runnable updater = new Runnable() {
                 public void run() {
                          transferBufferToMediaPlayer();
                 }
        };
        handler.post(updater);
     }

	/**
	 * 获得文件长度
	 * 
	 * @param urlString
	 * @return
	 */
	public long getFileSize(String urlString) {
		int nFileLength = -1;
		try {
			URL url = new URL(urlString);
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setRequestProperty("User-Agent", "NetFox");
			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				return -2; // -2 represent access is error
			}
			String sHeader;
			for (int i = 1;; i++) {
				sHeader = httpConnection.getHeaderFieldKey(i);
				if (sHeader != null) {
					if (sHeader.equals("content-length")) {
						nFileLength = Integer.parseInt(httpConnection
								.getHeaderField(sHeader));
						break;
					}
				} else
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nFileLength;
	}
     
    /*
     * 下载数据，分段下载
     * todo:联网方式和分段
     * @param mediaUrl
     * @param start
     * @param end
     */

private void playFromNet(String mediaUrl, int start, int end) {
    FileOutputStream out = null;
    InputStream is = null;
    try {
    	 
    	 URL url = new URL(mediaUrl);
    	 HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
    	
    	 DLTempFile = new File(android.os.Environment.getExternalStorageDirectory()+"/"+TEMP_DOWNLOAD_FILE_NAME+FILE_POSTFIX); 
    	 out = new FileOutputStream(DLTempFile,true);
         //获取当前文件的大小
         FileInputStream fis = new FileInputStream(DLTempFile);
         totalKbRead = fis.available();

         httpConnection.setRequestProperty("User-Agent", "NetFox");
         String sProperty = "bytes=" + totalKbRead + "-";
         httpConnection.setRequestProperty("RANGE", sProperty);
          
         is = httpConnection.getInputStream();
         mediaLength = httpConnection.getContentLength();
          
          if (is == null) {
                return;
          }
          
          byte buf[] = new byte[PER_READ];
          int readLength = 0;
          int oldlength  = 0;
          
          while (readLength != -1 && !stop) {
            readLength = is.read(buf);
            if (readLength > 0) {
               try {
                    out.write(buf, 0, readLength);
                    totalKbRead += readLength;
               } catch (Exception e) {
                    Log.e(TAG, e.toString());
               }
            }

            downper = totalKbRead*100.00/mediaLength*1.0; //下载比例
	        seekbar.setSecondaryProgress((int)downper);
	        if (downper>2.00 && (totalKbRead-oldlength)>REC_BUFF)
	        {
	           	oldlength = totalKbRead;
	           	myHandler.sendEmptyMessage(VIDEO_READY);
	         }
	         else
               myHandler.sendEmptyMessage(DOWN_REFRESH);	
            
            try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }//end while
          
          if (totalKbRead == mediaLength) {
             downloadOver = true;
             myHandler.sendEmptyMessageDelayed(VIDEO_BUFFERFULL, 1000);
          }
       } catch (MalformedURLException e) {
          Log.e(TAG, e.toString());
       } catch (IOException e) {
          Log.e(TAG, e.toString());
       } finally {
           if (out != null) {
              try {
                   out.close();
                  }
              catch (IOException e) {
                   e.printStackTrace();
              }
            }
            
           if (is != null) {
               try {
                    is.close();
                } catch (IOException e) {
                     e.printStackTrace();
                }
           }
          } 
}

private class PlayThread extends Thread {
    private String url;
    PlayThread(String url) {
          this.url = url;
    }
    
    public void run() {
       if (!URLUtil.isNetworkUrl(url)) {
            try {
            	// if (url.startsWith("content://")) {
                // mPlayer.setDataSource(MediaPlayService.this, Uri
                // .parse(url));
                // } else {
            	mVideoView.setVideoPath(url);
                // }
                //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //setVolume(0f);
            	mVideoView.start();
             } catch (IllegalArgumentException e) {
                  Log.e(TAG, e.toString());
             } catch (IllegalStateException e) {
                  Log.e(TAG, e.toString());
             } 
       } else {
             playFromNet(url, 0, UNKNOWN_LENGTH);
       }
    }
 }
///////////////////////////////////////
//线程部分完成
///////////////////////////////////////
}
