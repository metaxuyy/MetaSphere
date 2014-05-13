package ms.globalclass.listviewadapter;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import ms.activitys.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagerGridAdapter extends BaseAdapter {

	private class GridHolder {
		ImageView appImage;
		TextView appName;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public ManagerGridAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.manager_grid_item, null);
			holder = new GridHolder();
			holder.appImage = (ImageView) convertView
					.findViewById(R.id.itemImage);
			holder.appName = (TextView) convertView.findViewById(R.id.itemText);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();

		}
		Map<String, Object> info = list.get(index);
		if (info != null) {
			holder.appName.setText((String) info.get("name"));
			// holder.appImage.setImageResource((Integer) info.get("imgid"));
			try {
				URL url = new URL((String) info.get("imgid"));
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream in = conn.getInputStream();
				Bitmap map = BitmapFactory.decodeStream(in);
				holder.appImage.setImageBitmap(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return convertView;
	}
}
