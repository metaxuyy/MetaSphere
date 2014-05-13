package ms.globalclass.listviewadapter;

import ms.activitys.R;
import ms.globalclass.map.MyApp;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends View {  
	 private char[] l;  
	    private SectionIndexer sectionIndexter = null;  
	    private ListView list;  
	    private TextView mDialogText;
	    private int m_nItemHeight = 14;  
	    private int m_textsize = 12;
	    private DisplayMetrics dm;
	    private MyApp myapp;
	    private Context context;
	    public SideBar(Context context) {
	        super(context);

//	        myapp = (MyApp)context.getApplicationContext();
//	        if(myapp.getScreenWidth() <= 320 && myapp.getScreenHeight() <= 400 )
//	        {
//	        	m_nItemHeight = 14;
//	        	m_textsize = 12;
//	        }
//	        else if(myapp.getScreenWidth() <= 480 && myapp.getScreenHeight() <= 800 )
//	        {
//	        	m_nItemHeight = 24;
//	        	m_textsize = 18;
//	        }
//	        else if(myapp.getScreenWidth() <= 720 && myapp.getScreenHeight() <= 1280 )
//	        {
//	        	m_nItemHeight = 28;
//	        	m_textsize = 22;
//	        }
	        init(context,null);  
	    }  
	    public SideBar(Context context, AttributeSet attrs) {  
	        super(context, attrs);
	        init(context, attrs);  
	    }  
	    private void init(Context context, AttributeSet attrs) {
	        l = new char[] { '@','#','1','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
//	        if(attrs != null) {     
//	            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBar);   
//	            m_nItemHeight= a.getDimensionPixelSize(R.styleable.SideBar_textHeight, m_nItemHeight);  
//	            m_textsize= a.getDimensionPixelSize(R.styleable.SideBar_textSize, m_textsize);  
//	        }
	        this.context = context;
	        myapp = (MyApp)context.getApplicationContext();
	        if(myapp.getScreenWidth() <= 320 && myapp.getScreenHeight() <= 400 )
	        {
	        	m_nItemHeight = 14;
	        	m_textsize = 12;
	        }
	        else if(myapp.getScreenWidth() > 320 && myapp.getScreenWidth() < 720 && myapp.getScreenHeight() > 400 && myapp.getScreenHeight() < 900 )
	        {
	        	m_nItemHeight = 20;
	        	m_textsize = 16;
	        }
	        else if(myapp.getScreenWidth() > 320 && myapp.getScreenWidth() < 720 && myapp.getScreenHeight() > 400 && myapp.getScreenHeight() < 980 )
	        {
	        	m_nItemHeight = 26;
	        	m_textsize = 22;
	        }
	        else if(myapp.getScreenWidth() >= 720 && myapp.getScreenHeight() >= 1280 )
	        {
	        	m_nItemHeight = 35;
	        	m_textsize = 28;
	        }
	    }  
	    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle); 
	        init(context, attrs);
	    }
	    public void setListView(ListView _list) {  
	        list = _list;  
	        sectionIndexter = (SectionIndexer) _list.getAdapter();
	    }  
	    public void setTextView(TextView mDialogText) {  
	    	this.mDialogText = mDialogText;  
	    }  
	    public boolean onTouchEvent(MotionEvent event) {  
	        super.onTouchEvent(event);  
	        int i = (int) event.getY();  
	        int idx = i / m_nItemHeight;  
	        if (idx >= l.length) {  
	            idx = l.length - 1;  
	        } else if (idx < 0) {  
	            idx = 0;  
	        }  
	        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	        	mDialogText.setVisibility(View.VISIBLE);
	        	this.setBackgroundResource(R.drawable.mm_text_bg_trans);
	        	if(idx == 0)
	        	{
	        		mDialogText.setText(context.getString(R.string.hotel_label_13));
	        	}
	        	else if(idx == 2)
	        		mDialogText.setText("❤");
	        	else
	        		mDialogText.setText(""+l[idx]);
	            if (sectionIndexter == null) {  
	                sectionIndexter = (SectionIndexer) list.getAdapter();  
	            }
	            if(idx == 0)
	            {
	            	list.setSelection(0);  
	            }
	            else if(idx == 1)
	            {
	            	list.setSelection(1);  
	            }
//	            else if(idx == 2)
//	            {
//	            	list.setSelection(2);
//	            }
	            else
	            {
	            	try{
		            int position = sectionIndexter.getPositionForSection(l[idx]);
		            if (position == -1) {
		                return true;
		            }  
		            list.setSelection(position);  
	            	}catch(Exception ex){
	            		ex.printStackTrace();
	            	}
	            }
	        }else{
	        	mDialogText.setVisibility(View.INVISIBLE);
	        	this.setBackgroundResource(R.drawable.silver);
	        }  
	        return true;  
	    }  
	    protected void onDraw(Canvas canvas) {  
	        Paint paint = new Paint();  
	        paint.setColor(0xff595c61);  
	        paint.setTextSize(m_textsize);  
	        paint.setTextAlign(Paint.Align.CENTER);  
	        float widthCenter = getMeasuredWidth() / 2;  
	        for (int i = 0; i < l.length; i++) {
	        	if(i == 0)
	        	{
	        		Resources res = getResources();  
	        		Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.scroll_bar_search_icon);
	        		canvas.drawBitmap(bmp, widthCenter-10, 10, paint);
	        	}
//	        	else
	        	else if(i == 2)
	        		canvas.drawText("❤", widthCenter, m_nItemHeight + (i * m_nItemHeight), paint); 
	        	else
	        		canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);  
	        }  
	        super.onDraw(canvas);  
	    }  
}
