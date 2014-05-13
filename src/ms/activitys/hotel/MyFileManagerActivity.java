package ms.activitys.hotel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import ms.activitys.R;
import ms.globalclass.listviewadapter.MyFileSelAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyFileManagerActivity extends ListActivity {
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = getSDDir();
	private String curPath = getSDDir();
	private TextView mPath;
	private boolean isSearchVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fileselect);

		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			isSearchVideo = data.getBoolean("isVideo");
		}

		mPath = (TextView) findViewById(R.id.mPath);
		getFileDir(rootPath);
	}

	private void getFileDir(String filePath) {
		mPath.setText("文件所在主路径：" + filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();
		if (!filePath.equals(rootPath)) {
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			// check file is shp file or not
			// if is,add into list to show
			if (checkShapeFile(file)) {
				items.add(file.getName());
				paths.add(file.getPath());
			}
		}

		setListAdapter(new MyFileSelAdapter(this, items, paths));
	}

	// open allocate file
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(paths.get(position));
		if (file.isDirectory()) {
			curPath = paths.get(position);
			getFileDir(paths.get(position));
		} else {
			System.out.println(file);
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				if(fis.available()>5*1024*1024){
					Toast.makeText(MyFileManagerActivity.this, "文件大小超出5MB，请重新选择！",
							Toast.LENGTH_SHORT).show();
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent data = new Intent(MyFileManagerActivity.this,
					MomentsUploadActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("file", file.getPath());
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	public boolean checkShapeFile(File file) {
		String fileNameString = file.getName();
		String endNameString = fileNameString.substring(
				fileNameString.lastIndexOf(".") + 1, fileNameString.length())
				.toLowerCase();
		// file is directory or not
		if (fileNameString.lastIndexOf(".") == -1) {
			return true;
		}
		//System.out.println("文件后缀名=="+endNameString);
		if (!isSearchVideo && (endNameString.equals("txt") || endNameString.equals("docx")
				|| endNameString.equals("doc") || endNameString.equals("xlsx") || endNameString.equals("xls") || endNameString.equals("pptx")
				|| endNameString.equals("pdf"))) {
			return true;
		} else if (isSearchVideo && (endNameString.equals("mp4") || endNameString.equals("3gp"))) {
			return true;
		} else {
			return false;
		}
	}

	protected final String getSDDir() {
		if (!checkSDcard()) {
			Toast.makeText(this, "no sdcard", Toast.LENGTH_SHORT).show();
			return "";
		}
		try {
			String SD_DIR = Environment.getExternalStorageDirectory()
					.toString();
			return SD_DIR;
		} catch (Exception e) {
			return "";
		}
	}

	public boolean checkSDcard() {
		String sdStutusString = Environment.getExternalStorageState();
		if (sdStutusString.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}
