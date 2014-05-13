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
        
        //���û�����ɫ     
        mPaint.setColor(Color.RED);     
        //�������     
        mPaint.setStyle(Style.FILL);     
             
        //��һ������,ǰ�����Ǿ������Ͻ����꣬�������������½�����     
        canvas.drawRect(new Rect(10, 10, 200, 50), mPaint);  
        
        Paint mPaint2 = new Paint();     
        
        //���û�����ɫ     
        mPaint2.setColor(Color.GREEN);     
        //�������     
        mPaint2.setStyle(Style.FILL);     
             
        //��һ������,ǰ�����Ǿ������Ͻ����꣬�������������½�����     
        canvas.drawRect(new Rect(10, 10, 100, 50), mPaint2);     
        
        canvas.save(Canvas.ALL_SAVE_FLAG);// ����

		// store

        canvas.restore();// �洢
    }     

}
