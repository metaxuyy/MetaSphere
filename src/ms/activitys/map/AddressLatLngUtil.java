package ms.activitys.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.util.HttpURLConnection;

public class AddressLatLngUtil {

	private static final String REQUEST_ENCODE = "UTF-8";
	private static final String ADDRESS_KEY = "/address/";
	private static final String ADDRESS_COUNTRY_KEY = "/CountryName/";
	private static final String ADDRESS_REGION_KEY = "/AdministrativeAreaName/";
	private static final String ADDRESS_CITY_KEY = "/LocalityName/";
	private static final String ADDRESS_POINT_KEY = "/coordinates/";
	private static final String ADDRESS_SPLIT_STR = ":";

	private static final String LOCATION_COUNTRY_KEY = "/country/";
	private static final String LOCATION_REGION_KEY = "/region/";
	private static final String LOCATION_CITY_KEY = "/city/";
	// private static final String LOCATION_POINT_KEY = "/"location/"";
	private static final String LOCATION_POINT_LATITUDE_KEY = "/latitude/";
	private static final String LOCATION_POINT_LONGITUDE_KEY = "/longitude/";
	private static final String LOCATION_POINT_PRECISIONY_KEY = "/accuracy/";
	private static final String LOCATION_SPLIT_STR = ",";

	public static final int MIN_ZOOM = 11;
	public static final int MAX_ZOOM = 18;

	private static final int DOWNNUM_FOR_PROXY = 8000;
	private static final int CONNECT_TIMEOUT = 30000; // 30秒
	private static final int THREAD_SLEEP_MILLIS = 200;
	private static boolean flag = false;
	private static int downNum = 0;

	private static String proxyHost = "165.228.128.10";
	private static String proxyPort = "3128";
	private static String proxySet = "true";

	/**
	 * 获取地址详细信息及经纬度信息
	 * 
	 * @param address
	 * @return Map<地址, 经纬度>
	 */
	public static Map<String, Point> getAddressLatLng(String address) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://ditu.google.cn/maps/geo");
		urlBuilder.append("?output=json");
		urlBuilder.append("&oe=utf-8");
		urlBuilder.append("&q=").append(encodeURLForUTF8(address));
		urlBuilder
				.append("&key=ABQIAAAAzr2EBOXUKnm_jVnk0OJI7xSosDVG8KKPE1-m51RBrvYughuyMxQ-i1QfUnH94QxWIa6N4U6MouMmBA");
		urlBuilder.append("&mapclient=jsapi");
		urlBuilder.append("&hl=zh-CN");
		urlBuilder.append("&callback=_xdc_._1g4gm5mh3");
		// logger.info(urlBuilder.toString());

