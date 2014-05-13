package ms.globalclass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class TomorrowWeatherPullParse {
	
	
	// 解析天气预报字符串成一个天气信息对象
	public static List<TomorrowWeatherVO> parse(String googleWeatherString) {

		Log.i("yao", "TomorrowWeatherPullParse.parse");

		// 记录出现次数
		int findCount = 0;
		
		List<TomorrowWeatherVO> tvolists = new ArrayList<TomorrowWeatherVO>();

		// 明日天气预报Bean
		TomorrowWeatherVO tomorrowWeatherVO = new TomorrowWeatherVO();

		try {

			//定义工厂 XmlPullParserFactory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			//定义解析器 XmlPullParser
			XmlPullParser parser = factory.newPullParser();

			//获取xml输入数据
			parser.setInput(new StringReader(googleWeatherString));

			//开始解析事件
			int eventType = parser.getEventType();

			//处理事件，不碰到文档结束就一直处理
			while (eventType != XmlPullParser.END_DOCUMENT) {
				//因为定义了一堆静态常量，所以这里可以用switch
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					//给当前标签起个名字
					String tagName = parser.getName();
					//看到感兴趣的标签个计数
					if (tagName.equals("forecast_conditions")) {
						findCount++;
					}
					//看到要处理的标签，就处理
					if (findCount > 1) {
						if (tagName.equals("low")) {
							//XML中的属性可以用下面的方法获取，其中0是序号，代表第一个属性
							tomorrowWeatherVO.setLow(parser.getAttributeValue(0));
						}
						if (tagName.equals("high")) {
							tomorrowWeatherVO.setHigh(parser.getAttributeValue(0));
						}
						if (tagName.equals("icon")) {
							tomorrowWeatherVO.setIcon(parser.getAttributeValue(0));
						}
						if (tagName.equals("condition")) {
							Log.i("yao", "condition=" + parser.getAttributeValue(0));
							tomorrowWeatherVO.setCondition(parser.getAttributeValue(0));
						}
						tvolists.add(tomorrowWeatherVO);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				}

				//别忘了用next方法处理下一个事件，忘了的结果就成死循环#_#
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tvolists;
//		return tomorrowWeatherVO;
	}
	
	public static List<TomorrowWeatherVO> getGoogleWeatherList(String googleWeatherStrin) {
		List<TomorrowWeatherVO> tvolists = new ArrayList<TomorrowWeatherVO>();
		try{
			InputStream is = new ByteArrayInputStream(googleWeatherStrin.getBytes());
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
			Document d = b.parse(new InputSource(is));
			NodeList n = d.getElementsByTagName("forecast_conditions");
			for (int i = 0; i < n.getLength(); i++) {
				TomorrowWeatherVO tomorrowWeatherVO = new TomorrowWeatherVO();
				tomorrowWeatherVO.setWeek(n.item(i).getChildNodes().item(0).getAttributes().item(0).getNodeValue());
				tomorrowWeatherVO.setLow(n.item(i).getChildNodes().item(1).getAttributes().item(0).getNodeValue());
				tomorrowWeatherVO.setHigh(n.item(i).getChildNodes().item(2).getAttributes().item(0).getNodeValue());
				tomorrowWeatherVO.setIcon(n.item(i).getChildNodes().item(3).getAttributes().item(0).getNodeValue());
				tomorrowWeatherVO.setCondition(n.item(i).getChildNodes().item(4).getAttributes().item(0).getNodeValue());
				tvolists.add(tomorrowWeatherVO);
            }  
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return tvolists;
	}
}
