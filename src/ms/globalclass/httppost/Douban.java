package ms.globalclass.httppost;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ms.globalclass.FormFile;
import ms.globalclass.map.MyApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Douban {
	public static String BASE_URL = "http://test:8088/customize/control/";
	public static String IMG_BASE_URL = "http://test:8088/";
	private static final String EXTENSION = "?alt=json";
	private static final String TAG = "API";
	private static final String USER_AGENT = "Mozilla/4.5";
	private static final String SOURCE_PARAMETER = "fanfou api";
	private String path;
	private Bitmap bitmap;
	private SharedPreferences  share;
	private MyApp myapp;
	private static HttpClientToServer httpClientToServer = new HttpClientToServer();

	public Douban(SharedPreferences  share,MyApp myapps){
		this.share = share;
		this.myapp = myapps;
		String ip = null;
		if(this.share != null)
			ip = this.share.getString("ipadrees", "121.199.8.186");
//			ip = "119.147.213.172";
		BASE_URL = "http://"+ ip +":80/customize/control/";
		IMG_BASE_URL = "http://"+ ip +":80/upload/";
		//BASE_URL = "http://192.168.254.17:80/customize/control/";
	}
	
	/**
	 * 
	 * ����ҳ������
	 */
	public JSONObject saveMenuPageData(String menuName, JSONObject dataObj, Map<String, File> files)
	{
		JSONObject job = null;
		try{
			Map<String, String> params = new HashMap<String, String>();
 			params.put("menuName", menuName);
 			params.put("dataObj", dataObj.toString());
 			params.put("storeid", myapp.getAppstoreid());
 			params.put("userid", myapp.getUserNameId());
 			params.put("ishasfile", (files.size() == 0)?"0":"1");
			String str = addParam2(params);
			String actionUrl = BASE_URL+"updateMenuPage;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return job;
	}
	
	/**
	 * ��ȡҳ��ģ���б�
	 * 
	 * @throws Exception
	 */
	public JSONObject getPageModeList() throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		return getJSONObject(getRequest(BASE_URL + "getPageModeList;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡҳ����ʽ
	 * 
	 * @throws Exception
	 */
	public JSONObject getPageStyleByName(String menuName, String storeid) throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", storeid));
		params.add(new BasicNameValuePair("pageName", menuName));
		return getJSONObject(getRequest(BASE_URL + "getPageStyleByName;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ���͹ؼ���
	 * 
	 * @throws Exception
	 */
	public JSONObject sendKeyword() throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		return getJSONObject(getRequest(BASE_URL + "sendKeyword;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡҳ�水ť�����б�(ҳ����ת�б�)
	 * 
	 * @throws Exception
	 */
	public JSONObject getPageSkip() throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		return getJSONObject(getRequest(BASE_URL + "getPageSkip;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡҳ��ͼ�����ת
	 * 
	 * @throws Exception
	 */
	public JSONObject getPageIconSkip() throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		return getJSONObject(getRequest(BASE_URL + "getPageIconSkip;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡҳ������
	 * 
	 * @throws Exception
	 */
	public JSONObject getMenuByName(String menuName) throws JSONException,
			Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("userid", myapp.getUserNameId()));
		params.add(new BasicNameValuePair("menuName", menuName));
		return getJSONObject(getRequest(BASE_URL + "getMenuByNameHttp;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * �������
	 * 
	 * @throws Exception
	 */
	public JSONObject sendResponse(String context) throws JSONException,
			Exception {
		// MideaAndroid1219V1.2.apk
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("context", context));
		params.add(new BasicNameValuePair("userId", myapp.getPfprofileId()));
		return getJSONObject(getRequest(BASE_URL + "sendResponse;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ���汾
	 * 
	 * @throws Exception
	 */
	public JSONObject checkVersion(String curVersion,int versionCode,String packageNames) throws JSONException,
			Exception {
		// MideaAndroid1219V1.2.apk
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("curversion", curVersion));
		params.add(new BasicNameValuePair("versionCode", String.valueOf(versionCode)));
		params.add(new BasicNameValuePair("packageNames", packageNames));
		System.out.println("���汾���ӣ�"+BASE_URL + "checkVersion;jsessionid="+myapp.getSessionId());
		return getJSONObject(getRequest(BASE_URL + "checkVersion;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��������
	 * 
	 * @throws Exception
	 */
	public JSONObject sendComment(String commentStr, String publishID) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("discusDesc", commentStr));//��������
		params.add(new BasicNameValuePair("userId", myapp.getPfprofileId()));//������
		params.add(new BasicNameValuePair("publishuserType", myapp.getIsServer() ? "1" : "2"));//����������
		params.add(new BasicNameValuePair("noticeId", publishID));//������������ID
		return getJSONObject(getRequest(BASE_URL + "saveComment;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡ�����µĹ����б�
	 * 
	 * @throws Exception
	 */
	public JSONObject getManagerApp() throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("businessid", myapp.getBusinessid()));
		return getJSONObject(getRequest(BASE_URL + "getManagerApp;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡ����������
	 * @throws Exception 
	 */
	public JSONObject getPublishBoardType() throws Exception{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("businessid", myapp.getBusinessid()));
		return getJSONObject(getRequest(BASE_URL + "getPublishBoardType;jsessionid="+myapp.getSessionId(), params));
	}
	
	/**
	 * ��ȡ�������ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getServiceData() throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
//		params.add(new BasicNameValuePair("PASSWORD",paw));
		return getJSONObject(getRequest(BASE_URL+"FindServer;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵķ������ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyServiceData(String roomNo) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("roomNo",roomNo));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
//		params.add(new BasicNameValuePair("PASSWORD",paw));
		return getJSONObject(getRequest(BASE_URL+"FindServerStatus;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�˵��������ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMenuTypeData(String storeID) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeID",storeID));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
//		params.add(new BasicNameValuePair("PASSWORD",paw));
		return getJSONObject(getRequest(BASE_URL+"storeProducts;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�˵������������ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMenuTypeData2(String storeID,String typeid) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeID",storeID));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		params.add(new BasicNameValuePair("typeid",typeid));
		return getJSONObject(getRequest(BASE_URL+"storeProducts2;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�˵������������ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMenuTypeData3(String storeID,String typeid) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeID",storeID));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		params.add(new BasicNameValuePair("typeid",typeid));
		return getJSONObject(getRequest(BASE_URL+"storeProducts3;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�˵������µĲ˵����ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMenuData(String productTypeId,String sid,String step,String sortStr,int page) throws JSONException, Exception {
//		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
//			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid",sid));
		params.add(new BasicNameValuePair("step",step));
		params.add(new BasicNameValuePair("productTypeId",productTypeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		params.add(new BasicNameValuePair("sortStr",sortStr));
		params.add(new BasicNameValuePair("page",String.valueOf(page)));
		return getJSONObject(getRequest(BASE_URL+"FindProducts;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�˵������µĲ˵����ݼ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getProductsDetil(String pid) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pid",pid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getProductsDetil;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���̨���ͷ�������
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject setRoomService(String roomNo,String content) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("roomNo",roomNo));
		params.add(new BasicNameValuePair("srverContent",content));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"InsertServerStatus;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���̨���ͽ�����������
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject endRoomService(String sId) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("sId",sId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"DeleteServerStatus;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���̨���Ϳ��˵�Ķ���
	 * @param jsonstr
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject addMyOrderItem(String jsonstr,String address,String desc,String payment,String sendtime,String invoiceTitle,String invoiceType,String businessId) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("jsonstr",jsonstr));
		params.add(new BasicNameValuePair("address",address));
		params.add(new BasicNameValuePair("desc",desc));
		params.add(new BasicNameValuePair("payment",payment));
		params.add(new BasicNameValuePair("sendtime",sendtime));
		params.add(new BasicNameValuePair("invoiceTitle",invoiceTitle));
		params.add(new BasicNameValuePair("invoiceType",invoiceType));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		params.add(new BasicNameValuePair("businessId",businessId));
		return getJSONObject(getRequest(BASE_URL+"InsertExtraConsumptions;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ѯ���÷�������е���˵����Ѽ�¼��ÿ���˵�״̬
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyMenuDataList(String roomNo) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("roomNo",roomNo));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"SelectExtraConsumptions;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����˵��б�
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getBillListData(String starTime,String endTime,String storeID) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("starTime",starTime));
		params.add(new BasicNameValuePair("endTime",endTime));
		params.add(new BasicNameValuePair("profileID",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeID",storeID));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"SelectAllConsume;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����˵���ϸ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getBillDetatil(String totalid) throws JSONException, Exception {
		if(myapp.getSessionId() == null || myapp.getSessionId().equals(""))
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("conID",totalid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"SelectConsume;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * δע���û���¼
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject unregisteredLogin(String lname,String pawstr) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("UserName",lname));
		params.add(new BasicNameValuePair("Password",pawstr));
		params.add(new BasicNameValuePair("bid",myapp.getBusinessid()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		String isservice = "1";
		if(myapp.getIsServer())
			isservice = "0";
		params.add(new BasicNameValuePair("isService",isservice));
		return getJSONObject(getRequest(BASE_URL+"login2",params));
	}
	
	/**
	 * �û���¼
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject login(String lname,String pawstr) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("UserName",lname));
		params.add(new BasicNameValuePair("Password",pawstr));
		params.add(new BasicNameValuePair("bid",myapp.getBusinessid()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequestGet(BASE_URL+"login2",params));
	}
	
	/**
	 * �����û�ע����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject saveRegister(String lname,String emailstr,String pawstr) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("lname",lname));
		params.add(new BasicNameValuePair("email",emailstr));
		params.add(new BasicNameValuePair("pawstr",pawstr));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"saveRegister",params));
	}
	
	/**
	 * ��������ŵ����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStoreAll(String typeid,String meter,String filterContent,String score,String flocation) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("typeID",typeid));
		params.add(new BasicNameValuePair("meter",meter));
		params.add(new BasicNameValuePair("filterContent",filterContent));
		params.add(new BasicNameValuePair("score",score));
		params.add(new BasicNameValuePair("flocation",flocation));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("swoof",myapp.getSlat()));
		params.add(new BasicNameValuePair("slong",myapp.getSlng()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		params.add(new BasicNameValuePair("centerLat",myapp.getCenterLat()));
		params.add(new BasicNameValuePair("centerLng",myapp.getCenterLng()));
		params.add(new BasicNameValuePair("mapZoom",String.valueOf(myapp.getMapZoom())));
		params.add(new BasicNameValuePair("latspan",String.valueOf(myapp.getLatspan())));
		params.add(new BasicNameValuePair("longspan",String.valueOf(myapp.getLongspan())));
//		return getJSONObject(getRequest(BASE_URL+"SelectStoreTypes;jsessionid="+myapp.getSessionId(),params));
		return getJSONObject(getRequest(BASE_URL+"SelectStoreTypesForBaidu;jsessionid="+myapp.getSessionId(),params));
	}
	
	
	/**
	 * ��������ŵ����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStorePeripheryAll(String typeid,String meter,String lat,String lng) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("typeID",typeid));
		params.add(new BasicNameValuePair("meter",meter));
		params.add(new BasicNameValuePair("mywoof",lat));
		params.add(new BasicNameValuePair("mylong",lng));
		params.add(new BasicNameValuePair("filterContent",""));
		params.add(new BasicNameValuePair("score","All"));
		params.add(new BasicNameValuePair("flocation",""));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"SelectStoreTypesForBaidu;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��������ŵ����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStorePeripheryAll3(String typeid,String meter,String lat,String lng) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("typeID",typeid));
		params.add(new BasicNameValuePair("meter",meter));
		params.add(new BasicNameValuePair("mywoof",lat));
		params.add(new BasicNameValuePair("mylong",lng));
		params.add(new BasicNameValuePair("filterContent",""));
		params.add(new BasicNameValuePair("score","All"));
		params.add(new BasicNameValuePair("flocation",""));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"SelectStoreTypesForBaidu3;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �����û���ӻ�Ա��
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject addCards(String profileid,String stroid,String businessid,String storeType,String province) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("id",profileid));
		params.add(new BasicNameValuePair("stroid",stroid));
		params.add(new BasicNameValuePair("businessid",businessid));
		params.add(new BasicNameValuePair("storeId",storeType));
		params.add(new BasicNameValuePair("province",province));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"addCards;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û��Ƿ��иõ��Ա��
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject isSelectCards(String profileid,String stroid,String businessid,String storeId) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("id",profileid));
		params.add(new BasicNameValuePair("stroid",stroid));
		params.add(new BasicNameValuePair("businessid",businessid));
		params.add(new BasicNameValuePair("storeId",storeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"isSelectCards;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ������е������
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStoreTypeAll() throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getStoreTypeAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ĳ�����Ͷ���
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStoreTypeAll(String type) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("type",type));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getStoreTypeAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û������л�Ա��
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyCardsAll(String tag) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("appstoreid",myapp.getAppstoreid()));
//		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		return getJSONObject(getRequestGet(BASE_URL+"getMyCardsAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û���ĳһ�Ż�Ա����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyOneCardInfo(String tag,String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("storeid",storeid));
//		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		return getJSONObject(getRequestGet(BASE_URL+"getMyCardsAll;jsessionid="+myapp.getSessionId(),params));
	}
	
    /**
     * �����ŵ�ID���ظ�vip����Ϣ
     * @param storeid
     * @return
     * @throws JSONException
     * @throws Exception
     */
	public JSONObject getCards(String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getCards;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
     * �����ŵ�ID���ظ�vip����Ϣ
     * @param storeid
     * @return
     * @throws JSONException
     * @throws Exception
     */
	public JSONObject getNfcUser(String storeid,String pfid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",pfid));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getCards;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
     * NFC�����ŵ�ID���ظ�vip����Ϣ
     * @param storeid
     * @return
     * @throws JSONException
     * @throws Exception
     */
	public JSONObject getNfcStore(String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getNfcStore;jsessionid="+myapp.getSessionId(),params));
	}
	
	
	/**
	 * ������л�Ա��
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getCardsAll(String typeid,String meter) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("typeID",typeid));
		params.add(new BasicNameValuePair("meter",meter));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getCardsAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ָ����Ա����Ϣ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getCardDetail(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",sid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getCardDetail;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øû�Ա���������Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getCouponAll(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",sid));
		params.add(new BasicNameValuePair("lat",myapp.getLat()));
		params.add(new BasicNameValuePair("lng",myapp.getLng()));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
//		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		return getJSONObject(getRequest(BASE_URL+"selectCoupon;jsessionid="+myapp.getSessionId(),params));
	}
	
	public JSONObject getGPSOffset(String lat,String lng) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("lat",lat));
		params.add(new BasicNameValuePair("lng",lng));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getGPSOffset;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øû�Ա���������Ѿ����ص��Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyCouponAll(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",sid));
		params.add(new BasicNameValuePair("lat",myapp.getLat()));
		params.add(new BasicNameValuePair("lng",myapp.getLng()));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"selectMyCoupon;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øû�Ա���������Ѿ����ص��Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject updateCoupon(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("couponNo",sid));
		params.add(new BasicNameValuePair("pid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"updateCoupon;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �����Ż�ȯʹ��״̬
	 * @param sid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject updateCouponStart(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("couponLoadId",sid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"updateCouponStart;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øû�Ա���й�ע�Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getGuanZuCouponAll() throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"selectCouponGz;jsessionid="+myapp.getSessionId(),params));
	}
	/**
	 * ��øû�Ա���������Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getSrotCommentData(String sid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",sid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getSrotCommentData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �����Ż�ȯ��Ϊ�ҹ�ע���Ż�ȯ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject isASttention(String cid,String tag) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pkid",cid));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"isASttention;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ȡ���Ը��Ż�ȯ�Ĺ�ע
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject notASttention(String cid,String tag) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pkid",cid));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"notASttention;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���̼ҵĶ���״̬
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOrderItem(String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getMyOrderStatus;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���̼ҵĶ�����ϸ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOrderDetailsItem(String orderid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("orderid",orderid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getOrderDetailsItem;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���̼ҵĶ�����ϸ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOrderHistoryDetailsItem(String orderid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("orderid",orderid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getOrderHistoryDetailsItem;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���̼ҵĶ�����ʷ��ϸ
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOrderDetailsHItem(String orderhid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("orderhid",orderhid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getOrderDetailsHItem;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ȡ�����̼ҵĶ���
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOrderStatusChange(String orderid,String type,String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("orderid",orderid));
		params.add(new BasicNameValuePair("type",type));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getOrderStatusChange;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþƵ��̼��µ�����Ԥ���ķ���
	 * @param storeid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getHotelReservation(String storeid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getHotelReservation;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ӾƵ�Ԥ����¼
	 * @param jsonstr
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject addHotolReservation(String jsonstr)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("jsonstr",jsonstr));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"addHotolReservation;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþƵ��̼����ҵ�����Ԥ���ķ���
	 * @param storeid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyHotelReservation(String storeid,String stime,String etime) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("stime",stime));
		params.add(new BasicNameValuePair("etime",etime));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getMyHotelReservation;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øò�Ʒ������ͼƬ
	 * @param storeid
	 * @param stime
	 * @param etime
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getProductImageList(String productid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("productid",productid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequestGet(BASE_URL+"getProductImageList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øò�Ʒ����������
	 * @param storeid
	 * @param stime
	 * @param etime
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getProductCommentList(String productid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("productid",productid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getProductCommentList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��Ӹò�Ʒ������
	 * @param storeid
	 * @param stime
	 * @param etime
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject addProudectComment(String storeid,String score,String message,String summary,String productid,String ohdid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("score",score));
		params.add(new BasicNameValuePair("message",message));
		params.add(new BasicNameValuePair("summary",summary));
		params.add(new BasicNameValuePair("pid",productid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("ohdid",ohdid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"addProudectComment;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øò�Ʒ����ɫ
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getProductColorList(String pid)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("productid",pid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getProductColorList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øò�Ʒ��Ʒζ
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getProductTasteList(String pid)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("productid",pid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getProductTasteList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øò�Ʒ�����
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getProductSideDishesList(String pid)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("productid",pid));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getProductSideDishesList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û������е�ַ
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getPfAddressList()throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getPfAddressList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��̼�������˾
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getLogisticsList(String storeId)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeId",storeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getLogisticsList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��̼�������˾
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getOtherStoreAll(String businessId,String storeid)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("businessId",businessId));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getOtherStoreAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �޸��û�����
	 * @param oldpaw 
	 * @param newpaw
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject updateUserPassword(String oldpaw,String newpaw) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("oldpaw",oldpaw));
		params.add(new BasicNameValuePair("newpaw",newpaw));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"updateUserPassword;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø����ξ����б�
	 * @param storeid �ŵ�Id
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getTravelList(String storeid,int page) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pageindex",String.valueOf(page)));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getTravelList;jsessionid="+myapp.getSessionId(),params));
	}
	public JSONObject getTravelList(String storeid,String str) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pageindex",str));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getTravelList;jsessionid="+myapp.getSessionId(),params));
	}
	public JSONObject getTravelList(String storeid,String str,String landscapeId) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("mywoof",myapp.getLat()));
		params.add(new BasicNameValuePair("mylong",myapp.getLng()));
		params.add(new BasicNameValuePair("storeid",storeid));
		params.add(new BasicNameValuePair("pageindex",str));
		params.add(new BasicNameValuePair("landscapeId",landscapeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getTravelList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþ����μ��б�
	 * @param lvid
	 * @param page
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getTravelNotesList(String lvid,int page) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("lvid",lvid));
		params.add(new BasicNameValuePair("pageindex",String.valueOf(page)));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getTravelNotesList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþ��������б�
	 * @param lvid
	 * @param page
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getTravelCommentList(String lsid,int page,String tnid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("lsid",lsid));
		if(tnid != null)
			params.add(new BasicNameValuePair("tnid",tnid));
		params.add(new BasicNameValuePair("pageindex",String.valueOf(page)));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getTravelCommentList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþ����µ������Ƽ�·��
	 * @param landid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getRecommendAllWay(String landid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeInfo",landid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"recommendAllWay;jsessionid="+myapp.getSessionId(),params));
	}
	
	public JSONObject getPointJZS(String jsonstr) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("jsonstr",jsonstr));
		return getJSONObject(getRequest(BASE_URL+"getPointJZS;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �Ƴ�����session
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject destroySession() throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"destroySession;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���Ʊ����Ϣ
	 * */
	public JSONObject getTicketsInfomation(String start,String end,String type) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("startStation", start));
		params.add(new BasicNameValuePair("endStation",end));
		params.add(new BasicNameValuePair("type",type));
		return getJSONObject(getRequest(BASE_URL+"getTicketsInfomations;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ��Ա���ּ�¼
	 * */
	public JSONObject outIntergralHistory(String page,String startime,String endtime) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid", myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("pageindex", page));
		params.add(new BasicNameValuePair("startime", startime));
		params.add(new BasicNameValuePair("endtime", endtime));
		return getJSONObject(getRequest(BASE_URL+"outIntergralHistory;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�̼һ�����Ʒ����
	 * */
	public JSONObject outIntergralType(String cardId) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("cardId", cardId));
		return getJSONObject(getRequest(BASE_URL+"outbusIntergralgoodType;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �һ���Ʒ ���»���
	 * */
	public JSONObject upadteIntergralgood(String jsons) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("jsons", jsons));
		return getJSONObject(getRequest(BASE_URL+"upadteIntergralgood;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�̼һ�����Ʒ
	 * */
	public JSONObject outbusIntergralgood(String cardid,String typeid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("cardid", cardid));
		params.add(new BasicNameValuePair("typeid", typeid));
		return getJSONObject(getRequest(BASE_URL+"outbusIntergralgood;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ɾ����ַ
	 * */
	public JSONObject deleteAddress(String addressid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("addressid", addressid));
		return getJSONObject(getRequest(BASE_URL+"deleteAddress;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���µ�ַ
	 * */
	public JSONObject updateAddress(String addressid,String jsonstr) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("addressid", addressid));
		params.add(new BasicNameValuePair("jsonstr", jsonstr));
		return getJSONObject(getRequest(BASE_URL+"updateAddress;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ӵ�ַ
	 * */
	public JSONObject addAddress(String addressid,String jsonstr,String storeid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("addressid", addressid));
		params.add(new BasicNameValuePair("jsonstr", jsonstr));
		params.add(new BasicNameValuePair("storeid", storeid));
		return getJSONObject(getRequest(BASE_URL+"addAddress;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����Ĭ�ϵ�ַ
	 * */
	public JSONObject updateAddressDefult(String addressid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("addressid", addressid));
		params.add(new BasicNameValuePair("pfid", myapp.getPfprofileId()));
		return getJSONObject(getRequest(BASE_URL+"updateAddressDefult;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����Ĭ�ϵ�ַ
	 * */
	public JSONObject loadAddressDefult() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid", myapp.getPfprofileId()));
		return getJSONObject(getRequest(BASE_URL+"loadAddressDefult;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���м���
	 * */
	public JSONObject getCascadeData(String moduleid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("moduleid", moduleid));
		params.add(new BasicNameValuePair("pfid", myapp.getPfprofileId()));
		return getJSONObject(getRequest(BASE_URL+"getCascadeData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ñ��οɶԻ�������
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyPoints(String storeId,String curpoints,String peices)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeDNames",storeId));
		params.add(new BasicNameValuePair("cur_points",curpoints));
		params.add(new BasicNameValuePair("eventDTypes","4"));
		params.add(new BasicNameValuePair("peices",peices));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"mathematics;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ñ��ζ����ɶԻ�����/����
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getMyOrderPoints(String storeId,String moneys,String time)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeDNames",storeId));
		params.add(new BasicNameValuePair("time",time));
		params.add(new BasicNameValuePair("moneys",moneys));
		params.add(new BasicNameValuePair("eventid","01"));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"mathematics;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��̼�֧����ʽ
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getStorePayList(String storeId)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeId",storeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getStorePayList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþƵ���ʩ�б�
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getHotelFacilityList(String storeId)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeId",storeId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequestGet(BASE_URL+"getHotelFacilityList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��øþƵ��û������б�
	 * @param pid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getReviewList(String storeId,String pageindex,String tage)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeId",storeId));
		params.add(new BasicNameValuePair("pageindex",pageindex));
		params.add(new BasicNameValuePair("tage",tage));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getReviewList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����Ԥ��
	 * @param storeid
	 * @param user
	 * @param sex
	 * @param phone
	 * @param dtime
	 * @param muble
	 * @param roomtype
	 * @param content
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject saveOrUpdateOrders(String storeid,String user,String sex,String phone,String dtime,String muble,String roomtype,String content)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeName",storeid));
		params.add(new BasicNameValuePair("customerName",user));
		params.add(new BasicNameValuePair("sex",sex));
		params.add(new BasicNameValuePair("tel",phone));
		params.add(new BasicNameValuePair("orderTime",dtime));
		params.add(new BasicNameValuePair("number",muble));
		params.add(new BasicNameValuePair("roomHotelStyle",roomtype));
		params.add(new BasicNameValuePair("message",content));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"saveOrUpdateOrders;jsessionid="+myapp.getSessionId(),params));
	}
	
	public JSONObject getConfirmOrder(String orderId)throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("orderid",orderId));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"confirmProduct;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ú͸��û������������Ϣ�б�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyMessageListData(String fromname,String toname,boolean wifistart,boolean inetnState,int page,String tag) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("page",String.valueOf(page)));
		params.add(new BasicNameValuePair("tag",tag));
		return getJSONObject(getRequest(BASE_URL+"getMyMessagesDataList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û�����δ�յ�����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyUnreadMessageListData(String userid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
//		if(!inetnState)
//			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("userid",userid));
		return getJSONObject(getRequestGet(BASE_URL+"getUnreadMessages;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û�����δ�յ��Ĺ�����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyUnreadGonGaoMessageListData() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
//		if(!inetnState)
//			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("userid",myapp.getPfprofileId()));
		return getJSONObject(getRequestGet(BASE_URL+"getUnreadGonGaoMessages;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ø��û�����δ�յ�������Ȧ��Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyUnreadPenyouMessageListData() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
//		if(!inetnState)
//			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("userid",myapp.getPfprofileId()));
		return getJSONObject(getRequestGet(BASE_URL+"getUnreadPenyouMessages;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ɾ�����û������������Ϣ�б�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject delMyMessagesDataList(String fromname,String toname) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
//		if(!inetnState)
//			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		return getJSONObject(getRequest(BASE_URL+"delMyMessagesDataList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ĳ���û���Ե㷢��
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject sendOneMessage(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,String sessionid,boolean wifistart,boolean inetnState) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("fname",fname));
		params.add(new BasicNameValuePair("tname",tname));
		params.add(new BasicNameValuePair("toneetyid",toneetyid));
		params.add(new BasicNameValuePair("sendstatus",sendstatus));
		params.add(new BasicNameValuePair("content",content));
		return getJSONObject(getRequest(BASE_URL+"sendToMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ĳ���̼ҷ���
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject sendAutomaticMessage(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,int points,String sessionid,boolean wifistart,boolean inetnState,String filetype,String time,String mid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("fname",fname));
		params.add(new BasicNameValuePair("tname",tname));
		params.add(new BasicNameValuePair("toneetyid",toneetyid));
		params.add(new BasicNameValuePair("sendstatus",sendstatus));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("mid",mid));
		params.add(new BasicNameValuePair("filetype",filetype));
		params.add(new BasicNameValuePair("time",time));
		params.add(new BasicNameValuePair("messagetype",""));
		params.add(new BasicNameValuePair("points",String.valueOf(points)));
		params.add(new BasicNameValuePair("nettyid",String.valueOf(myapp.getNettyid())));
		
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		
		return getJSONObject(getRequest(BASE_URL+"sendAutomaticMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ҵĺ��ѷ�����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject sendFriendsMessage(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,int points,String sessionid,boolean wifistart,boolean inetnState,String mid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("fname",fname));
		params.add(new BasicNameValuePair("tname",tname));
		params.add(new BasicNameValuePair("toneetyid",toneetyid));
		params.add(new BasicNameValuePair("sendstatus",sendstatus));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("mid",mid));
		params.add(new BasicNameValuePair("points",String.valueOf(points)));
		params.add(new BasicNameValuePair("nettyid",String.valueOf(myapp.getNettyid())));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		
		return getJSONObject(getRequestGet(BASE_URL+"sendFriendsMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ҵ�Ⱥ�ĺ��ѷ�����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject sendFriendGroupMessage(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,int points,String sessionid,boolean wifistart,boolean inetnState,String mid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("fname",fname));
		params.add(new BasicNameValuePair("tname",tname));
		params.add(new BasicNameValuePair("toneetyid",toneetyid));
		params.add(new BasicNameValuePair("sendstatus",sendstatus));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("mid",mid));
		params.add(new BasicNameValuePair("points",String.valueOf(points)));
		params.add(new BasicNameValuePair("nettyid",String.valueOf(myapp.getNettyid())));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		
		return getJSONObject(getRequestGet(BASE_URL+"sendFriendGroupMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ҵĿͻ�������Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject sendServiceMessage(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,int points,String sessionid,boolean wifistart,boolean inetnState,String watag,String mid,String fromid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		if(!inetnState)
			return null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("fromname",fromname));
		params.add(new BasicNameValuePair("toname",toname));
		params.add(new BasicNameValuePair("fname",fname));
		params.add(new BasicNameValuePair("tname",tname));
		params.add(new BasicNameValuePair("toneetyid",toneetyid));
		params.add(new BasicNameValuePair("sendstatus",sendstatus));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("mid",mid));
		params.add(new BasicNameValuePair("formid",fromid));
		params.add(new BasicNameValuePair("points",String.valueOf(points)));
		params.add(new BasicNameValuePair("nettyid",String.valueOf(myapp.getNettyid())));
		String language = Locale.getDefault().getLanguage();
		params.add(new BasicNameValuePair("language",language));
		params.add(new BasicNameValuePair("watag",watag));
		
		return getJSONObject(getRequestGet(BASE_URL+"sendServiceMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �̻����˻ش�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject createQuestionsAnswered(String questions,String answered,String type,String storeid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("questions",questions));
		params.add(new BasicNameValuePair("answered",answered));
		params.add(new BasicNameValuePair("type",type));
		params.add(new BasicNameValuePair("storeid",storeid));
		
		return getJSONObject(getRequest(BASE_URL+"createQuestionsAnswered;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ʾû�ܴ��������
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject findLCanNotAnswerucene(String pageNo) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pageNo",pageNo));
		params.add(new BasicNameValuePair("pageSize","10"));
		
		return getJSONObject(getRequest(BASE_URL+"findLCanNotAnswerucene;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ɾ��û�ܴ��������
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject delLCanNotAnsweruceneIndex(String pkid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pkids",pkid));
		
		return getJSONObject(getRequest(BASE_URL+"delLCanNotAnsweruceneIndex;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ش���������
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getAnswerTypeList() throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"getAnswerTypeList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ע���귢�͸�л��Ϣ
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject registereSendMessage(String pfid,String sotreid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",pfid));
		params.add(new BasicNameValuePair("sotreid",sotreid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"registereSendMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	public JSONObject notStoreUnfollow(String pkid,String storeid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pkid",pkid));
		params.add(new BasicNameValuePair("storeid",storeid));
		return getJSONObject(getRequest(BASE_URL+"notStoreUnfollow;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��Ϣ������û���ӻ�Ա��
	 * @param roomNo
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject addCardsMessage(String profileid,String stroid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("id",profileid));
		params.add(new BasicNameValuePair("stroid",stroid));
		params.add(new BasicNameValuePair("language",myapp.getLanguage()));
		return getJSONObject(getRequest(BASE_URL+"addCardsMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�þƵ�������ע���û�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getStorepfprofileAll(String key,String tag) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("storeid",myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("businessid",myapp.getBusinessid()));
		params.add(new BasicNameValuePair("key",key));
		params.add(new BasicNameValuePair("tag",tag));
		
		return getJSONObject(getRequest(BASE_URL+"getStorepfprofileAll;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ú�����ӵ�ͨѶ¼��
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject deleteMyFriendData(String delpfid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("delpfid",delpfid));
		
		return getJSONObject(getRequest(BASE_URL+"deleteMyFriendData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ú�����ӵ�ͨѶ¼��
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject addMyFriendData(String addpfid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("addpfid",addpfid));
		
		return getJSONObject(getRequest(BASE_URL+"addMyFriendData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �޸ĸ�����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject upadateUserInfo(String username,String account,String sex,String area,String signature) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("account",account));
		params.add(new BasicNameValuePair("sex",sex));
		params.add(new BasicNameValuePair("area",area));
		params.add(new BasicNameValuePair("signature",signature));
		
		return getJSONObject(getRequest(BASE_URL+"upadateUserInfo;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ͬ��������Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject synchronousFriendInfo(String pfid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",pfid));
		
		
		return getJSONObject(getRequest(BASE_URL+"getPfprofileUserInfoData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ������֤����
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject addVerificationMessage(String acceptpfid,String message) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("acceptpfid",acceptpfid));
		params.add(new BasicNameValuePair("message",message));
		
		return getJSONObject(getRequest(BASE_URL+"addVerificationMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵ�������֤����
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject selectVerificationMessage() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		
		return getJSONObject(getRequestGet(BASE_URL+"selectVerificationMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ɾ�������ҵ�����
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject deleteMyVerificationMessage() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("acceptpfid",myapp.getPfprofileId()));
		
		return getJSONObject(getRequest(BASE_URL+"deleteMyVerificationMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �ı���֤����״̬
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject upadateVerificationMessageStart(String start,String requestpfid,String requestname,String acceptname) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("requestpfid",requestpfid));
		params.add(new BasicNameValuePair("requestname",requestname));
		params.add(new BasicNameValuePair("acceptpfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("acceptname",acceptname));
		params.add(new BasicNameValuePair("start",start));
		
		return getJSONObject(getRequest(BASE_URL+"upadateVerificationMessageStart;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵĺ����б�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject selectMyFriendList(String tag) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("tag",tag));
		return getJSONObject(getRequest(BASE_URL+"selectMyFriendList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵĺ����б�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject selectMyFriendList(String tag,String friendid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("friendid",friendid));
		return getJSONObject(getRequest(BASE_URL+"selectMyFriendList;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵĺ����б�
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject selectUnreadVerificationMessage() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		
		return getJSONObject(getRequestGet(BASE_URL+"selectUnreadVerificationMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�ҵĺ�������Ȧ��Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getMyFriendsMoments(String tag,String page,String type) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("mypfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("tag",tag));
		params.add(new BasicNameValuePair("pageNo",page));
		params.add(new BasicNameValuePair("publishType", type));
		params.add(new BasicNameValuePair("userType", myapp.getIsServer()?"1":"2"));
		params.add(new BasicNameValuePair("storeid", myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("businessid", myapp.getBusinessid()));
		
		return getJSONObject(getRequest(BASE_URL+"selectMyfrendMoments;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���̼ҵ�����ͼƬ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getStoreImageListData(String storeid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("storeid",storeid));
		
		return getJSONObject(getRequest(BASE_URL+"getStoreImageListData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ȷ���Ѿ��յ�����������������Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject confirmPusMessage(String pusid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pusid",pusid));
		
		return getJSONObject(getRequestGet(BASE_URL+"confirmPusMessage;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ���û�������ʷ��Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getUserOrderHistory(String openid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("openid",openid));
		
		return getJSONObject(getRequestGet(BASE_URL+"getUserOrderHistory;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡЭ���û�������ʷ��Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject getUserAgreementOrderHistory() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",myapp.getAppstoreid()));
		
		return getJSONObject(getRequestGet(BASE_URL+"getUserAgreementOrderHistory;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ����û�Ⱥ����
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject andGroupMessageData(String gname,String gids) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("groupname",gname));
		params.add(new BasicNameValuePair("groupids",gids));
		
		return getJSONObject(getRequestGet(BASE_URL+"andGroupMessageData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * �����û�Ⱥ����
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject updateGroupMessageData(String gname,String gids,String gid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("groupname",gname));
		params.add(new BasicNameValuePair("groupids",gids));
		params.add(new BasicNameValuePair("groupid",gid));
		
		return getJSONObject(getRequestGet(BASE_URL+"updateGroupMessageData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ������ID��ȡ�����������ID������
	 * @param groupid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getGroupNameIdData(String groupid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("groupid",groupid));
		
		return getJSONObject(getRequestGet(BASE_URL+"getGroupNameIdData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ���ݱ���ͨѶ¼��Ӻ���
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getPhoneUserContacts(String usercontactjson) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("usercontactjson",usercontactjson));
		params.add(new BasicNameValuePair("bid",myapp.getBusinessid()));
		
		return getJSONObject(getRequestGet(BASE_URL+"getPhoneUserContacts;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ������ID��ȡ�����������ID������
	 * @param groupid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject updateGroupName(String groupid,String groupname) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		params.add(new BasicNameValuePair("groupid",groupid));
		params.add(new BasicNameValuePair("groupname",groupname));
		
		return getJSONObject(getRequestGet(BASE_URL+"updateUserImageData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ͬ��Ⱥ����Ϣ
	 * @return JSONArray
	 * @throws JSONException 
	 * @throws ConnectionException 
	 */
	public JSONObject synchronousGroupInfo(String groupid) throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("groupid",groupid));
		
		
		return getJSONObject(getRequest(BASE_URL+"getGroupUserInfoData;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡandroid���°汾
	 * @param groupid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getAndroidVersionNew() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",myapp.getAppstoreid()));
		
		return getJSONObject(getRequestGet(BASE_URL+"getAndroidVersionNew;jsessionid="+myapp.getSessionId(),params));
	}
	
	/**
	 * ��ȡ�����ŵ�ͷ���
	 * @param groupid
	 * @return
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONObject getServiceAndHoteleList() throws JSONException, Exception {
		//http://api.douban.com/movie/subject/2277018?alt=json
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("storeid",myapp.getAppstoreid()));
		params.add(new BasicNameValuePair("bid",myapp.getBusinessid()));
		params.add(new BasicNameValuePair("pfid",myapp.getPfprofileId()));
		return getJSONObject(getRequestGet(BASE_URL+"getServiceAndHoteleList;jsessionid="+myapp.getSessionId(),params));
	}
	
	
	public String addParam(String fromname,String toname,String fname,String tname,String toneetyid,String sendstatus,String content,String filetype,String messagetype,String watag,String mid,String formid) {  
		String str = "";
		try{
			str = "fromname="+URLEncoder.encode(fromname,"UTF-8")+"&toname="+URLEncoder.encode(toname,"UTF-8")+"&fname="+URLEncoder.encode(fname,"UTF-8")+"&tname="+URLEncoder.encode(tname,"UTF-8")+"&toneetyid="+toneetyid+"&sendstatus="+sendstatus+"&content="+URLEncoder.encode(content,"UTF-8")+"&filetype="+filetype+"&messagetype="+messagetype+"&watag="+watag+"&mid="+mid+"&formid="+formid;
			Log.i("=====pstr=====", str);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
	}
	
//	/**
//	 * �򷹷�api����get����url�谴�շ���apiҪ��д�����شӷ���ȡ�õ���Ϣ��
//	 * 
//	 * @param url
//	 * @return String
//	 */
//	protected String getRequest(String url,List<NameValuePair> params) throws Exception {
//		System.out.println("url==="+url);
//		return getRequest(url,params, new DefaultHttpClient(new BasicHttpParams()));
//	}
	
	public JSONObject getNfcCardData(String cuid) throws JSONException, Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();  
		params.add(new BasicNameValuePair("cuid",cuid));
//		params.add(new BasicNameValuePair("PASSWORD",paw));
		return getJSONObject(getRequest(BASE_URL+"getNfcCardData",params));
	}
	
	/**
	 * ��api����get����url�谴��apiҪ��д�����شӷ���ȡ�õ���Ϣ��
	 * 
	 * @param url
	 * @param client
	 * @return String
	 */
	protected String getRequest(String url, List<NameValuePair> params) throws Exception {
		String result = null;
//		int statusCode = 0;
//		HttpPost getMethod = new HttpPost(url);
//		getMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		long starttime = System.currentTimeMillis();
		System.out.println("sarttime=="+starttime);
		Log.d(TAG, "do the getRequest,url="+url+"");
		try {
//			getMethod.setHeader("User-Agent", USER_AGENT);
//			//����û�������֤��Ϣ
////	    	client.getCredentialsProvider().setCredentials(
////	    			new AuthScope(null, -1),
////	    			new UsernamePasswordCredentials(mUsername, mPassword));
//			
//			HttpParams httpParameters = new BasicHttpParams(); 
//			int timeoutConnection = 10000; 
//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
//			int timeoutSocket = 10000; 
//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
//			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
//			
//			System.out.println("==========httpResponse");
//			HttpResponse httpResponse = httpClient.execute(getMethod);
////			HttpResponse httpResponse = client.execute(getMethod);
//			//statusCode == 200 ����
//			statusCode = httpResponse.getStatusLine().getStatusCode();
////			Log.d(TAG, "statuscode = "+statusCode);
////			//�����ص�httpResponse��Ϣ
//			result = retrieveInputStream(httpResponse.getEntity());
			result = httpClientToServer.doPost(url, params);
//			result = httpClientToServer.doGet(url, params);
			System.out.println("sarttime2=="+(System.currentTimeMillis()-starttime));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
//			result = "timeout";
//			throw new Exception(e);
			return null;
		} finally {
//			getMethod.abort();
		}
		return result;
	}
	
	protected String getRequestGet(String url, List<NameValuePair> params) throws Exception {
		String result = null;
//		int statusCode = 0;
//		HttpPost getMethod = new HttpPost(url);
//		getMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		long starttime = System.currentTimeMillis();
		System.out.println("sarttime=="+starttime);
		Log.d(TAG, "do the getRequest,url="+url+"");
		try {
//			getMethod.setHeader("User-Agent", USER_AGENT);
//			//����û�������֤��Ϣ
////	    	client.getCredentialsProvider().setCredentials(
////	    			new AuthScope(null, -1),
////	    			new UsernamePasswordCredentials(mUsername, mPassword));
//			
//			HttpParams httpParameters = new BasicHttpParams(); 
//			int timeoutConnection = 10000; 
//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
//			int timeoutSocket = 10000; 
//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
//			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters); 
//			
//			System.out.println("==========httpResponse");
//			HttpResponse httpResponse = httpClient.execute(getMethod);
////			HttpResponse httpResponse = client.execute(getMethod);
//			//statusCode == 200 ����
//			statusCode = httpResponse.getStatusLine().getStatusCode();
////			Log.d(TAG, "statuscode = "+statusCode);
////			//�����ص�httpResponse��Ϣ
//			result = retrieveInputStream(httpResponse.getEntity());
//			result = httpClientToServer.doGet(url, params);
			result = httpClientToServer.doPost(url, params);
			System.out.println("sarttime2=="+(System.currentTimeMillis()-starttime));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
//			result = "timeout";
//			throw new Exception(e);
			return null;
		} finally {
//			getMethod.abort();
		}
		return result;
	}
	
	/**
	 * ����httpResponse��Ϣ,����String
	 * 
	 * @param httpEntity
	 * @return String
	 */
	protected String retrieveInputStream(HttpEntity httpEntity) {
		Long l = httpEntity.getContentLength();		
		int length = (int) httpEntity.getContentLength();		
		//the number of bytes of the content, or a negative number if unknown. If the content length is known but exceeds Long.MAX_VALUE, a negative number is returned.
		//length==-1��������䱨��println needs a message
		if (length < 0) length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		String newStr = "";
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
			newStr = new String (stringBuffer.toString().getBytes(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return newStr;
	}
	
	/* �ϴ��ļ���Server�ķ��� */
	public boolean uploadFile(String sessionid,String uploadFile,String fileName,String mto,String sendstatus,String content,long fileSize) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		boolean bf = true;
		try {
			String pstr = addParam(mto,sendstatus,content);
//			pstr = new String(pstr.getBytes("UTF-8"));
			String urlstr = BASE_URL+"sendMessageFile;jsessionid="+sessionid+"?"+pstr;
//			urlstr = URLEncoder.encode(urlstr,"UTF-8");
			URL url = new URL(urlstr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* ����Input��Output����ʹ��Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* ���ô��͵�method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* ����DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//			ds.write(addParam(mto,sendstatus,content).getBytes("UTF-8"));
//			ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; name=\"mto\""+ end);
//            ds.writeBytes(end + URLEncoder.encode(mto, "UTF-8")+ end);
//            
//            ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; name=\"sendstatus\""+ end);
//            ds.writeBytes(end + URLEncoder.encode(sendstatus, "UTF-8")+ end);
//            
//            ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; name=\"content\""+ end);
//            ds.writeBytes(end + URLEncoder.encode(content, "UTF-8")+ end);
			
            ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + fileName + "\"" + end);
			
			ds.writeBytes(end);

			String newFileUrl = compressionImage(uploadFile,fileSize);
			/* ȡ���ļ���FileInputStream */
			FileInputStream fStream = new FileInputStream(newFileUrl);
			/* ����ÿ��д��1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* ���ļ���ȡ������������ */
			while ((length = fStream.read(buffer)) != -1) {
				/* ������д��DataOutputStream�� */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
//			
//			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			
			fStream.close();
			ds.flush();

			/* ȡ��Response���� */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* ��Response��ʾ��Dialog */
//			showDialog("�ϴ��ɹ�" + b.toString().trim());
			/* �ر�DataOutputStream */
			ds.close();
			deleteFile(newFileUrl);
//			con.getOutputStream().close();
		} catch (Exception e) {
//			showDialog("�ϴ�ʧ��" + e);
			bf = false;
			e.printStackTrace();
		} 
		return bf;
	}
	
	//�฽���ϴ�
	public boolean uploadFiles(Map<String, String> params,Map<String, File> files)
	{
		boolean bf = true;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"sendCommentFile;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			JSONObject job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			if(job.has("success"))
			{
				String tag = job.getString("success");
				if(tag.equals("false"))
					bf = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}
	
	//�฽���ϴ�
	public boolean uploadFiles(Map<String, String> params,Map<String, File> files,String actionName)
	{
		boolean bf = true;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+actionName+";jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			JSONObject job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			if(job.has("success"))
			{
				String tag = job.getString("success");
				if(tag.equals("false"))
					bf = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}
	
	public boolean uploadFilesmssage(Map<String, String> params,Map<String, File> files,String sessionid)
	{
		boolean bf = true;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"sendSpackMessageFile;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			JSONObject job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			if(job.has("success"))
			{
				String tag = job.getString("success");
				if(tag.equals("false"))
					bf = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}
	
	public boolean uploadFilesGroupmssage(Map<String, String> params,Map<String, File> files,String sessionid)
	{
		boolean bf = true;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"sendSpackGroupMessageFile;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			JSONObject job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			if(job.has("success"))
			{
				String tag = job.getString("success");
				if(tag.equals("false"))
					bf = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}
	
	public boolean uploadFilesAutomaticMssage(Map<String, String> params,Map<String, File> files,String sessionid)
	{
		boolean bf = true;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"sendAutomaticMessage;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			JSONObject job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			if(job.has("success"))
			{
				String tag = job.getString("success");
				if(tag.equals("false"))
					bf = false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}
	
	//�฽���ϴ�
	public JSONObject uploadFilesRegister(Map<String, String> params,Map<String, File> files)
	{
		boolean bf = true;
		JSONObject job = null;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"saveRegister?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return job;
	}
	
	//�฽���ϴ�
	public JSONObject updateUserImg(Map<String, String> params,Map<String, File> files)
	{
		boolean bf = true;
		JSONObject job = null;
		try{
			String str = addParam2(params);
			String actionUrl = BASE_URL+"updateUserImg;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return job;
	}
	
	public JSONObject updateMomentsImg(Map<String, String> params,Map<String, File> files)
	{
		boolean bf = true;
		JSONObject job = null;
		try{
			String str = addParam2(params);
			//String actionUrl = BASE_URL+"uploadFileMore;jsessionid="+myapp.getSessionId()+"?"+str;
			String actionUrl = BASE_URL+"updateMomentsImg;jsessionid="+myapp.getSessionId()+"?"+str;
			Map<String, String> nparams = new HashMap<String, String>();
			job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
			//HttpFileUpTool.cutFileUpload("video", "mnt/sdcard/bqz.mp4", actionUrl);
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return job;
	}
	
	//�ϴ�����Ȧ����
	public JSONObject updatThemeImg(Map<String, String> params,
			Map<String, File> files) {
		boolean bf = true;
		JSONObject job = null;
		try {
			String str = addParam2(params);
			String actionUrl = BASE_URL + "updateMomentsThemeImg;jsessionid="
					+ myapp.getSessionId() + "?" + str;
			Map<String, String> nparams = new HashMap<String, String>();
			job = getJSONObject(HttpFileUpTool.post(actionUrl, nparams, files));
		} catch (Exception ex) {
			ex.printStackTrace();
			bf = false;
		}
		return job;
	}
	
	//�฽���ϴ�
	public boolean uploadFiles2(Map<String, String> params,FormFile[] files)
	{
		boolean bf = true;
		try{
			String actionUrl = BASE_URL+"sendCommentFile;jsessionid="+myapp.getSessionId();
			bf = SocketHttpRequester.post(actionUrl, params, files);
		}catch(Exception ex){
			ex.printStackTrace();
			bf = false;
		}
		return bf;
	}

	private String addParam(String mto,String sendstatus,String content) {  
		String str = "";
		try{
			str = "mto="+URLEncoder.encode(mto,"UTF-8")+"&sendstatus="+sendstatus+"&content="+URLEncoder.encode(content,"UTF-8");
			Log.i("=====pstr=====", str);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
	}
	
	public static String addParam2(Map<String, String> params) {  
		StringBuilder sb = new StringBuilder();
		try{
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if(value != null)
					value = URLEncoder.encode(entry.getValue(),"UTF-8");
				else
					value = "";
				if(i == 0)
				{
					sb.append(entry.getKey()+"="+value);
				}
				else
				{
					sb.append("&"+entry.getKey()+"="+value);
				}
				i++;
			}
			Log.i("=====pstr=====", sb.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return sb.toString();
	}
	
	private String compressionImage(String oldFileUrl,long fileSize){
		String newFileUrl = "";
		FileOutputStream fos = null;
		try{
			if(mkdirFile())
			{
				if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
				 }
				bitmap = zoomImage(bitmap,fileSize,oldFileUrl);
				newFileUrl = path +System.currentTimeMillis() + ".jpg";
				fos = new FileOutputStream( newFileUrl );
		        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);//60ΪͼƬ����������Խ��ͼƬЧ��Խ��ļ���С��ԽС
			}
		}catch(Exception ex){
			
		}finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
		return newFileUrl;
	}
	
	//sd�������ļ���
	private boolean mkdirFile()
	{
		boolean b = true;
		try{
			File sd=Environment.getExternalStorageDirectory();
	        path=sd.getPath()+"/uploadFile/";
	        File file=new File(path);
	        if(!file.exists())
	         file.mkdir(); 
		}catch(Exception ex){
			ex.printStackTrace();
			b = false;
		}
		return b;
	}
	
	//sd��ɾ��ָ���ļ�
	private boolean deleteFile(String fileUrl)
	{
		boolean b = true;
		try{
	        File file=new File(fileUrl);
	        b = file.delete(); 
		}catch(Exception ex){
			ex.printStackTrace();
			b = false;
		}
		return b;
	}
	
	public Bitmap zoomImage(Bitmap bgimage, long imageSize, String fileUrl) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ�Ŀǰ��ֻ����ֻ��ͨ��inSampleSize���������ţ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ
		if (imageSize < 20480) { // 0-20k
			newOpts.inSampleSize = 1;
        } else if (imageSize < 51200) { // 20-50k
        	newOpts.inSampleSize = 2;
        } else if (imageSize < 307200) { // 50-300k
        	newOpts.inSampleSize = 4;
        } else if (imageSize < 819200) { // 300-800k
        	newOpts.inSampleSize = 6;
        } else if (imageSize < 1048576) { // 800-1024k
        	newOpts.inSampleSize = 8;
        } else {
        	newOpts.inSampleSize = 10;
        }

		// inJustDecodeBounds��Ϊfalse��ʾ��ͼƬ�����ڴ���
		newOpts.inJustDecodeBounds = false;
		// ���ô�С�����һ���ǲ�׼ȷ�ģ�����inSampleSize��Ϊ׼���������������ȴ��������
//		newOpts.outHeight = bgimage.getHeight();
//		newOpts.outWidth = bgimage.getWidth();
		
		bgimage = BitmapFactory.decodeFile(fileUrl, newOpts);
		return bgimage;
	}
	
	public JSONObject getJSONObject(String str)
	{
		JSONObject job = null;
		try{
			System.out.println("getJsonObjectStr===="+str);
			if(str != null && !str.equals("timeout"))
				job = new JSONObject(str);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return job;
	}
	
}
