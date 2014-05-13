package ms.globalclass.map;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class MyAdapter extends SimpleAdapter{

//	private int[] colors = new int[] { 0xFF434343, 0x70809000 };
	private List<Map<String,Object>> list; 
	
	public MyAdapter(Context context, List<Map<String,Object>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.list = data;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		Map map = list.get(position);
		String sname = (String)map.get("sname");
//		int colorPos = position % colors.length;
		if(sname.equals("1"))
			view.setBackgroundColor(Color.RED);
		else if(sname.equals("2"))
			view.setBackgroundColor(Color.GREEN);
//		view.setBackgroundColor(colors[colorPos]);
		return view;
	}

}