		HttpURLConnection httpConnection = null;
		try {
			// 1、构造HttpURLConnection连接
			URL url = new URL(urlBuilder.toString());
			URLConnection urlConnection = url.openConnection();
			httpConnection = (HttpURLConnection) urlConnection;
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(CONNECT_TIMEOUT);
			httpConnection.connect();

			// 2、接收响应结果
			InputStream inStream = httpConnection.getInputStream();
			String htmlContent = getContentByStream(inStream, REQUEST_ENCODE);
			// 关闭流资源
			inStream.close();

			// 3、解析结果
			Map<String, Point> map = parseAddressLatLng(htmlContent);
			updateProxy();

			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logger.error("===========获取经纬度信息异常！请求地址：" + address, e);
			return java.util.Collections.emptyMap();
		} finally {
			// 关闭连接
			if (null != httpConnection) {
				httpConnection.disconnect();
			}
		}
	}

	/**
	 * 根据地址信息获取经纬度
	 * 
	 * @param addressList
	 *            地址集合
	 * @return List<Map<地址, 经纬度对象>>
	 */
	public static List<Map<String, Point>> getAddressLatLng(
			List<String> addressList) {
		List<Map<String, Point>> list = new ArrayList<Map<String, Point>>();
		if (null == addressList || addressList.isEmpty()) {
			return list;
		}

		for (String address : addressList) {
			try {
				// 休息一下
				Thread.sleep(THREAD_SLEEP_MILLIS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Map<String, Point> addrMap = getAddressLatLng(address);
			if (false == addrMap.isEmpty()) {
				list.add(addrMap);
			} else {
				// System.out.println(address + " point is null!!!");
				// logger.warn("===========获取经纬度信息为空！请求地址：" + address);
			}
		}
		return list;
	}

	/**
	 * 解析网页内容地址信息及经纬度信息
	 * 
	 * @param htmlContent
	 * @return Map<地址, 经纬度>
	 */
	private static Map<String, Point> parseAddressLatLng(String htmlContent) {
		Map<String, Point> addr = new HashMap<String, Point>(1);
		if (isNullOrEmpty(htmlContent)) {
			return addr;
		}

		String[] contents = htmlContent.split("/r/n");
		String[] ss = null;
		String address = null;
		String country = null;
		String region = null;
		String city = null;
		Point point = null;
		for (String line : contents) {
			if (isNullOrEmpty(line)) {
				continue;
			}
			line = line.trim();
			if (line.contains(ADDRESS_POINT_KEY)) {
				/*
				 * "coordinates": [ 113.9465830, 22.5309650, 0 ]
				 */
				ss = line.split(ADDRESS_SPLIT_STR);
				if (null != ss && ss.length > 1) {
					String pointStr = getMiddleStr(ss[1], "[", "]");
					String[] pss = pointStr.split(",");
					if (null != pss && pss.length > 1) {
						double defaultValue = 0D;
						point = new Point(isNullOrEmpty(pss[0]) ? defaultValue
								: Double.parseDouble(pss[0].trim()),
								isNullOrEmpty(pss[1]) ? defaultValue : Double
										.parseDouble(pss[1].trim()));
					}
				}
			} else if (line.contains(ADDRESS_KEY)) {
				address = getValue(line, ADDRESS_KEY);
			} else if (line.contains(ADDRESS_COUNTRY_KEY)) {
				country = getValue(line, ADDRESS_COUNTRY_KEY);
			} else if (line.contains(ADDRESS_REGION_KEY)) {
				region = getValue(line, ADDRESS_REGION_KEY);
			} else if (line.contains(ADDRESS_CITY_KEY)) {
				city = getValue(line, ADDRESS_CITY_KEY);
			}

			// 默认取第一个地址信息
			if (false == isNullOrEmpty(address) && null != point) {
				point.setCountry(country);
				point.setRegion(region);
				point.setCity(city);
				break;
			}
		} // end-for-contents

		// 如果地址不为空
		if (false == isNullOrEmpty(address)) {
			addr.put(address, point);
		}
		return addr;
	}

	/**
	 * 获取中间字符串内容
	 * 
	 * @param content
	 * @param beginStr
	 * @param endStr
	 * @return
	 */
	private static String getMiddleStr(String content, String beginStr,
			String endStr) {
		String str = "";
		if (isNullOrEmpty(content)) {
			return str;
		}

		content = content.trim();
		int bIndex = content.indexOf(beginStr);
		int eIndex = -1;
		if (null != beginStr && beginStr.equals(endStr)) {
			int index = content.substring(bIndex + beginStr.length()).indexOf(
					endStr);
			eIndex = content.substring(0, bIndex + beginStr.length()).length()
					+ index;
		} else if (null != endStr && false == endStr.equals(beginStr)) {
			eIndex = content.indexOf(endStr);
		}

		if (-1 != bIndex && -1 != eIndex) {
			str = content.substring(bIndex + beginStr.length(), eIndex);
		}
		return str;
	}

	private static final String PROXY_HOST_KEY = "http.proxyHost";
	private static final String PROXY_PORT_KEY = "http.proxyPort";
	private static final String PROXY_SET_KEY = "http.proxySet";

	/**
	 * 切换代理
	 */
	private static synchronized void updateProxy() {
		downNum++;
		if (downNum % DOWNNUM_FOR_PROXY == 0) {
			if (flag) {
				clearProxy();
				flag = false;
			} else {
				setProxy();
				flag = true;
			}
		}
	}

	private static void setProxy() {
		System.setProperty(PROXY_HOST_KEY, proxyHost);
		System.setProperty(PROXY_PORT_KEY, proxyPort);
		System.setProperty(PROXY_SET_KEY, proxySet);

		// logger.info("setProxy====="+proxyHost+":"+proxyPort);
		// System.out.println("setProxy====="+proxyHost+":"+proxyPort);
	}

	private static void clearProxy() {
		System.clearProperty(PROXY_HOST_KEY);
		System.clearProperty(PROXY_PORT_KEY);
		System.clearProperty(PROXY_SET_KEY);

		// logger.info("clearProxy=====");
		// System.out.println("clearProxy=====");
	}

	private static String encodeURLForUTF8(String str) {
		try {
			str = java.net.URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logger.error("字符串转码异常，字符串："+ str, e);
		}
		return str;
	}

	/**
	 * 按照指定编码从流中读取信息
	 * 
	 * @param inStream
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	private static String getContentByStream(InputStream inStream, String encode)
			throws IOException {
		if (null == inStream) {
			return null;
		}

		StringBuilder content = new StringBuilder();
		// 采用指定编码格式读取流内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream, encode));
		String message = null;
		while (null != (message = reader.readLine())) {
			content.append(message);
			content.append("/r/n");
		}
		// 关闭读取器，释放资源
		reader.close();
		return (content.toString());
	}

	/**
	 * 获取偏移后的经纬度（Google中国地图偏移接口） 中国地图和卫星图都存在偏移量，这个是由中国规划局确定的，
	 * google的地图服务，以ditu.gogle开头的都没有偏差，以maps.google开头的服务就有偏差
	 * 
	 * @param zoom
	 *            偏移级别（从11级到18级，18级最精确）
	 * @param sourcePoint
	 *            经纬度对象
	 * @return
	 */
	public static Point getOffsetLatLng(int zoom, Point sourcePoint) {
		if (null == sourcePoint) {
			return null;
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://ditu.google.cn/maps/vp");
		urlBuilder.append("?spn=0.0,0.0");
		urlBuilder.append("&z=").append(zoom);
		urlBuilder.append("&vp=");
		urlBuilder.append(sourcePoint.getLatitude());// 纬度
		urlBuilder.append(",");
		urlBuilder.append(sourcePoint.getLongitude());// 经度

		HttpURLConnection httpConnection = null;
		try {
			// 1、构造HttpURLConnection连接
			URL url = new URL(urlBuilder.toString());
			URLConnection urlConnection = url.openConnection();
			httpConnection = (HttpURLConnection) urlConnection;
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(CONNECT_TIMEOUT);
			httpConnection.connect();

			// 2、接收响应结果
			InputStream inStream = httpConnection.getInputStream();
			String htmlContent = getContentByStream(inStream, REQUEST_ENCODE);
			// 关闭流资源
			inStream.close();

			// 3、解析结果
			String offset = parseOffsetFromZoom(zoom, htmlContent);
			// 如果没有偏移值，则返回原经纬度对象
			if (isNullOrEmpty(offset)) {
				return sourcePoint;
			}
			Point targetPoint = getOffsetPoint(offset, zoom, sourcePoint);
			updateProxy();

			// logger.info("sourcePoint: "+ sourcePoint);
			// logger.info("targetPoint: "+ targetPoint);

			return targetPoint;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logger.error("===========获取偏移经纬度信息异常！zoom = " + zoom +
			// ", sourcePoint = " + sourcePoint, e);
		} finally {
			// 关闭连接
			if (null != httpConnection) {
				httpConnection.disconnect();
			}
		}
		return null;
	}

	/**
	 * 获取对应级别像素偏移量
	 * 
	 * @param zoom
	 * @param htmlContent
	 * @return
	 */
	private static String parseOffsetFromZoom(int zoom, String htmlContent) {
		String offset = null;
		if (isNullOrEmpty(htmlContent) || zoom > MAX_ZOOM || zoom < MIN_ZOOM) {
			return offset;
		}

		/*
		 * 下面分别表示经纬度、级别、偏移像素数量【级别从11级到18级，共8组数字】
		 * 前一组数字精确的等于后一组数字除二，我们为了得到最精确的偏移，故选择第18级的偏移量1193，-270，
		 * 1193为x方向上精度的偏移像素，-270为y方向上维度偏移像素
		 * 
		 * window.GTileShiftUpdateOffset && window.GTileShiftUpdateOffset(
		 * 39.111195, 117.148067, 18, [9, -2, 18, -4, 37, -8, 74, -16, 149, -33,
		 * 298, -67, 596, -135, 1193, -270]);
		 */
		int beginIndex = htmlContent.lastIndexOf("[");
		int endIndex = htmlContent.lastIndexOf("]");
		if (beginIndex > 0 && endIndex > 0) {
			// 获取各级别像素偏移量内容
			String content = htmlContent.substring(beginIndex + 1, endIndex);
			offset = getOffsetByZoom(zoom, content);
		}
		return offset;
	}

	/**
	 * 获取zoom级别的像素偏移量
	 * 
	 * @param zoom
	 * @param content
	 * @return
	 */
	private static String getOffsetByZoom(int zoom, String content) {
		String[] ss = content.split(",");
		int index = ((zoom - 10) << 1) - 2;
		if (null == ss || ss.length < (index + 1)) {
			return null;
		}
		return (ss[index].trim() + "," + ss[index + 1].trim());
	}

	/**
	 * 获取校正后的经纬度
	 * 
	 * @param offset
	 * @param zoom
	 * @param point
	 * @return
	 */
	private static Point getOffsetPoint(String offset, int zoom, Point point) {
		String[] ss = offset.split(",");
		int offsetX = Integer.parseInt(ss[0]);
		int offsetY = Integer.parseInt(ss[1]);

		double lngPixel = (Math.round(lngToPixel(point.getLongitude(), zoom)) - offsetX);
		double latPixel = (Math.round(latToPixel(point.getLatitude(), zoom)) - offsetY);
		return new Point(pixelToLng(lngPixel, zoom), pixelToLat(latPixel, zoom));
	}

	/*
	 * sinLatitude = sin(latitude * pi/180)
	 * 
	 * pixelX = ((longitude + 180) / 360) * 256 * 2level
	 * 
	 * pixelY = (0.5 – log((1 + sinLatitude) / (1 – sinLatitude)) / (4 * pi)) *
	 * 256 * 2level
	 */

	/**
	 * 经度到像素X值
	 * 
	 * @param lng
	 * @param zoom
	 * @return
	 */
	private static double lngToPixel(double lng, int zoom) {
		return (lng + 180) * (256L << zoom) / 360;
	}

	/**
	 * 纬度到像素Y
	 * 
	 * @param lat
	 * @param zoom
	 * @return
	 */
	private static double latToPixel(double lat, int zoom) {
		double siny = Math.sin(lat * Math.PI / 180);
		double y = Math.log((1 + siny) / (1 - siny));
		return (256L << zoom) * (0.5 - y / (4 * Math.PI));
	}

	/**
	 * 像素X到经度
	 * 
	 * @param pixelX
	 * @param zoom
	 * @return
	 */
	private static double pixelToLng(double pixelX, int zoom) {
		return pixelX * 360 / (256L << zoom) - 180;
	}

	/**
	 * 像素Y到纬度
	 * 
	 * @param pixelY
	 * @param zoom
	 * @return
	 */
	private static double pixelToLat(double pixelY, int zoom) {
		double y = 4 * Math.PI * (0.5 - pixelY / (256L << zoom));
		double z = Math.pow(Math.E, y);
		double siny = (z - 1) / (z + 1);
		return Math.asin(siny) * 180 / Math.PI;
	}

	/**
	 * 根据地区编码（LAC）和基站编号（CID）获取经纬度信息
	 * 
	 * @param lac
	 *            地区编码
	 * @param cellId
	 *            基站编号
	 * @deprecated 改为调用 getLocationByLacAndCid方法
	 * @return
	 */
	public static Point getLatLngByLacAndCid(int lac, int cellId) {
		String urlString = "http://www.google.com/glm/mmap";

		HttpURLConnection httpConn = null;
		try {
			// ---open a connection to Google Maps API---
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			httpConn = (HttpURLConnection) conn;
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setDefaultUseCaches(false);
			httpConn.setRequestMethod("POST");
			httpConn.setConnectTimeout(CONNECT_TIMEOUT);
			httpConn.connect();

			// ---write some custom data to Google Maps API---
			OutputStream outputStream = httpConn.getOutputStream();
			writeData(outputStream, cellId, lac);
			outputStream.close();

			// ---get the response---
			DataInputStream dataInputStream = new DataInputStream(
					httpConn.getInputStream());

			// ---interpret the response obtained---
			dataInputStream.readShort();
			dataInputStream.readByte();
			int code = dataInputStream.readInt();
			Point point = null;
			if (code == 0) {
				double lat = (double) dataInputStream.readInt() / 1000000D;
				double lng = (double) dataInputStream.readInt() / 1000000D;
				int i = dataInputStream.readInt();
				int j = dataInputStream.readInt();
				String s = dataInputStream.readUTF();
				// 关闭流
				dataInputStream.close();

				// 包装结果
				point = new Point(lng, lat);
				point.setPrecision(i); // 精确度

				// ---display Google Maps---
				// logger.info("lac = "+lac+", cellId = "+cellId+", Latitude = "+lat+", Longitude = "+lng
				// + ", precision = "+i+", j = "+j+", s = "+s);
				// System.out.println("Latitude = " + lat + ", Longitude = " +
				// lng
				// + ", precision = " + i + ", j = " + j + ", s = " + s);
			} else {
				// logger.warn("===========根据地区编码和基站编号获取经纬度信息失败！lac = "+ lac +
				// ", cellId = " + cellId);
			}

			return point;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logger.error("===========根据地区编码和基站编号获取经纬度信息异常！lac = "+ lac +
			// ", cellId = " + cellId, e);
		} finally {
			// 关闭连接
			if (null != httpConn) {
				httpConn.disconnect();
			}
		}
		return null;
	}

	private static void writeData(OutputStream out, int cellId, int lac)
			throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(out);
		dataOutputStream.writeShort(21);
		dataOutputStream.writeLong(0);
		dataOutputStream.writeUTF("en");
		dataOutputStream.writeUTF("Android");
		dataOutputStream.writeUTF("1.0");
		dataOutputStream.writeUTF("Web");
		dataOutputStream.writeByte(27);
		dataOutputStream.writeInt(0);
		dataOutputStream.writeInt(0);
		if (cellId >= 65536) {
			// 联通3G
			dataOutputStream.writeInt(5);
		} else {
			// 移动3G
			dataOutputStream.writeInt(3);
		}
		dataOutputStream.writeUTF("");

		dataOutputStream.writeInt(cellId);
		dataOutputStream.writeInt(lac);

		dataOutputStream.writeInt(0); // mnc
		dataOutputStream.writeInt(0); // mcc
		dataOutputStream.writeInt(0);
		dataOutputStream.writeInt(0);
		dataOutputStream.flush();
		dataOutputStream.close();
	}

	/**
	 * 根据地区编码（LAC）和基站编号（CID）获取地区信息
	 * 
	 * @param lac
	 *            地区编码
	 * @param cellId
	 *            基站编号
	 * @return
	 */
	public static Point getLocationByLacAndCid(int lac, int cellId) {
		String urlString = "http://www.google.cn/loc/json";

		HttpURLConnection httpConn = null;
		try {
			// ---open a connection to Google Maps API---
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			httpConn = (HttpURLConnection) conn;
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setDefaultUseCaches(false);
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setConnectTimeout(CONNECT_TIMEOUT);
			httpConn.connect();

			// ---write some custom data to Google Maps API---
			OutputStreamWriter outputStream = new OutputStreamWriter(
					httpConn.getOutputStream());
			outputStream.write(getLocationRequest(lac, cellId));
			outputStream.flush();
			outputStream.close();

			// ---get the response---
			String responseContent = getContentByStream(
					httpConn.getInputStream(), REQUEST_ENCODE);

			// ---interpret the response obtained---
			Point point = parseLocationContent(responseContent);

			// logger.info(responseContent);
			return point;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// logger.error("===========根据地区编码和基站编号获取地区信息异常！lac = "+ lac +
			// ", cellId = " + cellId, e);
		} finally {
			// 关闭连接
			if (null != httpConn) {
				httpConn.disconnect();
			}
		}
		return null;
	}

	private static String getLocationRequest(int lac, int cellId) {
		StringBuilder requestContent = new StringBuilder();
		requestContent.append("{ ");
		requestContent.append("  /version/ : /1.1.0/, ");
		requestContent.append("  /host/ : /ditu.google.cn/, ");
		requestContent
				.append("  /access_token/ : /2:k7j3G6LaL6u_lafw:4iXOeOpTh1glSXe/, ");
		requestContent.append("  /request_address/ : true,");
		requestContent.append("  /cell_towers/ : ");
		requestContent.append("    [ ");
		requestContent.append("      { ");
		requestContent.append("        /cell_id/ : ").append(cellId)
				.append(", ");
		requestContent.append("        /location_area_code/ : ").append(lac)
				.append(", ");
		requestContent.append("        /mobile_country_code/ : 460, ");
		requestContent.append("        /mobile_network_code/ : 00, ");
		requestContent.append("        /age/ : 0, ");
		requestContent.append("        /signal_strength/ : -60, ");
		requestContent.append("        /timing_advance/ : 5555 ");
		requestContent.append("      } ");
		requestContent.append("    ] ");
		requestContent.append("} ");
		return (requestContent.toString());
	}

	/**
	 * 解析地区信息，并构造Point对象
	 * 
	 * @param respContent
	 * @return
	 */
	private static Point parseLocationContent(String respContent) {
		if (isNullOrEmpty(respContent)) {
			return null;
		}
		/*
		 * {"location":{"latitude":31.148662,"longitude":114.957975,
		 * "address":{"country"
		 * :"中国","country_code":"CN","region":"湖北省","city":"黄冈市"},
		 * "accuracy":1625.0},
		 * "access_token":"2:i1-PwttBtQsSyYvX:VVq7Nsl89ut7l9aV"}
		 */
		// 获取纬度、经度、精确度、国家、省份、城市信息
		String latitude = getValue(respContent, LOCATION_POINT_LATITUDE_KEY);
		String longitude = getValue(respContent, LOCATION_POINT_LONGITUDE_KEY);
		String precisiony = getValue(respContent, LOCATION_POINT_PRECISIONY_KEY);
		String country = getValue(respContent, LOCATION_COUNTRY_KEY);
		String region = getValue(respContent, LOCATION_REGION_KEY);
		String city = getValue(respContent, LOCATION_CITY_KEY);
		Point point = null;
		if (false == isNullOrEmpty(latitude)
				&& false == isNullOrEmpty(longitude)) {
			point = new Point(Double.parseDouble(longitude),
					Double.parseDouble(latitude));
			if (false == isNullOrEmpty(precisiony)) {
				point.setPrecision(Double.parseDouble(precisiony));
			}
			point.setCountry(country);
			point.setRegion(region);
			point.setCity(city);
		}
		return point;
	}

	private static String getValue(String content, String key) {
		if (false == isNullOrEmpty(content) && false == isNullOrEmpty(key)) {
			String[] ss = content.split("/r/n");
			for (String line : ss) {
				if (isNullOrEmpty(line)) {
					continue;
				}

				line = line.replace("{", "").replace("}", "").replace("[", "")
						.replace("]", "").trim();
				String[] sss = line.split(LOCATION_SPLIT_STR);
				for (String str : sss) {
					if (isNullOrEmpty(str) || false == str.contains(key)) {
						continue;
					}

					String[] sub = str.split(ADDRESS_SPLIT_STR);
					for (int i = 0; i < sub.length; i++) {
						if (isNullOrEmpty(sub[i])) {
							continue;
						}
						sub[i] = sub[i].trim();
					}
					List<String> subList = java.util.Arrays.asList(sub);
					int subIndex = subList.indexOf(key);
					if (-1 != subIndex && subList.size() > (subIndex + 1)) {
						String value = subList.get(subIndex + 1);
						return (value.replace("/", "").trim());
					}
				}// end-for-sss

			}// end-for-ss
		}
		return null;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 经纬度坐标信息
	 */
	public static class Point {
		/**
		 * 经度
		 */
		private double longitude;

		/**
		 * 纬度
		 */
		private double latitude;

		/**
		 * 精确度
		 */
		private double precision;

		/**
		 * 国家
		 */
		private String country;

		/**
		 * 省份
		 */
		private String region;

		/**
		 * 城市
		 */
		private String city;

		/**
		 * 构造函数
		 * 
		 * @param longitude
		 *            经度
		 * @param latitude
		 *            纬度
		 */
		public Point(double longitude, double latitude) {
			this.longitude = longitude;
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getPrecision() {
			return precision;
		}

		public void setPrecision(double precision) {
			this.precision = precision;
		}

		public String toString() {
			StringBuilder content = new StringBuilder();
			content.append(getClass().getName());
			content.append("[ latitude=");
			content.append(this.latitude);
			content.append(", longitude=");
			content.append(this.longitude);
			if (this.precision != 0.0) {
				content.append(", precision=");
				content.append(this.precision);
			}

			if (false == isNullOrEmpty(this.country)) {
				content.append(", country=");
				content.append(this.country);
			}

			if (false == isNullOrEmpty(this.region)) {
				content.append(", region=");
				content.append(this.region);
			}

			if (false == isNullOrEmpty(this.city)) {
				content.append(", city=");
				content.append(this.city);
			}
			content.append(" ]");
			return (content.toString());
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
	}

	public static String getProxyHost() {
		return proxyHost;
	}

	public static void setProxyHost(String proxyHost) {
		AddressLatLngUtil.proxyHost = proxyHost;
	}

	public static String getProxyPort() {
		return proxyPort;
	}

	public static void setProxyPort(String proxyPort) {
		AddressLatLngUtil.proxyPort = proxyPort;
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// System.getProperties().list(System.out);

		// 深圳: (经度)lng:114.0578,(纬度)lat:22.5434
		List<String> addrList = new ArrayList<String>();
		addrList.add("留学生创业大厦");
		// addrList.add("深圳市");
		// addrList.add("世界之窗");
		// addrList.add("地王大厦");
		// addrList.add("黄鹤楼");

		List<Map<String, Point>> list = getAddressLatLng(addrList);
		for (Map<String, Point> map : list) {
			for (Entry<String, Point> entry : map.entrySet()) {
				// logger.info(entry.getKey() + entry.getValue());

				// 获取偏移坐标
				Point offsetPoint = getOffsetLatLng(MAX_ZOOM, entry.getValue());
				// logger.info("偏移经纬度：" + offsetPoint);
				System.out.println();
			}
		}

		int lac = 0x717f;
		int cellId = 0x3341;
		Point point = getLocationByLacAndCid(lac, cellId);
		// logger.info(point);

		point = getOffsetLatLng(MAX_ZOOM, point);
		// logger.info(point);

		System.out.println("finished!");

	}
}
