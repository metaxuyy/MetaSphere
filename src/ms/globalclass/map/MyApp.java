package ms.globalclass.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ms.activitys.R;
import ms.activitys.myAnimation;
import ms.activitys.hotel.MessageListActivity;
import ms.activitys.hotel.MomentsActivity;
import ms.activitys.hotel.MomentsUploadActivity;
import ms.globalclass.FileUtils;
import ms.globalclass.TomorrowWeatherVO;
import ms.globalclass.imagegrid.BitmapUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.netty.channel.Channel;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;

public class MyApp extends Application implements Thread.UncaughtExceptionHandler {
//	private int[] menu_image_array = { R.drawable.menu_search,
//			R.drawable.menu_filemanager, R.drawable.menu_downmanager,
//			R.drawable.menu_fullscreen, R.drawable.menu_inputurl,
//			R.drawable.menu_bookmark, R.drawable.menu_bookmark_sync_import,
//			R.drawable.menu_sharepage, R.drawable.menu_quit,
//			R.drawable.menu_nightmode, R.drawable.menu_refresh,
//			R.drawable.menu_more };
	
//	private int[] google_menu_image_array = { R.drawable.suoxiao,
//			R.drawable.fangda, R.drawable.weixing,
//			R.drawable.jiejing, R.drawable.puton,
//			R.drawable.danqing,
//			R.drawable.typesouty
//			};
	private int[] card_menu_image_array = {
			R.drawable.card_meun_shoping_icon, R.drawable.card_menu_parking_icon,
			R.drawable.card_meun_entertainment_icon
			};
	
	private int[] card_menu_image_array2 = { R.drawable.dianhuayudin,
			R.drawable.my_yudin_icon, R.drawable.youhuiquan,
			R.drawable.muen_map,R.drawable.periphery_icon
			};
	
	/** 菜单文字 **/
	private String[] menu_name_array = { "搜索", "文件管理", "下载管理", "全屏", "网址", "书签",
			"加入书签", "分享页面", "退出", "夜间模式", "刷新", "更多" };
	
	private String[] google_menu_name_array = { "缩小", "放大", "卫星视图", "街景视图", "普通地图视图", "当前位置",
			"根据类型范围筛选" };
	
	private String[] card_menu_name_array = { "景点介绍", "周边酒店", "周边美食", "周边购物", "周边停车", "周边娱乐","景点攻略"};
	
	private List<String> tapeimgs1 = new ArrayList<String>(Arrays.asList("//tape1","//tape2","//tape3","//tape4","//tape5",
    		"//tape6","//tape7","//tape8","//tape9","//tape10","//tape11","//tape12","//tape13",
    		"//tape14","//tape15","//tape16","//tape17","//tape18","//tape19","//tape20","//tape21",
    		"//tape22","//tape23","//tape24","//tape25","//tape26","//tape27","//tape28","//tape29",
    		"//tape30","//tape31","//tape32","//tape33","//tape34","//tape35","//tape36","//tape37","//tape38","//tape39","//tape40"));
	
//	private String[] card_menu_name_array2 = { getString(R.string.menu_lable_169), getString(R.string.menu_lable_170), getString(R.string.coupon_dowenloads), getString(R.string.menu_lable_171), getString(R.string.menu_lable_1), getString(R.string.menu_lable_2),getString(R.string.menu_lable_172),getString(R.string.menu_lable_39),
//											  getString(R.string.cards_lable_2),getString(R.string.cards_lable_3),getString(R.string.cards_lable_4),getString(R.string.produce_discuss1),getString(R.string.menu_lable_173),getString(R.string.menu_lable_174),getString(R.string.menu_lable_175)};
	
	private String[] card_menu_name_array2;
	/** 菜单图片2 **/
//	private int[] menu_image_array2 = { R.drawable.menu_auto_landscape,
//			R.drawable.menu_penselectmodel, R.drawable.menu_page_attr,
//			R.drawable.menu_novel_mode, R.drawable.menu_page_updown,
//			R.drawable.menu_checkupdate, R.drawable.menu_checknet,
//			R.drawable.menu_refreshtimer, R.drawable.menu_syssettings,
//			R.drawable.menu_help, R.drawable.menu_about, R.drawable.menu_return };
	/** 菜单文字2 **/
	private String[] menu_name_array2 = { "自动横屏", "笔选模式", "阅读模式", "浏览模式", "快捷翻页",
			"检查更新", "检查网络", "定时刷新", "设置", "帮助", "关于", "返回" };

	/** 底部菜单图片 **/
	private int[] menu_toolbar_image_array = { R.drawable.card_icon_cn_64,
			R.drawable.map_icon_cn_64,
			R.drawable.nfc_icon_cn_64};
	/** 底部菜单文字 **/
	private String[] menu_toolbar_name_array = { "会员卡", "地图", "扫描", "NFC" };
	
//	private int[] menu_image_array3 = { R.drawable.t28,
//			R.drawable.t8, R.drawable.shao_miao_card,
//			R.drawable.t33, R.drawable.t18,
//			R.drawable.t34, R.drawable.t46,
//			R.drawable.t15, R.drawable.msn,
//			R.drawable.t40, R.drawable.t35,
//			R.drawable.t20, R.drawable.t48,
//			R.drawable.t21, R.drawable.mycards,
//			R.drawable.shejiao};
	private String[] menu_name_array3 = { "房间服务", "自助点菜","条码扫描", "员工API","项目管理", "月销售量",
			"销售报表", "雷达图", "本地社交", "精品广告","房间平面图","账单列表","登录/注册","定点互动地图","我的移动钱包","NFC" };
	
	private String roomNo;
	
	private String flashNam;
	
	private String totailId;
	
	private List<Map<String,Object>> mymenulist = new ArrayList<Map<String,Object>>();
	
	private String customerName;
	
	private String totalConsume;
	
	private String sessionId;
	
	private String upmenustate;
	
	private int latitude = 0;
	
	private String userName;
	
	private String systemIp;
	
	private String lat = "29.788211";
	
	private String lng = "121.637676";
	
	private String slat = "";
	
	private String slng = "";
	
	public String getSlat() {
		return slat;
	}

	public void setSlat(String slat) {
		this.slat = slat;
	}

	public String getSlng() {
		return slng;
	}

	public void setSlng(String slng) {
		this.slng = slng;
	}

	private String pfaddress;
	
	private String country;
	
	private String city;
	
	private String road;
	
	private String area;
	
	private String numbers;
	
	private List<Map<String,Object>> reservationRooms;
	
	private List<Map<String,Object>> myReservationRooms;
	
	private List<Map<String,Object>> menuList;
	private List<Map<String,Object>> menuList2;
	private List<Map<String,Object>> menuList3;
	
	private List<Map<String,Object>> myOrderList;
	
	private List<Map<String,Object>> myOrderHitList;
	
	private boolean isDBend = false;
	
	private String userimg;
	
	private boolean bindingSina = false;
	
	private List<Map<String,Object>> travelList;
	
	private List<Map<String,Object>> travelNotesList;
	
	private List<Map<String,Object>> travelCommentList;
	
	private List<Map<String,Object>> travelAllList;
	
	private List<Map<String,Object>> recommendAllWay;
	
	private List<Paint> pathlist = new ArrayList<Paint>(); 
	
	private List<TomorrowWeatherVO> tvolist;
	
	private String language;
	
	private String centerLat;
	
	private String centerLng;
	
	private int mapZoom;
	
	private int latspan;
	
	private int longspan;
	
	private List<Map<String,Object>> productList;
	
	private List<Map<String,Object>> productCartList;
	
	private Map<String,Map<String,Object>> addressmap = new HashMap<String,Map<String,Object>>();
	
	private List<Map<String,Object>> nfcStoreList;
	
	private Map<String,Object> dodoMap;
	private List<Map<String,Object>> smsMessageAll;
	
	private Channel channel;
	private Integer nettyid;
	private String userNameId;
	private Map<String,Map<String,Object>> onlineUserList = new HashMap<String,Map<String,Object>>();
	private Bitmap userimgbitmap;
	private Bitmap storeimgbitmap;
	private int screenWidth;
    private int screenHeight;
    private boolean network = true;
    private int currentDelindex;
    private int currentWidth;
//    private String appstoreid = "Moed435aa6e6hff3k8yn";
//    private String businessid = "Mned42c8bf30hff0aehu";
    private String appstoreid = "Moed41a1899ehjpddawi";
    private String businessid = "Mned4155a1cdhjpdc1gl";
//    private String appstoreid = "Moed408f8b7fhhbjvhk6";
//    private String businessid = "Mned416ca0f3hhbjov04";
//    private String appstoreid = "Moed477da755htlaiiac";//澳门kts
//    private String businessid = "Mned4581a7d4htladjvg";//澳门kts
//    private String appstoreid = "Moed4bfd906fhp0za2v5";//澳门ktstest
//    private String businessid = "Mned4c0db718htfh0n3x";//澳门ktstest
    private boolean iszhuche = false;
    private boolean breakStrat = false;
    private Map<String, View> viewMap = new HashMap<String, View>();
    private String ipaddress = "121.199.8.186";
    private List<Map<String,Object>> friendlist;
    private String myaccount;
    private String mysex;
    private String myarea;
    private String mySignature;
    private List<Map<String,Object>> verificationMessage = new ArrayList<Map<String,Object>>();
    private static String BASE_URL = "http://121.199.8.186:80/upload/";
	private static String BASE_URL2 = "http://121.199.8.186:80/customize/control/";
	private String thembgurl;
	private List<Map<String,Object>> storeImages = new ArrayList<Map<String,Object>>();
	private List<Bitmap> momentsimgs = new ArrayList<Bitmap>();
	private static FileUtils fileUtil = new FileUtils();
	private String newmomentsuserimg;
	private Map<String,Object> hotelMap;
	private String companyid;
	
