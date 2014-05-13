package ms.globalclass.listviewadapter;

import java.util.List;
import java.util.Map;

import ms.activitys.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuPageModeAdapter extends BaseAdapter {

	private class GridHolder {
		ImageView img;
		TextView tv;
		ImageView selimg;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public MenuPageModeAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
//		mInflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater = LayoutInflater.from(context);

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
			convertView = mInflater.inflate(R.layout.menupagemode_grid_item, null);
			holder = new GridHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.menupagemode_Img);
			holder.tv = (TextView) convertView.findViewById(R.id.menupageshow_TV);
			holder.selimg = (ImageView) convertView.findViewById(R.id.menupageshow_selimg);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();

		}
		Map<String, Object> info = list.get(index);
		if (info != null) {
			holder.img.setImageBitmap((Bitmap) info.get("modeImgData"));
			if(info.get("menuPageModeSel").equals("1")){
				holder.selimg.setBackgroundResource(R.drawable.sns_shoot_select_checked);
			}else{
				holder.selimg.setBackgroundResource(R.drawable.sns_shoot_select_normal);
			}
			holder.tv.setText((String) info.get("modeName"));
			
		}
		return convertView;
	}

}
