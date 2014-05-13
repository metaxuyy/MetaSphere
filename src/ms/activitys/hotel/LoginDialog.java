package ms.activitys.hotel;

import ms.activitys.MainTabActivity;
import ms.activitys.R;
import ms.globalclass.dbhelp.DBHelperMessage;
import ms.globalclass.map.MyApp;
import ms.globalclass.pushmessage.MyService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginDialog extends Activity{

	// private MyDialog dialog;
	private LinearLayout layout;
	private MyApp myapp;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_dialog);

		myapp = (MyApp) this.getApplicationContext();
		share = getSharedPreferences("perference", MODE_PRIVATE);

		// dialog=new MyDialog(this);
		layout = (LinearLayout) findViewById(R.id.login_layout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton0(View v){
		this.finish();
		MainTabActivity.instance.openLogin();
	}
	
	public void exitbutton2(View v){
		this.finish();
		MainTabActivity.instance.openRegisterAction();
	}
}