	private Bitmap cutImg;//裁剪图片
	private List<Map<String, Object>> selPicList;//选中的图片集合
	private List<Map<String, Object>> picList;//要展示的图片集合
	private Boolean isServer = false;//是否是客户端 或服务端，true为服务端 false为客户端
	private SharedPreferences  share;
	private Map<String,Object> momentsAllData = new HashMap<String,Object>();
	private Map<String,String> momentsNewNumber = new HashMap<String,String>();
	private String puspfid;
	private List<String> momentsimgnames = new ArrayList<String>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		share = this.getSharedPreferences("perference", MODE_PRIVATE);
		//设置Thread Exception Handler 
		Thread.setDefaultUncaughtExceptionHandler(this); 
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("uncaughtException");
		ex.printStackTrace();
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		writeErrorFileToSD("错误时间:"+time.format(date) + ";"+ex.getMessage()+"\n-----------------------------------------\n");
		System.exit(0);
//		Intent intent = new Intent(this, myAnimation.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//				| Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());  
		
//		Intent i = getBaseContext().getPackageManager()
//				.getLaunchIntentForPackage(getBaseContext().getPackageName());
//		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(i);
	}
	
    private void writeErrorFileToSD(String errormsg) {  
    	String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");  
            return;  
        }  
        try {  
            String pathName = fileUtil.getSDPATH() + fileUtil.rootName + "/";  
            String fileName = "errorfile.txt";  
            File file = new File(pathName + fileName);  
            if( !file.exists()) {  
                Log.d("TestFile", "Create the file:" + fileName);  
                file.createNewFile();  
            }
            FileOutputStream stream = new FileOutputStream(file,true);
            byte[] buf = errormsg.getBytes();  
            stream.write(buf);         
            stream.close();
        } catch(Exception e) {  
            Log.e("TestFile", "Error on writeFilToSD.");  
            e.printStackTrace();  
        }  
    }
    
    public static String getTxtString(File file) { 
        InputStreamReader inputStreamReader = null;  
        try {  
        	InputStream inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream, "gbk");  
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }  
        BufferedReader reader = new BufferedReader(inputStreamReader);  
        StringBuffer sb = new StringBuffer("");  
        String line;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line);  
                sb.append("\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sb.toString();  
    }  
    
    /** 
     * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true 
     *  
     * @param fileName 
     * @param content 
     */  
    public static void method1(String file, String conent) {  
        BufferedWriter out = null;  
        try {  
            out = new BufferedWriter(new OutputStreamWriter(  
                    new FileOutputStream(file, true)));  
            out.write(conent);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                out.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
	

	public List<String> getMomentsimgnames() {
		return momentsimgnames;
	}

	public void setMomentsimgnames(List<String> momentsimgnames) {
		this.momentsimgnames = momentsimgnames;
	}

	public List<String> getTapeimgs1() {
		return tapeimgs1;
	}

	public void setTapeimgs1(List<String> tapeimgs1) {
		this.tapeimgs1 = tapeimgs1;
	}

	public String getPuspfid() {
		return puspfid;
	}

	public void setPuspfid(String puspfid) {
		this.puspfid = puspfid;
	}

	public Map<String, String> getMomentsNewNumber() {
		return momentsNewNumber;
	}

	public void setMomentsNewNumber(Map<String, String> momentsNewNumber) {
		this.momentsNewNumber = momentsNewNumber;
	}

	public Map<String, Object> getMomentsAllData() {
		return momentsAllData;
	}

	public void setMomentsAllData(Map<String, Object> momentsAllData) {
		this.momentsAllData = momentsAllData;
	}
	
	public Boolean getIsServer() {
		return isServer;
	}

	public void setIsServer(Boolean isServer) {
		this.isServer = isServer;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public Bitmap getCutImg() {
		return cutImg;
	}

	public void setCutImg(Bitmap bitmap) {
		this.cutImg = bitmap;
	}
	
	public List<Map<String, Object>> getSelPicList() {
		return selPicList;
	}

	public void setSelPicList(List<Map<String, Object>> list) {
		this.selPicList = list;
	}
	
	public List<Map<String, Object>> getPicList() {
		return picList;
	}

	public void setPicList(List<Map<String, Object>> list) {
		this.picList = list;
	}
    
	public Map<String, Object> getHotelMap() {
		return hotelMap;
	}

	public void setHotelMap(Map<String, Object> hotelMap) {
		this.hotelMap = hotelMap;
	}

	public String getNewmomentsuserimg() {
		return newmomentsuserimg;
	}

	public void setNewmomentsuserimg(String newmomentsuserimg) {
		this.newmomentsuserimg = newmomentsuserimg;
	}

	public List<Bitmap> getMomentsimgs() {
		return momentsimgs;
	}

	public void setMomentsimgs(List<Bitmap> momentsimgs) {
		this.momentsimgs = momentsimgs;
	}

	public List<Map<String, Object>> getStoreImages() {
		return storeImages;
	}

	public void setStoreImages(List<Map<String, Object>> storeImages) {
		this.storeImages = storeImages;
	}

	public String getThembgurl() {
		return thembgurl;
	}

	public void setThembgurl(String thembgurl) {
		this.thembgurl = thembgurl;
	}

	public List<Map<String, Object>> getVerificationMessage() {
		return verificationMessage;
	}

	public void setVerificationMessage(List<Map<String, Object>> verificationMessage) {
		this.verificationMessage = verificationMessage;
	}

	public String getMyaccount() {
		return myaccount;
	}

	public void setMyaccount(String myaccount) {
		this.myaccount = myaccount;
	}

	public String getMysex() {
		return mysex;
	}

	public void setMysex(String mysex) {
		this.mysex = mysex;
	}

	public String getMyarea() {
		return myarea;
	}

	public void setMyarea(String myarea) {
		this.myarea = myarea;
	}

	public String getMySignature() {
		return mySignature;
	}

	public void setMySignature(String mySignature) {
		this.mySignature = mySignature;
	}

	public List<Map<String, Object>> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(List<Map<String, Object>> friendlist) {
		this.friendlist = friendlist;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Map<String, View> getViewMap() {
		return viewMap;
	}

	public void setViewMap(Map<String, View> viewMap) {
		this.viewMap = viewMap;
	}

	public boolean isBreakStrat() {
		return breakStrat;
	}

	public void setBreakStrat(boolean breakStrat) {
		this.breakStrat = breakStrat;
	}

	public boolean isIszhuche() {
		return iszhuche;
	}

	public void setIszhuche(boolean iszhuche) {
		this.iszhuche = iszhuche;
	}

	public String getBusinessid() {
		return businessid;
	}

	public void setBusinessid(String businessid) {
		this.businessid = businessid;
	}

	public String getAppstoreid() {
		return appstoreid;
	}

	public void setAppstoreid(String appstoreid) {
		this.appstoreid = appstoreid;
	}

	public int getCurrentWidth() {
		return currentWidth;
	}

	public void setCurrentWidth(int currentWidth) {
		this.currentWidth = currentWidth;
	}

	public int getCurrentDelindex() {
		return currentDelindex;
	}

	public void setCurrentDelindex(int currentDelindex) {
		this.currentDelindex = currentDelindex;
	}

	public boolean isNetwork() {
		return network;
	}

	public void setNetwork(boolean network) {
		this.network = network;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public Bitmap getStoreimgbitmap() {
		return storeimgbitmap;
	}

	public void setStoreimgbitmap(Bitmap storeimgbitmap) {
		this.storeimgbitmap = storeimgbitmap;
	}

	public Bitmap getUserimgbitmap() {
		return userimgbitmap;
	}

	public void setUserimgbitmap(Bitmap userimgbitmap) {
		this.userimgbitmap = userimgbitmap;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Integer getNettyid() {
		return nettyid;
	}

	public void setNettyid(Integer nettyid) {
		this.nettyid = nettyid;
	}

	public String getUserNameId() {
		return userNameId;
	}

	public void setUserNameId(String userNameId) {
		this.userNameId = userNameId;
	}

	public Map<String, Map<String, Object>> getOnlineUserList() {
		return onlineUserList;
	}

	public void setOnlineUserList(Map<String, Map<String, Object>> onlineUserList) {
		this.onlineUserList = onlineUserList;
	}

	public List<Map<String, Object>> getSmsMessageAll() {
		return smsMessageAll;
	}

	public void setSmsMessageAll(List<Map<String, Object>> smsMessageAll) {
		this.smsMessageAll = smsMessageAll;
	}

	public Map<String, Object> getDodoMap() {
		return dodoMap;
	}

	public void setDodoMap(Map<String, Object> dodoMap) {
		this.dodoMap = dodoMap;
	}

	public int[] getCard_menu_image_array2() {
		return card_menu_image_array2;
	}

	public void setCard_menu_image_array2(int[] card_menu_image_array2) {
		this.card_menu_image_array2 = card_menu_image_array2;
	}

	public String[] getCard_menu_name_array2() {
		return card_menu_name_array2;
	}

	public void setCard_menu_name_array2(String[] card_menu_name_array2) {
		this.card_menu_name_array2 = card_menu_name_array2;
	}

	public List<Map<String, Object>> getNfcStoreList() {
		return nfcStoreList;
	}

	public void setNfcStoreList(List<Map<String, Object>> nfcStoreList) {
		this.nfcStoreList = nfcStoreList;
	}

	public Map<String, Map<String, Object>> getAddressmap() {
		return addressmap;
	}

	public void setAddressmap(Map<String, Map<String, Object>> addressmap) {
		this.addressmap = addressmap;
	}

	public List<Map<String, Object>> getProductCartList() {
		return productCartList;
	}

	public void setProductCartList(List<Map<String, Object>> productCartList) {
		this.productCartList = productCartList;
	}

	public List<Map<String, Object>> getProductList() {
		return productList;
	}

	public void setProductList(List<Map<String, Object>> productList) {
		this.productList = productList;
	}

	public List<Map<String, Object>> getMenuList2() {
		return menuList2;
	}

	public void setMenuList2(List<Map<String, Object>> menuList2) {
		this.menuList2 = menuList2;
	}

	public List<Map<String, Object>> getMenuList3() {
		return menuList3;
	}

	public void setMenuList3(List<Map<String, Object>> menuList3) {
		this.menuList3 = menuList3;
	}

	private List<Map<String,Object>> advertiseProducts;
	
	
	public List<Map<String, Object>> getAdvertiseProducts() {
		return advertiseProducts;
	}

	public void setAdvertiseProducts(List<Map<String, Object>> advertiseProducts) {
		this.advertiseProducts = advertiseProducts;
	}

	public int getLatspan() {
		return latspan;
	}

	public void setLatspan(int latspan) {
		this.latspan = latspan;
	}

	public int getLongspan() {
		return longspan;
	}

	public void setLongspan(int longspan) {
		this.longspan = longspan;
	}

	public int getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(int mapZoom) {
		this.mapZoom = mapZoom;
	}

	public String getCenterLat() {
		return centerLat;
	}

	public void setCenterLat(String centerLat) {
		this.centerLat = centerLat;
	}

	public String getCenterLng() {
		return centerLng;
	}

	public void setCenterLng(String centerLng) {
		this.centerLng = centerLng;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Map<String, Object>> getRecommendAllWay() {
		return recommendAllWay;
	}

	public void setRecommendAllWay(List<Map<String, Object>> recommendAllWay) {
		this.recommendAllWay = recommendAllWay;
	}

	public List<Map<String, Object>> getTravelCommentList() {
		return travelCommentList;
	}

	public void setTravelCommentList(List<Map<String, Object>> travelCommentList) {
		this.travelCommentList = travelCommentList;
	}

	public List<Map<String, Object>> getTravelNotesList() {
		return travelNotesList;
	}

	public void setTravelNotesList(List<Map<String, Object>> travelNotesList) {
		this.travelNotesList = travelNotesList;
	}

	public List<Map<String, Object>> getTravelAllList() {
		return travelAllList;
	}

	public void setTravelAllList(List<Map<String, Object>> travelAllList) {
		this.travelAllList = travelAllList;
	}

	public List<TomorrowWeatherVO> getTvolist() {
		return tvolist;
	}

	public void setTvolist(List<TomorrowWeatherVO> tvolist) {
		this.tvolist = tvolist;
	}

	public List<Paint> getPathlist() {
		return pathlist;
	}

	public void setPathlist(List<Paint> pathlist) {
		this.pathlist = pathlist;
	}

	public List<Map<String, Object>> getTravelList() {
		return travelList;
	}

	public void setTravelList(List<Map<String, Object>> travelList) {
		this.travelList = travelList;
	}

	public boolean isBindingSina() {
		return bindingSina;
	}

	public void setBindingSina(boolean bindingSina) {
		this.bindingSina = bindingSina;
	}

	public String getUserimg() {
		return userimg;
	}

	public void setUserimg(String userimg) {
		this.userimg = userimg;
	}

	public boolean isDBend() {
		return isDBend;
	}

	public void setDBend(boolean isDBend) {
		this.isDBend = isDBend;
	}

	public String updatetag = "1";
	
	public String getUpdatetag() {
		return updatetag;
	}

	public void setUpdatetag(String updatetag) {
		this.updatetag = updatetag;
	}
	
	
	public List<Map<String, Object>> getMyOrderHitList() {
		return myOrderHitList;
	}

	public void setMyOrderHitList(List<Map<String, Object>> myOrderHitList) {
		this.myOrderHitList = myOrderHitList;
	}

	public List<Map<String, Object>> getMyOrderList() {
		return myOrderList;
	}

	public void setMyOrderList(List<Map<String, Object>> myOrderList) {
		this.myOrderList = myOrderList;
	}

	public List<Map<String, Object>> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Map<String, Object>> menuList) {
		this.menuList = menuList;
	}

	public List<Map<String, Object>> getMyReservationRooms() {
		return myReservationRooms;
	}

	public void setMyReservationRooms(List<Map<String, Object>> myReservationRooms) {
		this.myReservationRooms = myReservationRooms;
	}

	public List<Map<String, Object>> getReservationRooms() {
		return reservationRooms;
	}

	public void setReservationRooms(List<Map<String, Object>> reservationRooms) {
		this.reservationRooms = reservationRooms;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getNumbers() {
		return numbers;
	}

	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}

	public String getPfaddress() {
		return pfaddress;
	}

	public void setPfaddress(String pfaddress) {
		this.pfaddress = pfaddress;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getSystemIp() {
		return systemIp;
	}

	public void setSystemIp(String systemIp) {
		this.systemIp = systemIp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Thread getMyThread() {
		return myThread;
	}

	public void setMyThread(Thread myThread) {
		this.myThread = myThread;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private int longitude = 0;
	
	private List<Map<String,Object>> storelist;
	
	private String pfprofileId;
	
	private List<Map<String,Object>> myCardsAll;
	
	private Thread myThread;
	
	private Socket socket;
	
	private String host = "121.199.8.186";
	
	private int port = 9999;
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private List<Map<String,Object>> couponAll;
	
	public List<Map<String, Object>> getCouponAll() {
		return couponAll;
	}

	public void setCouponAll(List<Map<String, Object>> couponAll) {
		this.couponAll = couponAll;
	}

	public int[] getCard_menu_image_array() {
		return card_menu_image_array;
	}

	public void setCard_menu_image_array(int[] cardMenuImageArray) {
		card_menu_image_array = cardMenuImageArray;
	}

	public String[] getCard_menu_name_array() {
		return card_menu_name_array;
	}

	public void setCard_menu_name_array(String[] cardMenuNameArray) {
		card_menu_name_array = cardMenuNameArray;
	}
	
	public String getPfprofileId() {
		return pfprofileId;
	}

	public List<Map<String, Object>> getMyCardsAll() {
		return myCardsAll;
	}

	public void setMyCardsAll(List<Map<String, Object>> myCardsAll) {
		this.myCardsAll = myCardsAll;
	}

	public void setPfprofileId(String pfprofileId) {
		this.pfprofileId = pfprofileId;
	}

	public List<Map<String, Object>> getStorelist() {
		return storelist;
	}

	public void setStorelist(List<Map<String, Object>> storelist) {
		this.storelist = storelist;
	}

//	public int[] getGoogle_menu_image_array() {
//		return google_menu_image_array;
//	}
//
//	public void setGoogle_menu_image_array(int[] googleMenuImageArray) {
//		google_menu_image_array = googleMenuImageArray;
//	}

	public String[] getGoogle_menu_name_array() {
		return google_menu_name_array;
	}

	public void setGoogle_menu_name_array(String[] googleMenuNameArray) {
		google_menu_name_array = googleMenuNameArray;
	}
	
	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getUpMenuState() {
		return upmenustate;
	}
	
	public void setUpMenuState(String upmenustate) {
		this.upmenustate = upmenustate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getTotalConsume() {
		return totalConsume;
	}

	public void setTotalConsume(String totalConsume) {
		this.totalConsume = totalConsume;
	}

	public String getTotailId() {
		return totailId;
	}

	public void setTotailId(String totailId) {
		this.totailId = totailId;
	}

	public List<Map<String, Object>> getMymenulist() {
		return mymenulist;
	}

	public void setMymenulist(List<Map<String, Object>> mymenulist) {
		this.mymenulist = mymenulist;
	}

	public String getFlashNam() {
		return flashNam;
	}

	public void setFlashNam(String flashNam) {
		this.flashNam = flashNam;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

//	public int[] getMenu_image_array() {
//		return menu_image_array;
//	}
//
//	public void setMenu_image_array(int[] menuImageArray) {
//		menu_image_array = menuImageArray;
//	}

	public String[] getMenu_name_array() {
		return menu_name_array;
	}

	public void setMenu_name_array(String[] menuNameArray) {
		menu_name_array = menuNameArray;
	}

//	public int[] getMenu_image_array2() {
//		return menu_image_array2;
//	}
//
//	public void setMenu_image_array2(int[] menuImageArray2) {
//		menu_image_array2 = menuImageArray2;
//	}

	public String[] getMenu_name_array2() {
		return menu_name_array2;
	}

	public void setMenu_name_array2(String[] menuNameArray2) {
		menu_name_array2 = menuNameArray2;
	}

	public int[] getMenu_toolbar_image_array() {
		return menu_toolbar_image_array;
	}

	public void setMenu_toolbar_image_array(int[] menuToolbarImageArray) {
		menu_toolbar_image_array = menuToolbarImageArray;
	}

	public String[] getMenu_toolbar_name_array() {
		return menu_toolbar_name_array;
	}

	public void setMenu_toolbar_name_array(String[] menuToolbarNameArray) {
		menu_toolbar_name_array = menuToolbarNameArray;
	}

//	public int[] getMenu_image_array3() {
//		return menu_image_array3;
//	}
//
//	public void setMenu_image_array3(int[] menuImageArray3) {
//		menu_image_array3 = menuImageArray3;
//	}

	public String[] getMenu_name_array3() {
		return menu_name_array3;
	}

	public void setMenu_name_array3(String[] menuNameArray3) {
		menu_name_array3 = menuNameArray3;
	}
	
	private boolean m_bKeyRight = true;
	private BMapManager mBMapManager = null;
	private double logdeviation = 1.0000568461567492425578691530827;// 经度偏差
	private double latdeviation = 1.0002012762190961772159526495686;// 纬度偏差

	public boolean isM_bKeyRight() {
		return m_bKeyRight;
	}

	public void setM_bKeyRight(boolean m_bKeyRight) {
		this.m_bKeyRight = m_bKeyRight;
	}

	public BMapManager getmBMapManager() {
		return mBMapManager;
	}

	public void setmBMapManager(BMapManager mBMapManager) {
		this.mBMapManager = mBMapManager;
	}

	public double getLogdeviation() {
		return logdeviation;
	}

	public void setLogdeviation(double logdeviation) {
		this.logdeviation = logdeviation;
	}

	public double getLatdeviation() {
		return latdeviation;
	}

	public void setLatdeviation(double latdeviation) {
		this.latdeviation = latdeviation;
	}
	
	public List<Map<String,Object>> getMyCardList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
//			nicks = new String[jArr.length()];
//			userimgs = new String[jArr.length()];
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				Integer points = 0; // 当前积分
				if(dobj.has("points"))
					points = (Integer) dobj.get("points"); 
				
				String nameOnCard = ""; // 卡上的名字
				if(dobj.has("nameOnCard"))
					nameOnCard = (String) dobj.get("nameOnCard");
				
				String nameid = ""; // 
				if(dobj.has("nameid"))
					nameid = (String) dobj.get("nameid"); 
				
				String pfids = ""; // 
				if(dobj.has("pfids"))
					pfids = (String) dobj.get("pfids"); 
				
				String cardNo = ""; // 会员卡编号
				if(dobj.has("cardNo"))
					cardNo = (String) dobj.get("cardNo"); 
				
				String joinedDate = ""; // 加入日期
				if(dobj.has("joinedDate"))
				{
					joinedDate = (String) dobj.get("joinedDate"); 
					String str[] = joinedDate.split(" ");
					if(str.length>1)
						joinedDate = str[0];
				}
				
				String mdmType = ""; // 会员类型
				if(dobj.has("mdmType"))
					mdmType = (String) dobj.get("mdmType"); 
				
				String mdmLevel = ""; // 会员等级
				if(dobj.has("mdmLevel"))
					mdmLevel = (String) dobj.get("mdmLevel"); 
				
				String mdmstatus = ""; // 会员状态
				if(dobj.has("mdmstatus"))
					mdmstatus = (String) dobj.get("mdmstatus"); 
				
				String expDate = ""; // 失效日期
				if(dobj.has("expDate"))
					expDate = (String) dobj.get("expDate"); 
				
				String chainCode = ""; // 条形码号或硬卡号
				if(dobj.has("chainCode"))
					chainCode = (String) dobj.get("chainCode"); 
				
				String storeid = ""; // 门店id
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
				String storeNo = "99999999999999999999999"; // 门店编号
				if(dobj.has("storeNo"))
					storeNo = (String) dobj.get("storeNo"); 
				
				String storeName = ""; // 门店名字
				if(dobj.has("storeName"))
					storeName = (String) dobj.get("storeName"); 
				
				String img = ""; // 门店会员卡图片
				if(dobj.has("img"))
				{
					img = (String) dobj.get("img");
					String strimg = img.replaceAll("http://"+this.getIpaddress()+":80/upload/", "");
					if(strimg.equals(""))
						img = "";
				}
				
				String isASttention = ""; 
				if(dobj.has("isASttention"))
					isASttention = (String) dobj.get("isASttention"); 
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid"); 
				
				String couponNumber = ""; 
				if(dobj.has("couponNumber"))
					couponNumber = (String) dobj.get("couponNumber"); 
				
				String storePhone = ""; 
				if(dobj.has("storePhone"))
					storePhone = (String) dobj.get("storePhone"); 
				
				String addressInfomation = ""; 
				if(dobj.has("addressInfomation"))
					addressInfomation = (String) dobj.get("addressInfomation"); 
				
				String storeDesc = ""; 
				if(dobj.has("storeDesc"))
					storeDesc = (String) dobj.get("storeDesc"); 
				
				String typeName = "";  //酒店类型
				if(dobj.has("typeName"))
					typeName = (String) dobj.get("typeName"); 
				
				String storeType = "";  //酒店类型
				if(dobj.has("storeType"))
					storeType = (String) dobj.get("storeType"); 
				
				String typesMapping = "";  //酒店类型与客户端得映射
				if(dobj.has("typesMapping"))
					typesMapping = (String) dobj.get("typesMapping"); 
				
				String businessId = ""; 
				if(dobj.has("businessId"))
				{
					businessId = (String) dobj.get("businessId");
					if(businessId == null || businessId.equals(""))
						businessId = this.getBusinessid();
				}
				
				String woof = ""; 
				if(dobj.has("woof"))
					woof = (String) dobj.get("woof"); 
				
				String longItude = ""; 
				if(dobj.has("longItude"))
					longItude = (String) dobj.get("longItude"); 
				
				String userimg = ""; 
				if(dobj.has("userimg"))
				{
					userimg = (String) dobj.get("userimg"); 
					String strimg = userimg.replaceAll("http://"+this.getIpaddress()+":80/upload/", "");
					if(strimg.equals(""))
						userimg = "";
				}
				
				String isLu = ""; 
				if(dobj.has("isLu"))
					isLu = (String) dobj.get("isLu"); 
				
				String province = ""; 
					if(dobj.has("province"))
						province = (String) dobj.get("province");
					
				String roomIntroduction = ""; 
					if(dobj.has("roomIntroduction"))
						roomIntroduction = (String) dobj.get("roomIntroduction");
					
				String periphery = ""; 
					if(dobj.has("periphery"))
						periphery = (String) dobj.get("periphery");
					
				String trafficWay = ""; 
					if(dobj.has("trafficWay"))
						trafficWay = (String) dobj.get("trafficWay");
						
				String startingPrice = ""; 
					if(dobj.has("startingPrice"))
						startingPrice = (String) dobj.get("startingPrice");
					
				String score = ""; 
					if(dobj.has("score"))
						score = (String) dobj.get("score");
						
				String comments = ""; 
					if(dobj.has("comments"))
						comments = (String) dobj.get("comments");
					
				String username = ""; 
					if(dobj.has("username"))
						username = (String) dobj.get("username");
					
				String profileId = ""; 
					if(dobj.has("profileId"))
						profileId = (String) dobj.get("profileId");
					
				String password = ""; 
					if(dobj.has("password"))
						password = (String) dobj.get("password");
					
				String lastmessage = ""; 
					if(dobj.has("lastmessage"))
					{
						try{
							JSONObject msgobj = (JSONObject) dobj.get("lastmessage");
							lastmessage = msgobj.toString();
						}catch(Exception ex){
							ex.printStackTrace();
							lastmessage = (String) dobj.get("lastmessage");
						}
						
					}
					
				String language = ""; 
					if(dobj.has("language"))
						language = (String) dobj.get("language");
					
				String linkurl = ""; 
					if(dobj.has("linkurl"))
						linkurl = (String) dobj.get("linkurl");
					
				String servicetype = ""; 
					if(dobj.has("servicetype"))
						servicetype = (String) dobj.get("servicetype");
					
				String customizeMenu = ""; 
				if(dobj.has("customizeMenu"))
				{
					Object str = dobj.get("customizeMenu");
					if(!str.equals(""))
					{
						JSONArray jarry = (JSONArray)dobj.get("customizeMenu");
						customizeMenu = jarry.toString();
					}
				}
					
				String lastmessagetime = ""; 
					if(dobj.has("lastmessagetime"))
					{
						lastmessagetime = (String) dobj.get("lastmessagetime");
						if(lastmessagetime != null && !lastmessagetime.equals(""))
						{
							String str[] = lastmessagetime.split(" ");
							SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
							String datastr1 = formatter.format(new Date());
							if(str.length>1)
							{
								String datastr = str[0];
								if(datastr.equals(datastr1))
								{
									lastmessagetime = str[1].substring(0,5);
								}
								else
								{
									lastmessagetime = str[0].substring(5,str[0].length());
								}
							}
						}
					}
					
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("points", points);
				map.put("storeNo", storeNo);
				map.put("nameOnCard", nameOnCard);
				map.put("nameid", nameid);
				map.put("pfids", pfids);
				map.put("cardNo", cardNo);
				map.put("joinedDate", joinedDate);
				map.put("mdmType", mdmType);
				map.put("mdmLevel", mdmLevel);
				map.put("mdmstatus", mdmstatus);
				map.put("expDate", expDate);
				map.put("chainCode", chainCode);
				map.put("storeid", storeid);
				map.put("storeName", storeName);
				map.put("profileId", profileId);
				map.put("linkurl", linkurl);
				map.put("servicetype", servicetype);
				String sortName = storeName.replaceAll("\\(", "@").replaceAll("\\)", "#").replaceAll("\\（", "@").replaceAll("\\）", "#");
				if(isASttention.equals("0"))
					map.put("sortName", "1"+sortName);
				else
					map.put("sortName", sortName);
				map.put("username", username);
				map.put("password", password);
				map.put("language",language);
//				boolean loadimgTag = share.getBoolean("webimage", true);
//				if(loadimgTag)
//				{
//					map.put("img", getImageDrawable(img));
//					map.put("img2", getImageBitmap(img));
//				}
//				else
//				{
//					map.put("img", null);
//					map.put("img2", BitmapFactory.decodeResource(this.getResources(), R.drawable.local_card_img));
//				}
//				map.put("imgurl", img);
				if(img != null && !img.equals(""))
	            {
	            	if(!fileUtil.isFileExist2(storeid))
	            	{
	            		fileUtil.createUserFile(storeid);
	            	}
	            	if(img.indexOf("http") >= 0)
	            	{
//	            		String [] strs = img.split("\\/");
//		            	String filename = strs[strs.length-1];
//		            	String [] names = filename.split("\\.");
//		            	filename = names[0];
//		            	String furl = fileUtil.getImageFile1aPath(storeid, filename);
//						File file = new File(furl);
//						Bitmap bitmimg = null;
//						if(!file.exists())
//							bitmimg = returnBitMap(img);
//						else
//							bitmimg = getLoacalBitmap(furl);
//			            if(bitmimg != null)
//			            {
////				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
////							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
////							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
////							cv.put("imgurl", oss.toByteArray());
//			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//							if(!file.exists())
//								fileUtil.saveMyBitmap(furl, bitmimg);
////							cv.put("imgurl", furl);
//							map.put("imgurl", furl);
//			            }
	            		map.put("imgurl", img);
	            	}
	            	else
	            	{
	            		Bitmap bitmimg = getLoacalBitmap(img);
	            		if(bitmimg != null)
			            {
//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
//							cv.put("imgurl", oss.toByteArray());
	            			String [] strs = img.split("\\/");
			            	String filename = strs[strs.length];
	            			
	            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
							UUID uuid = UUID.randomUUID();
							String furl = fileUtil.getImageFile1aPath(storeid, filename);
							File file = new File(furl);
							if(!file.exists())
								fileUtil.saveMyBitmap(furl, bitmimg);
//							cv.put("imgurl", furl);
							map.put("imgurl", furl);
			            }
	            	}
	            }
//				userimgs[i] = img;
				map.put("pkid", pkid);
				map.put("storePhone", storePhone);
				map.put("addressInfomation", addressInfomation);
				map.put("storeDesc", storeDesc);
				map.put("isASttention", isASttention);
				if(isASttention.equals("0"))
					map.put("xinxin", R.drawable.ic_star_small);
				else
					map.put("xinxin", null);
				map.put("couponNumber", couponNumber);
				map.put("typeName", typeName);
				map.put("typesMapping", typesMapping);
				map.put("businessId", businessId);
				map.put("woof", woof);
				map.put("longItude", longItude);
				map.put("userimg", userimg);
				map.put("isLu", isLu);
				map.put("storeType", storeType);
				map.put("province", province);
				map.put("roomIntroduction", roomIntroduction);
				map.put("periphery", periphery);
				map.put("trafficWay", trafficWay);
				map.put("startingPrice", startingPrice);
				map.put("score", score);
				map.put("comments", comments);
				map.put("lastmessage", lastmessage);
				map.put("lastmessagetime", lastmessagetime);
				map.put("customizeMenu", customizeMenu);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMyCardList(JSONArray jArr,Map<String,Object> storekeylist){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
//			nicks = new String[jArr.length()];
//			userimgs = new String[jArr.length()];
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				Integer points = 0; // 当前积分
				if(dobj.has("points"))
					points = (Integer) dobj.get("points"); 
				
				String nameOnCard = ""; // 卡上的名字
				if(dobj.has("nameOnCard"))
					nameOnCard = (String) dobj.get("nameOnCard");
				
				String nameid = ""; // 
				if(dobj.has("nameid"))
					nameid = (String) dobj.get("nameid"); 
				
				String pfids = ""; // 
				if(dobj.has("pfids"))
					pfids = (String) dobj.get("pfids"); 
				
				String cardNo = ""; // 会员卡编号
				if(dobj.has("cardNo"))
					cardNo = (String) dobj.get("cardNo"); 
				
				String joinedDate = ""; // 加入日期
				if(dobj.has("joinedDate"))
				{
					joinedDate = (String) dobj.get("joinedDate"); 
					String str[] = joinedDate.split(" ");
					if(str.length>1)
						joinedDate = str[0];
				}
				
				String mdmType = ""; // 会员类型
				if(dobj.has("mdmType"))
					mdmType = (String) dobj.get("mdmType"); 
				
				String mdmLevel = ""; // 会员等级
				if(dobj.has("mdmLevel"))
					mdmLevel = (String) dobj.get("mdmLevel"); 
				
				String mdmstatus = ""; // 会员状态
				if(dobj.has("mdmstatus"))
					mdmstatus = (String) dobj.get("mdmstatus"); 
				
				String expDate = ""; // 失效日期
				if(dobj.has("expDate"))
					expDate = (String) dobj.get("expDate"); 
				
				String chainCode = ""; // 条形码号或硬卡号
				if(dobj.has("chainCode"))
					chainCode = (String) dobj.get("chainCode"); 
				
				String storeid = ""; // 门店id
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
				if(!storekeylist.containsKey(storeid))
				{
					String storeNo = "99999999999999999999999"; // 门店编号
					if(dobj.has("storeNo"))
						storeNo = (String) dobj.get("storeNo"); 
					
					String storeName = ""; // 门店名字
					if(dobj.has("storeName"))
						storeName = (String) dobj.get("storeName"); 
					
					String img = ""; // 门店会员卡图片
					if(dobj.has("img"))
					{
						img = (String) dobj.get("img");
						String strimg = img.replaceAll("http://"+this.getIpaddress()+":80/upload/", "");
						if(strimg.equals(""))
							img = "";
					}
					
					String isASttention = ""; 
					if(dobj.has("isASttention"))
						isASttention = (String) dobj.get("isASttention"); 
					
					String pkid = ""; 
					if(dobj.has("pkid"))
						pkid = (String) dobj.get("pkid"); 
					
					String couponNumber = ""; 
					if(dobj.has("couponNumber"))
						couponNumber = (String) dobj.get("couponNumber"); 
					
					String storePhone = ""; 
					if(dobj.has("storePhone"))
						storePhone = (String) dobj.get("storePhone"); 
					
					String addressInfomation = ""; 
					if(dobj.has("addressInfomation"))
						addressInfomation = (String) dobj.get("addressInfomation"); 
					
					String storeDesc = ""; 
					if(dobj.has("storeDesc"))
						storeDesc = (String) dobj.get("storeDesc"); 
					
					String typeName = "";  //酒店类型
					if(dobj.has("typeName"))
						typeName = (String) dobj.get("typeName"); 
					
					String storeType = "";  //酒店类型
					if(dobj.has("storeType"))
						storeType = (String) dobj.get("storeType"); 
					
					String typesMapping = "";  //酒店类型与客户端得映射
					if(dobj.has("typesMapping"))
						typesMapping = (String) dobj.get("typesMapping"); 
					
					String businessId = ""; 
					if(dobj.has("businessId"))
					{
						businessId = (String) dobj.get("businessId");
						if(businessId == null || businessId.equals(""))
							businessId = this.getBusinessid();
					}
					
					String woof = ""; 
					if(dobj.has("woof"))
						woof = (String) dobj.get("woof"); 
					
					String longItude = ""; 
					if(dobj.has("longItude"))
						longItude = (String) dobj.get("longItude"); 
					
					String userimg = ""; 
					if(dobj.has("userimg"))
					{
						userimg = (String) dobj.get("userimg"); 
						String strimg = userimg.replaceAll("http://"+this.getIpaddress()+":80/upload/", "");
						if(strimg.equals(""))
							userimg = "";
					}
					
					String isLu = ""; 
					if(dobj.has("isLu"))
						isLu = (String) dobj.get("isLu"); 
					
					String province = ""; 
						if(dobj.has("province"))
							province = (String) dobj.get("province");
						
					String roomIntroduction = ""; 
						if(dobj.has("roomIntroduction"))
							roomIntroduction = (String) dobj.get("roomIntroduction");
						
					String periphery = ""; 
						if(dobj.has("periphery"))
							periphery = (String) dobj.get("periphery");
						
					String trafficWay = ""; 
						if(dobj.has("trafficWay"))
							trafficWay = (String) dobj.get("trafficWay");
							
					String startingPrice = ""; 
						if(dobj.has("startingPrice"))
							startingPrice = (String) dobj.get("startingPrice");
						
					String score = ""; 
						if(dobj.has("score"))
							score = (String) dobj.get("score");
							
					String comments = ""; 
						if(dobj.has("comments"))
							comments = (String) dobj.get("comments");
						
					String username = ""; 
						if(dobj.has("username"))
							username = (String) dobj.get("username");
						
					String profileId = ""; 
						if(dobj.has("profileId"))
							profileId = (String) dobj.get("profileId");
						
					String password = ""; 
						if(dobj.has("password"))
							password = (String) dobj.get("password");
						
					String lastmessage = ""; 
						if(dobj.has("lastmessage"))
							lastmessage = (String) dobj.get("lastmessage");
						
					String language = ""; 
						if(dobj.has("language"))
							language = (String) dobj.get("language");
						
					String linkurl = ""; 
						if(dobj.has("linkurl"))
							linkurl = (String) dobj.get("linkurl");
						
					String servicetype = ""; 
						if(dobj.has("servicetype"))
							servicetype = (String) dobj.get("servicetype");
						
					String customizeMenu = ""; 
					if(dobj.has("customizeMenu"))
					{
						Object str = dobj.get("customizeMenu");
						if(!str.equals(""))
						{
							JSONArray jarry = (JSONArray)dobj.get("customizeMenu");
							customizeMenu = jarry.toString();
						}
					}
						
					String lastmessagetime = ""; 
						if(dobj.has("lastmessagetime"))
						{
							lastmessagetime = (String) dobj.get("lastmessagetime");
							if(lastmessagetime != null && !lastmessagetime.equals(""))
							{
								String str[] = lastmessagetime.split(" ");
								SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
								String datastr1 = formatter.format(new Date());
								if(str.length>1)
								{
									String datastr = str[0];
									if(datastr.equals(datastr1))
									{
										lastmessagetime = str[1].substring(0,5);
									}
									else
									{
										lastmessagetime = str[0].substring(5,str[0].length());
									}
								}
							}
						}
						
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("points", points);
					map.put("storeNo", storeNo);
					map.put("nameOnCard", nameOnCard);
					map.put("nameid", nameid);
					map.put("pfids", pfids);
					map.put("cardNo", cardNo);
					map.put("joinedDate", joinedDate);
					map.put("mdmType", mdmType);
					map.put("mdmLevel", mdmLevel);
					map.put("mdmstatus", mdmstatus);
					map.put("expDate", expDate);
					map.put("chainCode", chainCode);
					map.put("storeid", storeid);
					map.put("storeName", storeName);
					map.put("profileId", profileId);
					map.put("linkurl", linkurl);
					map.put("servicetype", servicetype);
					String sortName = storeName.replaceAll("\\(", "@").replaceAll("\\)", "#").replaceAll("\\（", "@").replaceAll("\\）", "#");
					if(isASttention.equals("0"))
						map.put("sortName", "1"+sortName);
					else
						map.put("sortName", sortName);
					map.put("username", username);
					map.put("password", password);
					map.put("language",language);
	//				boolean loadimgTag = share.getBoolean("webimage", true);
	//				if(loadimgTag)
	//				{
	//					map.put("img", getImageDrawable(img));
	//					map.put("img2", getImageBitmap(img));
	//				}
	//				else
	//				{
	//					map.put("img", null);
	//					map.put("img2", BitmapFactory.decodeResource(this.getResources(), R.drawable.local_card_img));
	//				}
	//				map.put("imgurl", img);
					if(img != null && !img.equals(""))
		            {
		            	if(!fileUtil.isFileExist2(storeid))
		            	{
		            		fileUtil.createUserFile(storeid);
		            	}
		            	if(img.indexOf("http") >= 0)
		            	{
	//	            		String [] strs = img.split("\\/");
	//		            	String filename = strs[strs.length-1];
	//		            	String [] names = filename.split("\\.");
	//		            	filename = names[0];
	//		            	String furl = fileUtil.getImageFile1aPath(storeid, filename);
	//						File file = new File(furl);
	//						Bitmap bitmimg = null;
	//						if(!file.exists())
	//							bitmimg = returnBitMap(img);
	//						else
	//							bitmimg = getLoacalBitmap(furl);
	//			            if(bitmimg != null)
	//			            {
	////				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
	////							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
	////							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
	////							cv.put("imgurl", oss.toByteArray());
	//			            	bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
	//							if(!file.exists())
	//								fileUtil.saveMyBitmap(furl, bitmimg);
	////							cv.put("imgurl", furl);
	//							map.put("imgurl", furl);
	//			            }
		            		map.put("imgurl", img);
		            	}
		            	else
		            	{
		            		Bitmap bitmimg = getLoacalBitmap(img);
		            		if(bitmimg != null)
				            {
	//				            int size=bitmimg.getWidth()*bitmimg.getHeight()*1; 
	//							ByteArrayOutputStream oss = new ByteArrayOutputStream(size);
	//							bitmimg.compress(Bitmap.CompressFormat.PNG, 100, oss);
	//							cv.put("imgurl", oss.toByteArray());
		            			String [] strs = img.split("\\/");
				            	String filename = strs[strs.length];
		            			
		            			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
								UUID uuid = UUID.randomUUID();
								String furl = fileUtil.getImageFile1aPath(storeid, filename);
								File file = new File(furl);
								if(!file.exists())
									fileUtil.saveMyBitmap(furl, bitmimg);
	//							cv.put("imgurl", furl);
								map.put("imgurl", furl);
				            }
		            	}
		            }
	//				userimgs[i] = img;
					map.put("pkid", pkid);
					map.put("storePhone", storePhone);
					map.put("addressInfomation", addressInfomation);
					map.put("storeDesc", storeDesc);
					map.put("isASttention", isASttention);
					if(isASttention.equals("0"))
						map.put("xinxin", R.drawable.ic_star_small);
					else
						map.put("xinxin", null);
					map.put("couponNumber", couponNumber);
					map.put("typeName", typeName);
					map.put("typesMapping", typesMapping);
					map.put("businessId", businessId);
					map.put("woof", woof);
					map.put("longItude", longItude);
					map.put("userimg", userimg);
					map.put("isLu", isLu);
					map.put("storeType", storeType);
					map.put("province", province);
					map.put("roomIntroduction", roomIntroduction);
					map.put("periphery", periphery);
					map.put("trafficWay", trafficWay);
					map.put("startingPrice", startingPrice);
					map.put("score", score);
					map.put("comments", comments);
					map.put("lastmessage", lastmessage);
					map.put("lastmessagetime", lastmessagetime);
					map.put("customizeMenu", customizeMenu);
		
					list.add(map);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public Map<String,Object> getMyVerificationMessageList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String,Object> dmap = new HashMap<String,Object>();
		int newnumber = 0;
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = "";
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String frompfid = ""; 
				if(dobj.has("frompfid"))
					frompfid = (String) dobj.get("frompfid"); 
				
				String acceptpfid = ""; 
				if(dobj.has("acceptpfid"))
					acceptpfid = (String) dobj.get("acceptpfid"); 
				
				String requestmessage = ""; 
				if(dobj.has("requestmessage"))
					requestmessage = (String) dobj.get("requestmessage"); 
				
				String requeststart = ""; 
				if(dobj.has("requeststart"))
					requeststart = (String) dobj.get("requeststart"); 
				
				String fromname = ""; 
				if(dobj.has("fromname"))
					fromname = (String) dobj.get("fromname"); 
				
				String mdmstatus = ""; 
				if(dobj.has("mdmstatus"))
					mdmstatus = (String) dobj.get("mdmstatus"); 
				
				String sex = ""; 
				if(dobj.has("sex"))
					sex = (String) dobj.get("sex"); 
				
				String area = ""; 
				if(dobj.has("area"))
					area = (String) dobj.get("area"); 
				
				String acceptname = ""; 
				if(dobj.has("acceptname"))
					acceptname = (String) dobj.get("acceptname"); 
				
				String imgurl = ""; 
				if(dobj.has("imgurl"))
					imgurl = (String) dobj.get("imgurl"); 
				
				String account = ""; 
				if(dobj.has("account"))
					account = (String) dobj.get("account"); 
				
				String signature = ""; 
				if(dobj.has("signature"))
					signature = (String) dobj.get("signature"); 
				
				String companyid = ""; 
				if(dobj.has("companyid"))
					companyid = (String) dobj.get("companyid"); 
				
				String storeid = ""; 
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
					
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("frompfid", frompfid);
				map.put("acceptpfid", acceptpfid);
				map.put("requestmessage", requestmessage);
				map.put("requeststart", requeststart);
				map.put("fromname", fromname);
				map.put("sex", sex);
				map.put("area", area);
				map.put("acceptname", acceptname);
//				if(imgurl != null && !imgurl.equals(""))
//				{
//					String furl = fileUtil.getImageFile2aPath(frompfid, frompfid);
//					Bitmap bmpsimg = returnUserImgBitMap(imgurl);
//					if(bmpsimg != null)
//					{
//						if(!fileUtil.isFileExist2(frompfid))
//							fileUtil.createUserFile(frompfid);
//						saveMyBitmap(furl, bmpsimg);
//						imgurl = furl;
//					}
//				}
				map.put("imgurl", imgurl);
				map.put("account", account);
				map.put("signature", signature);
				map.put("companyid", companyid);
				map.put("storeid", storeid);
				
				if(requeststart.equals(""))
					newnumber++;
	
				list.add(map);
			}
			dmap.put("datalist", list);
			dmap.put("newnumber", newnumber);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dmap;
	}
	
	public List<Map<String,Object>> getMessageDetialData(JSONArray jArr)
	{
		String ip = getHost();
		BASE_URL = "http://"+ip+":80/upload/";
		BASE_URL2 = "http://"+ip+":80/customize/control/";
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
//			for (int i = jArr.length()-1; i >= 0; i--) {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String mid = ""; 
				if(dobj.has("mid"))
					mid = (String) dobj.get("mid");
				
				String groupid = ""; 
				if(dobj.has("groupid"))
					groupid = (String) dobj.get("groupid");
					
				String groupStaff = ""; 
				if(dobj.has("groupStaff"))
					groupStaff = (String) dobj.get("groupStaff");
				
				String groupName = ""; 
				if(dobj.has("groupName"))
					groupName = (String) dobj.get("groupName");
				
				String toid = ""; 
				if(dobj.has("toid"))
					toid = (String) dobj.get("toid");
				
				String sender = ""; 
				if(dobj.has("sender"))
					sender = (String) dobj.get("sender");
				
				String fname = ""; 
				if(dobj.has("fname"))
					fname = (String) dobj.get("fname");
				
				String tname = ""; 
				if(dobj.has("tname"))
					tname = (String) dobj.get("tname");
				
				String reciver = ""; 
				if(dobj.has("reciver"))
					reciver = (String) dobj.get("reciver");
				
				String content = ""; 
				if(dobj.has("content"))
				{
					content = (String) dobj.get("content");
//					if(content.equals("【图片】") || content.equals("【语音】"))
//						content = "";
				}
				
				String fuserimg = ""; 
				if(dobj.has("fuserimg"))
					fuserimg = (String) dobj.get("fuserimg");
				
				String serviceid = ""; 
				if(dobj.has("serviceid"))
					serviceid = (String) dobj.get("serviceid");
				
				String servicename = ""; 
				if(dobj.has("servicename"))
					servicename = (String) dobj.get("servicename");
				
				String title = ""; //表示是我们自己的app过来还是微信过来
				if(dobj.has("title"))
					title = (String) dobj.get("title");
				
				String sendTime = ""; 
				if(dobj.has("sendTime"))
				{
					sendTime = (String)dobj.get("sendTime");
				}
				
				String time = "1"; 
				if(dobj.has("time"))
				{
					time = (String)dobj.get("time");
					if(time.equals(""))
						time = "1";
				}
				
				String fileType = "";
				String fileType2 = "";
				if(dobj.has("fileType"))
				{
					String fileTypes = dobj.getString("fileType");
					if(fileTypes != null && !fileTypes.equals(""))
					{
						String [] filetypes = fileTypes.split(",");
						if(filetypes.length > 1)
						{
							String str = filetypes[0];
							String str2 = filetypes[1];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = str2;
							}
							else
							{
								fileType = str2;
								fileType2 = str;
							}
						}
						else
						{
							String str = filetypes[0];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = "";
							}
							else
							{
								fileType = "";
								fileType2 = str;
							}
						}
					}
				}
				
				String fileName = "";
				String fileName2 = "";
				String fileUrl = ""; 
				String fileUrl2 = "";
				if(dobj.has("fileName"))
				{
					String fileNames = dobj.getString("fileName");
					if(fileNames != null && !fileNames.equals(""))
					{
						String [] filenames = fileNames.split(",");
						if(filenames.length > 1)
						{
							String str = filenames[0];
							String str2 = filenames[1];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = filenames[1];
								fileUrl = BASE_URL + filenames[1];
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = filenames[1];
								fileUrl2 = BASE_URL + filenames[1];
							}
						}
						else
						{
							String str = filenames[0];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = "";
								fileUrl = "";
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = "";
								fileUrl2 = "";
							}
						}
					}
				}
				
				String messagetype = ""; 
				if(dobj.has("messagetype"))
				{
					messagetype = (String)dobj.get("messagetype");
				}
				
				JSONObject job = null;
				if(dobj.has("jsoncontent"))
				{
					job = dobj.getJSONObject("jsoncontent");
				}
				
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mid", mid);
				map.put("groupid", groupid);
				map.put("groupStaff",groupStaff);
				map.put("groupName", groupName);
				map.put("storeid", sender);
				map.put("serviceid", serviceid);
				map.put("servicename", servicename);
				map.put("sname", fname);
				map.put("nameid", toid);
				map.put("toid", toid);
				map.put("mysendname", sender);
				if(toid.equals(sender))
					map.put("userimg", getUserimgbitmap());
				else
				{
					map.put("userimg", getStoreimgbitmap());
				}
				map.put("fname", fname);
				map.put("tname", tname);
				map.put("yiman", R.drawable.yi_man);
				map.put("mymessagecontent", content);
				map.put("watag", title);
				map.put("mysendtime",sendTime);
				if(fileUrl != null && !fileUrl.equals(""))
		        {
					if(fileUrl.indexOf("http") >= 0)
		            {
						Bitmap bitmimg = getGossipImage(fileUrl);
			            if(bitmimg != null)
			            {
			            	fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
							String [] names = fileName.split("\\.");
			            	String username = (String)map.get("mysendname");
			            	bitmimg = adaptive(bitmimg, getScreenWidth(), getScreenHeight());
							Bitmap iconbitmap = adaptive(bitmimg);
							UUID uuid = UUID.randomUUID();
							String fileNames = uuid.toString() + "." + names[1];
							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
							String iconfurl = fileUtil.getImageFile1aPath(username, uuid.toString());
							if(!fileUtil.isFileExist2(username))
								fileUtil.createUserFile(username);
							saveMyBitmap(furl, bitmimg,iconfurl,iconbitmap);
							map.put("fileName", fileNames);
							map.put("fileUrl", furl);
			            }
		            }
		        }
				else
				{
					map.put("fileUrl",fileUrl);
					map.put("fileName", fileName);
				}
				if(fileUrl2 != null && !fileUrl2.equals(""))
	            {
		            String username = (String)map.get("mysendname");
		            String touserid = (String)map.get("toname");
		            if(fileUrl2.indexOf("http") >= 0)
		            {
		            	String filePath = null;
		            	if(username.equals(getPfprofileId()))
		            	{
		            		filePath = fileUtil.createVoice2File1a(touserid, fileUrl2, fileName2);
		            	}
		            	else
		            	{
		            		filePath = fileUtil.createVoiceFile1a(username, fileUrl2, fileName2);
		            	}
		            	
						if (filePath != null) {
							map.put("fileUrl2", filePath);
						}
		            }
		            else
		            {
						map.put("fileUrl2", fileUrl2);
		            }
	            }
				if(fuserimg != null && !fuserimg.equals(""))
				{
					if(fuserimg.indexOf("http") >= 0)
		            {
						String fuserimgname = fuserimg.substring(fuserimg.lastIndexOf("/")+1,fuserimg.length());
						String [] names = fuserimgname.split("\\.");
		            	String username = (String)map.get("mysendname");
						String fileNames = names[0];
						String iconfurl = fileUtil.getImageFile1aPath(username, fileNames);
						File file = new File(iconfurl);
						
						if(!file.exists())
						{
							Bitmap bitmimg = getGossipImage(fuserimg);
				            if(bitmimg != null)
				            {
				            	Bitmap iconbitmap = adaptive(bitmimg);
	//							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
								if(!fileUtil.isFileExist2(username))
									fileUtil.createUserFile(username);
								
								saveMyBitmap(iconfurl,iconbitmap);
								map.put("fuserimg", iconfurl);
				            }
						}
						else
						{
							map.put("fuserimg", iconfurl);
						}
		            }
				}
				else
				{
					map.put("fuserimg", "");
				}
				map.put("toname", reciver);
				map.put("fileType", fileType);
				map.put("fileType2", fileType2);
				map.put("fileName2", fileName2);
				map.put("time", time);
				map.put("timetext", time+"″");
				map.put("messagetype", messagetype);
				map.put("sendimg", "1");
				map.put("sendprogress", "1");
				if(job != null)
				{
					String username = (String)map.get("mysendname");
					if(messagetype.equals("fj"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的房间");
						}
					}
					else if(messagetype.equals("js"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String storeDoc = job.getString("storeDoc");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", null);
					}
					else if(messagetype.equals("yh"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getStoreCoupData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的优惠券及活动");
						}
					}
					else if(messagetype.equals("tq"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getCustomizeData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的天气信息");
						}
					}
					else if(messagetype.equals("dz"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String addressInfomation = job.getString("addressInfomation");
						String longItude = job.getString("longItude");
						String woof = job.getString("woof");
						String storeDoc = job.getString("storeDoc");
						String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x250&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
						map.put("storeimg", url);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", null);
					}
					else if(messagetype.equals("zdy"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String storeDoc = job.getString("storeDoc");
						String url = job.getString("url");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", url);
					}
					else if(messagetype.equals("ct"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的餐厅");
						}
					}
					else if(messagetype.equals("zb"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,username);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "该商家周边没有找到相应娱乐设施");
						}
					}
				}
				
				map.put("isRead", "1");
				
				dlist.add(map);
			}
//			saveMessageLocal(dlist);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public List<Map<String,Object>> getMessageDetialData(JSONArray jArr,String toname,String storeName,String nameid,String fromname,boolean b)
	{
		String ip = getHost();
		BASE_URL = "http://"+ip+":80/upload/";
		BASE_URL2 = "http://"+ip+":80/customize/control/";
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
//			for (int i = jArr.length()-1; i >= 0; i--) {
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String storeid = ""; 
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid");
				
				String storeNames = ""; 
				if(dobj.has("storeName"))
					storeNames = (String) dobj.get("storeName");
				
				String mid = ""; 
				if(dobj.has("mid"))
					mid = (String) dobj.get("mid");
				
				String groupid = ""; 
				if(dobj.has("groupid"))
					groupid = (String) dobj.get("groupid");
					
				String groupStaff = ""; 
				if(dobj.has("groupStaff"))
					groupStaff = (String) dobj.get("groupStaff");
				
				String groupName = ""; 
				if(dobj.has("groupName"))
					groupName = (String) dobj.get("groupName");
				
				String toid = ""; 
				if(dobj.has("toid"))
					toid = (String) dobj.get("toid");
				
				String sender = ""; 
				if(dobj.has("sender"))
					sender = (String) dobj.get("sender");
				
				String fname = ""; 
				if(dobj.has("fname"))
					fname = (String) dobj.get("fname");
				
				String tname = ""; 
				if(dobj.has("tname"))
					tname = (String) dobj.get("tname");
				
				String reciver = ""; 
				if(dobj.has("reciver"))
					reciver = (String) dobj.get("reciver");
				
				String content = ""; 
				if(dobj.has("content"))
				{
					try{
						content = (String) dobj.get("content");
					}catch(Exception ex){
						Object jj = dobj.get("content");
						content = jj.toString();
					}
//					if(content.equals("【图片】") || content.equals("【语音】"))
//						content = "";
				}
				
				String fuserimg = ""; 
				if(dobj.has("fuserimg"))
					fuserimg = (String) dobj.get("fuserimg");
				
				String serviceid = ""; 
				if(dobj.has("serviceid"))
					serviceid = (String) dobj.get("serviceid");
				
				String servicename = ""; 
				if(dobj.has("servicename"))
					servicename = (String) dobj.get("servicename");
				
				String title = ""; //表示是我们自己的app过来还是微信过来
				if(dobj.has("title"))
					title = (String) dobj.get("title");
				
				String sendTime = ""; 
				if(dobj.has("sendTime2"))
				{
					sendTime = (String)dobj.get("sendTime2");
				}
				
				String time = "1"; 
				if(dobj.has("time"))
				{
					time = (String)dobj.get("time");
					if(time == null || time.equals(""))
						time = "1";
				}
				
				String fileType = "";
				String fileType2 = "";
				if(dobj.has("fileType"))
				{
					String fileTypes = dobj.getString("fileType");
					if(fileTypes != null && !fileTypes.equals(""))
					{
						String [] filetypes = fileTypes.split(",");
						if(filetypes.length > 1)
						{
							String str = filetypes[0];
							String str2 = filetypes[1];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = str2;
							}
							else
							{
								fileType = str2;
								fileType2 = str;
							}
						}
						else
						{
							String str = filetypes[0];
							if(str.equals("image/png"))
							{
								fileType = str;
								fileType2 = "";
							}
							else
							{
								fileType = "";
								fileType2 = str;
							}
						}
					}
				}
				
				String fileName = "";
				String fileName2 = "";
				String fileUrl = ""; 
				String fileUrl2 = "";
				if(dobj.has("fileName"))
				{
					String fileNames = dobj.getString("fileName");
					if(fileNames != null && !fileNames.equals(""))
					{
						String [] filenames = fileNames.split(",");
						if(filenames.length > 1)
						{
							String str = filenames[0];
							String str2 = filenames[1];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = filenames[1];
								fileUrl = BASE_URL + filenames[1];
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = filenames[1];
								fileUrl2 = BASE_URL + filenames[1];
							}
						}
						else
						{
							String str = filenames[0];
							if(str.contains(".amr"))
							{
								fileName2 = filenames[0];
								fileUrl2 = BASE_URL + filenames[0];
								
								fileName = "";
								fileUrl = "";
							}
							else
							{
								fileName = filenames[0];
								fileUrl = BASE_URL + filenames[0];
								
								fileName2 = "";
								fileUrl2 = "";
							}
						}
					}
				}
				
				String messagetype = ""; 
				if(dobj.has("messagetype"))
				{
					messagetype = (String)dobj.get("messagetype");
				}
				
				JSONObject job = null;
				if(dobj.has("jsoncontent"))
				{
					job = dobj.getJSONObject("jsoncontent");
				}
				
//				String userimg = ""; 
//				if(dobj.has("storeimg"))
//				{
//					userimg = (String)dobj.get("storeimg");
//					if(userimg != null && !userimg.equals(""))
//						userimg = BASE_URL + userimg;
//				}
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mid", mid);
				map.put("groupid", groupid);
				map.put("groupStaff",groupStaff);
				map.put("groupName", groupName);
				if(!storeid.equals(""))
					map.put("storeid", storeid);
				else
					map.put("storeid", toname);
				if(!storeNames.equals(""))
					map.put("sname", storeNames);
				else
					map.put("sname", storeName);
				map.put("nameid", nameid);
				map.put("toid", toid);
				map.put("mysendname", sender);
				if(fromname.equals(sender))
					map.put("userimg", getUserimgbitmap());
				else
					map.put("userimg", getStoreimgbitmap());
				map.put("fname", fname);
				map.put("tname", tname);
				map.put("toname", reciver);
				map.put("yiman", R.drawable.yi_man);
				map.put("mymessagecontent", content);
				map.put("serviceid", serviceid);
				map.put("servicename", servicename);
				map.put("watag", title);
				map.put("mysendtime",format.format(new Date()));
				if(fileUrl != null && !fileUrl.equals(""))
		        {
					if(fileUrl.indexOf("http") >= 0)
		            {
						Bitmap bitmimg = getGossipImage(fileUrl);
			            if(bitmimg != null)
			            {
			            	fileUrl = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
							String [] names = fileName.split("\\.");
			            	String username = (String)map.get("mysendname");
			            	bitmimg = adaptive(bitmimg, getScreenWidth(), getScreenHeight());
							Bitmap iconbitmap = adaptive(bitmimg);
							UUID uuid = UUID.randomUUID();
							String fileNames = uuid.toString() + "." + names[1];
							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
							String iconfurl = fileUtil.getImageFile1aPath(username, uuid.toString());
							if(!fileUtil.isFileExist2(username))
								fileUtil.createUserFile(username);
							saveMyBitmap(furl, bitmimg,iconfurl,iconbitmap);
							map.put("fileName", fileNames);
							map.put("fileUrl", furl);
			            }
		            }
		        }
				else
				{
					map.put("fileUrl",fileUrl);
					map.put("fileName", fileName);
				}
//				map.put("fileUrl2",fileUrl2);
				if(fileUrl2 != null && !fileUrl2.equals(""))
	            {
		            String username = (String)map.get("mysendname");
		            String touserid = (String)map.get("toname");
		            if(fileUrl2.indexOf("http") >= 0)
		            {
		            	String filePath = null;
		            	if(username.equals(getPfprofileId()))
		            	{
		            		filePath = fileUtil.createVoice2File1a(touserid, fileUrl2, fileName2);
		            	}
		            	else
		            	{
		            		filePath = fileUtil.createVoiceFile1a(username, fileUrl2, fileName2);
		            	}
		            	
						if (filePath != null) {
							map.put("fileUrl2", filePath);
						}
		            }
		            else
		            {
						map.put("fileUrl2", fileUrl2);
		            }
	            }
				if(fuserimg != null && !fuserimg.equals(""))
				{
					if(fuserimg.indexOf("http") >= 0)
		            {
						String fuserimgname = fuserimg.substring(fuserimg.lastIndexOf("/")+1,fuserimg.length());
						String [] names = fuserimgname.split("\\.");
		            	String username = (String)map.get("mysendname");
						String fileNames = names[0];
						String iconfurl = fileUtil.getImageFile1aPath(username, fileNames);
						File file = new File(iconfurl);
						
						if(!file.exists())
						{
							Bitmap bitmimg = getGossipImage(fuserimg);
				            if(bitmimg != null)
				            {
				            	Bitmap iconbitmap = adaptive(bitmimg);
	//							String furl = fileUtil.getImageFile1bPath(username, uuid.toString());
								if(!fileUtil.isFileExist2(username))
									fileUtil.createUserFile(username);
								
								saveMyBitmap(iconfurl,iconbitmap);
								map.put("fuserimg", iconfurl);
				            }
						}
						else
						{
							map.put("fuserimg", iconfurl);
						}
		            }
				}
				else
				{
					map.put("fuserimg", "");
				}
				map.put("fileType", fileType);
				map.put("fileType2", fileType2);
				map.put("fileName2", fileName2);
				map.put("time", time);
				map.put("timetext", time+"″");
				map.put("messagetype", messagetype);
				map.put("sendimg", "1");
				map.put("sendprogress", "1");
				if(job != null)
				{
					if(messagetype.equals("fj"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的房间");
						}
					}
					else if(messagetype.equals("js"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String storeDoc = job.getString("storeDoc");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", null);
					}
					else if(messagetype.equals("yh"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getStoreCoupData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的优惠券及活动");
						}
					}
					else if(messagetype.equals("tq"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getCustomizeData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的天气信息");
						}
					}
					else if(messagetype.equals("dz"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String addressInfomation = job.getString("addressInfomation");
						String longItude = job.getString("longItude");
						String woof = job.getString("woof");
						String storeDoc = job.getString("storeDoc");
						String url = "http://maps.googleapis.com/maps/api/staticmap?center="+woof+","+longItude+"&zoom=17&size=470x250&key=ABQIAAAA9rPHPzW1TH1vr2ejmjcezxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxS10E4LTpyI96dJmlDoOiCIW9u_kA&markers=color:blue|label:S|"+woof+","+longItude+"&sensor=false";
						map.put("storeimg", url);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", null);
					}
					else if(messagetype.equals("zdy"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String storeDoc = job.getString("storeDoc");
						String url = job.getString("url");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", storeDoc);
						map.put("storelist", null);
						map.put("url", url);
					}
					else if(messagetype.equals("ct"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "没有找到相应的餐厅");
						}
					}
					else if(messagetype.equals("zb"))
					{
						String storename = job.getString("name");
						String storeimg = job.getString("img");
						String imgfile = saveStoreImage(BASE_URL+storeimg,storeid);
						map.put("storeimg", imgfile);
						map.put("storename", storename);
						map.put("storeDoc", null);
						map.put("url", null);
						if(job.has("dlist"))
						{
							JSONArray jArray = (JSONArray) job.get("dlist");
							List<Map<String,Object>> dlists = getRoomDetialData(jArray);
							map.put("storelist", dlists);
						}
						else
						{
							map.put("messagetype", "");
							map.put("mymessagecontent", "该商家周边没有找到相应娱乐设施");
						}
					}
				}
				
				if(b)
				{
					map.put("isRead", "1");
				}
				else
				{
//					if(reciver.equals(toname) || sender.equals(toname))//如果不等于当前商家的话需要响铃震动
					if(sender.equals(MessageListActivity.instance.toname))
					{
						map.put("isRead", "0");
					}
					else if(groupid.equals(MessageListActivity.instance.toname))
					{
						map.put("isRead", "0");
					}
					else
					{
	//					shockAndRingtones();
						map.put("isRead", "1");
					}
				}
				dlist.add(map);
			}
			
//			saveMessageLocal(dlist);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public List<Map<String,Object>> getGroupNameIdListData(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
//			nicks = new String[jArr.length()];
//			userimgs = new String[jArr.length()];
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String userid = ""; 
				if(dobj.has("userid"))
					userid = (String) dobj.get("userid"); 
				
				String username = ""; 
				if(dobj.has("username"))
					username = (String) dobj.get("username");
				
				String userimg = ""; 
				if(dobj.has("userimg"))
					userimg = (String) dobj.get("userimg");
				
				String account = ""; 
				if(dobj.has("account"))
					account = (String) dobj.get("account");
				
				String sex = ""; 
				if(dobj.has("sex"))
					sex = (String) dobj.get("sex");
				
				String signature = ""; 
				if(dobj.has("signature"))
					signature = (String) dobj.get("signature");
				
				String storeids = ""; 
				if(dobj.has("storeids"))
					storeids = (String) dobj.get("storeids");
				
				String area = ""; 
				if(dobj.has("area"))
					area = (String) dobj.get("area");
				
				String companyid = ""; 
				if(dobj.has("companyid"))
					companyid = (String) dobj.get("companyid");
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("userid", userid);
				map.put("username", username);
				map.put("userimg", userimg);
				map.put("account", account);
				map.put("sex", sex);
				map.put("area", area);
				map.put("signature", signature);
				map.put("companyid", companyid);
				map.put("storeids", storeids);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public static String saveStoreImage(String storeimg,String username)
	{
		String filepath = "";
		try{
			if(storeimg != null && !storeimg.equals(""))
            {
            	if(storeimg.indexOf("http") >= 0)
            	{
		            Bitmap bmpsimg = returnBitMap(storeimg);
		            if(bmpsimg != null)
		            {
		            	int width = bmpsimg.getWidth();
						int height = bmpsimg.getHeight();
						int newwidth = 0;
						int newheight = 140;
						if(height > newheight || height < newheight)
						{
							BigDecimal b = new BigDecimal((float)height / (float)width);  
							float bili = b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();;
							newwidth = (int)(newheight/bili);
							if(newwidth >= 300)
								bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
							else
							{
								newwidth = 300;
								BigDecimal wb = new BigDecimal((float)width / (float)height);
								float wbili = wb.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
								newheight = (int)(newwidth/wbili);
								bmpsimg = Bitmap.createScaledBitmap(bmpsimg,newwidth,newheight,true);
							}
						}
						
						int mScreenWidth = (bmpsimg.getWidth() - 300) / 2;
						int mScreenHeight = (bmpsimg.getHeight() - 140) / 2;
						if(mScreenWidth < 0)
							mScreenWidth = 0;
						if(mScreenHeight < 0)
							mScreenHeight = 0;
						bmpsimg = Bitmap.createBitmap(bmpsimg, mScreenWidth, mScreenHeight, 300, 140);
						
						UUID uuid = UUID.randomUUID();
						String furl = fileUtil.getImageFile2aPath(username, uuid.toString());
						if(!fileUtil.isFileExist2(username))
							fileUtil.createUserFile(username);
						saveMyBitmap(furl, bmpsimg);
						filepath = furl;
		            }
            	}
            	else
            	{
            		filepath = storeimg;
            	}
            }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return filepath;
	}
	
	public static List<Map<String,Object>> getStoreCoupData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String couponName = ""; 
				if(dobj.has("couponName"))
					couponName = (String) dobj.get("couponName");
				
				String couponDesc = ""; 
				if(dobj.has("couponDesc"))
					couponDesc = (String) dobj.get("couponDesc");
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", couponName);
				map.put("desc", couponDesc);
				map.put("price", "");
				map.put("sysImg", sysImg);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public static List<Map<String,Object>> getCustomizeData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String title = ""; 
				if(dobj.has("title"))
					title = (String) dobj.get("title");
				
				String desc = ""; 
				if(dobj.has("desc"))
					desc = (String) dobj.get("desc");
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", title);
				map.put("desc", desc);
				map.put("price", "");
				map.put("sysImg", sysImg);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public List<Map<String,Object>> getRoomDetialData(JSONArray jArr)
	{
		List<Map<String,Object>> dlist = new ArrayList<Map<String,Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobj = (JSONObject) jArr.get(i);
				
				String roomStyle = ""; 
				if(dobj.has("roomStyle"))
					roomStyle = (String) dobj.get("roomStyle");
				
				String roomDesc = ""; 
				if(dobj.has("roomDesc"))
					roomDesc = (String) dobj.get("roomDesc");
				
				String pkid = ""; 
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String roomPrice = ""; 
				if(dobj.has("roomPrice"))
				{
					roomPrice = (String) dobj.get("roomPrice");
					if(!roomPrice.equals(""))
					{
						String [] str = roomPrice.split("\\.");
						roomPrice = str[0] + "￥";
					}
				}
				
				String sysImg = ""; 
				if(dobj.has("sysImg"))
				{
					sysImg = (String) dobj.get("sysImg");
					if(!sysImg.equals(""))
						sysImg = BASE_URL + sysImg;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", roomStyle);
				map.put("desc", roomDesc);
				map.put("price", roomPrice);
				map.put("sysImg", sysImg);
				map.put("pkid", pkid);
				
				dlist.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dlist;
	}
	
	public List<Map<String,Object>> getMyFriendList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = "";
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String start = ""; 
				if(dobj.has("start"))
					start = (String) dobj.get("start"); 
				
				String username = ""; 
				if(dobj.has("username"))
					username = (String) dobj.get("username"); 
				
				String pfid = ""; 
				if(dobj.has("pfid"))
					pfid = (String) dobj.get("pfid"); 
				
				String sex = ""; 
				if(dobj.has("sex"))
					sex = (String) dobj.get("sex"); 
				
				String area = ""; 
				if(dobj.has("area"))
					area = (String) dobj.get("area"); 
				
				String imgurl = ""; 
				if(dobj.has("imgurl"))
					imgurl = (String) dobj.get("imgurl"); 
				
				String account = ""; 
				if(dobj.has("account"))
					account = (String) dobj.get("account"); 
				
				String signature = ""; 
				if(dobj.has("signature"))
					signature = (String) dobj.get("signature"); 
				
				String companyid = ""; 
				if(dobj.has("companyid"))
					companyid = (String) dobj.get("companyid"); 
				
				String storeid = ""; 
				if(dobj.has("storeid"))
					storeid = (String) dobj.get("storeid"); 
				
					
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("username", username);
				map.put("pfid", pfid);
//				if(imgurl != null && !imgurl.equals(""))
//				{
//					String furl = fileUtil.getImageFile2aPath(pfid, pfid);
//					Bitmap bmpsimg = returnUserImgBitMap(imgurl);
//					if(bmpsimg != null)
//					{
//						if(!fileUtil.isFileExist2(pfid))
//							fileUtil.createUserFile(pfid);
//						
//						saveMyBitmap(furl, bmpsimg);
//						imgurl = furl;
//					}
//				}
				map.put("imgurl", imgurl);
				
				map.put("account", account);
				map.put("sex", sex);
				map.put("area", area);
				map.put("signature", signature);
				map.put("start", start);
				map.put("companyid", companyid);
				map.put("storeid", storeid);
	
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMyFriendGuolvList(JSONArray jArr,Map<String,Object> friendmap){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = "";
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				if(!friendmap.containsKey(pkid))
				{
					String start = ""; 
					if(dobj.has("start"))
						start = (String) dobj.get("start"); 
					
					String username = ""; 
					if(dobj.has("username"))
						username = (String) dobj.get("username"); 
					
					String pfid = ""; 
					if(dobj.has("pfid"))
						pfid = (String) dobj.get("pfid"); 
					
					String sex = ""; 
					if(dobj.has("sex"))
						sex = (String) dobj.get("sex"); 
					
					String area = ""; 
					if(dobj.has("area"))
						area = (String) dobj.get("area"); 
					
					String imgurl = ""; 
					if(dobj.has("imgurl"))
						imgurl = (String) dobj.get("imgurl"); 
					
					String account = ""; 
					if(dobj.has("account"))
						account = (String) dobj.get("account"); 
					
					String signature = ""; 
					if(dobj.has("signature"))
						signature = (String) dobj.get("signature"); 
					
					String companyid = ""; 
					if(dobj.has("companyid"))
						companyid = (String) dobj.get("companyid"); 
					
					String storeid = ""; 
					if(dobj.has("storeid"))
						storeid = (String) dobj.get("storeid"); 
					
						
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("pkid", pkid);
					map.put("username", username);
					map.put("pfid", pfid);
					if(imgurl != null && !imgurl.equals(""))
					{
						String furl = fileUtil.getImageFile2aPath(pfid, pfid);
						Bitmap bmpsimg = returnUserImgBitMap(imgurl);
						if(bmpsimg != null)
						{
							if(!fileUtil.isFileExist2(pfid))
								fileUtil.createUserFile(pfid);
							
							saveMyBitmap(furl, bmpsimg);
							imgurl = furl;
						}
					}
					map.put("imgurl", imgurl);
					
					map.put("account", account);
					map.put("sex", sex);
					map.put("area", area);
					map.put("signature", signature);
					map.put("start", start);
					map.put("companyid", companyid);
					map.put("storeid", storeid);
		
					list.add(map);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getMyFriendMomentsList(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String ip = getHost();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String pkid = "";
				if(dobj.has("pkid"))
					pkid = (String) dobj.get("pkid");
				
				String content = ""; 
				if(dobj.has("content"))
					content = (String) dobj.get("content"); 
				
				String publishtime = ""; 
				if(dobj.has("publishtime"))
				{
					publishtime = (String) dobj.get("publishtime"); 
//					if(publishtime.equals("0"))
//						publishtime = getString(R.string.fmt_pre_nowday);
//					else
//						publishtime = String.format(getString(R.string.fmt_indayh), Integer.valueOf(publishtime));
				}
				
				String publishuser = ""; 
				if(dobj.has("publishuser"))
					publishuser = (String) dobj.get("publishuser"); 
				
				String publishid = ""; 
				if(dobj.has("publishid"))
					publishid = (String) dobj.get("publishid"); 
				
				String publishtype = ""; 
				if(dobj.has("publishtype"))
					publishtype = (String) dobj.get("publishtype");
				
				String position = ""; 
				if(dobj.has("position"))
					position = (String) dobj.get("position");
				
				JSONArray filelist = null;
				List<Map<String,String>> files = new ArrayList<Map<String,String>>();
				List<Map<String,String>> files2 = new ArrayList<Map<String,String>>();
				List<Map<String,String>> filesNoUpload = new ArrayList<Map<String,String>>();
				if(dobj.has("filelist"))
				{
					filelist = (JSONArray) dobj.get("filelist");
					for(int j=0;j<filelist.length();j++)
					{
						JSONObject filejob = filelist.getJSONObject(j);
						String filename = filejob.getString("Meta_attachmentname");
						filename = java.net.URLEncoder.encode(filename,"UTF-8"); 
						String fileurl = "http://"+ip+":80/upload/"+filename;
						String filetype = filejob.getString("Meta_attachmenttype");
						Map<String,String> filemap = new HashMap<String,String>();
						UUID uuid = UUID.randomUUID();
						filemap.put("pkid", uuid.toString());
						filemap.put("filename", filename);
//						filemap.put("fileurl", fileurl);
						int filewidth = 0;
						int fileheight = 0;
						int imgsize = 0;
						if(fileurl.contains("http://"))
		        		{
							if(filetype.equals("4")){
								String fname = fileurl.substring(fileurl.lastIndexOf("/")+1,fileurl.length());
								String [] fnames = fname.split("\\.");
								fname = fnames[0];
								
								String furl = fileUtil.getImageFile1aPath(publishid, fname);
								String furl2 = fileUtil.getImageFile1bPath(publishid, fname);
								
								File file3 = new File(furl);
								if(!file3.exists())
								{
//									Bitmap bitmimg = returnBitMap(fileurl);
//					        		if(bitmimg != null)
//						            {
//					        			if(!fileUtil.isFileExist2(publishid))
//						            	{
//						            		fileUtil.createUserFile(publishid);
//						            	}
//					        			
//					//        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//					        			Bitmap bitmap2 = null;
//					        			if(imgsize > 0)
//					        			{
////					        				bitmap2 = adaptive(bitmimg,100,100);
//					        				bitmap2 = Bitmap.createScaledBitmap(bitmimg, 100, 100,true);
//					        			}
//					        			else
//					        			{
//					        				bitmap2 = adaptive(bitmimg,300,300);ddddd
//					        			}
//					        			filewidth = bitmap2.getWidth();
//					        			fileheight = bitmap2.getHeight();
//										
//										fileUtil.saveMyBitmap(furl, bitmap2);
//										fileUtil.saveMyBitmap(furl2, bitmimg);
//										filemap.put("fileurl", furl2);
//						            }
									filemap.put("fileurl", fileurl);
								}
								else
								{
//									Bitmap bitmimg = getLoacalBitmap(furl2);
									Bitmap bitmap2 = getLoacalBitmap(furl);
									int width = getScreenWidth()-150;
									bitmap2 = adaptive(bitmap2,width,width);
									
									filewidth = bitmap2.getWidth();
				        			fileheight = bitmap2.getHeight();
									
//									fileUtil.saveMyBitmap(furl, bitmap2);
//									fileUtil.saveMyBitmap(furl2, bitmimg);
									filemap.put("fileurl", furl2);
								}
								
								imgsize++;
								filemap.put("filetype", filetype);
								filemap.put("momentsid", pkid);
								filemap.put("filewidth", String.valueOf(filewidth));
								filemap.put("fileheight", String.valueOf(fileheight));
								files.add(filemap);
							}else{
								if (!fileUtil.isFileExist2(publishid)) {
									fileUtil.createUserFile(publishid);
								}
								String filepath = fileUtil.getImageFile1aPath(publishid,filename);
								File file = new File(filepath);
								if(!file.exists())
								{
									fileUtil.downFile(fileurl,filepath);
								}
								
								String imgpath = "";
								if(filetype.equals("2"))//如果是视频的话获取缩略图保存
								{
									String [] strs = filename.split("\\.");
									String name = strs[0]+"img";
									imgpath = fileUtil.getImageFile1aPath(publishid,name);
									File file2 = new File(imgpath);
									if(!file2.exists())
									{
										Bitmap b = BitmapUtil.getVideoThumbnail(MomentsActivity.instance.getContentResolver(), filepath);
							        	b = adaptive(b,100,100);
							        	if(!fileUtil.isFileExist2(publishid))
											fileUtil.createUserFile(publishid);
							        	
							        	fileUtil.saveMyBitmap(imgpath, b);
									}
								}
								filemap.put("voidimg", imgpath);
								filemap.put("fileurl", filepath);
								
								filemap.put("filetype", filetype);
								filemap.put("momentsid", pkid);
								filemap.put("filewidth", String.valueOf(filewidth));
								filemap.put("fileheight", String.valueOf(fileheight));
								files2.add(filemap);
							}
		        		}
						
					}
				}
				
				//评论
				//"commentlist":[{"pkid":"Msud4c86a880hlhv07iz","commentUsername":"连锐","cmmentDesc":"呵呵"}]
				JSONArray commentlist = null;
				List<Map<String,String>> comments = new ArrayList<Map<String,String>>();
				if(dobj.has("commentlist")){
					commentlist = (JSONArray) dobj.get("commentlist"); 
					for(int n=0;n<commentlist.length();n++){
						JSONObject commentJsonObj = commentlist.getJSONObject(n);
						String commentPkid = commentJsonObj.getString("pkid");
						String discusDesc = commentJsonObj.getString("cmmentDesc");
						String userName = commentJsonObj.getString("commentUsername");
						Map<String,String> commentmap = new HashMap<String,String>();
						commentmap.put("pkid", commentPkid);
						commentmap.put("userName", userName);
						commentmap.put("discusDesc", discusDesc);
						
						comments.add(commentmap);
					}
				}
					
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pkid", pkid);
				map.put("content", content);
				map.put("publishtime", getInterval(publishtime));
				map.put("publishuser", publishuser);
				map.put("publishtype", publishtype);
				map.put("position", position);
				
				map.put("publishid", publishid);
				map.put("filelist", files);
				map.put("filelist2", files2);
				
				map.put("commentlist", comments);
	
				list.add(map);
				
				//开启线程加载文件
//				System.out.println("为加载的文件数："+filesNoUpload.size());
//				final List<Map<String, String>> filesNoUploadTemp = filesNoUpload;
//				new Thread() {
//					public void run() {
//						Message msg = new Message();
//						msg.what = 0;
//						
//						for(int i=0; i<filesNoUploadTemp.size(); i++){
//							Map<String, String> mapTemp = filesNoUploadTemp.get(i);
//							String fileurl = mapTemp.get("url");
//							String furl = mapTemp.get("tagUrl1");
//							String furl2 = mapTemp.get("tagUrl2");
//							String publishid = mapTemp.get("publishid");
//							int imgsize = Integer.parseInt(mapTemp.get("imgsize"));
//							
//							Bitmap bitmimg = returnBitMap(fileurl);
//			        		if(bitmimg != null)
//				            {
//			        			if(!fileUtil.isFileExist2(publishid))
//				            	{
//				            		fileUtil.createUserFile(publishid);
//				            	}
//			        			
////			        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
//			        			Bitmap bitmap2 = null;
//			        			if(imgsize > 0)
//			        			{
////			        				bitmap2 = adaptive(bitmimg,100,100);
//			        				bitmap2 = Bitmap.createScaledBitmap(bitmimg, 100, 100,true);
//			        			}
//			        			else
//			        			{
//			        				bitmap2 = adaptive(bitmimg,300,300);
//			        			}
//			        			//filewidth = bitmap2.getWidth();
//			        			//fileheight = bitmap2.getHeight();
//								
//								fileUtil.saveMyBitmap(furl, bitmap2);
//								fileUtil.saveMyBitmap(furl2, bitmimg);
//				            }
//						}
//
//						handlerForLoadFile.sendMessage(msg);
//					}
//				}.start();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private Handler handlerForLoadFile = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				System.out.println("图片加载完毕");
				break;
			}
		}
	};
	
	public Map<String,Object> getMyFriendMoments(JSONObject job,JSONArray jArr){
		Map<String,Object> map = new HashMap<String,Object>();
		String ip = getHost();
		try{
			map.put("pkid", job.getString("Meta_publishboardId"));
			map.put("content", job.getString("Meta_content"));
			String publishtime = job.getString("Meta_publishtime");
			map.put("publishtime", getInterval(publishtime));
			map.put("publishuser", job.getString("username"));
			
			String pfid = job.getString("Meta_publishuser_id");
			map.put("publishid", pfid);
			
			List<Map<String,String>> files = new ArrayList<Map<String,String>>();
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String filename = "";
				if(dobj.has("filename"))
					filename = (String) dobj.get("filename");
				
				String fileurl = ""; 
				if(dobj.has("fileurl"))
				{
					fileurl = (String) dobj.get("fileurl"); 
					fileurl = "http://"+ip+":80/upload/"+filename;
				}
				
				String filetype = ""; 
				if(dobj.has("filetype"))
				{
					filetype = (String) dobj.get("filetype"); 
				}
				
				Map<String,String> filemap = new HashMap<String,String>();
				UUID uuid = UUID.randomUUID();
				filemap.put("pkid", uuid.toString());
				filemap.put("filename", filename);
//				filemap.put("fileurl", fileurl);
				int filewidth = 0;
				int fileheight = 0;
				if(fileurl.contains("http://"))
        		{
	        		Bitmap bitmimg = returnBitMap(fileurl);
	        		if(bitmimg != null)
		            {
	        			if(!fileUtil.isFileExist2(pfid))
		            	{
		            		fileUtil.createUserFile(pfid);
		            	}
	        			
	//        			bitmimg = Bitmap.createScaledBitmap(bitmimg, 80, 80,true);
	        			Bitmap bitmap2 = null;
	        			if(jArr.length() > 1)
	        			{
//	        				bitmap2 = adaptive(bitmimg,100,100);
	        				bitmap2 = Bitmap.createScaledBitmap(bitmimg, 100, 100,true);
	        			}
	        			else
	        			{
	        				bitmap2 = adaptive(bitmimg,300,300);
	        			}
	        			filewidth = bitmap2.getWidth();
	        			fileheight = bitmap2.getHeight();
						UUID fuuid = UUID.randomUUID();
						String furl = fileUtil.getImageFile1aPath(pfid, fuuid.toString());
						String furl2 = fileUtil.getImageFile1bPath(pfid, fuuid.toString());
						if(!fileUtil.isFileExist2(pfid))
							fileUtil.createUserFile(pfid);
						fileUtil.saveMyBitmap(furl, bitmap2);
						fileUtil.saveMyBitmap(furl2, bitmimg);
						filemap.put("fileurl", furl2);
		            }
        		}
				filemap.put("filetype", filetype);
				filemap.put("momentsid", job.getString("Meta_publishboardId"));
				filemap.put("filewidth", String.valueOf(filewidth));
				filemap.put("fileheight", String.valueOf(fileheight));
				files.add(filemap);
			}
			map.put("filelist", files);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
	public Map<String,Object> getMomentsGonGao(JSONObject job,JSONArray filelist){
		Map<String,Object> map = new HashMap<String,Object>();
		String ip = getHost();
		try{
			String pkid = job.getString("Meta_publishboardId");
			
			map.put("pkid", job.getString("Meta_publishboardId"));
			map.put("content", job.getString("Meta_content"));
			String publishtime = job.getString("Meta_publishtime");
			map.put("publishtime", getInterval(publishtime));
			map.put("publishuser", job.getString("username"));
			map.put("publishtype", job.getString("Meta_publishtype"));
			String position = job.getString("Meta_publishuser_position");
			if(position == null)
				position = "";
			map.put("position", position);
			
			String pfid = job.getString("Meta_publishuser_id");
			String publishid = pfid;
			map.put("publishid", pfid);
			
			
			List<Map<String,String>> files = new ArrayList<Map<String,String>>();
			List<Map<String,String>> files2 = new ArrayList<Map<String,String>>();
			for(int j=0;j<filelist.length();j++)
			{
				JSONObject filejob = filelist.getJSONObject(j);
				String filename = filejob.getString("filename");
//				filename = java.net.URLEncoder.encode(filename,"UTF-8"); 
				String fileurl = filejob.getString("fileurl");
				String filetype = filejob.getString("filetype");
				Map<String,String> filemap = new HashMap<String,String>();
				UUID uuid = UUID.randomUUID();
				filemap.put("pkid", uuid.toString());
				filemap.put("filename", filename);
				int filewidth = 0;
				int fileheight = 0;
				int imgsize = 0;
				if(fileurl.contains("http://"))
        		{
					if(filetype.equals("4")){
						String fname = fileurl.substring(fileurl.lastIndexOf("/")+1,fileurl.length());
						String [] fnames = fname.split("\\.");
						fname = fnames[0];
						
						String furl = fileUtil.getImageFile1aPath(publishid, fname);
						String furl2 = fileUtil.getImageFile1bPath(publishid, fname);
						
						File file3 = new File(furl);
						if(!file3.exists())
						{
							filemap.put("fileurl", fileurl);
						}
						else
						{
							Bitmap bitmap2 = getLoacalBitmap(furl);
							
							filewidth = bitmap2.getWidth();
		        			fileheight = bitmap2.getHeight();
							
							filemap.put("fileurl", furl2);
						}
						
						imgsize++;
						filemap.put("filetype", filetype);
						filemap.put("momentsid", pkid);
						filemap.put("filewidth", String.valueOf(filewidth));
						filemap.put("fileheight", String.valueOf(fileheight));
						files.add(filemap);
					}else{
						if (!fileUtil.isFileExist2(publishid)) {
							fileUtil.createUserFile(publishid);
						}
						String filepath = fileUtil.getImageFile1aPath(publishid,filename);
						File file = new File(filepath);
						if(!file.exists())
						{
							fileUtil.downFile(fileurl,filepath);
						}
						
						String imgpath = "";
						if(filetype.equals("2"))//如果是视频的话获取缩略图保存
						{
							String [] strs = filename.split("\\.");
							String name = strs[0]+"img";
							imgpath = fileUtil.getImageFile1aPath(publishid,name);
							File file2 = new File(imgpath);
							if(!file2.exists())
							{
								Bitmap b = BitmapUtil.getVideoThumbnail(MomentsActivity.instance.getContentResolver(), filepath);
					        	b = adaptive(b,100,100);
					        	if(!fileUtil.isFileExist2(publishid))
									fileUtil.createUserFile(publishid);
					        	fileUtil.saveMyBitmap(imgpath, b);
							}
						}
						filemap.put("voidimg", imgpath);
						filemap.put("fileurl", filepath);
						
						filemap.put("filetype", filetype);
						filemap.put("momentsid", pkid);
						filemap.put("filewidth", String.valueOf(filewidth));
						filemap.put("fileheight", String.valueOf(fileheight));
						files2.add(filemap);
					}
        		}
			}
			map.put("filelist", files);
			map.put("filelist2", files2);
			
			//评论
			//"commentlist":[{"pkid":"Msud4c86a880hlhv07iz","commentUsername":"连锐","cmmentDesc":"呵呵"}]
			JSONArray commentlist = null;
			List<Map<String,String>> comments = new ArrayList<Map<String,String>>();
			if(job.has("commentlist")){
				commentlist = (JSONArray) job.get("commentlist");
				for(int n=0;n<commentlist.length();n++){
					JSONObject commentJsonObj = commentlist.getJSONObject(n);
					String commentPkid = commentJsonObj.getString("pkid");
					String discusDesc = commentJsonObj.getString("cmmentDesc");
					String userName = commentJsonObj.getString("commentUsername");
					Map<String,String> commentmap = new HashMap<String,String>();
					commentmap.put("pkid", commentPkid);
					commentmap.put("userName", userName);
					commentmap.put("discusDesc", discusDesc);
					
					comments.add(commentmap);
				}
			}
			map.put("commentlist", comments);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
	public List<Map<String,Object>> getFrindListData(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String username = ""; 
				if(dobj.has("username"))
					username = (String) dobj.get("username"); 
				
				String pfid = ""; 
				if(dobj.has("pfid"))
					pfid = (String) dobj.get("pfid");
				
				String imgurl = ""; // 
				if(dobj.has("imgurl"))
					imgurl = (String) dobj.get("imgurl"); 
				
				String account = ""; // 
				if(dobj.has("account"))
					account = (String) dobj.get("account"); 
				
				String sex = ""; // 
				if(dobj.has("sex"))
					sex = (String) dobj.get("sex"); 
				
				String area = ""; // 
				if(dobj.has("area"))
					area = (String) dobj.get("area"); 
				
				String signature = ""; // 
				if(dobj.has("signature"))
					signature = (String) dobj.get("signature"); 
				
				String companyid = ""; // 
				if(dobj.has("companyid"))
					companyid = (String) dobj.get("companyid"); 
				
				String storeids = ""; // 
				if(dobj.has("storeids"))
					storeids = (String) dobj.get("storeids"); 
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("username", username);
				map.put("pfid", pfid);
//				if(imgurl != null && !imgurl.equals(""))
//				{
//					String furl = fileUtil.getImageFile2aPath(pfid, pfid);
//					Bitmap bmpsimg = returnUserImgBitMap(imgurl);
//					if(bmpsimg != null)
//					{
//						if(!fileUtil.isFileExist2(pfid))
//							fileUtil.createUserFile(pfid);
//						saveMyBitmap(furl, bmpsimg);
//						imgurl = furl;
//					}
//				}
				map.put("imgurl", imgurl);
				map.put("account", account);
				map.put("sex", sex);
				map.put("area", area);
				map.put("signature", signature);
				map.put("companyid", companyid);
				map.put("storeids", storeids);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> geGroupInfoListData(JSONArray jArr){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject dobjs = (JSONObject) jArr.get(i);
				Log.i("dobjString====", dobjs.toString());
				String jsonstr = dobjs.toString();
				JSONObject dobj = new JSONObject(jsonstr);
				
				String groupName = ""; 
				if(dobj.has("groupName"))
					groupName = (String) dobj.get("groupName"); 
				
				String groupids = ""; 
				if(dobj.has("groupids"))
					groupids = (String) dobj.get("groupids");
				
				String createid = ""; // 
				if(dobj.has("createid"))
					createid = (String) dobj.get("createid"); 
				
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("groupName", groupName);
				map.put("groupids", groupids);
				map.put("createid", createid);
				list.add(map);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public static List<Map<String,Object>> getStroeList(JSONArray jarry,Map<String,Object> storekeylist)
	{
		List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
		try{
			for(int i=0;i<jarry.length();i++)
			{
				JSONObject dobj = jarry.getJSONObject(i);
				String pfid = ""; 
				if(dobj.has("pfid"))
					pfid = (String) dobj.get("pfid"); 
				
				String pid = ""; 
					if(dobj.has("pid"))
						pid = (String) dobj.get("pid");
					
				String bid = ""; 
					if(dobj.has("bid"))
						bid = (String) dobj.get("bid");
					
				String storeTyle = ""; 
					if(dobj.has("storeTyle"))
						storeTyle = (String) dobj.get("storeTyle");
					
				String servicetype = ""; 
					if(dobj.has("servicetype"))
						servicetype = (String) dobj.get("servicetype");
					
				String province = ""; 
					if(dobj.has("province"))
						province = (String) dobj.get("province");
					
				String img = ""; 
					if(dobj.has("img"))
						img = (String) dobj.get("img");
					
				String storeName = ""; 
					if(dobj.has("storeName"))
						storeName = (String) dobj.get("storeName");
				
				if(!storekeylist.containsKey(pid))
				{
					Map<String, Object> dmap = new HashMap<String, Object>();
					dmap.put("pfid", pfid);
					dmap.put("pid", pid);
					dmap.put("bid", bid);
					dmap.put("storeTyle", storeTyle);
					dmap.put("servicetype", servicetype);
					dmap.put("province", province);
					dmap.put("imgurl", img);
					dmap.put("storeName", storeName);
					slist.add(dmap);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return slist;
	}
	
	//等比例缩放
	public Bitmap adaptive(Bitmap bgimage, int newWidth, int newHeight) {
		if (newHeight > 960) {
			newWidth = 640;
			newHeight = 960;
		}

		// 获取这个图片的宽和高
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		if (bgimage.getWidth() > bgimage.getHeight())
			matrix.postScale(scaleWidth, scaleWidth);
		else
			matrix.postScale(scaleHeight, scaleHeight);
		int x = 0;
		int y = 0;
		bgimage = Bitmap.createBitmap(bgimage, x, y, width, height, matrix,
				true);
		return compressImage(bgimage, 50);
	}
	
	//等比例缩放
	public Bitmap adaptive(Bitmap bitmap) {
		int newWidth = 150;
		int newHeight = 150;
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		if (bitmap.getWidth() > bitmap.getHeight())
			matrix.postScale(scaleHeight, scaleHeight);
		else
			matrix.postScale(scaleWidth, scaleWidth);
		int x = 0;
		int y = 0;
		bitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
		return compressImage(bitmap, 10);
	}
	
	private Bitmap compressImage(Bitmap image, int options) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		// int options = 100;
		// while ( baos.toByteArray().length / 1024>100) {
		// //循环判断如果压缩后图片是否大于100kb,大于继续压缩
		// baos.reset();//重置baos即清空baos
		// image.compress(Bitmap.CompressFormat.JPEG, options,
		// baos);//这里压缩options%，把压缩后的数据存放到baos中
		// options -= 10;//每次都减少10
		// }
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	public void saveMyBitmap(String bitName, Bitmap mBitmap,String iconfileUrl,Bitmap iconbitmap) {
		File f = new File(bitName);
		File f2 = new File(iconfileUrl);
		try {
			f.createNewFile();
			f2.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		FileOutputStream fOut = null;
		FileOutputStream fOut2 = null;
		try {
			fOut = new FileOutputStream(f);
			fOut2 = new FileOutputStream(f2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//		iconbitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut2);
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
		iconbitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut2);
		try {
			fOut.flush();
			fOut2.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
			fOut2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(bitName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
		
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
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
			if(imageUrl != null)
			{
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
	
				BitmapFactory.Options opt = new BitmapFactory.Options();  
			    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
			    opt.inPurgeable = true;  
			    opt.inInputShareable = true;  
	//		    opt.inSampleSize = 2;
			    
				bitmap = BitmapFactory.decodeStream(is);
			}
//			bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	
	public Bitmap getGossipImage(String httpUrl){      
        Bitmap bitmap = null;            
        HttpGet httpRequest = new HttpGet(httpUrl); 
//        httpRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"); //认证token  
//        httpRequest.addHeader("Accept-Encoding", "gzip, deflate");  
//        httpRequest.addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");  
//        httpRequest.addHeader("Connection", "keep-alive");  
        httpRequest.addHeader("Host", "121.199.8.186");  
        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");  
        //取得HttpClient 对象    
        HttpClient httpclient = new DefaultHttpClient();    
        try {    
            //请求httpClient ，取得HttpRestponse    
            HttpResponse httpResponse = httpclient.execute(httpRequest);    
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){    
                //取得相关信息 取得HttpEntiy    
                HttpEntity httpEntity = httpResponse.getEntity();    
                InputStream is = httpEntity.getContent();
                bitmap = BitmapFactory.decodeStream(is);    
                is.close();     
            }      
        } catch (ClientProtocolException e) {    
            e.printStackTrace();    
        } catch (IOException e) {     
            e.printStackTrace();    
        }      
        return bitmap;  
    }  
	
	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public Bitmap getLoacalBitmap2(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);  
	        if(file.exists())  
	        {  
//	        	BitmapFactory.Options opts = new BitmapFactory.Options();
//				
//				opts.inSampleSize = 1;
//				opts.inPreferredConfig = Bitmap.Config.RGB_565;
//				opts.inPurgeable = true;
//				opts.inInputShareable = true;
				
	            bitmap = BitmapFactory.decodeFile(pathString);  
	        }  
			
			
			

//			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, opts);
			// bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
			// dstHeight,true);
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 转换本地图片为bitmap http://bbs.3gstdy.com
	 * 
	 * @param url
	 * @return
	 */
	public synchronized Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;

			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, opts);
			// bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
			// dstHeight,true);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized Bitmap getLoacalBitmap(String url,int dstWidth,int dstHeight,boolean isdenbili) {
		Bitmap bitmap = null;
		try {
			FileInputStream fis = new FileInputStream(url);
			if(fis.available() > 0)
			{
				BitmapFactory.Options opts = new BitmapFactory.Options();
				
				opts.inSampleSize = 1;
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
	
				bitmap = BitmapFactory.decodeStream(fis, null, opts);
				if(bitmap != null)
					bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,dstHeight,isdenbili);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	public Bitmap getLoacalBitmap2(String url,int dstWidth,int dstHeight,boolean isdenbili) {
		Bitmap bitmap = null;
		try {
			FileInputStream fis = new FileInputStream(url);
			if(fis.available() > 0)
			{
				int size = fis.available() / 1000;//等于K
				BitmapFactory.Options opts = new BitmapFactory.Options();
				
				if(size <= 200 )
					opts.inSampleSize = 1;
				else if(size <= 500 )
					opts.inSampleSize = 2;
				else if(size <= 700 )
					opts.inSampleSize = 3;
				else if(size <= 900 )
					opts.inSampleSize = 4;
				else
					opts.inSampleSize = 5;
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
	
				bitmap = BitmapFactory.decodeStream(fis, null, opts);
				if(bitmap != null)
					bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,dstHeight,isdenbili);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	public synchronized static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			System.out.println("ssssssssurl===="+url);
			if(url == null || url.equals(""))
				return null;
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
//			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
//			bitmap = Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	public synchronized static Bitmap returnUserImgBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			System.out.println("ssssssssurl===="+url);
			if(url == null || url.equals(""))
				return null;
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			
//			opts.inSampleSize = 2;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			
			bitmap = BitmapFactory.decodeStream(is,null,opts);
			if(bitmap != null)
				bitmap = Bitmap.createScaledBitmap(bitmap,55,55,true);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	public static String getCombID() {
		UUID temp = UUID.randomUUID();
		String s = temp.toString();
		long date = new Date().getTime();

		return s.substring(7, 13) + s.substring(14, 18) + s.substring(19, 23)
				+ Long.toString(date, 36);

	}
	
	public static String getFileInfoString(InputStream in) {
		InputStreamReader iReader = null;
		StringBuffer sb = new StringBuffer("");
		try {
			iReader = new InputStreamReader(in, "utf-8");
			BufferedReader reader = new BufferedReader(iReader);

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getValues(String str)
	{
		String s = "";
		try{
			if(str != null)
				s = str;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return s;
	}
	
	public boolean isInetnState() {
		boolean inetnState = true;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			inetnState = false;
		} else {
			inetnState = true;
		}
		return inetnState;
	}
	
	public boolean isWifistart() {
		boolean wifistart = false;
		ContentResolver cv = getContentResolver();
		String tmpS = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.BLUETOOTH_ON);
		tmpS = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.WIFI_ON);
		if (tmpS.equals("1")) {
			wifistart = true;
		} else {
			wifistart = false;
		}
		
		return wifistart;
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
	
	public String getInterval(String createtime) { //传入的时间格式必须类似于2012-8-21 17:53:20这样的格式  
        String interval = null;  
          
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        ParsePosition pos = new ParsePosition(0);  
        Date d1 = (Date) sd.parse(createtime, pos);  
          
        //用现在距离1970年的时间间隔new Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔  
        long time = new Date().getTime() - d1.getTime();// 得出的时间间隔是毫秒  
          
        if(time/1000 < 10 && time/1000 >= 0) {  
        //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒  
            interval ="刚刚";  
              
        } else if(time/1000 < 60 && time/1000 > 0) {  
        //如果时间间隔小于60秒则显示多少秒前  
            int se = (int) ((time%60000)/1000);  
            interval = se + "秒前";  
              
        } else if(time/60000 < 60 && time/60000 > 0) {  
        //如果时间间隔小于60分钟则显示多少分钟前  
            int m = (int) ((time%3600000)/60000);//得出的时间间隔的单位是分钟  
            interval = m + "分钟前";  
              
        } else if(time/3600000 < 24 && time/3600000 >= 0) {  
        //如果时间间隔小于24小时则显示多少小时前  
            int h = (int) (time/3600000);//得出的时间间隔的单位是小时  
            interval = h + "小时前";  
              
        } else {
        	
            //大于24小时，则显示正常的时间，但是不显示秒  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
  
            ParsePosition pos2 = new ParsePosition(0);  
            Date d2 = (Date) sdf.parse(createtime, pos2);  
            
            long days =  getDayShu(new Date(),d2);
            String str = "";
            if(days == 1)
            	str = "昨天";
            else if(days == 2)
            	str = "前天";
            else
            	str = days + "天前";
            
            interval = str + " " +sdf2.format(d2);
        }  
        return interval;  
    }  
	
	
	public static long getDayShu(Date date1, Date date2){
		// TODO 自动生成方法存根
		// 日期相减算出秒的算法
		// Date date1 = new SimpleDateFormat("yyyy-mm-dd").parse("2006-06-08");
		// Date date2 = new SimpleDateFormat("yyyy-mm-dd").parse("2006-06-12");
		long day = 0;
		try{
			long l = date1.getTime() - date2.getTime() > 0 ? date1.getTime()
					- date2.getTime() : date2.getTime() - date1.getTime();
	
			System.out.println(l / 1000 + "秒");
	
			// 日期相减得到相差的日期
			day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000) > 0 ? (date1
					.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000)
					: (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
	
			System.out.println("相差的日期: " + day);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return day;
	}
}
