package ms.activitys.hotel;

import ms.activitys.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageShower extends Activity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageshower);
        
        Bundle bunde = this.getIntent().getExtras();
        byte[] imgbyte = bunde.getByteArray("storeimg");
        
        ImageView img = (ImageView)findViewById(R.id.img_view);
        
        if(imgbyte != null)
		{
			Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte,0,imgbyte.length);
//			bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
			if(bitmap != null)
				img.setImageBitmap(bitmap);
		}

        final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
        dialog.show();
        // 两秒后关闭后dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000 * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }

}
