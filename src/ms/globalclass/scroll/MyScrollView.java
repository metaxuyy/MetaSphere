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
 * ScrollView����Ч����ʵ�� 
 */  
public class MyScrollView extends ScrollView{

	private View inner;// ����View  
	
	private Gallery  ralleryView;
	private RelativeLayout rview;
	private int yuanHight; //ԭʼ�߶�
	private int curedeleY;//��ǰ��������
	  
    private float y;// ���ʱy����  
  
    private Rect normal = new Rect();// ����(����ֻ�Ǹ���ʽ��ֻ�������ж��Ƿ���Ҫ����.)  
  
    private boolean isCount = false;// �Ƿ�ʼ����  
  
    public MyScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    /*** 
     * ���� XML ������ͼ�������.�ú�����������ͼ�������ã�����������ͼ�����֮��. ��ʹ���า���� onFinishInflate 
     * ������ҲӦ�õ��ø���ķ�����ʹ�÷�������ִ��. 
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
     * ����touch 
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        if (inner != null) {  
            commOnTouchEvent(ev);  
        }  
  
        return super.onTouchEvent(ev);  
    }  
  
    /*** 
     * �����¼� 
     *  
     * @param ev 
     */  
    public void commOnTouchEvent(MotionEvent ev) {  
        int action = ev.getAction();  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            break;  
        case MotionEvent.ACTION_UP:  
            // ��ָ�ɿ�.  
            if (isNeedAnimation()) {  
                animation();  
                isCount = false;  
            }
            
//            isCount = false;  
            RelativeLayout.LayoutParams linearParams2 = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // ȡ�ؼ�mGrid��ǰ�Ĳ��ֲ���
            if(linearParams2.height > getHeight()/2)
        	{
            	linearParams2.height = getHeight();// ���ؼ��ĸ�ǿ�����75����
            	rview.setLayoutParams(linearParams2); // ʹ���úõĲ��ֲ���Ӧ�õ��ؼ�mGrid2 
            	
        		 // �����ƶ�����  
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
            		linearParams2.height = yuanHight;// ���ؼ��ĸ�ǿ�����75����
                	rview.setLayoutParams(linearParams2); // ʹ���úõĲ��ֲ���Ӧ�õ��ؼ�mGrid2 
            	}
//            	RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // ȡ�ؼ�mGrid��ǰ�Ĳ��ֲ���
            	
            	
        		 // �����ƶ�����  
//                TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),  
//                        normal.top);
//                ta.setDuration(500);  
//                inner.startAnimation(ta);
            }
            break;  
        /*** 
         * �ų�����һ���ƶ����㣬��Ϊ��һ���޷���֪y���꣬ ��MotionEvent.ACTION_DOWN�л�ȡ������ 
         * ��Ϊ��ʱ��MyScrollView��touch�¼����ݵ�����LIstView�ĺ���item����.���Դӵڶ��μ��㿪ʼ. 
         * Ȼ������ҲҪ���г�ʼ�������ǵ�һ���ƶ���ʱ���û��������0. ֮���¼׼ȷ�˾�����ִ��. 
         */  
        case MotionEvent.ACTION_MOVE:  
            final float preY = y;// ����ʱ��y����  
            float nowY = ev.getY();// ʱʱy����  
            int deltaY = (int) (preY - nowY);// ��������  
            if (!isCount) {  
                deltaY = 0; // ������Ҫ��0.  
            }  
  
            y = nowY; 
            int ddheight = getHeight();
            System.out.println("�߶�=="+getHeight());
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) rview.getLayoutParams(); // ȡ�ؼ�mGrid��ǰ�Ĳ��ֲ���
            // �����������ϻ�������ʱ�Ͳ����ٹ�������ʱ�ƶ�����  
            if(isNeedMoveTop() && linearParams.height < ddheight && Math.abs(deltaY) > 1)
            {
//            	System.out.println("��������=="+deltaY);
            	curedeleY = Math.abs(deltaY);
            	linearParams.height = Math.abs(deltaY) + linearParams.height;// ���ؼ��ĸ�ǿ�����75����
            	rview.setLayoutParams(linearParams); // ʹ���úõĲ��ֲ���Ӧ�õ��ؼ�mGrid2 
            }
            if(isNeedMoveTop2() && linearParams.height > yuanHight)
            {
//            	System.out.println("��������=="+deltaY);
//            	scrollTo(0,0);
            	curedeleY = deltaY - deltaY*2;
            	linearParams.height = linearParams.height - Math.abs(deltaY);// ���ؼ��ĸ�ǿ�����75����
            	rview.setLayoutParams(linearParams); // ʹ���úõĲ��ֲ���Ӧ�õ��ؼ�mGrid2 
            }
//            else if(isNeedMovebommo()) {
//                // ��ʼ��ͷ������  
//                if (normal.isEmpty()) {  
//                    // ���������Ĳ���λ��  
//                    normal.set(inner.getLeft(), inner.getTop(),  
//                            inner.getRight(), inner.getBottom());  
//                }  
//                Log.e("jj", "���Σ�" + inner.getLeft() + "," + inner.getTop()  
//                        + "," + inner.getRight() + "," + inner.getBottom());  
//                // �ƶ�����  
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
     * �������� 
     */  
    public void animation() {  
        // �����ƶ�����  
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),  
                normal.top);  
        ta.setDuration(200);  
        inner.startAnimation(ta);  
        // ���ûص������Ĳ���λ��  
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);  
  
        Log.e("jj", "�ع飺" + normal.left + "," + normal.top + "," + normal.right  
                + "," + normal.bottom);  
  
        normal.setEmpty();  
  
    } 
  
    // �Ƿ���Ҫ��������  
    public boolean isNeedAnimation() {  
        return !normal.isEmpty();  
    }  
  
    /*** 
     * �Ƿ���Ҫ�ƶ����� inner.getMeasuredHeight():��ȡ���ǿؼ����ܸ߶� 
     *  
     * getHeight()����ȡ������Ļ�ĸ߶� 
     *  
     * @return 
     */  
    public boolean isNeedMove() {  
        int offset = inner.getMeasuredHeight() - getHeight();  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0�Ƕ����������Ǹ��ǵײ�  
        if (scrollY == 0 || scrollY == offset) {  
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMoveTop() {  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0�Ƕ����������Ǹ��ǵײ�  
        if (scrollY == 0) {
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMoveTop2() {  
        int scrollY = getScrollY();  
        Log.e("jj", "scrolly=" + scrollY);  
        // 0�Ƕ����������Ǹ��ǵײ�  
        if (scrollY > 0) {
            return true;  
        }  
        return false;  
    }
    
    public boolean isNeedMovebommo() {  
    	int offset = inner.getMeasuredHeight() - getHeight(); 
    	int scrollY = getScrollY();
        // 0�Ƕ����������Ǹ��ǵײ�  
        if (scrollY == offset) {
            return true;  
        }  
        return false;  
    }  
}
