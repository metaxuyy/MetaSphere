package ms.globalclass.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class DashedLine extends View {
	
	private final String namespace = "http://com.smartmap.driverbook";
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private Rect mRect;
 
    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);         
       
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);       
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.DKGRAY);
//        Path path = new Path();    
//        path.moveTo(0, 10);
//        path.lineTo(480,10);     
//        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
//        paint.setPathEffect(effects);
//        canvas.drawPath(path, paint);
        
        Paint mPaint = new Paint();     
		      
		//���û�����ɫ     
		mPaint.setColor(Color.RED);     
		//�������     
		mPaint.setStyle(Style.FILL);     
		      
		//��һ������,ǰ�����Ǿ������Ͻ����꣬�������������½�����     
		canvas.drawRect(new Rect(10, 10, 200, 100), mPaint);     
    }
}
