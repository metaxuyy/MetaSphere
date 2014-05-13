package ms.globalclass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ReflectionImage extends ImageView{
	//�Ƿ�ΪReflectionģʽ  
    private boolean mReflectionMode = true;  
    public ReflectionImage(Context context) {  
        super(context);  
    }  
    public ReflectionImage(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        //ȡ��ԭʼͼƬ��bitmap���ػ�  
        Bitmap originalImage = ((BitmapDrawable)this.getDrawable()).getBitmap();  
        DoReflection(originalImage);  
    }  
    public ReflectionImage(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
        Bitmap originalImage = ((BitmapDrawable)this.getDrawable()).getBitmap();  
        DoReflection(originalImage);  
    }  
    public void setReflectionMode(boolean isRef) {  
        mReflectionMode = isRef;  
    }  
    public boolean getReflectionMode() {  
        return mReflectionMode;  
    }  
    //͵����,ֻ��д��setImageResource,�͹��캯���������ͬ��������  
    @Override  
    public void setImageResource(int resId) {  
        Bitmap originalImage = BitmapFactory.decodeResource(  
                getResources(), resId);  
        DoReflection(originalImage);  
        //super.setImageResource(resId);  
    } 
//    @Override  
//    public void setImageBitmap(Bitmap bitmap) {  
//        DoReflection(bitmap);  
//        //super.setImageResource(resId);  
//    }  
//    @Override  
//    public void setImageDrawable(Drawable drawable) { 
//    	Bitmap bitmap = drawableToBitmap(drawable);
//        DoReflection(bitmap);  
//        //super.setImageResource(resId);  
//    }

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
				.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

    private void DoReflection(Bitmap originalImage) {  
        final int reflectionGap = 2;                            //ԭʼͼƬ�ͷ���ͼƬ�м�ļ��  
        int width = originalImage.getWidth();  
        int height = originalImage.getHeight();
        
        //��ת  
        Matrix matrix = new Matrix();  
        matrix.preScale(1, -1);
      //reflectionImage��������͸�����ǲ���,�����������ĸ߶�Ϊԭʼ��3/4,����Ч�������Щ  
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,  
                0, width, height, matrix, false);  
        //����һ���µ�bitmap,�߶�Ϊԭ��������  
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height), Config.ARGB_8888);  
        Canvas canvasRef = new Canvas(bitmapWithReflection);  
          
        //�Ȼ�ԭʼ��ͼƬ  
        canvasRef.drawBitmap(originalImage, 0, 0, null);  
        //�����  
        Paint deafaultPaint = new Paint();  
        canvasRef.drawRect(0, height, width, height + reflectionGap, deafaultPaint);  
          
        //������ת�Ժ��ͼƬ  
        canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);  
        // ����һ��������ɰ�������汻��ת��ͼƬ����  
        Paint paint = new Paint();  
        LinearGradient shader = new LinearGradient(0,  
                originalImage.getHeight(), 0, bitmapWithReflection.getHeight()  
                        + reflectionGap, 0x80ffffff, 0x00ffffff, TileMode.CLAMP);  
        // Set the paint to use this shader (linear gradient)  
        paint.setShader(shader);  
        // Set the Transfer mode to be porter duff and destination in  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        // Draw a rectangle using the paint with our linear gradient  
        canvasRef.drawRect(0, height, width, bitmapWithReflection.getHeight()  
                + reflectionGap, paint);  
      //����ImageView�е�setImageBitmap  
        this.setImageBitmap(bitmapWithReflection);  
    } 
    
}
