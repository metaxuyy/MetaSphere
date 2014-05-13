package ms.globalclass.listviewadapter;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.activitys.R;
import ms.activitys.hotel.SelImageActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private class GridHolder {
		ImageView img;
		Button selBtn;
	}

	private Context context;

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;
	private OnClickListener selBtnClick;
	private OnClickListener imgClick;
	private Map<Integer,ImageView> imagelist = new HashMap<Integer,ImageView>();

	public ImageAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<Map<String, Object>> list) {
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
	
	public ImageView getImageView(int index)
	{
		return imagelist.get(index);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_image_item, null);
			holder = new GridHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.selBtn = (Button) convertView.findViewById(R.id.imgSelBtn);
			convertView.setTag(holder);
		} else {
			holder = (GridHolder) convertView.getTag();
		}

		Map<String, Object> info = list.get(index);
		imagelist.put(index, holder.img);
		if (info != null) {
			holder.selBtn.setVisibility(View.VISIBLE);
			holder.selBtn.setOnClickListener(selBtnClick);
			if (info.get("isSel").equals("1")) {
				holder.selBtn.setSelected(true);
				holder.selBtn
						.setBackgroundResource(R.drawable.friends_sends_pictures_select_big_icon_selected);
			} else {
				holder.selBtn.setSelected(false);
				holder.selBtn
						.setBackgroundResource(R.drawable.friends_sends_pictures_select_big_icon_unselected);
			}
			
			holder.img.setImageBitmap(null);
			holder.img.setOnClickListener(imgClick);

			((SelImageActivity) context).c++;
			if (((SelImageActivity) context).isFirstLoad && (((SelImageActivity) context).c < 30)){
				((SelImageActivity) context).loadImage(
						(String) info.get("imgPath"), holder.img, index);
			}
		}
		return convertView;
	}
}
