package ms.globalclass;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class MyDialog extends Dialog{

	public MyDialog(Context context, int theme) {  
	    super(context, theme);  
	}  
	  
	public void setView(View view)
	{
		setContentView(view);
	}
	
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
//	    setContentView(R.layout.slt_cnt_type);  
	}  
}
