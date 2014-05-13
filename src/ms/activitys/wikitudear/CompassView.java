package ms.activitys.wikitudear;

import ms.activitys.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends ImageView{

	private float b;
	private float c;
	private final Paint d;
	private final Paint e;
	private final Paint f;
	private float g;
	private Bitmap h;
	  
	public CompassView(Context context) {
		super(context);
	    Paint localPaint1 = new Paint();
	    this.d = localPaint1;
	    Paint localPaint2 = new Paint();
	    this.e = localPaint2;
	    Paint localPaint3 = new Paint();
	    this.f = localPaint3;
	    this.g = 0;
	    a(null, context);
	}

	public CompassView(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	    Paint localPaint1 = new Paint();
	    this.d = localPaint1;
	    Paint localPaint2 = new Paint();
	    this.e = localPaint2;
	    Paint localPaint3 = new Paint();
	    this.f = localPaint3;
	    this.g = 0;
	    a(null, paramContext);
	  }

	  public CompassView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	  {
	    super(paramContext, paramAttributeSet, paramInt);
	    Paint localPaint1 = new Paint();
	    this.d = localPaint1;
	    Paint localPaint2 = new Paint();
	    this.e = localPaint2;
	    Paint localPaint3 = new Paint();
	    this.f = localPaint3;
	    this.g = 0;
	    a(null, paramContext);
	  }
	  
	  private void a(AttributeSet paramAttributeSet, Context paramContext)
	  {
	    setImageResource(2130837526);
	    this.c = 2139095039;
	    this.f.setARGB(255, 150, 150, 150);
	    this.f.setAntiAlias(true);
	    this.d.setARGB(255, 247, 99, 0);
	    this.d.setAntiAlias(true);
	    this.e.setARGB(255, 0, 0, 0);
	    this.e.setAntiAlias(true);
	    Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), R.drawable.compass_needle);
	    this.h = localBitmap;
	  }
}
