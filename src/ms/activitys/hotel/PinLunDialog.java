package ms.activitys.hotel;

import ms.activitys.R;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

public class PinLunDialog extends AlertDialog{

	protected PinLunDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public PinLunDialog(Context context,int theme) {
		super(context,theme);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.pin_lun_dialog);
	}
	
}
