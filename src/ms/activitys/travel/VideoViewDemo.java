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
package ms.activitys.travel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ms.activitys.R;
import ms.activitys.more.Settings;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoViewDemo extends Activity {
	private static final String TAG = "VideoViewDemo";

	private VideoView mVideoView;
	private String current;
	private String path;
	
	private MediaPlayer mediaPlayer;
	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private ProgressDialog mypDialog;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.voidview);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		
		myapp = (MyApp)this.getApplicationContext();
		
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		
		path = this.getIntent().getExtras().getString("path");
		String name = this.getIntent().getExtras().getString("name");

//		mPath = (EditText) findViewById(R.id.path);
//		mPath.setText("http://192.168.1.4:8088/upload/640x360-3gpCD.mp4");
//
//		mPlay = (ImageButton) findViewById(R.id.play);
//		mPause = (ImageButton) findViewById(R.id.pause);
//		mReset = (ImageButton) findViewById(R.id.reset);
//		mStop = (ImageButton) findViewById(R.id.stop);
//
//		mPlay.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				playVideo();
//			}
//		});
//		mPause.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				if (mVideoView != null) {
//					mVideoView.pause();
//				}
//			}
//		});
//		mReset.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				if (mVideoView != null) {
//					mVideoView.seekTo(0);
//				}
//			}
//		});
//		mStop.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				if (mVideoView != null) {
//					current = null;
//					mVideoView.stopPlayback();
//				}
//			}
//		});
		mypDialog = ProgressDialog.show(this, null, name+this.getString(R.string.travel_lable_29), true,
                false);
		runOnUiThread(new Runnable(){
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				playVideo();
				handler.sendMessage(msg);
			}
			
		});
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(mypDialog != null)
					mypDialog.dismiss();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			VideoViewDemo.this.setResult(RESULT_OK, getIntent());
			VideoViewDemo.this.finish();
			return false;
		}
		return false;
	}

	private void playVideo() {
		try {
//			final String path = mPath.getText().toString();
			if(path.indexOf(".amr") >= 0)
			{
				mVideoView.setVisibility(View.GONE);
				
				mediaPlayer = new MediaPlayer();
				// 为播放器设置数据文件 
				mediaPlayer.setDataSource(getDataSource(path));
				// 准备并且启动播放器
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								setTitle(VideoViewDemo.this.getString(R.string.record_lable_5));

							}
						});
				makeText(VideoViewDemo.this.getString(R.string.record_lable_6));
			}
			else
			{
				mVideoView.setVisibility(View.VISIBLE);
				Log.v(TAG, "path: " + path);
				if (path == null || path.length() == 0) {
					Toast.makeText(VideoViewDemo.this, "File URL/path is empty",
							Toast.LENGTH_LONG).show();
	
				} else {
//					if(path.indexOf(".mp3") < 0)
//					{
//						mVideoView.setBackgroundDrawable(null);
//					}
//					else
//					{
//						mVideoView.setBackgroundResource(R.drawable.pause);
//					}
					MediaController controller = new MediaController(VideoViewDemo.this);
					mVideoView.setMediaController(controller);
					// If the path has not changed, just start the media player
					if (path.equals(current) && mVideoView != null) {
						mVideoView.start();
						mVideoView.requestFocus();
						return;
					}
					current = path;
					mVideoView.setVideoPath(getDataSource(path));
					mVideoView.start();
					mVideoView.requestFocus();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
		}
	}

	private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[128];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
			} catch (IOException ex) {
				Log.e(TAG, "error: " + ex.getMessage(), ex);
			}
			return tempPath;
		}
	}
	
	public void makeText(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
