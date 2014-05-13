package ms.activitys.dodowaternew;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class FlowTag {
	private int flowId;
	private String fileName;
	public final int what = 1;
	private String urlstr;
	private Bitmap notimg;

	public String getUrlstr() {
		return urlstr;
	}

	public void setUrlstr(String urlstr) {
		this.urlstr = urlstr;
	}

	public Bitmap getNotimg() {
		return notimg;
	}

	public void setNotimg(Bitmap notimg) {
		this.notimg = notimg;
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private AssetManager assetManager;
	private int ItemWidth;

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
