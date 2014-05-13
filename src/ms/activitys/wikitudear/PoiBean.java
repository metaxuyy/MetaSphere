package ms.activitys.wikitudear;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * PoiBean class that serves as a container for poi information
 * contains basic information such as name, description, type and location
 */
public class PoiBean {
	private String id;
	private String name;
	private String imgname;
	private String description;
	private int type;
	private String juli;
	
	private Point point;
	
	private class Point
	{
		private double latitude;
		private double longitude;
		private double altitude;
		
		public Point(double lat, double lon,double altitude)
		{
			this.latitude = lat;
			this.longitude = lon;
			this.altitude = altitude;
		}
		
		public JSONObject toJSONString() throws JSONException
		{
			JSONObject object = new JSONObject();
			object.put("latitude", this.latitude);
			object.put("longitude", this.longitude);
			object.put("altitude", this.altitude);
			return object;
		}
	}
	
	public PoiBean(String _id, String _name, String _desc,String _imgname,String _juli, int _type, double lat, double lon,double alt)
	{
		this.id = _id;
		this.name = _name;
		this.description = _desc;
		this.imgname = _imgname;
		this.juli = _juli;
		this.type = _type;
		this.point = new Point(lat, lon, alt);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String getImgname() {
		return imgname;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

	public String getJuli() {
		return juli;
	}

	public void setJuli(String juli) {
		this.juli = juli;
	}

	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("id", this.id);
		object.put("name", this.name);
		object.put("description", this.description);
		object.put("imgname", this.imgname);
		object.put("juli", this.juli);
		object.put("type", this.type);
		object.put("Point", this.point.toJSONString());
		
		return object;
	}

}
