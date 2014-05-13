package ms.globalclass.scroll;

import android.content.Context;  
import android.graphics.Rect;  
import android.util.AttributeSet;  
import android.util.Log;  
import android.view.MotionEvent;  
import android.view.View;  
import android.view.animation.TranslateAnimation;  
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView; 

/** 
 * ScrollView反弹效果的实现 
 */  
public class MyScrollView extends ScrollView{

	private View inner;// 孩子View  
	
	private Gallery  ralleryView;
	private RelativeLayout rview;
	private int yuanHight; //原始高度
	private int curedeleY;//当前滑动距离
	  
    private float y;// 点击时y坐标  
  
    private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)  
  
    private boolean isCount = false;// 是否开始计算  
  
    public MyScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    /*** 
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate 
     * 方法，也应该调用父类的方法，使该方法得以执行. 
     */  
    @Override  
    protected void onFinishInflate() {  
        if (getChildCount() > 0) {  
            inner = getChildAt(0);  
            rview = (RelativeLayout)((RelativeLayout)inner).getChildAt(0);
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) rview.getLayoutParams();
            yuanHight = linearParams.height;
//            ralleryView = (Gallery)rview.getChildAt(0);
        }  
    }  
  
    /*** 
     * 监听touch 
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        if (inner != null) {  
            commOnTouchEvent(ev);  
        }  
  
        return super.onTouchEvent(ev);  
    }  
  
    /*** 
     * 触摸事件 
     *  
     * @param ev 
     */  
    public void commOnTouchEvent(MotionEvent ev) {  
        int action = ev.getAction();  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            break;  
        case MotionEvent.ACTION_UP:  
            // 手指松开.  
            if (isNeedAnimation()) {  
                animation();  
                isCount = false;  
            }
            
//            isCount = false;  
            RelativeLayout.LayoutParams linearParams2 = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // 取控件mGrid当前的布局参数
            if(linearParams2.height > getHeight()/2)
        	{
            	linearParams2.height = getHeight();// 当控件的高强制设成75象素
            	rview.setLayoutParams(linearParams2); // 使设置好的布局参数应用到控件mGrid2 
            	
        		 // 开启移动动画  
//                TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
//                        normal.top);
//                ta.setDuration(1500);  
//                inner.startAnimation(ta);
        	}
            else
            {
            	if(linearParams2.height == yuanHight)
            	{
            		
            	}
            	else
            	{
            		linearParams2.height = yuanHight;// 当控件的高强制设成75象素
                	rview.setLayoutParams(linearParams2); // 使设置好的布局参数应用到控件mGrid2 
            	}
//            	RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // 取控件mGrid当前的布局参数
            	
            	
        		 // 开启移动动画  
//                TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),  
//                        normal.top);
//                ta.setDuration(500);  
//                inner.startAnimation(ta);
            }
            break;  
        /*** 
         * 排除出第一次移动计算，因为第一次无法得知y坐标， 在MotionEvent.ACTION_DOWN中获取不到， 
         * 因为此时是MyScrollView的touch事件传递到到了LIstView的孩子item上面.所以从第二次计算开始. 
         * 然而我们也要进行初始化，就是第一次移动的时候让滑动距离归0. 之后记录准确了就正常执行. 
         */  
        case MotionEvent.ACTION_MOVE:  
            final float preY = y;// 按下时的y坐标  
            float nowY = ev.getY();// 时时y坐标  
            int deltaY = (int) (preY - nowY);// 滑动距离  
            if (!isCount) {  
                deltaY = 0; // 在这里要归0.  
            }  
  
            y = nowY; 
            int ddheight = getHeight();
            System.out.println("高度=="+getHeight());
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // 取控件mGrid当前的布局参数
            // 当滚动到最上或者最下时就不会再滚动，这时移动布局  
            if(isNeedMoveTop() && linearParams.height < ddheight && Math.abs(deltaY) > 1)
            {
//            	System.out.println("滑动距离=="+deltaY);
            	curedeleY = Math.abs(deltaY);
            	linearParams.height = Math.abs(deltaY) + linearParams.height;// 当控件的高强制设成75象素
            	rview.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
            }
            if(isNeedMoveTop2() && linearParams.height > yuanHight)
            {
//            	System.out.println("滑动距离=="+deltaY);
//            	scrollTo(0,0);
            	curedeleY = deltaY - deltaY*2;
            	linearParams.height = linearParams.height - Math.abs(deltaY);// 当控件的高强制设成75象素
            	rview.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
            }
//            else if(isNeedMovebommo()) {
//                // 初始化头部矩形  
//                if (normal.isEmpty()) {  
//                    // 保存正常的布局位置  
//                    normal.set(inner.getLeft(), inner.getTop(),  
//                            inner.getRight(), inner.getBottom());  
//                }  
//                Log.e("jj", "矩形：" + inner.getLeft() + "," + inner.getTop()  
//                        + "," + inner.getRight() + "," + inner.getBottom());  
//                // 移动布局  
//                inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,  
//                        inner.getRight(), inner.getBottom() - deltaY / 2);  
//            }  
            isCount = true;  
            break;  
  
        default:  
            break;  
        }  
    }  
  
    /*** 
     * 回缩动画 
     */  
    public void animation() {  
        // 开启移动动画  
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),  
                normal.top);  
        ta.setDuration(200);  
        inner.startAnimation(ta);  
        // 设置回到正常的布局位置  
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);  
  
        Log.e("jj", "回归：" + normal.left + "," + normal.top + "," + normal.right  
                + "," + normal.bottom);  
  
        normal.setEmpty();  
  
    } 
  
    // 是否需要开启动画  
    public boolean isNeedAnimation() {  
        return !normal.isEmpty();  
    }  
  
    /*** 
     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度 
     *  
     * getHeight()：获取的是屏幕的高度 
     *  
     * @return 
     */  
    public boolean isNeedMove() {  
        int offset = inner.getMeasuredHeight() - getHeight();  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0是顶部，后面那个是底部  
        if (scrollY == 0 || scrollY == offset) {  
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMoveTop() {  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0是顶部，后面那个是底部  
        if (scrollY == 0) {
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMoveTop2() {  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0是顶部，后面那个是底部  
        if (scrollY > 0) {
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMovebommo() {  
    	int offset = inner.getMeasuredHeight() - getHeight(); 
    	int scrollY = getScrollY();
        // 0是顶部，后面那个是底部  
        if (scrollY == offset) {
            return true;  
        }  
        return false;  
    }  
}
