package ms.globalclass.listviewadapter;

import java.io.File;
import java.util.List;

import ms.activitys.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyFileSelAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Bitmap mIconBack01;
	private Bitmap mIconBack02;
	private Bitmap mIconFolder;
	private Bitmap mIconTxt;
	private Bitmap mIconDoc;
	private Bitmap mIconExcel;
	private Bitmap mIconPpt;
	private Bitmap mIconPdf;
	private Bitmap mIconMp4;
	private List<String> items;
	private List<String> paths;

	public MyFileSelAdapter(Context context, List<String> it, List<String> pa) {
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
		mIconBack01 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.back01);
		mIconBack02 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.back02);
		mIconFolder = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.folder);
		mIconTxt = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.txt);
		mIconDoc = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.word);
		mIconExcel = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.excel);
		mIconPpt = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ppt);
		mIconPdf = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.pdf);
		mIconMp4 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mp4);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView
					.findViewById(R.id.fileSel_text);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.fileSel_icon);
			// holder.checkBox = (CheckBox) convertView
			// .findViewById(R.id.fileSel_checkBox);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		File f = new File(paths.get(position).toString());
		if (items.get(position).toString().equals("b1")) {
			holder.text.setText("返回根目录..");
			holder.icon.setImageBitmap(mIconBack02);
		} else if (items.get(position).toString().equals("b2")) {
			holder.text.setText("返回上一层..");
			holder.icon.setImageBitmap(mIconBack02);
		} else {
			holder.text.setText(f.getName());
			if (f.isDirectory()) {
				holder.icon.setImageBitmap(mIconFolder);
			} else {
				String fileNameString = f.getName();
				String endNameString = fileNameString.substring(
						fileNameString.lastIndexOf(".") + 1,
						fileNameString.length()).toLowerCase();
				if (endNameString.equals("txt")) {
					holder.icon.setImageBitmap(mIconTxt);
				} else if (endNameString.equals("docx")) {
					holder.icon.setImageBitmap(mIconDoc);
				} else if (endNameString.equals("doc")) {
					holder.icon.setImageBitmap(mIconDoc);
				} else if (endNameString.equals("xlsx")) {
					holder.icon.setImageBitmap(mIconExcel);
				}else if (endNameString.equals("xls")) {
					holder.icon.setImageBitmap(mIconExcel);
				}else if (endNameString.equals("pptx")) {
					holder.icon.setImageBitmap(mIconPpt);
				} else if (endNameString.equals("pdf")) {
					holder.icon.setImageBitmap(mIconPdf);
				} else if (endNameString.equals("mp4") || endNameString.equals("3gp")) {
					holder.icon.setImageBitmap(mIconMp4);
				}
			}
		}
		return convertView;
	}

	private class ViewHolder {
		TextView text;
		ImageView icon;
		CheckBox checkBox;
	}

}
