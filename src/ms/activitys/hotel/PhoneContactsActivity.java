package ms.activitys.hotel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.FileUtils;
import ms.globalclass.MyLoadingDialog;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.httppost.Douban;
import ms.globalclass.listviewadapter.ContactAdapter2;
import ms.globalclass.listviewadapter.PhoneContactAdapter;
import ms.globalclass.listviewadapter.SideBar;
import ms.globalclass.map.MyApp;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class PhoneContactsActivity extends Activity{

	private ListView lvContact;
	private SideBar indexBar;
//	private WindowManager mWindowManager;
	private TextView mDialogText;
//	private static String[] nicks;
//	private static String[] nicks = {"����","����","��ɽ","����","ŷ����","����","����","���","���","ܽ�ؽ��","������","ӣľ����","������","������","÷����"};
//	private static String[] userimgs;
	private static List<Map<String,Object>> dlist;
	
	
	private static SharedPreferences  share;
	private Douban api;
	private MyApp myapp;
	private MyLoadingDialog loadDialog;
	public static PhoneContactsActivity instance = null;
	private PhoneContactAdapter contactAdapter;
	private static DBHelperMessage db;
	public String mykey = "";
	public static String isYn;//0�����ڲ�Ա����1�����ⲿ����
	private int activitystart = 0;
	private View yaoqinview;
	private Dialog myDialogs;
	private String currePhoneNumber;
	private String curreEmail;
	public static FileUtils fileUtil = new FileUtils();
	
	 /**��ȡ��Phon���ֶ�**/  

    private static final String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID,Phone.SORT_KEY_PRIMARY };  
    /**��ϵ����ʾ����**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    /**�绰����**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
    /**ͷ��ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
    /**��ϵ�˵�ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_contact_view);
//        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        myapp = (MyApp)this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);
		api = new Douban(share,myapp);
		instance = this;
		
		db = new DBHelperMessage(this, myapp);
		
		Bundle bunde = this.getIntent().getExtras();
		isYn = bunde.getString("isYn");
		
		Button break_btn = (Button)findViewById(R.id.break_btn);
		break_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMainView();
			}
		});
		
		showMyLoadingDialog();
		findView();
    }
	
	private void findView(){
    	lvContact = (ListView)this.findViewById(R.id.lvContact);
    	indexBar = (SideBar) findViewById(R.id.sideBar);  
//        indexBar.setListView(lvContact); 
//        mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
    	mDialogText = (TextView)findViewById(R.id.diogtext);
        mDialogText.setVisibility(View.INVISIBLE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        
        lvContact.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
				{
					int sart = lvContact.getFirstVisiblePosition();
					int end = lvContact.getLastVisiblePosition();
//					System.out.println("sart=="+sart+"end===="+end);
					if(contactAdapter != null)
						contactAdapter.removeImageView(sart,end);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});

//        getMyCardListData();
//        getMyStoreListDatas();//�����ŵ�����
        getMyFriendDatas();//����ֻ�ͨѶ¼
    }
	
	public void getMyFriendDatas()
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Map<String,Object> listmap = PhoneContactsActivity.instance.getPhoneContactList();
					List<Map<String,Object>> storelist = (List<Map<String,Object>>)listmap.get("list");
					String phones = (String)listmap.get("list2");
					JSONObject job = api.getPhoneUserContacts(phones);
					if(job != null)
					{
						JSONArray jarry = job.getJSONArray("data");

						storelist = getPhoneContactData(jarry,storelist);
					}
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public Map<String,Object> getPhoneContactList2()
	{
		Map<String,Object> ddmap = new HashMap<String,Object>();
		StringBuffer psb = new StringBuffer();
		List<Map<String,Object>> contactlist = new ArrayList<Map<String,Object>>();
		try{
			// ������е���ϵ��
			String where = null;
			if(mykey != null && !mykey.equals(""))
				where = ContactsContract.Contacts.DISPLAY_NAME +" like '%?'";
			
			Cursor cur = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					where,
					new String[] {mykey},
					ContactsContract.Contacts.SORT_KEY_PRIMARY
							+ " COLLATE LOCALIZED ASC");
			// ѭ������
			if (cur.moveToFirst()) {
				
				int idColumn = cur
						.getColumnIndex(ContactsContract.Contacts._ID);

				int displayNameColumn = cur
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				
				int pinyinColumn = cur.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY);
				
				do {
					Map<String,Object> map = new HashMap<String,Object>();
					
					// �����ϵ�˵�ID��
					String contactId = cur.getString(idColumn);
					map.put("contactId", contactId);
					
					// �鿴����ϵ���ж��ٸ��绰���롣���û���ⷵ��ֵΪ0
					int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//					sb.append(disPlayName).append(":");
					StringBuffer sb = new StringBuffer();
					if (phoneCount > 0) {
						// �����ϵ�˵ĵ绰����
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + contactId, null, null);
						if (phones.moveToFirst()) {
							do {
								// �������еĵ绰����
								String phoneNumber = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String phoneType = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
								Log.i("phoneNumber", phoneNumber);
								Log.i("phoneType", phoneType);
								sb.append(phoneNumber).append(",");
							} while (phones.moveToNext());
						}
					}
					String phoneNumber = sb.toString();
					if(phoneNumber.equals(""))
					{
						continue;
					}
					else
					{
						psb.append(phoneNumber.substring(0,phoneNumber.length()-1)+",");
						map.put("phoneNumber", phoneNumber.substring(0,phoneNumber.length()-1));
					}
					
					// �����ϵ������
					String disPlayName = cur.getString(displayNameColumn);
					map.put("disPlayName", disPlayName);
					
					String namePinyin = cur.getString(pinyinColumn);//db.converterToFirstSpell(disPlayName);
					map.put("namePinyin", namePinyin);

					
					Bitmap contactPhoto = null;
					Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+disPlayName);
					ContentResolver resolver = getContentResolver();
					Cursor cursor2=resolver.query(uri, new String[]{"photo_id"}, null, null, null);
					if (cursor2.moveToFirst()) {
						String phot_IDo = cursor2.getString(0);
						System.out.println("��Ƭ------->" + phot_IDo);
						if (phot_IDo != null) {
							Cursor cursor3 = resolver.query(
									ContactsContract.Data.CONTENT_URI,
									new String[] { "data15" },
									"ContactsContract.Data._ID=" + phot_IDo, null,
									null);
							if (cursor3.moveToFirst()) {
								byte[] photoicon = cursor3.getBlob(0);
								System.out.println(photoicon);
								ByteArrayInputStream inputStream = new ByteArrayInputStream(
										photoicon);
								contactPhoto = BitmapFactory
										.decodeStream(inputStream);
							}
						}
					}
					map.put("userbitmap", contactPhoto);
					map.put("isstart", "");
					map.put("pfobject", null);
					
					contactlist.add(map);
					// ��ȡ����ϵ������
//					Cursor emails = getContentResolver().query(
//							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//							null,
//							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//									+ " = " + contactId, null, null);
//					if (emails.moveToFirst()) {
//						do {
//							// �������еĵ绰����
//							String emailType = emails
//									.getString(emails
//											.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//							String emailValue = emails
//									.getString(emails
//											.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//
//							Log.i("emailType", emailType);
//							Log.i("emailValue", emailValue);
//						} while (emails.moveToNext());
//					}
//
//					// ��ȡ����ϵ��IM
//					Cursor IMs = getContentResolver().query(
//							Data.CONTENT_URI,
//							new String[] { Data._ID, Im.PROTOCOL, Im.DATA },
//							Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE
//									+ "='" + Im.CONTENT_ITEM_TYPE + "'",
//							new String[] { contactId }, null);
//					if (IMs.moveToFirst()) {
//						do {
//							String protocol = IMs.getString(IMs
//									.getColumnIndex(Im.PROTOCOL));
//							String date = IMs.getString(IMs
//									.getColumnIndex(Im.DATA));
//							Log.i("protocol", protocol);
//							Log.i("date", date);
//						} while (IMs.moveToNext());
//					}
//
//					// ��ȡ����ϵ�˵�ַ
//					Cursor address = getContentResolver()
//							.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
//									null,
//									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//											+ " = " + contactId, null, null);
//					if (address.moveToFirst()) {
//						do {
//							// �������еĵ�ַ
//							String street = address
//									.getString(address
//											.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//							String city = address
//									.getString(address
//											.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//							String region = address
//									.getString(address
//											.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//							String postCode = address
//									.getString(address
//											.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//							String formatAddress = address
//									.getString(address
//											.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
//							Log.i("street", street);
//							Log.i("city", city);
//							Log.i("region", region);
//							Log.i("postCode", postCode);
//							Log.i("formatAddress", formatAddress);
//						} while (address.moveToNext());
//					}
//
//					// ��ȡ����ϵ����֯
//					Cursor organizations = getContentResolver().query(
//							Data.CONTENT_URI,
//							new String[] { Data._ID, Organization.COMPANY,
//									Organization.TITLE },
//							Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE
//									+ "='" + Organization.CONTENT_ITEM_TYPE
//									+ "'", new String[] { contactId }, null);
//					if (organizations.moveToFirst()) {
//						do {
//							String company = organizations
//									.getString(organizations
//											.getColumnIndex(Organization.COMPANY));
//							String title = organizations
//									.getString(organizations
//											.getColumnIndex(Organization.TITLE));
//							Log.i("company", company);
//							Log.i("title", title);
//						} while (organizations.moveToNext());
//					}
//
//					// ��ȡ��ע��Ϣ
//					Cursor notes = getContentResolver().query(
//							Data.CONTENT_URI,
//							new String[] { Data._ID, Note.NOTE },
//							Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE
//									+ "='" + Note.CONTENT_ITEM_TYPE + "'",
//							new String[] { contactId }, null);
//					if (notes.moveToFirst()) {
//						do {
//							String noteinfo = notes.getString(notes
//									.getColumnIndex(Note.NOTE));
//							Log.i("noteinfo", noteinfo);
//						} while (notes.moveToNext());
//					}
//
//					// ��ȡnickname��Ϣ
//					Cursor nicknames = getContentResolver().query(
//							Data.CONTENT_URI,
//							new String[] { Data._ID, Nickname.NAME },
//							Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE
//									+ "='" + Nickname.CONTENT_ITEM_TYPE + "'",
//							new String[] { contactId }, null);
//					if (nicknames.moveToFirst()) {
//						do {
//							String nickname_ = nicknames.getString(nicknames
//									.getColumnIndex(Nickname.NAME));
//							Log.i("nickname_", nickname_);
//						} while (nicknames.moveToNext());
//					}

				} while (cur.moveToNext());
				ddmap.put("list", contactlist);
				ddmap.put("list2", psb.toString().substring(0,psb.toString().length()-1));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ddmap;
	}
	
	/**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/  
    public Map<String,Object> getPhoneContactList() { 
    	Map<String,Object> ddmap = new HashMap<String,Object>();
    	List<Map<String,Object>> contactlist = new ArrayList<Map<String,Object>>();
    	StringBuffer sb = new StringBuffer();
		try {
			ContentResolver resolver = getContentResolver();

			String where = null;
			if(mykey != null && !mykey.equals(""))
				where = Phone.DISPLAY_NAME + " like '"+mykey+"%'";
			
			// ��ȡ�ֻ���ϵ��
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
					PHONES_PROJECTION, where, null, Phone.SORT_KEY_PRIMARY + " COLLATE LOCALIZED ASC");

			if (phoneCursor != null) {

				while (phoneCursor.moveToNext()) {
					Map<String,Object> map = new HashMap<String,Object>();
					
					// �õ��ֻ�����
					String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

					// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
					if (TextUtils.isEmpty(phoneNumber))
						continue;
					
					map.put("phoneNumber", phoneNumber);
					if(isMobile(phoneNumber))
						sb.append(phoneNumber+",");
					
					// �õ���ϵ������
					String contactName = phoneCursor
							.getString(PHONES_DISPLAY_NAME_INDEX);
					map.put("disPlayName", contactName);
//					map2.put("disPlayName", contactName);
					
					String namePinyin = phoneCursor.getString(4);
					map.put("namePinyin", namePinyin);
//					String str = contactName.substring(0,1);
//					if(str.matches("^[a-zA-Z]*"))
//						map.put("namePinyin", contactName);
//					else
//					{
//						String namePinyin = db.converterToFirstSpell(contactName);
//						map.put("namePinyin", namePinyin);
//					}
//					map2.put("namePinyin", namePinyin);

					// �õ���ϵ��ID
					Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
					map.put("contactId", String.valueOf(contactid));
//					map2.put("contactId", contactid);

					// �õ���ϵ��ͷ��ID
					Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

					// �õ���ϵ��ͷ��Bitamp
					Bitmap contactPhoto = null;

					// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�

					if (photoid > 0) {
						Uri uri = ContentUris.withAppendedId(
								ContactsContract.Contacts.CONTENT_URI,
								contactid);

						InputStream input = ContactsContract.Contacts
								.openContactPhotoInputStream(resolver, uri);

						contactPhoto = BitmapFactory.decodeStream(input);
						map.put("userbitmap", contactPhoto);
					} else {

						// contactPhoto =
						// BitmapFactory.decodeResource(getResources(),
						// R.drawable.contact_photo);
						map.put("userbitmap", contactPhoto);
					}
					
					String email = "";
					//Fetch email
					Cursor emailCursor = resolver.query(
							android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null, android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID+"="+contactid, null, null);
					while(emailCursor.moveToNext()) {
						email = emailCursor.getString(
								emailCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.DATA));
						if(isEmail(email))
							break;
					}
					map.put("email", email);
//					map2.put("userbitmap", "");
					
					map.put("isstart", "");
					map.put("pfobject", null);
					
					if(isMobile(phoneNumber))
						contactlist.add(map);
				}
				phoneCursor.close();
				
				ddmap.put("list", contactlist);
				ddmap.put("list2", sb.toString().substring(0,sb.toString().length()-1));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ddmap;
    }  
    
    public List<Map<String,Object>> getPhoneContactData(JSONArray jarry,List<Map<String,Object>> storelist)
    {
    	List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
    	try{
    		for(int i=0;i<jarry.length();i++)
    		{
    			JSONObject dobj = jarry.getJSONObject(i);
    			Map<String,Object> smap = storelist.get(i);
    			
    			String contactId = ""; 
				contactId = (String) smap.get("contactId");
				
				String disPlayName = ""; 
				disPlayName = (String) smap.get("disPlayName");
				
				String namePinyin = ""; 
				namePinyin = (String) smap.get("namePinyin");
				
				String phoneNumber = ""; 
				phoneNumber = (String) smap.get("phoneNumber");
				
				String email = (String) smap.get("email");
					
				String isstart = ""; 
				if(dobj.has("isstart"))
					isstart = (String) dobj.get("isstart");
				
				Bitmap userbitmap = (Bitmap)smap.get("userbitmap");
				
				JSONObject job = null; 
				Map<String,Object> dmap = new HashMap<String,Object>();
				if(dobj.has("pfobject"))
				{
					job = (JSONObject) dobj.get("pfobject");
					if(job != null)
					{
						String username = (String)job.get("username");
						String pfid = (String)job.get("pfid");
						String account = (String)job.get("account");
						String sex = (String)job.get("sex");
						String area = (String)job.get("area");
						String signature = (String)job.get("signature");
						String companyid = (String)job.get("companyid");
						String storeids = (String)job.get("storeids");
						String imgurl = (String)job.get("imgurl");
						String pkid = (String)job.get("pkid");
						
						dmap.put("pkid", pkid);
						dmap.put("username", username);
						dmap.put("pfid", pfid);
						dmap.put("account", account);
						dmap.put("sex", sex);
						dmap.put("area", area);
						dmap.put("signature", signature);
						dmap.put("companyid", companyid);
						dmap.put("storeids", storeids);
						if(imgurl != null && !imgurl.equals(""))
						{
							String furl = fileUtil.getImageFile2aPath(pfid, pfid);
							Bitmap bmpsimg = myapp.returnUserImgBitMap(imgurl);
							if(bmpsimg != null)
							{
								myapp.saveMyBitmap(furl, bmpsimg);
								imgurl = furl;
							}
						}
						dmap.put("imgurl", imgurl);
					}
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("contactId", contactId);
				map.put("disPlayName", disPlayName);
				map.put("namePinyin", namePinyin);
				map.put("phoneNumber", phoneNumber);
				map.put("isstart", isstart);
				map.put("email", email);
				map.put("userbitmap", userbitmap);
				if(job == null)
					map.put("pfobject", null);
				else
					map.put("pfobject", dmap);
				
				dlist.add(map);
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return dlist;
    }
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Map<String, Object>> list3 = (List<Map<String, Object>>)msg.obj;
				if(list3 != null)
				{
//					dlist = myapp.getMyCardsAll();
					contactAdapter = new PhoneContactAdapter(PhoneContactsActivity.this,list3,R.layout.contact_item,R.layout.store_search_view,
							new String[] { "namePinyin", "userbitmap","disPlayName","phoneNumber"},
							new int[] { R.id.contactitem_catalog, R.id.contactitem_avatar_iv,R.id.contactitem_nick},myapp,isYn);
					lvContact.setAdapter(contactAdapter);
					
					lvContact.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if(arg2 > 0)
							{
								Map<String,Object> map = (Map<String,Object>)contactAdapter.getItem(arg2);
								String isstart = (String)map.get("isstart");
								String phoneNumber = (String)map.get("phoneNumber");
								String email = (String)map.get("email");
								Map<String,Object> pmap = (Map<String,Object>)map.get("pfobject");
								openFriendView(isstart,pmap,phoneNumber,email);
							}
						}
					});
					
					indexBar.setListView(lvContact);
				}
				else
				{
					
				}
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 1:
				String apkurl = (String)msg.obj;
				EditText message_edit = (EditText)yaoqinview.findViewById(R.id.message_edit);
				String message = message_edit.getText().toString();
				myDialogs.dismiss();
		
//				//��ȡ���Ź�����
//		        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault(); 
//		        //��ֶ������ݣ��ֻ����ų������ƣ� 
//		        List<String> divideContents = smsManager.divideMessage(message);  
//		        for (String text : divideContents) {   
//		            smsManager.sendTextMessage(currePhoneNumber, null, text, sentPI, deliverPI);   
//		        }  
				
				Uri smsToUri = Uri.parse("smsto:"+currePhoneNumber);
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", message+"\n"+apkurl+" "+getString(R.string.hotel_label_188));
				startActivity(intent);
				
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			case 2:
				String apkurl2 = (String)msg.obj;
				EditText message_edit2 = (EditText)yaoqinview.findViewById(R.id.message_edit);
				String message2 = message_edit2.getText().toString();
				myDialogs.dismiss();
				
//				String[] reciver = new String[] { curreEmail };  
//		        String[] mySbuject = new String[] { "test" };  
////		        String myCc = "cc";  
//		        String mybody = message2+"\n"+apkurl2+" "+getString(R.string.hotel_label_188); 
//		        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);  
//		        myIntent.setType("plain/text");  
//		        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);  
////		        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);  
//		        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
//		        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);  
//		        startActivity(Intent.createChooser(myIntent, getString(R.string.app_name)));
		        
		        Intent myIntent = new Intent(Intent.ACTION_SENDTO);  
		        myIntent.setData(Uri.parse("mailto:"+curreEmail));  
		        myIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+getString(R.string.hotel_label_184));  
		        myIntent.putExtra(Intent.EXTRA_TEXT, message2+"\n"+apkurl2+" "+getString(R.string.hotel_label_188));  
	            startActivity(myIntent);
		        
				if(loadDialog != null)
					loadDialog.dismiss();
				break;
			}
		}
	};
	
	public void getMyPhoneContactListDatas(final String key,final String tag)
	{
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				
				try {
					Map<String,Object> listmap = PhoneContactsActivity.instance.getPhoneContactList();
					List<Map<String,Object>> storelist = (List<Map<String,Object>>)listmap.get("list");
					String phones = (String)listmap.get("list2");
					JSONObject job = api.getPhoneUserContacts(phones);
					if(job != null)
					{
						JSONArray jarry = job.getJSONArray("data");

						storelist = getPhoneContactData(jarry,storelist);
					}
					
					msg.obj = storelist;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			openMainView();
			return false;
		}
		return false;
	}
	
	public void openMainView()
	{
		try{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setClass( this,FriendsAddedActivity.class);
			intent.putExtras(bundle);
		    startActivity(intent);//��ʼ�������ת����
		    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
		    this.finish();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openFriendView(String isstart,Map<String,Object> map,String phoneNumber,String email)
	{
		try{
			if(isstart.equals("1"))//�Ѿ��Ǻ���
			{
				String pkid = (String)map.get("pkid");
				String username = (String)map.get("username");
				String pfid = (String)map.get("pfid");
				String imgurl = fileUtil.getImageFile1aPath(pfid, pfid);
				String account = (String)map.get("account");
				String sex = (String)map.get("sex");
				String area = (String)map.get("area");
				String signature = (String)map.get("signature");
				String companyid = (String)map.get("companyid");
				String storeids = (String)map.get("storeids");
				
				Intent intent = new Intent();
			    intent.setClass( this,FriendInfoViewActivity.class);
			    Bundle bundle = new Bundle();
			    bundle.putString("username", username);
			    bundle.putString("addpfid", pfid);
			    bundle.putString("imgurl", imgurl);
			    bundle.putString("pkid", pkid);
			    
			    bundle.putString("account", account);
			    bundle.putString("sex", sex);
			    bundle.putString("area", area);
			    bundle.putString("signature", signature);
			    bundle.putString("tag","phonecontact");
			    bundle.putString("isYn",isYn);
				intent.putExtras(bundle);
				startActivity(intent);//��ʼ�������ת����
				overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else if(isstart.equals("2"))//�����Ǻ���
			{
				String pkid = (String)map.get("pkid");
				String username = (String)map.get("username");
				String pfid = (String)map.get("pfid");
				String imgurl = (String)map.get("imgurl");
				String account = (String)map.get("account");
				String sex = (String)map.get("sex");
				String area = (String)map.get("area");
				String signature = (String)map.get("signature");
				String companyid = (String)map.get("companyid");
				String storeids = (String)map.get("storeids");
				
				Intent intent = new Intent();
			    intent.setClass( this,FriendInfoActivity.class);
			    Bundle bundle = new Bundle();
			    bundle.putString("username", username);
			    bundle.putString("addpfid", pfid);
			    bundle.putString("imgurl", imgurl);
			    bundle.putString("tag", "phonecontact");
			    
			    bundle.putString("account", account);
			    bundle.putString("sex", sex);
			    bundle.putString("area", area);
			    bundle.putString("signature", signature);
			    bundle.putString("companyid", companyid);
			    bundle.putString("storeids", storeids);
				intent.putExtras(bundle);
			    startActivity(intent);//��ʼ�������ת����
			    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
			}
			else//��û�а�װ���ǵĳ���������
			{
				currePhoneNumber = phoneNumber;
				curreEmail = email;
				openVerificationWindo();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void openVerificationWindo()
	{
		try{
			yaoqinview = LayoutInflater.from(this).inflate(R.layout.phone_contact_yaoqin_windo, null);
			
			myDialogs = new Dialog(this,R.style.MyMapDialog);
			myDialogs.setContentView(yaoqinview);
			
			if(curreEmail != null && !curreEmail.equals(""))
			{
				Button btn = (Button)yaoqinview.findViewById(R.id.add_vip_email_btn);
				btn.setVisibility(View.VISIBLE);
			}
			Window dialogWindow = myDialogs.getWindow();
			WindowManager m = getWindowManager();
	        Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = (int) (d.getWidth() * 0.9);
			dialogWindow.setAttributes(lp);
			
			myDialogs.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void cleraWindo(View v)
	{
		myDialogs.dismiss();
	}
	
	public void sendFriendMessage(View v)
	{
		showMyLoadingDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				
				try {
					JSONObject job = api.getAndroidVersionNew();
					String apkurl = "";
					if(job != null && job.has("url"))
						 apkurl = job.getString("url");
					msg.obj = apkurl;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public void sendFriendEmail(View v)
	{
		showMyLoadingDialog();
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				
				try {
					JSONObject job = api.getAndroidVersionNew();
					String apkurl = "";
					if(job != null && job.has("url"))
						 apkurl = job.getString("url");
					msg.obj = apkurl;
				} catch (Exception ex) {
					Log.i("erroyMessage", ex.getMessage());
					ex.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * ��֤�Ƿ����ֻ��Ÿ�ʽ �÷��������Ǻ��Ͻ�,ֻ�ǿ��Լ���֤
	 * 
	 * @param mobile
	 * @return true��ʾ����ȷ���ֻ��Ÿ�ʽ,false��ʾ������ȷ���ֻ��Ÿ�ʽ
	 */
	public static boolean isMobile(String mobile) {
		// ��ǰ��Ӫ�̺Ŷη���
		// �й��ƶ��Ŷ� 1340-1348 135 136 137 138 139 150 151 152 157 158 159 187 188
		// 147
		// �й���ͨ�Ŷ� 130 131 132 155 156 185 186 145
		// �й����źŶ� 133 1349 153 180 189
		String regular = "^((\\+861)|(861)|(1))[3,4,5,8]{1}\\d{9}";
		Pattern pattern = Pattern.compile(regular);
		boolean flag = false;
		if (mobile != null) {
			Matcher matcher = pattern.matcher(mobile);
			flag = matcher.matches();
		}
		return flag;
	}
	
	/**
	 * ��֤�����ַ
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String regular = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(regular);
		boolean flag = false;
		if (email != null) {
			Matcher matcher = pattern.matcher(email);
			flag = matcher.matches();
		}
		return flag;
	}
	
	public void showMyLoadingDialog()
    {
    	loadDialog = new MyLoadingDialog(this, getString(R.string.map_lable_11),R.style.MyDialog);
    	loadDialog.show();
    }
}
