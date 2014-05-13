package ms.activitys;

import ms.globalclass.map.MyApp;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
 */
public class MyLocationListenner implements BDLocationListener{

	private MyApp myapp;
	
	public MyLocationListenner(MyApp myapp)
	{
		this.myapp = myapp;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		myapp.setLatitude((int)(location.getLatitude() * 1E6));
		myapp.setLongitude((int)(location.getLongitude() * 1E6));
		myapp.setLat(String.valueOf(location.getLatitude()));
		myapp.setLng(String.valueOf(location.getLongitude()));
//		System.out.println("百度定位 lat==" + myapp.getLat());
//		System.out.println("百度定位 lng==" + myapp.getLng());
	}

	public void onReceivePoi(BDLocation poiLocation) {
		if (poiLocation == null) {
			return;
		}
	}
}
