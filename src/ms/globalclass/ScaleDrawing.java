package ms.globalclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.view.View;

public class ScaleDrawing extends View{

	private Context mContext;
	
	public ScaleDrawing(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub     
        super.onDraw(canvas);     
             
        Paint mPaint = new Paint();     
        
        //设置画笔颜色     
        mPaint.setColor(Color.RED);     
        //设置填充     
        mPaint.setStyle(Style.FILL);     
             
        //画一个矩形,前俩个是矩形左上角坐标，后面俩个是右下角坐标     
        canvas.drawRect(new Rect(10, 10, 200, 50), mPaint);  
        
        Paint mPaint2 = new Paint();     
        
        //设置画笔颜色     
        mPaint2.setColor(Color.GREEN);     
        //设置填充     
        mPaint2.setStyle(Style.FILL);     
             
        //画一个矩形,前俩个是矩形左上角坐标，后面俩个是右下角坐标     
        canvas.drawRect(new Rect(10, 10, 100, 50), mPaint2);     
        
        canvas.save(Canvas.ALL_SAVE_FLAG);// 保存

		// store

        canvas.restore();// 存储
    }     

}
