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
	
	
	// ��������Ԥ���ַ�����һ��������Ϣ����
	public static List<TomorrowWeatherVO> parse(String googleWeatherString) {

		Log.i("yao", "TomorrowWeatherPullParse.parse");

		// ��¼���ִ���
		int findCount = 0;
		
		List<TomorrowWeatherVO> tvolists = new ArrayList<TomorrowWeatherVO>();

		// ��������Ԥ��Bean
		TomorrowWeatherVO tomorrowWeatherVO = new TomorrowWeatherVO();

		try {

			//���幤�� XmlPullParserFactory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			//��������� XmlPullParser
			XmlPullParser parser = factory.newPullParser();

			//��ȡxml��������
			parser.setInput(new StringReader(googleWeatherString));

			//��ʼ�����¼�
			int eventType = parser.getEventType();

			//�����¼����������ĵ�������һֱ����
			while (eventType != XmlPullParser.END_DOCUMENT) {
				//��Ϊ������һ�Ѿ�̬�������������������switch
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					//����ǰ��ǩ�������
					String tagName = parser.getName();
					//��������Ȥ�ı�ǩ������
					if (tagName.equals("forecast_conditions")) {
						findCount++;
					}
					//����Ҫ����ı�ǩ���ʹ���
					if (findCount > 1) {
						if (tagName.equals("low")) {
							//XML�е����Կ���������ķ�����ȡ������0����ţ������һ������
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

				//��������next����������һ���¼������˵Ľ���ͳ���ѭ��#_#
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
