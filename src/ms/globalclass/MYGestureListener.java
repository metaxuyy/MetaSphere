package ms.globalclass;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

public class MYGestureListener extends SimpleOnGestureListener implements OnTouchListener{

	private GestureDetector gDetector;  
    private ViewFlipper viewFlipper;  
      
    public MYGestureListener(){  
        super();  
    }  
      
    public MYGestureListener(Context con){  
        this(con, null, null);  
    }  
    public MYGestureListener(Context con, GestureDetector gDetector, ViewFlipper viewFlipper){  
        if(null == gDetector){  
            gDetector = new GestureDetector(con, this);  
        };  
          
        this.gDetector = gDetector;  
        this.viewFlipper = viewFlipper;  
    }  
      
    @Override  
    public boolean onSingleTapConfirmed(MotionEvent e) {  
        return super.onSingleTapConfirmed(e);  
    }  
      
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY) {  
        //TODO viewFlipper.showNext()...whatever you want  
        return false;  
    }  
  
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        return gDetector.onTouchEvent(event);  
    }  
      
    public GestureDetector getDector(){  
        return this.gDetector;  
    }  
}
