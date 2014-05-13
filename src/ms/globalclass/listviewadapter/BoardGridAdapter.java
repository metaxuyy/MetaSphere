package ms.globalclass.listviewadapter;

import java.util.List;
import java.util.Map;

import ms.activitys.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BoardGridAdapter extends BaseAdapter {

	private class GridHolder {
		ImageView appImage;
		TextView appTV;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public BoardGridAdapter(Context c) {
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
			convertView = mInflater.inflate(R.layout.board_grid_item, null);
			holder = new GridHolder();
			holder.appImage = (ImageView) convertView
					.findViewById(R.id.publishBoard_addImgItemImg);
			holder.appTV = (TextView) convertView
					.findViewById(R.id.publishBoard_addImgItemTV);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();

		}
		Map<String, Object> info = list.get(index);
		if (info != null) {
			holder.appImage.setImageDrawable((Drawable) info.get("imgid"));
			holder.appTV.setText((String) info.get("imgname"));
		}
		return convertView;
	}

}
