package ms.activitys.dodowaterfall;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class TaskParam {
	private String filename;
	private AssetManager assetManager;
	private int ItemWidth;
	private String urlstr;
	private Bitmap notimg;
	private int hig;

	public int getHig() {
		return hig;
	}

	public void setHig(int hig) {
		this.hig = hig;
	}

	public Bitmap getNotimg() {
		return notimg;
	}

	public void setNotimg(Bitmap notimg) {
		this.notimg = notimg;
	}

	public String getUrlstr() {
		return urlstr;
	}

	public void setUrlstr(String urlstr) {
		this.urlstr = urlstr;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}
}
