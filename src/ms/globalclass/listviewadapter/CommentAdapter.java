package ms.globalclass.listviewadapter;

import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.hotel.SelImageActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private class GridHolder {
		TextView userName;
		TextView commentText;
	}

	private Context context;

	private List<Map<String, String>> list;
	private LayoutInflater mInflater;
	private OnClickListener selBtnClick;
	private OnClickListener imgClick;

	public CommentAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<Map<String, String>> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void setSelBtnClick(OnClickListener click) {
		selBtnClick = click;
	}

	public void setImgClick(OnClickListener click) {
		imgClick = click;
	}

	public int getCount() {
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
			convertView = mInflater.inflate(R.layout.adapter_comment_item, null);
			holder = new GridHolder();
			holder.userName = (TextView) convertView.findViewById(R.id.comment_name);
			holder.commentText = (Button) convertView.findViewById(R.id.comment_text);
			convertView.setTag(holder);
		} else {
			holder = (GridHolder) convertView.getTag();
		}

		Map<String, String> info = list.get(index);
		if (info != null) {
			holder.userName.setText(info.get("userName"));
			holder.commentText.setText(info.get("discusDesc"));
		}
		return convertView;
	}
}
