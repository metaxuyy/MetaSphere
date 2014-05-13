package ms.activitys.nfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.AssetFileDescriptor;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import ms.activitys.R;
import ms.activitys.LoginMain;
import ms.activitys.travel.TravelDetatilActivity;
import ms.activitys.user.UserInfoDetailed;
import ms.globalclass.httppost.Douban;
import ms.globalclass.map.MyApp;
import ms.pstreets.nfc.dataobject.mifare.MifareBlock;
import ms.pstreets.nfc.dataobject.mifare.MifareClassCard;
import ms.pstreets.nfc.dataobject.mifare.MifareSector;
import ms.pstreets.nfc.util.Converter;

public class ReadNfcTag extends Activity{

	private SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	
	// NFC parts
		private static NfcAdapter mAdapter;
		private static PendingIntent mPendingIntent;
		private static IntentFilter[] mFilters;
		private static String[][] mTechLists;


		private static final int AUTH = 1;
		private static final int EMPTY_BLOCK_0 = 2;
		private static final int EMPTY_BLOCK_1 = 3;
		private static final int NETWORK = 4;
		private static final String TAG = "mifare";
		private TextView txt;
		private String nfctype;
		private String jsonstr = "";
		private String landscapeId;
		private String storeid;
		private ProgressDialog mypDialog;
		private String pfid;
		private MediaPlayer mediaPlayer;
		private static final float BEEP_VOLUME = 0.10f;
		private boolean playBeep;
		private boolean vibrate;
		
		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.read_nfc_view);
			
			playBeep = true;
			AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				playBeep = false;
			}
			initBeepSound();
			vibrate = true;

			myapp = (MyApp)this.getApplicationContext();
			
			share = this.getSharedPreferences("perference", MODE_PRIVATE);
			api = new Douban(share,myapp);
			
			mAdapter = NfcAdapter.getDefaultAdapter(this);
			mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

			try {
				ndef.addDataType("*/*");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("fail", e);
			}
			mFilters = new IntentFilter[] { ndef, };

			mTechLists = new String[][] { new String[] { MifareClassic.class
					.getName() } };

			Intent intent = getIntent();
			resolveIntent(intent);
		}

		

		void resolveIntent(Intent intent) {
			// 1) Parse the intent and get the action that triggered this intent
			String action = intent.getAction();
			// 2) Check if it was triggered by a tag discovered interruption.
			if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
				// 3) Get an instance of the TAG from the NfcAdapter
				Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				// 4) Get an instance of the Mifare classic card from this TAG
				// intent
				MifareClassic mfc = MifareClassic.get(tagFromIntent);
				MifareClassCard mifareClassCard=null;

				try { // 5.1) Connect to card
					mfc.connect();
					boolean auth = false;
					// 5.2) and get the number of sectors this card has..and loop
					// thru these sectors
					int secCount = mfc.getSectorCount();
					Tag tag = mfc.getTag();
					byte[] uidbyte = tag.getId();
					String cardUid = Converter.byte2hex(uidbyte);
					mifareClassCard= new MifareClassCard(secCount);
					int bCount = 0;
					int bIndex = 0;
					for (int j = 0; j < secCount; j++) {
						MifareSector mifareSector = new MifareSector();
						mifareSector.sectorIndex = j;
						// 6.1) authenticate the sector
						String key = "AAAAAAAAAAAA";
//						String key = "FFFFFFFFFFFF";
						System.out.println(key);
						byte[] keybyte = Converter.hexStringToBytes(key);
						auth = mfc.authenticateSectorWithKeyA(j,keybyte);
						mifareSector.authorized = auth;
						if (auth) {
							// 6.2) In each sector - get the block count
							bCount = mfc.getBlockCountInSector(j);
							bCount =Math.min(bCount, MifareSector.BLOCKCOUNT);
							bIndex = mfc.sectorToBlock(j);
							for (int i = 0; i < bCount; i++) {

								// 6.3) Read the block
								byte []data = mfc.readBlock(bIndex);
								MifareBlock mifareBlock = new MifareBlock(data);
								mifareBlock.blockIndex = bIndex;
								// 7) Convert the data into a string from Hex
								// format.

								bIndex++;
								mifareSector.blocks[i] = mifareBlock;
		
								
							}
							mifareClassCard.setSector(mifareSector.sectorIndex,
									mifareSector);
						} else { // Authentication failed - Handle it

						}
					}
					ArrayList<String> blockData=new ArrayList<String>();
					int blockIndex=0;
					for(int i=1;i<secCount;i++){
						
						MifareSector mifareSector=mifareClassCard.getSector(i);
						for(int j=0;j<MifareSector.BLOCKCOUNT;j++){
							MifareBlock mifareBlock=mifareSector.blocks[j];
							byte []data=mifareBlock.getData();
							if(j != 3)//json字符串
							{
								String str = Converter.getHexString(data, data.length);
								str = str.trim();
								if(!str.equals(""))
									jsonstr = jsonstr + str;
								else
									break;
							}
						}
					}
//					String []contents=new String[blockData.size()];
//					blockData.toArray(contents);
//					if(contents.length > 0)
//					{
//						setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, contents));
//						txt.setVisibility(View.GONE);
//						getListView().setTextFilterEnabled(true);
//					}
//					else
//					{
//						txt.setText("没有找到NFC标签");
//					}
					
					//获取alarm uri
					Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

					playBeepSoundAndVibrate();
					
					String sessionid = this.share.getString("sessionidnfc", "");
					myapp.setSessionId(sessionid);
					loadThreadTypeData(cardUid);
//					JSONObject job = new JSONObject(jsonstr);
//					nfctype = job.getString("nfctype");
//					if(nfctype.equals("viewpoint"))
//					{
//						showProgressDialog();
//						landscapeId = job.getString("landscapeId");
//						storeid = job.getString("storeid");
//						loadThreadData();
//					}
//					else if(nfctype.equals("userinfo"))
//					{
//						showProgressDialog();
//						pfid = job.getString("pfid");
//						storeid = job.getString("storeid");
//						loadThreadData2();
//					}
					
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
					showAlert(3);
				}finally{
					if(mifareClassCard!=null){
						mifareClassCard.debugPrint();
					}
				}
			}// End of method
		}
		
		public void loadThreadTypeData(final String cuid)
		{
			showProgressDialog();
			new Thread() {
				public void run() {
					try{
						List<Map<String,Object>> list = null;
						JSONObject jobj = api.getNfcCardData(cuid);
						if(!jobj.has("error"))
						{
							String nfcno = jobj.getString("nfcno");
							String keyid = jobj.getString("keyid");
							String updateuser = jobj.getString("updateuser");
							String nfctype = jobj.getString("nfctype");
							String lastUpdatedStamp = jobj.getString("lastUpdatedStamp");
							String title = jobj.getString("title");
							String typevalue = jobj.getString("typevalue");
							storeid = jobj.getString("storeid");
							
							if(nfctype.equals("1"))
							{
								landscapeId = keyid;
								loadThreadData();
							} else if (nfctype.equals("4")) {
								pfid = keyid;
								loadThreadData2();
							} else if (nfctype.equals("7")) {
								pfid = keyid;
								loadThreadData3();
							}
						}
						else
						{
							makeText("没数据");
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}.start();
		}
		
		
		private void initBeepSound() {
			if (playBeep && mediaPlayer == null) {
				// The volume on STREAM_SYSTEM is not adjustable, and users found it
				// too loud,
				// so we now play on the music stream.
				setVolumeControlStream(AudioManager.STREAM_MUSIC);
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnCompletionListener(beepListener);

				AssetFileDescriptor file = getResources().openRawResourceFd(
						R.raw.beep);
				try {
					mediaPlayer.setDataSource(file.getFileDescriptor(),
							file.getStartOffset(), file.getLength());
					file.close();
					mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
					mediaPlayer.prepare();
				} catch (IOException e) {
					mediaPlayer = null;
				}
			}
		}
		
		private static final long VIBRATE_DURATION = 200L;
		
		private void playBeepSoundAndVibrate() {
			if (playBeep && mediaPlayer != null) {
				mediaPlayer.start();
			}
			if (vibrate) {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(VIBRATE_DURATION);
			}
		}
		
		public void makeText(String str)
		{
			Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		}
		
		/**
		 * When the beep has finished playing, rewind to queue up another one.
		 */
		private final OnCompletionListener beepListener = new OnCompletionListener() {
			public void onCompletion(MediaPlayer mediaPlayer) {
				mediaPlayer.seekTo(0);
			}
		};
		
		private void showAlert(int alertCase) {
			// prepare the alert box
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			switch (alertCase) {

			case AUTH:// Card Authentication Error
				alertbox.setMessage("Authentication Failed ");
				break;
			case EMPTY_BLOCK_0: // Block 0 Empty
				alertbox.setMessage("Failed reading ");
				break;
			case EMPTY_BLOCK_1:// Block 1 Empty
				alertbox.setMessage("Failed reading 0");
				break;
			case NETWORK: // Communication Error
				alertbox.setMessage("Tag reading error");
				
				break;
			}
			// set a positive/yes button and create a listener
			alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				// Save the data from the UI to the database - already done
				public void onClick(DialogInterface arg0, int arg1) {
					
				}
			});
			// display box
			alertbox.show();

		}


		public void loadThreadData()
		{
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 0;
					
					try{
						List<Map<String,Object>> list = null;
						JSONObject jobj = api.getNfcStore(storeid);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
//							list = HomePage.getMyCardList(jArr);
							myapp.setNfcStoreList(list);
						}
						
						msg.obj = list;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
		
		public void loadThreadData2()
		{
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					
					try{
						List<Map<String,Object>> list = null;
						JSONObject jobj = api.getNfcUser(storeid,pfid);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
//							list = HomePage.getMyCardList(jArr);
							myapp.setNfcStoreList(list);
						}
						
						msg.obj = list;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
		
		public void loadThreadData3()
		{
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = 2;
					
					try{
						List<Map<String,Object>> list = null;
						JSONObject jobj = api.getNfcUser(storeid,pfid);
						if(jobj != null)
						{
							JSONArray jArr = (JSONArray) jobj.get("data");
//							list = HomePage.getMyCardList(jArr);
							myapp.setNfcStoreList(list);
						}
						
						msg.obj = list;
					}catch(Exception ex){
						ex.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}.start();
		}

		private Handler handler = new Handler() {
			@Override
			public synchronized void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					if(mypDialog != null)
						mypDialog.dismiss();
					Intent intent = new Intent();
				    intent.setClass( ReadNfcTag.this,TravelDetatilActivity.class);
				    Bundle bundle = new Bundle();
					bundle.putInt("index", 0);
					bundle.putString("landscapeId", landscapeId);
					bundle.putString("nfc", "true");
					intent.putExtras(bundle);
				    startActivity(intent);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				    ReadNfcTag.this.finish();//关闭显示的Activity
					break;
				case 1:
					if(mypDialog != null)
						mypDialog.dismiss();
					Intent intent2 = new Intent();
				    intent2.setClass( ReadNfcTag.this,UserInfoDetailed.class);
				    Bundle bundle2 = new Bundle();
					bundle2.putInt("index", 0);
					bundle2.putString("nfc", "true");
					intent2.putExtras(bundle2);
				    startActivity(intent2);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				    ReadNfcTag.this.finish();//关闭显示的Activity
					break;
				case 2:
					if(mypDialog != null)
						mypDialog.dismiss();
					Intent intent3 = new Intent();
				    intent3.setClass( ReadNfcTag.this,LoginMain.class);
				    Bundle bundle3 = new Bundle();
					bundle3.putInt("index", 0);
					bundle3.putString("nfc", "true");
					intent3.putExtras(bundle3);
				    startActivity(intent3);//开始界面的跳转函数
				    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
				    ReadNfcTag.this.finish();//关闭显示的Activity
					break;
				default:
					super.handleMessage(msg);
				}
			}
		};
		
		public void showProgressDialog(){
	 		try{
	 			mypDialog=new ProgressDialog(this);
	             //实例化
	             mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	             //设置进度条风格，风格为圆形，旋转的
//	             mypDialog.setTitle("等待");
	             //设置ProgressDialog 标题
	             mypDialog.setMessage(this.getString(R.string.map_lable_11));
	             //设置ProgressDialog 提示信息
//	             mypDialog.setIcon(R.drawable.wait_icon);
	             //设置ProgressDialog 标题图标
//	             mypDialog.setButton("",this);
	             //设置ProgressDialog 的一个Button
	             mypDialog.setIndeterminate(false);
	             //设置ProgressDialog 的进度条是否不明确
	             mypDialog.setCancelable(true);
	             //设置ProgressDialog 是否可以按退回按键取消
	             mypDialog.show();
	             //让ProgressDialog显示
	 		}catch(Exception ex){
	 			ex.printStackTrace();
	 		}
	 	}

		@Override
		public void onResume() {
			super.onResume();
//			playBeep = true;
//			AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//				playBeep = false;
//			}
//			initBeepSound();
//			vibrate = true;
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
		}
		
		@Override
		public void onNewIntent(Intent intent) {
			Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
			resolveIntent(intent);
			// mText.setText("Discovered tag " + ++mCount + " with intent: " +
			// intent);
		}

		@Override
		public void onPause() {
			super.onPause();
			mAdapter.disableForegroundDispatch(this);
		}
}
