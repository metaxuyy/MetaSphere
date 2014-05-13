package ms.activitys.hotel;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import ms.activitys.hotel.CustomMultiPartEntity.ProgressListener;
import ms.globalclass.map.MyApp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HttpMultipartPost extends AsyncTask<HttpResponse, Integer, String> {
	ProgressDialog pd;
	long totalSize;
	Context context;
	private String url;
	private String path;
	private TextView ptext;
	private LinearLayout playout;
	private int rowindex;
	private MyApp myapp;

	public MyApp getMyapp() {
		return myapp;
	}

	public void setMyapp(MyApp myapp) {
		this.myapp = myapp;
	}

	public int getRowindex() {
		return rowindex;
	}

	public void setRowindex(int rowindex) {
		this.rowindex = rowindex;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HttpMultipartPost(Context context) {
		this.context = context;
	}

	public TextView getPtext() {
		return ptext;
	}

	public void setPtext(TextView ptext) {
		this.ptext = ptext;
	}

	public LinearLayout getPlayout() {
		return playout;
	}

	public void setPlayout(LinearLayout playout) {
		this.playout = playout;
	}

	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("Uploading Picture...");
//		pd.setCancelable(false);
//		pd.show();
	}

	@Override
	protected String doInBackground(HttpResponse... arg0) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(new ProgressListener() {
				@Override
				public void transferred(long num) {
					publishProgress((int) ((num / (float) totalSize) * 100));
				}
			});

			// We use FileBody to transfer an image
			multipartContent.addPart("uploaded_file", new FileBody(new File(path)));
			totalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			String serverResponse = EntityUtils.toString(response.getEntity());

			// ResponseFactory rp = new ResponseFactory(serverResponse);
			// return (TypeImage) rp.getData();

			return serverResponse;
		}

		catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
//		pd.setProgress((int) (progress[0]));
		if(myapp.isNetwork())
			ptext.setText(String.valueOf(progress[0])+"%");
		else
		{
//			playout.setVisibility(View.GONE);
			MessageListActivity.instance.updateImageUploadStart(false, rowindex);
		}
	}

	@Override
	protected void onPostExecute(String result) {
//		pd.dismiss();
		if(myapp.isNetwork())
		{
			if(result != null && !result.equals(""))
			{
				MessageListActivity.instance.updateImageUploadStart(true, rowindex);
				playout.setVisibility(View.GONE);
			}
			else
			{
				MessageListActivity.instance.updateImageUploadStart(false, rowindex);
				playout.setVisibility(View.GONE);
			}
		}
		else
		{
			MessageListActivity.instance.updateImageUploadStart(false, rowindex);
			playout.setVisibility(View.GONE);
		}
	}
}